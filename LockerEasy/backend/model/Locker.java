package backend.model;

public class Locker implements ObjetoRenta {
    private int id;
    private boolean estaOcupado;
    private Ubicacion ubicacion;

    public Locker(int id, boolean estaOcupado, Ubicacion ubicacion) {
        this.id = id;
        this.estaOcupado = estaOcupado;
        this.ubicacion = ubicacion;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public boolean estaOcupado() {
        return estaOcupado;
    }

    @Override
    public Ubicacion getUbicacion() {
        return ubicacion;
    }

    public void setOcupado(boolean ocupado) {
        this.estaOcupado = ocupado;
    }

    public void setUbicacion(Ubicacion ubicacion) {
        this.ubicacion = ubicacion;
    }

    @Override
    public String toString() {
        return "Locker{" +
                "id=" + id +
                ", estaOcupado=" + estaOcupado +
                ", ubicacion=" + ubicacion +
                '}';
    }
}