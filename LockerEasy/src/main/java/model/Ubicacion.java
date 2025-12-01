package model;

import jakarta.persistence.*; 

@Entity
@Table(name = "ubicaciones")
public class Ubicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ubicacion_id")
    private Long ubicacion_id; //ID unica 

    @Column(name = "nombre_locker", nullable = false)
    private String nombre_locker; // Ej: "Locker 1 - est├índar", "Locker 2 - mediano"

    @Column(name = "nombre_torre", nullable = false)
    private String nombre_torre; // Ej: "Torre 1", "Torre 2"

    @Column(name = "localizacion", nullable = false)
    private String localizacion; // Ej: "Planta Baja", "Planta Alta"

    @Column(name = "disponible")
    private boolean disponible; // true = libre, false = ocupado
    
    public Ubicacion(){
        this.disponible = true;
    }

    public Ubicacion(String nombre_locker, String nombre_torre, String localizacion) {
        this.nombre_locker = nombre_locker;
        this.nombre_torre = nombre_torre;
        this.localizacion = localizacion;
        this.disponible = true;
    }

    // gets y sets
    public Long getUbicacionId() {return ubicacion_id; }
    public String getNombreLocker() { return nombre_locker; }
    public String getNombreTorre() { return nombre_torre; }
    public String getLocalizacion() { return localizacion; }
    public boolean isDisponible() { return disponible; }

    public void setUbicacionId(Long ubicacion_id) {this.ubicacion_id = ubicacion_id; }
    public void setNombreLocker(String nombre_locker) { this.nombre_locker = nombre_locker; }
    public void setNombreTorre(String nombre_torre) { this.nombre_torre = nombre_torre; }
    public void setLocalizacion(String localizacion) { this.localizacion = localizacion; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
}
