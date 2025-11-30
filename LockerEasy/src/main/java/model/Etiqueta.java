package model;   

import jakarta.persistence.*;
@Entity
@Table(name = "etiquetas")
public class Etiqueta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "etiqueta_id")    
    private Long etiqueta_id; 

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "afecta_inventario")
    private boolean afectaInventario;

    public Etiqueta() {

    }

    public Etiqueta(String nombre, boolean afectaInventario) {   //quité el ID porque se generara splo
        this.nombre = nombre;
        this.afectaInventario = afectaInventario;
    }

    //public Etiqueta() {}

    public Long getEtiquetaId() { return etiqueta_id; }
    public String getNombre() { return nombre; }
    public boolean isAfectaInventario() { return afectaInventario; }

    public void setEtiquetaId(Long etiqueta_id) { this.etiqueta_id = etiqueta_id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setAfectaInventario(boolean afectaInventario) { this.afectaInventario = afectaInventario; }
}
