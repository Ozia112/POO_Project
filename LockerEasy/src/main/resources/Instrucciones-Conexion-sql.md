# LockerEasy - Configuración

## Configurar la base de datos

1. Copia `hibernate.cfg.xml.example` a `hibernate.cfg.xml`
2. Edita `hibernate.cfg.xml` con tus credenciales:
   - `connection.username`: tu usuario de PostgreSQL
   - `connection.password`: tu contraseña
   - `connection.url`: ajusta el nombre de la base de datos si es necesario

3. Crea la base de datos en PostgreSQL:

   ```sql
   CREATE DATABASE lockereasy;
   ```

4. Ejecuta `TestConection.java` para verificar la conexión
