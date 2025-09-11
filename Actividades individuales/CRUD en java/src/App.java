import dto.FormularioCiudadano;
import controller.CiudadanoController;
import model.Ciudadano;

public class App {
    public static void main(String[] args) {
        System.out.println("");
        CiudadanoController controller = new CiudadanoController();

        // Caso válido
        FormularioCiudadano fValido = new FormularioCiudadano(
                "Isaac",
                "Alejandro",
                "Ortiz",
                "Zaldivar",
                "OIZA001201HYNRLSA5",
                "correo@example.com",
                "9994588510",
                9);
        crear(controller, fValido, "Caso válido:\n");

        // CURP inválida
        FormularioCiudadano fCurpInvalida = new FormularioCiudadano(
                "Isaac",
                "Alejandro",
                "Ortiz",
                "Zaldivar",
                "XXXX001201GYNRLSA5",
                "correo@example.com",
                "9994588510",
                9);

        crear(controller, fCurpInvalida, "Caso CURP inválida:\n");

        // Menor de edad (año 2022)
        FormularioCiudadano fMenor = new FormularioCiudadano(
                "Ana",
                "Maria", 
                "Lopez",
                "Perez",
                "LOPA220101MDFRRNA3",
                "ana@example.com",
                "9991234567",
                5);
        crear(controller, fMenor, "Caso Menor de edad:\n");

        // Teléfono incorrecto
        FormularioCiudadano fTel = new FormularioCiudadano(
                "Luis",
                null,
                "Mendez",
                "Ruiz",
                "MENL001201HDFRZL09",
                "luis@example.com",
                "12",
                3);
        crear(controller, fTel, " Caso Telefono corto:\n");
    }

    private static void crear(CiudadanoController controller, FormularioCiudadano f, String label) {
        try {
            Ciudadano c = controller.crearCiudadano(f);
            System.out.println(label + " -> OK: " + c.getCurp() + " edad=" + c.getEdad());
        } catch (Exception e) {
            System.out.println(label + " -> ERROR: " + e.getMessage());
        }
    }
}