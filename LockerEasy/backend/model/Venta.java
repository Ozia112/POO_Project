package backend.model;

import java.time.LocalDateTime;
import java.util.List;

public class Venta implements TipoServicio {
    private int id;
    private String nombre;
    private LocalDateTime fecha;
    private float precio;
    private int cantidad;
    private List<String> productos;

    public Venta(int id, String nombre, LocalDateTime fecha, float precio, int cantidad, List<String> productos) {
        this.id = id;
        this.nombre = nombre;
        this.fecha = fecha;
        this.precio = precio;
        this.cantidad = cantidad;
        this.productos = productos;
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

    public List<String> getProductos() {
        return productos;
    }

    public float calcularTotalVenta() {
        return precio * cantidad;
    }

    @Override
    public String toString() {
        return "Venta{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", fecha=" + fecha +
                ", precio=" + precio +
                ", cantidad=" + cantidad +
                ", productos=" + productos +
                '}';
    }
}
