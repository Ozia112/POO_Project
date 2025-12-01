package model;  

import jakarta.persistence.*;   

@Entity
@Table(name = "servicios")


public class Servicio {

    @Transient
    private final float DESCUENTO = controller.Config.getDescuentoUnico();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "servicio_id")
    private Long servicio_id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tipo_servicio_id")
    private TipoServicio tipo_servicio;

    @Column(name = "total_servicio")
    private float total_servicio;

    @Column(name = "aplica_descuento")
    private boolean aplicar_descuento;

    public Servicio(){

    }

    public Servicio(Long servicio_id, TipoServicio tipo_servicio, boolean aplicar_descuento) {
        this.servicio_id = servicio_id;
        this.tipo_servicio = tipo_servicio;
        this.aplicar_descuento = aplicar_descuento;
        this.total_servicio = 0f;
    }

    //public Servicio() {
    //}

    public Long getServicioId() { return servicio_id; }
    public TipoServicio getTipoServicio() { return tipo_servicio; }
    public boolean isAplicarDescuento() { return aplicar_descuento; }
    public float getTotalServicio() {
        total_servicio = tipo_servicio.cantidad  * tipo_servicio.precio;
        return aplicar_descuento ? total_servicio - (total_servicio * DESCUENTO / 100) : total_servicio;
    }

    public void setServicioId(Long servicio_id) { this.servicio_id = servicio_id; }
    public void setTipoServicio(TipoServicio tipo_servicio) { this.tipo_servicio = tipo_servicio; }
    public void setAplicarDescuento(boolean aplicar_descuento) { this.aplicar_descuento = aplicar_descuento; }
    public void setTotalServicio(float total_servicio) { this.total_servicio = total_servicio; }
}