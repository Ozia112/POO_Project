package controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import model.Reporte;
import model.Ticket;

public class ReporteController {
    private static final String REPORTES_FOLDER = "src/main/resources/data/reportes/";

    private final TicketController ticketController;

    public ReporteController() {
        this.ticketController = new TicketController();
        cargarReporteActual(); // Si falla al cargar, crea uno nuevo
    }

    /**
     * Cargar el reporte actual desde un archivo JSON basado en la fecha actual.
    
     */
    private void cargarReporteActual() {
        try {
            LocalDate hoy = LocalDate.now();
            String nombre_archivo = REPORTES_FOLDER + "reporte_" + hoy.toString() + ".json";

            if (Files.exists(Paths.get(nombre_archivo))) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(new File(nombre_archivo));

                Reporte reporte = Reporte.crearNuevaInstancia();
                reporte.setFechaReporte(LocalDate.parse(rootNode.get("fecha").asText()));
                reporte.setTotal((float) rootNode.get("total").asDouble());

                // Cargar tickets asociados por ID
                if (rootNode.has("tickets_ids")) {
                    JsonNode ticketsIdsNode = rootNode.get("tickets_ids");
                    List<Ticket> tickets = cargarTicketsDesdeIds(ticketsIdsNode, hoy);
                    reporte.setTickets(tickets);
                }

                Reporte.setInstancia(reporte);
                System.out.println("Reporte cargado: " + hoy);
            } else {
                crearReporteActual();
                System.out.println("Nuevo reporte creado: " + hoy);
            }
        } catch (java.io.IOException | java.time.format.DateTimeParseException e) {
            System.err.println("Error al cargar reporte: " + e.getMessage());
            crearReporteActual();
        }
    }

    /**
     * Cargar tickets desde una lista de IDs.
     * @param ticketsIdsArray
     * @param fecha_reporte
     * @return Lista de tickets cargados
     */
    private List<Ticket> cargarTicketsDesdeIds(JsonNode ticketsIdsNode, LocalDate fecha_reporte) {
        List<Ticket> tickets = new ArrayList<>();
        if (ticketsIdsNode.isArray()) {
            for (JsonNode ticketINode : ticketsIdsNode) {
                try {
                    int ticketId = ticketINode.asInt();
                    Ticket ticket = ticketController.cargarTicket(ticketId, fecha_reporte);
                
                    if (ticket != null) {
                        tickets.add(ticket);
                    } else {
                        System.err.println("Ticket no encontrado: ID " + ticketId);
                    }
                } catch (org.json.JSONException e) {
                    int ticketId = ticketINode.asInt();
                    System.err.println("Error al cargar ticket con ID " + ticketId + ": " + e.getMessage());
                }
            }
        }
        return tickets;
    }

    /**
     * Crear un nuevo reporte actual y guardarlo.
     */
    public void crearReporteActual() {
        Reporte nuevo_reporte = Reporte.crearNuevaInstancia();
        Reporte.setInstancia(nuevo_reporte);
        guardarReporte();
    }

    public void agregarTicket(Ticket ticket) {
        if (ticket == null) {
            throw new IllegalArgumentException("El ticket no puede ser nulo");
        }

        Reporte reporte = Reporte.getInstancia();
        if (reporte == null) {
            crearReporteActual();
            reporte = Reporte.getInstancia();
        }

        ticketController.guardarTicket(ticket);

        reporte.getTickets().add(ticket);

        recalcularTotal();

        guardarReporte();
        int ticketId = ticket.getTicketId();
        System.out.println("Ticket agregado al reporte:" + ticketId);
    }

    /**
     * Eliminar un ticket del reporte actual.
     * @param ticket
     */
    public void eliminarTicketDelReporte(Ticket ticket) {
        if (ticket == null) {
            throw new IllegalArgumentException("El ticket no puede ser nulo");
        }

        Reporte reporte = Reporte.getInstancia();
        if (reporte == null) {
            throw new IllegalStateException("No hay un reporte cargado");
        }

        boolean eliminado = reporte.getTickets().remove(ticket);
        if (!eliminado) {
            throw new IllegalArgumentException("El ticket no existe en el reporte");
        }

        LocalDate fecha_ticket = ticket.getFechaReporte();
        ticketController.eliminarTicket(ticket.getTicketId(), fecha_ticket);

        recalcularTotal();

        guardarReporte();
        System.out.println("Ticket eliminado del reporte: " + ticket.getTicketId());
    }

    public Reporte getReporte() {
        return Reporte.getInstancia();
    }

    private void recalcularTotal(){
        Reporte reporte = Reporte.getInstancia();
        if (reporte == null) return;

        float total = 0.0f;
        List<Ticket> tickets = reporte.getTickets();

        for (Ticket ticket : tickets) {
            total += ticket.getTotalTicket();
        }
        reporte.setTotal(total);
    }

    public void guardarReporte() {
        try {
            Reporte reporte = Reporte.getInstancia();
            if (reporte == null) return;

            Files.createDirectories(Paths.get(REPORTES_FOLDER));
            String nombre_archivo = REPORTES_FOLDER + "reporte_" + reporte.getFechaReporte().toString() + ".json";

            LinkedHashMap<String, Object> map = new LinkedHashMap<>();
            map.put("fecha", reporte.getFechaReporte().toString());
            recalcularTotal();
            map.put("total", reporte.getTotal());
            
            // Guardar solo los IDs de los tickets (no los objetos completos)
            List<Integer> ticketsIds = new ArrayList<>();
            if (reporte.getTickets() != null) {
                for (Ticket ticket : reporte.getTickets()) {
                    ticketsIds.add(ticket.getTicketId());
                }
            }
            map.put("tickets_ids", ticketsIds);

            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            
            String json = mapper.writeValueAsString(map);
            Files.write(Paths.get(nombre_archivo), json.getBytes());
            System.out.println("Reporte guardado:" + nombre_archivo);

        } catch (java.io.IOException | org.json.JSONException e) {
            System.err.println("Error al guardar reporte: " + e.getMessage());
        }
    }

    public Reporte generarReportePorFecha(LocalDate fecha) {
        try {
            String nombre_archivo = REPORTES_FOLDER + "reporte_" + fecha.toString() + ".json";

            if (!Files.exists(Paths.get(nombre_archivo))) {
                System.err.println("No existe un reporte para la fecha: " + fecha);
                return null;
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(new File(nombre_archivo));

            Reporte reporte = new Reporte();
            reporte.setFechaReporte(LocalDate.parse(rootNode.get("fecha").asText()));
            reporte.setTotal((float) rootNode.get("total").asDouble());

            // Cargar tickets asociados por ID
            if (rootNode.has("tickets_ids")) {
                JsonNode ticketsIdsNode = rootNode.get("tickets_ids");
                List<Ticket> tickets = cargarTicketsDesdeIds(ticketsIdsNode, fecha);
                reporte.setTickets(tickets);
            }
            return reporte;

        } catch (java.io.IOException | org.json.JSONException | java.time.format.DateTimeParseException e) {
            System.err.println("Error al generar reporte por fecha: " + e.getMessage());
            return null;
        }
    }
}