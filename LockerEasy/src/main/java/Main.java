
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        view.PruebasGUI gui = new view.PruebasGUI();
        gui.mostrar(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
