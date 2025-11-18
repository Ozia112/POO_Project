package controller;

import model.Venta;

import org.json.JSONArray;
import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class VentaController {
    private static final String PRODUCTOS_FILE = "LockerEasy/src/main/resources/data/productos.json";
    private List<Venta> catalogo;   // Productos del JSON
    private List<Venta> ventasRealizadas;  // Lo que el cliente compra

    public VentaController() {
        this.catalogo = cargarProductos();
        this.ventasRealizadas = new ArrayList<>();
    }
    // Inventariado de productos desde JSON
    
    public static List<Venta> cargarProductos() {
        List<Venta> productos = new ArrayList<>();
        try {
            String content = new String(Files.readAllBytes(Paths.get(PRODUCTOS_FILE)));
            JSONArray arr = new JSONArray(content);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                int id = obj.getInt("id");
                String nombre = obj.getString("nombre");
                float precio = obj.getFloat("precio");
                int existencias = obj.getInt("existentes");
                boolean disponible = obj.getBoolean("disponible");
                List<String> etiquetas = new ArrayList<>();
                if (obj.get("etiquetas") instanceof JSONArray) {
                    JSONArray etiquetasArr = obj.getJSONArray("etiquetas");
                    for (int j = 0; j < etiquetasArr.length(); j++) {
                        etiquetas.add(etiquetasArr.getString(j));
                    }
                } else if (obj.get("etiquetas") instanceof String) {
                    etiquetas.add(obj.getString("etiquetas"));
                }
                productos.add(new Venta(id, nombre, precio, existencias, etiquetas, disponible));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return productos;
    }

    public static void guardarProductos(List<Venta> productos) {
        JSONArray arr = new JSONArray();
        for (Venta p : productos) {
            JSONObject obj = new JSONObject();
            obj.put("id", p.getId());
            obj.put("nombre", p.getNombre());
            obj.put("precio", p.getPrecio());
            obj.put("existentes", p.getExistentes());
            obj.put("disponible", p.isDisponible());
            obj.put("etiquetas", p.getEtiquetas());
            arr.put(obj);
        }
        try {
            Files.write(Paths.get(PRODUCTOS_FILE), arr.toString(2).getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Registrar una venta del cliente.
     * Verifica inventario y descuenta stock.
     */
    public boolean registrarVenta(int idProducto, int cantidad) {
        Venta producto = buscarEnProductos(idProducto);
        if (producto == null) return false;

        if (producto.getExistentes() < cantidad) return false;

        // descontar stock
        producto.setExistentes(producto.getExistentes() - cantidad);

        // crear venta realizada
        Venta venta = new Venta(
                producto.getId(),
                producto.getNombre(),
                producto.getPrecio(),
                producto.getExistentes(),
                producto.getEtiquetas(),
                producto.isDisponible()
        );
        venta.setCantidad(cantidad);

        ventasRealizadas.add(venta);
        return true;
    }

    public void actualizarPrecio(int idProducto, float nuevoPrecio) {
        Venta producto = buscarEnProductos(idProducto);
        if (producto != null) {
            producto.setPrecio(nuevoPrecio);
        }
    }

    public void actualizarNombre(int idProducto, String nuevoNombre) {
        Venta producto = buscarEnProductos(idProducto);
        if (producto != null) {
            producto.setNombre(nuevoNombre);
        }
    }

    public void actualizarPrecio(String nombreProducto, float nuevoPrecio) {
        Venta producto = buscarEnProductos(nombreProducto);
        if (producto != null) {
            producto.setPrecio(nuevoPrecio);
        }
    }

    private Venta buscarEnProductos(int idProducto) {
        for (Venta p : catalogo) {
            if (p.getId() == idProducto) {
                return p;
            }
        }
        return null;
    }

    private Venta buscarEnProductos(String nombreProducto) {
        for (Venta p : catalogo) {
            if (p.getNombre().equalsIgnoreCase(nombreProducto)) {
                return p;
            }
        }
        return null;
    }

    public List<Venta> getProductoList() { return catalogo; }
    public List<Venta> getVentasList() { return ventasRealizadas; } 
}