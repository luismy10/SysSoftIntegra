package model;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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
                        preparedCliente = DBUtil.getConnection().prepareStatement("update ClienteTB set TipoDocumento=?,NumeroDocumento=?,Informacion=UPPER(?),Telefono=?,Celular=?,Email=?,Direccion=?,Representante=?,Estado=? where IdCliente = ?");

                        preparedCliente.setInt(1, clienteTB.getTipoDocumento());
                        preparedCliente.setString(2, clienteTB.getNumeroDocumento());
                        preparedCliente.setString(3, clienteTB.getInformacion());
                        preparedCliente.setString(4, clienteTB.getTelefono());
                        preparedCliente.setString(5, clienteTB.getCelular());
                        preparedCliente.setString(6, clienteTB.getEmail());
                        preparedCliente.setString(7, clienteTB.getDireccion());
                        preparedCliente.setString(8, clienteTB.getRepresentante());
                        preparedCliente.setInt(9, clienteTB.getEstado());
                        preparedCliente.setString(10, clienteTB.getIdCliente());

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

                        preparedCliente = DBUtil.getConnection().prepareStatement("insert into ClienteTB(IdCliente,TipoDocumento,NumeroDocumento,Informacion,Telefono,Celular,Email,Direccion,Representante,Estado)values(?,?,?,?,?,?,?,?,?,?)");
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
                clienteTB.setNumeroDocumento(rsEmps.getString("NumeroDocumento"));
                clienteTB.setInformacion(rsEmps.getString("Informacion"));
                clienteTB.setTelefono(rsEmps.getString("Telefono"));
                clienteTB.setCelular(rsEmps.getString("Celular"));
                clienteTB.setDireccion(rsEmps.getString("Direccion"));
                clienteTB.setRepresentante(rsEmps.getString("Representante"));
                clienteTB.setEstadoName(rsEmps.getString("Estado"));
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

    public static ClienteTB GetByIdCliente(String documento) {
        String selectStmt = "{call Sp_Get_Cliente_By_Id(?)}";
        PreparedStatement preparedStatement = null;
        ResultSet rsEmps = null;
        ClienteTB clienteTB = null;
        try {
            DBUtil.dbConnect();
            preparedStatement = DBUtil.getConnection().prepareStatement(selectStmt);
            preparedStatement.setString(1, documento);
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

    public static ClienteTB GetByIdClienteVenta(String documento) {
        String selectStmt = "select ci.IdCliente,ci.Informacion, ci.NumeroDocumento, ci.Direccion from ClienteTB as ci where ci.NumeroDocumento = ?";
        PreparedStatement preparedStatement = null;
        ResultSet rsEmps = null;
        ClienteTB clienteTB = null;
        try {
            DBUtil.dbConnect();
            preparedStatement = DBUtil.getConnection().prepareStatement(selectStmt);
            preparedStatement.setString(1, documento);
            rsEmps = preparedStatement.executeQuery();

            if (rsEmps.next()) {
                clienteTB = new ClienteTB();
                clienteTB.setIdCliente(rsEmps.getString("IdCliente"));
                clienteTB.setInformacion(rsEmps.getString("Informacion"));
                clienteTB.setNumeroDocumento(rsEmps.getString("NumeroDocumento"));
                clienteTB.setDireccion(rsEmps.getString("Direccion"));
            }
        } catch (SQLException e) {
            System.out.println("La operación de selección de SQL ha fallado en GetByIdClienteVenta(): " + e);
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

}
