package view;

import controller.EtiquetaController;
import controller.RentaController;
import controller.ReporteController;
import controller.TicketController;
import controller.VentaController;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import model.Renta;
import model.Servicio;
import model.Ticket;
import model.Ubicacion;
import model.Reporte;

import view.VentaGUI;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class ServiciosGUI {

    // =========================================================
    // CONTROLADORES
    // =========================================================
    private final TicketController ticketController;
    private final VentaController ventaController;
    private final RentaController rentaController;
    private final ReporteController reporteController;
    private final EtiquetaController etiquetaController;

    private Ticket ticketActual = null;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================
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

        rentaController.setReporteController(reporteController);
    }


    // =========================================================
    // CREAR TICKET
    // =========================================================
    private void crearNuevoTicket(String nombre, String correo) {

        if (correo == null || correo.isBlank())
            correo = "correo@desconocido.com";

        ticketActual = ticketController.crearNuevoTicket(nombre, correo);

        // Agregar al reporte del día
        reporteController.getReporte().getTickets().add(ticketActual);

        ticketController.guardarTicket(ticketActual);

        System.out.println("[LOG] Ticket creado: " + ticketActual.getTicketId());
    }


    // =========================================================
    // AGREGAR SERVICIO (SOLO lo que YA existe)
    // =========================================================
    private void agregarServicio(String tipo) {

        if (ticketActual == null) return;

        switch (tipo) {

            case "Consumible":
                ventaController.registrarVenta(1, 1, ticketActual, ticketController);
                break;

            case "Impresión":
                ventaController.registrarVenta(2, 1, ticketActual, ticketController);
                break;

            case "Agregar":
                ventaController.registrarVenta(3, 1, ticketActual, ticketController);
                break;

            case "Trámite":
                ventaController.registrarVenta(1, 1, ticketActual, ticketController);
                break;

            case "Renta":
                rentaController.iniciarRenta(
                        Ubicacion.PA_T1_L1,
                        ticketActual,
                        ticketController
                );
                break;
        }

        System.out.println("[LOG] Servicio agregado: " + tipo);
    }


    // =========================================================
    // TABLA REPORTE DEL DÍA
    // =========================================================
    private void actualizarTablaDia(TableView<Ticket> tabla) {
        tabla.getItems().clear();
        tabla.getItems().addAll(reporteController.getReporte().getTickets());
    }


    // =========================================================
    // PREVIEW TICKET (COMPLETO)
    // =========================================================
    private void mostrarTicket(VBox box, Ticket t) {

        box.getChildren().clear();

        if (t == null) {
            box.getChildren().add(new Label("Aún no hay ticket seleccionado."));
            return;
        }

        Label titulo = new Label("=== Ticket ID: " + t.getTicketId() + " ===");
        titulo.setStyle("-fx-font-size: 22; -fx-font-weight: bold;");

        box.getChildren().add(titulo);
        box.getChildren().add(new Label("Cliente: " + t.getNombreCliente()));
        box.getChildren().add(new Label("Correo: " + t.getCorreoCliente()));

        // Servicios
        box.getChildren().add(new Label("\nServicios:"));

        VBox lista = new VBox(5);

        if (t.getServicios().isEmpty()) {
            lista.getChildren().add(new Label("(Sin servicios)"));
        } else {

            for (Servicio s : t.getServicios()) {

                if (s.getTipoServicio() instanceof model.Venta v) {
                    lista.getChildren().add(new Label(
                            "[VENTA] " + v.getNombre() +
                                    " | Cant: " + v.getCantidad() +
                                    " | Precio: $" + v.getPrecio() +
                                    " | Total: $" + s.getTotalServicio()
                    ));
                }

                if (s.getTipoServicio() instanceof Renta r) {
                    lista.getChildren().add(new Label(
                            "[RENTA] " + r.getUbicacion() +
                                    " | Inicio: " + r.getInicioRenta() +
                                    " | Cierre: " + r.getCierreRenta() +
                                    " | Total: $" + s.getTotalServicio()
                    ));
                }
            }
        }

        box.getChildren().add(lista);

        Label total = new Label("\nTotal: $" + t.getTotalTicket());
        total.setStyle("-fx-font-weight: bold; -fx-font-size: 18;");
        box.getChildren().add(total);
    }


    // =========================================================
    // HISTORIAL REPORTES
    // =========================================================
    private List<File> obtenerReportes() {
        File carpeta = new File("src/main/resources/data/reportes");
        return List.of(carpeta.listFiles((d, name) -> name.endsWith(".json")));
    }


    // =========================================================
    // UI PRINCIPAL
    // =========================================================
    public void mostrar(Stage stage) {

        // ========================= LATERAL =========================
        VBox side = new VBox(25);
        side.getStyleClass().add("sidebar");
        Label lblServicios = new Label("Servicios");
        Label lblRenta = new Label("Renta");
        Button btnVentas = new Button("Ventas");
        btnVentas.setOnAction(ev -> {
            // Abrir ventana de gestión de productos en una nueva Stage
            Stage ventaStage = new Stage();
            new VentaGUI(ventaController).mostrar(ventaStage);
        });

        side.getChildren().addAll(lblServicios, lblRenta, btnVentas);

        // ========================= FORM =========================
        VBox form = new VBox(20);
        form.setPrefWidth(420);
        form.getStyleClass().add("caja-form");

        Label tituloForm = new Label("Crear ticket de venta");
        tituloForm.getStyleClass().add("titulo1");

        TextField txtNombre = new TextField();
        TextField txtCorreo = new TextField();

        txtNombre.setPromptText("Nombre del cliente");
        txtCorreo.setPromptText("Correo (opcional)");

        ComboBox<String> combo = new ComboBox<>();
        combo.getItems().addAll("Consumible", "Impresión", "Agregar", "Trámite", "Renta");

        Button btn = new Button("Agregar");
        btn.getStyleClass().add("btn-green");

        form.getChildren().addAll(
                tituloForm, txtNombre, txtCorreo,
                new Label("Tipo de servicio"),
                combo, btn
        );

        // ========================= PANEL INFERIOR (OPCIÓN B) =========================
        HBox panelInferior = new HBox(25);
        panelInferior.setPadding(new Insets(15));

        // PREVIEW TICKET
        VBox preview = new VBox(10);
        preview.setPadding(new Insets(15));
        preview.setStyle("-fx-background-color:white; -fx-background-radius:10;");
        preview.setPrefSize(450, 350);

        // GRÁFICA (placeholder)
        VBox grafica = new VBox();
        grafica.setPrefSize(450, 350);
        grafica.setStyle("-fx-background-color:white; -fx-background-radius:10;");
        grafica.setPadding(new Insets(15));
        grafica.getChildren().add(new Label("Gráfica semanal (placeholder)"));

        panelInferior.getChildren().addAll(preview, grafica);

        VBox centro = new VBox(25, form, panelInferior);
        centro.setPadding(new Insets(20));


        // ========================= DERECHA =========================

        // TABLA DEL DÍA
        TableView<Ticket> tablaDia = new TableView<>();
        tablaDia.setPrefWidth(380);

        TableColumn<Ticket, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty("#" + c.getValue().getTicketId())
        );

        TableColumn<Ticket, String> colCliente = new TableColumn<>("Cliente");
        colCliente.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().getNombreCliente())
        );

        TableColumn<Ticket, String> colTotal = new TableColumn<>("Total");
        colTotal.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty("$" + c.getValue().getTotalTicket())
        );

        tablaDia.getColumns().addAll(colId, colCliente, colTotal);

        // Seleccionar ticket → actualizar preview
        tablaDia.getSelectionModel().selectedItemProperty().addListener((obs, o, nuevo) -> {
            if (nuevo != null) mostrarTicket(preview, nuevo);
        });

        // HISTORIAL
        ListView<String> listaReportes = new ListView<>();

        listaReportes.getItems().addAll(
                obtenerReportes().stream()
                        .map(File::getName)
                        .collect(Collectors.toList())
        );

        TabPane tabsDer = new TabPane();

        Tab tabDia = new Tab("Reporte del día", tablaDia);
        tabDia.setClosable(false);

        Tab tabHist = new Tab("Historial", listaReportes);
        tabHist.setClosable(false);

        tabsDer.getTabs().addAll(tabDia, tabHist);

        VBox derecha = new VBox(20, new Label("Reportes"), tabsDer);
        derecha.setPadding(new Insets(20));


        // ========================= ROOT =========================
        HBox root = new HBox(side, centro, derecha);

        Scene scene = new Scene(new StackPane(root), 1280, 720);
        scene.getStylesheets().add(
                getClass().getResource("Styles/Styles.css").toExternalForm()
        );

        stage.setTitle("Servicios - LockerEasy");
        stage.setScene(scene);
        stage.show();

        // ========================= EVENTO AGREGAR =========================
        btn.setOnAction(e -> {

            String nombre = txtNombre.getText().trim();
            String correo = txtCorreo.getText().trim();
            String tipo = combo.getValue();

            if (nombre.isEmpty()) {
                System.out.println("[UI] Nombre vacío");
                return;
            }

            if (ticketActual == null)
                crearNuevoTicket(nombre, correo);

            agregarServicio(tipo);

            mostrarTicket(preview, ticketActual);
            actualizarTablaDia(tablaDia);

            ticketActual = null;

            txtNombre.clear();
            txtCorreo.clear();
        });

        actualizarTablaDia(tablaDia);
    }
}
