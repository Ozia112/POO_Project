package backend.controllers;

import backend.model.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Controller para gestionar las ventas de productos
 * Cumple con FR4
 */
public class VentaController {
    private Map<Integer, Venta> ventas;
    private Map<String, Float> catalogo; // Productos y sus precios
    private int siguienteIdVenta;

    public VentaController() {
        this.ventas = new HashMap<>();
        this.catalogo = new HashMap<>();
        this.siguienteIdVenta = 1;
        inicializarCatalogo();
    }

    // Inicializa un catálogo de productos de ejemplo
    private void inicializarCatalogo() {
        catalogo.put("Candado", 150.0f);
        catalogo.put("Agua", 15.0f);
        catalogo.put("Snack", 25.0f);
        catalogo.put("Bebida Energética", 35.0f);
        catalogo.put("Cargador USB", 200.0f);
    }

    /**
     * FR4: Registrar una venta y vincularla a un cliente con renta activa
     * @param nombreCliente Nombre del cliente (debe tener renta activa)
     * @param productosVendidos Lista de productos vendidos
     * @return La venta creada o null si no se pudo crear
     */
    public Venta registrarVenta(String nombreCliente, List<String> productosVendidos) {
        if (productosVendidos == null || productosVendidos.isEmpty()) {
            System.out.println("Error: No se especificaron productos para la venta.");
            return null;
        }

        // Calcular el total
        float precioTotal = 0;
        for (String producto : productosVendidos) {
            Float precio = catalogo.get(producto);
            if (precio == null) {
                System.out.println("Advertencia: Producto '" + producto + "' no encontrado en catálogo.");
            } else {
                precioTotal += precio;
            }
        }

        if (precioTotal == 0) {
            System.out.println("Error: Ningún producto válido en la venta.");
            return null;
        }

        // Crear la venta
        Venta venta = new Venta(
            siguienteIdVenta++,
            nombreCliente,
            LocalDateTime.now(),
            precioTotal,
            productosVendidos.size(),
            productosVendidos
        );

        ventas.put(venta.getCantidad(), venta); // Usando cantidad como ID temporal
        
        System.out.println("Venta registrada exitosamente:");
        System.out.println("- Cliente: " + nombreCliente);
        System.out.println("- Productos: " + productosVendidos);
        System.out.println("- Total: $" + venta.calcularTotalVenta());
        System.out.println("- Fecha: " + venta.getFecha());

        return venta;
    }

    /**
     * FR5: Actualizar precio de un producto
     * @param nombreProducto Nombre del producto
     * @param nuevoPrecio Nuevo precio
     * @return true si se actualizó correctamente
     */
    public boolean actualizarPrecioProducto(String nombreProducto, float nuevoPrecio) {
        if (nuevoPrecio <= 0) {
            System.out.println("Error: El precio debe ser mayor a 0.");
            return false;
        }

        Float precioAnterior = catalogo.get(nombreProducto);
        if (precioAnterior == null) {
            System.out.println("Producto no encontrado. Agregando al catálogo...");
        }

        catalogo.put(nombreProducto, nuevoPrecio);
        System.out.println("Precio actualizado:");
        System.out.println("- Producto: " + nombreProducto);
        if (precioAnterior != null) {
            System.out.println("- Precio anterior: $" + precioAnterior);
        }
        System.out.println("- Precio nuevo: $" + nuevoPrecio);

        return true;
    }

    /**
     * Agregar un nuevo producto al catálogo
     * @param nombreProducto Nombre del producto
     * @param precio Precio del producto
     * @return true si se agregó correctamente
     */
    public boolean agregarProducto(String nombreProducto, float precio) {
        if (catalogo.containsKey(nombreProducto)) {
            System.out.println("Error: El producto ya existe. Use actualizarPrecioProducto() para modificarlo.");
            return false;
        }
        return actualizarPrecioProducto(nombreProducto, precio);
    }

    /**
     * Eliminar un producto del catálogo
     * @param nombreProducto Nombre del producto
     * @return true si se eliminó correctamente
     */
    public boolean eliminarProducto(String nombreProducto) {
        if (catalogo.remove(nombreProducto) != null) {
            System.out.println("Producto '" + nombreProducto + "' eliminado del catálogo.");
            return true;
        }
        System.out.println("Error: Producto no encontrado.");
        return false;
    }

    /**
     * Obtener el catálogo completo de productos
     * @return Mapa de productos y precios
     */
    public Map<String, Float> obtenerCatalogo() {
        return new HashMap<>(catalogo);
    }

    /**
     * Obtener todas las ventas registradas
     * @return Lista de ventas
     */
    public List<Venta> obtenerVentas() {
        return new ArrayList<>(ventas.values());
    }

    /**
     * Obtener ventas de un cliente específico
     * @param nombreCliente Nombre del cliente
     * @return Lista de ventas del cliente
     */
    public List<Venta> obtenerVentasPorCliente(String nombreCliente) {
        List<Venta> ventasCliente = new ArrayList<>();
        for (Venta venta : ventas.values()) {
            if (venta.getNombre().equalsIgnoreCase(nombreCliente)) {
                ventasCliente.add(venta);
            }
        }
        return ventasCliente;
    }

    /**
     * Mostrar el catálogo de productos
     */
    public void mostrarCatalogo() {
        System.out.println("\n=== CATÁLOGO DE PRODUCTOS ===");
        catalogo.forEach((producto, precio) -> 
            System.out.println("- " + producto + ": $" + precio)
        );
    }
}
