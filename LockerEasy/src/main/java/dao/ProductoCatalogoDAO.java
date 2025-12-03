package dao;

import model.ProductoCatalogo;
import util.AppLogger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.util.List;

public class ProductoCatalogoDAO {

    public void guardar(ProductoCatalogo producto) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        long startTime = System.currentTimeMillis();

        try {
            AppLogger.transaccionIniciada("guardar producto catálogo",
                "Producto: " + producto.getNombre() +
                " Precio: $" + producto.getPrecio() +
                " Existentes: " + producto.getExistentes()
            );
            
            transaction = session.beginTransaction();
            session.persist(producto);
            transaction.commit();

            long duration = System.currentTimeMillis() - startTime;
            AppLogger.transaccionCommit("guardar producto catálogo", duration);

            AppLogger.info("Producto catálogo agregado ID: {} Nombre: [{}] Precio: ${} Stock: {}",
                producto.getId(), producto.getNombre(), producto.getPrecio(), producto.getExistentes());

            if (producto.getExistentes() < 5 && producto.getEtiqueta().isAfectaInventario()) {
                AppLogger.inventarioBajo(producto.getNombre(), producto.getExistentes(), 5);
            }

        } catch (Exception e) {
            if (transaction != null && transaction.getStatus().canRollback()) {
                transaction.rollback();
                AppLogger.transaccionRollback("guardar producto catálogo", e.getMessage());
            }
            AppLogger.errorDBDetallado("guardar", "ProductoCatalogo", 
                producto.getId() != null ? producto.getId().intValue() : null, e);
            throw new RuntimeException("Error al guardar producto catálogo", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public void actualizar(ProductoCatalogo producto) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        long startTime = System.currentTimeMillis();

        try {
            ProductoCatalogo productoAnterior = session.get(ProductoCatalogo.class, producto.getId());
            int stockAnterior = productoAnterior != null ? productoAnterior.getExistentes() : 0;

            AppLogger.transaccionIniciada("actualizar producto catálogo",
                "ID: " + producto.getId() + " Producto: " + producto.getNombre()
            );

            transaction = session.beginTransaction();
            session.merge(producto);
            transaction.commit();

            long duration = System.currentTimeMillis() - startTime;
            AppLogger.transaccionCommit("actualizar producto catálogo", duration);

            AppLogger.info("Producto catálogo actualizado ID: {} [{}] Stock: {} -> {}",
                producto.getId(), producto.getNombre(), stockAnterior, producto.getExistentes());

            if (producto.getEtiqueta().isAfectaInventario()) {
                if (producto.getExistentes() < 5 && producto.getExistentes() > 0) {
                    AppLogger.inventarioBajo(producto.getNombre(), producto.getExistentes(), 5);
                } else if (producto.getExistentes() == 0) {
                    AppLogger.warningDatosInconsistentes("ProductoCatalogo",
                        producto.getId().intValue(),
                        "Producto sin existencias: " + producto.getNombre()
                    );
                }
            }

        } catch (Exception e) {
            if (transaction != null && transaction.getStatus().canRollback()) {
                transaction.rollback();
                AppLogger.transaccionRollback("actualizar producto catálogo", e.getMessage());
            }
            AppLogger.errorDBDetallado("actualizar", "ProductoCatalogo",
                producto.getId().intValue(), e);
            throw new RuntimeException("Error al actualizar producto catálogo", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public ProductoCatalogo obtener(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            ProductoCatalogo producto = session.get(ProductoCatalogo.class, id);
            if (producto == null) {
                AppLogger.warningDatosInconsistentes("ProductoCatalogo", id.intValue(),
                    "No existe producto con este ID");
            }
            return producto;
        }
    }

    public List<ProductoCatalogo> obtenerTodos() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<ProductoCatalogo> productos = session.createQuery(
                "FROM ProductoCatalogo", ProductoCatalogo.class).list();
            AppLogger.debug("Obtenidos {} productos catálogo", productos.size());
            return productos;
        }
    }

    public List<ProductoCatalogo> obtenerDisponibles() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM ProductoCatalogo p WHERE p.disponible = true";
            List<ProductoCatalogo> productos = session.createQuery(hql, ProductoCatalogo.class).list();
            AppLogger.debug("Obtenidos {} productos catálogo disponibles", productos.size());
            return productos;
        }
    }

    public boolean eliminar(Long id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        
        try {
            transaction = session.beginTransaction();
            ProductoCatalogo producto = session.get(ProductoCatalogo.class, id);
            
            if (producto != null) {
                String nombre = producto.getNombre();
                session.remove(producto);
                transaction.commit();

                AppLogger.info("Producto catálogo eliminado ID: {} [{}]", id, nombre);
                return true;
            } else {
                AppLogger.warningDatosInconsistentes("ProductoCatalogo", id.intValue(),
                    "Intento de eliminar producto inexistente");
                return false;
            }
        } catch (Exception e) {
            if (transaction != null && transaction.getStatus().canRollback()) {
                transaction.rollback();
                AppLogger.transaccionRollback("eliminar producto catálogo", e.getMessage());
            }
            AppLogger.errorDBDetallado("eliminar", "ProductoCatalogo", id.intValue(), e);
            throw new RuntimeException("Error al eliminar producto catálogo", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
}