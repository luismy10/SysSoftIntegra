package model;

import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class MovimientoInventarioTB {

    private int id;
    private String idMovimientoInventario;
    private String fecha;
    private String hora;
    private boolean tipoAjuste;
    private int tipoMovimiento;
    private String tipoMovimientoName;
    private String observacion;
    private String informacion;
    private boolean suministro;
    private boolean articulo;
    private short estado;
    private String estadoName;
    private String proveedor;

    private Button validar;
    private Label lblEstado;

    public MovimientoInventarioTB() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIdMovimientoInventario() {
        return idMovimientoInventario;
    }

    public void setIdMovimientoInventario(String idMovimientoInventario) {
        this.idMovimientoInventario = idMovimientoInventario;
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

    public boolean isTipoAjuste() {
        return tipoAjuste;
    }

    public void setTipoAjuste(boolean tipoAjuste) {
        this.tipoAjuste = tipoAjuste;
    }

    public int getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(int tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public String getTipoMovimientoName() {
        return tipoMovimientoName;
    }

    public void setTipoMovimientoName(String tipoMovimientoName) {
        this.tipoMovimientoName = tipoMovimientoName;
    }

    public String getInformacion() {
        return informacion;
    }

    public void setInformacion(String informacion) {
        this.informacion = informacion;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public boolean isSuministro() {
        return suministro;
    }

    public void setSuministro(boolean suministro) {
        this.suministro = suministro;
    }

    public boolean isArticulo() {
        return articulo;
    }

    public void setArticulo(boolean articulo) {
        this.articulo = articulo;
    }

    public short getEstado() {
        return estado;
    }

    public void setEstado(short estado) {
        this.estado = estado;
    }

    public String getEstadoName() {
        return estadoName;
    }

    public void setEstadoName(String estadoName) {
        this.estadoName = estadoName;
    }

    public String getProveedor() {
        return proveedor;
    }

    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
    }

    public Button getValidar() {
        return validar;
    }

    public void setValidar(Button validar) {
        this.validar = validar;
    }

    @Override
    public String toString() {
        return "MovimientoInventarioTB{" + "id=" + id + ", idMovimientoInventario=" + idMovimientoInventario + ", fecha=" + fecha + ", hora=" + hora + ", tipoAjuste=" + tipoAjuste + ", tipoMovimiento=" + tipoMovimiento + ", tipoMovimientoName=" + tipoMovimientoName + ", observacion=" + observacion + ", informacion=" + informacion + ", suministro=" + suministro + ", articulo=" + articulo + ", estado=" + estado + ", estadoName=" + estadoName + ", proveedor=" + proveedor + ", validar=" + validar + '}';
    }

    public Label getLblEstado() {
        return lblEstado;
    }

    public void setLblEstado(Label lblEstado) {
        this.lblEstado = lblEstado;
    }

}
