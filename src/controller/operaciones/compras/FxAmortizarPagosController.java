package controller.operaciones.compras;

import controller.consultas.compras.FxComprasDetalleController;
import controller.operaciones.compras.FxComprasCreditoController;
import controller.reporte.FxReportViewController;
import controller.tools.FilesRouters;
import controller.tools.Session;
import controller.tools.Tools;
import controller.tools.WindowStage;
import java.awt.HeadlessException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.BancoADO;
import model.BancoHistorialTB;
import model.BancoTB;
import model.CompraADO;
import model.CompraCreditoTB;
import model.ProveedorTB;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class FxAmortizarPagosController implements Initializable {

    @FXML
    private AnchorPane apWindow;
    @FXML
    private TextField txtMonto;
    @FXML
    private DatePicker dtFecha;
    @FXML
    private ComboBox<BancoTB> cbCuenta;

    private FxComprasCreditoController creditoController;

    private TableView<CompraCreditoTB> tvList;

    private String idCompra;

    private double monto;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tools.DisposeWindow(apWindow, KeyEvent.KEY_RELEASED);
        Tools.actualDate(Tools.getDate(), dtFecha);
        cbCuenta.getItems().add(new BancoTB("0", "Seleccionar..."));
        BancoADO.GetBancoComboBox().forEach(e -> {
            cbCuenta.getItems().add(new BancoTB(e.getIdBanco(), e.getNombreCuenta()));
        });
        cbCuenta.getSelectionModel().select(0);
    }

    public void setInitValues(String idCompra, TableView<CompraCreditoTB> tvList) {
        this.idCompra = idCompra;
        this.tvList = tvList;
        tvList.getItems().forEach((cctb) -> {
            monto += cctb.getCbSeleccion().isSelected() && !cctb.getCbSeleccion().isDisable() ? cctb.getMonto() : 0;
        });
        txtMonto.setText(Tools.roundingValue(monto, 2));
    }

    private void openWindowReport() {
        try {
            ArrayList<CompraCreditoTB> list = new ArrayList();
            tvList.getItems().stream().filter((cctb) -> (cctb.getCbSeleccion().isSelected() && !cctb.getCbSeleccion().isDisable())).map((cctb) -> {
                CompraCreditoTB compraCreditoTB = new CompraCreditoTB();
                compraCreditoTB.setId(1);
                compraCreditoTB.setFechaPago("Se realizó el pago de la fecha del " + cctb.getFechaRegistro() + " por el monto de " + Tools.roundingValue(cctb.getMonto(), 2));
                compraCreditoTB.setMonto(cctb.getMonto());
                return compraCreditoTB;
            }).forEachOrdered((compraCreditoTB) -> {
                list.add(compraCreditoTB);
            });

            if (list.isEmpty()) {
                Tools.AlertMessageWarning(apWindow, "Compra realizada", "No hay registros para mostrar en el reporte.");
                return;
            }

            ProveedorTB proveedorTB = CompraADO.Obtener_Proveedor_Por_Id_Compra(idCompra);

            InputStream imgInputStream
                    = getClass().getResourceAsStream(FilesRouters.IMAGE_LOGO);

            Map map = new HashMap();
            map.put("LOGO", imgInputStream);
            map.put("EMPRESA", Session.COMPANY_RAZON_SOCIAL);
            map.put("DIRECCION", Session.COMPANY_DOMICILIO);
            map.put("TELEFONOCELULAR", "Tel.: " + Session.COMPANY_TELEFONO + " Cel.: " + Session.COMPANY_CELULAR);
            map.put("EMAIL", "Email: " + Session.COMPANY_EMAIL);
            map.put("DOCUMENTOEMPRESA", "R.U.C " + Session.COMPANY_NUM_DOCUMENTO);

            map.put("FECHA_EMISION", Tools.getDate("dd/MM/yyyy"));
            map.put("PROVEEDOR", proveedorTB.getRazonSocial());
            map.put("PROVEEDORNOMDOCUMENTO", proveedorTB.getTipoDocumentoName() + ":");
            map.put("PROVEEDORNUMDOCUMENTO", proveedorTB.getNumeroDocumento());
            map.put("PROVEEDORDIRECCION", proveedorTB.getDireccion());
            map.put("PROVEEDOROTRODATOS", proveedorTB.getTelefono() + " - " + proveedorTB.getCelular() + " - " + proveedorTB.getEmail());

            map.put("TOTAL", Tools.roundingValue(monto, 2));

            JasperPrint jasperPrint = JasperFillManager.fillReport(FxComprasDetalleController.class.getResourceAsStream("/report/CompraAmortizar.jasper"), map, new JRBeanCollectionDataSource(list));

            URL url = getClass().getResource(FilesRouters.FX_REPORTE_VIEW);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxReportViewController controller = fXMLLoader.getController();
            controller.setJasperPrint(jasperPrint);
            controller.show();
            Stage stage = WindowStage.StageLoader(parent, "Reporte de Pago");
            stage.setResizable(true);
            stage.show();
            stage.requestFocus();
        } catch (HeadlessException | JRException | IOException ex) {
            Tools.AlertMessageError(apWindow, "Reporte de Pago", "Error al generar el reporte : " + ex.getLocalizedMessage());
        }
    }

    private void eventGuardar() {
        if (dtFecha.getValue() == null) {
            Tools.AlertMessageWarning(apWindow, "Generar Pago", "Ingrese la fecha de abono");
            dtFecha.requestFocus();
        } else if (cbCuenta.getSelectionModel().getSelectedIndex() <= 0) {
            Tools.AlertMessageWarning(apWindow, "Generar Pago", "Seleccione el banco o caja");
            cbCuenta.requestFocus();
        } else {
            short value = Tools.AlertMessageConfirmation(apWindow, "Generar Pago", "¿Está seguro de continuar?");
            if (value == 1) {
                for (int i = 0; i < tvList.getItems().size(); i++) {
                    if (tvList.getItems().get(i).getCbSeleccion().isSelected() && !tvList.getItems().get(i).getCbSeleccion().isDisable()) {
                        tvList.getItems().get(i).setFechaPago(Tools.getDatePicker(dtFecha));
                        tvList.getItems().get(i).setHoraPago(Tools.getHour());
                        tvList.getItems().get(i).setEstado(true);
                    }
                }

                BancoHistorialTB bancoHistorialTB = new BancoHistorialTB();
                bancoHistorialTB.setIdBanco(cbCuenta.getSelectionModel().getSelectedItem().getIdBanco());
                bancoHistorialTB.setDescripcion("Salida de dinero por pago a proveedor");
                bancoHistorialTB.setFecha(Tools.getDate());
                bancoHistorialTB.setHora(Tools.getHour());
                bancoHistorialTB.setEntrada(0);
                bancoHistorialTB.setSalida(monto);

                String result = CompraADO.Registrar_Amortizacion(tvList, bancoHistorialTB);
                if (result.equalsIgnoreCase("updated")) {
                    Tools.AlertMessageInformation(apWindow, "Generar Pago", "Se registro correctamente el pago.");
                    ObservableList<CompraCreditoTB> observableList = CompraADO.Listar_Compra_Credito(idCompra);
                    openWindowReport();
                    int validateTotal = 0;
                    int validateActual = 0;
                    for (CompraCreditoTB creditoTB : observableList) {
                        if (creditoTB.isEstado()) {
                            validateActual++;
                        }
                        validateTotal++;
                    }
                    if (validateTotal == validateActual) {
                        String completed = CompraADO.Actualizar_Compra_Estado(idCompra);
                        if (completed.equalsIgnoreCase("updated")) {
                            Tools.AlertMessageInformation(apWindow, "Generar Pago", "Se completo todos los pagos.");
                            creditoController.loadTableCompraCredito();
                        }
                    } else {
                        creditoController.loadTableCompraCredito();
                    }
                    Tools.Dispose(apWindow);
                } else if (result.equalsIgnoreCase("pagado")) {
                    Tools.AlertMessageWarning(apWindow, "Generar Pago", "La cuota seleccionada ya está cancelada.");
                    Tools.Dispose(apWindow);
                } else {
                    Tools.AlertMessageError(apWindow, "Generar Pago", result);
                }
            }
        }
    }

    @FXML
    private void onKeyPressedGuardar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            eventGuardar();
        }
    }

    @FXML
    private void onActionGuardar(ActionEvent event) {
        eventGuardar();
    }

    @FXML
    private void onKeyPressedCancelar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            Tools.Dispose(apWindow);
        }
    }

    @FXML
    private void onActionCancelar(ActionEvent event) {
        Tools.Dispose(apWindow);
    }

    public void setInitAmortizarPagosController(FxComprasCreditoController creditoController) {
        this.creditoController = creditoController;
    }

}
