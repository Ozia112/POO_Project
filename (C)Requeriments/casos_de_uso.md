# Casos de uso 

## 01 RF: Registrar Alquiler de Taquilla

**Actor:** Empleado  
**Objetivo:** Crear un nuevo registro de alquiler con datos del cliente y taquilla asignada  
**Precondiciones:** 
- Empleado con sesión activa
- Taquillas disponibles existentes

**Flujo Principal:**
1. Empleado selecciona "Nuevo Alquiler"
2. Sistema muestra formulario y mapa de taquillas disponibles
3. Empleado ingresa datos del cliente y selecciona taquilla
4. Sistema calcula monto y confirma registro
5. Sistema marca taquilla como ocupada y genera transacción

**Flujos Alternos:**
- Cliente recurrente: Sistema autocompleta datos
- Datos inválidos: Sistema muestra errores específicos

**Resultado:** Alquiler registrado, taquilla ocupada, transacción contable creada

## 02 RF: Finalizar Alquiler

**Actor:** Empleado  
**Objetivo:** Liberar taquilla y completar proceso de alquiler  
**Precondiciones:** Alquiler activo existente

**Flujo Principal:**
1. Empleado selecciona alquiler activo
2. Sistema muestra detalles y opción "Finalizar"
3. Empleado confirma finalización
4. Sistema marca taquilla como disponible
5. Sistema solicita feedback opcional al cliente
6. Sistema registra hora de finalización

**Flujos Alternos:**
- Cancelación: Empleado cancela sin completar período
- Sin feedback: Cliente declina dar opinión

**Resultado:** Taquilla liberada, alquiler completado, registro actualizado

## 03 RF: Registrar Venta de Productos

**Actor:** Empleado  
**Objetivo:** Vender productos y vincularlos a clientes con alquileres activos  
**Precondiciones:** Productos en inventario, cliente con alquiler activo

**Flujo Principal:**
1. Empleado selecciona "Venta de Productos"
2. Sistema muestra lista de productos disponibles
3. Empleado selecciona productos y cantidades
4. Sistema sugiere vincular a alquiler activo
5. Empleado confirma venta y vinculación
6. Sistema actualiza inventario y registra transacción

**Flujos Alternos:**
- Sin alquiler activo: Venta se registra sin vinculación
- Stock insuficiente: Sistema alerta y limita cantidad

**Resultado:** Venta registrada, inventario actualizado, transacción financiera creada


## 04 RF: Gestionar Precios

**Actor:** Administrador  
**Objetivo:** Modificar tarifas de alquiler y precios de productos  
**Precondiciones:** Sesión de administrador activa

**Flujo Principal:**
1. Administrador accede a "Panel de Configuración"
2. Sistema muestra opciones de precios
3. Administrador modifica tarifas o precios
4. Sistema valida cambios
5. Administrador confirma actualización
6. Sistema aplica nuevos precios inmediatamente

**Flujos Alternos:**
- Precio inválido: Sistema rechaza y muestra error
- Cambio masivo: Administrador actualiza múltiples precios

**Resultado:** Precios actualizados en todo el sistema


## 05 RF: Registrar Transacción Financiera

**Actor:** Sistema  
**Objetivo:** Crear registro contable de cada operación financiera  
**Precondiciones:** Operación financiera en proceso

**Flujo Principal:**
1. Sistema detecta operación financiera (alquiler, venta, pago)
2. Sistema captura: monto, tipo, fecha/hora, usuario, ubicación
3. Sistema valida integridad de datos
4. Sistema guarda registro permanente
5. Sistema confirma creación del registro

**Flujos Alternos:**
- Datos incompletos: Sistema rechaza transacción
- Error de almacenamiento: Reintenta guardado

**Resultado:** Transacción registrada con todos los datos


## 06 RF: Generar Reporte Diario

**Actor:** Empleado/Administrador  
**Objetivo:** Visualizar ingresos e historial de transacciones del día  
**Precondiciones:** Sesión activa

**Flujo Principal:**
1. Usuario selecciona "Reportes Diarios"
2. Sistema muestra selector de fechas
3. Usuario selecciona fecha específica
4. Sistema genera reporte con: ingresos totales, desglose por tipo, transacciones
5. Sistema muestra reporte en pantalla

**Flujos Alternos:**
- Sin datos: Sistema muestra mensaje "No hay transacciones"
- Filtros: Usuario aplica filtros por tipo de transacción

**Resultado:** Reporte visualizado con datos financieros del día


## 07 RF: Aplicar Descuentos

