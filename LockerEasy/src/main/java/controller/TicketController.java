package controller;

import dao.TicketDAO;
import model.Ticket;
import model.Servicio;
import model.TipoServicio;
import model.Renta;
import model.Ubicacion;

import java.time.LocalDate;
import java.util.List;

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
     * Crea nuevo ticket con daots inicializados y guarda con DAO
     * @param nombre_cliente
     * @param correoCliente
     * @return Ticket creado
     */
    public Ticket crearNuevoTicket(String nombre_cliente, String correo_cliente) {
        Ticket ticket = new Ticket(nombre_cliente, validarCorreoCliente(correo_cliente), reporteController.getReporteActual());
        ticketDAO.guardar(ticket);

        System.out.println("Ticket creado, ID:" + ticket.getTicketId() + ", cliente: " + nombre_cliente);
        return ticket;
    }
    
    /**
     * Agrega un servicio al ticket y actualiza el total
     * @param ticket
     * @param tipo
     * @param aplicarDescuento
     */
    public void agregarServicio(Ticket ticket, TipoServicio tipo) {
        if (ticket == null) {
            throw new IllegalArgumentException("El ticket no puede ser nulo");
        }

        Servicio servicio = new Servicio();
        servicio.setTipoServicio(tipo);
        servicio.setAplicarDescuento(false);

        ticket.agregarServicio(servicio);
        ticket.setTotalTicket(calcularTotalTicket(ticket));

        ticketDAO.actualizar(ticket);

        if (reporteController != null) {
            reporteController.recalcularTotal();
            reporteController.getReporteDAO().actualizar(reporteController.getReporteActual());
        }
    }

    /**
     * Elimina un servicio del ticket y actualiza el total
     * @param ticket
     * @param servicioId
     * @return true si se elimino, false si no se encontro el servicio
     */
    public boolean eliminarServicio(Ticket ticket, int servicioId) {
        if (ticket == null) {
            throw new IllegalArgumentException("El ticket no puede ser nulo");
        }

        Servicio servicio = ticket.getServicios().stream()
                .filter(s -> s.getServicioId() == servicioId)
                .findFirst()
                .orElse(null);

        if (servicio != null) {
            ticket.eliminarServicio(servicio);
            ticket.setTotalTicket(calcularTotalTicket(ticket));

            ticketDAO.actualizar(ticket);

            if (reporteController != null) {
                reporteController.recalcularTotal();
                reporteController.getReporteDAO().actualizar(reporteController.getReporteActual());
            }
            return true;
        }
        return false;
    }

    public Servicio getServicio(Ticket ticket, int servicioId) {
        if (ticket == null) {
            return null;
        }

        return ticket.getServicios().stream()
                .filter(s -> s.getServicioId() == servicioId)
                .findFirst()
                .orElse(null);
    }

    public void actualizarServicio(Ticket ticket, Servicio servicioActualizado) {
        if (ticket == null || servicioActualizado == null) {
            throw new IllegalArgumentException("El ticket y el servicio no pueden ser nulos");
        }

        List<Servicio> servicios = ticket.getServicios();
        for (int i = 0; i < servicios.size(); i++) {
            if (servicios.get(i).getServicioId().equals(servicioActualizado.getServicioId())) {
                servicios.set(i, servicioActualizado);
                break;
            }
        }

        ticket.setTotalTicket(calcularTotalTicket(ticket));

        ticketDAO.actualizar(ticket);

        if (reporteController != null) {
            reporteController.recalcularTotal();
            reporteController.getReporteDAO().actualizar(reporteController.getReporteActual());
        }
    }

    public float calcularTotalTicket(Ticket ticket) {
        if (ticket == null) {
            throw new IllegalArgumentException("El ticket no puede ser nulo");
        }

        float total = 0f;
        for (Servicio servicio : ticket.getServicios()) {
            total += servicio.getTotalServicio();
        }
        return total;
    }

    public boolean eliminarTicket(Ticket ticket) {
        if (ticket == null) {
            throw new IllegalArgumentException("El ticket no puede ser nulo");
        }

        ticketDAO.eliminar(ticket.getTicketId());

        if (reporteController != null) {
            reporteController.recalcularTotal();
            reporteController.getReporteDAO().actualizar(reporteController.getReporteActual());
        }

        System.out.println("Ticket " + ticket.getTicketId() + " eliminado.");
        return true;
    }

    public float getTotalServicio(Servicio servicio) {
        return servicio.getTotalServicio();
    }

    public Servicio getServicioRenta(Ticket ticket, Ubicacion ubicacion) {
        if (ticket == null || ticket.getServicios() == null) {
            return null;
        }

        return ticket.getServicios().stream()
                .filter(s -> s.getTipoServicio() instanceof Renta)
                .filter(s -> {
                    Renta renta = (Renta) s.getTipoServicio();
                    return ubicacion.equals(renta.getUbicacion());
                })
                .findFirst()
                .orElse(null);
    }

    private String validarCorreoCliente(String correo_cliente) {
        String CORREO_REGEX = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        if (correo_cliente == null || correo_cliente.isBlank()) {
            return "";
        }
        if (correo_cliente.matches(CORREO_REGEX)) {
            return correo_cliente;
        } else {
            System.err.println("Correo invalido: " + correo_cliente);
            return "";
        }
    }

    public TicketDAO getTicketDAO() {
        return ticketDAO;
    }
}
