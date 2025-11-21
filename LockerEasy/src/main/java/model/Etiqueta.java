package model;

public class Etiqueta {
    private int etiqueta_id;
    private String nombre;
    private boolean afectaInventario;

    public Etiqueta(int etiqueta_id, String nombre, boolean afectaInventario) {
        this.etiqueta_id = etiqueta_id;
        this.nombre = nombre;
        this.afectaInventario = afectaInventario;
    }

    public Etiqueta() {}

    public int getEtiquetaId() { return etiqueta_id; }
    public String getNombre() { return nombre; }
    public boolean isAfectaInventario() { return afectaInventario; }

    public void setEtiquetaId(int etiqueta_id) { this.etiqueta_id = etiqueta_id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setAfectaInventario(boolean afectaInventario) { this.afectaInventario = afectaInventario; }
}
