package model;

public class Descuento {

    private int id;
    private boolean activo;
    private String nombre_descuento;
    private float porcentaje;

    public Descuento(int id, boolean activo, String nombre_descuento, float porcentaje) {
        this.id = id;
        this.activo = activo;
        this.nombre_descuento = nombre_descuento;
        this.porcentaje = porcentaje;
    }

    public int getId() {
        return id;
    }

    // En tu Servicio usas descuento.getState()
    public boolean getState() {
        return activo;
    }

    public String getName() {
        return nombre_descuento;
    }

    // En tu Servicio usas descuento.getDiscount()
    public float getDiscount() {
        return porcentaje;
    }
}
