package model;

import java.util.ArrayList;

public class FormulaTB {

    private int id;

    private String idFormula;

    private String titulo;

    private double cantidad;

    private String idSuministro;

    private double costoAdicional;

    private String instrucciones;

    private String fecha;

    private String hora;

    private SuministroTB suministroTB;
    
    private ArrayList<InsumoTB> insumoTBs;

    public FormulaTB() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIdFormula() {
        return idFormula;
    }

    public void setIdFormula(String idFormula) {
        this.idFormula = idFormula;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    public String getIdSuministro() {
        return idSuministro;
    }

    public void setIdSuministro(String idSuministro) {
        this.idSuministro = idSuministro;
    }

    public double getCostoAdicional() {
        return costoAdicional;
    }

    public void setCostoAdicional(double costoAdicional) {
        this.costoAdicional = costoAdicional;
    }

    public String getInstrucciones() {
        return instrucciones;
    }

    public void setInstrucciones(String instrucciones) {
        this.instrucciones = instrucciones;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public SuministroTB getSuministroTB() {
        return suministroTB;
    }

    public void setSuministroTB(SuministroTB suministroTB) {
        this.suministroTB = suministroTB;
    }

    public ArrayList<InsumoTB> getInsumoTBs() {
        return insumoTBs;
    }

    public void setInsumoTBs(ArrayList<InsumoTB> insumoTBs) {
        this.insumoTBs = insumoTBs;
    }
    
}
