package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ServiciosGUI {

    // referencia a la lógica (controllers envueltos)
    private final PruebasGUI logica;

    public ServiciosGUI(PruebasGUI logica) {
        this.logica = logica;
    }

    // ---------- helper: mostrar popup del ticket ----------
    private void mostrarPopupTicket(String nombre, StackPane root) {
        TicketGUI tg = new TicketGUI();
        VBox popup = tg.crearTicket(nombre);

        StackPane.setAlignment(popup, Pos.CENTER);
        root.getChildren().add(popup);

        Button cerrar = (Button) popup.lookup("#btnCerrar");
        if (cerrar != null) {
            cerrar.setOnAction(e -> root.getChildren().remove(popup));
        }
    }

    // ============================================================
    //   UI PRINCIPAL
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

        // ---------------- CAJA PRINCIPAL ----------------
        VBox caja1 = new VBox(20);
        caja1.getStyleClass().add("caja-form");
        caja1.setPrefWidth(460);

        Label titulo = new Label("Crear ticket de venta");
        titulo.getStyleClass().add("titulo1");

        Label lblCliente = new Label("Cliente");
        lblCliente.getStyleClass().add("label");

        TextField txtCliente = new TextField();
        txtCliente.getStyleClass().add("text-field");
        txtCliente.setPromptText("Nombre cliente");

        Label lblTipo = new Label("Tipo");
        lblTipo.getStyleClass().add("label");

        ComboBox<String> comboTipo = new ComboBox<>();
        comboTipo.getStyleClass().add("combo-box");
        comboTipo.getItems().addAll(
                "Renta - Casillero",
                "Impresión",
                "Alimentos",
                "Otro"
        );

        Button btnAgregarCliente = new Button("Agregar cliente");
        btnAgregarCliente.getStyleClass().add("btn-green");

        Button btnAgregarServicio = new Button("Agregar servicio");
        btnAgregarServicio.getStyleClass().add("btn-green");

        HBox contBotonServicio = new HBox(btnAgregarServicio);
        contBotonServicio.setStyle("-fx-alignment: center-right;");

        HBox contBotonCliente = new HBox(btnAgregarCliente);
        contBotonCliente.setStyle("-fx-alignment: center-left;");

        caja1.getChildren().addAll(
                titulo,
                lblCliente,
                txtCliente,
                lblTipo,
                comboTipo,
                contBotonServicio,
                contBotonCliente
        );

        // ---------------- GRAFICA FAKE ----------------
        Label semanaBtn = new Label("Semana");
        semanaBtn.getStyleClass().add("pill");

        Label mesBtn = new Label("Mes");
        mesBtn.getStyleClass().add("pill");

        HBox filtroGrafica = new HBox(10, semanaBtn, mesBtn);

        Pane graficaFake = new Pane();
        graficaFake.getStyleClass().add("graph-placeholder");

        VBox caja2 = new VBox(10, filtroGrafica, graficaFake);
        caja2.getStyleClass().add("caja-grafica");

        // ---------------- ZONA CENTRAL ----------------
        VBox zonaCentral = new VBox(25, caja1, caja2);
        zonaCentral.setPadding(new Insets(20));

        // ---------------- TABLA DERECHA ----------------
        Label tituloReporte = new Label("Reporte de servicios prestados");
        tituloReporte.getStyleClass().add("titulo-reporte");

        TableView<String> tabla = new TableView<>();
        tabla.getItems().add("Ejemplo vacío");

        VBox reporteBox = new VBox(20, tituloReporte, tabla);
        reporteBox.getStyleClass().add("caja-reporte");

        // ---------------- ROOT PRINCIPAL ----------------
        StackPane root = new StackPane(
                new HBox(side, zonaCentral, reporteBox)
        );
        root.getStyleClass().add("root");

        // ==================== EVENTOS ====================

        // Agregar cliente → crea ticket + mete nombre a la tabla
        btnAgregarCliente.setOnAction(e -> {
            String nombre = txtCliente.getText().trim();
            if (nombre.isEmpty()) {
                System.out.println("[UI] Nombre vacío, no creo ticket.");
                return;
            }

            // 1) lógica (controllers)
            logica.crearNuevoTicket(nombre);

            // 2) UI (tabla)
            tabla.getItems().add(nombre);

            txtCliente.clear();
        });

        // Agregar servicio según tipo seleccionado
        btnAgregarServicio.setOnAction(e -> {
            String tipo = comboTipo.getValue();
            if (tipo == null) {
                System.out.println("[UI] No hay tipo seleccionado.");
                return;
            }

            if (tipo.startsWith("Renta")) {
                logica.iniciarRentaDemo();
            } else {
                logica.registrarServicioEnTicket(tipo);
            }
        });

        // Click en tabla → mostrar popup de ticket
        tabla.setOnMouseClicked(event -> {
            String nombre = tabla.getSelectionModel().getSelectedItem();
            if (nombre != null) {
                mostrarPopupTicket(nombre, root);
            }
        });

        // -------------------------------------------------
        // MOSTRAR ESCENA
        // -------------------------------------------------
        Scene scene = new Scene(root, 1280, 720);
        scene.getStylesheets().add(
                getClass().getResource("Styles/Styles.css").toExternalForm()
        );

        stage.setScene(scene);
        stage.setTitle("Sistema de Tickets");
        stage.show();
    }
}
