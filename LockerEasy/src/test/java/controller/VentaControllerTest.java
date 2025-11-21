package controller;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import model.Ticket;

/**
 * Pruebas unitarias para VentaController
 * Verifica el registro de ventas de productos
 */
class VentaControllerTest {

    private VentaController ventaController;
    private TicketController ticketController;
    private Ticket ticket;

    @BeforeEach
    public void setUp() {
        ventaController = new VentaController();
        ticketController = new TicketController();
        ticket = ticketController.crearNuevoTicket("Cliente Test", "test@example.com");
    }

    @Test
    @DisplayName("Debe registrar una venta correctamente")
    void testRegistrarVenta() {
        // Arrange
        int productoId = 1;
        int cantidad = 2;
        double totalInicial = ticket.getTotalTicket();

        // Act
        boolean resultado = ventaController.registrarVenta(
            productoId, 
            cantidad, 
            ticket, 
            ticketController
        );

        // Assert
        assertTrue(resultado, "La venta debe registrarse correctamente");
        assertTrue(ticket.getTotalTicket() > totalInicial, 
                   "El total del ticket debe aumentar después de la venta");
    }

    @Test
    @DisplayName("Debe manejar múltiples ventas en el mismo ticket")
    void testMultiplesVentas() {
        // Arrange & Act
        boolean venta1 = ventaController.registrarVenta(1, 1, ticket, ticketController);
        double totalDespuesVenta1 = ticket.getTotalTicket();
        
        boolean venta2 = ventaController.registrarVenta(2, 3, ticket, ticketController);
        double totalDespuesVenta2 = ticket.getTotalTicket();

        // Assert
        assertTrue(venta1, "La primera venta debe registrarse");
        assertTrue(venta2, "La segunda venta debe registrarse");
        assertTrue(totalDespuesVenta2 > totalDespuesVenta1, 
                   "El total debe seguir aumentando con cada venta");
    }

    @Test
    @DisplayName("Debe calcular correctamente el total con diferentes cantidades")
    void testCalculoTotalConCantidades() {
        // Arrange
        int productoId = 1;
        
        // Act
        ventaController.registrarVenta(productoId, 1, ticket, ticketController);
        double totalCon1Producto = ticket.getTotalTicket();
        
        Ticket ticket2 = ticketController.crearNuevoTicket("Cliente 2", "c2@test.com");
        ventaController.registrarVenta(productoId, 5, ticket2, ticketController);
        double totalCon5Productos = ticket2.getTotalTicket();

        // Assert
        assertTrue(totalCon5Productos > totalCon1Producto, 
                   "El total con 5 productos debe ser mayor que con 1 producto");
    }

    @Test
    @DisplayName("Debe agregar servicios de venta al ticket")
    void testServiciosAgregadosAlTicket() {
        // Arrange
        int cantidadServiciosInicial = ticket.getServicios().size();

        // Act
        ventaController.registrarVenta(1, 2, ticket, ticketController);

        // Assert
        assertTrue(ticket.getServicios().size() > cantidadServiciosInicial,
                   "Debe agregar servicios al ticket");
    }
}
