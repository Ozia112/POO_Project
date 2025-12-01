package dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.time.LocalDate;
import java.util.List;

import javax.management.RuntimeErrorException;

import model.Ticket;

public class TicketDAO {
    /**
     * Metodo para guardar un nuevo ticket en la base de datos
     * @param Ticket El ticket a guardar
     */
    public boolean guardar(Ticket ticket) {
        Transaction transaction = null;
        Session session = null;

        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            session.persist(ticket);
            transaction.commit();
            System.out.println("Ticket guardado con ID: " + ticket.getTicketId());
            return true;

        } catch (Exception e) {
            if (transaction != null && transaction.getStatus().canRollback()) {
                transaction.rollback();
            }
            System.err.println("Error al guardar el ticket: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    /**
     * Metodo para actualizar un ticket existente en la base de datos
     * @param Ticket El ticket a actualizar
     */
    public boolean actualizar(Ticket ticket) {
        Transaction transaction = null;
        Session session = null;
        
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            
            session.merge(ticket);
            
            transaction.commit();
            return true;
            
        } catch (Exception e) {
            if (transaction != null && transaction.getStatus().canRollback()) {
                transaction.rollback();
            }
            System.err.println("Error al actualizar ticket: " + e.getMessage());
            return false;
            
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    /**
     * Metodo para obtener un ticket de la base de datos por su ID
     * @param ticketId El ID del ticket a obtener
     * @return El ticket encontrado o null si no existe
     */
    public Ticket obtener(Long ticketId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Ticket.class, ticketId);
        }
    }

    /**
     * Metodo para obtener los tickets de una fecha especifica
     * @param fecha LocalDate de los tickets a obtener
     * @return Lista de tickets encontrados
     */
    public List<Ticket> obtener(LocalDate fecha) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Ticket t WHERE t.reporte.fecha_reporte =:fecha";
            Query<Ticket> query = session.createQuery(hql, Ticket.class);
            query.setParameter("fecha", fecha);
            return query.list();
        }
    }

    public boolean eliminar(Long ticketId) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            Ticket ticket = session.get(Ticket.class, ticketId);
            if (ticket != null) {
                session.remove(ticket);
            }

            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null && transaction.getStatus().canRollback()) {
                transaction.rollback();
            }
            System.err.println("Error al eliminar ticket: " + e.getMessage());
            return false;
        } finally {
            if (session != null && session.isOpen()) session.close();
        }
    }
}
