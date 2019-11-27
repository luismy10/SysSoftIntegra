package model;

public class DistritoTB {

    private int idDistrito;
    private String distrito;
    private int idProvincia;

    public DistritoTB() {
    }

    public DistritoTB(int idDistrito, String distrito) {
        this.idDistrito = idDistrito;
        this.distrito = distrito;
    }

    public int getIdDistrito() {
        return idDistrito;
    }

    public void setIdDistrito(int idDistrito) {
        this.idDistrito = idDistrito;
    }

    public String getDistrito() {
        return distrito;
    }

    public void setDistrito(String distrito) {
        this.distrito = distrito;
    }

    public int getIdProvincia() {
        return idProvincia;
    }

    public void setIdProvincia(int idProvincia) {
        this.idProvincia = idProvincia;
    }

    @Override
    public String toString() {
        return distrito;
    }

}
