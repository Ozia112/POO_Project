package dao;

import model.Renta;
import model.Ubicacion;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class RentaDAO {
    public void guardar(Renta renta) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(renta);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Error al guardar la renta");
        }
    }

    public void actualizar(Renta renta) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(renta);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Error al actualizar la renta");
        }
    }

    public Renta obtener(Long rentaId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Renta.class, rentaId);
        }
    }

    public Renta obtener(Ubicacion ubicacion) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Renta r WHERE r.ubicacion = :ubicacion AND r.cierre_renta IS NULL";
            Query<Renta> query = session.createQuery(hql, Renta.class);
            query.setParameter("ubicacion", ubicacion);
            return query.uniqueResult();
        }
    }

    public List<Renta> obtenerActivas() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Renta r WHERE r.cierre_renta IS NULL";
            Query<Renta> query = session.createQuery(hql, Renta.class);
            return query.list();
        }
    }

    public boolean eliminar(Long rentaId) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Renta renta = session.get(Renta.class, rentaId);
            if (renta != null) {
                session.remove(renta);
                transaction.commit();
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Error al eliminar la renta");
        }
    }
}
