package backend.controllers;

import backend.model.*;
import java.util.*;

/**
 * Controller principal que coordina todos los demás controllers
 * Cumple con todos los requisitos funcionales (FR1-FR6)
 */
public class LockerEasyController {
    private RentaController rentaController;
    private VentaController ventaController;
    private TicketController ticketController;
    private DescuentoController descuentoController;
    private TransaccionController transaccionController;

    public LockerEasyController() {
        this.rentaController = new RentaController();
        this.ventaController = new VentaController();
        this.ticketController = new TicketController();
        this.descuentoController = new DescuentoController();
        this.transaccionController = new TransaccionController();
    }

    // ===== MÉTODOS DE RENTA (FR1, FR2) =====

    /**
     * FR1: Registrar una nueva renta
     */
    public Renta registrarRenta(String nombreCliente, int lockerId, int duracionHoras, 
                                float precioHora, String metodoPago) {
        Renta renta = rentaController.registrarRenta(nombreCliente, lockerId, duracionHoras, precioHora);
        
        if (renta != null) {
            // FR6: Registrar la transacción
            transaccionController.registrarTransaccionRenta(renta, metodoPago);
        }
        
        return renta;
    }

    /**
     * FR2: Finalizar una renta
     */
    public boolean finalizarRenta(int lockerId) {
        return rentaController.finalizarRenta(lockerId);
    }

    /**
     * FR2: Cancelar una renta con feedback
     */
    public boolean cancelarRenta(int lockerId, String motivoCancelacion, String nombreCliente, float monto) {
        boolean resultado = rentaController.cancelarRenta(lockerId, motivoCancelacion);
        
        if (resultado) {
            // FR6: Registrar la cancelación
            transaccionController.registrarCancelacion(nombreCliente, monto, motivoCancelacion);
        }
        
        return resultado;
    }

    // ===== MÉTODOS DE VENTA (FR4) =====

    /**
     * FR4: Registrar una venta vinculada a un cliente
     */
    public Venta registrarVenta(String nombreCliente, List<String> productos, String metodoPago) {
        Venta venta = ventaController.registrarVenta(nombreCliente, productos);
        
        if (venta != null) {
            // FR6: Registrar la transacción
            transaccionController.registrarTransaccionVenta(venta, metodoPago);
        }
        
        return venta;
    }

    // ===== MÉTODOS DE DESCUENTOS (FR3) =====

    /**
     * FR3: Aplicar descuento a un servicio
     */
    public Servicio aplicarDescuento(Servicio servicio, String codigoDescuento) {
        return descuentoController.aplicarDescuento(servicio, codigoDescuento);
    }

    /**
     * FR3: Crear nueva promoción
     */
    public boolean crearPromocion(String codigo, String nombre, float porcentaje) {
        return descuentoController.crearDescuento(codigo, nombre, porcentaje);
    }

    // ===== MÉTODOS DE PRECIOS (FR5) =====

    /**
     * FR5: Actualizar precio de producto
     */
    public boolean actualizarPrecioProducto(String nombreProducto, float nuevoPrecio) {
        return ventaController.actualizarPrecioProducto(nombreProducto, nuevoPrecio);
    }

    /**
     * FR5: Agregar nuevo producto al catálogo
     */
    public boolean agregarProducto(String nombreProducto, float precio) {
        return ventaController.agregarProducto(nombreProducto, precio);
    }

    // ===== MÉTODOS DE TICKETS =====

    /**
     * Crear ticket completo para un cliente con todos sus servicios
     */
    public Ticket crearTicketCompleto(String nombreCliente, String correoCliente,
                                     Renta renta, Venta venta, String codigoDescuento) {
        List<Servicio> servicios = new ArrayList<>();
        int idServicio = 1;

        // Agregar renta como servicio si existe
        if (renta != null) {
            float totalRenta = renta.calcularTotalRenta();
            Servicio servicioRenta = new Servicio(idServicio++, renta, null, totalRenta);
            
            // Aplicar descuento si hay código
            if (codigoDescuento != null && !codigoDescuento.isEmpty()) {
                servicioRenta = descuentoController.aplicarDescuento(servicioRenta, codigoDescuento);
            }
            
            servicios.add(servicioRenta);
        }

        // Agregar venta como servicio si existe
        if (venta != null) {
            float totalVenta = venta.calcularTotalVenta();
            Servicio servicioVenta = new Servicio(idServicio++, venta, null, totalVenta);
            
            // Aplicar descuento si hay código
            if (codigoDescuento != null && !codigoDescuento.isEmpty()) {
                servicioVenta = descuentoController.aplicarDescuento(servicioVenta, codigoDescuento);
            }
            
            servicios.add(servicioVenta);
        }

        return ticketController.crearTicket(nombreCliente, correoCliente, servicios);
    }

    // ===== MÉTODOS DE CONSULTA =====

    public List<Locker> obtenerLockersDisponibles() {
        return rentaController.obtenerLockersDisponibles();
    }

    public Map<String, Float> obtenerCatalogo() {
        return ventaController.obtenerCatalogo();
    }

    public void mostrarDescuentos() {
        descuentoController.mostrarDescuentos();
    }

    public void generarReporteFinanciero() {
        transaccionController.generarReporteFinanciero();
    }

    public void mostrarHistorialTransacciones(int limite) {
        transaccionController.mostrarHistorial(limite);
    }

    public void imprimirTicket(int ticketId) {
        ticketController.imprimirTicket(ticketId);
    }

    public void mostrarEstadoSistema() {
        rentaController.mostrarEstadoSistema();
        System.out.println("Transacciones registradas: " + 
            transaccionController.obtenerHistorial().size());
    }

    // ===== GETTERS PARA ACCESO DIRECTO A CONTROLLERS =====

    public RentaController getRentaController() {
        return rentaController;
    }

    public VentaController getVentaController() {
        return ventaController;
    }

    public TicketController getTicketController() {
        return ticketController;
    }

    public DescuentoController getDescuentoController() {
        return descuentoController;
    }

    public TransaccionController getTransaccionController() {
        return transaccionController;
    }
}
