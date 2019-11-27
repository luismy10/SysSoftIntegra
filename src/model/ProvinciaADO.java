
package model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


public class ProvinciaADO {
    public static ObservableList<ProvinciaTB> ListProvincia(int value) {
        String selectStmt = "{call Sp_Listar_Provincia(?)}";
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ObservableList<ProvinciaTB> empList = FXCollections.observableArrayList();
        try {
            DBUtil.dbConnect();
            preparedStatement = DBUtil.getConnection().prepareStatement(selectStmt);
            preparedStatement.setInt(1, value);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                ProvinciaTB provinciaTB = new ProvinciaTB(resultSet.getInt("IdProvincia"), resultSet.getString("Provincia"));
                empList.add(provinciaTB);
            }

        } catch (SQLException e) {
            System.out.println("La operación de selección de SQL ha fallado: " + e);
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (resultSet != null) {
                    resultSet.close();
                }
                DBUtil.dbDisconnect();

            } catch (SQLException ex) {

            }

        }
        return empList;
    }
}
