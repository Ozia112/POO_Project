package dao;

import model.Reporte;
import util.AppLogger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.time.LocalDate;
import java.util.List;

public class ReporteDAO {
    
    public void guardar(Reporte reporte) {
        Transaction transaction = null;
        Session session = session = HibernateUtil.getSessionFactory().openSession();
        long startTime = System.currentTimeMillis();
        
        try {
            AppLogger.transaccionIniciada("guardar reporte",
                "Fecha: " + reporte.getFechaReporte() + " Estado: " + reporte.getEstado());
            
            transaction = session.beginTransaction();
            session.persist(reporte);
            transaction.commit();
            
            long duration = System.currentTimeMillis() - startTime;
            AppLogger.transaccionCommit("guardar reporte", duration);
            
            AppLogger.reporteCreado(
                reporte.getFechaReporte().toString(),
                reporte.getEstado().toString()
            );
            
        } catch (Exception e) {
            if (transaction != null && transaction.getStatus().canRollback()) {
                transaction.rollback();
                AppLogger.transaccionRollback("guardar reporte", e.getMessage());
            }
            AppLogger.errorDBDetallado("guardar", "Reporte", null, e);
            throw new RuntimeException("Error al guardar el reporte", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public void actualizar(Reporte reporte) {
        Transaction transaction = null;
        Session session = session = HibernateUtil.getSessionFactory().openSession();
        long startTime = System.currentTimeMillis();
        
        try {
            // Obtener estado anterior
            Reporte reporteAnterior = session.get(Reporte.class, reporte.getFechaReporte());
            double totalAnterior = reporteAnterior != null ? reporteAnterior.getTotal() : 0.0;
            
            AppLogger.transaccionIniciada("actualizar reporte",
                "Fecha: " + reporte.getFechaReporte());
            
            transaction = session.beginTransaction();
            
            // IMPORTANTE: Usar UPDATE nativo para evitar que cascade sobrescriba
            // cambios hechos por JDBC directo en las entidades relacionadas (Renta)
            String updateSql = "UPDATE reportes SET total_reporte = :total, estado = :estado WHERE fecha_reporte = :fecha";
            session.createNativeMutationQuery(updateSql)
                .setParameter("total", reporte.getTotal())
                .setParameter("estado", reporte.getEstado().name())
                .setParameter("fecha", reporte.getFechaReporte())
                .executeUpdate();
            
            transaction.commit();
            
            long duration = System.currentTimeMillis() - startTime;
            AppLogger.transaccionCommit("actualizar reporte", duration);
            
            int numTickets = reporte.getTickets() != null ? reporte.getTickets().size() : 0;
            
            AppLogger.reporteActualizado(
                reporte.getFechaReporte().toString(),
                totalAnterior,
                reporte.getTotal(),
                numTickets
            );
            
        } catch (Exception e) {
            if (transaction != null && transaction.getStatus().canRollback()) {
                transaction.rollback();
                AppLogger.transaccionRollback("actualizar reporte", e.getMessage());
            }
            AppLogger.errorDBDetallado("actualizar", "Reporte", null, e);
            throw new RuntimeException("Error al actualizar el reporte", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public Reporte obtener(LocalDate fecha) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Reporte r WHERE r.fecha_reporte = :fecha";
            Query<Reporte> query = session.createQuery(hql, Reporte.class);
            query.setParameter("fecha", fecha);
            
            Reporte reporte = query.uniqueResult();
            if (reporte == null) {
                AppLogger.debug("No existe reporte para la fecha: {}", fecha);
            }
            return reporte;
        }
    }

    public List<Reporte> obtener(LocalDate fechaInicio, LocalDate fechaFin) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Reporte r WHERE r.fecha_reporte BETWEEN :inicio AND :fin ORDER BY r.fecha_reporte";
            Query<Reporte> query = session.createQuery(hql, Reporte.class);
            query.setParameter("inicio", fechaInicio);
            query.setParameter("fin", fechaFin);
            List<Reporte> reportes = query.list();
            
            AppLogger.debug("Obtenidos {} reportes entre {} y {}", 
                reportes.size(), fechaInicio, fechaFin);
            return reportes;
        }
    }

    public void cerrar(LocalDate fecha) {
        Reporte reporte = obtener(fecha);

        if (reporte != null) {
            Transaction transaction = null;
            Session session = session = HibernateUtil.getSessionFactory().openSession();
            
            try {
                transaction = session.beginTransaction();
                
                reporte.setEstado(Reporte.EstadoReporte.CERRADO);
                session.merge(reporte);
                transaction.commit();
                
                int numTickets = reporte.getTickets() != null ? reporte.getTickets().size() : 0;
                int numServicios = 0;
                if (reporte.getTickets() != null) {
                    for (var ticket : reporte.getTickets()) {
                        if (ticket.getServicios() != null) {
                            numServicios += ticket.getServicios().size();
                        }
                    }
                }
                
                AppLogger.reporteCerrado(
                    fecha.toString(),
                    reporte.getTotal(),
                    numTickets,
                    numServicios
                );
                
            } catch (Exception e) {
                if (transaction != null && transaction.getStatus().canRollback()) {
                    transaction.rollback();
                }
                AppLogger.errorDBDetallado("cerrar", "Reporte", null, e);
                throw new RuntimeException("Error al cerrar el reporte", e);
            } finally {
                if (session != null && session.isOpen()) {
                    session.close();
                }
            }
        } else {
            AppLogger.warningDatosInconsistentes("Reporte", null, 
                "No se encontró reporte para la fecha: " + fecha);
        }
    }

    public boolean existe(LocalDate fecha) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT COUNT(r) FROM Reporte r WHERE r.fecha_reporte = :fecha";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("fecha", fecha);
            return query.uniqueResult() > 0;
        }
    }
}