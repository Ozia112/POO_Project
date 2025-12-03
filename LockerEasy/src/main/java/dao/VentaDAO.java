package dao;

import model.Venta;
import util.AppLogger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.util.List;

public class VentaDAO {

    public void guardar(Venta venta) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        long startTime = System.currentTimeMillis();

        try {
            String productoInfo = venta.getProductoCatalogo() != null ? 
                venta.getProductoCatalogo().getNombre() : venta.getNombre();
            
            AppLogger.transaccionIniciada("guardar venta",
                "Producto: " + productoInfo + 
                " Cantidad: " + venta.getCantidad() +
                " Precio: $" + venta.getPrecio()
            );
            
            transaction = session.beginTransaction();
            session.persist(venta);
            transaction.commit();

            long duration = System.currentTimeMillis() - startTime;
            AppLogger.transaccionCommit("guardar venta", duration);

            Long ticketId = venta.getTicket() != null ? venta.getTicket().getTicketId() : null;
            AppLogger.ventaAgregada(
                venta.getTipoServicioId().intValue(),
                productoInfo,
                ticketId != null ? ticketId.intValue() : 0,
                venta.getPrecio(),
                venta.getCantidad()
            );

            // Advertencia si el producto tiene stock bajo
            if (venta.getProductoCatalogo() != null && 
                venta.getProductoCatalogo().getEtiqueta().isAfectaInventario()) {
                int existentesActuales = venta.getProductoCatalogo().getExistentes();
                if (existentesActuales < 5 && existentesActuales > 0) {
                    AppLogger.inventarioBajo(productoInfo, existentesActuales, 5);
                }
            }

        } catch (Exception e) {
            if (transaction != null && transaction.getStatus().canRollback()) {
                transaction.rollback();
                AppLogger.transaccionRollback("guardar venta", e.getMessage());
            }
            AppLogger.errorDBDetallado("guardar", "Venta", 
                venta.getTipoServicioId() != null ? venta.getTipoServicioId().intValue() : null, e);
            throw new RuntimeException("Error al guardar venta", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public void actualizar(Venta venta) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        long startTime = System.currentTimeMillis();

        try {
            String productoInfo = venta.getProductoCatalogo() != null ? 
                venta.getProductoCatalogo().getNombre() : venta.getNombre();
            
            AppLogger.transaccionIniciada("actualizar venta",
                "ID: " + venta.getTipoServicioId() + " Producto: " + productoInfo
            );

            transaction = session.beginTransaction();
            session.merge(venta);
            transaction.commit();

            long duration = System.currentTimeMillis() - startTime;
            AppLogger.transaccionCommit("actualizar venta", duration);

            AppLogger.info("Venta actualizada ID: {} Producto: [{}] Cantidad: {} Total: ${}",
                venta.getTipoServicioId(), 
                productoInfo, 
                venta.getCantidad(), 
                venta.getTotal()
            );

            // Verificar stock después de actualizar
            if (venta.getProductoCatalogo() != null && 
                venta.getProductoCatalogo().getEtiqueta().isAfectaInventario()) {
                int existentesActuales = venta.getProductoCatalogo().getExistentes();
                if (existentesActuales < 5 && existentesActuales > 0) {
                    AppLogger.inventarioBajo(productoInfo, existentesActuales, 5);
                } else if (existentesActuales == 0) {
                    AppLogger.warningDatosInconsistentes("ProductoCatalogo",
                        venta.getProductoCatalogo().getId().intValue(),
                        "Producto sin existencias después de venta: " + productoInfo
                    );
                }
            }

        } catch (Exception e) {
            if (transaction != null && transaction.getStatus().canRollback()) {
                transaction.rollback();
                AppLogger.transaccionRollback("actualizar venta", e.getMessage());
            }
            AppLogger.errorDBDetallado("actualizar", "Venta",
                venta.getTipoServicioId().intValue(), e);
            throw new RuntimeException("Error al actualizar venta", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public Venta obtener(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Venta venta = session.get(Venta.class, id);
            if (venta == null) {
                AppLogger.warningDatosInconsistentes("Venta", id.intValue(),
                    "No existe venta con este ID");
            }
            return venta;
        }
    }

    public List<Venta> obtenerPorTicket(Long ticketId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Venta v WHERE v.ticket.ticket_id = :ticketId";
            Query<Venta> query = session.createQuery(hql, Venta.class);
            query.setParameter("ticketId", ticketId);
            List<Venta> ventas = query.list();
            
            AppLogger.debug("Obtenidas {} ventas para ticket ID: {}", ventas.size(), ticketId);
            return ventas;
        }
    }

    public List<Venta> obtenerPorProducto(Long productoId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Venta v WHERE v.productoCatalogo.id = :productoId";
            Query<Venta> query = session.createQuery(hql, Venta.class);
            query.setParameter("productoId", productoId);
            List<Venta> ventas = query.list();
            
            AppLogger.debug("Obtenidas {} ventas del producto ID: {}", ventas.size(), productoId);
            return ventas;
        }
    }

    public boolean eliminar(Long id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        
        try {
            transaction = session.beginTransaction();
            Venta venta = session.get(Venta.class, id);
            
            if (venta != null) {
                String productoInfo = venta.getProductoCatalogo() != null ? 
                    venta.getProductoCatalogo().getNombre() : venta.getNombre();
                Long ticketId = venta.getTicket() != null ? venta.getTicket().getTicketId() : null;
                
                session.remove(venta);
                transaction.commit();

                AppLogger.ventaEliminada(
                    id.intValue(), 
                    productoInfo, 
                    ticketId != null ? ticketId.intValue() : 0
                );
                return true;
            } else {
                AppLogger.warningDatosInconsistentes("Venta", id.intValue(),
                    "Intento de eliminar venta inexistente");
                return false;
            }
        } catch (Exception e) {
            if (transaction != null && transaction.getStatus().canRollback()) {
                transaction.rollback();
                AppLogger.transaccionRollback("eliminar venta", e.getMessage());
            }
            AppLogger.errorDBDetallado("eliminar", "Venta", id.intValue(), e);
            throw new RuntimeException("Error al eliminar venta", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
}