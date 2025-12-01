package dao;

import model.Ubicacion;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class UbicacionDAO {
    public void guardar(Ubicacion ubicacion) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(ubicacion);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Error al guardar la ubicacion", e);
        }
    }

    public void actualizar(Ubicacion ubicacion) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(ubicacion);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Error al actualizar la ubicacion");
        }
    }

    public Ubicacion obtener(Long ubicacionId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Ubicacion.class, ubicacionId);
        }
    }

    public List<Ubicacion> obtenerTodas() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Ubicacion u ORDER BY u.nombre_torre, u.nombre_locker";
            Query<Ubicacion> query = session.createQuery(hql, Ubicacion.class);
            return query.list();
        }
    }
    //Filtrar por disponible
    public List<Ubicacion> obtenerPorDisponible(boolean isDisponible) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Ubicacion u WHERE u.disponible = :disponible ORDER BY u.nombre_torre, u.nombre_locker";
            Query<Ubicacion> query = session.createQuery(hql, Ubicacion.class);
            query.setParameter("disponible", isDisponible);
            return query.list();
        }
    }
    //Filtrar por Torre
    public List<Ubicacion> obtenerPorTorre(String nombreTorre) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Ubicacion u WHERE u.nombre_torre = :torre ORDER BY u.nombre_locker";
            Query<Ubicacion> query = session.createQuery(hql, Ubicacion.class);
            query.setParameter("torre", nombreTorre);
            return query.list();
        }
    }

    //Filtrar por Localizaci├│n
    public List<Ubicacion> obtenerPorLocalizacion(String localizacion) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Ubicacion u WHERE u.localizacion = :loc ORDER BY u.nombre_torre, u.nombre_locker";
            Query<Ubicacion> query = session.createQuery(hql, Ubicacion.class);
            query.setParameter("loc", localizacion);
            return query.list();
        }
    }

    public boolean eliminar(Long ubicacionId) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Ubicacion ubicacion = session.get(Ubicacion.class, ubicacionId);
            if (ubicacion != null) {
                session.remove(ubicacion);
            }
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Error al eliminar la ubicacion");
        }
    }
}
