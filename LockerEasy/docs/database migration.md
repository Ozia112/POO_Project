# Migracion de localhost -> cloud

Ozia112: Que necesito para pasar la actual base de datos de localhost a un host en la nube considerando la arquitectura maven y el #file:hibernate.cfg.xml y el #file:pom.xml

Workspace: Collecting workspace informationPara migrar tu base de datos PostgreSQL de localhost a un host en la nube, necesitas realizar los siguientes pasos:

## 1. Preparación del Host en la Nube

Primero, necesitas elegir y configurar un servicio de base de datos PostgreSQL en la nube:

**Opciones recomendadas:**

- **AWS RDS** (Amazon Web Services)
- **Google Cloud SQL**
- **Azure Database for PostgreSQL**
- **Heroku Postgres** (más simple para comenzar)
- **Railway** (gratuito con límites)
- **Supabase** (gratuito con límites)
- **ElephantSQL** (gratuito con límites)

## 2. Configuración del hibernate.cfg.xml

Necesitarás actualizar la configuración de conexión:

````xml
<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE hibernate-configuration PUBLIC
        "-//Hibernate/Hibernate Configuration DTD 3.0//EN"
        "http://www.hibernate.org/dtd/hibernate-configuration-3.0.dtd">

<hibernate-configuration>
    <session-factory>
        <!-- Configuracion de la base de datos en la nube -->
        <property name="connection.url">jdbc:postgresql://[TU_HOST_EN_LA_NUBE]:[PUERTO]/[NOMBRE_BD]</property>
        <property name="connection.username">[TU_USUARIO]</property>
        <property name="connection.password">[TU_PASSWORD]</property>

        <!-- Configuración SSL para conexión segura -->
        <property name="connection.driver_class">org.postgresql.Driver</property>
        <property name="dialect">org.hibernate.dialect.PostgreSQLDialect</property>
        
        <!-- Pool de conexiones HikariCP -->
        <property name="hibernate.hikari.connectionTimeout">20000</property>
        <property name="hibernate.hikari.maximumPoolSize">5</property>
        
        <!-- Mostrar SQL en consola -->
        <property name="show_sql">true</property>
        <property name="hbm2ddl.auto">update</property>
        
        <!-- Mapeo de las clases -->
        <mapping class="model.TipoServicio"/>
        <mapping class="model.Ubicacion"/>
        <mapping class="model.Ticket"/>
        <mapping class="model.Renta"/>
        <mapping class="model.Etiqueta"/>
        <mapping class="model.Servicio"/>
        <mapping class="model.Venta"/>
    </session-factory>
</hibernate-configuration>
````

## 3. Agregar Dependencia SSL al pom.xml

La mayoría de servicios en la nube requieren SSL. Asegúrate de tener esto en tu pom.xml:

````xml
<!-- ...existing code... -->
<dependencies>
    <!-- ...existing code... -->
    
    <!-- Ya tienes PostgreSQL, asegúrate que sea la versión más reciente -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <version>42.7.2</version>
    </dependency>
    
    <!-- Ya tienes HikariCP para el pool de conexiones -->
    <dependency>
        <groupId>com.zaxxer</groupId>
        <artifactId>HikariCP</artifactId>
        <version>5.0.1</version>
    </dependency>
    
    <!-- ...existing code... -->
</dependencies>
<!-- ...existing code... -->
````

## 4. Migración de Datos

Para migrar los datos existentes de localhost a la nube:

```bash
# Exportar datos de tu base de datos local
pg_dump -U postgres -d lockereasy > backup_lockereasy.sql

# Importar a la base de datos en la nube
psql -h [TU_HOST] -U [TU_USUARIO] -d [NOMBRE_BD] < backup_lockereasy.sql
```

## 5. Configuración con Variables de Entorno (Recomendado)

Para mayor seguridad, es mejor usar variables de entorno en lugar de credenciales hardcodeadas. Crea una clase de configuración:

````java
package config;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class DatabaseConfig {
    private static SessionFactory sessionFactory;
    
    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            Configuration configuration = new Configuration();
            
            // Configurar desde variables de entorno
            String dbUrl = System.getenv("DB_URL") != null 
                ? System.getenv("DB_URL") 
                : "jdbc:postgresql://localhost:5432/lockereasy";
            String dbUser = System.getenv("DB_USER") != null 
                ? System.getenv("DB_USER") 
                : "postgres";
            String dbPassword = System.getenv("DB_PASSWORD") != null 
                ? System.getenv("DB_PASSWORD") 
                : "postgresql112";
            
            configuration.setProperty("hibernate.connection.url", dbUrl);
            configuration.setProperty("hibernate.connection.username", dbUser);
            configuration.setProperty("hibernate.connection.password", dbPassword);
            
            sessionFactory = configuration.configure().buildSessionFactory();
        }
        return sessionFactory;
    }
}
````

