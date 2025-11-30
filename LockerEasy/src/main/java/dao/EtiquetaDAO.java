package dao;

import model.Etiqueta;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.util.List;

public class EtiquetaDAO{

    //Guarda una nueva etiqueta en base de datos 
    public void guardar(Etiqueta etiqueta) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(etiqueta);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Error al guardar la etiqueta", e);
        }
    }
    // Actualiza etiqueta
    public void actualizar(Etiqueta etiqueta) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(etiqueta);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Error al actualizar la etiqueta", e);
        }
    }

    //Busca la etiqueta mediante su id numerico
    public Etiqueta obtener(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Etiqueta.class, id);
        }
    }

    //Busca una etiqueta por su nombre 
    public Etiqueta obtenerPorNombre(String nombre) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Etiqueta e WHERE e.nombre = :nombre";
            Query<Etiqueta> query = session.createQuery(hql, Etiqueta.class);
            query.setParameter("nombre", nombre);
            return query.uniqueResult();
        }
    }

    // Devuelve todas las etiquetas registradas 
    public List<Etiqueta> obtenerTodas() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Etiqueta", Etiqueta.class).list();
        }
    }

    //Elimina la etiqueta por su ID
    public boolean eliminar(Long id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Etiqueta etiqueta = session.get(Etiqueta.class, id);
            if (etiqueta != null) {
                session.remove(etiqueta);
                transaction.commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Error al eliminar la etiqueta", e);
        }
    }


}