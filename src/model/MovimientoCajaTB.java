
package model;


public class MovimientoCajaTB {
    
    private int id;
    private int idMovimientoCaja;
    private String idCaja;
    private String fechaMovimiento;
    private String horaMovimiento;
    private String comentario;
    private short tipoMovimiento;
    private double monto;
    
    public MovimientoCajaTB(){
        
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdMovimientoCaja() {
        return idMovimientoCaja;
    }

    public void setIdMovimientoCaja(int idMovimientoCaja) {
        this.idMovimientoCaja = idMovimientoCaja;
    }

    public String getIdCaja() {
        return idCaja;
    }

    public void setIdCaja(String idCaja) {
        this.idCaja = idCaja;
    }

    public String getFechaMovimiento() {
        return fechaMovimiento;
    }

    public void setFechaMovimiento(String fechaMovimiento) {
        this.fechaMovimiento = fechaMovimiento;
    }

    public String getHoraMovimiento() {
        return horaMovimiento;
    }

    public void setHoraMovimiento(String horaMovimiento) {
        this.horaMovimiento = horaMovimiento;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public short getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(short tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }
    
}
