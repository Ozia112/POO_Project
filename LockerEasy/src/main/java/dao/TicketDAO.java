package dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import util.AppLogger;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import model.Ticket;
import model.Reporte;
import model.TipoServicio;
import model.Venta;
import model.ProductoCatalogo;
import controller.InventarioController;

public class TicketDAO {
    
    public boolean guardar(Ticket ticket) {
        Transaction transaction = null;
        Session session = session = HibernateUtil.getSessionFactory().openSession();
        long startTime = System.currentTimeMillis();

        try {
            AppLogger.transaccionIniciada("guardar ticket",
                "Cliente: " + ticket.getNombreCliente() + " Correo: " + ticket.getCorreoCliente());
            
            transaction = session.beginTransaction();
            session.persist(ticket);
            transaction.commit();
            
            long duration = System.currentTimeMillis() - startTime;
            AppLogger.transaccionCommit("guardar ticket", duration);
            
            AppLogger.ticketCreado(
                ticket.getTicketId().intValue(),
                ticket.getNombreCliente(),
                ticket.getCorreoCliente(),
                ticket.getReporte().getFechaReporte().getDayOfYear(),
                ticket.getTotalTicket()
            );
            
            return true;

        } catch (Exception e) {
            if (transaction != null && transaction.getStatus().canRollback()) {
                transaction.rollback();
                AppLogger.transaccionRollback("guardar ticket", e.getMessage());
            }
            AppLogger.errorDBDetallado("guardar", "Ticket", 
                ticket.getTicketId() != null ? ticket.getTicketId().intValue() : null, e);
            return false;
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public boolean actualizar(Ticket ticket) {
        Transaction transaction = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        long startTime = System.currentTimeMillis();
        
        try {
            // Obtener estado anterior
            Ticket ticketAnterior = session.get(Ticket.class, ticket.getTicketId());
            double totalAnterior = ticketAnterior != null ? ticketAnterior.getTotalTicket() : 0.0;
            
            AppLogger.transaccionIniciada("actualizar ticket",
                "ID: " + ticket.getTicketId() + " Cliente: " + ticket.getNombreCliente());
            
            transaction = session.beginTransaction();
            
            // IMPORTANTE: Usar UPDATE nativo para evitar que cascade sobrescriba
            // cambios hechos por JDBC directo en las entidades relacionadas
            String updateSql = "UPDATE tickets SET total_ticket = :total WHERE ticket_id = :id";
            session.createNativeMutationQuery(updateSql)
                .setParameter("total", ticket.getTotalTicket())
                .setParameter("id", ticket.getTicketId())
                .executeUpdate();
            
            transaction.commit();
            
            long duration = System.currentTimeMillis() - startTime;
            AppLogger.transaccionCommit("actualizar ticket", duration);
            
            int numServicios = ticket.getServicios() != null ? ticket.getServicios().size() : 0;
            
            AppLogger.ticketActualizado(
                ticket.getTicketId().intValue(),
                totalAnterior,
                ticket.getTotalTicket(),
                numServicios
            );
            
            // Warning si el ticket no tiene servicios
            if (numServicios == 0) {
                AppLogger.ticketSinServicios(ticket.getTicketId().intValue());
            }
            
            return true;
            
        } catch (Exception e) {
            if (transaction != null && transaction.getStatus().canRollback()) {
                transaction.rollback();
                AppLogger.transaccionRollback("actualizar ticket", e.getMessage());
            }
            AppLogger.errorDBDetallado("actualizar", "Ticket", 
                ticket.getTicketId().intValue(), e);
            return false;
            
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public Ticket obtener(Long ticketId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Ticket ticket = session.get(Ticket.class, ticketId);
            if (ticket == null) {
                AppLogger.warningDatosInconsistentes("Ticket", ticketId.intValue(), 
                    "No existe ticket con ese ID");
            }
            return ticket;
        }
    }

    public List<Ticket> obtener(LocalDate fecha) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Ticket t WHERE t.reporte.fecha_reporte = :fecha";
            Query<Ticket> query = session.createQuery(hql, Ticket.class);
            query.setParameter("fecha", fecha);
            List<Ticket> tickets = query.list();
            
            AppLogger.debug("Obtenidos {} tickets para la fecha {}", tickets.size(), fecha);
            return tickets;
        }
    }

    public List<Ticket> obtenerTodos() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Ticket> tickets = session.createQuery("FROM Ticket", Ticket.class).list();
            AppLogger.debug("Obtenidos {} tickets totales", tickets.size());
            return tickets;
        }
    }

    public boolean eliminar(Long ticketId) {
        Transaction transaction = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        
        try {
            transaction = session.beginTransaction();

            Ticket ticket = session.get(Ticket.class, ticketId);
            if (ticket != null) {
                String nombreCliente = ticket.getNombreCliente();
                Reporte reporte = ticket.getReporte();
                int reporteId = reporte != null ? reporte.getFechaReporte().getDayOfYear() : 0;
                int numServicios = ticket.getServicios() != null ? ticket.getServicios().size() : 0;
                
                // IMPORTANTE: Revertir inventario para ventas que afectan inventario
                // Usamos InventarioController fuera de esta transacción para evitar conflictos
                InventarioController inventarioController = new InventarioController();
                if (ticket.getServicios() != null) {
                    for (TipoServicio servicio : ticket.getServicios()) {
                        // Forzar inicialización del proxy si es necesario
                        org.hibernate.Hibernate.initialize(servicio);
                        
                        if (servicio instanceof Venta) {
                            Venta venta = (Venta) servicio;
                            ProductoCatalogo producto = venta.getProductoCatalogo();
                            
                            if (producto != null) {
                                // Forzar inicialización del producto y su etiqueta
                                org.hibernate.Hibernate.initialize(producto);
                                org.hibernate.Hibernate.initialize(producto.getEtiqueta());
                                
                                if (producto.getEtiqueta() != null && producto.getEtiqueta().isAfectaInventario()) {
                                    Long productoId = producto.getId();
                                    int cantidad = venta.getCantidad();
                                    
                                    // Cerrar transacción temporalmente para el update de inventario
                                    // ya que InventarioController abre su propia sesión
                                    inventarioController.aumentarExistencias(productoId, cantidad);
                                    AppLogger.info("Inventario restaurado para producto ID {} cantidad {}", 
                                        productoId, cantidad);
                                }
                            }
                        }
                    }
                }
                
                // IMPORTANTE: Remover el ticket de la lista del reporte para evitar re-save por cascade
                if (reporte != null && reporte.getTickets() != null) {
                    reporte.getTickets().remove(ticket);
                    ticket.setReporte(null);
                }
                
                // Eliminar servicios asociados primero (romper relación bidireccional)
                if (ticket.getServicios() != null) {
                    for (TipoServicio servicio : new ArrayList<>(ticket.getServicios())) {
                        servicio.setTicket(null);
                        session.remove(servicio);
                    }
                    ticket.getServicios().clear();
                }
                
                session.remove(ticket);
                transaction.commit();
                
                AppLogger.ticketEliminado(ticketId.intValue(), nombreCliente, reporteId, numServicios);
                return true;
            } else {
                AppLogger.warningDatosInconsistentes("Ticket", ticketId.intValue(), 
                    "Intento de eliminar ticket inexistente");
                return false;
            }
            
        } catch (Exception e) {
            if (transaction != null && transaction.getStatus().canRollback()) {
                transaction.rollback();
            }
            AppLogger.errorDBDetallado("eliminar", "Ticket", ticketId.intValue(), e);
            return false;
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
}