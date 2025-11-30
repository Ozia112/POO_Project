package view;

import java.util.List;

import controller.EtiquetaController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Etiqueta;

public class EtiquetasGUI {

    private final EtiquetaController etiquetaController;

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

        // Panel informativo más compacto
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
        Label lblExplicacion3 = new Label("⚠️ Solo CONSUMIBLES afecta inventario");
        lblExplicacion3.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #D32F2F;");
        Label lblExplicacion4 = new Label("💡 Doble click para modificar");
        lblExplicacion4.setStyle("-fx-font-size: 11px; -fx-font-style: italic; -fx-text-fill: #666;");
        col2.getChildren().addAll(lblExplicacion3, lblExplicacion4);
        
        hboxInfo.getChildren().addAll(col1, col2);
        infoBox.getChildren().addAll(lblInfo, hboxInfo);

        // Lista de etiquetas (VBox que se actualiza)
        VBox listaEtiquetas = new VBox(8);
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
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 6, 0, 0, 3);"
        ));

        btnAgregar.setOnAction(e -> mostrarDialogoAgregar(listaEtiquetas));

        // Cargar etiquetas iniciales
        actualizarLista(listaEtiquetas);

        root.getChildren().addAll(titulo, infoBox, scrollPane, btnAgregar);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        return root;
    }

    private void actualizarLista(VBox listaEtiquetas) {
        listaEtiquetas.getChildren().clear();
        List<Etiqueta> etiquetas = etiquetaController.obtenerTodasEtiquetas();

        for (Etiqueta etiqueta : etiquetas) {
            HBox itemBox = crearItemEtiqueta(etiqueta, listaEtiquetas);
            listaEtiquetas.getChildren().add(itemBox);
        }
    }

    private HBox crearItemEtiqueta(Etiqueta etiqueta, VBox listaEtiquetas) {
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
            ? "-fx-text-fill: white; -fx-background-color: #f44336; -fx-font-weight: bold; -fx-padding: 6 14; -fx-background-radius: 15; -fx-font-size: 12px; -fx-min-width: 160;"
            : "-fx-text-fill: white; -fx-background-color: #9e9e9e; -fx-padding: 6 14; -fx-background-radius: 15; -fx-font-size: 12px; -fx-min-width: 160;";
        
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
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 4, 0, 0, 2);"
        );
        
        btnEliminar.setOnMouseEntered(e -> btnEliminar.setStyle(
            "-fx-background-color: #d32f2f; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 18px; " +
            "-fx-padding: 10 16; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 6, 0, 0, 3);"
        ));
        
        btnEliminar.setOnMouseExited(e -> btnEliminar.setStyle(
            "-fx-background-color: #f44336; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 18px; " +
            "-fx-padding: 10 16; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 4, 0, 0, 2);"
        ));
        
        btnEliminar.setOnAction(e -> {
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmar eliminación");
            confirmacion.setHeaderText("¿Eliminar etiqueta?");
            confirmacion.setContentText("Etiqueta: " + etiqueta.getNombre());
            confirmacion.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            
            if (confirmacion.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                if (etiquetaController.eliminarEtiqueta(etiqueta.getEtiquetaId())) {
                    actualizarLista(listaEtiquetas);
                    mostrarAlerta("Éxito", "Etiqueta eliminada correctamente");
                } else {
                    mostrarAlerta("Error", "No se pudo eliminar la etiqueta");
                }
            }
        });

        // Doble click para modificar
        itemBox.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                mostrarDialogoModificar(etiqueta, listaEtiquetas);
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

    private void mostrarDialogoAgregar(VBox listaEtiquetas) {
        Alert dialogo = new Alert(Alert.AlertType.NONE);
        dialogo.setTitle("Agregar Etiqueta");
        dialogo.setHeaderText("Nueva Etiqueta");
        dialogo.initModality(javafx.stage.Modality.APPLICATION_MODAL);

        VBox contenido = new VBox(15);
        contenido.setPadding(new Insets(20));
        contenido.setStyle("-fx-background-color: white;");

        Label lblNombre = new Label("Nombre:");
        lblNombre.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Ej: Consumibles, Papelería, Trámites");
        txtNombre.setStyle("-fx-font-size: 13px; -fx-padding: 8;");
        txtNombre.setPrefWidth(350);

        CheckBox chkAfecta = new CheckBox("Afecta Inventario");
        chkAfecta.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
        
        Label lblAyuda = new Label("✓ Marcar SOLO si requiere control de stock (ej: consumibles)");
        lblAyuda.setStyle("-fx-font-size: 11px; -fx-text-fill: #666; -fx-font-style: italic;");

        contenido.getChildren().addAll(lblNombre, txtNombre, chkAfecta, lblAyuda);
        dialogo.getDialogPane().setContent(contenido);
        dialogo.getDialogPane().setMinWidth(450);

        ButtonType btnGuardar = new ButtonType("Guardar", ButtonType.OK.getButtonData());
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonType.CANCEL.getButtonData());
        dialogo.getButtonTypes().addAll(btnGuardar, btnCancelar);

        dialogo.showAndWait().ifPresent(response -> {
            if (response == btnGuardar) {
                String nombre = txtNombre.getText().trim();
                if (nombre.isEmpty()) {
                    mostrarAlerta("Error", "El nombre no puede estar vacío");
                    return;
                }

                if (etiquetaController.agregarEtiqueta(nombre, chkAfecta.isSelected())) {
                    actualizarLista(listaEtiquetas);
                    mostrarAlerta("Éxito", "Etiqueta agregada: " + nombre);
                } else {
                    mostrarAlerta("Error", "No se pudo agregar la etiqueta");
                }
            }
        });
    }

    private void mostrarDialogoModificar(Etiqueta etiqueta, VBox listaEtiquetas) {
        Alert dialogo = new Alert(Alert.AlertType.NONE);
        dialogo.setTitle("Modificar Etiqueta");
        dialogo.setHeaderText("Editar: " + etiqueta.getNombre());
        dialogo.initModality(javafx.stage.Modality.APPLICATION_MODAL);

        VBox contenido = new VBox(15);
        contenido.setPadding(new Insets(20));
        contenido.setStyle("-fx-background-color: white;");

        Label lblId = new Label("ID: #" + etiqueta.getEtiquetaId());
        lblId.setStyle("-fx-font-weight: bold; -fx-text-fill: #2196F3; -fx-font-size: 13px;");

        Label lblNombre = new Label("Nombre:");
        lblNombre.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        TextField txtNombre = new TextField(etiqueta.getNombre());
        txtNombre.setStyle("-fx-font-size: 13px; -fx-padding: 8;");
        txtNombre.setPrefWidth(350);

        CheckBox chkAfecta = new CheckBox("Afecta Inventario");
        chkAfecta.setSelected(etiqueta.isAfectaInventario());
        chkAfecta.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
        
        Label lblAyuda = new Label("✓ Marcar SOLO si requiere control de stock (ej: consumibles)");
        lblAyuda.setStyle("-fx-font-size: 11px; -fx-text-fill: #666; -fx-font-style: italic;");

        contenido.getChildren().addAll(lblId, lblNombre, txtNombre, chkAfecta, lblAyuda);
        dialogo.getDialogPane().setContent(contenido);
        dialogo.getDialogPane().setMinWidth(450);

        ButtonType btnGuardar = new ButtonType("Guardar", ButtonType.OK.getButtonData());
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonType.CANCEL.getButtonData());
        dialogo.getButtonTypes().addAll(btnGuardar, btnCancelar);

        dialogo.showAndWait().ifPresent(response -> {
            if (response == btnGuardar) {
                String nombre = txtNombre.getText().trim();
                if (nombre.isEmpty()) {
                    mostrarAlerta("Error", "El nombre no puede estar vacío");
                    return;
                }

                if (etiquetaController.actualizarEtiqueta(etiqueta.getEtiquetaId(), nombre, chkAfecta.isSelected())) {
                    actualizarLista(listaEtiquetas);
                    mostrarAlerta("Éxito", "Etiqueta modificada correctamente");
                } else {
                    mostrarAlerta("Error", "No se pudo modificar la etiqueta");
                }
            }
        });
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        alert.showAndWait();
    }
}
