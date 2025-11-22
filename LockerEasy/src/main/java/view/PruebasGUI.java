package view;

import controller.RentaController;
import controller.ReporteController;
import controller.TicketController;
import controller.VentaController;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Ticket;
import model.Ubicacion;

public class PruebasGUI {

    // ------------------ CONTROLADORES REALES ------------------
    private final ReporteController reporteController = new ReporteController();
    private final TicketController ticketController = new TicketController();
    private final RentaController rentaController = new RentaController();
    private final VentaController ventaController = new VentaController();

    // Último ticket creado
    private Ticket ticketActual = null;

    public void mostrar(Stage stage) {

        // Necesito inyectar estas dependencias EXACTO como en App.java:
        rentaController.setReporteController(reporteController);

        TextArea consola = new TextArea();
        consola.setPrefHeight(350);
        consola.setEditable(false);

        // ------------------- CREAR TICKET -------------------
        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Nombre cliente");

        TextField txtCorreo = new TextField();
        txtCorreo.setPromptText("Correo cliente");

        Button btnCrearTicket = new Button("Crear Ticket");
        btnCrearTicket.setOnAction(e -> {
            String nombre = txtNombre.getText().trim();
            String correo = txtCorreo.getText().trim();

            if (nombre.isEmpty() || correo.isEmpty()) {
                consola.appendText("[ERROR] Debes llenar nombre y correo.\n");
                return;
            }

            ticketActual = ticketController.crearNuevoTicket(nombre, correo);

            consola.appendText("\n=== Ticket creado ===\n");
            consola.appendText("ID: " + ticketActual.getTicketId() + "\n");
            consola.appendText("Cliente: " + ticketActual.getNombreCliente() + "\n");
        });

        VBox boxCrear = new VBox(5,
                new Label("Crear Ticket"),
                txtNombre,
                txtCorreo,
                btnCrearTicket
        );

        // ------------------- ACCIONES -------------------
        ChoiceBox<String> acciones = new ChoiceBox<>();
        acciones.getItems().addAll("Iniciar renta", "Finalizar renta", "Registrar venta");
        acciones.setValue("Iniciar renta");

        Button btnAccion = new Button("Ejecutar acción");

        btnAccion.setOnAction(e -> {
            if (ticketActual == null) {
                consola.appendText("[ERROR] Primero crea un ticket.\n");
                return;
            }

            String accion = acciones.getValue();

            switch (accion) {
                case "Iniciar renta" -> {
                    consola.appendText("\n--- Iniciando renta ---\n");

                    boolean okR = rentaController.iniciarRenta(
                            Ubicacion.PA_T1_L1,
                            ticketActual,
                            ticketController
                    );

                    consola.appendText(okR ?
                            "Renta iniciada.\nTotal: $" + ticketActual.getTotalTicket() + "\n"
                            : "ERROR: No se pudo iniciar la renta.\n");
                }

                case "Finalizar renta" -> {
                    consola.appendText("\n--- Finalizando renta ---\n");

                    boolean okF = rentaController.finalizarRenta(
                            Ubicacion.PA_T1_L1, ticketActual, ticketController
                    );

                    consola.appendText(okF ?
                            "Renta finalizada.\nTotal final: $" + ticketActual.getTotalTicket() + "\n"
                            : "ERROR: No se pudo finalizar la renta.\n");
                }

                case "Registrar venta" -> {
                    consola.appendText("\n--- Registrando venta ---\n");

                    boolean okV = ventaController.registrarVenta(
                            1,      // ID producto
                            2,      // Cantidad
                            ticketActual,
                            ticketController
                    );

                    consola.appendText(okV ?
                            "Venta registrada.\nTotal ahora: $" + ticketActual.getTotalTicket() + "\n"
                            : "ERROR: No se pudo hacer la venta.\n");
                }
                default -> {
                    // No hacer nada
                }
            }
        });

        VBox boxAcciones = new VBox(5,
                new Label("Acción"),
                acciones,
                btnAccion
        );

        // ------------------- REPORTE -------------------
        Button btnReporte = new Button("Mostrar reporte del día");
        btnReporte.setOnAction(e -> {
            var rep = reporteController.getReporte();

            consola.appendText("\n=== REPORTE DEL DÍA ===\n");
            consola.appendText("Fecha: " + rep.getFechaReporte() + "\n");
            consola.appendText("Tickets: " + rep.getTickets().size() + "\n");
            consola.appendText("Total del día: $" + rep.getTotal() + "\n");
        });

        VBox boxReporte = new VBox(5, new Label("Reportes"), btnReporte);

        // ------------------- LAYOUT -------------------
        HBox filaArriba = new HBox(20, boxCrear, boxAcciones, boxReporte);
        filaArriba.setPadding(new Insets(10));

        VBox root = new VBox(15, filaArriba, consola);
        root.setPadding(new Insets(10));

        Scene scene = new Scene(root, 1200, 600);
        stage.setScene(scene);
        stage.setTitle("Pruebas del Sistema - LockerEasy");
        stage.show();
    }
}
