

package model;

public class UbigeoTB {
    
   private int idUbigeo;
   private String ubigeo;
   private String departamento;
   private String provincia;
   private String distrito;

    public UbigeoTB() {
    }

    public int getIdUbigeo() {
        return idUbigeo;
    }

    public void setIdUbigeo(int idUbigeo) {
        this.idUbigeo = idUbigeo;
    }

    public String getUbigeo() {
        return ubigeo;
    }

    public void setUbigeo(String ubigeo) {
        this.ubigeo = ubigeo;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getDistrito() {
        return distrito;
    }

    public void setDistrito(String distrito) {
        this.distrito = distrito;
    }

    @Override
    public String toString() {
        return departamento + "-" + provincia + "-" + distrito + '}';
    }
   
   

}
