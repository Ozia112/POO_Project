package model;

import java.time.Instant;

public class Renta extends TipoServicio {
    private Instant inicio_renta;
    private Instant cierre_renta;
    private boolean stateOcupado;
    private Ubicacion ubicacion;

    public Renta(String nombre, float precio, Instant inicio_renta, Ubicacion ubicacion) {
        super(nombre, precio, 0);
        this.inicio_renta = inicio_renta;
        this.stateOcupado = false;
        this.ubicacion = ubicacion;
    }

    public Renta() {
        super();
    }

    public float getPrecio() { return precio; }
    public Instant getInicioRenta() { return inicio_renta; }
    public Instant getCierreRenta() { return cierre_renta; }
    public int getCantidad() { return cantidad; }
    public boolean getStateOcupado() { return stateOcupado; }
    public Ubicacion getUbicacion() { return ubicacion; }

    public void setInicioRenta(Instant inicio_renta) { this.inicio_renta = inicio_renta; }
    public void setPrecioRenta(float precio) { this.precio = precio; }
    public void setCierreRenta(Instant cierre_renta) { this.cierre_renta = cierre_renta; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public void setStateOcupado(boolean stateOcupado) { this.stateOcupado = stateOcupado; }
    public void setUbicacion(Ubicacion ubicacion) { this.ubicacion = ubicacion; }
}