package model;

import controller.tools.SearchComboBox;
import java.io.File;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

public class SuministroTB {

    private int id;
    private String idSuministro;
    private int origen;
    private SimpleStringProperty origenName;
    private String clave;
    private String claveAlterna;
    private String nombreMarca;
    private String nombreGenerico;
    private int categoria;
    private String categoriaName;
    private int marcar;
    private String marcaName;
    private int unidadCompra;
    private String unidadCompraName;
    private int unidadVenta;
    private String unidadVentaName;
    private int presentacion;
    private String presentacionName;
    private int estado;
    private String estadoName;

    private double stockMinimo;
    private double stockMaximo;
    private double cantidad;
    private double costoCompra;
    private double costoCompraReal;
    private double porLlevar;

    private double precioVentaGeneral;
    private double precioVentaGeneralReal;
    private double precioVentaGeneralUnico;
    private double precioVentaGeneralAuxiliar;
    private short precioMargenGeneral;
    private double precioUtilidadGeneral;

    private PreciosTB preciosTB;

    private boolean lote;
    private boolean inventario;
    private short valorInventario;
    private ImageView imageValorInventario;
    private ImageView imageLote;
    private double descuento;
    private double descuentoSumado;
    private double descuentoCalculado;
    
    private double movimiento;
    private double diferencia;

    private double importeBruto;
    private double subImporteNeto;
    private double importeNeto;
    //private String imagenTB;
    private File imagenFile;
    private String claveSat;
    private boolean tipoPrecio;
    private byte[] nuevaImagen;

    private int impuestoOperacion;
    private int impuestoId;
    private String impuestoNombre;
    private double impuestoSumado;
    private double impuestoValor;

    private String descripcion;
    private Label lblCantidad;
    private TextField txtCantidad;
    private TextField txtStockMinimo;
    private TextField txtStockMaximo;
    private TextField txtMovimiento;
    private CheckBox validar;
    private Label estadoAsignacion;
    private Button btnRemove;
    private boolean cambios;
    private ImpuestoTB impuestoTB;
    private double bonificacion;
    private TextField txtMerma;
    private ComboBox<SuministroTB> cbSuministro;
    private SearchComboBox<SuministroTB> searchComboBox;
    
    public SuministroTB() {
        
    }

    public SuministroTB(String idSuministro, String clave, String nombreMarca) {
        this.idSuministro = idSuministro;
        this.clave = clave;
        this.nombreMarca = nombreMarca;
    }   
    
    public SuministroTB(String clave, String nombreMarca) {
        this.clave = clave;
        this.nombreMarca = nombreMarca;
    }
        
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrigen() {
        return origen;
    }

    public void setOrigen(int origen) {
        this.origen = origen;
    }

    public SimpleStringProperty getOrigenName() {
        return origenName;
    }

    public void setOrigenName(SimpleStringProperty origenName) {
        this.origenName = origenName;
    }

    public String getIdSuministro() {
        return idSuministro;
    }

    public void setIdSuministro(String idSuministro) {
        this.idSuministro = idSuministro;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave == null ? "" : clave;
    }

    public String getClaveAlterna() {
        return claveAlterna;
    }

    public void setClaveAlterna(String claveAlterna) {
        this.claveAlterna = claveAlterna == null ? "" : claveAlterna;
    }

    public String getNombreMarca() {
        return nombreMarca;
    }

    public void setNombreMarca(String nombreMarca) {
        this.nombreMarca = nombreMarca == null ? "" : nombreMarca;
    }

    public String getNombreGenerico() {
        return nombreGenerico;
    }

    public void setNombreGenerico(String nombreGenerico) {
        this.nombreGenerico = nombreGenerico;
    }

//    public String getImagenTB() {
//        return imagenTB;
//    }
//
//    public void setImagenTB(String imagenTB) {
//        this.imagenTB = imagenTB == null ? "" : imagenTB;
//    }
    public File getImagenFile() {
        return imagenFile;
    }

    public void setImagenFile(File imagenFile) {
        this.imagenFile = imagenFile;
    }

    public int getCategoria() {
        return categoria;
    }

    public void setCategoria(int categoria) {
        this.categoria = categoria;
    }

    public String getCategoriaName() {
        return categoriaName;
    }

    public void setCategoriaName(String categoriaName) {
        this.categoriaName = categoriaName == null ? "" : categoriaName;
    }

    public int getMarcar() {
        return marcar;
    }

    public void setMarcar(int marcar) {
        this.marcar = marcar;
    }

    public String getMarcaName() {
        return marcaName;
    }

    public void setMarcaName(String marcaName) {
        this.marcaName = marcaName == null ? "" : marcaName;
    }

    public int getUnidadCompra() {
        return unidadCompra;
    }

    public void setUnidadCompra(int unidadCompra) {
        this.unidadCompra = unidadCompra;
    }

