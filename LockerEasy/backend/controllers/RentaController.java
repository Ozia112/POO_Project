package backend.controllers;

import backend.model.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Controller para gestionar las rentas de lockers
 * Cumple con FR1 y FR2
 */
public class RentaController {
    private Map<Integer, Renta> rentasActivas;
    private Map<Integer, Locker> lockers;
    private int siguienteIdRenta;

    public RentaController() {
        this.rentasActivas = new HashMap<>();
        this.lockers = new HashMap<>();
        this.siguienteIdRenta = 1;
        inicializarLockers();
    }

    // Inicializa algunos lockers de ejemplo
    private void inicializarLockers() {
        for (int i = 1; i <= 10; i++) {
            Locker locker = new Locker(i, false, i <= 5 ? Ubicacion.PLANTA_BAJA : Ubicacion.PLANTA_ALTA);
            lockers.put(i, locker);
        }
    }

    /**
     * FR1: Registrar una nueva renta de locker
     * @param nombreCliente Nombre del cliente
     * @param lockerId ID del locker a rentar
     * @param duracionHoras Duración de la renta en horas
     * @param precioHora Precio por hora
     * @return La renta creada o null si no se pudo crear
     */
    public Renta registrarRenta(String nombreCliente, int lockerId, int duracionHoras, float precioHora) {
        // Validar que el locker existe
        Locker locker = lockers.get(lockerId);
        if (locker == null) {
            System.out.println("Error: El locker " + lockerId + " no existe.");
            return null;
        }

        // FR2: Prevenir doble reserva
        if (locker.estaOcupado()) {
            System.out.println("Error: El locker " + lockerId + " ya está ocupado.");
            return null;
        }

        // Crear la renta
        Renta renta = new Renta(
            siguienteIdRenta++,
            nombreCliente,
            LocalDateTime.now(),
            precioHora,
            1,
            locker,
            duracionHoras
        );

        // Marcar el locker como ocupado
        locker.setOcupado(true);
        rentasActivas.put(renta.getTiempo(), renta); // Usando tiempo como ID temporal

        System.out.println("Renta registrada exitosamente:");
        System.out.println("- Cliente: " + nombreCliente);
        System.out.println("- Locker ID: " + lockerId);
        System.out.println("- Duración: " + duracionHoras + " horas");
        System.out.println("- Precio total: $" + renta.calcularTotalRenta());
        System.out.println("- Inicio: " + renta.getFecha());

        return renta;
    }

    /**
     * FR2: Finalizar una renta
     * @param lockerId ID del locker
     * @return true si se finalizó correctamente
     */
    public boolean finalizarRenta(int lockerId) {
        Locker locker = lockers.get(lockerId);
        if (locker == null) {
            System.out.println("Error: El locker " + lockerId + " no existe.");
            return false;
        }

        if (!locker.estaOcupado()) {
            System.out.println("Error: El locker " + lockerId + " no está en renta.");
            return false;
        }

        // Buscar la renta activa para este locker
        Renta rentaFinalizada = null;
        for (Renta renta : rentasActivas.values()) {
            if (renta.getLocker().getId() == lockerId) {
                rentaFinalizada = renta;
                break;
            }
        }

        if (rentaFinalizada != null) {
            rentasActivas.remove(rentaFinalizada.getTiempo());
            locker.setOcupado(false);
            System.out.println("Renta finalizada exitosamente.");
            System.out.println("- Locker " + lockerId + " ahora está disponible.");
            return true;
        }

        return false;
    }

    /**
     * FR2: Cancelar una renta (solicita feedback)
     * @param lockerId ID del locker
     * @param motivoCancelacion Razón de la cancelación
     * @return true si se canceló correctamente
     */
    public boolean cancelarRenta(int lockerId, String motivoCancelacion) {
        System.out.println("Procesando cancelación...");
        System.out.println("Motivo: " + motivoCancelacion);
        System.out.println("Se solicita feedback al cliente.");
        
        boolean resultado = finalizarRenta(lockerId);
        
        if (resultado) {
            System.out.println("Renta cancelada. Se ha enviado solicitud de feedback al cliente.");
        }
        
        return resultado;
    }

    /**
     * Obtener todos los lockers disponibles
     * @return Lista de lockers disponibles
     */
    public List<Locker> obtenerLockersDisponibles() {
        List<Locker> disponibles = new ArrayList<>();
        for (Locker locker : lockers.values()) {
            if (!locker.estaOcupado()) {
                disponibles.add(locker);
            }
        }
        return disponibles;
    }

    /**
     * Obtener todas las rentas activas
     * @return Lista de rentas activas
     */
    public List<Renta> obtenerRentasActivas() {
        return new ArrayList<>(rentasActivas.values());
    }

    /**
     * Obtener información de un locker específico
     * @param lockerId ID del locker
     * @return El locker o null si no existe
     */
    public Locker obtenerLocker(int lockerId) {
        return lockers.get(lockerId);
    }

    /**
     * Obtener el estado general del sistema
     */
    public void mostrarEstadoSistema() {
        System.out.println("\n=== ESTADO DEL SISTEMA ===");
        System.out.println("Lockers disponibles: " + obtenerLockersDisponibles().size());
        System.out.println("Rentas activas: " + rentasActivas.size());
        System.out.println("Total de lockers: " + lockers.size());
    }
}
