package controller;
import java.time.LocalDate;
import java.time.Period;

import model.Ciudadano;
import view.CiudadanoFormulario;

public class CiudadanoController {

    private FileManager fileManager;

    public CiudadanoController(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    public Ciudadano procesarCiudadano(CiudadanoFormulario form) throws Exception {

        // Naturalizar entradas
        String nombres = safe(form.getNombres());
        String apellidoPaterno = safe(form.getPrimerApellido());
        String apellidoMaterno = safe(form.getSegundoApellido());
        String curp = safe(form.getCurp()).toUpperCase();
        String email = safe(form.getEmail()).toLowerCase();
        String telefono = safe(form.getTelefono());
        String distritoStr = safe(form.getDistrito());

        // Validar obligatoriedad y formatos
        validarFormatoCurp(curp);
        validarEmail(email);
        validarTelefono(telefono);

        // Parseamientos
        int distrito = parseDistrito(distritoStr);
        String primerNombre = parsearNombres(nombres)[0];
        String segundoNombre = parsearNombres(nombres)[1];
        int edad = calcularEdadDesdeCurp(curp);

        // Validaciones adicionales al parsear
        validarDistrito(distrito);
        validarEdad(edad);

        // Validar unicidad de CURP y edad
        if (fileManager.ciudadanoExiste(curp)) {
            throw new IllegalArgumentException("ERR_CURP_DUP");
        }

        // Crear y guardar ciudadano
        Ciudadano ciudadano = new Ciudadano(primerNombre, 
                                            segundoNombre,
                                            apellidoPaterno,
                                            apellidoMaterno,
                                            curp,
                                            email,
                                            telefono,
                                            distrito,
                                            edad);
        
        fileManager.guardarCiudadano(curp);

        return ciudadano;
    }

    private String safe(String str) { return str == null ? "" : str.trim(); }

    private void validarFormatoCurp(String curp) {

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

        if (curp == null || curp.length() != 18) throw new IllegalArgumentException("ERR_CURP_LENGTH");
        if (curp.matches(REGEX_CURP)) throw new IllegalArgumentException("ERR_CURP_FORMAT");
    }
    
    private void validarEmail(String email) {
        if (!email.contains("@")) { 
            throw new IllegalArgumentException("ERR_MAIL_FORMAT");
        }
    }

    private void validarTelefono(String telefono) {
        if (!telefono.matches("\\d{10}")) {
            throw new IllegalArgumentException("ERR_TEL_FORMAT");
        }
    }

    private int parseDistrito(String distritoStr) {
        int distrito;
        try {
            distrito = Integer.parseInt(distritoStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("ERR_DIST_FORMAT");
        }
        return distrito;
    }

    private void validarDistrito(int distrito) {
        if (distrito < 1 || distrito > 9) {
            throw new IllegalArgumentException("ERR_DIST_OUT_OF_RANGE");
        }
    }

    private void validarEdad(int edad) {
        if (edad < 18) {
            throw new IllegalArgumentException("ERR_EDAD");
        }
    }

    /**
     * Calcula la edad a partir de la CURP.
     * Regla oficial (DOF): El carácter 17 (índice 16) de la CURP ayuda a distinguir 
     * siglo de nacimiento. Si es dígito => nacido 1900-1999; si es letra siglo XXI en adelante.
     * 'A' = siglo XXI
     * '0' = siglo XX
     * @see https://www.dof.gob.mx/nota_detalle_popup.php?codigo=5526717
    */
    private int calcularEdadDesdeCurp(String curp) {
        if (curp == null || curp.length() != 18) return 0;
        try {
            String yyStr = curp.substring(4, 6);
            String mmStr = curp.substring(6, 8);
            String ddStr = curp.substring(8, 10);

            int yy = Integer.parseInt(yyStr);
            int month = Integer.parseInt(mmStr);
            int day = Integer.parseInt(ddStr);

            char centuryFlag = curp.charAt(16);
            int century = centuryFlag == '0' ? 1900 : (centuryFlag == 'A' ? 2000 : 0);
            int year = century + yy;

            LocalDate birth = LocalDate.of(year, month, day);
            LocalDate today = LocalDate.now();
            if (birth.isAfter(today)) return 0;

            return Period.between(birth, today).getYears();
        } catch (RuntimeException e) {
            return 0;
        }
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
