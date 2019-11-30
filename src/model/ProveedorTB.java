package model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class ProveedorTB {

    private SimpleIntegerProperty id;
    private SimpleStringProperty idProveedor;
    private int tipoDocumento;
    private SimpleStringProperty tipoDocumentoName;
    private SimpleStringProperty numeroDocumento;
    private SimpleStringProperty razonSocial;
    private SimpleStringProperty nombreComercial;
    private String pais;
    private int ciudad;
    private int provincia;
    private int distrito;
    private int ambito;
    private int estado;
    private String telefono;
    private String celular;
    private String email;
    private String paginaWeb;
    private String direccion;
    private SimpleStringProperty estadoName;
    private String representante;

    public ProveedorTB() {

    }

    public ProveedorTB(String numeroDocumento, String razonSocial) {
        this.numeroDocumento = new SimpleStringProperty(numeroDocumento);
        this.razonSocial = new SimpleStringProperty(razonSocial);
    }

    public ProveedorTB(String razonSocial) {
        this.razonSocial = new SimpleStringProperty(razonSocial);
    }

    public SimpleIntegerProperty getId() {
        return id;
    }

    public void setId(int id) {
        this.id = new SimpleIntegerProperty(id);
    }

    public SimpleStringProperty getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(String idProveedor) {
        this.idProveedor = new SimpleStringProperty(idProveedor);
    }

    public int getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(int tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public SimpleStringProperty getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = new SimpleStringProperty(numeroDocumento);
    }

    public SimpleStringProperty getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = new SimpleStringProperty(razonSocial);
    }

    public SimpleStringProperty getNombreComercial() {
        return nombreComercial;
    }

    public void setNombreComercial(String nombreComercial) {
        this.nombreComercial = new SimpleStringProperty(nombreComercial != null ? nombreComercial : "");
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public int getCiudad() {
        return ciudad;
    }

    public void setCiudad(int ciudad) {
        this.ciudad = ciudad;
    }

    public int getProvincia() {
        return provincia;
    }

    public void setProvincia(int provincia) {
        this.provincia = provincia;
    }

    public int getDistrito() {
        return distrito;
    }

    public void setDistrito(int distrito) {
        this.distrito = distrito;
    }

    public int getAmbito() {
        return ambito;
    }

    public void setAmbito(int ambito) {
        this.ambito = ambito;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono == null ? "" : telefono;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular == null ? "" : celular;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPaginaWeb() {
        return paginaWeb;
    }

    public void setPaginaWeb(String paginaWeb) {
        this.paginaWeb = paginaWeb;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public SimpleStringProperty getTipoDocumentoName() {
        return tipoDocumentoName;
    }

    public void setTipoDocumentoName(String tipoDocumentoName) {
        this.tipoDocumentoName = new SimpleStringProperty(tipoDocumentoName);
    }

    public SimpleStringProperty getEstadoName() {
        return estadoName;
    }

    public void setEstadoName(String estadoName) {
        this.estadoName = new SimpleStringProperty(estadoName);
    }

    public String getRepresentante() {
        return representante;
    }

    public void setRepresentante(String representante) {
        this.representante = representante == null ? "" : representante;
    }

}
