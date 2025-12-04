package model;

import jakarta.persistence.*;

@Entity
@Table(name = "ventas") 
@PrimaryKeyJoinColumn(name = "venta_id")
public class Venta extends TipoServicio {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "producto_catalogo_id", nullable = false)
    private ProductoCatalogo productoCatalogo;

    public Venta() {
        super();
    }

    public Venta(String nombre, int cantidad, ProductoCatalogo productoCatalogo) {
        super(nombre, cantidad);
        this.productoCatalogo = productoCatalogo;
    }

    public ProductoCatalogo getProductoCatalogo() { return productoCatalogo; }

    
    
    @Override
    public float getPrecio() { return productoCatalogo != null ? productoCatalogo.getPrecio() : 0f; }

    public void setProductoCatalogo(ProductoCatalogo productoCatalogo) { this.productoCatalogo = productoCatalogo; }
}
