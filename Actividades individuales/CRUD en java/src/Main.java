import java.util.*;

public class Main {

    /* ---- Fecha “fija”  ---- */
    static final int CURRENT_YEAR = 2025;
    static final int CURRENT_MONTH = 8;
    static final int CURRENT_DAY = 18;

    /* ---- Modelos (renombrados) ---- */
    static class Domicilio {
        String calle = "";
        String colonia = "";
        String numero = "";
        int codigo_postal;
    }

    static class NombresAp {
        String paterno = "";
        String materno = "";
    }

    static class FechaNac {
        int year;
        int month;
        int day;
    }

    static class Registro {
        String nombre_completo = "";
        String nombre1 = "";
        String nombre2 = "";
        NombresAp apellidos = new NombresAp();
        FechaNac nacimiento = new FechaNac();
        int edad;
        Domicilio domicilio = new Domicilio();
        String estado_nacimiento = "";
        char sexo;              // 'M' o 'F'
        String clave_curp = "";
        String correo = "";
        String celular = "";
        boolean mayor_edad;
        int dependientes;
    }

    /* ===========================================================
       Helpers cortos
       =========================================================== */

    static void leerTextoLineaMayus(Scanner sc, String[] destino) {
        String s = sc.nextLine();
        s = s.toUpperCase(Locale.ROOT);
        destino[0] = s;
    }

    static boolean mayorEdad(int edad) {
        return edad >= 18;
    }

    static char vocalInterna(String str) {
        for (int i = 1; i < str.length(); i++) {
            char c = Character.toUpperCase(str.charAt(i));
            if (c=='A'||c=='E'||c=='I'||c=='O'||c=='U') return c;
        }
        return 'X';
    }

    static char consInterna(String str) {
        for (int i = 1; i < str.length(); i++) {
            char c = Character.toUpperCase(str.charAt(i));
            if (Character.isLetter(c) && !(c=='A'||c=='E'||c=='I'||c=='O'||c=='U')) return c;
        }
        return 'X';
    }

    /* ===========================================================
       Lógica media
       =========================================================== */

    static void separarNombres(String nombre_completo, String[] nombre1, String[] nombre2) {
        int i = nombre_completo.indexOf(' ');
        if (i < 0) {
            nombre1[0] = nombre_completo;
            nombre2[0] = "";
        } else {
            nombre1[0] = nombre_completo.substring(0, i);
            nombre2[0] = nombre_completo.substring(i + 1);
        }
    }

    static int edadActual(Registro r) {
        int edad = CURRENT_YEAR - r.nacimiento.year;
        if (r.nacimiento.month > CURRENT_MONTH ||
           (r.nacimiento.month == CURRENT_MONTH && r.nacimiento.day > CURRENT_DAY)) {
            edad--;
        }
        return edad;
    }

    static String codigo_estado(String estado) {
        if (estado == null) return "NE";
        String nombre = estado.toUpperCase(Locale.ROOT);

        if (nombre.equals("AGUASCALIENTES")) return "AS";
        if (nombre.equals("BAJA CALIFORNIA")) return "BC";
        if (nombre.equals("BAJA CALIFORNIA SUR")) return "BS";
        if (nombre.equals("CAMPECHE")) return "CC";
        if (nombre.equals("COAHUILA")) return "CL";
        if (nombre.equals("COLIMA")) return "CM";
        if (nombre.equals("CHIAPAS")) return "CS";
        if (nombre.equals("CHIHUAHUA")) return "CH";
        if (nombre.equals("CIUDAD DE MEXICO") || nombre.equals("CDMX")) return "DF";
        if (nombre.equals("DURANGO")) return "DG";
        if (nombre.equals("GUANAJUATO")) return "GT";
        if (nombre.equals("GUERRERO")) return "GR";
        if (nombre.equals("HIDALGO")) return "HG";
        if (nombre.equals("JALISCO")) return "JC";
        if (nombre.equals("MEXICO") || nombre.equals("ESTADO DE MEXICO")) return "MC";
        if (nombre.equals("MICHOACAN")) return "MN";
        if (nombre.equals("MORELOS")) return "MS";
        if (nombre.equals("NAYARIT")) return "NT";
        if (nombre.equals("NUEVO LEON")) return "NL";
        if (nombre.equals("OAXACA")) return "OC";
        if (nombre.equals("PUEBLA")) return "PL";
        if (nombre.equals("QUERETARO")) return "QT";
        if (nombre.equals("QUINTANA ROO")) return "QR";
        if (nombre.equals("SAN LUIS POTOSI")) return "SP";
        if (nombre.equals("SINALOA")) return "SL";
        if (nombre.equals("SONORA")) return "SR";
        if (nombre.equals("TABASCO")) return "TC";
        if (nombre.equals("TAMAULIPAS")) return "TS";
        if (nombre.equals("TLAXCALA")) return "TL";
        if (nombre.equals("VERACRUZ")) return "VZ";
        if (nombre.equals("YUCATAN")) return "YN";
        if (nombre.equals("ZACATECAS")) return "ZS";
        if (nombre.equals("NACIDO EN EL EXTRANJERO") || nombre.equals("EXTRANJERO")) return "NE";
        return "NE";
    }

