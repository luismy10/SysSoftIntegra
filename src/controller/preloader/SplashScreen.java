package controller.preloader;

import controller.tools.FilesRouters;
import controller.tools.Json;
import controller.tools.LoadFont;
import controller.tools.ObjectGlobal;
import controller.tools.Session;
import controller.tools.Tools;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.ClienteADO;
import model.ClienteTB;
import model.DBUtil;
import model.EmpresaADO;
import model.EmpresaTB;
import model.MonedaADO;
import model.TicketADO;
import model.TicketTB;
import org.json.simple.JSONObject;

public class SplashScreen extends Preloader {

    private Stage preloaderStage;

    private Scene scene;

    public SplashScreen() {
        Session.CONNECTION_SESSION = false;
        Session.CONFIGURATION_STATE = false;
    }

    @Override
    public void init() throws Exception {
        FXMLLoader fXMLPreloader = new FXMLLoader(getClass().getResource(FilesRouters.FX_PRELOADER));
        AnchorPane parent = fXMLPreloader.load();
        scene = new Scene(parent);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        preloaderStage = primaryStage;
        preloaderStage.getIcons().add(new Image(FilesRouters.IMAGE_ICON));
        preloaderStage.initStyle(StageStyle.UNDECORATED);
        preloaderStage.setScene(scene);
        preloaderStage.show();
    }

