package backend.model;

import java.util.ArrayList;
import java.util.List;

public class Model {
    private List<Ticket> tickets;
    private List<Locker> lockers;

    public Model() {
        this.tickets = new ArrayList<>();
        this.lockers = new ArrayList<>();
    }

    public void agregarTicket(Ticket ticket) {
        tickets.add(ticket);
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public void agregarLocker(Locker locker) {
        lockers.add(locker);
    }

    public List<Locker> getLockers() {
        return lockers;
    }

    @Override
    public String toString() {
        return "Model{" +
                "tickets=" + tickets +
                ", lockers=" + lockers +
                '}';
    }
}