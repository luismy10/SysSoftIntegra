package model;

import java.util.ArrayList;
import javafx.collections.ObservableList;

public class PedidoTB {
    
    private int id;
    private String idPedido;
    private String idProveedor;
    private String idVendedor;
    private String fechaEmision;
    private String horaEmision;
    private String fechaVencimiento;
    private String horaVencimiento;
    private int idMoneda;
    private String Observacion;
    private double importeBruto;
    private double descuentoTotal;
    private double subImporte;
    private double impuestoGenerado;
    private double importeNeto;
    private MonedaTB monedaTB;
    private ProveedorTB proveedorTB;
    private EmpleadoTB empleadoTB;
    private ArrayList<SuministroTB> suministroTBs;
    private ObservableList<PedidoDetalleTB> pedidoDetalleTBs;

    public PedidoTB() {
    }    

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(String idPedido) {
        this.idPedido = idPedido;
    }

    public String getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(String idProveedor) {
        this.idProveedor = idProveedor;
    }

    public String getIdVendedor() {
        return idVendedor;
    }

    public void setIdVendedor(String idVendedor) {
        this.idVendedor = idVendedor;
    }

    public String getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(String fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public String getHoraEmision() {
        return horaEmision;
    }

    public void setHoraEmision(String horaEmision) {
        this.horaEmision = horaEmision;
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

    public int getIdMoneda() {
        return idMoneda;
    }

    public void setIdMoneda(int idMoneda) {
        this.idMoneda = idMoneda;
    }

    public String getObservacion() {
        return Observacion;
    }

    public void setObservacion(String Observacion) {
        this.Observacion = Observacion;
    }

    public double getImporteBruto() {
        return importeBruto;
    }

    public void setImporteBruto(double importeBruto) {
        this.importeBruto = importeBruto;
    }

    public double getDescuentoTotal() {
        return descuentoTotal;
    }

    public void setDescuentoTotal(double descuentoTotal) {
        this.descuentoTotal = descuentoTotal;
    }

    public double getSubImporte() {
        return subImporte;
    }

    public void setSubImporte(double subImporte) {
        this.subImporte = subImporte;
    }

    public double getImpuestoGenerado() {
        return impuestoGenerado;
    }

    public void setImpuestoGenerado(double impuestoGenerado) {
        this.impuestoGenerado = impuestoGenerado;
    }

    public double getImporteNeto() {
        return importeNeto;
    }

    public void setImporteNeto(double importeNeto) {
        this.importeNeto = importeNeto;
    }

    public MonedaTB getMonedaTB() {
        return monedaTB;
    }

    public void setMonedaTB(MonedaTB monedaTB) {
        this.monedaTB = monedaTB;
    }

    public ProveedorTB getProveedorTB() {
        return proveedorTB;
    }

    public void setProveedorTB(ProveedorTB proveedorTB) {
        this.proveedorTB = proveedorTB;
    }

    public EmpleadoTB getEmpleadoTB() {
        return empleadoTB;
    }

    public void setEmpleadoTB(EmpleadoTB empleadoTB) {
        this.empleadoTB = empleadoTB;
    }

    public ArrayList<SuministroTB> getSuministroTBs() {
        return suministroTBs;
    }

    public void setSuministroTBs(ArrayList<SuministroTB> suministroTBs) {
        this.suministroTBs = suministroTBs;
    }

    public ObservableList<PedidoDetalleTB> getPedidoDetalleTBs() {
        return pedidoDetalleTBs;
    }

    public void setPedidoDetalleTBs(ObservableList<PedidoDetalleTB> pedidoDetalleTBs) {
        this.pedidoDetalleTBs = pedidoDetalleTBs;
    }

    
}