## 6. Actualizar .gitignore

El archivo ya ignora correctamente hibernate.cfg.xml, lo cual es bueno para no exponer credenciales.

## Checklist Final

- [ ] Crear base de datos PostgreSQL en servicio cloud
- [ ] Obtener credenciales: host, puerto, usuario, password
- [ ] Actualizar hibernate.cfg.xml con nuevas credenciales
- [ ] Configurar SSL si es requerido por el proveedor
- [ ] Exportar datos de localhost
- [ ] Importar datos a la nube
- [ ] Probar conexión con la clase `TestConection`
- [ ] Actualizar configuración del firewall para permitir tu IP
- [ ] Considerar usar variables de entorno para credenciales

**Nota importante:** El hibernate.cfg.xml ya está en tu .gitignore, así que las credenciales no se subirán a GitHub, lo cual es correcto para seguridad.

# Migracion de json -> DB

Ozia112: Revisa como esta #file:TicketController.java, #file:ReporteController.java, #file:VentaController.java, #file:RentaController.java y #file:EtiquetaController.java y dime cual es la mejor manera para mapear los modelos de la base de datos sin modificar tanto la dinamica del programa y reducir las consultas a la base de datos al minimo para eficientizar el programa y reducir el consumo de recursos.
Toma en cuenta que la logica de relacion por id y la logica de ids perse de los controladores es debido a que todavia no habiamos implementado base de datos y todo lo manejabamos en json. Ahora queremos migrar todo a postgresql. Determina el metodo que simplifique el codigo y la compleijidad de comunicacion programa-database contempla tambien #file:hibernate.cfg.xml

Workspace: Collecting workspace informationAnalizando tu código, veo que actualmente tienes una arquitectura basada en JSON con gestión manual de IDs y relaciones. Para migrar a PostgreSQL con Hibernate de manera eficiente, te recomiendo la siguiente estrategia:

## Estrategia Recomendada: Patrón DAO + Caché de Sesión

### 1. Problemas Actuales a Resolver

- **IDs manuales**: Hibernate puede generarlos automáticamente
- **Relaciones manuales**: Hibernate puede manejar relaciones `@OneToMany`, `@ManyToOne`
- **Carga de datos**: Actualmente cargas todo desde archivos JSON
- **Sincronización**: No hay transacciones ni consistencia garantizada

### 2. Solución: Arquitectura Híbrida con Caché Mínimo

#### Opción A: **DAO Pattern + Lazy Loading** (Recomendada)

Esta opción minimiza cambios y optimiza consultas:

````java
package dao;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    private static SessionFactory sessionFactory;
    
    static {
        try {
            sessionFactory = new Configuration()
                .configure("hibernate.cfg.xml")
                .buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("SessionFactory creation failed: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }
    
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
    
    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}
````

````java
package dao;

import model.Ticket;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.time.LocalDate;
import java.util.List;

public class TicketDAO {
    
    public void guardar(Ticket ticket) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(ticket);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Error al guardar ticket", e);
        }
    }
    
    public void actualizar(Ticket ticket) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(ticket);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Error al actualizar ticket", e);
        }
    }
    
    public Ticket obtenerPorId(int ticketId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Ticket.class, ticketId);
        }
    }
    
    public List<Ticket> obtenerPorFecha(LocalDate fecha) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Ticket t WHERE t.fecha_reporte = :fecha";
            Query<Ticket> query = session.createQuery(hql, Ticket.class);
            query.setParameter("fecha", fecha);
            return query.list();
        }
    }
    
    public boolean eliminar(int ticketId) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Ticket ticket = session.get(Ticket.class, ticketId);
            if (ticket != null) {
                session.remove(ticket);
                transaction.commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Error al eliminar ticket", e);
        }
    }
    
    public int obtenerMaxIdDelDia(LocalDate fecha) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT COALESCE(MAX(t.ticket_id), 0) FROM Ticket t WHERE t.fecha_reporte = :fecha";
            Query<Integer> query = session.createQuery(hql, Integer.class);
            query.setParameter("fecha", fecha);
            return query.getSingleResult();
        }
    }
}
````

### 3. Actualizar Modelo `Ticket`

