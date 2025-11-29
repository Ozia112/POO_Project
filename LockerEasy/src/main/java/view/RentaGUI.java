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

import java.time.Instant;

public class RentaGUI {

    // =====================================================
    // CONTROLADORES
    // =====================================================
    private final RentaController rentaController;
    private final TicketController ticketController;
    private final ReporteController reporteController;

    // =====================================================
    // UI ELEMENTOS
    // =====================================================
    private GridPane gridLockers;
    private ListView<Renta> listaRentas;
    private VBox panelDetalle;

    // =====================================================
    // CONSTRUCTOR
    // =====================================================
    public RentaGUI(
            RentaController rentaController,
            TicketController ticketController,
            ReporteController reporteController
    ) {
        this.rentaController = rentaController;
        this.ticketController = ticketController;
        this.reporteController = reporteController;
    }

    // =====================================================
    // CONSTRUIR CUADRÍCULA DE LOCKERS
    // Igualito a PRUEBASGUI pero en forma de botones
    // =====================================================
    private void construirGridLockers() {

        gridLockers = new GridPane();
        gridLockers.setHgap(15);
        gridLockers.setVgap(15);

        int col = 0;
        int row = 0;

        for (Ubicacion u : Ubicacion.values()) {

            Button b = new Button(u.name());
            b.setPrefWidth(120);
            b.setPrefHeight(40);

            actualizarColorLocker(b, u);

            b.setOnAction(e -> manejarClickLocker(u));

            gridLockers.add(b, col, row);

            col++;
            if (col == 4) {
                col = 0;
                row++;
            }
        }
    }

    // =====================================================
    // ACTUALIZAR COLOR SEGÚN ESTADO (igual que PruebasGUI)
    // =====================================================
    private void actualizarColorLocker(Button b, Ubicacion u) {
        boolean ocupado = rentaController.getRentaActiva(u) != null;

        if (!ocupado) {
            b.setStyle("-fx-background-color: #7ED477; -fx-font-weight: bold;");
        } else {
            b.setStyle("-fx-background-color: #FF8A8A; -fx-font-weight: bold;");
        }
    }

    private void actualizarColoresLockers() {
        for (var node : gridLockers.getChildren()) {
            if (node instanceof Button b) {
                Ubicacion u = Ubicacion.valueOf(b.getText());
                actualizarColorLocker(b, u);
            }
        }
    }

    // =====================================================
    // MANEJAR CLICK EN LOCKER (LÓGICA REAL → PruebasGUI)
    // =====================================================
    private void manejarClickLocker(Ubicacion u) {

        // ¿Hay renta activa?
        Renta activa = rentaController.getRentaActiva(u);

        if (activa == null) {
            // NO HAY RENTA → iniciar nueva
            Ticket t = ticketController.crearNuevoTicket("Cliente", "desconocido@mail.com");

            boolean ok = rentaController.iniciarRenta(
                    u,
                    t,
                    ticketController
            );

            if (ok) {
                reporteController.getReporte().getTickets().add(t);
                ticketController.guardarTicket(t);
                cargarRentasEnLista();
                mostrarDetalle(rentaController.getRentaActiva(u));
                actualizarColoresLockers();
            }

        } else {
            // YA HAY → mostrar detalles
            mostrarDetalle(activa);
        }
    }

    // =====================================================
    // LISTA IZQUIERDA: RENTAS ACTIVAS (igual que PruebasGUI)
    // =====================================================
    private void cargarRentasEnLista() {
        listaRentas.getItems().clear();

        for (Ubicacion u : Ubicacion.values()) {
            Renta r = rentaController.getRentaActiva(u);

            if (r != null) listaRentas.getItems().add(r);
        }
    }

    // =====================================================
    // MOSTRAR DETALLE (SIN INVENTAR MÉTODOS)
    // =====================================================
    private void mostrarDetalle(Renta r) {

        panelDetalle.getChildren().clear();

        if (r == null) {
            panelDetalle.getChildren().add(new Label("Selecciona un locker..."));
            return;
        }

        Ticket t = rentaController.getTicketDeRenta(r.getUbicacion());

        Label titulo = new Label("Locker: " + r.getUbicacion());
        titulo.setStyle("-fx-font-size: 22; -fx-font-weight: bold;");

        Label cliente = new Label("Cliente: " + t.getNombreCliente());
        Label inicio = new Label("Inicio: " + r.getInicioRenta());
        Label cierre = new Label("Cierre: " +
                (r.getCierreRenta() == null ? "Abierto" : r.getCierreRenta()));

        // Calcular precio (solo usando métodos existentes)
        int horas = rentaController.calcularTiempoTrancurrido(r, Instant.now());
        double total = horas * r.getPrecio();

        Label totalLbl = new Label("Total acumulado: $" + total);
        totalLbl.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        Button cerrar = new Button("Cerrar renta");
        cerrar.setStyle("-fx-background-color:#5da36c; -fx-font-weight: bold;");

        cerrar.setOnAction(e -> {

            boolean ok = rentaController.finalizarRenta(
                    r.getUbicacion(),
                    t,
                    ticketController
            );

            if (ok) {
                ticketController.guardarTicket(t);
                reporteController.getReporte().getTickets().add(t);

                rentaController.liberarUbicacion(r.getUbicacion());
                cargarRentasEnLista();
                mostrarDetalle(null);
                actualizarColoresLockers();
            }
        });

        panelDetalle.getChildren().addAll(
                titulo, cliente, inicio, cierre, totalLbl, cerrar
        );
    }

    // =====================================================
    // MOSTRAR UI PRINCIPAL
    // =====================================================
    public void mostrar(Stage stage) {

        construirGridLockers();

        listaRentas = new ListView<>();
        panelDetalle = new VBox(10);

        listaRentas.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            mostrarDetalle(newV);
        });

        cargarRentasEnLista();

        HBox root = new HBox(
                20,
                new VBox(new Label("Lockers activos"), listaRentas),
                gridLockers,
                panelDetalle
        );

        root.setPadding(new Insets(20));

        Scene scene = new Scene(root, 1280, 720);
        stage.setTitle("Renta de Lockers - LockerEasy");
        stage.setScene(scene);
        stage.show();
    }
}
