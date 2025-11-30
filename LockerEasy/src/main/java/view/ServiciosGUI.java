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

import view.VentaGUI;
import view.RentaGUI;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class ServiciosGUI {

    private final TicketController ticketController;
    private final VentaController ventaController;
    private final RentaController rentaController;
    private final ReporteController reporteController;
    private final EtiquetaController etiquetaController;

    private Ticket ticketActual = null;

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

    private void crearNuevoTicket(String nombre, String correo) {

        if (correo == null || correo.isBlank())
            correo = "correo@desconocido.com";

        ticketActual = ticketController.crearNuevoTicket(nombre, correo);

        // Agregar al reporte
        reporteController.getReporte().getTickets().add(ticketActual);
        ticketController.guardarTicket(ticketActual);

        System.out.println("[LOG] Ticket creado: " + ticketActual.getTicketId());
    }

    private void actualizarTablaDia(TableView<Ticket> tabla) {
        tabla.getItems().clear();
        tabla.getItems().addAll(reporteController.getReporte().getTickets());
    }

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

    private List<File> obtenerReportes() {
        File carpeta = new File("src/main/resources/data/reportes");
        return List.of(carpeta.listFiles((d, name) -> name.endsWith(".json")));
    }

    public void mostrar(Stage stage) {

        // ------------------ LATERAL ------------------
        VBox side = new VBox(25);
        side.getStyleClass().add("sidebar");

        Label lblRenta = new Label("Renta");
        lblRenta.setStyle("-fx-cursor: hand;");
        lblRenta.setOnMouseClicked(e -> {
            Stage rentaStage = new Stage();
            new RentaGUI(rentaController, ticketController, reporteController).mostrar(rentaStage);
        });

        Label lblVentas = new Label("Ventas");
        lblVentas.setStyle("-fx-cursor: hand;");
        lblVentas.setOnMouseClicked(ev -> {
            Stage ventaStage = new Stage();
            new VentaGUI(ventaController).mostrar(ventaStage);
        });

        side.getChildren().addAll(
                new Label("Servicios"),
                lblRenta,
                lblVentas
        );

        // ------------------ FORM ------------------
        VBox form = new VBox(20);
        form.setPrefWidth(420);
        form.getStyleClass().add("caja-form");

        Label tituloForm = new Label("Crear ticket de venta");
        tituloForm.getStyleClass().add("titulo1");

        TextField txtNombre = new TextField();
        TextField txtCorreo = new TextField();

        txtNombre.setPromptText("Nombre del cliente");
        txtCorreo.setPromptText("Correo (opcional)");

        Label lblTipoServicio = new Label("Tipo de servicio");

        ComboBox<String> comboTipo = new ComboBox<>();
        comboTipo.getItems().addAll("Consumible", "Impresión", "Agregar", "Trámite", "Renta");

        // ------------------ LISTA NORMAL ------------------
        List<model.Venta> consumibles = ventaController.obtenerTodosLosProductos();

        VBox listaConsumiblesBox = new VBox(10);
        listaConsumiblesBox.setPadding(new Insets(10));
        listaConsumiblesBox.setStyle("-fx-background-color:white; -fx-background-radius:10;");
        listaConsumiblesBox.setManaged(false);
        listaConsumiblesBox.setVisible(false);

        Label lblCons = new Label("Consumibles disponibles");
        listaConsumiblesBox.getChildren().add(lblCons);

        VBox listaVBox = new VBox(8);

        for (model.Venta v : consumibles) {

            HBox fila = new HBox(10);
            Label lbl = new Label(v.getNombre() + " - $" + v.getPrecio());
            Button btnAdd = new Button("Agregar");
            btnAdd.getStyleClass().add("btn-green");

            btnAdd.setOnAction(e -> {

                if (ticketActual == null) {
                    crearNuevoTicket(txtNombre.getText(), txtCorreo.getText());
                    mostrarTicket(listaVBox, ticketActual);
                }

                ventaController.registrarVenta(
                        v.getIdProducto(),
                        1,
                        ticketActual,
                        ticketController
                );

                mostrarTicket(listaVBox.getParent() instanceof VBox pv ? (VBox) pv : new VBox(), ticketActual);
            });

            fila.getChildren().addAll(lbl, btnAdd);
            listaVBox.getChildren().add(fila);
        }

        listaConsumiblesBox.getChildren().add(listaVBox);

        comboTipo.valueProperty().addListener((obs, old, val) -> {
            boolean mostrar = "Consumible".equals(val);
            listaConsumiblesBox.setManaged(mostrar);
            listaConsumiblesBox.setVisible(mostrar);
        });

        Button btn = new Button("Agregar");
        btn.getStyleClass().add("btn-green");

        VBox boxPreview = new VBox();

        form.getChildren().addAll(
                tituloForm, txtNombre, txtCorreo,
                lblTipoServicio,
                comboTipo,
                listaConsumiblesBox,
                btn
        );

        // ------------------ PANEL INFERIOR ------------------
        VBox preview = new VBox(10);
        preview.setPadding(new Insets(15));
        preview.setStyle("-fx-background-color:white; -fx-background-radius:10;");
        preview.setPrefSize(450, 350);

        VBox grafica = new VBox();
        grafica.setPrefSize(450, 350);
        grafica.setStyle("-fx-background-color:white; -fx-background-radius:10;");
        grafica.setPadding(new Insets(15));
        grafica.getChildren().add(new Label("Gráfica semanal (placeholder)"));

        HBox panelInferior = new HBox(25, preview, grafica);
        panelInferior.setPadding(new Insets(15));

        VBox centro = new VBox(25, form, panelInferior);
        centro.setPadding(new Insets(20));

        // ------------------ DERECHA ------------------
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

        tablaDia.getSelectionModel().selectedItemProperty().addListener((obs, o, nuevo) -> {
            if (nuevo != null) mostrarTicket(preview, nuevo);
        });

        ListView<String> listaReportes = new ListView<>();
        listaReportes.getItems().addAll(
                obtenerReportes().stream().map(File::getName).collect(Collectors.toList())
        );

        TabPane tabsDer = new TabPane();
        Tab tabDia = new Tab("Reporte del día", tablaDia);
        tabDia.setClosable(false);
        Tab tabHist = new Tab("Historial", listaReportes);
        tabHist.setClosable(false);
        tabsDer.getTabs().addAll(tabDia, tabHist);

        VBox derecha = new VBox(20, new Label("Reportes"), tabsDer);
        derecha.setPadding(new Insets(20));

        HBox root = new HBox(side, centro, derecha);

        Scene scene = new Scene(new StackPane(root), 1280, 720);
        scene.getStylesheets().add(
                getClass().getResource("Styles/Styles.css").toExternalForm()
        );

        stage.setTitle("Servicios - LockerEasy");
        stage.setScene(scene);
        stage.show();

        // BOTÓN AGREGAR (general)
        btn.setOnAction(e -> {
            String nombre = txtNombre.getText().trim();
            String correo = txtCorreo.getText().trim();
            String tipo = comboTipo.getValue();

            if (nombre.isEmpty()) {
                System.out.println("[UI] Nombre vacío");
                return;
            }

            if (tipo == null) {
                System.out.println("[UI] Tipo de servicio no seleccionado");
                return;
            }

            if (ticketActual == null)
                crearNuevoTicket(nombre, correo);

            if (!"Consumible".equals(tipo)) {
                ventaController.registrarVenta(1, 1, ticketActual, ticketController);
            }

            mostrarTicket(preview, ticketActual);
            actualizarTablaDia(tablaDia);
        });

        txtNombre.textProperty().addListener((obs, old, newText) -> {
            if (newText != null && !newText.isBlank() && ticketActual == null) {
                crearNuevoTicket(txtNombre.getText(), txtCorreo.getText());
                mostrarTicket(preview, ticketActual);
            }
        });

        actualizarTablaDia(tablaDia);
    }
}
