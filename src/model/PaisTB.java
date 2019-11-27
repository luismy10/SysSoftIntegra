
package model;


public class PaisTB {
    private String paisCodigo;
    private String paisNombre;
    private String paisContinente;
    private String paisRegion;
    private float paisArea;
    private int paisCapital;
    
    private PaisTB(){
        
    }

    public PaisTB(String paisCodigo, String paisNombre) {
        this.paisCodigo = paisCodigo;
        this.paisNombre = paisNombre;
    }
    
    
    
    public PaisTB(String paisCodigo, String paisNombre, String paisContinente, String paisRegion, float paisArea, int paisCapital) {
        this.paisCodigo = paisCodigo;
        this.paisNombre = paisNombre;
        this.paisContinente = paisContinente;
        this.paisRegion = paisRegion;
        this.paisArea = paisArea;
        this.paisCapital = paisCapital;
    }
    
    

    public String getPaisCodigo() {
        return paisCodigo;
    }

    public void setPaisCodigo(String paisCodigo) {
        this.paisCodigo = paisCodigo;
    }

    public String getPaisNombre() {
        return paisNombre;
    }

    public void setPaisNombre(String paisNombre) {
        this.paisNombre = paisNombre;
    }

    public String getPaisContinente() {
        return paisContinente;
    }

    public void setPaisContinente(String paisContinente) {
        this.paisContinente = paisContinente;
    }

    public String getPaisRegion() {
        return paisRegion;
    }

    public void setPaisRegion(String paisRegion) {
        this.paisRegion = paisRegion;
    }

    public float getPaisArea() {
        return paisArea;
    }

    public void setPaisArea(float paisArea) {
        this.paisArea = paisArea;
    }

    public int getPaisCapital() {
        return paisCapital;
    }

    public void setPaisCapital(int paisCapital) {
        this.paisCapital = paisCapital;
    }

    @Override
    public String toString() {
        return  paisNombre ;
    }
    
    
    
}
