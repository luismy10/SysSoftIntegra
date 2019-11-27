package model;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DirectorioADO {

    public static ObservableList<DirectorioTB> ListDirectory(String value) {
        String selectStmt = "{call Sp_Listar_Directorio(?)}";
        PreparedStatement preparedStatement = null;
        ResultSet rsEmps = null;
        ObservableList<DirectorioTB> empList = FXCollections.observableArrayList();
        try {
            DBUtil.dbConnect();
            preparedStatement = DBUtil.getConnection().prepareStatement(selectStmt);
            preparedStatement.setString(1, value);
            rsEmps = preparedStatement.executeQuery();
            while (rsEmps.next()) {
                DirectorioTB directorioTB = new DirectorioTB();
                directorioTB.setId(rsEmps.getRow());
                directorioTB.setPersona(new PersonaTB(rsEmps.getString("Codigo"),rsEmps.getString("Tipo"),rsEmps.getString("Documento"),rsEmps.getString("Datos")));
                empList.add(directorioTB);  
            }
        } catch (SQLException e) {
            System.out.println("SQL select operation has been failed: " + e);

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

    public static String CrudEntity(DirectorioTB directorioTB) {
        String selectStmt = "{call Sp_Crud_Directorio(?,?,?,?,?)}";
        CallableStatement callableStatement = null;
        try {
            DBUtil.dbConnect();
            callableStatement = DBUtil.getConnection().prepareCall(selectStmt);
            callableStatement.setLong("IdDirectorio", directorioTB.getIdDirectorio());
            callableStatement.setInt("Atributo", directorioTB.getAtributo());
            callableStatement.setString("Valor", directorioTB.getValor());
            callableStatement.setString("IdPersona", directorioTB.getIdPersona());

            callableStatement.registerOutParameter("Message", java.sql.Types.VARCHAR, 20);
            callableStatement.execute();
            return callableStatement.getString("Message");
        } catch (SQLException e) {
            return e.getLocalizedMessage();
        } finally {
            try {
                if (callableStatement != null) {
                    callableStatement.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException ex) {
                return ex.getLocalizedMessage();
            }
        }
    }

    public static ArrayList<DirectorioTB> GetIdDirectorio(String documento) {
        String selectStmt = "{call Sp_Get_Directorio_By_Id(?)}";
        PreparedStatement preparedStatement = null;
        ResultSet rsEmps = null;
        ArrayList<DirectorioTB> arrayList = new ArrayList<>();
        try {
            DBUtil.dbConnect();
            preparedStatement = DBUtil.getConnection().prepareStatement(selectStmt);
            preparedStatement.setString(1, documento);
            rsEmps = preparedStatement.executeQuery();
            while (rsEmps.next()) {
                DirectorioTB directorioTB = new DirectorioTB();
                directorioTB.setIdDirectorio(rsEmps.getLong("IdDirectorio"));
                directorioTB.setAtributo(rsEmps.getInt("Atributo"));
                directorioTB.setNombre(rsEmps.getString("Nombre"));
                directorioTB.setValor(rsEmps.getString("Valor"));
                directorioTB.setIdPersona(rsEmps.getString("IdPersona"));
                arrayList.add(directorioTB);
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
        return arrayList;
    }
    
    public static String DeleteDirectory(long idDirectorio) {
        String selectStmt = "delete from DirectorioTB where IdDirectorio = ?";
        PreparedStatement preparedStatement = null;
        try {
            DBUtil.dbConnect();
            preparedStatement = DBUtil.getConnection().prepareStatement(selectStmt);
            preparedStatement.setLong(1, idDirectorio);
            return preparedStatement.executeUpdate() == 1 ? "eliminado" : "error";
        } catch (SQLException e) {
            return "La operación de selección de SQL ha fallado: " + e.getLocalizedMessage();
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException ex) {
                return "La consulta se cerro antes de finalizar: " + ex.getLocalizedMessage();
            }
        }
    }
    
}
