package view;

import controller.RentaController;
import controller.Config;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import model.Ubicacion;

import java.util.*;
import java.util.stream.Collectors;

public class ConfigRentaGUI {

    private final RentaController rentaController;
    private VBox torresContainer;
    private VBox formularioContainer;
    
    // Callback para notificar a ConfigView que actualice indicadores
    private Runnable onDataChanged;

    public ConfigRentaGUI(RentaController rentaController) {
        this.rentaController = rentaController;
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
        // Contenedor principal interno
        VBox contenidoInterno = new VBox(15);
        contenidoInterno.setPadding(new Insets(20));

        Label titulo = new Label("Gestión de Torres y Lockers");
        titulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // ==================== SECCIÓN DE CONFIGURACIÓN ====================
        VBox configSection = crearSeccionConfiguracion();
        
        // Separador
        Separator separator = new Separator();
        separator.setPadding(new Insets(10, 0, 10, 0));

        // Botón agregar torre
        Button btnAgregarTorre = new Button("➕ Agregar Nueva Torre");
        btnAgregarTorre.setStyle(
            "-fx-background-color: #4CAF50; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 10 20; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand;"
        );
        btnAgregarTorre.setOnAction(e -> mostrarFormularioAgregarTorre());

        // Contenedor para formularios dinámicos
        formularioContainer = new VBox(10);
        formularioContainer.setAlignment(Pos.TOP_CENTER);

        // Lista de torres
        torresContainer = new VBox(10);
        torresContainer.setPadding(new Insets(10));

        contenidoInterno.getChildren().addAll(titulo, configSection, separator, btnAgregarTorre, formularioContainer, torresContainer);

        // Envolver TODO en un solo ScrollPane como página web
        ScrollPane scrollPrincipal = new ScrollPane(contenidoInterno);
        scrollPrincipal.setFitToWidth(true);
        scrollPrincipal.setStyle("-fx-background: white;");

        // Contenedor raíz
        VBox root = new VBox(scrollPrincipal);
        VBox.setVgrow(scrollPrincipal, Priority.ALWAYS);

        // Cargar torres iniciales
        actualizarListaTorres();

        return root;
    }

    private void mostrarFormularioAgregarTorre() {
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
        formulario.setMaxWidth(700);

        Label lblTitulo = new Label("➕ Nueva Torre");
        lblTitulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2E7D32;");

        construirFormularioTorre(formulario, lblTitulo, null, null);
    }

    private void mostrarFormularioEditarTorre(String nombreTorre, List<Ubicacion> lockersExistentes) {
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
        formulario.setMaxWidth(700);

        Label lblTitulo = new Label("✏️ Editar Torre: " + nombreTorre);
        lblTitulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1565C0;");

        construirFormularioTorre(formulario, lblTitulo, nombreTorre, lockersExistentes);
    }

