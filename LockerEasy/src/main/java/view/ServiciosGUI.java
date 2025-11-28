package view;

import controller.EtiquetaController;
import controller.RentaController;
import controller.ReporteController;
import controller.TicketController;
import controller.VentaController;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Reporte;
import model.Ticket;

public class ServiciosGUI {

    // ============================================================
    // 1. CONTROLLERS REALES (inyectados desde Main)
    // ============================================================
    private final TicketController ticketController;
    private final VentaController ventaController;
    private final RentaController rentaController;
    private final ReporteController reporteController;
    private final EtiquetaController etiquetaController;

    // Ticket actual que se está armando
    private Ticket ticketActual;


    // ============================================================
    // 2. CONSTRUCTOR
    // ============================================================
    public ServiciosGUI(
            TicketController ticketController,
            VentaController ventaController,
            RentaController rentaController,
            ReporteController reporteController,
            EtiquetaController etiquetaController
    ) {
        this.ticketController = ticketController;
        this.ventaController = ventaController;
        this.rentaController = rentaController;
        this.reporteController = reporteController;
        this.etiquetaController = etiquetaController;
    }

    // ============================================================
    // 3. LÓGICA BÁSICA (solo lo necesario hoy)
    // ============================================================

    private void crearNuevoTicket(String nombreCliente, String correo) {

        if (correo == null || correo.isBlank()) {
            correo = "correo@desconocido.com";
        }

        ticketActual = ticketController.crearNuevoTicket(nombreCliente, correo);

        System.out.println("[LOG] Ticket creado → ID=" + ticketActual.getTicketId());
    }


    private void agregarServicioDummy(String tipo) {

        if (ticketActual == null) {
            System.out.println("[WARN] No hay ticket actual.");
            return;
        }

        // Solo para probar: agregamos un producto con ID 1 y cantidad 1
        ventaController.registrarVenta(
                1,              // ID ficticio para probar
                1,              // cantidad
                ticketActual,
                ticketController
        );

        System.out.println("[LOG] Servicio agregado: " + tipo);
    }


    private void actualizarPreview(VBox box) {
        box.getChildren().clear();

        if (ticketActual == null) {
            box.getChildren().add(new Label("Ticket vacío."));
            return;
        }

        Label titulo = new Label("Ticket #" + ticketActual.getTicketId());
        titulo.setStyle("-fx-font-size:18; -fx-font-weight:bold;");

        Label cliente = new Label("Cliente: " + ticketActual.getNombreCliente());

        Label total = new Label("Total: $" + ticketActual.getTotalTicket());
        total.setStyle("-fx-font-size:14; -fx-font-weight:bold;");

        box.getChildren().addAll(titulo, cliente, total);
    }


    private void actualizarTabla(TableView<String> tabla) {
        tabla.getItems().clear();

        Reporte reporte = reporteController.getReporte();
        for (Ticket t : reporte.getTickets()) {
            tabla.getItems().add(t.getNombreCliente() + "  —  $" + t.getTotalTicket());
        }
    }

    // ============================================================
    // 4. UI PRINCIPAL
    // ============================================================
    public void mostrar(Stage stage) {

        // PANEL LATERAL
        VBox side = new VBox(25);
        side.getStyleClass().add("sidebar");

        Label op1 = new Label("Servicios");
        Label op2 = new Label("Renta");
        op1.getStyleClass().add("menu-item");
        op2.getStyleClass().add("menu-item");

        side.getChildren().addAll(op1, op2);

        // CAJA PRINCIPAL
        VBox caja1 = new VBox(20);
        caja1.getStyleClass().add("caja-form");
        caja1.setPrefWidth(460);

        Label titulo = new Label("Crear ticket de venta");
        titulo.getStyleClass().add("titulo1");

        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Nombre del cliente");
        txtNombre.getStyleClass().add("text-field");

        TextField txtCorreo = new TextField();
        txtCorreo.setPromptText("Correo (opcional)");
        txtCorreo.getStyleClass().add("text-field");

        ComboBox<String> comboTipo = new ComboBox<>();
        comboTipo.getItems().addAll(
                "Agregar",
                "Trámite",
                "Papelería",
                "Consumible",
                "Renta"
        );
        comboTipo.getStyleClass().add("combo-box");

        Button btnAgregar = new Button("Agregar");
        btnAgregar.getStyleClass().add("btn-green");

        caja1.getChildren().addAll(
                titulo, txtNombre, txtCorreo,
                new Label("Tipo"), comboTipo,
                btnAgregar
        );

        // TABS — Ticket y gráfica fake
        VBox previewBox = new VBox(10);
        previewBox.setPadding(new Insets(15));
        previewBox.setStyle("-fx-background-color:white; -fx-background-radius:10;");

        TabPane tabs = new TabPane();

        Tab tabTicket = new Tab("Ticket");
        tabTicket.setClosable(false);
        tabTicket.setContent(previewBox);

        Tab tabGraf = new Tab("Ganancias de la semana");
        tabGraf.setClosable(false);
        VBox graf = new VBox();
        graf.setPadding(new Insets(20));
        graf.getChildren().add(new Label("Aquí irá la gráfica semanal :)"));
        tabGraf.setContent(graf);

        tabs.getTabs().addAll(tabTicket, tabGraf);

        VBox zonaCentral = new VBox(25, caja1, tabs);
        zonaCentral.setPadding(new Insets(20));

        // TABLA DERECHA
        TableView<String> tabla = new TableView<>();
        tabla.getItems().add("Ejemplo vacío");

        VBox reporteBox = new VBox(20,
                new Label("Reporte de servicios prestados"),
                tabla
        );
        reporteBox.getStyleClass().add("caja-reporte");

        // ROOT
        HBox rootContent = new HBox(side, zonaCentral, reporteBox);
        StackPane root = new StackPane(rootContent);

        // ============================================================
        // EVENTOS
        // ============================================================

        btnAgregar.setOnAction(e -> {

            String nombre = txtNombre.getText().trim();
            String correo = txtCorreo.getText().trim();
            String tipo = comboTipo.getValue();

            if (nombre.isEmpty()) {
                System.out.println("[UI] Nombre vacío.");
                return;
            }

            if (ticketActual == null) {
                crearNuevoTicket(nombre, correo);
            }

            agregarServicioDummy(tipo);
            actualizarPreview(previewBox);
            actualizarTabla(tabla);

            txtNombre.clear();
            txtCorreo.clear();
        });

        Scene scene = new Scene(root, 1280, 720);
        scene.getStylesheets().add(
                getClass().getResource("Styles/Styles.css").toExternalForm()
        );

        stage.setScene(scene);
        stage.setTitle("Sistema de Tickets");
        stage.show();
    }
}
