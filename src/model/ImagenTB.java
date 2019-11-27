package model;

import java.io.InputStream;
import javafx.scene.image.Image;

public class ImagenTB {

    private long idImage;
    private Image imagen;
    private InputStream file;
    private String idRelacionado;

    public ImagenTB() {

    }

    public ImagenTB(Image imagen) {
        this.imagen = imagen;
    }

    public ImagenTB(InputStream file) {
        this.file = file;
    }

    public long getIdImage() {
        return idImage;
    }

    public void setIdImage(long idImage) {
        this.idImage = idImage;
    }

    public Image getImagen() {
        return imagen;
    }

    public void setImagen(Image imagen) {
        this.imagen = imagen;
    }

    public String getIdRelacionado() {
        return idRelacionado;
    }

    public void setIdRelacionado(String idRelacionado) {
        this.idRelacionado = idRelacionado;
    }

    public InputStream getFile() {
        return file;
    }

    public void setFile(InputStream file) {
        this.file = file;
    }

}
