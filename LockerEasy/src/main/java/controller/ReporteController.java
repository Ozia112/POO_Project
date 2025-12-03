package controller;

import dao.ReporteDAO;
import dao.TicketDAO;
import model.Reporte;
import model.Ticket;

import java.time.LocalDate;
import java.util.List;

public class ReporteController {
    private final ReporteDAO reporteDAO;
    private final TicketDAO ticketDAO;
    private Reporte reporteActual; // Cach├® en memoria del dia

    public ReporteController() {
        this.reporteDAO = new ReporteDAO();
        this.ticketDAO = new TicketDAO();
        this.reporteActual = cargarOCrearReporteDelDia();
    }

    /**
     * Cargar el reporte actual desde un archivo JSON basado en la fecha actual.
    
     */
    private Reporte cargarOCrearReporteDelDia() {
        LocalDate hoy = LocalDate.now();
        Reporte reporte = reporteDAO.obtener(hoy);

        if (reporte == null) {
            reporte = new Reporte(hoy);
            reporteDAO.guardar(reporte);
            System.out.println("Nuevo reporte creado para el dia: " + hoy);
        } else {
            System.out.println("Reporte cargado para el dia: " + hoy);
        }

        return reporte;
    }
    //Permite que otros controladores fuercen el guardado del reporte actual
    public void guardarReporte() {
        if (reporteActual != null) {
            reporteDAO.actualizar(reporteActual);
        }
    }
    
    /**
     * Recarga el reporte actual desde la base de datos para obtener datos frescos.
     * Útil para sincronizar después de operaciones que modifican tickets.
     */
    public Reporte recargarReporte() {
        LocalDate hoy = LocalDate.now();
        Reporte reporteFresco = reporteDAO.obtener(hoy);
        if (reporteFresco != null) {
            this.reporteActual = reporteFresco;
        }
        return this.reporteActual;
    }

    public void agregarTicket(Ticket ticket) {
        if (ticket == null) {
            throw new IllegalArgumentException("El ticket no puede ser nulo");
        }

        if (!reporteActual.esActivo()) {
            throw new IllegalStateException("No se puede agregar tickets a un reporte cerrado");
        }

        reporteActual.getTickets().add(ticket);

        recalcularTotal();

        reporteDAO.actualizar(reporteActual);

        System.out.println("Ticket " + ticket.getTicketId() + " agregado al reporte de " + reporteActual.getFechaReporte());
    }

    public void eliminarTicket(Ticket ticket) {
        if (ticket == null) {
            throw new IllegalArgumentException("El ticket no puede ser nulo");
        }

        if (!reporteActual.esActivo()) {
            throw new IllegalStateException("No se puede eliminar tickets de un reporte cerrado");
        }

        Long ticketId = ticket.getTicketId();
        
        // Primero eliminar de la BD
        ticketDAO.eliminar(ticketId);
        
        // IMPORTANTE: Recargar el reporte desde la BD para evitar referencias huérfanas
        // Esto asegura que reporteActual no tenga referencias a tickets eliminados
        reporteActual = reporteDAO.obtener(reporteActual.getFechaReporte());
        
        if (reporteActual == null) {
            // Si por alguna razón no existe, recrearlo
            reporteActual = cargarOCrearReporteDelDia();
        }

        System.out.println("Ticket " + ticketId + " eliminado del reporte de " + reporteActual.getFechaReporte());
    }

    public void recalcularTotal(){
        if (reporteActual == null) return;

        float total = 0.0f;

        // Recargar cada ticket desde la BD para obtener totales actualizados
        for (Ticket ticket : reporteActual.getTickets()) {
            Ticket ticketActualizado = ticketDAO.obtener(ticket.getTicketId());
            if (ticketActualizado != null) {
                total += ticketActualizado.getTotalTicket();
                // Actualizar también el total en memoria para mantener sincronizado
                ticket.setTotalTicket(ticketActualizado.getTotalTicket());
            } else {
                total += ticket.getTotalTicket();
            }
        }
        reporteActual.setTotal(total);
    }

    public void cerrarReporteDelDia() {
        if (reporteActual == null) {
            throw new IllegalStateException("No hay un reporte activo para cerrar");
        }

        LocalDate hoy = LocalDate.now();
        if (!reporteActual.getFechaReporte().equals(hoy)) {
            throw new IllegalStateException("El reporte actual no corresponde al dia de hoy");
        }

        recalcularTotal();

        reporteDAO.cerrar(hoy);

        System.out.println("Reporte del " + hoy + " cerrado. Total: $" + reporteActual.getTotal());
    }

    public Reporte getReporteActual() {
        //A├▒ad├¡ esto debido a que si el cliente no cerr├│ el programa y pasa un dia, pues al guardar se guardaran las cosas al dia anteriror
        if (!reporteActual.getFechaReporte().equals(LocalDate.now())){
            this.reporteActual = cargarOCrearReporteDelDia();
        }
        return reporteActual;
    }

    public List<Reporte> obtenerReportesSemana() {
        LocalDate hoy = LocalDate.now();
        LocalDate haceUnaSemana = hoy.minusDays(7);
        return reporteDAO.obtener(haceUnaSemana, hoy);
    }

    public List<Reporte> obtenerReportesMes() {
        LocalDate hoy = LocalDate.now();
        LocalDate haceUnMes = hoy.minusMonths(1);
        return reporteDAO.obtener(haceUnMes, hoy);
    }

    public List<Reporte> obtenerReportesSeisMeses() {
        LocalDate hoy = LocalDate.now();
        LocalDate haceSeisMeses = hoy.minusMonths(6);
        return reporteDAO.obtener(haceSeisMeses, hoy);
    } 

    public List<Reporte> obtenerReportesUltimoAnio() {
        LocalDate hoy = LocalDate.now();
        LocalDate haceUnAnio = hoy.minusYears(1);
        return reporteDAO.obtener(haceUnAnio, hoy);
    }

    public List<Reporte> obtenerReportesUltimos5Anios() {
        LocalDate hoy = LocalDate.now();
        LocalDate haceCincoAnios = hoy.minusYears(5);
        return reporteDAO.obtener(haceCincoAnios, hoy);
    }

    public float obtenerGananciaPorgrupo(List<Reporte> reportes) {
        float total = 0.0f;
        for (Reporte reporte : reportes) {
            total += reporte.getTotal();
        }
        return total;
    }
    
    public ReporteDAO getReporteDAO() {
        return reporteDAO;
    }
}
