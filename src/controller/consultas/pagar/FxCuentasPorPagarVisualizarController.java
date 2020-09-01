package controller.consultas.pagar;

import controller.consultas.compras.FxComprasDetalleController;
import controller.operaciones.compras.FxAmortizarPagosController;
import controller.reporte.FxReportViewController;
import controller.tools.ConvertMonedaCadena;
import controller.tools.FilesRouters;
import controller.tools.ObjectGlobal;
import controller.tools.Session;
import controller.tools.Tools;
import controller.tools.WindowStage;
import java.awt.HeadlessException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.CompraADO;
import model.CompraCreditoTB;
import model.CompraTB;
import model.ProveedorTB;
import model.TransaccionTB;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class FxCuentasPorPagarVisualizarController implements Initializable {

    @FXML
    private ScrollPane spWindow;
    @FXML
    private Label lblLoad;
    @FXML
    private Label lblProveedor;
    @FXML
    private Label lblComprobante;
    @FXML
    private Label lblEstado;
    @FXML
    private Label lblTotalCompra;
    @FXML
    private Label lblTelefonoCelular;
    @FXML
    private Label lblDireccion;
    @FXML
    private Label lblEmail;
    @FXML
    private GridPane gpList;
    @FXML
    private Label lblTotal;
    @FXML
    private Label lblMontoPagado;
    @FXML
    private Label lblDiferencia;
    @FXML
    private Button btnAmortizar;

    private AnchorPane vbPrincipal;

    private AnchorPane vbContent;

    private FxCuentasPorPagarController cuentasPorPagarController;

    private ObservableList<CompraCreditoTB> empList;

    private ConvertMonedaCadena monedaCadena;

    private String idCompra;

    private double total;

    private double pagado;

    private double diferencia;

    private double monto;

    private Alert alert = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        monedaCadena = new ConvertMonedaCadena();
    }

    public void loadData(String idCompra, double total) {
        this.idCompra = idCompra;
        lblTotal.setText(Tools.roundingValue(total, 2));
        loadTableCompraCredito(this.idCompra);
    }

    public void validarEstadoCompra() {
        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<String> task = new Task<String>() {
            @Override
            public String call() {
                return CompraADO.Actualizar_Compra_Estado(idCompra);

            }
        };
        task.setOnScheduled(w -> {
            ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
            alert = Tools.AlertMessage(spWindow.getScene().getWindow(), Alert.AlertType.NONE, "Procesando Información...");
        });
        task.setOnFailed(w -> {
            if (alert != null) {
                ((Stage) (alert.getDialogPane().getScene().getWindow())).close();
            }
            Tools.AlertMessageWarning(spWindow, "Cuentas por pagar", "Error en la ejecución, intente nuevamente.");
            vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
        });
        task.setOnSucceeded(w -> {
            if (!task.isRunning()) {
                if (alert != null) {
                    ((Stage) (alert.getDialogPane().getScene().getWindow())).close();
                }
            }
            String result = task.getValue();
            if (result.equalsIgnoreCase("updated")) {
                Tools.AlertMessageInformation(spWindow, "Cuentas por pagar", "Se actualizó correctamente los cambios.");
                vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
            } else {
                Tools.AlertMessageError(spWindow, "Cuentas por pagar", "Se produjo un error, actualize manualmente los cambios.");
                vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
            }
        });
        exec.execute(task);
        if (!exec.isShutdown()) {
            exec.shutdown();
        }
    }

    public void loadTableCompraCredito(String idCompra) {
        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<ArrayList<Object>> task = new Task<ArrayList<Object>>() {
            @Override
            public ArrayList<Object> call() {
                return CompraADO.Listar_Compra_Credito(idCompra);
            }
        };
        task.setOnScheduled(w -> {
            lblLoad.setVisible(true);
        });
        task.setOnFailed(w -> {
            lblLoad.setVisible(false);
        });
        task.setOnSucceeded(w -> {
            ArrayList<Object> objects = task.getValue();
            if (!objects.isEmpty()) {
                if (objects.get(0) != null) {
                    CompraTB compraTB = (CompraTB) objects.get(0);
                    if (compraTB != null) {
                        lblProveedor.setText(compraTB.getProveedorTB().getNumeroDocumento() + " - " + compraTB.getProveedorTB().getRazonSocial());
                        lblTelefonoCelular.setText("Teléfono: " + (compraTB.getProveedorTB().getTelefono().equalsIgnoreCase("") ? "Teléfono no registro" : compraTB.getProveedorTB().getTelefono()) + " Celular: " + (compraTB.getProveedorTB().getCelular().equalsIgnoreCase("") ? "Celular no registrado" : compraTB.getProveedorTB().getCelular()));
                        lblDireccion.setText(compraTB.getProveedorTB().getDireccion().equalsIgnoreCase("") ? "Dirección no registrada" : compraTB.getProveedorTB().getDireccion());
                        lblEmail.setText(compraTB.getProveedorTB().getEmail().equalsIgnoreCase("") ? "Email no registro" : compraTB.getProveedorTB().getEmail());
                        lblComprobante.setText(compraTB.getSerie().toUpperCase() + "-" + compraTB.getNumeracion().toUpperCase());
                        lblEstado.setText(compraTB.getEstadoName());
                        lblTotalCompra.setText(Tools.roundingValue(compraTB.getTotal(), 4));
                        btnAmortizar.setDisable((compraTB.getEstado() == 3));
                    }
                }

                if (objects.get(1) != null) {
                    ObservableList<CompraCreditoTB> observableList = (ObservableList<CompraCreditoTB>) objects.get(1);
                    observableList.forEach(e -> {
                        total += e.getMonto();
                        if (e.getCbSeleccion().isSelected()) {
                            pagado += e.getMonto();
                        }
                        e.getBtnImprimir().setOnAction(event -> {
                            openWindowReport(e.getIdTransaccion());
                        });
                        e.getBtnImprimir().setOnKeyPressed(event -> {
                            if (event.getCode() == KeyCode.ENTER) {
                                openWindowReport(e.getIdTransaccion());
                            }
                        });
                    });
                    diferencia = total - pagado;
                    lblTotal.setText(Tools.roundingValue(total, 2));
                    lblMontoPagado.setText(Tools.roundingValue(pagado, 2));
                    lblDiferencia.setText(Tools.roundingValue(diferencia, 2));
                    total = pagado = diferencia = 0;
                    fillVentasDetalleTable(observableList);
                }

                lblLoad.setVisible(false);
            } else {
                lblLoad.setVisible(false);

            }

        });
        exec.execute(task);
        if (!exec.isShutdown()) {
            exec.shutdown();
        }
    }

    private void fillVentasDetalleTable(ObservableList<CompraCreditoTB> empList) {
        this.empList = empList;
        for (int i = 0; i < this.empList.size(); i++) {
            gpList.add(addElementGridPane("l1" + (i + 1), this.empList.get(i).getId() + "", Pos.CENTER, null), 0, (i + 1));
            gpList.add(addElementGridPane("l2" + (i + 1), "", Pos.CENTER, this.empList.get(i).getCbSeleccion()), 1, (i + 1));
            gpList.add(addElementGridPane("l3" + (i + 1), this.empList.get(i).getCuota(), Pos.CENTER, null), 2, (i + 1));
            gpList.add(addElementGridPane("l4" + (i + 1), this.empList.get(i).getIdTransaccion().equalsIgnoreCase("") ? "--" : this.empList.get(i).getIdTransaccion(), Pos.CENTER, null), 3, (i + 1));
            gpList.add(addElementGridPane("l5" + (i + 1), this.empList.get(i).isEstado() ? this.empList.get(i).getFechaPago() + "\n" + this.empList.get(i).getHoraPago() : "--", Pos.CENTER, null), 4, (i + 1));
            gpList.add(addElementGridPane("l6" + (i + 1), "", Pos.CENTER, this.empList.get(i).getTxtEstado()), 5, (i + 1));
            gpList.add(addElementGridPane("l7" + (i + 1), Session.MONEDA_SIMBOLO + " " + Tools.roundingValue(this.empList.get(i).getMonto(), 2), Pos.CENTER, null), 6, (i + 1));
            gpList.add(addElementGridPane("l8" + (i + 1), "", Pos.CENTER, this.empList.get(i).getBtnImprimir()), 7, (i + 1));
        }
    }

    private Label addElementGridPane(String id, String nombre, Pos pos, Node node) {
        Label label = new Label(nombre);
        label.setId(id);
        label.setGraphic(node);
        label.setStyle("-fx-text-fill:#020203;-fx-background-color: #dddddd;-fx-padding: 0.4166666666666667em 0.8333333333333334em 0.4166666666666667em 0.8333333333333334em;");
        label.getStyleClass().add("labelRoboto14");
        label.setAlignment(pos);
        label.setWrapText(true);
        label.setPrefWidth(Control.USE_COMPUTED_SIZE);
        label.setPrefHeight(Control.USE_COMPUTED_SIZE);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setMaxHeight(Double.MAX_VALUE);
        return label;
    }

    private void onEventAmortizar() {
        if (!idCompra.equalsIgnoreCase("") || idCompra != null) {
            int value = 0;
            value = empList.stream().map((CompraCreditoTB cctb) -> cctb.getCbSeleccion().isSelected() && !cctb.getCbSeleccion().isDisable() ? 1 : 0).reduce(value, Integer::sum);
            if (value == 0) {
                Tools.AlertMessageWarning(spWindow, "Generar Pago", "Seleccione un elemento de la lista para continuar.");
                return;
            }
            try {
                ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
                URL url = getClass().getResource(FilesRouters.FX_AMARTIZAR_PAGOS);
                FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
                Parent parent = fXMLLoader.load(url.openStream());
                //Controlller here
                FxAmortizarPagosController controller = fXMLLoader.getController();
                controller.setInitValues(idCompra, lblComprobante.getText(), empList);
                controller.setInitAmortizarPagosController(this);
                //
                Stage stage = WindowStage.StageLoaderModal(parent, "Generar Pago", spWindow.getScene().getWindow());
                stage.setResizable(false);
                stage.sizeToScene();
                stage.setOnHiding(w -> vbPrincipal.getChildren().remove(ObjectGlobal.PANE));
                stage.show();
            } catch (IOException ex) {
                System.out.println("Controller banco" + ex.getLocalizedMessage());
            }
        } else {
            Tools.AlertMessageWarning(spWindow, "Generar Pago", "No se puedo cargar los datos, intente nuevamente.");
        }
    }

    public void openWindowReport(String idTransaccion) {

        if (idTransaccion.equals("") || idTransaccion == null) {
            Tools.AlertMessageWarning(spWindow, "Amortizar pago", "No se pudo generar el reporte por problemas con el número de transacción..");
            return;
        }

        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });
        try {

            Task<ArrayList<Object>> task = new Task<ArrayList<Object>>() {
                @Override
                public ArrayList<Object> call() {
                    return CompraADO.Listar_Compra_Credito_By_IdTransaccion(idTransaccion);
                }
            };

            task.setOnScheduled(w -> {
                Tools.println("inicio");
            });

            task.setOnFailed(w -> {
                Tools.println("fallo");
            });

            task.setOnSucceeded(w -> {
                try {
                    ArrayList<Object> objects = task.getValue();
                    if (objects == null) {
                        Tools.AlertMessageWarning(spWindow, "Amortizar pago", "No se pudo crear el reporte por problemas de conexión intente nuevamente.");
                    } else if (objects.get(0) == null && objects.get(1) == null && objects.get(2) == null) {
                        Tools.AlertMessageWarning(spWindow, "Amortizar pago", "No se pudo crear el reporte por problemas de conexión intente nuevamente.");
                    } else {

                        CompraTB compraTB = (CompraTB) objects.get(0);
                        ArrayList<CompraCreditoTB> empListThis = (ArrayList<CompraCreditoTB>) objects.get(1);
                        TransaccionTB transaccionTB = (TransaccionTB) objects.get(2);

                        double montoPagar = 0;
                        for (CompraCreditoTB cc : empListThis) {
                            montoPagar += cc.getMonto();
                        }

                        InputStream dir = getClass().getResourceAsStream("/report/CompraAmortizarPago.jasper");

                        InputStream imgInputStream = getClass().getResourceAsStream(FilesRouters.IMAGE_LOGO);

                        if (Session.COMPANY_IMAGE != null) {
                            imgInputStream = new ByteArrayInputStream(Session.COMPANY_IMAGE);
                        }

                        Map map = new HashMap();
                        map.put("LOGO", imgInputStream);

                        map.put("NOMBRE_EMPRESA", Session.COMPANY_RAZON_SOCIAL);
                        map.put("NUMERODOCUMENTO_EMPRESA", Session.COMPANY_NUMERO_DOCUMENTO);
                        map.put("DIRECCION_EMPRESA", Session.COMPANY_DOMICILIO.equalsIgnoreCase("") ? "Domicio no registrado" : Session.COMPANY_DOMICILIO);
                        map.put("TELEFONOS_EMPRESA", (Session.COMPANY_TELEFONO.equalsIgnoreCase("") ? "Teléfono no registrado" : Session.COMPANY_TELEFONO) + " " + (Session.COMPANY_CELULAR.equalsIgnoreCase("") ? "Celular no registrado" : Session.COMPANY_CELULAR));
                        map.put("EMAIL_EMPRESA", Session.COMPANY_EMAIL.equalsIgnoreCase("") ? "Email no registrado" : Session.COMPANY_EMAIL);
                        map.put("PAGINAWEB_EMPRESA", Session.COMPANY_PAGINAWEB.equalsIgnoreCase("") ? "Pagina web no registrada" : Session.COMPANY_PAGINAWEB);
//
                        map.put("NUMERODOCUMENTO_PROVEEDOR", compraTB.getProveedorTB().getNumeroDocumento());
                        map.put("INFORMACION_PROVEEDOR", compraTB.getProveedorTB().getRazonSocial());
                        map.put("TELEFONO_PROVEEDOR", compraTB.getProveedorTB().getTelefono().equalsIgnoreCase("") ? "Teléfono no registrado" : compraTB.getProveedorTB().getTelefono());
                        map.put("CELULAR_PROVEEDOR", compraTB.getProveedorTB().getCelular().equalsIgnoreCase("") ? "Celular no registrado" : compraTB.getProveedorTB().getCelular());
                        map.put("EMAIL_PROVEEDOR", compraTB.getProveedorTB().getEmail().equalsIgnoreCase("") ? "Email no registrado" : compraTB.getProveedorTB().getEmail());
                        map.put("DIRECCION_PROVEEDOR", compraTB.getProveedorTB().getDireccion().equalsIgnoreCase("") ? "Dirección no registrada" : compraTB.getProveedorTB().getDireccion());

                        map.put("NUM_TRANSACCION", idTransaccion);
                        map.put("FECHA_PAGO", transaccionTB.getFecha());
                        map.put("METODO_PAGO", "EFECTIVO");

                        map.put("TOTAL_LETRAS", monedaCadena.Convertir(Tools.roundingValue(montoPagar, 2), true, Session.MONEDA_NOMBRE));
                        map.put("TOTAL", Session.MONEDA_SIMBOLO + " " + Tools.roundingValue(montoPagar, 2));

                        JasperPrint jasperPrint = JasperFillManager.fillReport(dir, map, new JRBeanCollectionDataSource(empListThis));

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
                    }

                } catch (HeadlessException | JRException | IOException ex) {
                    Tools.AlertMessageError(spWindow, "Reporte de Pago", "Error al generar el reporte: " + ex.getLocalizedMessage());
                }
            });

            exec.execute(task);

        } catch (Exception ex) {
            Tools.AlertMessageError(spWindow, "Reporte de Pago", "Error en el ExecutorService: " + ex.getLocalizedMessage());
        } finally {
            if (!exec.isShutdown()) {
                exec.shutdown();
                Tools.println("detro...");
            }
            Tools.println("finally");
        }
    }

    private void onEventReporteGeneral() {
        try {
            ArrayList<CompraCreditoTB> list = new ArrayList();
            empList.stream().filter((cctb) -> (cctb.getCbSeleccion().isSelected() && cctb.getCbSeleccion().isDisable() && cctb.isEstado())).forEachOrdered((CompraCreditoTB cctb) -> {
                CompraCreditoTB compraCreditoTB = new CompraCreditoTB();
                compraCreditoTB.setId(1);
                compraCreditoTB.setIdTransaccion(cctb.getIdTransaccion());
                compraCreditoTB.setFechaPago("Se realizó el pago de la fecha del " + cctb.getFechaRegistro() + " por el monto de " + Tools.roundingValue(cctb.getMonto(), 2));
                compraCreditoTB.setMonto(cctb.getMonto());
                monto += cctb.getMonto();
                list.add(compraCreditoTB);
            });

            if (list.isEmpty()) {
                Tools.AlertMessageWarning(spWindow, "Compra realizada", "No hay registros para mostrar en el reporte.");
                return;
            }

            ProveedorTB proveedorTB = CompraADO.Obtener_Proveedor_Por_Id_Compra(idCompra);

            InputStream imgInputStream = getClass().getResourceAsStream(FilesRouters.IMAGE_LOGO);

            if (Session.COMPANY_IMAGE != null) {
                imgInputStream = new ByteArrayInputStream(Session.COMPANY_IMAGE);
            }

            Map map = new HashMap();
            map.put("LOGO", imgInputStream);
            map.put("EMPRESA", Session.COMPANY_RAZON_SOCIAL);
            map.put("DIRECCION", Session.COMPANY_DOMICILIO);
            map.put("TELEFONOCELULAR", "TEL.: " + Session.COMPANY_TELEFONO + " CEL.: " + Session.COMPANY_CELULAR);
            map.put("EMAIL", "EMAIL: " + Session.COMPANY_EMAIL);
            map.put("DOCUMENTOEMPRESA", "R.U.C " + Session.COMPANY_NUMERO_DOCUMENTO);

//            map.put("FECHA_EMISION", Tools.getDate("dd/MM/yyyy"));
            map.put("PROVEEDOR", proveedorTB.getRazonSocial());
            map.put("PROVEEDORNOMDOCUMENTO", proveedorTB.getTipoDocumentoName() + ":");
            map.put("PROVEEDORNUMDOCUMENTO", proveedorTB.getNumeroDocumento());
            map.put("PROVEEDORDIRECCION", proveedorTB.getDireccion().equalsIgnoreCase("") ? "Dirección no registrada" : proveedorTB.getDireccion());
            map.put("PROVEEDORTELEFONOS", (proveedorTB.getTelefono().equalsIgnoreCase("") ? "Teléfono no registrado" : proveedorTB.getTelefono()) + " - " + (proveedorTB.getCelular().equalsIgnoreCase("") ? "Celular no registrado" : proveedorTB.getCelular()));
            map.put("PROVEEDOREMAIL", proveedorTB.getEmail().equalsIgnoreCase("") ? "Email no registrada" : proveedorTB.getEmail());

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
            Tools.AlertMessageError(spWindow, "Reporte de Pago", "Error al generar el reporte : " + ex.getLocalizedMessage());
        }
    }

    @FXML
    private void onMouseClickedBehind(MouseEvent event) {
        vbContent.getChildren().remove(spWindow);
        vbContent.getChildren().clear();
        AnchorPane.setLeftAnchor(cuentasPorPagarController.getVbWindow(), 0d);
        AnchorPane.setTopAnchor(cuentasPorPagarController.getVbWindow(), 0d);
        AnchorPane.setRightAnchor(cuentasPorPagarController.getVbWindow(), 0d);
        AnchorPane.setBottomAnchor(cuentasPorPagarController.getVbWindow(), 0d);
        vbContent.getChildren().add(cuentasPorPagarController.getVbWindow());
    }

    @FXML
    private void onKeyPressedAmortizar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onEventAmortizar();
        }
    }

    @FXML
    private void onActionAmortizar(ActionEvent event) {
        onEventAmortizar();
    }

    @FXML
    private void onKeyPressedReporte(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onEventReporteGeneral();
        }
    }

    @FXML
    private void onActionReporte(ActionEvent event) {
        onEventReporteGeneral();
    }

    public void setInitCuentasPorPagar(AnchorPane vbPrincipal, AnchorPane vbContent, FxCuentasPorPagarController cuentasPorPagarController) {
        this.vbPrincipal = vbPrincipal;
        this.vbContent = vbContent;
        this.cuentasPorPagarController = cuentasPorPagarController;
    }

}
