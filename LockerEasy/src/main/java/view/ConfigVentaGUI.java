package view;

import controller.VentaController;
import controller.InventarioController;
import controller.EtiquetaController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.stage.Popup;
import model.ProductoCatalogo;
import model.Etiqueta;

import java.util.List;

public class ConfigVentaGUI {

    private final VentaController ventaController;
    private final InventarioController inventarioController;
    
    private VBox listaProductos;
    private VBox formularioContainer;
    private javafx.animation.PauseTransition mensajeTimer;
    
    // Callback para notificar cambios
    private Runnable onDataChanged;

    public ConfigVentaGUI(VentaController ventaController, InventarioController inventarioController) {
        this.ventaController = ventaController;
        this.inventarioController = inventarioController;
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
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #f5f5f5;");

        Label titulo = new Label("Gestión de Productos");
        titulo.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #333;");

        // Panel informativo
        VBox infoBox = new VBox(5);
        infoBox.setStyle("-fx-background-color: #E3F2FD; -fx-padding: 12; -fx-background-radius: 10; -fx-border-color: #2196F3; -fx-border-width: 2; -fx-border-radius: 10;");
        infoBox.setMaxWidth(950);
        
        Label lblInfo = new Label("💡 Edición Inline");
        lblInfo.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #1976D2;");
        
        HBox hboxInfo = new HBox(30);
        hboxInfo.setAlignment(Pos.CENTER_LEFT);
        Label lbl1 = new Label("📝 Doble clic en nombre para editar");
        lbl1.setStyle("-fx-font-size: 11px;");
        Label lbl2 = new Label("🔢 Doble clic en cantidad/precio para cambiar");
        lbl2.setStyle("-fx-font-size: 11px;");
        Label lbl3 = new Label("🏷️ Clic en etiqueta para cambiar | Toggle disponible");
        lbl3.setStyle("-fx-font-size: 11px;");
        hboxInfo.getChildren().addAll(lbl1, lbl2, lbl3);
        infoBox.getChildren().addAll(lblInfo, hboxInfo);

        // Contenedor para formulario de agregar y mensajes
        formularioContainer = new VBox(10);
        formularioContainer.setAlignment(Pos.TOP_CENTER);
        formularioContainer.setMaxWidth(950);

        // Header de la lista
        HBox header = crearHeaderLista();

        // Lista de productos
        listaProductos = new VBox(6);
        listaProductos.setPadding(new Insets(10));
        listaProductos.setStyle("-fx-background-color: white;");
        
        ScrollPane scrollPane = new ScrollPane(listaProductos);
        scrollPane.setFitToWidth(true);
        scrollPane.setMaxWidth(950);
        scrollPane.setPrefHeight(400);
        scrollPane.setStyle("-fx-background: white; -fx-border-color: #ddd; -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10;");

        // Botón agregar
        Button btnAgregar = new Button("➕ Agregar Nuevo Producto");
        btnAgregar.setStyle(
            "-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 15px; " +
            "-fx-font-weight: bold; -fx-padding: 14 28; -fx-background-radius: 10; -fx-cursor: hand; " +
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

        root.getChildren().addAll(titulo, infoBox, formularioContainer, header, scrollPane, btnAgregar);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        return root;
    }
    
    private HBox crearHeaderLista() {
        HBox header = new HBox(10);
        header.setPadding(new Insets(10, 20, 10, 20));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: #E0E0E0; -fx-background-radius: 8;");
        header.setMaxWidth(950);
        
        Label lblId = new Label("ID");
        lblId.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
        lblId.setPrefWidth(50);
        
        Label lblNombre = new Label("Nombre");
        lblNombre.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
        lblNombre.setPrefWidth(200);
        
        Label lblPrecio = new Label("Precio");
        lblPrecio.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
        lblPrecio.setPrefWidth(80);
        
        Label lblExist = new Label("Stock");
        lblExist.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
        lblExist.setPrefWidth(70);
        
        Label lblEtiq = new Label("Etiqueta");
        lblEtiq.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
        lblEtiq.setPrefWidth(120);
        
        Label lblDisp = new Label("Disponible");
        lblDisp.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
        lblDisp.setPrefWidth(80);
        
        Label lblAcc = new Label("Acciones");
        lblAcc.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
        
        header.getChildren().addAll(lblId, lblNombre, lblPrecio, lblExist, lblEtiq, lblDisp, lblAcc);
        
        return header;
    }

    private void actualizarLista() {
        listaProductos.getChildren().clear();
        List<ProductoCatalogo> productos = inventarioController.getProductoCatalogoDAO().obtenerTodos();

        for (ProductoCatalogo producto : productos) {
            HBox itemBox = crearItemProductoInline(producto);
            listaProductos.getChildren().add(itemBox);
        }
        
        if (productos.isEmpty()) {
            Label lblVacio = new Label("No hay productos registrados. ¡Agrega el primero!");
            lblVacio.setStyle("-fx-text-fill: #999; -fx-font-size: 14px; -fx-padding: 20;");
            listaProductos.getChildren().add(lblVacio);
        }
    }

    public void actualizarTablas() {
        actualizarLista();
    }
    
    public boolean tieneProductos() {
        return !inventarioController.obtenerTodosLosProductos().isEmpty();
    }

    private HBox crearItemProductoInline(ProductoCatalogo producto) {
        HBox itemBox = new HBox(10);
        itemBox.setPadding(new Insets(10, 20, 10, 20));
        itemBox.setAlignment(Pos.CENTER_LEFT);
        itemBox.setStyle(
            "-fx-background-color: white; -fx-border-color: #e0e0e0; " +
            "-fx-border-width: 1; -fx-border-radius: 8; -fx-background-radius: 8;"
        );
        itemBox.setMaxWidth(950);
        itemBox.setPrefWidth(950);

        // Estado original
        final String nombreOriginal = producto.getNombre();
        final float precioOriginal = producto.getPrecio();
        final int existOriginal = producto.getExistentes();
        final Long etiquetaIdOriginal = producto.getEtiqueta() != null ? producto.getEtiqueta().getEtiquetaId() : null;
        final boolean dispOriginal = producto.isDisponible();

        // ID
        Label lblId = new Label("#" + producto.getId());
        lblId.setStyle("-fx-font-weight: bold; -fx-text-fill: #2196F3; -fx-font-size: 12px;");
        lblId.setPrefWidth(50);

        // Nombre (editable con doble clic)
        Label lblNombre = new Label(producto.getNombre());
        lblNombre.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        lblNombre.setPrefWidth(200);
        
        TextField txtNombre = new TextField(producto.getNombre());
        txtNombre.setPrefWidth(200);
        txtNombre.setVisible(false);
        txtNombre.setManaged(false);

        // Precio (editable con doble clic)
        Label lblPrecio = new Label(String.format("$%.2f", producto.getPrecio()));
        lblPrecio.setStyle("-fx-font-size: 13px; -fx-text-fill: #4CAF50; -fx-font-weight: bold;");
        lblPrecio.setPrefWidth(80);
        
        // Stock (editable con doble clic)
        Label lblExist = new Label(String.valueOf(producto.getExistentes()));
        lblExist.setStyle("-fx-font-size: 13px;");
        lblExist.setPrefWidth(70);
        
        // Etiqueta (ComboBox)
        ComboBox<Etiqueta> comboEtiqueta = new ComboBox<>();
        comboEtiqueta.setPrefWidth(120);
        cargarEtiquetas(comboEtiqueta);
        if (producto.getEtiqueta() != null) {
            for (Etiqueta et : comboEtiqueta.getItems()) {
                if (et.getEtiquetaId().equals(producto.getEtiqueta().getEtiquetaId())) {
                    comboEtiqueta.getSelectionModel().select(et);
                    break;
                }
            }
        }
        
        // Toggle disponible
        ToggleButton toggleDisp = new ToggleButton(producto.isDisponible() ? "✓ Sí" : "✗ No");
        toggleDisp.setSelected(producto.isDisponible());
        actualizarEstiloToggleDisp(toggleDisp);
        toggleDisp.setPrefWidth(70);
        
        // Botón guardar (oculto inicialmente)
        Button btnGuardar = new Button("💾");
        btnGuardar.setStyle(
            "-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; " +
            "-fx-padding: 6 12; -fx-background-radius: 6; -fx-cursor: hand;"
        );
        btnGuardar.setVisible(false);
        btnGuardar.setManaged(false);
        
        // Botón eliminar
        Button btnEliminar = new Button("🗑️");
        btnEliminar.setStyle(
            "-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; " +
            "-fx-padding: 6 12; -fx-background-radius: 6; -fx-cursor: hand;"
        );
        
        btnEliminar.setOnMouseEntered(e -> btnEliminar.setStyle(
            "-fx-background-color: #d32f2f; -fx-text-fill: white; -fx-font-size: 14px; " +
            "-fx-padding: 6 12; -fx-background-radius: 6; -fx-cursor: hand;"
        ));
        
        btnEliminar.setOnMouseExited(e -> btnEliminar.setStyle(
            "-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; " +
            "-fx-padding: 6 12; -fx-background-radius: 6; -fx-cursor: hand;"
        ));

        // Variables para tracking de valores actuales
        final float[] precioActual = {precioOriginal};
        final int[] existActual = {existOriginal};
        
        // Función para verificar cambios
        Runnable verificarCambios = () -> {
            String nombreActual = txtNombre.isVisible() ? txtNombre.getText().trim() : lblNombre.getText();
            boolean nombreCambiado = !nombreActual.equals(nombreOriginal);
            boolean precioCambiado = precioActual[0] != precioOriginal;
            boolean existCambiado = existActual[0] != existOriginal;
            
            Etiqueta etiquetaActual = comboEtiqueta.getValue();
            Long etiquetaIdActual = etiquetaActual != null ? etiquetaActual.getEtiquetaId() : null;
            boolean etiquetaCambiada = (etiquetaIdOriginal == null && etiquetaIdActual != null) ||
                (etiquetaIdOriginal != null && !etiquetaIdOriginal.equals(etiquetaIdActual));
            
            boolean dispCambiado = toggleDisp.isSelected() != dispOriginal;
            
            boolean hayCambios = nombreCambiado || precioCambiado || existCambiado || etiquetaCambiada || dispCambiado;
            btnGuardar.setVisible(hayCambios);
            btnGuardar.setManaged(hayCambios);
        };

        // Doble clic en nombre
        lblNombre.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                lblNombre.setVisible(false);
                lblNombre.setManaged(false);
                txtNombre.setVisible(true);
                txtNombre.setManaged(true);
                txtNombre.requestFocus();
                txtNombre.selectAll();
            }
        });
        
        txtNombre.textProperty().addListener((obs, oldVal, newVal) -> verificarCambios.run());
        
        txtNombre.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                String nuevoNombre = txtNombre.getText().trim();
                if (!nuevoNombre.isEmpty()) {
                    lblNombre.setText(nuevoNombre);
                } else {
                    txtNombre.setText(nombreOriginal);
                }
                txtNombre.setVisible(false);
                txtNombre.setManaged(false);
                lblNombre.setVisible(true);
                lblNombre.setManaged(true);
                verificarCambios.run();
            }
        });
        
        txtNombre.setOnAction(e -> itemBox.requestFocus());

        // Doble clic en precio - mostrar popup
        lblPrecio.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                mostrarPopupEdicion("Precio", String.valueOf(precioActual[0]), lblPrecio, nuevoValor -> {
                    try {
                        float nuevo = Float.parseFloat(nuevoValor);
                        if (nuevo > 0) {
                            precioActual[0] = nuevo;
                            lblPrecio.setText(String.format("$%.2f", nuevo));
                            verificarCambios.run();
                        }
                    } catch (NumberFormatException ex) {
                        // Ignorar
                    }
                });
            }
        });
        
        // Doble clic en stock - mostrar popup
        lblExist.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                mostrarPopupEdicion("Stock", String.valueOf(existActual[0]), lblExist, nuevoValor -> {
                    try {
                        int nuevo = Integer.parseInt(nuevoValor);
                        if (nuevo >= 0) {
                            existActual[0] = nuevo;
                            lblExist.setText(String.valueOf(nuevo));
                            verificarCambios.run();
                        }
                    } catch (NumberFormatException ex) {
                        // Ignorar
                    }
                });
            }
        });
        
        // Cambio en etiqueta
        comboEtiqueta.setOnAction(e -> verificarCambios.run());
        
        // Toggle disponible
        toggleDisp.setOnAction(e -> {
            actualizarEstiloToggleDisp(toggleDisp);
            verificarCambios.run();
        });
        
        // Guardar cambios
        btnGuardar.setOnAction(e -> {
            String nuevoNombre = txtNombre.isVisible() ? txtNombre.getText().trim() : lblNombre.getText();
            if (nuevoNombre.isEmpty()) {
                mostrarMensaje("⚠️ El nombre no puede estar vacío", "#FFF3CD", "#856404");
                return;
            }
            
            producto.setNombre(nuevoNombre);
            producto.setPrecio(precioActual[0]);
            producto.setExistentes(existActual[0]);
            producto.setEtiqueta(comboEtiqueta.getValue());
            producto.setDisponible(toggleDisp.isSelected());
            
            inventarioController.getProductoCatalogoDAO().actualizar(producto);
            mostrarMensaje("✓ Producto actualizado", "#D4EDDA", "#155724");
            actualizarLista();
            notificarCambio();
        });
        
        // Eliminar
        btnEliminar.setOnAction(e -> mostrarConfirmacionEliminar(producto));

        // Hover effect
        itemBox.setOnMouseEntered(e -> itemBox.setStyle(
            "-fx-background-color: #f8f9fa; -fx-border-color: #2196F3; -fx-border-width: 2; " +
            "-fx-border-radius: 8; -fx-background-radius: 8; -fx-cursor: hand;"
        ));
        
        itemBox.setOnMouseExited(e -> itemBox.setStyle(
            "-fx-background-color: white; -fx-border-color: #e0e0e0; " +
            "-fx-border-width: 1; -fx-border-radius: 8; -fx-background-radius: 8;"
        ));

        itemBox.getChildren().addAll(lblId, lblNombre, txtNombre, lblPrecio, lblExist, comboEtiqueta, toggleDisp, btnGuardar, btnEliminar);

        return itemBox;
    }
    
    private void mostrarPopupEdicion(String campo, String valorActual, Label lblTarget, java.util.function.Consumer<String> onAceptar) {
        Popup popup = new Popup();
        
        VBox contenido = new VBox(8);
        contenido.setPadding(new Insets(12));
        contenido.setStyle(
            "-fx-background-color: white; -fx-border-color: #2196F3; " +
            "-fx-border-width: 2; -fx-border-radius: 8; -fx-background-radius: 8; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 5);"
        );
        
        Label lblTitulo = new Label("Editar " + campo);
        lblTitulo.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        
        TextField txtValor = new TextField(valorActual);
        txtValor.setPrefWidth(120);
        
        HBox botones = new HBox(8);
        botones.setAlignment(Pos.CENTER_RIGHT);
        
        Button btnAceptar = new Button("✓");
        btnAceptar.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 4 10; -fx-background-radius: 4;");
        
        Button btnCancelar = new Button("✗");
        btnCancelar.setStyle("-fx-background-color: #9e9e9e; -fx-text-fill: white; -fx-padding: 4 10; -fx-background-radius: 4;");
        
        btnAceptar.setOnAction(e -> {
            onAceptar.accept(txtValor.getText().trim());
            popup.hide();
        });
        
        btnCancelar.setOnAction(e -> popup.hide());
        
        txtValor.setOnAction(e -> {
            onAceptar.accept(txtValor.getText().trim());
            popup.hide();
        });
        
        botones.getChildren().addAll(btnAceptar, btnCancelar);
        contenido.getChildren().addAll(lblTitulo, txtValor, botones);
        
        popup.getContent().add(contenido);
        popup.setAutoHide(true);
        
        // Mostrar debajo del label
        javafx.geometry.Bounds bounds = lblTarget.localToScreen(lblTarget.getBoundsInLocal());
        popup.show(lblTarget, bounds.getMinX(), bounds.getMaxY() + 5);
        
        txtValor.requestFocus();
        txtValor.selectAll();
    }
    
    private void actualizarEstiloToggleDisp(ToggleButton toggle) {
        if (toggle.isSelected()) {
            toggle.setText("✓ Sí");
            toggle.setStyle(
                "-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; " +
                "-fx-padding: 4 8; -fx-background-radius: 12; -fx-font-size: 11px; -fx-cursor: hand;"
            );
        } else {
            toggle.setText("✗ No");
            toggle.setStyle(
                "-fx-background-color: #9e9e9e; -fx-text-fill: white; " +
                "-fx-padding: 4 8; -fx-background-radius: 12; -fx-font-size: 11px; -fx-cursor: hand;"
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
        formulario.setMaxWidth(950);

        Label titulo = new Label("➕ Nuevo Producto");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2E7D32;");

        // Fila 1: Nombre y Precio
        HBox fila1 = new HBox(20);
        fila1.setAlignment(Pos.CENTER_LEFT);
        
        Label lblNombre = new Label("Nombre:");
        lblNombre.setStyle("-fx-font-weight: bold;");
        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Ej: Coca Cola 600ml");
        txtNombre.setPrefWidth(250);
        
        Label lblPrecio = new Label("Precio:");
        lblPrecio.setStyle("-fx-font-weight: bold;");
        TextField txtPrecio = new TextField();
        txtPrecio.setPromptText("Ej: 25.00");
        txtPrecio.setPrefWidth(100);
        
        fila1.getChildren().addAll(lblNombre, txtNombre, lblPrecio, txtPrecio);
        
        // Fila 2: Stock y Etiqueta
        HBox fila2 = new HBox(20);
        fila2.setAlignment(Pos.CENTER_LEFT);
        
        Label lblStock = new Label("Stock inicial:");
        lblStock.setStyle("-fx-font-weight: bold;");
        Spinner<Integer> spinnerStock = new Spinner<>(0, 9999, 10);
        spinnerStock.setEditable(true);
        spinnerStock.setPrefWidth(100);
        
        Label lblEtiqueta = new Label("Etiqueta:");
        lblEtiqueta.setStyle("-fx-font-weight: bold;");
        ComboBox<Etiqueta> comboEtiqueta = new ComboBox<>();
        comboEtiqueta.setPrefWidth(150);
        cargarEtiquetas(comboEtiqueta);
        
        fila2.getChildren().addAll(lblStock, spinnerStock, lblEtiqueta, comboEtiqueta);

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
            
            Etiqueta etiquetaSel = comboEtiqueta.getValue();
            if (etiquetaSel == null) {
                mostrarMensaje("⚠️ Debe seleccionar una etiqueta", "#FFF3CD", "#856404");
                return;
            }
            
            try {
                float precio = Float.parseFloat(txtPrecio.getText().trim());
                int stock = spinnerStock.getValue();
                
                boolean ok = inventarioController.agregarProducto(nombre, precio, stock, etiquetaSel.getEtiquetaId());
                if (ok) {
                    actualizarLista();
                    mostrarMensaje("✓ Producto agregado: " + nombre, "#D4EDDA", "#155724");
                    formularioContainer.getChildren().clear();
                    notificarCambio();
                } else {
                    mostrarMensaje("✗ No se pudo agregar el producto", "#F8D7DA", "#721C24");
                }
            } catch (NumberFormatException ex) {
                mostrarMensaje("⚠️ El precio debe ser un número válido", "#FFF3CD", "#856404");
            }
        });

        btnCancelar.setOnAction(e -> formularioContainer.getChildren().clear());

        botones.getChildren().addAll(btnGuardar, btnCancelar);
        formulario.getChildren().addAll(titulo, fila1, fila2, botones);
        formularioContainer.getChildren().add(formulario);
        
        txtNombre.requestFocus();
    }
    
    private void mostrarConfirmacionEliminar(ProductoCatalogo producto) {
        cancelarTimerMensaje();
        formularioContainer.getChildren().clear();
        
        VBox formulario = new VBox(15);
        formulario.setPadding(new Insets(20));
        formulario.setStyle(
            "-fx-background-color: #FFEBEE; -fx-border-color: #f44336; " +
            "-fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10;"
        );
        formulario.setMaxWidth(950);
        formulario.setAlignment(Pos.CENTER);

        Label titulo = new Label("⚠️ ¿Eliminar producto?");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #c62828;");

        Label mensaje = new Label("Producto: " + producto.getNombre() + " (ID: " + producto.getId() + ")");
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
            if (inventarioController.eliminarProducto(producto.getId())) {
                actualizarLista();
                mostrarMensaje("✓ Producto eliminado", "#D4EDDA", "#155724");
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

    private void cargarEtiquetas(ComboBox<Etiqueta> comboEtiqueta) {
        try {
            comboEtiqueta.getItems().clear();
            var etiquetaDAO = new EtiquetaController().getEtiquetaDAO();
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
    }

    private void mostrarMensaje(String texto, String bgColor, String textColor) {
        if (mensajeTimer != null) {
            mensajeTimer.stop();
        }
        
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
        mensaje.setMaxWidth(950);
        
        if (hayFormulario) {
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

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
