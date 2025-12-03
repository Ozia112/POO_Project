package dao;

import model.Configuracion;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class ConfiguracionDAO {
    
    /**
     * Guarda o actualiza un valor de configuración
     */
    public boolean guardar(Configuracion config) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(config);
            transaction.commit();
            System.out.println("[Config] Guardado: " + config.getClave() + " = " + config.getValor());
            return true;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            System.err.println("[Config] Error al guardar: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Obtiene un valor de configuración por su clave
     */
    public Configuracion obtener(String clave) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Configuracion.class, clave);
        } catch (Exception e) {
            System.err.println("[Config] Error al obtener " + clave + ": " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Obtiene un valor como String, con valor por defecto si no existe
     */
    public String obtenerValor(String clave, String valorDefecto) {
        Configuracion config = obtener(clave);
        return config != null ? config.getValor() : valorDefecto;
    }
    
    /**
     * Obtiene un valor como int, con valor por defecto si no existe
     */
    public int obtenerValorInt(String clave, int valorDefecto) {
        Configuracion config = obtener(clave);
        if (config != null) {
            try {
                return config.getValorInt();
            } catch (NumberFormatException e) {
                return valorDefecto;
            }
        }
        return valorDefecto;
    }
    
    /**
     * Obtiene un valor como float, con valor por defecto si no existe
     */
    public float obtenerValorFloat(String clave, float valorDefecto) {
        Configuracion config = obtener(clave);
        if (config != null) {
            try {
                return config.getValorFloat();
            } catch (NumberFormatException e) {
                return valorDefecto;
            }
        }
        return valorDefecto;
    }
    
    /**
     * Guarda un valor de configuración (crea o actualiza)
     */
    public boolean guardarValor(String clave, String valor, String descripcion) {
        Configuracion config = new Configuracion(clave, valor, descripcion);
        return guardar(config);
    }
    
    /**
     * Guarda un valor int
     */
    public boolean guardarValorInt(String clave, int valor, String descripcion) {
        return guardarValor(clave, String.valueOf(valor), descripcion);
    }
    
    /**
     * Guarda un valor float
     */
    public boolean guardarValorFloat(String clave, float valor, String descripcion) {
        return guardarValor(clave, String.valueOf(valor), descripcion);
    }
    
    /**
     * Obtiene todas las configuraciones
     */
    public List<Configuracion> obtenerTodas() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Configuracion", Configuracion.class).list();
        } catch (Exception e) {
            System.err.println("[Config] Error al obtener configuraciones: " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Elimina una configuración
     */
    public boolean eliminar(String clave) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Configuracion config = session.get(Configuracion.class, clave);
            if (config != null) {
                session.remove(config);
                transaction.commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            System.err.println("[Config] Error al eliminar: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Inicializa valores por defecto si no existen
     */
    public void inicializarValoresPorDefecto() {
        if (obtener("precio_hora_locker") == null) {
            guardarValorFloat("precio_hora_locker", 50.0f, "Precio por hora de renta de locker");
        }
        if (obtener("minutos_tolerancia") == null) {
            guardarValorInt("minutos_tolerancia", 10, "Minutos de tolerancia antes de cobrar hora extra");
        }
        if (obtener("minutos_cancelacion") == null) {
            guardarValorInt("minutos_cancelacion", 5, "Minutos máximos para cancelar sin cargo");
        }
        if (obtener("descuento_unico") == null) {
            guardarValorFloat("descuento_unico", 10.0f, "Porcentaje de descuento único aplicable");
        }
        System.out.println("[Config] Configuraciones inicializadas desde BD");
    }
}
