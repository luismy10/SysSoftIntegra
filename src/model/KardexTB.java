
package model;

import javafx.scene.control.Label;


public class KardexTB {
    
    private int id;
    private int idKardex;
    private String idArticulo;
    private String fecha;
    private String hora;
    private short tipo;
    private int movimiento;
    private String movimientoName;
    private String detalle;
    private double cantidad;    
    private double costo;
    private double total;
    
    private Label lblCantidadEntreda;
    private Label lblCostoEntrada;
    private Label lblTotalEntrada;
    
    private Label lblCantidadSalida;
    private Label lblCostoSalida;
    private Label lblTotalSalida;
    
    private Label lblCantidadSaldo;
    private Label lblCostoSaldo;
    private Label lblTotalSaldo;
    
    public KardexTB(){
        
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdKardex() {
        return idKardex;
    }

    public void setIdKardex(int idKardex) {
        this.idKardex = idKardex;
    }

    public String getIdArticulo() {
        return idArticulo;
    }

    public void setIdArticulo(String idArticulo) {
        this.idArticulo = idArticulo;
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

    public short getTipo() {
        return tipo;
    }

    public void setTipo(short tipo) {
        this.tipo = tipo;
    }

    public int getMovimiento() {
        return movimiento;
    }

    public void setMovimiento(int movimiento) {
        this.movimiento = movimiento;
    }

    public String getMovimientoName() {
        return movimientoName;
    }

    public void setMovimientoName(String movimientoName) {
        this.movimientoName = movimientoName;
    }
 
    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    public double getCosto() {
        return costo;
    }

    public void setCosto(double costo) {
        this.costo = costo;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public Label getLblCantidadEntreda() {
        return lblCantidadEntreda;
    }

    public void setLblCantidadEntreda(Label lblCantidadEntreda) {
        this.lblCantidadEntreda = lblCantidadEntreda;
    }

    public Label getLblCostoEntrada() {
        return lblCostoEntrada;
    }

    public void setLblCostoEntrada(Label lblCostoEntrada) {
        this.lblCostoEntrada = lblCostoEntrada;
    }

    public Label getLblTotalEntrada() {
        return lblTotalEntrada;
    }

    public void setLblTotalEntrada(Label lblTotalEntrada) {
        this.lblTotalEntrada = lblTotalEntrada;
    }

    public Label getLblCantidadSalida() {
        return lblCantidadSalida;
    }

    public void setLblCantidadSalida(Label lblCantidadSalida) {
        this.lblCantidadSalida = lblCantidadSalida;
    }

    public Label getLblCostoSalida() {
        return lblCostoSalida;
    }

    public void setLblCostoSalida(Label lblCostoSalida) {
        this.lblCostoSalida = lblCostoSalida;
    }

    public Label getLblTotalSalida() {
        return lblTotalSalida;
    }

    public void setLblTotalSalida(Label lblTotalSalida) {
        this.lblTotalSalida = lblTotalSalida;
    }

    public Label getLblCantidadSaldo() {
        return lblCantidadSaldo;
    }

    public void setLblCantidadSaldo(Label lblCantidadSaldo) {
        this.lblCantidadSaldo = lblCantidadSaldo;
    }

    public Label getLblCostoSaldo() {
        return lblCostoSaldo;
    }

    public void setLblCostoSaldo(Label lblCostoSaldo) {
        this.lblCostoSaldo = lblCostoSaldo;
    }

    public Label getLblTotalSaldo() {
        return lblTotalSaldo;
    }

    public void setLblTotalSaldo(Label lblTotalSaldo) {
        this.lblTotalSaldo = lblTotalSaldo;
    }
  
}
