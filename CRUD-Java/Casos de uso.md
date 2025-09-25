# Caso de Uso: Registrar Ciudadano

## 1. Identificación

- ID: CU-01
- Versión: 1.0
- Autor: Isaac Ortiz
- Fecha: 21 de Septiembre

## 2. Propósito

Permitir registrar un ciudadano en el sistema garantizando integridad y validaciones de CURP, edad y datos de contacto.

## 3. Descripción breve

El operador ingresa los datos del ciudadano y el sistema valida y persiste en almacenamiento (archivo).

## 4. Actores

- Primario: Operador (usuario de escritorio)

## 5. Stakeholders y Necesidades (resumido)

- Operador: Registrar sin errores y con retroalimentación clara.
- Administración: Datos consistentes y sin duplicados.

## 6. Alcance

- Dentro: Captura y validación de datos de ciudadano (CRUD básico).
- Fuera: Autenticación, reportes avanzados, integraciones externas.

## 7. Disparador

Necesidad de registrar ciudadanos

## 8. Precondiciones

- La aplicación está abierta.
- Archivo de almacenamiento accesible (db/ciudadanos.txt).

## 9. Postcondiciones

### Éxito

- Ciudadano agregado al archivo sin duplicados.
  
### Fallo

- No se modifica el archivo; se muestra causa.

## 10. Flujo Principal (Éxito – Caso regular)

1. Operador solicita crear nuevo ciudadano.
2. Sistema muestra formulario vacío.
3. Operador ingresa Nombre(s) "Isaac Alejandro", Apellidos, CURP válida, correo, teléfono 10 dígitos, distrito.
4. Operador confirma guardar.
5. Sistema valida: campos obligatorios, formato CURP, no duplicado, edad >= 18, teléfono 10 dígitos, correo formato.
6. Sistema persiste registro en archivo.
7. Sistema muestra mensaje de éxito.
8. Operador visualiza el nuevo ciudadano en la lista.

## 11. Flujos Alternos

A1. Nombres con un solo nombre (Caso: "Isaac")  

- Igual al paso 3; no hay cambio de lógica. Continúa en paso 4.

A2. Nombres con tres componentes (Caso: "Isaac Alejandro Raúl")  

- Igual al paso 3; continúa en paso 4.

## 12. Flujos de Excepción

E1. CURP duplicada  

1. Ocurre en validación (paso 5).  
2. Sistema detecta CURP existente.  
3. Sistema muestra ERR_CURP_DUP.  
4. No se guarda. Caso termina.

E2. CURP inválida (formato o estructura)  

1. En paso 5 se detecta formato incorrecto (ej. "XXXX001201GYNRLSA5").  
2. Sistema muestra ERR_CURP_FORMAT.  
3. Caso termina.

E3. Menor de edad  

1. Sistema calcula edad a partir de CURP (ej. "LOPA220101...").  
2. Edad < 18.  
3. Sistema muestra ERR_EDAD.  
4. Caso termina.

E4. Teléfono incorrecto  

1. Teléfono no tiene 10 dígitos (ej. "12").  
2. Sistema muestra ERR_TEL_FORMAT.  
3. Caso termina.

E5. Correo inválido  

1. Formato no coincide con patrón básico.  
2. Sistema muestra ERR_MAIL_FORMAT.  
3. Caso termina.

## 13. Reglas de Negocio

- RN1: La CURP debe ser única.
- RN2: La CURP debe cumplir patrón alfanumérico de 18 chars (A-Z0-9).
- RN3: El ciudadano debe ser mayor o igual a 18 años (fecha en CURP).
- RN4: Teléfono debe contener exactamente 10 dígitos.
- RN5: Correo con formato válido (expresión regular estándar simplificada).
- RN6: Todos los campos son obligatorios salvo aclaración futura.

## 14. Validaciones de Datos

| Campo      | Regla / Patrón          | Obligatorio | Notas                          |
| ---------- | ----------------------- | ----------- | ------------------------------ |
| Nombres    | Texto letras y espacios | Sí          | Se permiten 1..3 componentes   |
| Apellido P | Letras                  | Sí          |                                |
| Apellido M | Letras                  | Sí          |                                |
| CURP       | ^ [A-Z0-9] {18}$        | Sí          | Deriva edad                    |
| Correo     | Patrón correo           | Sí          | Único opcional (no confirmado) |
| Teléfono   | ^\\d{10}$               | Sí          | Solo dígitos                   |
| Distrito   | Entero >=1              | Sí          | Rango pendiente                |

## 15. Datos Involucrados

Entidad Ciudadano: nombres, apellidoP, apellidoM, curp, correo, telefono, distrito.

## 16. Requerimientos No Funcionales (pendiente / inferido)

- Respuesta validación inmediata en UI (objetivo < 1s local).
- Persistencia en archivo plano.

## 17. Suposiciones

- CURP contiene fecha correcta.
- No hay zonas horarias que alteren cálculo de edad.

## 18. Dependencias

- Sistema de archivos local (lectura/escritura).

## 19. Riesgos (borrador)

- Corrupción de archivo por escritura concurrente (no manejado).
- Falta de validación robusta de correo.

## 20. Frecuencia / Volumen

(Pendiente – no se deduce del repo).

## 21. Mensajes / Feedback

| Código                | Mensaje                                  |
| --------------------- | ---------------------------------------- |
| OK_REGISTRO           | Ciudadano registrado correctamente.      |
| ERR_EMPTY_FIELDS      | Alguno de los campos esta vacio          |
| ERR_CURP_DUP          | La CURP ya existe.                       |
| ERR_CURP_FORMAT       | CURP inválida.                           |
| ERR_CURP_LENGTH       | CURP debe tener 18 digitos.              |
| ERR_EDAD              | El ciudadano debe ser mayor de edad.     |
| ERR_TEL_FORMAT        | Teléfono inválido (10 dígitos).          |
| ERR_MAIL_FORMAT       | Correo con formato inválido.             |
| ERR_DIST_FORMAT       | El distrito debe de ser un numero entero |
| ERR_DIST_OUT_OF_RANGE | distrito fuera de rango (1-9)            |
| ERR_IO                | Error al guardar datos.                  |
