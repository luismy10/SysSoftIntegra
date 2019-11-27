package model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CiudadADO {

    public static ObservableList<CiudadTB> ListCiudad(String value) {
        String selectStmt = "{call Sp_Listar_Ciudad(?)}";
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ObservableList<CiudadTB> empList = FXCollections.observableArrayList();
        try {
            DBUtil.dbConnect();
            preparedStatement = DBUtil.getConnection().prepareStatement(selectStmt);
            preparedStatement.setString(1, value);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                CiudadTB ciudadTB = new CiudadTB(resultSet.getInt("IdCiudad"), resultSet.getString("Departamento"));
                empList.add(ciudadTB);
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
