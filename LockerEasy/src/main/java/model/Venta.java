package model;

import java.util.List;

public class Venta {

    private int id;
    private String nombre;
    private float precio;
    private int existentes; // inventario
    private List<String> etiquetas;
    private int cantidad; // cantidad vendida
    private boolean disponible; // true si existen > 0 o si es inagotable(estos pueden ser modificados desde configuracion)

    public Venta(int id, String nombre, float precio, int existentes, List<String> etiquetas, boolean disponible) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.existentes = existentes;
        this.etiquetas = etiquetas;
        this.cantidad = 0;
        this.disponible = disponible;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public float getPrecio() { return precio; }
    public int getExistentes() { return existentes; }
    public List<String> getEtiquetas() { return etiquetas; }
    public int getCantidad() { return cantidad; }
    public boolean isDisponible() { return disponible; }

    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setPrecio(float precio) { this.precio = precio; }
    public void setExistentes(int existentes) { this.existentes = existentes; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
    public void setEtiquetas(List<String> etiquetas) { this.etiquetas = etiquetas; }
}
