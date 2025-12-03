package dao;

import model.Renta;
import model.Ubicacion;
import util.AppLogger;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.time.Instant;
import java.util.List;

public class RentaDAO {
    
    // Configuracion de conexion JDBC
    private static final String JDBC_URL = "jdbc:postgresql://localhost:5432/lockereasy";
    private static final String JDBC_USER = "postgres";
    private static final String JDBC_PASS = "postgresql112";
    
    public void guardar(Renta renta) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        Long startTime = System.currentTimeMillis();

        try {
            String ubicacionInfo = formatearUbicacion(renta.getUbicacion());
            AppLogger.transaccionIniciada("guardar renta",
                "Ubicacion: " + ubicacionInfo + " Precio: $" + renta.getPrecio()
            );

            transaction = session.beginTransaction();
            session.persist(renta);
            transaction.commit();

            Long duration = System.currentTimeMillis() - startTime;
            AppLogger.transaccionCommit("guardar renta", duration);

            AppLogger.rentaAgregada(
                renta.getTipoServicioId().intValue(),
                ubicacionInfo,
                0,
                renta.getPrecio(),
                renta.getInicioRenta()
            );
        } catch (Exception e) {
            if (transaction != null && transaction.getStatus().canRollback()) {
                transaction.rollback();
                AppLogger.transaccionRollback("guardar renta", e.getMessage());
            }
            AppLogger.errorDBDetallado("guardar", "Renta", 
                renta.getTipoServicioId() != null ? renta.getTipoServicioId().intValue() : null, e);
            throw new RuntimeException("Error al guardar la renta", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public void actualizar(Renta renta) {
        Long startTime = System.currentTimeMillis();
        String ubicacionInfo = formatearUbicacion(renta.getUbicacion());
        
        try {
            AppLogger.transaccionIniciada("actualizar renta",
                "ID: " + renta.getTipoServicioId() + " Ubicacion: " + ubicacionInfo
            );
            
            // Si estamos cerrando la renta, usar JDBC directo para evitar problemas con Hibernate cascade
            if (renta.getCierreRenta() != null) {
                actualizarCierreConJDBC(renta);
            } else {
                actualizarConHibernate(renta);
            }

            Long duration = System.currentTimeMillis() - startTime;
            AppLogger.transaccionCommit("actualizar renta", duration);

            if (renta.getCierreRenta() != null) {
                AppLogger.rentaCerrada(
                    renta.getTipoServicioId().intValue(),
                    ubicacionInfo,
                    renta.getInicioRenta(),
                    renta.getCierreRenta(),
                    calcularCostoTotal(renta)
                );
            } else {
                AppLogger.info("Renta actualizada ID: {} Ubicacion: [{}]", 
                    renta.getTipoServicioId(), ubicacionInfo);
            }

        } catch (Exception e) {
            AppLogger.transaccionRollback("actualizar renta", e.getMessage());
            AppLogger.errorDBDetallado("actualizar", "Renta", 
                renta.getTipoServicioId().intValue(), e);
            throw new RuntimeException("Error al actualizar la renta", e);
        }
    }
    
    /**
     * Actualiza el cierre de renta usando JDBC directo.
     * Esto evita que Hibernate cascade sobrescriba el valor con objetos en memoria.
     */
    private void actualizarCierreConJDBC(Renta renta) throws Exception {
        java.sql.Connection conn = java.sql.DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
        conn.setAutoCommit(false);
        
        // Recalcular el total basado en la cantidad actualizada
        renta.calcularTotal();
        
        try {
            // UPDATE en tabla rentas
            String updateRentas = "UPDATE rentas SET cierre_renta = ? WHERE renta_id = ?";
            try (java.sql.PreparedStatement ps = conn.prepareStatement(updateRentas)) {
                ps.setTimestamp(1, java.sql.Timestamp.from(renta.getCierreRenta()));
                ps.setLong(2, renta.getTipoServicioId());
                ps.executeUpdate();
            }
            
            // UPDATE en tabla tipos_servicio (cantidad Y total)
            String updateTipos = "UPDATE tipos_servicio SET cantidad = ?, total = ? WHERE tipo_servicio_id = ?";
            try (java.sql.PreparedStatement ps = conn.prepareStatement(updateTipos)) {
                ps.setInt(1, renta.getCantidad());
                ps.setFloat(2, renta.getTotal());
                ps.setLong(3, renta.getTipoServicioId());
                ps.executeUpdate();
            }
            
            conn.commit();
            
        } catch (Exception e) {
            conn.rollback();
            throw e;
        } finally {
            conn.close();
        }
        
        // Limpiar caché de Hibernate para evitar datos obsoletos
        limpiarCacheHibernate(renta.getTipoServicioId());
    }
    
    /**
     * Actualiza renta usando Hibernate (para cambios que no son cierre).
     */
    private void actualizarConHibernate(Renta renta) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.merge(renta);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.getStatus().canRollback()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            session.close();
        }
    }
    
