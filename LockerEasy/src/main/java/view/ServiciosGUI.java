package view;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import controller.EtiquetaController;
import controller.RentaController;
import controller.ReporteController;
import controller.TicketController;
import controller.VentaController;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.time.Instant;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Renta;
import model.Servicio;
import model.Ticket;
import model.Ubicacion;

import model.Venta;

public class ServiciosGUI {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    // ------------------ CONTROLLERS ------------------
    private final ReporteController reporteController = new ReporteController();
    private final TicketController ticketController = new TicketController(reporteController);
    private final RentaController rentaController = new RentaController();
    private final VentaController ventaController = new VentaController();
    private final EtiquetaController etiquetaController = new EtiquetaController();

    // ------------------ UI COMPONENTS ------------------
    private Ticket ticketActual = null;
    private Ubicacion ubicacionSeleccionada = null;
    private TextArea consolaTickets;
    private TextArea consolaReportes;
    private TextField txtNombre;
    private TextField txtCorreo;
    private VBox listaTickets;
    private GridPane gridUbicaciones;
    private VBox estadoUbicacionBox;
    private VBox rentasEnProgresoBox;
    private boolean modoIniciarRenta = false;
    private TabPane tabPane;

    // ------------------- TEMP VARIABLES -------------------
    private String nombreClienteTemp = null;
    private String correoClienteTemp = null;

    public void mostrar(Stage stage) {
        rentaController.setReporteController(reporteController);

        // Crear consolas
        crearConsolas();

        // Crear TabPane
        tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setSide(javafx.geometry.Side.LEFT);

        Tab tabServicios = new Tab("Servicios");
        tabServicios.setContent(crearVistaServicios());

        Tab tabRenta = new Tab("Renta");
        tabRenta.setContent(crearVistaRenta());

        // Pestaña Config con sub-pestañas
        Tab tabConfig = new Tab("Config");
        tabConfig.setContent(crearVistaConfig());

        tabPane.getTabs().addAll(tabServicios, tabRenta, tabConfig);

        VBox root = new VBox(tabPane);
        root.setPadding(new Insets(10));

        Scene scene = new Scene(root, 1200, 600);

        // ============================
        // CSS APLICADO
        // ============================
        scene.getStylesheets().add("view/Styles/Styles.css");

        stage.setScene(scene);
        stage.setTitle("Servicios - LockerEasy");
        stage.show();
    }

    // ------------------- COMPONENT CREATION METHODS -------------------

    private VBox crearVistaServicios() {

        VBox boxCrearTicket = crearSeccionCrearTicket();
        boxCrearTicket.getStyleClass().add("caja-form"); // CSS APLICADO

        VBox boxAcciones = crearSeccionAcciones();
        boxAcciones.getStyleClass().add("caja-form"); // CSS APLICADO

        HBox boxBotonesUtilidad = crearBotonesUtilidad();
        boxBotonesUtilidad.getStyleClass().add("pill"); // CSS APLICADO

        listaTickets = new VBox(5);
        listaTickets.setPadding(new Insets(10));
        listaTickets.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1;");
        ScrollPane scrollTickets = new ScrollPane(listaTickets);
        scrollTickets.setFitToWidth(true);
        scrollTickets.setPrefHeight(200);

        VBox boxListaTickets = new VBox(new Label("Tickets creados"), scrollTickets);

        HBox mainSection = new HBox(20, boxCrearTicket, boxAcciones, boxListaTickets);
        mainSection.setPadding(new Insets(2));

        VBox topSection = new VBox(0, boxBotonesUtilidad, mainSection);

        VBox boxConsolaTickets = new VBox(new Label("Consola de Tickets"), consolaTickets);
        VBox boxConsolaReportes = new VBox(new Label("Consola de Reportes"), consolaReportes);
        boxConsolaTickets.getStyleClass().add("caja-reporte"); // CSS APLICADO
        boxConsolaReportes.getStyleClass().add("caja-reporte"); // CSS APLICADO

        HBox consolasHBox = new HBox(20, boxConsolaTickets, boxConsolaReportes);
        consolasHBox.setPadding(new Insets(10));

        VBox.setVgrow(consolasHBox, Priority.ALWAYS);

        return new VBox(10, topSection, consolasHBox);
    }

    private VBox crearVistaConfig() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        // Sub-pestañas dentro de Config
        TabPane subTabPane = new TabPane();
        subTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // Sub-pestaña Venta
        Tab tabVenta = new Tab("Venta");
        VentaGUI ventaGUI = new VentaGUI(ventaController);
        tabVenta.setContent(ventaGUI.getVistaIntegrada());

