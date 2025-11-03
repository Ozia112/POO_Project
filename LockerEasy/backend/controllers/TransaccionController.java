package backend.controllers;

import backend.model.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Controller para gestionar transacciones financieras
 * Cumple con FR6
 */
public class TransaccionController {
    private List<Transaccion> historialTransacciones;
    private int siguienteIdTransaccion;

    // Clase interna para representar una transacción
    public static class Transaccion {
        private int id;
        private TipoTransaccion tipo;
        private String descripcion;
        private float monto;
        private LocalDateTime fecha;
        private String cliente;
        private String metodoPago;

        public Transaccion(int id, TipoTransaccion tipo, String descripcion, float monto, 
                          String cliente, String metodoPago) {
            this.id = id;
            this.tipo = tipo;
            this.descripcion = descripcion;
            this.monto = monto;
            this.fecha = LocalDateTime.now();
            this.cliente = cliente;
            this.metodoPago = metodoPago;
        }

        // Getters
        public int getId() { return id; }
        public TipoTransaccion getTipo() { return tipo; }
        public String getDescripcion() { return descripcion; }
        public float getMonto() { return monto; }
        public LocalDateTime getFecha() { return fecha; }
        public String getCliente() { return cliente; }
        public String getMetodoPago() { return metodoPago; }

        @Override
        public String toString() {
            return String.format("Transaccion #%d | %s | %s | $%.2f | %s | %s",
                id, tipo, cliente, monto, metodoPago, fecha);
        }
    }

    // Enum para tipos de transacción
    public enum TipoTransaccion {
        RENTA("Renta de Locker"),
        VENTA("Venta de Producto"),
        PAGO("Pago"),
        REEMBOLSO("Reembolso"),
        CANCELACION("Cancelación");

        private final String descripcion;

        TipoTransaccion(String descripcion) {
            this.descripcion = descripcion;
        }

        public String getDescripcion() {
            return descripcion;
        }
    }

    public TransaccionController() {
        this.historialTransacciones = new ArrayList<>();
        this.siguienteIdTransaccion = 1;
    }

    /**
     * FR6: Registrar una transacción de renta
     * @param renta La renta a registrar
     * @param metodoPago Método de pago utilizado
     * @return La transacción creada
     */
    public Transaccion registrarTransaccionRenta(Renta renta, String metodoPago) {
        String descripcion = String.format("Renta de locker #%d por %d horas",
            renta.getLocker().getId(), renta.getTiempo());
        
        Transaccion transaccion = new Transaccion(
            siguienteIdTransaccion++,
            TipoTransaccion.RENTA,
            descripcion,
            renta.calcularTotalRenta(),
            renta.getNombre(),
            metodoPago
        );

        historialTransacciones.add(transaccion);
        
        System.out.println("Transacción de renta registrada:");
        System.out.println("- ID: " + transaccion.getId());
        System.out.println("- Cliente: " + transaccion.getCliente());
        System.out.println("- Monto: $" + transaccion.getMonto());
        System.out.println("- Método de pago: " + metodoPago);

        return transaccion;
    }

    /**
     * FR6: Registrar una transacción de venta
     * @param venta La venta a registrar
     * @param metodoPago Método de pago utilizado
     * @return La transacción creada
     */
    public Transaccion registrarTransaccionVenta(Venta venta, String metodoPago) {
        String descripcion = String.format("Venta de %d productos",
            venta.getProductos().size());
        
        Transaccion transaccion = new Transaccion(
            siguienteIdTransaccion++,
            TipoTransaccion.VENTA,
            descripcion,
            venta.calcularTotalVenta(),
            venta.getNombre(),
            metodoPago
        );

        historialTransacciones.add(transaccion);
        
        System.out.println("Transacción de venta registrada:");
        System.out.println("- ID: " + transaccion.getId());
        System.out.println("- Cliente: " + transaccion.getCliente());
        System.out.println("- Monto: $" + transaccion.getMonto());
        System.out.println("- Método de pago: " + metodoPago);

        return transaccion;
    }

    /**
     * FR6: Registrar un pago general
     * @param cliente Nombre del cliente
     * @param monto Monto del pago
     * @param descripcion Descripción del pago
     * @param metodoPago Método de pago
     * @return La transacción creada
     */
    public Transaccion registrarPago(String cliente, float monto, String descripcion, String metodoPago) {
        Transaccion transaccion = new Transaccion(
            siguienteIdTransaccion++,
            TipoTransaccion.PAGO,
            descripcion,
            monto,
            cliente,
            metodoPago
        );

        historialTransacciones.add(transaccion);
        
        System.out.println("Pago registrado:");
        System.out.println("- ID: " + transaccion.getId());
        System.out.println("- Monto: $" + monto);
        
        return transaccion;
    }

