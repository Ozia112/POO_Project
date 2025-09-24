package view;
public class CiudadanoFormulario {
    private String nombres;
    private String primerApellido;
    private String segundoApellido;
    private String curp;
    private String email;
    private String telefono;
    private String distrito;


    // Constructor
    public CiudadanoFormulario(String nombres,
                               String apellidoPaterno,
                               String apellidoMaterno,
                               String curp,
                               String email,
                               String telefono,
                               String distrito) {
        this.nombres = nombres;
        this.primerApellido = apellidoPaterno;
        this.segundoApellido = apellidoMaterno;
        this.curp = (curp == null) ? null : curp.toUpperCase();
        this.email = email;
        this.telefono = telefono;
        this.distrito = distrito;
    }

    // Metodos Get/Set
    public String getNombres() { return nombres; }
    public String getPrimerApellido() { return primerApellido; }
    public String getSegundoApellido() { return segundoApellido; }
    public String getCurp() { return curp; }
    public String getEmail() { return email; }
    public String getTelefono() { return telefono; }
    public String getDistrito() { return distrito; }

    public void setNombres(String nombres) { this.nombres = nombres; }
    public void setPrimerNombre(String primerNombre) { this.nombres = primerNombre; }
    public void setSegundoNombre(String segundoNombre) { this.nombres = segundoNombre; }
    public void setPrimerApellido(String apellidoPaterno) { this.primerApellido = apellidoPaterno; }
    public void setSegundoApellido(String apellidoMaterno) { this.segundoApellido = apellidoMaterno; }
    public void setCurp(String curp) { this.curp = curp == null ? null : curp.toUpperCase(); }
    public void setEmail(String email) { this.email = email; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public void setDistrito(String distrito) { this.distrito = distrito; }
}
