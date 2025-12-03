package controller;

import dao.EtiquetaDAO;
import model.Etiqueta;

import java.util.List;

public class EtiquetaController {
    private final EtiquetaDAO etiquetaDAO;

    public EtiquetaController() {
        this.etiquetaDAO = new EtiquetaDAO();
    }

    public boolean agregarEtiqueta(String nombre, boolean afectaInventario) {
        if (nombre == null || nombre.trim().isEmpty()) {
            System.err.println("El nombre de la etiqueta no puede estar vac├¡o");
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
            System.err.println("El nombre de la etiqueta no puede estar vac├¡o");
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
