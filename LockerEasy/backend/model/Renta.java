package backend.model;

import java.time.LocalDateTime;

public class Renta implements TipoServicio {
    private int id;
    private String nombre;
    private LocalDateTime fecha;
    private float precio;
    private int cantidad;
    private ObjetoRenta locker;
    private int tiempo;

    public Renta(int id, String nombre, LocalDateTime fecha, float precio, int cantidad, ObjetoRenta locker, int tiempo) {
        this.id = id;
        this.nombre = nombre;
        this.fecha = fecha;
        this.precio = precio;
        this.cantidad = cantidad;
        this.locker = locker;
        this.tiempo = tiempo;
    }

    @Override
    public String getNombre() {
        return nombre;
    }

    @Override
    public float getPrecio() {
        return precio;
    }

    @Override
    public LocalDateTime getFecha() {
        return fecha;
    }

    @Override
    public int getCantidad() {
        return cantidad;
    }

    public ObjetoRenta getLocker() {
        return locker;
    }

    public int getTiempo() {
        return tiempo;
    }

    public float calcularTotalRenta() {
        return precio * tiempo;
    }

    @Override
    public String toString() {
        return "Renta{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", fecha=" + fecha +
                ", precio=" + precio +
                ", cantidad=" + cantidad +
                ", locker=" + locker +
                ", tiempo=" + tiempo +
                '}';
    }
}