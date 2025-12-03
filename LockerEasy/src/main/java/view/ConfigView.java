package view;

import controller.*;
import javafx.geometry.Insets;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;

public class ConfigView {
    
    private VBox root;
    private TabPane subTabs;
    
    private ConfigVentaGUI configVentaGUI;
    private ConfigEtiquetasGUI configEtiquetasGUI;
    private ConfigRentaGUI configRentaGUI;
    private ConfigCuentaGUI configCuentaGUI;
    
    private Tab tabVenta;
    private Tab tabEtiquetas;
    private Tab tabRenta;
    private Tab tabCuenta;

    public ConfigView(
        VentaController ventaController,
        EtiquetaController etiquetaController,
        RentaController rentaController,
        InventarioController inventarioController
    ) {
        construirVista(ventaController, etiquetaController, rentaController, inventarioController);
    }

    private void construirVista(
        VentaController ventaController,
        EtiquetaController etiquetaController,
        RentaController rentaController,
        InventarioController inventarioController
    ) {
        subTabs = new TabPane();
        subTabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        
        // Tab de Configuración de Venta (Productos)
        configVentaGUI = new ConfigVentaGUI(ventaController, inventarioController);
        configVentaGUI.setOnDataChanged(this::actualizarIndicadoresAdvertencia);
        tabVenta = new Tab("Productos", configVentaGUI.getVistaIntegrada());
        tabVenta.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
        
        // Tab de Configuración de Etiquetas
        configEtiquetasGUI = new ConfigEtiquetasGUI(etiquetaController);
        configEtiquetasGUI.setOnDataChanged(this::actualizarIndicadoresAdvertencia);
        tabEtiquetas = new Tab("Etiquetas", configEtiquetasGUI.getVistaIntegrada());
        tabEtiquetas.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
        
        // Tab de Configuración de Renta (Ubicaciones y Torres)
        configRentaGUI = new ConfigRentaGUI(rentaController);
        configRentaGUI.setOnDataChanged(this::actualizarIndicadoresAdvertencia);
        tabRenta = new Tab("Torres y Lockers", configRentaGUI.getVistaIntegrada());
        tabRenta.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
        
        // Tab de Configuración de Cuenta (Base de Datos)
        configCuentaGUI = new ConfigCuentaGUI();
        configCuentaGUI.setOnDataChanged(this::actualizarIndicadoresAdvertencia);
        tabCuenta = new Tab("🔐 Cuenta", configCuentaGUI.getVistaIntegrada());
        tabCuenta.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
        
        subTabs.getTabs().addAll(tabVenta, tabEtiquetas, tabRenta, tabCuenta);
        
        // Agregar scroll para la pestaña de Torres y Lockers
        subTabs.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab == tabRenta) {
                // Asegurar que el contenido de ConfigRentaGUI sea scrollable
                if (configRentaGUI != null) {
                    configRentaGUI.actualizarTablas();
                }
            }
            if (newTab == tabCuenta) {
                // Actualizar lista de perfiles al entrar
                if (configCuentaGUI != null) {
                    configCuentaGUI.actualizarLista();
                }
            }
            // Actualizar indicadores cuando cambie de pestaña
            actualizarIndicadoresAdvertencia();
        });
        
        root = new VBox(subTabs);
        root.setPadding(new Insets(10));
        VBox.setVgrow(subTabs, javafx.scene.layout.Priority.ALWAYS);
        
        // Actualizar indicadores al inicio
        actualizarIndicadoresAdvertencia();
    }

    public VBox getView() {
        return root;
    }
    
    /**
     * Actualizar vistas de configuración cuando se cambie a esta pestaña
     */
    public void actualizarVistas() {
        if (configVentaGUI != null) {
            configVentaGUI.actualizarTablas();
        }
        if (configEtiquetasGUI != null) {
            configEtiquetasGUI.actualizarTabla();
        }
        if (configRentaGUI != null) {
            configRentaGUI.actualizarTablas();
        }
        if (configCuentaGUI != null) {
            configCuentaGUI.actualizarLista();
        }
        actualizarIndicadoresAdvertencia();
    }
    
    /**
     * Actualiza los indicadores de advertencia (⚠️) en las pestañas vacías
     */
    public void actualizarIndicadoresAdvertencia() {
        // Etiquetas
        if (configEtiquetasGUI != null && !configEtiquetasGUI.tieneEtiquetas()) {
            tabEtiquetas.setText("⚠️ Etiquetas");
            tabEtiquetas.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #FF9800;");
        } else {
            tabEtiquetas.setText("Etiquetas");
            tabEtiquetas.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
        }
        
        // Productos
        if (configVentaGUI != null && !configVentaGUI.tieneProductos()) {
            tabVenta.setText("⚠️ Productos");
            tabVenta.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #FF9800;");
        } else {
            tabVenta.setText("Productos");
            tabVenta.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
        }
        
        // Lockers
        if (configRentaGUI != null && !configRentaGUI.tieneLockers()) {
            tabRenta.setText("⚠️ Torres y Lockers");
            tabRenta.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #FF9800;");
        } else {
            tabRenta.setText("Torres y Lockers");
            tabRenta.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
        }
        
        // Cuenta no necesita indicador de advertencia
    }
    
    /**
     * Verifica si la aplicación necesita configuración inicial
     */
    public boolean necesitaConfiguracionInicial() {
        return !configRentaGUI.tieneLockers();
    }
    
    /**
     * Selecciona la pestaña de Torres y Lockers para configuración inicial
     */
    public void irAConfiguracionLockers() {
        subTabs.getSelectionModel().select(tabRenta);
    }
}