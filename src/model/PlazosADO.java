/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ruberfc
 */
public class PlazosADO {

    public static String crudPlazos(PlazosTB plazosTB) {
        PreparedStatement preparedStatement = null;

        try {
            
            DBUtil.dbConnect();
            DBUtil.getConnection().setAutoCommit(false);
            preparedStatement = DBUtil.getConnection().prepareStatement("INSERT INTO PlazosTB(Nombre,Dias,Estado,Predeterminado) "
                    + "VALUES(?,?,?,?)");
            
            preparedStatement.setString(1, plazosTB.getNombre());
            preparedStatement.setInt(2, plazosTB.getDias());
            preparedStatement.setBoolean(3, plazosTB.getEstado());
            preparedStatement.setBoolean(4,plazosTB.getPredeterminado());
            
            preparedStatement.addBatch();
            preparedStatement.executeBatch();
            DBUtil.getConnection().commit();        
            return "register";
            
        } catch (SQLException ex) {
            try {
                DBUtil.getConnection().rollback();
                return ex.getLocalizedMessage();
            } catch (SQLException ex1) {
                return ex1.getLocalizedMessage();
            }
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
  
                DBUtil.dbDisconnect();
            } catch (SQLException e) {
            }
        }

    }
    
    public static List<PlazosTB> GetTipoPlazoCombBox() {
        
        List<PlazosTB> list = new ArrayList<>();
        DBUtil.dbConnect();
        if (DBUtil.getConnection() != null) {
            PreparedStatement statement = null;
            ResultSet resultSet = null;            
            try {
                statement = DBUtil.getConnection().prepareStatement("select IdPlazos, Nombre, Dias, Estado, Predeterminado from PlazosTB order by Dias ASC");
                resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    
                    PlazosTB plazosTB = new PlazosTB();
                    plazosTB.setIdPlazos(resultSet.getInt("idPlazos"));
                    plazosTB.setNombre(resultSet.getString("Nombre"));
                    plazosTB.setDias(resultSet.getInt("Dias"));
                    plazosTB.setEstado(resultSet.getBoolean("Estado"));
                    plazosTB.setPredeterminado(resultSet.getBoolean("Predeterminado"));
                    list.add(plazosTB);
                }
            } catch (SQLException ex) {
                System.out.println("Error¨Plazos: " + ex.getLocalizedMessage());
            } finally {
                try {
                    if (statement != null) {
                        statement.close();
                    }
                    if (resultSet != null) {
                        resultSet.close();
                    }
                    DBUtil.dbDisconnect();
                } catch (SQLException ex) {
                    System.out.println("Error Plazos: " + ex.getLocalizedMessage());
                }
            }
        }
        return list;
        
    }
}
