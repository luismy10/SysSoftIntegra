package model;

public class CiudadTB {

    private int idCiudad;
    private String ciudadNombre;
    private String paisCodigo;
    private String ciudadDistrito;
    private int iudadPoblacion;
    private ProvinciaTB provinciaTB;

    public CiudadTB() {
    }

    public CiudadTB(int idCiudad, String ciudadDistrito) {
        this.idCiudad = idCiudad;
        this.ciudadDistrito = ciudadDistrito;
    }


    public int getIdCiudad() {
        return idCiudad;
    }

    public void setIdCiudad(int idCiudad) {
        this.idCiudad = idCiudad;
    }

    public String getCiudadNombre() {
        return ciudadNombre;
    }

    public void setCiudadNombre(String ciudadNombre) {
        this.ciudadNombre = ciudadNombre;
    }

    public String getPaisCodigo() {
        return paisCodigo;
    }

    public void setPaisCodigo(String paisCodigo) {
        this.paisCodigo = paisCodigo;
    }

    public String getCiudadDistrito() {
        return ciudadDistrito;
    }

    public void setCiudadDistrito(String ciudadDistrito) {
        this.ciudadDistrito = ciudadDistrito;
    }

    public int getIudadPoblacion() {
        return iudadPoblacion;
    }

    public void setIudadPoblacion(int iudadPoblacion) {
        this.iudadPoblacion = iudadPoblacion;
    }

    public ProvinciaTB getProvinciaTB() {
        return provinciaTB;
    }

    public void setProvinciaTB(ProvinciaTB provinciaTB) {
        this.provinciaTB = provinciaTB;
    }
    
    
    @Override
    public String toString() {
        return ciudadDistrito;
    }

}
