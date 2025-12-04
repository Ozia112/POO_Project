package dao;

import model.CredencialBD;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import util.AppLogger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

/**
 * DAO para gestionar las credenciales de conexión a base de datos.
 * Permite guardar múltiples perfiles de conexión y cambiar entre ellos.
 * 
 * Usado en: ConfigCuentaGUI (pestaña Cuenta en Configuración)
 */
public class CredencialBDDAO {
    
    /**
     * Guarda o actualiza una credencial.
     */
    public void guardar(CredencialBD credencial) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(credencial);
            transaction.commit();
            AppLogger.info("Credencial guardada: {}", credencial.getNombrePerfil());
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            AppLogger.excepcionNoControlada("CredencialBDDAO.guardar", e);
            throw new RuntimeException("Error al guardar credencial: " + e.getMessage(), e);
        }
    }
    
    /**
     * Obtiene una credencial por su ID.
     */
    public CredencialBD obtenerPorId(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(CredencialBD.class, id);
        } catch (Exception e) {
            AppLogger.excepcionNoControlada("CredencialBDDAO.obtenerPorId", e);
            return null;
        }
    }
    
    /**
     * Obtiene una credencial por nombre de perfil.
     */
    public CredencialBD obtenerPorNombre(String nombrePerfil) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<CredencialBD> query = session.createQuery(
                "FROM CredencialBD WHERE nombrePerfil = :nombre", CredencialBD.class);
            query.setParameter("nombre", nombrePerfil);
            return query.uniqueResult();
        } catch (Exception e) {
            AppLogger.excepcionNoControlada("CredencialBDDAO.obtenerPorNombre", e);
            return null;
        }
    }
    
    /**
     * Obtiene la credencial activa (solo debería haber una).
     */
    public CredencialBD obtenerActiva() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<CredencialBD> query = session.createQuery(
                "FROM CredencialBD WHERE activo = true", CredencialBD.class);
            return query.uniqueResult();
        } catch (Exception e) {
            AppLogger.excepcionNoControlada("CredencialBDDAO.obtenerActiva", e);
            return null;
        }
    }
    
    /**
     * Obtiene todas las credenciales guardadas.
     */
    public List<CredencialBD> obtenerTodas() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM CredencialBD ORDER BY nombrePerfil", CredencialBD.class).list();
        } catch (Exception e) {
            AppLogger.excepcionNoControlada("CredencialBDDAO.obtenerTodas", e);
            return List.of();
        }
    }
    
    /**
     * Establece una credencial como activa y desactiva las demás.
     */
    public void establecerActiva(Long credencialId) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            
            // Desactivar todas
            session.createMutationQuery("UPDATE CredencialBD SET activo = false").executeUpdate();
            
            // Activar la seleccionada
            session.createMutationQuery("UPDATE CredencialBD SET activo = true WHERE id = :id")
                   .setParameter("id", credencialId)
                   .executeUpdate();
            
            transaction.commit();
            AppLogger.info("Credencial activada: ID {}", credencialId);
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            AppLogger.excepcionNoControlada("CredencialBDDAO.establecerActiva", e);
            throw new RuntimeException("Error al activar credencial: " + e.getMessage(), e);
        }
    }
    
    /**
     * Elimina una credencial por su ID.
     */
    public void eliminar(Long id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            CredencialBD credencial = session.get(CredencialBD.class, id);
            if (credencial != null) {
                session.remove(credencial);
                AppLogger.info("Credencial eliminada: {}", credencial.getNombrePerfil());
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            AppLogger.excepcionNoControlada("CredencialBDDAO.eliminar", e);
            throw new RuntimeException("Error al eliminar credencial: " + e.getMessage(), e);
        }
    }
    
    /**
     * Prueba la conexión con las credenciales proporcionadas.
     * @return true si la conexión es exitosa, false si falla
     */
    public boolean probarConexion(CredencialBD credencial) {
        if (credencial == null || !credencial.esValida()) {
            return false;
        }
        
        String url = credencial.construirUrlJDBC();
        String usuario = credencial.getUsuario();
        String password = credencial.getPassword();
        
        try {
            // Cargar el driver según el tipo de BD
            String driver = obtenerDriver(credencial.getTipoBaseDatos());
            Class.forName(driver);
            
            // Intentar conexión con timeout
            try (Connection conn = DriverManager.getConnection(url, usuario, password)) {
                boolean valida = conn.isValid(5); // 5 segundos de timeout
                AppLogger.info("Prueba de conexión {} para: {}", (valida ? "exitosa" : "fallida"), url);
                return valida;
            }
        } catch (Exception e) {
            AppLogger.excepcionNoControlada("CredencialBDDAO.probarConexion: " + url, e);
            return false;
        }
    }
    
    /**
     * Prueba la conexión con parámetros individuales (sin guardar).
     */
    public boolean probarConexion(String host, int puerto, String nombreBD, 
                                   String usuario, String password, String tipoBD) {
        CredencialBD temp = new CredencialBD();
        temp.setUrlConexion(host);
        temp.setPuerto(puerto);
        temp.setNombreBaseDatos(nombreBD);
        temp.setUsuario(usuario);
        temp.setPassword(password);
        temp.setTipoBaseDatos(tipoBD);
        return probarConexion(temp);
    }
    
    /**
     * Obtiene el driver JDBC según el tipo de base de datos.
     */
    private String obtenerDriver(String tipoBD) {
        if (tipoBD == null) tipoBD = "postgresql";
        
        return switch (tipoBD.toLowerCase()) {
            case "mysql" -> "com.mysql.cj.jdbc.Driver";
            case "mariadb" -> "org.mariadb.jdbc.Driver";
            case "sqlserver" -> "com.microsoft.sqlserver.jdbc.SQLServerDriver";
            case "oracle" -> "oracle.jdbc.OracleDriver";
            case "h2" -> "org.h2.Driver";
            default -> "org.postgresql.Driver"; // PostgreSQL por defecto
        };
    }
    
    /**
     * Crea el perfil por defecto "Local" si no existe ninguna credencial.
     */
    public void crearPerfilPorDefectoSiNoExiste() {
        if (obtenerTodas().isEmpty()) {
            CredencialBD local = new CredencialBD();
            local.setNombrePerfil("Local (Desarrollo)");
            local.setUrlConexion("localhost");
            local.setPuerto(5432);
            local.setNombreBaseDatos("lockereasy");
            local.setUsuario("postgres");
            local.setPassword(""); // Vacío, el usuario debe configurarlo
            local.setTipoBaseDatos("postgresql");
            local.setDescripcion("Perfil de conexión local para desarrollo");
            local.setActivo(true);
            
            guardar(local);
            AppLogger.info("Perfil de conexión por defecto creado");
        }
    }
}
