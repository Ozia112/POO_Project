package controller;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import model.Servicio;
import model.Ticket;
import model.Venta;

public class VentaController {
    private static final String PRODUCTOS_FILE = "src/main/resources/data/catalogo/productos.json";
    
    private final Map<Integer, Venta> catalogo;
    private final EtiquetaController etiquetaController;
    private int contadorIds;

    public VentaController() {
        this.catalogo = new HashMap<>();
        this.etiquetaController = new EtiquetaController();
        cargarProductos();
    }
    
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

            String content = new String(Files.readAllBytes(Paths.get(PRODUCTOS_FILE)));
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
                    switch (etiquetasObj) {
                        case JSONArray etiquetasArr -> {
                            for (int j = 0; j < etiquetasArr.length(); j++) {
                                etiquetas.add(etiquetasArr.getString(j));
                            }
                        }
                        case String etiquetaStr -> etiquetas.add(etiquetaStr);
                        default -> {
                            // Tipo no soportado
                        }
                    }
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
    }

    public boolean agregarProducto(String nombre, float precio, int existentes, List<String> etiquetas) {
        if (nombre == null || nombre.trim().isEmpty()) {
            System.err.println("El nombre del producto no puede estar vacío");
            return false;
        }

        int nuevoId = contadorIds++;

        boolean disponible = existentes > 0 || !etiquetaController.etiquetasAfectanInventario(etiquetas);

        Venta producto = new Venta(nuevoId, nombre, precio, existentes, etiquetas, disponible);
        catalogo.put(nuevoId, producto);

        guardarProductos();
        System.out.println("Producto agregado: " + nombre + " (ID: " + nuevoId + ")");
        return true;
    }

    public boolean actualizarProducto(int idProducto, String nuevoNombre) {
        Venta producto = catalogo.get(idProducto);
        if (producto == null) {
            System.err.println("Producto no encontrado: " + idProducto);
            return false;
        }

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

        // Validar inventario si aplica
        if (etiquetaController.etiquetasAfectanInventario(producto.getEtiquetas())) {
            if (producto.getExistentes() < cantidad) {
                System.err.println("Inventario insuficiente. Disponible: " + producto.getExistentes());
                return false;
            }

            producto.setExistentes(producto.getExistentes() - cantidad);
            actualizarDisponibilidad(producto);
            guardarProductos();
        }

        // -----------------------------------------------
        // 🔥 Crear objeto VENTA para el ticket
        // -----------------------------------------------
        Venta ventaServicio = new Venta(
                producto.getIdProducto(),
                producto.getNombre(),
                producto.getPrecio(),
                cantidad,
                producto.getEtiquetas(),
                producto.isDisponible()
        );

        ventaServicio.setCantidad(cantidad);

        // Crear Servicio y agregar al Ticket
        Servicio servicio = new Servicio();
        servicio.setServicioId(ticket.getServicios().size() + 1);
        servicio.setTipoServicio(ventaServicio);
        servicio.setAplicarDescuento(false);

        ticket.getServicios().add(servicio);

        // Guardar Ticket
        ticketController.guardarTicket(ticket);

        System.out.println("Venta registrada y agregada al ticket: " 
            + producto.getNombre() + " x" + cantidad);

        return true;
    }



    public Venta buscarProdcuto(int idProducto) {
        return catalogo.get(idProducto);
    }

    public Venta buscarProducto(String nombre) {
        for (Venta producto : catalogo.values()) {
            if (producto.getNombre().equalsIgnoreCase(nombre)) {
                return producto;
            }
        }
        return null;
    }

    public List<Venta> obtenerTodosLosProductos() {
        return new ArrayList<>(catalogo.values());
    }

    public List<Venta> obtenerProductosDisponibles() {
        List<Venta> disponibles = new ArrayList<>();
        for (Venta producto : catalogo.values()) {
            if (producto.isDisponible()) {
                disponibles.add(producto);
            }
        }
        return disponibles;
    }
    
    /**
     * Elimina un producto del catálogo
     */
    public boolean eliminarProducto(int idProducto) {

        if (!catalogo.containsKey(idProducto)) {
            System.err.println("Producto no encontrado: " + idProducto);
            return false;
        }

        catalogo.remove(idProducto);
        guardarProductos();
        System.out.println("Producto eliminado: " + idProducto);
        return true;
    }

    private void guardarProductos() {
        try {
            Files.createDirectories(Paths.get(PRODUCTOS_FILE).getParent());

            JSONArray arr = new JSONArray();
            for (Venta p : catalogo.values()) {
                JSONObject obj = new JSONObject();
                obj.put("id", p.getIdProducto());
                obj.put("nombre", p.getNombre());
                obj.put("precio", p.getPrecio());
                obj.put("existentes", p.getExistentes());
                obj.put("disponible", p.isDisponible());
                obj.put("etiquetas", new JSONArray(p.getEtiquetas()));
                arr.put(obj);
            }

            Files.write(Paths.get(PRODUCTOS_FILE), arr.toString(2).getBytes());
            System.out.println("Productos guardados correctamente.");

        } catch (java.io.IOException | org.json.JSONException e) {
            System.err.println("Error al guardar productos: " + e.getMessage());
        }
    }

    private void crearArchivoVacio() {
        try {
            Files.createDirectories(Paths.get(PRODUCTOS_FILE).getParent());
            JSONArray arr = new JSONArray();
            Files.write(Paths.get(PRODUCTOS_FILE), arr.toString(2).getBytes());
            System.out.println("Archivo de productos creado.");
        } catch (java.io.IOException e) {
            System.err.println("Error al crear archivo de productos: " + e.getMessage());
        }
    }
}
