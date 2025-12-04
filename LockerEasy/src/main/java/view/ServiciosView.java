package view;

import controller.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import model.*;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ServiciosView {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final TicketController ticketController;
    private final VentaController ventaController;
    private final RentaController rentaController;
    private final ReporteController reporteController;
    private final EtiquetaController etiquetaController;
    private final InventarioController inventarioController;

    // UI Componentes - Formulario Cliente
    private VBox formCliente;
    private Button btnCrearTicket;
    private TextField txtNombre;
    private TextField txtCorreo;
    
    // UI Components - Formulario Servicios
    private VBox formServicios;
    private ComboBox<String> comboServicio;
    private ComboBox<ProductoCatalogo> comboProductos;
    private Spinner<Integer> spinnerCantidad;
    private Label lblCantidad;
    private Button btnAgregarServicio;
    
    // UI Components - Otros
    private VBox root;
    private VBox listaTicketsReporte;
    private TextArea consolaTickets;
    private Label lblTotalReporte;

    // Estado Temporal
    private Ticket ticketActual = null;
    private boolean creandoNuevoTicket = false;

    // Callback para cambiar a pestaña Renta
    private Consumer<Ubicacion> onCambiarARenta;

    public ServiciosView(
            TicketController ticketController,
            VentaController ventaController,
            RentaController rentaController,
            ReporteController reporteController,
            EtiquetaController etiquetaController,
            InventarioController inventarioController
    ) {
        this.ticketController = ticketController;
        this.ventaController = ventaController;
        this.rentaController = rentaController;
        this.reporteController = reporteController;
        this.etiquetaController = etiquetaController;
        this.inventarioController = inventarioController;

        construirVista();
        cargarTicketsDelDia();
    }

    private void construirVista() {
        root = new VBox(15);
        root.setPadding(new Insets(20));

        // Layout principal: Izquierda (Formulario + Consola Tickets) | Derecha (Consola Reportes)
        HBox mainLayout = new HBox(10);
        HBox.setHgrow(mainLayout, Priority.ALWAYS);

        // Columna izquierda: Formulario + Consola tickets
        VBox leftColumn = crearColumnaIzquierda();
        HBox.setHgrow(leftColumn, Priority.ALWAYS);

        // Columna derecha: Consola reportes(Siempre visible)
        VBox rightColumn = crearColumnaDerecha();
        rightColumn.setPrefWidth(400);

        mainLayout.getChildren().addAll(leftColumn, rightColumn);

        root.getChildren().add(mainLayout);
        VBox.setVgrow(mainLayout, Priority.ALWAYS);
    }

    // Constructores de sub-vistas
    private VBox crearColumnaIzquierda() {
        VBox column = new VBox(10);

        // Seccion superior: Formulario de Cliente + Servicios + Tickets del dia
        HBox seccionSuperior = crearSeccionSuperior();

        // Seccion inferior: Consola de Tickets (debajo del formulario)
        VBox seccionConsolaTickets = crearSeccionConsolaTickets();
        VBox.setVgrow(seccionConsolaTickets, Priority.ALWAYS);

        column.getChildren().addAll(seccionSuperior, seccionConsolaTickets);

        return column;
    }

    private HBox crearSeccionSuperior() {
        formCliente = crearFormularioCliente();
        formServicios = crearFormularioServicios();

        HBox box = new HBox(10, formCliente, formServicios);
        box.setPadding(new Insets(5));

        return box;
    }

    private VBox crearFormularioCliente() {
        VBox box = new VBox(5);
        box.setPrefWidth(220);
        box.getStyleClass().add("caja-form");

        // Titulo
        Label lblTitulo = new Label("Crear Ticket");
        lblTitulo.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        
        // Boton crear ticket (Visible por defecto)
        btnCrearTicket = new Button("+ Nuevo Ticket");
        btnCrearTicket.getStyleClass().add("btn-green");
        btnCrearTicket.setMaxWidth(Double.MAX_VALUE);
        btnCrearTicket.setOnAction(e -> activarModoCrearTicket());

        // Campos de texto (ocultos por defecto)
        txtNombre = new TextField();
        txtNombre.setPromptText("Nombre cliente");
        txtNombre.getStyleClass().add("text-field");
        txtNombre.setVisible(false);
        txtNombre.setManaged(false);

        txtCorreo = new TextField();
        txtCorreo.setPromptText("Correo cliente");
        txtCorreo.getStyleClass().add("text-field");
        txtCorreo.setVisible(false);
        txtCorreo.setManaged(false);

        box.getChildren().addAll(
                lblTitulo,
                btnCrearTicket,
                txtNombre,
                txtCorreo
        );

        return box;
    }

    private VBox crearFormularioServicios() {
        VBox box = new VBox(5);
        box.setPrefWidth(280);
        box.getStyleClass().add("caja-form");
        box.setVisible(false);
        box.setManaged(false);

        // Titulo
        Label lblTitulo = new Label("Agregar Servicio");
        lblTitulo.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        
        // ComboBox de servicio (Renta + Etiquetas)
        comboServicio = new ComboBox<>();
        cargarServiciosDisponibles();
        comboServicio.setPromptText("Selecciona servicio");
        comboServicio.setMaxWidth(Double.MAX_VALUE);
        comboServicio.getStyleClass().add("combo-box");

        // ComboBox de productos (solo visible para ventas)
        comboProductos = new ComboBox<>();
        comboProductos.setPromptText("Selecciona producto");
        comboProductos.setVisible(false);
        comboProductos.setManaged(false);
        comboProductos.setMaxWidth(Double.MAX_VALUE);
        comboProductos.getStyleClass().add("combo-box");

        // Configurar Cell Factory para mostrar nombre del producto
        comboProductos.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(ProductoCatalogo producto, boolean empty) {
                super.updateItem(producto, empty);
                setText(empty || producto == null ? null : producto.getNombre());
            }
        });

        comboProductos.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(ProductoCatalogo producto, boolean empty) {
                super.updateItem(producto, empty);
                setText(empty || producto == null ? null : producto.getNombre());
            }
        });

        // Spinner de cantidad (solo visible para ventas) - en HBox con label
        spinnerCantidad = new Spinner<>(1, 100, 1);
        spinnerCantidad.setPrefWidth(70);

        lblCantidad = new Label("Cant:");
        
        HBox cantidadBox = new HBox(5, lblCantidad, spinnerCantidad);
        cantidadBox.setAlignment(Pos.CENTER_LEFT);
        cantidadBox.setVisible(false);
        cantidadBox.setManaged(false);

        // Botones
        Button btnAgregarServicio = new Button("+ Agregar");
        btnAgregarServicio.getStyleClass().add("btn-green");
        btnAgregarServicio.setMaxWidth(Double.MAX_VALUE);
        btnAgregarServicio.setStyle("-fx-font-size: 12px;");

        // Listener del ComboBox de servicio
        comboServicio.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) return;

            if ("Renta".equals(newVal)) {
                // Mostrar solo opciones de renta
                comboProductos.setVisible(false);
                comboProductos.setManaged(false);
                cantidadBox.setVisible(false);
                cantidadBox.setManaged(false);
                btnAgregarServicio.setText("Iniciar Renta");
            } else {
                cargarProductosPorEtiqueta(newVal);
                comboProductos.setVisible(true);
                comboProductos.setManaged(true);
                cantidadBox.setVisible(true);
                cantidadBox.setManaged(true);
                btnAgregarServicio.setText("+ Agregar");
            }
        });

        // Accion de agregar serrvicio
        btnAgregarServicio.setOnAction(e -> manejarAgregarServicio());

        box.getChildren().addAll(
                lblTitulo,
                comboServicio,
                comboProductos,
                cantidadBox,
                btnAgregarServicio
        );

        return box;
    }

    private VBox crearSeccionConsolaTickets() {
        consolaTickets = new TextArea();
        consolaTickets.setEditable(false);
        consolaTickets.getStyleClass().add("ticket-box");
        VBox.setVgrow(consolaTickets, Priority.ALWAYS);

        // Header con botón eliminar ticket
        Button btnEliminarTicket = new Button("🗑 Eliminar");
        btnEliminarTicket.setOnAction(e -> eliminarTicketSeleccionado());
        btnEliminarTicket.setStyle("-fx-background-color: #d9534f; -fx-text-fill: white; -fx-font-size: 11px;");
        
        Button btnLimpiar = new Button("Limpiar");
        btnLimpiar.setOnAction(e -> consolaTickets.clear());
        btnLimpiar.setStyle("-fx-font-size: 11px;");
        
        Label lblConsola = new Label("Consola de Tickets");
        lblConsola.setStyle("-fx-font-weight: bold;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        HBox header = new HBox(10, btnEliminarTicket, lblConsola, spacer, btnLimpiar);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox box = new VBox(5, header, consolaTickets);
        box.getStyleClass().add("caja-reporte");
        VBox.setVgrow(box, Priority.ALWAYS);

        return box;
    }

    private VBox crearColumnaDerecha() {
        VBox box = new VBox(10);
        box.getStyleClass().add("caja-reporte");
        
        // Título del reporte
        Label lblTitulo = new Label("📋 Reporte del Día");
        lblTitulo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        // Fecha del reporte
        Label lblFecha = new Label("Fecha: " + reporteController.getReporteActual().getFechaReporte());
        lblFecha.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");
        
        // Lista de tickets del reporte (scrollable)
        listaTicketsReporte = new VBox(5);
        listaTicketsReporte.setPadding(new Insets(10));
        listaTicketsReporte.setStyle("-fx-background-color: white;");
        
        ScrollPane scrollTickets = new ScrollPane(listaTicketsReporte);
        scrollTickets.setFitToWidth(true);
        scrollTickets.setStyle("-fx-background: white; -fx-border-color: #cccccc;");
        VBox.setVgrow(scrollTickets, Priority.ALWAYS);
        
        // Separador antes del total
        Separator separator = new Separator();
        
        // Total del reporte (siempre visible abajo)
        lblTotalReporte = new Label("TOTAL: $0.00");
        lblTotalReporte.setStyle(
            "-fx-font-size: 18px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #28a745; " +
            "-fx-padding: 10; " +
            "-fx-background-color: #f8f9fa; " +
            "-fx-border-color: #28a745; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 5; " +
            "-fx-background-radius: 5;"
        );
        lblTotalReporte.setMaxWidth(Double.MAX_VALUE);
        lblTotalReporte.setAlignment(Pos.CENTER);
        
        box.getChildren().addAll(lblTitulo, lblFecha, scrollTickets, separator, lblTotalReporte);
        
        return box;
    }

    // ====================== 1. METODOS DE LOGICA ====================================

    private void activarModoCrearTicket() {
        creandoNuevoTicket = true;
        ticketActual = null;

        btnCrearTicket.setVisible(false);
        btnCrearTicket.setManaged(false);

        txtNombre.setVisible(true);
        txtNombre.setManaged(true);
        txtCorreo.setVisible(true);
        txtCorreo.setManaged(true);

        formServicios.setVisible(true);
        formServicios.setManaged(true);

        txtNombre.clear();
        txtCorreo.clear();
        comboServicio.setValue(null);

        consolaTickets.clear();
        consolaTickets.appendText("[INFO] Ingresa los datos del cliente y selecciona un servicio\n");
    }

    private void activarModoEditarTicket(Ticket ticket) {
        creandoNuevoTicket = false;
        ticketActual = ticket;

        btnCrearTicket.setVisible(true);
        btnCrearTicket.setManaged(true);
        txtNombre.setVisible(false);
        txtNombre.setManaged(false);
        txtCorreo.setVisible(false);
        txtCorreo.setManaged(false);
        
        // Mostrar formulario de servicios
        formServicios.setVisible(true);
        formServicios.setManaged(true);

        comboServicio.setValue(null);

        mostrarTicket(ticket);
        consolaTickets.appendText("\n[INFO] Puedes agregar más servicios a este ticket\n");
    }

    private void manejarAgregarServicio() {
        String tipoServicio = comboServicio.getValue();
        
        if (tipoServicio == null) {
            mostrarError("Selecciona un tipo de servicio");
            return;
        }

        if ("Renta".equals(tipoServicio)) {
            manejarIniciarRenta();
        } else {
            manejarAgregarVenta();
        }
    }

    private void manejarIniciarRenta() {
        if (creandoNuevoTicket) {
            String nombre = txtNombre.getText().trim();
            if (nombre.isEmpty()) {
                mostrarError("Ingresa el nombre del cliente");
                return;
            }
        }

        if (ticketActual == null && !creandoNuevoTicket) {
            mostrarError("Error interno: No hay ticket seleccionado");
            return;
        }

        consolaTickets.appendText("[INFO] Selecciona una ubicacion para iniciar la renta\n");

        // Cambiar a pestaña Renta
        if (onCambiarARenta != null) {
            onCambiarARenta.accept(null);
        }
    }

    private void manejarAgregarVenta() {
        ProductoCatalogo producto = comboProductos.getValue();
        int cantidad = spinnerCantidad.getValue();

        if (producto == null) {
            mostrarError("Selecciona un producto");
            return;
        }
        
        // Si estamos creando nuevo ticket
        if (creandoNuevoTicket) {
            String nombre = txtNombre.getText().trim();
            String correo = txtCorreo.getText().trim();

            if (nombre.isEmpty()) {
                mostrarError("Ingresa el nombre del cliente");
                return;
            }

            // Crear nuevo ticket
            ticketActual = ticketController.crearNuevoTicket(nombre, correo);
            consolaTickets.appendText("[OK] Ticket creado - ID: " + ticketActual.getTicketId() + "\n");
        }

        boolean ok = ventaController.registrarVenta(producto.getId(), cantidad, ticketActual);

        if (ok) {
            consolaTickets.appendText("[OK] Agregado: " + producto.getNombre() + " x" + cantidad + "\n");

            // Actualizar vista
            mostrarTicket(ticketActual);

            if (creandoNuevoTicket) {
                cargarTicketsDelDia(); // Refrescar lista de tickets en reporte
                resetearVista();
            } else {
                // Actualizar el total del ticket en la lista
                cargarTicketsDelDia();
            }
        } else {
            mostrarError("No se pudo registrar la venta. Verifica el inventario");
        }
    }

    private void eliminarTicketSeleccionado() {
        if (ticketActual == null) {
            mostrarError("No hay ticket seleccionado");
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Advertencia");
            alert.setHeaderText(null);
            alert.setContentText("Selecciona un ticket de la lista para eliminarlo");
            alert.showAndWait();
            return;
        }

        // Confirmación
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Eliminar ticket?");
        confirmacion.setContentText(
            "Ticket ID: " + ticketActual.getTicketId() + "\n" +
            "Cliente: " + ticketActual.getNombreCliente() + "\n\n" +
            "Esta acción no se puede deshacer."
        );

        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // Verificar primero si el ticket aún existe en BD
                    Ticket ticketEnBD = ticketController.getTicketDAO().obtener(ticketActual.getTicketId());
                    if (ticketEnBD == null) {
                        mostrarError("El ticket ya no existe en la base de datos");
                        cargarTicketsDelDia(); // Refrescar lista
                        resetearVista();
                        return;
                    }
                    
                    // Guardar el total del ticket antes de eliminarlo (para restarlo del reporte)
                    float totalTicketEliminado = ticketEnBD.getTotalTicket();
                    
                    // Eliminar de la base de datos
                    boolean eliminado = ticketController.getTicketDAO().eliminar(ticketEnBD.getTicketId());

                    if (eliminado) {
                        consolaTickets.appendText("[OK] Ticket eliminado: ID " + ticketEnBD.getTicketId() + "\n");
                        consolaTickets.appendText("[INFO] Monto devuelto/cancelado: $" + String.format("%.2f", totalTicketEliminado) + "\n");
                        
                        // Actualizar reporte - remover ticket y recalcular total
                        reporteController.getReporteActual().getTickets().removeIf(
                            t -> t.getTicketId().equals(ticketEnBD.getTicketId())
                        );
                        reporteController.recalcularTotal();
                        reporteController.guardarReporte();
                        
                        // Resetear vista
                        resetearVista();
                        
                        // Recargar lista de tickets
                        cargarTicketsDelDia();
                        
                        // Mostrar reporte actualizado
                        mostrarReporteDelDia();
                        
                        // Limpiar consola
                        consolaTickets.clear();
                        consolaTickets.appendText("[INFO] Ticket eliminado correctamente\n");
                        consolaTickets.appendText("[INFO] Total del reporte actualizado\n");
                        
                        Alert exito = new Alert(Alert.AlertType.INFORMATION);
                        exito.setTitle("Éxito");
                        exito.setHeaderText(null);
                        exito.setContentText("Ticket eliminado correctamente.\nMonto devuelto: $" + String.format("%.2f", totalTicketEliminado));
                        exito.showAndWait();
                    } else {
                        mostrarError("No se pudo eliminar el ticket de la base de datos");
                    }
                } catch (Exception ex) {
                    mostrarError("Error al eliminar ticket: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });
    }

    private void resetearVista() {
        creandoNuevoTicket = false;
        ticketActual = null;

        // Ocultar campos de texto
        txtNombre.setVisible(false);
        txtNombre.setManaged(false);
        txtCorreo.setVisible(false);
        txtCorreo.setManaged(false);

        for (var node : formCliente.getChildren()) {
            if (node instanceof Label) {
                Label lbl = (Label) node;
                if (lbl.getText().equals("Nombre:") || lbl.getText().equals("Correo:")) {
                    lbl.setVisible(false);
                    lbl.setManaged(false);
                }
            }
        }

        // Mostrar botón crear ticket
        btnCrearTicket.setVisible(true);
        btnCrearTicket.setManaged(true);

        // Ocultar formulario de servicios
        formServicios.setVisible(false);
        formServicios.setManaged(false);

        // Limpiar campos
        txtNombre.clear();
        txtCorreo.clear();
        comboServicio.setValue(null);
    }

    private void cargarTicketsDelDia() {
        listaTicketsReporte.getChildren().clear();

        // Recargar reporte desde BD para obtener datos frescos
        var reporte = reporteController.recargarReporte();
        if (reporte == null) {
            reporte = reporteController.getReporteActual();
        }
        List<Ticket> tickets = reporte.getTickets();

        // Encabezado de la tabla
        HBox header = new HBox(10);
        header.setPadding(new Insets(5));
        header.setStyle("-fx-background-color: #e9ecef; -fx-border-color: #dee2e6; -fx-border-width: 0 0 2 0;");
        
        Label lblIdHeader = new Label("ID");
        lblIdHeader.setPrefWidth(50);
        lblIdHeader.setStyle("-fx-font-weight: bold;");
        
        Label lblNombreHeader = new Label("Cliente");
        lblNombreHeader.setPrefWidth(150);
        lblNombreHeader.setStyle("-fx-font-weight: bold;");
        HBox.setHgrow(lblNombreHeader, Priority.ALWAYS);
        
        Label lblTotalHeader = new Label("Total");
        lblTotalHeader.setPrefWidth(80);
        lblTotalHeader.setStyle("-fx-font-weight: bold; -fx-alignment: center-right;");
        
        header.getChildren().addAll(lblIdHeader, lblNombreHeader, lblTotalHeader);
        listaTicketsReporte.getChildren().add(header);

        for (Ticket ticket : tickets) {
            agregarTicketAListaReporte(ticket);
        }

        if (tickets.isEmpty()) {
            Label lblVacio = new Label("No hay tickets registrados");
            lblVacio.setStyle("-fx-text-fill: gray; -fx-padding: 20;");
            listaTicketsReporte.getChildren().add(lblVacio);
        }
        
        // Actualizar total del reporte
        actualizarTotalReporte();
    }

    private void agregarTicketAListaReporte(Ticket ticket) {
        // Obtener ticket actualizado de BD para tener total correcto
        Ticket ticketActualizado = ticketController.getTicketDAO().obtener(ticket.getTicketId());
        if (ticketActualizado == null) {
            // El ticket ya no existe en BD, no agregarlo a la lista
            return;
        }
        
        final Ticket ticketFinal = ticketActualizado;
        
        HBox row = new HBox(10);
        row.setPadding(new Insets(8, 5, 8, 5));
        row.setStyle("-fx-border-color: #dee2e6; -fx-border-width: 0 0 1 0; -fx-cursor: hand;");
        row.setAlignment(Pos.CENTER_LEFT);
        
        Label lblId = new Label(String.valueOf(ticketFinal.getTicketId()));
        lblId.setPrefWidth(50);
        lblId.setStyle("-fx-text-fill: #6c757d;");
        
        Label lblNombre = new Label(ticketFinal.getNombreCliente());
        lblNombre.setPrefWidth(150);
        HBox.setHgrow(lblNombre, Priority.ALWAYS);
        
        Label lblTotal = new Label("$" + String.format("%.2f", ticketFinal.getTotalTicket()));
        lblTotal.setPrefWidth(80);
        lblTotal.setStyle("-fx-font-weight: bold; -fx-text-fill: #28a745;");
        
        row.getChildren().addAll(lblId, lblNombre, lblTotal);
        
        // Evento click para mostrar ticket en consola
        row.setOnMouseClicked(e -> {
            activarModoEditarTicket(ticketFinal);
            mostrarTicket(ticketFinal);
        });
        
        // Efectos hover
        row.setOnMouseEntered(e -> row.setStyle("-fx-border-color: #dee2e6; -fx-border-width: 0 0 1 0; -fx-cursor: hand; -fx-background-color: #e3f2fd;"));
        row.setOnMouseExited(e -> row.setStyle("-fx-border-color: #dee2e6; -fx-border-width: 0 0 1 0; -fx-cursor: hand;"));
        
        listaTicketsReporte.getChildren().add(row);
    }
    
    private void actualizarTotalReporte() {
        var reporte = reporteController.getReporteActual();
        float total = reporte.getTotal();
        lblTotalReporte.setText("TOTAL DEL DÍA: $" + String.format("%.2f", total));
    }

    private void cargarServiciosDisponibles() {
        comboServicio.getItems().clear();
        
        // Agregar "Renta" como primera opción
        comboServicio.getItems().add("Renta");

        // Agregar etiquetas de la base de datos
        List<Etiqueta> etiquetas = etiquetaController.getEtiquetaDAO().obtenerTodas();
        for (Etiqueta etiqueta : etiquetas) {
            comboServicio.getItems().add(etiqueta.getNombre());
        }
    }

    private void cargarProductosPorEtiqueta(String nombreEtiqueta) {
        comboProductos.getItems().clear();

        List<ProductoCatalogo> productos = inventarioController.obtenerTodosLosProductos()
                .stream()
                .filter(p -> p.getEtiqueta() != null && nombreEtiqueta.equals(p.getEtiqueta().getNombre()))
                .filter(ProductoCatalogo::isDisponible)
                .toList();
        comboProductos.getItems().addAll(productos);
    }

    private void mostrarTicket(Ticket ticket) {
        if (ticket == null) return;

        consolaTickets.clear();
        consolaTickets.appendText("========== TICKET ==========\n");
        consolaTickets.appendText("ID: " + ticket.getTicketId() + "\n");
        consolaTickets.appendText("Cliente: " + ticket.getNombreCliente() + "\n");
        if (ticket.getCorreoCliente() != null && !ticket.getCorreoCliente().isEmpty()) {
            consolaTickets.appendText("Correo: " + ticket.getCorreoCliente() + "\n");
        }
        consolaTickets.appendText("Hora: " + instantToString(ticket.getTiempoEmision()) + "\n");
        consolaTickets.appendText("============================\n\n");

        // Acumular total calculado dinámicamente
        float totalCalculado = 0f;

        if (ticket.getServicios().isEmpty()) {
            consolaTickets.appendText("  (Sin servicios)\n");
        } else {
            for (TipoServicio servicio : ticket.getServicios()) {
                if (servicio instanceof Renta) {
                    Renta renta = (Renta) servicio;
                    
                    // REFRESCAR objeto Renta desde BD para obtener estado actualizado
                    Renta rentaActualizada = rentaController.getRentaDAO().obtener(renta.getTipoServicioId());
                    if (rentaActualizada != null) {
                        renta = rentaActualizada;
                    }
                    
                    consolaTickets.appendText("[RENTA]\n");
                    consolaTickets.appendText("  Ubicación: " + renta.getUbicacion().getNombreLocker() + "\n");
                    consolaTickets.appendText("  Inicio: " + instantToDateTime(renta.getInicioRenta()) + "\n");
                    
                    // Calcular subtotal basado en horas actuales
                    float subtotalRenta;
                    if (renta.getCierreRenta() != null) {
                        consolaTickets.appendText("  Cierre: " + instantToDateTime(renta.getCierreRenta()) + "\n");
                        consolaTickets.appendText("  Estado: FINALIZADA\n");
                        subtotalRenta = renta.getTotal();
                    } else {
                        consolaTickets.appendText("  Estado: ACTIVA\n");
                        // Recalcular horas transcurridas para mostrar subtotal actualizado
                        int horasActuales = rentaController.calcularTiempoTranscurrido(renta, java.time.Instant.now());
                        subtotalRenta = horasActuales * renta.getPrecio();
                    }
                    
                    consolaTickets.appendText("  Subtotal: $" + String.format("%.2f", subtotalRenta) + "\n\n");
                    totalCalculado += subtotalRenta;
                } else if (servicio instanceof Venta) {
                    Venta venta = (Venta) servicio;
                    consolaTickets.appendText("[VENTA]\n");
                    consolaTickets.appendText("  Producto: " + venta.getNombre() + "\n");
                    consolaTickets.appendText("  Cantidad: " + venta.getCantidad() + "\n");
                    consolaTickets.appendText("  Precio unitario: $" + String.format("%.2f", venta.getPrecio()) + "\n");
                    consolaTickets.appendText("  Subtotal: $" + String.format("%.2f", servicio.getTotal()) + "\n\n");
                    totalCalculado += servicio.getTotal();
                }
            }
        }

        consolaTickets.appendText("============================\n");
        consolaTickets.appendText("TOTAL: $" + String.format("%.2f", totalCalculado) + "\n");
        consolaTickets.appendText("============================\n");
    }

    private void mostrarReporteDelDia() {
        // Refrescar la lista de tickets y el total
        cargarTicketsDelDia();
    }

    public void actualizarEtiquetas() {
        cargarServiciosDisponibles();
    }

    public void notificarRentaCreada(Ticket ticket, Ubicacion ubicacion) {
        this.ticketActual = ticket;

        if (creandoNuevoTicket) {
            cargarTicketsDelDia(); // Refrescar lista de tickets en reporte
            resetearVista();
        }

        mostrarTicket(ticket);
        consolaTickets.appendText("[OK] Renta iniciada en " + ubicacion.getNombreLocker() + "\n");
    }

    public void notificarRentaFinalizada(Ticket ticket, Ubicacion ubicacion) {
        // Actualizar vista con el ticket finalizado
        if (ticket != null) {
            mostrarTicket(ticket);
            consolaTickets.appendText("[OK] Renta finalizada en " + ubicacion.getNombreLocker() + "\n");
        }
        
        // Actualizar reporte del día
        mostrarReporteDelDia();
        
        // Recargar lista de tickets
        cargarTicketsDelDia();
    }

    private void mostrarError(String mensaje) {
        consolaTickets.appendText("[ERROR] " + mensaje + "\n");
    }

    private String instantToString(Instant instant) {
        if (instant == null) return "--:--:--";
        return instant.atZone(ZoneId.systemDefault()).format(TIME_FORMATTER);
    }

    private String instantToDateTime(Instant instant) {
        if (instant == null) return "N/A";
        return instant.atZone(ZoneId.systemDefault()).format(DATE_TIME_FORMATTER);
    }

    // ================== GETTERS Y SETTERS ===================================

    public VBox getView() {
        return root;
    }

    public void setOnCambiarARenta(Consumer<Ubicacion> callback) {
        this.onCambiarARenta = callback;
    }

    public Ticket getTicketActual() {
        return ticketActual;
    }

    public boolean isCreandoNuevoTicket() {
        return creandoNuevoTicket;
    }

    public String getNombreClienteTemp() {
        return creandoNuevoTicket ? txtNombre.getText().trim() : null;
    }

    public String getCorreoClienteTemp() {
        if (!creandoNuevoTicket) return null;
        String correo = txtCorreo.getText().trim();
        return correo.isEmpty() ? null : correo;
    }
}