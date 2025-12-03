package model;

import jakarta.persistence.*;

@Entity
@Table(name = "productos_catalogos")
public class ProductoCatalogo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "producto_id")
    private Long id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "precio", nullable = false)
    private float precio;

    @Column(name = "existentes", nullable = false)
    private int existentes;

    @Column(name = "disponible", nullable = false)
    private boolean disponible;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "etiqueta_id")
    private Etiqueta etiqueta;

    ProductoCatalogo() {
        this.disponible = true;
        this.existentes = 0;
    }

    public ProductoCatalogo(String nombre, float precio, int existentes, Etiqueta etiqueta) {
        this.nombre = nombre;
        this.precio = precio;
        this.existentes = existentes;
        this.etiqueta = etiqueta;
        this.disponible = existentes > 0;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public float getPrecio() { return precio; }
    public int getExistentes() { return existentes; }
    public boolean isDisponible() { return disponible; }
    public Etiqueta getEtiqueta() { return etiqueta; }

    public void setId(Long id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setPrecio(float precio) { this.precio = precio; }
    public void setExistentes(int existentes) { this.existentes = existentes; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
    public void setEtiqueta(Etiqueta etiqueta) { this.etiqueta = etiqueta; }
}
