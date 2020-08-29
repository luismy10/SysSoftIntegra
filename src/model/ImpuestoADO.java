package model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ImpuestoADO {

    public static String CrudImpuesto(ImpuestoTB impuestoTB) {
        String result = null;
        DBUtil.dbConnect();
        if (DBUtil.getConnection() != null) {
            PreparedStatement statementValidate = null;
            PreparedStatement statementImpuesto = null;
            try {
                DBUtil.getConnection().setAutoCommit(false);
                statementValidate = DBUtil.getConnection().prepareStatement("SELECT IdImpuesto FROM ImpuestoTB WHERE IdImpuesto = ?");
                statementValidate.setInt(1, impuestoTB.getIdImpuesto());
                if (statementValidate.executeQuery().next()) {

                    statementValidate = DBUtil.getConnection().prepareStatement("SELECT Nombre FROM ImpuestoTB WHERE IdImpuesto <> ? AND Nombre = ?");
                    statementValidate.setInt(1, impuestoTB.getIdImpuesto());
                    statementValidate.setString(2, impuestoTB.getNombreImpuesto());
                    if (statementValidate.executeQuery().next()) {
                        DBUtil.getConnection().rollback();
                        result = "duplicated";
                    } else {
                        statementImpuesto = DBUtil.getConnection().prepareStatement("UPDATE ImpuestoTB SET Operacion=?,Nombre=?,Valor=?,CodigoAlterno=? WHERE IdImpuesto = ?");
                        statementImpuesto.setInt(1, impuestoTB.getOperacion());
                        statementImpuesto.setString(2, impuestoTB.getNombreImpuesto());
                        statementImpuesto.setDouble(3, impuestoTB.getValor());
                        statementImpuesto.setString(4, impuestoTB.getCodigo());
                        statementImpuesto.setInt(5, impuestoTB.getIdImpuesto());
                        statementImpuesto.addBatch();
                        statementImpuesto.executeBatch();
                        DBUtil.getConnection().commit();
                        result = "updated";
                    }

                } else {

                    statementValidate = DBUtil.getConnection().prepareStatement("SELECT Nombre FROM ImpuestoTB WHERE Nombre = ?");
                    statementValidate.setString(1, impuestoTB.getNombreImpuesto());
                    if (statementValidate.executeQuery().next()) {
                        DBUtil.getConnection().rollback();
                        result = "duplicated";
                    } else {
                        statementImpuesto = DBUtil.getConnection().prepareStatement("INSERT INTO ImpuestoTB(Operacion,Nombre,Valor,Predeterminado,CodigoAlterno,Sistema) values(?,?,?,?,?,?)");
                        statementImpuesto.setInt(1, impuestoTB.getOperacion());
                        statementImpuesto.setString(2, impuestoTB.getNombreImpuesto());
                        statementImpuesto.setDouble(3, impuestoTB.getValor());
                        statementImpuesto.setBoolean(4, impuestoTB.getPredeterminado());
                        statementImpuesto.setString(5, impuestoTB.getCodigo());
                        statementImpuesto.setBoolean(6, impuestoTB.isSistema());
                        statementImpuesto.addBatch();
                        statementImpuesto.executeBatch();
                        DBUtil.getConnection().commit();
                        result = "inserted";
                    }
                }
            } catch (SQLException ex) {
                try {
                    DBUtil.getConnection().rollback();
                    result = ex.getLocalizedMessage();
                } catch (SQLException e) {
                    result = e.getLocalizedMessage();
                }
            } finally {
                try {
                    if (statementValidate != null) {
                        statementValidate.close();
                    }
                    if (statementImpuesto != null) {
                        statementImpuesto.close();
                    }
                    DBUtil.dbDisconnect();
                } catch (SQLException ex) {
                    result = ex.getLocalizedMessage();
                }
            }
        }
        return result;
    }

    public static ObservableList<ImpuestoTB> ListImpuestos() {
        ObservableList<ImpuestoTB> observableList = FXCollections.observableArrayList();
        DBUtil.dbConnect();
        if (DBUtil.getConnection() != null) {
            PreparedStatement statementList = null;
            try {
                statementList = DBUtil.getConnection().prepareStatement("{call Sp_Listar_Impuestos()}");
                try (ResultSet resultSet = statementList.executeQuery()) {
                    while (resultSet.next()) {
                        ImpuestoTB impuestoTB = new ImpuestoTB();
                        impuestoTB.setId(resultSet.getRow());
                        impuestoTB.setIdImpuesto(resultSet.getInt("IdImpuesto"));
                        impuestoTB.setNombreOperacion(resultSet.getString("Operacion"));
                        impuestoTB.setNombreImpuesto(resultSet.getString("Nombre"));
                        impuestoTB.setValor(resultSet.getDouble("Valor"));
                        impuestoTB.setPredeterminado(resultSet.getBoolean("Predeterminado"));
                        impuestoTB.setCodigo(resultSet.getString("CodigoAlterno"));
                        impuestoTB.setImagePredeterminado(resultSet.getBoolean("Predeterminado")
                                ? new ImageView(new Image("/view/image/checked.png", 22, 22, false, false))
                                : new ImageView(new Image("/view/image/unchecked.png", 22, 22, false, false)));
                        impuestoTB.setSistema(resultSet.getBoolean("Sistema"));
                        observableList.add(impuestoTB);
                    }
                }
            } catch (SQLException ex) {
                System.out.println("Error en ImpuestoADO:" + ex.getLocalizedMessage());
            } finally {
                try {
                    if (statementList != null) {
                        statementList.close();
                    }
                    DBUtil.dbDisconnect();
                } catch (SQLException ex) {
                    System.out.println("Error en ImpuestoADO:" + ex.getLocalizedMessage());
                }
            }
        }

        return observableList;
    }

    public static ObservableList<ImpuestoTB> GetTipoImpuestoCombBox() {
        ObservableList<ImpuestoTB> empList = FXCollections.observableArrayList();
        DBUtil.dbConnect();
        if (DBUtil.getConnection() != null) {
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                statement = DBUtil.getConnection().prepareStatement("{call Sp_Listar_Impuesto_Calculo()}");
                resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    ImpuestoTB impuestoTB = new ImpuestoTB();
                    impuestoTB.setIdImpuesto(resultSet.getInt("IdImpuesto")); 
                    impuestoTB.setOperacion(resultSet.getInt("Operacion")); 
                    impuestoTB.setNombreOperacion(resultSet.getString("OperacionNombre"));
                    impuestoTB.setNombreImpuesto(resultSet.getString("Nombre"));
                    impuestoTB.setValor(resultSet.getDouble("Valor"));
                    impuestoTB.setPredeterminado(resultSet.getBoolean("Predeterminado"));
                    impuestoTB.setSistema(resultSet.getBoolean("Sistema")); 
                    empList.add(impuestoTB);
                }
            } catch (SQLException ex) {
                System.out.println("Error Impuesto: " + ex.getLocalizedMessage());
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
                    System.out.println("Error Impuesto: " + ex.getLocalizedMessage());
                }
            }
        }
        return empList;
    }

    public static String ChangeDefaultStateImpuesto(boolean state, int id) {
        String result = null;
        DBUtil.dbConnect();
        if (DBUtil.getConnection() != null) {
            PreparedStatement statementSelect = null;
            PreparedStatement statementUpdate = null;
            PreparedStatement statementState = null;
            try {
                System.out.println("Entre ImpuestoADO");
                DBUtil.getConnection().setAutoCommit(false);
                statementSelect = DBUtil.getConnection().prepareStatement("SELECT Predeterminado FROM ImpuestoTB WHERE Predeterminado = 1");
                if (statementSelect.executeQuery().next()) {
                    statementUpdate = DBUtil.getConnection().prepareStatement("UPDATE ImpuestoTB SET Predeterminado = 0 WHERE Predeterminado = 1");
                    statementUpdate.addBatch();

                    statementState = DBUtil.getConnection().prepareStatement("UPDATE ImpuestoTB SET Predeterminado = ? WHERE IdImpuesto = ?");
                    statementState.setBoolean(1, state);
                    statementState.setInt(2, id);
                    statementState.addBatch();

                    statementUpdate.executeBatch();
                    statementState.executeBatch();
                    DBUtil.getConnection().commit();
                    result = "updated";
                } else {
                    statementState = DBUtil.getConnection().prepareStatement("UPDATE ImpuestoTB SET Predeterminado = ? WHERE IdImpuesto = ?");
                    statementState.setBoolean(1, state);
                    statementState.setInt(2, id);
                    statementState.addBatch();
                    statementState.executeBatch();
                    DBUtil.getConnection().commit();
                    result = "updated";
                }

            } catch (SQLException ex) {
                try {
                    DBUtil.getConnection().rollback();
                    result = ex.getLocalizedMessage();
                } catch (SQLException e) {
                    result = e.getLocalizedMessage();
                }

            } finally {
                try {
                    if (statementSelect != null) {
                        statementSelect.close();
                    }
                    if (statementUpdate != null) {
                        statementUpdate.close();
                    }
                    if (statementState != null) {
                        statementState.close();
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

    public static String DeleteImpuestoById(int idImpuesto) {
        String result = "";
        DBUtil.dbConnect();
        if (DBUtil.getConnection() != null) {
            PreparedStatement statementValidation = null;
            PreparedStatement statementImpuesto = null;
            try {
                DBUtil.getConnection().setAutoCommit(false);
                statementValidation = DBUtil.getConnection().prepareStatement("SELECT * FROM SuministroTB WHERE Impuesto = ?");
                statementValidation.setInt(1, idImpuesto);
                if (statementValidation.executeQuery().next()) {
                    DBUtil.getConnection().rollback();
                    result = "suministro";
                } else {
                    statementValidation = DBUtil.getConnection().prepareStatement("SELECT * FROM ImpuestoTB WHERE IdImpuesto = ? AND Sistema = ?");
                    statementValidation.setInt(1, idImpuesto);
                    statementValidation.setBoolean(2, true);
                    if (statementValidation.executeQuery().next()) {
                        DBUtil.getConnection().rollback();
                        result = "sistema";
                    } else {
                        statementImpuesto = DBUtil.getConnection().prepareStatement("DELETE FROM ImpuestoTB WHERE IdImpuesto = ?");
                        statementImpuesto.setInt(1, idImpuesto);
                        statementImpuesto.addBatch();
                        statementImpuesto.executeBatch();
                        DBUtil.getConnection().commit();
                        result = "deleted";
                    }
                }
            } catch (SQLException ex) {
                try {
                    result = ex.getLocalizedMessage();
                    DBUtil.getConnection().rollback();
                } catch (SQLException e) {
                    result = e.getLocalizedMessage();
                }
            } finally {
                try {
                    if (statementValidation != null) {
                        statementValidation.close();
                    }
                    if (statementImpuesto != null) {
                        statementImpuesto.close();
                    }
                    DBUtil.dbDisconnect();
                } catch (SQLException ex) {
                    result = ex.getLocalizedMessage();
                }
            }
        } else {
            result = "No se puedo conectar el servidor, revise su conexión.";
        }
        return result;
    }

    public static ImpuestoTB GetImpuestoById(int idImpuesto) {
        ImpuestoTB impuestoTB = null;
        DBUtil.dbConnect();
        if (DBUtil.getConnection() != null) {
            PreparedStatement statement = null;    
            try {
                statement = DBUtil.getConnection().prepareStatement("SELECT Operacion,Nombre,Valor,CodigoAlterno FROM ImpuestoTB WHERE IdImpuesto = ?");
                statement.setInt(1, idImpuesto);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        impuestoTB = new ImpuestoTB();
                        impuestoTB.setOperacion(resultSet.getInt("Operacion"));
                        impuestoTB.setNombreImpuesto(resultSet.getString("Nombre"));
                        impuestoTB.setValor(resultSet.getDouble("Valor"));
                        impuestoTB.setCodigo(resultSet.getString("CodigoAlterno"));
                    }
                }
            } catch (SQLException ex) {
                System.out.println("Error Impuesto: " + ex.getLocalizedMessage());
            } finally {
                try {
                    if (statement != null) {
                        statement.close();
                    }                    
                    DBUtil.dbDisconnect();
                } catch (SQLException ex) {
                    System.out.println("Error Impuesto: " + ex.getLocalizedMessage());
                }
            }
        }
        return impuestoTB;
    }
}
