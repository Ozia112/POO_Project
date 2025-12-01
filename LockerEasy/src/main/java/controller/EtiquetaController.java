package controller;

import dao.EtiquetaDAO;
import model.Etiqueta;

import java.util.List;

public class EtiquetaController {
    private final EtiquetaDAO etiquetaDAO;

    public EtiquetaController() {
<<<<<<< HEAD
        this.etiquetas = new HashMap<>();
        this.cargarEtiquetasInternas();
    }

    private void cargarEtiquetasInternas() {
        cargarEtiquetas();
    }

    public final void cargarEtiquetas() {
        etiquetas.clear();
        int maxId = 0;
        try {
            if (!Files.exists(Paths.get(ETIQUETAS_FILE))) {
                crearArchivoVacio();
                return;
            }
            
            String content = new String(Files.readAllBytes(Paths.get(ETIQUETAS_FILE)), "UTF-8");
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

        } catch (java.io.IOException | org.json.JSONException e) {
            System.err.println("Error al cargar etiquetas: " + e.getMessage());
        }
=======
        this.etiquetaDAO = new EtiquetaDAO();
>>>>>>> temp.TM-01.Design.DATABASE-WIP
    }

    public boolean agregarEtiqueta(String nombre, boolean afectaInventario) {
        if (nombre == null || nombre.trim().isEmpty()) {
            System.err.println("El nombre de la etiqueta no puede estar vacío");
            return false;
        }

        Etiqueta etiqueta = new Etiqueta(nombre, afectaInventario);
        try {
            etiquetaDAO.guardar(etiqueta);
            System.out.println("Etiqueta agregada: " + nombre);
            return true;
        } catch (Exception e) {
            System.err.println("Error al guardar etiqueta: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarEtiqueta(Long id, String nuevoNombre, boolean afectaInventario) {
        if (nuevoNombre == null || nuevoNombre.trim().isEmpty()) {
            System.err.println("El nombre de la etiqueta no puede estar vacío");
            return false;
        }

        Etiqueta etiqueta = etiquetaDAO.obtener(id);
        if (etiqueta == null) {
            System.err.println("Etiqueta no encontrada: ID " + id);
            return false;
        }

        etiqueta.setNombre(nuevoNombre);
        etiqueta.setAfectaInventario(afectaInventario);
        try {
            etiquetaDAO.actualizar(etiqueta);
            System.out.println("Etiqueta actualizada: ID " + id);
            return true;
        } catch (Exception e) {
            System.err.println("Error al actualizar etiqueta: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarEtiqueta(Etiqueta etiqueta) {
        try {
            etiquetaDAO.eliminar(etiqueta.getEtiquetaId());
            System.out.println("Etiqueta eliminada: " + etiqueta.getNombre());
            return true;
        } catch (Exception e) {
            System.err.println("Error al eliminar etiqueta: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarEtiqueta(String nombre) {
        Long idAEliminar = null;
        
        List<Etiqueta> todasEtiquetas = etiquetaDAO.obtenerTodas();
        for (Etiqueta e : todasEtiquetas) {
            if (e.getNombre().equalsIgnoreCase(nombre)) {
                idAEliminar = e.getEtiquetaId();
                break;
            }
        }
        if (idAEliminar == null) {
            System.err.println("Etiqueta no encontrada: " + nombre);
            return false;
        }
        return eliminarEtiqueta(etiquetaDAO.obtener(idAEliminar));
    }

    public EtiquetaDAO getEtiquetaDAO() {
        return etiquetaDAO;
    }
}