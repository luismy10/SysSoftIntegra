package model;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PedidoADO {

    public static Object CrudPedido(PedidoTB pedidoTB) {
        CallableStatement statementIdPedido = null;
        PreparedStatement statementValidar = null;
        PreparedStatement statementPedido = null;
        PreparedStatement statementPedidoDetalle = null;
        PreparedStatement statementDetallePedidoBorrar = null;
        try {
            DBUtil.dbConnect();
            DBUtil.getConnection().setAutoCommit(false);
            Object[] object = new Object[2];

            statementValidar = DBUtil.getConnection().prepareCall("SELECT * FROM PedidoTB WHERE IdPedido = ?");
            statementValidar.setString(1, pedidoTB.getIdPedido());
            if (statementValidar.executeQuery().next()) {

                statementPedido = DBUtil.getConnection().prepareStatement("UPDATE PedidoTB SET IdProveedor=?,IdVendedor=?,FechaEmision=?,HoraEmision=?,FechaVencimiento=?,HoraVencimiento=?,IdMoneda=?,IdFormaPago=?,Observacion=? WHERE IdPedido = ?");
                statementPedido.setString(1, pedidoTB.getIdProveedor());
                statementPedido.setString(2, pedidoTB.getIdVendedor());
                statementPedido.setString(3, pedidoTB.getFechaEmision());
                statementPedido.setString(4, pedidoTB.getHoraEmision());
                statementPedido.setString(5, pedidoTB.getFechaVencimiento());
                statementPedido.setString(6, pedidoTB.getHoraVencimiento());
                statementPedido.setInt(7, pedidoTB.getIdMoneda());
                statementPedido.setInt(8, pedidoTB.getIdFormaPago());
                statementPedido.setString(9, pedidoTB.getObservacion());
                statementPedido.setString(10, pedidoTB.getIdPedido());
                statementPedido.addBatch();

                statementDetallePedidoBorrar = DBUtil.getConnection().prepareStatement("DELETE FROM PedidoDetalleTB WHERE IdPedido = ?");
                statementDetallePedidoBorrar.setString(1, pedidoTB.getIdPedido());
                statementDetallePedidoBorrar.addBatch();
                statementDetallePedidoBorrar.executeBatch();

                statementPedidoDetalle = DBUtil.getConnection().prepareStatement("INSERT INTO PedidoDetalleTB(IdPedido,IdSuministro,Cantidad,Costo,Descuento,IdImpuesto)VALUES(?,?,?,?,?,?)");
                for (PedidoDetalleTB pedidoDetalleTB : pedidoTB.getPedidoDetalleTBs()) {
                    statementPedidoDetalle.setString(1, pedidoTB.getIdPedido());
                    statementPedidoDetalle.setString(2, pedidoDetalleTB.getIdSuministro());
                    statementPedidoDetalle.setDouble(3, pedidoDetalleTB.getCantidad());
                    statementPedidoDetalle.setDouble(4, pedidoDetalleTB.getCosto());
                    statementPedidoDetalle.setDouble(5, pedidoDetalleTB.getDescuento());
                    statementPedidoDetalle.setInt(6, pedidoDetalleTB.getIdImpuesto());
                    statementPedidoDetalle.addBatch();
                }

                statementPedido.executeBatch();
                statementPedidoDetalle.executeBatch();
                DBUtil.getConnection().commit();
                object[0] = "1";
                object[1] = pedidoTB.getIdPedido();
                return object;
            } else {

                statementIdPedido = DBUtil.getConnection().prepareCall("{? = call Fc_Pedido_Codigo_Alfanumerico()}");
                statementIdPedido.registerOutParameter(1, java.sql.Types.VARCHAR);
                statementIdPedido.execute();
                String idPedido = statementIdPedido.getString(1);

                statementPedido = DBUtil.getConnection().prepareStatement("INSERT INTO PedidoTB(IdPedido,IdProveedor,IdVendedor,FechaEmision,HoraEmision,FechaVencimiento,HoraVencimiento,IdMoneda,IdFormaPago,Observacion) VALUES(?,?,?,?,?,?,?,?,?,?)");
                statementPedido.setString(1, idPedido);
                statementPedido.setString(2, pedidoTB.getIdProveedor());
                statementPedido.setString(3, pedidoTB.getIdVendedor());
                statementPedido.setString(4, pedidoTB.getFechaEmision());
                statementPedido.setString(5, pedidoTB.getHoraEmision());
                statementPedido.setString(6, pedidoTB.getFechaVencimiento());
                statementPedido.setString(7, pedidoTB.getHoraVencimiento());
                statementPedido.setInt(8, pedidoTB.getIdMoneda());
                statementPedido.setInt(9, pedidoTB.getIdFormaPago());
                statementPedido.setString(10, pedidoTB.getObservacion());
                statementPedido.addBatch();

                statementPedidoDetalle = DBUtil.getConnection().prepareStatement("INSERT INTO PedidoDetalleTB(IdPedido,IdSuministro,Cantidad,Costo,Descuento,IdImpuesto)VALUES(?,?,?,?,?,?)");
                for (PedidoDetalleTB pedidoDetalleTB : pedidoTB.getPedidoDetalleTBs()) {
                    statementPedidoDetalle.setString(1, idPedido);
                    statementPedidoDetalle.setString(2, pedidoDetalleTB.getIdSuministro());
                    statementPedidoDetalle.setDouble(3, pedidoDetalleTB.getCantidad());
                    statementPedidoDetalle.setDouble(4, pedidoDetalleTB.getCosto());
                    statementPedidoDetalle.setDouble(5, pedidoDetalleTB.getDescuento());
                    statementPedidoDetalle.setInt(6, pedidoDetalleTB.getIdImpuesto());
                    statementPedidoDetalle.addBatch();
                }

                statementPedido.executeBatch();
                statementPedidoDetalle.executeBatch();
                DBUtil.getConnection().commit();
                object[0] = "0";
                object[1] = idPedido;
                return object;
            }
        } catch (SQLException ex) {
            try {
                DBUtil.getConnection().rollback();
            } catch (SQLException e) {

            }
            return ex.getLocalizedMessage();
        } catch (Exception ex) {
            try {
                DBUtil.getConnection().rollback();
            } catch (SQLException e) {

            }
            return ex.getLocalizedMessage();
        } finally {
            try {
                if (statementPedido != null) {
                    statementPedido.close();
                }
                if (statementValidar != null) {
                    statementValidar.close();
                }
                if (statementIdPedido != null) {
                    statementIdPedido.close();
                }
                if (statementPedidoDetalle != null) {
                    statementPedidoDetalle.close();
                }
                if (statementDetallePedidoBorrar != null) {
                    statementDetallePedidoBorrar.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException ex) {
                return ex.getLocalizedMessage();
            }
        }
    }

    public static Object ListarPedidos(int tipo, String search, String fechaInicio, String fechaFinal, int posicionPagina, int filasPorPagina) {
        PreparedStatement statementPedidos = null;
        ResultSet result = null;
        try {
            DBUtil.dbConnect();
            Object[] object = new Object[2];

            ObservableList<PedidoTB> pedidoTBs = FXCollections.observableArrayList();
            statementPedidos = DBUtil.getConnection().prepareStatement("{CALL Sp_Listar_Pedido(?,?,?,?,?,?)}");
            statementPedidos.setInt(1, tipo);
            statementPedidos.setString(2, search);
            statementPedidos.setString(3, fechaInicio);
            statementPedidos.setString(4, fechaFinal);
            statementPedidos.setInt(5, posicionPagina);
            statementPedidos.setInt(6, filasPorPagina);
            result = statementPedidos.executeQuery();
            while (result.next()) {
                PedidoTB pedidoTB = new PedidoTB();
                pedidoTB.setId(result.getRow());
                pedidoTB.setIdPedido(result.getString(""));
                pedidoTB.setFechaEmision(result.getDate("FechaEmision").toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                pedidoTB.setHoraEmision(result.getTime("HoraEmision").toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss a")));
                pedidoTB.setProveedorTB(new ProveedorTB(result.getString("NumeroDocumento"), result.getString("RazonSocial")));
                pedidoTB.setEmpleadoTB(new EmpleadoTB(result.getString("NumeroDocumento"), result.getString("Apellidos"), result.getString("Nombres")));
                pedidoTB.setMonedaTB(new MonedaTB(result.getInt("IdMoneda"), result.getString("Simbolo")));
                pedidoTB.setImporteNeto(result.getDouble("Total"));
                pedidoTBs.add(pedidoTB);
            }

            statementPedidos = DBUtil.getConnection().prepareStatement("{CALL Sp_Listar_Pedido_Count(?,?,?,?)}");
            statementPedidos.setInt(1, tipo);
            statementPedidos.setString(2, search);
            statementPedidos.setString(3, fechaInicio);
            statementPedidos.setString(4, fechaFinal);
            result = statementPedidos.executeQuery();
            Integer total = 0;
            if (result.next()) {
                total = result.getInt("Total");
            }

            object[0] = pedidoTBs;
            object[1] = total;
            return object;
        } catch (SQLException ex) {
            return ex.getLocalizedMessage();
        } finally {
            try {
                if (statementPedidos != null) {
                    statementPedidos.close();
                }
                if (result != null) {
                    result.close();
                }
            } catch (SQLException ex) {
                return ex.getLocalizedMessage();
            }
        }
    }

}
