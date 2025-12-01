package dao;

import model.Servicio;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.util.List;

public class ServicioDAO{

    public void guardar(Servicio servicio) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(servicio);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Error al guardar el servicio", e);
        }
    }

    //Actualiza un servicio existente 
    public void actualizar(Servicio servicio) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(servicio);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Error al actualizar el servicio", e);
        }
    }

    //Busca un Serviicio por su ID
    public Servicio obtener(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Servicio.class, id);
        }
    }

    //Obtiene todos los servicios registrados
    public List<Servicio> obtenerTodos() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Servicio", Servicio.class).list();
        }
    }

    //Elimina un servicio por su ID 
    public boolean eliminar(Long id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Servicio servicio = session.get(Servicio.class, id);
            if (servicio != null) {
                session.remove(servicio);
                transaction.commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Error al eliminar el servicio", e);
        }
    }
}
