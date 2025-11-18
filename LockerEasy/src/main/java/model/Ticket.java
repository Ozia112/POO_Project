package model;

import java.time.Instant;
import java.util.List;

public class Ticket {
    private int ticket_id;
    private String nombre_cliente;
    private String correo_cliente;
    private Instant fecha_emision;
    private List<Servicio> servicios;
    private float total_ticket;

    public Ticket(int ticket_id, String nombre_cliente, String correo_cliente, Instant fecha_emision, List<Servicio> servicios, float total_ticket) {
        this.ticket_id = ticket_id;
        this.nombre_cliente = nombre_cliente;
        this.correo_cliente = correo_cliente;
        this.fecha_emision = fecha_emision;
        this.servicios = servicios;
        this.total_ticket = total_ticket;
    }

    public int getTicketId() {
        return ticket_id;
    }

    public String getNombre_cliente() {
        return nombre_cliente;
    }

    public String getCorreo_cliente() {
        return correo_cliente;
    }

    public Instant getFecha_emision() {
        return fecha_emision;
    }

    public List<Servicio> getServicios() {
        return servicios;
    }

    public float getTotalTicket() {
        return total_ticket;
    }
}
