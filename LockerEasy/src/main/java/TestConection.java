// Este archivo es solo para probar la conexión a la base de datosss

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class TestConection{
    public static void main(String[] args){
        System.out.println("Prueba de conexión iniciada");

        try {
            SessionFactory factory = new Configuration()
                                    .configure("hibernate.cfg.xml")
                                    .buildSessionFactory();
            
            System.out.println (" Conexion exitosa");
            System.out.println ("Usuario, contraseña y base de dAtos estan correctos");

            factory.close();

            
        }   catch (Exception e){
            System.out.println("Fallo la conexion");
            e.printStackTrace();

        }
    }
}