package view;

import dao.CredencialBDDAO;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import model.CredencialBD;

import java.util.List;
import java.util.Optional;

/**
 * Vista de configuración de credenciales de base de datos.
 * Permite:
 * - Ver y editar perfiles de conexión
 * - Agregar nuevos perfiles (localhost, cloud, etc.)
 * - Probar conexión antes de guardar
 * - Cambiar entre perfiles activos
 * 
 * Usado en: ConfigView (pestaña "Cuenta")
 */
public class ConfigCuentaGUI {

    private final CredencialBDDAO credencialDAO;
    private VBox root;
    private VBox listaPerfiles;
    private VBox formularioContainer;
    private Label lblEstadoConexion;
    
    // Callback para notificar cambios
    private Runnable onDataChanged;

    public ConfigCuentaGUI() {
        this.credencialDAO = new CredencialBDDAO();
    }
    
    public void setOnDataChanged(Runnable callback) {
        this.onDataChanged = callback;
    }
    
    private void notificarCambio() {
        if (onDataChanged != null) {
            onDataChanged.run();
        }
    }

    public VBox getVistaIntegrada() {
        root = new VBox(20);
        root.setPadding(new Insets(25));
        root.setAlignment(Pos.TOP_CENTER);
        root.getStyleClass().add("config-root");

        // === TÍTULO ===
        Label titulo = new Label("🔐 Configuración de Base de Datos");
        titulo.getStyleClass().add("config-titulo");

        // === PANEL DE ALERTA DE SEGURIDAD ===
        VBox alertaSeguridad = crearAlertaSeguridad();

        // === ESTADO DE CONEXIÓN ACTUAL ===
        HBox estadoBox = crearEstadoConexion();

        // === CONTENEDOR PARA FORMULARIO DE NUEVO PERFIL ===
        formularioContainer = new VBox(10);
        formularioContainer.setAlignment(Pos.TOP_CENTER);
        formularioContainer.setMaxWidth(700);

        // === LISTA DE PERFILES ===
        Label lblPerfiles = new Label("📋 Perfiles de Conexión");
        lblPerfiles.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");
        
        listaPerfiles = new VBox(10);
        listaPerfiles.setPadding(new Insets(15));
        listaPerfiles.setStyle("-fx-background-color: white;");
        
        ScrollPane scrollPerfiles = new ScrollPane(listaPerfiles);
        scrollPerfiles.setFitToWidth(true);
        scrollPerfiles.setMaxWidth(700);
        scrollPerfiles.setPrefHeight(300);
        scrollPerfiles.getStyleClass().add("scroll-lista");

        // === BOTÓN AGREGAR PERFIL ===
        Button btnAgregar = new Button("➕ Agregar Nuevo Perfil de Conexión");
        btnAgregar.getStyleClass().add("btn-agregar");
        btnAgregar.setMaxWidth(350);
        btnAgregar.setOnAction(e -> mostrarFormularioAgregar());

        actualizarListaPerfiles();

        root.getChildren().addAll(
            titulo, 
            alertaSeguridad, 
            estadoBox, 
            formularioContainer,
            lblPerfiles, 
            scrollPerfiles, 
            btnAgregar
        );

        // Cargar estilos
        try {
            root.getStylesheets().add(getClass().getResource("/Styles-app.css").toExternalForm());
        } catch (Exception e) {
            // CSS opcional
        }

        return root;
    }

    private VBox crearAlertaSeguridad() {
        VBox alerta = new VBox(5);
        alerta.getStyleClass().add("alerta-seguridad");
        alerta.setMaxWidth(700);
        
        Label lblTitulo = new Label("⚠️ Información de Seguridad");
        lblTitulo.setStyle("-fx-font-weight: bold; -fx-text-fill: #E65100;");
        
        Label lblTexto1 = new Label("• Las contraseñas se guardan codificadas en la base de datos local.");
        lblTexto1.getStyleClass().add("alerta-seguridad-texto");
        
        Label lblTexto2 = new Label("• Para máxima seguridad, no compartas el archivo hibernate.cfg.xml.");
        lblTexto2.getStyleClass().add("alerta-seguridad-texto");
        
        Label lblTexto3 = new Label("• Los perfiles guardados aquí son para referencia. La conexión activa se configura en hibernate.cfg.xml.");
        lblTexto3.getStyleClass().add("alerta-seguridad-texto");
        
        alerta.getChildren().addAll(lblTitulo, lblTexto1, lblTexto2, lblTexto3);
        return alerta;
    }

