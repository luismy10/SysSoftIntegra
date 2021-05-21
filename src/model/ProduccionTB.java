package model;

import java.util.ArrayList;
import javafx.scene.control.Label;

public class ProduccionTB {

    private int id;
    private String idProduccion;
    private String idProducto;
    private boolean tipoOrden;
    private String idEncargado;
    private String proyecto;
    private String descripcion;
    private String fechaInicio;
    private String horaInicio;
    private String horaRegistro;
    private String fechaRegistro;
    private int dias;
    private int horas;
    private int minutos;
    private double cantidad;
    private double costoAdicional;
    private int estado;
    private Label lblEstado;
    private SuministroTB suministroTB;
    private EmpleadoTB empleadoTB;
    private ArrayList<ProduccionHistorialTB> produccionHistorialTBs;
    private ArrayList<SuministroTB> suministroInsumos;
    private ArrayList<SuministroTB> suministroMermas;
    
    public ProduccionTB() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIdProduccion() {
        return idProduccion;
    }

    public void setIdProduccion(String idProduccion) {
        this.idProduccion = idProduccion;
    }

    public String getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(String idProducto) {
        this.idProducto = idProducto;
    }

    public boolean isTipoOrden() {
        return tipoOrden;
    }

    public void setTipoOrden(boolean tipoOrden) {
        this.tipoOrden = tipoOrden;
    }

    public String getIdEncargado() {
        return idEncargado;
    }

    public void setIdEncargado(String idEncargado) {
        this.idEncargado = idEncargado;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public String getProyecto() {
        return proyecto;
    }

    public void setProyecto(String proyecto) {
        this.proyecto = proyecto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getHoraRegistro() {
        return horaRegistro;
    }

    public void setHoraRegistro(String horaRegistro) {
        this.horaRegistro = horaRegistro;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public int getDias() {
        return dias;
    }

    public void setDias(int dias) {
        this.dias = dias;
    }

    public int getHoras() {
        return horas;
    }

    public void setHoras(int horas) {
        this.horas = horas;
    }

    public int getMinutos() {
        return minutos;
    }

    public void setMinutos(int minutos) {
        this.minutos = minutos;
    }    

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    public double getCostoAdicional() {
        return costoAdicional;
    }

    public void setCostoAdicional(double costoAdicional) {
        this.costoAdicional = costoAdicional;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public Label getLblEstado() {
        return lblEstado;
    }

    public void setLblEstado(Label lblEstado) {
        this.lblEstado = lblEstado;
    }

    public SuministroTB getSuministroTB() {
        return suministroTB;
    }

    public void setSuministroTB(SuministroTB suministroTB) {
        this.suministroTB = suministroTB;
    }    

    public EmpleadoTB getEmpleadoTB() {
        return empleadoTB;
    }

    public void setEmpleadoTB(EmpleadoTB empleadoTB) {
        this.empleadoTB = empleadoTB;
    }

    public ArrayList<ProduccionHistorialTB> getProduccionHistorialTBs() {
        return produccionHistorialTBs;
    }

    public void setProduccionHistorialTBs(ArrayList<ProduccionHistorialTB> produccionHistorialTBs) {
        this.produccionHistorialTBs = produccionHistorialTBs;
    }

    public ArrayList<SuministroTB> getSuministroInsumos() {
        return suministroInsumos;
    }

    public void setSuministroInsumos(ArrayList<SuministroTB> suministroInsumos) {
        this.suministroInsumos = suministroInsumos;
    }

    public ArrayList<SuministroTB> getSuministroMermas() {
        return suministroMermas;
    }

    public void setSuministroMermas(ArrayList<SuministroTB> suministroMermas) {
        this.suministroMermas = suministroMermas;
    }
    
}