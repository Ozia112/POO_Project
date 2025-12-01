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
}
