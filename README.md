# LockerEasy - Sistema de Gestión de Lockers

## 📋 Descripción

Sistema MVC en Java para la gestión de rentas de lockers, ventas de productos, descuentos, y transacciones financieras. Prototipo simple que cumple con todos los requisitos funcionales especificados.

## 🎯 Requisitos Funcionales Implementados

### ✅ FR1: Registro y Gestión de Rentas

- Registrar rentas de lockers con datos del cliente
- Capturar hora de inicio, duración y estado
- Asociar clientes con lockers específicos

**Controller:** `RentaController.java`

**Métodos principales:**

- `registrarRenta()` - Registra nueva renta
- `obtenerRentasActivas()` - Lista rentas activas
- `obtenerLockersDisponibles()` - Muestra lockers libres

### ✅ FR2: Finalización y Cancelación de Rentas

- Finalizar rentas y liberar lockers
- Cancelar rentas con solicitud de feedback
- Prevención de doble reserva (double booking)

**Controller:** `RentaController.java`

**Métodos principales:**

- `finalizarRenta()` - Finaliza renta y libera locker
- `cancelarRenta()` - Cancela con motivo y solicita feedback

### ✅ FR3: Descuentos y Promociones

- Aplicar descuentos a servicios
- Gestionar códigos promocionales
- Crear, actualizar y eliminar descuentos

**Controller:** `DescuentoController.java`

**Métodos principales:**

- `aplicarDescuento()` - Aplica descuento a un servicio
- `crearDescuento()` - Crea nueva promoción
- `validarCodigo()` - Verifica validez del código

**Descuentos predefinidos:**

- `ESTUDIANTE` - 15% de descuento
- `CLIENTE_FRECUENTE` - 20% de descuento
- `PRIMERA_COMPRA` - 10% de descuento
- `PROMO_VERANO` - 25% de descuento

### ✅ FR4: Registro de Ventas

- Registrar ventas de productos
- Vincular ventas a clientes con rentas activas
- Historial de compras por cliente

**Controller:** `VentaController.java`

**Métodos principales:**

- `registrarVenta()` - Registra venta vinculada a cliente
- `obtenerVentasPorCliente()` - Historial de compras

**Catálogo de productos:**

- Candado - $150
- Agua - $15
- Snack - $25
- Bebida Energética - $35
- Cargador USB - $200

### ✅ FR5: Gestión de Precios

- Editar precios de productos desde panel admin
- Modificar tarifas de renta
- Agregar/eliminar productos del catálogo

**Controllers:** `VentaController.java`, `RentaController.java`

**Métodos principales:**

- `actualizarPrecioProducto()` - Modifica precio de producto
- `agregarProducto()` - Añade nuevo producto
- `eliminarProducto()` - Elimina producto del catálogo

### ✅ FR6: Registro de Transacciones Financieras

- Registrar todas las transacciones (rentas, ventas, pagos)
- Historial completo de movimientos
- Reportes financieros

**Controller:** `TransaccionController.java`

**Métodos principales:**

- `registrarTransaccionRenta()` - Registra transacción de renta
- `registrarTransaccionVenta()` - Registra transacción de venta
- `registrarPago()` - Registra pagos generales
- `registrarCancelacion()` - Registra cancelaciones y reembolsos
- `generarReporteFinanciero()` - Genera reporte completo

**Tipos de transacciones:**

- RENTA - Renta de locker
- VENTA - Venta de producto
- PAGO - Pago general
- REEMBOLSO - Devolución de dinero
- CANCELACION - Cancelación de servicio

## 🏗️ Arquitectura MVC

### 📁 Estructura del Proyecto

```ascii
LockerEasy/
├── Main.java                          # Punto de entrada con demo
└── backend/
    ├── controllers/                   # Capa de Control
    │   ├── RentaController.java       # FR1, FR2
    │   ├── VentaController.java       # FR4, FR5
    │   ├── TicketController.java      # Gestión de tickets
    │   ├── DescuentoController.java   # FR3
    │   ├── TransaccionController.java # FR6
    │   └── LockerEasyController.java  # Controller principal
    └── model/                         # Capa de Modelo
        ├── Renta.java
        ├── Venta.java
        ├── Locker.java
        ├── Ticket.java
        ├── Servicio.java
        ├── Descuento.java (interface)
        ├── TipoServicio.java (interface)
        ├── ObjetoRenta.java (interface)
        └── Ubicacion.java (enum)
```

### 📊 Diagrama de Relaciones

```ascii
LockerEasyController (Principal)
    │
    ├── RentaController
    │       └── Gestiona: Renta, Locker
    │
    ├── VentaController
    │       └── Gestiona: Venta, Catálogo
    │
    ├── TicketController
    │       └── Gestiona: Ticket, Servicio
    │
    ├── DescuentoController
    │       └── Gestiona: Descuento, Promociones
    │
    └── TransaccionController
            └── Gestiona: Transaccion, Historial
```

