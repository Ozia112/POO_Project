package backend.model;

import java.time.LocalDateTime;

public interface TipoServicio {
    String getNombre();
    float getPrecio();
    LocalDateTime getFecha();
    int getCantidad();
}
