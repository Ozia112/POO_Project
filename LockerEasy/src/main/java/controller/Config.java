package controller;

public class Config {
    // Configuraciones renta
    private static float precio_hora_locker = 50.0f;
    private static int minutos_tolerancia = 15;
    private static int minutos_cancelacion = 5;
    private static float descuento_unico = 0.0f;

    public static float getPrecioHoraLocker() { return precio_hora_locker; }
    public static int getMinutosTolerancia() { return minutos_tolerancia; }
    public static int getMinutosCancelacion() { return minutos_cancelacion; }
    public static float getDescuentoUnico() { return descuento_unico; }

    public static void setPrecioHoraLocker(float precio) { precio_hora_locker = precio; }
    public static void setMinutosTolerancia(int minutos) { minutos_tolerancia = minutos; }
    public static void setMinutosCancelacion(int minutos) { minutos_cancelacion = minutos; }
    public static void setDescuentoUnico(float descuento) { descuento_unico = descuento; }
<<<<<<< HEAD

    /**
     * Metodo que convierte diferentes tiupos de objetos a LinkedHashMap.
     * @param objeto El objeto a convertir (puede ser List<Servicio>, Renta, Venta)
     * @return LinkedHashMap con las propiedades del objeto o null si el tipo no es soportado
     */
    @SuppressWarnings("unchecked")
    public static Object convertirAMap(Object objeto) {
        if (objeto == null) {
            return null;
        }

        // Manejar listas
        if (objeto instanceof List<?>) {
            List<?> lista = (List<?>) objeto;
            if (lista.isEmpty()) {
                return new ArrayList<>();
            }
            
            if (lista.get(0) instanceof Servicio) {
                List<LinkedHashMap<String, Object>> resultado = new ArrayList<>();
                for (Servicio servicio : (List<Servicio>) lista) {
                    LinkedHashMap<String, Object> servicioMap = new LinkedHashMap<>();
                    servicioMap.put("id", servicio.getServicioId());
                    servicioMap.put("tipo_servicio", servicio.getTipoServicio().getClass().getSimpleName());
                    servicioMap.put("descuento", servicio.isAplicarDescuento());
                    servicioMap.put("total_servicio", servicio.getTotalServicio());
                    
                    // Recursión: convertir el tipo de servicio (Renta o Venta)
                    TipoServicio tipo = servicio.getTipoServicio();
                    if (tipo instanceof Renta) {
                        servicioMap.put("renta_properties", convertirAMap(tipo));
                    } else if (tipo instanceof Venta) {
                        servicioMap.put("venta_properties", convertirAMap(tipo));
                    }
                    
                    resultado.add(servicioMap);
                }
                return resultado;
            } else if (lista.get(0) instanceof String) {
                return lista;
            } else {
                return lista;
            }
        }
        
        // Manejar Renta
        if (objeto instanceof Renta) {
            Renta renta = (Renta) objeto;
            LinkedHashMap<String, Object> map = new LinkedHashMap<>();
            map.put("nombre", renta.getNombre());
            map.put("precio", renta.getPrecio());
            map.put("inicio_renta", renta.getInicioRenta().toString());
            map.put("cierre_renta", renta.getCierreRenta() != null ?
                            renta.getCierreRenta().toString() : "--:--:--");
            map.put("cantidad", renta.getCantidad());
            map.put("isActive", renta.getStateOcupado());
            map.put("ubicacion", renta.getUbicacion().name());
            return map;
        }

        // Manejar Venta
        if (objeto instanceof Venta) {
            Venta venta = (Venta) objeto;
            LinkedHashMap<String, Object> map = new LinkedHashMap<>();
            map.put("id_producto", venta.getIdProducto());
            map.put("nombre", venta.getNombre());
            map.put("precio", venta.getPrecio());
            map.put("cantidad", venta.getCantidad());
            if (venta.getEtiquetas() != null) {
                map.put("etiquetas", venta.getEtiquetas());
            }
            return map;
        }
        
        // Caso default: devolver el objeto tal cual
        System.err.println("Tipo de objeto no soportado para conversión: " + 
                         (objeto != null ? objeto.getClass().getName() : "null"));
        return objeto;
    }
=======
>>>>>>> temp.TM-01.Design.DATABASE-WIP
}