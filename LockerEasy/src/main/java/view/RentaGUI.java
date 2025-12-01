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
import java.util.List;

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

    private void construirGridLockers() {
        gridLockers = new GridPane();
        gridLockers.setHgap(15);
        gridLockers.setVgap(15);

        // Obtener ubicaciones desde BD
        List<Ubicacion> ubicaciones = rentaController.getUbicacionDAO().obtenerTodas();

        int col = 0;
        int row = 0;

        for (Ubicacion u : ubicaciones) {
            Button b = new Button(u.getNombreLocker());
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

    private void actualizarColorLocker(Button b, Ubicacion u) {
        boolean disponible = rentaController.estaDisponible(u);

        if (disponible) {
            b.setStyle("-fx-background-color: #7ED477; -fx-font-weight: bold;");
        } else {
            b.setStyle("-fx-background-color: #FF8A8A; -fx-font-weight: bold;");
        }
    }

    private void actualizarColoresLockers() {
        List<Ubicacion> ubicaciones = rentaController.getUbicacionDAO().obtenerTodas();
        
        int index = 0;
        for (var node : gridLockers.getChildren()) {
            if (node instanceof Button b && index < ubicaciones.size()) {
                actualizarColorLocker(b, ubicaciones.get(index));
                index++;
            }
        }
    }

    private void manejarClickLocker(Ubicacion u) {
        Renta activa = rentaController.getRenta(u);

        if (activa == null) {
            // No hay renta → iniciar nueva
            Ticket t = ticketController.crearNuevoTicket("Cliente", "desconocido@mail.com");
            ticketController.getTicketDAO().guardar(t);

            boolean ok = rentaController.iniciarRenta(u, t);

            if (ok) {
                cargarRentasEnLista();
                mostrarDetalle(rentaController.getRenta(u));
                actualizarColoresLockers();
            }
        } else {
            // Ya hay renta → mostrar detalles
            mostrarDetalle(activa);
        }
    }

    private void cargarRentasEnLista() {
        listaRentas.getItems().clear();
        
        List<Ubicacion> ubicaciones = rentaController.getUbicacionDAO().obtenerTodas();
        
        for (Ubicacion u : ubicaciones) {
            Renta r = rentaController.getRenta(u);
            if (r != null && r.getCierreRenta() == null) {
                listaRentas.getItems().add(r);
            }
        }
    }

    private void mostrarDetalle(Renta r) {
        panelDetalle.getChildren().clear();

        if (r == null) {
            panelDetalle.getChildren().add(new Label("Selecciona un locker..."));
            return;
        }

        Ticket t = rentaController.getTicketDeRenta(r.getUbicacion());

        Label titulo = new Label("Locker: " + r.getUbicacion().getNombreLocker());
        titulo.setStyle("-fx-font-size: 22; -fx-font-weight: bold;");

        Label cliente = new Label("Cliente: " + (t != null ? t.getNombreCliente() : "Desconocido"));
        Label inicio = new Label("Inicio: " + r.getInicioRenta());
        Label cierre = new Label("Cierre: " + (r.getCierreRenta() == null ? "Abierto" : r.getCierreRenta()));

        int horas = rentaController.calcularTiempoTrancurrido(r, Instant.now());
        double total = horas * r.getPrecio();

        Label totalLbl = new Label("Total acumulado: $" + String.format("%.2f", total));
        totalLbl.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        Button cerrar = new Button("Cerrar renta");
        cerrar.setStyle("-fx-background-color:#5da36c; -fx-font-weight: bold;");

        cerrar.setOnAction(e -> {
            if (t == null) {
                mostrarAlerta("Error", "No se encontró el ticket asociado");
                return;
            }

            boolean ok = rentaController.finalizarRenta(r.getUbicacion(), t);

            if (ok) {
                cargarRentasEnLista();
                mostrarDetalle(null);
                actualizarColoresLockers();
            }
        });

        panelDetalle.getChildren().addAll(titulo, cliente, inicio, cierre, totalLbl, cerrar);
    }

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

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
