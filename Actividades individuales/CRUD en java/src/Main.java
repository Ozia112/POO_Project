package srcj;


public class Main {
    public static void main(String[] args) {
        ciudadanos ciudadanos1 = new ciudadanos();
        System.out.println("\nCreando ciudadano 1...");
        ciudadanos1.setNombres("Isaac", "Alejandro");
        ciudadanos1.setApellidos("Ortiz", "Zaldivar");
        ciudadanos1.setCURP("OIZI001201HYNRLSA5");
        System.out.println("Nombres: " + ciudadanos1.getNombresString());
        System.out.println("Apellidos: " + ciudadanos1.getApellidosString());
        System.out.println("Edad: " + ciudadanos1.getEdad());
    }
}