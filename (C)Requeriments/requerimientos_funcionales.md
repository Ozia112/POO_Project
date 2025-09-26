# Requisitos del Sistema: Gestor de Renta de Lockers y Venta de Artículos

## Requisitos Funcionales (RF)

### 1. Gestión de Renta de Lockers
- RF1. El sistema debe permitir registrar la renta de un locker con los datos del cliente, hora de inicio y duración
- RF2. El sistema debe calcular automáticamente el tiempo restante de la renta en base a la hora de inicio y la duración
- RF3. El sistema debe permitir extenderse del tiempo de renta de un locker (se calculará hasta un máximo de 15 minutos sin cargo extra)
- RF4. El sistema debe permitir cancelar una renta activa
- RF5. El sistema debe permitir aplicar descuentos a una renta
- RF6. El sistema debe permitir finalizar la renta de un locker y marcar el locker como disponible

### 2. Gestión de Disponibilidad de Lockers
- RF7. El sistema debe mostrar visualmente el estado de cada locker (disponible, rentado, en limpieza/mantenimiento)
- RF8. El sistema debe evitar la doble asignación de un mismo locker

### 3. Gestión de Ventas de Productos
- RF9. El sistema debe permitir registrar ventas de productos desde una pestaña separada
- RF10. El sistema debe permitir aplicar descuentos a productos
- RF11. El sistema debe poder asociar una venta con un cliente que haya rentado un locker

### 4. Historial y Reportes
- RF12. El sistema debe registrar un historial de rentas y ventas diarias
- RF13. El sistema debe permitir consultar reportes pasados diarios de ingresos por rentas y ventas
- RF14. El sistema debe generar poder generar tableas en formato (por definir PDF, Excel, .txt)

### 5. Ajustes y Administración
- RF15. El sistema debe permitir editar las tarifas de renta y precios de productos desde un panel de administración
- RF16. El sistema debe permitir registrar nuevos productos para la venta