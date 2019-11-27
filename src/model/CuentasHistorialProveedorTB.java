

package model;

public class CuentasHistorialProveedorTB {
    
    private int id;
    private int idCuentasHistorialProveedor;
    private int idCuentasProveedor;
    private double monto;
    private int cuota;
    private String fecha;
    private String hora;
    private String observacion;
    private short estado;
    private String usuario;

    public CuentasHistorialProveedorTB() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdCuentasHistorialProveedor() {
        return idCuentasHistorialProveedor;
    }

    public void setIdCuentasHistorialProveedor(int idCuentasHistorialProveedor) {
        this.idCuentasHistorialProveedor = idCuentasHistorialProveedor;
    }

    public int getIdCuentasProveedor() {
        return idCuentasProveedor;
    }

    public void setIdCuentasProveedor(int idCuentasProveedor) {
        this.idCuentasProveedor = idCuentasProveedor;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public int getCuota() {
        return cuota;
    }

    public void setCuota(int cuota) {
        this.cuota = cuota;
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

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public short getEstado() {
        return estado;
    }

    public void setEstado(short estado) {
        this.estado = estado;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
    
    
}
