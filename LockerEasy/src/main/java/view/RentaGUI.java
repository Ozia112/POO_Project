package view;

import controller.RentaController;
import controller.ReporteController;
import controller.TicketController;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Renta;
import model.Ticket;
import model.Ubicacion;

public class RentaGUI {

    private final RentaController rentaController;
    private final TicketController ticketController;
    private final ReporteController reporteController;

    private GridPane gridLockers;
    private ListView<Renta> listaRentas;
    private VBox panelDetalle;

    public RentaGUI(
            RentaController rentaController,
            TicketController ticketController,
            ReporteController reporteController
    ) {
        this.rentaController = rentaController;
        this.ticketController = ticketController;
        this.reporteController = reporteController;
    }


    // ============================================
    // CONSTRUIR GRID DE LOCKERS
    // ============================================
    private void construirGridLockers() {

        gridLockers = new GridPane();
        gridLockers.setHgap(15);
        gridLockers.setVgap(15);

        int col = 0, row = 0;

        for (Ubicacion u : Ubicacion.values()) {

            Button b = new Button(u.name());
            b.setPrefWidth(120);
            b.setPrefHeight(45);

            actualizarColorLocker(b, u);

            b.setOnAction(e -> manejarClickLocker(u));

            gridLockers.add(b, col, row);

            row++;
            if (row == 4) {
                row = 0;
                col++;
            }
        }
    }


    // ============================================
    // COLOR DEL LOCKER (verde si libre, rojo si ocupado)
    // ============================================
    private void actualizarColorLocker(Button b, Ubicacion u) {

        Renta r = rentaController.getRentaActiva(u);

        if (r == null) {
            b.setStyle("-fx-background-color: #85c285; -fx-font-weight: bold;");
        } else {
            b.setStyle("-fx-background-color: #c26c6c; -fx-font-weight: bold;");
        }
    }


    // ============================================
    // CLICK EN LOCKER (INICIAR O MOSTRAR RENTA)
    // ============================================
    private void manejarClickLocker(Ubicacion u) {

        Renta activa = rentaController.getRentaActiva(u);

        if (activa == null) {

            // Crear nuevo ticket
            Ticket t = ticketController.crearNuevoTicket("Cliente", "correo@desconocido.com");

            // Iniciar renta REAL (regresa boolean)
            boolean ok = rentaController.iniciarRenta(u, t, ticketController);

            if (!ok) {
                System.out.println("[ERROR] No se pudo iniciar la renta.");
                return;
            }

            // Guardar ticket y añadir a reporte
            ticketController.guardarTicket(t);
            reporteController.getReporte().getTickets().add(t);

            cargarRentasEnLista();
            mostrarDetalle(rentaController.getRentaActiva(u));
            actualizarColoresLockers();

        } else {
            // Ya existe renta → mostrar detalles
            mostrarDetalle(activa);
        }
    }


    // ============================================
    // LISTA DE RENTAS ACTIVAS (solo las que existan)
    // ============================================
    private void cargarRentasEnLista() {

        listaRentas.getItems().clear();

        for (Ubicacion u : Ubicacion.values()) {
            Renta r = rentaController.getRentaActiva(u);
            if (r != null) listaRentas.getItems().add(r);
        }
    }


    // ============================================
    // MOSTRAR DETALLE (SIN INVENTAR MÉTODOS)
    // ============================================
    private void mostrarDetalle(Renta r) {

        panelDetalle.getChildren().clear();

        if (r == null) {
            panelDetalle.getChildren().add(new Label("Selecciona un locker para ver detalles."));
            return;
        }

        // NO EXISTE getTicket() → así que no mostramos datos del objeto Ticket
        Label titulo = new Label("Locker: " + r.getUbicacion());
        titulo.setStyle("-fx-font-size: 22; -fx-font-weight: bold;");

        Label inicio = new Label("Inicio: " + r.getInicioRenta());
        Label cierre = new Label("Cierre: " + (r.getCierreRenta() == null ? "Abierto" : r.getCierreRenta()));

        // NO EXISTE total de renta → no mostramos precio

        Button cerrar = new Button("Cerrar renta");
        cerrar.setStyle("-fx-background-color: #5da36c; -fx-font-weight: bold;");

        cerrar.setOnAction(e -> {

            rentaController.cerrarRenta(r.getUbicacion(), ticketController);

            // Guardar última información del ticket en reporte
            Ticket t = ticketController.getTicketById(r.getTicketId());
            if (t != null) {
                ticketController.guardarTicket(t);
                reporteController.getReporte().getTickets().add(t);
            }

            cargarRentasEnLista();
            mostrarDetalle(null);
            actualizarColoresLockers();
        });

        panelDetalle.getChildren().addAll(titulo, inicio, cierre, cerrar);
    }


    // ============================================
    // RECOLOREA LOCKERS
    // ============================================
    private void actualizarColoresLockers() {

        for (javafx.scene.Node n : gridLockers.getChildren()) {
            if (n instanceof Button b) {
                Ubicacion u = Ubicacion.valueOf(b.getText());
                actualizarColorLocker(b, u);
            }
        }
    }


    // ============================================
    // MOSTRAR UI
    // ============================================
    public void mostrar(Stage stage) {

        construirGridLockers();

        listaRentas = new ListView<>();
        cargarRentasEnLista();

        listaRentas.getSelectionModel().selectedItemProperty().addListener((obs, ov, nv) -> {
            mostrarDetalle(nv);
        });

        panelDetalle = new VBox(10);
        panelDetalle.setPadding(new Insets(15));

        VBox leftBox = new VBox(15, new Label("Lockers disponibles"), gridLockers);
        leftBox.setPadding(new Insets(20));

        VBox rightBox = new VBox(15, new Label("Rentas activas"), listaRentas, panelDetalle);
        rightBox.setPadding(new Insets(20));

        HBox root = new HBox(leftBox, rightBox);

        Scene sc = new Scene(root, 1280, 720);
        stage.setScene(sc);
        stage.setTitle("Renta de Lockers - LockerEasy");
        stage.show();
    }
}
