package controller;   //list0

import dao.RentaDAO;
import dao.UbicacionDAO;
import dao.ServicioDAO;
import model.Renta;
import model.Servicio;
import model.Ticket;
import model.Ubicacion;

import java.time.Duration;
import java.time.Instant;
import java.util.List;


public class RentaController {
    private final RentaDAO rentaDAO;
    private final UbicacionDAO ubicacionDAO;
    private final ServicioDAO servicioDAO;
    private ReporteController reporteController;
    private TicketController ticketController;

    private static final int MINUTOS_EN_HORA = 60;
    private final int MINUTOS_CANCELACION = Config.getMinutosCancelacion();
    private final int MINUTOS_TOLERANCIA = Config.getMinutosTolerancia();
    private final int LIMITE_MINUTOS_PRIMERA_HORA = MINUTOS_EN_HORA + MINUTOS_TOLERANCIA;
    

    public RentaController() {
        this.rentaDAO = new RentaDAO();
        this.ubicacionDAO = new UbicacionDAO();
        this.servicioDAO = new ServicioDAO();
    }

    public RentaController(TicketController ticketController, ReporteController reporteController) {
        this();
        this.ticketController = ticketController;
        this.reporteController = reporteController;
    }

    /**
     * FLUJO CORRECTO:
     * 1. Crear TipoServicio (Renta) -> Guardar en BD
     * 2. Crear Servicio con el TipoServicio -> Guardar en BD
     * 3. Agregar Servicio al Ticket -> Guardar Ticket en BD
     * 4. Marcar Ubicaci├│n como NO disponible -> Guardar en BD
     * 5. Agregar Ticket al Reporte -> Guardar Reporte en BD
     */
    public boolean iniciarRenta(Ubicacion ubicacion, Ticket ticket) {
        if (ubicacion == null || ticket == null) {
            System.err.println("Ubicaci├│n o ticket no pueden ser nulos");
            return false;
        }

        // Recargar ubicaci├│n desde BD para asegurar estado actualizado
        ubicacion = ubicacionDAO.obtener(ubicacion.getUbicacionId());

        if (ubicacion == null) {
            System.err.println("La ubicaci├│n no existe");
            return false;
        }

        if (!ubicacion.isDisponible()) {
            System.err.println("La ubicaci├│n" + ubicacion + "ya esta ocupada");
            return false;
        }

        try {
            // 1. Crear y guardar Renta (TipoServicio)
            Renta renta = new Renta(
                "Renta - " + ubicacion.getNombreLocker(),
                Config.getPrecioHoraLocker(),
                ticket.getTiempoEmision(),
                ubicacion
            );
            rentaDAO.guardar(renta);
            System.out.println("Renta guardada en DB - ID: " + renta.getTipoServicioId());

            ubicacion.setDisponible(false);
            ubicacionDAO.actualizar(ubicacion);

            // 2. Crear y guardar Servicio
            Servicio servicio = new Servicio();
            servicio.setTipoServicio(renta);
            servicio.setAplicarDescuento(false);

            float totalInicial = renta.getPrecio() * 1; // Hora inicial
            servicio.setTotalServicio(totalInicial);

            servicioDAO.guardar(servicio);
            System.out.println("Servicio de renta guardado en DB - ID: " + servicio.getServicioId());

            // 3. Agregar serivicio al ticket y guardar
            ticket.getServicios().add(servicio);

            float nuevoTotal = ticketController.calcularTotalTicket(ticket);
            ticket.setTotalTicket(nuevoTotal);

            ticketController.getTicketDAO().actualizar(ticket);
            System.out.println("Ticket actualizado en DB - ID: " + ticket.getTicketId());

            // 4. Marcar ubicaci├│n como no disponible
            ubicacion.setDisponible(false);
            ubicacionDAO.actualizar(ubicacion);
            System.out.println("Ubicaci├│n marcada como no disponible en DB: " + ubicacion);

            if (reporteController != null) {
                boolean inReporte = reporteController.getReporteActual()
                                    .getTickets()
                                    .stream()
                                    .anyMatch(t -> t.getTicketId().equals(ticket.getTicketId()));
                if (!inReporte) {
                    reporteController.agregarTicket(ticket);
                    System.out.println("Ticket agregado al reporte actual");
                } else {
                    reporteController.recalcularTotal();
                    reporteController.getReporteDAO().actualizar(reporteController.getReporteActual());
                    System.out.println("Reporte actualizado con nuevo total");
                }
            }

            System.out.println("Renta iniciada exitosamente en " + ubicacion);
            return true;

        } catch (Exception e) {
            System.err.println("Error al iniciar renta: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * FLUJO CORRECTO:
     * 1. Obtener Renta activa (cierre_renta = NULL)
     * 2. Establecer cierre_renta -> Actualizar Renta en BD
     * 3. Calcular horas -> Actualizar cantidad en Renta -> Actualizar en BD
     * 4. Liberar Ubicaci├│n -> Actualizar en BD
     * 5. Buscar Servicio en Ticket
     * 6. Calcular total del Servicio -> Actualizar Servicio en BD
     * 7. Recalcular total del Ticket -> Actualizar Ticket en BD
     * 8. Recalcular total del Reporte -> Actualizar Reporte en BD
     */
    public boolean finalizarRenta(Ubicacion ubicacion, Ticket ticket) {
        if (ubicacion == null || ticket == null) {
            System.err.println("Ubicaci├│n o ticket no pueden ser nulos");
            return false;
        } 

        try {
            // 1. Obtener Renta activa
            Renta renta = rentaDAO.obtener(ubicacion);
            
            if (renta == null) {
                System.err.println("No hay una renta activa en ubicaci├│n " + ubicacion);
                return false;
            }

            if (renta.getCierreRenta() != null) {
                System.err.println("La renta en ya ha sido finalizada");
                return false;
            }

            // 2. Establecer cierre_renta
            Instant cierre = Instant.now();
            renta.setCierreRenta(cierre);
            
            // 3. Calcular horas y actualizar cantidad
            int horasRentadas = calcularTiempoTrancurrido(renta, cierre);
            renta.setCantidad(horasRentadas);

            rentaDAO.actualizar(renta);
            System.out.println("Renta actualizada en DB - Horas: " + horasRentadas);

            // 4. Liberar Ubicaci├│n
            ubicacion.setDisponible(true);
            ubicacionDAO.actualizar(ubicacion);
            System.out.println("Ubicaci├│n liberada en DB: " + ubicacion);
            
            // 5. Buscar Servicio en Ticket
            Servicio servicioRenta = getServicioRentaEnTicket(ticket, ubicacion);

            if (servicioRenta == null) {
                System.err.println("No se encontr├│ el servicio de renta en el ticket");
                return false;
            }

            // 6. Calcular total del Servicio
            float totalCobrar =  horasRentadas*renta.getPrecio();
            servicioRenta.setTotalServicio(totalCobrar);

            servicioDAO.actualizar(servicioRenta);
            System.out.println("Servicio actualizado en DB - Total: $" + totalCobrar);

            float nuevoTotal = ticketController.calcularTotalTicket(ticket);
            ticket.setTotalTicket(nuevoTotal);

            ticketController.getTicketDAO().actualizar(ticket);

            // 8. Recalcular total del Reporte
            if (reporteController != null) {
                reporteController.recalcularTotal();
                reporteController.getReporteDAO().actualizar(reporteController.getReporteActual());
                System.out.println("Reporte actualizado en DB");
            }

            System.out.println("Renta finalizada exitosamente en " + ubicacion + 
                                " - Horas: " + horasRentadas +
                                " Cierre: " + cierre.toString());
            return true;

        } catch (RuntimeException e) {
            System.err.println("Error al finalizar renta: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public int calcularTiempoTrancurrido(Renta renta, Instant tiempo) {
        Instant inicio = renta.getInicioRenta();
        long diferenciaMinutos = Duration.between(inicio, tiempo).toMinutes();
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

        return horasRentadas;
    }

    public boolean estaDisponible(Ubicacion ubicacion) {
        if (ubicacion == null) return false;

        Ubicacion ubicacionAtual = ubicacionDAO.obtener(ubicacion.getUbicacionId());
        return ubicacionAtual != null && ubicacionAtual.isDisponible();
    }

    public Renta getRenta(Ubicacion ubicacion) {
        if (ubicacion == null) return null;
        return rentaDAO.obtener(ubicacion);
    }

    public Ticket getTicketDeRenta(Ubicacion ubicacion) {
        Renta renta = getRenta(ubicacion);
        if (renta == null) return null;
        
        // Buscar en el reporte el ticket que contenga este servicio de renta
        if (reporteController != null) {
            List<Ticket> tickets = reporteController.getReporteActual().getTickets();
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

    private Servicio getServicioRentaEnTicket(Ticket ticket, Ubicacion ubicacion) {
        if (ticket == null || ubicacion == null) return null;

        for (Servicio servicio : ticket.getServicios()) {
            if (servicio.getTipoServicio() instanceof Renta) {
                Renta r = (Renta) servicio.getTipoServicio();
                if (r.getUbicacion().equals(ubicacion)) {
                    return servicio;
                }
            }
        }
        return null;
    }

    public float calcularTotalActual(Ubicacion ubicacion) {
        Renta renta = getRenta(ubicacion);
        if (renta == null || renta.getCierreRenta() != null) return 0f;

        int horasRentadas = calcularTiempoTrancurrido(renta, Instant.now());
        return horasRentadas * renta.getPrecio();
    }

    public RentaDAO getRentaDAO() {
        return rentaDAO;
    }

    public UbicacionDAO getUbicacionDAO() {
        return ubicacionDAO;
    }

    public ServicioDAO getServicioDAO() {
        return servicioDAO;
    }
}
