package controller;

import java.util.List;

import model.Servicio;
import model.Ticket;

public class TicketController {

    public void addServicioToTicket(Ticket ticket, Servicio servicio) {
        List<Servicio> servicios = ticket.getServicios();
        servicios.add(servicio);
    }

    public void removeServicioFromTicket(Ticket ticket, Servicio servicio) {
        List<Servicio> servicios = ticket.getServicios();
        servicios.remove(servicio);
    }

    public float calcularTotalTicket(Ticket ticket) {
        float total_ticket = 0;
        total_ticket += calcularTotalServicios(ticket.getServicios());
        return total_ticket;
    }

    public float calcularTotalServicios(List<Servicio> servicios) {
        float total_servicio = 0;
        for (Servicio servicio : servicios) {
            total_servicio = servicio.getTotal();
        }
        return total_servicio;
    }


}
