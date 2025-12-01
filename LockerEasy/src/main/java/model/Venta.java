package model;

import jakarta.persistence.*;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "ventas") 
@PrimaryKeyJoinColumn(name = "venta_id")         //aqui iba a poner un primaryKey para que se una a TipoServicio con el mismo Id, esta bien?
public class Venta extends TipoServicio {

    //private int id;

    @Column(name = "cantidad_existentes")
    private int existentes; // inventario
<<<<<<< HEAD
    private List<String> etiquetas;
            private boolean disponible; // true si existen > 0 o si es inagotable(estos pueden ser modificados desde configuracion)

                   
    
        @Override
        public String toString() {
            return getNombre() + " ($" + getPrecio() + ")";
        }


=======

    @Column(name = "disponible")
    private boolean disponible; // true si existen > 0 o si es inagotable(estos pueden ser modificados desde configuracion)
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "etiqueta_id")
    private Etiqueta etiqueta;
    
    //Constructor vacio
    public Venta() {
        super();
     }
    
>>>>>>> temp.TM-01.Design.DATABASE-WIP

    public Venta( String nombre, float precio, int cantidad, int existentes, Etiqueta etiqueta, boolean disponible) {
        super(nombre, precio, cantidad);
        this.existentes = existentes;
        this.etiqueta = etiqueta;
        this.disponible = disponible;
    }

    public Venta(String nombre, float precio, int existentes, Etiqueta etiqueta, boolean disponible) {
        super(nombre, precio,0);
        this.existentes = existentes;
        this.etiqueta = etiqueta;
        this.disponible = disponible;
    }

    
    
    public Long getIdProducto() { return super.getTipoServicioId (); }
    public int getExistentes() { return existentes; }
    public Etiqueta getEtiqueta() { return etiqueta; }
    public boolean isDisponible() { return disponible; }

    public void setIdProducto(Long id) { super.setId (id); }
    public void setExistentes(int existentes) { this.existentes = existentes; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
<<<<<<< HEAD
    public void setEtiquetas(List<String> etiquetas) { this.etiquetas = etiquetas; }



}
=======
    public void setEtiqueta(Etiqueta etiqueta) { this.etiqueta = etiqueta; }
}
>>>>>>> temp.TM-01.Design.DATABASE-WIP
