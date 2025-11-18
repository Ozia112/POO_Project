package view;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class TicketGUI {

    public void mostrar(Stage stage) {

        VBox side = new VBox(20);
        side.getChildren().addAll(new Label("Servicios"), new Label("Renta"));
        side.setStyle("-fx-padding:20; -fx-background-color:#dddddd; -fx-pref-width:150;");

        Label titulo = new Label("Crear ticket de venta");

        TextField cliente = new TextField();
        cliente.setPromptText("Nombre del cliente");

        ComboBox<String> tipo = new ComboBox<>();
        tipo.getItems().addAll("Venta", "Renta", "Servicio especial");

        Button agregar = new Button("Agregar");

        VBox crear = new VBox(10, titulo, cliente, tipo, agregar);
        crear.setStyle("-fx-padding:20; -fx-background-color:#eef5ea; -fx-pref-width:350;");

        Label ticket = new Label("Ticket #");
        Label clienteT = new Label("Cliente: —");

        VBox ticketBox = new VBox(10, ticket, clienteT);
        ticketBox.setStyle("-fx-padding:20; -fx-background-color:#f5fff1; -fx-pref-width:350;");

        HBox root = new HBox(side, crear, ticketBox);
        Scene scene = new Scene(root, 900, 500);

        stage.setScene(scene);
        stage.setTitle("Sistema de Tickets");
        stage.show();
    }
}
