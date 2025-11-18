Ozia112: Quiero que el usuario pueda agregar objetos de #file:Venta.java a voluntad con metodos desde la clase #file:Config.java ya que esta sera la encargada de modificar valores como nombres, categorias, precio y cantidad desde una interfaz. entonces necesito crear un json o una tabla sql para almacenar los objetos a vender(primero un json para las pruebas) y conectarlo a #file:VentaController.java que va a inicializar cada objeto a vender y administrara la resta de ejemplares en stock en automatico, el calculo de cantidad siendo vendida y su obtener su precio desde config

GitHub Copilot: Para lograr esto, sigue estos pasos:

### 1. Crear un archivo JSON para almacenar los productos

Por ejemplo, crea `productos.json` en la carpeta `LockerEasy\src\data`:

````json
[
    {
        "nombre": "Candado",
        "precio": 30.0,
        "cantidad": 10
    },
    {
        "nombre": "Botella de agua",
        "precio": 15.0,
        "cantidad": 20
    }
]
````

---

### 2. Agrega métodos en Config.java para manejar productos

Utiliza la librería `org.json` o Jackson para leer/escribir el JSON. Ejemplo usando org.json:

````java
// ...existing code...
import org.json.JSONArray;
import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import model.Venta;

public class Config {
    // ...existing code...

    private static final String PRODUCTOS_PATH = "LockerEasy/src/data/productos.json";

    public static List<Venta> cargarProductos() {
        List<Venta> productos = new ArrayList<>();
        try {
            String content = new String(Files.readAllBytes(Paths.get(PRODUCTOS_PATH)));
            JSONArray arr = new JSONArray(content);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                productos.add(new Venta(
                    obj.getString("nombre"),
                    (float)obj.getDouble("precio"),
                    obj.getInt("cantidad")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return productos;
    }

    public static void guardarProductos(List<Venta> productos) {
        JSONArray arr = new JSONArray();
        for (Venta v : productos) {
            JSONObject obj = new JSONObject();
            obj.put("nombre", v.getNombre());
            obj.put("precio", v.getPrecio());
            obj.put("cantidad", v.getCantidad());
            arr.put(obj);
        }
        try {
            Files.write(Paths.get(PRODUCTOS_PATH), arr.toString(2).getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // ...existing code...
}
````

---

### 3. Modifica VentaController.java para inicializar desde JSON

````java
// ...existing code...
    public VentaController() {
        this.listaVentas = Config.cargarProductos();
    }
// ...existing code...

    public void guardarCambios() {
        Config.guardarProductos(listaVentas);
    }
// ...existing code...

    public boolean venderProducto(String nombre, int cantidadVendida) {
        Venta venta = getVentaByNombre(nombre);
        if (venta != null && venta.getCantidad() >= cantidadVendida) {
            venta.setCantidad(venta.getCantidad() - cantidadVendida);
            guardarCambios();
            return true;
        }
        return false;
    }
// ...existing code...
````

---

### 4. Agrega métodos en Config.java para modificar productos desde la interfaz

````java
// ...existing code...
    public static void agregarProducto(Venta nuevo) {
        List<Venta> productos = cargarProductos();
        productos.add(nuevo);
        guardarProductos(productos);
    }

    public static void actualizarProducto(String nombre, String nuevoNombre, float nuevoPrecio, int nuevaCantidad) {
        List<Venta> productos = cargarProductos();
        for (Venta v : productos) {
            if (v.getNombre().equalsIgnoreCase(nombre)) {
                v.setNombre(nuevoNombre);
                v.setPrecio(nuevoPrecio);
                v.setCantidad(nuevaCantidad);
                break;
            }
        }
        guardarProductos(productos);
    }

    public static void eliminarProducto(String nombre) {
        List<Venta> productos = cargarProductos();
        productos.removeIf(v -> v.getNombre().equalsIgnoreCase(nombre));
        guardarProductos(productos);
    }
// ...existing code...
````

---

**Resumen:**  
- Los productos se almacenan en un JSON.  
- `Config` administra la lectura/escritura del JSON.  
- `VentaController` inicializa la lista desde `Config` y administra ventas/resta de stock.  
- Puedes modificar productos desde la interfaz usando métodos de `Config`.  

¿Quieres el ejemplo de cómo usar estos métodos desde la interfaz?