    /**
     * Registrar una cancelación
     * @param cliente Nombre del cliente
     * @param montoReembolso Monto a reembolsar
     * @param motivo Motivo de la cancelación
     * @return La transacción creada
     */
    public Transaccion registrarCancelacion(String cliente, float montoReembolso, String motivo) {
        Transaccion transaccion = new Transaccion(
            siguienteIdTransaccion++,
            TipoTransaccion.CANCELACION,
            "Cancelación: " + motivo,
            -montoReembolso, // Monto negativo para reembolso
            cliente,
            "Reembolso"
        );

        historialTransacciones.add(transaccion);
        
        System.out.println("Cancelación registrada:");
        System.out.println("- Cliente: " + cliente);
        System.out.println("- Reembolso: $" + montoReembolso);
        
        return transaccion;
    }

    /**
     * Obtener el historial completo de transacciones
     * @return Lista de todas las transacciones
     */
    public List<Transaccion> obtenerHistorial() {
        return new ArrayList<>(historialTransacciones);
    }

    /**
     * Obtener transacciones por tipo
     * @param tipo Tipo de transacción
     * @return Lista de transacciones del tipo especificado
     */
    public List<Transaccion> obtenerTransaccionesPorTipo(TipoTransaccion tipo) {
        List<Transaccion> resultado = new ArrayList<>();
        for (Transaccion t : historialTransacciones) {
            if (t.getTipo() == tipo) {
                resultado.add(t);
            }
        }
        return resultado;
    }

    /**
     * Obtener transacciones de un cliente
     * @param nombreCliente Nombre del cliente
     * @return Lista de transacciones del cliente
     */
    public List<Transaccion> obtenerTransaccionesPorCliente(String nombreCliente) {
        List<Transaccion> resultado = new ArrayList<>();
        for (Transaccion t : historialTransacciones) {
            if (t.getCliente().equalsIgnoreCase(nombreCliente)) {
                resultado.add(t);
            }
        }
        return resultado;
    }

    /**
     * Calcular ingresos totales
     * @return Total de ingresos
     */
    public float calcularIngresosTotales() {
        float total = 0;
        for (Transaccion t : historialTransacciones) {
            if (t.getMonto() > 0) { // Solo contar ingresos positivos
                total += t.getMonto();
            }
        }
        return total;
    }

    /**
     * Calcular ingresos por tipo de transacción
     * @param tipo Tipo de transacción
     * @return Total de ingresos de ese tipo
     */
    public float calcularIngresosPorTipo(TipoTransaccion tipo) {
        float total = 0;
        for (Transaccion t : historialTransacciones) {
            if (t.getTipo() == tipo && t.getMonto() > 0) {
                total += t.getMonto();
            }
        }
        return total;
    }

    /**
     * Generar reporte financiero
     */
    public void generarReporteFinanciero() {
        System.out.println("\n========== REPORTE FINANCIERO ==========");
        System.out.println("Total de transacciones: " + historialTransacciones.size());
        System.out.println();
        
        System.out.println("INGRESOS POR TIPO:");
        System.out.println("- Rentas: $" + calcularIngresosPorTipo(TipoTransaccion.RENTA));
        System.out.println("- Ventas: $" + calcularIngresosPorTipo(TipoTransaccion.VENTA));
        System.out.println("- Pagos: $" + calcularIngresosPorTipo(TipoTransaccion.PAGO));
        System.out.println();
        
        System.out.println("INGRESOS TOTALES: $" + calcularIngresosTotales());
        System.out.println("========================================\n");
    }

    /**
     * Mostrar historial de transacciones
     * @param limite Número máximo de transacciones a mostrar (0 = todas)
     */
    public void mostrarHistorial(int limite) {
        System.out.println("\n=== HISTORIAL DE TRANSACCIONES ===");
        
        if (historialTransacciones.isEmpty()) {
            System.out.println("No hay transacciones registradas.");
            return;
        }

        int contador = 0;
        int maxMostrar = (limite > 0 && limite < historialTransacciones.size()) 
                        ? limite : historialTransacciones.size();

        // Mostrar las más recientes primero
        for (int i = historialTransacciones.size() - 1; i >= 0 && contador < maxMostrar; i--) {
            System.out.println(historialTransacciones.get(i));
            contador++;
        }
        
        if (limite > 0 && historialTransacciones.size() > limite) {
            System.out.println("... y " + (historialTransacciones.size() - limite) + " más.");
        }
        System.out.println();
    }
}
