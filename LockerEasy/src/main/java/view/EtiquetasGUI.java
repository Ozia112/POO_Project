package view;

import java.util.List;

import controller.EtiquetaController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import model.Etiqueta;

public class EtiquetasGUI {

    private final EtiquetaController etiquetaController;
    private VBox formularioContainer;
    private VBox listaEtiquetas;

    public EtiquetasGUI(EtiquetaController etiquetaController) {
        this.etiquetaController = etiquetaController;
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

    /**
     * Devuelve el contenido visual para integrar en un TabPane.
     * Lista vertical con botón eliminar por elemento y botón + al final.
     */
    public VBox getVistaIntegrada() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #f5f5f5;");

        // Título
        Label titulo = new Label("Gestionar Etiquetas");
        titulo.getStyleClass().add("titulo1");
        titulo.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #333;");

        // Panel informativo más compacto con emojis
        VBox infoBox = new VBox(5);
        infoBox.setStyle("-fx-background-color: #E3F2FD; -fx-padding: 12; -fx-background-radius: 10; -fx-border-color: #2196F3; -fx-border-width: 2; -fx-border-radius: 10;");
        infoBox.setMaxWidth(900);
        
        Label lblInfo = new Label("ℹ️ Guía Rápida");
        lblInfo.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #1976D2; -fx-font-family: 'Segoe UI Emoji', 'Segoe UI';");
        
        HBox hboxInfo = new HBox(30);
        hboxInfo.setAlignment(Pos.CENTER_LEFT);
        
        VBox col1 = new VBox(3);
        Label lblExplicacion1 = new Label("✓ Afecta Inventario = Las ventas reducen existencias");
        lblExplicacion1.setStyle("-fx-font-size: 11px; -fx-font-family: 'Segoe UI Emoji', 'Segoe UI';");
        Label lblExplicacion2 = new Label("✗ No afecta = Las ventas NO modifican existencias");
        lblExplicacion2.setStyle("-fx-font-size: 11px; -fx-font-family: 'Segoe UI Emoji', 'Segoe UI';");
        col1.getChildren().addAll(lblExplicacion1, lblExplicacion2);
        
        VBox col2 = new VBox(3);
        Label lblExplicacion3 = new Label("⚠️ Solo CONSUMIBLES afecta inventario");
        lblExplicacion3.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #D32F2F; -fx-font-family: 'Segoe UI Emoji', 'Segoe UI';");
        Label lblExplicacion4 = new Label("💡 Doble click para modificar");
        lblExplicacion4.setStyle("-fx-font-size: 11px; -fx-font-style: italic; -fx-text-fill: #666; -fx-font-family: 'Segoe UI Emoji', 'Segoe UI';");
        col2.getChildren().addAll(lblExplicacion3, lblExplicacion4);
        
        hboxInfo.getChildren().addAll(col1, col2);
        infoBox.getChildren().addAll(lblInfo, hboxInfo);

        // Contenedor para formularios inline
        formularioContainer = new VBox(10);
        formularioContainer.setAlignment(Pos.TOP_CENTER);
        formularioContainer.setMaxWidth(900);

        // Lista de etiquetas (VBox que se actualiza)
        listaEtiquetas = new VBox(8);
        listaEtiquetas.setPadding(new Insets(15));
        listaEtiquetas.setAlignment(Pos.TOP_CENTER);
        listaEtiquetas.setStyle("-fx-background-color: white;");
        
        ScrollPane scrollPane = new ScrollPane(listaEtiquetas);
        scrollPane.setFitToWidth(true);
        scrollPane.setMaxWidth(900);
        scrollPane.setPrefHeight(450);
        scrollPane.setStyle("-fx-background: white; -fx-border-color: #ddd; -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10;");

        // Botón "+" al final para agregar
        Button btnAgregar = new Button("➕ Agregar Nueva Etiqueta");
        btnAgregar.setStyle(
            "-fx-background-color: #4CAF50; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 15px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 14 28; " +
            "-fx-background-radius: 10; " +
            "-fx-cursor: hand; " +
            "-fx-font-family: 'Segoe UI Emoji', 'Segoe UI'; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 6, 0, 0, 3);"
        );
        btnAgregar.setMaxWidth(350);

        btnAgregar.setOnMouseEntered(e -> btnAgregar.setStyle(
            "-fx-background-color: #45a049; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 15px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 14 28; " +
            "-fx-background-radius: 10; " +
            "-fx-cursor: hand; " +
            "-fx-font-family: 'Segoe UI Emoji', 'Segoe UI'; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 8, 0, 0, 4);"
        ));
        
        btnAgregar.setOnMouseExited(e -> btnAgregar.setStyle(
            "-fx-background-color: #4CAF50; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 15px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 14 28; " +
            "-fx-background-radius: 10; " +
            "-fx-cursor: hand; " +
            "-fx-font-family: 'Segoe UI Emoji', 'Segoe UI'; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 6, 0, 0, 3);"
        ));

        btnAgregar.setOnAction(e -> mostrarFormularioAgregar());

        // Cargar etiquetas iniciales
        actualizarLista();

        root.getChildren().addAll(titulo, infoBox, formularioContainer, scrollPane, btnAgregar);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        return root;
    }

