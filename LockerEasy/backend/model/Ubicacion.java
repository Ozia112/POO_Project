package backend.model;

public enum Ubicacion {
    PLANTA_BAJA("Planta Baja"),
    PLANTA_ALTA("Planta Alta");

    private final String descripcion;

    Ubicacion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}