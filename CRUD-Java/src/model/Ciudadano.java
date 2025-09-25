package model;

public class Ciudadano {

    private String primerNombre;
    private String segundoNombre;
    private String primerApellido;
    private String segundoApellido;
    private String curp;
    private String email;
    private String telefono;
    private int distrito;
    private int edad;

    public Ciudadano(String primerNombre,
                     String segundoNombre,
                     String primerApellido,
                     String segundoApellido,
                     String curp,
                     String email,
                     String telefono,
                     int distrito,
                     int edad) {
        this.primerNombre    = primerNombre;
        this.segundoNombre   = segundoNombre;
        this.primerApellido = primerApellido;
        this.segundoApellido = segundoApellido;
        this.curp            = curp;
        this.email           = email;
        this.telefono        = telefono;
        this.distrito        = distrito;
        this.edad            = edad;
    }

    // Metodos Get/Set

    public String getPrimerNombre() { return primerNombre; }
    public String getSegundoNombre() { return segundoNombre; }
    public String getPrimerApellido() { return primerApellido; }
    public String getSegundoApellido() { return segundoApellido; }
    public String getCurp() { return curp; }
    public String getEmail() { return email; }
    public String getTelefono() { return telefono; }
    public int getDistrito() { return distrito; }
    public int getEdad() { return edad; }
}
