import controller.CiudadanoController;
import controller.FileManager;
import view.CiudadanoGUI;

public class App {
    public static void main(String[] args) {
        FileManager repo = new FileManager("CRUD-Java/src/BD/ciudadanos.txt");
        // Funcion para interfaz grafica descomentar para usarla
        System.out.println("\n");
        //lanzarInterfaz(args, repo);

        // Pruebas de creación de ciudadanos
        lanzarCasos(repo);
        

    }

    public static void lanzarInterfaz(String[] args, FileManager repo) {
        CiudadanoController controller = new CiudadanoController(repo);
        CiudadanoGUI.launchUI(controller, args);
    }

    public static void lanzarCasos(FileManager repo) {
        CiudadanoController flujoPrincipal = new CiudadanoController(repo);
        CiudadanoController flujoAlt1 = new CiudadanoController(repo);
        CiudadanoController flujoAlt2 = new CiudadanoController(repo);
        CiudadanoController excepcion1 = new CiudadanoController(repo);
        CiudadanoController excepcion2 = new CiudadanoController(repo);
        CiudadanoController excepcion3 = new CiudadanoController(repo);
        CiudadanoController excepcion4 = new CiudadanoController(repo);
        CiudadanoController excepcion5 = new CiudadanoController(repo);
        CiudadanoController excepcion6 = new CiudadanoController(repo);
        CiudadanoController excepcion7 = new CiudadanoController(repo);
        
        // Flujo principal y alternativos
        crear(flujoPrincipal, "Ana María", 
                          "López", 
                          "García", 
                          "LOGA800101HDFRRN09",
                          "anamraia@example.com",
                          "5512345678",
                          "5");
        crear(flujoAlt1, "Juan", 
                         "Pérez",
                         "Hernández",
                         "PEHJ990202MDFRRN03",
                         "juanperez@example.com",
                         "5598765432",
                         "1");
        crear(flujoAlt2, "María Fernanda Estefania", 
                         "Sánchez",
                         "Ramírez",
                         "SARA030303MDFRRN08",
                         "maria@example.com",
                         "5587654321",
                         "3");
        // Excepciones
        crear(excepcion1, "Ana María", 
                          "López", 
                          "García", 
                          "LOGA800101HDFRRN09",
                          "anamraia@example.com",
                          "5512345678",
                          "5"); // CURP duplicada solo funciona si se ejecuta el flujo principal antes
        crear(excepcion2, "Luis", 
                          "Martínez", 
                          "Vega", 
                          "M1VE850505HDFRRN0X",
                          "luismarti@example.com",
                          "5512345678",
                          "2"); // CURP formato inválido
        crear(excepcion3, "Carla", 
                          "Gómez",
                          "Luna",
                          "GOLC150303MDFRRNA7",
                          "carla@example.com",
                          "5512345678",
                          "4"); // Edad menor a 18
        crear(excepcion4, "Pedro", 
                          "Díaz",
                          "Cruz",
                          "DICP760707HDFRRN02",
                          "pedroe@xample.com",
                          "55123458",
                          "2"); // Teléfono inválido
        crear(excepcion5, "Lucía", 
                          "Torres",
                          "Flores",
                          "TOFL920202MDFRRN01",
                          "luciaexample.com",
                          "5512345678",
                          "3"); // Email inválido
        crear(excepcion6, "Miguel", 
                          "Ruiz",
                          "Soto",
                          "RUSM880808HDFRRN01",
                          "miguel@example.com",
                          "5512345678",
                          "14"); // Distrito fuera de rango
        crear(excepcion7, "Jose Luis", 
                          "Martínez",
                          "Alvarez",
                          "MALJ900101HDFRRN09",
                          "",
                          "",
                          "1"); // Campos vacíos
    }

    private static void crear(CiudadanoController ctrl, 
                              String nombres, 
                              String apellidoPaterno, 
                              String apellidoMaterno, 
                              String curp, 
                              String email, 
                              String telefono, 
                              String distrito) {
        try {
            ctrl.procesarCiudadano(new view.CiudadanoFormulario(nombres, apellidoPaterno, apellidoMaterno, curp, email, telefono, distrito));
            System.out.println("Ciudadano creado exitosamente: " + curp);
        } catch (Exception e) {
            System.out.println("Error al crear ciudadano: " + e.getMessage() + " para " + nombres);
        }
    }
}