package model;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class PedidoDetalleTB {

    private int id;
    private String idPedido;
    private String idSuministro;
    private double cantidad;
    private double existencia;
    private int idImpuesto;
    private String stock;
    private double costo;
    private double descuento;
    private double impuesto;
    private double importe;
    private TextField txtCantidad;
    private TextField txtCosto;
    private TextField txtDescuento;
    private Button btnQuitar;
    private SuministroTB suministroTB;

    public PedidoDetalleTB() {
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

    public String getIdSuministro() {
        return idSuministro;
    }

    public void setIdSuministro(String idSuministro) {
        this.idSuministro = idSuministro;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    public double getExistencia() {
        return existencia;
    }

    public void setExistencia(double existencia) {
        this.existencia = existencia;
    }

    public int getIdImpuesto() {
        return idImpuesto;
    }

    public void setIdImpuesto(int idImpuesto) {
        this.idImpuesto = idImpuesto;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public double getCosto() {
        return costo;
    }

    public void setCosto(double costo) {
        this.costo = costo;
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

    public double getImporte() {
        return importe;
    }

    public void setImporte(double importe) {
        this.importe = importe;
    }

    public TextField getTxtCantidad() {
        return txtCantidad;
    }

    public void setTxtCantidad(TextField txtCantidad) {
        this.txtCantidad = txtCantidad;
    }

    public TextField getTxtCosto() {
        return txtCosto;
    }

    public void setTxtCosto(TextField txtCosto) {
        this.txtCosto = txtCosto;
    }

    public TextField getTxtDescuento() {
        return txtDescuento;
    }

    public void setTxtDescuento(TextField txtDescuento) {
        this.txtDescuento = txtDescuento;
    }

    public Button getBtnQuitar() {
        return btnQuitar;
    }

    public void setBtnQuitar(Button btnQuitar) {
        this.btnQuitar = btnQuitar;
    }

    public SuministroTB getSuministroTB() {
        return suministroTB;
    }

    public void setSuministroTB(SuministroTB suministroTB) {
        this.suministroTB = suministroTB;
    }

  
}
