
package model;

import java.util.ArrayList;


public class MermaTB {
    
    private String idMerma;
    private String idProduccion;
    private String idUsuario;
    private ArrayList<SuministroTB> suministroMerma;

    public MermaTB() {
    }

    public String getIdMerma() {
        return idMerma;
    }

    public void setIdMerma(String idMerma) {
        this.idMerma = idMerma;
    }

    public String getIdProduccion() {
        return idProduccion;
    }

    public void setIdProduccion(String idProduccion) {
        this.idProduccion = idProduccion;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public ArrayList<SuministroTB> getSuministroMerma() {
        return suministroMerma;
    }

    public void setSuministroMerma(ArrayList<SuministroTB> suministroMerma) {
        this.suministroMerma = suministroMerma;
    }
    
    

}
