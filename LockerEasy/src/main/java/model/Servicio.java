package model;

public class Servicio {

    private int id;
    private TipoServicio tipoServicio;
    private Descuento descuento;
    private float total;

    public Servicio(int id, TipoServicio tipoServicio, Descuento descuento) {
        this.id = id;
        this.tipoServicio = tipoServicio;
        this.descuento = descuento;
        this.total = 0f;
    }

    public int getId() {
        return id;
    }

    public TipoServicio getTipoServicio() {
        return tipoServicio;
    }

    public Descuento getDescuento() {
        return descuento;
    }

    public float getTotal() {
        total = tipoServicio.cantidad  * tipoServicio.precio;
        return total;
    }
}