package model;

import java.util.List;

public class Venta extends TipoServicio {

    private int id;
    private int existentes; // inventario
    private List<String> etiquetas;
    private boolean disponible; // true si existen > 0 o si es inagotable(estos pueden ser modificados desde configuracion)

    public Venta(int id, String nombre, float precio, int cantidad, int existentes, List<String> etiquetas, boolean disponible) {
        super(nombre, precio, cantidad);
        this.id = id;
        this.existentes = existentes;
        this.etiquetas = etiquetas;
        this.disponible = disponible;
    }

    public Venta(int id, String nombre, float precio, int existentes, List<String> etiquetas, boolean disponible) {
        super(nombre, precio);
        this.id = id;
        this.existentes = existentes;
        this.etiquetas = etiquetas;
        this.disponible = disponible;
    }

    public Venta() { }

    public int getIdProducto() { return id; }
    public int getExistentes() { return existentes; }
    public List<String> getEtiquetas() { return etiquetas; }
    public boolean isDisponible() { return disponible; }

    public void setIdProducto(int id) { this.id = id; }
    public void setExistentes(int existentes) { this.existentes = existentes; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
    public void setEtiquetas(List<String> etiquetas) { this.etiquetas = etiquetas; }
}