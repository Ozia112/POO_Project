package view;
public class FormularioCiudadano {
    private String nombres;
    private String primerNombre;
    private String segundoNombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String curp;
    private String email;
    private String telefono;
    private int distrito;

    public FormularioCiudadano(String nombres,
                               String apellidoPaterno,
                               String apellidoMaterno,
                               String curp,
                               String email,
                               String telefono,
                               int distrito) {
        this.nombres = nombres;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.curp = (curp == null) ? null : curp.toUpperCase();
        this.email = email;
        this.telefono = telefono;
        this.distrito = distrito;
    }

    // Metodos Get/Set
    public String getNombres() { return nombres; }
    public String getPrimerNombre() { return primerNombre; }
    public String getSegundoNombre() { return segundoNombre; }
    public String getApellidoPaterno() { return apellidoPaterno; }
    public String getApellidoMaterno() { return apellidoMaterno; }
    public String getCurp() { return curp; }
    public String getEmail() { return email; }
    public String getTelefono() { return telefono; }
    public int getDistrito() { return distrito; }

    public void setNombres(String nombres) { this.nombres = nombres; }
    public void setPrimerNombre(String primerNombre) { this.nombres = primerNombre; }
    public void setSegundoNombre(String segundoNombre) { this.nombres = segundoNombre; }
    public void setApellidoPaterno(String apellidoPaterno) { this.apellidoPaterno = apellidoPaterno; }
    public void setApellidoMaterno(String apellidoMaterno) { this.apellidoMaterno = apellidoMaterno; }
    public void setCurp(String curp) { this.curp = curp == null ? null : curp.toUpperCase(); }
    public void setEmail(String email) { this.email = email; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public void setDistrito(int distrito) { this.distrito = distrito; }
}
