
package model;

import javafx.scene.image.ImageView;


public class ImpuestoTB {
    
    private int idImpuesto;
    private String nombre;
    private double valor;
    private boolean predeterminado;
    private String codigoAlterno;
    private boolean sistema;
    private ImageView imagePredeterminado;
    
    public ImpuestoTB(){
    }

    public ImpuestoTB(int idImpuesto, String nombre, Boolean predeterminado) {
        this.idImpuesto = idImpuesto;
        this.nombre = nombre;
        this.predeterminado = predeterminado;
    }

    public ImpuestoTB(int idImpuesto, String nombre, double valor) {
        this.idImpuesto = idImpuesto;
        this.nombre = nombre;
        this.valor = valor;
    }    

    public ImpuestoTB(int idImpuesto, String nombre, double valor, boolean predeterminado) {
        this.idImpuesto = idImpuesto;
        this.nombre = nombre;
        this.valor = valor;
        this.predeterminado = predeterminado;
    }
    
    
    
    public ImpuestoTB(int idImpuesto, String nombre, double valor, boolean predeterminado, String codigoAlterno, ImageView imagePredeterminado) {
        this.idImpuesto = idImpuesto;
        this.nombre = nombre;
        this.valor = valor;
        this.predeterminado = predeterminado;
        this.codigoAlterno = codigoAlterno;
        this.imagePredeterminado = imagePredeterminado;
    }

    public int getIdImpuesto() {
        return idImpuesto;
    }

    public void setIdImpuesto(int idImpuesto) {
        this.idImpuesto = idImpuesto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public boolean getPredeterminado() {
        return predeterminado;
    }

    public void setPredeterminado(boolean predeterminado) {
        this.predeterminado = predeterminado;
    }

    public String getCodigoAlterno() {
        return codigoAlterno;
    }

    public void setCodigoAlterno(String codigoAlterno) {
        this.codigoAlterno = codigoAlterno;
    }

    public ImageView getImagePredeterminado() {
        return imagePredeterminado;
    }

    public void setImagePredeterminado(ImageView imagePredeterminado) {
        this.imagePredeterminado = imagePredeterminado;
    }

    public boolean isSistema() {
        return sistema;
    }

    public void setSistema(boolean sistema) {
        this.sistema = sistema;
    }
    
    

    @Override
    public String toString() {
        return nombre;
    }
    
    
           
}
