package model;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

public class Reporte {
    private static Reporte instancia_actual;
    private LocalDate fecha_reporte;
    private List<Ticket> tickets;
    private float total_reporte;

    public Reporte() {
        this.tickets = new ArrayList<>();
        this.fecha_reporte = LocalDate.now();
    }

    public static Reporte crearNuevaInstancia() { return new Reporte(); }

    public static Reporte getInstancia() { return instancia_actual; }
    public LocalDate getFechaReporte() { return fecha_reporte; }
    public List<Ticket> getTickets() { return tickets; }
    public float getTotal() { return total_reporte; }

    public static void setInstancia(Reporte reporte) { instancia_actual = reporte; }
    public void setFechaReporte(LocalDate fecha_reporte) { this.fecha_reporte = fecha_reporte; }
    public void setTickets (List<Ticket> tickets){ this.tickets = tickets; }
    public void setTotal(float total_reporte) { this.total_reporte = total_reporte; }
}