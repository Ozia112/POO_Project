package view;

import controller.RentaController;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import model.Ubicacion;

import java.util.List;

public class ConfigRentaGUI {

    private final RentaController rentaController;

    public ConfigRentaGUI(RentaController rentaController) {
        this.rentaController = rentaController;
    }

    public VBox getVistaIntegrada() {
        VBox root = new VBox(12);
        root.setPadding(new Insets(14));

        Label titulo = new Label("Configuración - Torres y Lockers");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Tabla de ubicaciones
        TableView<Ubicacion> tabla = new TableView<>();
        tabla.setPrefWidth(800);
        tabla.setPrefHeight(320);

        TableColumn<Ubicacion, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(String.valueOf(c.getValue().getUbicacionId())));
        colId.setPrefWidth(80);

        TableColumn<Ubicacion, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getNombreLocker()));
        colNombre.setPrefWidth(300);

        TableColumn<Ubicacion, String> colUbicacion = new TableColumn<>("Ubicación");
        colUbicacion.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getNombreTorre()));
        colUbicacion.setPrefWidth(220);

        TableColumn<Ubicacion, String> colLocalizacion = new TableColumn<>("Localización");
        colLocalizacion.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getLocalizacion()));
        colLocalizacion.setPrefWidth(160);

        tabla.getColumns().addAll(colId, colNombre, colUbicacion, colLocalizacion);

        // Formulario de edición/creación
        GridPane formulario = new GridPane();
        formulario.setHgap(10);
        formulario.setVgap(10);

        Label lblId = new Label("ID:");
        TextField txtId = new TextField();
        txtId.setDisable(true);
        txtId.setPrefWidth(120);

        Label lblNombre = new Label("Nombre (Locker):");
        TextField txtNombre = new TextField();
        txtNombre.setPrefWidth(300);

        Label lblTorre = new Label("Torre:");
        TextField txtTorre = new TextField();
        txtTorre.setPrefWidth(200);

        Label lblLocal = new Label("Localización:");
        ToggleGroup tg = new ToggleGroup();
        RadioButton rbArriba = new RadioButton("Planta Alta");
        RadioButton rbAbajo = new RadioButton("Planta Baja");
        rbArriba.setToggleGroup(tg);
        rbAbajo.setToggleGroup(tg);
        rbAbajo.setSelected(true);
        HBox hbLocal = new HBox(8, rbArriba, rbAbajo);

        Label lblDisponible = new Label("Disponible:");
        CheckBox chkDisponible = new CheckBox();

        formulario.add(lblId, 0, 0);
        formulario.add(txtId, 1, 0);
        formulario.add(lblNombre, 0, 1);
        formulario.add(txtNombre, 1, 1, 3, 1);
        formulario.add(lblTorre, 0, 2);
        formulario.add(txtTorre, 1, 2);
        formulario.add(lblLocal, 2, 2);
        formulario.add(hbLocal, 3, 2);
        formulario.add(lblDisponible, 0, 3);
        formulario.add(chkDisponible, 1, 3);

        // Botones CRUD
        HBox botones = new HBox(10);
        Button btnNuevo = new Button("Nuevo");
        Button btnAgregar = new Button("Agregar");
        Button btnModificar = new Button("Modificar");
        Button btnEliminar = new Button("Eliminar");
        Button btnRefrescar = new Button("Refrescar");
        botones.getChildren().addAll(btnNuevo, btnAgregar, btnModificar, btnEliminar, btnRefrescar);

        root.getChildren().addAll(titulo, tabla, formulario, botones);

        // Cargar datos iniciales
        actualizarTabla(tabla);

        // Nuevo
        btnNuevo.setOnAction(e -> {
            limpiarFormulario(txtId, txtNombre, txtTorre, chkDisponible, rbArriba, rbAbajo);
            tabla.getSelectionModel().clearSelection();
            txtNombre.requestFocus();
        });

        // Selección en tabla
        tabla.getSelectionModel().selectedItemProperty().addListener((obs, oldV, nueva) -> {
            if (nueva == null) return;
            txtId.setText(String.valueOf(nueva.getUbicacionId()));
            txtNombre.setText(nueva.getNombreLocker());
            txtTorre.setText(nueva.getNombreTorre());
            if ("Planta Alta".equalsIgnoreCase(nueva.getLocalizacion())) rbArriba.setSelected(true);
            else rbAbajo.setSelected(true);
            chkDisponible.setSelected(nueva.isDisponible());
        });

        // Agregar
        btnAgregar.setOnAction(e -> {
            if (!txtId.getText().trim().isEmpty()) { mostrarAlerta("Error", "Para agregar una nueva ubicación presione 'Nuevo'"); return; }
            String nombre = txtNombre.getText().trim();
            String torre = txtTorre.getText().trim();
            String loc = rbArriba.isSelected() ? "Planta Alta" : "Planta Baja";
            if (nombre.isEmpty() || torre.isEmpty()) { mostrarAlerta("Error", "Nombre y torre son obligatorios"); return; }
            try {
                Ubicacion u = new Ubicacion(nombre, torre, loc);
                u.setDisponible(chkDisponible.isSelected());
                rentaController.getUbicacionDAO().guardar(u);
                actualizarTabla(tabla);
                limpiarFormulario(txtId, txtNombre, txtTorre, chkDisponible, rbArriba, rbAbajo);
                mostrarAlerta("Éxito", "Ubicación agregada correctamente");
            } catch (Exception ex) {
                mostrarAlerta("Error", "No se pudo agregar la ubicación: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        // Modificar
        btnModificar.setOnAction(e -> {
            String idText = txtId.getText().trim();
            if (idText.isEmpty()) { mostrarAlerta("Error", "Seleccione una ubicación de la tabla"); return; }
            try {
                Long id = Long.parseLong(idText);
                var dao = rentaController.getUbicacionDAO();
                Ubicacion u = dao.obtener(id);
                if (u == null) { mostrarAlerta("Error", "Ubicación no encontrada"); return; }
                String nuevoNombre = txtNombre.getText().trim();
                String nuevaTorre = txtTorre.getText().trim();
                String loc = rbArriba.isSelected() ? "Planta Alta" : "Planta Baja";
                if (!nuevoNombre.isEmpty()) u.setNombreLocker(nuevoNombre);
                if (!nuevaTorre.isEmpty()) u.setNombreTorre(nuevaTorre);
                u.setLocalizacion(loc);
                u.setDisponible(chkDisponible.isSelected());
                dao.actualizar(u);
                actualizarTabla(tabla);
                mostrarAlerta("Éxito", "Ubicación modificada correctamente");
            } catch (NumberFormatException ex) {
                mostrarAlerta("Error", "ID inválido");
            } catch (Exception ex) {
                mostrarAlerta("Error", "No se pudo modificar: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        // Eliminar
        btnEliminar.setOnAction(e -> {
            String idText = txtId.getText().trim();
            if (idText.isEmpty()) { mostrarAlerta("Error", "Seleccione una ubicación de la tabla"); return; }
            try {
                Long id = Long.parseLong(idText);
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Confirmar eliminación"); confirm.setHeaderText(null);
                confirm.setContentText("¿Eliminar esta ubicación?");
                if (confirm.showAndWait().filter(r -> r == ButtonType.OK).isPresent()) {
                    boolean ok = rentaController.getUbicacionDAO().eliminar(id);
                    if (ok) {
                        actualizarTabla(tabla);
                        limpiarFormulario(txtId, txtNombre, txtTorre, chkDisponible, rbArriba, rbAbajo);
                        tabla.getSelectionModel().clearSelection();
                        mostrarAlerta("Éxito", "Ubicación eliminada correctamente");
                    }
                }
            } catch (NumberFormatException ex) {
                mostrarAlerta("Error", "ID inválido");
            } catch (Exception ex) {
                mostrarAlerta("Error", "No se pudo eliminar: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        btnRefrescar.setOnAction(e -> actualizarTabla(tabla));

        return root;
    }

    private void actualizarTabla(TableView<Ubicacion> tabla) {
        tabla.getItems().clear();
        List<Ubicacion> lista = rentaController.getUbicacionDAO().obtenerTodas();
        tabla.getItems().addAll(lista);
    }

    private void limpiarFormulario(TextField txtId, TextField txtNombre, TextField txtTorre, CheckBox chkDisponible, RadioButton rbArriba, RadioButton rbAbajo) {
        txtId.clear();
        txtNombre.clear();
        txtTorre.clear();
        chkDisponible.setSelected(true);
        rbAbajo.setSelected(true);
    }

    private void refreshComboTorres(ComboBox<String> combo) {
        combo.getItems().clear();
        List<Ubicacion> list = rentaController.getUbicacionDAO().obtenerTodas();
        java.util.Set<String> torres = new java.util.LinkedHashSet<>();
        for (Ubicacion u : list) torres.add(u.getNombreTorre());
        combo.getItems().addAll(torres);
        if (!combo.getItems().isEmpty()) combo.getSelectionModel().select(0);
    }

    private void crearTorre(String nombreTorre, int cantidad, String localizacion) {
        try {
            var dao = rentaController.getUbicacionDAO();
            List<Ubicacion> existentes = dao.obtenerPorTorre(nombreTorre);
            int inicio = existentes.size() + 1;
            for (int i = 0; i < cantidad; i++) {
                String nombreLocker = nombreTorre + " - Locker " + (inicio + i);
                Ubicacion u = new Ubicacion(nombreLocker, nombreTorre, localizacion);
                dao.guardar(u);
            }
            mostrarAlerta("Éxito", "Torre creada: " + nombreTorre);
        } catch (Exception ex) {
            mostrarAlerta("Error", "No se pudo crear la torre: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void borrarTorre(String nombreTorre) {
        try {
            var dao = rentaController.getUbicacionDAO();
            List<Ubicacion> lista = dao.obtenerPorTorre(nombreTorre);
            for (Ubicacion u : lista) dao.eliminar(u.getUbicacionId());
            mostrarAlerta("Éxito", "Torre eliminada: " + nombreTorre);
        } catch (Exception ex) {
            mostrarAlerta("Error", "No se pudo borrar la torre: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void modificarCantidadTorre(String nombreTorre, int nuevaCantidad, String localizacion) {
        try {
            var dao = rentaController.getUbicacionDAO();
            List<Ubicacion> existentes = dao.obtenerPorTorre(nombreTorre);
            int actuales = existentes.size();
            if (nuevaCantidad == actuales) { mostrarAlerta("Info", "La torre ya tiene " + actuales + " lockers"); return; }
            if (nuevaCantidad > actuales) {
                int aCrear = nuevaCantidad - actuales;
                int inicio = actuales + 1;
                for (int i = 0; i < aCrear; i++) {
                    String nombreLocker = nombreTorre + " - Locker " + (inicio + i);
                    Ubicacion u = new Ubicacion(nombreLocker, nombreTorre, localizacion);
                    dao.guardar(u);
                }
                mostrarAlerta("Éxito", "Se agregaron " + aCrear + " lockers a " + nombreTorre);
            } else {
                int aEliminar = actuales - nuevaCantidad;
                existentes.sort((a,b) -> a.getNombreLocker().compareTo(b.getNombreLocker()));
                for (int i = 0; i < aEliminar; i++) {
                    Ubicacion u = existentes.get(existentes.size() - 1 - i);
                    dao.eliminar(u.getUbicacionId());
                }
                mostrarAlerta("Éxito", "Se eliminaron " + aEliminar + " lockers de " + nombreTorre);
            }
        } catch (Exception ex) {
            mostrarAlerta("Error", "No se pudo modificar la torre: " + ex.getMessage());
            ex.printStackTrace();
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
