package model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "reportes")
public class Reporte {

    @Id
    @Column(name = "fecha_reporte")
    private LocalDate fecha_reporte;
    
    @OneToMany(mappedBy = "reporte", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Ticket> tickets;
    
    @Column(name = "total_reporte")
    private float total_reporte;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private EstadoReporte estado;

    public enum EstadoReporte {
        ACTIVO,
        CERRADO
    }

    public Reporte() {
        this.tickets = new ArrayList<>();
        this.fecha_reporte = LocalDate.now();
        this.total_reporte = 0f;
        this.estado = EstadoReporte.ACTIVO;
    }

    public Reporte(LocalDate fecha) {
        this.tickets = new ArrayList<>();
        this.fecha_reporte = fecha;
        this.total_reporte = 0f;
        this.estado = fecha.equals(LocalDate.now()) ? EstadoReporte.ACTIVO : EstadoReporte.CERRADO;
    }

    // Getters y Setters
    public LocalDate getFechaReporte() { return fecha_reporte; }
    public List<Ticket> getTickets() { return tickets; }
    public float getTotal() { return total_reporte; }
    public EstadoReporte getEstado() { return estado; }

    public void setFechaReporte(LocalDate fecha_reporte) { this.fecha_reporte = fecha_reporte; }
    public void setTickets (List<Ticket> tickets){ this.tickets = tickets; }
    public void setTotal(float total_reporte) { this.total_reporte = total_reporte; }
    public void setEstado(EstadoReporte estado) { this.estado = estado; }

    // Helper para verificar si el reporte es del dia actual
    public boolean esActivo() {
        return estado == EstadoReporte.ACTIVO && fecha_reporte.equals(LocalDate.now());
    }
}