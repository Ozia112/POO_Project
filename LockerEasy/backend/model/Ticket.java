package backend.model;

import java.util.List;

public class Ticket {
    private int id;
    private String nombreCliente;
    private String correoCliente;
    private List<Servicio> servicios;

    public Ticket(int id, String nombreCliente, String correoCliente, List<Servicio> servicios) {
        this.id = id;
        this.nombreCliente = nombreCliente;
        this.correoCliente = correoCliente;
        this.servicios = servicios;
    }

    public int getTicketId() {
        return id;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public String getCorreoCliente() {
        return correoCliente;
    }

    public List<Servicio> getArrayServicios() {
        return servicios;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "id=" + id +
                ", nombreCliente='" + nombreCliente + '\'' +
                ", correoCliente='" + correoCliente + '\'' +
                ", servicios=" + servicios +
                '}';
    }
}
