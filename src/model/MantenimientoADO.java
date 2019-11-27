package model;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class MantenimientoADO {

    public static ObservableList<MantenimientoTB> ListMantenimiento(String value) {
        String selectStmt = "{call Sp_List_Table_Matenimiento(?)}";
        PreparedStatement preparedStatement = null;
        ResultSet rsEmps = null;
        ObservableList<MantenimientoTB> empList = FXCollections.observableArrayList();
        try {
            DBUtil.dbConnect();
            preparedStatement = DBUtil.getConnection().prepareStatement(selectStmt);
            preparedStatement.setString(1, value);
            rsEmps = preparedStatement.executeQuery();
            while (rsEmps.next()) {
                MantenimientoTB emp = new MantenimientoTB();
                emp.setIdMantenimiento(rsEmps.getString("IdMantenimiento"));
                emp.setNombre(rsEmps.getString("Nombre"));
                emp.setValidar(rsEmps.getString("Validar"));              
                empList.add(emp);
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

    public static String CrudEntity(MantenimientoTB mantenimientoTB) {
        String selectStmt = "{call Sp_Crud_Mantenimiento(?,?,?,?,?)}";
        CallableStatement callableStatement = null;
        try {
            DBUtil.dbConnect();
            callableStatement = DBUtil.getConnection().prepareCall(selectStmt);
            callableStatement.setString("IdMantenimiento", mantenimientoTB.getIdMantenimiento());
            callableStatement.setString("Nombre", mantenimientoTB.getNombre());
            callableStatement.setString("Estado", mantenimientoTB.getEstado().toString());
            callableStatement.setString("UsuarioRegistro", mantenimientoTB.getUsuarioRegistro());
            callableStatement.registerOutParameter("Message", java.sql.Types.VARCHAR, 20);
            callableStatement.execute();
            return callableStatement.getString("Message");
        } catch (SQLException ex) {
            return ex.getLocalizedMessage();
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

    public static String GetIdMantenimiento() {
        String selectStmt = "{? = call Fc_Mantenimiento_Generar_Codigo()}";
        CallableStatement callableStatement = null;
        try {
            DBUtil.dbConnect();
            callableStatement = DBUtil.getConnection().prepareCall(selectStmt);
            callableStatement.registerOutParameter(1, java.sql.Types.VARCHAR);
            callableStatement.execute();
            return callableStatement.getString(1);
        } catch (SQLException e) {
            System.out.println("La operación de selección de SQL ha fallado: " + e.getLocalizedMessage());
            return "Error al generar";
        } finally {
            try {
                if (callableStatement != null) {
                    callableStatement.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException ex) {
                System.out.println("La consulta se cerro antes de finalizar: " + ex.getLocalizedMessage());
            }
        }
    }

    public static String DeleteMantenimiento(String idMantenimiento) {
        String selectStmt = "delete from MantenimientoTB where IdMantenimiento = ?";
        PreparedStatement preparedStatement = null;
        try {
            DBUtil.dbConnect();
            preparedStatement = DBUtil.getConnection().prepareStatement(selectStmt);
            preparedStatement.setString(1, idMantenimiento);
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
