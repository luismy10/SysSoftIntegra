
package model;

public class FormaPagoTB {
    
    private int id;
    private int idFormPago;
    private String idVenta;
    private String nombre;
    private double monto;

    public FormaPagoTB() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdFormPago() {
        return idFormPago;
    }

    public void setIdFormPago(int idFormPago) {
        this.idFormPago = idFormPago;
    }

    public String getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(String idVenta) {
        this.idVenta = idVenta;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }
    
    
    
}
