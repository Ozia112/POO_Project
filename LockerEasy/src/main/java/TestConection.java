// Este archivo es solo para probar la conexión a la base de datos

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.Session;

public class TestConection{
    public static void main(String[] args){
        System.out.println("\n==========Prueba de conexión iniciada===========\n");

        try {
            SessionFactory factory = new Configuration()
                                    .configure("hibernate.cfg.xml")
                                    .buildSessionFactory();
            
            displayEntityDebugInfo(factory);

            factory.close();

            
        }   catch (Exception e){
            System.out.println("\n============Fallo la conexion=============");
            e.printStackTrace();

        }
    }

    private static void displayEntityDebugInfo(SessionFactory factory) {
        System.out.println("\n=======Conexion exitosa==========");
        System.out.println("Usuario, contraseña y base de datos estan correctos\n");
        System.out.println("=== TABLAS CREADAS ===");
        
        for (var entityType : factory.getMetamodel().getEntities()) {
            System.out.println("Tabla: " + entityType.getName());
        }
        
        System.out.println("\n=== RELACIONES ===");
        for (var entityType : factory.getMetamodel().getEntities()) {
            for (var attr : entityType.getAttributes()) {
                if (attr.isAssociation()) {
                    String relationType = "";
                    if (attr instanceof jakarta.persistence.metamodel.ListAttribute || 
                        attr instanceof jakarta.persistence.metamodel.SetAttribute || 
                        attr instanceof jakarta.persistence.metamodel.CollectionAttribute) {
                        
                        // Verificar si es Many-to-Many (tiene @JoinTable)
                        try {
                            var field = entityType.getJavaType().getDeclaredField(attr.getName());
                            if (field.isAnnotationPresent(jakarta.persistence.JoinTable.class)) {
                                relationType = "Many-to-Many";
                            } else {
                                relationType = "One-to-Many";
                            }
                        } catch (Exception e) {
                            relationType = "One-to-Many";
                        }
                    } else {
                        // Es una relación singular (Many-to-One o One-to-One)
                        try {
                            var field = entityType.getJavaType().getDeclaredField(attr.getName());
                            if (field.isAnnotationPresent(jakarta.persistence.OneToOne.class)) {
                                relationType = "One-to-One";
                            } else {
                                relationType = "Many-to-One";
                            }
                        } catch (Exception e) {
                            relationType = "Many-to-One";
                        }
                    }
                    System.out.println(entityType.getName() + " -> " + attr.getName() + " (" + relationType + ")");
                }
            }
        }
        
        System.out.println("\n=== TABLAS PUENTE ===");
        for (var entityType : factory.getMetamodel().getEntities()) {
            try {
                for (var field : entityType.getJavaType().getDeclaredFields()) {
                    if (field.isAnnotationPresent(jakarta.persistence.JoinTable.class)) {
                        var joinTable = field.getAnnotation(jakarta.persistence.JoinTable.class);
                        System.out.println(joinTable.name() + ": " + entityType.getName() + " - " + 
                                        field.getType().getSimpleName());
                    }
                }
            } catch (Exception e) {
                // Ignorar
            }
        }
        System.out.println();
    }
}