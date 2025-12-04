package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import model.*;
import java.time.LocalDateTime;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;

/**
 * Sistema de logging centralizado para LockerEasy
 * Proporciona informacion estructurada para debugging y análisis
 */
public class AppLogger {
    private static final Logger dbLogger;
    private static final Logger appLogger;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss");
    
    static {
        // Configurar la ruta de logs ANTES de inicializar los loggers
        configurarRutaLogs();
        
        dbLogger = LoggerFactory.getLogger("dao");
        appLogger = LoggerFactory.getLogger("LockerEasy");
    }
    
    /**
     * Configura la ruta correcta de logs independientemente desde dónde se ejecute
     */
    private static void configurarRutaLogs() {
        try {
            String userDir = System.getProperty("user.dir");
            Path logsPath;
            
            // Detectar si estamos en la carpeta LockerEasy o fuera de ella
            if (userDir.endsWith("LockerEasy") || userDir.contains("LockerEasy\\") || userDir.contains("LockerEasy/")) {
                // Ya estamos en LockerEasy o dentro de ella
                if (userDir.endsWith("LockerEasy")) {
                    logsPath = Paths.get(userDir, "logs");
                } else {
                    // Estamos dentro de LockerEasy, subir al directorio raíz
                    String lockerEasyPath = userDir.substring(0, userDir.lastIndexOf("LockerEasy") + "LockerEasy".length());
                    logsPath = Paths.get(lockerEasyPath, "logs");
                }
            } else {
                // Estamos fuera, buscar la carpeta LockerEasy
                logsPath = Paths.get(userDir, "LockerEasy", "logs");
            }
            
            // Crear directorio si no existe
            if (!Files.exists(logsPath)) {
                Files.createDirectories(logsPath);
            }
            
            // Establecer la propiedad del sistema para que logback la use
            System.setProperty("LOG_HOME", logsPath.toString());
            
        } catch (Exception e) {
            System.err.println("Error al configurar ruta de logs: " + e.getMessage());
            e.printStackTrace();
            // Fallback a ruta relativa
            System.setProperty("LOG_HOME", "logs");
        }
    }
    
    // ========== VENTAS ==========
    
    public static void ventaAgregada(int id, String producto, int ticketId, double precio, int cantidad) {
        dbLogger.info("Venta agregada a la base de datos ID: {} Producto: [{}] Precio: ${} Cantidad: {} ticket_id: {}", 
            id, producto, precio, cantidad, ticketId);
    }
    
    public static void ventaEliminada(int id, String producto, int ticketId) {
        dbLogger.info("Venta eliminada de la base de datos ID: {} Producto: [{}] ticket_id: {}", 
            id, producto, ticketId);
    }
    
    public static void ventaActualizada(int id, String producto, int stockAnterior, int stockNuevo) {
        dbLogger.info("Venta actualizada ID: {} Producto: [{}] Stock: {} -> {}", 
            id, producto, stockAnterior, stockNuevo);
    }
    
    public static void inventarioBajo(String producto, int cantidad, int minimo) {
        appLogger.warn("INVENTARIO BAJO - Producto: [{}] Cantidad actual: {} (Mínimo: {})", 
            producto, cantidad, minimo);
    }
    
    // ========== RENTAS ==========
    
    public static void rentaAgregada(int id, String locker, int ticketId, double precio, Instant inicio) {
        String ubicacion = formatearUbicacion(locker);
        dbLogger.info("Renta agregada a la base de datos ID: {} Locker: [{}] Precio: ${} Inicio: {} ticket_id: {}", 
            id, ubicacion, precio, inicio.toString(), ticketId);
    }
    
    public static void rentaEliminada(int id, String locker, int ticketId) {
        String ubicacion = formatearUbicacion(locker);
        dbLogger.info("Renta eliminada de la base de datos ID: {} Locker: [{}] ticket_id: {}", 
            id, ubicacion, ticketId);
    }
    
    public static void rentaCerrada(int id, String locker, Instant inicio, Instant cierre, double costoTotal) {
        String ubicacion = formatearUbicacion(locker);
        long horas = java.time.Duration.between(inicio, cierre).toHours();
        dbLogger.info("Renta cerrada ID: {} Locker: [{}] Duracion: {}h Costo total: ${}", 
            id, ubicacion, horas, costoTotal);
    }
    
    public static void rentaExpirada(int id, String locker, long horasActivas) {
        String ubicacion = formatearUbicacion(locker);
        appLogger.warn("RENTA EXPIRADA - ID: {} Locker: [{}] Tiempo activo: {}h", 
            id, ubicacion, horasActivas);
    }
    
