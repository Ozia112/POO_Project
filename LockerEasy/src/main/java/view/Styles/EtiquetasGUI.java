package view.Styles;

import java.util.List;

import controller.EtiquetaController;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Etiqueta;

public class EtiquetasGUI {

    private final EtiquetaController etiquetaController;

    public EtiquetasGUI(EtiquetaController etiquetaController) {
        this.etiquetaController = etiquetaController;
    }

    public void mostrar(Stage stage) {

        // Tabla de etiquetas
        TableView<Etiqueta> tabla = new TableView<>();
        tabla.setPrefWidth(700);

        TableColumn<Etiqueta, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty("#" + c.getValue().getEtiquetaId()));

        TableColumn<Etiqueta, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getNombre()));

        TableColumn<Etiqueta, String> colAfectaInventario = new TableColumn<>("Afecta Inventario");
        colAfectaInventario.setCellValueFactory(c -> {
            boolean afecta = c.getValue().isAfectaInventario();
            String valor = afecta ? "✓ SÍ" : "✗ NO";
            return new javafx.beans.property.SimpleStringProperty(valor);
        });

        // Aplicar estilo a las celdas de "Afecta Inventario"
        colAfectaInventario.setCellFactory(col -> new TableCell<Etiqueta, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.contains("SÍ")) {
                        setStyle("-fx-text-fill: #d32f2f; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #666666;");
                    }
                }
            }
        });

        tabla.getColumns().addAll(colId, colNombre, colAfectaInventario);

        // Ajustes de ancho de columnas
        colId.setMaxWidth(100); colId.setPrefWidth(80);
        colNombre.setMinWidth(300); colNombre.setPrefWidth(400);
        colAfectaInventario.setMaxWidth(180); colAfectaInventario.setPrefWidth(180);

        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        HBox.setHgrow(tabla, Priority.ALWAYS);

        // Formulario
        VBox form = new VBox(12);
        form.setPadding(new Insets(20));
        form.setPrefWidth(420);
        form.getStyleClass().add("caja-form");

        Label tituloForm = new Label("Gestionar Etiquetas");
        tituloForm.getStyleClass().add("titulo1");

        // Panel informativo con explicación
        VBox infoBox = new VBox(8);
        infoBox.setStyle("-fx-background-color: #E3F2FD; -fx-padding: 15; -fx-background-radius: 12; -fx-border-color: #2196F3; -fx-border-width: 2; -fx-border-radius: 12;");
        infoBox.setMaxWidth(390); // Limitar ancho del panel
        
        Label lblInfo = new Label("ℹ️ Información sobre Etiquetas");
        lblInfo.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1976D2;");
        lblInfo.setWrapText(true);
        lblInfo.setMaxWidth(360);
        
        Label lblExplicacion1 = new Label("• Afecta Inventario = SÍ: Las ventas reducen existencias");
        lblExplicacion1.setStyle("-fx-font-size: 12px;");
        lblExplicacion1.setWrapText(true);
        lblExplicacion1.setMaxWidth(360);
        
        Label lblExplicacion2 = new Label("• Afecta Inventario = NO: Las ventas NO modifican existencias");
        lblExplicacion2.setStyle("-fx-font-size: 12px;");
        lblExplicacion2.setWrapText(true);
        lblExplicacion2.setMaxWidth(360);
        
        Label lblExplicacion3 = new Label("⚠️ Solo CONSUMIBLES afecta inventario");
        lblExplicacion3.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #D32F2F;");
        lblExplicacion3.setWrapText(true);
        lblExplicacion3.setMaxWidth(360);
        
        infoBox.getChildren().addAll(lblInfo, lblExplicacion1, lblExplicacion2, lblExplicacion3);

        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Ej: Consumibles, Papelería, Trámites, Rentas");

        CheckBox chkAfectaInventario = new CheckBox("Afecta inventario");
        chkAfectaInventario.setStyle("-fx-font-size: 14px;");

        Label lblAyuda = new Label("✓ Marcar SOLO si requiere control de stock (como Consumibles)");
        lblAyuda.setStyle("-fx-font-size: 11px; -fx-text-fill: #666; -fx-font-style: italic;");

        // Campo ID (para modificar/eliminar)
        TextField txtId = new TextField();
        txtId.setPromptText("Ej: 10 (solo para modificar/eliminar)");

        // GridPane para alinear etiquetas y campos
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(8));

        ColumnConstraints col1 = new ColumnConstraints(160);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(col1, col2);

        Label lblId = new Label("ID:"); 
        lblId.setLabelFor(txtId);
        lblId.setStyle("-fx-font-size:13px;");

        Label lblNombre = new Label("Nombre:"); 
        lblNombre.setLabelFor(txtNombre);
        lblNombre.setStyle("-fx-font-size:13px;");

        grid.add(lblId, 0, 0);
        grid.add(txtId, 1, 0);
        grid.add(lblNombre, 0, 1);
        grid.add(txtNombre, 1, 1);
        grid.add(chkAfectaInventario, 1, 2);

        // Botones de acción: Agregar, Modificar, Eliminar
        HBox botones = new HBox(8);
        Button btnAgregar = new Button("Agregar");
        Button btnModificar = new Button("Modificar");
        Button btnEliminar = new Button("Eliminar");
        
        String btnStyle = "-fx-font-size:12px; -fx-padding:6 12 6 12;";
        btnAgregar.setStyle(btnStyle);
        btnModificar.setStyle(btnStyle);
        btnEliminar.setStyle(btnStyle);
        
        botones.getChildren().addAll(btnAgregar, btnModificar, btnEliminar);

        form.getChildren().addAll(tituloForm, infoBox, grid, lblAyuda, botones);

        // Layout: formulario a la izquierda y tabla a la derecha
        form.setPrefWidth(420);
        tabla.setPrefWidth(750);

        HBox root = new HBox(20, form, tabla);
        root.setPadding(new Insets(20));

        Scene scene = new Scene(new StackPane(root), 1280, 720);
        try {
            scene.getStylesheets().add(getClass().getResource("Styles.css").toExternalForm());
        } catch (Exception ex) {
            // stylesheet opcional
        }

        stage.setTitle("Etiquetas - Gestión");
        stage.setScene(scene);
        stage.show();

        // Cargar datos iniciales
        actualizarTabla(tabla);

        // Selección en tabla → llenar formulario con los datos de la etiqueta seleccionada
        tabla.getSelectionModel().selectedItemProperty().addListener((obs, oldV, nueva) -> {
            if (nueva == null) return;
            txtId.setText(String.valueOf(nueva.getEtiquetaId()));
            txtNombre.setText(nueva.getNombre());
            chkAfectaInventario.setSelected(nueva.isAfectaInventario());
        });

        // AGREGAR
        btnAgregar.setOnAction(e -> {
            String nombre = txtNombre.getText().trim();
            if (nombre.isEmpty()) {
                mostrarAlerta("Error", "El nombre de la etiqueta no puede estar vacío");
                return;
            }

            boolean afectaInventario = chkAfectaInventario.isSelected();

            if (etiquetaController.agregarEtiqueta(nombre, afectaInventario)) {
                // Refrescar vista y limpiar formulario
                actualizarTabla(tabla);
                txtId.clear();
                txtNombre.clear();
                chkAfectaInventario.setSelected(false);
                tabla.getSelectionModel().clearSelection();
                
                String mensaje = afectaInventario 
                    ? "Etiqueta creada: " + nombre + " (AFECTA INVENTARIO)" 
                    : "Etiqueta creada: " + nombre + " (no afecta inventario)";
                mostrarAlerta("Éxito", mensaje);
            } else {
                mostrarAlerta("Error", "No se pudo crear la etiqueta (posiblemente ya existe)");
            }
        });

        // MODIFICAR
        btnModificar.setOnAction(e -> {
            String idStr = txtId.getText().trim();
            if (idStr.isEmpty()) {
                mostrarAlerta("Error", "Selecciona una etiqueta de la tabla o ingresa un ID");
                return;
            }

            int id;
            try {
                id = Integer.parseInt(idStr);
            } catch (NumberFormatException ex) {
                mostrarAlerta("Error", "ID inválido");
                return;
            }

            String nombre = txtNombre.getText().trim();
            if (nombre.isEmpty()) {
                mostrarAlerta("Error", "El nombre no puede estar vacío");
                return;
            }

            boolean afectaInventario = chkAfectaInventario.isSelected();

            if (etiquetaController.actualizarEtiqueta(id, nombre, afectaInventario)) {
                actualizarTabla(tabla);
                txtId.clear();
                txtNombre.clear();
                chkAfectaInventario.setSelected(false);
                tabla.getSelectionModel().clearSelection();
                mostrarAlerta("Éxito", "Etiqueta modificada correctamente");
            } else {
                mostrarAlerta("Error", "No se pudo modificar la etiqueta");
            }
        });

        // ELIMINAR
        btnEliminar.setOnAction(e -> {
            String idStr = txtId.getText().trim();
            if (idStr.isEmpty()) {
                mostrarAlerta("Error", "Selecciona una etiqueta de la tabla o ingresa un ID");
                return;
            }

            int id;
            try {
                id = Integer.parseInt(idStr);
            } catch (NumberFormatException ex) {
                mostrarAlerta("Error", "ID inválido");
                return;
            }

            // Confirmación antes de eliminar
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmar eliminación");
            confirmacion.setHeaderText("¿Eliminar etiqueta?");
            confirmacion.setContentText("Esta acción no se puede deshacer.");
            
            if (confirmacion.showAndWait().get() == ButtonType.OK) {
                if (etiquetaController.eliminarEtiqueta(id)) {
                    actualizarTabla(tabla);
                    txtId.clear();
                    txtNombre.clear();
                    chkAfectaInventario.setSelected(false);
                    tabla.getSelectionModel().clearSelection();
                    mostrarAlerta("Éxito", "Etiqueta eliminada correctamente");
                } else {
                    mostrarAlerta("Error", "No se pudo eliminar la etiqueta");
                }
            }
        });
    }

    private void actualizarTabla(TableView<Etiqueta> tabla) {
        tabla.getItems().clear();
        List<Etiqueta> etiquetas = etiquetaController.obtenerTodasEtiquetas();
        tabla.getItems().addAll(etiquetas);
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
