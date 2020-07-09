package model;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DetalleADO {

    public static ObservableList<DetalleTB> ListDetail(String... value) {
        String selectStmt = "{call Sp_List_Table_Detalle(?,?)}";
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ObservableList<DetalleTB> empList = FXCollections.observableArrayList();
        try {
            DBUtil.dbConnect();
            preparedStatement = DBUtil.getConnection().prepareStatement(selectStmt);
            preparedStatement.setString(1, value[0]);
            preparedStatement.setString(2, value[1]);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                DetalleTB emp = new DetalleTB();
                emp.setIdDetalle(resultSet.getInt("IdDetalle"));
                emp.setIdAuxiliar(resultSet.getString("IdAuxiliar"));
                emp.setNombre(resultSet.getString("Nombre"));
                emp.setDescripcion(resultSet.getString("Descripcion"));
                emp.setEstado(resultSet.getString("Estado"));
                empList.add(emp);
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
                System.out.println("La operación de selección de SQL ha fallado: " + ex);
            }

        }
        return empList;
    }

    public static String CrudEntity(DetalleTB detalleTB) {
        String result = null;
        PreparedStatement statementValidate = null;
        PreparedStatement statementDetalle = null;
        DBUtil.dbConnect();
        if (DBUtil.getConnection() != null) {
            try {
                DBUtil.getConnection().setAutoCommit(false);
                statementValidate = DBUtil.getConnection().prepareStatement("select IdDetalle,IdMantenimiento from DetalleTB where IdDetalle=? and IdMantenimiento=?");
                statementValidate.setInt(1, detalleTB.getIdDetalle().get());
                statementValidate.setString(2, detalleTB.getIdMantenimiento().get());
                if(statementValidate.executeQuery().next()){

                    statementDetalle = DBUtil.getConnection().prepareStatement("select IdDetalle,IdMantenimiento from DetalleTB where IdDetalle<>? and IdMantenimiento=? and Nombre = ?");
                    statementDetalle.setInt(1, detalleTB.getIdDetalle().get());
                    statementDetalle.setString(2, detalleTB.getIdMantenimiento().get());
                    statementDetalle.setString(3, detalleTB.getNombre().get());
                    if(statementDetalle.executeQuery().next()){
                        DBUtil.getConnection().rollback();
                        result = "duplicate";
                    }else{
                        statementDetalle = DBUtil.getConnection().prepareStatement("update DetalleTB set IdAuxiliar=UPPER(?),Nombre=UPPER(?),Descripcion=UPPER(?),Estado=? where IdDetalle =? and IdMantenimiento = ?");
                        statementDetalle.setString(1, detalleTB.getIdAuxiliar().get());
                        statementDetalle.setString(2, detalleTB.getNombre().get());
                        statementDetalle.setString(3, detalleTB.getDescripcion().get());
                        statementDetalle.setString(4, detalleTB.getEstado().get());
                        statementDetalle.setInt(5, detalleTB.getIdDetalle().get());
                        statementDetalle.setString(6, detalleTB.getIdMantenimiento().get());
                        statementDetalle.addBatch();
                        
                        statementDetalle.executeBatch();
                        DBUtil.getConnection().commit();
                        result = "updated";
                    }
                }else{
                    statementDetalle = DBUtil.getConnection().prepareStatement("select Nombre from DetalleTB where IdMantenimiento = ? and Nombre = ?");
                    statementDetalle.setString(1, detalleTB.getIdMantenimiento().get());
                    statementDetalle.setString(2, detalleTB.getNombre().get());
                    if(statementDetalle.executeQuery().next()){
                        DBUtil.getConnection().rollback();
                        result = "duplicate";
                    }else{
                        statementDetalle = DBUtil.getConnection().prepareStatement("insert into DetalleTB(IdDetalle,IdMantenimiento,IdAuxiliar,Nombre,Descripcion,Estado,UsuarioRegistro) values(dbo.Fc_Detalle_Generar_Codigo(?),?,UPPER(?),UPPER(?),UPPER(?),?,?)");
                        statementDetalle.setInt(1, detalleTB.getIdDetalle().get());
                        statementDetalle.setString(2, detalleTB.getIdMantenimiento().get());
                        statementDetalle.setString(3, detalleTB.getIdAuxiliar().get());
                        statementDetalle.setString(4, detalleTB.getNombre().get());
                        statementDetalle.setString(5, detalleTB.getDescripcion().get());
                        statementDetalle.setString(6, detalleTB.getEstado().get());
                        statementDetalle.setString(7, detalleTB.getUsuarioRegistro().get());
                        statementDetalle.addBatch();
                        
                        statementDetalle.executeBatch();
                        DBUtil.getConnection().commit();
                        result = "inserted";
                    }
                 
                }

            } catch (SQLException ex) {
                result = ex.getLocalizedMessage();
            } finally {
                try {
                    if (statementValidate != null) {
                        statementValidate.close();
                    }
                    if (statementDetalle != null) {
                        statementDetalle.close();
                    }
                    DBUtil.dbDisconnect();
                } catch (SQLException ex) {
                    result = ex.getLocalizedMessage();
                }
            }
        } else {
            result = "No se puedo establecer conexión con el servidor, revice y vuelva a intentarlo.";
        }
        return result;

    }

    public static String DeleteDetail(DetalleTB detalleTB) {
        String result = null;
        DBUtil.dbConnect();
        if (DBUtil.getConnection() != null) {
            String selectStmt = "delete from DetalleTB where IdDetalle = ? and IdMantenimiento = ?";
            PreparedStatement preparedStatement = null;
            try {
                DBUtil.getConnection().setAutoCommit(false);
                preparedStatement = DBUtil.getConnection().prepareStatement(selectStmt);
                preparedStatement.setInt(1, detalleTB.getIdDetalle().get());
                preparedStatement.setString(2, detalleTB.getIdMantenimiento().get());
                preparedStatement.addBatch();
                preparedStatement.executeBatch();
                DBUtil.getConnection().commit();
                result = "eliminado";
            } catch (SQLException ex) {
                try {
                    DBUtil.getConnection().rollback();
                    result = ex.getLocalizedMessage();
                } catch (SQLException e) {
                    result = e.getLocalizedMessage();
                }
            } finally {
                try {
                    if (preparedStatement != null) {
                        preparedStatement.close();
                    }
                    DBUtil.dbDisconnect();
                } catch (SQLException ex) {
                    result = ex.getLocalizedMessage();
                }
            }
        } else {
            result = "No se puedo establecer conexión con el servidor, revice y vuelva a intentarlo.";
        }
        return result;
    }

    public static ObservableList<DetalleTB> GetDetailId(String value) {
        String selectStmt = "{call Sp_Get_Detalle_Id(?)}";
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ObservableList<DetalleTB> empList = FXCollections.observableArrayList();
        try {
            DBUtil.dbConnect();
            preparedStatement = DBUtil.getConnection().prepareStatement(selectStmt);
            preparedStatement.setString(1, value);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                DetalleTB detalleTB = new DetalleTB();
                detalleTB.setIdDetalle(resultSet.getInt("IdDetalle"));
                detalleTB.setNombre(resultSet.getString("Nombre"));
                empList.add(detalleTB);
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

    public static ObservableList<DetalleTB> GetDetailIdName(String... value) {
        String selectStmt = "{call Sp_Get_Detalle_IdNombre(?,?,?)}";
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ObservableList<DetalleTB> empList = FXCollections.observableArrayList();
        try {
            DBUtil.dbConnect();
            preparedStatement = DBUtil.getConnection().prepareStatement(selectStmt);
            preparedStatement.setString(1, value[0]);
            preparedStatement.setString(2, value[1]);
            preparedStatement.setString(3, value[2]);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                DetalleTB detalleTB = new DetalleTB();
                detalleTB.setIdDetalle(resultSet.getInt("IdDetalle"));
                detalleTB.setNombre(resultSet.getString("Nombre"));
                if (value[0].equalsIgnoreCase("3")) {
                    detalleTB.setDescripcion(resultSet.getString("Descripcion"));
                }
                empList.add(detalleTB);
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

    public static ObservableList<DetalleTB> GetDetailNameImpuesto() {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ObservableList<DetalleTB> empList = FXCollections.observableArrayList();
        try {
            DBUtil.dbConnect();
            preparedStatement = DBUtil.getConnection().prepareStatement("select IdImpuesto,Nombre from ImpuestoTB");
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                DetalleTB detalleTB = new DetalleTB();
                detalleTB.setIdDetalle(resultSet.getInt("IdImpuesto"));
                detalleTB.setNombre(resultSet.getString("Nombre"));
                empList.add(detalleTB);
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
