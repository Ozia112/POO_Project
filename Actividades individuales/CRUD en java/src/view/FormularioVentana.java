package view;

import controller.CiudadanoController;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Ciudadano;

public class FormularioVentana extends Application {

    private static CiudadanoController controller;
    public static void launchUI(CiudadanoController ctrl, String[] args) {
        controller = ctrl;
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        GridPane gp = new GridPane();
        VBox root = new VBox(gp);
        root.setPadding(new Insets(20));
        root.setSpacing(10);
        root.setAlignment(javafx.geometry.Pos.CENTER);

        gp.setPadding(new Insets(12));
        gp.setHgap(8);
        gp.setVgap(8);
        
        TextField nombres = crearCampoFormulario("Nombre(s)*:", "Ingresa tu(s) nombre(s)", 0, gp);
        TextField apellidoPaterno = crearCampoFormulario("Primer Apellido*:", "Ingresa tu primer apellido", 1, gp);
        TextField apellidoMaterno = crearCampoFormulario("Segundo Apellido*:", "Ingresa tu segundo apellido", 2, gp);
        TextField curp = crearCampoFormulario("CURP*:", "Ingresa tu CURP", 3, gp);
        TextField email = crearCampoFormulario("Email*:", "Ingresa tu email", 4, gp);
        TextField telefono = crearCampoFormulario("Telefono*:", "Ingresa tu telefono", 5, gp);
        TextField distrito = crearCampoFormulario("Distrito(1-9)*:", "Ingresa tu distrito", 6, gp);

        Label requiredNote = new Label("* Campos obligatorios");
        requiredNote.setStyle("-fx-text-fill: red;");

        Button botonEnviar = new Button("Enviar");

        botonEnviar.setOnAction(e -> {
            try {
                Ciudadano ciudadano = controller.procesarCiudadano(
                    nombres.getText(),
                    "",
                    "",
                    apellidoPaterno.getText(),
                    apellidoMaterno.getText(),
                    curp.getText(),
                    email.getText(),
                    telefono.getText(),
                    distrito.getText(),
                    0,
                    0
                );
                showInfo("Registrado","Se registró: " + ciudadano.getCurp());
                clearFields(nombres, apellidoPaterno, apellidoMaterno, curp, email, telefono, distrito);
            } catch (Exception ex) {
                showAlert("Error", ex.getMessage());
            }
        });

        dinamicButton(botonEnviar, nombres, apellidoPaterno, apellidoMaterno, curp, email, telefono, distrito);
        gp.add(botonEnviar, 1, 7);
        gp.add(requiredNote, 0, 7);
        stage.setScene(new Scene(root, 450, 350));
        stage.getScene().getStylesheets().add("file:src/view/assets/estilos.css");
        stage.setMinHeight(320);
        stage.setMinWidth(410);
        stage.setTitle("Formulario de Ciudadano");
        stage.getIcons().add(new javafx.scene.image.Image("file:src/view/assets/icon.png"));
        stage.show();
    }

    private TextField crearCampoFormulario(String labelText, String placeholder, int rowIndex, GridPane grid) {
        Label label = new Label(labelText.replace("*:", ""));
        Label asterisco = new Label("*");
        asterisco.setStyle("-fx-text-fill: red;");
        HBox labelBox = new HBox(label, asterisco);
        labelBox.setSpacing(2);

        TextField campo = new TextField();
        campo.setPromptText(placeholder);
        campo.setPrefWidth(200);

        grid.add(labelBox, 0, rowIndex);
        grid.add(campo, 1, rowIndex);

        return campo;
    }

    private void dinamicButton(Button boton, TextField... campos) {
        ChangeListener<String> listener = (obs, oldVal, newVal) -> {
            boolean todosLlenos = true;
            for (TextField campo : campos) {
                if (campo.getText().trim().isEmpty()) {
                    todosLlenos = false;
                    break;
                }
            }
            boton.setDisable(!todosLlenos);
        };

        for (TextField campo : campos) {
            campo.textProperty().addListener(listener);
        }

        boton.setDisable(true); // Inicialmente desactivado
    }



    private void showInfo(String titulo, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setTitle(titulo);
        alert.showAndWait();
    }
    private void showAlert(String titulo, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle(titulo);
        alert.showAndWait();
    }

    private void clearFields(TextField... fields) {
        for (TextField field : fields) {
            field.clear();
        }
    }
}
