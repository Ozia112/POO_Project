package model;       

import jakarta.persistence.*; //bd
import jakarta.persistence.criteria.CriteriaBuilder.In;

import java.time.Instant;
import java.time.LocalDate;
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

    //Quit├® transient porque como ya hicimos todo pues creamos la relacion real
    // Crea una tabla intemedia
    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Servicio> servicios;
    
    public Ticket () {
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

    public Reporte getReporte() { return reporte; }
    public Long getTicketId() { return ticket_id; }
    public String getNombreCliente() { return nombre_cliente; }
    public String getCorreoCliente() { return correo_cliente; }
    public Instant getTiempoEmision() { return tiempo_emision; }
    public List<Servicio> getServicios() { return servicios; }
    public float getTotalTicket() { return total_ticket; }

    public void setReporte(Reporte reporte) {  this.reporte = reporte; }
    public void setTicketId(Long ticket_id) { this.ticket_id = ticket_id; }
    public void setNombreCliente(String nombre_cliente) { this.nombre_cliente = nombre_cliente; }
    public void setCorreoCliente(String correo_cliente) { this.correo_cliente = correo_cliente; }
    public void setTiempoEmision(Instant tiempo_emision) { this.tiempo_emision = tiempo_emision; }
    public void setServicios(List<Servicio> servicios) { this.servicios = servicios; }
    public void setTotalTicket(float total_ticket) { this.total_ticket = total_ticket; }

    public void agregarServicio(Servicio servicio) {
        if (this.servicios == null) {
            this.servicios = new ArrayList<>();
        }
        this.servicios.add(servicio);
    }
    
    public void eliminarServicio(Servicio servicio) {
        if (this.servicios != null) {
            this.servicios.remove(servicio);
        }
    }
}
