package controller;

import java.time.Instant;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import model.Renta;
import model.Ticket;
import model.Ubicacion;

public class RentaController {
    final int MINUTOS_EN_HORA = 60;
    final int MINUTOS_CANCELACION = Config.getMinutosCancelacion();
    final int MINUTOS_TOLERANCIA = Config.getMinutosTolerancia();
    final int LIMITE_MINUTOS_PRIMERA_HORA = MINUTOS_EN_HORA + MINUTOS_TOLERANCIA;
    private Map<Ubicacion, Renta> lockers;

    public RentaController() {
        this.lockers = new HashMap<>();
        inicializarLockers();
    }

    private void inicializarLockers() {
        // Crear los lockers en todas las ubicaciones
        for (Ubicacion ubicacion : Ubicacion.values()) {
            Renta locker = new Renta("Locker", Config.getPrecioHoraLocker(), null, ubicacion);
            locker.setStateOcupado(false); // Inicialmente todos los lockers están libres
            lockers.put(ubicacion, locker);
        }
    }

    public void iniciarRenta(Ubicacion ubicacion, Ticket ticket) throws Exception {
        Renta locker = lockers.get(ubicacion);
        
        if (locker.getStateOcupado()) {
            throw new Exception("El locker ya está ocupado");
        }
        
        // Sobreescribir el locker con una nueva renta
        locker.setInicio_renta(ticket.getFecha_emision());
        locker.setStateOcupado(true);
        locker.setCierre_renta(null);
        locker.setCantidad(0); // La cantidad se calculará al cerrar la renta
    }

    public Renta cerrarRenta(Ubicacion ubicacion) throws Exception {
        Renta renta = lockers.get(ubicacion);

        if (!renta.getStateOcupado()) {
            throw new Exception("El locker ya está libre");
        }
        
        Instant inicio = renta.getInicio_renta();
        Instant cierre = Instant.now();

        renta.setCierre_renta(cierre);
        
        // Calcular la cantidad de horas rentadas
        long diferenciaMinutos = Duration.between(inicio, cierre).toMinutes();
        int horasRentadas;

        if (diferenciaMinutos <= MINUTOS_CANCELACION) {
            horasRentadas = 0; // Se puede cancelar sin costo antes de este límite
        } else if (diferenciaMinutos <= LIMITE_MINUTOS_PRIMERA_HORA) {
            horasRentadas = 1; // Dentro del tiempo de tolerancia, se cobra solo la primera hora
        } else {
            long minutosRestantes = diferenciaMinutos - LIMITE_MINUTOS_PRIMERA_HORA;
            int horasAdicionales = (int) ((minutosRestantes + MINUTOS_EN_HORA - 1) / MINUTOS_EN_HORA);
            horasRentadas = 1 + horasAdicionales;
        }

        renta.setCantidad(horasRentadas);
        renta.setStateOcupado(false);

        return renta;
    }

    public boolean estaDisponible(Ubicacion ubicacion) {
        return !lockers.get(ubicacion).getStateOcupado();
    }

    public Renta getRenta(Ubicacion ubicacion) {
        return lockers.get(ubicacion);
    }
}
