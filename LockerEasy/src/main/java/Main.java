import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import view.MainGUI;
import view.LoginDialog;
import controller.Config;
import dao.HibernateUtil;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        // Intentar inicializar Hibernate (puede requerir diálogo de login)
        if (!inicializarConexion(stage)) {
            // Si no se pudo conectar, cerrar la aplicación
            Platform.exit();
            return;
        }
        
        // Inicializar configuración desde la base de datos
        Config.inicializar();
        
        // ServiciosGUI ya tiene sus propios controladores internos
        new MainGUI().mostrar(stage);
    }
    
    /**
     * Inicializa la conexión a la base de datos.
     * Si no hay credenciales, muestra el diálogo de login.
     * @return true si la conexión fue exitosa, false si se canceló
     */
    private boolean inicializarConexion(Stage ownerStage) {
        // Verificar si ya hay una SessionFactory inicializada
        if (HibernateUtil.estaInicializado()) {
            return true;
        }
        
        // Intentar inicializar (puede usar variables de entorno o archivo)
        try {
            HibernateUtil.getSessionFactory();
            return true;
        } catch (IllegalStateException e) {
            // No hay credenciales, mostrar diálogo
            System.out.println("[Main] No hay credenciales guardadas. Mostrando diálogo de login...");
        }
        
        // Mostrar diálogo de login
        LoginDialog loginDialog = new LoginDialog();
        return loginDialog.mostrarYEsperar(ownerStage);
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}