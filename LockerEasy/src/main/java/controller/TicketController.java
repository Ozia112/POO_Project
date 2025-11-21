package controller;

import model.Ticket;
import model.Servicio;
import model.TipoServicio;
import model.Renta;
import model.Ubicacion;
import model.Venta;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TicketController {
    private static final String TICKETS_FOLDER = "LockerEasy/src/main/resources/data/tickets/";

    public TicketController() { }

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
                Files.list(Paths.get(TICKETS_FOLDER))
                    .filter(path -> path.getFileName().toString().endsWith(fechaStr))
                    .forEach(path -> {
                        try {
                            String nombre = path.getFileName().toString();
                            String idStr = nombre.substring("ticket_".length(), nombre.indexOf(fechaStr));
                            int id = Integer.parseInt(idStr);
                            // No se actualiza maxId aqui, se hace se hace en el metodo que llama
                        } catch (Exception e) {
                            System.err.println("Error al parsear nombre del archivo: " + path.getFileName().toString());
                            e.printStackTrace();
                        }
                    });
                // Leer los Ids del contenido de los archivos
                int[] maxIdArray = {0};
                Files.list(Paths.get(TICKETS_FOLDER))
                    .filter(path -> path.getFileName().toString().endsWith(fechaStr))
                    .forEach(path -> {
                        try {
                            String content = new String(Files.readAllBytes(path));
                            JSONObject obj = new JSONObject(content);
                            int id = obj.getInt("id");
                            if (id > maxIdArray[0]) {
                                maxIdArray[0] = id;
                            }
                        } catch (Exception e) {
                            System.err.println("error leyendo ticket: " + path.getFileName().toString());
                            e.printStackTrace();
                        }
                    });
                maxId = maxIdArray[0];
            }
            return maxId;
       } catch (Exception e) {
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

            String content = new String(Files.readAllBytes(Paths.get(nombreArchivo)));
            JSONObject obj = new JSONObject(content);

            Ticket ticket = new Ticket();
            ticket.setFechaReporte(LocalDate.parse(obj.getString("fecha_reporte")));
            ticket.setTicketId(obj.getInt("id"));
            ticket.setNombreCliente(obj.getString("nombre_cliente"));
            ticket.setCorreoCliente(obj.getString("correo_cliente"));
            ticket.setTiempoEmision(Instant.parse(obj.getString("tiempo_emision")));
            ticket.setTotalTicket(obj.getFloat("total_ticket"));

            if (obj.has("servicios")) {
                JSONArray serviciosArray = obj.getJSONArray("servicios");
                List<Servicio> servicios = cargarServicios(serviciosArray);
                ticket.setServicios(servicios);
            }

            return ticket;
        } catch (Exception e) {
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
    private List<Servicio> cargarServicios(JSONArray serviciosArray) {
        List<Servicio> servicios = new ArrayList<>();

        try {
            for (int i = 0; i < serviciosArray.length(); i++) {
                JSONObject servicioObj = serviciosArray.getJSONObject(i);

                int servicioId = servicioObj.getInt("id");
                String tipoServicio = servicioObj.getString("tipo_servicio");
                boolean descuento = servicioObj.getBoolean("descuento");
                float totalServicio = servicioObj.getFloat("total_servicio");

                TipoServicio tipo = null;
                
                if ("Renta".equals(tipoServicio)) {
                    JSONObject rentaProps = servicioObj.getJSONObject("renta_properties");
                    tipo = cargarRenta(rentaProps);
                } else if ("Venta".equals(tipoServicio)) {
                    JSONObject ventaProps = servicioObj.getJSONObject("venta_properties");
                    tipo = cargarVenta(ventaProps);
                }

                if (tipo != null) {
                    Servicio servicio = new Servicio(servicioId, tipo, descuento);
                    servicio.setTotalServicio(totalServicio);
                    servicios.add(servicio);
                }
            }
        } catch (Exception e) {
            System.err.println("Error al cargar servicios: " + e.getMessage());
            e.printStackTrace();
        }
        return servicios;
    }

    /**
     * Carga una renta desde el JSON de tickets.
     * @param rentaProps
     * @return Renta
     */

    private Renta cargarRenta(JSONObject rentaProps) {
        try {
            Renta renta = new Renta();
            renta.setNombre(rentaProps.getString("nombre"));
            renta.setPrecioRenta(rentaProps.getFloat("precio"));
            renta.setCantidad(rentaProps.getInt("cantidad"));
            renta.setInicioRenta(Instant.parse(rentaProps.getString("inicio_renta")));

            String cierreRenta = rentaProps.optString("cierre_renta", "--:--:--");
            if (!"--:--:--".equals(cierreRenta)) {
                renta.setCierreRenta(Instant.parse(cierreRenta));
            }

            renta.setStateOcupado(rentaProps.getBoolean("isActive"));
            renta.setUbicacion(Ubicacion.valueOf(rentaProps.getString("ubicacion")));

            return renta;
        } catch (Exception e) {
            System.err.println("Error al cargar renta: " + e.getMessage());
            return null;
        }
    }

    private Venta cargarVenta(JSONObject ventaProps) {
        try {
            Venta venta = new Venta();
            venta.setIdProducto(ventaProps.getInt("id_producto"));
            venta.setNombre(ventaProps.getString("nombre"));
            venta.setPrecio(ventaProps.getFloat("precio"));
            venta.setCantidad(ventaProps.getInt("cantidad"));

            // Cargar etiquetas
            if (ventaProps.has("etiquetas")) {
                JSONArray etiquetasArray = ventaProps.getJSONArray("etiquetas");
                List<String> etiquetas = new ArrayList<>();
                for (int i = 0; i < etiquetasArray.length(); i++) {
                    etiquetas.add(etiquetasArray.getString(i));
                }
                venta.setEtiquetas(etiquetas);
            }
            return venta;
        } catch (Exception e) {
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

            JSONObject obj = new JSONObject();
            obj.put("fecha_reporte", fecha.toString());
            obj.put("id", ticket.getTicketId());
            obj.put("nombre_cliente", ticket.getNombreCliente());
            obj.put("correo_cliente", ticket.getCorreoCliente());
            obj.put("tiempo_emision", ticket.getTiempoEmision().toString());
            obj.put("total_ticket", ticket.getTotalTicket());

            // Guardar solo IDs de servicios para evitar duplicados
            JSONArray servicioArray = new JSONArray();
            if (ticket.getServicios() != null) {
                for (Servicio servicio : ticket.getServicios()) {
                    JSONObject servicioObj = new JSONObject();
                    servicioObj.put("id", servicio.getServicioId());
                    servicioObj.put("total_servicio", servicio.getTotalServicio());
                    servicioObj.put("descuento", servicio.isAplicarDescuento());

                    TipoServicio tipo = servicio.getTipoServicio();
                    
                    // Determinar el tipo de servicio
                    if (tipo instanceof Renta) {
                        servicioObj.put("tipo_servicio", "Renta");
                        servicioObj.put("renta_properties", crearRentaJSON((Renta) tipo));
                    } else if (tipo instanceof Venta) {
                        servicioObj.put("tipo_servicio", "Venta");
                        servicioObj.put("venta_properties", crearVentaJSON((Venta) tipo));
                    }

                    servicioArray.put(servicioObj);
                }
            }

            obj.put("servicios", servicioArray);

            Files.write(Paths.get(nombreArchivo), obj.toString(2).getBytes());
            System.out.println("Ticket guardado en: " + nombreArchivo);

        } catch (Exception e) {
            System.err.println("Error al guardar el ticket: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Crea un objeto JOSN con las propiedades de la renta.
     * @param renta
     * @return JSONObject con las propiedades de la renta
     */
    private JSONObject crearRentaJSON(Renta renta) {
        JSONObject rentaProps = new JSONObject();
        rentaProps.put("nombre", renta.getNombre());
        rentaProps.put("precio", renta.getPrecio());
        rentaProps.put("cantidad", renta.getCantidad());
        rentaProps.put("inicio_renta", renta.getInicioRenta().toString());
        rentaProps.put("cierre_renta", renta.getCierreRenta() != null ? 
                        renta.getCierreRenta().toString() : "--:--:--");    
        rentaProps.put("isActive", renta.getStateOcupado());
        rentaProps.put("ubicacion", renta.getUbicacion().name());
        return rentaProps;
    }

    /**
     * Crea un objeto JSON con las propiedades de la venta.
     * @param venta
     * @return JSONObject con las propiedades de la venta
     */
    private JSONObject crearVentaJSON(Venta venta) {
        JSONObject ventaProps = new JSONObject();
        ventaProps.put("id_producto", venta.getIdProducto());
        ventaProps.put("nombre", venta.getNombre());
        ventaProps.put("precio", venta.getPrecio());
        ventaProps.put("cantidad", venta.getCantidad());

        // Agregar etiquetas
        if (venta.getEtiquetas() != null) {
            ventaProps.put("etiquetas", new JSONArray(venta.getEtiquetas()));
        }
        return ventaProps;
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
        } catch (Exception e) {
            System.err.println("Error al eliminar el ticket " + ticket_id + ": " + e.getMessage());
            return false;
        }
    }
}
