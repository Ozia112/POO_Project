package model;

import jakarta.persistence.*;

@Entity
@Table(name = "configuracion")
public class Configuracion {
    
    @Id
    @Column(name = "clave", length = 50)
    private String clave;
    
    @Column(name = "valor", nullable = false)
    private String valor;
    
    @Column(name = "descripcion")
    private String descripcion;
    
    public Configuracion() {}
    
    public Configuracion(String clave, String valor, String descripcion) {
        this.clave = clave;
        this.valor = valor;
        this.descripcion = descripcion;
    }
    
    // Getters
    public String getClave() { return clave; }
    public String getValor() { return valor; }
    public String getDescripcion() { return descripcion; }
    
    // Setters
    public void setClave(String clave) { this.clave = clave; }
    public void setValor(String valor) { this.valor = valor; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    // Métodos de conveniencia para obtener valores tipados
    public int getValorInt() {
        return Integer.parseInt(valor);
    }
    
    public float getValorFloat() {
        return Float.parseFloat(valor);
    }
    
    public boolean getValorBoolean() {
        return Boolean.parseBoolean(valor);
    }
}
