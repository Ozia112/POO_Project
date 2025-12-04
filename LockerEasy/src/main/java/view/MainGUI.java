package view;

import controller.*;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainGUI {
    private final ReporteController reporteController;
    private final TicketController ticketController;
    private final RentaController rentaController;
    private final VentaController ventaController;
    private final EtiquetaController etiquetaController;
    private final InventarioController inventarioController;

    private ServiciosView serviciosView;
    private RentaView rentaView;
    private ConfigView configView;

    private TabPane tabPane;
    private Tab tabConfig;

    public MainGUI() {
        this.reporteController = new ReporteController();
        this.ticketController = new TicketController(reporteController);
        this.rentaController = new RentaController(ticketController, reporteController);
        this.ventaController = new VentaController(ticketController, reporteController);
        this.etiquetaController = new EtiquetaController();
        this.inventarioController = new InventarioController();
    }

    public void mostrar(Stage stage) {
        // Pestañas principales
        tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setSide(javafx.geometry.Side.LEFT);

        // Crear vistas modulares
        serviciosView = new ServiciosView(
            ticketController,
            ventaController,
            rentaController,
            reporteController,
            etiquetaController,
            inventarioController
        );

        rentaView = new RentaView(
            rentaController,
            ticketController
        );

        configView = new ConfigView(
            ventaController,
            etiquetaController,
            rentaController,
            inventarioController
        );

        // ===== SINCRONIZACIÓN ENTRE PESTAÑAS =====
        
        // Cuando ServiciosView quiere iniciar una renta
        serviciosView.setOnCambiarARenta(ubicacion -> {
            tabPane.getSelectionModel().select(1); // Cambiar a pestaña Renta
            
            // Obtener datos temporales de ServiciosView
            String nombre = serviciosView.getNombreClienteTemp();
            String correo = serviciosView.getCorreoClienteTemp();
            var ticketActual = serviciosView.getTicketActual();
            
            // Pasar datos a RentaView para que inicie la renta
            rentaView.iniciarRentaConDatosTemporales(nombre, correo, ticketActual);
        });

        // Cuando RentaView crea una renta exitosamente
        rentaView.setOnRentaCreada((ticket, ubicacion) -> {
            tabPane.getSelectionModel().select(0); // Volver a Servicios
            serviciosView.notificarRentaCreada(ticket, ubicacion);
        });

        // Cuando RentaView finaliza una renta exitosamente
        rentaView.setOnRentaFinalizada((ticket, ubicacion) -> {
            tabPane.getSelectionModel().select(0); // Ir a Servicios
            serviciosView.notificarRentaFinalizada(ticket, ubicacion);
        });

        // Crear pestañas
        Tab tabServicios = new Tab("Servicios", serviciosView.getView());
        Tab tabRenta = new Tab("Rentas", rentaView.getView());
        tabConfig = new Tab("Configuración", configView.getView());

        tabPane.getTabs().addAll(tabServicios, tabRenta, tabConfig);

        // Listener para actualizar vistas al cambiar de pestaña
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab == tabServicios) {
                serviciosView.actualizarEtiquetas();
            } else if (newTab == tabRenta) {
                rentaView.actualizarVista();
            } else if (newTab == tabConfig) {
                configView.actualizarVistas();
            }
        });

        VBox root = new VBox(tabPane);
        root.setPadding(new Insets(10));

        Scene scene = new Scene(root, 1200, 600);
        scene.getStylesheets().add("view/Styles/Styles.css");

        stage.setScene(scene);
        stage.setTitle("LockerEasy - Sistema de Gestión");
        stage.show();
        
        // Verificar si es primera ejecución (sin lockers configurados)
        verificarPrimeraEjecucion();
    }
    
    /**
     * Verifica si es la primera ejecución de la aplicación.
     * Si no hay lockers configurados, guía al usuario a la configuración.
     */
    private void verificarPrimeraEjecucion() {
        if (configView.necesitaConfiguracionInicial()) {
            // Mostrar mensaje de bienvenida
            Alert bienvenida = new Alert(Alert.AlertType.INFORMATION);
            bienvenida.setTitle("¡Bienvenido a LockerEasy!");
            bienvenida.setHeaderText("Configuración Inicial Requerida");
            bienvenida.setContentText(
                "Parece que es tu primera vez usando LockerEasy.\n\n" +
                "Para empezar, necesitas configurar:\n" +
                "1. ⚙️ Precio de renta y tiempos de tolerancia\n" +
                "2. 🏢 Registrar tus torres y lockers\n" +
                "3. 🏷️ (Opcional) Crear etiquetas de productos\n" +
                "4. 📦 (Opcional) Registrar productos para venta\n\n" +
                "Te llevaremos a la configuración ahora."
            );
            bienvenida.showAndWait();
            
            // Ir a la pestaña de configuración
            tabPane.getSelectionModel().select(tabConfig);
            
            // Ir específicamente a la pestaña de Torres y Lockers
            configView.irAConfiguracionLockers();
        }
    }
}