
package model;

import controller.tools.ObjectGlobal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {
    
    private static Connection connection = null;
    private static final String ADDRES = ObjectGlobal.ADDRES;
    private static final String PORT = ObjectGlobal.PORT;
    private static final String DATABASENAME = ObjectGlobal.DATABASENAME;
    private static final String USER = ObjectGlobal.USER;
    private static final String PASSWORD = ObjectGlobal.PASSWORD;//Qz0966lb
//    private static final String URL = "jdbc:sqlserver://" + ADDRES + ":" + PORT + ";databaseName=" + DATABASENAME + "";
    private static final String URL = "jdbc:mysql://"+ ADDRES +":"+PORT+"/"+DATABASENAME+"";
    public static void dbConnect()  {
        try {   
//           Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
           Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Error en conexión: "+e.getLocalizedMessage());
        }
    }

    public static void dbDisconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    public static Connection getConnection() {
        return connection;
    }
    
}
