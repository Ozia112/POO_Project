package view;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

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

import model.Renta;
import model.Servicio;
import model.Ticket;
import model.Ubicacion;

public class PruebasGUI {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    // ------------------ CONTROLLERS ------------------
    private final ReporteController reporteController = new ReporteController();
    private final TicketController ticketController = new TicketController(reporteController);
    private final RentaController rentaController = new RentaController();
    private final VentaController ventaController = new VentaController();

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

        tabPane.getTabs().addAll(tabServicios, tabRenta);

        VBox root = new VBox(tabPane);
        root.setPadding(new Insets(10));

        Scene scene = new Scene(root, 1200, 600);
        stage.setScene(scene);
        stage.setTitle("Pruebas del Sistema - LockerEasy");
        stage.show();
    }

    // ------------------- COMPONENT CREATION METHODS -------------------

    private VBox crearVistaServicios() {
        VBox boxCrearTicket = crearSeccionCrearTicket();
        VBox boxAcciones = crearSeccionAcciones();
        HBox boxBotonesUtilidad = crearBotonesUtilidad();

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

        HBox consolasHBox = new HBox(20, boxConsolaTickets, boxConsolaReportes);
        consolasHBox.setPadding(new Insets(10));
        consolasHBox.setStyle("-fx-border-color: green; -fx-border-width: 2;");

        VBox.setVgrow(consolasHBox, Priority.ALWAYS);

        return new VBox(10, topSection, consolasHBox);
    }

    private HBox crearVistaRenta() {
        gridUbicaciones = crearGridUbicaciones();

        estadoUbicacionBox = new VBox(10);
        estadoUbicacionBox.setPadding(new Insets(10));
        estadoUbicacionBox.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1;");
        estadoUbicacionBox.setPrefWidth(300);
        estadoUbicacionBox.getChildren().add(new Label("Selecciona una ubicación"));

        rentasEnProgresoBox = new VBox(5);
        rentasEnProgresoBox.setPadding(new Insets(10));
        rentasEnProgresoBox.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1;");
        rentasEnProgresoBox.setPrefWidth(250);

        ScrollPane scrollRentas = new ScrollPane(rentasEnProgresoBox);
        scrollRentas.setFitToWidth(true);

        VBox boxRentas = new VBox(new Label("Rentas en progreso"), scrollRentas);

        actualizarRentasEnProgreso();

        HBox contenido = new HBox(10, gridUbicaciones, estadoUbicacionBox, boxRentas);
        contenido.setPadding(new Insets(10));
        HBox.setHgrow(rentasEnProgresoBox, Priority.ALWAYS);
        
        return contenido;
    }

    private void crearConsolas() {
        consolaTickets = new TextArea();
        consolaTickets.setPrefHeight(400);
        consolaTickets.setEditable(false);
        consolaTickets.setPromptText("Consola de Tickets");

        consolaReportes = new TextArea();
        consolaReportes.setPrefHeight(400);
        consolaReportes.setEditable(false);
        consolaReportes.setPromptText("Consola de Reportes");
    }

    private VBox crearSeccionCrearTicket() {
        txtNombre = new TextField();
        txtNombre.setPromptText("Nombre cliente");

        txtCorreo = new TextField();
        txtCorreo.setPromptText("Correo cliente");

        VBox dataCliente = new VBox(5, txtNombre, txtCorreo);
        HBox clientBox = new HBox(10, new Label("Cliente:"), dataCliente);

        return new VBox(5,
                new Label("Datos del Cliente"),
                clientBox
        );
    }

    private VBox crearSeccionAcciones() {
        ChoiceBox<String> acciones = new ChoiceBox<>();
        acciones.getItems().addAll("Agregar servicio", "Iniciar renta", "Registrar venta");
        acciones.setValue("Agregar servicio");

        Button btnAccion = new Button("Ejecutar acción");
        btnAccion.setVisible(false);

        // Listener para cambios en el ChoiceBox
        acciones.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.equals("Agregar servicio")) {
                acciones.getItems().remove("Agregar servicio");
                btnAccion.setVisible(true);
                btnAccion.setText(newVal);
            } else {
                btnAccion.setVisible(false);
            }
        });

        btnAccion.setOnAction(e -> {
            String accion = acciones.getValue();

            if(txtNombre.getText().trim().isEmpty()) {
                consolaTickets.appendText("[ERROR] Debes llenar nombre.\n");
                return;
            }

            switch (accion) {
                case "Iniciar renta" -> manejarPreIniciarRenta();
                case "Registrar venta" -> manejarCrearTicketConVenta();
            }
        });

        return new VBox(5,
                new Label("Acciones"),
                acciones,
                btnAccion
        );
    }

    private GridPane crearGridUbicaciones() {
        GridPane grid = new GridPane();
        grid.setHgap(5);
        grid.setVgap(5);

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
            grid.add(btn, col, row);
        }

        return grid;
    }

    private HBox crearBotonesUtilidad() {
        Button btnActualizarInicio = new Button("Actualizar -1 hora inicio(TEST)");
        btnActualizarInicio.setOnAction(e -> manejarActualizarHora());

        Button btnClsTicketConsole = new Button("Limpiar consola");
        btnClsTicketConsole.setOnAction(e -> consolaTickets.clear());

        Button btnReporte = new Button("Mostrar reporte del día");
        btnReporte.setOnAction(e -> manejarMostrarReporte());

        HBox box = new HBox(20, btnActualizarInicio, btnClsTicketConsole, btnReporte);
        box.setAlignment(Pos.CENTER_RIGHT);
        return box;
    }

    // --------------- HANDLER EVENT METHODS -------------------

    private void manejarPreIniciarRenta() {
        String nombre = txtNombre.getText().trim();
        String correo = txtCorreo.getText().trim();

        // Crear ticket temporal
        nombreClienteTemp = nombre;
        correoClienteTemp = correo;

        modoIniciarRenta = true;

        tabPane.getSelectionModel().select(1); // Cambiar a la pestaña de Renta
        gridUbicaciones.setVisible(true);

        actualizarGridUbicaciones();
    }

     private void manejarCrearTicketConVenta() {
        String nombre = txtNombre.getText().trim();
        String correo = txtCorreo.getText().trim();

        ticketActual = ticketController.crearNuevoTicket(nombre, correo);
        
        boolean ok = ventaController.registrarVenta(1, 2, ticketActual, ticketController);
        
        if (ok) {
            agregarTicketALista(ticketActual);
            txtNombre.clear();
            txtCorreo.clear();
        } else {
            consolaTickets.clear();
            consolaTickets.appendText("[ERROR] No se pudo registrar la venta.\n");
        }
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

            tabPane.getSelectionModel().select(0); // Volver a la pestaña de Servicios
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
            // Actualizar la vista del ticket
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

        // Actualizar vista
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

        // Encabezado
        consolaTickets.appendText("=== Ticket ID: " + ticket.getTicketId() + " ===\n");
        consolaTickets.appendText("Cliente: " + ticket.getNombreCliente() + "\n");

        // Correo si existe
        if (ticket.getCorreoCliente() != null && !ticket.getCorreoCliente().isEmpty()) {
            consolaTickets.appendText("Correo: " + ticket.getCorreoCliente() + "\n");
        }

        consolaTickets.appendText("Tiempo emision: " + instantToString(ticket.getTiempoEmision()) + "\n");
        consolaTickets.appendText("\nServicios:\n");

        // Mostrar servicios
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
        // Verificar si el ticket ya está en la lista
        for (var node : listaTickets.getChildren()) {
            if (node instanceof Label) {
                Label lbl = (Label) node;
                if (lbl.getText().contains("ID: " + ticket.getTicketId())) {
                    // Ya existe, solo actualizar
                    ticketActual = ticket;
                    showCurrentTicket(ticket);
                    return;
                }
            }
        }

        // Si no existe, crear nuevo
        Label lblTicket = new Label("ID: " + ticket.getTicketId() + " - " + ticket.getNombreCliente());
        lblTicket.setStyle("-fx-padding: 5; -fx-border-color: #cccccc; -fx-border-width: 0 0 1 0; -fx-cursor: hand;");
        
        lblTicket.setOnMouseClicked(e -> {
            ticketActual = ticket;
            showCurrentTicket(ticket);
        });
        
        lblTicket.setOnMouseEntered(e -> lblTicket.setStyle("-fx-padding: 5; -fx-border-color: #cccccc; -fx-border-width: 0 0 1 0; -fx-background-color: #f0f0f0; -fx-cursor: hand;"));
        lblTicket.setOnMouseExited(e -> lblTicket.setStyle("-fx-padding: 5; -fx-border-color: #cccccc; -fx-border-width: 0 0 1 0; -fx-cursor: hand;"));
        
        listaTickets.getChildren().add(lblTicket);
        
        // Mostrar el ticket recién agregado
        ticketActual = ticket;
        showCurrentTicket(ticket);
    }

    // GRID UBICACIONES METHODS

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

}