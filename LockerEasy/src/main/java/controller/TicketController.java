package controller;

import dao.TicketDAO;
import model.Ticket;
import model.TipoServicio;
import model.Renta;
import model.Ubicacion;

public class TicketController {
    private final TicketDAO ticketDAO;
    private ReporteController reporteController;

    public TicketController() {
        this.ticketDAO = new TicketDAO();
    }

    public TicketController(ReporteController reporteController) {
        this();
        this.reporteController = reporteController;
    }

    /**
     * Crea un nuevo ticket
     */
    public Ticket crearNuevoTicket(String nombre_cliente, String correo_cliente) {
        Ticket ticket = new Ticket(
            nombre_cliente, 
            validarCorreoCliente(correo_cliente), 
            reporteController.getReporteActual()
        );
        ticketDAO.guardar(ticket);

        System.out.println("Ticket creado - ID: " + ticket.getTicketId() + 
                         " Cliente: " + nombre_cliente);
        return ticket;
    }

    /**
     * Calcula el total del ticket sumando todos sus servicios
     */
    public float calcularTotalTicket(Ticket ticket) {
        if (ticket == null || ticket.getServicios() == null) {
            return 0f;
        }

        float total = 0f;
        for (TipoServicio servicio : ticket.getServicios()) {
            total += servicio.getTotal();
        }
        return total;
    }

    /**
     * Elimina un ticket completo
     */
    public boolean eliminarTicket(Ticket ticket) {
        if (ticket == null) {
            throw new IllegalArgumentException("El ticket no puede ser nulo");
        }

        try {
            ticketDAO.eliminar(ticket.getTicketId());

            if (reporteController != null) {
                reporteController.eliminarTicket(ticket);
                reporteController.recalcularTotal();
                reporteController.guardarReporte();
            }

            System.out.println("Ticket eliminado - ID: " + ticket.getTicketId());
            return true;
        } catch (Exception e) {
            System.err.println("Error al eliminar ticket: " + e.getMessage());
            return false;
        }
    }

    /**
     * Busca el servicio de renta asociado a una ubicación en un ticket
     */
    public Renta getRentaDeUbicacion(Ticket ticket, Ubicacion ubicacion) {
        if (ticket == null || ticket.getServicios() == null || ubicacion == null) {
            return null;
        }

        return ticket.getServicios().stream()
            .filter(s -> s instanceof Renta)
            .map(s -> (Renta) s)
            .filter(r -> ubicacion.equals(r.getUbicacion()))
            .findFirst()
            .orElse(null);
    }

    /**
     * Valida formato de correo electrónico
     */
    private String validarCorreoCliente(String correo_cliente) {
        String CORREO_REGEX = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        if (correo_cliente == null || correo_cliente.isBlank()) {
            return "";
        }
        if (correo_cliente.matches(CORREO_REGEX)) {
            return correo_cliente;
        } else {
            System.err.println("Correo inválido: " + correo_cliente);
            return "";
        }
    }

    public TicketDAO getTicketDAO() {
        return ticketDAO;
    }
}