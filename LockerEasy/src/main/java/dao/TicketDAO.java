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
    public void guardar(Ticket ticket) {
        Transaction transaction = null;
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(ticket);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Error al guardar el ticket");
        }
    }

    /**
     * Metodo para actualizar un ticket existente en la base de datos
     * @param Ticket El ticket a actualizar
     */
    public void actualizar(Ticket ticket) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()){
            transaction = session.beginTransaction();
            session.merge(ticket);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Error al actualizar el ticket");
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
     * Metodo para obtener los tickets de unq fecha especifica
     * @param fecha LocalDate de los tickets a obtener
     * @return Lista de tickets encontrados
     */
    public List<Ticket> obtener(LocalDate fecha) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Ticket t WHERE t.fecha_reporte =:fecha";
            Query<Ticket> query = session.createQuery(hql, Ticket.class);
            query.setParameter("fecha", fecha);
            return query.list();
        }
    }

    public boolean eliminar(Long ticketId) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Ticket ticket = session.get(Ticket.class, ticketId);
            if (ticket != null) {
                session.remove(ticket);
                transaction.commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Error al eliminar ticket", e);
        }
    }
}
