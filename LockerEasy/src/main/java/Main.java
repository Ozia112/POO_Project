import javafx.application.Application;
import javafx.stage.Stage;
import view.ServiciosGUI;

public class Main extends Application {
  
    @Override
    public void start(Stage stage) {
        // Instancia DIRECTA porque ServiciosGUI NO tiene constructor con parámetros
        ServiciosGUI gui = new ServiciosGUI();

        // Mostrar GUI
        gui.mostrar(stage);
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}