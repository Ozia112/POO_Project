package view;

import controller.VentaController;
import controller.InventarioController;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import model.Venta;
import model.Etiqueta;

import java.util.List;

public class VentaGUI {

	private final VentaController ventaController;
	private final InventarioController inventarioController;

	public VentaGUI(VentaController ventaController) {
		this.ventaController = ventaController;
		this.inventarioController = new InventarioController();
	}

	public VBox getVistaIntegrada() {
		VBox contenedor = new VBox(15);
		contenedor.setPadding(new Insets(20));

		// Tabla de productos
		TableView<Venta> tabla = new TableView<>();
		tabla.setPrefWidth(800);
		tabla.setPrefHeight(400);

		TableColumn<Venta, String> colId = new TableColumn<>("ID");
		colId.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(String.valueOf(c.getValue().getIdProducto())));
		colId.setPrefWidth(60);

		TableColumn<Venta, String> colNombre = new TableColumn<>("Nombre");
		colNombre.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getNombre()));
		colNombre.setPrefWidth(250);

		TableColumn<Venta, String> colPrecio = new TableColumn<>("Precio");
		colPrecio.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty("$" + String.format("%.2f", c.getValue().getPrecio())));
		colPrecio.setPrefWidth(100);

		TableColumn<Venta, String> colExistentes = new TableColumn<>("Existentes");
		colExistentes.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(String.valueOf(c.getValue().getExistentes())));
		colExistentes.setPrefWidth(100);

		TableColumn<Venta, String> colDisponible = new TableColumn<>("Disponible");
		colDisponible.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().isDisponible() ? "Sí" : "No"));
		colDisponible.setPrefWidth(100);

		TableColumn<Venta, String> colEtiqueta = new TableColumn<>("Etiqueta");
		colEtiqueta.setCellValueFactory(c -> {
			Etiqueta etiqueta = c.getValue().getEtiqueta();
			String etiquetaTexto = (etiqueta == null) ? "" : etiqueta.getNombre();
			return new javafx.beans.property.SimpleStringProperty(etiquetaTexto);
		});
		colEtiqueta.setPrefWidth(150);

		tabla.getColumns().addAll(colId, colNombre, colPrecio, colExistentes, colDisponible, colEtiqueta);

		// Formulario de edición
		GridPane formulario = new GridPane();
		formulario.setHgap(10);
		formulario.setVgap(10);

		Label lblId = new Label("ID:");
		TextField txtId = new TextField();
		txtId.setDisable(true);
		txtId.setPrefWidth(100);

		Label lblNombre = new Label("Nombre:");
		TextField txtNombre = new TextField();
		txtNombre.setPrefWidth(200);

		Label lblPrecio = new Label("Precio:");
		TextField txtPrecio = new TextField();
		txtPrecio.setPrefWidth(100);

		Label lblExistentes = new Label("Existentes:");
		TextField txtExistentes = new TextField();
		txtExistentes.setPrefWidth(100);

		Label lblDisponible = new Label("Disponible:");
		CheckBox chkDisponible = new CheckBox();
		
		Label lblEtiqueta = new Label("Etiqueta:");
		ComboBox<Etiqueta> comboEtiqueta = new ComboBox<>();
		comboEtiqueta.setPrefWidth(150);
		
		// Cargar etiquetas
		try {
			var etiquetaDAO = new controller.EtiquetaController().getEtiquetaDAO();
			comboEtiqueta.getItems().addAll(etiquetaDAO.obtenerTodas());
			comboEtiqueta.setCellFactory(param -> new ListCell<Etiqueta>() {
				@Override
				protected void updateItem(Etiqueta item, boolean empty) {
					super.updateItem(item, empty);
					setText(empty || item == null ? null : item.getNombre());
				}
			});
			comboEtiqueta.setButtonCell(new ListCell<Etiqueta>() {
				@Override
				protected void updateItem(Etiqueta item, boolean empty) {
					super.updateItem(item, empty);
					setText(empty || item == null ? null : item.getNombre());
				}
			});
			if (!comboEtiqueta.getItems().isEmpty()) {
				comboEtiqueta.getSelectionModel().select(0);
			}
		} catch (Exception ex) {
			System.err.println("Error cargando etiquetas: " + ex.getMessage());
		}

		formulario.add(lblId, 0, 0);
		formulario.add(txtId, 1, 0);
		formulario.add(lblNombre, 2, 0);
		formulario.add(txtNombre, 3, 0);
		formulario.add(lblPrecio, 0, 1);
		formulario.add(txtPrecio, 1, 1);
		formulario.add(lblExistentes, 2, 1);
		formulario.add(txtExistentes, 3, 1);
		formulario.add(lblDisponible, 0, 2);
		formulario.add(chkDisponible, 1, 2);
		formulario.add(lblEtiqueta, 2, 2);
		formulario.add(comboEtiqueta, 3, 2);

		// Botones
		HBox botones = new HBox(10);
		Button btnNuevo = new Button("Nuevo");
		Button btnAgregar = new Button("Agregar");
		Button btnModificar = new Button("Modificar");
		Button btnEliminar = new Button("Eliminar");
		Button btnRefrescar = new Button("Refrescar");
		botones.getChildren().addAll(btnNuevo, btnAgregar, btnModificar, btnEliminar, btnRefrescar);

		contenedor.getChildren().addAll(new Label("Gestión de Productos"), tabla, formulario, botones);

		// Cargar datos iniciales
		actualizarTabla(tabla);
		
		// Botón Nuevo - Limpiar formulario para agregar nuevo producto
		btnNuevo.setOnAction(e -> {
			limpiarFormulario(txtId, txtNombre, txtPrecio, txtExistentes, chkDisponible);
			tabla.getSelectionModel().clearSelection();
			if (!comboEtiqueta.getItems().isEmpty()) {
				comboEtiqueta.getSelectionModel().select(0);
			}
			txtNombre.requestFocus();
		});

		// Listener para selección de tabla
		tabla.getSelectionModel().selectedItemProperty().addListener((obs, oldV, nueva) -> {
			if (nueva == null) return;
			txtId.setText(String.valueOf(nueva.getIdProducto()));
			txtNombre.setText(nueva.getNombre());
			txtPrecio.setText(String.valueOf(nueva.getPrecio()));
			txtExistentes.setText(String.valueOf(nueva.getExistentes()));
			chkDisponible.setSelected(nueva.isDisponible());
			
			// Seleccionar la etiqueta correspondiente
			if (nueva.getEtiqueta() != null) {
				for (Etiqueta etiq : comboEtiqueta.getItems()) {
					if (etiq.getEtiquetaId().equals(nueva.getEtiqueta().getEtiquetaId())) {
						comboEtiqueta.getSelectionModel().select(etiq);
						break;
					}
				}
			}
		});

		// Botón Agregar
		btnAgregar.setOnAction(e -> {
			// Verificar que no haya un ID (para asegurar que es un producto nuevo)
			if (!txtId.getText().trim().isEmpty()) {
				mostrarAlerta("Error", "Para agregar un nuevo producto, presione el botón 'Nuevo' primero");
				return;
			}
			
			String nombre = txtNombre.getText().trim();
			if (nombre.isEmpty()) {
				mostrarAlerta("Error", "El nombre no puede estar vacío");
				return;
			}

			try {
				float precio = Float.parseFloat(txtPrecio.getText().trim());
				int exist = Integer.parseInt(txtExistentes.getText().trim());
				
				// Obtener etiqueta seleccionada
				Etiqueta etiquetaSeleccionada = comboEtiqueta.getValue();
				if (etiquetaSeleccionada == null) {
					mostrarAlerta("Error", "Debe seleccionar una etiqueta");
					return;
				}
				
				boolean ok = inventarioController.agregarProducto(nombre, precio, exist, etiquetaSeleccionada.getEtiquetaId());
				if (ok) {
					actualizarTabla(tabla);
					limpiarFormulario(txtId, txtNombre, txtPrecio, txtExistentes, chkDisponible);
					tabla.getSelectionModel().clearSelection();
					mostrarAlerta("Éxito", "Producto agregado correctamente");
				} else {
					mostrarAlerta("Error", "No se pudo agregar el producto");
				}
			} catch (NumberFormatException ex) {
				mostrarAlerta("Error", "Precio y existentes deben ser números válidos");
			}
		});

		// Botón Modificar
		btnModificar.setOnAction(e -> {
			String idText = txtId.getText().trim();
			if (idText.isEmpty()) {
				mostrarAlerta("Error", "Seleccione un producto de la tabla");
				return;
			}

			try {
				Long id = Long.parseLong(idText);
				Venta p = inventarioController.getVentaDAO().obtener(id);
				if (p == null) {
					mostrarAlerta("Error", "Producto no encontrado");
					return;
				}

				String nuevoNombre = txtNombre.getText().trim();
				if (!nuevoNombre.isEmpty()) p.setNombre(nuevoNombre);

				String precioText = txtPrecio.getText().trim();
				if (!precioText.isEmpty()) {
					p.setPrecio(Float.parseFloat(precioText));
				}

				String existText = txtExistentes.getText().trim();
				if (!existText.isEmpty()) {
					p.setExistentes(Integer.parseInt(existText));
				}

				p.setDisponible(chkDisponible.isSelected());
				
				// Actualizar etiqueta si se seleccionó una
				Etiqueta etiquetaSeleccionada = comboEtiqueta.getValue();
				if (etiquetaSeleccionada != null) {
					p.setEtiqueta(etiquetaSeleccionada);
				}
				
				inventarioController.getVentaDAO().actualizar(p);

				actualizarTabla(tabla);
				mostrarAlerta("Éxito", "Producto modificado correctamente");
			} catch (NumberFormatException ex) {
				mostrarAlerta("Error", "ID, precio y existentes deben ser números válidos");
			}
		});

		// Botón Eliminar
		btnEliminar.setOnAction(e -> {
			String idText = txtId.getText().trim();
			if (idText.isEmpty()) {
				mostrarAlerta("Error", "Seleccione un producto de la tabla");
				return;
			}

			try {
				Long id = Long.parseLong(idText);
				
				// Confirmar eliminación
				Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
				confirmacion.setTitle("Confirmar eliminación");
				confirmacion.setHeaderText(null);
				confirmacion.setContentText("¿Está seguro de eliminar este producto?");
				
				if (confirmacion.showAndWait().get() == ButtonType.OK) {
					boolean ok = inventarioController.eliminarProducto(id);
					if (ok) {
						actualizarTabla(tabla);
						limpiarFormulario(txtId, txtNombre, txtPrecio, txtExistentes, chkDisponible);
						tabla.getSelectionModel().clearSelection();
						mostrarAlerta("Éxito", "Producto eliminado correctamente");
					} else {
						mostrarAlerta("Error", "No se pudo eliminar el producto");
					}
				}
			} catch (NumberFormatException ex) {
				mostrarAlerta("Error", "ID inválido");
			}
		});

		// Botón Refrescar
		btnRefrescar.setOnAction(e -> actualizarTabla(tabla));

		return contenedor;
	}

	private void actualizarTabla(TableView<Venta> tabla) {
		tabla.getItems().clear();
		List<Venta> productos = inventarioController.getVentaDAO().obtenerTodas();
		tabla.getItems().addAll(productos);
	}

	private void limpiarFormulario(TextField txtId, TextField txtNombre, TextField txtPrecio, TextField txtExistentes, CheckBox chkDisponible) {
		txtId.clear();
		txtNombre.clear();
		txtPrecio.clear();
		txtExistentes.clear();
		chkDisponible.setSelected(false);
	}

	private void mostrarAlerta(String titulo, String mensaje) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(titulo);
		alert.setHeaderText(null);
		alert.setContentText(mensaje);
		alert.showAndWait();
	}
}
