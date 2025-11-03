import backend.controllers.*;
import backend.model.*;
import java.util.*;

/**
 * Ejemplos de uso del sistema LockerEasy
 * Casos de uso específicos para cada requisito funcional
 */
public class EjemplosUso {
    
    public static void main(String[] args) {
        LockerEasyController sistema = new LockerEasyController();
        
        // Descomentar el ejemplo que deseas ejecutar:
        
        // ejemploFR1_RegistroRentas(sistema);
        // ejemploFR2_FinalizarCancelar(sistema);
        // ejemploFR3_Descuentos(sistema);
        // ejemploFR4_Ventas(sistema);
        // ejemploFR5_GestionPrecios(sistema);
        // ejemploFR6_Transacciones(sistema);
        ejemploCompleto(sistema);
    }

    /**
     * Ejemplo FR1: Registro y gestión de rentas
     */
    public static void ejemploFR1_RegistroRentas(LockerEasyController sistema) {
        System.out.println("=== EJEMPLO FR1: REGISTRO DE RENTAS ===\n");
        
        // Ver lockers disponibles
        System.out.println("Lockers disponibles:");
        List<Locker> disponibles = sistema.obtenerLockersDisponibles();
        for (Locker locker : disponibles) {
            System.out.println("  - Locker #" + locker.getId() + 
                             " (" + locker.getUbicacion() + ")");
        }
        
        // Registrar rentas
        System.out.println("\nRegistrando rentas...");
        sistema.registrarRenta("Ana García", 1, 2, 50.0f, "Tarjeta");
        sistema.registrarRenta("Luis Martínez", 3, 4, 50.0f, "Efectivo");
        sistema.registrarRenta("Sofia Ruiz", 5, 1, 50.0f, "Transferencia");
        
        // Ver estado
        sistema.mostrarEstadoSistema();
    }

    /**
     * Ejemplo FR2: Finalizar y cancelar rentas
     */
    public static void ejemploFR2_FinalizarCancelar(LockerEasyController sistema) {
        System.out.println("=== EJEMPLO FR2: FINALIZAR Y CANCELAR RENTAS ===\n");
        
        // Primero registrar rentas
        sistema.registrarRenta("Pedro Sánchez", 1, 3, 50.0f, "Tarjeta");
        sistema.registrarRenta("Laura Torres", 2, 2, 50.0f, "Efectivo");
        
        System.out.println("\n--- Finalizando renta normal ---");
        sistema.finalizarRenta(1);
        
        System.out.println("\n--- Cancelando renta con feedback ---");
        sistema.cancelarRenta(2, "Emergencia familiar", "Laura Torres", 100.0f);
        
        // Intentar cancelar una renta no existente
        System.out.println("\n--- Intentando cancelar renta inexistente ---");
        sistema.cancelarRenta(5, "Test", "Nadie", 0);
    }

    /**
     * Ejemplo FR3: Aplicación de descuentos
     */
    public static void ejemploFR3_Descuentos(LockerEasyController sistema) {
        System.out.println("=== EJEMPLO FR3: DESCUENTOS Y PROMOCIONES ===\n");
        
        // Mostrar descuentos disponibles
        sistema.mostrarDescuentos();
        
        // Crear una renta
        Renta renta = sistema.registrarRenta("Carlos Díaz", 1, 5, 50.0f, "Tarjeta");
        
        if (renta != null) {
            // Crear servicio
            Servicio servicio = new Servicio(
                1, 
                renta, 
                null, 
                renta.calcularTotalRenta()
            );
            
            System.out.println("\n--- Aplicando descuento ESTUDIANTE ---");
            sistema.aplicarDescuento(servicio, "ESTUDIANTE");
            
            System.out.println("\n--- Aplicando descuento CLIENTE_FRECUENTE ---");
            sistema.aplicarDescuento(servicio, "CLIENTE_FRECUENTE");
        }
        
        // Crear nueva promoción
        System.out.println("\n--- Creando promoción personalizada ---");
        sistema.crearPromocion("NAVIDAD2024", "Especial Navidad", 35.0f);
        
        sistema.mostrarDescuentos();
    }

    /**
     * Ejemplo FR4: Registro de ventas vinculadas
     */
    public static void ejemploFR4_Ventas(LockerEasyController sistema) {
        System.out.println("=== EJEMPLO FR4: REGISTRO DE VENTAS ===\n");
        
        // Mostrar catálogo
        sistema.getVentaController().mostrarCatalogo();
        
        // Cliente 1: Renta locker y compra productos
        System.out.println("\n--- Cliente 1: Renta + Venta ---");
        sistema.registrarRenta("Roberto Cruz", 1, 3, 50.0f, "Tarjeta");
        
        List<String> compra1 = Arrays.asList("Candado", "Agua", "Snack");
        sistema.registrarVenta("Roberto Cruz", compra1, "Tarjeta");
        
        // Cliente 2: Solo compra productos
        System.out.println("\n--- Cliente 2: Solo venta ---");
        List<String> compra2 = Arrays.asList("Bebida Energética", "Cargador USB");
        sistema.registrarVenta("Elena Mora", compra2, "Efectivo");
        
        // Obtener ventas de un cliente
        System.out.println("\n--- Historial de compras de Roberto Cruz ---");
        List<Venta> ventasRoberto = sistema.getVentaController()
            .obtenerVentasPorCliente("Roberto Cruz");
        for (Venta v : ventasRoberto) {
            System.out.println("  Productos: " + v.getProductos());
            System.out.println("  Total: $" + v.calcularTotalVenta());
        }
    }

