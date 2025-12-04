package dao;

import model.Ubicacion;
import util.AppLogger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class UbicacionDAO {
    
    public void guardar(Ubicacion ubicacion) {
        Transaction transaction = null;
        Session session = session = HibernateUtil.getSessionFactory().openSession();
        long startTime = System.currentTimeMillis();
        
        try {
            AppLogger.transaccionIniciada("guardar ubicacion",
                "Torre: " + ubicacion.getNombreTorre() + 
                " Locker: " + ubicacion.getNombreLocker());
            
            transaction = session.beginTransaction();
            session.persist(ubicacion);
            transaction.commit();
            
            long duration = System.currentTimeMillis() - startTime;
            AppLogger.transaccionCommit("guardar ubicacion", duration);
            
            AppLogger.ubicacionCreada(
                ubicacion.getUbicacionId().intValue(),
                ubicacion.getNombreTorre(),
                ubicacion.getNombreLocker(),
                ubicacion.getLocalizacion()
            );
            
        } catch (Exception e) {
            if (transaction != null && transaction.getStatus().canRollback()) {
                transaction.rollback();
                AppLogger.transaccionRollback("guardar ubicacion", e.getMessage());
            }
            AppLogger.errorDBDetallado("guardar", "Ubicacion", 
                ubicacion.getUbicacionId() != null ? ubicacion.getUbicacionId().intValue() : null, e);
            throw new RuntimeException("Error al guardar la ubicacion", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public void actualizar(Ubicacion ubicacion) {
        Transaction transaction = null;
        Session session = session = HibernateUtil.getSessionFactory().openSession();
        long startTime = System.currentTimeMillis();
        
        try {
            // Obtener estado anterior
            Ubicacion ubicacionAnterior = session.get(Ubicacion.class, ubicacion.getUbicacionId());
            boolean disponibleAntes = ubicacionAnterior != null ? ubicacionAnterior.isDisponible() : true;
            
            String ubicacionInfo = String.format("%s - %s - %s",
                ubicacion.getNombreLocker(),
                ubicacion.getNombreTorre(),
                ubicacion.getLocalizacion()
            );
            
            AppLogger.transaccionIniciada("actualizar ubicacion",
                "ID: " + ubicacion.getUbicacionId() + " " + ubicacionInfo);
            
            transaction = session.beginTransaction();
            session.merge(ubicacion);
            transaction.commit();
            
            // Limpiar caché para esta ubicación
            HibernateUtil.getSessionFactory().getCache().evict(Ubicacion.class, ubicacion.getUbicacionId());
            
            long duration = System.currentTimeMillis() - startTime;
            AppLogger.transaccionCommit("actualizar ubicacion", duration);
            
            AppLogger.ubicacionActualizada(
                ubicacion.getUbicacionId().intValue(),
                ubicacionInfo,
                disponibleAntes,
                ubicacion.isDisponible()
            );
            
        } catch (Exception e) {
            if (transaction != null && transaction.getStatus().canRollback()) {
                transaction.rollback();
                AppLogger.transaccionRollback("actualizar ubicacion", e.getMessage());
            }
            AppLogger.errorDBDetallado("actualizar", "Ubicacion", 
                ubicacion.getUbicacionId().intValue(), e);
            throw new RuntimeException("Error al actualizar la ubicacion", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public Ubicacion obtener(Long ubicacionId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Limpiar caché para obtener datos frescos
            session.clear();
            HibernateUtil.getSessionFactory().getCache().evict(Ubicacion.class, ubicacionId);
            
            Ubicacion ubicacion = session.get(Ubicacion.class, ubicacionId);
            if (ubicacion == null) {
                AppLogger.warningDatosInconsistentes("Ubicacion", ubicacionId.intValue(), 
                    "No existe ubicación con ese ID");
            }
            return ubicacion;
        }
    }

    public List<Ubicacion> obtenerTodas() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Limpiar caché de segundo nivel para obtener datos completamente frescos
            HibernateUtil.getSessionFactory().getCache().evict(Ubicacion.class);
            session.clear();
            
            String hql = "FROM Ubicacion u ORDER BY u.nombre_torre, u.nombre_locker";
            Query<Ubicacion> query = session.createQuery(hql, Ubicacion.class);
            
            // Deshabilitar caché de consultas
            query.setCacheable(false);
            
            List<Ubicacion> ubicaciones = query.list();
            
            return ubicaciones;
        }
    }

    public List<Ubicacion> obtenerPorDisponible(boolean isDisponible) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Ubicacion u WHERE u.disponible = :disponible ORDER BY u.nombre_torre, u.nombre_locker";
            Query<Ubicacion> query = session.createQuery(hql, Ubicacion.class);
            query.setParameter("disponible", isDisponible);
            List<Ubicacion> ubicaciones = query.list();
            
            String estado = isDisponible ? "disponibles" : "ocupadas";
            AppLogger.debug("Obtenidas {} ubicaciones {}", ubicaciones.size(), estado);
            
            return ubicaciones;
        }
    }

    public List<Ubicacion> obtenerPorTorre(String nombreTorre) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Ubicacion u WHERE u.nombre_torre = :torre ORDER BY u.nombre_locker";
            Query<Ubicacion> query = session.createQuery(hql, Ubicacion.class);
            query.setParameter("torre", nombreTorre);
            List<Ubicacion> ubicaciones = query.list();
            
            AppLogger.debug("Obtenidas {} ubicaciones en torre {}", 
                ubicaciones.size(), nombreTorre);
            return ubicaciones;
        }
    }

    public List<Ubicacion> obtenerPorLocalizacion(String localizacion) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Ubicacion u WHERE u.localizacion = :loc ORDER BY u.nombre_torre, u.nombre_locker";
            Query<Ubicacion> query = session.createQuery(hql, Ubicacion.class);
            query.setParameter("loc", localizacion);
            List<Ubicacion> ubicaciones = query.list();
            
            AppLogger.debug("Obtenidas {} ubicaciones en localización {}", 
                ubicaciones.size(), localizacion);
            return ubicaciones;
        }
    }

    public boolean eliminar(Long ubicacionId) {
        Transaction transaction = null;
        Session session = session = HibernateUtil.getSessionFactory().openSession();
        
        try {
            transaction = session.beginTransaction();
            
            Ubicacion ubicacion = session.get(Ubicacion.class, ubicacionId);
            if (ubicacion != null) {
                String ubicacionInfo = String.format("%s - %s - %s",
                    ubicacion.getNombreLocker(),
                    ubicacion.getNombreTorre(),
                    ubicacion.getLocalizacion()
                );
                
                session.remove(ubicacion);
                transaction.commit();
                
                AppLogger.info("Ubicación eliminada ID: {} [{}]", 
                    ubicacionId, ubicacionInfo);
                return true;
            } else {
                AppLogger.warningDatosInconsistentes("Ubicacion", ubicacionId.intValue(), 
                    "Intento de eliminar ubicación inexistente");
                return false;
            }
        } catch (Exception e) {
            if (transaction != null && transaction.getStatus().canRollback()) {
                transaction.rollback();
            }
            AppLogger.errorDBDetallado("eliminar", "Ubicacion", ubicacionId.intValue(), e);
            throw new RuntimeException("Error al eliminar la ubicacion", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
}