**Actor:** Empleado  
**Objetivo:** Aplicar promociones o descuentos a servicios  
**Precondiciones:** Operación de venta o alquiler en proceso

**Flujo Principal:**
1. Durante registro de alquiler/venta, empleado selecciona "Aplicar Descuento"
2. Sistema muestra promociones disponibles
3. Empleado selecciona descuento o ingresa código
4. Sistema valida y aplica descuento
5. Sistema recalcula total automáticamente

**Flujos Alternos:**
- Descuento inválido: Sistema rechaza y explica razón
- Descuento manual: Empleado ingresa porcentaje/monto específico

**Resultado:** Descuento aplicado, monto final actualizado


## 08 RF: Exportar Reportes a Excel

**Actor:** Administrador  
**Objetivo:** Exportar datos contables a formato Excel  
**Precondiciones:** Reporte generado en pantalla

**Flujo Principal:**
1. Administrador genera reporte específico
2. Sistema muestra opción "Exportar a Excel"
3. Administrador selecciona formato y rango
4. Sistema genera archivo .xlsx
5. Sistema descarga archivo al dispositivo

**Flujos Alternos:**
- Datos extensos: Sistema divide en múltiples archivos
- Error de generación: Sistema notifica y sugiere reintentar

**Resultado:** Archivo Excel descargado con datos contables


## 09 RF: Visualizar Estado de Taquillas

**Actor:** Empleado  
**Objetivo:** Ver estado actual de taquillas y resumen de ventas  
**Precondiciones:** Sesión activa

**Flujo Principal:**
1. Empleado accede a pantalla principal
2. Sistema carga y muestra:
   - Mapa visual de taquillas (colores por estado)
   - Resumen de ventas del día
   - Productos más vendidos
   - Métricas clave
3. Sistema actualiza información en tiempo real

**Flujos Alternos:**
- Primero uso: Sistema muestra tutorial breve
- Sin conexión: Sistema muestra última data guardada

**Resultado:** Vista completa del estado actual del negocio

---

## 01 NFR: Operación Offline

**Actor:** Sistema  
**Objetivo:** Funcionar sin conexión a internet mantiendo rendimiento  
**Precondiciones:** Sistema instalado y configurado

**Flujo Principal:**
1. Sistema detecta falta de conexión
2. Cambia a modo offline automáticamente
3. Almacena transacciones localmente
4. Mantiene interfaz responsive
5. Al recuperar conexión, sincroniza datos pendientes

**Resultado:** Operación continua sin interrupciones por falta de conexión


## 02 NFR: Confirmación Administrativa

**Actor:** Sistema  
**Objetivo:** Validar autorización para operaciones críticas  
**Precondiciones:** Intento de modificación/eliminación de datos

**Flujo Principal:**
1. Usuario intenta editar/eliminar dato crítico
2. Sistema solicita credenciales de administrador
3. Usuario ingresa usuario/contraseña
4. Sistema valida permisos
5. Si es válido, permite operación
6. Sistema registra acción en bitácora

**Resultado:** Operación autorizada, o rechazada por falta de permisos


## 03 NFR: Auditoría de Transacciones

**Actor:** Sistema  
**Objetivo:** Registrar trazabilidad completa de cada transacción  
**Precondiciones:** Transacción en proceso

**Flujo Principal:**
1. Sistema captura transacción
2. Registra automáticamente: timestamp, usuario, ubicación, monto, tipo
3. Almacena en base de datos segura
4. Genera ID único de transacción
5. Confirma registro exitoso

**Resultado:** Transacción registrada con todos los datos de auditoría requeridos


## 04 NFR: Interfaz Intuitiva

**Actor:** Sistema  
**Objetivo:** Proporcionar experiencia de usuario optimizada para diferentes dispositivos  
**Precondiciones:** Sistema en ejecución

**Flujo Principal:**
1. Sistema detecta resolución de pantalla
2. Adapta interfaz a tamaño detectado (tablet/laptop)
3. Organiza elementos para fácil acceso
4. Mantiene tiempos de respuesta
5. Proporciona navegación consistente

**Resultado:** Interfaz usable y eficiente en diferentes dispositivos


## 05 NFR: Compatibilidad Multiplataforma

**Actor:** Sistema  
**Objetivo:** Ejecutarse correctamente en Windows y Android  
**Precondiciones:** Instalación completada

**Flujo Principal:**
1. Sistema detecta plataforma 
2. Carga configuración específica para plataforma
3. Ajusta interfaz según especificaciones del SO
4. Inicializa componentes compatibles
5. Confirma operatividad completa

**Resultado:** Sistema funcionando correctamente en la plataforma detectada