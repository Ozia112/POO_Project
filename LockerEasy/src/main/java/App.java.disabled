import controller.EtiquetaController;
import controller.RentaController;
import controller.ReporteController;
import controller.TicketController;
import controller.VentaController;
import model.Ticket;
import model.Ubicacion;

public class App {
    public static void main(String[] args) throws Exception {

        System.out.println("=== Iniciando LockerEasy ===\n");

        // Crear todos los controladores (sin dependencias circulares)
        ReporteController reporteController = new ReporteController();
        TicketController ticketController = new TicketController();
        RentaController rentaController = new RentaController();
        VentaController ventaController = new VentaController();
        @SuppressWarnings("unused")
        EtiquetaController etiquetaController = new EtiquetaController();

        // Configurar dependencias necesarias
        rentaController.setReporteController(reporteController);

        System.out.println("Controladores inicializados correctamente\n");

        // === EJEMPLO DE USO ===
        
        // 1. Crear un nuevo ticket
        System.out.println("--- Creando nuevo ticket ---");
        Ticket ticket1 = ticketController.crearNuevoTicket("Juan Perez", "juan@example.com");
        System.out.println("Ticket creado: ID=" + ticket1.getTicketId() + 
                         ", Cliente=" + ticket1.getNombreCliente() + "\n");

        // 2. Iniciar una renta (pasar ticketController como parámetro)
        System.out.println("--- Iniciando renta ---");
        boolean rentaIniciada = rentaController.iniciarRenta(
            Ubicacion.PA_T1_L1, 
            ticket1, 
            ticketController  // Pasar el controller como parámetro
        );
        
        if (rentaIniciada) {
            System.out.println("Renta iniciada exitosamente");
            System.out.println("Total del ticket: $" + ticket1.getTotalTicket() + "\n");
        }

        // 3. Realizar una venta (pasar ticketController como parámetro)
        System.out.println("--- Realizando venta ---");
        boolean ventaRealizada = ventaController.registrarVenta(
            1,              // ID del producto
            2,              // Cantidad
            ticket1,        // Ticket
            ticketController // Pasar el controller como parámetro
        );

        if (ventaRealizada) {
            System.out.println("Venta realizada exitosamente");
            System.out.println("Total actualizado del ticket: $" + ticket1.getTotalTicket() + "\n");
        }

        // 4. Simular paso del tiempo y finalizar renta
        System.out.println("--- Finalizando renta ---");
        try {
            Thread.sleep(2000); // Simular 2 segundos
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Error al esperar: " + e.getMessage());
        }

        boolean rentaFinalizada = rentaController.finalizarRenta(
            Ubicacion.PA_T1_L1,
            ticket1,
            ticketController // Pasar el controller como parámetro
        );

        if (rentaFinalizada) {
            System.out.println("Renta finalizada exitosamente");
            System.out.println("Total final del ticket: $" + ticket1.getTotalTicket() + "\n");
        }

        // 5. Ver reporte del día
        System.out.println("--- Reporte del día ---");
        var reporte = reporteController.getReporte();
        System.out.println("Fecha: " + reporte.getFechaReporte());
        System.out.println("Total tickets: " + reporte.getTickets().size());
        System.out.println("Total del día: $" + reporte.getTotal());

        System.out.println("\n=== Sistema funcionando correctamente ===");
    }
}