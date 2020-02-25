package model;

import javafx.scene.image.ImageView;

public class TipoDocumentoTB {
    
    private int id;
    private int idTipoDocumento;
    private String nombre;
    private String serie;
    private boolean predeterminado;
    private String nombreDocumento;
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

    public TipoDocumentoTB(int idTipoDocumento, String nombre, boolean predeterminado, String nombreDocumento, ImageView imagePredeterminado) {
        this.idTipoDocumento = idTipoDocumento;
        this.nombre = nombre;
        this.predeterminado = predeterminado;
        this.nombreDocumento = nombreDocumento;
        this.imagePredeterminado = imagePredeterminado;
    }

    public TipoDocumentoTB(int idTipoDocumento, String nombre, boolean predeterminado, String nombreDocumento) {
        this.idTipoDocumento = idTipoDocumento;
        this.nombre = nombre;
        this.predeterminado = predeterminado;
        this.nombreDocumento = nombreDocumento;
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

    public String getNombreDocumento() {
        return nombreDocumento;
    }

    public void setNombreDocumento(String nombreDocumento) {
        this.nombreDocumento = nombreDocumento;
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