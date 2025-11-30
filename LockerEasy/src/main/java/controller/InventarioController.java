package controller;

import dao.VentaDAO;
import model.Etiqueta;
import model.Venta;

import java.util.List;

public class InventarioController {
    private final VentaDAO ventaDAO;
    private EtiquetaController etiquetaController;

    public InventarioController() {
        this.ventaDAO = new VentaDAO();
    }

    public InventarioController(EtiquetaController etiquetaController) {
        this();
        this.etiquetaController = etiquetaController;
    }

    public boolean agregarProducto(String nombre, float precio, int existentes, Long etiquetaId) {
        if (nombre == null || nombre.trim().isEmpty()) {
            System.err.println("El nombre del producto no puede estar vacío");
            return false;
        }

        Etiqueta etiqueta = etiquetaController.getEtiquetaDAO().obtener(etiquetaId);
        if (etiqueta == null) {
            System.err.println("Etiqueta no encontrada: ID " + etiquetaId);
            return false;
        }
        
        boolean disponible;

        if (etiqueta.isAfectaInventario()) {
            if (existentes < 0) {
                System.err.println("Las existentes no pueden ser negativas para etiquetas que afectan inventario");
                return false;
            }

            disponible = existentes > 0;
        } else {
            if (existentes == 0) {
                existentes = 1; // Cambiar 0 a 1 automaticamente
            } else if (existentes < 0) {
                System.err.println("Las existentes no pueden ser negativas");
                return false;
            }

            disponible = true; // Siempre disponible si no afecta inventario
        }

        Venta producto = new Venta();
        producto.setNombre(nombre);
        producto.setPrecio(precio);
        producto.setExistentes(existentes);
        producto.setDisponible(disponible);
    

        try {
            ventaDAO.guardar(producto);
            System.out.println("Producto agregado: " + nombre);
            return true;
        } catch (Exception e) {
            System.err.println("Error al guardar producto: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarProducto(Long idProducto, String nuevoNombre) {
        Venta producto = ventaDAO.obtener(idProducto);
        if (producto != null) {
            producto.setNombre(nuevoNombre);
            ventaDAO.actualizar(producto);
            return true;
        }
        return false;
    }

    public boolean actualizarProducto(Long idProducto, float nuevoPrecio) {
        Venta producto = ventaDAO.obtener(idProducto);
        if (producto != null) {
            producto.setPrecio(nuevoPrecio);
            ventaDAO.actualizar(producto);
            return true;
        }
        return false;
    }

    public boolean actualizarExistencias(Long idProducto, int nuevasExistentes) {
        Venta producto = ventaDAO.obtener(idProducto);
        if (producto == null) {
            System.err.println("Producto no encontrado: " + idProducto);
            return false;
        }

        producto.setExistentes(nuevasExistentes);
        actualizarDisponibilidad(producto);

        try {
            ventaDAO.actualizar(producto);
            System.out.println("Existencias actualizadas para producto: " + idProducto);
            return true;
        } catch (Exception e) {
            System.err.println("Error al actualizar existencias: " + e.getMessage());
            return false;
        }
    }

    private void actualizarDisponibilidad(Venta producto) {
        // Si hay inventario, está disponible.
        producto.setDisponible(producto.getExistentes() > 0);
    }

    public void actualizarDisponibilidad(Venta producto, boolean disponible) {
        producto.setDisponible(disponible);
        try {
            ventaDAO.actualizar(producto);
        } catch (Exception e) {
            System.err.println("Error al actualizar disponibilidad: " + e.getMessage());
        }
    }

    public Venta buscarProducto(Long idProducto) {
        return ventaDAO.obtener(idProducto);
    }

    public List<Venta> obtenerTodosLosProductos() {
        return ventaDAO.obtenerTodas();
    }

    public List<Venta> obtenerProductosDisponibles() {
        return ventaDAO.obtenerDisponibles();
    }
    
    public boolean eliminarProducto(Long idProducto) {
        return ventaDAO.eliminar(idProducto);
    }

    public VentaDAO getVentaDAO() {
        return ventaDAO;
    }
}
