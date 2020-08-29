package model;

import javafx.scene.image.ImageView;

public class TipoDocumentoTB {
    
    private int id;
    private int idTipoDocumento;
    private String nombre;
    private String serie;
    private boolean predeterminado;
    private boolean sistema;
    private String codigoAlterno;
    private ImageView imagePredeterminado;

    public TipoDocumentoTB() {
        
    }

    public TipoDocumentoTB(int idTipoDocumento, String nombre) {
        this.idTipoDocumento = idTipoDocumento;
        this.nombre = nombre;
    }
    
    public TipoDocumentoTB(int idTipoDocumento, String nombre, boolean predeterminado) {
        this.idTipoDocumento = idTipoDocumento;
        this.nombre = nombre;
        this.predeterminado = predeterminado;
    }

    public TipoDocumentoTB(int idTipoDocumento, String nombre, boolean predeterminado, ImageView imagePredeterminado) {
        this.idTipoDocumento = idTipoDocumento;
        this.nombre = nombre;
        this.predeterminado = predeterminado;
        this.imagePredeterminado = imagePredeterminado;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdTipoDocumento() {
        return idTipoDocumento;
    }

    public void setIdTipoDocumento(int idTipoDocumento) {
        this.idTipoDocumento = idTipoDocumento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public boolean isPredeterminado() {
        return predeterminado;
    }

    public void setPredeterminado(boolean predeterminado) {
        this.predeterminado = predeterminado;
    }

    public boolean isSistema() {
        return sistema;
    }

    public void setSistema(boolean sistema) {
        this.sistema = sistema;
    }

    public String getCodigoAlterno() {
        return codigoAlterno;
    }

    public void setCodigoAlterno(String codigoAlterno) {
        this.codigoAlterno = codigoAlterno == null ? "":codigoAlterno;
    }

    public ImageView getImagePredeterminado() {
        return imagePredeterminado;
    }

    public void setImagePredeterminado(ImageView imagePredeterminado) {
        this.imagePredeterminado = imagePredeterminado;
    }

    @Override
    public String toString() {
        return nombre;
    }

}