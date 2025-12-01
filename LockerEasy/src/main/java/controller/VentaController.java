package controller;

<<<<<<< HEAD
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

=======
import dao.VentaDAO;
import dao.ServicioDAO;
import model.Etiqueta;
>>>>>>> temp.TM-01.Design.DATABASE-WIP
import model.Servicio;
import model.Ticket;
import model.Venta;
import java.util.List;

public class VentaController {
    
    private final ServicioDAO servicioDAO;
    private TicketController ticketController;
    private ReporteController reporteController;
    private final InventarioController inventarioController;

    
    public VentaController() {
        this.servicioDAO = new ServicioDAO();
        this.inventarioController = new InventarioController();
    }

    
<<<<<<< HEAD
    /**
     * Carga el catálogo de productos en memoria
     */
    private void cargarProductos() {
        catalogo.clear();
        int maxId = 0;

        try {
            if (!Files.exists(Paths.get(PRODUCTOS_FILE))) {
                crearArchivoVacio();
                return;
            }

            String content = new String(Files.readAllBytes(Paths.get(PRODUCTOS_FILE)), "UTF-8");
            JSONArray arr = new JSONArray(content);

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);

                int id = obj.getInt("id");
                String nombre = obj.getString("nombre");
                float precio = obj.getFloat("precio");
                int existentes = obj.getInt("existentes");
                boolean disponible = obj.getBoolean("disponible");

                List<String> etiquetas = new ArrayList<>();
                if (obj.has("etiquetas")) {
                    Object etiquetasObj = obj.get("etiquetas");
                    if (etiquetasObj instanceof JSONArray) {
                        JSONArray etiquetasArr = (JSONArray) etiquetasObj;
                        for (int j = 0; j < etiquetasArr.length(); j++) {
                            etiquetas.add(etiquetasArr.getString(j));
                        }
                    } else if (etiquetasObj instanceof String) {
                        etiquetas.add((String) etiquetasObj);
                    }
                    // Si es otro tipo, simplemente no agregamos etiquetas
                }

                Venta producto = new Venta(id, nombre, precio, existentes, etiquetas, disponible);
                catalogo.put(id, producto);

                if (id > maxId) maxId = id;
            }

            contadorIds = maxId + 1;
            System.out.println("Productos cargados: " + catalogo.size());

        } catch (java.io.IOException | org.json.JSONException e) {
            System.err.println("Error al cargar productos: " + e.getMessage());
        }
