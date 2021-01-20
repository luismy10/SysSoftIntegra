package model;

import java.util.ArrayList;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class VentaTB {

    private int id;
    private String idVenta;
    private String idCliente;
    private String vendedor;
    private int comprobante;
    private int moneda;
    private String monedaName;
    private String comprobanteName;
    private String serie;
    private String numeracion;
    private String fechaVenta;
    private String horaVenta;
    private String fechaVencimiento;
    private String horaVencimiento;
    private double subTotal;
    private double descuento;
    private double subImporte;
    private double impuesto;
    private double total;
    private String totalFormat;
    private int tipo;
    private String tipoName;
    private int forma;
    private String formaName;
    private int estado;
    private String estadoName;
    private Label estadoLabel;
    private String observaciones;
    private double efectivo;
    private double vuelto;
    private double tarjeta;
    private String codigo;
    private boolean tipopago;
    private String codigoAlterno;
    private ClienteTB clienteTB;
    private MonedaTB monedaTB;
    private EmpleadoTB empleadoTB;
    private ArrayList<SuministroTB> suministroTBs;
    private HBox hbOpciones;
    private ArrayList<VentaCreditoTB> ventaCreditoTBs;
    private String clienteName;
    private double montoTotal;
    private double montoCobrado;
    private double montoRestante;

    public VentaTB() {

    }

    public VentaTB(int id, String fechaVenta, double total) {
        this.id = id;
        this.fechaVenta = fechaVenta;
        this.total = total;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(String idVenta) {
        this.idVenta = idVenta;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public String getVendedor() {
        return vendedor;
    }

    public void setVendedor(String vendedor) {
        this.vendedor = vendedor;
    }

    public int getComprobante() {
        return comprobante;
    }

    public void setComprobante(int comprobante) {
        this.comprobante = comprobante;
    }

    public int getMoneda() {
        return moneda;
    }

    public void setMoneda(int moneda) {
        this.moneda = moneda;
    }

    public String getMonedaName() {
        return monedaName;
    }

    public void setMonedaName(String monedaName) {
        this.monedaName = monedaName == null ? "" : monedaName;
    }

    public String getComprobanteName() {
        return comprobanteName;
    }

    public void setComprobanteName(String comprobanteName) {
        this.comprobanteName = comprobanteName;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public String getNumeracion() {
        return numeracion;
    }

    public void setNumeracion(String numeracion) {
        this.numeracion = numeracion;
    }

    public String getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(String fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public String getHoraVenta() {
        return horaVenta;
    }

    public void setHoraVenta(String horaVenta) {
        this.horaVenta = horaVenta;
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

    public double getSubImporte() {
        return subImporte;
    }

    public void setSubImporte(double subImporte) {
        this.subImporte = subImporte;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public String getTipoName() {
        return tipoName;
    }

    public void setTipoName(String tipoName) {
        this.tipoName = tipoName;
    }

    public int getForma() {
        return forma;
    }

    public void setForma(int forma) {
        this.forma = forma;
    }

    public String getFormaName() {
        return formaName;
    }

    public void setFormaName(String formaName) {
        this.formaName = formaName;
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

    public String getTotalFormat() {
        return totalFormat;
    }

    public void setTotalFormat(String totalFormat) {
        this.totalFormat = totalFormat;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public String getEstadoName() {
        return estadoName;
    }

    public void setEstadoName(String estadoName) {
        this.estadoName = estadoName;
    }

    public Label getEstadoLabel() {
        return estadoLabel;
    }

    public void setEstadoLabel(Label estadoLabel) {
        this.estadoLabel = estadoLabel;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public double getEfectivo() {
        return efectivo;
    }

    public void setEfectivo(double efectivo) {
        this.efectivo = efectivo;
    }

    public double getVuelto() {
        return vuelto;
    }

    public void setVuelto(double vuelto) {
        this.vuelto = vuelto;
    }

    public double getTarjeta() {
        return tarjeta;
    }

    public void setTarjeta(double tarjeta) {
        this.tarjeta = tarjeta;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo == null ? "" : codigo;
    }

    public ClienteTB getClienteTB() {
        return clienteTB;
    }

    public void setClienteTB(ClienteTB clienteTB) {
        this.clienteTB = clienteTB;
    }

    public MonedaTB getMonedaTB() {
        return monedaTB;
    }

    public void setMonedaTB(MonedaTB monedaTB) {
        this.monedaTB = monedaTB;
    }

    public boolean isTipopago() {
        return tipopago;
    }

    public void setTipopago(boolean tipopago) {
        this.tipopago = tipopago;
    }

    public String getCodigoAlterno() {
        return codigoAlterno;
    }

    public void setCodigoAlterno(String codigoAlterno) {
        this.codigoAlterno = codigoAlterno;
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

    public HBox getHbOpciones() {
        return hbOpciones;
    }

    public void setHbOpciones(HBox hbOpciones) {
        this.hbOpciones = hbOpciones;
    }

    public ArrayList<VentaCreditoTB> getVentaCreditoTBs() {
        return ventaCreditoTBs;
    }

    public void setVentaCreditoTBs(ArrayList<VentaCreditoTB> ventaCreditoTBs) {
        this.ventaCreditoTBs = ventaCreditoTBs;
    }

    public String getClienteName() {
        return clienteName;
    }

    public void setClienteName(String clienteName) {
        this.clienteName = clienteName;
    }

    public double getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(double montoTotal) {
        this.montoTotal = montoTotal;
    }

    public double getMontoCobrado() {
        return montoCobrado;
    }

    public void setMontoCobrado(double montoCobrado) {
        this.montoCobrado = montoCobrado;
    }

    public double getMontoRestante() {
        return montoRestante;
    }

    public void setMontoRestante(double montoRestante) {
        this.montoRestante = montoRestante;
    }

   
}
