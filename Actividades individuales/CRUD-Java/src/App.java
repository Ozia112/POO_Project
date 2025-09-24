import controller.CiudadanoController;
import controller.FileManager;
import view.CiudadanoGUI;

public class App {
    public static void main(String[] args) {
        FileManager repo = new FileManager("db/ciudadanos.txt");
        CiudadanoController controller = new CiudadanoController(repo);
        CiudadanoGUI.launchUI(controller, args);
    }
}