    /**
     * Limpia el caché de Hibernate para una renta específica.
     */
    private void limpiarCacheHibernate(Long rentaId) {
        HibernateUtil.getSessionFactory().getCache().evict(Renta.class, rentaId);
        HibernateUtil.getSessionFactory().getCache().evict(Renta.class);
        HibernateUtil.getSessionFactory().getCache().evictQueryRegions();
    }

    public Renta obtener(Long rentaId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Renta renta = session.get(Renta.class, rentaId);
            if (renta == null) {
                AppLogger.warningDatosInconsistentes("Renta", rentaId.intValue(), 
                    "No existe renta con ese ID");
            }
            return renta;
        }
    }

    /**
     * Obtiene la renta activa para una ubicacion usando JDBC directo.
     * Esto asegura leer el estado más reciente de la base de datos.
     */
    public Renta obtener(Ubicacion ubicacion) {
        if (ubicacion == null || ubicacion.getUbicacionId() == null) return null;
        
        Long activeRentaId = buscarRentaActivaConJDBC(ubicacion.getUbicacionId());
        
        if (activeRentaId == null) {
            return null;
        }
        
        // Limpiar caché antes de cargar
        limpiarCacheHibernate(activeRentaId);
        
        // Cargar el objeto usando sesion limpia
        Session freshSession = HibernateUtil.getSessionFactory().openSession();
        try {
            freshSession.clear();
            return freshSession.get(Renta.class, activeRentaId);
        } finally {
            freshSession.close();
        }
    }
    
    /**
     * Busca renta activa por ubicacion usando JDBC directo.
     * Retorna el ID de la renta activa o null si no hay ninguna.
     */
    private Long buscarRentaActivaConJDBC(Long ubicacionId) {
        try {
            java.sql.Connection conn = java.sql.DriverManager.getConnection(
                JDBC_URL + "?prepareThreshold=0", JDBC_USER, JDBC_PASS);
            conn.setAutoCommit(true);
            conn.setTransactionIsolation(java.sql.Connection.TRANSACTION_READ_COMMITTED);
            
            try {
                String sql = "SELECT renta_id FROM rentas WHERE ubicacion_id = " + 
                            ubicacionId + " AND cierre_renta IS NULL ORDER BY renta_id DESC LIMIT 1";
                try (java.sql.Statement stmt = conn.createStatement();
                     java.sql.ResultSet rs = stmt.executeQuery(sql)) {
                    if (rs.next()) {
                        return rs.getLong(1);
                    }
                }
            } finally {
                conn.close();
            }
        } catch (Exception e) {
            AppLogger.debug("Error en query JDBC para renta activa: {}", e.getMessage());
        }
        return null;
    }

    public List<Renta> obtenerActivas() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.clear();
            
            String hql = "FROM Renta r WHERE r.cierre_renta IS NULL";
            Query<Renta> query = session.createQuery(hql, Renta.class);
            query.setCacheable(false);
            
            List<Renta> rentas = query.list();
            
            AppLogger.debug("Obtenidas {} rentas activas", rentas.size());
            
            // Verificar rentas que llevan mucho tiempo activas
            Instant ahora = Instant.now();
            for (Renta renta : rentas) {
                long horasActivas = java.time.Duration.between(
                    renta.getInicioRenta(), ahora).toHours();
                if (horasActivas > 24) {
                    AppLogger.rentaExpirada(
                        renta.getTipoServicioId().intValue(),
                        formatearUbicacion(renta.getUbicacion()),
                        horasActivas
                    );
                }
            }
            
            return rentas;
        }
    }

    public boolean eliminar(Long rentaId) {
        Transaction transaction = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        
        try {
            transaction = session.beginTransaction();
            
            Renta renta = session.get(Renta.class, rentaId);
            if (renta != null) {
                String ubicacionInfo = formatearUbicacion(renta.getUbicacion());
                
                // Romper relacion bidireccional con Ticket antes de eliminar
                model.Ticket ticket = renta.getTicket();
                if (ticket != null) {
                    ticket.getServicios().remove(renta);
                    renta.setTicket(null);
                    session.merge(ticket);
                }
                
                session.remove(renta);
                transaction.commit();
                
                AppLogger.rentaEliminada(rentaId.intValue(), ubicacionInfo, 0);
                return true;
            } else {
                AppLogger.warningDatosInconsistentes("Renta", rentaId.intValue(), 
                    "Intento de eliminar renta inexistente");
                return false;
            }
        } catch (Exception e) {
            if (transaction != null && transaction.getStatus().canRollback()) {
                transaction.rollback();
            }
            AppLogger.errorDBDetallado("eliminar", "Renta", rentaId.intValue(), e);
            throw new RuntimeException("Error al eliminar la renta", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    // Métodos auxiliares
    private String formatearUbicacion(Ubicacion ubicacion) {
        return String.format("%s - %s - %s",
            ubicacion.getNombreLocker(),
            ubicacion.getNombreTorre(),
            ubicacion.getLocalizacion()
        );
    }

    private double calcularCostoTotal(Renta renta) {
        if (renta.getCierreRenta() == null) return 0.0;
        long horas = java.time.Duration.between(
            renta.getInicioRenta(),
            renta.getCierreRenta()
        ).toHours();
        return horas * renta.getPrecio();
    }
}