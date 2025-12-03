package view;

import dao.HibernateUtil;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Diálogo de login para ingresar credenciales de base de datos.
 * Se muestra al iniciar la aplicación si no hay credenciales guardadas.
 * 
 * Las credenciales se pueden guardar localmente en ~/.lockereasy/db.properties
 * para no tener que ingresarlas cada vez.
 */
public class LoginDialog {
    
    private Stage dialogStage;
    private boolean conectado = false;
    private Label lblEstado;
    private Button btnConectar;
    private ProgressIndicator progressIndicator;
    
    /**
     * Muestra el diálogo y espera a que el usuario se conecte.
     * @param owner Stage padre (puede ser null)
     * @return true si la conexión fue exitosa, false si canceló
     */
    public boolean mostrarYEsperar(Stage owner) {
        dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initStyle(StageStyle.DECORATED);
        dialogStage.setTitle("LockerEasy - Conexión a Base de Datos");
        dialogStage.setResizable(false);
        
        if (owner != null) {
            dialogStage.initOwner(owner);
        }
        
        VBox root = crearContenido();
        Scene scene = new Scene(root, 500, 600);
        
        // Intentar cargar CSS
        try {
            scene.getStylesheets().add(getClass().getResource("/Styles-app.css").toExternalForm());
        } catch (Exception e) {
            // CSS opcional
        }
        
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
        
        return conectado;
    }
    
    private VBox crearContenido() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #667eea, #764ba2);");
        
        // Logo/Título
        Label lblTitulo = new Label("🔐 LockerEasy");
        lblTitulo.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        Label lblSubtitulo = new Label("Conexión a Base de Datos");
        lblSubtitulo.setStyle("-fx-font-size: 14px; -fx-text-fill: rgba(255,255,255,0.8);");
        
