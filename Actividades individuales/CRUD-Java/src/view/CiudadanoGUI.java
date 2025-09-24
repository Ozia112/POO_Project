package view;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

import controller.CiudadanoController;
import model.Ciudadano;

public class CiudadanoGUI extends Application {

    private static CiudadanoController controller;

    private static final Map<String,String> FEEDBACK_MAP = crearFeedbackMap();

        private static Map<String,String> crearFeedbackMap() {
        Map<String,String> m = new HashMap<>();
        m.put("OK_REGISTRO",        "Ciudadano registrado correctamente.");
        m.put("ERR_CURP_DUP",       "La CURP ya existe.");
        m.put("ERR_CURP_FORMAT",    "CURP inválida.");
        m.put("ERR_CURP_LENGTH",    "CURP debe tener 18 digitos.");
        m.put("ERR_EDAD",           "El ciudadano debe ser mayor de edad.");
        m.put("ERR_TEL_FORMAT",     "Teléfono inválido (10 dígitos).");
        m.put("ERR_MAIL_FORMAT",    "Correo con formato inválido.");
        m.put("ERR_DIST_FORMAT",    "Distrito inválido (1-9).");
        m.put("ERR_DIST_OUT_OF_RANGE", "Distrito fuera de rango (1-9).");
        m.put("ERR_IO",             "Error al guardar datos.");
        return m;
    }

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
            String nombresStr = nombres.getText();
            String primerApellidoStr = apellidoPaterno.getText();
            String segundoapellidoStr = apellidoMaterno.getText();
            String curpStr = curp.getText();
            String emailStr = email.getText();
            String telStr = telefono.getText();
            String distStr = distrito.getText();
            try {
                controller.procesarCiudadano(new CiudadanoFormulario(
                    nombresStr,
                    primerApellidoStr,
                    segundoapellidoStr,
                    curpStr,
                    emailStr,
                    telStr,
                    distStr)
                );
                showInfo("OK_REGISTRO");
                clearFields(nombres, apellidoPaterno, apellidoMaterno, curp, email, telefono, distrito);
            } catch (Exception ex) {
                showAlert(ex.getMessage());
            }
        });

        dynamicButton(botonEnviar, nombres, apellidoPaterno, apellidoMaterno, curp, email, telefono, distrito);
        gp.add(botonEnviar, 1, 7);
        gp.add(requiredNote, 0, 7);
        stage.setScene(new Scene(root, 450, 380));
        stage.getScene().getStylesheets().add("file:src/view/assets/estilos.css");
        stage.setMinHeight(369);
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

    private void dynamicButton(Button boton, TextField... campos) {
        ChangeListener<String> listener = (obs, oldVal, newVal) -> {
            boolean todosLlenos = true;
            for (TextField campo : campos) {
                if (campo.getText().trim().isEmpty()) {
                    todosLlenos = false; // Si algún campo está vacío, no todos están llenos
                    break;
                }
            }
            boton.setDisable(!todosLlenos);
        };

        for (TextField campo : campos) {
            campo.textProperty().addListener(listener); // Agrega el listener a cada campo
        }

        boton.setDisable(true); // Inicialmente desactivado
    }

    private void clearFields(TextField... fields) {
        for (TextField field : fields) {
            field.clear();
        }
    }

    private void showSuccessConCurp(Ciudadano c) {
        mostrarFeedback("OK_REGISTRO", Alert.AlertType.INFORMATION,  );
    }

    public void showInfo(String code) {
        String uiMsg = FEEDBACK_MAP.getOrDefault(code, code);
        System.out.println(code); // imprime código (feedback) en consola
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(uiMsg);
        alert.showAndWait();
    }

    public void showAlert(String code) {
        String uiMsg = FEEDBACK_MAP.getOrDefault(code, code);
        System.err.println(code); // imprime código de error en consola
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(uiMsg);
        alert.showAndWait();
    }
}
