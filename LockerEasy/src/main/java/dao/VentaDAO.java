package dao;

import model.Venta;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.util.List;

public class VentaDAO {

    public void guardar(Venta venta) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(venta);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Error al guardar la venta", e);
        }
    }

    //Actualiza un producto existente
    public void actualizar(Venta venta) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(venta);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Error al actualizar la venta", e);
        }
    }

    //Busca un producto por su id 
    public Venta obtener(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Venta.class, id);
        }
    }

    //Obtiene todos los productos
    public List<Venta> obtenerTodas() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Venta", Venta.class).list();
        }
    }

    //Obtiene solo los productos que se puedeb vender
    //flitra si está dispponible y que tenga existencias
    public List<Venta> obtenerDisponibles() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Venta v WHERE v.disponible = true AND v.existentes > 0";
            return session.createQuery(hql, Venta.class).list();
        }
    }

    //Elimina un producto por su ID
    public boolean eliminar(Long id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Venta venta = session.get(Venta.class, id);
            if (venta != null) {
                session.remove(venta);
                transaction.commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Error al eliminar la venta", e);
        }
    }
}


