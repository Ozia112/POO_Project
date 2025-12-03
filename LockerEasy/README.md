# LockerEasy V0.1

Sistema de gestión de lockers para negocios. Permite administrar ventas, rentas, tickets, reportes e inventario de productos con una interfaz gráfica moderna.

## Características

- 🎫 **Gestión de Tickets** - Crear y administrar tickets de venta/renta
- 📦 **Control de Inventario** - Manejo de productos y existencias
- 🏷️ **Sistema de Etiquetas** - Categorización de productos
- 📊 **Reportes Diarios** - Generación automática de reportes
- 🔐 **Autenticación** - Sistema de login seguro
- 💾 **Base de Datos** - Persistencia con PostgreSQL + Hibernate

## Requisitos del Sistema

### Para usar el ejecutable (.exe)
- **Windows 10/11** (64-bit)
- **PostgreSQL 14+** (base de datos)
- **No requiere Java instalado** - incluye runtime embebido

### Para usar el JAR o compilar
- **Java 17** o superior
- **PostgreSQL 14+** (base de datos)
- **Maven 3.8+** (solo para compilar desde código fuente)

## Instalación

### Opción 1: Usar el ejecutable .exe (Recomendado para Windows)

1. Descargar toda la carpeta `LockerEasy/` de esta rama (incluye `LockerEasy.exe` y `runtime/`)
2. Configurar la base de datos PostgreSQL (ver sección Base de Datos)
3. Ejecutar `LockerEasy.exe`

> ⚠️ **Importante**: El ejecutable necesita la carpeta `runtime/` en el mismo directorio para funcionar.

### Opción 2: Usar el JAR ejecutable

1. Descargar `LockerEasy-V0.1.jar` de esta rama
2. Configurar la base de datos PostgreSQL (ver sección Base de Datos)
3. Ejecutar:

```bash
java -jar LockerEasy-V0.1.jar
```

### Opción 3: Compilar desde código fuente

1. Clonar el repositorio:
```bash
git clone https://github.com/Ozia112/POO_Project.git
cd POO_Project
git checkout LockerEasy-V.0.1
```

2. Configurar la base de datos (ver sección Base de Datos)

3. Compilar y ejecutar:
```bash
cd LockerEasy
mvn clean compile
mvn javafx:run
```

4. Para generar el JAR con dependencias:
```bash
mvn package -DskipTests
```

## Configuración de Base de Datos

1. Instalar PostgreSQL

2. Crear la base de datos:
```sql
CREATE DATABASE lockereasy;
```

3. Configurar credenciales en `src/main/resources/hibernate.cfg.xml`:
```xml
<property name="hibernate.connection.url">jdbc:postgresql://localhost:5432/lockereasy</property>
<property name="hibernate.connection.username">tu_usuario</property>
<property name="hibernate.connection.password">tu_contraseña</property>
```

4. Las tablas se crean automáticamente al iniciar la aplicación (Hibernate DDL auto)

## Estructura del Proyecto

```
LockerEasy/
├── LockerEasy.exe          # Ejecutable Windows (no requiere Java)
├── LockerEasy-V0.1.jar     # JAR ejecutable con dependencias
├── runtime/                # JVM embebida para el .exe
├── pom.xml                 # Configuración Maven
├── src/main/java/
│   ├── Main.java           # Punto de entrada
│   ├── controller/         # Controladores de lógica
│   ├── dao/                # Acceso a datos (Hibernate)
│   ├── model/              # Entidades/Modelos
│   ├── view/               # Interfaces gráficas (JavaFX)
│   └── util/               # Utilidades (Logger)
└── src/main/resources/
    ├── hibernate.cfg.xml   # Config de base de datos
    └── logback.xml         # Config de logs
```

## Dependencias Principales

| Dependencia | Versión | Uso |
|-------------|---------|-----|
| JavaFX | 17.0.13 | Interfaz gráfica |
| Hibernate | 6.4.4 | ORM / Base de datos |
| PostgreSQL Driver | 42.7.2 | Conexión a PostgreSQL |
| HikariCP | 5.0.1 | Pool de conexiones |
| Jackson | 2.16.0 | Serialización JSON |
| Logback | 1.4.14 | Sistema de logs |
| ControlsFX | 11.2.0 | Componentes UI adicionales |

## Licencia

Proyecto académico - Universidad Autónoma de Yucatán (UADY)
Programación Orientada a Objetos - Semestre 3

## Autores

Equipo de desarrollo POO 2025
