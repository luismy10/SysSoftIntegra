package controller.preloader;

import controller.tools.FilesRouters;
import controller.tools.Json;
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
import javafx.scene.control.Alert;
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
                }

                DBUtil.dbConnect();
                if (DBUtil.getConnection() != null) {
                    Session.CONNECTION_SESSION = true;

                    String ruta = "./archivos/printSetting.properties";
                    try (InputStream input = new FileInputStream(ruta)) {

                        Properties prop = new Properties();
                        prop.load(input);

                        Session.ESTADO_IMPRESORA = true;
                        Session.NOMBRE_IMPRESORA = prop.getProperty("printername");
                        Session.CORTAPAPEL_IMPRESORA = Boolean.parseBoolean(prop.getProperty("printcutspaper"));
                      
                    } catch (IOException ex) {                    
                        Session.ESTADO_IMPRESORA = false;
                    }

                    TicketTB ticketTB = TicketADO.GetTicketRuta(1);
                    if (ticketTB != null) {
                        Session.RUTA_TICKET_VENTA = ticketTB.getRuta();
                    }

                    EmpresaTB list = EmpresaADO.GetEmpresa();
                    if (list != null) {
                        Session.COMPANY_REPRESENTANTE = list.getNombre();
                        Session.COMPANY_RAZON_SOCIAL = list.getRazonSocial();
                        Session.COMPANY_NOMBRE_COMERCIAL = list.getNombreComercial();
                        Session.COMPANY_NUM_DOCUMENTO = list.getNumeroDocumento();
                        Session.COMPANY_TELEFONO = list.getTelefono();
                        Session.COMPANY_CELULAR = list.getCelular();
                        Session.COMPANY_PAGINAWEB = list.getPaginaWeb();
                        Session.COMPANY_EMAIL = list.getEmail();
                        Session.COMPANY_DOMICILIO = list.getDomicilio();
                    }

                    Session.MONEDA = MonedaADO.GetMonedaPredetermined();

                    ClienteTB clienteTB = ClienteADO.GetByIdClienteVenta("00000000");
                    if (clienteTB != null) {
                        Session.IDCLIENTE = clienteTB.getIdCliente();
                        Session.DATOSCLIENTE = clienteTB.getApellidos() + " " + clienteTB.getNombres();
                        Session.N_DOCUMENTO_CLIENTE = clienteTB.getNumeroDocumento();
                        Session.DIRECCION_CLIENTE = clienteTB.getDireccion();
                    } else {
                        ClienteTB clienteInsert = new ClienteTB();
                        clienteInsert.setTipoDocumento(1);
                        clienteInsert.setNumeroDocumento("00000000");
                        clienteInsert.setApellidos("PUBLICO");
                        clienteInsert.setNombres("GENERAL");
                        clienteInsert.setSexo(0);
                        clienteInsert.setFechaNacimiento(null);
                        clienteInsert.setTelefono("");
                        clienteInsert.setCelular("");
                        clienteInsert.setEmail("");
                        clienteInsert.setDireccion("");
                        clienteInsert.setEstado(1);

                        String result = ClienteADO.CrudCliente(clienteInsert);
                        if (result.equalsIgnoreCase("registered")) {
                            ClienteTB clienteSelect = ClienteADO.GetByIdClienteVenta("00000000");
                            if (clienteTB != null) {
                                Session.IDCLIENTE = clienteSelect.getIdCliente();
                                Session.DATOSCLIENTE = clienteSelect.getApellidos() + " " + clienteTB.getNombres();
                                Session.N_DOCUMENTO_CLIENTE = clienteSelect.getNumeroDocumento();
                                Session.DIRECCION_CLIENTE = clienteSelect.getDireccion();
                            }

                        }
                    }

                } else {
                    Tools.AlertMessage(preloaderStage.getScene().getWindow(), Alert.AlertType.ERROR, "Preloader", "No se pudo conectar al servidor, revise su conexión e intente nuevamente.", false);
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
