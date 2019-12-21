package model;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ClienteADO {

    public static String CrudCliente(ClienteTB clienteTB) {
        String selectStmt = "{call Sp_Crud_Persona_Cliente(?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
        CallableStatement callableStatement = null;
        try {
            DBUtil.dbConnect();
            callableStatement = DBUtil.getConnection().prepareCall(selectStmt);
            //cliente
            callableStatement.setString("IdCliente", clienteTB.getIdCliente());
            callableStatement.setInt("TipoDocumento", clienteTB.getTipoDocumento());
            callableStatement.setString("NumeroDocumento", clienteTB.getNumeroDocumento());
            callableStatement.setString("Apellidos", clienteTB.getApellidos());
            callableStatement.setString("Nombres", clienteTB.getNombres());
            callableStatement.setInt("Sexo", clienteTB.getSexo());
            callableStatement.setDate("FechaNacimiento", clienteTB.getFechaNacimiento());
            callableStatement.setString("Telefono", clienteTB.getTelefono());
            callableStatement.setString("Celular", clienteTB.getCelular());
            callableStatement.setString("Email", clienteTB.getEmail());
            callableStatement.setString("Direccion", clienteTB.getDireccion());
            callableStatement.setString("Representante", clienteTB.getRepresentante());
            callableStatement.setInt("Estado", clienteTB.getEstado());

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
                clienteTB.setApellidos(rsEmps.getString("Apellidos"));
                clienteTB.setNombres(rsEmps.getString("Nombres"));
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
                clienteTB.setApellidos(rsEmps.getString("Apellidos"));
                clienteTB.setNombres(rsEmps.getString("Nombres"));
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
                clienteTB.setApellidos(rsEmps.getString("Apellidos"));
                clienteTB.setNombres(rsEmps.getString("Nombres"));
                clienteTB.setSexo(rsEmps.getInt("Sexo"));
                clienteTB.setFechaNacimiento(rsEmps.getDate("FechaNacimiento"));
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
        String selectStmt = "select ci.IdCliente,ci.Apellidos,ci.Nombres, ci.NumeroDocumento, ci.Direccion from ClienteTB as ci where ci.NumeroDocumento = ?";
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
                clienteTB.setApellidos(rsEmps.getString("Apellidos"));
                clienteTB.setNombres(rsEmps.getString("Nombres"));
                clienteTB.setNumeroDocumento(rsEmps.getString("NumeroDocumento"));
                clienteTB.setDireccion(rsEmps.getString("Direccion"));
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

}
