package model;    
import jakarta.persistence.*;

@Entity
@Table(name = "tipos_servicio")
@Inheritance(strategy = InheritanceType.JOINED)  //inheritance hace que renta pueda heredar aqui
public abstract class TipoServicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name="tipo_servicio_id")    //Agregu├® un ID porque hibernate necesita de un ID para identificar la fila
    protected Long tipo_servicio_id;

    @Column(name = "nombre")
    private String nombre;

    @Column (name = "precio")
    protected float precio;

    @Column (name = "cantidad")
    protected int cantidad;

    public TipoServicio() {}

    public TipoServicio(String nombre, float precio, int cantidad) {
        this.nombre = nombre;
        this.precio = precio;
        this.cantidad = cantidad;
    }

    //gets y sets para el id nuebo
    public Long getTipoServicioId() { return tipo_servicio_id; }
    public String getNombre() { return nombre; }
    public float getPrecio() { return precio; }
    public int getCantidad() { return cantidad; }

    public void setId(Long tipo_servicio_id) { this.tipo_servicio_id = tipo_servicio_id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setPrecio(float precio) { this.precio = precio; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
}