    /* ===========================================================
       Funciones 
       =========================================================== */

    static void crearCurp(Registro r) {
        String pat = r.apellidos.paterno != null ? r.apellidos.paterno : "";
        String mat = r.apellidos.materno != null ? r.apellidos.materno : "";
        String nom = r.nombre_completo != null ? r.nombre_completo : "";

        StringBuilder curp = new StringBuilder(16);

        curp.append(pat.isEmpty() ? 'X' : Character.toUpperCase(pat.charAt(0)));
        curp.append(vocalInterna(pat));
        curp.append(mat.isEmpty() ? 'X' : Character.toUpperCase(mat.charAt(0)));
        curp.append(nom.isEmpty() ? 'X' : Character.toUpperCase(nom.charAt(0)));

        curp.append(String.format(Locale.ROOT, "%02d%02d%02d",
                r.nacimiento.year % 100, r.nacimiento.month, r.nacimiento.day));

        curp.append(Character.toUpperCase(r.sexo));

        String ent = codigo_estado(r.estado_nacimiento);
        curp.append(ent.charAt(0)).append(ent.charAt(1));

        curp.append(consInterna(pat));
        curp.append(consInterna(mat));
        curp.append(consInterna(nom));

        r.clave_curp = curp.toString(); // igual que tu C (16 chars)
        System.out.println("CURP generado: " + r.clave_curp);
    }

    static void nuevoRegistro(List<Registro> lista, Scanner sc) {
        Registro r = new Registro();

        System.out.print("Año de nacimiento: ");
        r.nacimiento.year = leerEntero(sc);

        System.out.print("Mes de nacimiento: ");
        r.nacimiento.month = leerEntero(sc);

        System.out.print("Día de nacimiento: ");
        r.nacimiento.day = leerEntero(sc);

        r.edad = edadActual(r);
        r.mayor_edad = mayorEdad(r.edad);

        System.out.print("Nombre(s): ");
        sc.nextLine(); // limpiar salto previo de nextInt
        String[] tmp = new String[1];
        leerTextoLineaMayus(sc, tmp);
        r.nombre_completo = tmp[0];
        String[] n1 = new String[1];
        String[] n2 = new String[1];
        separarNombres(r.nombre_completo, n1, n2);
        r.nombre1 = n1[0];
        r.nombre2 = n2[0];

        System.out.print("Apellido paterno: ");
        leerTextoLineaMayus(sc, tmp);
        r.apellidos.paterno = tmp[0];

        System.out.print("Apellido materno: ");
        leerTextoLineaMayus(sc, tmp);
        r.apellidos.materno = tmp[0];

        System.out.print("Estado de nacimiento: ");
        r.estado_nacimiento = sc.next().toUpperCase(Locale.ROOT);

        System.out.print("Sexo (M/F): ");
        r.sexo = sc.next().toUpperCase(Locale.ROOT).charAt(0);

        System.out.print("Correo electrónico: ");
        r.correo = sc.next();

        System.out.print("Teléfono: ");
        r.celular = sc.next();

        System.out.print("Calle: ");
        r.domicilio.calle = sc.next().toUpperCase(Locale.ROOT);

        System.out.print("Colonia: ");
        r.domicilio.colonia = sc.next().toUpperCase(Locale.ROOT);

        System.out.print("Número: ");
        r.domicilio.numero = sc.next().toUpperCase(Locale.ROOT);

        System.out.print("Código postal: ");
        r.domicilio.codigo_postal = leerEntero(sc);

        r.dependientes = 0;

        crearCurp(r);
        lista.add(r);
    }

    /* ===========================================================
       main
       =========================================================== */

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        List<Registro> personas = new ArrayList<>();

        while (true) {
            System.out.print("Agregar registro (1) o salir (0): ");
            int op = leerEntero(sc);
            if (op == 0) break;
            if (op == 1) {
                nuevoRegistro(personas, sc);
            }
        }

        sc.close();
    }


    static int leerEntero(Scanner sc) {
        while (true) {
            if (sc.hasNextInt()) {
                int v = sc.nextInt();
                return v;
            } else {
                sc.next(); // descartar token inválido
                System.out.print("Entrada inválida. Intenta de nuevo: ");
            }
        }
    }
}