    /**
     * Ejemplo FR5: Gestión de precios
     */
    public static void ejemploFR5_GestionPrecios(LockerEasyController sistema) {
        System.out.println("=== EJEMPLO FR5: GESTIÓN DE PRECIOS ===\n");
        
        System.out.println("--- Catálogo original ---");
        sistema.getVentaController().mostrarCatalogo();
        
        // Actualizar precios existentes
        System.out.println("\n--- Actualizando precios ---");
        sistema.actualizarPrecioProducto("Agua", 20.0f);
        sistema.actualizarPrecioProducto("Snack", 30.0f);
        
        // Agregar nuevos productos
        System.out.println("\n--- Agregando nuevos productos ---");
        sistema.agregarProducto("Audífonos", 450.0f);
        sistema.agregarProducto("Batería Portátil", 550.0f);
        sistema.agregarProducto("Cable Lightning", 180.0f);
        
        // Eliminar producto
        System.out.println("\n--- Eliminando producto ---");
        sistema.getVentaController().eliminarProducto("Candado");
        
        System.out.println("\n--- Catálogo actualizado ---");
        sistema.getVentaController().mostrarCatalogo();
    }

    /**
     * Ejemplo FR6: Transacciones financieras
     */
    public static void ejemploFR6_Transacciones(LockerEasyController sistema) {
        System.out.println("=== EJEMPLO FR6: TRANSACCIONES FINANCIERAS ===\n");
        
        // Generar varias transacciones
        System.out.println("--- Generando transacciones ---");
        
        // Rentas
        sistema.registrarRenta("Cliente A", 1, 3, 50.0f, "Tarjeta");
        sistema.registrarRenta("Cliente B", 2, 5, 50.0f, "Efectivo");
        
        // Ventas
        sistema.registrarVenta("Cliente A", Arrays.asList("Agua", "Snack"), "Tarjeta");
        sistema.registrarVenta("Cliente C", Arrays.asList("Cargador USB"), "Transferencia");
        
        // Pagos adicionales
        sistema.getTransaccionController().registrarPago(
            "Cliente A", 
            75.0f, 
            "Tiempo extra", 
            "Tarjeta"
        );
        
        // Cancelación
        sistema.cancelarRenta(2, "Cliente no satisfecho", "Cliente B", 250.0f);
        
        // Ver historial
        System.out.println("\n--- Historial completo ---");
        sistema.mostrarHistorialTransacciones(0);
        
        // Generar reporte
        sistema.generarReporteFinanciero();
    }

    /**
     * Ejemplo completo: Flujo de trabajo típico
     */
    public static void ejemploCompleto(LockerEasyController sistema) {
        System.out.println("=== EJEMPLO COMPLETO: FLUJO DE TRABAJO ===\n");
        
        // 1. Cliente llega y renta un locker
        System.out.println(">>> PASO 1: Cliente renta locker <<<");
        Renta renta = sistema.registrarRenta("María Fernández", 3, 4, 50.0f, "Tarjeta");
        
        // 2. Cliente compra productos
        System.out.println("\n>>> PASO 2: Cliente compra productos <<<");
        List<String> productos = Arrays.asList("Candado", "Agua", "Bebida Energética");
        Venta venta = sistema.registrarVenta("María Fernández", productos, "Tarjeta");
        
        // 3. Crear ticket con descuento
        System.out.println("\n>>> PASO 3: Generando ticket con descuento <<<");
        Ticket ticket = sistema.crearTicketCompleto(
            "María Fernández",
            "maria@email.com",
            renta,
            venta,
            "ESTUDIANTE"
        );
        
        if (ticket != null) {
            sistema.imprimirTicket(ticket.getTicketId());
        }
        
        // 4. Cliente finaliza su renta
        System.out.println(">>> PASO 4: Cliente finaliza renta <<<");
        sistema.finalizarRenta(3);
        
        // 5. Ver resumen final
        System.out.println("\n>>> PASO 5: Resumen del sistema <<<");
        sistema.mostrarEstadoSistema();
        sistema.generarReporteFinanciero();
    }

    /**
     * Prueba de prevención de doble reserva
     */
    public static void pruebaDobleReserva(LockerEasyController sistema) {
        System.out.println("=== PRUEBA: PREVENCIÓN DOBLE RESERVA ===\n");
        
        // Primera reserva exitosa
        System.out.println("--- Reserva 1 ---");
        sistema.registrarRenta("Cliente 1", 1, 2, 50.0f, "Tarjeta");
        
        // Intento de doble reserva (debe fallar)
        System.out.println("\n--- Intento de doble reserva ---");
        sistema.registrarRenta("Cliente 2", 1, 3, 50.0f, "Efectivo");
        
        // Después de finalizar, debe permitir nueva reserva
        System.out.println("\n--- Finalizando primera reserva ---");
        sistema.finalizarRenta(1);
        
        System.out.println("\n--- Nueva reserva (debe funcionar) ---");
        sistema.registrarRenta("Cliente 2", 1, 3, 50.0f, "Efectivo");
    }
}
