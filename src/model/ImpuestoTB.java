package model;

import javafx.scene.image.ImageView;

public class ImpuestoTB {

    private int idImpuesto;
    private int operacion;
    private String nombreOperacion;
    private String nombreImpuesto;
    private double valor;
    private boolean predeterminado;
    private String codigoAlterno;
    private boolean sistema;
    private ImageView imagePredeterminado;

    public ImpuestoTB() {
    }

    public ImpuestoTB(int idImpuesto, String nombreImpuesto, Boolean predeterminado) {
        this.idImpuesto = idImpuesto;
        this.nombreImpuesto = nombreImpuesto;
        this.predeterminado = predeterminado;
    }

    public ImpuestoTB(int idImpuesto, String nombreImpuesto, double valor) {
        this.idImpuesto = idImpuesto;
        this.nombreImpuesto = nombreImpuesto;
        this.valor = valor;
    }

    public ImpuestoTB(int idImpuesto, String nombreImpuesto, double valor, boolean predeterminado) {
        this.idImpuesto = idImpuesto;
        this.nombreImpuesto = nombreImpuesto;
        this.valor = valor;
        this.predeterminado = predeterminado;
    }

    public int getIdImpuesto() {
        return idImpuesto;
    }

    public void setIdImpuesto(int idImpuesto) {
        this.idImpuesto = idImpuesto;
    }

    public int getOperacion() {
        return operacion;
    }

    public void setOperacion(int operacion) {
        this.operacion = operacion;
    }

    public String getNombreOperacion() {
        return nombreOperacion;
    }

    public void setNombreOperacion(String nombreOperacion) {
        this.nombreOperacion = nombreOperacion == null ? "" : nombreOperacion;
    }

    public String getNombreImpuesto() {
        return nombreImpuesto;
    }

    public void setNombreImpuesto(String nombreImpuesto) {
        this.nombreImpuesto = nombreImpuesto;
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
        return nombreImpuesto;
    }

}
