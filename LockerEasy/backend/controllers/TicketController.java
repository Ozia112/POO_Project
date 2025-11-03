package backend.controllers;

import backend.model.*;
import java.util.*;

/**
 * Controller para gestionar tickets de clientes
 * Vincula servicios (rentas y ventas) con clientes
 */
public class TicketController {
    private Map<Integer, Ticket> tickets;
    private int siguienteIdTicket;

    public TicketController() {
        this.tickets = new HashMap<>();
        this.siguienteIdTicket = 1;
    }

    /**
     * Crear un nuevo ticket para un cliente
     * @param nombreCliente Nombre del cliente
     * @param correoCliente Correo del cliente
     * @param servicios Lista de servicios (rentas y ventas)
     * @return El ticket creado
     */
    public Ticket crearTicket(String nombreCliente, String correoCliente, List<Servicio> servicios) {
        if (nombreCliente == null || nombreCliente.isEmpty()) {
            System.out.println("Error: Nombre del cliente es requerido.");
            return null;
        }

        Ticket ticket = new Ticket(siguienteIdTicket++, nombreCliente, correoCliente, servicios);
        tickets.put(ticket.getTicketId(), ticket);

        System.out.println("Ticket creado exitosamente:");
        System.out.println("- ID: " + ticket.getTicketId());
        System.out.println("- Cliente: " + nombreCliente);
        System.out.println("- Correo: " + correoCliente);
        System.out.println("- Servicios: " + servicios.size());

        return ticket;
    }

    /**
     * Agregar un servicio a un ticket existente
     * @param ticketId ID del ticket
     * @param servicio Servicio a agregar
     * @return true si se agregó correctamente
     */
    public boolean agregarServicioATicket(int ticketId, Servicio servicio) {
        Ticket ticket = tickets.get(ticketId);
        if (ticket == null) {
            System.out.println("Error: Ticket no encontrado.");
            return false;
        }

        ticket.getArrayServicios().add(servicio);
        System.out.println("Servicio agregado al ticket " + ticketId);
        return true;
    }

    /**
     * Obtener un ticket por ID
     * @param ticketId ID del ticket
     * @return El ticket o null si no existe
     */
    public Ticket obtenerTicket(int ticketId) {
        return tickets.get(ticketId);
    }

    /**
     * Obtener todos los tickets de un cliente
     * @param nombreCliente Nombre del cliente
     * @return Lista de tickets del cliente
     */
    public List<Ticket> obtenerTicketsPorCliente(String nombreCliente) {
        List<Ticket> ticketsCliente = new ArrayList<>();
        for (Ticket ticket : tickets.values()) {
            if (ticket.getNombreCliente().equalsIgnoreCase(nombreCliente)) {
                ticketsCliente.add(ticket);
            }
        }
        return ticketsCliente;
    }

    /**
     * Obtener todos los tickets
     * @return Lista de todos los tickets
     */
    public List<Ticket> obtenerTodosLosTickets() {
        return new ArrayList<>(tickets.values());
    }

    /**
     * Calcular el total de un ticket
     * @param ticketId ID del ticket
     * @return Total del ticket
     */
    public float calcularTotalTicket(int ticketId) {
        Ticket ticket = tickets.get(ticketId);
        if (ticket == null) {
            System.out.println("Error: Ticket no encontrado.");
            return 0;
        }

        float total = 0;
        for (Servicio servicio : ticket.getArrayServicios()) {
            total += servicio.calcularTotal();
        }
        return total;
    }

    /**
     * Imprimir detalles de un ticket
     * @param ticketId ID del ticket
     */
    public void imprimirTicket(int ticketId) {
        Ticket ticket = tickets.get(ticketId);
        if (ticket == null) {
            System.out.println("Error: Ticket no encontrado.");
            return;
        }

        System.out.println("\n========== TICKET #" + ticket.getTicketId() + " ==========");
        System.out.println("Cliente: " + ticket.getNombreCliente());
        System.out.println("Correo: " + ticket.getCorreoCliente());
        System.out.println("\nServicios:");
        
        float total = 0;
        for (Servicio servicio : ticket.getArrayServicios()) {
            System.out.println("  - " + servicio.getTipoServicio().getNombre() + 
                             " - $" + servicio.calcularTotal());
            total += servicio.calcularTotal();
        }
        
        System.out.println("\nTOTAL: $" + total);
        System.out.println("================================\n");
    }
}
