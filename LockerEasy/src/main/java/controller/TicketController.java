package controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import model.Renta;
import model.Servicio;
import model.Ticket;
import model.TipoServicio;
import model.Ubicacion;
import model.Venta;

public class TicketController {
    private static final String TICKETS_FOLDER = "src/main/resources/data/tickets/";
    private ReporteController reporteController;

    public TicketController() { }

    public TicketController(ReporteController reporteController) {
        this.reporteController = reporteController;
    }

    public Ticket crearNuevoTicket(String nombre_cliente, String correoCliente) {
        LocalDate hoy = LocalDate.now();
        int nuevoId = generarIdTicket(hoy);

        Ticket ticket = new Ticket();
        ticket.setFechaReporte(hoy);
        ticket.setTicketId(nuevoId);
        ticket.setNombreCliente(nombre_cliente);
        ticket.setCorreoCliente(correoCliente != null ? correoCliente : "");
        ticket.setTiempoEmision(Instant.now());
        ticket.setServicios(new ArrayList<>());
        ticket.setTotalTicket(0f);

        System.out.println("Ticket creado, ID: " + nuevoId + ", cliente: " + nombre_cliente);
        guardarTicket(ticket);
        return ticket;
    }

    public synchronized int generarIdTicket(LocalDate fecha) {
        int maxId = obtenerMaxIdTicketDelDia(fecha);
        return maxId + 1;
    }

    public synchronized int generarIdServicio(Ticket ticket) {
        if (ticket.getServicios() == null || ticket.getServicios().isEmpty()) {
            return 1;
        }

        int maxId = 0;
        List<Servicio> servicios = ticket.getServicios();
        for (Servicio servicio : servicios) {
            if (servicio.getServicioId() > maxId) {
                maxId = servicio.getServicioId();
            }
        }
        return maxId + 1;
    }

