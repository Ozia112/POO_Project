package backend.model;

public interface Descuento {
    String getName();
    float getPorcentaje();

    default float aplicar(float montoOriginal) {
        return montoOriginal - (montoOriginal * (getPorcentaje() / 100f));
    }
}