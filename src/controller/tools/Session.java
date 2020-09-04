package controller.tools;

public class Session {

    /*
    connection status
     */
    public static boolean CONNECTION_SESSION;
    public static boolean CONFIGURATION_STATE;
    /*
    user session data 
     */
    public static int USER_ROL;
    public static String USER_ID;
    public static String USER_NAME;
    public static String USER_PUESTO;
    /*
    size of the initial window 
     */
    public static double WIDTH_WINDOW;
    public static double HEIGHT_WINDOW;
    /*
    my company session data
     */
    public static String COMPANY_GIRO_COMERCIAL = "";
    public static String COMPANY_REPRESENTANTE = "";
    public static String COMPANY_TELEFONO = "";
    public static String COMPANY_CELULAR = "";
    public static String COMPANY_PAGINAWEB = "";
    public static String COMPANY_EMAIL = "";
    public static String COMPANY_DOMICILIO = "";
    public static String COMPANY_NUMERO_DOCUMENTO = "";
    public static String COMPANY_RAZON_SOCIAL = "";
    public static String COMPANY_NOMBRE_COMERCIAL = "";
    public static String COMPANY_PAIS = "";
    public static String COMPANY_DEPARTAMENTO = "";
    public static String COMPANY_PROVINCIA = "";
    public static String COMPANY_DISTRITO = "";
    public static byte[] COMPANY_IMAGE = null;

    /*
    impresora session
     */
    public static int TICKET_VENTA_ID;
    public static String TICKET_VENTA_RUTA;

    public static String NOMBRE_IMPRESORA;
    public static boolean CORTAPAPEL_IMPRESORA;
    public static boolean ESTADO_IMPRESORA;
    public static String TIPO_IMPRESORA;
    /*
    variable de cliente
     */
    public static String CLIENTE_ID;
    public static int CLIENTE_TIPO_DOCUMENTO;
    public static String CLIENTE_DATOS;
    public static String CLIENTE_NUMERO_DOCUMENTO;
    public static String CLIENTE_DIRECCION;

    /*
    varialble de moneda
     */
    public static int MONEDA_ID;
    public static String MONEDA_NOMBRE;
    public static String MONEDA_SIMBOLO;

    /*
    variables de caja/banco
     */
    public static String CAJA_ID;

    public static String BANCO_EFECTIVO_ID;
    public static String BANCO_EFECTIVO_NOMBRE;

    public static String BANCO_TARJETA_ID;
    public static String BANCO_TARJETA_NOMBRE;
}
