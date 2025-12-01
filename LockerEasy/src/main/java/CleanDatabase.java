import dao.*;
import model.Servicio;

public class CleanDatabase {
    
    public static void main(String[] args) {
        System.out.println("=== Limpiando rentas y tickets de la base de datos ===\n");
        
        RentaDAO rentaDAO = new RentaDAO();
        TicketDAO ticketDAO = new TicketDAO();
        UbicacionDAO ubicacionDAO = new UbicacionDAO();
        ServicioDAO servicioDAO = new ServicioDAO();
        
        // 1. Eliminar servicios que referencian rentas
        System.out.println("1. Eliminando servicios con rentas...");
        var rentas = rentaDAO.obtenerActivas();
        int serviciosEliminados = 0;
        for (var renta : rentas) {
            var servicios = servicioDAO.obtenerTodos();
            for (Servicio servicio : servicios) {
                if (servicio.getTipoServicio().getTipoServicioId().equals(renta.getTipoServicioId())) {
                    servicioDAO.eliminar(servicio.getServicioId());
                    serviciosEliminados++;
                }
            }
        }
        System.out.println("   ✓ " + serviciosEliminados + " servicios eliminados");
        
        // 2. Eliminar rentas
        System.out.println("\n2. Eliminando rentas activas...");
        rentas = rentaDAO.obtenerActivas();
        for (var renta : rentas) {
            rentaDAO.eliminar(renta.getTipoServicioId());
        }
        System.out.println("   ✓ " + rentas.size() + " rentas eliminadas");
        
        // 3. Marcar todas las ubicaciones como disponibles
        System.out.println("\n3. Liberando todas las ubicaciones...");
        var ubicaciones = ubicacionDAO.obtenerTodas();
        for (var ubicacion : ubicaciones) {
            ubicacion.setDisponible(true);
            ubicacionDAO.actualizar(ubicacion);
        }
        System.out.println("   ✓ " + ubicaciones.size() + " ubicaciones liberadas");
        
        System.out.println("\n✓ Base de datos limpiada exitosamente");
        System.out.println("\nPuedes ejecutar ahora: mvn javafx:run");
    }
}
