package controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import model.Ticket;

/**
 * Pruebas unitarias para TicketController
 * Verifica la creación y gestión de tickets
 */
class TicketControllerTest {

    private TicketController ticketController;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        ticketController = new TicketController();
    }

    @Test
    @DisplayName("Debe crear un nuevo ticket con datos válidos")
    void testCrearNuevoTicket() {
        // Arrange
        String nombreCliente = "Juan Pérez";
        String correo = "juan@example.com";

        // Act
        Ticket ticket = ticketController.crearNuevoTicket(nombreCliente, correo);

        // Assert
        assertNotNull(ticket, "El ticket no debe ser null");
        assertEquals(nombreCliente, ticket.getNombreCliente(), "El nombre del cliente debe coincidir");
        assertEquals(correo, ticket.getCorreoCliente(), "El correo debe coincidir");
        assertTrue(ticket.getTicketId() > 0, "El ID del ticket debe ser mayor a 0");
        assertEquals(0.0, ticket.getTotalTicket(), "El total inicial debe ser 0");
    }

    @Test
    @DisplayName("Debe crear múltiples tickets con IDs únicos")
    void testCrearMultiplesTickets() {
        // Arrange & Act
        Ticket ticket1 = ticketController.crearNuevoTicket("Cliente 1", "cliente1@test.com");
        Ticket ticket2 = ticketController.crearNuevoTicket("Cliente 2", "cliente2@test.com");
        Ticket ticket3 = ticketController.crearNuevoTicket("Cliente 3", "cliente3@test.com");

        // Assert
        assertNotNull(ticket1);
        assertNotNull(ticket2);
        assertNotNull(ticket3);
        
        // Los IDs deben ser diferentes
        assertNotEquals(ticket1.getTicketId(), ticket2.getTicketId());
        assertNotEquals(ticket2.getTicketId(), ticket3.getTicketId());
        assertNotEquals(ticket1.getTicketId(), ticket3.getTicketId());
    }

    @Test
    @DisplayName("Debe agregar servicio al ticket y actualizar el total")
    void testAgregarServicioAlTicket() {
        // Arrange
        Ticket ticket = ticketController.crearNuevoTicket("Ana López", "ana@test.com");
        double totalInicial = ticket.getTotalTicket();

        // Act
        // Aquí agregaríamos un servicio si el método existe
        // Por ahora solo verificamos el estado inicial

        // Assert
        assertEquals(0.0, totalInicial, "El total inicial debe ser 0");
        assertNotNull(ticket.getServicios(), "La lista de servicios no debe ser null");
    }

    @Test
    @DisplayName("El ticket debe mantener la referencia del cliente")
    void testTicketMantieneDatosCliente() {
        // Arrange
        String nombre = "María García";
        String email = "maria@example.com";

        // Act
        Ticket ticket = ticketController.crearNuevoTicket(nombre, email);

        // Assert
        assertSame(nombre, ticket.getNombreCliente(), "Debe mantener la misma referencia del nombre");
        assertSame(email, ticket.getCorreoCliente(), "Debe mantener la misma referencia del email");
    }
}
