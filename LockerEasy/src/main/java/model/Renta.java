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

    public Renta(String nombre, int cantidad, Instant inicio_renta, Ubicacion ubicacion) {
        super(nombre, cantidad);
        this.inicio_renta = inicio_renta;
        this.ubicacion = ubicacion;
    }

    // gets y sets
    public Instant getInicioRenta() { return inicio_renta; }
    public Instant getCierreRenta() { return cierre_renta; }
    public Ubicacion getUbicacion() { return ubicacion; }
    
    @Override
    public float getPrecio() { return ubicacion != null ? ubicacion.getPrecio() : 0f; }
    
    public void setInicioRenta(Instant inicio_renta) { this.inicio_renta = inicio_renta; }
    public void setCierreRenta(Instant cierre_renta) { this.cierre_renta = cierre_renta; }
    public void setUbicacion(Ubicacion ubicacion) { this.ubicacion = ubicacion; }

    
}
