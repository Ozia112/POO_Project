package controller;
import java.time.LocalDate;
import java.time.Period;

import model.Ciudadano;

public class CiudadanoController {

    private FileManager fileManager;

    public CiudadanoController(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    public Ciudadano procesarCiudadano(
        String nombres,
        String primerNombre,
        String segundoNombre,
        String apellidoPaterno,
        String apellidoMaterno,
        String curp,
        String email,
        String telefono,
        String distritoStr,
        int distrito,
        int edad
    ) throws Exception {

        // Naturalizar entradas
        nombres = safe(nombres);
        apellidoPaterno = safe(apellidoPaterno);
        apellidoMaterno = safe(apellidoMaterno);
        curp = safe(curp).toUpperCase();
        email = safe(email).toLowerCase();
        telefono = safe(telefono);
        distritoStr = safe(distritoStr);

        // Validar obligatoriedad y formatos
        validarObligatorios(nombres, apellidoPaterno, apellidoMaterno, curp, email, telefono, distritoStr);
        validarCurp(curp);
        validarEmail(email);
        validarTelefono(telefono);

        // Parseamientos
        distrito = parseDistrito(distritoStr);
        primerNombre = parsearNombres(nombres)[0];
        segundoNombre = parsearNombres(nombres)[1];
        edad = calcularEdadDesdeCurp(curp);

        // Validaciones adicionales al parsear
        validarDistrito(distrito);
        validarEdad(edad);

        // Validar unicidad de CURP y edad
        if (fileManager.ciudadanoExiste(curp)) {
            throw new IllegalArgumentException("CURP ya registrado");
        }

        // Crear y guardar ciudadano
        Ciudadano ciudadano = new Ciudadano(primerNombre, segundoNombre, apellidoPaterno, apellidoMaterno, curp, email, telefono, distrito, edad);
        
        fileManager.guardarCiudadano(curp);

        return ciudadano;
    }

    private String safe(String str) { return str == null ? "" : str.trim(); }

    private void validarObligatorios(String... campos) {
        for (String campo : campos) {
            if (campo.isEmpty()) {
                throw new IllegalArgumentException("Campos obligatorios no pueden estar vacíos");
            }
        }
    }

    private void validarCurp(String curp) {
    /*
    Estructura CURP referencia oficial: https://www.gob.mx/curp: 
    - 4 letras (nombre y apellidos), 
    - 6 dígitos (fecha),
    - 1 letra (sexo),
    - 2 letras (entidad), 
    - 3 letras (consonantes internas),
    - 1 alfanumérico (homoclave), 
    - 1 dígito verificador
    */

        String REGEX_FIRST_L = "[A-Z][AEIOU][A-Z]{2}";
        String REGEX_DATE = "\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12][0-9]|3[01])";
        String REGEX_GNERE = "[HMX]";
        String REGEX_ENTITY = "(AS|BC|BS|CC|CL|CM|CS|CH|DF|DG|GT|GR|HG|JC|MC|MN|MS|NT|NL|OC|PL|QT|QR|SP|SL|SR|TC|TL|TS|VZ|YN|ZS)"; // Entidad federativa
        String REGEX_CONSO = "[B-DF-HJ-NP-TV-Z]{3}";
        String REGEX_HOMOCLAVE = "[A-Z0-9]";
        String REGEX_VERIF_DGT = "\\d";

        String REGEX_CURP = "^" +
                            REGEX_FIRST_L +
                            REGEX_DATE +
                            REGEX_GNERE +
                            REGEX_ENTITY +
                            REGEX_CONSO +
                            REGEX_HOMOCLAVE +
                            REGEX_VERIF_DGT +
                            "$";

        if (curp == null || curp.length() != 18) throw new IllegalArgumentException("CURP debe tener 18 caracteres");
        if (!curp.matches(REGEX_CURP)) throw new IllegalArgumentException("CURP formato inválido");
    }
    
    private void validarEmail(String email) {
        if (!email.contains("@")) { 
            throw new IllegalArgumentException("Email inválido");
        }
    }

    private void validarTelefono(String telefono) {
        if (!telefono.matches("\\d{10}")) {
            throw new IllegalArgumentException("Teléfono debe tener 10 dígitos numéricos");
        }
    }

    private int parseDistrito(String distritoStr) {
        int distrito;
        try {
            distrito = Integer.parseInt(distritoStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Distrito debe ser un número entero");
        }
        return distrito;
    }

    private void validarDistrito(int distrito) {
        if (distrito < 1 || distrito > 9) {
            throw new IllegalArgumentException("Distrito fuera de rango (1-9)");
        }
    }

    private void validarEdad(int edad) {
        if (edad < 18) {
            throw new IllegalArgumentException("El ciudadano debe ser mayor de edad");
        }
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

    public static String[] parsearNombres(String nombres) {
        nombres = nombres.trim().replaceAll("\\s+", " "); // Reemplaza múltiples espacios por uno solo
        int espacio = nombres.indexOf(' ');
        if (espacio == -1) {
            return new String[]{nombres, ""};
        }
        String primerNombre = nombres.substring(0, espacio).trim();
        String segundoNombre = nombres.substring(espacio + 1).trim();
        return new String[]{primerNombre, segundoNombre};
    }
}