    public String getUnidadCompraName() {
        return unidadCompraName;
    }

    public void setUnidadCompraName(String unidadCompraName) {
        this.unidadCompraName = unidadCompraName == null ? "" : unidadCompraName;
    }

    public int getPresentacion() {
        return presentacion;
    }

    public void setPresentacion(int presentacion) {
        this.presentacion = presentacion;
    }

    public String getPresentacionName() {
        return presentacionName;
    }

    public void setPresentacionName(String presentacionName) {
        this.presentacionName = presentacionName;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public boolean isLote() {
        return lote;
    }

    public void setLote(boolean lote) {
        this.lote = lote;
    }

    public String getEstadoName() {
        return estadoName;
    }

    public void setEstadoName(String estadoName) {
        this.estadoName = estadoName;
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

    public double getCostoCompra() {
        return costoCompra;
    }

    public void setCostoCompra(double costoCompra) {
        this.costoCompra = costoCompra;
    }

    public double getCostoCompraReal() {
        return costoCompraReal;
    }

    public void setCostoCompraReal(double costoCompraReal) {
        this.costoCompraReal = costoCompraReal;
    }

    public double getPorLlevar() {
        return porLlevar;
    }

    public void setPorLlevar(double porLlevar) {
        this.porLlevar = porLlevar;
    }

    public PreciosTB getPreciosTB() {
        return preciosTB;
    }

    public void setPreciosTB(PreciosTB preciosTB) {
        this.preciosTB = preciosTB;
    }

    public double getPrecioVentaGeneral() {
        return precioVentaGeneral;
    }

    public void setPrecioVentaGeneral(double precioVentaGeneral) {
        this.precioVentaGeneral = precioVentaGeneral;
    }

    public short getPrecioMargenGeneral() {
        return precioMargenGeneral;
    }

    public void setPrecioMargenGeneral(short precioMargenGeneral) {
        this.precioMargenGeneral = precioMargenGeneral;
    }

    public double getPrecioUtilidadGeneral() {
        return precioUtilidadGeneral;
    }

    public void setPrecioUtilidadGeneral(double precioUtilidadGeneral) {
        this.precioUtilidadGeneral = precioUtilidadGeneral;
    }

    public double getPrecioVentaGeneralReal() {
        return precioVentaGeneralReal;
    }

    public void setPrecioVentaGeneralReal(double precioVentaGeneralReal) {
        this.precioVentaGeneralReal = precioVentaGeneralReal;
    }

    public double getPrecioVentaGeneralUnico() {
        return precioVentaGeneralUnico;
    }

    public void setPrecioVentaGeneralUnico(double precioVentaGeneralUnico) {
        this.precioVentaGeneralUnico = precioVentaGeneralUnico;
    }

    public double getPrecioVentaGeneralAuxiliar() {
        return precioVentaGeneralAuxiliar;
    }

    public void setPrecioVentaGeneralAuxiliar(double precioVentaGeneralAuxiliar) {
        this.precioVentaGeneralAuxiliar = precioVentaGeneralAuxiliar;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }
    
    public double getDescuento() {
        return descuento;
    }

    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }

    public double getDescuentoSumado() {
        return descuentoSumado;
    }

    public void setDescuentoSumado(double descuentoSumado) {
        this.descuentoSumado = descuentoSumado;
    }

    public double getDescuentoCalculado() {
        return descuentoCalculado;
    }

    public void setDescuentoCalculado(double descuentoCalculado) {
        this.descuentoCalculado = descuentoCalculado;
    }

    public double getImporteBruto() {
        return importeBruto;
    }

    public void setImporteBruto(double importeBruto) {
        this.importeBruto = importeBruto;
    }

    public double getSubImporteNeto() {
        return subImporteNeto;
    }

    public void setSubImporteNeto(double subImporteNeto) {
        this.subImporteNeto = subImporteNeto;
    }

    public double getMovimiento() {
        return movimiento;
    }

    public void setMovimiento(double movimiento) {
        this.movimiento = movimiento;
    }

    public double getDiferencia() {
        return diferencia;
    }

    public void setDiferencia(double diferencia) {
        this.diferencia = diferencia;
    }

    public double getImporteNeto() {
        return importeNeto;
    }

    public void setImporteNeto(double importeNeto) {
        this.importeNeto = importeNeto;
    }    

    public ImageView getImageValorInventario() {
        return imageValorInventario;
    }

    public void setImageValorInventario(ImageView imageValorInventario) {
        this.imageValorInventario = imageValorInventario;
    }

    public ImageView getImageLote() {
        return imageLote;
    }

    public void setImageLote(ImageView imageLote) {
        this.imageLote = imageLote;
    }

    public boolean isInventario() {
        return inventario;
    }

    public void setInventario(boolean inventario) {
        this.inventario = inventario;
    }

    public short getValorInventario() {
        return valorInventario;
    }

    public void setValorInventario(short valorInventario) {
        this.valorInventario = valorInventario;
    }

    public int getUnidadVenta() {
        return unidadVenta;
    }

    public void setUnidadVenta(int unidadVenta) {
        this.unidadVenta = unidadVenta;
    }

    public String getUnidadVentaName() {
        return unidadVentaName;
    }

    public void setUnidadVentaName(String unidadVentaName) {
        this.unidadVentaName = unidadVentaName;
    }

    public int getImpuestoOperacion() {
        return impuestoOperacion;
    }

    public void setImpuestoOperacion(int impuestoOperacion) {
        this.impuestoOperacion = impuestoOperacion;
    }

    public int getImpuestoId() {
        return impuestoId;
    }

    public void setImpuestoId(int impuestoId) {
        this.impuestoId = impuestoId;
    }

    public String getImpuestoNombre() {
        return impuestoNombre;
    }

    public void setImpuestoNombre(String impuestoNombre) {
        this.impuestoNombre = impuestoNombre == null ? "" : impuestoNombre;
    }

    public double getImpuestoSumado() {
        return impuestoSumado;
    }

    public void setImpuestoSumado(double impuestoSumado) {
        this.impuestoSumado = impuestoSumado;
    }

    public double getImpuestoValor() {
        return impuestoValor;
    }

    public void setImpuestoValor(double impuestoValor) {
        this.impuestoValor = impuestoValor;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion == null ? "" : descripcion;
    }

    public Label getLblCantidad() {
        return lblCantidad;
    }

    public void setLblCantidad(Label lblCantidad) {
        this.lblCantidad = lblCantidad;
    }

    public TextField getTxtCantidad() {
        return txtCantidad;
    }

    public void setTxtCantidad(TextField txtCantidad) {
        this.txtCantidad = txtCantidad;
    }

    public TextField getTxtStockMinimo() {
        return txtStockMinimo;
    }

    public void setTxtStockMinimo(TextField txtStockMinimo) {
        this.txtStockMinimo = txtStockMinimo;
    }

    public TextField getTxtStockMaximo() {
        return txtStockMaximo;
    }

    public void setTxtStockMaximo(TextField txtStockMaximo) {
        this.txtStockMaximo = txtStockMaximo;
    }

    public TextField getTxtMovimiento() {
        return txtMovimiento;
    }

    public void setTxtMovimiento(TextField txtMovimiento) {
        this.txtMovimiento = txtMovimiento;
    }

    public CheckBox getValidar() {
        return validar;
    }

    public void setValidar(CheckBox validar) {
        this.validar = validar;
    }

    public Label getEstadoAsignacion() {
        return estadoAsignacion;
    }

    public void setEstadoAsignacion(Label estadoAsignacion) {
        this.estadoAsignacion = estadoAsignacion;
    }

    public String getClaveSat() {
        return claveSat;
    }

    public void setClaveSat(String claveSat) {
        this.claveSat = claveSat == null ? "" : claveSat;
    }

    public boolean isTipoPrecio() {
        return tipoPrecio;
    }

    public void setTipoPrecio(boolean tipoPrecio) {
        this.tipoPrecio = tipoPrecio;
    }

    public Button getBtnRemove() {
        return btnRemove;
    }

    public void setBtnRemove(Button remover) {
        this.btnRemove = remover;
    }

    public boolean isCambios() {
        return cambios;
    }

    public void setCambios(boolean cambios) {
        this.cambios = cambios;
    }

    public byte[] getNuevaImagen() {
        return nuevaImagen;
    }

    public void setNuevaImagen(byte[] nuevaImagen) {
        this.nuevaImagen = nuevaImagen;
    }

    public ImpuestoTB getImpuestoTB() {
        return impuestoTB;
    }

    public void setImpuestoTB(ImpuestoTB impuestoTB) {
        this.impuestoTB = impuestoTB;
    }

    public double getBonificacion() {
        return bonificacion;
    }

    public void setBonificacion(double bonificacion) {
        this.bonificacion = bonificacion;
    }

    public TextField getTxtMerma() {
        return txtMerma;
    }

    public void setTxtMerma(TextField txtMerma) {
        this.txtMerma = txtMerma;
    }

    public ComboBox<SuministroTB> getCbSuministro() {
        return cbSuministro;
    }

    public void setCbSuministro(ComboBox<SuministroTB> cbSuministro) {
        this.cbSuministro = cbSuministro;
    }

    public SearchComboBox<SuministroTB> getSearchComboBox() {
        return searchComboBox;
    }

    public void setSearchComboBox(SearchComboBox<SuministroTB> searchComboBox) {
        this.searchComboBox = searchComboBox;
    }

    @Override
    public String toString() {
        return nombreMarca;
    }

}
