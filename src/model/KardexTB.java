
package model;


public class KardexTB {
    
    private int idKardex;
    private String idArticulo;
    private String fecha;
    private String hora;
    private short tipo;
    private int movimiento;
    private String movimientoName;
    private String detalle;
    private double cantidad;
    private double cUnitario;
    private double cTotal;
    
    private double cantidadTotal;
    private double cUnictarioTotal;
    private double cTotalTotal;
    
    public KardexTB(){
        
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

    public double getcUnitario() {
        return cUnitario;
    }

    public void setcUnitario(double cUnitario) {
        this.cUnitario = cUnitario;
    }

    public double getcTotal() {
        return cTotal;
    }

    public void setcTotal(double cTotal) {
        this.cTotal = cTotal;
    }

    public double getCantidadTotal() {
        return cantidadTotal;
    }

    public void setCantidadTotal(double cantidadTotal) {
        this.cantidadTotal = cantidadTotal;
    }

    public double getcUnictarioTotal() {
        return cUnictarioTotal;
    }

    public void setcUnictarioTotal(double cUnictarioTotal) {
        this.cUnictarioTotal = cUnictarioTotal;
    }

    public double getcTotalTotal() {
        return cTotalTotal;
    }

    public void setcTotalTotal(double cTotalTotal) {
        this.cTotalTotal = cTotalTotal;
    }
    
    
}
