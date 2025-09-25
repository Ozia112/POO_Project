# Registro de ciudadano con MVC (Model–View–Controller) e interfaz

En esta iteración se implementa el sistema **CRUD** (Create, Read, Update, Delete) completo para el registro de ciudadanos mexicanos utilizando **JavaFX** para la interfaz gráfica y siguiendo el patrón de diseño **MVC** (Model-View-Controller). Incluye validaciones de **CURP** con expresiones regulares(`Regex`), persistencia en archivos y una interfaz de usuario simple.

## Lógica del sistema

- Primero se reciben los datos a través de la interfaz para ser transferidos a un formulario
- Se implementa un algoritmo simple de lectura para la base de datos para confirmar que el ciudadano sea nuevo
- Luego cada parámetro es validado por el paquete controller que todos los campos obligatorios estén llenos y cumplan con el formato correspondiente.
- El paquete controller a su vez crea los campos de primer nombre, segundo nombre y edad conforme va mapeando cada dato del ciudadano.
- Si el ciudadano pasa todas las validaciones entonces se guarda su CURP en la base de datos y se da aviso a través de la interfaz.

## Características principales

- Interfaz gráfica implementada con librerías JavaFX.
- Persistencia en archivos de texto.
- Patrón MVC estructurado correctamente.
- Validación de CURP, edad, teléfono, email y distrito.

## Estructura del proyecto

### View

- **CiudadanoFormulario**: DTO (Data Transfer Object) para transferencia de datos.
- **CiudadanoGUI**: Interfaz gráfica principal con JavaFX.

### Controller

- **CiudadanoController**: Lógica de validación y procesamiento.
- **FileManager**: Gestión de persistencia en archivos.

### Model

- **Ciudadano**: Entidad de dominio

## db

- **ciudadanos.txt**: Base de datos simple en archivo de texto.

## Validaciones implementadas

### CURP

