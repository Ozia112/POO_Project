package model;

import jakarta.persistence.*; //bd
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

/*@Entity     //Con esto identificamos que la clase va a ae ser una tabla 
@Table(name = "tickets")*/

public class Ticket {
    private LocalDate fecha_reporte;
    private int ticket_id;
    private String nombre_cliente;
    private String correo_cliente;
    private Instant tiempo_emision;
    private List<Servicio> servicios;
    private float total_ticket;

    public Ticket(LocalDate fecha_reporte ,int ticket_id, String nombre_cliente, String correo_cliente, Instant tiempo_emision, List<Servicio> servicios, float total_ticket) {
        this.fecha_reporte = fecha_reporte;
        this.ticket_id = ticket_id;
        this.nombre_cliente = nombre_cliente;
        this.correo_cliente = correo_cliente;
        this.tiempo_emision = tiempo_emision;
        this.servicios = servicios;
        this.total_ticket = total_ticket;
    }

    public Ticket() {
        this.servicios = new ArrayList<>();
        this.fecha_reporte = LocalDate.now();
    }

    public LocalDate getFechaReporte() { return fecha_reporte; }
    public int getTicketId() { return ticket_id; }
    public String getNombreCliente() { return nombre_cliente; }
    public String getCorreoCliente() { return correo_cliente; }
    public Instant getTiempoEmision() { return tiempo_emision; }
    public List<Servicio> getServicios() { return servicios; }
    public float getTotalTicket() { return total_ticket; }

    public void setFechaReporte(LocalDate fecha_reporte) { this.fecha_reporte = fecha_reporte; }
    public void setTicketId(int ticket_id) { this.ticket_id = ticket_id; }
    public void setNombreCliente(String nombre_cliente) { this.nombre_cliente = nombre_cliente; }
    public void setCorreoCliente(String correo_cliente) { this.correo_cliente = correo_cliente; }
    public void setTiempoEmision(Instant tiempo_emision) { this.tiempo_emision = tiempo_emision; }
    public void setServicios(List<Servicio> servicios) { this.servicios = servicios; }
    public void setTotalTicket(float total_ticket) { this.total_ticket = total_ticket; }
    
    //================================
    //  NUEVOS MÉTODOS NECESARIOS
    // ================================

    /**
     * Agrega un servicio al ticket y recalcula automáticamente el total.
     */
    public void agregarServicio(Servicio servicio) {
        if (servicio == null) return;

        if (this.servicios == null) {
            this.servicios = new ArrayList<>();
        }

        this.servicios.add(servicio);
        recalcularTotal();
    }

    /**
     * Recalcula el total del ticket sumando todos los servicios existentes.
     */
    public void recalcularTotal() {
        float total = 0f;

        if (servicios != null) {
            for (Servicio s : servicios) {
                if (s != null) {
                    total += s.getTotalServicio();
                }
            }
        }

        this.total_ticket = total;
    }



}
