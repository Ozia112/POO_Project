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

    private static final int MINUTOS_EN_HORA = 60;
    private final int MINUTOS_CANCELACION = Config.getMinutosCancelacion();
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
            renta.setPrecio(Config.getPrecioHoraLocker());
            renta.setCantidad(1); // Se calcula al cerrar
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

            // Guardar en cache
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
            
            Instant inicio = renta.getInicioRenta();
            long diferenciaMinutos = Duration.between(inicio, cierre).toMinutes();
            int horasRentadas;

            if (diferenciaMinutos <= MINUTOS_CANCELACION) {
                horasRentadas = 0;
            } else if (diferenciaMinutos <= LIMITE_MINUTOS_PRIMERA_HORA) {
                horasRentadas = 1;
            } else {
                long minutosRestantes = diferenciaMinutos - LIMITE_MINUTOS_PRIMERA_HORA;
                int horasAdicionales = (int) ((minutosRestantes + MINUTOS_EN_HORA - 1) / MINUTOS_EN_HORA);
                horasRentadas = 1 + horasAdicionales;
            }

            renta.setCantidad(horasRentadas);
            renta.setStateOcupado(false);

            float nuevoTotal = ticketController.calcularTotalTicket(ticket);
            ticket.setTotalTicket(nuevoTotal);

            ticketController.guardarTicket(ticket);

            rentasActivas.remove(ubicacion);

            System.out.println("Renta finalizada en " + ubicacion + 
                                " - Horas: " + horasRentadas +
                                " Cierre: " + cierre.toString());
            return true;

        } catch (RuntimeException e) {
            System.err.println("Error al finalizar renta: " + e.getMessage());
            return false;
        }
    }  

    public boolean estaDisponible(Ubicacion ubicacion) {
        return !rentasActivas.get(ubicacion).getStateOcupado();
    }

    public Renta getRentaActiva(Ubicacion ubicacion) {
        return rentasActivas.get(ubicacion);
    }

    public Ubicacion[] obtenerUbicacionesDisponibles() {
        return Ubicacion.values();
    }
}
