package dao;

import model.Etiqueta;
import util.AppLogger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.util.List;

public class EtiquetaDAO{

    /**
     * Guarda una nueva etiqueta en base de datos
     * Calcula el tiempo de transaccion y maneja logs detallados
     * @param etiqueta
     */
    public void guardar(Etiqueta etiqueta) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        long startTime = System.currentTimeMillis(); // inicio de transaccion

        try {
            AppLogger.transaccionIniciada("guardar etiqueta",
                "Etiqueta: " + etiqueta.getNombre() + "Afecta inventario: " + etiqueta.isAfectaInventario());
            transaction = session.beginTransaction();
            session.persist(etiqueta);
            transaction.commit();

            long duration = System.currentTimeMillis() - startTime; // fin de transaccion
            AppLogger.transaccionCommit("guardar etiqueta", duration);

            AppLogger.etiquetaCreada(
                etiqueta.getEtiquetaId().intValue(),
                etiqueta.getNombre(),
                etiqueta.isAfectaInventario()
            );

        } catch (Exception e) {
            if (transaction != null && transaction.getStatus().canRollback()) {
                transaction.rollback();
                AppLogger.transaccionRollback("guardar etiqueta", e.getMessage());
            }
            AppLogger.errorDBDetallado("guardar", "etiqueta", etiqueta.getEtiquetaId().intValue(), e);
            throw e;
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    // Actualiza etiqueta
    public void actualizar(Etiqueta etiqueta) {
        Session session =  HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        long startTime = System.currentTimeMillis(); // inicio de transaccion

        try {
            AppLogger.transaccionIniciada("actualizar etiqueta",
                "ID: " + etiqueta.getEtiquetaId() + " Nombre: " + etiqueta.getNombre());

            transaction = session.beginTransaction();
            session.merge(etiqueta);
            transaction.commit();

            long duration = System.currentTimeMillis() - startTime; // fin de transaccion
            AppLogger.transaccionCommit("actualizar etiqueta", duration);

            AppLogger.info("Etiqueta actualizada ID: {} Nombre: [{}]",
                etiqueta.getEtiquetaId(), etiqueta.getNombre());
            
        } catch (Exception e) {
            if (transaction != null && transaction.getStatus().canRollback()) {
                transaction.rollback();
                AppLogger.transaccionRollback("actualizar etiqueta", e.getMessage());
            }
            AppLogger.errorDBDetallado("actualizar", "etiqueta", etiqueta.getEtiquetaId().intValue(), e);
            throw e;

        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    //Busca la etiqueta mediante su id numerico
    public Etiqueta obtener(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Etiqueta etiqueta = session.get(Etiqueta.class, id);
            if (etiqueta == null) {
                AppLogger.warningDatosInconsistentes("Etiqueta", id.intValue(),
                    "No existe etiqueta con ese ID."); 
            }
            return etiqueta;
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
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            Etiqueta etiqueta = session.get(Etiqueta.class, id);

            if (etiqueta != null) {
                String nombreEtiqueta = etiqueta.getNombre();
                session.remove(etiqueta);
                transaction.commit();
                
                AppLogger.etiquetaEliminada(id.intValue(), nombreEtiqueta, 0);
                return true;
            } else {
                AppLogger.warningDatosInconsistentes("Etiqueta", id.intValue(),
                    "Intento de eliminar etiqueta inexistente.");
                return false;
            }

        } catch (Exception e) {
            if (transaction != null && transaction.getStatus().canRollback()) {
                transaction.rollback();
                AppLogger.transaccionRollback("eliminar etiqueta", e.getMessage());
            } 
            throw e;
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
}