    // ========== TICKETS ==========
    
    public static void ticketCreado(int id, String cliente, String correo, int reporteId, double total) {
        dbLogger.info("Ticket agregado a la base de datos ID: {} Cliente: [{}] Correo: [{}] Total: ${} reporte_id: {}", 
            id, cliente, correo, total, reporteId);
    }
    
    public static void ticketEliminado(int id, String cliente, int reporteId, int numServicios) {
        dbLogger.info("Ticket eliminado de la base de datos ID: {} Cliente: [{}] Servicios eliminados: {} reporte_id: {}", 
            id, cliente, numServicios, reporteId);
    }
    
    public static void ticketActualizado(int id, double totalAnterior, double totalNuevo, int numServicios) {
        dbLogger.info("Ticket actualizado ID: {} Total: ${} -> ${} Servicios: {}", 
            id, totalAnterior, totalNuevo, numServicios);
    }
    
    public static void ticketSinServicios(int id) {
        appLogger.warn("TICKET SIN SERVICIOS - ID: {} (Considerar eliminacion)", id);
    }
    
    // ========== REPORTES ==========
    
    public static void reporteCreado(String fecha, String estado) {
        dbLogger.info("Reporte agregado a la base de datos ID: {} Estado: [{}]", fecha, estado);
    }
    
    public static void reporteEliminado(String fecha, int numTickets) {
        dbLogger.info("Reporte eliminado de la base de datos ID: {} Tickets eliminados: {}", 
            fecha, numTickets);
    }
    
    public static void reporteActualizado(String fecha, double totalAnterior, double totalNuevo, int numTickets) {
        dbLogger.info("Reporte actualizado ID: {} Total: ${} -> ${} Tickets: {}", 
            fecha, totalAnterior, totalNuevo, numTickets);
    }
    
    public static void reporteCerrado(String fecha, double totalFinal, int totalTickets, int totalServicios) {
        dbLogger.info("Reporte cerrado ID: {} Total final: ${} Tickets: {} Servicios: {}", 
            fecha, totalFinal, totalTickets, totalServicios);
    }
    
    // ========== SERVICIOS ==========
    
    public static void servicioAgregado(int id, String tipo, int tipoServicioId, int ticketId, double total) {
        dbLogger.info("Servicio agregado ID: {} Tipo: [{}] tipo_servicio_id: {} ticket_id: {} Total: ${}", 
            id, tipo, tipoServicioId, ticketId, total);
    }
    
    public static void servicioEliminado(int id, String tipo, int ticketId) {
        dbLogger.info("Servicio eliminado ID: {} Tipo: [{}] ticket_id: {}", 
            id, tipo, ticketId);
    }
    
    public static void descuentoAplicado(int servicioId, double descuento, double totalAnterior, double totalNuevo) {
        dbLogger.info("Descuento aplicado Servicio ID: {} Descuento: {}% Total: ${} -> ${}", 
            servicioId, descuento, totalAnterior, totalNuevo);
    }
    
    // ========== ETIQUETAS ==========
    
    public static void etiquetaCreada(int id, String nombre, boolean afectaInventario) {
        dbLogger.info("Etiqueta agregada ID: {} Nombre: [{}] Afecta inventario: {}", 
            id, nombre, afectaInventario);
    }
    
    public static void etiquetaEliminada(int id, String nombre, int productosAfectados) {
        dbLogger.info("Etiqueta eliminada ID: {} Nombre: [{}] Productos afectados: {}", 
            id, nombre, productosAfectados);
    }
    
    // ========== UBICACIONES ==========
    
    public static void ubicacionCreada(int id, String torre, String locker, String localizacion) {
        dbLogger.info("Ubicacion creada ID: {} Torre: [{}] Locker: [{}] Localizacion: [{}]", 
            id, torre, locker, localizacion);
    }
    
    public static void ubicacionActualizada(int id, String ubicacion, boolean disponibleAntes, boolean disponibleAhora) {
        String cambio = disponibleAntes != disponibleAhora ? 
            (disponibleAhora ? "ocupada -> disponible" : "disponible -> ocupada") : 
            "sin cambios";
        dbLogger.info("Ubicacion actualizada ID: {} [{}] Estado: [{}]", 
            id, ubicacion, cambio);
    }
    
    public static void ubicacionesDisponibles(int total, int disponibles, int ocupadas) {
        appLogger.info("ESTADO UBICACIONES - Total: {} Disponibles: {} Ocupadas: {}", 
            total, disponibles, ocupadas);
    }
    
    // ========== OPERACIONES BATCH ==========
    
