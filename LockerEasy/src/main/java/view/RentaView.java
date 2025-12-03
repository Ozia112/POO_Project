package view;

import controller.RentaController;
import controller.TicketController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import model.Renta;
import model.Ticket;
import model.Ubicacion;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class RentaView {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final RentaController rentaController;
    private final TicketController ticketController;

    private VBox root;
    private VBox torresContainer;
    private VBox estadoUbicacionBox;
    private VBox rentasActivasBox;

    private Ubicacion ubicacionSeleccionada = null;
    private BiConsumer<Ticket, Ubicacion> onRentaCreada;
    private BiConsumer<Ticket, Ubicacion> onRentaFinalizada;

    // Flag para saber si estmaos esperando seleccion de ubicacion
    private boolean esperandoSeleccionUbicacion = false;
    private String nombreClienteTemp = null;
    private String correoClienteTemp = null;
    private Ticket ticketTemp = null;

    public RentaView(RentaController rentaController, TicketController ticketController) {
        this.rentaController = rentaController;
        this.ticketController = ticketController;
        
        construirVista();
        actualizarVista();
    }

    private void construirVista() {
        root = new VBox(15);
        root.setPadding(new Insets(15));

        // Titulo principal
        Label lblTitulo = new Label("Gestion de Rentas");
        lblTitulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Layout principal: Izquierda (Torres) | Centro (Estado) | Derecha (Rentas activas)
        HBox mainLayout = new HBox(15);
        HBox.setHgrow(mainLayout, Priority.ALWAYS);

        // Columna izquierda: Torres con scroll
        VBox leftColumn = crearColumnaIzquierda();
        HBox.setHgrow(leftColumn,Priority.ALWAYS);

        // Columna central: Estado de ubicacion seleccionada
        VBox centerColumn = crearColumnaCentro();
        centerColumn.setPrefWidth(320);

        // Columna derecha: Rentas activas
        VBox rightColumn = crearColumnaDerecha();
        rightColumn.setPrefWidth(280);

        mainLayout.getChildren().addAll(leftColumn, centerColumn, rightColumn);

        root.getChildren().addAll(lblTitulo, mainLayout);
    }

    public VBox crearColumnaIzquierda() {
        VBox column = new VBox(10);

        Label lblTitulo = new Label("Ubicaicones por Torre");
        lblTitulo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Container para las torres con scroll
        torresContainer = new VBox(15);
        torresContainer.setPadding(new Insets(10));

        ScrollPane scroll = new ScrollPane(torresContainer);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: white; -fx-border-color: lightgray;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        column.getChildren().addAll(lblTitulo, scroll);

        return column;
    }

    private VBox crearColumnaCentro() {
        VBox column = new VBox(10);

        Label lblTitulo = new Label("Estado de Ubicación");
        lblTitulo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        estadoUbicacionBox = new VBox(10);
        estadoUbicacionBox.setPadding(new Insets(10));
        estadoUbicacionBox.setStyle("-fx-border-color: lightgray; -fx-background-color: #f9f9f9;");
        VBox.setVgrow(estadoUbicacionBox, Priority.ALWAYS);

        // Mensaje inicial
        Label lblMensaje = new Label("Selecciona una ubicación para ver su estado");
        lblMensaje.setStyle("-fx-text-fill: gray;");
        estadoUbicacionBox.getChildren().add(lblMensaje);

        column.getChildren().addAll(lblTitulo, estadoUbicacionBox);

        return column;
    }

    private VBox crearColumnaDerecha() {
        VBox column = new VBox(10);
        
        Label lblTitulo = new Label("Rentas Activas");
        lblTitulo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        rentasActivasBox = new VBox(5);
        rentasActivasBox.setPadding(new Insets(10));
        rentasActivasBox.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1; -fx-background-color: white;");
        
        ScrollPane scroll = new ScrollPane(rentasActivasBox);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: white;");
        VBox.setVgrow(scroll, Priority.ALWAYS);
        
        column.getChildren().addAll(lblTitulo, scroll);
        
        return column;
    }

    public void actualizarVista() {
        actualizarTorres();
        actualizarRentasActivas();
        
        if (ubicacionSeleccionada != null) {
            mostrarEstadoUbicacion(ubicacionSeleccionada);
        }
    }

    // Cache de rentas activas para evitar múltiples consultas
    private Map<Long, Renta> rentasActivasCache = new HashMap<>();
    
    private void actualizarTorres() {
        torresContainer.getChildren().clear();
        
        // Cargar todas las rentas activas de una sola vez (optimización)
        List<Renta> rentasActivas = rentaController.getRentaDAO().obtenerActivas();
        rentasActivasCache.clear();
        for (Renta renta : rentasActivas) {
            if (renta.getUbicacion() != null) {
                rentasActivasCache.put(renta.getUbicacion().getUbicacionId(), renta);
            }
        }
        
        // Obtener todas las ubicaciones
        List<Ubicacion> ubicaciones = rentaController.getUbicacionDAO().obtenerTodas();
        
        if (ubicaciones.isEmpty()) {
            Label lblVacio = new Label("No hay ubicaciones registradas");
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
        
        // Crear un bloque por cada torre
        for (Map.Entry<String, List<Ubicacion>> entry : ubicacionesPorTorre.entrySet()) {
            String nombreTorre = entry.getKey();
            List<Ubicacion> ubicacionesTorre = entry.getValue();
            
            VBox bloqueT = crearBloqueTorre(nombreTorre, ubicacionesTorre);
            torresContainer.getChildren().add(bloqueT);
        }
    }

    private VBox crearBloqueTorre(String nombreTorre, List<Ubicacion> ubicaciones) {
        VBox bloque = new VBox(8);
        bloque.setPadding(new Insets(12));
        bloque.setStyle("-fx-border-color: #0066cc; -fx-border-width: 2; -fx-background-color: #f8f9fa; -fx-border-radius: 5; -fx-background-radius: 5;");
        
        // Título de la torre
        Label lblTorre = new Label("🏢 " + nombreTorre);
        lblTorre.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #0066cc;");
        
        // Contenedor de lockers
        VBox lockersContainer = new VBox(5);
        
        // Ordenar por nombre de locker
        ubicaciones.sort(Comparator.comparing(Ubicacion::getNombreLocker));
        
        for (Ubicacion ubicacion : ubicaciones) {
            HBox lockerRow = crearFilaLocker(ubicacion);
            lockersContainer.getChildren().add(lockerRow);
        }
        
        bloque.getChildren().addAll(lblTorre, new Separator(), lockersContainer);
        
        return bloque;
    }

    private HBox crearFilaLocker(Ubicacion ubicacion) {
        HBox row = new HBox(10);
        row.setPadding(new Insets(8));
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-border-color: #dddddd; -fx-border-width: 1; -fx-background-color: white; -fx-cursor: hand; -fx-border-radius: 3; -fx-background-radius: 3;");
        
        // Verificar estado usando el cache (evita consulta JDBC por cada locker)
        Renta rentaActiva = rentasActivasCache.get(ubicacion.getUbicacionId());
        boolean ocupado = (rentaActiva != null && rentaActiva.getCierreRenta() == null);
        
        // Indicador de estado (círculo)
        Region indicador = new Region();
        indicador.setPrefSize(12, 12);
        indicador.setMaxSize(12, 12);
        indicador.setMinSize(12, 12);
        indicador.setStyle(String.format(
            "-fx-background-color: %s; -fx-background-radius: 50%%;",
            ocupado ? "#f0ad4e" : "#5cb85c"
        ));
        
        // Nombre del locker
        Label lblNombre = new Label(ubicacion.getNombreLocker());
        lblNombre.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        HBox.setHgrow(lblNombre, Priority.ALWAYS);
        
        // Estado
        Label lblEstado = new Label(ocupado ? "Ocupado" : "Disponible");
        lblEstado.setStyle(String.format(
            "-fx-text-fill: %s; -fx-font-size: 11px; -fx-padding: 2 8 2 8; -fx-background-color: %s; -fx-background-radius: 10;",
            ocupado ? "#856404" : "#155724",
            ocupado ? "#fff3cd" : "#d4edda"
        ));
        
        row.getChildren().addAll(indicador, lblNombre, lblEstado);
        
        // Eventos
        row.setOnMouseClicked(e -> seleccionarUbicacion(ubicacion));
        
        row.setOnMouseEntered(e -> {
            if (esperandoSeleccionUbicacion && !ocupado) {
                row.setStyle("-fx-border-color: #0066cc; -fx-border-width: 2; -fx-background-color: #e7f3ff; -fx-cursor: hand; -fx-border-radius: 3; -fx-background-radius: 3;");
            } else {
                row.setStyle("-fx-border-color: #0066cc; -fx-border-width: 1; -fx-background-color: #f0f0f0; -fx-cursor: hand; -fx-border-radius: 3; -fx-background-radius: 3;");
            }
        });
        
        row.setOnMouseExited(e -> {
            row.setStyle("-fx-border-color: #dddddd; -fx-border-width: 1; -fx-background-color: white; -fx-cursor: hand; -fx-border-radius: 3; -fx-background-radius: 3;");
        });
        
        return row;
    }

    public void seleccionarUbicacion(Ubicacion ubicacion) {
        if (ubicacion == null) return;
        
        ubicacionSeleccionada = ubicacion;
        
        // Si estamos esperando selección para iniciar renta
        if (esperandoSeleccionUbicacion) {
            manejarSeleccionParaRenta(ubicacion);
        } else {
            // Solo mostrar estado
            mostrarEstadoUbicacion(ubicacion);
        }
    }

    private void manejarSeleccionParaRenta(Ubicacion ubicacion) {
        Renta rentaActiva = rentaController.getRenta(ubicacion);
        
        if (rentaActiva != null && rentaActiva.getCierreRenta() == null) {
            mostrarAlerta("Error", "Esta ubicación ya está ocupada. Selecciona otra.");
            mostrarEstadoUbicacion(ubicacion);
            return;
        }
        
        // Crear o usar ticket existente
        Ticket ticket;
        
        if (ticketTemp != null) {
            // Modo editar: usar ticket existente
            ticket = ticketTemp;
        } else {
            // Modo crear: validar datos y crear ticket
            if (nombreClienteTemp == null || nombreClienteTemp.isEmpty()) {
                mostrarAlerta("Error", "Datos del cliente no válidos");
                return;
            }
            ticket = ticketController.crearNuevoTicket(nombreClienteTemp, correoClienteTemp);
        }
        
        // Iniciar renta
        boolean ok = rentaController.iniciarRenta(ubicacion, ticket);
        
        if (ok) {
            // Resetear flags
            esperandoSeleccionUbicacion = false;
            nombreClienteTemp = null;
            correoClienteTemp = null;
            ticketTemp = null;
            
            // Actualizar vista
            actualizarVista();
            mostrarEstadoUbicacion(ubicacion);
            
            // Notificar a ServiciosView
            if (onRentaCreada != null) {
                onRentaCreada.accept(ticket, ubicacion);
            }
        } else {
            mostrarAlerta("Error", "No se pudo iniciar la renta");
        }
    }

    private void mostrarEstadoUbicacion(Ubicacion ubicacion) {
        estadoUbicacionBox.getChildren().clear();
        
        // Título
        Label lblNombre = new Label(ubicacion.getNombreLocker());
        lblNombre.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #0066cc;");
        
        Label lblTorre = new Label("Torre: " + (ubicacion.getNombreTorre() != null ? ubicacion.getNombreTorre() : "N/A"));
        lblTorre.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");
        
        estadoUbicacionBox.getChildren().addAll(lblNombre, lblTorre, new Separator());
        
        // Verificar renta activa
        Renta renta = rentaController.getRenta(ubicacion);
        
        if (renta == null || renta.getCierreRenta() != null) {
            // ========== DISPONIBLE ==========
            Label lblEstado = new Label("✅ Disponible");
            lblEstado.setStyle("-fx-font-size: 16px; -fx-text-fill: #28a745; -fx-font-weight: bold;");
            
            estadoUbicacionBox.getChildren().addAll(
                lblEstado,
                new Label(""),
                new Label("Esta ubicación está lista para rentar.")
            );
            
            // Si estamos en modo de selección para renta
            if (esperandoSeleccionUbicacion) {
                Button btnConfirmar = new Button("Confirmar Ubicación");
                btnConfirmar.getStyleClass().add("btn-green");
                btnConfirmar.setMaxWidth(Double.MAX_VALUE);
                btnConfirmar.setOnAction(e -> manejarSeleccionParaRenta(ubicacion));
                
                estadoUbicacionBox.getChildren().addAll(
                    new Label(""),
                    btnConfirmar
                );
            }
        } else {
            // ========== OCUPADO ==========
            Ticket ticket = rentaController.getTicketDeRenta(ubicacion);
            
            String nombreCliente = ticket != null ? ticket.getNombreCliente() : "Cliente sin asignar";
            Long ticketId = ticket != null ? ticket.getTicketId() : null;
            String correoCliente = ticket != null ? ticket.getCorreoCliente() : null;
            
            int horasAcumuladas = rentaController.calcularTiempoTranscurrido(renta, Instant.now());
            float totalAcumulado = horasAcumuladas * renta.getPrecio();
            
            Label lblEstado = new Label("🔒 Ocupado");
            lblEstado.setStyle("-fx-font-size: 16px; -fx-text-fill: #dc3545; -fx-font-weight: bold;");
            
            estadoUbicacionBox.getChildren().add(lblEstado);
            estadoUbicacionBox.getChildren().add(new Separator());
            
            // ========== INFORMACIÓN DEL CLIENTE ==========
            Label lblClienteTitulo = new Label("Cliente");
            lblClienteTitulo.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
            estadoUbicacionBox.getChildren().add(lblClienteTitulo);
            
            if (ticketId != null) {
                estadoUbicacionBox.getChildren().add(new Label("Ticket ID: " + ticketId));
            }
            estadoUbicacionBox.getChildren().add(new Label("Nombre: " + nombreCliente));
            
            if (correoCliente != null && !correoCliente.isEmpty()) {
                estadoUbicacionBox.getChildren().add(new Label("Correo: " + correoCliente));
            }
            
            estadoUbicacionBox.getChildren().add(new Separator());
            
            // ========== INFORMACIÓN DE LA RENTA ==========
            Label lblRentaTitulo = new Label("Detalles de Renta");
            lblRentaTitulo.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
            estadoUbicacionBox.getChildren().add(lblRentaTitulo);
            
            estadoUbicacionBox.getChildren().addAll(
                new Label("Inicio: " + instantToDateTime(renta.getInicioRenta())),
                new Label("Horas acumuladas: " + horasAcumuladas + " hrs"),
                new Label("Precio/hora: $" + String.format("%.2f", renta.getPrecio())),
                new Label("Total acumulado: $" + String.format("%.2f", totalAcumulado))
            );
            
            // ========== BOTONES ==========
            estadoUbicacionBox.getChildren().add(new Separator());
            
            // Botón de prueba: Retroceder 1 hora el inicio
            Button btnRetroceder = new Button("⏪ Retroceder 1 hora (Prueba)");
            btnRetroceder.setMaxWidth(Double.MAX_VALUE);
            btnRetroceder.setStyle(
                "-fx-background-color: #ff9800; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 12px; " +
                "-fx-padding: 8;"
            );
            btnRetroceder.setOnAction(e -> retrocederHoraInicio(renta, ubicacion));
            
            // Botón finalizar renta
            Button btnFinalizar = new Button("Finalizar Renta");
            btnFinalizar.getStyleClass().add("btn-green");
            btnFinalizar.setMaxWidth(Double.MAX_VALUE);
            btnFinalizar.setPrefHeight(40);
            btnFinalizar.setStyle(
                "-fx-background-color: #d9534f; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 14px;"
            );

            final Ticket ticketFinal = ticket;
            
            // Pasar el ticket (puede ser null)
            btnFinalizar.setOnAction(ev -> {
                if (ticket != null) {
                    finalizarRenta(ubicacion, ticket);
                } else {
                    // Si no hay ticket, mostrar error
                    mostrarAlerta("Error", "No se puede finalizar una renta sin ticket asociado");
                }
            });
            
            estadoUbicacionBox.getChildren().addAll(btnRetroceder, btnFinalizar);
        }
    }

    private void retrocederHoraInicio(Renta renta, Ubicacion ubicacion) {
        if (renta == null) {
            mostrarAlerta("Error", "No hay renta para modificar");
            return;
        }
        
        // Retroceder 1 hora (3600 segundos)
        Instant nuevoInicio = renta.getInicioRenta().minusSeconds(3600);
        renta.setInicioRenta(nuevoInicio);
        
        // Actualizar en BD
        rentaController.getRentaDAO().actualizar(renta);
        
        // Actualizar vista
        actualizarVista();
        mostrarEstadoUbicacion(ubicacion);
        
        mostrarAlerta("Éxito", "Hora de inicio retrocedida 1 hora para pruebas");
    }

    private void finalizarRenta(Ubicacion ubicacion, Ticket ticket) {
        if (ubicacion == null || ticket == null) {
            mostrarAlerta("Error", "Datos inválidos");
            return;
        }
        
        // Verificar que la renta aún esté activa antes de continuar
        Renta rentaActual = rentaController.getRenta(ubicacion);
        if (rentaActual == null || rentaActual.getCierreRenta() != null) {
            mostrarAlerta("Información", "Esta renta ya fue finalizada anteriormente");
            actualizarVista();
            mostrarEstadoUbicacion(ubicacion);
            return;
        }
        
        // Confirmación
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar");
        confirmacion.setHeaderText("¿Finalizar renta?");
        confirmacion.setContentText("Se cerrará la renta de " + ubicacion.getNombreLocker());
        
        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean ok = rentaController.finalizarRenta(ubicacion, ticket);
                
                if (ok) {
                    actualizarVista();
                    mostrarEstadoUbicacion(ubicacion);
                    
                    mostrarAlerta("Éxito", "Renta finalizada correctamente");
                    
                    // Notificar que la renta fue finalizada para navegar a servicios
                    if (onRentaFinalizada != null) {
                        onRentaFinalizada.accept(ticket, ubicacion);
                    }
                } else {
                    mostrarAlerta("Error", "No se pudo finalizar la renta");
                }
            }
        });
    }

    private void actualizarRentasActivas() {
        rentasActivasBox.getChildren().clear();
        
        List<Renta> rentasActivas = rentaController.getRentaDAO().obtenerActivas();
        
        if (rentasActivas.isEmpty()) {
            Label lblVacio = new Label("No hay rentas activas");
            lblVacio.setStyle("-fx-text-fill: gray; -fx-padding: 10;");
            rentasActivasBox.getChildren().add(lblVacio);
            return;
        }
        
        for (Renta renta : rentasActivas) {
            Ubicacion ubicacion = renta.getUbicacion();
            Ticket ticket = rentaController.getTicketDeRenta(ubicacion);
            
            VBox rentaCard = crearTarjetaRenta(ubicacion, renta, ticket);
            rentasActivasBox.getChildren().add(rentaCard);
        }
    }

    private VBox crearTarjetaRenta(Ubicacion ubicacion, Renta renta, Ticket ticket) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-border-color: #f0ad4e; -fx-border-width: 2; -fx-background-color: #fff3cd; -fx-cursor: hand; -fx-border-radius: 5; -fx-background-radius: 5;");
        
        String nombreCliente = ticket != null ? ticket.getNombreCliente() : "Sin asignar";
        int horas = rentaController.calcularTiempoTranscurrido(renta, Instant.now());
        
        Label lblLocker = new Label("🔒 " + ubicacion.getNombreLocker());
        lblLocker.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        Label lblCliente = new Label("Cliente: " + nombreCliente);
        lblCliente.setStyle("-fx-font-size: 12px;");
        
        Label lblHoras = new Label("Horas: " + horas + " hrs");
        lblHoras.setStyle("-fx-font-size: 12px;");
        
        Label lblTorre = new Label("Torre: " + (ubicacion.getNombreTorre() != null ? ubicacion.getNombreTorre() : "N/A"));
        lblTorre.setStyle("-fx-font-size: 11px; -fx-text-fill: gray;");
        
        card.getChildren().addAll(lblLocker, lblCliente, lblHoras, lblTorre);
        
        card.setOnMouseClicked(e -> seleccionarUbicacion(ubicacion));
        
        card.setOnMouseEntered(e -> card.setStyle("-fx-border-color: #f0ad4e; -fx-border-width: 2; -fx-background-color: #ffe69c; -fx-cursor: hand; -fx-border-radius: 5; -fx-background-radius: 5;"));
        card.setOnMouseExited(e -> card.setStyle("-fx-border-color: #f0ad4e; -fx-border-width: 2; -fx-background-color: #fff3cd; -fx-cursor: hand; -fx-border-radius: 5; -fx-background-radius: 5;"));
        
        return card;
    }

    /**
     * Método llamado desde MainGUI cuando ServiciosView quiere iniciar una renta
     */
    public void iniciarRentaConDatosTemporales(String nombre, String correo, Ticket ticketExistente) {
        this.esperandoSeleccionUbicacion = true;
        this.nombreClienteTemp = nombre;
        this.correoClienteTemp = correo;
        this.ticketTemp = ticketExistente;
        
        estadoUbicacionBox.getChildren().clear();
        
        Label lblInstruccion = new Label("Selecciona una ubicación disponible");
        lblInstruccion.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #0066cc;");
        
        Label lblCliente = new Label("Cliente: " + (nombre != null ? nombre : "N/A"));
        
        estadoUbicacionBox.getChildren().addAll(
            lblInstruccion,
            new Separator(),
            lblCliente
        );
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private String instantToString(Instant instant) {
        if (instant == null) return "--:--:--";
        return instant.atZone(ZoneId.systemDefault()).format(TIME_FORMATTER);
    }

    private String instantToDateTime(Instant instant) {
        if (instant == null) return "--/--/---- --:--:--";
        return instant.atZone(ZoneId.systemDefault()).format(DATE_TIME_FORMATTER);
    }

    // ==================== GETTERS Y SETTERS ====================

    public VBox getView() {
        return root;
    }

    public void setOnRentaCreada(BiConsumer<Ticket, Ubicacion> callback) {
        this.onRentaCreada = callback;
    }

    public void setOnRentaFinalizada(BiConsumer<Ticket, Ubicacion> callback) {
        this.onRentaFinalizada = callback;
    }

    public Ubicacion getUbicacionSeleccionada() {
        return ubicacionSeleccionada;
    }
}
