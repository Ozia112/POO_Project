package backend.controllers;

import backend.model.*;
import java.util.*;

/**
 * Controller para gestionar descuentos y promociones
 * Cumple con FR3
 */
public class DescuentoController {
    private Map<String, DescuentoImpl> descuentosDisponibles;

    public DescuentoController() {
        this.descuentosDisponibles = new HashMap<>();
        inicializarDescuentos();
    }

    // Clase interna para implementar la interfaz Descuento
    private static class DescuentoImpl implements Descuento {
        private String nombre;
        private float porcentaje;

        public DescuentoImpl(String nombre, float porcentaje) {
            this.nombre = nombre;
            this.porcentaje = porcentaje;
        }

        @Override
        public String getName() {
            return nombre;
        }

        @Override
        public float getPorcentaje() {
            return porcentaje;
        }
    }

    // Inicializa descuentos de ejemplo
    private void inicializarDescuentos() {
        descuentosDisponibles.put("ESTUDIANTE", new DescuentoImpl("Descuento Estudiante", 15.0f));
        descuentosDisponibles.put("CLIENTE_FRECUENTE", new DescuentoImpl("Cliente Frecuente", 20.0f));
        descuentosDisponibles.put("PRIMERA_COMPRA", new DescuentoImpl("Primera Compra", 10.0f));
        descuentosDisponibles.put("PROMO_VERANO", new DescuentoImpl("Promoción de Verano", 25.0f));
    }

    /**
     * FR3: Aplicar un descuento a un servicio
     * @param servicio Servicio al que aplicar el descuento
     * @param codigoDescuento Código del descuento
     * @return El servicio con descuento aplicado o null si falla
     */
    public Servicio aplicarDescuento(Servicio servicio, String codigoDescuento) {
        if (servicio == null) {
            System.out.println("Error: Servicio no válido.");
            return null;
        }

        Descuento descuento = descuentosDisponibles.get(codigoDescuento.toUpperCase());
        if (descuento == null) {
            System.out.println("Error: Código de descuento '" + codigoDescuento + "' no válido.");
            return null;
        }

        // Calcular el monto original
        float montoOriginal = servicio.getTotal();
        
        // Aplicar descuento
        float montoConDescuento = descuento.aplicar(montoOriginal);
        
        System.out.println("Descuento aplicado exitosamente:");
        System.out.println("- Descuento: " + descuento.getName());
        System.out.println("- Porcentaje: " + descuento.getPorcentaje() + "%");
        System.out.println("- Monto original: $" + montoOriginal);
        System.out.println("- Ahorro: $" + (montoOriginal - montoConDescuento));
        System.out.println("- Total con descuento: $" + montoConDescuento);

        // Crear nuevo servicio con descuento
        Servicio servicioConDescuento = new Servicio(
            servicio.getId(),
            servicio.getTipoServicio(),
            descuento,
            montoConDescuento
        );

        return servicioConDescuento;
    }

    /**
     * Crear un nuevo descuento/promoción
     * @param codigo Código del descuento
     * @param nombre Nombre descriptivo
     * @param porcentaje Porcentaje de descuento (0-100)
     * @return true si se creó correctamente
     */
    public boolean crearDescuento(String codigo, String nombre, float porcentaje) {
        if (porcentaje < 0 || porcentaje > 100) {
            System.out.println("Error: El porcentaje debe estar entre 0 y 100.");
            return false;
        }

        if (descuentosDisponibles.containsKey(codigo.toUpperCase())) {
            System.out.println("Error: El código '" + codigo + "' ya existe.");
            return false;
        }

        descuentosDisponibles.put(codigo.toUpperCase(), new DescuentoImpl(nombre, porcentaje));
        System.out.println("Descuento creado exitosamente:");
        System.out.println("- Código: " + codigo);
        System.out.println("- Nombre: " + nombre);
        System.out.println("- Porcentaje: " + porcentaje + "%");
        
        return true;
    }

    /**
     * Actualizar un descuento existente
     * @param codigo Código del descuento
     * @param nuevoNombre Nuevo nombre
     * @param nuevoPorcentaje Nuevo porcentaje
     * @return true si se actualizó correctamente
     */
    public boolean actualizarDescuento(String codigo, String nuevoNombre, float nuevoPorcentaje) {
        if (nuevoPorcentaje < 0 || nuevoPorcentaje > 100) {
            System.out.println("Error: El porcentaje debe estar entre 0 y 100.");
            return false;
        }

        if (!descuentosDisponibles.containsKey(codigo.toUpperCase())) {
            System.out.println("Error: El código '" + codigo + "' no existe.");
            return false;
        }

        descuentosDisponibles.put(codigo.toUpperCase(), new DescuentoImpl(nuevoNombre, nuevoPorcentaje));
        System.out.println("Descuento actualizado exitosamente.");
        
        return true;
    }

    /**
     * Eliminar un descuento
     * @param codigo Código del descuento
     * @return true si se eliminó correctamente
     */
    public boolean eliminarDescuento(String codigo) {
        if (descuentosDisponibles.remove(codigo.toUpperCase()) != null) {
            System.out.println("Descuento '" + codigo + "' eliminado.");
            return true;
        }
        System.out.println("Error: Descuento no encontrado.");
        return false;
    }

    /**
     * Obtener un descuento por código
     * @param codigo Código del descuento
     * @return El descuento o null si no existe
     */
    public Descuento obtenerDescuento(String codigo) {
        return descuentosDisponibles.get(codigo.toUpperCase());
    }

    /**
     * Obtener todos los descuentos disponibles
     * @return Mapa de códigos y descuentos
     */
    public Map<String, ? extends Descuento> obtenerDescuentosDisponibles() {
        return new HashMap<>(descuentosDisponibles);
    }

    /**
     * Verificar si un código de descuento es válido
     * @param codigo Código a verificar
     * @return true si es válido
     */
    public boolean validarCodigo(String codigo) {
        return descuentosDisponibles.containsKey(codigo.toUpperCase());
    }

    /**
     * Mostrar todos los descuentos disponibles
     */
    public void mostrarDescuentos() {
        System.out.println("\n=== DESCUENTOS DISPONIBLES ===");
        if (descuentosDisponibles.isEmpty()) {
            System.out.println("No hay descuentos disponibles.");
            return;
        }
        
        descuentosDisponibles.forEach((codigo, descuento) -> {
            System.out.println("- Código: " + codigo);
            System.out.println("  Nombre: " + descuento.getName());
            System.out.println("  Descuento: " + descuento.getPorcentaje() + "%");
            System.out.println();
        });
    }
}
