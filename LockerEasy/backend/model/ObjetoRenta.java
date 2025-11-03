package backend.model;

public interface ObjetoRenta {
    int getId();
    boolean estaOcupado();
    Ubicacion getUbicacion();
}