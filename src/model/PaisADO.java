package model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PaisADO {

    public static ObservableList<PaisTB> ListPais() {
        String selectStmt = "{call Sp_Listar_Pais()}";
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ObservableList<PaisTB> empList = FXCollections.observableArrayList();
        try {
            DBUtil.dbConnect();
            preparedStatement = DBUtil.getConnection().prepareStatement(selectStmt);
//            preparedStatement.setString(1, value[0]);
//            preparedStatement.setString(2, value[1]);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                PaisTB paisTB = new PaisTB(resultSet.getString("PaisCodigo"), resultSet.getString("PaisNombre"));
                empList.add(paisTB);
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
