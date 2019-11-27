
package model;


public class PlazosTB {
    
    private int idPlazos;
    private String nombre;
    private int dias;
    private boolean estado;
    private boolean predeterminado;
    
    public PlazosTB(){}

    public PlazosTB(int idPlazos, String nombre, int dias, boolean estado, boolean predeterminado) {
        this.idPlazos = idPlazos;
        this.nombre = nombre;
        this.dias = dias;
        this.estado = estado;
        this.predeterminado = predeterminado;
                
    }

    public int getIdPlazos() {
        return idPlazos;
    }

    public void setIdPlazos(int idPlazos) {
        this.idPlazos = idPlazos;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getDias() {
        return dias;
    }

    public void setDias(int dias) {
        this.dias = dias;
    }

    public boolean getEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public boolean getPredeterminado() {
        return predeterminado;
    }

    public void setPredeterminado(boolean predeterminado) {
        this.predeterminado = predeterminado;
    }
    
    @Override
    public String toString() {
        return  nombre;
    }
      
}
