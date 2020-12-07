
package report;

import model.SuministroTB;


public class ProduccionTB {
    
    private int id;
    private String idProduccion;
    private String fechaProduccion;
    private String horaProduccion;
    private String fechaInicio;
    private String fechaTermino;
    private String idSuministro;
    private int numeroOrden;
    private short estado;
    private boolean tipoOrden;
    private SuministroTB suministroTB;

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

    public String getFechaProduccion() {
        return fechaProduccion;
    }

    public void setFechaProduccion(String fechaProduccion) {
        this.fechaProduccion = fechaProduccion;
    }

    public String getHoraProduccion() {
        return horaProduccion;
    }

    public void setHoraProduccion(String horaProduccion) {
        this.horaProduccion = horaProduccion;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getFechaTermino() {
        return fechaTermino;
    }

    public void setFechaTermino(String fechaTermino) {
        this.fechaTermino = fechaTermino;
    }

    public String getIdSuministro() {
        return idSuministro;
    }

    public void setIdSuministro(String idSuministro) {
        this.idSuministro = idSuministro;
    }

    public int getNumeroOrden() {
        return numeroOrden;
    }

    public void setNumeroOrden(int numeroOrden) {
        this.numeroOrden = numeroOrden;
    }

    public short getEstado() {
        return estado;
    }

    public void setEstado(short estado) {
        this.estado = estado;
    }

    public boolean isTipoOrden() {
        return tipoOrden;
    }

    public void setTipoOrden(boolean tipoOrden) {
        this.tipoOrden = tipoOrden;
    }

    public SuministroTB getSuministroTB() {
        return suministroTB;
    }

    public void setSuministroTB(SuministroTB suministroTB) {
        this.suministroTB = suministroTB;
    }    
     
}
