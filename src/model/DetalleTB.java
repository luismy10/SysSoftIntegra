package model;

import java.io.Serializable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class DetalleTB implements Serializable {

    private SimpleIntegerProperty idDetalle;
    private SimpleStringProperty idMantenimiento;
    private SimpleStringProperty idAuxiliar;
    private SimpleStringProperty nombre;
    private SimpleStringProperty descripcion;
    private SimpleStringProperty estado;
    private SimpleStringProperty usuarioRegistro;

    public DetalleTB() {
    }

    public DetalleTB(SimpleStringProperty nombre) {
        this.nombre = nombre;
    }

    public DetalleTB(SimpleIntegerProperty idDetalle, SimpleStringProperty nombre) {
        this.idDetalle = idDetalle;
        this.nombre = nombre;
    }
  

    public SimpleIntegerProperty getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(int idDetalle) {
        this.idDetalle = new SimpleIntegerProperty(idDetalle);
    }

    public SimpleStringProperty getIdAuxiliar() {
        return idAuxiliar;
    }

    public void setIdAuxiliar(String idAuxiliar) {
        this.idAuxiliar = new SimpleStringProperty(idAuxiliar);
    }

    public SimpleStringProperty getIdMantenimiento() {
        return idMantenimiento;
    }

    public void setIdMantenimiento(String idMantenimiento) {
        this.idMantenimiento = new SimpleStringProperty(idMantenimiento);
    }

    public SimpleStringProperty getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = new SimpleStringProperty(nombre);
    }

    public SimpleStringProperty getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = new SimpleStringProperty(descripcion);
    }

    public SimpleStringProperty getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = new SimpleStringProperty(estado);
    }

    public SimpleStringProperty getUsuarioRegistro() {
        return usuarioRegistro;
    }

    public void setUsuarioRegistro(String usuarioRegistro) {
        this.usuarioRegistro = new SimpleStringProperty(usuarioRegistro);
    }

    @Override
    public String toString() {
        return nombre.get();
    }

}