        // Card de formulario
        VBox card = new VBox(15);
        card.setPadding(new Insets(25));
        card.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 15; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 20, 0, 0, 5);"
        );
        card.setMaxWidth(380);
        
        // Campos
        TextField txtHost = new TextField("localhost");
        txtHost.setPromptText("Host (ej: localhost)");
        estilizarCampo(txtHost);
        
        TextField txtPuerto = new TextField("5432");
        txtPuerto.setPromptText("Puerto");
        txtPuerto.setPrefWidth(80);
        estilizarCampo(txtPuerto);
        
        TextField txtBaseDatos = new TextField("lockereasy");
        txtBaseDatos.setPromptText("Nombre de la base de datos");
        estilizarCampo(txtBaseDatos);
        
        HBox hostRow = new HBox(10);
        hostRow.setAlignment(Pos.CENTER_LEFT);
        VBox hostBox = new VBox(3);
        hostBox.getChildren().addAll(new Label("Host"), txtHost);
        HBox.setHgrow(hostBox, Priority.ALWAYS);
        
        VBox puertoBox = new VBox(3);
        puertoBox.getChildren().addAll(new Label("Puerto"), txtPuerto);
        
        hostRow.getChildren().addAll(hostBox, puertoBox);
        
        VBox dbBox = new VBox(3);
        dbBox.getChildren().addAll(new Label("Base de datos"), txtBaseDatos);
        
        TextField txtUsuario = new TextField("postgres");
        txtUsuario.setPromptText("Usuario");
        estilizarCampo(txtUsuario);
        VBox userBox = new VBox(3);
        userBox.getChildren().addAll(new Label("Usuario"), txtUsuario);
        
        PasswordField txtPassword = new PasswordField();
        txtPassword.setPromptText("Contraseña");
        estilizarCampo(txtPassword);
        VBox passBox = new VBox(3);
        passBox.getChildren().addAll(new Label("Contraseña"), txtPassword);
        
        // Checkbox para recordar
        CheckBox chkRecordar = new CheckBox("Recordar credenciales en este equipo");
        chkRecordar.setSelected(true);
        chkRecordar.setStyle("-fx-font-size: 12px;");
        
        // Estado y progreso
        HBox estadoBox = new HBox(10);
        estadoBox.setAlignment(Pos.CENTER);
        
        progressIndicator = new ProgressIndicator();
        progressIndicator.setPrefSize(20, 20);
        progressIndicator.setVisible(false);
        
        lblEstado = new Label("");
        lblEstado.setStyle("-fx-font-size: 12px;");
        
        estadoBox.getChildren().addAll(progressIndicator, lblEstado);
        
        // Botones
        HBox botonesBox = new HBox(15);
        botonesBox.setAlignment(Pos.CENTER);
        
        btnConectar = new Button("🔌 Conectar");
        btnConectar.setStyle(
            "-fx-background-color: #4CAF50; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 12 30; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand;"
        );
        
        Button btnCancelar = new Button("Cancelar");
        btnCancelar.setStyle(
            "-fx-background-color: #9E9E9E; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px; " +
            "-fx-padding: 12 25; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand;"
        );
        
        botonesBox.getChildren().addAll(btnConectar, btnCancelar);
        
        // Acciones
        btnConectar.setOnAction(e -> {
            String url = construirUrl(txtHost.getText(), txtPuerto.getText(), txtBaseDatos.getText());
            String user = txtUsuario.getText().trim();
            String password = txtPassword.getText();
            
            if (user.isEmpty()) {
                mostrarEstado("⚠️ Ingresa el usuario", "#FF9800");
                return;
            }
            
            intentarConexion(url, user, password, chkRecordar.isSelected());
        });
        
        btnCancelar.setOnAction(e -> {
            conectado = false;
            dialogStage.close();
        });
        
        // Enter para conectar
        txtPassword.setOnAction(e -> btnConectar.fire());
        
        card.getChildren().addAll(
            hostRow,
            dbBox,
            userBox,
            passBox,
            chkRecordar,
            estadoBox,
            botonesBox
        );
        
        // Nota sobre requisitos
        Label lblRequisito = new Label("📋 Requisito: La base de datos debe existir en PostgreSQL.\n" +
            "Si es primera vez, crea la BD con: CREATE DATABASE lockereasy;");
        lblRequisito.setStyle("-fx-font-size: 11px; -fx-text-fill: rgba(255,255,255,0.9); -fx-padding: 10 0 0 0;");
        lblRequisito.setWrapText(true);
        lblRequisito.setMaxWidth(380);
        
        // Info de variables de entorno
        Label lblInfo = new Label("💡 Tip: También puedes usar variables de entorno: DB_URL, DB_USER, DB_PASSWORD");
        lblInfo.setStyle("-fx-font-size: 10px; -fx-text-fill: rgba(255,255,255,0.6);");
        lblInfo.setWrapText(true);
        lblInfo.setMaxWidth(380);
        
        root.getChildren().addAll(lblTitulo, lblSubtitulo, card, lblRequisito, lblInfo);
        
        // Focus en password si usuario ya está lleno
        Platform.runLater(() -> {
            if (!txtUsuario.getText().isEmpty()) {
                txtPassword.requestFocus();
            }
        });
        
        return root;
    }
    
    private void estilizarCampo(TextField campo) {
        campo.setStyle(
            "-fx-background-color: #f5f5f5; " +
            "-fx-border-color: #e0e0e0; " +
            "-fx-border-radius: 6; " +
            "-fx-background-radius: 6; " +
            "-fx-padding: 10;"
        );
        campo.setMaxWidth(Double.MAX_VALUE);
    }
    
    private String construirUrl(String host, String puerto, String db) {
        host = host.trim().isEmpty() ? "localhost" : host.trim();
        puerto = puerto.trim().isEmpty() ? "5432" : puerto.trim();
        db = db.trim().isEmpty() ? "lockereasy" : db.trim();
        
        return String.format("jdbc:postgresql://%s:%s/%s", host, puerto, db);
    }
    
    private void intentarConexion(String url, String user, String password, boolean recordar) {
        btnConectar.setDisable(true);
        progressIndicator.setVisible(true);
        mostrarEstado("Conectando...", "#2196F3");
        
        new Thread(() -> {
            boolean exito = HibernateUtil.probarConexion(url, user, password);
            
            Platform.runLater(() -> {
                progressIndicator.setVisible(false);
                btnConectar.setDisable(false);
                
                if (exito) {
                    mostrarEstado("✅ Conexión exitosa!", "#4CAF50");
                    
                    // Guardar credenciales
                    HibernateUtil.setCredenciales(url, user, password);
                    
                    if (recordar) {
                        HibernateUtil.guardarCredencialesLocalmente(url, user, password);
                    }
                    
                    // Inicializar Hibernate
                    try {
                        HibernateUtil.inicializar();
                        conectado = true;
                        
                        // Cerrar después de un momento para que vea el mensaje
                        new Thread(() -> {
                            try { Thread.sleep(500); } catch (Exception ignored) {}
                            Platform.runLater(() -> dialogStage.close());
                        }).start();
                        
                    } catch (Exception e) {
                        mostrarEstado("❌ Error al inicializar: " + e.getMessage(), "#f44336");
                    }
                } else {
                    mostrarEstado("❌ No se pudo conectar. Verifica los datos.", "#f44336");
                }
            });
        }).start();
    }
    
    private void mostrarEstado(String mensaje, String color) {
        lblEstado.setText(mensaje);
        lblEstado.setStyle("-fx-font-size: 12px; -fx-text-fill: " + color + "; -fx-font-weight: bold;");
    }
}
