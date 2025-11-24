
import javafx.application.Application;
import javafx.stage.Stage;
import view.PruebasGUI;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        PruebasGUI gui = new PruebasGUI();
        gui.mostrar(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
