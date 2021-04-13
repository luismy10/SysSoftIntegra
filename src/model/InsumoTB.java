package model;

import controller.tools.SearchComboBox;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class InsumoTB {

    private int id;
    private String idInsumo;
    private String clave;
    private String claveAlterna;
    private String nombreMarca;
    private int medida;
    private int categoria;
    private String medidaName;
    private String categoriaName;
    private double cantidad;
    private double cantidadUtilizada;
    private double cantidadAUtilizar;
    private double merma;
    private double costo;
    private double stockMinimo;
    private double stockMaximo;

    private ComboBox<InsumoTB> comboBox;
    private SearchComboBox<?> searchComboBox;
    private Button btnRemove;
    private TextField txtCantidad;
    private TextField txtCantidadUtilizada;
    private TextField txtCantidadAUtilizar;
    private TextField txtMerma;
    
    private String idClase;
    private String nombreClase;
    private String idSubClase;
    private String nombreSubClase;
    private String idSubSubClase;
    private String nombreSubSubClase;

    public InsumoTB() {
    }

    public InsumoTB(String idInsumo, String clave, String nombreMarca) {
        this.idInsumo = idInsumo;
        this.clave = clave;
        this.nombreMarca = nombreMarca;
    }  

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIdInsumo() {
        return idInsumo;
    }

    public void setIdInsumo(String idInsumo) {
        this.idInsumo = idInsumo;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getClaveAlterna() {
        return claveAlterna;
    }

    public void setClaveAlterna(String claveAlterna) {
        this.claveAlterna = claveAlterna;
    }

    public String getNombreMarca() {
        return nombreMarca;
    }

    public void setNombreMarca(String nombreMarca) {
        this.nombreMarca = nombreMarca;
    }

    public int getMedida() {
        return medida;
    }

    public void setMedida(int medida) {
        this.medida = medida;
    }

    public int getCategoria() {
        return categoria;
    }

    public void setCategoria(int categoria) {
        this.categoria = categoria;
    }

    public String getMedidaName() {
        return medidaName;
    }

    public void setMedidaName(String medidaName) {
        this.medidaName = medidaName;
    }

    public String getCategoriaName() {
        return categoriaName;
    }

    public void setCategoriaName(String categoriaName) {
        this.categoriaName = categoriaName;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    public double getCantidadUtilizada() {
        return cantidadUtilizada;
    }

    public void setCantidadUtilizada(double cantidadUtilizada) {
        this.cantidadUtilizada = cantidadUtilizada;
    }

    public double getCantidadAUtilizar() {
        return cantidadAUtilizar;
    }

    public void setCantidadAUtilizar(double cantidadAUtilizar) {
        this.cantidadAUtilizar = cantidadAUtilizar;
    }
    
     public double getMerma() {
        return merma;
    }

    public void setMerma(double merma) {
        this.merma = merma;
    }

    public double getCosto() {
        return costo;
    }

    public void setCosto(double costo) {
        this.costo = costo;
    }

    public double getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(double stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public double getStockMaximo() {
        return stockMaximo;
    }

    public void setStockMaximo(double stockMaximo) {
        this.stockMaximo = stockMaximo;
    }

    public ComboBox<InsumoTB> getComboBox() {
        return comboBox;
    }

    public void setComboBox(ComboBox<InsumoTB> comboBox) {
        this.comboBox = comboBox;
    }

    public SearchComboBox<?> getSearchComboBox() {
        return searchComboBox;
    }

    public void setSearchComboBox(SearchComboBox<?> searchComboBox) {
        this.searchComboBox = searchComboBox;
    }

    public Button getBtnRemove() {
        return btnRemove;
    }

    public void setBtnRemove(Button btnRemove) {
        this.btnRemove = btnRemove;
    }

    public TextField getTxtCantidad() {
        return txtCantidad;
    }

    public void setTxtCantidad(TextField txtCantidad) {
        this.txtCantidad = txtCantidad;
    }

    public TextField getTxtCantidadUtilizada() {
        return txtCantidadUtilizada;
    }

    public void setTxtCantidadUtilizada(TextField txtCantidadUtilizada) {
        this.txtCantidadUtilizada = txtCantidadUtilizada;
    }

    public TextField getTxtCantidadAUtilizar() {
        return txtCantidadAUtilizar;
    }

    public void setTxtCantidadAUtilizar(TextField txtCantidadAUtilizar) {
        this.txtCantidadAUtilizar = txtCantidadAUtilizar;
    }

    public TextField getTxtMerma() {
        return txtMerma;
    }

    public void setTxtMerma(TextField txtMerma) {
        this.txtMerma = txtMerma;
    }
    
    public String getIdClase() {
        return idClase;
    }

    public void setIdClase(String idClase) {
        this.idClase = idClase;
    }

    public String getNombreClase() {
        return nombreClase;
    }

    public void setNombreClase(String nombreClase) {
        this.nombreClase = nombreClase;
    }

    public String getIdSubClase() {
        return idSubClase;
    }

    public void setIdSubClase(String idSubClase) {
        this.idSubClase = idSubClase;
    }

    public String getNombreSubClase() {
        return nombreSubClase;
    }

    public void setNombreSubClase(String nombreSubClase) {
        this.nombreSubClase = nombreSubClase;
    }

    public String getIdSubSubClase() {
        return idSubSubClase;
    }

    public void setIdSubSubClase(String idSubSubClase) {
        this.idSubSubClase = idSubSubClase;
    }

    public String getNombreSubSubClase() {
        return nombreSubSubClase;
    }

    public void setNombreSubSubClase(String nombreSubSubClase) {
        this.nombreSubSubClase = nombreSubSubClase;
    }
    
    @Override
    public String toString() {
        return "(" + clave + ") " + nombreMarca;
    }

}
