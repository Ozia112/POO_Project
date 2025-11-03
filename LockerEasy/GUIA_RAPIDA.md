# 🚀 Guía Rápida - LockerEasy

## Inicio Rápido

### Compilar el Proyecto

```powershell
# Navegar al directorio
cd "LockerEasy"

# Compilar todos los archivos
javac Main.java backend/model/*.java backend/controllers/*.java
```

### Ejecutar

```powershell
# Demo completa
java Main

# Ejemplos específicos
java EjemplosUso

# Pruebas del sistema
java PruebasSistema
```

## 📚 Referencia Rápida de Controllers

### LockerEasyController (Principal)

```java
LockerEasyController sistema = new LockerEasyController();

// Registrar renta (FR1)
Renta renta = sistema.registrarRenta("Cliente", lockerId, horas, precioPorHora, "Tarjeta");

// Finalizar renta (FR2)
sistema.finalizarRenta(lockerId);

// Cancelar renta (FR2)
sistema.cancelarRenta(lockerId, "motivo", "cliente", monto);

// Registrar venta (FR4)
List<String> productos = Arrays.asList("Agua", "Snack");
Venta venta = sistema.registrarVenta("Cliente", productos, "Efectivo");

// Aplicar descuento (FR3)
Servicio servicioConDescuento = sistema.aplicarDescuento(servicio, "ESTUDIANTE");

// Actualizar precio (FR5)
sistema.actualizarPrecioProducto("Agua", 20.0f);

// Ver reportes (FR6)
sistema.generarReporteFinanciero();
```

### RentaController

```java
RentaController rentaCtrl = sistema.getRentaController();

// Ver lockers disponibles
List<Locker> disponibles = rentaCtrl.obtenerLockersDisponibles();

// Ver rentas activas
List<Renta> activas = rentaCtrl.obtenerRentasActivas();

// Obtener locker específico
Locker locker = rentaCtrl.obtenerLocker(lockerId);

// Estado del sistema
rentaCtrl.mostrarEstadoSistema();
```

### VentaController

```java
VentaController ventaCtrl = sistema.getVentaController();

// Ver catálogo
ventaCtrl.mostrarCatalogo();
Map<String, Float> catalogo = ventaCtrl.obtenerCatalogo();

// Gestionar productos
ventaCtrl.agregarProducto("Producto", precio);
ventaCtrl.actualizarPrecioProducto("Producto", nuevoPrecio);
ventaCtrl.eliminarProducto("Producto");

// Historial de ventas
List<Venta> ventas = ventaCtrl.obtenerVentas();
List<Venta> ventasCliente = ventaCtrl.obtenerVentasPorCliente("Cliente");
```

### DescuentoController

```java
DescuentoController descuentoCtrl = sistema.getDescuentoController();

// Ver descuentos
descuentoCtrl.mostrarDescuentos();

// Gestionar descuentos
descuentoCtrl.crearDescuento("CODIGO", "Nombre", porcentaje);
descuentoCtrl.actualizarDescuento("CODIGO", "NuevoNombre", nuevoPorcentaje);
descuentoCtrl.eliminarDescuento("CODIGO");

// Validar código
boolean valido = descuentoCtrl.validarCodigo("CODIGO");

// Obtener descuento
Descuento descuento = descuentoCtrl.obtenerDescuento("CODIGO");
```

### TicketController

```java
TicketController ticketCtrl = sistema.getTicketController();

// Crear ticket
List<Servicio> servicios = new ArrayList<>();
Ticket ticket = ticketCtrl.crearTicket("Cliente", "email@example.com", servicios);

// Agregar servicio a ticket
ticketCtrl.agregarServicioATicket(ticketId, servicio);

// Consultar tickets
Ticket ticket = ticketCtrl.obtenerTicket(ticketId);
List<Ticket> tickets = ticketCtrl.obtenerTicketsPorCliente("Cliente");

// Imprimir ticket
ticketCtrl.imprimirTicket(ticketId);

// Calcular total
float total = ticketCtrl.calcularTotalTicket(ticketId);
```

### TransaccionController

```java
TransaccionController transCtrl = sistema.getTransaccionController();

// Registrar transacciones
transCtrl.registrarTransaccionRenta(renta, "Tarjeta");
transCtrl.registrarTransaccionVenta(venta, "Efectivo");
transCtrl.registrarPago("Cliente", monto, "descripción", "metodoPago");
transCtrl.registrarCancelacion("Cliente", monto, "motivo");

// Consultar historial
List<Transaccion> historial = transCtrl.obtenerHistorial();
transCtrl.mostrarHistorial(limite); // 0 = todas

// Filtrar transacciones
List<Transaccion> porTipo = transCtrl.obtenerTransaccionesPorTipo(TipoTransaccion.RENTA);
List<Transaccion> porCliente = transCtrl.obtenerTransaccionesPorCliente("Cliente");

// Reportes financieros
float ingresos = transCtrl.calcularIngresosTotales();
float ingresosTipo = transCtrl.calcularIngresosPorTipo(TipoTransaccion.VENTA);
transCtrl.generarReporteFinanciero();
```

## 🎯 Casos de Uso Comunes

### Caso 1: Cliente Renta Locker

```java
// 1. Ver lockers disponibles
List<Locker> disponibles = sistema.obtenerLockersDisponibles();

// 2. Registrar renta
Renta renta = sistema.registrarRenta("Juan Pérez", 1, 3, 50.0f, "Tarjeta");

// 3. Verificar registro
if (renta != null) {
    System.out.println("Renta registrada. Total: $" + renta.calcularTotalRenta());
}
```

