package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class TicketGUI {

    // ============================================================
    //   POPUP REAL (Ticket dinámico)
    // ============================================================

    public VBox crearTicket(String nombreCliente) {

        // ----------------- BARRA SUPERIOR -----------------
        Label titulo = new Label("Ticket ##");
        titulo.getStyleClass().add("ticket-title");

        Button btnCerrar = new Button("Cerrar");
        btnCerrar.setId("btnCerrar");
        btnCerrar.getStyleClass().add("btn-cerrar");

        HBox barraSuperior = new HBox(titulo, btnCerrar);
        barraSuperior.getStyleClass().add("ticket-top");
        barraSuperior.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(titulo, Priority.ALWAYS); // empuja el boton a la derecha


        // ----------------- CLIENTE -----------------
        Label lblCliente = new Label("Cliente");
        lblCliente.getStyleClass().add("ticket-section-title");

        Label valorCliente = new Label(nombreCliente);
        valorCliente.getStyleClass().add("ticket-value");

        VBox clienteBox = new VBox(5, lblCliente, valorCliente);
        clienteBox.getStyleClass().add("ticket-block");


        // ----------------- SERVICIOS -----------------
        Label lblServicios = new Label("Servicios");
        lblServicios.getStyleClass().add("ticket-section-title");

        HBox encabezadoServicios = new HBox();
        encabezadoServicios.getChildren().addAll(
                new Label("Cant."),
                spacer(),
                new Label("Precio Unit."),
                spacer(),
                new Label("Total")
        );
        encabezadoServicios.getStyleClass().add("ticket-header");


        VBox serviciosBox = new VBox(8, lblServicios, encabezadoServicios);
        serviciosBox.getStyleClass().add("ticket-block");


        // ----------------- RENTAS EN PROCESO -----------------
        Label lblRentas = new Label("Rentas en proceso");
        lblRentas.getStyleClass().add("ticket-section-title");

        // Ejemplo temporal mientras implementan model
        Label ejemploRenta = new Label("• Locker T1 PA 1     00:00 hrs     Abierto     $50");
        ejemploRenta.getStyleClass().add("ticket-item");

        VBox rentasBox = new VBox(8, lblRentas, ejemploRenta);
        rentasBox.getStyleClass().add("ticket-block");


        // ----------------- POPUP COMPLETO -----------------
        VBox popup = new VBox(20, barraSuperior, clienteBox, serviciosBox, rentasBox);
        popup.setPadding(new Insets(20));

        popup.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 15;" +
                "-fx-border-radius: 15;" +
                "-fx-border-color: #b9b9b9;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.22), 18, 0, 0, 4);"
        );

        popup.setPrefWidth(380);
        popup.setMaxWidth(380);
        popup.setId("ticketPopup");

        return popup;
    }




    // ============================================================
    //   DEMO PARA MOSTRAR EN LA TABLA (ticket de ejemplo)
    // ============================================================

    public VBox crearTicketDemo() {
        return crearTicket("Luis Altamirano");
    }




    // ============================================================
    //   SPACER — separador horizontal flexible
    // ============================================================

    private Region spacer() {
        Region r = new Region();
        HBox.setHgrow(r, Priority.ALWAYS);
        return r;
    }
}