    private void actualizarLista() {
        listaEtiquetas.getChildren().clear();
        List<Etiqueta> etiquetas = etiquetaController.getEtiquetaDAO().obtenerTodas();

        for (Etiqueta etiqueta : etiquetas) {
            HBox itemBox = crearItemEtiqueta(etiqueta);
            listaEtiquetas.getChildren().add(itemBox);
        }
    }

    private HBox crearItemEtiqueta(Etiqueta etiqueta) {
        HBox itemBox = new HBox(20);
        itemBox.setPadding(new Insets(15, 20, 15, 20));
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

        // ID con badge
        Label lblId = new Label("#" + etiqueta.getEtiquetaId());
        lblId.setStyle(
            "-fx-font-weight: bold; " +
            "-fx-text-fill: white; " +
            "-fx-background-color: #2196F3; " +
            "-fx-padding: 4 10; " +
            "-fx-background-radius: 12; " +
            "-fx-font-size: 12px; " +
            "-fx-min-width: 45;"
        );
        lblId.setAlignment(Pos.CENTER);

        // Nombre con más espacio
        Label lblNombre = new Label(etiqueta.getNombre());
        lblNombre.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333; -fx-min-width: 300;");
        HBox.setHgrow(lblNombre, Priority.ALWAYS);

        // Indicador de afecta inventario con badge
        String textoAfecta = etiqueta.isAfectaInventario() ? "✓ Afecta Inventario" : "✗ No afecta";
        String estiloAfecta = etiqueta.isAfectaInventario() 
            ? "-fx-text-fill: white; -fx-background-color: #f44336; -fx-font-weight: bold; -fx-padding: 6 14; -fx-background-radius: 15; -fx-font-size: 12px; -fx-min-width: 160; -fx-font-family: 'Segoe UI Emoji', 'Segoe UI';"
            : "-fx-text-fill: white; -fx-background-color: #9e9e9e; -fx-padding: 6 14; -fx-background-radius: 15; -fx-font-size: 12px; -fx-min-width: 160; -fx-font-family: 'Segoe UI Emoji', 'Segoe UI';";
        
        Label lblAfecta = new Label(textoAfecta);
        lblAfecta.setStyle(estiloAfecta);
        lblAfecta.setAlignment(Pos.CENTER);

        // Botón eliminar (🗑️) más grande
        Button btnEliminar = new Button("🗑️");
        btnEliminar.setStyle(
            "-fx-background-color: #f44336; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 18px; " +
            "-fx-padding: 10 16; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand; " +
            "-fx-font-family: 'Segoe UI Emoji', 'Segoe UI'; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 4, 0, 0, 2);"
        );
        
        btnEliminar.setOnMouseEntered(e -> btnEliminar.setStyle(
            "-fx-background-color: #d32f2f; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 18px; " +
            "-fx-padding: 10 16; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand; " +
            "-fx-font-family: 'Segoe UI Emoji', 'Segoe UI'; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 6, 0, 0, 3);"
        ));
        
        btnEliminar.setOnMouseExited(e -> btnEliminar.setStyle(
            "-fx-background-color: #f44336; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 18px; " +
            "-fx-padding: 10 16; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand; " +
            "-fx-font-family: 'Segoe UI Emoji', 'Segoe UI'; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 4, 0, 0, 2);"
        ));
        
        btnEliminar.setOnAction(e -> mostrarConfirmacionEliminar(etiqueta));

        // Doble click para modificar
        itemBox.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                mostrarFormularioModificar(etiqueta);
            }
        });

        // Hover effect mejorado
        itemBox.setOnMouseEntered(e -> itemBox.setStyle(
            "-fx-background-color: #f8f9fa; " +
            "-fx-border-color: #2196F3; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 10; " +
            "-fx-background-radius: 10; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(33, 150, 243, 0.3), 10, 0, 0, 4);"
        ));
        
        itemBox.setOnMouseExited(e -> itemBox.setStyle(
            "-fx-background-color: white; " +
            "-fx-border-color: #e0e0e0; " +
            "-fx-border-width: 1; " +
            "-fx-border-radius: 10; " +
            "-fx-background-radius: 10; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.08), 5, 0, 0, 2);"
        ));

        itemBox.getChildren().addAll(lblId, lblNombre, lblAfecta, btnEliminar);

        return itemBox;
    }

    private void mostrarFormularioAgregar() {
        formularioContainer.getChildren().clear();
        
        VBox formulario = new VBox(12);
        formulario.setPadding(new Insets(20));
        formulario.setStyle(
            "-fx-background-color: #E8F5E9; " +
            "-fx-border-color: #4CAF50; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 10; " +
            "-fx-background-radius: 10;"
        );
        formulario.setMaxWidth(850);

        Label titulo = new Label("➕ Nueva Etiqueta");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2E7D32; -fx-font-family: 'Segoe UI Emoji', 'Segoe UI';");

        Label lblNombre = new Label("Nombre:");
        lblNombre.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Ej: Consumibles, Papelería, Trámites");
        txtNombre.setStyle("-fx-font-size: 13px; -fx-padding: 8;");
        txtNombre.setPrefWidth(400);

        CheckBox chkAfecta = new CheckBox("Afecta Inventario");
        chkAfecta.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
        
        Label lblAyuda = new Label("💡 Marcar SOLO si requiere control de stock (ej: consumibles)");
        lblAyuda.setStyle("-fx-font-size: 11px; -fx-text-fill: #666; -fx-font-style: italic; -fx-font-family: 'Segoe UI Emoji', 'Segoe UI';");

        HBox botones = new HBox(10);
        botones.setAlignment(Pos.CENTER_RIGHT);
        
        Button btnGuardar = new Button("✓ Guardar");
        btnGuardar.setStyle(
            "-fx-background-color: #4CAF50; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 13px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 8 20; " +
            "-fx-background-radius: 5; " +
            "-fx-cursor: hand; " +
            "-fx-font-family: 'Segoe UI Emoji', 'Segoe UI';"
        );
        
        Button btnCancelar = new Button("✗ Cancelar");
        btnCancelar.setStyle(
            "-fx-background-color: #9e9e9e; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 13px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 8 20; " +
            "-fx-background-radius: 5; " +
            "-fx-cursor: hand; " +
            "-fx-font-family: 'Segoe UI Emoji', 'Segoe UI';"
        );

        btnGuardar.setOnAction(e -> {
            String nombre = txtNombre.getText().trim();
            if (nombre.isEmpty()) {
                mostrarMensaje("⚠️ El nombre no puede estar vacío", "#FFF3CD", "#856404");
                return;
            }

            if (etiquetaController.agregarEtiqueta(nombre, chkAfecta.isSelected())) {
                actualizarLista();
                mostrarMensaje("✓ Etiqueta agregada: " + nombre, "#D4EDDA", "#155724");
                formularioContainer.getChildren().clear();
            } else {
                mostrarMensaje("✗ No se pudo agregar la etiqueta", "#F8D7DA", "#721C24");
            }
        });

        btnCancelar.setOnAction(e -> formularioContainer.getChildren().clear());

        botones.getChildren().addAll(btnGuardar, btnCancelar);
        formulario.getChildren().addAll(titulo, lblNombre, txtNombre, chkAfecta, lblAyuda, botones);
        formularioContainer.getChildren().add(formulario);
    }

    private void mostrarFormularioModificar(Etiqueta etiqueta) {
        formularioContainer.getChildren().clear();
        
        VBox formulario = new VBox(12);
        formulario.setPadding(new Insets(20));
        formulario.setStyle(
            "-fx-background-color: #E3F2FD; " +
            "-fx-border-color: #2196F3; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 10; " +
            "-fx-background-radius: 10;"
        );
        formulario.setMaxWidth(850);

        Label titulo = new Label("✏️ Modificar Etiqueta #" + etiqueta.getEtiquetaId());
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1976D2; -fx-font-family: 'Segoe UI Emoji', 'Segoe UI';");

        Label lblNombre = new Label("Nombre:");
        lblNombre.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        TextField txtNombre = new TextField(etiqueta.getNombre());
        txtNombre.setStyle("-fx-font-size: 13px; -fx-padding: 8;");
        txtNombre.setPrefWidth(400);

        CheckBox chkAfecta = new CheckBox("Afecta Inventario");
        chkAfecta.setSelected(etiqueta.isAfectaInventario());
        chkAfecta.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
        
        Label lblAyuda = new Label("💡 Marcar SOLO si requiere control de stock (ej: consumibles)");
        lblAyuda.setStyle("-fx-font-size: 11px; -fx-text-fill: #666; -fx-font-style: italic; -fx-font-family: 'Segoe UI Emoji', 'Segoe UI';");

        HBox botones = new HBox(10);
        botones.setAlignment(Pos.CENTER_RIGHT);
        
        Button btnGuardar = new Button("✓ Guardar");
        btnGuardar.setStyle(
            "-fx-background-color: #2196F3; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 13px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 8 20; " +
            "-fx-background-radius: 5; " +
            "-fx-cursor: hand; " +
            "-fx-font-family: 'Segoe UI Emoji', 'Segoe UI';"
        );
        
        Button btnCancelar = new Button("✗ Cancelar");
        btnCancelar.setStyle(
            "-fx-background-color: #9e9e9e; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 13px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 8 20; " +
            "-fx-background-radius: 5; " +
            "-fx-cursor: hand; " +
            "-fx-font-family: 'Segoe UI Emoji', 'Segoe UI';"
        );

        btnGuardar.setOnAction(e -> {
            String nombre = txtNombre.getText().trim();
            if (nombre.isEmpty()) {
                mostrarMensaje("⚠️ El nombre no puede estar vacío", "#FFF3CD", "#856404");
                return;
            }

            if (etiquetaController.actualizarEtiqueta(etiqueta.getEtiquetaId(), nombre, chkAfecta.isSelected())) {
                actualizarLista();
                mostrarMensaje("✓ Etiqueta modificada correctamente", "#D4EDDA", "#155724");
                formularioContainer.getChildren().clear();
            } else {
                mostrarMensaje("✗ No se pudo modificar la etiqueta", "#F8D7DA", "#721C24");
            }
        });

        btnCancelar.setOnAction(e -> formularioContainer.getChildren().clear());

        botones.getChildren().addAll(btnGuardar, btnCancelar);
        formulario.getChildren().addAll(titulo, lblNombre, txtNombre, chkAfecta, lblAyuda, botones);
        formularioContainer.getChildren().add(formulario);
    }

    private void mostrarConfirmacionEliminar(Etiqueta etiqueta) {
        formularioContainer.getChildren().clear();
        
        VBox formulario = new VBox(15);
        formulario.setPadding(new Insets(20));
        formulario.setStyle(
            "-fx-background-color: #FFEBEE; " +
            "-fx-border-color: #f44336; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 10; " +
            "-fx-background-radius: 10;"
        );
        formulario.setMaxWidth(850);
        formulario.setAlignment(Pos.CENTER);

        Label titulo = new Label("⚠️ ¿Eliminar etiqueta?");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #c62828; -fx-font-family: 'Segoe UI Emoji', 'Segoe UI';");

        Label mensaje = new Label("Etiqueta: " + etiqueta.getNombre());
        mensaje.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");

        HBox botones = new HBox(10);
        botones.setAlignment(Pos.CENTER);
        
        Button btnAceptar = new Button("✓ Aceptar");
        btnAceptar.setStyle(
            "-fx-background-color: #f44336; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 13px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 8 20; " +
            "-fx-background-radius: 5; " +
            "-fx-cursor: hand; " +
            "-fx-font-family: 'Segoe UI Emoji', 'Segoe UI';"
        );
        
        Button btnCancelar = new Button("✗ Cancelar");
        btnCancelar.setStyle(
            "-fx-background-color: #9e9e9e; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 13px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 8 20; " +
            "-fx-background-radius: 5; " +
            "-fx-cursor: hand; " +
            "-fx-font-family: 'Segoe UI Emoji', 'Segoe UI';"
        );

        btnAceptar.setOnAction(e -> {
            Etiqueta etiquetaAEliminar = etiquetaController.getEtiquetaDAO().obtener(etiqueta.getEtiquetaId());
            if (etiquetaAEliminar != null && etiquetaController.eliminarEtiqueta(etiquetaAEliminar)) {
                actualizarLista();
                mostrarMensaje("✓ Etiqueta eliminada correctamente", "#D4EDDA", "#155724");
                formularioContainer.getChildren().clear();
            } else {
                mostrarMensaje("✗ No se pudo eliminar la etiqueta", "#F8D7DA", "#721C24");
            }
        });

        btnCancelar.setOnAction(e -> formularioContainer.getChildren().clear());

        botones.getChildren().addAll(btnAceptar, btnCancelar);
        formulario.getChildren().addAll(titulo, mensaje, botones);
        formularioContainer.getChildren().add(formulario);
    }

    private void mostrarMensaje(String texto, String bgColor, String textColor) {
        formularioContainer.getChildren().clear();
        
        Label mensaje = new Label(texto);
        mensaje.setStyle(
            "-fx-background-color: " + bgColor + "; " +
            "-fx-text-fill: " + textColor + "; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 15 20; " +
            "-fx-background-radius: 8; " +
            "-fx-border-color: " + textColor + "; " +
            "-fx-border-width: 1; " +
            "-fx-border-radius: 8; " +
            "-fx-font-family: 'Segoe UI Emoji', 'Segoe UI';"
        );
        mensaje.setMaxWidth(850);
        
        formularioContainer.getChildren().add(mensaje);
        
        // Auto-ocultar después de 3 segundos
        javafx.animation.PauseTransition delay = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(3));
        delay.setOnFinished(e -> formularioContainer.getChildren().clear());
        delay.play();
    }
}
