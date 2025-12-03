package model;

import jakarta.persistence.*;
import java.util.Base64;

/**
 * Entidad para almacenar credenciales de conexión a base de datos.
 * Las contraseñas se guardan codificadas en Base64 (no es encriptación fuerte,
 * pero oculta el texto plano. Para producción usar encriptación real).
 * 
 * Usado en: ConfigCuentaGUI (pestaña Cuenta en Configuración)
 */
@Entity
@Table(name = "credenciales_bd")
public class CredencialBD {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "credencial_id")
    private Long id;
    
    @Column(name = "nombre_perfil", nullable = false, unique = true)
    private String nombrePerfil;
    
    @Column(name = "url_conexion", nullable = false)
    private String urlConexion;
    
    @Column(name = "nombre_bd")
    private String nombreBaseDatos;
    
    @Column(name = "puerto")
    private Integer puerto;
    
    @Column(name = "usuario")
    private String usuario;
    
    @Column(name = "password_encoded")
    private String passwordEncoded;
    
    @Column(name = "es_activo")
    private boolean activo;
    
    @Column(name = "tipo_bd")
    private String tipoBaseDatos; // postgresql, mysql, etc.
    
    @Column(name = "descripcion")
    private String descripcion;

    // ================== CONSTRUCTORES ==================
    
    public CredencialBD() {
        this.activo = false;
        this.tipoBaseDatos = "postgresql";
        this.puerto = 5432;
    }
    
    public CredencialBD(String nombrePerfil, String urlConexion, String usuario, String password) {
        this();
        this.nombrePerfil = nombrePerfil;
        this.urlConexion = urlConexion;
        this.usuario = usuario;
        setPassword(password);
    }

    // ================== GETTERS Y SETTERS ==================
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombrePerfil() {
        return nombrePerfil;
    }

    public void setNombrePerfil(String nombrePerfil) {
        this.nombrePerfil = nombrePerfil;
    }

    public String getUrlConexion() {
        return urlConexion;
    }

    public void setUrlConexion(String urlConexion) {
        this.urlConexion = urlConexion;
    }
    
    public String getNombreBaseDatos() {
        return nombreBaseDatos;
    }
    
    public void setNombreBaseDatos(String nombreBaseDatos) {
        this.nombreBaseDatos = nombreBaseDatos;
    }
    
    public Integer getPuerto() {
        return puerto;
    }
    
    public void setPuerto(Integer puerto) {
        this.puerto = puerto;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
    
    /**
     * Obtiene la contraseña decodificada.
     */
    public String getPassword() {
        if (passwordEncoded == null || passwordEncoded.isEmpty()) {
            return "";
        }
        try {
            return new String(Base64.getDecoder().decode(passwordEncoded));
        } catch (IllegalArgumentException e) {
            return passwordEncoded; // Si no está codificada, retornar como está
        }
    }
    
    /**
     * Guarda la contraseña codificada en Base64.
     */
    public void setPassword(String password) {
        if (password == null || password.isEmpty()) {
            this.passwordEncoded = "";
        } else {
            this.passwordEncoded = Base64.getEncoder().encodeToString(password.getBytes());
        }
    }
    
    public String getPasswordEncoded() {
        return passwordEncoded;
    }
    
    public void setPasswordEncoded(String passwordEncoded) {
        this.passwordEncoded = passwordEncoded;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
    
    public String getTipoBaseDatos() {
        return tipoBaseDatos;
    }
    
    public void setTipoBaseDatos(String tipoBaseDatos) {
        this.tipoBaseDatos = tipoBaseDatos;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    // ================== MÉTODOS ÚTILES ==================
    
    /**
     * Construye la URL JDBC completa basándose en los campos individuales.
     * Ejemplo: jdbc:postgresql://localhost:5432/lockereasy
     */
    public String construirUrlJDBC() {
        if (urlConexion != null && urlConexion.startsWith("jdbc:")) {
            return urlConexion; // Ya es una URL JDBC completa
        }
        
        String host = urlConexion != null ? urlConexion : "localhost";
        int p = puerto != null ? puerto : 5432;
        String db = nombreBaseDatos != null ? nombreBaseDatos : "lockereasy";
        String tipo = tipoBaseDatos != null ? tipoBaseDatos : "postgresql";
        
        return String.format("jdbc:%s://%s:%d/%s", tipo, host, p, db);
    }
    
    /**
     * Verifica si esta credencial tiene la información mínima para conectarse.
     */
    public boolean esValida() {
        return urlConexion != null && !urlConexion.trim().isEmpty() &&
               usuario != null && !usuario.trim().isEmpty();
    }

    @Override
    public String toString() {
        return nombrePerfil + " (" + (activo ? "✓ Activo" : "Inactivo") + ")";
    }
}
