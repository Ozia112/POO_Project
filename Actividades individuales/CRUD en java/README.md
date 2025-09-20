# Registro de ciudadano con MVC (Model–View–Controller)

Este ejercicio implementa validaciones y creación de un ciudadano en Java siguiendo el patrón MVC. Primero se reciben los datos (View/DTO), luego se validan en el Controller y, solo si son válidos, se construye la entidad del dominio (Model). Así se evita crear instancias cuando los datos no cumplen los requisitos.

Nota sobre convenciones: los paquetes se nombran en minúsculas conforme a la convención oficial de Java (Java Package Naming Conventions / JLS), por ejemplo: `dto`, `controller`, `model`.

## Paquetes

### dto

Contiene `FormularioCiudadano`, un DTO (Data Transfer Object) que agrupa los datos de entrada antes de ser validados por el controlador.

**Imports**: ninguno.

**Atributos**:

- `primerNombre`, `segundoNombre`, `apellidoPaterno`, `apellidoMaterno`, `curp`, `email`, `telefono`, `distrito` (int).

**Constructor**:

- `public FormularioCiudadano(String primerNombre, String segundoNombre, String apellidoPaterno, String apellidoMaterno, String curp, String email, String telefono, int distrito)`
  - Convierte `curp` a mayúsculas si no es `null`.

**Métodos**:

- Getters y setters estándar. Nota: `distrito` es un entero esperado en el rango 1–9.

### controller

Contiene `CiudadanoController`, responsable de validar el `FormularioCiudadano` y crear el `Ciudadano` cuando todo es correcto.

**Imports**:

- `java.time.LocalDate`, `java.time.Period`, `dto.FormularioCiudadano`, `model.Ciudadano`.

**Atributos**:

- `private Ciudadano ciudadanoValidado;` referencia a la última instancia creada (útil para inspección/pruebas).

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
