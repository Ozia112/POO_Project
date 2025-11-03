import backend.controllers.*;
import backend.model.*;
import java.util.*;

/**
 * Pruebas básicas del sistema LockerEasy
 * Verifica el funcionamiento de cada requisito funcional
 */
public class PruebasSistema {
    
    private static int pruebasExitosas = 0;
    private static int pruebasFallidas = 0;

    public static void main(String[] args) {
        System.out.println("======================================");
        System.out.println("  PRUEBAS DEL SISTEMA LOCKEREASY");
        System.out.println("======================================\n");

        LockerEasyController sistema = new LockerEasyController();

        // Ejecutar todas las pruebas
        probarFR1_RegistroRentas(sistema);
        probarFR2_FinalizarCancelar(sistema);
        probarFR3_Descuentos(sistema);
        probarFR4_Ventas(sistema);
        probarFR5_GestionPrecios(sistema);
        probarFR6_Transacciones(sistema);
        probarPrevencionDobleReserva(sistema);

        // Resumen
        System.out.println("\n======================================");
        System.out.println("         RESUMEN DE PRUEBAS");
        System.out.println("======================================");
        System.out.println("✅ Pruebas exitosas: " + pruebasExitosas);
        System.out.println("❌ Pruebas fallidas: " + pruebasFallidas);
        System.out.println("Total: " + (pruebasExitosas + pruebasFallidas));
        
        if (pruebasFallidas == 0) {
            System.out.println("\n🎉 ¡Todas las pruebas pasaron exitosamente!");
        } else {
            System.out.println("\n⚠️  Algunas pruebas fallaron. Revisar el código.");
        }
    }

    private static void probarFR1_RegistroRentas(LockerEasyController sistema) {
        System.out.println("\n--- PRUEBA FR1: Registro de Rentas ---");
        
        // Prueba 1: Registrar renta válida
        Renta renta = sistema.registrarRenta("Test Cliente", 7, 3, 50.0f, "Tarjeta");
        verificar("FR1.1: Registrar renta válida", renta != null);
        
        // Prueba 2: Verificar que el locker está ocupado
        Locker locker = sistema.getRentaController().obtenerLocker(7);
        verificar("FR1.2: Locker marcado como ocupado", locker != null && locker.estaOcupado());
        
        // Prueba 3: Verificar cálculo de total
        if (renta != null) {
            float totalEsperado = 150.0f; // 3 horas * 50 por hora
            verificar("FR1.3: Cálculo correcto del total", 
                     Math.abs(renta.calcularTotalRenta() - totalEsperado) < 0.01);
        }
    }

    private static void probarFR2_FinalizarCancelar(LockerEasyController sistema) {
        System.out.println("\n--- PRUEBA FR2: Finalizar y Cancelar Rentas ---");
        
        // Preparar: Registrar renta
        sistema.registrarRenta("Cliente FR2", 8, 2, 50.0f, "Efectivo");
        
        // Prueba 1: Finalizar renta
        boolean finalizada = sistema.finalizarRenta(8);
        verificar("FR2.1: Finalizar renta exitosamente", finalizada);
        
        // Prueba 2: Verificar locker disponible
        Locker locker = sistema.getRentaController().obtenerLocker(8);
        verificar("FR2.2: Locker liberado después de finalizar", 
                 locker != null && !locker.estaOcupado());
        
        // Prueba 3: Cancelar renta
        sistema.registrarRenta("Cliente FR2-2", 9, 2, 50.0f, "Tarjeta");
        boolean cancelada = sistema.cancelarRenta(9, "Prueba", "Cliente FR2-2", 100.0f);
        verificar("FR2.3: Cancelar renta con feedback", cancelada);
        
        // Prueba 4: Prevención de doble reserva (implícita en las pruebas anteriores)
        Locker locker9 = sistema.getRentaController().obtenerLocker(9);
        verificar("FR2.4: Locker liberado después de cancelar", 
                 locker9 != null && !locker9.estaOcupado());
    }

    private static void probarFR3_Descuentos(LockerEasyController sistema) {
        System.out.println("\n--- PRUEBA FR3: Descuentos y Promociones ---");
        
        // Prueba 1: Verificar descuentos predefinidos
        Descuento descuento = sistema.getDescuentoController().obtenerDescuento("ESTUDIANTE");
        verificar("FR3.1: Descuento ESTUDIANTE existe", descuento != null);
        
        // Prueba 2: Aplicar descuento
        Renta renta = new Renta(1, "Test", null, 50.0f, 1, null, 2);
        Servicio servicio = new Servicio(1, renta, null, 100.0f);
        Servicio conDescuento = sistema.aplicarDescuento(servicio, "ESTUDIANTE");
        verificar("FR3.2: Aplicar descuento correctamente", conDescuento != null);
        
        // Prueba 3: Verificar cálculo de descuento
        if (descuento != null) {
            float montoOriginal = 100.0f;
            float montoEsperado = descuento.aplicar(montoOriginal);
            verificar("FR3.3: Cálculo correcto del descuento", 
                     Math.abs(montoEsperado - 85.0f) < 0.01); // 15% de 100 = 85
        }
        
        // Prueba 4: Crear nueva promoción
        boolean creada = sistema.crearPromocion("TEST2024", "Test", 20.0f);
        verificar("FR3.4: Crear nueva promoción", creada);
        
        // Prueba 5: Validar código
        boolean valido = sistema.getDescuentoController().validarCodigo("TEST2024");
        verificar("FR3.5: Validar código de descuento", valido);
    }