    private void construirFormularioTorre(VBox formulario, Label lblTitulo, String nombreTorreExistente, List<Ubicacion> lockersExistentes) {
        boolean modoEdicion = nombreTorreExistente != null;

        // Campo Torre
        Label lblTorre = new Label("Torre:");
        lblTorre.setStyle("-fx-font-weight: bold;");
        TextField txtTorre = new TextField();
        txtTorre.setPromptText("Ej: Torre A, Edificio Central");
        txtTorre.setPrefWidth(300);
        
        // En modo edición, prellenar con el nombre existente
        if (modoEdicion) {
            txtTorre.setText(nombreTorreExistente);
        }

        // Localización
        Label lblLocal = new Label("Localización:");
        lblLocal.setStyle("-fx-font-weight: bold;");
        
        ToggleGroup tgLocal = new ToggleGroup();
        RadioButton rbPlantaAlta = new RadioButton("Planta Alta");
        RadioButton rbPlantaBaja = new RadioButton("Planta Baja");
        rbPlantaAlta.setToggleGroup(tgLocal);
        rbPlantaBaja.setToggleGroup(tgLocal);
        
        // En modo edición, prellenar localización
        if (modoEdicion && lockersExistentes != null && !lockersExistentes.isEmpty()) {
            String localizacion = lockersExistentes.get(0).getLocalizacion();
            if ("Planta Alta".equals(localizacion)) {
                rbPlantaAlta.setSelected(true);
            } else {
                rbPlantaBaja.setSelected(true);
            }
        } else {
            rbPlantaBaja.setSelected(true);
        }
        
        HBox hbLocal = new HBox(15, rbPlantaAlta, rbPlantaBaja);

        // Contenedor de lockers
        VBox lockersContainer = new VBox(8);
        lockersContainer.setPadding(new Insets(10));
        lockersContainer.setStyle("-fx-background-color: white; -fx-border-color: #4CAF50; -fx-border-width: 2; -fx-border-radius: 5; -fx-background-radius: 5;");
        
        Label lblLockers = new Label("Lockers:");
        lblLockers.setStyle("-fx-font-weight: bold;");

        // Lista para almacenar nombres de lockers
        List<String> lockersNombres = new ArrayList<>();
        
        // En modo edición, cargar lockers existentes
        if (modoEdicion && lockersExistentes != null) {
            for (Ubicacion ubicacion : lockersExistentes) {
                lockersNombres.add(ubicacion.getNombreLocker());
            }
        }

        // Botón agregar locker
        Button btnAgregarLocker = new Button("➕ Agregar Locker");
        btnAgregarLocker.setStyle(
            "-fx-background-color: #2196F3; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 12px; " +
            "-fx-padding: 8 16; " +
            "-fx-background-radius: 5; " +
            "-fx-cursor: hand;"
        );

        lockersContainer.getChildren().addAll(lblLockers, btnAgregarLocker);
        
        // En modo edición, mostrar lockers existentes
        if (modoEdicion && lockersExistentes != null) {
            for (Ubicacion ubicacion : lockersExistentes) {
                HBox lockerItem = new HBox(10);
                lockerItem.setAlignment(Pos.CENTER_LEFT);
                lockerItem.setPadding(new Insets(5));
                lockerItem.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #ddd; -fx-border-width: 1; -fx-border-radius: 3;");

                TextField txtLockerNombre = new TextField(ubicacion.getNombreLocker());
                txtLockerNombre.setPrefWidth(200);
                
                Button btnGuardarCambio = new Button("💾");
                btnGuardarCambio.setStyle(
                    "-fx-background-color: #4CAF50; " +
                    "-fx-text-fill: white; " +
                    "-fx-padding: 4 8; " +
                    "-fx-background-radius: 3; " +
                    "-fx-cursor: hand; " +
                    "-fx-font-size: 12px;"
                );
                btnGuardarCambio.setOnAction(ev -> {
                    String nuevoNombre = txtLockerNombre.getText().trim();
                    if (!nuevoNombre.isEmpty() && !nuevoNombre.equals(ubicacion.getNombreLocker())) {
                        ubicacion.setNombreLocker(nuevoNombre);
                        rentaController.getUbicacionDAO().actualizar(ubicacion);
                        mostrarAlerta("Éxito", "Locker renombrado correctamente");
                        actualizarListaTorres();
                    }
                });

                Button btnEliminarLocker = new Button("🗑️");
                btnEliminarLocker.setStyle(
                    "-fx-background-color: #f44336; " +
                    "-fx-text-fill: white; " +
                    "-fx-padding: 4 8; " +
                    "-fx-background-radius: 3; " +
                    "-fx-cursor: hand; " +
                    "-fx-font-size: 12px;"
                );
                btnEliminarLocker.setOnAction(evElim -> {
                    try {
                        rentaController.getUbicacionDAO().eliminar(ubicacion.getUbicacionId());
                        lockersNombres.remove(ubicacion.getNombreLocker());
                        lockersContainer.getChildren().remove(lockerItem);
                        mostrarAlerta("Éxito", "Locker eliminado");
                    } catch (Exception ex) {
                        mostrarAlerta("Error", "No se pudo eliminar: " + ex.getMessage());
                    }
                });

                lockerItem.getChildren().addAll(new Label("📦"), txtLockerNombre, btnGuardarCambio, btnEliminarLocker);
                lockersContainer.getChildren().add(lockersContainer.getChildren().size() - 1, lockerItem);
            }
        }
        // Acción agregar locker
        btnAgregarLocker.setOnAction(e -> {
            // Ocultar botón agregar locker temporalmente
            btnAgregarLocker.setVisible(false);

            // Crear formulario inline para nuevo locker
            HBox lockerForm = new HBox(10);
            lockerForm.setAlignment(Pos.CENTER_LEFT);
            lockerForm.setPadding(new Insets(8));
            lockerForm.setStyle("-fx-background-color: #E3F2FD; -fx-border-radius: 5; -fx-background-radius: 5;");

            Label lblNombre = new Label("Nombre:");
            TextField txtNombreLocker = new TextField();
            txtNombreLocker.setPromptText("Ej: Locker 1, Casillero A1");
            txtNombreLocker.setPrefWidth(200);

            Button btnAceptar = new Button("✓ Aceptar");
            btnAceptar.setStyle(
                "-fx-background-color: #4CAF50; " +
                "-fx-text-fill: white; " +
                "-fx-padding: 6 12; " +
                "-fx-background-radius: 5; " +
                "-fx-cursor: hand;"
            );

            Button btnCancelar = new Button("✗");
            btnCancelar.setStyle(
                "-fx-background-color: #f44336; " +
                "-fx-text-fill: white; " +
                "-fx-padding: 6 12; " +
                "-fx-background-radius: 5; " +
                "-fx-cursor: hand;"
            );

            lockerForm.getChildren().addAll(lblNombre, txtNombreLocker, btnAceptar, btnCancelar);
            lockersContainer.getChildren().add(lockersContainer.getChildren().size() - 1, lockerForm);

            txtNombreLocker.requestFocus();

            // Aceptar locker
            btnAceptar.setOnAction(ev -> {
                String nombreLocker = txtNombreLocker.getText().trim();
                if (nombreLocker.isEmpty()) {
                    mostrarAlerta("Error", "El nombre del locker no puede estar vacío");
                    return;
                }

                // Agregar a lista
                lockersNombres.add(nombreLocker);

                // Crear label con el locker agregado
                HBox lockerItem = new HBox(10);
                lockerItem.setAlignment(Pos.CENTER_LEFT);
                lockerItem.setPadding(new Insets(5));
                lockerItem.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #ddd; -fx-border-width: 1; -fx-border-radius: 3;");

                Label lblLockerAgregado = new Label("📦 " + nombreLocker);
                lblLockerAgregado.setStyle("-fx-font-size: 13px;");

                Button btnEliminarLocker = new Button("🗑️");
                btnEliminarLocker.setStyle(
                    "-fx-background-color: #f44336; " +
                    "-fx-text-fill: white; " +
                    "-fx-padding: 4 8; " +
                    "-fx-background-radius: 3; " +
                    "-fx-cursor: hand; " +
                    "-fx-font-size: 12px;"
                );
                btnEliminarLocker.setOnAction(evElim -> {
                    lockersNombres.remove(nombreLocker);
                    lockersContainer.getChildren().remove(lockerItem);
                });

                lockerItem.getChildren().addAll(lblLockerAgregado, btnEliminarLocker);
                HBox.setHgrow(lblLockerAgregado, Priority.ALWAYS);

                // Insertar antes del botón agregar locker
                lockersContainer.getChildren().add(lockersContainer.getChildren().size() - 1, lockerItem);

                // Eliminar formulario inline
                lockersContainer.getChildren().remove(lockerForm);

                // Mostrar botón agregar locker nuevamente
                btnAgregarLocker.setVisible(true);
            });

            // Cancelar
            btnCancelar.setOnAction(ev -> {
                lockersContainer.getChildren().remove(lockerForm);
                btnAgregarLocker.setVisible(true);
            });
        });

        // Botones de acción
        HBox botones = new HBox(10);
        botones.setAlignment(Pos.CENTER_RIGHT);

        Button btnGuardar = new Button(modoEdicion ? "💾 Guardar Cambios" : "✓ Guardar Torre");
        btnGuardar.setStyle(
            "-fx-background-color: #4CAF50; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 10 20; " +
            "-fx-background-radius: 5; " +
            "-fx-cursor: hand;"
        );

        Button btnCancelar = new Button("✗ Cancelar");
        btnCancelar.setStyle(
            "-fx-background-color: #9e9e9e; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 10 20; " +
            "-fx-background-radius: 5; " +
            "-fx-cursor: hand;"
        );

        btnGuardar.setOnAction(e -> {
            String nombreTorre = txtTorre.getText().trim();
            if (nombreTorre.isEmpty()) {
                mostrarAlerta("Error", "El nombre de la torre no puede estar vacío");
                return;
            }

            // Validar que haya lockers (nuevos o existentes)
            if (lockersNombres.isEmpty() && (!modoEdicion || lockersExistentes == null || lockersExistentes.isEmpty())) {
                mostrarAlerta("Error", "Debe agregar al menos un locker");
                return;
            }

            String localizacion = rbPlantaAlta.isSelected() ? "Planta Alta" : "Planta Baja";

            try {
                var dao = rentaController.getUbicacionDAO();
                
                if (modoEdicion) {
                    // Modo edición: actualizar torre y lockers existentes
                    if (lockersExistentes != null) {
                        for (Ubicacion ubicacion : lockersExistentes) {
                            // Actualizar nombre de torre y localización
                            ubicacion.setNombreTorre(nombreTorre);
                            ubicacion.setLocalizacion(localizacion);
                            dao.actualizar(ubicacion);
                        }
                    }
                }
                
                // Agregar nuevos lockers (tanto en modo creación como edición)
                for (String nombreLocker : lockersNombres) {
                    // Verificar que no exista ya (en modo edición)
                    if (modoEdicion && lockersExistentes != null) {
                        boolean existe = lockersExistentes.stream()
                            .anyMatch(u -> u.getNombreLocker().equals(nombreLocker));
                        if (existe) continue; // Ya existe, no crear duplicado
                    }
                    
                    // Usar precio predeterminado de Config
                    float precioDefault = controller.Config.getPrecioHoraLocker();
                    Ubicacion ubicacion = new Ubicacion(nombreLocker, nombreTorre, localizacion, precioDefault);
                    ubicacion.setDisponible(true);
                    dao.guardar(ubicacion);
                }

                actualizarListaTorres();
                formularioContainer.getChildren().clear();
                notificarCambio(); // Actualizar indicadores de advertencia
                
                if (modoEdicion) {
                    mostrarAlerta("Éxito", "Torre actualizada correctamente");
                } else {
                    mostrarAlerta("Éxito", "Torre creada con " + lockersNombres.size() + " lockers");
                }
            } catch (Exception ex) {
                mostrarAlerta("Error", "No se pudo guardar la torre: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        btnCancelar.setOnAction(e -> formularioContainer.getChildren().clear());

        botones.getChildren().addAll(btnGuardar, btnCancelar);

        formulario.getChildren().addAll(
            lblTitulo,
            new Separator(),
            lblTorre, txtTorre,
            lblLocal, hbLocal,
            new Separator(),
            lockersContainer,
            new Separator(),
            botones
        );

        formularioContainer.getChildren().add(formulario);
    }

    private void actualizarListaTorres() {
        torresContainer.getChildren().clear();

        List<Ubicacion> ubicaciones = rentaController.getUbicacionDAO().obtenerTodas();

        if (ubicaciones.isEmpty()) {
            Label lblVacio = new Label("No hay torres registradas");
            lblVacio.setStyle("-fx-text-fill: gray; -fx-padding: 20;");
            torresContainer.getChildren().add(lblVacio);
            return;
        }

        // Agrupar por torre
        Map<String, List<Ubicacion>> ubicacionesPorTorre = ubicaciones.stream()
            .collect(Collectors.groupingBy(
                u -> u.getNombreTorre() != null ? u.getNombreTorre() : "Sin Torre",
                LinkedHashMap::new,
                Collectors.toList()
            ));

        // Crear bloque por cada torre
        for (Map.Entry<String, List<Ubicacion>> entry : ubicacionesPorTorre.entrySet()) {
            String nombreTorre = entry.getKey();
            List<Ubicacion> lockersLista = entry.getValue();

            VBox bloqueTorre = crearBloqueTorre(nombreTorre, lockersLista);
            torresContainer.getChildren().add(bloqueTorre);
        }
    }

    private VBox crearBloqueTorre(String nombreTorre, List<Ubicacion> lockers) {
        VBox bloque = new VBox(10);
        bloque.setPadding(new Insets(15));
        bloque.setStyle(
            "-fx-background-color: white; " +
            "-fx-border-color: #0066cc; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 10; " +
            "-fx-background-radius: 10;"
        );
        bloque.setMaxWidth(850);

        // Encabezado
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);

        Label lblTorre = new Label("🏢 " + nombreTorre);
        lblTorre.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #0066cc;");
        HBox.setHgrow(lblTorre, Priority.ALWAYS);

        String localizacion = lockers.isEmpty() ? "N/A" : lockers.get(0).getLocalizacion();
        Label lblLocalizacion = new Label("📍 " + localizacion);
        lblLocalizacion.setStyle("-fx-font-size: 13px; -fx-text-fill: #666;");

        Label lblCantidad = new Label("📦 " + lockers.size() + " lockers");
        lblCantidad.setStyle("-fx-font-size: 13px; -fx-text-fill: #666;");

        Button btnEditarTorre = new Button("✏️ Editar");
        btnEditarTorre.setStyle(
            "-fx-background-color: #2196F3; " +
            "-fx-text-fill: white; " +
            "-fx-padding: 6 12; " +
            "-fx-background-radius: 5; " +
            "-fx-cursor: hand;"
        );
        btnEditarTorre.setOnAction(e -> mostrarFormularioEditarTorre(nombreTorre, lockers));

        Button btnEliminarTorre = new Button("🗑️ Eliminar Torre");
        btnEliminarTorre.setStyle(
            "-fx-background-color: #f44336; " +
            "-fx-text-fill: white; " +
            "-fx-padding: 6 12; " +
            "-fx-background-radius: 5; " +
            "-fx-cursor: hand;"
        );
        btnEliminarTorre.setOnAction(e -> {
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmar eliminación");
            confirmacion.setHeaderText("¿Eliminar toda la torre?");
            confirmacion.setContentText("Se eliminarán " + lockers.size() + " lockers de " + nombreTorre);

            if (confirmacion.showAndWait().get() == ButtonType.OK) {
                try {
                    var dao = rentaController.getUbicacionDAO();
                    for (Ubicacion u : lockers) {
                        dao.eliminar(u.getUbicacionId());
                    }
                    actualizarListaTorres();
                    mostrarAlerta("Éxito", "Torre eliminada correctamente");
                } catch (Exception ex) {
                    mostrarAlerta("Error", "No se pudo eliminar la torre: " + ex.getMessage());
                }
            }
        });

        header.getChildren().addAll(lblTorre, lblLocalizacion, lblCantidad, btnEditarTorre, btnEliminarTorre);

        // Lista de lockers
        VBox lockersLista = new VBox(5);
        lockers.sort(Comparator.comparing(Ubicacion::getNombreLocker));

        for (Ubicacion ubicacion : lockers) {
            HBox lockerItem = crearItemLocker(ubicacion);
            lockersLista.getChildren().add(lockerItem);
        }

        bloque.getChildren().addAll(header, new Separator(), lockersLista);

        return bloque;
    }

    private HBox crearItemLocker(Ubicacion ubicacion) {
        HBox item = new HBox(15);
        item.setPadding(new Insets(8));
        item.setAlignment(Pos.CENTER_LEFT);
        item.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #ddd; -fx-border-width: 1; -fx-border-radius: 5;");

        Label lblNombre = new Label("📦 " + ubicacion.getNombreLocker());
        lblNombre.setStyle("-fx-font-size: 13px;");
        HBox.setHgrow(lblNombre, Priority.ALWAYS);

        Label lblId = new Label("ID: " + ubicacion.getUbicacionId());
        lblId.setStyle("-fx-font-size: 11px; -fx-text-fill: gray;");

        Button btnEliminar = new Button("🗑️");
        btnEliminar.setStyle(
            "-fx-background-color: #f44336; " +
            "-fx-text-fill: white; " +
            "-fx-padding: 4 8; " +
            "-fx-background-radius: 3; " +
            "-fx-cursor: hand; " +
            "-fx-font-size: 12px;"
        );
        btnEliminar.setOnAction(e -> {
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmar");
            confirmacion.setHeaderText("¿Eliminar locker?");
            confirmacion.setContentText(ubicacion.getNombreLocker());

            if (confirmacion.showAndWait().get() == ButtonType.OK) {
                try {
                    rentaController.getUbicacionDAO().eliminar(ubicacion.getUbicacionId());
                    actualizarListaTorres();
                    mostrarAlerta("Éxito", "Locker eliminado correctamente");
                } catch (Exception ex) {
                    mostrarAlerta("Error", "No se pudo eliminar: " + ex.getMessage());
                }
            }
        });

        item.getChildren().addAll(lblNombre, lblId, btnEliminar);

        return item;
    }

    public void actualizarTablas() {
        actualizarListaTorres();
    }
    
    /**
     * Verifica si hay lockers registrados
     */
    public boolean tieneLockers() {
        return !rentaController.getUbicacionDAO().obtenerTodas().isEmpty();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Crea la sección de configuración de precios y tiempos para rentas.
     */
    private VBox crearSeccionConfiguracion() {
        VBox configBox = new VBox(12);
        configBox.setPadding(new Insets(15));
        configBox.setStyle(
            "-fx-background-color: #FFF3E0; " +
            "-fx-border-color: #FF9800; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 10; " +
            "-fx-background-radius: 10;"
        );
        configBox.setMaxWidth(600);

        Label lblConfigTitulo = new Label("⚙️ Configuración de Rentas");
        lblConfigTitulo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #E65100;");

        // Verificar si hay configuración en la BD (Config usa -1 como "no configurado")
        boolean hayConfiguracion = Config.existeConfiguracion();

        // ===== Precio por hora de locker =====
        HBox precioRow = new HBox(10);
        precioRow.setAlignment(Pos.CENTER_LEFT);
        Label lblPrecio = new Label("Precio por hora (locker):");
        lblPrecio.setStyle("-fx-font-weight: bold;");
        lblPrecio.setMinWidth(180);
        
        // Solo mostrar valor si existe en BD (no es -1)
        float precioActual = Config.getPrecioHoraLocker();
        String valorPrecioInicial = (hayConfiguracion && precioActual > 0) ? String.valueOf(precioActual) : "";
        TextField txtPrecio = new TextField(valorPrecioInicial);
        txtPrecio.setPrefWidth(100);
        txtPrecio.setPromptText("Ej: 50.0");
        
        Label lblPesosPrecio = new Label("MXN");
        
        Button btnGuardarPrecio = new Button("💾 Guardar");
        btnGuardarPrecio.setStyle(
            "-fx-background-color: #4CAF50; " +
            "-fx-text-fill: white; " +
            "-fx-padding: 5 10; " +
            "-fx-background-radius: 5; " +
            "-fx-cursor: hand;"
        );
        btnGuardarPrecio.setVisible(false);
        
        // Listener para mostrar botón solo si hay cambios
        final String precioOriginal = valorPrecioInicial;
        txtPrecio.textProperty().addListener((obs, oldVal, newVal) -> {
            boolean hayCambio = !newVal.equals(precioOriginal) && !newVal.trim().isEmpty();
            btnGuardarPrecio.setVisible(hayCambio);
        });
        
        btnGuardarPrecio.setOnAction(e -> {
            try {
                float nuevoPrecio = Float.parseFloat(txtPrecio.getText().trim());
                if (nuevoPrecio <= 0) {
                    mostrarAlerta("Error", "El precio debe ser mayor a 0.");
                    return;
                }
                Config.setPrecioHoraLocker(nuevoPrecio);
                btnGuardarPrecio.setVisible(false);
                mostrarAlerta("Éxito", "Precio por hora actualizado a $" + nuevoPrecio + " MXN");
                notificarCambio();
            } catch (NumberFormatException ex) {
                mostrarAlerta("Error", "Ingrese un número válido para el precio.");
            }
        });
        
        precioRow.getChildren().addAll(lblPrecio, txtPrecio, lblPesosPrecio, btnGuardarPrecio);

        // ===== Minutos de tolerancia =====
        HBox toleranciaRow = new HBox(10);
        toleranciaRow.setAlignment(Pos.CENTER_LEFT);
        Label lblTolerancia = new Label("Minutos de tolerancia:");
        lblTolerancia.setStyle("-fx-font-weight: bold;");
        lblTolerancia.setMinWidth(180);
        
        // Si no hay configuración, usar TextField vacío en lugar de Spinner
        int toleranciaActual = Config.getMinutosTolerancia();
        String valorToleranciaStr = (hayConfiguracion && toleranciaActual >= 0) ? String.valueOf(toleranciaActual) : "";
        TextField txtTolerancia = new TextField(valorToleranciaStr);
        txtTolerancia.setPrefWidth(80);
        txtTolerancia.setPromptText("Ej: 15");
        
        Label lblMinTolerancia = new Label("minutos");
        
        Button btnGuardarTolerancia = new Button("💾 Guardar");
        btnGuardarTolerancia.setStyle(
            "-fx-background-color: #4CAF50; " +
            "-fx-text-fill: white; " +
            "-fx-padding: 5 10; " +
            "-fx-background-radius: 5; " +
            "-fx-cursor: hand;"
        );
        btnGuardarTolerancia.setVisible(false);
        
        final String toleranciaOriginal = valorToleranciaStr;
        txtTolerancia.textProperty().addListener((obs, oldVal, newVal) -> {
            boolean hayCambio = !newVal.equals(toleranciaOriginal) && !newVal.trim().isEmpty();
            btnGuardarTolerancia.setVisible(hayCambio);
        });
        
        btnGuardarTolerancia.setOnAction(e -> {
            try {
                int nuevaTolerancia = Integer.parseInt(txtTolerancia.getText().trim());
                if (nuevaTolerancia < 0 || nuevaTolerancia > 60) {
                    mostrarAlerta("Error", "Los minutos deben estar entre 0 y 60.");
                    return;
                }
                Config.setMinutosTolerancia(nuevaTolerancia);
                btnGuardarTolerancia.setVisible(false);
                mostrarAlerta("Éxito", "Minutos de tolerancia actualizados a " + nuevaTolerancia + " minutos");
                notificarCambio();
            } catch (NumberFormatException ex) {
                mostrarAlerta("Error", "Ingrese un número válido.");
            }
        });
        
        toleranciaRow.getChildren().addAll(lblTolerancia, txtTolerancia, lblMinTolerancia, btnGuardarTolerancia);

        // ===== Minutos de cancelación =====
        HBox cancelacionRow = new HBox(10);
        cancelacionRow.setAlignment(Pos.CENTER_LEFT);
        Label lblCancelacion = new Label("Minutos para cancelación:");
        lblCancelacion.setStyle("-fx-font-weight: bold;");
        lblCancelacion.setMinWidth(180);
        
        int cancelacionActual = Config.getMinutosCancelacion();
        String valorCancelacionStr = (hayConfiguracion && cancelacionActual >= 0) ? String.valueOf(cancelacionActual) : "";
        TextField txtCancelacion = new TextField(valorCancelacionStr);
        txtCancelacion.setPrefWidth(80);
        txtCancelacion.setPromptText("Ej: 5");
        
        Label lblMinCancelacion = new Label("minutos");
        
        Button btnGuardarCancelacion = new Button("💾 Guardar");
        btnGuardarCancelacion.setStyle(
            "-fx-background-color: #4CAF50; " +
            "-fx-text-fill: white; " +
            "-fx-padding: 5 10; " +
            "-fx-background-radius: 5; " +
            "-fx-cursor: hand;"
        );
        btnGuardarCancelacion.setVisible(false);
        
        final String cancelacionOriginal = valorCancelacionStr;
        txtCancelacion.textProperty().addListener((obs, oldVal, newVal) -> {
            boolean hayCambio = !newVal.equals(cancelacionOriginal) && !newVal.trim().isEmpty();
            btnGuardarCancelacion.setVisible(hayCambio);
        });
        
        btnGuardarCancelacion.setOnAction(e -> {
            try {
                int nuevaCancelacion = Integer.parseInt(txtCancelacion.getText().trim());
                if (nuevaCancelacion < 0 || nuevaCancelacion > 30) {
                    mostrarAlerta("Error", "Los minutos deben estar entre 0 y 30.");
                    return;
                }
                Config.setMinutosCancelacion(nuevaCancelacion);
                btnGuardarCancelacion.setVisible(false);
                mostrarAlerta("Éxito", "Minutos para cancelación actualizados a " + nuevaCancelacion + " minutos");
                notificarCambio();
            } catch (NumberFormatException ex) {
                mostrarAlerta("Error", "Ingrese un número válido.");
            }
        });
        
        cancelacionRow.getChildren().addAll(lblCancelacion, txtCancelacion, lblMinCancelacion, btnGuardarCancelacion);

        // ===== Descripción informativa =====
        Label lblInfo = new Label(
            "ℹ️ Tolerancia: tiempo extra sin cobro adicional después de vencer la hora.\n" +
            "ℹ️ Cancelación: tiempo máximo para cancelar una renta sin penalización."
        );
        lblInfo.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");
        lblInfo.setWrapText(true);

        configBox.getChildren().addAll(
            lblConfigTitulo,
            precioRow,
            toleranciaRow,
            cancelacionRow,
            lblInfo
        );

        return configBox;
    }
}