    @Override
    public void handleStateChangeNotification(StateChangeNotification info) {
        StateChangeNotification.Type type = info.getType();
        switch (type) {
            case BEFORE_LOAD:
                System.out.println("BEFORE_LOAD");
                break;
            case BEFORE_INIT:
                System.out.println("BEFORE_INIT");

                File archivoc = new File("./archivos/connection.json");
                if (archivoc.exists()) {
                    JSONObject jSONObject = Json.obtenerObjetoJSON(Json.leerArchivoTexto(archivoc.getAbsolutePath()));
                    if (jSONObject.get("body") != null) {
                        JSONObject object = Json.obtenerObjetoJSON(jSONObject.get("body").toString());
                        ObjectGlobal.ADDRES = String.valueOf(object.get("addres"));
                        ObjectGlobal.PORT = String.valueOf(object.get("port"));
                        ObjectGlobal.DATABASENAME = String.valueOf(object.get("dbname"));
                        ObjectGlobal.USER = String.valueOf(object.get("user"));
                        ObjectGlobal.PASSWORD = String.valueOf(object.get("password"));
                    }
                } else {
                    Tools.AlertMessageError(preloaderStage.getScene().getRoot(), "Preloader", "No se puedo cargar el archivo conexión, intente de nuevo o comuníquese con su proveedor.");
                    preloaderStage.hide();
                    Platform.exit();
                    return;
                }

                DBUtil.dbConnect();
                if (DBUtil.getConnection() != null) {

                    boolean validateRegister = EmpresaADO.isConfiguration();
                    if (!validateRegister) {
                        Session.CONFIGURATION_STATE = true;
                    } else {
                        Session.CONNECTION_SESSION = true;

                        String rutaVenta = "./archivos/VENTA.properties";
                        try (InputStream input = new FileInputStream(rutaVenta)) {
                            Properties prop = new Properties();
                            prop.load(input);
                            Session.ESTADO_IMPRESORA_VENTA = true;
                            Session.NOMBRE_IMPRESORA_VENTA = prop.getProperty("printerNameVenta");
                            Session.CORTAPAPEL_IMPRESORA_VENTA = Boolean.parseBoolean(prop.getProperty("printerCutPaperVenta"));
                            Session.FORMATO_IMPRESORA_VENTA = prop.getProperty("printerTypeFormatVenta");
                            Session.DESING_IMPRESORA_VENTA = prop.getProperty("printerTypeDesingVenta");
                        } catch (IOException ex) {
                            Session.ESTADO_IMPRESORA_VENTA = false;
                        }

                        String rutaPreVenta = "./archivos/PRE VENTA.properties";
                        try (InputStream input = new FileInputStream(rutaPreVenta)) {
                            Properties prop = new Properties();
                            prop.load(input);
                            Session.ESTADO_IMPRESORA_PRE_VENTA = true;
                            Session.NOMBRE_IMPRESORA_PRE_VENTA = prop.getProperty("printerNamePreVenta");
                            Session.CORTAPAPEL_IMPRESORA_PRE_VENTA = Boolean.parseBoolean(prop.getProperty("printerCutPaperPreVenta"));
                            Session.FORMATO_IMPRESORA_PRE_VENTA = prop.getProperty("printerTypeFormatPreVenta");
                            Session.DESING_IMPRESORA_PRE_VENTA = prop.getProperty("printerTypeDesingVenta");
                        } catch (IOException ex) {
                            Session.ESTADO_IMPRESORA_PRE_VENTA = false;
                        }

                        String rutaCorteCaja = "./archivos/CORTE DE CAJA.properties";
                        try (InputStream input = new FileInputStream(rutaCorteCaja)) {
                            Properties prop = new Properties();
                            prop.load(input);
                            Session.ESTADO_IMPRESORA_CORTE_CAJA = true;
                            Session.NOMBRE_IMPRESORA_CORTE_CAJA = prop.getProperty("printerNameCorteCaja");
                            Session.CORTAPAPEL_IMPRESORA_CORTE_CAJA = Boolean.parseBoolean(prop.getProperty("printerCutPaperCorteCaja"));
                            Session.FORMATO_IMPRESORA_CORTE_CAJA = prop.getProperty("printerTypeFormatCorteCaja");
                            Session.DESING_IMPRESORA_CORTE_CAJA = prop.getProperty("printerTypeDesingCorteCaja");
                        } catch (IOException ex) {
                            Session.ESTADO_IMPRESORA_CORTE_CAJA = false;
                        }

                        String rutaCotizacion = "./archivos/COTIZACION.properties";
                        try (InputStream input = new FileInputStream(rutaCotizacion)) {
                            Properties prop = new Properties();
                            prop.load(input);
                            Session.ESTADO_IMPRESORA_COTIZACION = true;
                            Session.NOMBRE_IMPRESORA_COTIZACION = prop.getProperty("printerNameCotizacion");
                            Session.CORTAPAPEL_IMPRESORA_COTIZACION = Boolean.parseBoolean(prop.getProperty("printerCutPaperCotizacion"));
                            Session.FORMATO_IMPRESORA_COTIZACION = prop.getProperty("printerTypeFormatCotizacion");
                            Session.DESING_IMPRESORA_COTIZACION = prop.getProperty("printerTypeDesingCotizacion");
                        } catch (IOException ex) {
                            Session.ESTADO_IMPRESORA_COTIZACION = false;
                        }

                        String rutaCuentasPorCobrar = "./archivos/CUENTAS POR COBRAR.properties";
                        try (InputStream input = new FileInputStream(rutaCuentasPorCobrar)) {
                            Properties prop = new Properties();
                            prop.load(input);
                            Session.ESTADO_IMPRESORA_CUENTA_POR_COBRAR = true;
                            Session.NOMBRE_IMPRESORA_CUENTA_POR_COBRAR = prop.getProperty("printerNameCuentasPorCobrar");
                            Session.CORTAPAPEL_IMPRESORA_CUENTA_POR_COBRAR = Boolean.parseBoolean(prop.getProperty("printerCutPaperCuentasPorCobrar"));
                            Session.FORMATO_IMPRESORA_CUENTA_POR_COBRAR = prop.getProperty("printerTypeFormatCuentasPorCobrar");
                            Session.DESING_IMPRESORA_CUENTA_POR_COBRAR = prop.getProperty("printerTypeDesingCuentasPorCobrar");
                        } catch (IOException ex) {
                            Session.ESTADO_IMPRESORA_CUENTA_POR_COBRAR = false;
                        }

                        LoadFont loadFont = new LoadFont();
                        loadFont.loadFont();

                        TicketTB ticketVentaTB = TicketADO.GetTicketRuta(1);
                        if (ticketVentaTB != null) {
                            Session.TICKET_VENTA_ID = ticketVentaTB.getId();
                            Session.TICKET_VENTA_RUTA = ticketVentaTB.getRuta();
                        } else {
                            Session.TICKET_VENTA_ID = 0;
                            Session.TICKET_VENTA_RUTA = "";
                        }

                        TicketTB ticketCorteCajaTB = TicketADO.GetTicketRuta(5);
                        if (ticketCorteCajaTB != null) {
                            Session.TICKET_CORTE_CAJA_ID = ticketCorteCajaTB.getId();
                            Session.TICKET_CORTE_CAJA_RUTA = ticketCorteCajaTB.getRuta();
                        } else {
                            Session.TICKET_CORTE_CAJA_ID = 0;
                            Session.TICKET_CORTE_CAJA_RUTA = "";
                        }

                        TicketTB ticketPreVentaTB = TicketADO.GetTicketRuta(7);
                        if (ticketPreVentaTB != null) {
                            Session.TICKET_PRE_VENTA_ID = ticketPreVentaTB.getId();
                            Session.TICKET_PRE_VENTA_RUTA = ticketPreVentaTB.getRuta();
                        } else {
                            Session.TICKET_PRE_VENTA_ID = 0;
                            Session.TICKET_PRE_VENTA_RUTA = "";
                        }

                        TicketTB ticketCotizacionTB = TicketADO.GetTicketRuta(8);
                        if (ticketCotizacionTB != null) {
                            Session.TICKET_COTIZACION_ID = ticketCotizacionTB.getId();
                            Session.TICKET_COTIZACION_RUTA = ticketCotizacionTB.getRuta();
                        } else {
                            Session.TICKET_COTIZACION_ID = 0;
                            Session.TICKET_COTIZACION_RUTA = "";
                        }

                        TicketTB ticketCuentasPorCobrarTB = TicketADO.GetTicketRuta(9);
                        if (ticketCuentasPorCobrarTB != null) {
                            Session.TICKET_CUENTA_POR_COBRAR_ID = ticketCuentasPorCobrarTB.getId();
                            Session.TICKET_CUENTA_POR_COBRAR_RUTA = ticketCuentasPorCobrarTB.getRuta();
                        } else {
                            Session.TICKET_CUENTA_POR_COBRAR_ID = 0;
                            Session.TICKET_CUENTA_POR_COBRAR_RUTA = "";
                        }

                        TicketTB ticketCuentasPorPagarTB = TicketADO.GetTicketRuta(10);
                        if (ticketCuentasPorPagarTB != null) {
                            Session.TICKET_CUENTA_POR_PAGAR_ID = ticketCuentasPorPagarTB.getId();
                            Session.TICKET_CUENTA_POR_PAGAR_RUTA = ticketCuentasPorPagarTB.getRuta();
                        } else {
                            Session.TICKET_CUENTA_POR_PAGAR_ID = 0;
                            Session.TICKET_CUENTA_POR_PAGAR_RUTA = "";
                        }

                        EmpresaTB list = EmpresaADO.GetEmpresa();
                        if (list != null) {
                            Session.COMPANY_REPRESENTANTE = list.getNombre();
                            Session.COMPANY_RAZON_SOCIAL = list.getRazonSocial();
                            Session.COMPANY_NOMBRE_COMERCIAL = list.getNombreComercial();
                            Session.COMPANY_NUMERO_DOCUMENTO = list.getNumeroDocumento();
                            Session.COMPANY_TELEFONO = list.getTelefono();
                            Session.COMPANY_CELULAR = list.getCelular();
                            Session.COMPANY_PAGINAWEB = list.getPaginaWeb();
                            Session.COMPANY_EMAIL = list.getEmail();
                            Session.COMPANY_DOMICILIO = list.getDomicilio();
                            Session.COMPANY_IMAGE = list.getImage();
                        }

                        MonedaADO.GetMonedaPredetermined();

                        ClienteTB clienteTB = ClienteADO.GetClientePredetermined();
                        if (clienteTB != null) {
                            Session.CLIENTE_ID = clienteTB.getIdCliente();
                            Session.CLIENTE_TIPO_DOCUMENTO = clienteTB.getTipoDocumento();
                            Session.CLIENTE_DATOS = clienteTB.getInformacion();
                            Session.CLIENTE_NUMERO_DOCUMENTO = clienteTB.getNumeroDocumento();
                            Session.CLIENTE_CELULAR = clienteTB.getCelular();
                            Session.CLIENTE_EMAIL = clienteTB.getEmail();
                            Session.CLIENTE_DIRECCION = clienteTB.getDireccion();
                        } else {
                            Session.CLIENTE_ID = "";
                            Session.CLIENTE_TIPO_DOCUMENTO = 0;
                            Session.CLIENTE_DATOS = "";
                            Session.CLIENTE_NUMERO_DOCUMENTO = "";
                            Session.CLIENTE_CELULAR = "";
                            Session.CLIENTE_EMAIL = "";
                            Session.CLIENTE_DIRECCION = "";
                        }

                    }

                } else {
                    Tools.AlertMessageError(preloaderStage.getScene().getRoot(), "Preloader", "No se pudo conectar al servidor, revise su conexión e intente nuevamente.");
                    preloaderStage.hide();
                    Platform.exit();
                }

                break;

            case BEFORE_START:
                preloaderStage.hide();
                System.out.println("BEFORE_START");
                break;
        }
    }

}
