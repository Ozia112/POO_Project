package controller;

import dao.VentaDAO;
import model.ProductoCatalogo;
import model.Ticket;
import model.Venta;

public class VentaController {
    
    private final VentaDAO ventaDAO;
    private TicketController ticketController;
    private ReporteController reporteController;
    private final InventarioController inventarioController;

    public VentaController() {
        this.ventaDAO = new VentaDAO();
        this.inventarioController = new InventarioController();
    }

    public VentaController(TicketController ticketController, ReporteController reporteController) {
        this();
        this.ticketController = ticketController;
        this.reporteController = reporteController;
    }

    /**
     * Registra una venta de un producto del catálogo
     * @param idProductoCatalogo ID del producto en el catálogo
     * @param cantidad Cantidad a vender
     * @param ticket Ticket al que se agregará la venta
     * @return true si la venta se registró exitosamente
     */
    public boolean registrarVenta(Long idProductoCatalogo, int cantidad, Ticket ticket) {
        if (cantidad <= 0) {
            System.err.println("La cantidad debe ser mayor a 0");
            return false;
        }

        // 1. Obtener producto del catálogo
        ProductoCatalogo producto = inventarioController.buscarProducto(idProductoCatalogo);
        
        if (producto == null) {
            System.err.println("Producto no encontrado: " + idProductoCatalogo);
            return false;
        }

        if (!producto.isDisponible()) {
            System.err.println("Producto no disponible: " + producto.getNombre());
            return false;
        }

        try {
            // 2. Reducir existencias si afecta inventario (lógica de negocio en InventarioController)
            if (producto.getEtiqueta().isAfectaInventario()) {
                boolean reducido = inventarioController.reducirExistencias(idProductoCatalogo, cantidad);
                if (!reducido) {
                    return false;
                }
            }

            // 3. Crear instancia de Venta (TipoServicio)
            Venta venta = new Venta(
                producto.getNombre(),
                cantidad,
                producto
            );
            venta.setTicket(ticket);
            venta.setAplicarDescuento(false);

            // 4. Guardar la venta
            ventaDAO.guardar(venta);
            System.out.println("Venta guardada - ID: " + venta.getTipoServicioId() + 
                             " Producto: " + producto.getNombre() + 
                             " Cantidad: " + cantidad);

            // 5. Agregar al ticket
            ticket.agregarServicio(venta);
            
            // 6. Actualizar totales
            if (ticketController != null) {
                ticket.setTotalTicket(ticketController.calcularTotalTicket(ticket));
                ticketController.getTicketDAO().actualizar(ticket);
            }

            // 7. Actualizar reporte
            if (reporteController != null) {
                reporteController.recalcularTotal();
                reporteController.guardarReporte();
            }

            System.out.println("Venta registrada exitosamente: " + producto.getNombre());
            return true;

        } catch (Exception e) {
            System.err.println("Error al registrar venta: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Aplica o remueve descuento de una venta específica
     */
    public boolean aplicarDescuento(Long ventaId, boolean aplicar) {
        Venta venta = ventaDAO.obtener(ventaId);
        if (venta == null) {
            System.err.println("Venta no encontrada: " + ventaId);
            return false;
        }

        venta.setAplicarDescuento(aplicar);
        
        try {
            ventaDAO.actualizar(venta);
            
            // Actualizar totales del ticket
            if (ticketController != null && venta.getTicket() != null) {
                Ticket ticket = venta.getTicket();
                ticket.setTotalTicket(ticketController.calcularTotalTicket(ticket));
                ticketController.getTicketDAO().actualizar(ticket);
                
                // Actualizar reporte
                if (reporteController != null) {
                    reporteController.recalcularTotal();
                    reporteController.guardarReporte();
                }
            }
            
            System.out.println("Descuento " + (aplicar ? "aplicado" : "removido") + 
                             " en venta ID: " + ventaId);
            return true;
            
        } catch (Exception e) {
            System.err.println("Error al aplicar descuento: " + e.getMessage());
            return false;
        }
    }

    /**
     * Cancela una venta y restaura el inventario si aplica
     */
    public boolean cancelarVenta(Long ventaId) {
        Venta venta = ventaDAO.obtener(ventaId);
        if (venta == null) {
            System.err.println("Venta no encontrada: " + ventaId);
            return false;
        }

        try {
            ProductoCatalogo producto = venta.getProductoCatalogo();
            
            // Restaurar existencias si afecta inventario
            if (producto.getEtiqueta().isAfectaInventario()) {
                int existenciasActuales = producto.getExistentes();
                inventarioController.actualizarExistencias(
                    producto.getId(), 
                    existenciasActuales + venta.getCantidad()
                );
            }

            // Eliminar venta
            Ticket ticket = venta.getTicket();
            if (ticket != null) {
                ticket.eliminarServicio(venta);
            }
            
            ventaDAO.eliminar(ventaId);

            // Actualizar totales
            if (ticketController != null && ticket != null) {
                ticket.setTotalTicket(ticketController.calcularTotalTicket(ticket));
                ticketController.getTicketDAO().actualizar(ticket);
                
                if (reporteController != null) {
                    reporteController.recalcularTotal();
                    reporteController.guardarReporte();
                }
            }

            System.out.println("Venta cancelada: " + producto.getNombre());
            return true;

        } catch (Exception e) {
            System.err.println("Error al cancelar venta: " + e.getMessage());
            return false;
        }
    }

    public VentaDAO getVentaDAO() {
        return ventaDAO;
    }
}