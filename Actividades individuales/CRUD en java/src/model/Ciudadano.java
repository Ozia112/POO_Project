package model;

import dto.FormularioCiudadano;

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

    public void setPrimerNombre(String primerNombre) { this.primerNombre = primerNombre; }
    public void setSegundoNombre(String segundoNombre) { this.segundoNombre = segundoNombre; }
    public void setApellidoPaterno(String apellidoPaterno) { this.apellidoPaterno = apellidoPaterno; }
    public void setApellidoMaterno(String apellidoMaterno) { this.apellidoMaterno = apellidoMaterno; }
    public void setCurp(String curp) { this.curp = curp; }
    public void setEmail(String email) { this.email = email; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public void setDistrito(int distrito) { this.distrito = distrito; }
    public void setEdad(int edad) { this.edad = edad; }

    public static Ciudadano fromFormulario(FormularioCiudadano formularioCiudadano, int edadCalculada) {
        Ciudadano newCiudadano = new Ciudadano();
        newCiudadano.setPrimerNombre(formularioCiudadano.getPrimerNombre());
        newCiudadano.setSegundoNombre(formularioCiudadano.getSegundoNombre());
        newCiudadano.setApellidoPaterno(formularioCiudadano.getApellidoPaterno());
        newCiudadano.setApellidoMaterno(formularioCiudadano.getApellidoMaterno());
        newCiudadano.setCurp(formularioCiudadano.getCurp());
        newCiudadano.setEmail(formularioCiudadano.getEmail());
        newCiudadano.setTelefono(formularioCiudadano.getTelefono());
        newCiudadano.setDistrito(formularioCiudadano.getDistrito());
        newCiudadano.setEdad(edadCalculada);
        return newCiudadano;
    }
}
