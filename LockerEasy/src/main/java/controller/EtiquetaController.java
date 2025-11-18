package controller;

import org.json.JSONArray;
import org.json.JSONObject;

import model.Etiqueta;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class EtiquetaController {
    private static final String ETIQUETAS_FILE = "LockerEasy/src/main/resources/data/etiquetas.json";

    public static List<Etiqueta> cargarEtiquetas() {
        List<Etiqueta> etiquetas = new ArrayList<>();
        try {
            String content = new String(Files.readAllBytes(Paths.get(ETIQUETAS_FILE)));
            JSONArray arr = new JSONArray(content);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                etiquetas.add(new Etiqueta(
                    obj.getString("nombre"),
                    obj.getBoolean("afecta_inventario")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return etiquetas;
    }

    public static void agregarEtiqueta(Etiqueta nueva) {
        List<Etiqueta> etiquetas = cargarEtiquetas();
        etiquetas.add(nueva);
        guardarEtiquetas(etiquetas);
    }

    public static void eliminarEtiqueta(String nombre) {
        List<Etiqueta> etiquetas = cargarEtiquetas();
        etiquetas.removeIf(e -> e.getNombre().equalsIgnoreCase(nombre));
        guardarEtiquetas(etiquetas);
    }

    public static void guardarEtiquetas(List<Etiqueta> etiquetas) {
        JSONArray arr = new JSONArray();
        for (Etiqueta e : etiquetas) {
            JSONObject obj = new JSONObject();
            obj.put("nombre", e.getNombre());
            obj.put("afecta_inventario", e.isAfectaInventario());
            arr.put(obj);
        }
        try {
            Files.write(Paths.get(ETIQUETAS_FILE), arr.toString(2).getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean etiquetasAfectanInventario(List<String> etiquetasProducto) {
        List<Etiqueta> etiquetas = cargarEtiquetas();
        for (String nombre : etiquetasProducto) {
            for (Etiqueta e : etiquetas) {
                if (e.getNombre().equalsIgnoreCase(nombre)) {
                    if (!e.isAfectaInventario()) return false;
                }
            }
        }
        return true;
    }
}