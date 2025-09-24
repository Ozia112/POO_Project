# Registro de ciudadano con MVC (Model–View–Controller) e interfaz

En esta iteracion se implementa el sistema **CRUD** (Create, Read, Update, Delete) completo para el registro de ciudadanos mexicanos utilizando **JavaFX** para la interfaz gráfica y siguiendo el patrón de diseño **MVC** (Model-View-Controller). Incluye validaciones de **CURP** con expresiones regulares(`Regex`), persistencia en archivos y una interfaz de usuario simple.

## Logica del sistema

- Primero se reciben los datos a traves de la interfaz para ser transferidos a un formulario
- se implementa un algoritmo simple de lectura para la base de datso para confirmar que el ciudadano sea nuevo
- Luego cada paramentro es validado por el paquete controller que todos los campos obligatorios esten llenos y cumplan con el formato correspondiente.
- El paquete controller a su vez crea los campos de primer nombre, segundo nombre  y edad conforme va mapeando cada dato del ciudadano.
- Si el ciudadano pasa todas las validaciones entonces se guarda su CURP en la base de datos y se da aviso a traves de la interfaz.

## Caracteristicas principales

- Interfaz grafica implementada con librerias JavaFX.
- Peristencia en archivos de texto.
- Patron MVC estructurado correctamente.
- Validacion de CURP, edad, telefono, email y distrito.

## Estructura del proyecto

### View

- **FormularioCiudadano**: DTO (Data transfer Object) para transferencia de datos.
- **FormularioVentana**: Interfaz gráfica principal con JavaFX.

### Controller

- **CiudadanoController**: Lógica de validación y procesamiento.
- **FileManager**: Gestión de persistencia en archivos.

### Model

- **Ciudadano**: Entidad de dominio

## db

- **ciudadanos.txt**: Base de datos simple en archivo de texto.

## Validaciones implementadas

### CURP

- Formato de 18 caracteres según estructura oficial de [**Reglamento normativo para la asignacion del CURP para el ciudadano**](https://www.dof.gob.mx/nota_detalle_popup.php?codigo=5526717).
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

## Ejecucion

1. compilar el proyecto.
2. Ejecutar [`App.java`](/CRUD-Java/src/App.java) como punto de entrada.
3. La interfaz gráfica se abrirá automáticamente.

## Casos de prueba

Ver [`Casos de uso.md`](/CRUD-Java/Casos%20de%20uso.md) para ejemplos de CURPs validas e invalidas para testings

---

## Explicacion tecnica de las clases

![Diagrama de clases](/CRUD-Java/assets/class_diagram.svg)

### Paquete view

#### Clase `FormularioCiudadano`

clase de tipo DTO (Data Transfer Object). Sirve para almacenar los datos de entrada de la interfaz generada por `FormularioVentana`

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
public FormularioCiudadano(String nombres,
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

#### Clase `FormularioVentana`

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

- `private static CiudadanoController controller` Atributo privado para crear una instancia controladora para poder utilizar el metodo `procesarCiudadano` en el boton enviar del formulario.

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

#### Clase ``

Clase responsable de validar el `FormularioCiudadano` calcular la edad del ciudadano, parsear los nombres y dividirlos en sus respectivos campos, crear el `Ciudadano` cuando todo es correcto y orquestar el guardado de su curp.

**Imports**:

- `import java.time.LocalDate;`
- `import java.time.Period;`
- `import model.Ciudadano;`

**Atributos**:

- `private FileManager`
- 

**Métodos**:

- `public void validar(FormularioCiudadano formulario)`: valida todos los campos principales.
  - Rechaza `formulario == null` y primer nombre vacío.
  - CURP: con base en la estructura oficial ([gob.mx/curp](https://www.gob.mx/curp)), se valida con una expresión regular compuesta:
    - Primeras 4 posiciones: `[A-Z][AEIOU][A-Z]{2}` (iniciales y vocal interna). No incluye Ñ.
    - Fecha YYMMDD: `\d{2}(0[1-9]|1[0-2])(0[1-9]|[12][0-9]|3[01])`.
    - Sexo: `[HMX]` (H=Hombre, M=Mujer, X=No binario).
    - Entidad federativa: catálogo oficial (AS, BC, …, ZS).
    - Consonantes internas: `[B-DF-HJ-NP-TV-Z]{3}`.
    - Homoclave: `[A-Z0-9]`.
    - Dígito verificador: `\d`.
  - Rechaza CURP `null`, distinta de 18 caracteres o que no cumpla el patrón.
  - Calcula edad desde CURP y exige mayoría de edad (>= 18). Si la fecha en la CURP es inválida, se considera edad 0 y se rechaza.
  - Requiere apellidos no vacíos.
  - Teléfono: exactamente 10 dígitos (`\d{10}`).
  - Email: debe contener `@` (validación básica).
  - Distrito: entero en el rango 1–9.
- `public Ciudadano crearCiudadano(FormularioCiudadano formulario)`: llama a `validar`, calcula la edad y construye la entidad mediante `Ciudadano.fromFormulario`.
- `public Ciudadano getCiudadanoValidado()`: expone la última instancia creada.
- `private boolean esVacio(String str)`: utilidad para cadenas vacías/nulas.
- `private int calcularEdadDesdeCurp(String curp)`: extrae YY, MM, DD de la CURP, deduce siglo, crea `LocalDate` y calcula años con `Period.between`. Si la fecha es inválida, retorna 0.

Limitación conocida: la regex de CURP valida formato, no la consistencia calendario (p.ej., 31/02). Esa consistencia se aborda parcialmente al intentar construir la fecha real.

### model

Contiene `Ciudadano`, la entidad de dominio con los datos del ciudadano.

**Atributos**:

- `primerNombre`, `segundoNombre`, `apellidoPaterno`, `apellidoMaterno`, `curp`, `email`, `telefono`, `distrito` (int), `edad` (int).

**Métodos**:

- Getters y setters estándar.
- `public static Ciudadano fromFormulario(FormularioCiudadano formulario, int edad)`: fábrica estática que centraliza la copia de datos del DTO a la entidad e incorpora la edad calculada.

## Clase App

Punto de entrada con `main`, donde se instancian formularios de prueba (válidos y con errores) y se invoca al `CiudadanoController`. Incluye el método auxiliar `crear` que imprime el resultado o el mensaje de error capturado.
