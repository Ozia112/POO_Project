
import javafx.application.Application;
import javafx.stage.Stage;
import view.PruebasGUI;
import view.ServiciosGUI;

public class Main extends Application {

    @Override
    public void start(Stage stage) {

        // 1) Crear la lógica (PruebasGUI)
        PruebasGUI logica = new PruebasGUI();

        // 2) Crear la interfaz bonita con la lógica inyectada
        ServiciosGUI ui = new ServiciosGUI(logica);

        // 3) Mostrar la ventana principal
        ui.mostrar(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
