import backend.controllers.*;
import backend.model.*;
import java.util.*;

/**
 * Aplicación LockerEasy - Sistema de gestión de lockers
 * Demostración de todos los requisitos funcionales (FR1-FR6)
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("    LOCKEREASE - Sistema de Gestión    ");
        System.out.println("========================================\n");

        // Inicializar el controller principal
        LockerEasyController sistema = new LockerEasyController();

        // ===== DEMO FR1: Registrar rentas =====
        System.out.println("\n>>> DEMO FR1: Registro de Rentas <<<");
        System.out.println("------------------------------------");
        
        Renta renta1 = sistema.registrarRenta("Juan Pérez", 1, 3, 50.0f, "Tarjeta");
        Renta renta2 = sistema.registrarRenta("María López", 2, 5, 50.0f, "Efectivo");
        
        // Intentar rentar un locker ocupado (debe fallar)
        System.out.println("\n[Intentando rentar locker ya ocupado...]");
        sistema.registrarRenta("Carlos Gómez", 1, 2, 50.0f, "Tarjeta");

        // ===== DEMO FR4: Registrar ventas vinculadas a clientes =====
        System.out.println("\n\n>>> DEMO FR4: Registro de Ventas <<<");
        System.out.println("------------------------------------");
        
        List<String> productosJuan = Arrays.asList("Candado", "Agua", "Snack");
        Venta venta1 = sistema.registrarVenta("Juan Pérez", productosJuan, "Tarjeta");
        
        List<String> productosMaria = Arrays.asList("Bebida Energética", "Snack");
        Venta venta2 = sistema.registrarVenta("María López", productosMaria, "Efectivo");

        // ===== DEMO FR3: Aplicar descuentos =====
        System.out.println("\n\n>>> DEMO FR3: Aplicación de Descuentos <<<");
        System.out.println("------------------------------------------");
        
        sistema.mostrarDescuentos();
        
        // Crear servicios y aplicar descuentos
        if (renta1 != null) {
            Servicio servicioConDescuento = new Servicio(1, renta1, null, renta1.calcularTotalRenta());
            sistema.aplicarDescuento(servicioConDescuento, "ESTUDIANTE");
        }
        
        // Crear nueva promoción
        System.out.println("\n[Creando nueva promoción...]");
        sistema.crearPromocion("BFRIDAY", "Black Friday", 30.0f);

        // ===== DEMO FR5: Gestión de precios =====
        System.out.println("\n\n>>> DEMO FR5: Gestión de Precios <<<");
        System.out.println("------------------------------------");
        
        sistema.getVentaController().mostrarCatalogo();
        
        System.out.println("\n[Actualizando precios...]");
        sistema.actualizarPrecioProducto("Agua", 18.0f);
        sistema.agregarProducto("Powerbank", 350.0f);
        
        System.out.println("\n[Catálogo actualizado:]");
        sistema.getVentaController().mostrarCatalogo();

        // ===== DEMO: Crear tickets completos =====
        System.out.println("\n\n>>> DEMO: Creación de Tickets <<<");
        System.out.println("---------------------------------");
        
        Ticket ticket1 = sistema.crearTicketCompleto(
            "Juan Pérez", 
            "juan@email.com", 
            renta1, 
            venta1, 
            "ESTUDIANTE"
        );
        
        if (ticket1 != null) {
            sistema.imprimirTicket(ticket1.getTicketId());
        }

        // ===== DEMO FR2: Finalizar y cancelar rentas =====
        System.out.println("\n>>> DEMO FR2: Finalizar y Cancelar Rentas <<<");
        System.out.println("----------------------------------------------");
        
        System.out.println("\n[Finalizando renta del locker 1...]");
        sistema.finalizarRenta(1);
        
        System.out.println("\n[Cancelando renta del locker 2...]");
        sistema.cancelarRenta(2, "Cliente necesita salir antes de tiempo", "María López", 250.0f);

        // ===== DEMO FR6: Transacciones financieras =====
        System.out.println("\n\n>>> DEMO FR6: Registro de Transacciones <<<");
        System.out.println("-------------------------------------------");
        
        // Registrar un pago adicional
        sistema.getTransaccionController().registrarPago(
            "Juan Pérez", 
            100.0f, 
            "Pago por tiempo extra", 
            "Tarjeta"
        );
        
        // Mostrar historial
        System.out.println("\n[Historial reciente de transacciones:]");
        sistema.mostrarHistorialTransacciones(5);
        
        // Generar reporte financiero
        sistema.generarReporteFinanciero();

        // ===== Estado final del sistema =====
        System.out.println("\n>>> Estado Final del Sistema <<<");
        System.out.println("--------------------------------");
        sistema.mostrarEstadoSistema();
        
        System.out.println("\nLockers disponibles:");
        for (Locker locker : sistema.obtenerLockersDisponibles()) {
            System.out.println("  - Locker #" + locker.getId() + " (" + 
                             locker.getUbicacion() + ")");
        }

        System.out.println("\n========================================");
        System.out.println("       Demo completada exitosamente     ");
        System.out.println("========================================");
    }
}
