package view;

import java.util.List;

import controller.EtiquetaController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Etiqueta;

public class ConfigEtiquetasGUI {

    private final EtiquetaController etiquetaController;
    private VBox formularioContainer;
    private VBox listaEtiquetas;
    private javafx.animation.PauseTransition mensajeTimer;
    
    // Callback para notificar cambios
    private Runnable onDataChanged;

    public ConfigEtiquetasGUI(EtiquetaController etiquetaController) {
        this.etiquetaController = etiquetaController;
    }
    
    public void setOnDataChanged(Runnable callback) {
        this.onDataChanged = callback;
    }
    
    private void notificarCambio() {
        if (onDataChanged != null) {
            onDataChanged.run();
        }
    }

    public void mostrar(Stage stage) {
        VBox root = getVistaIntegrada();
        Scene scene = new Scene(root, 800, 600);
        
        try {
            scene.getStylesheets().add(getClass().getResource("/view/Styles/Styles.css").toExternalForm());
        } catch (Exception e) {
            // CSS opcional
        }
        
        stage.setTitle("Etiquetas - Gestión");
        stage.setScene(scene);
        stage.show();
    }

    public VBox getVistaIntegrada() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #f5f5f5;");

        // Título
        Label titulo = new Label("Gestionar Etiquetas");
        titulo.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #333;");

        // Panel informativo compacto
        VBox infoBox = new VBox(5);
        infoBox.setStyle("-fx-background-color: #E3F2FD; -fx-padding: 12; -fx-background-radius: 10; -fx-border-color: #2196F3; -fx-border-width: 2; -fx-border-radius: 10;");
        infoBox.setMaxWidth(900);
        
        Label lblInfo = new Label("ℹ️ Guía Rápida");
        lblInfo.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #1976D2;");
        
        HBox hboxInfo = new HBox(30);
        hboxInfo.setAlignment(Pos.CENTER_LEFT);
        
        VBox col1 = new VBox(3);
        Label lblExplicacion1 = new Label("✓ Afecta Inventario = Las ventas reducen existencias");
        lblExplicacion1.setStyle("-fx-font-size: 11px;");
        Label lblExplicacion2 = new Label("✗ No afecta = Las ventas NO modifican existencias");
        lblExplicacion2.setStyle("-fx-font-size: 11px;");
        col1.getChildren().addAll(lblExplicacion1, lblExplicacion2);
        
        VBox col2 = new VBox(3);
        Label lblExplicacion3 = new Label("⚠️ Solo CONSUMIBLES debería afectar inventario");
        lblExplicacion3.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #D32F2F;");
        Label lblExplicacion4 = new Label("💡 Doble clic en nombre para editar | Toggle para inventario");
        lblExplicacion4.setStyle("-fx-font-size: 11px; -fx-font-style: italic; -fx-text-fill: #666;");
        col2.getChildren().addAll(lblExplicacion3, lblExplicacion4);
        
        hboxInfo.getChildren().addAll(col1, col2);
        infoBox.getChildren().addAll(lblInfo, hboxInfo);

        // Contenedor para formularios inline y mensajes
        formularioContainer = new VBox(10);
        formularioContainer.setAlignment(Pos.TOP_CENTER);
        formularioContainer.setMaxWidth(900);

        // Lista de etiquetas
        listaEtiquetas = new VBox(8);
        listaEtiquetas.setPadding(new Insets(15));
        listaEtiquetas.setAlignment(Pos.TOP_CENTER);
        listaEtiquetas.setStyle("-fx-background-color: white;");
        
        ScrollPane scrollPane = new ScrollPane(listaEtiquetas);
        scrollPane.setFitToWidth(true);
        scrollPane.setMaxWidth(900);
        scrollPane.setPrefHeight(450);
        scrollPane.setStyle("-fx-background: white; -fx-border-color: #ddd; -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10;");

