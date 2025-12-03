package controller;

import dao.ConfiguracionDAO;

/**
 * Clase de configuración que persiste valores en la base de datos.
 * Los valores se cargan al iniciar y se guardan automáticamente al modificarlos.
 */
public class Config {
    private static ConfiguracionDAO dao = null;
    
    // Valores en memoria (cache)
    private static float precio_hora_locker = 50.0f;
    private static int minutos_tolerancia = 15;
    private static int minutos_cancelacion = 5;
    private static float descuento_unico = 10.0f;
    
    private static boolean inicializado = false;
    
    /**
     * Inicializa la configuración cargando valores desde la base de datos.
     * Debe llamarse una vez al inicio de la aplicación.
     * NO crea valores por defecto automáticamente - el usuario debe configurarlos.
     */
    public static void inicializar() {
        if (inicializado) return;
        
        dao = new ConfiguracionDAO();
        
        // Solo cargar valores desde BD (NO crear valores por defecto)
        cargarDesdeDB();
        
        inicializado = true;
    }
    
    /**
     * Verifica si existe configuración guardada en la base de datos.
     * Útil para detectar primera ejecución.
     */
    public static boolean existeConfiguracion() {
        if (dao == null) dao = new ConfiguracionDAO();
        return dao.obtener("precio_hora_locker") != null;
    }
    
    /**
     * Carga todos los valores de configuración desde la base de datos.
     * Si no existe configuración, usa -1 como marcador (para detectar primera ejecución).
     */
    public static void cargarDesdeDB() {
        if (dao == null) dao = new ConfiguracionDAO();
        
        // Usar -1 como valor especial para indicar "no configurado"
        precio_hora_locker = dao.obtenerValorFloat("precio_hora_locker", -1f);
        minutos_tolerancia = dao.obtenerValorInt("minutos_tolerancia", -1);
        minutos_cancelacion = dao.obtenerValorInt("minutos_cancelacion", -1);
        descuento_unico = dao.obtenerValorFloat("descuento_unico", -1f);
    }
    
    // ================== GETTERS ==================
    public static float getPrecioHoraLocker() { return precio_hora_locker; }
    public static int getMinutosTolerancia() { return minutos_tolerancia; }
    public static int getMinutosCancelacion() { return minutos_cancelacion; }
    public static float getDescuentoUnico() { return descuento_unico; }

    // ================== SETTERS (guardan en BD automáticamente) ==================
    public static void setPrecioHoraLocker(float precio) { 
        precio_hora_locker = precio;
        guardarEnDB("precio_hora_locker", precio, "Precio por hora de renta de locker");
    }
    
    public static void setMinutosTolerancia(int minutos) { 
        minutos_tolerancia = minutos;
        guardarEnDB("minutos_tolerancia", minutos, "Minutos de tolerancia antes de cobrar hora extra");
    }
    
    public static void setMinutosCancelacion(int minutos) { 
        minutos_cancelacion = minutos;
        guardarEnDB("minutos_cancelacion", minutos, "Minutos máximos para cancelar sin cargo");
    }
    
    public static void setDescuentoUnico(float descuento) { 
        descuento_unico = descuento;
        guardarEnDB("descuento_unico", descuento, "Porcentaje de descuento único aplicable");
    }
    
    // ================== HELPERS ==================
    private static void guardarEnDB(String clave, float valor, String descripcion) {
        if (dao == null) dao = new ConfiguracionDAO();
        dao.guardarValorFloat(clave, valor, descripcion);
    }
    
    private static void guardarEnDB(String clave, int valor, String descripcion) {
        if (dao == null) dao = new ConfiguracionDAO();
        dao.guardarValorInt(clave, valor, descripcion);
    }
}
