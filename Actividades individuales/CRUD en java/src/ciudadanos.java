package srcj;
import java.time.LocalDate;
import java.time.Period;
import java.time.DateTimeException;

public class ciudadanos {

   private String PrimerNombre;
   private String SegundoNombre;
   private String PrimerApellido;
   private String SegundoApellido;
   private String CURP;
   private int edad;

    public String getNombresString() {
        return PrimerNombre + " " + SegundoNombre;
    }

    public String getApellidosString() {
        return PrimerApellido + " " + SegundoApellido;
    }

    public int getEdad() {
        return edad;
    }

    public String getCURP() {
        return CURP;
    }

    public void setNombres(String primerNombre, String segundoNombre) {
        this.PrimerNombre = primerNombre;
        this.SegundoNombre = segundoNombre;
    }

    public void setApellidos(String primerApellido, String segundoApellido) {
        this.PrimerApellido = primerApellido;
        this.SegundoApellido = segundoApellido;
    }

    public void setCURP(String CURP) {
        if (CURP == null || CURP.isBlank()) {
            throw new IllegalArgumentException("CURP no puede ser nulo o vacio");
        }
        this.CURP = CURP.trim().toUpperCase();
        this.edad = setEdad();
    }

    private int setEdad() {
        String src = this.CURP;
        if (src == null || src.length() < 18) {
            throw new IllegalArgumentException("CURP invalido, debe tener 18 caracteres");
        }

        String yearString = src.substring(4, 6);
        String monthString = src.substring(6, 8);
        String dayString = src.substring(8, 10);
        
        char centuryFlag = src.charAt(16); // index pos 16, string pos 17
        
        int yy = Integer.parseInt(yearString);
        int yyyy = (centuryFlag == 'A') ? 2000 : 1900 + yy;
        int mm = Integer.parseInt(monthString);
        int dd = Integer.parseInt(dayString);

        LocalDate birthDate;
        try {
            birthDate = LocalDate.of(yyyy, mm, dd);
        } catch (DateTimeException e) {
            throw new IllegalArgumentException("Fecha de nacimiento invalida en CURP", e);
        }
        
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
}
