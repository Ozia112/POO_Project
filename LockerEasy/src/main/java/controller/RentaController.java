package controller;

import dao.RentaDAO;
import dao.UbicacionDAO;
import model.Renta;
import model.Ticket;
import model.Ubicacion;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class RentaController {
    private final RentaDAO rentaDAO;
    private final UbicacionDAO ubicacionDAO;
    private ReporteController reporteController;
    private TicketController ticketController;

    private static final int MINUTOS_EN_HORA = 60;

    public RentaController() {
        this.rentaDAO = new RentaDAO();
        this.ubicacionDAO = new UbicacionDAO();
    }

    public RentaController(TicketController ticketController, ReporteController reporteController) {
        this();
        this.ticketController = ticketController;
        this.reporteController = reporteController;
    }

    /**
     * Inicia una renta en una ubicación específica
     */
    public boolean iniciarRenta(Ubicacion ubicacion, Ticket ticket) {
        if (ubicacion == null || ticket == null) {
            System.err.println("Ubicación o ticket no pueden ser nulos");
            return false;
        }

        // Recargar ubicación desde BD
        ubicacion = ubicacionDAO.obtener(ubicacion.getUbicacionId());

        if (ubicacion == null) {
            System.err.println("La ubicación no existe");
            return false;
        }

        if (!ubicacion.isDisponible()) {
            System.err.println("La ubicación " + ubicacion.getNombreLocker() + " ya está ocupada");
            return false;
        }

        try {
            // 1. Crear Renta (TipoServicio)
            Renta renta = new Renta(
                "Renta - " + ubicacion.getNombreLocker(),
                1, // Cantidad inicial (horas)
                ticket.getTiempoEmision(),
                ubicacion
            );
            renta.setTicket(ticket);
            renta.setAplicarDescuento(false);

            // 2. Guardar renta
            rentaDAO.guardar(renta);
            System.out.println("Renta guardada - ID: " + renta.getTipoServicioId());

            // 3. Marcar ubicación como ocupada
            ubicacion.setDisponible(false);
            ubicacionDAO.actualizar(ubicacion);

            // 4. Agregar al ticket
            ticket.agregarServicio(renta);

            // 5. Actualizar totales
            if (ticketController != null) {
                ticket.setTotalTicket(ticketController.calcularTotalTicket(ticket));
                ticketController.getTicketDAO().actualizar(ticket);
            }

            // 6. Actualizar reporte
            if (reporteController != null) {
                boolean enReporte = reporteController.getReporteActual()
                    .getTickets()
                    .stream()
                    .anyMatch(t -> t.getTicketId().equals(ticket.getTicketId()));
                
                if (!enReporte) {
                    reporteController.agregarTicket(ticket);
                } else {
                    reporteController.recalcularTotal();
                    reporteController.guardarReporte();
                }
            }

            System.out.println("Renta iniciada en: " + ubicacion.getNombreLocker());
            return true;

        } catch (Exception e) {
            System.err.println("Error al iniciar renta: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Finaliza una renta calculando las horas transcurridas
     */
    public boolean finalizarRenta(Ubicacion ubicacion, Ticket ticket) {
        if (ubicacion == null || ticket == null) {
            System.err.println("Ubicación o ticket no pueden ser nulos");
            return false;
        }

        try {
            // 1. Obtener renta activa
            Renta renta = rentaDAO.obtener(ubicacion);
            
            if (renta == null) {
                System.err.println("No hay renta activa en: " + ubicacion.getNombreLocker());
                return false;
            }

            if (renta.getCierreRenta() != null) {
                System.err.println("La renta ya fue finalizada");
                return false;
            }

            // 2. Establecer cierre y calcular horas
            Instant cierre = Instant.now();
            renta.setCierreRenta(cierre);
            
            int horasRentadas = calcularTiempoTranscurrido(renta, cierre);
            renta.setCantidad(horasRentadas);

            rentaDAO.actualizar(renta);
            System.out.println("Renta actualizada - Horas: " + horasRentadas);

            // 3. Liberar ubicación - IMPORTANTE: recargar desde BD para evitar problemas de sesión
            Ubicacion ubicacionActualizada = ubicacionDAO.obtener(ubicacion.getUbicacionId());
            if (ubicacionActualizada != null && !ubicacionActualizada.isDisponible()) {
                ubicacionActualizada.setDisponible(true);
                ubicacionDAO.actualizar(ubicacionActualizada);
                System.out.println("Ubicación liberada: " + ubicacionActualizada.getNombreLocker());
            } else {
                System.out.println("ADVERTENCIA: La ubicación ya estaba disponible o no se encontró");
            }

            // 4. Recalcular totales del ticket
            // IMPORTANTE: La renta ya fue actualizada en BD con JDBC, pero el objeto en memoria
            // del ticket puede tener valores viejos. Calculamos el total manualmente.
            if (ticketController != null) {
                Ticket ticketActualizado = ticketController.getTicketDAO().obtener(ticket.getTicketId());
                if (ticketActualizado != null) {
                    // Recalcular total sumando todos los servicios con valores frescos
                    float nuevoTotal = 0f;
                    for (model.TipoServicio servicio : ticketActualizado.getServicios()) {
                        if (servicio instanceof Renta) {
                            Renta r = (Renta) servicio;
                            // Si es la renta que acabamos de cerrar, usar los valores recalculados
                            if (r.getTipoServicioId().equals(renta.getTipoServicioId())) {
                                nuevoTotal += renta.getTotal();
                            } else {
                                nuevoTotal += r.getTotal();
                            }
                        } else {
                            nuevoTotal += servicio.getTotal();
                        }
                    }
                    ticketActualizado.setTotalTicket(nuevoTotal);
                    ticketController.getTicketDAO().actualizar(ticketActualizado);
                }
            }

            // 5. Actualizar reporte
            if (reporteController != null) {
                reporteController.recalcularTotal();
                reporteController.guardarReporte();
            }

            System.out.println("Renta finalizada - Ubicación: " + ubicacion.getNombreLocker() + 
                             " Horas: " + horasRentadas + 
                             " Total: $" + renta.getTotal());
            return true;

        } catch (Exception e) {
            System.err.println("Error al finalizar renta: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Calcula el tiempo transcurrido en horas según las reglas de negocio
     * Lee dinámicamente la configuración para respetar los valores del usuario
     * 
     * Reglas:
     * - Menos de minutosCancelacion (5 min): 0 horas (cancelación gratuita)
     * - Primera hora: cuenta desde minutosCancelacion hasta 60+tolerancia minutos
     * - Horas adicionales: cada 60 minutos adicionales cuenta como 1 hora
     */
    public int calcularTiempoTranscurrido(Renta renta, Instant tiempo) {
        Instant inicio = renta.getInicioRenta();
        long diferenciaMinutos = Duration.between(inicio, tiempo).toMinutes();
        
        // Leer valores dinámicamente de Config para respetar configuración del usuario
        int minutosCancelacion = Config.getMinutosCancelacion();
        int minutosTolerancia = Config.getMinutosTolerancia();
        
        // Ventana de cancelación gratuita
        if (diferenciaMinutos <= minutosCancelacion) {
            return 0;
        }
        
        // Primera hora incluye tolerancia (ej: 0-75 min = 1 hora si tolerancia=15)
        int limiteMinutosPrimeraHora = MINUTOS_EN_HORA + minutosTolerancia;
        if (diferenciaMinutos <= limiteMinutosPrimeraHora) {
            return 1;
        }
        
        // Horas adicionales: cada 60 minutos adicionales después de la primera hora
        // Ejemplo con tolerancia=15:
        //   76-135 min = 2 horas (1 base + 1 adicional)
        //   136-195 min = 3 horas (1 base + 2 adicionales)
        long minutosExcedentes = diferenciaMinutos - limiteMinutosPrimeraHora;
        int horasAdicionales = (int) Math.ceil((double) minutosExcedentes / MINUTOS_EN_HORA);
        
        return 1 + horasAdicionales;
    }

    /**
     * Calcula el total actual de una renta activa
     */
    public float calcularTotalActual(Ubicacion ubicacion) {
        Renta renta = getRenta(ubicacion);
        if (renta == null || renta.getCierreRenta() != null) {
            return 0f;
        }

        int horasActuales = calcularTiempoTranscurrido(renta, Instant.now());
        renta.setCantidad(horasActuales); // Actualizar cantidad temporal
        return renta.getTotal();
    }

    /**
     * Aplica descuento a una renta específica
     */
    public boolean aplicarDescuento(Ubicacion ubicacion, boolean aplicar) {
        Renta renta = getRenta(ubicacion);
        if (renta == null) {
            System.err.println("No hay renta activa en esta ubicación");
            return false;
        }

        renta.setAplicarDescuento(aplicar);
        
        try {
            rentaDAO.actualizar(renta);
            
            // Actualizar totales
            if (ticketController != null && renta.getTicket() != null) {
                Ticket ticket = renta.getTicket();
                ticket.setTotalTicket(ticketController.calcularTotalTicket(ticket));
                ticketController.getTicketDAO().actualizar(ticket);
                
                if (reporteController != null) {
                    reporteController.recalcularTotal();
                    reporteController.guardarReporte();
                }
            }
            
            System.out.println("Descuento " + (aplicar ? "aplicado" : "removido") + 
                             " en renta de: " + ubicacion.getNombreLocker());
            return true;
            
        } catch (Exception e) {
            System.err.println("Error al aplicar descuento: " + e.getMessage());
            return false;
        }
    }

    // Métodos auxiliares
    public boolean estaDisponible(Ubicacion ubicacion) {
        if (ubicacion == null) return false;
        Ubicacion ubicacionActual = ubicacionDAO.obtener(ubicacion.getUbicacionId());
        return ubicacionActual != null && ubicacionActual.isDisponible();
    }

    public Renta getRenta(Ubicacion ubicacion) {
        if (ubicacion == null) return null;
        return rentaDAO.obtener(ubicacion);
    }

    public Ticket getTicketDeRenta(Ubicacion ubicacion) {
        Renta renta = getRenta(ubicacion);
        return renta != null ? renta.getTicket() : null;
    }

    public RentaDAO getRentaDAO() {
        return rentaDAO;
    }

    public UbicacionDAO getUbicacionDAO() {
        return ubicacionDAO;
    }
}