## 🚀 Uso del Sistema

### Compilación y Ejecución

```powershell
# Compilar
javac Main.java backend/model/*.java backend/controllers/*.java

# Ejecutar
java Main
```

### Ejemplo de Uso Básico

```java
// Inicializar sistema
LockerEasyController sistema = new LockerEasyController();

// FR1: Registrar renta
Renta renta = sistema.registrarRenta("Juan Pérez", 1, 3, 50.0f, "Tarjeta");

// FR4: Registrar venta
List<String> productos = Arrays.asList("Candado", "Agua");
Venta venta = sistema.registrarVenta("Juan Pérez", productos, "Efectivo");

// FR3: Aplicar descuento
Servicio servicio = new Servicio(1, renta, null, renta.calcularTotalRenta());
sistema.aplicarDescuento(servicio, "ESTUDIANTE");

// FR2: Finalizar renta
sistema.finalizarRenta(1);

// FR6: Ver reporte financiero
sistema.generarReporteFinanciero();
```

## 📦 Modelos Principales

### Renta

```java
- id: int
- nombre: String (cliente)
- fecha: LocalDateTime
- precio: float (por hora)
- cantidad: int
- locker: ObjetoRenta
- tiempo: int (horas)
```

### Venta

```java
- id: int
- nombre: String (cliente)
- fecha: LocalDateTime
- precio: float
- cantidad: int
- productos: List<String>
```

### Locker

```java
- id: int
- estaOcupado: boolean
- ubicacion: Ubicacion (enum)
```

### Ticket

```java
- id: int
- nombreCliente: String
- correoCliente: String
- servicios: List<Servicio>
```

### Transaccion

```java
- id: int
- tipo: TipoTransaccion
- descripcion: String
- monto: float
- fecha: LocalDateTime
- cliente: String
- metodoPago: String
```

## 🔧 Controllers y sus Responsabilidades

### LockerEasyController (Principal)

- Coordina todos los demás controllers
- Proporciona una interfaz unificada
- Integra transacciones con rentas y ventas

### RentaController

- Gestión completa de rentas
- Control de disponibilidad de lockers
- Prevención de doble reserva

### VentaController

- Registro de ventas de productos
- Gestión del catálogo
- Actualización de precios

### TicketController

- Creación de tickets
- Asociación de servicios con clientes
- Cálculo de totales

### DescuentoController

- Aplicación de descuentos
- Gestión de promociones
- Validación de códigos

### TransaccionController

- Registro de todas las transacciones
- Generación de reportes financieros
- Historial de movimientos

## 📝 Notas Técnicas

### Características del Prototipo

- **Simplicidad**: Diseño simple y directo
- **Extensibilidad**: Fácil de ampliar con nuevas funcionalidades
- **Modularidad**: Controllers independientes y reutilizables
- **Persistencia**: Usa estructuras en memoria (HashMap, List)

### Próximas Mejoras Sugeridas

1. Agregar persistencia con base de datos
2. Implementar interfaz gráfica (GUI)
3. Agregar autenticación y roles de usuario
4. Sistema de notificaciones por email
5. Generar reportes en PDF
6. API REST para integración

## 👥 Casos de Uso

### 1. Cliente Renta un Locker

1. Cliente solicita locker
2. Sistema muestra lockers disponibles
3. Sistema registra renta (FR1)
4. Sistema registra transacción (FR6)
5. Locker marcado como ocupado (FR2)

### 2. Cliente Compra Productos

1. Cliente selecciona productos
2. Sistema calcula total
3. Opcionalmente aplica descuento (FR3)
4. Sistema registra venta (FR4)
5. Sistema registra transacción (FR6)

### 3. Admin Actualiza Precios

1. Admin accede al sistema
2. Selecciona producto/servicio
3. Actualiza precio (FR5)
4. Sistema confirma cambio

### 4. Cliente Finaliza Renta

1. Cliente devuelve locker
2. Sistema finaliza renta (FR2)
3. Locker queda disponible
4. Sistema genera ticket final

### Prompt

Necesito crees un controller con estos models que te daré, los requisitos que deben cumplir el controller son estos: FR1. Register and manage locker rentals including customer data, start time, duration, and status FR2. End or cancel (feedback request) a rental and mark the locker as available (prevent double booking) FR3. Apply discounts or promotions to services FR4. Saves record product sales and link them to customers with active rentals FR5. Manage pricing: edit rental rates and product prices from the admin panel FR6. Register all financial transactions (rentals, sales, payments) además, formato MVC, java, prototipo simple

## 📞 Soporte

Para preguntas o sugerencias sobre este prototipo, consulta la documentación del código fuente donde cada método incluye comentarios detallados.

---

**Versión:** 1.0  
**Fecha:** Noviembre 2025  
**Lenguaje:** Java  
**Patrón:** MVC (Model-View-Controller)
