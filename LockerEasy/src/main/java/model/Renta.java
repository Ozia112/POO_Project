package model;

import java.time.Instant;

public class Renta extends TipoServicio {

    private Instant inicio_renta;
    private Instant cierre_renta;
    private boolean stateOcupado;
    private Ubicacion ubicacion;

    public Renta(String nombre, float precio, Instant inicio_renta, Ubicacion ubicacion) {
        super(nombre, precio, 0); // La cantidad para Renta es 0 se calcula al cierre de la renta.
        this.inicio_renta = inicio_renta;
        this.stateOcupado = false; // Al inicializar un locker, este está libre
        this.ubicacion = ubicacion;
    }

    public Instant getInicio_renta() {
        return inicio_renta;
    }

    public Instant getCierre_renta() {
        return cierre_renta;
    }

    public boolean getStateOcupado() {
        return stateOcupado;
    }

    public Ubicacion getUbicacion() {
        return ubicacion;
    }

    public void setInicio_renta(Instant inicio_renta) {
        this.inicio_renta = inicio_renta;
    }

    public void setCierre_renta(Instant cierre_renta) {
        this.cierre_renta = cierre_renta;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public void setStateOcupado(boolean stateOcupado) {
        this.stateOcupado = stateOcupado;
    }

    public void setUbicacion(Ubicacion ubicacion) {
        this.ubicacion = ubicacion;
    }
}