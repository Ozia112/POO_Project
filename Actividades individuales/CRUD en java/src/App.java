import controller.CiudadanoController;
import controller.FileManager;
import view.FormularioVentana;

public class App {
    public static void main(String[] args) {
        FileManager repo = new FileManager("db/ciudadanos.txt");
        CiudadanoController controller = new CiudadanoController(repo);
        FormularioVentana.launchUI(controller, args);
    }
}