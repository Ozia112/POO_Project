package model;

public class Etiqueta {
    private String nombre;
    private boolean afectaInventario;

    public Etiqueta(String nombre, boolean afectaInventario) {
        this.nombre = nombre;
        this.afectaInventario = afectaInventario;
    }

    public String getNombre() {
        return nombre;
    }

    public boolean isAfectaInventario() {
        return afectaInventario;
    }
}
