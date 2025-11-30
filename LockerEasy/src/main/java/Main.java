

import controller.EtiquetaController;
import controller.RentaController;
import controller.ReporteController;
import controller.TicketController;
import controller.VentaController;
import javafx.application.Application;
import javafx.stage.Stage;
import view.ServiciosGUI;

public class Main extends Application {

    @Override
    public void start(Stage stage) {

        // ===== Controladores Reales =====
        ReporteController reporteController = new ReporteController();
        TicketController ticketController = new TicketController(reporteController);
        VentaController ventaController = new VentaController();
        RentaController rentaController = new RentaController();
        EtiquetaController etiquetaController = new EtiquetaController();

        // ===== Configurar dependencias =====
        rentaController.setReporteController(reporteController);

        // ===== UI Bonita =====
        
        
        ServiciosGUI gui = new ServiciosGUI(
            ticketController,
            ventaController,
            rentaController,
            reporteController,
            etiquetaController
        );

        gui.mostrar(stage);
        

        /*  Versión temporal para probar la gestión de productos
        VentaGUI ventaGui = new VentaGUI(ventaController);
        ventaGui.mostrar(stage);
        */
    }

    public static void main(String[] args) {
        launch(args);
    }
}