    private int obtenerMaxIdTicketDelDia(LocalDate fecha) {
        try {
            String fechaStr = "_" + fecha.toString() + ".json";
            int maxId = 0;

            if(Files.exists(Paths.get(TICKETS_FOLDER))) {
                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());

                int[] maxIdArray = {0};
                Files.list(Paths.get(TICKETS_FOLDER))
                    .filter(path -> path.getFileName().toString().endsWith(fechaStr))
                    .forEach(path -> {
                        try {
                            JsonNode node = mapper.readTree(path.toFile());
                            int id = node.get("id").asInt();
                            if (id > maxIdArray[0]) {
                                maxIdArray[0] = id;
                            }
                        } catch (java.io.IOException e) {
                            System.err.println("error leyendo ticket: " + path.getFileName().toString());
                        }
                    });
                maxId = maxIdArray[0];
            }
            return maxId;
        } catch (java.io.IOException e) {
            System.err.println("Error al obtener max ID de tickets: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Carga un ticket especifico desde un archivo JSON.
     * @param ticket_id ID del ticket
     * @param fecha_reporte     Fecha del ticket
     * @return Ticket cargado o null si no existe
    */
    public Ticket cargarTicket(int ticket_id, LocalDate fecha_reporte) {
        try {
            String nombreArchivo = TICKETS_FOLDER + "ticket_" + ticket_id + "_" + fecha_reporte.toString() + ".json";
            
            if (!Files.exists(Paths.get(nombreArchivo))) {
                System.err.println("Ticket no encontrado: " + nombreArchivo);
                return null; // El archivo no existe
            }

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());

            JsonNode rootNode = mapper.readTree(new File(nombreArchivo));

            Ticket ticket = new Ticket();
            ticket.setFechaReporte(LocalDate.parse(rootNode.get("fecha_reporte").asText()));
            ticket.setTicketId(rootNode.get("id").asInt());
            ticket.setNombreCliente(rootNode.get("nombre_cliente").asText());
            ticket.setCorreoCliente(rootNode.get("correo_cliente").asText());
            ticket.setTiempoEmision(Instant.parse(rootNode.get("tiempo_emision").asText()));
            ticket.setTotalTicket((float) rootNode.get("total_ticket").asDouble());

            if (rootNode.has("servicios")) {
                JsonNode serviciosNode = rootNode.get("servicios");
                List<Servicio> servicios = cargarServicios(serviciosNode);
                ticket.setServicios(servicios);
            }

            return ticket;
        } catch (java.io.IOException | org.json.JSONException | java.time.format.DateTimeParseException e) {
            System.err.println("Error al cargar el ticket " + ticket_id + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Carga servicios desde el JSON (Renta o Venta).
     * @param tipo
     * @param serviciosIds
     * @param fecha
     * @return Lista de servicios cargados
     */
    private List<Servicio> cargarServicios(JsonNode serviciosNode) {
        List<Servicio> servicios = new ArrayList<>();

        try {
            if (serviciosNode.isArray()) {
                for (JsonNode servicioNode : serviciosNode) {
                    int servicioId = servicioNode.get("id").asInt();
                    String tipoServicio = servicioNode.get("tipo_servicio").asText();
                    boolean descuento = servicioNode.get("descuento").asBoolean();
                    float totalServicio = (float) servicioNode.get("total_servicio").asDouble();

                    TipoServicio tipo = null;

                    if ("Renta".equals(tipoServicio)) {
                        JsonNode rentaProps = servicioNode.get("renta_properties");
                        tipo = cargarRenta(rentaProps);
                    } else if ("Venta".equals(tipoServicio)) {
                        JsonNode ventaProps = servicioNode.get("venta_properties");
                        tipo = cargarVenta(ventaProps);
                    }

                    if (tipo != null) {
                        Servicio servicio = new Servicio(servicioId, tipo, descuento);
                        servicio.setTotalServicio(totalServicio);
                        servicios.add(servicio);
                    }
                }
            }
        } catch (org.json.JSONException e) {
            System.err.println("Error al cargar servicios: " + e.getMessage());
        }
        return servicios;
    }

    /**
     * Carga una renta desde el JSON de tickets.
     * @param rentaProps
     * @return Renta
     */

    private Renta cargarRenta(JsonNode rentaProps) {
        try {
            Renta renta = new Renta();
            renta.setNombre(rentaProps.get("nombre").asText());
            renta.setPrecioRenta((float) rentaProps.get("precio").asDouble());
            renta.setCantidad(rentaProps.get("cantidad").asInt());
            renta.setInicioRenta(Instant.parse(rentaProps.get("inicio_renta").asText()));

            String cierreRenta = rentaProps.has("cierre_renta") ? 
                                 rentaProps.get("cierre_renta").asText() : "--:--:--";
            if (!"--:--:--".equals(cierreRenta)) {
                renta.setCierreRenta(Instant.parse(cierreRenta));
            }

            renta.setStateOcupado(rentaProps.get("isActive").asBoolean());
            renta.setUbicacion(Ubicacion.valueOf(rentaProps.get("ubicacion").asText()));

            return renta;
        } catch (org.json.JSONException | java.time.format.DateTimeParseException | IllegalArgumentException e) {
            System.err.println("Error al cargar renta: " + e.getMessage());
            return null;
        }
    }

    private Venta cargarVenta(JsonNode ventaProps) {
        try {
            Venta venta = new Venta();
            venta.setIdProducto(ventaProps.get("id_producto").asInt());
            venta.setNombre(ventaProps.get("nombre").asText());
            venta.setPrecio((float) ventaProps.get("precio").asDouble());
            venta.setCantidad(ventaProps.get("cantidad").asInt());

            // Cargar etiquetas
            if (ventaProps.has("etiquetas")) {
                JsonNode etiquetasNode = ventaProps.get("etiquetas");
                List<String> etiquetas = new ArrayList<>();
                if (etiquetasNode.isArray()) {
                    for (JsonNode etiquetaNode : etiquetasNode) {
                        etiquetas.add(etiquetaNode.asText());
                    }
                }
                venta.setEtiquetas(etiquetas);
            }
            return venta;
        } catch (org.json.JSONException e) {
            System.err.println("Error al cargar venta: " + e.getMessage());
            return null;
        }
    }

    /**
     * Guarda un ticket en formato JSON.
     * @param ticket Ticket a guardar
     */
    public void guardarTicket(Ticket ticket) {
        if (ticket == null) {
           throw new IllegalArgumentException("El ticket no puede ser nulo");
        }

        try {
            Files.createDirectories(Paths.get(TICKETS_FOLDER));

            LocalDate fecha = ticket.getFechaReporte();
            String nombreArchivo = TICKETS_FOLDER + "ticket_" + ticket.getTicketId() + "_" + fecha.toString() + ".json";
            
            LinkedHashMap<String, Object> map = new LinkedHashMap<>();
            map.put("id", ticket.getTicketId());
            map.put("fecha_reporte", fecha.toString());
            map.put("tiempo_emision", ticket.getTiempoEmision().toString());
            map.put("nombre_cliente", ticket.getNombreCliente());
            map.put("correo_cliente", ticket.getCorreoCliente());
            map.put("servicios",Config.convertirAMap(ticket.getServicios()));
            map.put("total_ticket", ticket.getTotalTicket());

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            
            String json = mapper.writeValueAsString(map);
            Files.write(Paths.get(nombreArchivo), json.getBytes());
            System.out.println("Ticket guardado en: " + nombreArchivo);

            if (reporteController != null) {
                reporteController.recalcularTotal();
                reporteController.guardarReporte();
            }


        } catch (java.io.IOException | org.json.JSONException e) {
            System.err.println("Error al guardar el ticket: " + e.getMessage());
        }
    }

    public void agregarServicio(Ticket ticket, TipoServicio tipo, boolean aplicarDescuento) {
        int nuevoId = generarIdServicio(ticket);

        Servicio servicio = new Servicio();
        servicio.setServicioId(nuevoId);
        servicio.setTipoServicio(tipo);
        servicio.setAplicarDescuento(aplicarDescuento);

        ticket.getServicios().add(servicio);

        float nuevoTotal = calcularTotalTicket(ticket);
        ticket.setTotalTicket(nuevoTotal);

        String tipoStr = (tipo instanceof Renta) ? "Renta" : (tipo instanceof Venta) ? "Venta" : "Desconocido";
        System.out.println("Servicio agregado al ticket " + ticket.getTicketId() + ", servicio " + tipoStr + ", ID servicio: " + nuevoId);
    }

    public boolean eliminarServicio(Ticket ticket, int servicioId) {
        boolean eliminado = ticket.getServicios().removeIf(s -> s.getServicioId() == servicioId);
        if (eliminado) {
            // Reasignar IDs de servicios
            for (int i = 0; i < ticket.getServicios().size(); i++) {
                ticket.getServicios().get(i).setServicioId(i + 1);
            }

            // Recalcular total
            float nuevoTotal = calcularTotalTicket(ticket);
            ticket.setTotalTicket(nuevoTotal);

            System.out.println("Servicio eliminado. IDs reasignados");
            return true;
        }

        System.err.println("Servicio no encontrado: " + servicioId);
        return false;
    }

    public float calcularTotalTicket(Ticket ticket) {
        float total = 0f;
        if (ticket.getServicios() != null) {
            for (Servicio servicio : ticket.getServicios()) {
                total += servicio.getTotalServicio();
            }
        }
        return total;
    }

    public boolean eliminarTicket(int ticket_id, LocalDate fecha) {
        try {
            String nombreArchivo = TICKETS_FOLDER + "ticket_" + ticket_id + "_" + fecha.toString() + ".json";
            boolean eliminado = Files.deleteIfExists(Paths.get(nombreArchivo));

            if (eliminado) {
                System.out.println("Ticket eliminado" + ticket_id);
            }

            return eliminado;
        } catch (java.io.IOException e) {
            System.err.println("Error al eliminar el ticket " + ticket_id + ": " + e.getMessage());
            return false;
        }
    }

    public float getTotalServicio(Servicio servicio) {
        return servicio.getTotalServicio();
    }

    public Servicio getServicioRenta(Ticket ticket, Ubicacion ubicacion) {
        if (ticket == null || ticket.getServicios() == null) {
            return null;
        }

        return ticket.getServicios().stream()
                .filter(s -> s.getTipoServicio() instanceof Renta)
                .filter(s -> {
                    Renta renta = (Renta) s.getTipoServicio();
                    return ubicacion.equals(renta.getUbicacion());
                })
                .findFirst()
                .orElse(null);
    }
}
