import java.time.Instant;

import controller.ReporteController;
import controller.RentaController;
import model.Ticket;
import model.Ubicacion;

public class App {
    public static void main(String[] args) throws Exception {
        Instant hora_inicio = Instant.now();

        ReporteController reporteController = new ReporteController();


        
        RentaController rentaController = new RentaController();
        Ticket ticket1 = new Ticket(1, "Juan Perez", "", hora_inicio, null, 0);
        
        rentaController.iniciarRenta(Ubicacion.PA_T1_L1, ticket1);
    }
}

