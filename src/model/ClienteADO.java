package model;

import controller.tools.Session;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ClienteADO {

    public static String CrudCliente(ClienteTB clienteTB) {
        String result = "";
        DBUtil.dbConnect();
        if (DBUtil.getConnection() != null) {
            CallableStatement codigoCliente = null;
            PreparedStatement preparedCliente = null;
            PreparedStatement preparedValidation = null;
            try {
                DBUtil.getConnection().setAutoCommit(false);
                preparedValidation = DBUtil.getConnection().prepareStatement("select IdCliente from ClienteTB where IdCliente = ?");
                preparedValidation.setString(1, clienteTB.getIdCliente());
                if (preparedValidation.executeQuery().next()) {
                    preparedValidation = DBUtil.getConnection().prepareStatement("select NumeroDocumento from ClienteTB where IdCliente <> ? and NumeroDocumento = ?");
                    preparedValidation.setString(1, clienteTB.getIdCliente());
                    preparedValidation.setString(2, clienteTB.getNumeroDocumento());
                    if (preparedValidation.executeQuery().next()) {
                        DBUtil.getConnection().rollback();
                        result = "duplicate";
                    } else {
                        preparedCliente = DBUtil.getConnection().prepareStatement("update ClienteTB set TipoDocumento=?,NumeroDocumento=?,Informacion=UPPER(?),Telefono=?,Celular=?,Email=?,Direccion=?,Representante=?,Estado=?,Predeterminado=? where IdCliente = ?");

                        preparedCliente.setInt(1, clienteTB.getTipoDocumento());
                        preparedCliente.setString(2, clienteTB.getNumeroDocumento());
                        preparedCliente.setString(3, clienteTB.getInformacion());
                        preparedCliente.setString(4, clienteTB.getTelefono());
                        preparedCliente.setString(5, clienteTB.getCelular());
                        preparedCliente.setString(6, clienteTB.getEmail());
                        preparedCliente.setString(7, clienteTB.getDireccion());
                        preparedCliente.setString(8, clienteTB.getRepresentante());
                        preparedCliente.setInt(9, clienteTB.getEstado());
                        preparedCliente.setBoolean(10, clienteTB.isPredeterminado());
                        preparedCliente.setString(11, clienteTB.getIdCliente());

                        preparedCliente.addBatch();
                        preparedCliente.executeBatch();

                        DBUtil.getConnection().commit();
                        result = "updated";
                    }

                } else {
                    preparedValidation = DBUtil.getConnection().prepareStatement("select NumeroDocumento from ClienteTB where NumeroDocumento = ?");
                    preparedValidation.setString(1, clienteTB.getNumeroDocumento());
                    if (preparedValidation.executeQuery().next()) {
                        DBUtil.getConnection().rollback();
                        result = "duplicate";
                    } else {
                        codigoCliente = DBUtil.getConnection().prepareCall("{? = call Fc_Cliente_Codigo_Alfanumerico()}");
                        codigoCliente.registerOutParameter(1, java.sql.Types.VARCHAR);
                        codigoCliente.execute();
                        String idSuministro = codigoCliente.getString(1);

                        preparedCliente = DBUtil.getConnection().prepareStatement("INSERT INTO ClienteTB(IdCliente,TipoDocumento,NumeroDocumento,Informacion,Telefono,Celular,Email,Direccion,Representante,Estado,Predeterminado,Sistema)VALUES(?,?,?,?,?,?,?,?,?,?,?,?)");
                        preparedCliente.setString(1, idSuministro);
                        preparedCliente.setInt(2, clienteTB.getTipoDocumento());
                        preparedCliente.setString(3, clienteTB.getNumeroDocumento());
                        preparedCliente.setString(4, clienteTB.getInformacion());
                        preparedCliente.setString(5, clienteTB.getTelefono());
                        preparedCliente.setString(6, clienteTB.getCelular());
                        preparedCliente.setString(7, clienteTB.getEmail());
                        preparedCliente.setString(8, clienteTB.getDireccion());
                        preparedCliente.setString(9, clienteTB.getRepresentante());
                        preparedCliente.setInt(10, clienteTB.getEstado());
                        preparedCliente.setBoolean(11, clienteTB.isPredeterminado());
                        preparedCliente.setBoolean(12, clienteTB.isSistema());

                        preparedCliente.addBatch();
                        preparedCliente.executeBatch();

                        DBUtil.getConnection().commit();
                        result = "registered";
                    }
                }
            } catch (SQLException ex) {
                try {
                    DBUtil.getConnection().rollback();
                } catch (SQLException exr) {
                }
                result = ex.getLocalizedMessage();
            } finally {
                try {
                    if (codigoCliente != null) {
                        codigoCliente.close();
                    }
                    if (preparedValidation != null) {
                        preparedValidation.close();
                    }
                    if (preparedCliente != null) {
                        preparedCliente.close();
                    }
                    DBUtil.dbDisconnect();
                } catch (SQLException ex) {
                    result = ex.getLocalizedMessage();
                }
            }
        } else {
            result = "No se puedo establecer una conexión, intente nuevamente.";
        }
        return result;
    }

    public static ObservableList<ClienteTB> ListCliente(String value) {
        String selectStmt = "{call Sp_Listar_Clientes(?)}";
        PreparedStatement preparedStatement = null;
        ResultSet rsEmps = null;
        ObservableList<ClienteTB> empList = FXCollections.observableArrayList();
        try {
            DBUtil.dbConnect();
            preparedStatement = DBUtil.getConnection().prepareStatement(selectStmt);
            preparedStatement.setString(1, value);
            rsEmps = preparedStatement.executeQuery();

            while (rsEmps.next()) {
                ClienteTB clienteTB = new ClienteTB();
                clienteTB.setId(rsEmps.getRow());
                clienteTB.setIdCliente(rsEmps.getString("IdCliente"));
                clienteTB.setNumeroDocumento(rsEmps.getString("NumeroDocumento"));
                clienteTB.setInformacion(rsEmps.getString("Informacion"));
                clienteTB.setTelefono(rsEmps.getString("Telefono"));
                clienteTB.setCelular(rsEmps.getString("Celular"));
                clienteTB.setDireccion(rsEmps.getString("Direccion"));
                clienteTB.setRepresentante(rsEmps.getString("Representante"));
                clienteTB.setEstadoName(rsEmps.getString("Estado"));
                clienteTB.setPredeterminado(rsEmps.getBoolean("Predeterminado"));
                clienteTB.setImagePredeterminado(rsEmps.getBoolean("Predeterminado")
                        ? new ImageView(new Image("/view/image/checked.png", 22, 22, false, false))
                        : new ImageView(new Image("/view/image/unchecked.png", 22, 22, false, false)));

                empList.add(clienteTB);
            }
        } catch (SQLException e) {
            System.out.println("ListCliente - La operación de selección de SQL ha fallado: " + e);

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

    public static ObservableList<ClienteTB> ListClienteVenta(String value) {
        String selectStmt = "{call Sp_Listar_Clientes_Venta(?)}";
        PreparedStatement preparedStatement = null;
        ResultSet rsEmps = null;
        ObservableList<ClienteTB> empList = FXCollections.observableArrayList();
        try {
            DBUtil.dbConnect();
            preparedStatement = DBUtil.getConnection().prepareStatement(selectStmt);
            preparedStatement.setString(1, value);
            rsEmps = preparedStatement.executeQuery();

            while (rsEmps.next()) {
                ClienteTB clienteTB = new ClienteTB();
                clienteTB.setId(rsEmps.getRow());
                clienteTB.setIdCliente(rsEmps.getString("IdCliente"));
                clienteTB.setTipoDocumentoName(rsEmps.getString("Documento"));
                clienteTB.setNumeroDocumento(rsEmps.getString("NumeroDocumento"));
                clienteTB.setInformacion(rsEmps.getString("Informacion"));
                clienteTB.setDireccion(rsEmps.getString("Direccion"));
                empList.add(clienteTB);
            }
        } catch (SQLException e) {
            System.out.println("ListClienteVenta - La operación de selección de SQL ha fallado: " + e);

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

    public static ClienteTB GetByIdCliente(String idCliente) {
        String selectStmt = "{call Sp_Get_Cliente_By_Id(?)}";
        PreparedStatement preparedStatement = null;
        ResultSet rsEmps = null;
        ClienteTB clienteTB = null;
        try {
            DBUtil.dbConnect();
            preparedStatement = DBUtil.getConnection().prepareStatement(selectStmt);
            preparedStatement.setString(1, idCliente);
            rsEmps = preparedStatement.executeQuery();

            if (rsEmps.next()) {
                clienteTB = new ClienteTB();
                clienteTB.setIdCliente(rsEmps.getString("IdCliente"));
                clienteTB.setTipoDocumento(rsEmps.getInt("TipoDocumento"));
                clienteTB.setNumeroDocumento(rsEmps.getString("NumeroDocumento"));
                clienteTB.setInformacion(rsEmps.getString("Informacion"));
                clienteTB.setTelefono(rsEmps.getString("Telefono"));
                clienteTB.setCelular(rsEmps.getString("Celular"));
                clienteTB.setEmail(rsEmps.getString("Email"));
                clienteTB.setDireccion(rsEmps.getString("Direccion"));
                clienteTB.setEstado(rsEmps.getInt("Estado"));

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
        return clienteTB;
    }

    public static List<ClienteTB> GetSearchComboBoxCliente(short opcion,String search) {
        String selectStmt = "{call Sp_Obtener_Cliente_Informacion_NumeroDocumento(?,?)}";
        PreparedStatement preparedStatement = null;
        ResultSet rsEmps = null;
         List<ClienteTB> clienteTBs = new ArrayList<>();
        try {
            DBUtil.dbConnect();
            preparedStatement = DBUtil.getConnection().prepareStatement(selectStmt);
            preparedStatement.setShort(1, opcion);
            preparedStatement.setString(2, search);
            rsEmps = preparedStatement.executeQuery();
            while(rsEmps.next()) {
                ClienteTB clienteTB = new ClienteTB();
                clienteTB.setIdCliente(rsEmps.getString("IdCliente"));
                clienteTB.setNumeroDocumento(rsEmps.getString("NumeroDocumento"));
                clienteTB.setInformacion(rsEmps.getString("Informacion"));
                clienteTB.setDireccion(rsEmps.getString("Direccion"));
                clienteTB.setCelular(rsEmps.getString("Celular"));
                clienteTBs.add(clienteTB);
            }
        } catch (SQLException e) {
            System.out.println("La operación de selección de SQL ha fallado en GetSearchComboBoxCliente(): " + e);
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
        return clienteTBs;
    }
    
    
    public static ClienteTB GetSearchClienteNumeroDocumento(short opcion,String search) {
        String selectStmt = "{call Sp_Obtener_Cliente_Informacion_NumeroDocumento(?,?)}";
        PreparedStatement preparedStatement = null;
        ResultSet rsEmps = null;
        ClienteTB clienteTB = null;
        try {
            DBUtil.dbConnect();
            preparedStatement = DBUtil.getConnection().prepareStatement(selectStmt);
            preparedStatement.setShort(1, opcion);
            preparedStatement.setString(2, search);
            rsEmps = preparedStatement.executeQuery();
            if(rsEmps.next()) {
                clienteTB = new ClienteTB();
                clienteTB.setIdCliente(rsEmps.getString("IdCliente"));
                clienteTB.setTipoDocumento(rsEmps.getInt("TipoDocumento"));
                clienteTB.setNumeroDocumento(rsEmps.getString("NumeroDocumento"));
                clienteTB.setInformacion(rsEmps.getString("Informacion"));
                clienteTB.setDireccion(rsEmps.getString("Direccion"));
                clienteTB.setCelular(rsEmps.getString("Celular"));
            }
        } catch (SQLException e) {
            System.out.println("El clienteADO-> GetSearchClienteNumeroDocumento error: " + e);
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
        return clienteTB;
    }

    public static ClienteTB GetClientePredetermined() {
        PreparedStatement statement = null;
        ClienteTB clienteTB = null;
        try {
            DBUtil.dbConnect();
            statement = DBUtil.getConnection().prepareStatement("SELECT ci.IdCliente,ci.TipoDocumento,ci.Informacion, ci.NumeroDocumento, ci.Direccion FROM ClienteTB AS ci WHERE Predeterminado = 1");
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                clienteTB = new ClienteTB();
                clienteTB.setIdCliente(resultSet.getString("IdCliente"));
                clienteTB.setTipoDocumento(resultSet.getInt("TipoDocumento"));
                clienteTB.setInformacion(resultSet.getString("Informacion"));
                clienteTB.setNumeroDocumento(resultSet.getString("NumeroDocumento"));
                clienteTB.setDireccion(resultSet.getString("Direccion"));
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
        return clienteTB;
    }

     public static String RemoveCliente(String idCliente) {
        PreparedStatement statementValidate = null;
        PreparedStatement statementCliente = null;
        DBUtil.dbConnect();
        if (DBUtil.getConnection() == null) {
            return "No se pudo realizar tu petición por problemas de conexión, intente nuevamente";
        }
        try {
            DBUtil.getConnection().setAutoCommit(false);
            statementValidate = DBUtil.getConnection().prepareStatement("SELECT * FROM VentaTB WHERE Cliente = ?");
            statementValidate.setString(1, idCliente);
            if (statementValidate.executeQuery().next()) {
                DBUtil.getConnection().rollback();
                return "venta";
            } else {
                statementValidate = DBUtil.getConnection().prepareStatement("SELECT * FROM ClienteTB WHERE IdCliente = ? AND Sistema = 1");
                statementValidate.setString(1, idCliente);
                if (statementValidate.executeQuery().next()) {
                    DBUtil.getConnection().rollback();
                    return "sistema";
                } else {
                    statementCliente = DBUtil.getConnection().prepareStatement("DELETE FROM ClienteTB WHERE IdCliente = ?");
                    statementCliente.setString(1, idCliente);
                    statementCliente.addBatch();

                    statementCliente.executeBatch();
                    DBUtil.getConnection().commit();
                    return "deleted";
                }
            }
        } catch (SQLException ex) {
            try {
                DBUtil.getConnection().rollback();
            } catch (SQLException e) {

            }
            return ex.getLocalizedMessage();
        } finally {
            try {
                if (statementValidate != null) {
                    statementValidate.close();
                }
                if (statementCliente != null) {
                    statementCliente.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException ex) {

            }
        }
    }

    public static String ChangeDefaultState(boolean state, String idCliente) {
        String result = null;
        DBUtil.dbConnect();
        if (DBUtil.getConnection() != null) {
            PreparedStatement statementSelect = null;
            PreparedStatement statementUpdate = null;
            PreparedStatement statementState = null;
            try {
                DBUtil.getConnection().setAutoCommit(false);
                statementSelect = DBUtil.getConnection().prepareStatement("SELECT Predeterminado FROM ClienteTB WHERE Predeterminado = 1");
                if (statementSelect.executeQuery().next()) {
                    statementUpdate = DBUtil.getConnection().prepareStatement("UPDATE ClienteTB SET Predeterminado = 0 WHERE Predeterminado = 1");
                    statementUpdate.addBatch();

                    statementState = DBUtil.getConnection().prepareStatement("UPDATE ClienteTB SET Predeterminado = ? WHERE IdCliente = ?");
                    statementState.setBoolean(1, state);
                    statementState.setString(2, idCliente);
                    statementState.addBatch();

                    statementUpdate.executeBatch();
                    statementState.executeBatch();
                    DBUtil.getConnection().commit();
                    result = "updated";
                } else {
                    statementState = DBUtil.getConnection().prepareStatement("UPDATE ClienteTB SET Predeterminado = ? WHERE IdCliente = ?");
                    statementState.setBoolean(1, state);
                    statementState.setString(2, idCliente);
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
                    ClienteTB clienteTB = ClienteADO.GetClientePredetermined();
                    if (clienteTB != null) {
                        Session.CLIENTE_ID = clienteTB.getIdCliente();
                        Session.CLIENTE_DATOS = clienteTB.getInformacion();
                        Session.CLIENTE_NUMERO_DOCUMENTO = clienteTB.getNumeroDocumento();
                        Session.CLIENTE_DIRECCION = clienteTB.getDireccion();
                    } else {
                        Session.CLIENTE_ID = "";
                        Session.CLIENTE_DATOS = "";
                        Session.CLIENTE_NUMERO_DOCUMENTO = "";
                        Session.CLIENTE_DIRECCION = "";
                    }
                } catch (SQLException ex) {
                    result = ex.getLocalizedMessage();
                }
            }
        } else {
            result = "No se puedo establecer conexión con el servidor, revice y vuelva a intentarlo.";
        }
        return result;

    }

}
