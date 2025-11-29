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
import model.Renta;
import model.Reporte;
import model.Servicio;
import model.Ticket;
import model.Ubicacion;

public class ServiciosGUI {

    // ============================================================
    // 1. CONTROLADORES REALES
    // ============================================================
    private final TicketController ticketController;
    private final VentaController ventaController;
    private final RentaController rentaController;
    private final ReporteController reporteController;
    private final EtiquetaController etiquetaController;

    // ============================================================
    // 2. ESTADO INTERNO
    // ============================================================
    private Ticket ticketActual = null;


    // ============================================================
    // 3. CONSTRUCTOR
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

        // Renta necesita referencia al reporte
        rentaController.setReporteController(reporteController);
    }


    // ============================================================
    // 4. LÓGICA REAL BASADA EN PruebasGUI (SIN INVENTAR NADA)
    // ============================================================

    private void crearNuevoTicket(String nombre, String correo) {

        if (correo == null || correo.isBlank()) {
            correo = "correo@desconocido.com";
        }

        ticketActual = ticketController.crearNuevoTicket(nombre, correo);

        System.out.println("[LOG] Ticket creado: " + ticketActual.getTicketId());
    }


    private void agregarServicioAlTicket(String tipo) {

        if (ticketActual == null) {
            System.out.println("[WARN] No hay ticket actual.");
            return;
        }

        // DEMO EXACTO DEL PruebasGUI:
        if (!tipo.equalsIgnoreCase("Renta")) {
            ventaController.registrarVenta(
                    1,           // ID DEMO
                    2,           // cantidad DEMO
                    ticketActual,
                    ticketController
            );
        } else {
            rentaController.iniciarRenta(
                    Ubicacion.PA_T1_L1,
                    ticketActual,
                    ticketController
            );
        }

        System.out.println("[LOG] Servicio agregado: " + tipo);
    }


    private void actualizarTablaReporte(TableView<String> tabla) {

        tabla.getItems().clear();

        Reporte reporte = reporteController.getReporte();

        for (Ticket t : reporte.getTickets()) {
            tabla.getItems().add(t.getNombreCliente());
        }
    }


    // ============================================================
    // 5. PREVIEW TICKET (COPIADO DE PRUEBASGUI)
    // ============================================================

    private void actualizarPreviewTicket(VBox box) {

        box.getChildren().clear();

        if (ticketActual == null) {
            box.getChildren().add(new Label("Aún no hay ticket creado."));
            return;
        }

        // TÍTULO
        Label titulo = new Label("=== Ticket ID: " + ticketActual.getTicketId() + " ===");
        titulo.setStyle("-fx-font-size: 22; -fx-font-weight: bold;");

        // CLIENTE
        Label cliente = new Label("Cliente: " + ticketActual.getNombreCliente());

        if (ticketActual.getCorreoCliente() != null &&
                !ticketActual.getCorreoCliente().isBlank()) {
            box.getChildren().add(new Label("Correo: " + ticketActual.getCorreoCliente()));
        }

        // SERVICIOS
        Label servHeader = new Label("Servicios:");
        servHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 16;");

        VBox listaServicios = new VBox(5);

        if (ticketActual.getServicios().isEmpty()) {
            listaServicios.getChildren().add(new Label("  (Sin servicios)"));
        } else {
            for (Servicio s : ticketActual.getServicios()) {

                if (s.getTipoServicio() instanceof model.Venta venta) {
                    listaServicios.getChildren().add(new Label(
                            "[VENTA] " + venta.getNombre() +
                                    " | Cant: " + venta.getCantidad() +
                                    " | Precio: $" + venta.getPrecio() +
                                    " | Total: $" + s.getTotalServicio()
                    ));
                }

                if (s.getTipoServicio() instanceof Renta renta) {
                    listaServicios.getChildren().add(new Label(
                            "[RENTA] " +
                                    renta.getUbicacion() +
                                    " | Inicio: " + renta.getInicioRenta() +
                                    " | Cierre: " + renta.getCierreRenta() +
                                    " | Total: $" + s.getTotalServicio()
                    ));
                }
            }
        }

        // TOTAL
        Label total = new Label("Total: $" + ticketActual.getTotalTicket());
        total.setStyle("-fx-font-weight: bold; -fx-font-size: 18;");

        box.getChildren().addAll(
                titulo, cliente,
                servHeader, listaServicios,
                new Label("-------------------------"),
                total
        );
    }


    // ============================================================
    // 6. UI PRINCIPAL (BONITA + CORREGIDA)
    // ============================================================
    public void mostrar(Stage stage) {

        // ---------------- PANEL LATERAL ----------------
        VBox side = new VBox(25);
        side.getStyleClass().add("sidebar");

        Label op1 = new Label("Servicios");
        Label op2 = new Label("Renta");
        op1.getStyleClass().add("menu-item");
        op2.getStyleClass().add("menu-item");
        side.getChildren().addAll(op1, op2);

        // ---------------- FORMULARIO ----------------
        VBox caja1 = new VBox(20);
        caja1.getStyleClass().add("caja-form");
        caja1.setPrefWidth(460);

        Label tituloForm = new Label("Crear ticket de venta");
        tituloForm.getStyleClass().add("titulo1");

        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Nombre del cliente");

        TextField txtCorreo = new TextField();
        txtCorreo.setPromptText("Correo (opcional)");

        ComboBox<String> combo = new ComboBox<>();
        combo.getItems().addAll("Consumible", "Impresión", "Agregar", "Trámite", "Renta");

        Button btnAgregar = new Button("Agregar");
        btnAgregar.getStyleClass().add("btn-green");

        caja1.getChildren().addAll(
                tituloForm,
                txtNombre,
                txtCorreo,
                new Label("Tipo de servicio"),
                combo,
                btnAgregar
        );

        // ---------------- PREVIEW ----------------
        VBox preview = new VBox(10);
        preview.setPadding(new Insets(15));
        preview.setStyle("-fx-background-color:white; -fx-background-radius:10;");
        preview.setPrefHeight(420);
        preview.setMinHeight(420);

        // ---------------- TABS ----------------
        TabPane tabs = new TabPane();

        Tab tabTicket = new Tab("Ticket");
        tabTicket.setClosable(false);
        tabTicket.setContent(preview);

        Tab tabGrafica = new Tab("Ganancias de la semana");
        tabGrafica.setClosable(false);
        tabGrafica.setContent(new Label("Aquí irá la gráfica semanal :)"));

        tabs.getTabs().addAll(tabTicket, tabGrafica);

        VBox zonaCentral = new VBox(25, caja1, tabs);
        zonaCentral.setPadding(new Insets(20));

        // ---------------- TABLA DERECHA ----------------
        TableView<String> tabla = new TableView<>();
        tabla.setPrefWidth(300);

        VBox reporteBox = new VBox(20, new Label("Reporte del día"), tabla);
        reporteBox.getStyleClass().add("caja-reporte");

        // ---------------- ROOT FINAL ----------------
        HBox rootContent = new HBox(side, zonaCentral, reporteBox);
        StackPane root = new StackPane(rootContent);
        root.getStyleClass().add("root");

        // ============================================================
        // EVENTO: AGREGAR
        // ============================================================
        btnAgregar.setOnAction(e -> {

            String nombre = txtNombre.getText().trim();
            String correo = txtCorreo.getText().trim();
            String tipo = combo.getValue();

            if (nombre.isEmpty()) {
                System.out.println("[UI] Nombre vacío.");
                return;
            }

            if (ticketActual == null) {
                crearNuevoTicket(nombre, correo);
            }

            agregarServicioAlTicket(tipo);

            actualizarPreviewTicket(preview);
            actualizarTablaReporte(tabla);

            // CAMBIAR AUTOMÁTICAMENTE A LA PESTAÑA “Ticket”
            tabs.getSelectionModel().select(tabTicket);

            // Resetear para permitir crear otro ticket nuevo
            ticketActual = null;

            txtNombre.clear();
            txtCorreo.clear();
        });

        // ============================================================
        // MOSTRAR LA VENTANA
        // ============================================================
        Scene scene = new Scene(root, 1280, 720);
        scene.getStylesheets().add(
                getClass().getResource("Styles/Styles.css").toExternalForm()
        );

        stage.setScene(scene);
        stage.setTitle("Servicios - LockerEasy");
        stage.show();
    }
}
