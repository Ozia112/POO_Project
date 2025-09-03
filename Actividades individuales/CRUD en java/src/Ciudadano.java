import java.time.LocalDate;
import java.time.Period;
import java.time.DateTimeException;

public class Ciudadano {

   private String primerNombre;
   private String segundoNombre;
   private String apellidoPaterno;
   private String apellidoMaterno;
   private String curp;
   private String email;
   private String telefono;
   private char distrito;
   private int edad;

    public String getPrimerNombre() {
        return primerNombre;
    }

    public String getSegundoNombre() {
        return segundoNombre;
    }

    public String getApellidoPaterno() {
        return apellidoPaterno;
    }

    public String getApellidoMaterno() {
        return apellidoMaterno;
    }

    public String getCurp() {
        return curp;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefono() {
        return telefono;
    }

    public char getDistrito() {
        return distrito;
    }

    public int getEdad() {
        return edad;
    }

    public String setPrimerNombre(String primerNombre) {
        return this.primerNombre = primerNombre;
    }

    public String setSegundoNombre(String segundoNombre) {
        return this.segundoNombre = segundoNombre;
    }

    public String setApellidoPaterno(String apellidoPaterno) {
        return this.apellidoPaterno = apellidoPaterno;
    }

    public String setApellidoMaterno(String apellidoMaterno) {
        return this.apellidoMaterno = apellidoMaterno;
    }

    public void setCurp(String curp) {
        if (curp == null || curp.isBlank()) {
            throw new IllegalArgumentException("curp no puede ser nulo o vacio");
        }
        this.curp = curp.trim().toUpperCase();
        this.edad = setEdad();
    }

    public String setEmail(String email) {
        return this.email = email;
    }

    public String setTelefono(String telefono) {
        return this.telefono = telefono;
    }

    public char setDistrito(char distrito) {
        return this.distrito = distrito;
    }

    private int setEdad() {
        String src = this.curp;
        if (src == null || src.length() < 18) {
            throw new IllegalArgumentException("curp invalido, debe tener 18 caracteres");
        }

        String yearString = src.substring(4, 6);
        String monthString = src.substring(6, 8);
        String dayString = src.substring(8, 10);
        
        char centuryFlag = src.charAt(16); // index pos 16, string pos 17
        
        int yy = Integer.parseInt(yearString);
        int yyyy = (centuryFlag == 'A') ? 2000 + yy : 1900 + yy;
        int mm = Integer.parseInt(monthString);
        int dd = Integer.parseInt(dayString);

        LocalDate birthDate;
        try {
            birthDate = LocalDate.of(yyyy, mm, dd);
        } catch (DateTimeException e) {
            throw new IllegalArgumentException("Fecha de nacimiento invalida en curp", e);
        }
        
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    public static void main(String[] args) {
        Ciudadano c1 = new Ciudadano();
        
        System.out.println("\nCreando ciudadano 1...");
        c1.setPrimerNombre("Isaac");
        c1.setSegundoNombre("Alejandro");
        c1.setApellidoPaterno("Ortiz");
        c1.setApellidoMaterno("Zaldivar");
        c1.setEmail("macintosh22plust@hotmail.com");
        c1.setTelefono("9994588510");
        c1.setDistrito('D');
        c1.setCurp("PACP051121MYNRNLA9");
        System.out.println("Nombres: " + c1.getPrimerNombre() + " " + c1.getSegundoNombre());
        System.out.println("Apellidos: " + c1.getApellidoPaterno() + " " + c1.getApellidoMaterno());
        System.out.println("Edad: " + c1.getEdad());
    }
}
