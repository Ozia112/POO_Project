package backend.model;

public class Servicio {
    private int id;
    private TipoServicio tipoServicio;
    private Descuento descuento;
    private float total;

    public Servicio(int id, TipoServicio tipoServicio, Descuento descuento, float total) {
        this.id = id;
        this.tipoServicio = tipoServicio;
        this.descuento = descuento;
        this.total = total;
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
        return total;
    }

    public float calcularTotal() {
        return total;
    }

    public void aplicarDescuento(float porcentaje) {
        float descuentoAplicado = total * (porcentaje / 100f);
        total -= descuentoAplicado;
    }

    @Override
    public String toString() {
        return "Servicio{" +
                "id=" + id +
                ", tipoServicio=" + (tipoServicio != null ? tipoServicio.getNombre() : "null") +
                ", descuento=" + (descuento != null ? descuento.getName() : "null") +
                ", total=" + total +
                '}';
    }
}