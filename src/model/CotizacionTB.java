package model;

import java.util.ArrayList;
import javafx.collections.ObservableList;

public class CotizacionTB {

    private int id;
    private String idCotizacion;
    private String idCliente;
    private String idVendedor;
    private int idMoneda;
    private String fechaCotizacion;
    private String horaCotizacion;
    private String fechaVencimiento;
    private String horaVencimiento;
    private double subTotal;
    private double descuento;
    private double impuesto;
    private double total;
    private short estado;
    private String observaciones;

    private ClienteTB clienteTB;
    private EmpleadoTB empleadoTB;
    private MonedaTB monedaTB;
    private ArrayList<DetalleCotizacionTB> detalleCotizacionTBs;
    private ObservableList<SuministroTB> detalleSuministroTBs;

    public CotizacionTB() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIdCotizacion() {
        return idCotizacion;
    }

    public void setIdCotizacion(String idCotizacion) {
        this.idCotizacion = idCotizacion;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public String getIdVendedor() {
        return idVendedor;
    }

    public void setIdVendedor(String idVendedor) {
        this.idVendedor = idVendedor;
    }

    public int getIdMoneda() {
        return idMoneda;
    }

    public void setIdMoneda(int idMoneda) {
        this.idMoneda = idMoneda;
    }

    public String getFechaCotizacion() {
        return fechaCotizacion;
    }

    public void setFechaCotizacion(String fechaCotizacion) {
        this.fechaCotizacion = fechaCotizacion;
    }

    public String getHoraCotizacion() {
        return horaCotizacion;
    }

    public void setHoraCotizacion(String horaCotizacion) {
        this.horaCotizacion = horaCotizacion;
    }

    public String getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(String fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public String getHoraVencimiento() {
        return horaVencimiento;
    }

    public void setHoraVencimiento(String horaVencimiento) {
        this.horaVencimiento = horaVencimiento;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }

    public double getDescuento() {
        return descuento;
    }

    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }

    public double getImpuesto() {
        return impuesto;
    }

    public void setImpuesto(double impuesto) {
        this.impuesto = impuesto;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public short getEstado() {
        return estado;
    }

    public void setEstado(short estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public ClienteTB getClienteTB() {
        return clienteTB;
    }

    public void setClienteTB(ClienteTB clienteTB) {
        this.clienteTB = clienteTB;
    }

    public EmpleadoTB getEmpleadoTB() {
        return empleadoTB;
    }

    public void setEmpleadoTB(EmpleadoTB empleadoTB) {
        this.empleadoTB = empleadoTB;
    }

    public MonedaTB getMonedaTB() {
        return monedaTB;
    }

    public void setMonedaTB(MonedaTB monedaTB) {
        this.monedaTB = monedaTB;
    }

    public ArrayList<DetalleCotizacionTB> getDetalleCotizacionTBs() {
        return detalleCotizacionTBs;
    }

    public void setDetalleCotizacionTBs(ArrayList<DetalleCotizacionTB> detalleCotizacionTBs) {
        this.detalleCotizacionTBs = detalleCotizacionTBs;
    }

    public ObservableList<SuministroTB> getDetalleSuministroTBs() {
        return detalleSuministroTBs;
    }

    public void setDetalleSuministroTBs(ObservableList<SuministroTB> detalleSuministroTBs) {
        this.detalleSuministroTBs = detalleSuministroTBs;
    }

}
