package dao;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.*;
import java.nio.file.*;
import java.util.Properties;

/**
 * Utilidad para gestionar la conexión de Hibernate.
 * 
 * Orden de prioridad para credenciales:
 * 1. Variables de entorno (DB_URL, DB_USER, DB_PASSWORD)
 * 2. Archivo local db.properties (no se sube al repo)
 * 3. Diálogo de login al iniciar (si no hay credenciales)
 */
public class HibernateUtil {
    private static SessionFactory sessionFactory;
    private static String dbUrl;
    private static String dbUser;
    private static String dbPassword;
    private static boolean credencialesCargadas = false;
    
    // Archivo local para guardar credenciales (en .gitignore)
    private static final String CREDENTIALS_FILE = "db.properties";

    /**
     * Carga las credenciales desde variables de entorno o archivo local.
     * @return true si se encontraron credenciales, false si necesita login manual
     */
    public static boolean cargarCredenciales() {
        // 1. Intentar variables de entorno
        String envUrl = System.getenv("DB_URL");
        String envUser = System.getenv("DB_USER");
        String envPassword = System.getenv("DB_PASSWORD");
        
        if (envUrl != null && envUser != null && envPassword != null) {
            dbUrl = envUrl;
            dbUser = envUser;
            dbPassword = envPassword;
            credencialesCargadas = true;
            System.out.println("✓ Credenciales cargadas desde variables de entorno");
            return true;
        }
        
        // 2. Intentar archivo local db.properties
        Path propsPath = getCredentialsPath();
        if (Files.exists(propsPath)) {
            try (InputStream input = Files.newInputStream(propsPath)) {
                Properties props = new Properties();
                props.load(input);
                
                dbUrl = props.getProperty("db.url");
                dbUser = props.getProperty("db.user");
                dbPassword = props.getProperty("db.password");
                
                if (dbUrl != null && dbUser != null && dbPassword != null) {
                    credencialesCargadas = true;
                    System.out.println("✓ Credenciales cargadas desde " + propsPath);
                    return true;
                }
            } catch (IOException e) {
                System.err.println("Error leyendo " + propsPath + ": " + e.getMessage());
            }
        }
        
        // 3. No hay credenciales - se necesita login manual
        return false;
    }
    
    /**
     * Establece las credenciales programáticamente (desde diálogo de login).
     */
    public static void setCredenciales(String url, String user, String password) {
        dbUrl = url;
        dbUser = user;
        dbPassword = password;
        credencialesCargadas = true;
    }
    
    /**
     * Guarda las credenciales en archivo local para futuros inicios.
     */
    public static void guardarCredencialesLocalmente(String url, String user, String password) {
        Properties props = new Properties();
        props.setProperty("db.url", url);
        props.setProperty("db.user", user);
        props.setProperty("db.password", password);
        
        try {
            Path propsPath = getCredentialsPath();
            Files.createDirectories(propsPath.getParent());
            
            try (OutputStream output = Files.newOutputStream(propsPath)) {
                props.store(output, "LockerEasy - Credenciales de Base de Datos (NO SUBIR A GIT)");
            }
            System.out.println("✓ Credenciales guardadas en " + propsPath);
        } catch (IOException e) {
            System.err.println("Error guardando credenciales: " + e.getMessage());
        }
    }
    
    /**
     * Obtiene la ruta del archivo de credenciales.
     */
    private static Path getCredentialsPath() {
        // Guardar en carpeta del usuario para que no se mezcle con el proyecto
        String userHome = System.getProperty("user.home");
        return Paths.get(userHome, ".lockereasy", CREDENTIALS_FILE);
    }
    
    /**
     * Verifica si existen credenciales guardadas.
     */
    public static boolean existenCredencialesGuardadas() {
        // Verificar variables de entorno
        if (System.getenv("DB_URL") != null && 
            System.getenv("DB_USER") != null && 
            System.getenv("DB_PASSWORD") != null) {
            return true;
        }
        
        // Verificar archivo local
        return Files.exists(getCredentialsPath());
    }
    
    /**
     * Inicializa el SessionFactory con las credenciales configuradas.
     */
    public static void inicializar() {
        if (sessionFactory != null) {
            return; // Ya inicializado
        }
        
        if (!credencialesCargadas) {
            throw new IllegalStateException(
                "Debe cargar o establecer las credenciales antes de inicializar Hibernate");
        }
        
        try {
            Configuration configuration = new Configuration().configure("hibernate.cfg.xml");
            
            // Sobrescribir credenciales del XML con las cargadas
            configuration.setProperty("hibernate.connection.url", dbUrl);
            configuration.setProperty("hibernate.connection.username", dbUser);
            configuration.setProperty("hibernate.connection.password", dbPassword);
            
            sessionFactory = configuration.buildSessionFactory();
            System.out.println("✓ Conexión a base de datos establecida");
            
        } catch (Throwable ex) {
            System.err.println("Error al crear SessionFactory: " + ex.getMessage());
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            // Intentar inicialización automática si hay credenciales
            if (cargarCredenciales()) {
                inicializar();
            } else {
                throw new IllegalStateException(
                    "SessionFactory no inicializado. Debe proporcionar credenciales primero.");
            }
        }
        return sessionFactory;
    }
    
    /**
     * Prueba la conexión con las credenciales dadas sin inicializar el SessionFactory principal.
     */
    public static boolean probarConexion(String url, String user, String password) {
        try {
            Configuration testConfig = new Configuration().configure("hibernate.cfg.xml");
            testConfig.setProperty("hibernate.connection.url", url);
            testConfig.setProperty("hibernate.connection.username", user);
            testConfig.setProperty("hibernate.connection.password", password);
            
            // Crear sesión temporal para probar
            try (SessionFactory testFactory = testConfig.buildSessionFactory()) {
                testFactory.openSession().close();
                return true;
            }
        } catch (Exception e) {
            System.err.println("Error probando conexión: " + e.getMessage());
            return false;
        }
    }
    
    public static String getDbUrl() {
        return dbUrl;
    }
    
    public static String getDbUser() {
        return dbUser;
    }
    
    /**
     * Verifica si el SessionFactory está inicializado.
     */
    public static boolean estaInicializado() {
        return sessionFactory != null;
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
            sessionFactory = null;
        }
    }
    
    /**
     * Reinicia la conexión (útil para cambiar credenciales).
     */
    public static void reiniciar() {
        shutdown();
        credencialesCargadas = false;
    }
}
