package controller;

import model.Etiqueta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;

public class EtiquetaController {
    private static final String ETIQUETAS_FILE = "LockerEasy/src/main/resources/data/catalogo/etiquetas.json";
    private Map<Integer, Etiqueta> etiquetas;
    private int contadorIds;

    public EtiquetaController() {
        this.etiquetas = new HashMap<>();
        cargarEtiquetas();
    }

    public void cargarEtiquetas() {
        etiquetas.clear();
        int maxId = 0;
        try {
            if (!Files.exists(Paths.get(ETIQUETAS_FILE))) {
                crearArchivoVacio();
                return;
            }
            
            String content = new String(Files.readAllBytes(Paths.get(ETIQUETAS_FILE)));
            JSONArray arr = new JSONArray(content);

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                int id = obj.has("id") ? obj.getInt("id") : (i + 1);
                String nombre = obj.getString("nombre");
                boolean afectaInventario = obj.getBoolean("afecta_inventario");

                Etiqueta etiqueta = new Etiqueta(id, nombre, afectaInventario);
                etiquetas.put(id, etiqueta);

                if (id > maxId) maxId = id;
            }

            contadorIds = maxId + 1;
            System.out.println("Etiquetas cargadas: " + etiquetas.size());

        } catch (Exception e) {
            System.err.println("Error al cargar etiquetas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean agregarEtiqueta(String nombre, boolean afectaInventario) {
        if (nombre == null || nombre.trim().isEmpty()) {
            System.err.println("El nombre de la etiqueta no puede estar vacío");
            return false;
        }

        for (Etiqueta e : etiquetas.values()) {
            if (e.getNombre().equalsIgnoreCase(nombre)) {
                System.err.println("La etiqueta '" + nombre + "' ya existe");
                return false;
            }
        }

        int nuevoId = contadorIds++;
        Etiqueta nueva = new Etiqueta(nuevoId, nombre, afectaInventario);
        etiquetas.put(nuevoId, nueva);
        guardarEtiquetas();

        System.out.println("Etiqueta agregada: " + nombre + " (ID: " + nuevoId + ")");
        return true;
    }

    public boolean actualizarEtiqueta(int id, String nuevoNombre, boolean afectaInventario) {
        if (!etiquetas.containsKey(id)) {
            System.err.println("Etiqueta no encontrada: ID " + id);
            return false;
        }

        Etiqueta etiqueta = etiquetas.get(id);
        String antiguo_nombre = etiqueta.getNombre();
        boolean afectaba_inventario = etiqueta.isAfectaInventario();
        etiqueta.setNombre(nuevoNombre);
        etiqueta.setAfectaInventario(afectaInventario);
        guardarEtiquetas();

        if (antiguo_nombre.equals(nuevoNombre) && afectaba_inventario == afectaInventario) {
            System.out.println("No hubo cambios en la etiqueta ID " + id);
            return false;
        } else {
            String mensajeNombre = antiguo_nombre.equals(nuevoNombre) ? "" : "Nombre cambiado, Antes: '" + antiguo_nombre + " Ahora: " + nuevoNombre + ".";
            String mensajeAfecta = afectaba_inventario == afectaInventario ? "" : "Afecta inventario cambiado, Antes: '" + afectaba_inventario + " Ahora: " + afectaInventario + ".";
            System.out.println("Etiqueta actualizada: ID " + id);
            System.out.println(mensajeNombre + "\n" + mensajeAfecta);
        }
        
        return true;
    }

    public boolean eliminarEtiqueta(int id) {
        if (!etiquetas.containsKey(id)) {
            System.err.println("Etiqueta no encontrada: ID " + id);
            return false;
        }

        etiquetas.remove(id);
        guardarEtiquetas();

        System.out.println("Etiqueta eliminada: ID " + id);
        return true;
    }

    public boolean eliminarEtiqueta(String nombre) {
        Integer idAEliminar = null;
        
        for (Map.Entry<Integer, Etiqueta> entry : etiquetas.entrySet()) {
            if (entry.getValue().getNombre().equalsIgnoreCase(nombre)) {
                idAEliminar = entry.getKey();
                break;
            }
        }

        if (idAEliminar != null) {
            return eliminarEtiqueta(idAEliminar);
        }
        
        System.err.println("Etiqueta no encontrada: " + nombre);
        return false;
    }

    public Etiqueta obteneretiquetaPorId(int id) {
        return etiquetas.get(id);
    }

    public Etiqueta obtenerEtiquetaPorNombre(String nombre) {
        for (Etiqueta e : etiquetas.values()) {
            if (e.getNombre().equalsIgnoreCase(nombre)) {
                return e;
            }
        }
        return null;
    }

    public List<Etiqueta> obtenerTodasEtiquetas() {
        return new ArrayList<>(etiquetas.values());
    }

    public boolean etiquetasAfectanInventario(List<String> nombresEtiquetas) {
        if (nombresEtiquetas == null || nombresEtiquetas.isEmpty()) {
            return true;
        }

        for (String nombre : nombresEtiquetas) {
            Etiqueta etiqueta = obtenerEtiquetaPorNombre(nombre);
            if (etiqueta != null && !etiqueta.isAfectaInventario()) {
                return false;
            }
        }
        return true;
    }

    private void guardarEtiquetas() {
        try {
            Files.createDirectories(Paths.get(ETIQUETAS_FILE).getParent());
            
            JSONArray arr = new JSONArray();
            for (Etiqueta e : etiquetas.values()) {
                JSONObject obj = new JSONObject();
                obj.put("id", e.getEtiquetaId());
                obj.put("nombre", e.getNombre());
                obj.put("afecta_inventario", e.isAfectaInventario());
                arr.put(obj);
            }

            Files.write(Paths.get(ETIQUETAS_FILE), arr.toString(2).getBytes());
            System.out.println("Etiquetas guardadas correctamente.");
        } catch (Exception e) {
            System.err.println("Error al guardar etiquetas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void crearArchivoVacio() {
        try {
            Files.createDirectories(Paths.get(ETIQUETAS_FILE).getParent());
            JSONArray arr = new JSONArray();
            Files.write(Paths.get(ETIQUETAS_FILE), arr.toString(2).getBytes());
            System.out.println("Archivo de etiquetas creado.");
        } catch (Exception e) {
            System.err.println("Error al crear archivo de etiquetas: " + e.getMessage());
            e.printStackTrace();
        }
    }
}