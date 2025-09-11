package controller;
import java.time.LocalDate;
import java.time.Period;

import dto.FormularioCiudadano;
import model.Ciudadano;

public class CiudadanoController {
    private Ciudadano ciudadanoValidado;

    public void validar(FormularioCiudadano formulario) {
        if (formulario == null) throw new IllegalArgumentException("Formulario no puede ser nulo");
        if (esVacio(formulario.getPrimerNombre())) throw new IllegalArgumentException("Primer nombre obligatorio");

        String curp = formulario.getCurp();
        // Referencia oficial: https://www.gob.mx/curp
        // Estructura CURP: 4 letras (nombre y apellidos), 6 dígitos (fecha), 1 letra (sexo), 2 letras (entidad), 3 letras (consonantes internas), 1 alfanumérico (homoclave), 1 dígito verificador
        String REGEX_FIRST_L = "[A-Z][AEIOU][A-Z]{2}";
        String REGEX_DATE = "\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12][0-9]|3[01])";
        String REGEX_GNERE = "[HMX]";
        String REGEX_ENTITY = "(AS|BC|BS|CC|CL|CM|CS|CH|DF|DG|GT|GR|HG|JC|MC|MN|MS|NT|NL|OC|PL|QT|QR|SP|SL|SR|TC|TL|TS|VZ|YN|ZS)"; // Entidad federativa
        String REGEX_CONSO = "[B-DF-HJ-NP-TV-Z]{3}";
        String REGEX_HOMOCLAVE = "[A-Z0-9]";
        String REGEX_VERIF_DGT = "\\d";

        String REGEX_CURP = "^" + REGEX_FIRST_L + REGEX_DATE + REGEX_GNERE + REGEX_ENTITY + REGEX_CONSO + REGEX_HOMOCLAVE + REGEX_VERIF_DGT + "$";
        
        if (curp == null || curp.length() != 18) throw new IllegalArgumentException("CURP debe tener 18 caracteres");
        if (!curp.matches(REGEX_CURP)) throw new IllegalArgumentException("CURP formato inválido");

        int edad = calcularEdadDesdeCurp(curp);
        if (edad < 18) throw new IllegalArgumentException("El ciudadano debe ser mayor de edad");

        if (esVacio(formulario.getApellidoPaterno()) || esVacio(formulario.getApellidoMaterno()))
            throw new IllegalArgumentException("Apellidos no pueden estar vacíos");

        String telefono = formulario.getTelefono();
        if (telefono == null || !telefono.matches("\\d{10}"))
            throw new IllegalArgumentException("Teléfono debe tener 10 dígitos numéricos");

        String email = formulario.getEmail();
        if (email == null || !email.contains("@")) throw new IllegalArgumentException("Email inválido");

        int distrito = formulario.getDistrito();
        if (distrito < 1 || distrito > 9) throw new IllegalArgumentException("Distrito fuera de rango (1-9)");
    }

    public Ciudadano crearCiudadano(FormularioCiudadano formulario) {
        validar(formulario);
        int edad = calcularEdadDesdeCurp(formulario.getCurp());
        ciudadanoValidado = Ciudadano.fromFormulario(formulario, edad);
        return ciudadanoValidado;
    }

    public Ciudadano getCiudadanoValidado() {
        return ciudadanoValidado;
    }

    private boolean esVacio(String str) {
        return str == null || str.trim().isEmpty();
    }

    private int calcularEdadDesdeCurp(String curp) {
        String yyStr = curp.substring(4, 6);
        String mmStr = curp.substring(6, 8);
        String ddStr = curp.substring(8, 10);
        int yy = Integer.parseInt(yyStr);
        int currentYY = LocalDate.now().getYear() % 100;
        int century = (yy > currentYY) ? 1900 : 2000;
        int year = century + yy;
        int month = Integer.parseInt(mmStr);
        int day = Integer.parseInt(ddStr);
        LocalDate birth;
        try {
            birth = LocalDate.of(year, month, day);
        } catch (RuntimeException e) {
            return 0;
        }
        return Period.between(birth, LocalDate.now()).getYears();
    }
}