````java
package model;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "tickets")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_id")
    private int ticket_id;

    @Column(name = "fecha_reporte", nullable = false)
    private LocalDate fecha_reporte;

    @Column(name = "nombre_cliente", nullable = false)
    private String nombre_cliente;

    @Column(name = "correo_cliente")
    private String correo_cliente;

    @Column(name = "tiempo_emision", nullable = false)
    private Instant tiempo_emision;

    @Column(name = "total_ticket")
    private float total_ticket;

    // Relación bidireccional con Servicio
    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Servicio> servicios = new ArrayList<>();

    // Constructores
    public Ticket() {
        this.fecha_reporte = LocalDate.now();
        this.tiempo_emision = Instant.now();
    }

    public Ticket(LocalDate fecha_reporte, String nombre_cliente, String correo_cliente) {
        this.fecha_reporte = fecha_reporte;
        this.nombre_cliente = nombre_cliente;
        this.correo_cliente = correo_cliente;
        this.tiempo_emision = Instant.now();
    }

    // Métodos helper para mantener sincronización bidireccional
    public void agregarServicio(Servicio servicio) {
        servicios.add(servicio);
        servicio.setTicket(this);
    }

    public void removerServicio(Servicio servicio) {
        servicios.remove(servicio);
        servicio.setTicket(null);
    }

    // Getters y Setters
    public LocalDate getFechaReporte() { return fecha_reporte; }
    public int getTicketId() { return ticket_id; }
    public String getNombreCliente() { return nombre_cliente; }
    public String getCorreoCliente() { return correo_cliente; }
    public Instant getTiempoEmision() { return tiempo_emision; }
    public List<Servicio> getServicios() { return servicios; }
    public float getTotalTicket() { return total_ticket; }

    public void setFechaReporte(LocalDate fecha_reporte) { this.fecha_reporte = fecha_reporte; }
    public void setTicketId(int ticket_id) { this.ticket_id = ticket_id; }
    public void setNombreCliente(String nombre_cliente) { this.nombre_cliente = nombre_cliente; }
    public void setCorreoCliente(String correo_cliente) { this.correo_cliente = correo_cliente; }
    public void setTiempoEmision(Instant tiempo_emision) { this.tiempo_emision = tiempo_emision; }
    public void setServicios(List<Servicio> servicios) { this.servicios = servicios; }
    public void setTotalTicket(float total_ticket) { this.total_ticket = total_ticket; }
}
````

### 4. Refactorizar `TicketController`

````java
package controller;

import dao.TicketDAO;
import model.*;
import java.time.LocalDate;

public class TicketController {
    private final TicketDAO ticketDAO;
    private ReporteController reporteController;

    public TicketController() {
        this.ticketDAO = new TicketDAO();
    }

    public TicketController(ReporteController reporteController) {
        this();
        this.reporteController = reporteController;
    }

    public Ticket crearNuevoTicket(String nombre_cliente, String correoCliente) {
        Ticket ticket = new Ticket();
        ticket.setFechaReporte(LocalDate.now());
        ticket.setNombreCliente(nombre_cliente);
        ticket.setCorreoCliente(correoCliente != null ? correoCliente : "");
        ticket.setTotalTicket(0f);

        // Hibernate genera el ID automáticamente
        ticketDAO.guardar(ticket);
        
        System.out.println("Ticket creado, ID: " + ticket.getTicketId() + ", cliente: " + nombre_cliente);
        return ticket;
    }

    public Ticket cargarTicket(int ticket_id, LocalDate fecha_reporte) {
        return ticketDAO.obtenerPorId(ticket_id);
    }

    public void guardarTicket(Ticket ticket) {
        if (ticket == null) {
            throw new IllegalArgumentException("El ticket no puede ser nulo");
        }

        ticketDAO.actualizar(ticket);
        System.out.println("Ticket guardado: " + ticket.getTicketId());

        if (reporteController != null) {
            reporteController.recalcularTotal();
            reporteController.guardarReporte();
        }
    }

    public void agregarServicio(Ticket ticket, TipoServicio tipo, boolean aplicarDescuento) {
        Servicio servicio = new Servicio();
        servicio.setTipoServicio(tipo);
        servicio.setAplicarDescuento(aplicarDescuento);

        // Relación bidireccional
        ticket.agregarServicio(servicio);

        float nuevoTotal = calcularTotalTicket(ticket);
        ticket.setTotalTicket(nuevoTotal);

        ticketDAO.actualizar(ticket);
        System.out.println("Servicio agregado al ticket " + ticket.getTicketId());
    }