=======
    public VentaController(TicketController ticketController, ReporteController reporteController) {
        this();
        this.ticketController = ticketController;
        this.reporteController = reporteController;
>>>>>>> temp.TM-01.Design.DATABASE-WIP
    }

    //agrega nuevo producto al catalogo
    

    //Regiustra la venta de un producto
    public boolean registrarVenta(Long idProducto, int cantidad, Ticket ticket) {
        
        Venta producto = inventarioController.getVentaDAO().obtener(idProducto);
        
        if (producto == null) {
            System.err.println("Producto no encontrado: " + idProducto);
            return false;
        }

<<<<<<< HEAD
        producto.setNombre(nuevoNombre);
        guardarProductos();
        System.out.println("Producto actualizado: " + idProducto);
        return true;
    }

    public boolean actualizarProducto(int idProducto, float nuevoPrecio) {
        Venta producto = catalogo.get(idProducto);
        if (producto == null) {
            System.err.println("Producto no encontrado: " + idProducto);
            return false;
        }

        producto.setPrecio(nuevoPrecio);
        guardarProductos();
        System.out.println("Producto actualizado: " + idProducto);
        return true;
    }

    public boolean actualizarProducto(int idProducto, int nuevasExistentes) {
        Venta producto = catalogo.get(idProducto);
        if (producto == null) {
            System.err.println("Producto no encontrado: " + idProducto);
            return false;
        }

        producto.setExistentes(nuevasExistentes);
        guardarProductos();
        System.out.println("Producto actualizado: " + idProducto);
        return true;
    }

    public boolean actualizarProdcuto(int idProducto, List<String> nuevasEtiquetas) {
        Venta producto = catalogo.get(idProducto);
        if (producto == null) {
            System.err.println("Producto no encontrado: " + idProducto);
            return false;
        }

        producto.setEtiquetas(nuevasEtiquetas);
        actualizarDisponibilidad(producto);

        guardarProductos();
        System.out.println("Producto actualizado: " + idProducto);
        return true;
    }

    public boolean actualizarProducto(int idProducto, boolean disponible) {
        Venta producto = catalogo.get(idProducto);
        if (producto == null) {
            System.err.println("Producto no encontrado: " + idProducto);
            return false;
        }

        producto.setDisponible(disponible);
        guardarProductos();
        System.out.println("Producto actualizado: " + idProducto);
        return true;
    }

    private void actualizarDisponibilidad(Venta producto) {
        if (etiquetaController.etiquetasAfectanInventario(producto.getEtiquetas())) {
            producto.setDisponible(producto.getExistentes() > 0);
        } else {
            producto.setDisponible(true);
        }
    }
        public boolean registrarVenta(int idProducto, int cantidad, Ticket ticket, TicketController ticketController) {

            Venta producto = catalogo.get(idProducto);
            if (producto == null) {
                System.err.println("Producto no encontrado: " + idProducto);
                return false;
            }

            if (!producto.isDisponible()) {
                System.err.println("Producto no disponible: " + producto.getNombre());
                return false;
            }

            // Inventario si aplica
            if (etiquetaController.etiquetasAfectanInventario(producto.getEtiquetas())) {
                if (producto.getExistentes() < cantidad) {
                    System.err.println("Inventario insuficiente. Disponible: " + producto.getExistentes());
                    return false;
                }

                producto.setExistentes(producto.getExistentes() - cantidad);
                actualizarDisponibilidad(producto);
                guardarProductos();
            }

            // ---------------------------------------------
            // *** CREAR OBJETO SERVICIO ***
            // ---------------------------------------------
            Servicio servicio = new Servicio();
            servicio.setServicioId(ticket.getServicios().size() + 1);
            servicio.setTipoServicio(producto);       // TipoServicio = Venta
            producto.setCantidad(cantidad);           // Aplicar cantidad a la venta
            servicio.setAplicarDescuento(false);

            // ---------------------------------------------
            // AGREGAR AL TICKET
            // ---------------------------------------------
            ticket.getServicios().add(servicio);

            // ---------------------------------------------
            // RE-CALCULAR TOTAL DEL TICKET
            // ---------------------------------------------
            float total = 0f;
            for (Servicio s : ticket.getServicios()) {
                total += s.getTotalServicio();
            }
            ticket.setTotalTicket(total);

            // ---------------------------------------------
            // GUARDAR
            // ---------------------------------------------
            ticketController.guardarTicket(ticket);

            System.out.println("Venta registrada y agregada al ticket: " + producto.getNombre() + " x" + cantidad);
            return true;
        }



    public Venta buscarProdcuto(int idProducto) {
        return catalogo.get(idProducto);
    }

    public Venta buscarProducto(String nombre) {
        for (Venta producto : catalogo.values()) {
            if (producto.getNombre().equalsIgnoreCase(nombre)) {
                return producto;
=======
        if (!producto.isDisponible()) {
            System.err.println("Producto no disponible: " + producto.getNombre());
            return false;
        }

        boolean afectaInventario = producto.getEtiqueta().isAfectaInventario();
        try {
            if (afectaInventario) {
                if (producto.getExistentes() < cantidad) {
                    System.err.println("Inventario insuficiente. Disponible: " + producto.getExistentes());
                    return false;
                }

                // Restar inventario
                producto.setExistentes(producto.getExistentes() - cantidad);
                producto.setDisponible(producto.getExistentes() > 0);
                inventarioController.getVentaDAO().actualizar(producto);
            }

            // Crear el servicio para ticket
            Servicio servicio = new Servicio();
            servicio.setTipoServicio(producto); // El producto es el tipo de servicio
            servicio.setAplicarDescuento(false);
            // el precio del servicio se calcula automáticamente en base al producto

            
            if (ticketController != null) {
                
                ticketController.agregarServicio(ticket, producto);
                ticketController.calcularTotalTicket(ticket);
                ticketController.getTicketDAO().actualizar(ticket);
>>>>>>> temp.TM-01.Design.DATABASE-WIP
            }

            if (reporteController != null) {
                reporteController.agregarTicket(ticket);
                reporteController.recalcularTotal();
                reporteController.getReporteDAO().actualizar(reporteController.getReporteActual());
            }

            System.out.println("Venta registrada: " + producto.getNombre());
            return true;
        } catch (Exception e) {
            System.err.println("Error al registrar venta: " + e.getMessage());
            return false;
        }
    }

    public ServicioDAO getServicioDAO() {
        return servicioDAO;
    }
}