package model;

public abstract class TipoServicio {

    private String nombre;
    protected float precio;
    protected int cantidad;

    public TipoServicio(String nombre, float precio, int cantidad) {
        this.nombre = nombre;
        this.precio = precio;
        this.cantidad = cantidad;
    }

    public String getNombre() {
        return nombre;
    }

    public float getPrecio() {
        return precio;
    }

    public int getCantidad() {
        return cantidad;
    }
}
