
package model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


public class HistorialADO {
    
 
    
    public static ObservableList<HistorialTB> ListArticulos(String value){
        
        String selectStmt = "{call Sp_listar_Historial_Articulo(?)}";
        PreparedStatement preparedStatement = null;
        ResultSet rsEmps = null;
        ObservableList<HistorialTB> empList = FXCollections.observableArrayList();
        
        try {
            DBUtil.dbConnect();
            preparedStatement = DBUtil.getConnection().prepareStatement(selectStmt);
            preparedStatement.setString(1, value);
            rsEmps = preparedStatement.executeQuery();

            while (rsEmps.next()) {               
                
                HistorialTB historialTB = new HistorialTB();
                
                historialTB.setIdHistorial(rsEmps.getInt("Filas"));
                historialTB.setFechaRegistro(rsEmps.getTimestamp("FechaRegistro").toLocalDateTime());
                historialTB.setTipoOperacion(rsEmps.getString("TipoOperacion"));
                historialTB.setEntrada(rsEmps.getDouble("Entrada"));
                historialTB.setSalida(rsEmps.getDouble("Salida"));
                historialTB.setSaldo(rsEmps.getDouble("Saldo"));
                historialTB.setUsuarioRegistro(rsEmps.getString("UsuarioRegistro"));
                empList.add(historialTB);
                
            }
        } catch (SQLException e) {
            System.out.println("La operación de selección de SQL ha fallado: " + e);

        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (rsEmps != null) {
                    rsEmps.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException ex) {

            }
        }
        
        return empList;
    }
}
