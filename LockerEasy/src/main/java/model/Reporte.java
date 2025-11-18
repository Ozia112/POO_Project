package model;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

public class Reporte {
    private LocalDate fecha_reporte;
    private List<Ticket> tickets;
    private float total;

    public Reporte() {
        this.tickets = new ArrayList<>();
    }

    public LocalDate getFechaReporte() {
        return fecha_reporte;
    }

    public void setFechaReporte(LocalDate fecha_reporte) {
        this.fecha_reporte = fecha_reporte;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets (List<Ticket> tickets){
        this.tickets = tickets;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }
}

    
    


