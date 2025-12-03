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

    public TipoServicio(String nombre, float precio) {
        this.nombre = nombre;
        this.precio = precio;
    }

    public TipoServicio() {
        this.nombre = "";
        this.precio = 0f;
        this.cantidad = 0;
    }

    public String getNombre() { return nombre; }
    public float getPrecio() { return precio; }
    public int getCantidad() { return cantidad; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setPrecio(float precio) { this.precio = precio; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
}
