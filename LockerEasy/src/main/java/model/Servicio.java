package model;

public class Servicio {
    private final float DESCUENTO = controller.Config.getDescuentoUnico();
    private int servicio_id;
    private TipoServicio tipo_servicio;
    private float total_servicio;
    private boolean aplicar_descuento;

    public Servicio(int servicio_id, TipoServicio tipo_servicio, boolean aplicar_descuento) {
        this.servicio_id = servicio_id;
        this.tipo_servicio = tipo_servicio;
        this.aplicar_descuento = aplicar_descuento;
        this.total_servicio = 0f;
    }

    public Servicio() {
    }

    public int getServicioId() { return servicio_id; }
    public TipoServicio getTipoServicio() { return tipo_servicio; }
    public boolean isAplicarDescuento() { return aplicar_descuento; }
    public float getTotalServicio() {
        total_servicio = tipo_servicio.cantidad  * tipo_servicio.precio;
        return aplicar_descuento ? total_servicio - (total_servicio * DESCUENTO / 100) : total_servicio;
    }

    public void setServicioId(int servicio_id) { this.servicio_id = servicio_id; }
    public void setTipoServicio(TipoServicio tipo_servicio) { this.tipo_servicio = tipo_servicio; }
    public void setAplicarDescuento(boolean aplicar_descuento) { this.aplicar_descuento = aplicar_descuento; }
    public void setTotalServicio(float total_servicio) { this.total_servicio = total_servicio; }
}