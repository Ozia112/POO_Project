import controller.Config;
import dao.*;
import model.*;

public class InitializeData {
    
    public static void main(String[] args) {
        System.out.println("=== Inicializando datos en la base de datos ===\n");
        
        EtiquetaDAO etiquetaDAO = new EtiquetaDAO();
        UbicacionDAO ubicacionDAO = new UbicacionDAO();
        VentaDAO ventaDAO = new VentaDAO();
        
        // 1. CREAR ETIQUETAS
        System.out.println("1. Creando etiquetas...");
        
        Etiqueta etiquetaConsumible = new Etiqueta("Consumible", true);
        Etiqueta etiquetaTramite = new Etiqueta("Trámite", false);
        Etiqueta etiquetaImpresion = new Etiqueta("Impresión", false);
        
        etiquetaDAO.guardar(etiquetaConsumible);
        etiquetaDAO.guardar(etiquetaTramite);
        etiquetaDAO.guardar(etiquetaImpresion);
        
        System.out.println("   ✓ Etiquetas creadas: Consumible, Trámite, Impresión");
        
        // 2. CREAR UBICACIONES
        System.out.println("\n2. Creando ubicaciones de lockers...");
        
        String[] lockers = {
            "A1", "A2", "A3", "A4",
            "B1", "B2", "B3", "B4",
            "C1", "C2", "C3", "C4"
        };
        
        for (String locker : lockers) {
            Ubicacion ubicacion = new Ubicacion();
            ubicacion.setNombreLocker(locker);
            ubicacion.setNombreTorre("Torre Principal");
            ubicacion.setLocalizacion("Edificio Central");
            ubicacion.setDisponible(true);
            ubicacionDAO.guardar(ubicacion);
        }
        
        System.out.println("   ✓ " + lockers.length + " ubicaciones creadas");
        
        // 3. CREAR PRODUCTOS DE VENTA
        System.out.println("\n3. Creando productos de venta...");
        
        // Consumibles
        Venta producto1 = new Venta("Pluma", 10.0f, 50, etiquetaConsumible, true);
        Venta producto2 = new Venta("Lápiz", 5.0f, 100, etiquetaConsumible, true);
        Venta producto3 = new Venta("Borrador", 8.0f, 30, etiquetaConsumible, true);
        Venta producto4 = new Venta("Cuaderno", 25.0f, 20, etiquetaConsumible, true);
        
        // Trámites
        Venta producto5 = new Venta("Copia simple", 1.0f, 1000, etiquetaTramite, true);
        Venta producto6 = new Venta("Engargolado", 50.0f, 50, etiquetaTramite, true);
        Venta producto7 = new Venta("Enmicado", 30.0f, 30, etiquetaTramite, true);
        
        // Impresiones
        Venta producto8 = new Venta("Impresión B/N", 2.0f, 500, etiquetaImpresion, true);
        Venta producto9 = new Venta("Impresión Color", 5.0f, 200, etiquetaImpresion, true);
        
        ventaDAO.guardar(producto1);
        ventaDAO.guardar(producto2);
        ventaDAO.guardar(producto3);
        ventaDAO.guardar(producto4);
        ventaDAO.guardar(producto5);
        ventaDAO.guardar(producto6);
        ventaDAO.guardar(producto7);
        ventaDAO.guardar(producto8);
        ventaDAO.guardar(producto9);
        
        System.out.println("   ✓ 9 productos creados");
        
        // 4. MOSTRAR RESUMEN
        System.out.println("\n=== Resumen ===");
        System.out.println("Etiquetas: " + etiquetaDAO.obtenerTodas().size());
        System.out.println("Ubicaciones: " + ubicacionDAO.obtenerTodas().size());
        System.out.println("Productos: " + ventaDAO.obtenerTodas().size());
        
        System.out.println("\n✓ Inicialización completada exitosamente");
        System.out.println("\nPuedes ejecutar ahora: mvn javafx:run");
    }
}
