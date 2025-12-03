package model;    
import jakarta.persistence.*;
import controller.Config;

@Entity
@Table(name = "tipos_servicio")
@Inheritance(strategy = InheritanceType.JOINED)  //inheritance hace que renta pueda heredar aqui
public abstract class TipoServicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name="tipo_servicio_id")    //Agregu├® un ID porque hibernate necesita de un ID para identificar la fila
    protected Long tipo_servicio_id;

    @Column(name = "nombre")
    protected String nombre;

    @Column (name = "total")
    protected float total;

    @Column (name = "cantidad")
    protected int cantidad;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ticket_id")
    protected Ticket ticket;

    @Column(name = "aplicar_descuento")
    protected boolean aplicarDescuento;

    @Transient
    protected final float DESCUENTO = Config.getDescuentoUnico();

    public TipoServicio() {
        this.aplicarDescuento = false;
    }

    public TipoServicio(String nombre, int cantidad) {
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.aplicarDescuento = false;
    }

    //gets y sets para el id nuebo
    public Long getTipoServicioId() { return tipo_servicio_id; }
    public String getNombre() { return nombre; }
    public float getTotal() { return total; }
    public int getCantidad() { return cantidad; }
    public Ticket getTicket() { return ticket; }
    public boolean isAplicarDescuento() { return aplicarDescuento; }

    protected abstract float getPrecio();
    
    public void calcularTotal() {
        float precioBase = getPrecio() * cantidad;
        if (aplicarDescuento) {
            this.total = precioBase * (1 - DESCUENTO / 100);
        } else {
            this.total = precioBase;
        }
    }

    public void setId(Long tipo_servicio_id) { this.tipo_servicio_id = tipo_servicio_id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setPrecio(float total) { this.total = total; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; calcularTotal();}
    public void setTicket(Ticket ticket) { this.ticket = ticket; }
    public void setAplicarDescuento(boolean aplicarDescuento) { this.aplicarDescuento = aplicarDescuento; calcularTotal(); }
}
