

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

        // =======================
        //  CREAR CONTROLADORES
        // =======================
        ReporteController reporteController = new ReporteController();
        TicketController ticketController = new TicketController();
        RentaController rentaController = new RentaController();
        VentaController ventaController = new VentaController();
        EtiquetaController etiquetaController = new EtiquetaController();

        // Enlazar dependencias necesarias
        rentaController.setReporteController(reporteController);

        // =======================
        //   INICIAR LA UI
        // =======================
        ServiciosGUI ui = new ServiciosGUI(
                ticketController,
                ventaController,
                rentaController,
                reporteController,
                etiquetaController
        );

        ui.mostrar(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