### Caso 2: Cliente Compra con Descuento

```java
// 1. Crear venta
List<String> productos = Arrays.asList("Candado", "Agua");
Venta venta = sistema.registrarVenta("María López", productos, "Efectivo");

// 2. Crear servicio
Servicio servicio = new Servicio(1, venta, null, venta.calcularTotalVenta());

// 3. Aplicar descuento
Servicio conDescuento = sistema.aplicarDescuento(servicio, "ESTUDIANTE");
```

### Caso 3: Generar Ticket Completo

```java
// 1. Registrar renta y venta
Renta renta = sistema.registrarRenta("Carlos Díaz", 2, 4, 50.0f, "Tarjeta");
List<String> productos = Arrays.asList("Snack", "Bebida Energética");
Venta venta = sistema.registrarVenta("Carlos Díaz", productos, "Tarjeta");

// 2. Crear ticket con descuento
Ticket ticket = sistema.crearTicketCompleto(
    "Carlos Díaz",
    "carlos@email.com",
    renta,
    venta,
    "CLIENTE_FRECUENTE"
);

// 3. Imprimir
sistema.imprimirTicket(ticket.getTicketId());
```

### Caso 4: Admin Actualiza Catálogo

```java
// Actualizar precios
sistema.actualizarPrecioProducto("Agua", 18.0f);
sistema.actualizarPrecioProducto("Snack", 30.0f);

// Agregar productos
sistema.agregarProducto("Powerbank", 350.0f);
sistema.agregarProducto("Cable USB-C", 120.0f);

// Ver catálogo actualizado
sistema.getVentaController().mostrarCatalogo();
```

### Caso 5: Generar Reporte Financiero

```java
// Ver últimas transacciones
sistema.mostrarHistorialTransacciones(10);

// Reporte completo
sistema.generarReporteFinanciero();

// Detalles específicos
TransaccionController tc = sistema.getTransaccionController();
float totalRentas = tc.calcularIngresosPorTipo(TipoTransaccion.RENTA);
float totalVentas = tc.calcularIngresosPorTipo(TipoTransaccion.VENTA);
float totalGeneral = tc.calcularIngresosTotales();

System.out.println("Ingresos por rentas: $" + totalRentas);
System.out.println("Ingresos por ventas: $" + totalVentas);
System.out.println("Total general: $" + totalGeneral);
```

## 📊 Códigos de Descuento Predefinidos

| Código | Nombre | Descuento |
|--------|--------|-----------|
| `ESTUDIANTE` | Descuento Estudiante | 15% |
| `CLIENTE_FRECUENTE` | Cliente Frecuente | 20% |
| `PRIMERA_COMPRA` | Primera Compra | 10% |
| `PROMO_VERANO` | Promoción de Verano | 25% |

## 🛍️ Catálogo de Productos Inicial

| Producto | Precio |
|----------|--------|
| Candado | $150.00 |
| Agua | $15.00 |
| Snack | $25.00 |
| Bebida Energética | $35.00 |
| Cargador USB | $200.00 |

## 🔧 Configuración de Lockers

- **Total de lockers:** 10
- **Planta Baja:** Lockers 1-5
- **Planta Alta:** Lockers 6-10
- **Precio por hora:** Configurable (default: $50.00)

## ⚠️ Validaciones Importantes

### Rentas

- ✅ Locker debe existir
- ✅ Locker debe estar disponible (no ocupado)
- ✅ Prevención de doble reserva
- ✅ Duración debe ser mayor a 0

### Ventas

- ✅ Debe haber al menos un producto
- ✅ Productos deben existir en catálogo
- ✅ Total debe ser mayor a 0

### Descuentos

- ✅ Código debe ser válido
- ✅ Porcentaje entre 0-100
- ✅ Servicio debe existir

### Precios

- ✅ Precio debe ser mayor a 0
- ✅ Producto debe existir para actualizar

## 🐛 Solución de Problemas

### "Locker ya está ocupado"

- Verificar estado del locker antes de rentar
- Finalizar renta anterior si corresponde
- Usar otro locker disponible

### "Producto no encontrado"

- Verificar nombre exacto del producto
- Revisar catálogo actual
- Agregar producto si no existe

### "Código de descuento no válido"

- Verificar código exacto (case-sensitive)
- Crear descuento si no existe
- Ver lista de descuentos disponibles

## 📞 Comandos Útiles

```java
// Ver estado completo
sistema.mostrarEstadoSistema();
sistema.getVentaController().mostrarCatalogo();
sistema.getDescuentoController().mostrarDescuentos();

// Limpiar y resetear (crear nueva instancia)
sistema = new LockerEasyController();
```

## 📄 Archivos Importantes

- `Main.java` - Demo completa del sistema
- `EjemplosUso.java` - Ejemplos específicos por FR
- `PruebasSistema.java` - Pruebas automatizadas
- `README.md` - Documentación completa

## 🔄 Flujo de Trabajo Típico

1. **Cliente llega** → Ver lockers disponibles
2. **Rentar locker** → Registrar renta + transacción
3. **Comprar productos** → Registrar venta + transacción
4. **Aplicar descuento** → Opcional
5. **Generar ticket** → Con todos los servicios
6. **Finalizar** → Liberar locker
7. **Reporte** → Ver transacciones y finanzas

---

**Nota:** Esta guía cubre los casos de uso más comunes. Para funcionalidad avanzada, consultar README.md completo.