        // Sub-pestaña Etiquetas
        Tab tabEtiquetas = new Tab("Etiquetas");
        EtiquetasGUI etiquetasGUI = new EtiquetasGUI(etiquetaController);
        tabEtiquetas.setContent(etiquetasGUI.getVistaIntegrada());

        subTabPane.getTabs().addAll(tabVenta, tabEtiquetas);

        root.getChildren().add(subTabPane);
        VBox.setVgrow(subTabPane, Priority.ALWAYS);

        return root;
    }

        private HBox crearVistaRenta() {

        gridUbicaciones = crearGridUbicaciones();
        gridUbicaciones.getStyleClass().add("caja-form"); // CSS APLICADO

        estadoUbicacionBox = new VBox(10);
        estadoUbicacionBox.setPadding(new Insets(10));
        estadoUbicacionBox.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1;");
        estadoUbicacionBox.setPrefWidth(300);

        rentasEnProgresoBox = new VBox(5);
        rentasEnProgresoBox.setPadding(new Insets(10));
        rentasEnProgresoBox.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1;");
        rentasEnProgresoBox.setPrefWidth(250);

        ScrollPane scrollRentas = new ScrollPane(rentasEnProgresoBox);
        scrollRentas.setFitToWidth(true);

        VBox boxRentas = new VBox(new Label("Rentas en progreso"), scrollRentas);

        actualizarRentasEnProgreso();

        VBox herramientas = crearHerramientasDeLockers();
        herramientas.getStyleClass().add("caja-form"); // CSS APLICADO

        HBox contenido = new HBox(10,
                gridUbicaciones,
                estadoUbicacionBox,
                boxRentas,
                herramientas
        );

        contenido.setPadding(new Insets(10));
        HBox.setHgrow(rentasEnProgresoBox, Priority.ALWAYS);

        return contenido;
    }

    private VBox crearHerramientasDeLockers() {

        Button btnCancelar = new Button("Cancelar renta activa");
        btnCancelar.setMaxWidth(Double.MAX_VALUE);
        btnCancelar.getStyleClass().add("btn-green"); // CSS APLICADO

        btnCancelar.setOnAction(e -> {
            if (ubicacionSeleccionada == null) {
                consolaTickets.appendText("[ERROR] Selecciona una ubicación.\n");
                return;
            }

            var renta = rentaController.getRentaActiva(ubicacionSeleccionada);
            if (renta == null) {
                consolaTickets.appendText("[ERROR] No hay renta activa en esa ubicación.\n");
                return;
            }

            var ticket = rentaController.getTicketDeRenta(ubicacionSeleccionada);

            rentaController.finalizarRenta(ubicacionSeleccionada, ticket, ticketController);
            rentaController.liberarUbicacion(ubicacionSeleccionada);

            actualizarGridUbicaciones();
            actualizarRentasEnProgreso();
            mostrarEstadoUbicacion(ubicacionSeleccionada);

            consolaTickets.appendText("[OK] Renta cancelada manualmente.\n");
        });

        TextField txtTol = new TextField();
        txtTol.setPromptText("Minutos de tolerancia");
        txtTol.getStyleClass().add("text-field"); // CSS APLICADO

        Button btnTol = new Button("Guardar tolerancia");
        btnTol.getStyleClass().add("btn-green"); // CSS APLICADO

        btnTol.setOnAction(e -> {
            try {
                int min = Integer.parseInt(txtTol.getText());
                rentaController.setTolerancia(min);
                consolaTickets.appendText("[OK] Nueva tolerancia: " + min + " minutos.\n");
            } catch (NumberFormatException ex) {
                consolaTickets.appendText("[ERROR] Valor inválido.\n");
            }
        });

        TextField txtPrecio = new TextField();
        txtPrecio.setPromptText("Precio por hora ($)");
        txtPrecio.getStyleClass().add("text-field"); // CSS APLICADO

        Button btnPrecio = new Button("Guardar precio");
        btnPrecio.getStyleClass().add("btn-green"); // CSS APLICADO

        btnPrecio.setOnAction(e -> {
            try {
                float precio = Float.parseFloat(txtPrecio.getText());
                rentaController.setPrecioGeneral(precio);
                consolaTickets.appendText("[OK] Nuevo precio por hora: $" + precio + "\n");
            } catch (NumberFormatException ex) {
                consolaTickets.appendText("[ERROR] Valor inválido.\n");
            }
        });

        VBox box = new VBox(10,
                new Label("Herramientas de Lockers"),
                btnCancelar,
                new Separator(),
                new Label("Tolerancia"),
                txtTol,
                btnTol,
                new Separator(),
                new Label("Precio por hora"),
                txtPrecio,
                btnPrecio
        );

        box.setPadding(new Insets(10));
        box.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1;");

        return box;
    }

    private void crearConsolas() {
        consolaTickets = new TextArea();
        consolaTickets.setPrefHeight(400);
        consolaTickets.setEditable(false);
        consolaTickets.setPromptText("Consola de Tickets");
        consolaTickets.getStyleClass().add("ticket-box"); // CSS APLICADO

        consolaReportes = new TextArea();
        consolaReportes.setPrefHeight(400);
        consolaReportes.setEditable(false);
        consolaReportes.setPromptText("Consola de Reportes");
        consolaReportes.getStyleClass().add("ticket-box"); // CSS APLICADO
    }

    private VBox crearSeccionCrearTicket() {

        txtNombre = new TextField();
        txtNombre.setPromptText("Nombre cliente");
        txtNombre.getStyleClass().add("text-field"); // CSS APLICADO

        txtCorreo = new TextField();
        txtCorreo.setPromptText("Correo cliente");
        txtCorreo.getStyleClass().add("text-field"); // CSS APLICADO

        VBox dataCliente = new VBox(5, txtNombre, txtCorreo);
        HBox clientBox = new HBox(10, new Label("Cliente:"), dataCliente);

        return new VBox(5,
                new Label("Datos del Cliente"),
                clientBox
        );
    }

    private VBox crearSeccionAcciones() {

        ComboBox<String> combo = new ComboBox<>();
        combo.getItems().addAll("Renta", "Trámite", "Consumible", "Impresión");
        combo.setPromptText("Selecciona servicio");
        combo.getStyleClass().add("combo-box"); // CSS APLICADO

        Button btnAccion = new Button();
        btnAccion.setVisible(false);
        btnAccion.getStyleClass().add("btn-green"); // CSS APLICADO

        Button btnFinalizarTicket = new Button("Finalizar Ticket");
        btnFinalizarTicket.setVisible(false);
        btnFinalizarTicket.getStyleClass().add("btn-green"); // CSS APLICADO

        ComboBox<model.Venta> comboProductos = new ComboBox<>();
        comboProductos.setVisible(false);
        comboProductos.setPrefWidth(200);
        comboProductos.getStyleClass().add("combo-box"); // CSS APLICADO

        Button btnAgregarProducto = new Button("Agregar al Ticket");
        btnAgregarProducto.setVisible(false);
        btnAgregarProducto.getStyleClass().add("btn-green"); // CSS APLICADO

        combo.valueProperty().addListener((obs, oldVal, newVal) -> {

            comboProductos.setDisable(false);
            btnAgregarProducto.setDisable(false);
            btnFinalizarTicket.setDisable(false);

            btnAccion.setVisible(false);
            comboProductos.setVisible(false);
            btnAgregarProducto.setVisible(false);
            btnFinalizarTicket.setVisible(false);

            if (newVal == null) return;

            switch (newVal) {

                case "Renta" -> {
                    btnAccion.setVisible(true);
                    btnAccion.setText("Iniciar Renta");
                }

                case "Trámite" -> {
                    comboProductos.getItems().clear();
                    comboProductos.getItems().addAll(
                            ventaController.obtenerTodosLosProductos()
                                    .stream()
                                    .filter(p -> p.getEtiquetas().contains("Trámite"))
                                    .toList()
                    );
                    comboProductos.setVisible(true);
                    btnAgregarProducto.setVisible(true);
                    btnFinalizarTicket.setVisible(true);
                }

                case "Consumible" -> {
                    comboProductos.getItems().clear();
                    comboProductos.getItems().addAll(
                            ventaController.obtenerTodosLosProductos()
                                    .stream()
                                    .filter(p -> p.getEtiquetas().contains("Consumible"))
                                    .toList()
                    );
                    comboProductos.setVisible(true);
                    btnAgregarProducto.setVisible(true);
                    btnFinalizarTicket.setVisible(true);
                }

                case "Impresión" -> {
                    comboProductos.getItems().clear();
                    comboProductos.getItems().addAll(
                            ventaController.obtenerTodosLosProductos()
                                    .stream()
                                    .filter(p -> p.getEtiquetas().contains("Impresión"))
                                    .toList()
                    );
                    comboProductos.setVisible(true);
                    btnAgregarProducto.setVisible(true);
                    btnFinalizarTicket.setVisible(true);
                }
            }
        });

            // === BOTÓN PARA RENTA ===
            btnAccion.setOnAction(e -> {
                String tipo = combo.getValue();

                if (txtNombre.getText().trim().isEmpty()) {
                    consolaTickets.appendText("[ERROR] Debes llenar el nombre.\n");
                    return;
                }

                if ("Renta".equals(tipo)) {
                    manejarPreIniciarRenta();
                }
            });

            // === BOTÓN PARA AGREGAR PRODUCTO ===
            btnAgregarProducto.setOnAction(e -> {

                model.Venta producto = comboProductos.getValue();
                

                
                if (producto == null) {
                    consolaTickets.appendText("[ERROR] Selecciona un producto.\n");
                    return;
                }

                if (txtNombre.getText().trim().isEmpty()) {
                    consolaTickets.appendText("[ERROR] Debes llenar el nombre.\n");
                    return;
                }

                // Crear ticket si no existe
                if (ticketActual == null) {
                    ticketActual = ticketController.crearNuevoTicket(
                            txtNombre.getText(),
                            txtCorreo.getText()
                    );
                }

                boolean ok = ventaController.registrarVenta(
                        producto.getIdProducto(),
                        1,
                        ticketActual,
                        ticketController
                );

                if (ok) {
                    agregarTicketALista(ticketActual);
                    consolaTickets.appendText("[OK] Se agregó: " + producto.getNombre() + "\n");
                } else {
                    consolaTickets.appendText("[ERROR] No se pudo registrar la venta.\n");
                }
            });
            
            
                        // === BOTÓN PARA FINALIZAR TICKET ===

                        // === FINALIZAR TICKET ===
                 btnFinalizarTicket.setOnAction(e -> {

                        if (ticketActual == null) {
                            consolaTickets.appendText("[ERROR] No hay ticket activo.\n");
                            return;
                        }

                        // Primero agregar al reporte
                        reporteController.agregarTicket(ticketActual);

                        consolaTickets.appendText("[OK] Ticket finalizado.\n");

                        // Limpieza visual
                        showCurrentTicket(ticketActual);

                        txtNombre.clear();
                        txtCorreo.clear();

                        // Reset del ticket actual
                        ticketActual = null;

                        // REHABILITAR CONTROLES PARA NUEVO TICKET
                        comboProductos.setDisable(false);
                        btnAgregarProducto.setDisable(false);
                        btnFinalizarTicket.setDisable(false);
                    });




            return new VBox(10,
                    new Label("Tipo de Servicio:"),
                    combo,
                    btnAccion,
                    comboProductos,        // nuevo
                    btnAgregarProducto,     // nuevo
                    btnFinalizarTicket      // nuevo
                    
            );
        }

    

   private GridPane crearGridUbicaciones() {
    GridPane grid = new GridPane();
    grid.setHgap(5);
    grid.setVgap(5);

    // CSS APLICADO
    grid.getStyleClass().add("caja-form");

    Ubicacion[] ubicaciones = Ubicacion.values();

    for (int i = 0; i < ubicaciones.length; i++) {
        Ubicacion ubicacion = ubicaciones[i];
        Button btn = new Button(ubicacion.name());
        btn.setPrefSize(150, 80);
        btn.setUserData(ubicacion);

        actualizarEstiloBotonUbicacion(btn, ubicacion);

        btn.setOnAction(e -> manejarClickUbicacion(ubicacion));

        int row = i / 2;
        int col = i % 2;

        // CSS APLICADO
        btn.getStyleClass().add("pill");

        grid.add(btn, col, row);
    }

    return grid;
}
private HBox crearBotonesUtilidad() {
    Button btnActualizarInicio = new Button("Actualizar -1 hora inicio(TEST)");
    btnActualizarInicio.setOnAction(e -> manejarActualizarHora());
    btnActualizarInicio.getStyleClass().add("btn-green");  // CSS APLICADO

    Button btnClsTicketConsole = new Button("Limpiar consola");
    btnClsTicketConsole.setOnAction(e -> consolaTickets.clear());
    btnClsTicketConsole.getStyleClass().add("btn-green");  // CSS APLICADO

    Button btnReporte = new Button("Mostrar reporte del día");
    btnReporte.setOnAction(e -> manejarMostrarReporte());
    btnReporte.getStyleClass().add("btn-green");  // CSS APLICADO

    HBox box = new HBox(20, btnActualizarInicio, btnClsTicketConsole, btnReporte);
    box.setAlignment(Pos.CENTER_RIGHT);

    // CSS APLICADO
    box.getStyleClass().add("caja-form");

    return box;
}


    // --------------- HANDLER EVENT METHODS -------------------

    private void manejarPreIniciarRenta() {
        nombreClienteTemp = txtNombre.getText().trim();
        correoClienteTemp = txtCorreo.getText().trim();

        modoIniciarRenta = true;

        tabPane.getSelectionModel().select(1);
        gridUbicaciones.setVisible(true);

        actualizarGridUbicaciones();
    }

        private void manejarCrearTicketConVenta() {
            String nombre = txtNombre.getText().trim();
            String correo = txtCorreo.getText().trim();

            // Crear ticket si es el primero
            ticketActual = ticketController.crearNuevoTicket(nombre, correo);

            // Obtener el producto seleccionado del ComboBox
            ComboBox<Venta> comboProductos = (ComboBox<Venta>) ((HBox)((VBox)((VBox) ((HBox)((VBox) tabPane.getTabs().get(0).getContent()).getChildren().get(1)).getChildren().get(1))).getChildren().get(0)).getChildren().get(0);
            Venta producto = comboProductos.getValue();

            if (producto == null) {
                consolaTickets.appendText("[ERROR] Selecciona un producto.\n");
                return;
            }

            int idProducto = producto.getIdProducto();

            // Registrar venta REAL
            boolean ok = ventaController.registrarVenta(idProducto, 1, ticketActual, ticketController);

            if (ok) {
                agregarTicketALista(ticketActual);
                consolaTickets.appendText("[OK] Se agregó: " + producto.getNombre() + "\n");
                txtNombre.clear();
                txtCorreo.clear();
            } else {
                consolaTickets.appendText("[ERROR] No se pudo registrar la venta.\n");
            }

            showCurrentTicket(ticketActual);
        }


    private void manejarClickUbicacion(Ubicacion ubicacion) {
        if (modoIniciarRenta) {
            Renta rentaExistente = rentaController.getRentaActiva(ubicacion);
            if (rentaExistente != null) {
                consolaTickets.appendText("[ERROR] La ubicación " + ubicacion.name() + " ya tiene una renta activa.\n");
                return;
            }

            ticketActual = ticketController.crearNuevoTicket(nombreClienteTemp, correoClienteTemp);
            boolean ok = rentaController.iniciarRenta(ubicacion, ticketActual, ticketController);

            if (ok) {
                agregarTicketALista(ticketActual);

                nombreClienteTemp = null;
                correoClienteTemp = null;
                txtNombre.clear();
                txtCorreo.clear();

                modoIniciarRenta = false;
                actualizarGridUbicaciones();
                actualizarRentasEnProgreso();
                mostrarEstadoUbicacion(ubicacion);
            } else {
                consolaTickets.clear();
                consolaTickets.appendText("[ERROR] No se pudo iniciar la renta.\n");
            }

            tabPane.getSelectionModel().select(0);
        } else {
            mostrarEstadoUbicacion(ubicacion);
        }
    }

    private void mostrarEstadoUbicacion(Ubicacion ubicacion) {
        ubicacionSeleccionada = ubicacion;
        estadoUbicacionBox.getChildren().clear();

        Label lblTitulo = new Label("Ubicacion: " + ubicacion.name());
        lblTitulo.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        Renta renta = rentaController.getRentaActiva(ubicacion);

        if (renta == null) {
            estadoUbicacionBox.getChildren().addAll(
                    lblTitulo,
                    new Label("Estado: Disponible"),
                    new Label("No hay renta activa")
            );
        } else {
            Ticket ticket = rentaController.getTicketDeRenta(ubicacion);

            int horasAcumuladas = rentaController.calcularTiempoTrancurrido(renta, Instant.now());
            float totalAcumuladoActual = horasAcumuladas * renta.getPrecio();

            Label lblEstado = new Label("Estado: Ocupado");
            lblEstado.setStyle("-fx-text-fill: red;");

            estadoUbicacionBox.getChildren().addAll(
                    lblTitulo,
                    lblEstado,
                    new Label("TicketID: " + ticket.getTicketId()),
                    new Label("Cliente: " + ticket.getNombreCliente()),
                    new Label("Inicio: " + instantToString(renta.getInicioRenta())),
                    new Label("Horas acumuladas: " + horasAcumuladas),
                    new Label("Total acumulado: $" + String.format("%.2f", totalAcumuladoActual)),
                    new Label("Cierre: --:--:--")
            );

            Button btnFinalizar = new Button("Finalizar Renta");
            btnFinalizar.setOnAction(e -> manejarFinalizarRenta(ubicacion));

            estadoUbicacionBox.getChildren().add(btnFinalizar);
        }
    }

    private void manejarFinalizarRenta(Ubicacion ubicacion) {
        Renta renta = rentaController.getRentaActiva(ubicacion);
        if (renta == null) {
            consolaTickets.clear();
            consolaTickets.appendText("[ERROR] No hay una renta activa en " + ubicacion.name() + ".\n");
            return;
        }

        Ticket ticket = rentaController.getTicketDeRenta(ubicacion);
        boolean ok = rentaController.finalizarRenta(ubicacion, ticket, ticketController);

        if (ok) {
            if (ticketActual != null && ticketActual.getTicketId() == ticket.getTicketId()) {
                showCurrentTicket(ticket);
            }

            rentaController.liberarUbicacion(ubicacion);
            actualizarGridUbicaciones();
            actualizarRentasEnProgreso();
            mostrarEstadoUbicacion(ubicacion);
        } else {
            consolaTickets.clear();
            consolaTickets.appendText("[ERROR] No se pudo finalizar la renta en " + ubicacion.name() + ".\n");
        }
    }

    private void manejarActualizarHora() {
        if (ubicacionSeleccionada == null) {
            consolaTickets.clear();
            consolaTickets.appendText("[ERROR] Selecciona una ubicación primero.\n");
            return;
        }

        Renta rentaActual = rentaController.getRentaActiva(ubicacionSeleccionada);
        if (rentaActual == null) {
            consolaTickets.clear();
            consolaTickets.appendText("[ERROR] No hay renta activa en " + ubicacionSeleccionada.name() + ".\n");
            return;
        }

        Ticket ticket = rentaController.getTicketDeRenta(ubicacionSeleccionada);
        Instant tiempoRetrocedido = ticket.getTiempoEmision().minusSeconds(3600);
        ticket.setTiempoEmision(tiempoRetrocedido);
        rentaController.setInicioRentaFromTicket(ubicacionSeleccionada, ticket);

        if (ticketActual != null && ticketActual.getTicketId() == ticket.getTicketId()) {
            showCurrentTicket(ticket);
        }
        mostrarEstadoUbicacion(ubicacionSeleccionada);
    }

    private void manejarMostrarReporte() {
        var rep = reporteController.getReporte();

        consolaReportes.appendText("\n=== REPORTE DEL DÍA ===\n");
        consolaReportes.appendText("Fecha: " + rep.getFechaReporte() + "\n");
        consolaReportes.appendText("Tickets: " + rep.getTickets().size() + "\n");
        consolaReportes.appendText("Total del día: $" + rep.getTotal() + "\n");
    }

    // ------------------- UTIL METHODS -------------------

    private String instantToString(Instant instant) {
        if (instant == null) return "--:--:--";
        return instant.atZone(ZoneId.systemDefault()).format(TIME_FORMATTER);
    }

    private void showCurrentTicket(Ticket ticket) {
        if (ticket == null) {
            consolaTickets.appendText("[ERROR] No hay ticket que mostrar.\n");
            return;
        }

        consolaTickets.clear();

        consolaTickets.appendText("=== Ticket ID: " + ticket.getTicketId() + " ===\n");
        consolaTickets.appendText("Cliente: " + ticket.getNombreCliente() + "\n");

        if (ticket.getCorreoCliente() != null && !ticket.getCorreoCliente().isEmpty()) {
            consolaTickets.appendText("Correo: " + ticket.getCorreoCliente() + "\n");
        }

        consolaTickets.appendText("Tiempo emision: " + instantToString(ticket.getTiempoEmision()) + "\n");
        consolaTickets.appendText("\nServicios:\n");

        if (ticket.getServicios().isEmpty()) {
            consolaTickets.appendText("  (Sin servicios)\n");
        } else {
            for (Servicio servicio : ticket.getServicios()) {
                mostrarServicioEnConsola(servicio);
            }
        }

        consolaTickets.appendText("\n===========================\n");
        consolaTickets.appendText("Total del ticket: $" + String.format("%.2f", ticket.getTotalTicket()) + "\n");
        consolaTickets.appendText("===========================\n");
    }

    private void mostrarServicioEnConsola(Servicio servicio) {
        if (servicio == null) return;

        var tipoServicio = servicio.getTipoServicio();

        if (tipoServicio instanceof Renta) {
            Renta renta = (Renta) tipoServicio;
            consolaTickets.appendText("\n  [RENTA]\n");
            consolaTickets.appendText("  - Ubicacion: " + (renta.getUbicacion() != null ? renta.getUbicacion().name() : "N/A") + "\n");
            consolaTickets.appendText("  - Inicio: " + instantToString(renta.getInicioRenta()) + "\n");
            consolaTickets.appendText("  - Cierre: " + instantToString(renta.getCierreRenta()) + "\n");
            consolaTickets.appendText("  - Horas: " + renta.getCantidad() + "\n");
            consolaTickets.appendText("  - Total: $" + String.format("%.2f", servicio.getTotalServicio()) + "\n");
            
        } else if (tipoServicio instanceof model.Venta) {
            model.Venta venta = (model.Venta) tipoServicio;
            consolaTickets.appendText("\n  [VENTA]\n");
            consolaTickets.appendText("  - Nombre de producto: " + venta.getNombre() + "\n");
            consolaTickets.appendText("  - Precio: $" + String.format("%.2f", venta.getPrecio()) + "\n");
            consolaTickets.appendText("  - Cantidad: " + venta.getCantidad() + "\n");
            consolaTickets.appendText("  - Total: $" + String.format("%.2f", servicio.getTotalServicio()) + "\n");
        }
    }

    private void agregarTicketALista(Ticket ticket) {
        for (var node : listaTickets.getChildren()) {
            if (node instanceof Label) {
                Label lbl = (Label) node;
                if (lbl.getText().contains("ID: " + ticket.getTicketId())) {
                    ticketActual = ticket;
                    showCurrentTicket(ticket);
                    return;
                }
            }
        }

        Label lblTicket = new Label("ID: " + ticket.getTicketId() + " - " + ticket.getNombreCliente());
        lblTicket.setStyle("-fx-padding: 5; -fx-border-color: #cccccc; -fx-border-width: 0 0 1 0; -fx-cursor: hand;");
        
        lblTicket.setOnMouseClicked(e -> {
            ticketActual = ticket;
            showCurrentTicket(ticket);
        });
        
        lblTicket.setOnMouseEntered(e -> lblTicket.setStyle("-fx-padding: 5; -fx-border-color: #cccccc; -fx-border-width: 0 0 1 0; -fx-background-color: #f0f0f0; -fx-cursor: hand;"));
        lblTicket.setOnMouseExited(e -> lblTicket.setStyle("-fx-padding: 5; -fx-border-color: #cccccc; -fx-border-width: 0 0 1 0; -fx-cursor: hand;"));
        
        listaTickets.getChildren().add(lblTicket);
        
        ticketActual = ticket;
        showCurrentTicket(ticket);
    }

    private void actualizarGridUbicaciones() {
        for (var node : gridUbicaciones.getChildren()) {
            if (node instanceof Button) {
                Button btn = (Button) node;
                Ubicacion ub = (Ubicacion) btn.getUserData();
                actualizarEstiloBotonUbicacion(btn, ub);
            }
        }
    }

    private void actualizarEstiloBotonUbicacion(Button btn, Ubicacion ubicacion) {
        Renta renta = rentaController.getRentaActiva(ubicacion);
        
        if (modoIniciarRenta) {
            if (renta != null) {
                btn.setStyle("-fx-background-color: #d9534f; -fx-text-fill: white;");
                btn.setDisable(true);
            } else {
                btn.setStyle("-fx-background-color: #5cb85c; -fx-text-fill: white;");
                btn.setDisable(false);
            }
        } else {
            if (renta != null) {
                btn.setStyle("-fx-background-color: #f0ad4e; -fx-text-fill: white;");
            } else {
                btn.setStyle("-fx-background-color: #5cb85c; -fx-text-fill: white;");
            }
            btn.setDisable(false);
        }
    }

    private void actualizarRentasEnProgreso() {
        rentasEnProgresoBox.getChildren().clear();
        
        for (Ubicacion ub : Ubicacion.values()) {
            Renta renta = rentaController.getRentaActiva(ub);
            if (renta != null) {
                Ticket ticket = rentaController.getTicketDeRenta(ub);
                Label lblRenta = new Label(ub.name() + "\n" + ticket.getNombreCliente());
                lblRenta.setStyle("-fx-padding: 8; -fx-border-color: #cccccc; -fx-border-width: 1; -fx-background-color: #fff3cd; -fx-cursor: hand;");
                
                lblRenta.setOnMouseClicked(e -> {
                    tabPane.getSelectionModel().select(1);
                    mostrarEstadoUbicacion(ub);
                });
                
                lblRenta.setOnMouseEntered(e -> lblRenta.setStyle("-fx-padding: 8; -fx-border-color: #cccccc; -fx-border-width: 1; -fx-background-color: #ffe69c; -fx-cursor: hand;"));
                lblRenta.setOnMouseExited(e -> lblRenta.setStyle("-fx-padding: 8; -fx-border-color: #cccccc; -fx-border-width: 1; -fx-background-color: #fff3cd; -fx-cursor: hand;"));
                
                rentasEnProgresoBox.getChildren().add(lblRenta);
            }
        }
        
        if (rentasEnProgresoBox.getChildren().isEmpty()) {
            rentasEnProgresoBox.getChildren().add(new Label("No hay rentas activas"));
        }
    }


    // =========================================================
    // CREAR TICKET
    // =========================================================
    private void crearNuevoTicket(String nombre, String correo) {

        if (correo == null || correo.isBlank())
            correo = "correo@desconocido.com";

        ticketActual = ticketController.crearNuevoTicket(nombre, correo);

        // Agregar al reporte del día
        reporteController.getReporte().getTickets().add(ticketActual);

        ticketController.guardarTicket(ticketActual);

        System.out.println("[LOG] Ticket creado: " + ticketActual.getTicketId());
    }


    // =========================================================
    // AGREGAR SERVICIO (SOLO lo que YA existe)
    // =========================================================
    private void agregarServicio(String tipo) {

        if (ticketActual == null) return;

        switch (tipo) {

            case "Consumible":
                ventaController.registrarVenta(1, 1, ticketActual, ticketController);
                break;

            case "Impresión":
                ventaController.registrarVenta(2, 1, ticketActual, ticketController);
                break;

            case "Trámite":
                ventaController.registrarVenta(1, 1, ticketActual, ticketController);
                break;

            case "Renta":
                rentaController.iniciarRenta(
                        Ubicacion.PA_T1_L1,
                        ticketActual,
                        ticketController
                );
                break;
        }

        System.out.println("[LOG] Servicio agregado: " + tipo);
    }


    // =========================================================
    // TABLA REPORTE DEL DÍA
    // =========================================================
    private void actualizarTablaDia(TableView<Ticket> tabla) {
        tabla.getItems().clear();
        tabla.getItems().addAll(reporteController.getReporte().getTickets());
    }


    // =========================================================
    // PREVIEW TICKET (COMPLETO)
    // =========================================================
    private void mostrarTicket(VBox box, Ticket t) {

        box.getChildren().clear();

        if (t == null) {
            box.getChildren().add(new Label("Aún no hay ticket seleccionado."));
            return;
        }

        Label titulo = new Label("=== Ticket ID: " + t.getTicketId() + " ===");
        titulo.setStyle("-fx-font-size: 22; -fx-font-weight: bold;");

        box.getChildren().add(titulo);
        box.getChildren().add(new Label("Cliente: " + t.getNombreCliente()));
        box.getChildren().add(new Label("Correo: " + t.getCorreoCliente()));

        // Servicios
        box.getChildren().add(new Label("\nServicios:"));

        VBox lista = new VBox(5);

        if (t.getServicios().isEmpty()) {
            lista.getChildren().add(new Label("(Sin servicios)"));
        } else {

            for (Servicio s : t.getServicios()) {

                if (s.getTipoServicio() instanceof model.Venta v) {
                    lista.getChildren().add(new Label(
                            "[VENTA] " + v.getNombre() +
                                    " | Cant: " + v.getCantidad() +
                                    " | Precio: $" + v.getPrecio() +
                                    " | Total: $" + s.getTotalServicio()
                    ));
                }

                if (s.getTipoServicio() instanceof Renta r) {
                    lista.getChildren().add(new Label(
                            "[RENTA] " + r.getUbicacion() +
                                    " | Inicio: " + r.getInicioRenta() +
                                    " | Cierre: " + r.getCierreRenta() +
                                    " | Total: $" + s.getTotalServicio()
                    ));
                }
            }
        }

        box.getChildren().add(lista);

        Label total = new Label("\nTotal: $" + t.getTotalTicket());
        total.setStyle("-fx-font-weight: bold; -fx-font-size: 18;");
        box.getChildren().add(total);
    }


    // =========================================================
    // HISTORIAL REPORTES
    // =========================================================
    private List<File> obtenerReportes() {
        File carpeta = new File("src/main/resources/data/reportes");
        return List.of(carpeta.listFiles((d, name) -> name.endsWith(".json")));
    }

}
