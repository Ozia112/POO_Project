package controller;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import model.Ticket;
import model.Ubicacion;

/**
 * Pruebas unitarias para RentaController
 * Verifica el inicio y finalización de rentas
 */
class RentaControllerTest {

    private RentaController rentaController;
    private TicketController ticketController;
    private ReporteController reporteController;
    private Ticket ticket;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        reporteController = new ReporteController();
        ticketController = new TicketController();
        rentaController = new RentaController();
        
        // Configurar dependencias
        rentaController.setReporteController(reporteController);
        
        // Crear un ticket de prueba
        ticket = ticketController.crearNuevoTicket("Test Cliente", "test@example.com");
    }

    @Test
    @DisplayName("Debe iniciar una renta correctamente")
    void testIniciarRenta() {
        // Arrange
        Ubicacion ubicacion = Ubicacion.PA_T1_L1;
        double totalInicial = ticket.getTotalTicket();

        // Act
        boolean resultado = rentaController.iniciarRenta(ubicacion, ticket, ticketController);

        // Assert
        assertTrue(resultado, "La renta debe iniciarse correctamente");
        assertTrue(ticket.getTotalTicket() >= totalInicial, "El total debe aumentar o mantenerse");
    }

    @Test
    @DisplayName("Debe finalizar una renta correctamente")
    void testFinalizarRenta() {
        // Arrange
        Ubicacion ubicacion = Ubicacion.PA_T1_L2;
        rentaController.iniciarRenta(ubicacion, ticket, ticketController);
        
        // Simular paso del tiempo
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            fail("Error en el sleep");
        }

        double totalDespuesDeIniciar = ticket.getTotalTicket();

        // Act
        boolean resultado = rentaController.finalizarRenta(ubicacion, ticket, ticketController);

        // Assert
        assertTrue(resultado, "La renta debe finalizarse correctamente");
        assertTrue(ticket.getTotalTicket() >= totalDespuesDeIniciar, 
                   "El total debe aumentar al finalizar por el tiempo transcurrido");
    }

    @Test
    @DisplayName("Debe manejar múltiples ubicaciones simultáneamente")
    void testMultiplesUbicaciones() {
        // Arrange
        Ticket ticket1 = ticketController.crearNuevoTicket("Cliente 1", "c1@test.com");
        Ticket ticket2 = ticketController.crearNuevoTicket("Cliente 2", "c2@test.com");

        // Act
        boolean renta1 = rentaController.iniciarRenta(Ubicacion.PA_T1_L1, ticket1, ticketController);
        boolean renta2 = rentaController.iniciarRenta(Ubicacion.PA_T1_L2, ticket2, ticketController);

        // Assert
        assertTrue(renta1, "La primera renta debe iniciarse correctamente");
        assertTrue(renta2, "La segunda renta debe iniciarse correctamente");
    }

    @Test
    @DisplayName("Debe calcular el costo de la renta basado en el tiempo")
    void testCalculoCostoRenta() {
        // Arrange
        Ubicacion ubicacion = Ubicacion.PA_T1_L3;
        double totalInicial = ticket.getTotalTicket();

        // Act
        rentaController.iniciarRenta(ubicacion, ticket, ticketController);
        
        try {
            Thread.sleep(500); // Medio segundo
        } catch (InterruptedException e) {
            fail("Error en el sleep");
        }
        
        rentaController.finalizarRenta(ubicacion, ticket, ticketController);

        // Assert
        assertTrue(ticket.getTotalTicket() > totalInicial, 
                   "El total debe aumentar después de la renta");
    }
}
