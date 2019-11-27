package model;

import java.time.LocalDateTime;

public class CuentasHistorialClienteTB {

    private int idCuentasHistorialCliente;
    private int idCuentaClientes;
    private double abono;
    private LocalDateTime fechaAbono;
    private String referencia;

    public CuentasHistorialClienteTB() {
       
    }

    public int getIdCuentasHistorialCliente() {
        return idCuentasHistorialCliente;
    }

    public void setIdCuentasHistorialCliente(int idCuentasHistorialCliente) {
        this.idCuentasHistorialCliente = idCuentasHistorialCliente;
    }

    public int getIdCuentaClientes() {
        return idCuentaClientes;
    }

    public void setIdCuentaClientes(int idCuentaClientes) {
        this.idCuentaClientes = idCuentaClientes;
    }

    public double getAbono() {
        return abono;
    }

    public void setAbono(double abono) {
        this.abono = abono;
    }

    public LocalDateTime getFechaAbono() {
        return fechaAbono;
    }

    public void setFechaAbono(LocalDateTime fechaAbono) {
        this.fechaAbono = fechaAbono;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }
    
    
}
