package controller;

import dao.VentaDAO;
import dao.ServicioDAO;
import model.Etiqueta;
import model.Servicio;
import model.Ticket;
import model.Venta;
import java.util.List;

public class VentaController {
    
    private final ServicioDAO servicioDAO;
    private TicketController ticketController;
    private ReporteController reporteController;
    private final InventarioController inventarioController;

    
    public VentaController() {
        this.servicioDAO = new ServicioDAO();
        this.inventarioController = new InventarioController();
    }

    
    public VentaController(TicketController ticketController, ReporteController reporteController) {
        this();
        this.ticketController = ticketController;
        this.reporteController = reporteController;
    }

    //agrega nuevo producto al catalogo
    

    //Regiustra la venta de un producto
    public boolean registrarVenta(Long idProducto, int cantidad, Ticket ticket) {
        
        Venta producto = inventarioController.getVentaDAO().obtener(idProducto);
        
        if (producto == null) {
            System.err.println("Producto no encontrado: " + idProducto);
            return false;
        }

        if (!producto.isDisponible()) {
            System.err.println("Producto no disponible: " + producto.getNombre());
            return false;
        }

        boolean afectaInventario = producto.getEtiqueta().isAfectaInventario();
        try {
            if (afectaInventario) {
                if (producto.getExistentes() < cantidad) {
                    System.err.println("Inventario insuficiente. Disponible: " + producto.getExistentes());
                    return false;
                }

                // Restar inventario
                producto.setExistentes(producto.getExistentes() - cantidad);
                producto.setDisponible(producto.getExistentes() > 0);
                inventarioController.getVentaDAO().actualizar(producto);
            }

            // Crear el servicio para ticket
            Servicio servicio = new Servicio();
            servicio.setTipoServicio(producto); // El producto es el tipo de servicio
            servicio.setAplicarDescuento(false);
            // el precio del servicio se calcula automáticamente en base al producto

            
            if (ticketController != null) {
                
                ticketController.agregarServicio(ticket, producto);
                ticketController.calcularTotalTicket(ticket);
                ticketController.getTicketDAO().actualizar(ticket);
            }

            if (reporteController != null) {
                reporteController.agregarTicket(ticket);
                reporteController.recalcularTotal();
                reporteController.getReporteDAO().actualizar(reporteController.getReporteActual());
            }

            System.out.println("Venta registrada: " + producto.getNombre());
            return true;
        } catch (Exception e) {
            System.err.println("Error al registrar venta: " + e.getMessage());
            return false;
        }
    }

    public ServicioDAO getServicioDAO() {
        return servicioDAO;
    }
}