    private static void probarFR4_Ventas(LockerEasyController sistema) {
        System.out.println("\n--- PRUEBA FR4: Registro de Ventas ---");
        
        // Prueba 1: Registrar venta válida
        List<String> productos = Arrays.asList("Agua", "Snack");
        Venta venta = sistema.registrarVenta("Cliente Venta", productos, "Tarjeta");
        verificar("FR4.1: Registrar venta válida", venta != null);
        
        // Prueba 2: Verificar vinculación con cliente
        if (venta != null) {
            verificar("FR4.2: Venta vinculada a cliente", 
                     venta.getNombre().equals("Cliente Venta"));
        }
        
        // Prueba 3: Obtener ventas por cliente
        List<Venta> ventasCliente = sistema.getVentaController()
            .obtenerVentasPorCliente("Cliente Venta");
        verificar("FR4.3: Obtener ventas por cliente", ventasCliente.size() > 0);
        
        // Prueba 4: Cálculo de total de venta
        if (venta != null) {
            float total = venta.calcularTotalVenta();
            verificar("FR4.4: Cálculo correcto del total", total > 0);
        }
    }

    private static void probarFR5_GestionPrecios(LockerEasyController sistema) {
        System.out.println("\n--- PRUEBA FR5: Gestión de Precios ---");
        
        // Prueba 1: Actualizar precio existente
        boolean actualizado = sistema.actualizarPrecioProducto("Agua", 18.0f);
        verificar("FR5.1: Actualizar precio de producto", actualizado);
        
        // Prueba 2: Verificar precio actualizado
        Map<String, Float> catalogo = sistema.obtenerCatalogo();
        Float precioAgua = catalogo.get("Agua");
        verificar("FR5.2: Precio actualizado correctamente", 
                 precioAgua != null && Math.abs(precioAgua - 18.0f) < 0.01);
        
        // Prueba 3: Agregar nuevo producto
        boolean agregado = sistema.agregarProducto("Producto Test", 99.0f);
        verificar("FR5.3: Agregar nuevo producto", agregado);
        
        // Prueba 4: Verificar producto en catálogo
        catalogo = sistema.obtenerCatalogo();
        verificar("FR5.4: Producto agregado al catálogo", 
                 catalogo.containsKey("Producto Test"));
        
        // Prueba 5: Eliminar producto
        boolean eliminado = sistema.getVentaController().eliminarProducto("Producto Test");
        verificar("FR5.5: Eliminar producto", eliminado);
    }

    private static void probarFR6_Transacciones(LockerEasyController sistema) {
        System.out.println("\n--- PRUEBA FR6: Transacciones Financieras ---");
        
        int transaccionesIniciales = sistema.getTransaccionController()
            .obtenerHistorial().size();
        
        // Prueba 1: Registrar transacción de renta
        Renta renta = sistema.registrarRenta("Cliente Trans", 10, 2, 50.0f, "Tarjeta");
        int transaccionesDespuesRenta = sistema.getTransaccionController()
            .obtenerHistorial().size();
        verificar("FR6.1: Transacción de renta registrada", 
                 transaccionesDespuesRenta > transaccionesIniciales);
        
        // Prueba 2: Registrar transacción de venta
        sistema.registrarVenta("Cliente Trans", Arrays.asList("Agua"), "Efectivo");
        int transaccionesDespuesVenta = sistema.getTransaccionController()
            .obtenerHistorial().size();
        verificar("FR6.2: Transacción de venta registrada", 
                 transaccionesDespuesVenta > transaccionesDespuesRenta);
        
        // Prueba 3: Registrar pago
        sistema.getTransaccionController().registrarPago(
            "Cliente Trans", 50.0f, "Pago adicional", "Tarjeta"
        );
        int transaccionesDespuesPago = sistema.getTransaccionController()
            .obtenerHistorial().size();
        verificar("FR6.3: Pago registrado", 
                 transaccionesDespuesPago > transaccionesDespuesVenta);
        
        // Prueba 4: Calcular ingresos totales
        float ingresos = sistema.getTransaccionController().calcularIngresosTotales();
        verificar("FR6.4: Calcular ingresos totales", ingresos > 0);
        
        // Prueba 5: Obtener transacciones por cliente
        List<TransaccionController.Transaccion> transCliente = sistema
            .getTransaccionController().obtenerTransaccionesPorCliente("Cliente Trans");
        verificar("FR6.5: Obtener transacciones por cliente", 
                 transCliente.size() >= 2);
    }

    private static void probarPrevencionDobleReserva(LockerEasyController sistema) {
        System.out.println("\n--- PRUEBA: Prevención de Doble Reserva ---");
        
        // Prueba 1: Primera reserva debe funcionar
        Renta renta1 = sistema.registrarRenta("Cliente 1", 6, 2, 50.0f, "Tarjeta");
        verificar("DOBLE.1: Primera reserva exitosa", renta1 != null);
        
        // Prueba 2: Segunda reserva del mismo locker debe fallar
        Renta renta2 = sistema.registrarRenta("Cliente 2", 6, 2, 50.0f, "Efectivo");
        verificar("DOBLE.2: Segunda reserva rechazada", renta2 == null);
        
        // Prueba 3: Después de finalizar, debe permitir nueva reserva
        sistema.finalizarRenta(6);
        Renta renta3 = sistema.registrarRenta("Cliente 3", 6, 2, 50.0f, "Tarjeta");
        verificar("DOBLE.3: Nueva reserva después de finalizar", renta3 != null);
    }

    // Método auxiliar para verificar pruebas
    private static void verificar(String nombrePrueba, boolean resultado) {
        if (resultado) {
            System.out.println("✅ " + nombrePrueba);
            pruebasExitosas++;
        } else {
            System.out.println("❌ " + nombrePrueba);
            pruebasFallidas++;
        }
    }
}
