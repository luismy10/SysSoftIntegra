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

public class TipoDocumentoADO {

    public static String CrudTipoDocumento(TipoDocumentoTB documentoTB) {
        String result = null;
        DBUtil.dbConnect();
        if (DBUtil.getConnection() != null) {
            PreparedStatement statementUpdate = null;
            PreparedStatement statementState = null;
            try {
                DBUtil.getConnection().setAutoCommit(false);
                statementState = DBUtil.getConnection().prepareStatement("SELECT * FROM TipoDocumentoTB WHERE IdTipoDocumento <> ? AND Nombre = ? ");
                statementState.setInt(1, documentoTB.getIdTipoDocumento());
                statementState.setString(2, documentoTB.getNombre());
                if (statementState.executeQuery().next()) {
                    DBUtil.getConnection().rollback();
                    result = "duplicate";
                } else {
                    statementUpdate = DBUtil.getConnection().prepareStatement("UPDATE TipoDocumentoTB SET Nombre = ?,Predeterminado=?,NombreImpresion=? WHERE IdTipoDocumento = ?");
                    statementUpdate.setString(1, documentoTB.getNombre());
                    statementUpdate.setBoolean(2, documentoTB.isPredeterminado());
                    statementUpdate.setString(3, documentoTB.getNombreDocumento());
                    statementUpdate.setInt(4, documentoTB.getIdTipoDocumento());
                    statementUpdate.addBatch();

                    statementUpdate.executeBatch();
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

    public static ObservableList<TipoDocumentoTB> ListTipoDocumento() {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        ObservableList<TipoDocumentoTB> observableList = FXCollections.observableArrayList();
        try {
            DBUtil.dbConnect();
            statement = DBUtil.getConnection().prepareStatement("SELECT * FROM TipoDocumentoTB");
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                observableList.add(new TipoDocumentoTB(
                        resultSet.getInt("IdTipoDocumento"),
                        resultSet.getString("Nombre"),
                        resultSet.getBoolean("Predeterminado"),
                        resultSet.getString("NombreImpresion"),
                        resultSet.getBoolean("Predeterminado")
                        ? new ImageView(new Image("/view/image/checked.png", 22, 22, false, false))
                        : new ImageView(new Image("/view/image/unchecked.png", 22, 22, false, false))));
            }
        } catch (SQLException ex) {
            System.out.println("Tipo de Documento ADO:" + ex.getLocalizedMessage());
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
                System.out.println("Tipo de Documento ADO:" + ex.getLocalizedMessage());
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
                statementSelect = DBUtil.getConnection().prepareStatement("SELECT Predeterminado FROM TipoDocumentoTB WHERE Predeterminado = 1");
                if (statementSelect.executeQuery().next()) {
                    statementUpdate = DBUtil.getConnection().prepareStatement("UPDATE TipoDocumentoTB SET Predeterminado = 0 WHERE Predeterminado = 1");
                    statementUpdate.addBatch();

                    statementState = DBUtil.getConnection().prepareStatement("UPDATE TipoDocumentoTB SET Predeterminado = ? WHERE IdTipoDocumento = ?");
                    statementState.setBoolean(1, state);
                    statementState.setInt(2, id);
                    statementState.addBatch();

                    statementUpdate.executeBatch();
                    statementState.executeBatch();
                    DBUtil.getConnection().commit();
                    result = "updated";
                } else {
                    statementState = DBUtil.getConnection().prepareStatement("UPDATE TipoDocumentoTB SET Predeterminado = ? WHERE IdTipoDocumento = ?");
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

    public static List<TipoDocumentoTB> GetDocumentoCombBox() {
        List<TipoDocumentoTB> list = new ArrayList<>();
        DBUtil.dbConnect();
        if (DBUtil.getConnection() != null) {
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                statement = DBUtil.getConnection().prepareStatement("SELECT IdTipoDocumento,Nombre,Predeterminado,NombreImpresion FROM TipoDocumentoTB");
                resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    TipoDocumentoTB documentoTB = new TipoDocumentoTB();
                    documentoTB.setIdTipoDocumento(resultSet.getInt("IdTipoDocumento"));
                    documentoTB.setNombre(resultSet.getString("Nombre"));
                    documentoTB.setPredeterminado(resultSet.getBoolean("Predeterminado"));
                    documentoTB.setNombreDocumento(resultSet.getString("NombreImpresion"));
                    list.add(documentoTB);
                }
            } catch (SQLException ex) {
                System.out.println("Error Tipo de documento: " + ex.getLocalizedMessage());
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
                    System.out.println("Error Tipo de documento: " + ex.getLocalizedMessage());
                }
            }
        }
        return list;
    }

}
