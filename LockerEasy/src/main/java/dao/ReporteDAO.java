package dao;

import model.Reporte;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.time.LocalDate;
import java.util.List;

public class ReporteDAO {
    public void guardar(Reporte reporte) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(reporte);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();   //Esto que agregue imprime el error para saber que pasó
            throw new RuntimeException("Error al guardar el reporte");
        }
    }

    public void actualizar(Reporte reporte) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(reporte);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();   //Esto que agregue imprime el error para saber que pasó
            throw new RuntimeException("Error al actualizar el reporte");
        }
    }
    //Quité session.get porque la fecha no es un ID y usé HQL para buscar por la columna frecha
    public Reporte obtener(LocalDate fecha) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Reporte r WHERE r.fecha_reporte = :fecha";
            Query<Reporte> query = session.createQuery(hql, Reporte.class);
            query.setParameter("fecha", fecha);
            
            // uniqueResult devuelve el objeto si lo encuentra, o null si no existe
            return query.uniqueResult(); 
        }
    }
        
    
    public List<Reporte> obtener(LocalDate fechaInicio, LocalDate fechaFin) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Reporte r WHERE r.fecha_reporte BETWEEN :inicio AND :fin ORDER BY r.fecha_reporte";
            Query<Reporte> query = session.createQuery(hql, Reporte.class);
            query.setParameter("inicio", fechaInicio);
            query.setParameter("fin", fechaFin);
            return query.list();
        }
    }

    public void cerrar(LocalDate fecha) {
        // Buscamos el reporte usanod nuestro método corregido
        Reporte reporte = obtener(fecha);

        if (reporte !=null){
            Transaction transaction = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                transaction = session.beginTransaction();
                //Cambiamos el estado
                reporte.setEstado(Reporte.EstadoReporte.CERRADO);

                session.merge(reporte);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) transaction.rollback();
                e.printStackTrace();
                throw new RuntimeException("Error al cerrar el reporte");
            }
        } else {
            System.out.println("No se encontró reporte para la fecha: " + fecha);
        }
    }
        
        /*Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Reporte reporte = session.get(Reporte.class, fecha);
            if (reporte != null) {
                reporte.setEstado(Reporte.EstadoReporte.CERRADO);
                session.merge(reporte);
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Error al cerrar el reporte");
        }
    }
*/
    public boolean existe(LocalDate fecha) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT COUNT(r) FROM Reporte r WHERE r.fecha_reporte = :fecha";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("fecha", fecha);
            return query.uniqueResult() > 0;
        }
    }
}
