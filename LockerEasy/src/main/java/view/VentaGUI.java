package view;

import controller.VentaController;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Venta;

import java.util.Arrays;
import java.util.List;

public class VentaGUI {

	private final VentaController ventaController;

	public VentaGUI(VentaController ventaController) {
		this.ventaController = ventaController;
	}

	public void mostrar(Stage stage) {

		// Tabla de productos
		TableView<Venta> tabla = new TableView<>();
		tabla.setPrefWidth(700);

		TableColumn<Venta, String> colId = new TableColumn<>("ID");
		colId.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(String.valueOf(c.getValue().getIdProducto())));

		TableColumn<Venta, String> colNombre = new TableColumn<>("Nombre");
		colNombre.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getNombre()));

		TableColumn<Venta, String> colPrecio = new TableColumn<>("Precio");
		colPrecio.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty("$" + c.getValue().getPrecio()));

		TableColumn<Venta, String> colExistentes = new TableColumn<>("Existentes");
		colExistentes.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(String.valueOf(c.getValue().getExistentes())));

		TableColumn<Venta, String> colDisponible = new TableColumn<>("Disponible");
		colDisponible.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().isDisponible() ? "Sí" : "No"));

		TableColumn<Venta, String> colEtiquetas = new TableColumn<>("Etiquetas");
		colEtiquetas.setCellValueFactory(c -> {
			List<String> etiquetas = c.getValue().getEtiquetas();
			String etiquetasTexto = (etiquetas == null || etiquetas.isEmpty()) ? "" : String.join(", ", etiquetas);
			return new javafx.beans.property.SimpleStringProperty(etiquetasTexto);
		});

		tabla.getColumns().addAll(colId, colNombre, colPrecio, colExistentes, colDisponible, colEtiquetas);

		// Ajustes de ancho de columnas
		colId.setPrefWidth(50);
		colNombre.setPrefWidth(250);
		colPrecio.setPrefWidth(100);
		colExistentes.setPrefWidth(100);
		colDisponible.setPrefWidth(100);
		colEtiquetas.setPrefWidth(150);

		// Evitar que quede una 'columna' vacía al final: forzar política que distribuye
		tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		HBox.setHgrow(tabla, Priority.ALWAYS);

		// Formulario: usar GridPane para mostrar etiquetas visibles junto a cada campo
		VBox form = new VBox(12);
		form.setPadding(new Insets(20));
		form.setPrefWidth(420);
		form.getStyleClass().add("caja-form");

		Label tituloForm = new Label("Gestionar Productos");
		tituloForm.getStyleClass().add("titulo1");

		TextField txtId = new TextField();
		TextField txtNombre = new TextField();
		TextField txtPrecio = new TextField();
		TextField txtExistentes = new TextField();
		// Prompt text (ejemplos) para ayudar al usuario
		txtId.setPromptText("Ej: 10 (solo para modificar/eliminar)");
		txtNombre.setPromptText("Ej: Caja de tornillos");
		txtPrecio.setPromptText("Ej: 12.50");
		txtExistentes.setPromptText("Ej: 20");

		// Etiquetas restringidas a opciones predefinidas
		ComboBox<String> comboEtiquetas = new ComboBox<>();
		comboEtiquetas.getItems().addAll("Consumible", "Impresión", "Trámite");

		CheckBox chkDisponible = new CheckBox("Disponible para la venta");

		// GridPane para alinear etiquetas y campos
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(8));

		ColumnConstraints col1 = new ColumnConstraints(160); // ancho fijo para etiquetas
		ColumnConstraints col2 = new ColumnConstraints();
		col2.setHgrow(Priority.ALWAYS);
		grid.getColumnConstraints().addAll(col1, col2);

		// Añadir filas: Label + Control
		Label lblId = new Label("ID:"); lblId.setLabelFor(txtId);
		Label lblNombre = new Label("Producto:"); lblNombre.setLabelFor(txtNombre);
		Label lblPrecio = new Label("Precio :"); lblPrecio.setLabelFor(txtPrecio);
		Label lblExistentes = new Label("Existentes:"); lblExistentes.setLabelFor(txtExistentes);
		Label lblEtiqueta = new Label("Etiqueta:"); lblEtiqueta.setLabelFor(comboEtiquetas);
		// Ajustar tamaño de las etiquetas del formulario para que no sean tan grandes
		String labelStyle = "-fx-font-size:13px;";
		lblId.setStyle(labelStyle);
		lblNombre.setStyle(labelStyle);
		lblPrecio.setStyle(labelStyle);
		lblExistentes.setStyle(labelStyle);
		lblEtiqueta.setStyle(labelStyle);

		grid.add(lblId, 0, 0);
		grid.add(txtId, 1, 0);

		grid.add(lblNombre, 0, 1);
		grid.add(txtNombre, 1, 1);

		grid.add(lblPrecio, 0, 2);
		grid.add(txtPrecio, 1, 2);

		grid.add(lblExistentes, 0, 3);
		grid.add(txtExistentes, 1, 3);

		grid.add(lblEtiqueta, 0, 4);
		grid.add(comboEtiquetas, 1, 4);

		// Checkbox en columna de controles
		grid.add(chkDisponible, 1, 5);

		// Botones de acción: Agregar, Modificar, Eliminar
		HBox botones = new HBox(8);
		Button btnAgregar = new Button("Agregar");
		Button btnModificar = new Button("Modificar");
		Button btnEliminar = new Button("Eliminar");
		// Estilo directo para botones del formulario (fuente más pequeña y padding)
		String btnStyle = "-fx-font-size:12px; -fx-padding:6 12 6 12;";
		btnAgregar.setStyle(btnStyle);
		btnModificar.setStyle(btnStyle);
		btnEliminar.setStyle(btnStyle);
		botones.getChildren().addAll(btnAgregar, btnModificar, btnEliminar);

		form.getChildren().addAll(tituloForm, grid, botones);

		// Layout: formulario a la izquierda y catálogo a la derecha
		form.setPrefWidth(420);
		tabla.setPrefWidth(750);

		HBox root = new HBox(20, form, tabla);
		root.setPadding(new Insets(20));

		Scene scene = new Scene(new StackPane(root), 1280, 720);
		try {
			scene.getStylesheets().add(getClass().getResource("Styles/Styles.css").toExternalForm());
		} catch (Exception ex) {
			// stylesheet opcional: no bloquear si no existe
		}

		stage.setTitle("Productos - Gestión");
		stage.setScene(scene);
		stage.show();

		// Cargar datos iniciales
		actualizarTabla(tabla);

		// Selección en tabla → llenar formulario con los datos del producto seleccionado
		tabla.getSelectionModel().selectedItemProperty().addListener((obs, oldV, nueva) -> {
			if (nueva == null) return;
			txtId.setText(String.valueOf(nueva.getIdProducto()));
			txtNombre.setText(nueva.getNombre());
			txtPrecio.setText(String.valueOf(nueva.getPrecio()));
			txtExistentes.setText(String.valueOf(nueva.getExistentes()));
			List<String> etiquetasLista = nueva.getEtiquetas();
			comboEtiquetas.setValue(etiquetasLista == null || etiquetasLista.isEmpty() ? null : etiquetasLista.get(0));
			chkDisponible.setSelected(nueva.isDisponible());
		});

		// AGREGAR
		btnAgregar.setOnAction(e -> {
			String nombre = txtNombre.getText().trim();
			if (nombre.isEmpty()) {
				System.err.println("Nombre vacío");
				return;
			}

			float precio = 0f;
			int exist = 0;
			try { precio = Float.parseFloat(txtPrecio.getText().trim()); } catch (NumberFormatException ex) { /* dejar 0 */ }
			try { exist = Integer.parseInt(txtExistentes.getText().trim()); } catch (NumberFormatException ex) { /* dejar 0 */ }

			// Obtener etiqueta seleccionada (si hay)
			List<String> tags = comboEtiquetas.getValue() == null ? List.of() : List.of(comboEtiquetas.getValue());

			boolean ok = ventaController.agregarProducto(nombre, precio, exist, tags);
			if (ok) {
				// Refrescar vista y limpiar formulario manualmente (sin botón adicional)
				actualizarTabla(tabla);
				txtId.clear(); txtNombre.clear(); txtPrecio.clear(); txtExistentes.clear(); comboEtiquetas.getSelectionModel().clearSelection(); chkDisponible.setSelected(false);
				tabla.getSelectionModel().clearSelection();
			}
		});

		// MODIFICAR
		btnModificar.setOnAction(e -> {
			String idText = txtId.getText().trim();
			if (idText.isEmpty()) {
				System.err.println("Debe indicar el ID del producto a modificar");
				return;
			}
			int id;
			try { id = Integer.parseInt(idText); } catch (NumberFormatException ex) { System.err.println("ID inválido"); return; }

			Venta p = ventaController.buscarProdcuto(id);
			if (p == null) { System.err.println("Producto no encontrado: " + id); return; }

			String nuevoNombre = txtNombre.getText().trim();
			if (!nuevoNombre.isEmpty()) ventaController.actualizarProducto(id, nuevoNombre);

			String precioText = txtPrecio.getText().trim();
			if (!precioText.isEmpty()) {
				try { ventaController.actualizarProducto(id, Float.parseFloat(precioText)); } catch (NumberFormatException ex) { System.err.println("Precio inválido"); }
			}

			String existText = txtExistentes.getText().trim();
			if (!existText.isEmpty()) {
				try { ventaController.actualizarProducto(id, Integer.parseInt(existText)); } catch (NumberFormatException ex) { System.err.println("Existentes inválido"); }
			}

			List<String> tags = comboEtiquetas.getValue() == null ? List.of() : List.of(comboEtiquetas.getValue());
			if (!tags.isEmpty()) ventaController.actualizarProdcuto(id, tags);

			ventaController.actualizarProducto(id, chkDisponible.isSelected());

			// Actualizar vista tras cambios realizados
			actualizarTabla(tabla);
		});

		// ELIMINAR
		btnEliminar.setOnAction(e -> {
			String idText = txtId.getText().trim();
			if (idText.isEmpty()) { System.err.println("Indique ID a eliminar"); return; }
			int id;
			try { id = Integer.parseInt(idText); } catch (NumberFormatException ex) { System.err.println("ID inválido"); return; }

			boolean ok = ventaController.eliminarProducto(id);
			if (ok) {
				// Actualizar vista y limpiar formulario manualmente
				actualizarTabla(tabla);
				txtId.clear(); txtNombre.clear(); txtPrecio.clear(); txtExistentes.clear(); comboEtiquetas.getSelectionModel().clearSelection(); chkDisponible.setSelected(false);
				tabla.getSelectionModel().clearSelection();
			}
		});
	}

	private static List<String> parseEtiquetas(String texto) {
		if (texto == null || texto.trim().isEmpty()) return List.of();
		String[] parts = Arrays.stream(texto.split(",")).map(String::trim).filter(s -> !s.isEmpty()).toArray(String[]::new);
		return Arrays.asList(parts);
	}

	private void actualizarTabla(TableView<Venta> tabla) {
		tabla.getItems().clear();
		tabla.getItems().addAll(ventaController.obtenerTodosLosProductos());
	}
  
	public Pane getVistaIntegrada() {

		// ---- ES EXACTAMENTE LO MISMO DE mostrar(...) PERO SIN CREAR ESCENA NI STAGE ----

		TableView<Venta> tabla = new TableView<>();
		tabla.setPrefWidth(700);

		TableColumn<Venta, String> colId = new TableColumn<>("ID");
		colId.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(String.valueOf(c.getValue().getIdProducto())));

		TableColumn<Venta, String> colNombre = new TableColumn<>("Nombre");
		colNombre.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getNombre()));

		TableColumn<Venta, String> colPrecio = new TableColumn<>("Precio");
		colPrecio.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty("$" + c.getValue().getPrecio()));

		TableColumn<Venta, String> colExistentes = new TableColumn<>("Existentes");
		colExistentes.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(String.valueOf(c.getValue().getExistentes())));

		TableColumn<Venta, String> colDisponible = new TableColumn<>("Disponible");
		colDisponible.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().isDisponible() ? "Sí" : "No"));

		TableColumn<Venta, String> colEtiquetas = new TableColumn<>("Etiquetas");
		colEtiquetas.setCellValueFactory(c -> {
			List<String> etiquetas = c.getValue().getEtiquetas();
			String etiquetasTexto = (etiquetas == null || etiquetas.isEmpty()) ? "" : String.join(", ", etiquetas);
			return new javafx.beans.property.SimpleStringProperty(etiquetasTexto);
		});

		tabla.getColumns().addAll(colId, colNombre, colPrecio, colExistentes, colDisponible, colEtiquetas);

		// Ajustes de ancho de columnas
		colId.setPrefWidth(50);
		colNombre.setPrefWidth(250);
		colPrecio.setPrefWidth(100);
		colExistentes.setPrefWidth(100);
		colDisponible.setPrefWidth(100);
		colEtiquetas.setPrefWidth(150);

		tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		// ---- FORM ----
		VBox form = new VBox(12);
		form.setPadding(new Insets(20));
		form.setPrefWidth(420);

		Label tituloForm = new Label("Gestionar Productos");

		TextField txtId = new TextField();
		TextField txtNombre = new TextField();
		TextField txtPrecio = new TextField();
		TextField txtExistentes = new TextField();

		txtId.setPromptText("ID para editar/eliminar");
		txtNombre.setPromptText("Nombre del producto");
		txtPrecio.setPromptText("Precio");
		txtExistentes.setPromptText("Existentes");

		ComboBox<String> comboEtiquetas = new ComboBox<>();
		comboEtiquetas.getItems().addAll("Consumible", "Impresión", "Trámite");

		CheckBox chkDisponible = new CheckBox("Disponible");

		GridPane grid = new GridPane();
		grid.setHgap(10); grid.setVgap(10);
		grid.setPadding(new Insets(8));

		grid.addRow(0, new Label("ID:"), txtId);
		grid.addRow(1, new Label("Producto:"), txtNombre);
		grid.addRow(2, new Label("Precio:"), txtPrecio);
		grid.addRow(3, new Label("Existentes:"), txtExistentes);
		grid.addRow(4, new Label("Etiqueta:"), comboEtiquetas);
		grid.addRow(5, chkDisponible);

		HBox botones = new HBox(8);
		Button btnAgregar = new Button("Agregar");
		Button btnModificar = new Button("Modificar");
		Button btnEliminar = new Button("Eliminar");
		botones.getChildren().addAll(btnAgregar, btnModificar, btnEliminar);

		form.getChildren().addAll(tituloForm, grid, botones);

		HBox root = new HBox(20, form, tabla);
		root.setPadding(new Insets(20));

		// ---- MISMA LÓGICA ----
		actualizarTabla(tabla);

		tabla.getSelectionModel().selectedItemProperty().addListener((obs, oldV, nueva) -> {
			if (nueva == null) return;
			txtId.setText(String.valueOf(nueva.getIdProducto()));
			txtNombre.setText(nueva.getNombre());
			txtPrecio.setText(String.valueOf(nueva.getPrecio()));
			txtExistentes.setText(String.valueOf(nueva.getExistentes()));
			comboEtiquetas.setValue(
				nueva.getEtiquetas() == null || nueva.getEtiquetas().isEmpty() ?
						null : nueva.getEtiquetas().get(0)
			);
			chkDisponible.setSelected(nueva.isDisponible());
		});

		btnAgregar.setOnAction(e -> {
			String nombre = txtNombre.getText().trim();
			if (nombre.isEmpty()) return;

			float precio = 0;
			int exist = 0;
			try { precio = Float.parseFloat(txtPrecio.getText().trim()); } catch (Exception ignored) {}
			try { exist = Integer.parseInt(txtExistentes.getText().trim()); } catch (Exception ignored) {}

			List<String> tags = comboEtiquetas.getValue() == null ? List.of() : List.of(comboEtiquetas.getValue());

			if (ventaController.agregarProducto(nombre, precio, exist, tags)) {
				actualizarTabla(tabla);
				txtId.clear(); txtNombre.clear(); txtPrecio.clear(); txtExistentes.clear();
				comboEtiquetas.getSelectionModel().clearSelection();
				chkDisponible.setSelected(false);
			}
		});

		btnModificar.setOnAction(e -> {
			String idText = txtId.getText().trim();
			if (idText.isEmpty()) return;

			int id;
			try { id = Integer.parseInt(idText); } catch (Exception ex) { return; }

			Venta p = ventaController.buscarProdcuto(id);
			if (p == null) return;

			if (!txtNombre.getText().trim().isEmpty())
				ventaController.actualizarProducto(id, txtNombre.getText().trim());

			if (!txtPrecio.getText().trim().isEmpty())
				ventaController.actualizarProducto(id, Float.parseFloat(txtPrecio.getText().trim()));

			if (!txtExistentes.getText().trim().isEmpty())
				ventaController.actualizarProducto(id, Integer.parseInt(txtExistentes.getText().trim()));

			List<String> tags = comboEtiquetas.getValue() == null ? List.of() : List.of(comboEtiquetas.getValue());
			if (!tags.isEmpty())
				ventaController.actualizarProdcuto(id, tags);

			ventaController.actualizarProducto(id, chkDisponible.isSelected());

			actualizarTabla(tabla);
		});

		btnEliminar.setOnAction(e -> {
			String idText = txtId.getText().trim();
			if (idText.isEmpty()) return;

			int id;
			try { id = Integer.parseInt(idText); } catch (Exception ex) { return; }

			if (ventaController.eliminarProducto(id))
				actualizarTabla(tabla);
		});

		return root;
	}



}
