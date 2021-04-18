package model;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PedidoADO {

    public static String CrudPedido(PedidoTB pedidoTB) {
        CallableStatement statementIdPedido = null;
        PreparedStatement statementPedido = null;
        PreparedStatement statementPedidoDetalle = null;
        try {
            DBUtil.dbConnect();
            DBUtil.getConnection().setAutoCommit(false);

            statementIdPedido = DBUtil.getConnection().prepareCall("{? = call Fc_Pedido_Codigo_Alfanumerico()}");
            statementIdPedido.registerOutParameter(1, java.sql.Types.VARCHAR);
            statementIdPedido.execute();
            String idPedido = statementIdPedido.getString(1);

            statementPedido = DBUtil.getConnection().prepareStatement("INSERT INTO PedidoTB(IdPedido,IdProveedor,FechaEmision,HoraEmision,FechaVencimiento,HoraVencimiento,IdMoneda,IdFormaPago,Observacion) VALUES(?,?,?,?,?,?,?,?,?)");
            statementPedido.setString(1, idPedido);
            statementPedido.setString(2, pedidoTB.getIdProveedor());
            statementPedido.setString(3, pedidoTB.getFechaEmision());
            statementPedido.setString(4, pedidoTB.getHoraEmision());
            statementPedido.setString(5, pedidoTB.getFechaVencimiento());
            statementPedido.setString(6, pedidoTB.getHoraVencimiento());
            statementPedido.setInt(7, pedidoTB.getIdMoneda());
            statementPedido.setInt(8, pedidoTB.getIdFormaPago());
            statementPedido.setString(9, pedidoTB.getObservacion());
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
            return "inserted";
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
                if (statementIdPedido != null) {
                    statementIdPedido.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException ex) {
                return ex.getLocalizedMessage();
            }
        }
    }

}
