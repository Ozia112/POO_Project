import javafx.application.Application;
import javafx.stage.Stage;
import view.ServiciosGUI;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        // ServiciosGUI ya tiene sus propios controladores internos
        new ServiciosGUI().mostrar(stage);
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}