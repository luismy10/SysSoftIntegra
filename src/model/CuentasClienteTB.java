
package model;

import java.time.LocalDateTime;

public class CuentasClienteTB {
    
    private int idCuentasCliente;
    private String idVenta;
    private String idCliente;
    private int plazos;
    private String plazosName;
    private LocalDateTime fechaVencimiento;
    private double montoInicial;
    
    public CuentasClienteTB() {
    }

    public int getIdCuentasCliente() {
        return idCuentasCliente;
    }

    public void setIdCuentasCliente(int idCuentasCliente) {
        this.idCuentasCliente = idCuentasCliente;
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

    public int getPlazos() {
        return plazos;
    }

    public void setPlazos(int plazos) {
        this.plazos = plazos;
    }

    public String getPlazosName() {
        return plazosName;
    }

    public void setPlazosName(String plazosName) {
        this.plazosName = plazosName;
    }

    public LocalDateTime getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDateTime fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public double getMontoInicial() {
        return montoInicial;
    }

    public void setMontoInicial(double montoInicial) {
        this.montoInicial = montoInicial;
    }

}
