package model;       

import jakarta.persistence.*;
import java.time.Instant;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "tickets")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_id")
    private Long ticket_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporte_id")
    private Reporte reporte;

    @Column(name = "nombre_cliente")
    private String nombre_cliente;

    @Column(name = "correo_cliente") 
    private String correo_cliente;

    @Column(name = "tiempo_emision") 
    private Instant tiempo_emision;

    @Column(name = "total_ticket") 
    private float total_ticket;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<TipoServicio> servicios;
    
    public Ticket() {
        this.servicios = new ArrayList<>();
    }

    public Ticket(String nombre_cliente, String correo_cliente, Reporte reporte) {
        this.nombre_cliente = nombre_cliente;
        this.correo_cliente = correo_cliente;
        this.reporte = reporte;
        this.tiempo_emision = Instant.now();
        this.servicios = new ArrayList<>();
        this.total_ticket = 0f;
    }

    // Getters
    public Reporte getReporte() { return reporte; }
    public Long getTicketId() { return ticket_id; }
    public String getNombreCliente() { return nombre_cliente; }
    public String getCorreoCliente() { return correo_cliente; }
    public Instant getTiempoEmision() { return tiempo_emision; }
    public List<TipoServicio> getServicios() { return servicios; }
    public float getTotalTicket() { return total_ticket; }

    // Setters
    public void setReporte(Reporte reporte) { this.reporte = reporte; }
    public void setTicketId(Long ticket_id) { this.ticket_id = ticket_id; }
    public void setNombreCliente(String nombre_cliente) { this.nombre_cliente = nombre_cliente; }
    public void setCorreoCliente(String correo_cliente) { this.correo_cliente = correo_cliente; }
    public void setTiempoEmision(Instant tiempo_emision) { this.tiempo_emision = tiempo_emision; }
    public void setServicios(List<TipoServicio> servicios) { this.servicios = servicios; }
    public void setTotalTicket(float total_ticket) { this.total_ticket = total_ticket; }

    // Métodos auxiliares
    public void agregarServicio(TipoServicio servicio) {
        if (this.servicios == null) {
            this.servicios = new ArrayList<>();
        }
        this.servicios.add(servicio);
        servicio.setTicket(this); // Establecer relación bidireccional
    }
    
    public void eliminarServicio(TipoServicio servicio) {
        if (this.servicios != null) {
            this.servicios.remove(servicio);
            servicio.setTicket(null); // Romper relación bidireccional
        }
    }
}