package model;

import java.time.LocalDate;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

public class ArticuloTB {

    private int id;
    private String idArticulo;
    private int origen;
    private String origenName;
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
    private SimpleStringProperty estadoName;

    private double stockMinimo;
    private double stockMaximo;
    private double cantidad;
    private double cantidadGranel;
    private double costoCompra;
    private double costoCompraReal;

    private double precioVentaGeneral;
    private double precioVentaGeneralReal;
    private short precioMargenGeneral;
    private double precioUtilidadGeneral;

    private double precioVentaAuxiliar;
    private double precioVentaUnico;

    private PreciosTB preciosTB;

    private boolean lote;
    private boolean inventario;
    private boolean valorInventario;
    private ImageView imageLote;
    private double descuento;
    private double descuentoSumado;
    private double subImporte;

    private double subImporteDescuento;

    private double totalImporte;
    private String imagenTB;
    private ObjectProperty<LocalDate> fechaRegistro;

    private int impuestoArticulo;
    private String impuestoArticuloName;
    private double impuestoValor;
    private double impuestoSumado;

    private String descripcion;

    private TextField movimiento;

    private CheckBox validar;

    public ArticuloTB() {

    }

    public ArticuloTB(String nombreMarca, int unidadVenta) {
        this.nombreMarca = nombreMarca;
        this.unidadVenta = unidadVenta;
    }

    public ArticuloTB(String idArticulo, String nombreMarca, String unidadCompraName) {
        this.idArticulo = idArticulo;
        this.nombreMarca = nombreMarca;
        this.unidadCompraName = unidadCompraName;
    }

    public ArticuloTB(String clave, String nombreMarca) {
        this.clave = clave;
        this.nombreMarca = nombreMarca;
    }

    public ArticuloTB(String claveAlterna, String nombreMarca, boolean estado) {
        this.claveAlterna = claveAlterna;
        this.nombreMarca = nombreMarca;
    }

    public ArticuloTB(int impuestoArticulo, String impuestoArticuloName, double impuestoSumado) {
        this.impuestoArticulo = impuestoArticulo;
        this.impuestoArticuloName = impuestoArticuloName;
        this.impuestoSumado = impuestoSumado;
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

    public String getOrigenName() {
        return origenName;
    }

    public void setOrigenName(String origenName) {
        this.origenName = origenName;
    }

    public String getIdArticulo() {
        return idArticulo;
    }

    public void setIdArticulo(String idArticulo) {
        this.idArticulo = idArticulo;
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
        this.nombreMarca = nombreMarca == null ? "" : nombreMarca;
    }

    public String getNombreGenerico() {
        return nombreGenerico;
    }

    public void setNombreGenerico(String nombreGenerico) {
        this.nombreGenerico = nombreGenerico;
    }

    public String getImagenTB() {
        return imagenTB;
    }

    public void setImagenTB(String imagenTB) {
        this.imagenTB = imagenTB == null ? "" : imagenTB;
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

    public SimpleStringProperty getEstadoName() {
        return estadoName;
    }

    public void setEstadoName(String estadoName) {
        this.estadoName = new SimpleStringProperty(estadoName);
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

    public double getPrecioVentaAuxiliar() {
        return precioVentaAuxiliar;
    }

    public void setPrecioVentaAuxiliar(double precioVentaAuxiliar) {
        this.precioVentaAuxiliar = precioVentaAuxiliar;
    }

    public double getPrecioVentaUnico() {
        return precioVentaUnico;
    }

    public void setPrecioVentaUnico(double precioVentaUnico) {
        this.precioVentaUnico = precioVentaUnico;
    }

    public double getPrecioVentaGeneralReal() {
        return precioVentaGeneralReal;
    }

    public void setPrecioVentaGeneralReal(double precioVentaGeneralReal) {
        this.precioVentaGeneralReal = precioVentaGeneralReal;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    public double getCantidadGranel() {
        return cantidadGranel;
    }

    public void setCantidadGranel(double cantidadGranel) {
        this.cantidadGranel = cantidadGranel;
    }

    public double getDescuento() {
        return descuento;
    }

    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }

    public double getSubImporte() {
        return subImporte;
    }

    public void setSubImporte(double subImporte) {
        this.subImporte = subImporte;
    }

    public double getSubImporteDescuento() {
        return subImporteDescuento;
    }

    public void setSubImporteDescuento(double subImporteDescuento) {
        this.subImporteDescuento = subImporteDescuento;
    }

    public double getTotalImporte() {
        return totalImporte;
    }

    public void setTotalImporte(double totalImporte) {
        this.totalImporte = totalImporte;
    }

    public ImageView getImageLote() {
        return imageLote;
    }

    public void setImageLote(ImageView imageLote) {
        this.imageLote = imageLote;
    }

    public ObjectProperty<LocalDate> getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = new SimpleObjectProperty(fechaRegistro);
    }

    public boolean isInventario() {
        return inventario;
    }

    public void setInventario(boolean inventario) {
        this.inventario = inventario;
    }

    public boolean isValorInventario() {
        return valorInventario;
    }

    public void setValorInventario(boolean valorInventario) {
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

    public int getImpuestoArticulo() {
        return impuestoArticulo;
    }

    public void setImpuestoArticulo(int impuestoArticulo) {
        this.impuestoArticulo = impuestoArticulo;
    }

    public String getImpuestoArticuloName() {
        return impuestoArticuloName;
    }

    public void setImpuestoArticuloName(String impuestoArticuloName) {
        this.impuestoArticuloName = impuestoArticuloName;
    }

    public double getImpuestoValor() {
        return impuestoValor;
    }

    public void setImpuestoValor(double impuestoValor) {
        this.impuestoValor = impuestoValor;
    }

    public double getImpuestoSumado() {
        return impuestoSumado;
    }

    public void setImpuestoSumado(double impuestoSumado) {
        this.impuestoSumado = impuestoSumado;
    }

    public double getDescuentoSumado() {
        return descuentoSumado;
    }

    public void setDescuentoSumado(double descuentoSumado) {
        this.descuentoSumado = descuentoSumado;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public TextField getMovimiento() {
        return movimiento;
    }

    public void setMovimiento(TextField movimiento) {
        this.movimiento = movimiento;
    }

    public CheckBox getValidar() {
        return validar;
    }

    public void setValidar(CheckBox validar) {
        this.validar = validar;
    }
    
    

}
