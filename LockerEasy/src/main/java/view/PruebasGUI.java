package view;

import controller.RentaController;
import controller.ReporteController;
import controller.TicketController;
import controller.VentaController;
import model.Ticket;
import model.Ubicacion;

public class PruebasGUI {

    private final ReporteController reporteController;
    private final TicketController ticketController;
    private final RentaController rentaController;
    private final VentaController ventaController;

    private Ticket ticketActual;

    public PruebasGUI() {

        // === Crear controllers reales ===
        reporteController = new ReporteController();
        ticketController = new TicketController();
        rentaController = new RentaController();
        ventaController = new VentaController();

        // Dependencias necesarias
        rentaController.setReporteController(reporteController);

        System.out.println("[LOGICA] Controladores cargados correctamente.");
    }


    // ============================================================
    //   MÉTODO 1 — Crear ticket nuevo
    // ============================================================
    public void crearNuevoTicket(String nombreCliente) {
        ticketActual = ticketController.crearNuevoTicket(nombreCliente, nombreCliente + "@mail.com");

        System.out.println("[LOGICA] Ticket creado:");
        System.out.println(" → ID: " + ticketActual.getTicketId());
        System.out.println(" → Cliente: " + ticketActual.getNombreCliente());
    }


    // ============================================================
    //   MÉTODO 2 — Registrar un servicio
    // ============================================================
    public void registrarServicioEnTicket(String tipo) {

        if (ticketActual == null) {
            System.out.println("[LOGICA] No hay ticket activo.");
            return;
        }

        System.out.println("[LOGICA] Registrando venta → " + tipo);

        // De momento todo cuesta $50 para pruebas
        ventaController.registrarVenta(
                1,       // ID producto
                1,       // Cantidad
                ticketActual,
                ticketController
        );

        System.out.println(" → Total actual del ticket: $" + ticketActual.getTotalTicket());
    }


    // ============================================================
    //   MÉTODO 3 — Iniciar renta de casillero
    // ============================================================
    public void iniciarRentaDemo() {

        if (ticketActual == null) {
            System.out.println("[LOGICA] No hay ticket para asociar la renta.");
            return;
        }

        System.out.println("[LOGICA] Iniciando renta…");

        rentaController.iniciarRenta(
                Ubicacion.PA_T1_L1,
                ticketActual,
                ticketController
        );

        System.out.println(" → Renta iniciada.");
    }
}

