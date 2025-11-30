package model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "rentas")
@PrimaryKeyJoinColumn(name = "renta_id") //usamos primarykey para que coincida con la estrategia JOINED del padre 
public class Renta extends TipoServicio {

    @Column (name = "inicio_renta", nullable = false)
    private Instant inicio_renta;

    @Column(name = "cierre_renta")
    private Instant cierre_renta;

    // Relacion ManyToOne: Muchas rentas pueden usar la misma ubicacion (no al mismo tiempo)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn (name = "ubicacion_id", nullable = false)
    private Ubicacion ubicacion;

    public Renta() {
        super();
    }

    public Renta(String nombre, float precio, Instant inicio_renta, Ubicacion ubicacion) {
        super(nombre, precio, 1);
        this.inicio_renta = inicio_renta;
        this.ubicacion = ubicacion;
    }

    @Override
    public float getPrecio() { return precio; }
    public Instant getInicioRenta() { return inicio_renta; }
    public Instant getCierreRenta() { return cierre_renta; }
    @Override
    public int getCantidad() { return cantidad; }
    public Ubicacion getUbicacion() { return ubicacion; }

    public void setInicioRenta(Instant inicio_renta) { this.inicio_renta = inicio_renta; }
    public void setPrecioRenta(float precio) { this.precio = precio; }
    public void setCierreRenta(Instant cierre_renta) { this.cierre_renta = cierre_renta; }
    @Override
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public void setUbicacion(Ubicacion ubicacion) { this.ubicacion = ubicacion; }
}