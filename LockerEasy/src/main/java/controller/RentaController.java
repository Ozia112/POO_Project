package controller;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import model.Renta;
import model.Servicio;
import model.Ticket;
import model.Ubicacion;

public class RentaController {
    private ReporteController reporteController;
    private final Map<Ubicacion, Renta> rentasActivas;

    // ================= CONFIG DINÁMICO =================
    private int minutosTolerancia = Config.getMinutosTolerancia();     // PATCH
    private float precioHoraGeneral = Config.getPrecioHoraLocker();   // PATCH

    private static final int MINUTOS_EN_HORA = 60;
    private final int MINUTOS_CANCELACION = Config.getMinutosCancelacion();

    // (Ya no se usarán estas dos, pero se dejan para no romper compatibilidad)
    private final int MINUTOS_TOLERANCIA = Config.getMinutosTolerancia();
    private final int LIMITE_MINUTOS_PRIMERA_HORA = MINUTOS_EN_HORA + MINUTOS_TOLERANCIA;

    public RentaController() {
        this.rentasActivas = new HashMap<>();
    }

    public void setReporteController(ReporteController reporteController) {
        this.reporteController = reporteController;
    }

    public boolean iniciarRenta(Ubicacion ubicacion, Ticket ticket, TicketController ticketController) {
        if (ubicacion == null || ticket == null) {
            System.err.println("Ubicación o ticket no pueden ser nulos");
            return false;
        }
        
        if (rentasActivas.containsKey(ubicacion)) {
            System.err.println("La ubicación " + ubicacion + " ya está ocupada");
            return false;
        }

        try {
            Renta renta = new Renta();
            renta.setNombre("Locker");
            renta.setPrecio(precioHoraGeneral);    // PATCH: usa el precio dinámico
            renta.setCantidad(1);
            renta.setInicioRenta(ticket.getTiempoEmision());
            renta.setStateOcupado(true);
            renta.setUbicacion(ubicacion);

            int servicioId = ticketController.generarIdServicio(ticket);
            Servicio servicio = new Servicio();
            servicio.setServicioId(servicioId);
            servicio.setTipoServicio(renta);
            servicio.setAplicarDescuento(false);

            ticket.getServicios().add(servicio);

            float total = ticketController.calcularTotalTicket(ticket);
            ticket.setTotalTicket(total);

            if (reporteController != null) {
                reporteController.agregarTicket(ticket);
            }

            rentasActivas.put(ubicacion, renta);

            System.out.println("Renta iniciada en " + ubicacion + " con ticket ID: " + ticket.getTicketId());
            return true;

        } catch (RuntimeException e) {
            System.err.println("Error al iniciar renta: " + e.getMessage());
            return false;
        }
    }

    public boolean finalizarRenta(Ubicacion ubicacion, Ticket ticket, TicketController ticketController) {
        if (!rentasActivas.containsKey(ubicacion)) {
            System.err.println("No hay una renta activa en la ubicación " + ubicacion);
            return false;
        }

        try {
            Renta renta = rentasActivas.get(ubicacion);
            Instant cierre = Instant.now();
            renta.setCierreRenta(cierre);
            
            int horasRentadas = calcularTiempoTrancurrido(renta, cierre);

            renta.setCantidad(horasRentadas);
            renta.setStateOcupado(false);

            float nuevoTotal = ticketController.calcularTotalTicket(ticket);
            ticket.setTotalTicket(nuevoTotal);

            ticketController.guardarTicket(ticket);

            System.out.println("Renta finalizada en " + ubicacion + 
                                " - Horas: " + horasRentadas +
                                " Cierre: " + cierre.toString());
            return true;

        } catch (RuntimeException e) {
            System.err.println("Error al finalizar renta: " + e.getMessage());
            return false;
        }
    }

    public int calcularTiempoTrancurrido(Renta renta, Instant tiempo) {
        Instant inicio = renta.getInicioRenta();
        long diferenciaMinutos = Duration.between(inicio, tiempo).toMinutes();

        // ================== PATCH: usar tolerancia dinámica ==================
        int limiteTolerancia = minutosTolerancia;
        int limitePrimeraHora = MINUTOS_EN_HORA + minutosTolerancia;
        // ====================================================================

        int horasRentadas;

        if (diferenciaMinutos <= MINUTOS_CANCELACION) {
            horasRentadas = 0;
        } 
        else if (diferenciaMinutos <= limitePrimeraHora) {      // PATCH
            horasRentadas = 1;
        } 
        else {
            long minutosRestantes = diferenciaMinutos - limitePrimeraHora;   // PATCH
            int horasAdicionales = (int) ((minutosRestantes + MINUTOS_EN_HORA - 1) / MINUTOS_EN_HORA);
            horasRentadas = 1 + horasAdicionales;
        }

        return horasRentadas;
    }

    public void liberarUbicacion(Ubicacion ubicacion) {
        rentasActivas.remove(ubicacion);
        System.out.println("Ubicación " + ubicacion + " liberada.");
    }

    public boolean estaDisponible(Ubicacion ubicacion) {
        return !rentasActivas.get(ubicacion).getStateOcupado();
    }

    public Renta getRentaActiva(Ubicacion ubicacion) {
        return rentasActivas.get(ubicacion);
    }

    public Ticket getTicketDeRenta(Ubicacion ubicacion) {
        Renta renta = rentasActivas.get(ubicacion);
        if (renta == null) return null;
        
        if (reporteController != null) {
            var tickets = reporteController.getReporte().getTickets();
            for (Ticket ticket : tickets) {
                for (Servicio servicio : ticket.getServicios()) {
                    if (servicio.getTipoServicio() instanceof Renta) {
                        Renta r = (Renta) servicio.getTipoServicio();
                        if (r.getUbicacion().equals(ubicacion)) {
                            return ticket;
                        }
                    }
                }
            }
        }
        return null;
    }

    public Ubicacion[] obtenerUbicacionesDisponibles() {
        return Ubicacion.values();
    }

    public Instant obtenerTiempoInicio(Ubicacion ubicacion) {
        Renta renta = rentasActivas.get(ubicacion);
        if (renta != null) {
            return renta.getInicioRenta();
        }
        return null;
    }

    public Instant obtenerTiempoCierre(Ubicacion ubicacion) {
        Renta renta = rentasActivas.get(ubicacion);
        if (renta != null) {
            return renta.getCierreRenta();
        }
        return null;
    }

    public void setInicioRentaFromTicket(Ubicacion ubicacion, Ticket ticket) {
        Renta renta = rentasActivas.get(ubicacion);
        if (renta != null) {
            renta.setInicioRenta(ticket.getTiempoEmision());
        }
    }

    // ==========================================================
    // ===============   PARTE DINÁMICA REAL     ================
    // ==========================================================

    public void setTolerancia(int m) { minutosTolerancia = m; }
    public int getTolerancia() { return minutosTolerancia; }

    public void setPrecioGeneral(float p) { precioHoraGeneral = p; }
    public float getPrecioGeneral() { return precioHoraGeneral; }
}
