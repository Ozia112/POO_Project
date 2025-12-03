package controller;

import dao.ProductoCatalogoDAO;
import model.Etiqueta;
import model.ProductoCatalogo;
import java.util.List;

public class InventarioController {
    private final ProductoCatalogoDAO productoCatalogoDAO;
    private final EtiquetaController etiquetaController;

    public InventarioController() {
        this.productoCatalogoDAO = new ProductoCatalogoDAO();
        this.etiquetaController = new EtiquetaController();
    }

    public boolean agregarProducto(String nombre, float precio, int existentes, Long etiquetaId) {
        if (nombre == null || nombre.trim().isEmpty()) {
            System.err.println("El nombre del producto no puede estar vacío");
            return false;
        }

        if (precio < 0) {
            System.err.println("El precio no puede ser negativo");
            return false;
        }

        Etiqueta etiqueta = etiquetaController.getEtiquetaDAO().obtener(etiquetaId);
        if (etiqueta == null) {
            System.err.println("Etiqueta no encontrada: ID " + etiquetaId);
            return false;
        }
        
        // Validar existencias según la etiqueta
        if (etiqueta.isAfectaInventario()) {
            if (existentes < 0) {
                System.err.println("Las existencias no pueden ser negativas");
                return false;
            }
        } else {
            // Servicios sin inventario siempre tienen existencias = 1
            existentes = 1;
        }

        ProductoCatalogo producto = new ProductoCatalogo(nombre, precio, existentes, etiqueta);

        try {
            productoCatalogoDAO.guardar(producto);
            System.out.println("Producto agregado al catálogo: " + nombre);
            return true;
        } catch (Exception e) {
            System.err.println("Error al guardar producto: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarNombre(Long idProducto, String nuevoNombre) {
        if (nuevoNombre == null || nuevoNombre.trim().isEmpty()) {
            System.err.println("El nombre no puede estar vacío");
            return false;
        }

        ProductoCatalogo producto = productoCatalogoDAO.obtener(idProducto);
        if (producto == null) {
            System.err.println("Producto no encontrado: " + idProducto);
            return false;
        }

        producto.setNombre(nuevoNombre);
        
        try {
            productoCatalogoDAO.actualizar(producto);
            System.out.println("Nombre actualizado para producto ID: " + idProducto);
            return true;
        } catch (Exception e) {
            System.err.println("Error al actualizar nombre: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarPrecio(Long idProducto, float nuevoPrecio) {
        if (nuevoPrecio < 0) {
            System.err.println("El precio no puede ser negativo");
            return false;
        }

        ProductoCatalogo producto = productoCatalogoDAO.obtener(idProducto);
        if (producto == null) {
            System.err.println("Producto no encontrado: " + idProducto);
            return false;
        }

        producto.setPrecio(nuevoPrecio);
        
        try {
            productoCatalogoDAO.actualizar(producto);
            System.out.println("Precio actualizado para producto ID: " + idProducto);
            return true;
        } catch (Exception e) {
            System.err.println("Error al actualizar precio: " + e.getMessage());
            return false;
        }
    }

    /**
     * Actualiza las existencias del producto y su disponibilidad
     * Solo aplica para productos que afectan inventario
     */
    public boolean actualizarExistencias(Long idProducto, int nuevasExistencias) {
        if (nuevasExistencias < 0) {
            System.err.println("Las existencias no pueden ser negativas");
            return false;
        }

        ProductoCatalogo producto = productoCatalogoDAO.obtener(idProducto);
        if (producto == null) {
            System.err.println("Producto no encontrado: " + idProducto);
            return false;
        }

        // Solo actualizar si afecta inventario
        if (!producto.getEtiqueta().isAfectaInventario()) {
            System.err.println("Este producto no maneja inventario");
            return false;
        }

        producto.setExistentes(nuevasExistencias);
        producto.setDisponible(nuevasExistencias > 0);

        try {
            productoCatalogoDAO.actualizar(producto);
            System.out.println("Existencias actualizadas para producto: " + producto.getNombre());
            return true;
        } catch (Exception e) {
            System.err.println("Error al actualizar existencias: " + e.getMessage());
            return false;
        }
    }

    /**
     * Reduce las existencias por una venta
     * IMPORTANTE: Solo llamar desde VentaController
     */
    public boolean reducirExistencias(Long idProducto, int cantidad) {
        ProductoCatalogo producto = productoCatalogoDAO.obtener(idProducto);
        if (producto == null) {
            System.err.println("Producto no encontrado: " + idProducto);
            return false;
        }

        if (!producto.getEtiqueta().isAfectaInventario()) {
            // No reducir existencias para productos sin inventario
            return true;
        }

        int existenciasActuales = producto.getExistentes();
        if (existenciasActuales < cantidad) {
            System.err.println("Inventario insuficiente. Disponible: " + existenciasActuales);
            return false;
        }

        producto.setExistentes(existenciasActuales - cantidad);
        producto.setDisponible(producto.getExistentes() > 0);

        try {
            productoCatalogoDAO.actualizar(producto);
            System.out.println("Existencias reducidas: " + producto.getNombre() + 
                             " (Restantes: " + producto.getExistentes() + ")");
            return true;
        } catch (Exception e) {
            System.err.println("Error al reducir existencias: " + e.getMessage());
            return false;
        }
    }

    /**
     * Aumenta las existencias (revertir una venta/devolución)
     * IMPORTANTE: Llamar al eliminar un ticket con ventas que afectan inventario
     */
    public boolean aumentarExistencias(Long idProducto, int cantidad) {
        ProductoCatalogo producto = productoCatalogoDAO.obtener(idProducto);
        if (producto == null) {
            System.err.println("Producto no encontrado para devolución: " + idProducto);
            return false;
        }

        if (!producto.getEtiqueta().isAfectaInventario()) {
            // No aumentar existencias para productos sin inventario
            return true;
        }

        int existenciasActuales = producto.getExistentes();
        producto.setExistentes(existenciasActuales + cantidad);
        producto.setDisponible(true); // Si devolvemos productos, ya está disponible

        try {
            productoCatalogoDAO.actualizar(producto);
            System.out.println("Existencias restauradas: " + producto.getNombre() + 
                             " (Nuevas existencias: " + producto.getExistentes() + ")");
            return true;
        } catch (Exception e) {
            System.err.println("Error al restaurar existencias: " + e.getMessage());
            return false;
        }
    }

    public boolean cambiarDisponibilidad(Long idProducto, boolean disponible) {
        ProductoCatalogo producto = productoCatalogoDAO.obtener(idProducto);
        if (producto == null) {
            System.err.println("Producto no encontrado: " + idProducto);
            return false;
        }

        producto.setDisponible(disponible);
        
        try {
            productoCatalogoDAO.actualizar(producto);
            System.out.println("Disponibilidad actualizada para: " + producto.getNombre());
            return true;
        } catch (Exception e) {
            System.err.println("Error al cambiar disponibilidad: " + e.getMessage());
            return false;
        }
    }

    public ProductoCatalogo buscarProducto(Long idProducto) {
        return productoCatalogoDAO.obtener(idProducto);
    }

    public List<ProductoCatalogo> obtenerTodosLosProductos() {
        return productoCatalogoDAO.obtenerTodos();
    }

    public List<ProductoCatalogo> obtenerProductosDisponibles() {
        return productoCatalogoDAO.obtenerDisponibles();
    }
    
    public boolean eliminarProducto(Long idProducto) {
        return productoCatalogoDAO.eliminar(idProducto);
    }

    public ProductoCatalogoDAO getProductoCatalogoDAO() {
        return productoCatalogoDAO;
    }
}