package model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class MonedaADO {

    public static String CrudMoneda(MonedaTB monedaTB) {
        String result = null;
        DBUtil.dbConnect();
        if (DBUtil.getConnection() != null) {
            PreparedStatement statementValidate = null;
            PreparedStatement statementMoneda = null;
            try {
                DBUtil.getConnection().setAutoCommit(false);
                statementValidate = DBUtil.getConnection().prepareStatement("SELECT IdMoneda FROM MonedaTB WHERE IdMoneda = ?");
                statementValidate.setInt(1, monedaTB.getIdMoneda());
                if (statementValidate.executeQuery().next()) {
                    statementValidate = DBUtil.getConnection().prepareStatement("SELECT Nombre FROM MonedaTB WHERE IdMoneda <> ? AND Nombre = ?");
                    statementValidate.setInt(1, monedaTB.getIdMoneda());
                    statementValidate.setString(2, monedaTB.getNombre());
                    if (statementValidate.executeQuery().next()) {
                        DBUtil.getConnection().rollback();
                        result = "duplicated";
                    } else {
                        statementMoneda = DBUtil.getConnection().prepareStatement("UPDATE MonedaTB SET Nombre=?,Abreviado=?,Simbolo=?,TipoCambio=?,Predeterminado=? WHERE IdMoneda = ?");
                        statementMoneda.setString(1, monedaTB.getNombre());
                        statementMoneda.setString(2, monedaTB.getAbreviado());
                        statementMoneda.setString(3, monedaTB.getSimbolo());
                        statementMoneda.setDouble(4, monedaTB.getTipoCambio());
                        statementMoneda.setBoolean(5, monedaTB.getPredeterminado());
                        statementMoneda.setInt(6, monedaTB.getIdMoneda());
                        statementMoneda.addBatch();
                        statementMoneda.executeBatch();
                        DBUtil.getConnection().commit();
                        result = "updated";
                    }
                } else {
                    statementValidate = DBUtil.getConnection().prepareStatement("SELECT Nombre FROM MonedaTB WHERE Nombre = ?");
                    statementValidate.setString(1, monedaTB.getNombre());
                    if (statementValidate.executeQuery().next()) {
                        DBUtil.getConnection().rollback();
                        result = "duplicated";
                    } else {
                        statementMoneda = DBUtil.getConnection().prepareStatement("INSERT INTO MonedaTB(Nombre,Abreviado,Simbolo,TipoCambio,Predeterminado,Sistema) VALUES(?,?,?,?,?,?)");
                        statementMoneda.setString(1, monedaTB.getNombre());
                        statementMoneda.setString(2, monedaTB.getAbreviado());
                        statementMoneda.setString(3, monedaTB.getSimbolo());
                        statementMoneda.setDouble(4, monedaTB.getTipoCambio());
                        statementMoneda.setBoolean(5, monedaTB.getPredeterminado());
                        statementMoneda.setBoolean(6, monedaTB.getSistema());
                        statementMoneda.addBatch();
                        statementMoneda.executeBatch();
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
                    if (statementMoneda != null) {
                        statementMoneda.close();
                    }
                    DBUtil.dbDisconnect();
                } catch (SQLException ex) {
                    result = ex.getLocalizedMessage();
                }
            }
        }
        return result;
    }

    public static ObservableList<MonedaTB> ListMonedas() {
        ObservableList<MonedaTB> observableList = FXCollections.observableArrayList();
        DBUtil.dbConnect();
        if (DBUtil.getConnection() != null) {
            PreparedStatement statementList = null;
            try {
                statementList = DBUtil.getConnection().prepareStatement("{call Sp_Listar_Monedas()}");
                ResultSet resultSet = statementList.executeQuery();
                while (resultSet.next()) {
                    MonedaTB monedaTB = new MonedaTB();
                    monedaTB.setId(resultSet.getRow());
                    monedaTB.setIdMoneda(resultSet.getInt("IdMoneda"));
                    monedaTB.setNombre(resultSet.getString("Nombre"));
                    monedaTB.setAbreviado(resultSet.getString("Abreviado"));
                    monedaTB.setSimbolo(resultSet.getString("Simbolo"));
                    monedaTB.setTipoCambio(resultSet.getDouble("TipoCambio"));
                    monedaTB.setPredeterminado(resultSet.getBoolean("Predeterminado"));
                    monedaTB.setImagePredeterminado(resultSet.getBoolean("Predeterminado")
                            ? new ImageView(new Image("/view/image/checked.png", 22, 22, false, false))
                            : new ImageView(new Image("/view/image/unchecked.png", 22, 22, false, false)));
                    observableList.add(monedaTB);
                }
            } catch (SQLException ex) {
                System.out.println("Error en MonedaADO:" + ex.getLocalizedMessage());
            } finally {
                try {
                    if (statementList != null) {
                        statementList.close();
                    }
                    DBUtil.dbDisconnect();
                } catch (SQLException ex) {
                    System.out.println("Error en MonedaADO:" + ex.getLocalizedMessage());
                }
            }
        }
        return observableList;
    }

    public static String ChangeDefaultState(boolean state, int id) {
        String result = null;
        DBUtil.dbConnect();
        if (DBUtil.getConnection() != null) {
            PreparedStatement statementSelect = null;
            PreparedStatement statementUpdate = null;
            PreparedStatement statementState = null;
            try {
                DBUtil.getConnection().setAutoCommit(false);
                statementSelect = DBUtil.getConnection().prepareStatement("SELECT Predeterminado FROM MonedaTB WHERE Predeterminado = 1");
                if (statementSelect.executeQuery().next()) {
                    statementUpdate = DBUtil.getConnection().prepareStatement("UPDATE MonedaTB SET Predeterminado = 0 WHERE Predeterminado = 1");
                    statementUpdate.addBatch();

                    statementState = DBUtil.getConnection().prepareStatement("UPDATE MonedaTB SET Predeterminado = ? WHERE IdMoneda = ?");
                    statementState.setBoolean(1, state);
                    statementState.setInt(2, id);
                    statementState.addBatch();

                    statementUpdate.executeBatch();
                    statementState.executeBatch();
                    DBUtil.getConnection().commit();
                    result = "updated";
                } else {
                    statementState = DBUtil.getConnection().prepareStatement("UPDATE MonedaTB SET Predeterminado = ? WHERE IdMoneda = ?");
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

    public static String RemoveElement(int idMoneda) {
        String result = null;
        DBUtil.dbConnect();
        if (DBUtil.getConnection() != null) {
            PreparedStatement statementValidate = null;
            PreparedStatement statementRemove = null;
            try {
                DBUtil.getConnection().setAutoCommit(false);
                statementValidate = DBUtil.getConnection().prepareStatement("SELECT * FROM MonedaTB WHERE IdMoneda = ? AND Predeterminado = ?");
                statementValidate.setInt(1, idMoneda);
                statementValidate.setBoolean(2, true);
                if (statementValidate.executeQuery().next()) {
                    DBUtil.getConnection().rollback();
                    result = "predetermined";
                } else {
                    statementValidate = DBUtil.getConnection().prepareStatement("SELECT * FROM VentaTB WHERE Moneda = ?");
                    statementValidate.setInt(1, idMoneda);
                    if (statementValidate.executeQuery().next()) {
                        DBUtil.getConnection().rollback();
                        result = "venta";
                    } else {
                        statementValidate = DBUtil.getConnection().prepareStatement("SELECT * FROM CompraTB WHERE TipoMoneda ?");
                        statementValidate.setInt(1, idMoneda);
                        if (statementValidate.executeQuery().next()) {
                            DBUtil.getConnection().rollback();
                            result = "compra";
                        } else {
                            statementRemove = DBUtil.getConnection().prepareStatement("DELETE FROM MonedaTB WHERE IdMoneda = ?");
                            statementRemove.setInt(1, idMoneda);
                            statementRemove.addBatch();
                            statementRemove.executeBatch();
                            DBUtil.getConnection().commit();
                            result = "removed";
                        }
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
                    if (statementRemove != null) {
                        statementRemove.close();
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

    public static String GetMonedaPredetermined() {
        PreparedStatement statement = null;
        try {
            DBUtil.dbConnect();
            statement = DBUtil.getConnection().prepareStatement("SELECT Simbolo FROM MonedaTB WHERE Predeterminado = 1");
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("Simbolo");
            } else {
                return "M";
            }
        } catch (SQLException ex) {
            System.out.println("Error Moneda: " + ex.getLocalizedMessage());
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException e) {
                System.out.println("Error Moneda: " + e.getLocalizedMessage());
            }
        }
        return "M";
    }

    public static List<MonedaTB> GetMonedasCombBox() {
        List<MonedaTB> list = new ArrayList<>();
        DBUtil.dbConnect();
        if (DBUtil.getConnection() != null) {
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                statement = DBUtil.getConnection().prepareStatement("SELECT IdMoneda,Nombre,Simbolo,Predeterminado FROM MonedaTB");
                resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    MonedaTB monedaTB = new MonedaTB();
                    monedaTB.setIdMoneda(resultSet.getInt("IdMoneda"));
                    monedaTB.setNombre(resultSet.getString("Nombre"));
                    monedaTB.setSimbolo(resultSet.getString("Simbolo"));
                    monedaTB.setPredeterminado(resultSet.getBoolean("Predeterminado"));
                    list.add(monedaTB);
                }
            } catch (SQLException ex) {
                System.out.println("Error Moneda: " + ex.getLocalizedMessage());
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
                    System.out.println("Error Moneda: " + ex.getLocalizedMessage());
                }
            }
        }
        return list;
    }

}