        // Botón agregar
        Button btnAgregar = new Button("➕ Agregar Nueva Etiqueta");
        btnAgregar.setStyle(
            "-fx-background-color: #4CAF50; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 15px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 14 28; " +
            "-fx-background-radius: 10; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 6, 0, 0, 3);"
        );
        btnAgregar.setMaxWidth(350);

        btnAgregar.setOnMouseEntered(e -> btnAgregar.setStyle(
            "-fx-background-color: #45a049; -fx-text-fill: white; -fx-font-size: 15px; " +
            "-fx-font-weight: bold; -fx-padding: 14 28; -fx-background-radius: 10; -fx-cursor: hand; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 8, 0, 0, 4);"
        ));
        
        btnAgregar.setOnMouseExited(e -> btnAgregar.setStyle(
            "-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 15px; " +
            "-fx-font-weight: bold; -fx-padding: 14 28; -fx-background-radius: 10; -fx-cursor: hand; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 6, 0, 0, 3);"
        ));

        btnAgregar.setOnAction(e -> mostrarFormularioAgregar());

        actualizarLista();

        root.getChildren().addAll(titulo, infoBox, formularioContainer, scrollPane, btnAgregar);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        return root;
    }

    private void actualizarLista() {
        listaEtiquetas.getChildren().clear();
        List<Etiqueta> etiquetas = etiquetaController.getEtiquetaDAO().obtenerTodas();

        for (Etiqueta etiqueta : etiquetas) {
            HBox itemBox = crearItemEtiquetaInline(etiqueta);
            listaEtiquetas.getChildren().add(itemBox);
        }
    }

    public void actualizarTabla() {
        actualizarLista();
    }
    
    public boolean tieneEtiquetas() {
        return !etiquetaController.getEtiquetaDAO().obtenerTodas().isEmpty();
    }

    private HBox crearItemEtiquetaInline(Etiqueta etiqueta) {
        HBox itemBox = new HBox(15);
        itemBox.setPadding(new Insets(12, 20, 12, 20));
        itemBox.setAlignment(Pos.CENTER_LEFT);
        itemBox.setStyle(
            "-fx-background-color: white; " +
            "-fx-border-color: #e0e0e0; " +
            "-fx-border-width: 1; " +
            "-fx-border-radius: 10; " +
            "-fx-background-radius: 10; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.08), 5, 0, 0, 2);"
        );
        itemBox.setMaxWidth(850);
        itemBox.setPrefWidth(850);

        // ID badge
        Label lblId = new Label("#" + etiqueta.getEtiquetaId());
        lblId.setStyle(
            "-fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: #2196F3; " +
            "-fx-padding: 4 10; -fx-background-radius: 12; -fx-font-size: 12px; -fx-min-width: 45;"
        );
        lblId.setAlignment(Pos.CENTER);

        // Nombre - Label que se convierte en TextField con doble clic
        Label lblNombre = new Label(etiqueta.getNombre());
        lblNombre.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333; -fx-min-width: 250;");
        lblNombre.setPrefWidth(300);
        
        // TextField oculto para edición
        TextField txtNombreEdit = new TextField(etiqueta.getNombre());
        txtNombreEdit.setStyle("-fx-font-size: 14px; -fx-padding: 6;");
        txtNombreEdit.setPrefWidth(300);
        txtNombreEdit.setVisible(false);
        txtNombreEdit.setManaged(false);
        
        // Toggle para afecta inventario
        ToggleButton toggleAfecta = new ToggleButton(etiqueta.isAfectaInventario() ? "✓ Afecta Inventario" : "✗ No afecta");
        toggleAfecta.setSelected(etiqueta.isAfectaInventario());
        actualizarEstiloToggle(toggleAfecta);
        toggleAfecta.setPrefWidth(160);
        
        // Botón guardar (oculto inicialmente)
        Button btnGuardar = new Button("💾");
        btnGuardar.setStyle(
            "-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 16px; " +
            "-fx-padding: 8 14; -fx-background-radius: 8; -fx-cursor: hand;"
        );
        btnGuardar.setVisible(false);
        btnGuardar.setManaged(false);

        // Botón eliminar
        Button btnEliminar = new Button("🗑️");
        btnEliminar.setStyle(
            "-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 16px; " +
            "-fx-padding: 8 14; -fx-background-radius: 8; -fx-cursor: hand; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 4, 0, 0, 2);"
        );
        
        btnEliminar.setOnMouseEntered(e -> btnEliminar.setStyle(
            "-fx-background-color: #d32f2f; -fx-text-fill: white; -fx-font-size: 16px; " +
            "-fx-padding: 8 14; -fx-background-radius: 8; -fx-cursor: hand; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 6, 0, 0, 3);"
        ));
        
        btnEliminar.setOnMouseExited(e -> btnEliminar.setStyle(
            "-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 16px; " +
            "-fx-padding: 8 14; -fx-background-radius: 8; -fx-cursor: hand; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 4, 0, 0, 2);"
        ));

        // Estado original para detectar cambios
        final String nombreOriginal = etiqueta.getNombre();
        final boolean afectaOriginal = etiqueta.isAfectaInventario();
        
        // Función para verificar cambios
        Runnable verificarCambios = () -> {
            String nombreActual = txtNombreEdit.isVisible() ? txtNombreEdit.getText().trim() : lblNombre.getText();
            boolean nombreCambiado = !nombreActual.equals(nombreOriginal);
            boolean afectaCambiado = toggleAfecta.isSelected() != afectaOriginal;
            boolean hayCambios = nombreCambiado || afectaCambiado;
            
            btnGuardar.setVisible(hayCambios);
            btnGuardar.setManaged(hayCambios);
        };

        // Doble clic en nombre para editar
        lblNombre.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                lblNombre.setVisible(false);
                lblNombre.setManaged(false);
                txtNombreEdit.setVisible(true);
                txtNombreEdit.setManaged(true);
                txtNombreEdit.requestFocus();
                txtNombreEdit.selectAll();
            }
        });
        
        // Listener para cambios en texto
        txtNombreEdit.textProperty().addListener((obs, oldVal, newVal) -> {
            verificarCambios.run();
        });
        
        // Al perder foco, volver a label
        txtNombreEdit.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                String nuevoNombre = txtNombreEdit.getText().trim();
                if (!nuevoNombre.isEmpty()) {
                    lblNombre.setText(nuevoNombre);
                } else {
                    txtNombreEdit.setText(nombreOriginal);
                }
                txtNombreEdit.setVisible(false);
                txtNombreEdit.setManaged(false);
                lblNombre.setVisible(true);
                lblNombre.setManaged(true);
                verificarCambios.run();
            }
        });
        
        // Enter para confirmar edición de nombre
        txtNombreEdit.setOnAction(e -> {
            itemBox.requestFocus(); // Quitar foco del TextField
        });
        
        // Toggle cambia estado
        toggleAfecta.setOnAction(e -> {
            actualizarEstiloToggle(toggleAfecta);
            verificarCambios.run();
        });
        
        // Guardar cambios
        btnGuardar.setOnAction(e -> {
            String nuevoNombre = txtNombreEdit.isVisible() ? txtNombreEdit.getText().trim() : lblNombre.getText();
            if (nuevoNombre.isEmpty()) {
                mostrarMensaje("⚠️ El nombre no puede estar vacío", "#FFF3CD", "#856404");
                return;
            }
            
            if (etiquetaController.actualizarEtiqueta(etiqueta.getEtiquetaId(), nuevoNombre, toggleAfecta.isSelected())) {
                mostrarMensaje("✓ Etiqueta actualizada", "#D4EDDA", "#155724");
                actualizarLista();
                notificarCambio();
            } else {
                mostrarMensaje("✗ Error al actualizar", "#F8D7DA", "#721C24");
            }
        });
        
        // Eliminar
        btnEliminar.setOnAction(e -> mostrarConfirmacionEliminar(etiqueta));

        // Hover effect
        itemBox.setOnMouseEntered(e -> itemBox.setStyle(
            "-fx-background-color: #f8f9fa; -fx-border-color: #2196F3; -fx-border-width: 2; " +
            "-fx-border-radius: 10; -fx-background-radius: 10; -fx-cursor: hand; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(33, 150, 243, 0.3), 10, 0, 0, 4);"
        ));
        
        itemBox.setOnMouseExited(e -> itemBox.setStyle(
            "-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 1; " +
            "-fx-border-radius: 10; -fx-background-radius: 10; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.08), 5, 0, 0, 2);"
        ));

        // Spacer
        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        itemBox.getChildren().addAll(lblId, lblNombre, txtNombreEdit, spacer, toggleAfecta, btnGuardar, btnEliminar);

        return itemBox;
    }
    
    private void actualizarEstiloToggle(ToggleButton toggle) {
        if (toggle.isSelected()) {
            toggle.setText("✓ Afecta Inventario");
            toggle.setStyle(
                "-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold; " +
                "-fx-padding: 6 12; -fx-background-radius: 15; -fx-font-size: 12px; -fx-cursor: hand;"
            );
        } else {
            toggle.setText("✗ No afecta");
            toggle.setStyle(
                "-fx-background-color: #9e9e9e; -fx-text-fill: white; " +
                "-fx-padding: 6 12; -fx-background-radius: 15; -fx-font-size: 12px; -fx-cursor: hand;"
            );
        }
    }

    private void mostrarFormularioAgregar() {
        cancelarTimerMensaje();
        formularioContainer.getChildren().clear();
        
        VBox formulario = new VBox(12);
        formulario.setPadding(new Insets(20));
        formulario.setStyle(
            "-fx-background-color: #E8F5E9; -fx-border-color: #4CAF50; " +
            "-fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10;"
        );
        formulario.setMaxWidth(850);

        Label titulo = new Label("➕ Nueva Etiqueta");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2E7D32;");

        HBox camposRow = new HBox(20);
        camposRow.setAlignment(Pos.CENTER_LEFT);
        
        Label lblNombre = new Label("Nombre:");
        lblNombre.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Ej: Consumibles, Papelería");
        txtNombre.setStyle("-fx-font-size: 13px; -fx-padding: 8;");
        txtNombre.setPrefWidth(300);

        ToggleButton toggleAfecta = new ToggleButton("✗ No afecta");
        toggleAfecta.setSelected(false);
        actualizarEstiloToggle(toggleAfecta);
        toggleAfecta.setOnAction(e -> actualizarEstiloToggle(toggleAfecta));
        
        camposRow.getChildren().addAll(lblNombre, txtNombre, toggleAfecta);

        HBox botones = new HBox(10);
        botones.setAlignment(Pos.CENTER_RIGHT);
        
        Button btnGuardar = new Button("✓ Guardar");
        btnGuardar.setStyle(
            "-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 13px; " +
            "-fx-font-weight: bold; -fx-padding: 8 20; -fx-background-radius: 5; -fx-cursor: hand;"
        );
        
        Button btnCancelar = new Button("✗ Cancelar");
        btnCancelar.setStyle(
            "-fx-background-color: #9e9e9e; -fx-text-fill: white; -fx-font-size: 13px; " +
            "-fx-font-weight: bold; -fx-padding: 8 20; -fx-background-radius: 5; -fx-cursor: hand;"
        );

        btnGuardar.setOnAction(e -> {
            String nombre = txtNombre.getText().trim();
            if (nombre.isEmpty()) {
                mostrarMensaje("⚠️ El nombre no puede estar vacío", "#FFF3CD", "#856404");
                return;
            }

            if (etiquetaController.agregarEtiqueta(nombre, toggleAfecta.isSelected())) {
                actualizarLista();
                mostrarMensaje("✓ Etiqueta agregada: " + nombre, "#D4EDDA", "#155724");
                formularioContainer.getChildren().clear();
                notificarCambio();
            } else {
                mostrarMensaje("✗ No se pudo agregar la etiqueta", "#F8D7DA", "#721C24");
            }
        });

        btnCancelar.setOnAction(e -> formularioContainer.getChildren().clear());

        botones.getChildren().addAll(btnGuardar, btnCancelar);
        formulario.getChildren().addAll(titulo, camposRow, botones);
        formularioContainer.getChildren().add(formulario);
        
        txtNombre.requestFocus();
    }

    private void mostrarConfirmacionEliminar(Etiqueta etiqueta) {
        cancelarTimerMensaje();
        formularioContainer.getChildren().clear();
        
        VBox formulario = new VBox(15);
        formulario.setPadding(new Insets(20));
        formulario.setStyle(
            "-fx-background-color: #FFEBEE; -fx-border-color: #f44336; " +
            "-fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10;"
        );
        formulario.setMaxWidth(850);
        formulario.setAlignment(Pos.CENTER);

        Label titulo = new Label("⚠️ ¿Eliminar etiqueta?");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #c62828;");

        Label mensaje = new Label("Etiqueta: " + etiqueta.getNombre());
        mensaje.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");

        HBox botones = new HBox(10);
        botones.setAlignment(Pos.CENTER);
        
        Button btnAceptar = new Button("✓ Eliminar");
        btnAceptar.setStyle(
            "-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 13px; " +
            "-fx-font-weight: bold; -fx-padding: 8 20; -fx-background-radius: 5; -fx-cursor: hand;"
        );
        
        Button btnCancelar = new Button("✗ Cancelar");
        btnCancelar.setStyle(
            "-fx-background-color: #9e9e9e; -fx-text-fill: white; -fx-font-size: 13px; " +
            "-fx-font-weight: bold; -fx-padding: 8 20; -fx-background-radius: 5; -fx-cursor: hand;"
        );

        btnAceptar.setOnAction(e -> {
            Etiqueta etiquetaAEliminar = etiquetaController.getEtiquetaDAO().obtener(etiqueta.getEtiquetaId());
            if (etiquetaAEliminar != null && etiquetaController.eliminarEtiqueta(etiquetaAEliminar)) {
                actualizarLista();
                mostrarMensaje("✓ Etiqueta eliminada", "#D4EDDA", "#155724");
                formularioContainer.getChildren().clear();
                notificarCambio();
            } else {
                mostrarMensaje("✗ No se pudo eliminar", "#F8D7DA", "#721C24");
            }
        });

        btnCancelar.setOnAction(e -> formularioContainer.getChildren().clear());

        botones.getChildren().addAll(btnAceptar, btnCancelar);
        formulario.getChildren().addAll(titulo, mensaje, botones);
        formularioContainer.getChildren().add(formulario);
    }

    private void mostrarMensaje(String texto, String bgColor, String textColor) {
        if (mensajeTimer != null) {
            mensajeTimer.stop();
        }
        
        // No limpiar si hay un formulario abierto
        boolean hayFormulario = !formularioContainer.getChildren().isEmpty() && 
            formularioContainer.getChildren().get(0) instanceof VBox;
        
        if (!hayFormulario) {
            formularioContainer.getChildren().clear();
        }
        
        Label mensaje = new Label(texto);
        mensaje.setStyle(
            "-fx-background-color: " + bgColor + "; -fx-text-fill: " + textColor + "; " +
            "-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 15 20; " +
            "-fx-background-radius: 8; -fx-border-color: " + textColor + "; " +
            "-fx-border-width: 1; -fx-border-radius: 8;"
        );
        mensaje.setMaxWidth(850);
        
        if (hayFormulario) {
            // Agregar mensaje al inicio
            formularioContainer.getChildren().add(0, mensaje);
        } else {
            formularioContainer.getChildren().add(mensaje);
        }
        
        mensajeTimer = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(3));
        mensajeTimer.setOnFinished(e -> {
            formularioContainer.getChildren().remove(mensaje);
        });
        mensajeTimer.play();
    }
    
    private void cancelarTimerMensaje() {
        if (mensajeTimer != null) {
            mensajeTimer.stop();
            mensajeTimer = null;
        }
    }
}