    private HBox crearEstadoConexion() {
        HBox box = new HBox(15);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(15));
        box.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #E0E0E0; -fx-border-radius: 10;");
        box.setMaxWidth(700);
        
        Label lblTitulo = new Label("Estado actual:");
        lblTitulo.setStyle("-fx-font-weight: bold;");
        
        lblEstadoConexion = new Label("⏳ Verificando...");
        lblEstadoConexion.setStyle("-fx-font-weight: bold;");
        
        Button btnRefrescar = new Button("🔄 Verificar");
        btnRefrescar.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-padding: 5 15; -fx-background-radius: 5; -fx-cursor: hand;");
        btnRefrescar.setOnAction(e -> verificarConexionActual());
        
        box.getChildren().addAll(lblTitulo, lblEstadoConexion, btnRefrescar);
        
        // Verificar conexión al inicio
        Platform.runLater(this::verificarConexionActual);
        
        return box;
    }

    private void verificarConexionActual() {
        lblEstadoConexion.setText("⏳ Verificando...");
        lblEstadoConexion.setStyle("-fx-font-weight: bold; -fx-text-fill: #FF9800;");
        
        new Thread(() -> {
            try {
                // Intentar una operación simple de BD
                boolean conectado = credencialDAO.obtenerTodas() != null;
                Platform.runLater(() -> {
                    if (conectado) {
                        lblEstadoConexion.setText("✅ Conectado a la base de datos");
                        lblEstadoConexion.getStyleClass().removeAll("conexion-inactiva");
                        lblEstadoConexion.getStyleClass().add("conexion-activa");
                        lblEstadoConexion.setStyle("-fx-font-weight: bold; -fx-text-fill: #2E7D32;");
                    } else {
                        lblEstadoConexion.setText("❌ Sin conexión");
                        lblEstadoConexion.getStyleClass().removeAll("conexion-activa");
                        lblEstadoConexion.getStyleClass().add("conexion-inactiva");
                        lblEstadoConexion.setStyle("-fx-font-weight: bold; -fx-text-fill: #C62828;");
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    lblEstadoConexion.setText("❌ Error: " + e.getMessage());
                    lblEstadoConexion.setStyle("-fx-font-weight: bold; -fx-text-fill: #C62828;");
                });
            }
        }).start();
    }

    private void actualizarListaPerfiles() {
        listaPerfiles.getChildren().clear();
        
        List<CredencialBD> perfiles = credencialDAO.obtenerTodas();
        
        if (perfiles.isEmpty()) {
            VBox vacio = new VBox(10);
            vacio.setAlignment(Pos.CENTER);
            vacio.setPadding(new Insets(30));
            
            Label lblVacio = new Label("No hay perfiles de conexión guardados.");
            lblVacio.setStyle("-fx-text-fill: #999; -fx-font-size: 14px;");
            
            Label lblHint = new Label("💡 Agrega un perfil para guardar tus configuraciones de conexión.");
            lblHint.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");
            
            vacio.getChildren().addAll(lblVacio, lblHint);
            listaPerfiles.getChildren().add(vacio);
            return;
        }
        
        for (CredencialBD perfil : perfiles) {
            HBox itemBox = crearItemPerfil(perfil);
            listaPerfiles.getChildren().add(itemBox);
        }
    }

    private HBox crearItemPerfil(CredencialBD perfil) {
        HBox itemBox = new HBox(12);
        itemBox.setPadding(new Insets(15));
        itemBox.setAlignment(Pos.CENTER_LEFT);
        itemBox.getStyleClass().add("item-fila");
        itemBox.setMaxWidth(Double.MAX_VALUE);
        
        // Indicador de activo
        Label lblActivo = new Label(perfil.isActivo() ? "✓" : "○");
        lblActivo.setStyle(perfil.isActivo() 
            ? "-fx-font-size: 18px; -fx-text-fill: #4CAF50; -fx-font-weight: bold;"
            : "-fx-font-size: 18px; -fx-text-fill: #999;");
        lblActivo.setPrefWidth(30);
        
        // Info del perfil
        VBox infoBox = new VBox(3);
        HBox.setHgrow(infoBox, Priority.ALWAYS);
        
        Label lblNombre = new Label(perfil.getNombrePerfil());
        lblNombre.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #333;");
        
        String urlDisplay = perfil.getUrlConexion() + ":" + perfil.getPuerto() + "/" + perfil.getNombreBaseDatos();
        Label lblUrl = new Label("🌐 " + urlDisplay);
        lblUrl.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        
        Label lblUsuario = new Label("👤 " + perfil.getUsuario());
        lblUsuario.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        
        infoBox.getChildren().addAll(lblNombre, lblUrl, lblUsuario);
        
        // Botones de acción
        HBox botonesBox = new HBox(8);
        botonesBox.setAlignment(Pos.CENTER_RIGHT);
        
        Button btnProbar = new Button("🔌 Probar");
        btnProbar.getStyleClass().add("btn-probar-conexion");
        btnProbar.setOnAction(e -> probarConexion(perfil));
        
        Button btnActivar = new Button(perfil.isActivo() ? "✓ Activo" : "Activar");
        btnActivar.setStyle(perfil.isActivo()
            ? "-fx-background-color: #C8E6C9; -fx-text-fill: #2E7D32; -fx-padding: 8 15; -fx-background-radius: 5;"
            : "-fx-background-color: #E0E0E0; -fx-text-fill: #333; -fx-padding: 8 15; -fx-background-radius: 5; -fx-cursor: hand;");
        btnActivar.setDisable(perfil.isActivo());
        btnActivar.setOnAction(e -> {
            credencialDAO.establecerActiva(perfil.getId());
            actualizarListaPerfiles();
            notificarCambio();
        });
        
        Button btnEditar = new Button("✏️");
        btnEditar.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-padding: 8 12; -fx-background-radius: 5; -fx-cursor: hand;");
        btnEditar.setOnAction(e -> mostrarFormularioEditar(perfil));
        
        Button btnEliminar = new Button("🗑️");
        btnEliminar.getStyleClass().add("btn-eliminar-icono-pequeno");
        btnEliminar.setOnAction(e -> eliminarPerfil(perfil));
        
        botonesBox.getChildren().addAll(btnProbar, btnActivar, btnEditar, btnEliminar);
        
        itemBox.getChildren().addAll(lblActivo, infoBox, botonesBox);
        
        return itemBox;
    }

    private void mostrarFormularioAgregar() {
        mostrarFormulario(null);
    }
    
    private void mostrarFormularioEditar(CredencialBD perfil) {
        mostrarFormulario(perfil);
    }

    private void mostrarFormulario(CredencialBD perfilExistente) {
        formularioContainer.getChildren().clear();
        
        boolean esEdicion = perfilExistente != null;
        
        VBox form = new VBox(15);
        form.setPadding(new Insets(20));
        form.getStyleClass().add("formulario-agregar");
        form.setMaxWidth(700);
        
        Label lblTitulo = new Label(esEdicion ? "✏️ Editar Perfil de Conexión" : "➕ Nuevo Perfil de Conexión");
        lblTitulo.getStyleClass().add("formulario-titulo");
        
        // Grid de campos
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(12);
        
        // Nombre del perfil
        Label lblNombre = new Label("Nombre del perfil:");
        lblNombre.getStyleClass().add("formulario-campo-label");
        TextField txtNombre = new TextField(esEdicion ? perfilExistente.getNombrePerfil() : "");
        txtNombre.setPromptText("Ej: Producción, Desarrollo Local, Cloud");
        txtNombre.setPrefWidth(250);
        
        // Tipo de BD
        Label lblTipo = new Label("Tipo de BD:");
        lblTipo.getStyleClass().add("formulario-campo-label");
        ComboBox<String> comboTipo = new ComboBox<>();
        comboTipo.getItems().addAll("postgresql", "mysql", "mariadb", "sqlserver", "h2");
        comboTipo.setValue(esEdicion ? perfilExistente.getTipoBaseDatos() : "postgresql");
        comboTipo.setPrefWidth(150);
        
        // Host/URL
        Label lblHost = new Label("Host / URL:");
        lblHost.getStyleClass().add("formulario-campo-label");
        TextField txtHost = new TextField(esEdicion ? perfilExistente.getUrlConexion() : "localhost");
        txtHost.setPromptText("localhost o url-servidor.com");
        txtHost.setPrefWidth(250);
        
        // Puerto
        Label lblPuerto = new Label("Puerto:");
        lblPuerto.getStyleClass().add("formulario-campo-label");
        Spinner<Integer> spinnerPuerto = new Spinner<>(1, 65535, esEdicion ? perfilExistente.getPuerto() : 5432);
        spinnerPuerto.setEditable(true);
        spinnerPuerto.setPrefWidth(100);
        
        // Nombre BD
        Label lblBD = new Label("Nombre de BD:");
        lblBD.getStyleClass().add("formulario-campo-label");
        TextField txtBD = new TextField(esEdicion ? perfilExistente.getNombreBaseDatos() : "lockereasy");
        txtBD.setPromptText("lockereasy");
        txtBD.setPrefWidth(200);
        
        // Usuario
        Label lblUsuario = new Label("Usuario:");
        lblUsuario.getStyleClass().add("formulario-campo-label");
        TextField txtUsuario = new TextField(esEdicion ? perfilExistente.getUsuario() : "postgres");
        txtUsuario.setPromptText("postgres");
        txtUsuario.setPrefWidth(200);
        
        // Contraseña
        Label lblPassword = new Label("Contraseña:");
        lblPassword.getStyleClass().add("formulario-campo-label");
        PasswordField txtPassword = new PasswordField();
        txtPassword.setText(esEdicion ? perfilExistente.getPassword() : "");
        txtPassword.setPromptText("••••••••");
        txtPassword.setPrefWidth(200);
        
        // Toggle para mostrar/ocultar contraseña
        TextField txtPasswordVisible = new TextField();
        txtPasswordVisible.setPromptText("••••••••");
        txtPasswordVisible.setPrefWidth(200);
        txtPasswordVisible.setVisible(false);
        txtPasswordVisible.setManaged(false);
        
        CheckBox chkMostrarPassword = new CheckBox("Mostrar");
        chkMostrarPassword.setOnAction(e -> {
            if (chkMostrarPassword.isSelected()) {
                txtPasswordVisible.setText(txtPassword.getText());
                txtPassword.setVisible(false);
                txtPassword.setManaged(false);
                txtPasswordVisible.setVisible(true);
                txtPasswordVisible.setManaged(true);
            } else {
                txtPassword.setText(txtPasswordVisible.getText());
                txtPasswordVisible.setVisible(false);
                txtPasswordVisible.setManaged(false);
                txtPassword.setVisible(true);
                txtPassword.setManaged(true);
            }
        });
        
        HBox passwordBox = new HBox(10, txtPassword, txtPasswordVisible, chkMostrarPassword);
        passwordBox.setAlignment(Pos.CENTER_LEFT);
        
        // Descripción
        Label lblDesc = new Label("Descripción:");
        lblDesc.getStyleClass().add("formulario-campo-label");
        TextField txtDesc = new TextField(esEdicion ? perfilExistente.getDescripcion() : "");
        txtDesc.setPromptText("Descripción opcional");
        txtDesc.setPrefWidth(350);
        
        // Agregar al grid
        grid.add(lblNombre, 0, 0); grid.add(txtNombre, 1, 0);
        grid.add(lblTipo, 2, 0); grid.add(comboTipo, 3, 0);
        grid.add(lblHost, 0, 1); grid.add(txtHost, 1, 1);
        grid.add(lblPuerto, 2, 1); grid.add(spinnerPuerto, 3, 1);
        grid.add(lblBD, 0, 2); grid.add(txtBD, 1, 2);
        grid.add(lblUsuario, 0, 3); grid.add(txtUsuario, 1, 3);
        grid.add(lblPassword, 2, 3); grid.add(passwordBox, 3, 3);
        grid.add(lblDesc, 0, 4); grid.add(txtDesc, 1, 4, 3, 1);
        
        // Botones
        HBox botones = new HBox(10);
        botones.setAlignment(Pos.CENTER);
        
        Button btnProbar = new Button("🔌 Probar Conexión");
        btnProbar.getStyleClass().add("btn-probar-conexion");
        
        Button btnGuardar = new Button(esEdicion ? "💾 Guardar Cambios" : "💾 Guardar Perfil");
        btnGuardar.getStyleClass().add("btn-agregar");
        
        Button btnCancelar = new Button("✖ Cancelar");
        btnCancelar.getStyleClass().add("btn-cancelar");
        btnCancelar.setOnAction(e -> formularioContainer.getChildren().clear());
        
        // Label de resultado de prueba
        Label lblResultadoPrueba = new Label("");
        lblResultadoPrueba.setStyle("-fx-font-weight: bold;");
        
        btnProbar.setOnAction(e -> {
            lblResultadoPrueba.setText("⏳ Probando conexión...");
            lblResultadoPrueba.setStyle("-fx-font-weight: bold; -fx-text-fill: #FF9800;");
            
            new Thread(() -> {
                String pass = chkMostrarPassword.isSelected() ? txtPasswordVisible.getText() : txtPassword.getText();
                boolean exito = credencialDAO.probarConexion(
                    txtHost.getText().trim(),
                    spinnerPuerto.getValue(),
                    txtBD.getText().trim(),
                    txtUsuario.getText().trim(),
                    pass,
                    comboTipo.getValue()
                );
                
                Platform.runLater(() -> {
                    if (exito) {
                        lblResultadoPrueba.setText("✅ Conexión exitosa!");
                        lblResultadoPrueba.setStyle("-fx-font-weight: bold; -fx-text-fill: #2E7D32;");
                    } else {
                        lblResultadoPrueba.setText("❌ No se pudo conectar. Verifica los datos.");
                        lblResultadoPrueba.setStyle("-fx-font-weight: bold; -fx-text-fill: #C62828;");
                    }
                });
            }).start();
        });
        
        btnGuardar.setOnAction(e -> {
            String nombre = txtNombre.getText().trim();
            if (nombre.isEmpty()) {
                mostrarAlerta("Error", "El nombre del perfil es obligatorio.");
                return;
            }
            
            CredencialBD perfil = esEdicion ? perfilExistente : new CredencialBD();
            perfil.setNombrePerfil(nombre);
            perfil.setTipoBaseDatos(comboTipo.getValue());
            perfil.setUrlConexion(txtHost.getText().trim());
            perfil.setPuerto(spinnerPuerto.getValue());
            perfil.setNombreBaseDatos(txtBD.getText().trim());
            perfil.setUsuario(txtUsuario.getText().trim());
            
            String pass = chkMostrarPassword.isSelected() ? txtPasswordVisible.getText() : txtPassword.getText();
            perfil.setPassword(pass);
            perfil.setDescripcion(txtDesc.getText().trim());
            
            try {
                credencialDAO.guardar(perfil);
                formularioContainer.getChildren().clear();
                actualizarListaPerfiles();
                notificarCambio();
                mostrarMensajeExito(esEdicion ? "Perfil actualizado correctamente" : "Perfil guardado correctamente");
            } catch (Exception ex) {
                mostrarAlerta("Error", "No se pudo guardar: " + ex.getMessage());
            }
        });
        
        botones.getChildren().addAll(btnProbar, btnGuardar, btnCancelar);
        
        form.getChildren().addAll(lblTitulo, grid, lblResultadoPrueba, botones);
        formularioContainer.getChildren().add(form);
    }

    private void probarConexion(CredencialBD perfil) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Probando Conexión");
        alert.setHeaderText("Probando conexión a: " + perfil.getNombrePerfil());
        alert.setContentText("Por favor espere...");
        alert.show();
        
        new Thread(() -> {
            boolean exito = credencialDAO.probarConexion(perfil);
            Platform.runLater(() -> {
                alert.close();
                if (exito) {
                    mostrarAlerta("Éxito", "✅ Conexión exitosa a " + perfil.getNombrePerfil());
                } else {
                    mostrarAlerta("Error", "❌ No se pudo conectar a " + perfil.getNombrePerfil() + "\nVerifica las credenciales.");
                }
            });
        }).start();
    }

    private void eliminarPerfil(CredencialBD perfil) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar Eliminación");
        confirm.setHeaderText("¿Eliminar el perfil \"" + perfil.getNombrePerfil() + "\"?");
        confirm.setContentText("Esta acción no se puede deshacer.");
        
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                credencialDAO.eliminar(perfil.getId());
                actualizarListaPerfiles();
                notificarCambio();
            } catch (Exception e) {
                mostrarAlerta("Error", "No se pudo eliminar: " + e.getMessage());
            }
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    private void mostrarMensajeExito(String mensaje) {
        // Mensaje temporal en el formularioContainer
        Label lbl = new Label("✅ " + mensaje);
        lbl.getStyleClass().add("mensaje-exito");
        lbl.setMaxWidth(500);
        formularioContainer.getChildren().add(lbl);
        
        // Remover después de 3 segundos
        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(3));
        pause.setOnFinished(e -> formularioContainer.getChildren().remove(lbl));
        pause.play();
    }
    
    /**
     * Verifica si hay perfiles de conexión configurados.
     */
    public boolean tienePerfiles() {
        return !credencialDAO.obtenerTodas().isEmpty();
    }
    
    /**
     * Actualiza la lista de perfiles.
     */
    public void actualizarLista() {
        actualizarListaPerfiles();
        verificarConexionActual();
    }
}