    public static void operacionBatchIniciada(String operacion, int cantidad) {
        appLogger.info("OPERACION BATCH INICIADA - Tipo: [{}] Cantidad: {}", operacion, cantidad);
    }
    
    public static void operacionBatchCompletada(String operacion, int exitosos, int fallidos, long duracionMs) {
        appLogger.info("OPERACION BATCH COMPLETADA - Tipo: [{}] Exitosos: {} Fallidos: {} Duracion: {}ms", 
            operacion, exitosos, fallidos, duracionMs);
    }
    
    // ========== TRANSACCIONES ==========
    
    public static void transaccionIniciada(String operacion, String detalles) {
        appLogger.debug("TRANSACCION INICIADA - Operacion: [{}] Detalles: {}", operacion, detalles);
    }
    
    public static void transaccionCommit(String operacion, long duracionMs) {
        appLogger.debug("TRANSACCION COMMIT - Operacion: [{}] Duracion: {}ms", operacion, duracionMs);
    }
    
    public static void transaccionRollback(String operacion, String razon) {
        appLogger.warn("TRANSACCION ROLLBACK - Operacion: [{}] Razon: {}", operacion, razon);
    }
    
    // ========== ERRORES Y EXCEPCIONES ==========
    
    public static void errorDB(String operacion, String entidad, Integer id, Exception e) {
        dbLogger.error("ERROR DATABASE - Operacion: [{}] Entidad: [{}] ID: {} Error: {}", 
            operacion, entidad, id, e.getMessage());
    }
    
    public static void errorDBDetallado(String operacion, String entidad, Integer id, Exception e) {
        dbLogger.error("ERROR DATABASE DETALLADO - Operacion: [{}] Entidad: [{}] ID: {}", 
            operacion, entidad, id, e);
    }
    
    public static void errorValidacion(String campo, String valorInvalido, String razon) {
        appLogger.error("ERROR VALIDACION - Campo: [{}] Valor: [{}] Razon: {}", 
            campo, valorInvalido, razon);
    }
    
    public static void errorNegocio(String operacion, String razon) {
        appLogger.error("ERROR LOGICA NEGOCIO - Operacion: [{}] Razon: {}", operacion, razon);
    }
    
    public static void excepcionNoControlada(String ubicacion, Exception e) {
        appLogger.error("EXCEPCION NO CONTROLADA - Ubicacion: [{}]", ubicacion, e);
    }
    
    // ========== WARNINGS ==========
    
    public static void warningConexion(String mensaje) {
        appLogger.warn("WARNING CONEXION DB - {}", mensaje);
    }
    
    public static void warningDatosInconsistentes(String entidad, Integer id, String inconsistencia) {
        appLogger.warn("WARNING DATOS INCONSISTENTES - Entidad: [{}] ID: {} Problema: {}", 
            entidad, id, inconsistencia);
    }
    
    public static void warningOperacionLenta(String operacion, long duracionMs, long esperadoMs) {
        appLogger.warn("WARNING OPERACION LENTA - Operacion: [{}] Duracion: {}ms (Esperado: {}ms)", 
            operacion, duracionMs, esperadoMs);
    }
    
    // ========== SESION E INICIALIZACION ==========
    
    public static void sistemaIniciado(String version) {
        appLogger.info("========== SISTEMA LOCKEREASY INICIADO v{} ==========", version);
    }
    
    public static void sistemaCerrado() {
        appLogger.info("========== SISTEMA LOCKEREASY CERRADO ==========");
    }
    
    public static void conexionDBEstablecida(String url, String usuario) {
        appLogger.info("Conexion a base de datos establecida - URL: {} Usuario: {}", url, usuario);
    }
    
    public static void conexionDBCerrada() {
        appLogger.info("Conexion a base de datos cerrada");
    }
    
    public static void sessionFactoryCreada() {
        appLogger.info("Hibernate SessionFactory creada correctamente");
    }
    
    // ========== ESTADISTICAS ==========
    
    public static void estadisticasDiarias(int tickets, int rentas, int ventas, double totalIngresos) {
        appLogger.info("ESTADISTICAS DIARIAS - Tickets: {} Rentas: {} Ventas: {} Ingresos: ${}", 
            tickets, rentas, ventas, totalIngresos);
    }
    
    // ========== UTILIDADES ==========
    
    private static String formatearUbicacion(String locker) {
        // Si el locker ya viene formateado, devolverlo tal cual
        // Si viene como objeto Ubicacion, parsearlo
        return locker;
    }
    
    public static void debug(String mensaje, Object... args) {
        appLogger.debug(mensaje, args);
    }
    
    public static void info(String mensaje, Object... args) {
        appLogger.info(mensaje, args);
    }
}