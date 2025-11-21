# 📝 PARA NICO - Interfaz JavaFX Pendiente

**De:** @Fabio  
**Para:** @Nico Inge Soft  
**Fecha:** 20 de noviembre de 2025

---

## 🎯 Tu Tarea: Crear Interfaz JavaFX

### ¿Qué necesitas hacer?

Crear un archivo `AppTestGUI.java` con una interfaz JavaFX que conecte los métodos de `App.java`.

### 📁 Ubicación del archivo a crear:
```
LockerEasy/src/main/java/AppTestGUI.java
```

---

## 🔌 Métodos que debes conectar

### 1. Crear Ticket
```java
TicketController ticketController = new TicketController();
Ticket ticket = ticketController.crearNuevoTicket(nombre, email);
```

**UI necesaria:**
- TextField para nombre
- TextField para email
- Button "Crear Ticket"

### 2. Iniciar Renta
```java
RentaController rentaController = new RentaController();
rentaController.iniciarRenta(ubicacion, ticket, ticketController);
```

**UI necesaria:**
- ComboBox con las ubicaciones (Ubicacion.PA_T1_L1, etc.)
- Button "Iniciar Renta"

### 3. Finalizar Renta
```java
rentaController.finalizarRenta(ubicacion, ticket, ticketController);
```

**UI necesaria:**
- ComboBox para seleccionar ubicación
- Button "Finalizar Renta"

### 4. Registrar Venta
```java
VentaController ventaController = new VentaController();
ventaController.registrarVenta(productoId, cantidad, ticket, ticketController);
```

**UI necesaria:**
- TextField para producto ID
- TextField para cantidad
- Button "Registrar Venta"

### 5. Ver Reporte
```java
ReporteController reporteController = new ReporteController();
Reporte reporte = reporteController.getReporte();
// Mostrar: reporte.getFechaReporte(), reporte.getTotal(), etc.
```

**UI necesaria:**
- Button "Ver Reporte"
- Area para mostrar resultados

---

## 📚 Referencias que te ayudarán

### 1. Ver cómo se usan los métodos:
Abre: `src/main/java/App.java` (líneas 14-90)

### 2. Base de interfaz JavaFX existente:
Abre: `src/main/java/view/TicketGUI.java`

### 3. Ver ejemplos de uso en las pruebas:
- `src/test/java/controller/TicketControllerTest.java`
- `src/test/java/controller/RentaControllerTest.java`
- `src/test/java/controller/VentaControllerTest.java`

---

## 🎨 Estructura Sugerida de la Interfaz

```
┌─────────────────────────────────────────┐
│  LOCKEREASY - Panel de Control          │
│  Ticket Actual: #123 - Juan Pérez       │
│  Total: $150.00                         │
├─────────────────────────────────────────┤
│                                         │
│  [ Sección 1: Crear Ticket ]            │
│    Nombre: [_____________]              │
│    Email:  [_____________]              │
│    [Crear Ticket]                       │
│                                         │
│  [ Sección 2: Rentas ]                  │
│    Ubicación: [▼ PA_T1_L1]              │
│    [Iniciar Renta] [Finalizar Renta]    │
│                                         │
│  [ Sección 3: Ventas ]                  │
│    Producto ID: [___]  Cantidad: [___]  │
│    [Registrar Venta]                    │
│                                         │
│  [ Sección 4: Reportes ]                │
│    [Ver Reporte del Día]                │
│                                         │
├─────────────────────────────────────────┤
│  LOG DEL SISTEMA:                       │
│  [                                   ]  │
│  [ Aquí se muestran las operaciones  ]  │
│  [                                   ]  │
└─────────────────────────────────────────┘
```

---

## ✅ Checklist de lo que debe hacer tu interfaz

- [ ] Crear ventana principal con JavaFX
- [ ] Inicializar los 5 controladores
- [ ] Panel superior con info del ticket y total
- [ ] Formulario para crear ticket
- [ ] Sección para gestionar rentas
- [ ] Sección para registrar ventas
- [ ] Sección para ver reportes
- [ ] TextArea para log del sistema
- [ ] Actualizar totales después de cada operación
- [ ] Mostrar mensajes de éxito/error

---

## 🚀 Para probar tu interfaz cuando termines:

```bash
cd LockerEasy
mvn clean compile
mvn javafx:run -Djavafx.mainClass=AppTestGUI
```

O usa el script:
```bash
./run_tests.sh
# Selecciona opción 1 (cuando la implementes)
```

---

## 💡 Tips importantes:

1. **JavaFX ya está configurado** en `pom.xml`, no necesitas agregar dependencias

2. **Inicializa los controladores** en el método `start()`:
```java
public class AppTestGUI extends Application {
    private TicketController ticketController;
    private RentaController rentaController;
    // etc...
    
    @Override
    public void start(Stage primaryStage) {
        ticketController = new TicketController();
        rentaController = new RentaController();
        // etc...
    }
}
```

3. **Configura dependencias** entre controladores:
```java
rentaController.setReporteController(reporteController);
```

4. **Mantén un ticket actual** como variable de instancia:
```java
private Ticket ticketActual;
```

5. **Actualiza la UI** después de cada operación:
```java
private void actualizarInfoTicket() {
    if (ticketActual != null) {
        ticketInfoLabel.setText("Ticket #" + ticketActual.getTicketId());
        totalLabel.setText("Total: $" + ticketActual.getTotalTicket());
    }
}
```

---

## 📞 Si tienes dudas:

- Revisa el código en `App.java` - muestra exactamente cómo usar cada método
- Las pruebas unitarias tienen ejemplos de uso
- `TicketGUI.java` ya tiene estructura básica de JavaFX

---

## 🎉 Cuando termines:

1. Prueba que todos los botones funcionen
2. Verifica que los totales se actualicen
3. Asegúrate de que el log muestre las operaciones
4. Avísale a @Fabio para integrar con las pruebas unitarias

---

**¡Éxito con la implementación!** 🚀

---

**Nota:** Fabio ya hizo:
- ✅ Pruebas unitarias (12 tests)
- ✅ Script de ejecución
- ✅ Configuración de proyecto
- ✅ Documentación

Tu parte es: Crear la interfaz JavaFX para probar visualmente el sistema.