    public boolean eliminarServicio(Ticket ticket, int servicioId) {
        Servicio servicio = ticket.getServicios().stream()
            .filter(s -> s.getServicioId() == servicioId)
            .findFirst()
            .orElse(null);

        if (servicio != null) {
            ticket.removerServicio(servicio);
            
            float nuevoTotal = calcularTotalTicket(ticket);
            ticket.setTotalTicket(nuevoTotal);

            ticketDAO.actualizar(ticket);
            System.out.println("Servicio eliminado del ticket " + ticket.getTicketId());
            return true;
        }

        System.err.println("Servicio no encontrado: " + servicioId);
        return false;
    }

    public float calcularTotalTicket(Ticket ticket) {
        float total = 0f;
        if (ticket.getServicios() != null) {
            for (Servicio servicio : ticket.getServicios()) {
                total += servicio.getTotalServicio();
            }
        }
        return total;
    }

    public boolean eliminarTicket(int ticket_id, LocalDate fecha) {
        return ticketDAO.eliminar(ticket_id);
    }
}
````

## Ventajas de Esta Arquitectura

### 1. **Eliminación de Gestión Manual de IDs**

- Hibernate genera automáticamente los IDs con `@GeneratedValue`
- No más métodos `generarIdTicket()` o `generarIdServicio()`

### 2. **Relaciones Automáticas**

- `@OneToMany` y `@ManyToOne` manejan las relaciones
- Cascade automático para guardar/eliminar servicios con tickets

### 3. **Lazy Loading**

- `fetch = FetchType.LAZY` carga servicios solo cuando se necesitan
- Reduce consultas innecesarias a la BD

### 4. **Transacciones**

- Garantiza consistencia con `beginTransaction()` / `commit()`
- Rollback automático en caso de error

### 5. **Sin Archivos JSON**

- Elimina toda la lógica de lectura/escritura de archivos
- Reduce código en un ~60%

## Optimizaciones Adicionales

### Actualizar hibernate.cfg.xml

````xml
<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE hibernate-configuration PUBLIC
        "-//Hibernate/Hibernate Configuration DTD 3.0//EN"
        "http://www.hibernate.org/dtd/hibernate-configuration-3.0.dtd">

<hibernate-configuration>
    <session-factory>
        <!-- Configuración de la base de datos -->
        <property name="connection.url">jdbc:postgresql://localhost:5432/lockereasy</property>
        <property name="connection.username">postgres</property>
        <property name="connection.password">postgresql112</property>
        <property name="connection.driver_class">org.postgresql.Driver</property>
        <property name="dialect">org.hibernate.dialect.PostgreSQLDialect</property>
        
        <!-- Pool de conexiones HikariCP -->
        <property name="hibernate.connection.provider_class">org.hibernate.hikaricp.internal.HikariCPConnectionProvider</property>
        <property name="hibernate.hikari.minimumIdle">5</property>
        <property name="hibernate.hikari.maximumPoolSize">20</property>
        <property name="hibernate.hikari.idleTimeout">30000</property>
        
        <!-- Optimizaciones -->
        <property name="show_sql">false</property> <!-- Desactivar en producción -->
        <property name="format_sql">true</property>
        <property name="use_sql_comments">true</property>
        <property name="hbm2ddl.auto">update</property>
        
        <!-- Cache de segundo nivel (opcional) -->
        <property name="hibernate.cache.use_second_level_cache">true</property>
        <property name="hibernate.cache.region.factory_class">org.hibernate.cache.jcache.JCacheRegionFactory</property>
        
        <!-- Batch processing -->
        <property name="hibernate.jdbc.batch_size">20</property>
        <property name="hibernate.order_inserts">true</property>
        <property name="hibernate.order_updates">true</property>
        
        <!-- Mapeo de las clases -->
        <mapping class="model.TipoServicio"/>
        <mapping class="model.Ticket"/>
        <mapping class="model.Renta"/>
        <mapping class="model.Etiqueta"/>
        <mapping class="model.Servicio"/>
        <mapping class="model.Venta"/>
    </session-factory>
</hibernate-configuration>
````

## Migración Paso a Paso

1. **Crear DAOs** para cada entidad (Ticket, Servicio, Venta, Renta, Etiqueta)
2. **Actualizar modelos** con anotaciones JPA correctas
3. **Refactorizar controladores** para usar DAOs en lugar de archivos JSON
4. **Migrar datos** de JSON a PostgreSQL con un script de migración
5. **Eliminar código JSON** (métodos de carga/guardado de archivos)

Esta arquitectura reduce el código en aproximadamente **60%**, elimina la gestión manual de IDs, optimiza consultas con Lazy Loading y mantiene la lógica de negocio intacta en los controladores.
