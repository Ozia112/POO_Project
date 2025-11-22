package controller;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import model.Reporte;
import model.Ticket;

public class ReporteController {
    private static final String REPORTES_FOLDER = "LockerEasy/src/main/resources/data/reportes/";

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
                String content = new String(Files.readAllBytes(Paths.get(nombre_archivo)));
                JSONObject obj = new JSONObject(content);

                Reporte reporte = Reporte.crearNuevaInstancia();
                reporte.setFechaReporte(LocalDate.parse(obj.getString("fecha")));
                reporte.setTotal(obj.getFloat("total"));

                // Cargar tickets asociados por ID
                if (obj.has("tickets_ids")) {
                    JSONArray ticketsIdsArray = obj.getJSONArray("tickets_ids");
                    List<Ticket> tickets = cargarTicketsDesdeIds(ticketsIdsArray, hoy);
                    reporte.setTickets(tickets);
                }

                Reporte.setInstancia(reporte);
                System.out.println("Reporte cargado: " + hoy);
            } else {
                crearReporteActual();
                System.out.println("Nuevo reporte creado: " + hoy);
            }
        } catch (java.io.IOException | org.json.JSONException | java.time.format.DateTimeParseException e) {
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
    private List<Ticket> cargarTicketsDesdeIds(JSONArray ticketsIdsArray, LocalDate fecha_reporte) {
        List<Ticket> tickets = new ArrayList<>();

        for (int i = 0; i < ticketsIdsArray.length(); i++) {
            try {
                int ticketId = ticketsIdsArray.getInt(i);
                Ticket ticket = ticketController.cargarTicket(ticketId, fecha_reporte);
            
                if (ticket != null) {
                    tickets.add(ticket);
                } else {
                    System.err.println("Ticket no encontrado: ID " + ticketId);
                }
            } catch (org.json.JSONException e) {
                int ticketId = ticketsIdsArray.getInt(i);
                System.err.println("Error al cargar ticket con ID " + ticketId + ": " + e.getMessage());
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

    private void guardarReporte() {
        try {
            Reporte reporte = Reporte.getInstancia();
            if (reporte == null) return;

            Files.createDirectories(Paths.get(REPORTES_FOLDER));
            String nombre_archivo = REPORTES_FOLDER + "reporte_" + reporte.getFechaReporte().toString() + ".json";

            JSONObject obj = new JSONObject();
            obj.put("fecha", reporte.getFechaReporte().toString());
            obj.put("total", reporte.getTotal());
            
            // Guardar solo los IDs de los tickets (no los objetos completos)
            JSONArray ticketsIdsArray = new JSONArray();
            if (reporte.getTickets() != null) {
                for (Ticket ticket : reporte.getTickets()) {
                    ticketsIdsArray.put(ticket.getTicketId());
                }
            }
            obj.put("tickets_ids", ticketsIdsArray);

            Files.write(Paths.get(nombre_archivo), obj.toString(2).getBytes());
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

            String content = new String(Files.readAllBytes(Paths.get(nombre_archivo)));
            JSONObject obj = new JSONObject(content);

            Reporte reporte = new Reporte();
            reporte.setFechaReporte(LocalDate.parse(obj.getString("fecha")));
            reporte.setTotal(obj.getFloat("total"));

            // Cargar tickets asociados por ID
            if (obj.has("tickets_ids")) {
                JSONArray ticketsIdsArray = obj.getJSONArray("tickets_ids");
                List<Ticket> tickets = cargarTicketsDesdeIds(ticketsIdsArray, fecha);
                reporte.setTickets(tickets);
            }
            return reporte;

        } catch (java.io.IOException | org.json.JSONException | java.time.format.DateTimeParseException e) {
            System.err.println("Error al generar reporte por fecha: " + e.getMessage());
            return null;
        }
    }
}