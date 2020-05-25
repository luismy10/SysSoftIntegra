
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
    private double inicial;
    private double cantidad;    
    private double cantidadTotal;
    
    private Label lblInicial;
    private Label lblEntrada;
    private Label lblSalida;
    private Label lblSaldo;
    
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

    public double getInicial() {
        return inicial;
    }

    public void setInicial(double inicial) {
        this.inicial = inicial;
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

    public double getCantidadTotal() {
        return cantidadTotal;
    }

    public void setCantidadTotal(double cantidadTotal) {
        this.cantidadTotal = cantidadTotal;
    }

    public Label getLblInicial() {
        return lblInicial;
    }

    public void setLblInicial(Label lblInicial) {
        this.lblInicial = lblInicial;
    }

    public Label getLblEntrada() {
        return lblEntrada;
    }

    public void setLblEntrada(Label lblEntrada) {
        this.lblEntrada = lblEntrada;
    }

    public Label getLblSalida() {
        return lblSalida;
    }

    public void setLblSalida(Label lblSalida) {
        this.lblSalida = lblSalida;
    }

    public Label getLblSaldo() {
        return lblSaldo;
    }

    public void setLblSaldo(Label lblSaldo) {
        this.lblSaldo = lblSaldo;
    }
    
    
  
}
