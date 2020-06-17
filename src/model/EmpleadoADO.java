package model;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;

public class EmpleadoADO {

    public static String InsertEmpleado(EmpleadoTB empleadoTB) {
        PreparedStatement empleado = null;
        CallableStatement codigo_empleado = null;

        try {
            DBUtil.dbConnect();
            DBUtil.getConnection().setAutoCommit(false);

            codigo_empleado = DBUtil.getConnection().prepareCall("{? = call Fc_Empleado_Codigo_Alfanumerico()}");
            codigo_empleado.registerOutParameter(1, java.sql.Types.VARCHAR);
            codigo_empleado.execute();
            String id_empleado = codigo_empleado.getString(1);

            empleado = DBUtil.getConnection().prepareStatement("INSERT INTO EmpleadoTB(IdEmpleado,TipoDocumento,NumeroDocumento,Apellidos,Nombres,Sexo,FechaNacimiento,Puesto,Rol,Estado,Telefono,Celular,Email,Direccion,Pais,Ciudad,Provincia,Distrito,Usuario,Clave)\n"
                    + "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

            empleado.setString(1, id_empleado);
            empleado.setInt(2, empleadoTB.getTipoDocumento());
            empleado.setString(3, empleadoTB.getNumeroDocumento());
            empleado.setString(4, empleadoTB.getApellidos());
            empleado.setString(5, empleadoTB.getNombres());
            empleado.setInt(6, empleadoTB.getSexo());
            empleado.setDate(7, empleadoTB.getFechaNacimiento());
            empleado.setInt(8, empleadoTB.getPuesto());
            empleado.setInt(9, empleadoTB.getRol());
            empleado.setInt(10, empleadoTB.getEstado());
            empleado.setString(11, empleadoTB.getTelefono());
            empleado.setString(12, empleadoTB.getCelular());
            empleado.setString(13, empleadoTB.getEmail());
            empleado.setString(14, empleadoTB.getDireccion());
            empleado.setString(15, empleadoTB.getPais());
            empleado.setInt(16, empleadoTB.getCiudad());
            empleado.setInt(17, empleadoTB.getProvincia());
            empleado.setInt(18, empleadoTB.getDistrito());
            empleado.setString(19, empleadoTB.getUsuario());
            empleado.setString(20, empleadoTB.getClave());
            empleado.addBatch();

            empleado.executeBatch();

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
                if (empleado != null) {
                    empleado.close();
                }
                if (codigo_empleado != null) {
                    codigo_empleado.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException e) {
            }
        }
    }

    public static String UpdateEmpleado(EmpleadoTB empleadoTB) {
        PreparedStatement empleado = null;
        CallableStatement codigo_empleado = null;

        try {
            DBUtil.dbConnect();
            DBUtil.getConnection().setAutoCommit(false);

            empleado = DBUtil.getConnection().prepareStatement("UPDATE EmpleadoTB SET TipoDocumento = ?,NumeroDocumento = ?,Apellidos = ?,Nombres = ?,Sexo = ?,FechaNacimiento = ?,Puesto = ?,Rol = ?,Estado = ?,Telefono = ?,Celular = ?,Email = ?,Direccion = ?,Pais = ?,Ciudad = ?,Provincia = ?,Distrito = ?,Usuario = ?,Clave  = ? WHERE IdEmpleado = ?");

            empleado.setInt(1, empleadoTB.getTipoDocumento());
            empleado.setString(2, empleadoTB.getNumeroDocumento());
            empleado.setString(3, empleadoTB.getApellidos());
            empleado.setString(4, empleadoTB.getNombres());
            empleado.setInt(5, empleadoTB.getSexo());
            empleado.setDate(6, empleadoTB.getFechaNacimiento());
            empleado.setInt(7, empleadoTB.getPuesto());
            empleado.setInt(8, empleadoTB.getRol());
            empleado.setInt(9, empleadoTB.getEstado());
            empleado.setString(10, empleadoTB.getTelefono());
            empleado.setString(11, empleadoTB.getCelular());
            empleado.setString(12, empleadoTB.getEmail());
            empleado.setString(13, empleadoTB.getDireccion());
            empleado.setString(14, empleadoTB.getPais());
            empleado.setInt(15, empleadoTB.getCiudad());
            empleado.setInt(16, empleadoTB.getProvincia());
            empleado.setInt(17, empleadoTB.getDistrito());
            empleado.setString(18, empleadoTB.getUsuario());
            empleado.setString(19, empleadoTB.getClave());
            empleado.setString(20, empleadoTB.getIdEmpleado());
            empleado.addBatch();

            empleado.executeBatch();

            DBUtil.getConnection().commit();
            return "update";
        } catch (SQLException ex) {
            try {
                DBUtil.getConnection().rollback();
                return ex.getLocalizedMessage();
            } catch (SQLException ex1) {
                return ex1.getLocalizedMessage();
            }
        } finally {
            try {
                if (empleado != null) {
                    empleado.close();
                }
                if (codigo_empleado != null) {
                    codigo_empleado.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException e) {
            }
        }
    }

    public static ObservableList<EmpleadoTB> ListEmpleados(String value) {
        String selectStmt = "{call Sp_Listar_Empleados(?)}";
        PreparedStatement preparedStatement = null;
        ResultSet rsEmps = null;
        ObservableList<EmpleadoTB> empList = FXCollections.observableArrayList();
        try {
            DBUtil.dbConnect();
            preparedStatement = DBUtil.getConnection().prepareStatement(selectStmt);
            preparedStatement.setString(1, value);
            rsEmps = preparedStatement.executeQuery();
            while (rsEmps.next()) {
                EmpleadoTB empleadoTB = new EmpleadoTB();
                empleadoTB.setId(rsEmps.getInt("Filas"));
                empleadoTB.setIdEmpleado(rsEmps.getString("IdEmpleado"));
                empleadoTB.setNumeroDocumento(rsEmps.getString("NumeroDocumento"));
                empleadoTB.setApellidos(rsEmps.getString("Apellidos"));
                empleadoTB.setNombres(rsEmps.getString("Nombres"));
                empleadoTB.setTelefono(rsEmps.getString("Telefono"));
                empleadoTB.setCelular(rsEmps.getString("Celular"));
                empleadoTB.setPuestoName(rsEmps.getString("Puesto"));
                empleadoTB.setRolName(rsEmps.getString("Rol")); 
                empleadoTB.setEstadoName(rsEmps.getString("Estado"));
                empList.add(empleadoTB);
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

    public static EmpleadoTB GetByIdEmpleados(String value) {
        String selectStmt = "SELECT * FROM EmpleadoTB WHERE IdEmpleado = ?";
        PreparedStatement preparedStatement = null;
        ResultSet rsEmps = null;
        EmpleadoTB empleadoTB = null;
        try {
            DBUtil.dbConnect();
            preparedStatement = DBUtil.getConnection().prepareStatement(selectStmt);
            preparedStatement.setString(1, value);
            rsEmps = preparedStatement.executeQuery();

            if (rsEmps.next()) {
                empleadoTB = new EmpleadoTB();
                empleadoTB.setTipoDocumento(rsEmps.getInt("TipoDocumento"));
                empleadoTB.setNumeroDocumento(rsEmps.getString("NumeroDocumento"));
                empleadoTB.setApellidos(rsEmps.getString("Apellidos"));
                empleadoTB.setNombres(rsEmps.getString("Nombres"));
                empleadoTB.setSexo(rsEmps.getInt("Sexo"));
                empleadoTB.setFechaNacimiento(rsEmps.getDate("FechaNacimiento"));
                empleadoTB.setPuesto(rsEmps.getInt("Puesto"));
                empleadoTB.setEstado(rsEmps.getInt("Estado"));
                empleadoTB.setTelefono(rsEmps.getString("Telefono"));
                empleadoTB.setCelular(rsEmps.getString("Celular"));
                empleadoTB.setEmail(rsEmps.getString("Email"));
                empleadoTB.setDireccion(rsEmps.getString("Direccion"));
                empleadoTB.setPais(rsEmps.getString("Pais"));
                empleadoTB.setCiudad(rsEmps.getInt("Ciudad"));
                empleadoTB.setProvincia(rsEmps.getInt("Provincia"));
                empleadoTB.setDistrito(rsEmps.getInt("Distrito"));
                empleadoTB.setUsuario(rsEmps.getString("Usuario"));
                empleadoTB.setClave(rsEmps.getString("Clave"));
                empleadoTB.setRol(rsEmps.getInt("Rol"));
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
        return empleadoTB;
    }

    public static EmpleadoTB GetValidateUser(String user, String clave) {

        String selectStmt = "{CALL Sp_Validar_Ingreso(?,?)}";
        PreparedStatement preparedStatement = null;
        ResultSet rsEmps = null;
        EmpleadoTB empleadoTB = null;
        DBUtil.dbConnect();
        if (DBUtil.getConnection() != null) {
            System.out.println("model.EmpleadoADO.GetValidateUser()");
            try {
                preparedStatement = DBUtil.getConnection().prepareStatement(selectStmt);
                preparedStatement.setString(1, user);
                preparedStatement.setString(2, clave);
                rsEmps = preparedStatement.executeQuery();
                empleadoTB = new EmpleadoTB();
                if (rsEmps.next()) {                    
                    empleadoTB.setIdEmpleado(rsEmps.getString("IdEmpleado"));
                    empleadoTB.setApellidos(rsEmps.getString("Apellidos"));
                    empleadoTB.setNombres(rsEmps.getString("Nombres"));
                    empleadoTB.setRolName(rsEmps.getString("RolName"));
                    empleadoTB.setEstado(rsEmps.getInt("Estado"));
                    empleadoTB.setRol(rsEmps.getInt("Rol"));
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
            return empleadoTB;
        } else {
            return empleadoTB;
        }
    }
    
    public static ObservableList<EmpleadoTB> ListEmployeeInProProduction(String value) {
        String selectStmt = "{call Sp_Listar_Empleados(?)}";
        //String selectStmt = "select * from EmpleadoTB";
        PreparedStatement preparedStatement = null;
        ResultSet rsEmps = null;
        ObservableList<EmpleadoTB> empList = FXCollections.observableArrayList();
        try {
            DBUtil.dbConnect();
            preparedStatement = DBUtil.getConnection().prepareStatement(selectStmt);
            preparedStatement.setString(1, value);
            rsEmps = preparedStatement.executeQuery();

            while (rsEmps.next()) {
                EmpleadoTB empleadoTB = new EmpleadoTB();
                empleadoTB.setId(rsEmps.getInt("Filas"));
                //empleadoTB.setId(rsEmps.getRow());
                empleadoTB.setIdEmpleado(rsEmps.getString("IdEmpleado"));
                empleadoTB.setNumeroDocumento(rsEmps.getString("NumeroDocumento"));
                empleadoTB.setApellidos(rsEmps.getString("Apellidos"));
                empleadoTB.setNombres(rsEmps.getString("Nombres"));
                empleadoTB.setTelefono(rsEmps.getString("Telefono"));
                empleadoTB.setCelular(rsEmps.getString("Celular"));
                empleadoTB.setPuestoName(rsEmps.getString("Puesto"));
                empleadoTB.setEstadoName(rsEmps.getString("Estado"));
                
                CheckBox checkBox = new CheckBox();
                checkBox.getStyleClass().add("check-box-contenido");
                checkBox.setText("");
                empleadoTB.setValidarEm(checkBox);
                
                empList.add(empleadoTB);
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
    
      public static List<EmpleadoTB> Lista_Empleado_For_Compras(){
        List<EmpleadoTB> empleadoTBs = new ArrayList<>();
        DBUtil.dbConnect();
        if(DBUtil.getConnection() != null){
            PreparedStatement statementEmpleados = null;
            try{
                statementEmpleados = DBUtil.getConnection().prepareStatement("SELECT IdEmpleado,Apellidos,Nombres FROM EmpleadoTB ORDER BY Apellidos ASC");
                ResultSet resultSet = statementEmpleados.executeQuery();
                while(resultSet.next()){
                    EmpleadoTB empleadoTB = new EmpleadoTB();
                    empleadoTB.setIdEmpleado(resultSet.getString("IdEmpleado"));
                    empleadoTB.setNombres(resultSet.getString("Apellidos").toUpperCase());
                    empleadoTB.setApellidos(resultSet.getString("Nombres").toUpperCase());
                    empleadoTBs.add(empleadoTB);
                }
            }catch(SQLException ex){
                
            }finally{
                try{
                    if(statementEmpleados != null){
                        statementEmpleados.close();
                    }
                    DBUtil.dbDisconnect();
                }catch(SQLException ex){
                    
                }
            }
        }
        return empleadoTBs;
    }

}
