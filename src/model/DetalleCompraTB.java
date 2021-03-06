package model;

import java.util.ArrayList;
import javafx.scene.control.Button;

public class DetalleCompraTB {

    private int id;
    private String idCompra;
    private String idArticulo;
    private String descripcion;
    private String medida;
    private double cantidad;
    private double precioCompraUnico;
    private double precioCompraReal;
    private double precioCompra;
    private double descuentoSumado;
    private double descuento;
    private int idImpuesto;
    private String nombreImpuesto;
    private double valorImpuesto;
    private double impuestoSumado;
    private double importe;
    private boolean lote;
    private ArrayList<LoteTB> listLote;
    private SuministroTB suministroTB;
    private ArrayList<PreciosTB> listPrecios;
    private Button remove;

    public DetalleCompraTB() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIdCompra() {
        return idCompra;
    }

    public void setIdCompra(String idCompra) {
        this.idCompra = idCompra;
    }

    public String getIdArticulo() {
        return idArticulo;
    }

    public void setIdArticulo(String idArticulo) {
        this.idArticulo = idArticulo;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecioCompraUnico() {
        return precioCompraUnico;
    }

    public void setPrecioCompraUnico(double precioCompraUnico) {
        this.precioCompraUnico = precioCompraUnico;
    }

    public double getPrecioCompraReal() {
        return precioCompraReal;
    }

    public void setPrecioCompraReal(double precioCompraReal) {
        this.precioCompraReal = precioCompraReal;
    }

    public double getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(double precioCompra) {
        this.precioCompra = precioCompra;
    }

    public double getDescuentoSumado() {
        return descuentoSumado;
    }

    public void setDescuentoSumado(double descuentoSumado) {
        this.descuentoSumado = descuentoSumado;
    }

    public double getDescuento() {
        return descuento;
    }

    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }

    public int getIdImpuesto() {
        return idImpuesto;
    }

    public void setIdImpuesto(int idImpuesto) {
        this.idImpuesto = idImpuesto;
    }

    public String getNombreImpuesto() {
        return nombreImpuesto;
    }

    public void setNombreImpuesto(String nombreImpuesto) {
        this.nombreImpuesto = nombreImpuesto;
    }

    public double getValorImpuesto() {
        return valorImpuesto;
    }

    public void setValorImpuesto(double valorImpuesto) {
        this.valorImpuesto = valorImpuesto;
    }

    public double getImpuestoSumado() {
        return impuestoSumado;
    }

    public void setImpuestoSumado(double impuestoSumado) {
        this.impuestoSumado = impuestoSumado;
    }

    public double getImporte() {
        return importe;
    }

    public void setImporte(double importe) {
        this.importe = importe;
    }

    public boolean isLote() {
        return lote;
    }

    public void setLote(boolean lote) {
        this.lote = lote;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public ArrayList<LoteTB> getListLote() {
        return listLote;
    }

    public void setListLote(ArrayList<LoteTB> listLote) {
        this.listLote = listLote;
    }

    public SuministroTB getSuministroTB() {
        return suministroTB;
    }

    public void setSuministroTB(SuministroTB suministroTB) {
        this.suministroTB = suministroTB;
    }

    public ArrayList<PreciosTB> getListPrecios() {
        return listPrecios;
    }

    public void setListPrecios(ArrayList<PreciosTB> listPrecios) {
        this.listPrecios = listPrecios;
    }

    public Button getRemove() {
        return remove;
    }

    public void setRemove(Button remove) {
        this.remove = remove;
    }

    public String getMedida() {
        return medida;
    }

    public void setMedida(String medida) {
        this.medida = medida;
    }

}
