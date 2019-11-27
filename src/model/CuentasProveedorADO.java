package model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CuentasProveedorADO {

    public static String crudPagoProveedores(CuentasHistorialProveedorTB cuentasHistorialProveedorTB, boolean pagado, String idCompra) {

        String result = "";

        PreparedStatement pago_Proveedores = null;
        PreparedStatement update_Compra = null;
        PreparedStatement statementBuscar = null;
        try {
            DBUtil.dbConnect();
            DBUtil.getConnection().setAutoCommit(false);

            pago_Proveedores = DBUtil.getConnection().prepareStatement("INSERT INTO "
                    + "CuentasHistorialProveedorTB("
                    + "IdCuentasProveedor,"
                    + "Monto,"
                    + "Cuota,"
                    + "Fecha,"
                    + "Hora,"
                    + "Observacion,"
                    + "Estado,"
                    + "Usuario)"
                    + "values(?,?,?,?,?,?,?,?)");

            update_Compra = DBUtil.getConnection().prepareStatement("UPDATE CompraTB SET EstadoCompra = ? WHERE IdCompra = ?");

            statementBuscar = DBUtil.getConnection().prepareStatement("SELECT COUNT(Cuota) FROM CuentasHistorialProveedorTB WHERE IdCuentasProveedor = ?");
            statementBuscar.setInt(1, cuentasHistorialProveedorTB.getIdCuentasProveedor());
            ResultSet resultSet = statementBuscar.executeQuery();
            int couta = resultSet.next() ? resultSet.getInt(1) + 1 : 1;

            pago_Proveedores.setInt(1, cuentasHistorialProveedorTB.getIdCuentasProveedor());
            pago_Proveedores.setDouble(2, cuentasHistorialProveedorTB.getMonto());
            pago_Proveedores.setInt(3, couta);
            pago_Proveedores.setString(4, cuentasHistorialProveedorTB.getFecha());
            pago_Proveedores.setString(5, cuentasHistorialProveedorTB.getHora());
            pago_Proveedores.setString(6, cuentasHistorialProveedorTB.getObservacion());
            pago_Proveedores.setShort(7, cuentasHistorialProveedorTB.getEstado());
            pago_Proveedores.setString(8, cuentasHistorialProveedorTB.getUsuario());
            pago_Proveedores.addBatch();

            if (pagado) {
                update_Compra.setInt(1, 1);
                update_Compra.setString(2, idCompra);
                update_Compra.addBatch();

                pago_Proveedores.executeBatch();
                update_Compra.executeBatch();
                DBUtil.getConnection().commit();
                result = "completed";
            } else {
                pago_Proveedores.executeBatch();
                DBUtil.getConnection().commit();
                result = "register";
            }

        } catch (SQLException ex) {
            try {
                DBUtil.getConnection().rollback();
                result = ex.getLocalizedMessage();
            } catch (SQLException ex1) {
                result = ex1.getLocalizedMessage();
            }
        } finally {
            try {
                if (pago_Proveedores != null) {
                    pago_Proveedores.close();
                }
                if (update_Compra != null) {
                    update_Compra.close();
                }
                if (statementBuscar != null) {
                    statementBuscar.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException e) {
                result = e.getLocalizedMessage();
            }
        }
        return result;
    }

    public static List<Object> Lista_Completa_Historial_Pago_Por_IdCompra(String idCompra) {
        List<Object> objects = new ArrayList<Object>();
        DBUtil.dbConnect();
        if (DBUtil.getConnection() != null) {
            PreparedStatement statementCuentas = null;
            PreparedStatement statementCuentasHistorial = null;
            try {
                statementCuentas = DBUtil.getConnection().prepareStatement("{call Sp_Get_Cuentas_Proveedor_By_IdCompra(?)}");
                statementCuentas.setString(1, idCompra);
                ResultSet resultSetCuentas = statementCuentas.executeQuery();
                if (resultSetCuentas.next()) {
                    CuentasProveedorTB cuentasProveedorTB = new CuentasProveedorTB();
                    cuentasProveedorTB.setIdCuentasProveedor(resultSetCuentas.getInt("IdCuentasProveedor"));
                    cuentasProveedorTB.setMontoTotal(resultSetCuentas.getDouble("MontoTotal"));
                    cuentasProveedorTB.setPlazosName(resultSetCuentas.getString("Plazos"));
                    cuentasProveedorTB.setFechaPago(resultSetCuentas.getDate("FechaPago").toLocalDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
                    cuentasProveedorTB.setFechaRegistro(resultSetCuentas.getDate("FechaRegistro").toLocalDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
                    objects.add(cuentasProveedorTB);

                    ObservableList<CuentasHistorialProveedorTB> observableList = FXCollections.observableArrayList();
                    statementCuentasHistorial = DBUtil.getConnection().prepareStatement("SELECT * FROM CuentasHistorialProveedorTB WHERE IdCuentasProveedor = ?");
                    statementCuentasHistorial.setInt(1, cuentasProveedorTB.getIdCuentasProveedor());
                    ResultSet resultSetHistorial = statementCuentasHistorial.executeQuery();
//
                    while (resultSetHistorial.next()) {

                        CuentasHistorialProveedorTB cuentasHistorialProveedorTB = new CuentasHistorialProveedorTB();
                        cuentasHistorialProveedorTB.setId(resultSetHistorial.getRow());
                        cuentasHistorialProveedorTB.setIdCuentasHistorialProveedor(resultSetHistorial.getInt("IdCuentasHistorialProveedor"));
                        cuentasHistorialProveedorTB.setMonto(resultSetHistorial.getDouble("Monto"));
                        cuentasHistorialProveedorTB.setCuota(resultSetHistorial.getInt("Cuota"));
                        cuentasHistorialProveedorTB.setFecha(resultSetHistorial.getDate("Fecha").toLocalDate().format(DateTimeFormatter.ISO_DATE));
                        cuentasHistorialProveedorTB.setHora(resultSetHistorial.getTime("Hora").toLocalTime().format(DateTimeFormatter.ofPattern("hh:mm:ss a")));
                        cuentasHistorialProveedorTB.setObservacion(resultSetHistorial.getString("Observacion"));
                        cuentasHistorialProveedorTB.setEstado(resultSetHistorial.getShort("Estado"));

                        observableList.add(cuentasHistorialProveedorTB);
                    }
//
                    objects.add(observableList);
                }

            } catch (SQLException ex) {
                System.out.println("Error en Cuentas Proveedor:" + ex.getLocalizedMessage());
            } finally {
                try {
                    if (statementCuentas != null) {
                        statementCuentas.close();
                    }
                    if (statementCuentasHistorial != null) {
                        statementCuentasHistorial.close();
                    }
                    DBUtil.dbDisconnect();
                } catch (SQLException ex) {
                    System.out.println("Error en Cuentas Proveedor:" + ex.getLocalizedMessage());
                }
            }
        }
        return objects;
    }

    public static String EliminarPagoCuota(int idCuotasProveedor, String idCompra, String observacion) {

        String result = "";
        PreparedStatement statementPagoCuota = null;
        PreparedStatement update_Compra = null;
        try {

            DBUtil.dbConnect();
            DBUtil.getConnection().setAutoCommit(false);

            statementPagoCuota = DBUtil.getConnection().prepareStatement("UPDATE CuentasHistorialProveedorTB SET Monto = ?,Observacion = ?,Estado = ? where IdCuentasHistorialProveedor = ?");
            statementPagoCuota.setDouble(1, 0);
            statementPagoCuota.setString(2, observacion);
            statementPagoCuota.setShort(3, (short) 2);
            statementPagoCuota.setInt(4, idCuotasProveedor);
            statementPagoCuota.addBatch();

            update_Compra = DBUtil.getConnection().prepareStatement("UPDATE CompraTB SET EstadoCompra = ? WHERE IdCompra = ?");
            update_Compra.setInt(1, 2);
            update_Compra.setString(2, idCompra);
            update_Compra.addBatch();

            statementPagoCuota.executeBatch();
            update_Compra.executeBatch();
            DBUtil.getConnection().commit();
    
            result = "removed";
        } catch (SQLException ex) {
            try {
                result = ex.getLocalizedMessage();
                DBUtil.getConnection().rollback();
            } catch (SQLException e) {
                result = e.getLocalizedMessage();
            }
        } finally {
            try {
                if (statementPagoCuota != null) {
                    statementPagoCuota.close();
                }
                if (update_Compra != null) {
                    update_Compra.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException ex) {
                result = ex.getLocalizedMessage();
            }

        }
        return result;
    }

}