- Formato de 18 caracteres según estructura oficial de [**Reglamento normativo para la asignación del CURP para el ciudadano**](https://www.dof.gob.mx/nota_detalle_popup.php?codigo=5526717).
- Validación de fecha de nacimiento.
- Verificación de mayoría de edad (≥18 años).
- Códigos de entidad federativa válidos.

### Otros campos

- Nombres y apellidos obligatorios.
- Teléfono: exactamente 10 dígitos.
- Email: formato básico con @.
- Distrito: rango 1-9.

## Requisitos

- Java 11 o superior
- JavaFX SDK 25 (incluido en [`lib/`](/CRUD-Java/lib/))

## Ejecución

1. Compilar el proyecto.
2. Ejecutar [`App.java`](/CRUD-Java/src/App.java) como punto de entrada.
3. La interfaz gráfica se abrirá automáticamente.

## Casos de prueba

Ver [`Casos de uso.md`](/CRUD-Java/Casos%20de%20uso.md) para ejemplos de CURPs válidas e inválidas para testing

---

## Explicación técnica de las clases

![Diagrama de clases](/CRUD-Java/assets/class_diagram.svg)

### Paquete view

#### Clase `CiudadanoFormulario`

Clase de tipo DTO (Data Transfer Object). Sirve para almacenar los datos de entrada de la interfaz generada por `CiudadanoGUI`

**Imports**: ninguno.

**Atributos**:

- `private String nombres;`
- `private String primerNombre;`
- `private String segundoNombre;`
- `private String apellidoPaterno;`
- `private String apellidoMaterno;`
- `private String curp;`
- `private String email;`
- `private String telefono;`
- `private int distrito;`

**Constructor**:

``` java
public CiudadanoFormulario(String nombres,
                          String apellidoPaterno,
                          String apellidoMaterno,
                          String curp,
                          String email,
                          String telefono,
                          int distrito) {
        this.nombres = nombres;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.curp = (curp == null) ? null : curp.toUpperCase();
        this.email = email;
        this.telefono = telefono;
        this.distrito = distrito;
    }
```

- Convierte `curp` a mayúsculas si no es `null`.

**Métodos**:

- Getters y setters estándar. Nota: `distrito` es un entero esperado en el rango 1–9.

#### Clase `CiudadanoGUI`

**Imports**:  

- `import javafx.application.Application;`
- `import javafx.beans.value.ChangeListener;`
- `import javafx.geometry.Insets;`
- `import javafx.scene.Scene;`
- `import javafx.scene.control.*;`
- `import javafx.scene.layout.*;`
- `import javafx.stage.Stage;`
- `import controller.CiudadanoController;`
- `import model.Ciudadano;`

**Atributos**:

- `private static CiudadanoController controller` Atributo privado para crear una instancia controladora para poder utilizar el método `procesarCiudadano` en el botón enviar del formulario.

**Métodos**:

- Método `public static launchUI(CiudadanoController ctrl, String[] args)`  
  Establece el controlador estático que usará la ventana y delega en `Application.launch(args)` para iniciar el toolkit JavaFX. Debe llamarse antes de que la ventana se muestre para evitar `NullPointerException` al usar `controller`.
  - `ctrl`: instancia de `CiudadanoController` que procesará y validará los datos.
  - `args`: argumentos de la línea de comandos (se pasan al sistema JavaFX).

- Método `public void start(Stage stage)` (`@Override`)  
  Implementación obligatoria de `Application.start(Stage)` (JavaFX la invoca en el JavaFX Application Thread). Construye la interfaz:
  - Crea un `GridPane` y lo envuelve en un `VBox`.
  - Genera los campos de texto obligatorios (nombre(s), apellidos, CURP, email, teléfono, distrito).
  - Añade una nota visual de campos requeridos.
  - Configura el botón Enviar y su handler: arma la llamada a `controller.procesarCiudadano(...)`, muestra alertas de éxito o error y limpia campos.
  - Aplica activación/desactivación dinámica del botón mediante `dynamicButton`.
  - Ajusta escena, hojas de estilo ([`estilos.css`](/CRUD-Java/src/view/assets/estilos.css)), icono, título y tamaños mínimos, y finalmente muestra la ventana.

- Método `private TextField crearCampoFormulario(String labelText, String placeholder, int rowIndex, GridPane grid) : TextField`  
  Crea y coloca en el `GridPane` una etiqueta (más asterisco rojo para obligatorio) y un `TextField` con placeholder.
  - `labelText`: texto base de la etiqueta (si contiene `*:` se limpia visualmente).
  - `placeholder`: texto guía dentro del campo.
  - `rowIndex`: fila donde se añaden etiqueta y campo.
  - `grid`: contenedor destino.
  Retorna el `TextField` creado.

- Método `private void dynamicButton(Button boton, TextField... campos)`  
  Añade un `ChangeListener` a cada `TextField` para habilitar el botón solo cuando todos los campos tienen texto no vacío (trim).
  - `boton`: botón a controlar (se inicializa desactivado).
  - `campos`: lista variable de campos a verificar.

- Método `showInfo(String titulo, String message)`  
  Muestra una alerta modal de información (`AlertType.INFORMATION`) con botón OK.
  - `titulo`: título de la ventana de alerta.
  - `message`: contenido informativo.

- Método `showAlert(String titulo, String message)`  
  Muestra una alerta modal de error (`AlertType.ERROR`) con botón OK.
  - `titulo`: título de la alerta.
  - `message`: detalle del error.

- Método `clearFields(TextField... fields)`  
  Limpia (vacía) todos los `TextField` recibidos.
  - `fields`: campos a limpiar (varargs).

---

### Paquete controller

#### Clase `CiudadanoController`

Clase responsable de validar el `CiudadanoFormulario`(DTO), calcular la edad del ciudadano, parsear los nombres y dividirlos en sus respectivos campos, crear el `Ciudadano` cuando todo es correcto y orquestar el guardado de su CURP.

**Imports**:

- `import java.time.LocalDate;`
- `import java.time.Period;`
- `import model.Ciudadano;`
- `import view.CiudadanoFormulario;`

**Atributos**:

- `private FileManager fileManager;`

**Métodos**:

- `public void procesarCiudadano(CiudadanoFormulario form)` realiza:  
  - Throws Exception para validar todos los campos.
  - Normaliza todos los campos vacíos con el método `safe`.
  - Valida formato del CURP y luego comprueba que no esté repetido
  - Valida formatos con REGEX en sus respectivos métodos para el email y teléfono
  - Transforma en primitivo(int) el distrito
  - Divide los nombres en primero y segundo
  - Transforma en primitivo(int) la edad calculada desde el CURP con su respectivo método
  - Valida distrito y edad.
  - Si todo sale bien crea ciudadano con los campos validados
  - Agrega el CURP al dataBase.

- `private String safe(String str)` Si detecta que el campo es `null` retorna un string `""` de lo contrario retorna el mismo string.

- `private void validarFormatoCurp(String curp)` Utiliza regex para poder detectar el formato correcto con base a la documentación de la [Normativa para asignación de CURP](https://www.dof.gob.mx/nota_detalle_popup.php?codigo=5526717) normativa más actual con vigencia legal en México.

- `private void validarEmail(String email)` utiliza expresión regular para detectar si contiene un arroba el email.

- `private void validarTelefono(String telefono)` utiliza expresión regular para determinar que se hayan usado 10 dígitos en el campo de teléfono.

- `private int parseDistrito(String distritoStr)` Transforma el string del distrito del DTO a int y lo retorna.

- `private void validarDistrito(int distrito)` Valida que el distrito esté en el rango del 1 al 9

- `private void validarEdad(int edad)` determina si el usuario es mayor de edad >= 18

- `private int calcularEdadDesdeCurp(String curp)` retorna un entero calculando la edad del usuario, con los dígitos establecidos en la [normativa](https://www.dof.gob.mx/nota_detalle_popup.php?codigo=5526717).

- `public static String[] parsearNombres(String nombres)` Divide los nombres del usuario en dos partes partiendo desde el primer espacio detectado.

#### Clase `FileManager`

**Atributos**:

- `private final Path filePath`

**Métodos**:

- `public FileManager(String filename)` es un constructor parametrizado que se invoca al crear una instancia de la clase FileManager utiliza el parámetro de string para setear el filepath para la DB

- `private void crearDirectorio()` revisa si existe el folder del archivo a crear(parent) si no existe lo crea.

- `public boolean ciudadanoExiste(String curp)` retorna un true si el ciudadano ya se encuentra en la base de datos y false si es primera vez que existe.

- `public void guardarCiudadano(String curp)` se escribe el curp pasado como parámetro del ciudadano ya validado con la función `Files.writeString`

### Paquete model

Contiene `Ciudadano`, la entidad de dominio con los datos del ciudadano.

**Atributos**:

- `primerNombre`, `segundoNombre`, `primerApellido`, `segundoApellido`, `curp`, `email`, `telefono`, `distrito` (int), `edad` (int).

**Métodos**:

- Getters y constructor estándar.

## Clase `App`

Clase que contiene la función principal main que funge como punto de entrada para lanzar la GUI o las pruebas unitarias con funciones secuenciales.

**Imports**:

- `import controller.CiudadanoController;`
- `import controller.FileManager;`
- `import view.CiudadanoGUI;`
- `import view.CiudadanoFormulario;`

**Métodos**:

- **`public static void main(String[] args)`**  
  Punto de entrada principal de la aplicación. Permite al usuario elegir entre:
  - Opción 1: Lanzar la interfaz gráfica
  - Opción 2: Ejecutar casos de prueba predefinidos
  - Cualquier otra opción: Salir del programa

- **`private static void lanzarInterfaz(String[] args)`**  
  Crea una instancia de `CiudadanoController` con `FileManager` configurado para usar "BD/ciudadanos.txt" como base de datos, y lanza la interfaz gráfica mediante `CiudadanoGUI.launchUI()`.

- **`private static void lanzarCasos()`**  
  Ejecuta una serie de casos de prueba predefinidos para validar el funcionamiento del sistema:
  - Casos válidos: CURPs correctas que deberían ser aceptadas
  - Casos inválidos: CURPs con diferentes tipos de errores (formato, fecha, entidad federativa, etc.)
  - Muestra el resultado de cada prueba en consola

- **`private static void crear(String nombres, String apellidoPaterno, String apellidoMaterno, String curp, String email, String telefono, String distrito)`**  
  Método auxiliar para los casos de prueba. Crea un `CiudadanoFormulario` con los datos proporcionados y lo procesa mediante `CiudadanoController.procesarCiudadano()`. Captura y muestra cualquier excepción que ocurra durante el procesamiento.
  