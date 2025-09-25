# Requisitos del Sistema: Gestor de Renta de Lockers y Venta de Artículos

## Requisitos No Funcionales

### 1. Desempeño y Disponibilidad
- RNF1. El sistema debe funcionar sin conexión a internet (offline)
- RNF2. El sistema debe iniciar en menos de 5 segundos en el equipo usado para la gestión
- RNF3. El sistema debe permitir realizar cualquier operación (registro, edición, cancelación) en menos de 3 segundos

### 2. Usabilidad
- RNF4. La interfaz del sistema debe ser intuitiva y accesible para usuarios con pocos o nulos conocimientos técnicos
- RNF5. El sistema debe incluir íconos y elementos visuales que faciliten la navegación (por ejemplo colores para estado de lockers)
- RNF6. La interfaz debe estar optimizada para pantallas medianas (laptops)

### 3. Seguridad
- RNF7. El sistema debe proteger la edición o eliminación de rentas y ventas mediante confirmaciones o contraseñas (nivel administrativo).
- RNF8. El sistema debe tener confirmaciones para evitar modificaciones accidentales en los datos históricos

### 4. Mantenibilidad y Escalabilidad
- RNF9. El sistema debe ser fácil de actualizar por el desarrollador para incorporar cambios futuros según el feedback del cliente
- RNF10. El sistema debe permitir exportar datos a formatos (por definir PDF, Excel, .txt)

### 5. Compatibilidad
- RNF11. El sistema debe ser compatible con sistemas operativos Windows (o Linux, por definir)