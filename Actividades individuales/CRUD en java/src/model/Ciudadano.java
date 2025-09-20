package model;

public class Ciudadano {

    private String primerNombre;
    private String segundoNombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String curp;
    private String email;
    private String telefono;
    private int distrito;
    private int edad;

    public Ciudadano(String primerNombre,
                     String segundoNombre,
                     String apellidoPaterno,
                     String apellidoMaterno,
                     String curp,
                     String email,
                     String telefono,
                     int distrito,
                     int edad) {
        this.primerNombre    = primerNombre;
        this.segundoNombre   = segundoNombre;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.curp            = curp;
        this.email           = email;
        this.telefono        = telefono;
        this.distrito        = distrito;
        this.edad            = edad;
    }

    // Metodos Get/Set

    public String getPrimerNombre() { return primerNombre; }
    public String getSegundoNombre() { return segundoNombre; }
    public String getApellidoPaterno() { return apellidoPaterno; }
    public String getApellidoMaterno() { return apellidoMaterno; }
    public String getCurp() { return curp; }
    public String getEmail() { return email; }
    public String getTelefono() { return telefono; }
    public int getDistrito() { return distrito; }
    public int getEdad() { return edad; }
}
