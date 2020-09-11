
package model;

import java.util.ArrayList;


public class GuiaRemisionTB {

    private int id;
    private String idCliente;
    private String email;
    
    private int idMotivo;
    private int idModalidad;
    private String FechaTraslado;
    
    private double pesoBruto;
    private int numeroBultos;
    private int numeroContenedor;
    private int idCodigoPuerto;
    
    private int idTipoDocumentoConductor;
    private String numeroDocumentoConductor;
    private String nombreConductor;
    private String numeroPlacaVehiculo;
    
    private String direccionPartida;    
    
    private String direccionLlegada;
   
    private int tipoDocumento;
    private String serie;
    private String numero;
    
    private ArrayList<SuministroTB> suministroTBs;

    public GuiaRemisionTB() {
        
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getIdMotivo() {
        return idMotivo;
    }

    public void setIdMotivo(int idMotivo) {
        this.idMotivo = idMotivo;
    }

    public int getIdModalidad() {
        return idModalidad;
    }

    public void setIdModalidad(int idModalidad) {
        this.idModalidad = idModalidad;
    }

    public String getFechaTraslado() {
        return FechaTraslado;
    }

    public void setFechaTraslado(String FechaTraslado) {
        this.FechaTraslado = FechaTraslado;
    }

    public double getPesoBruto() {
        return pesoBruto;
    }

    public void setPesoBruto(double pesoBruto) {
        this.pesoBruto = pesoBruto;
    }

    public int getNumeroBultos() {
        return numeroBultos;
    }

    public void setNumeroBultos(int numeroBultos) {
        this.numeroBultos = numeroBultos;
    }

    public int getNumeroContenedor() {
        return numeroContenedor;
    }

    public void setNumeroContenedor(int numeroContenedor) {
        this.numeroContenedor = numeroContenedor;
    }

    public int getIdCodigoPuerto() {
        return idCodigoPuerto;
    }

    public void setIdCodigoPuerto(int idCodigoPuerto) {
        this.idCodigoPuerto = idCodigoPuerto;
    }

    public int getIdTipoDocumentoConductor() {
        return idTipoDocumentoConductor;
    }

    public void setIdTipoDocumentoConductor(int idTipoDocumentoConductor) {
        this.idTipoDocumentoConductor = idTipoDocumentoConductor;
    }

    public String getNumeroDocumentoConductor() {
        return numeroDocumentoConductor;
    }

    public void setNumeroDocumentoConductor(String numeroDocumentoConductor) {
        this.numeroDocumentoConductor = numeroDocumentoConductor;
    }

    public String getNombreConductor() {
        return nombreConductor;
    }

    public void setNombreConductor(String nombreConductor) {
        this.nombreConductor = nombreConductor;
    }

    public String getNumeroPlacaVehiculo() {
        return numeroPlacaVehiculo;
    }

    public void setNumeroPlacaVehiculo(String numeroPlacaVehiculo) {
        this.numeroPlacaVehiculo = numeroPlacaVehiculo;
    }

    public String getDireccionPartida() {
        return direccionPartida;
    }

    public void setDireccionPartida(String direccionPartida) {
        this.direccionPartida = direccionPartida;
    }

    public String getDireccionLlegada() {
        return direccionLlegada;
    }

    public void setDireccionLlegada(String direccionLlegada) {
        this.direccionLlegada = direccionLlegada;
    }

    public int getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(int tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public ArrayList<SuministroTB> getSuministroTBs() {
        return suministroTBs;
    }

    public void setSuministroTBs(ArrayList<SuministroTB> suministroTBs) {
        this.suministroTBs = suministroTBs;
    }
    
    
    
    
}
