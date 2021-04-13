package model;

import controller.tools.Session;
import controller.tools.Tools;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Random;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import static model.DBUtil.getConnection;

public class VentaADO {

    public static ResultTransaction registrarVentaContado(VentaTB ventaTB, ArrayList<SuministroTB> tvList, boolean privilegio) {
        CallableStatement serie_numeracion = null;
        CallableStatement codigoCliente = null;
        CallableStatement codigo_venta = null;
        PreparedStatement venta = null;
        PreparedStatement ventaVerificar = null;
        PreparedStatement clienteVerificar = null;
        PreparedStatement cliente = null;
        PreparedStatement comprobante = null;
        PreparedStatement detalle_venta = null;
        PreparedStatement suministro_update = null;
        PreparedStatement suministro_kardex = null;
        PreparedStatement movimiento_caja = null;
        ResultTransaction resultTransaction = new ResultTransaction();
        resultTransaction.setResult("Error en completar la petición intente nuevamente por favor.");
        try {
            DBUtil.dbConnect();
            DBUtil.getConnection().setAutoCommit(false);

            Random rd = new Random();
            int dig5 = rd.nextInt(90000) + 10000;

            int countValidate = 0;
            ArrayList<String> arrayResult = new ArrayList();
            ventaVerificar = DBUtil.getConnection().prepareStatement("SELECT Cantidad FROM SuministroTB WHERE IdSuministro = ?");
            for (int i = 0; i < tvList.size(); i++) {
                ventaVerificar.setString(1, tvList.get(i).getIdSuministro());
                ResultSet resultValidate = ventaVerificar.executeQuery();
                if (resultValidate.next()) {
                    double ca = tvList.get(i).getValorInventario() == 1 ? tvList.get(i).getCantidad() + tvList.get(i).getBonificacion() : tvList.get(i).getCantidad();
                    double cb = resultValidate.getDouble("Cantidad");
                    if (ca > cb) {
                        countValidate++;
                        arrayResult.add(tvList.get(i).getClave() + " - " + tvList.get(i).getNombreMarca() + " - Cantidad actual(" + Tools.roundingValue(cb, 2) + ")");
                    }
                }
            }

            if (privilegio && countValidate > 0) {
                DBUtil.getConnection().rollback();
                resultTransaction.setCode("nocantidades");
                resultTransaction.setArrayResult(arrayResult);
            } else {
                cliente = DBUtil.getConnection().prepareStatement("INSERT INTO ClienteTB(IdCliente,TipoDocumento,NumeroDocumento,Informacion,Telefono,Celular,Email,Direccion,Representante,Estado,Predeterminado,Sistema)VALUES(?,?,?,?,?,?,?,?,?,?,?,?)");
                ventaTB.setIdCliente(ventaTB.getClienteTB().getIdCliente());
                clienteVerificar = DBUtil.getConnection().prepareStatement("SELECT IdCliente FROM ClienteTB WHERE NumeroDocumento = ?");
                clienteVerificar.setString(1, ventaTB.getClienteTB().getNumeroDocumento().trim());
                if (clienteVerificar.executeQuery().next()) {
                    ResultSet resultSet = clienteVerificar.executeQuery();
                    resultSet.next();
                    ventaTB.setIdCliente(resultSet.getString("IdCliente"));

                    cliente = DBUtil.getConnection().prepareStatement("UPDATE ClienteTB SET TipoDocumento=?,Informacion = ?,Celular=?,Email=?,Direccion=? WHERE IdCliente =  ?");
                    cliente.setInt(1, ventaTB.getClienteTB().getTipoDocumento());
                    cliente.setString(2, ventaTB.getClienteTB().getInformacion().trim().toUpperCase());
                    cliente.setString(3, ventaTB.getClienteTB().getCelular().trim());
                    cliente.setString(4, ventaTB.getClienteTB().getEmail().trim());
                    cliente.setString(5, ventaTB.getClienteTB().getDireccion().trim());
                    cliente.setString(6, resultSet.getString("IdCliente"));
                    cliente.addBatch();

                } else {
                    codigoCliente = DBUtil.getConnection().prepareCall("{? = call Fc_Cliente_Codigo_Alfanumerico()}");
                    codigoCliente.registerOutParameter(1, java.sql.Types.VARCHAR);
                    codigoCliente.execute();
                    String idCliente = codigoCliente.getString(1);

                    cliente.setString(1, idCliente);
                    cliente.setInt(2, ventaTB.getClienteTB().getTipoDocumento());
                    cliente.setString(3, ventaTB.getClienteTB().getNumeroDocumento().trim());
                    cliente.setString(4, ventaTB.getClienteTB().getInformacion().trim().toUpperCase());
                    cliente.setString(5, "");
                    cliente.setString(6, ventaTB.getClienteTB().getCelular().trim());
                    cliente.setString(7, ventaTB.getClienteTB().getEmail().trim());
                    cliente.setString(8, ventaTB.getClienteTB().getDireccion().trim().toUpperCase());
                    cliente.setString(9, "");
                    cliente.setInt(10, 1);
                    cliente.setBoolean(11, false);
                    cliente.setBoolean(12, false);
                    cliente.addBatch();

                    ventaTB.setIdCliente(idCliente);
                }

                serie_numeracion = DBUtil.getConnection().prepareCall("{? = call Fc_Serie_Numero(?)}");
                serie_numeracion.registerOutParameter(1, java.sql.Types.VARCHAR);
                serie_numeracion.setInt(2, ventaTB.getIdComprobante());
                serie_numeracion.execute();
                String[] id_comprabante = serie_numeracion.getString(1).split("-");

                codigo_venta = DBUtil.getConnection().prepareCall("{? = call Fc_Venta_Codigo_Alfanumerico()}");
                codigo_venta.registerOutParameter(1, java.sql.Types.VARCHAR);
                codigo_venta.execute();

                String id_venta = codigo_venta.getString(1);

                venta = DBUtil.getConnection().prepareStatement("INSERT INTO VentaTB\n"
                        + "           (IdVenta\n"
                        + "           ,Cliente\n"
                        + "           ,Vendedor\n"
                        + "           ,Comprobante\n"
                        + "           ,Moneda\n"
                        + "           ,Serie\n"
                        + "           ,Numeracion\n"
                        + "           ,FechaVenta\n"
                        + "           ,HoraVenta\n"
                        + "           ,FechaVencimiento\n"
                        + "           ,HoraVencimiento\n"
                        + "           ,SubTotal\n"
                        + "           ,Descuento\n"
                        + "           ,Impuesto\n"
                        + "           ,Total"
                        + "           ,Tipo"
                        + "           ,Estado"
                        + "           ,Observaciones"
                        + "           ,Efectivo"
                        + "           ,Vuelto"
                        + "           ,Tarjeta"
                        + "           ,Codigo)\n"
                        + "     VALUES\n"
                        + "           (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

                comprobante = DBUtil.getConnection().prepareStatement("INSERT INTO ComprobanteTB(IdTipoDocumento,Serie,Numeracion,FechaRegistro)VALUES(?,?,?,?)");

                detalle_venta = DBUtil.getConnection().prepareStatement("INSERT INTO DetalleVentaTB\n"
                        + "(IdVenta\n"
                        + ",IdArticulo\n"
                        + ",Cantidad\n"
                        + ",CostoVenta\n"
                        + ",PrecioVenta\n"
                        + ",Descuento\n"
                        + ",IdOperacion\n"
                        + ",IdImpuesto\n"
                        + ",NombreImpuesto\n"
                        + ",ValorImpuesto\n"
                        + ",Importe"
                        + ",Bonificacion\n"
                        + ",PorLlevar\n"
                        + ",Estado)\n"
                        + "VALUES\n"
                        + "(?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

                suministro_kardex = DBUtil.getConnection().prepareStatement("INSERT INTO "
                        + "KardexSuministroTB("
                        + "IdSuministro,"
                        + "Fecha,"
                        + "Hora,"
                        + "Tipo,"
                        + "Movimiento,"
                        + "Detalle,"
                        + "Cantidad,"
                        + "Costo, "
                        + "Total) "
                        + "VALUES(?,?,?,?,?,?,?,?,?)");

                venta.setString(1, id_venta);
                venta.setString(2, ventaTB.getIdCliente());
                venta.setString(3, ventaTB.getVendedor());
                venta.setInt(4, ventaTB.getIdComprobante());
                venta.setInt(5, ventaTB.getIdMoneda());
                venta.setString(6, id_comprabante[0]);
                venta.setString(7, id_comprabante[1]);
                venta.setString(8, ventaTB.getFechaVenta());
                venta.setString(9, ventaTB.getHoraVenta());
                venta.setString(10, ventaTB.getFechaVenta());
                venta.setString(11, ventaTB.getHoraVenta());
                venta.setDouble(12, ventaTB.getSubTotal());
                venta.setDouble(13, ventaTB.getDescuento());
                venta.setDouble(14, ventaTB.getImpuesto());
                venta.setDouble(15, ventaTB.getTotal());
                venta.setInt(16, ventaTB.getTipo());
                venta.setInt(17, ventaTB.getEstado());
                venta.setString(18, ventaTB.getObservaciones());
                venta.setDouble(19, ventaTB.getEfectivo());
                venta.setDouble(20, ventaTB.getVuelto());
                venta.setDouble(21, ventaTB.getTarjeta());
                venta.setString(22, Integer.toString(dig5) + id_comprabante[1]);
                venta.addBatch();

                comprobante.setInt(1, ventaTB.getIdComprobante());
                comprobante.setString(2, id_comprabante[0]);
                comprobante.setString(3, id_comprabante[1]);
                comprobante.setString(4, ventaTB.getFechaVenta());
                comprobante.addBatch();

                suministro_update = DBUtil.getConnection().prepareStatement("UPDATE SuministroTB SET Cantidad = Cantidad - ? WHERE IdSuministro = ?");

                for (SuministroTB sm : tvList) {

                    double cantidad = sm.getValorInventario() == 2 ? sm.getImporteNeto() / sm.getPrecioVentaGeneralAuxiliar() : sm.getCantidad();
                    double precio = sm.getValorInventario() == 2 ? sm.getPrecioVentaGeneralAuxiliar() : sm.getPrecioVentaGeneral();
                    Tools.println(sm.getPrecioVentaGeneralAuxiliar());

                    detalle_venta.setString(1, id_venta);
                    detalle_venta.setString(2, sm.getIdSuministro());
                    detalle_venta.setDouble(3, cantidad);
                    detalle_venta.setDouble(4, sm.getCostoCompra());
                    detalle_venta.setDouble(5, precio);
                    detalle_venta.setDouble(6, sm.getDescuento());
                    detalle_venta.setDouble(7, sm.getImpuestoOperacion());
                    detalle_venta.setDouble(8, sm.getImpuestoId());
                    detalle_venta.setString(9, sm.getImpuestoNombre());
                    detalle_venta.setDouble(10, sm.getImpuestoValor());
                    detalle_venta.setDouble(11, precio * sm.getCantidad());
                    detalle_venta.setDouble(12, sm.getBonificacion());
                    detalle_venta.setDouble(13, cantidad);
                    detalle_venta.setString(14, "C");
                    detalle_venta.addBatch();

                    if (sm.isInventario() && sm.getValorInventario() == 1) {
                        suministro_update.setDouble(1, sm.getCantidad() + sm.getBonificacion());
                        suministro_update.setString(2, sm.getIdSuministro());
                        suministro_update.addBatch();
                    } else if (sm.isInventario() && sm.getValorInventario() == 2) {
                        suministro_update.setDouble(1, sm.getImporteNeto() / sm.getPrecioVentaGeneralAuxiliar());
                        suministro_update.setString(2, sm.getIdSuministro());
                        suministro_update.addBatch();
                    } else if (sm.isInventario() && sm.getValorInventario() == 3) {
                        suministro_update.setDouble(1, sm.getCantidad());
                        suministro_update.setString(2, sm.getIdSuministro());
                        suministro_update.addBatch();
                    }

                    double cantidadKardex = sm.getValorInventario() == 1
                            ? sm.getCantidad() + sm.getBonificacion()
                            : sm.getValorInventario() == 2
                            ? sm.getImporteNeto() / sm.getPrecioVentaGeneralAuxiliar()
                            : sm.getCantidad();

                    suministro_kardex.setString(1, sm.getIdSuministro());
                    suministro_kardex.setString(2, ventaTB.getFechaVenta());
                    suministro_kardex.setString(3, Tools.getHour());
                    suministro_kardex.setShort(4, (short) 2);
                    suministro_kardex.setInt(5, 1);
                    suministro_kardex.setString(6, "VENTA CON SERIE Y NUMERACIÓN: " + id_comprabante[0] + "-" + id_comprabante[1] + (sm.getBonificacion() <= 0 ? "" : "(BONIFICACIÓN: " + sm.getBonificacion() + ")"));
                    suministro_kardex.setDouble(7, cantidadKardex);
                    suministro_kardex.setDouble(8, sm.getCostoCompra());
                    suministro_kardex.setDouble(9, cantidadKardex * sm.getCostoCompra());
                    suministro_kardex.addBatch();
                }

                movimiento_caja = DBUtil.getConnection().prepareStatement("INSERT INTO MovimientoCajaTB(IdCaja,FechaMovimiento,HoraMovimiento,Comentario,TipoMovimiento,Monto,IdProcedencia)VALUES(?,?,?,?,?,?,?)");
                if (ventaTB.getEfectivo() > 0) {
                    movimiento_caja.setString(1, Session.CAJA_ID);
                    movimiento_caja.setString(2, ventaTB.getFechaVenta());
                    movimiento_caja.setString(3, ventaTB.getHoraVenta());
                    movimiento_caja.setString(4, "VENTA CON EFECTIVO DE SERIE Y NUMERACIÓN DEL COMPROBANTE " + id_comprabante[0] + "-" + id_comprabante[1]);
                    movimiento_caja.setShort(5, (short) 2);
                    movimiento_caja.setDouble(6, ventaTB.getTarjeta() > 0 ? ventaTB.getEfectivo() : ventaTB.getTotal());
                    movimiento_caja.setString(7, id_venta);
                    movimiento_caja.addBatch();
                }

                if (ventaTB.getTarjeta() > 0) {
                    movimiento_caja.setString(1, Session.CAJA_ID);
                    movimiento_caja.setString(2, ventaTB.getFechaVenta());
                    movimiento_caja.setString(3, ventaTB.getHoraVenta());
                    movimiento_caja.setString(4, "VENTA CON TAJETA DE SERIE Y NUMERACIÓN DEL COMPROBANTE  " + id_comprabante[0] + "-" + id_comprabante[1]);
                    movimiento_caja.setShort(5, (short) 3);
                    movimiento_caja.setDouble(6, ventaTB.getTarjeta());
                    movimiento_caja.setString(7, id_venta);
                    movimiento_caja.addBatch();
                }

                cliente.executeBatch();
                venta.executeBatch();
                movimiento_caja.executeBatch();
                comprobante.executeBatch();
                suministro_update.executeBatch();
                detalle_venta.executeBatch();
                suministro_kardex.executeBatch();
                DBUtil.getConnection().commit();
                resultTransaction.setCode("register");
                resultTransaction.setResult(id_venta);
            }
        } catch (SQLException ex) {
            try {
                DBUtil.getConnection().rollback();
                resultTransaction.setCode("error");
                resultTransaction.setResult(ex.getLocalizedMessage());
            } catch (SQLException ex1) {
                resultTransaction.setCode("error");
                resultTransaction.setResult(ex1.getLocalizedMessage());
            }
        } finally {
            try {
                if (serie_numeracion != null) {
                    serie_numeracion.close();
                }
                if (codigoCliente != null) {
                    codigoCliente.close();
                }
                if (venta != null) {
                    venta.close();
                }
                if (ventaVerificar != null) {
                    ventaVerificar.close();
                }
                if (clienteVerificar != null) {
                    clienteVerificar.close();
                }

                if (cliente != null) {
                    cliente.close();
                }
                if (comprobante != null) {
                    comprobante.close();
                }
                if (suministro_update != null) {
                    suministro_update.close();
                }
                if (detalle_venta != null) {
                    detalle_venta.close();
                }
                if (suministro_kardex != null) {
                    suministro_kardex.close();
                }
                if (codigo_venta != null) {
                    codigo_venta.close();
                }
                if (movimiento_caja != null) {
                    movimiento_caja.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException e) {
            }
        }
        return resultTransaction;
    }

    public static ResultTransaction registrarVentaAdelantado(VentaTB ventaTB, ArrayList<SuministroTB> tvList) {
        CallableStatement serie_numeracion = null;
        CallableStatement codigoCliente = null;
        CallableStatement codigo_venta = null;
        PreparedStatement venta = null;
        PreparedStatement clienteVerificar = null;
        PreparedStatement cliente = null;
        PreparedStatement comprobante = null;
        PreparedStatement detalle_venta = null;
        PreparedStatement movimiento_caja = null;
        ResultTransaction resultTransaction = new ResultTransaction();
        resultTransaction.setResult("Error en completar la petición intente nuevamente por favor.");
        try {
            DBUtil.dbConnect();
            DBUtil.getConnection().setAutoCommit(false);

            Random rd = new Random();
            int dig5 = rd.nextInt(90000) + 10000;

            cliente = DBUtil.getConnection().prepareStatement("INSERT INTO ClienteTB(IdCliente,TipoDocumento,NumeroDocumento,Informacion,Telefono,Celular,Email,Direccion,Representante,Estado,Predeterminado,Sistema)VALUES(?,?,?,?,?,?,?,?,?,?,?,?)");
            ventaTB.setIdCliente(ventaTB.getClienteTB().getIdCliente());
            clienteVerificar = DBUtil.getConnection().prepareStatement("SELECT IdCliente FROM ClienteTB WHERE NumeroDocumento = ?");
            clienteVerificar.setString(1, ventaTB.getClienteTB().getNumeroDocumento().trim());
            if (clienteVerificar.executeQuery().next()) {
                ResultSet resultSet = clienteVerificar.executeQuery();
                resultSet.next();
                ventaTB.setIdCliente(resultSet.getString("IdCliente"));

                cliente = DBUtil.getConnection().prepareStatement("UPDATE ClienteTB SET TipoDocumento=?,Informacion = ?,Celular=?,Email=?,Direccion=? WHERE IdCliente =  ?");
                cliente.setInt(1, ventaTB.getClienteTB().getTipoDocumento());
                cliente.setString(2, ventaTB.getClienteTB().getInformacion().trim().toUpperCase());
                cliente.setString(3, ventaTB.getClienteTB().getCelular().trim());
                cliente.setString(4, ventaTB.getClienteTB().getEmail().trim());
                cliente.setString(5, ventaTB.getClienteTB().getDireccion().trim());
                cliente.setString(6, resultSet.getString("IdCliente"));
                cliente.addBatch();

            } else {
                codigoCliente = DBUtil.getConnection().prepareCall("{? = call Fc_Cliente_Codigo_Alfanumerico()}");
                codigoCliente.registerOutParameter(1, java.sql.Types.VARCHAR);
                codigoCliente.execute();
                String idCliente = codigoCliente.getString(1);

                cliente.setString(1, idCliente);
                cliente.setInt(2, ventaTB.getClienteTB().getTipoDocumento());
                cliente.setString(3, ventaTB.getClienteTB().getNumeroDocumento().trim());
                cliente.setString(4, ventaTB.getClienteTB().getInformacion().trim().toUpperCase());
                cliente.setString(5, "");
                cliente.setString(6, ventaTB.getClienteTB().getCelular().trim());
                cliente.setString(7, ventaTB.getClienteTB().getEmail().trim());
                cliente.setString(8, ventaTB.getClienteTB().getDireccion().trim().toUpperCase());
                cliente.setString(9, "");
                cliente.setInt(10, 1);
                cliente.setBoolean(11, false);
                cliente.setBoolean(12, false);
                cliente.addBatch();

                ventaTB.setIdCliente(idCliente);
            }

            serie_numeracion = DBUtil.getConnection().prepareCall("{? = call Fc_Serie_Numero(?)}");
            serie_numeracion.registerOutParameter(1, java.sql.Types.VARCHAR);
            serie_numeracion.setInt(2, ventaTB.getIdComprobante());
            serie_numeracion.execute();
            String[] id_comprabante = serie_numeracion.getString(1).split("-");

            codigo_venta = DBUtil.getConnection().prepareCall("{? = call Fc_Venta_Codigo_Alfanumerico()}");
            codigo_venta.registerOutParameter(1, java.sql.Types.VARCHAR);
            codigo_venta.execute();

            String id_venta = codigo_venta.getString(1);

            venta = DBUtil.getConnection().prepareStatement("INSERT INTO VentaTB\n"
                    + "           (IdVenta\n"
                    + "           ,Cliente\n"
                    + "           ,Vendedor\n"
                    + "           ,Comprobante\n"
                    + "           ,Moneda\n"
                    + "           ,Serie\n"
                    + "           ,Numeracion\n"
                    + "           ,FechaVenta\n"
                    + "           ,HoraVenta\n"
                    + "           ,FechaVencimiento\n"
                    + "           ,HoraVencimiento\n"
                    + "           ,SubTotal\n"
                    + "           ,Descuento\n"
                    + "           ,Impuesto\n"
                    + "           ,Total"
                    + "           ,Tipo"
                    + "           ,Estado"
                    + "           ,Observaciones"
                    + "           ,Efectivo"
                    + "           ,Vuelto"
                    + "           ,Tarjeta"
                    + "           ,Codigo)\n"
                    + "     VALUES\n"
                    + "           (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

            comprobante = DBUtil.getConnection().prepareStatement("INSERT INTO ComprobanteTB(IdTipoDocumento,Serie,Numeracion,FechaRegistro)VALUES(?,?,?,?)");

            detalle_venta = DBUtil.getConnection().prepareStatement("INSERT INTO DetalleVentaTB\n"
                    + "(IdVenta\n"
                    + ",IdArticulo\n"
                    + ",Cantidad\n"
                    + ",CostoVenta\n"
                    + ",PrecioVenta\n"
                    + ",Descuento\n"
                    + ",IdOperacion\n"
                    + ",IdImpuesto\n"
                    + ",NombreImpuesto\n"
                    + ",ValorImpuesto\n"
                    + ",Importe"
                    + ",Bonificacion\n"
                    + ",PorLlevar\n"
                    + ",Estado)\n"
                    + "VALUES\n"
                    + "(?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

            venta.setString(1, id_venta);
            venta.setString(2, ventaTB.getIdCliente());
            venta.setString(3, ventaTB.getVendedor());
            venta.setInt(4, ventaTB.getIdComprobante());
            venta.setInt(5, ventaTB.getIdMoneda());
            venta.setString(6, id_comprabante[0]);
            venta.setString(7, id_comprabante[1]);
            venta.setString(8, ventaTB.getFechaVenta());
            venta.setString(9, ventaTB.getHoraVenta());
            venta.setString(10, ventaTB.getFechaVenta());
            venta.setString(11, ventaTB.getHoraVenta());
            venta.setDouble(12, ventaTB.getSubTotal());
            venta.setDouble(13, ventaTB.getDescuento());
            venta.setDouble(14, ventaTB.getImpuesto());
            venta.setDouble(15, ventaTB.getTotal());
            venta.setInt(16, ventaTB.getTipo());
            venta.setInt(17, ventaTB.getEstado());
            venta.setString(18, ventaTB.getObservaciones());
            venta.setDouble(19, ventaTB.getEfectivo());
            venta.setDouble(20, ventaTB.getVuelto());
            venta.setDouble(21, ventaTB.getTarjeta());
            venta.setString(22, Integer.toString(dig5) + id_comprabante[1]);
            venta.addBatch();

            comprobante.setInt(1, ventaTB.getIdComprobante());
            comprobante.setString(2, id_comprabante[0]);
            comprobante.setString(3, id_comprabante[1]);
            comprobante.setString(4, ventaTB.getFechaVenta());
            comprobante.addBatch();

            for (SuministroTB sm : tvList) {

                double precio = sm.getValorInventario() == 2 ? sm.getPrecioVentaGeneralAuxiliar() : sm.getPrecioVentaGeneral();

                detalle_venta.setString(1, id_venta);
                detalle_venta.setString(2, sm.getIdSuministro());
                detalle_venta.setDouble(3, sm.getValorInventario() == 2 ? sm.getImporteNeto() / sm.getPrecioVentaGeneralAuxiliar() : sm.getCantidad());
                detalle_venta.setDouble(4, sm.getCostoCompra());
                detalle_venta.setDouble(5, precio);
                detalle_venta.setDouble(6, sm.getDescuento());
                detalle_venta.setDouble(7, sm.getImpuestoOperacion());
                detalle_venta.setDouble(8, sm.getImpuestoId());
                detalle_venta.setString(9, sm.getImpuestoNombre());
                detalle_venta.setDouble(10, sm.getImpuestoValor());
                detalle_venta.setDouble(11, precio * sm.getCantidad());
                detalle_venta.setDouble(12, sm.getBonificacion());
                detalle_venta.setDouble(13, 0);
                detalle_venta.setString(14, "L");
                detalle_venta.addBatch();
            }

            movimiento_caja = DBUtil.getConnection().prepareStatement("INSERT INTO MovimientoCajaTB(IdCaja,FechaMovimiento,HoraMovimiento,Comentario,TipoMovimiento,Monto,IdProcedencia)VALUES(?,?,?,?,?,?,?)");
            if (ventaTB.getEfectivo() > 0) {
                movimiento_caja.setString(1, Session.CAJA_ID);
                movimiento_caja.setString(2, ventaTB.getFechaVenta());
                movimiento_caja.setString(3, ventaTB.getHoraVenta());
                movimiento_caja.setString(4, "VENTA CON EFECTIVO DE SERIE Y NUMERACIÓN DEL COMPROBANTE " + id_comprabante[0] + "-" + id_comprabante[1]);
                movimiento_caja.setShort(5, (short) 2);
                movimiento_caja.setDouble(6, ventaTB.getTarjeta() > 0 ? ventaTB.getEfectivo() : ventaTB.getTotal());
                movimiento_caja.setString(7, id_venta);
                movimiento_caja.addBatch();
            }

            if (ventaTB.getTarjeta() > 0) {
                movimiento_caja.setString(1, Session.CAJA_ID);
                movimiento_caja.setString(2, ventaTB.getFechaVenta());
                movimiento_caja.setString(3, ventaTB.getHoraVenta());
                movimiento_caja.setString(4, "VENTA CON TAJETA DE SERIE Y NUMERACIÓN DEL COMPROBANTE  " + id_comprabante[0] + "-" + id_comprabante[1]);
                movimiento_caja.setShort(5, (short) 3);
                movimiento_caja.setDouble(6, ventaTB.getTarjeta());
                movimiento_caja.setString(7, id_venta);
                movimiento_caja.addBatch();
            }

            cliente.executeBatch();
            venta.executeBatch();
            movimiento_caja.executeBatch();
            comprobante.executeBatch();
            detalle_venta.executeBatch();
            DBUtil.getConnection().commit();
            resultTransaction.setCode("register");
            resultTransaction.setResult(id_venta);

        } catch (SQLException ex) {
            try {
                DBUtil.getConnection().rollback();
                resultTransaction.setCode("error");
                resultTransaction.setResult(ex.getLocalizedMessage());
            } catch (SQLException ex1) {
                resultTransaction.setCode("error");
                resultTransaction.setResult(ex1.getLocalizedMessage());
            }
        } finally {
            try {
                if (serie_numeracion != null) {
                    serie_numeracion.close();
                }
                if (codigoCliente != null) {
                    codigoCliente.close();
                }
                if (venta != null) {
                    venta.close();
                }
                if (clienteVerificar != null) {
                    clienteVerificar.close();
                }
                if (cliente != null) {
                    cliente.close();
                }
                if (comprobante != null) {
                    comprobante.close();
                }
                if (detalle_venta != null) {
                    detalle_venta.close();
                }
                if (codigo_venta != null) {
                    codigo_venta.close();
                }
                if (movimiento_caja != null) {
                    movimiento_caja.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException e) {
            }
        }
        return resultTransaction;
    }

    public static ResultTransaction registrarVentaCredito(VentaTB ventaTB, ArrayList<SuministroTB> tvList, boolean privilegio) {
        CallableStatement serie_numeracion = null;
        CallableStatement codigoCliente = null;
        CallableStatement codigo_venta = null;
        PreparedStatement venta = null;
        PreparedStatement ventaVerificar = null;
        PreparedStatement clienteVerificar = null;
        PreparedStatement cliente = null;
        PreparedStatement comprobante = null;
        PreparedStatement detalle_venta = null;
        PreparedStatement suministro_update = null;
        PreparedStatement suministro_kardex = null;
        ResultTransaction resultTransaction = new ResultTransaction();
        resultTransaction.setResult("Error en completar la petición intente nuevamente por favor.");
        try {
            DBUtil.dbConnect();
            DBUtil.getConnection().setAutoCommit(false);

            Random rd = new Random();
            int dig5 = rd.nextInt(90000) + 10000;

            int countValidate = 0;
            ArrayList<String> arrayResult = new ArrayList();
            ventaVerificar = DBUtil.getConnection().prepareStatement("SELECT Cantidad FROM SuministroTB WHERE IdSuministro = ?");
            for (int i = 0; i < tvList.size(); i++) {
                ventaVerificar.setString(1, tvList.get(i).getIdSuministro());
                ResultSet resultValidate = ventaVerificar.executeQuery();
                if (resultValidate.next()) {
                    double ca = tvList.get(i).getValorInventario() == 1 ? tvList.get(i).getCantidad() + tvList.get(i).getBonificacion() : tvList.get(i).getCantidad();
                    double cb = resultValidate.getDouble("Cantidad");
                    if (ca > cb) {
                        countValidate++;
                        arrayResult.add(tvList.get(i).getClave() + " - " + tvList.get(i).getNombreMarca() + " - Cantidad actual(" + Tools.roundingValue(cb, 2) + ")");
                    }
                }
            }

            if (privilegio && countValidate > 0) {
                DBUtil.getConnection().rollback();
                resultTransaction.setCode("nocantidades");
                resultTransaction.setArrayResult(arrayResult);
            } else {
                cliente = DBUtil.getConnection().prepareStatement("INSERT INTO ClienteTB(IdCliente,TipoDocumento,NumeroDocumento,Informacion,Telefono,Celular,Email,Direccion,Representante,Estado,Predeterminado,Sistema)VALUES(?,?,?,?,?,?,?,?,?,?,?,?)");
                ventaTB.setIdCliente(ventaTB.getClienteTB().getIdCliente());
                clienteVerificar = DBUtil.getConnection().prepareStatement("SELECT IdCliente FROM ClienteTB WHERE NumeroDocumento = ?");
                clienteVerificar.setString(1, ventaTB.getClienteTB().getNumeroDocumento().trim());
                if (clienteVerificar.executeQuery().next()) {
                    ResultSet resultSet = clienteVerificar.executeQuery();
                    resultSet.next();
                    ventaTB.setIdCliente(resultSet.getString("IdCliente"));

                    cliente = DBUtil.getConnection().prepareStatement("UPDATE ClienteTB SET TipoDocumento=?,Informacion = ?,Celular=?,Email=?,Direccion=? WHERE IdCliente =  ?");
                    cliente.setInt(1, ventaTB.getClienteTB().getTipoDocumento());
                    cliente.setString(2, ventaTB.getClienteTB().getInformacion().trim().toUpperCase());
                    cliente.setString(3, ventaTB.getClienteTB().getCelular().trim());
                    cliente.setString(4, ventaTB.getClienteTB().getEmail().trim());
                    cliente.setString(5, ventaTB.getClienteTB().getDireccion().trim());
                    cliente.setString(6, resultSet.getString("IdCliente"));
                    cliente.addBatch();

                } else {
                    codigoCliente = DBUtil.getConnection().prepareCall("{? = call Fc_Cliente_Codigo_Alfanumerico()}");
                    codigoCliente.registerOutParameter(1, java.sql.Types.VARCHAR);
                    codigoCliente.execute();
                    String idCliente = codigoCliente.getString(1);

                    cliente.setString(1, idCliente);
                    cliente.setInt(2, ventaTB.getClienteTB().getTipoDocumento());
                    cliente.setString(3, ventaTB.getClienteTB().getNumeroDocumento().trim());
                    cliente.setString(4, ventaTB.getClienteTB().getInformacion().trim().toUpperCase());
                    cliente.setString(5, "");
                    cliente.setString(6, ventaTB.getClienteTB().getCelular().trim());
                    cliente.setString(7, ventaTB.getClienteTB().getEmail().trim());
                    cliente.setString(8, ventaTB.getClienteTB().getDireccion().trim().toUpperCase());
                    cliente.setString(9, "");
                    cliente.setInt(10, 1);
                    cliente.setBoolean(11, false);
                    cliente.setBoolean(12, false);
                    cliente.addBatch();

                    ventaTB.setIdCliente(idCliente);
                }

                serie_numeracion = DBUtil.getConnection().prepareCall("{? = call Fc_Serie_Numero(?)}");
                serie_numeracion.registerOutParameter(1, java.sql.Types.VARCHAR);
                serie_numeracion.setInt(2, ventaTB.getIdComprobante());
                serie_numeracion.execute();
                String[] id_comprabante = serie_numeracion.getString(1).split("-");

                codigo_venta = DBUtil.getConnection().prepareCall("{? = call Fc_Venta_Codigo_Alfanumerico()}");
                codigo_venta.registerOutParameter(1, java.sql.Types.VARCHAR);
                codigo_venta.execute();

                String id_venta = codigo_venta.getString(1);

                venta = DBUtil.getConnection().prepareStatement("INSERT INTO VentaTB\n"
                        + "           (IdVenta\n"
                        + "           ,Cliente\n"
                        + "           ,Vendedor\n"
                        + "           ,Comprobante\n"
                        + "           ,Moneda\n"
                        + "           ,Serie\n"
                        + "           ,Numeracion\n"
                        + "           ,FechaVenta\n"
                        + "           ,HoraVenta\n"
                        + "           ,FechaVencimiento\n"
                        + "           ,HoraVencimiento\n"
                        + "           ,SubTotal\n"
                        + "           ,Descuento\n"
                        + "           ,Impuesto\n"
                        + "           ,Total\n"
                        + "           ,Tipo\n"
                        + "           ,Estado\n"
                        + "           ,Observaciones\n"
                        + "           ,Efectivo\n"
                        + "           ,Vuelto\n"
                        + "           ,Tarjeta\n"
                        + "           ,Codigo)\n"
                        + "     VALUES\n"
                        + "           (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

                comprobante = DBUtil.getConnection().prepareStatement("INSERT INTO ComprobanteTB(IdTipoDocumento,Serie,Numeracion,FechaRegistro)VALUES(?,?,?,?)");

                detalle_venta = DBUtil.getConnection().prepareStatement("INSERT INTO DetalleVentaTB\n"
                        + "(IdVenta\n"
                        + ",IdArticulo\n"
                        + ",Cantidad\n"
                        + ",CostoVenta\n"
                        + ",PrecioVenta\n"
                        + ",Descuento\n"
                        + ",IdOperacion\n"
                        + ",IdImpuesto\n"
                        + ",NombreImpuesto\n"
                        + ",ValorImpuesto\n"
                        + ",Importe"
                        + ",Bonificacion\n"
                        + ",PorLlevar\n"
                        + ",Estado)\n"
                        + "VALUES\n"
                        + "(?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

                suministro_update = DBUtil.getConnection().prepareStatement("UPDATE SuministroTB SET Cantidad = Cantidad - ? WHERE IdSuministro = ?");

                suministro_kardex = DBUtil.getConnection().prepareStatement("INSERT INTO "
                        + "KardexSuministroTB("
                        + "IdSuministro,"
                        + "Fecha,"
                        + "Hora,"
                        + "Tipo,"
                        + "Movimiento,"
                        + "Detalle,"
                        + "Cantidad,"
                        + "Costo, "
                        + "Total) "
                        + "VALUES(?,?,?,?,?,?,?,?,?)");

                venta.setString(1, id_venta);
                venta.setString(2, ventaTB.getIdCliente());
                venta.setString(3, ventaTB.getVendedor());
                venta.setInt(4, ventaTB.getIdComprobante());
                venta.setInt(5, ventaTB.getIdMoneda());
                venta.setString(6, id_comprabante[0]);
                venta.setString(7, id_comprabante[1]);
                venta.setString(8, ventaTB.getFechaVenta());
                venta.setString(9, ventaTB.getHoraVenta());
                venta.setString(10, ventaTB.getFechaVencimiento());
                venta.setString(11, ventaTB.getHoraVencimiento());
                venta.setDouble(12, ventaTB.getSubTotal());
                venta.setDouble(13, ventaTB.getDescuento());
                venta.setDouble(14, ventaTB.getImpuesto());
                venta.setDouble(15, ventaTB.getTotal());
                venta.setInt(16, ventaTB.getTipo());
                venta.setInt(17, ventaTB.getEstado());
                venta.setString(18, ventaTB.getObservaciones());
                venta.setDouble(19, ventaTB.getEfectivo());
                venta.setDouble(20, ventaTB.getVuelto());
                venta.setDouble(21, ventaTB.getTarjeta());
                venta.setString(22, Integer.toString(dig5) + id_comprabante[1]);
                venta.addBatch();

                comprobante.setInt(1, ventaTB.getIdComprobante());
                comprobante.setString(2, id_comprabante[0]);
                comprobante.setString(3, id_comprabante[1]);
                comprobante.setString(4, ventaTB.getFechaVenta());
                comprobante.addBatch();

                for (SuministroTB sm : tvList) {

                    double precio = sm.getValorInventario() == 2 ? sm.getPrecioVentaGeneralAuxiliar() : sm.getPrecioVentaGeneral();

                    detalle_venta.setString(1, id_venta);
                    detalle_venta.setString(2, sm.getIdSuministro());
                    detalle_venta.setDouble(3, sm.getValorInventario() == 2 ? sm.getImporteNeto() / sm.getPrecioVentaGeneralAuxiliar() : sm.getCantidad());
                    detalle_venta.setDouble(4, sm.getCostoCompra());
                    detalle_venta.setDouble(5, precio);
                    detalle_venta.setDouble(6, sm.getDescuento());
                    detalle_venta.setDouble(7, sm.getImpuestoOperacion());
                    detalle_venta.setDouble(8, sm.getImpuestoId());
                    detalle_venta.setString(9, sm.getImpuestoNombre());
                    detalle_venta.setDouble(10, sm.getImpuestoValor());
                    detalle_venta.setDouble(11, precio * sm.getCantidad());
                    detalle_venta.setDouble(12, sm.getBonificacion());
                    detalle_venta.setDouble(13, sm.getValorInventario() == 2 ? sm.getImporteNeto() / sm.getPrecioVentaGeneralAuxiliar() : sm.getCantidad());
                    detalle_venta.setString(14, "C");
                    detalle_venta.addBatch();

                    if (sm.isInventario() && sm.getValorInventario() == 1) {
                        suministro_update.setDouble(1, sm.getCantidad() + sm.getBonificacion());
                        suministro_update.setString(2, sm.getIdSuministro());
                        suministro_update.addBatch();
                    } else if (sm.isInventario() && sm.getValorInventario() == 2) {
                        suministro_update.setDouble(1, sm.getImporteNeto() / sm.getPrecioVentaGeneralAuxiliar());
                        suministro_update.setString(2, sm.getIdSuministro());
                        suministro_update.addBatch();
                    } else if (sm.isInventario() && sm.getValorInventario() == 3) {
                        suministro_update.setDouble(1, sm.getCantidad());
                        suministro_update.setString(2, sm.getIdSuministro());
                        suministro_update.addBatch();
                    }

                    double cantidadKardex = sm.getValorInventario() == 1
                            ? sm.getCantidad() + sm.getBonificacion()
                            : sm.getValorInventario() == 2
                            ? sm.getImporteNeto() / sm.getPrecioVentaGeneralAuxiliar()
                            : sm.getCantidad();

                    suministro_kardex.setString(1, sm.getIdSuministro());
                    suministro_kardex.setString(2, ventaTB.getFechaVenta());
                    suministro_kardex.setString(3, Tools.getHour());
                    suministro_kardex.setShort(4, (short) 2);
                    suministro_kardex.setInt(5, 1);
                    suministro_kardex.setString(6, "VENTA CON SERIE Y NUMERACIÓN: " + id_comprabante[0] + "-" + id_comprabante[1] + (sm.getBonificacion() <= 0 ? "" : "(BONIFICACIÓN: " + sm.getBonificacion() + ")"));
                    suministro_kardex.setDouble(7, cantidadKardex);
                    suministro_kardex.setDouble(8, sm.getCostoCompra());
                    suministro_kardex.setDouble(9, cantidadKardex * sm.getCostoCompra());
                    suministro_kardex.addBatch();
                }

//                venta_credito = DBUtil.getConnection().prepareStatement("INSERT INTO VentaCreditoTB(IdVenta,Monto,FechaRegistro,HoraRegistro,FechaPago,HoraPago,Estado) VALUES(?,?,?,?,?,?,?)");
//                for (VentaCreditoTB creditoTB : tvPlazos.getItems()) {
//                    venta_credito.setString(1, id_venta);
//                    venta_credito.setDouble(2, Double.parseDouble(creditoTB.getTfMonto().getText()));
//                    venta_credito.setString(3, Tools.getDatePicker(creditoTB.getDpFecha()));
//                    venta_credito.setString(4, Tools.getHour());
//                    venta_credito.setString(5, "");
//                    venta_credito.setString(6, "");
//                    venta_credito.setBoolean(7, creditoTB.isEstado());
//                    venta_credito.addBatch();
//                }
                cliente.executeBatch();
                venta.executeBatch();
                comprobante.executeBatch();
                detalle_venta.executeBatch();
                suministro_update.executeBatch();
                suministro_kardex.executeBatch();
//                venta_credito.executeBatch();
                DBUtil.getConnection().commit();
                resultTransaction.setCode("register");
                resultTransaction.setResult(id_venta);
            }
        } catch (SQLException ex) {
            try {
                DBUtil.getConnection().rollback();
                resultTransaction.setCode("error");
                resultTransaction.setResult(ex.getLocalizedMessage());
            } catch (SQLException ex1) {
                resultTransaction.setCode("error");
                resultTransaction.setResult(ex1.getLocalizedMessage());
            }
        } finally {
            try {
                if (serie_numeracion != null) {
                    serie_numeracion.close();
                }
                if (codigoCliente != null) {
                    codigoCliente.close();
                }
                if (venta != null) {
                    venta.close();
                }
//                if (venta_credito != null) {
//                    venta_credito.close();
//                }
                if (ventaVerificar != null) {
                    ventaVerificar.close();
                }
                if (clienteVerificar != null) {
                    clienteVerificar.close();
                }

                if (cliente != null) {
                    cliente.close();
                }
                if (comprobante != null) {
                    comprobante.close();
                }

                if (detalle_venta != null) {
                    detalle_venta.close();
                }

                if (suministro_update != null) {
                    suministro_update.close();
                }
                if (suministro_kardex != null) {
                    suministro_kardex.close();
                }
                if (codigo_venta != null) {
                    codigo_venta.close();
                }

                DBUtil.dbDisconnect();
            } catch (SQLException e) {
            }
        }
        return resultTransaction;
    }

    public static Object ListVentas(short opcion, String value, String fechaInicial, String fechaFinal, int comprobante, int estado, String usuario, int posicionPagina, int filasPorPagina) {
        PreparedStatement preparedStatement = null;
        ResultSet rsEmps = null;

        try {
            DBUtil.dbConnect();
            Object[] objects = new Object[3];
            ObservableList<VentaTB> empList = FXCollections.observableArrayList();
            preparedStatement = DBUtil.getConnection().prepareStatement("{call Sp_Listar_Ventas(?,?,?,?,?,?,?,?,?)}");
            preparedStatement.setShort(1, opcion);
            preparedStatement.setString(2, value);
            preparedStatement.setString(3, fechaInicial);
            preparedStatement.setString(4, fechaFinal);
            preparedStatement.setInt(5, comprobante);
            preparedStatement.setInt(6, estado);
            preparedStatement.setString(7, usuario);
            preparedStatement.setInt(8, posicionPagina);
            preparedStatement.setInt(9, filasPorPagina);
            rsEmps = preparedStatement.executeQuery();
            while (rsEmps.next()) {
                VentaTB ventaTB = new VentaTB();
                ventaTB.setId(rsEmps.getRow() + posicionPagina);
                ventaTB.setIdVenta(rsEmps.getString("IdVenta"));
                ventaTB.setFechaVenta(rsEmps.getDate("FechaVenta").toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                ventaTB.setHoraVenta(rsEmps.getTime("HoraVenta").toLocalTime().format(DateTimeFormatter.ofPattern("hh:mm:ss a")));
                ventaTB.setClienteTB(new ClienteTB(rsEmps.getString("DocumentoCliente"), rsEmps.getString("Cliente")));
                ventaTB.setComprobanteName(rsEmps.getString("Comprobante"));
                ventaTB.setSerie(rsEmps.getString("Serie"));
                ventaTB.setNumeracion(rsEmps.getString("Numeracion"));
                ventaTB.setTipo(rsEmps.getInt("Tipo"));
                ventaTB.setEstado(rsEmps.getInt("Estado"));
                ventaTB.setMonedaName(rsEmps.getString("Simbolo"));
                ventaTB.setTotal(rsEmps.getDouble("Total"));
                ventaTB.setObservaciones(rsEmps.getString("Observaciones"));

                if (rsEmps.getInt("IdNotaCredito") == 1) {
                    NotaCreditoTB notaCreditoTB = new NotaCreditoTB();
                    notaCreditoTB.setSerie(rsEmps.getString("SerieNotaCredito"));
                    notaCreditoTB.setNumeracion(rsEmps.getString("NumeracionNotaCredito"));
                    ventaTB.setNotaCreditoTB(notaCreditoTB);
                }

                Label label = new Label();
                if (ventaTB.getTipo() == 1 && ventaTB.getEstado() == 1 || ventaTB.getTipo() == 2 && ventaTB.getEstado() == 1) {
                    label.setText("COBRADO");
                    label.getStyleClass().add("label-asignacion");
                } else if (ventaTB.getTipo() == 2 && ventaTB.getEstado() == 2) {
                    label.setText("POR COBRAR");
                    label.getStyleClass().add("label-medio");
                } else if (ventaTB.getTipo() == 1 && ventaTB.getEstado() == 2) {
                    label.setText("POR LLEVAR");
                    label.getStyleClass().add("label-ultimo");
                } else {
                    label.setText("ANULADO");
                    label.getStyleClass().add("label-proceso");
                }

                if (ventaTB.getTipo() == 1) {
                    ventaTB.setTipoName("CONTADO");
                } else {
                    ventaTB.setTipoName("CRÉDITO");
                }
                ventaTB.setEstadoLabel(label);
                empList.add(ventaTB);
            }

            preparedStatement = DBUtil.getConnection().prepareStatement("{call Sp_Listar_Ventas_Count(?,?,?,?,?,?,?)}");
            preparedStatement.setShort(1, opcion);
            preparedStatement.setString(2, value);
            preparedStatement.setString(3, fechaInicial);
            preparedStatement.setString(4, fechaFinal);
            preparedStatement.setInt(5, comprobante);
            preparedStatement.setInt(6, estado);
            preparedStatement.setString(7, usuario);
            rsEmps = preparedStatement.executeQuery();
            Integer cantidadTotal = 0;
            if (rsEmps.next()) {
                cantidadTotal = rsEmps.getInt("Total");
            }

            preparedStatement = DBUtil.getConnection().prepareStatement("SELECT ISNULL(SUM(Total),0) as Monto FROM VentaTB as v left join NotaCreditoTB as nc on nc.IdVenta = v.IdVenta WHERE CAST(v.FechaVenta AS DATE) BETWEEN ? AND ? AND v.Tipo = 1 AND v.Estado = 1 and nc.IdNotaCredito is null");
            preparedStatement.setString(1, fechaInicial);
            preparedStatement.setString(2, fechaFinal);
            rsEmps = preparedStatement.executeQuery();
            double montoTotal = 0;
            if (rsEmps.next()) {
                montoTotal = rsEmps.getDouble("Monto");
            }

            objects[0] = empList;
            objects[1] = cantidadTotal;
            objects[2] = montoTotal;

            return objects;
        } catch (SQLException ex) {
            return ex.getLocalizedMessage();
        } catch (Exception ex) {
            return ex.getLocalizedMessage();
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
                return ex.getLocalizedMessage();
            }
        }

    }

    public static Object ListVentasMostrar(int opcion, String search, int posicionPagina, int filasPorPagina) {
        PreparedStatement preparedStatement = null;
        ResultSet rsEmps = null;
        try {
            DBUtil.dbConnect();
            Object[] object = new Object[2];

            ObservableList<VentaTB> empList = FXCollections.observableArrayList();
            preparedStatement = DBUtil.getConnection().prepareStatement("{call Sp_Listar_Ventas_Mostrar(?,?,?,?)}");
            preparedStatement.setInt(1, opcion);
            preparedStatement.setString(2, search);
            preparedStatement.setInt(3, posicionPagina);
            preparedStatement.setInt(4, filasPorPagina);
            rsEmps = preparedStatement.executeQuery();
            while (rsEmps.next()) {
                VentaTB ventaTB = new VentaTB();
                ventaTB.setId(rsEmps.getRow() + posicionPagina);
                ventaTB.setClienteTB(new ClienteTB(rsEmps.getString("Cliente")));
                ventaTB.setIdVenta(rsEmps.getString("IdVenta"));
                ventaTB.setFechaVenta(rsEmps.getDate("FechaVenta").toLocalDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
                ventaTB.setHoraVenta(rsEmps.getTime("HoraVenta").toLocalTime().format(DateTimeFormatter.ofPattern("hh:mm:ss a")));
                ventaTB.setSerie(rsEmps.getString("Serie"));
                ventaTB.setNumeracion(rsEmps.getString("Numeracion"));
                ventaTB.setMonedaName(rsEmps.getString("Simbolo"));
                ventaTB.setTotal(rsEmps.getDouble("Total"));

                Button btnImprimir = new Button();
                ImageView imageViewPrint = new ImageView(new Image("/view/image/print.png"));
                imageViewPrint.setFitWidth(24);
                imageViewPrint.setFitHeight(24);
                btnImprimir.setGraphic(imageViewPrint);
                btnImprimir.getStyleClass().add("buttonLightError");
                ventaTB.setBtnImprimir(btnImprimir);

                Button btnAddVenta = new Button();
                ImageView imageViewAccept = new ImageView(new Image("/view/image/accept.png"));
                imageViewAccept.setFitWidth(24);
                imageViewAccept.setFitHeight(24);
                btnAddVenta.setGraphic(imageViewAccept);
                btnAddVenta.getStyleClass().add("buttonLightError");
                ventaTB.setBtnAgregar(btnAddVenta);

                Button btnPlusVenta = new Button();
                ImageView imageViewPlus = new ImageView(new Image("/view/image/plus.png"));
                imageViewPlus.setFitWidth(24);
                imageViewPlus.setFitHeight(24);
                btnPlusVenta.setGraphic(imageViewPlus);
                btnPlusVenta.getStyleClass().add("buttonLightError");
                ventaTB.setBtnSumar(btnPlusVenta);

                empList.add(ventaTB);
            }

            preparedStatement = DBUtil.getConnection().prepareStatement("{call Sp_Listar_Ventas_Mostrar_Count(?,?)}");
            preparedStatement.setInt(1, opcion);
            preparedStatement.setString(2, search);
            rsEmps = preparedStatement.executeQuery();
            Integer cantidadTotal = 0;
            if (rsEmps.next()) {
                cantidadTotal = rsEmps.getInt("Total");
            }

            object[0] = empList;
            object[1] = cantidadTotal;

            return object;
        } catch (SQLException ex) {
            return ex.getLocalizedMessage();
        } catch (Exception ex) {
            return ex.getLocalizedMessage();
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
                return ex.getLocalizedMessage();
            }
        }
    }

    public static Object ListCompletaVentasDetalle(String idVenta) {
        PreparedStatement statementVenta = null;
        PreparedStatement statementEmpleado = null;
        PreparedStatement statementVentaDetalle = null;
        try {
            DBUtil.dbConnect();
            statementVenta = DBUtil.getConnection().prepareStatement("{call Sp_Obtener_Venta_ById(?)}");
            statementVenta.setString(1, idVenta);
            ResultSet resultSetVenta = statementVenta.executeQuery();
            if (resultSetVenta.next()) {
                VentaTB ventaTB = new VentaTB();
                ventaTB.setIdVenta(idVenta);
                ventaTB.setFechaVenta(resultSetVenta.getDate("FechaVenta").toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                ventaTB.setHoraVenta(resultSetVenta.getTime("HoraVenta").toLocalTime().format(DateTimeFormatter.ofPattern("hh:mm:ss a")));
                //Cliente start
                ClienteTB clienteTB = new ClienteTB();
                clienteTB.setIdCliente(resultSetVenta.getString("IdCliente"));
                clienteTB.setIdAuxiliar(resultSetVenta.getString("IdAuxiliar"));
                clienteTB.setTipoDocumento(resultSetVenta.getInt("TipoDocumento"));
                clienteTB.setTipoDocumentoName(resultSetVenta.getString("NombreDocumento"));
                clienteTB.setNumeroDocumento(resultSetVenta.getString("NumeroDocumento"));
                clienteTB.setInformacion(resultSetVenta.getString("Informacion"));
                clienteTB.setTelefono(resultSetVenta.getString("Telefono"));
                clienteTB.setCelular(resultSetVenta.getString("Celular"));
                clienteTB.setEmail(resultSetVenta.getString("Email"));
                clienteTB.setDireccion(resultSetVenta.getString("Direccion"));
                ventaTB.setClienteTB(clienteTB);
                //Cliente end
                ventaTB.setIdMoneda(resultSetVenta.getInt("IdMoneda"));
                ventaTB.setCodigoAlterno(resultSetVenta.getString("CodigoAlterno"));
                ventaTB.setIdComprobante(resultSetVenta.getInt("IdComprobante"));
                ventaTB.setComprobanteName(resultSetVenta.getString("Comprobante"));
                ventaTB.setSerie(resultSetVenta.getString("Serie"));
                ventaTB.setNumeracion(resultSetVenta.getString("Numeracion"));
                ventaTB.setObservaciones(resultSetVenta.getString("Observaciones"));
                ventaTB.setTipo(resultSetVenta.getInt("Tipo"));
                ventaTB.setEstado(resultSetVenta.getInt("Estado"));
                ventaTB.setEfectivo(resultSetVenta.getDouble("Efectivo"));
                ventaTB.setVuelto(resultSetVenta.getDouble("Vuelto"));
                ventaTB.setTarjeta(resultSetVenta.getDouble("Tarjeta"));
                ventaTB.setSubTotal(resultSetVenta.getDouble("SubTotal"));
                ventaTB.setDescuento(resultSetVenta.getDouble("Descuento"));
                ventaTB.setSubImporte(ventaTB.getSubTotal() - ventaTB.getDescuento());
                ventaTB.setImpuesto(resultSetVenta.getDouble("Impuesto"));
                ventaTB.setTotal(resultSetVenta.getDouble("Total"));
                ventaTB.setCodigo(resultSetVenta.getString("Codigo"));

                if (ventaTB.getTipo() == 1) {
                    ventaTB.setTipoName("CONTADO");
                } else {
                    ventaTB.setTipoName("CRÉDITO");
                }

                if (ventaTB.getTipo() == 1 && ventaTB.getEstado() == 1) {
                    ventaTB.setEstadoName("COBRADO");
                } else if (ventaTB.getTipo() == 2 && ventaTB.getEstado() == 2) {
                    ventaTB.setEstadoName("POR COBRAR");
                } else if (ventaTB.getTipo() == 1 && ventaTB.getEstado() == 2) {
                    ventaTB.setEstadoName("POR LLEVAR");
                } else {
                    ventaTB.setEstadoName("ANULADO");
                }
                //moneda start
                MonedaTB monedaTB = new MonedaTB();
                monedaTB.setNombre(resultSetVenta.getString("Nombre"));
                monedaTB.setAbreviado(resultSetVenta.getString("Abreviado"));
                monedaTB.setSimbolo(resultSetVenta.getString("Simbolo"));
                ventaTB.setMonedaTB(monedaTB);
                //moneda end

                //empleado start
                statementEmpleado = DBUtil.getConnection().prepareStatement("select e.Apellidos,e.Nombres \n"
                        + "from VentaTB as v inner join EmpleadoTB as e \n"
                        + "on v.Vendedor = e.IdEmpleado\n"
                        + "where v.IdVenta = ?");
                statementEmpleado.setString(1, idVenta);
                try (ResultSet resultSetEmpleado = statementEmpleado.executeQuery()) {
                    EmpleadoTB empleadoTB = null;
                    if (resultSetEmpleado.next()) {
                        empleadoTB = new EmpleadoTB();
                        empleadoTB.setApellidos(resultSetEmpleado.getString("Apellidos"));
                        empleadoTB.setNombres(resultSetEmpleado.getString("Nombres"));
                    }
                    ventaTB.setEmpleadoTB(empleadoTB);
                }

                //empleado end
                //detalle venta start
                statementVentaDetalle = DBUtil.getConnection().prepareStatement("{call Sp_Listar_Ventas_Detalle_By_Id(?)}");
                statementVentaDetalle.setString(1, idVenta);
                ResultSet resultSetLista = statementVentaDetalle.executeQuery();
                ArrayList<SuministroTB> empList = new ArrayList();
                while (resultSetLista.next()) {
                    SuministroTB suministroTB = new SuministroTB();
                    suministroTB.setId(resultSetLista.getRow());
                    suministroTB.setIdSuministro(resultSetLista.getString("IdSuministro"));
                    suministroTB.setClave(resultSetLista.getString("Clave"));
                    suministroTB.setNombreMarca(resultSetLista.getString("NombreMarca") + (resultSetLista.getDouble("Bonificacion") <= 0 ? "" : "\nBONIFICACIÓN: " + resultSetLista.getDouble("Bonificacion")));
                    suministroTB.setInventario(resultSetLista.getBoolean("Inventario"));
                    suministroTB.setValorInventario(resultSetLista.getShort("ValorInventario"));
                    suministroTB.setUnidadCompraName(resultSetLista.getString("UnidadCompra"));
                    suministroTB.setEstadoName(resultSetLista.getString("Estado"));

                    suministroTB.setPorLlevar(resultSetLista.getDouble("PorLlevar"));
                    suministroTB.setCantidad(resultSetLista.getDouble("Cantidad"));
                    suministroTB.setBonificacion(resultSetLista.getDouble("Bonificacion"));
                    suministroTB.setCostoCompra(resultSetLista.getDouble("CostoVenta"));

                    suministroTB.setImpuestoOperacion(resultSetLista.getInt("Operacion"));
                    suministroTB.setImpuestoNombre(resultSetLista.getString("NombreImpuesto"));
                    suministroTB.setImpuestoId(resultSetLista.getInt("IdImpuesto"));
                    suministroTB.setImpuestoValor(resultSetLista.getDouble("ValorImpuesto"));

                    double valor_sin_impuesto = resultSetLista.getDouble("PrecioVenta") / ((suministroTB.getImpuestoValor() / 100.00) + 1);
                    double descuento = suministroTB.getDescuento();
                    double porcentajeRestante = valor_sin_impuesto * (descuento / 100.00);
                    double preciocalculado = valor_sin_impuesto - porcentajeRestante;

                    suministroTB.setDescuento(descuento);
                    suministroTB.setDescuentoCalculado(porcentajeRestante);
                    suministroTB.setDescuentoSumado(porcentajeRestante * suministroTB.getCantidad());

                    suministroTB.setPrecioVentaGeneralUnico(valor_sin_impuesto);
                    suministroTB.setPrecioVentaGeneralReal(preciocalculado);

                    double impuesto = Tools.calculateTax(suministroTB.getImpuestoValor(), suministroTB.getPrecioVentaGeneralReal());
                    suministroTB.setImpuestoSumado(suministroTB.getCantidad() * impuesto);
                    suministroTB.setPrecioVentaGeneral(suministroTB.getPrecioVentaGeneralReal() + impuesto);
                    suministroTB.setPrecioVentaGeneralAuxiliar(suministroTB.getPrecioVentaGeneral());

                    suministroTB.setImporteBruto(suministroTB.getPrecioVentaGeneralUnico() * suministroTB.getCantidad());
                    suministroTB.setSubImporteNeto(suministroTB.getCantidad() * suministroTB.getPrecioVentaGeneralReal());
                    suministroTB.setImporteNeto(suministroTB.getCantidad() * suministroTB.getPrecioVentaGeneral());

                    suministroTB.setInventario(resultSetLista.getBoolean("Inventario"));
                    suministroTB.setUnidadVenta(resultSetLista.getInt("UnidadVenta"));
                    suministroTB.setValorInventario(resultSetLista.getShort("ValorInventario"));

                    Button button = new Button("X");
                    button.getStyleClass().add("buttonDark");
                    suministroTB.setRemover(button);

                    empList.add(suministroTB);
                }
                ventaTB.setSuministroTBs(empList);
                return ventaTB;
            } else {
                throw new Exception("No se puedo obtener el detalle de la venta, intente nuvemante por favor.");
            }
        } catch (SQLException ex) {
            return ex.getLocalizedMessage();
        } catch (Exception ex) {
            return ex.getLocalizedMessage();
        } finally {
            try {
                if (statementVenta != null) {
                    statementVenta.close();
                }
                if (statementEmpleado != null) {
                    statementEmpleado.close();
                }
                if (statementVentaDetalle != null) {
                    statementVentaDetalle.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException ex) {
                return ex.getLocalizedMessage();
            }
        }

    }

    public static String CancelTheSale(String idVenta, ObservableList<SuministroTB> arrList) {

        String result = null;
        PreparedStatement statementValidar = null;
        PreparedStatement statementVenta = null;
        PreparedStatement statementSuministro = null;
        PreparedStatement statementKardex = null;
        PreparedStatement statementMovimientoCaja = null;

        try {
            DBUtil.dbConnect();
            DBUtil.getConnection().setAutoCommit(false);

            statementValidar = DBUtil.getConnection().prepareStatement("SELECT IdCaja FROM CajaTB WHERE IdUsuario = ? AND Estado = 1");
            statementValidar.setString(1, Session.USER_ID);
            if (statementValidar.executeQuery().next()) {

                statementValidar = DBUtil.getConnection().prepareStatement("SELECT * FROM VentaTB WHERE IdVenta = ? and Estado = ?");
                statementValidar.setString(1, idVenta);
                statementValidar.setInt(2, 3);
                if (statementValidar.executeQuery().next()) {
                    DBUtil.getConnection().rollback();
                    return "scrambled";
                } else {

                    statementValidar = DBUtil.getConnection().prepareStatement("SELECT * FROM VentaCreditoTB WHERE IdVenta = ?");
                    statementValidar.setString(1, idVenta);
                    if (statementValidar.executeQuery().next()) {
                        DBUtil.getConnection().rollback();
                        return "ventacredito";
                    } else {
                        statementValidar = DBUtil.getConnection().prepareStatement("SELECT * FROM VentaTB WHERE IdVenta = ? and FechaVenta = CAST(GETDATE() AS DATE)");
                        statementValidar.setString(1, idVenta);
                        ResultSet resultSet = statementValidar.executeQuery();
                        if (resultSet.next()) {

                            statementVenta = DBUtil.getConnection().prepareStatement("UPDATE VentaTB "
                                    + "SET Estado = ? "
                                    + "WHERE IdVenta = ?");

                            statementVenta.setInt(1, 3);
                            statementVenta.setString(2, idVenta);
                            statementVenta.addBatch();
                            statementVenta.executeBatch();

                            statementSuministro = DBUtil.getConnection().prepareStatement("UPDATE SuministroTB "
                                    + "SET Cantidad = Cantidad + ? "
                                    + "WHERE IdSuministro = ?");

                            statementKardex = DBUtil.getConnection().prepareStatement("INSERT INTO "
                                    + "KardexSuministroTB("
                                    + "IdSuministro,"
                                    + "Fecha,"
                                    + "Hora,"
                                    + "Tipo,"
                                    + "Movimiento,"
                                    + "Detalle,"
                                    + "Cantidad, "
                                    + "Costo, "
                                    + "Total) "
                                    + "VALUES(?,?,?,?,?,?,?,?,?)");

                            if (resultSet.getInt("Tipo") == 1 && resultSet.getInt("Estado") == 2) {

                            } else {
                                for (SuministroTB stb : arrList) {
                                    if (stb.isInventario() && stb.getValorInventario() == 1) {
                                        statementSuministro.setDouble(1, stb.getCantidad() + stb.getBonificacion());
                                        statementSuministro.setString(2, stb.getIdSuministro());
                                        statementSuministro.addBatch();
                                    } else if (stb.isInventario() && stb.getValorInventario() == 2) {
                                        statementSuministro.setDouble(1, stb.getCantidad());
                                        statementSuministro.setString(2, stb.getIdSuministro());
                                        statementSuministro.addBatch();
                                    } else if (stb.isInventario() && stb.getValorInventario() == 3) {
                                        statementSuministro.setDouble(1, stb.getCantidad());
                                        statementSuministro.setString(2, stb.getIdSuministro());
                                        statementSuministro.addBatch();
                                    }

                                    double cantidadTotal = stb.getValorInventario() == 1
                                            ? stb.getCantidad() + stb.getBonificacion()
                                            : stb.getValorInventario() == 2
                                            ? stb.getCantidad()
                                            : stb.getCantidad();

                                    statementKardex.setString(1, stb.getIdSuministro());
                                    statementKardex.setString(2, Tools.getDate());
                                    statementKardex.setString(3, Tools.getHour());
                                    statementKardex.setShort(4, (short) 1);
                                    statementKardex.setInt(5, 2);
                                    statementKardex.setString(6, "DEVOLUCIÓN DE PRODUCTO");
                                    statementKardex.setDouble(7, cantidadTotal);
                                    statementKardex.setDouble(8, stb.getCostoCompra());
                                    statementKardex.setDouble(9, cantidadTotal * stb.getCostoCompra());
                                    statementKardex.addBatch();
                                }
                            }

                            statementMovimientoCaja = DBUtil.getConnection().prepareStatement("DELETE FROM MovimientoCajaTB WHERE IdProcedencia = ?");
                            statementMovimientoCaja.setString(1, idVenta);
                            statementMovimientoCaja.addBatch();

                            statementVenta.executeBatch();
                            statementSuministro.executeBatch();
                            statementKardex.executeBatch();
                            statementMovimientoCaja.executeBatch();

                            DBUtil.getConnection().commit();
                            result = "updated";

                        } else {
                            DBUtil.getConnection().rollback();
                            return "nodate";
                        }
                    }
                }
            } else {
                DBUtil.getConnection().rollback();
                result = "nocaja";
            }
        } catch (SQLException ex) {
            try {
                DBUtil.getConnection().rollback();
            } catch (SQLException e) {
            }
            result = ex.getLocalizedMessage();
        } finally {
            try {
                if (statementValidar != null) {
                    statementValidar.close();
                }
                if (statementVenta != null) {
                    statementVenta.close();
                }
                if (statementSuministro != null) {
                    statementSuministro.close();
                }
                if (statementKardex != null) {
                    statementKardex.close();
                }
                if (statementMovimientoCaja != null) {
                    statementMovimientoCaja.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException e) {

            }
        }
        return result;
    }

    public static String movimientoCaja(MovimientoCajaTB movimientoCajaTB) {

        String result = null;
        PreparedStatement statementMovimientoCaja = null;

        try {
            DBUtil.dbConnect();
            DBUtil.getConnection().setAutoCommit(false);

            statementMovimientoCaja = DBUtil.getConnection().prepareStatement("INSERT INTO MovimientoCajaTB(IdCaja,FechaMovimiento,HoraMovimiento,Comentario,TipoMovimiento,Monto)VALUES(?,?,?,?,?,?)");
            statementMovimientoCaja.setString(1, Session.CAJA_ID);
            statementMovimientoCaja.setString(2, movimientoCajaTB.getFechaMovimiento());
            statementMovimientoCaja.setString(3, movimientoCajaTB.getHoraMovimiento());
            statementMovimientoCaja.setString(4, movimientoCajaTB.getComentario());
            statementMovimientoCaja.setShort(5, movimientoCajaTB.getTipoMovimiento());
            statementMovimientoCaja.setDouble(6, movimientoCajaTB.getMonto());
            statementMovimientoCaja.addBatch();

            statementMovimientoCaja.executeBatch();

            DBUtil.getConnection().commit();
            result = "updated";

        } catch (SQLException ex) {
            try {
                DBUtil.getConnection().rollback();
            } catch (SQLException e) {
            }
            result = ex.getLocalizedMessage();
        } finally {
            try {
                if (statementMovimientoCaja != null) {
                    statementMovimientoCaja.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException e) {

            }
        }
        return result;
    }

    public static ArrayList<VentaTB> GetReporteGenetalVentas(String fechaInicial, String fechaFinal, int tipoDocumento, String cliente, String vendedor) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ArrayList<VentaTB> arrayList = new ArrayList<>();
        try {
            DBUtil.dbConnect();
            preparedStatement = DBUtil.getConnection().prepareCall("{call Sp_Reporte_General_Ventas(?,?,?,?,?)}");
            preparedStatement.setString(1, fechaInicial);
            preparedStatement.setString(2, fechaFinal);
            preparedStatement.setInt(3, tipoDocumento);
            preparedStatement.setString(4, cliente);
            preparedStatement.setString(5, vendedor);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                VentaTB ventaTB = new VentaTB();
                ventaTB.setComprobanteName(resultSet.getString("Nombre"));
                ventaTB.setFechaVenta(resultSet.getDate("FechaVenta").toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                ventaTB.setIdCliente(resultSet.getString("NumeroDocumento") + "\n" + resultSet.getString("Cliente"));
                ventaTB.setSerie(resultSet.getString("Serie") + "-" + resultSet.getString("Numeracion"));
                ventaTB.setTipoName(resultSet.getString("Tipo"));
                ventaTB.setEstado(resultSet.getInt("Estado"));
                ventaTB.setEstadoName(resultSet.getString("EstadoName"));
                ventaTB.setMonedaName(resultSet.getString("Simbolo"));
                ventaTB.setTotal(resultSet.getDouble("Total"));
                ventaTB.setTotalFormat(Tools.roundingValue(
                        resultSet.getInt("Estado") == 3
                        ? -resultSet.getDouble("Total") : resultSet.getDouble("Total"),
                        2));
                arrayList.add(ventaTB);
            }
        } catch (SQLException ex) {
            System.out.println("Venta ADO GetReporteGenetalVentas():" + ex.getLocalizedMessage());
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (resultSet != null) {
                    resultSet.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException ex) {

            }
        }
        return arrayList;
    }

    public static ArrayList<VentaTB> GetReporteSumaVentaPorDia(String fechaInicial, String fechaFinal, boolean tipoOrden, boolean ordern) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ArrayList<VentaTB> arrayList = new ArrayList<>();
        try {
            DBUtil.dbConnect();
            preparedStatement = DBUtil.getConnection().prepareCall("{call Sp_Reporte_Ventas_Sumadas_Por_Fecha(?,?,?,?)}");
            preparedStatement.setString(1, fechaInicial);
            preparedStatement.setString(2, fechaFinal);
            preparedStatement.setBoolean(3, tipoOrden);
            preparedStatement.setBoolean(4, ordern);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                VentaTB ventaTB = new VentaTB();
                ventaTB.setFechaVenta(resultSet.getDate("FechaVenta").toLocalDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)));
                ventaTB.setEstado(resultSet.getInt("Estado"));
                ventaTB.setMonedaName(resultSet.getString("Simbolo"));
                ventaTB.setTotal(ventaTB.getEstado() == 3
                        ? 0 : resultSet.getDouble("Total"));
                arrayList.add(ventaTB);

            }
        } catch (SQLException ex) {
            System.out.println("Venta ADO:" + ex.getLocalizedMessage());
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (resultSet != null) {
                    resultSet.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException ex) {

            }
        }
        return arrayList;
    }

    public static Object ListarVentasCredito(short opcion, String buscar, String fechaInicial, String fechaFinal, int posicionPagina, int filasPorPagina) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            DBUtil.dbConnect();
            Object[] objects = new Object[2];
            preparedStatement = DBUtil.getConnection().prepareCall("{call Sp_Listar_Ventas_Credito(?,?,?,?,?,?)}");
            preparedStatement.setShort(1, opcion);
            preparedStatement.setString(2, buscar);
            preparedStatement.setString(3, fechaInicial);
            preparedStatement.setString(4, fechaFinal);
            preparedStatement.setInt(5, posicionPagina);
            preparedStatement.setInt(6, filasPorPagina);
            resultSet = preparedStatement.executeQuery();
            ObservableList<VentaTB> arrayList = FXCollections.observableArrayList();
            while (resultSet.next()) {
                VentaTB ventaTB = new VentaTB();
                ventaTB.setId(resultSet.getRow() + posicionPagina);
                ventaTB.setIdVenta(resultSet.getString("IdVenta"));
                ventaTB.setSerie(resultSet.getString("Serie"));
                ventaTB.setNumeracion(resultSet.getString("Numeracion"));
                ventaTB.setFechaVenta(resultSet.getDate("FechaVenta").toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                ventaTB.setHoraVenta(resultSet.getTime("HoraVenta").toLocalTime().format(DateTimeFormatter.ofPattern("hh:mm:ss a")));
                ventaTB.setClienteTB(new ClienteTB(resultSet.getString("NumeroDocumento"), resultSet.getString("Informacion")));
                ventaTB.setClienteName(resultSet.getString("Informacion"));
                ventaTB.setEstado(resultSet.getInt("Estado"));
                ventaTB.setEstadoName(ventaTB.getEstado() == 3 ? "CANCELADO" : ventaTB.getEstado() == 2 ? "POR PAGAR" : "PAGADO");
                ventaTB.setMonedaName(resultSet.getString("Simbolo"));
                ventaTB.setMontoTotal(resultSet.getDouble("MontoTotal"));
                ventaTB.setMontoCobrado(resultSet.getDouble("MontoCobrado"));
                ventaTB.setMontoRestante(ventaTB.getMontoTotal() - ventaTB.getMontoCobrado());

                Label label = new Label(ventaTB.getEstado() == 3 ? "ANULADO" : ventaTB.getEstado() == 2 ? "POR COBRAR" : "COBRADO");
                label.getStyleClass().add(ventaTB.getEstado() == 2 ? "label-medio" : ventaTB.getEstado() == 3 ? "label-proceso" : "label-ultimo");
                ventaTB.setEstadoLabel(label);

                Button btnVisualizar = new Button();
                ImageView imageViewVisualizar = new ImageView(new Image("/view/image/search_caja.png"));
                imageViewVisualizar.setFitWidth(24);
                imageViewVisualizar.setFitHeight(24);
                btnVisualizar.setGraphic(imageViewVisualizar);
                btnVisualizar.getStyleClass().add("buttonLightWarning");
                HBox hBox = new HBox();
                hBox.setAlignment(Pos.CENTER);
                hBox.setStyle(";-fx-spacing:0.8333333333333334em;");
                hBox.getChildren().add(btnVisualizar);
                ventaTB.setHbOpciones(hBox);

                arrayList.add(ventaTB);
            }

            objects[0] = arrayList;

            preparedStatement = DBUtil.getConnection().prepareCall("{call Sp_Listar_Ventas_Credito_Count(?,?,?,?)}");
            preparedStatement.setShort(1, opcion);
            preparedStatement.setString(2, buscar);
            preparedStatement.setString(3, fechaInicial);
            preparedStatement.setString(4, fechaFinal);
            resultSet = preparedStatement.executeQuery();
            Integer integer = 0;
            if (resultSet.next()) {
                integer = resultSet.getInt("Total");
            }
            objects[1] = integer;

            return objects;
        } catch (SQLException ex) {
            return ex.getLocalizedMessage();
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (resultSet != null) {
                    resultSet.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException ex) {

            }
        }

    }

    public static VentaTB ListarVentasDetalleCredito(String idVenta) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        VentaTB ventaTB = null;
        try {
            DBUtil.dbConnect();
            preparedStatement = DBUtil.getConnection().prepareCall("{call Sp_Obtener_Venta_ById_For_Credito(?)}");
            preparedStatement.setString(1, idVenta);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                ventaTB = new VentaTB();
                ventaTB.setIdVenta(idVenta);
                ventaTB.setClienteTB(new ClienteTB(resultSet.getString("NumeroDocumento"), resultSet.getString("Informacion"), resultSet.getString("Celular"), resultSet.getString("Email"), resultSet.getString("Direccion")));
                ventaTB.setSerie(resultSet.getString("Serie"));
                ventaTB.setNumeracion(resultSet.getString("Numeracion"));
                ventaTB.setEstado(resultSet.getInt("Estado"));
                ventaTB.setEstadoName(resultSet.getString("EstadoName"));
                ventaTB.setMonedaName(resultSet.getString("Simbolo"));
                ventaTB.setMontoTotal(resultSet.getDouble("MontoTotal"));
                ventaTB.setMontoCobrado(resultSet.getDouble("MontoCobrado"));
                ventaTB.setMontoRestante(ventaTB.getMontoTotal() - ventaTB.getMontoCobrado());

                preparedStatement = DBUtil.getConnection().prepareCall("{call Sp_Listar_Detalle_Venta_Credito(?)}");
                preparedStatement.setString(1, idVenta);
                resultSet = preparedStatement.executeQuery();
                ArrayList<VentaCreditoTB> ventaCreditoTBs = new ArrayList<>();
                while (resultSet.next()) {
                    VentaCreditoTB ventaCreditoTB = new VentaCreditoTB();
                    ventaCreditoTB.setId(resultSet.getRow());
                    ventaCreditoTB.setIdVentaCredito(resultSet.getString("IdVentaCredito"));
                    ventaCreditoTB.setFechaPago(resultSet.getDate("FechaPago").toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    ventaCreditoTB.setHoraPago(resultSet.getTime("HoraPago").toLocalTime().format(DateTimeFormatter.ofPattern("hh:mm:ss a")));
                    ventaCreditoTB.setEstado(resultSet.getShort("Estado"));
                    ventaCreditoTB.setObservacion(resultSet.getString("Observacion"));
                    ventaCreditoTB.setEmpleadoTB(new EmpleadoTB(resultSet.getString("NumeroDocumento"), resultSet.getString("Apellidos"), resultSet.getString("Nombres")));
                    ventaCreditoTB.setMonto(resultSet.getDouble("Monto"));

                    Button btnImprimir = new Button();
                    ImageView imageViewVisualizar = new ImageView(new Image("/view/image/print.png"));
                    imageViewVisualizar.setFitWidth(24);
                    imageViewVisualizar.setFitHeight(24);
                    btnImprimir.setGraphic(imageViewVisualizar);
                    btnImprimir.getStyleClass().add("buttonLightSuccess");
                    ventaCreditoTB.setBtnImprimir(btnImprimir);
                    ventaCreditoTBs.add(ventaCreditoTB);
                }
                ventaTB.setVentaCreditoTBs(ventaCreditoTBs);
            }
        } catch (SQLException ex) {
            System.out.println("ListarVentasDetalleCredito:" + ex.getLocalizedMessage());
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (resultSet != null) {
                    resultSet.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException ex) {

            }
        }
        return ventaTB;
    }

    public static ModeloObject RegistrarAbono(VentaCreditoTB ventaCreditoTB, BancoHistorialTB bancoHistorialTB, MovimientoCajaTB movimientoCajaTB) {
        ModeloObject result = new ModeloObject();
        DBUtil.dbConnect();
        if (getConnection() != null) {
            PreparedStatement statementValidate = null;
            PreparedStatement statementVenta = null;
            PreparedStatement statementAbono = null;
            PreparedStatement preparedBanco = null;
            PreparedStatement preparedBancoHistorial = null;
            PreparedStatement movimiento_caja = null;
            CallableStatement statementCodigo = null;
            try {

                DBUtil.getConnection().setAutoCommit(false);

                statementValidate = DBUtil.getConnection().prepareStatement("SELECT Total FROM VentaTB WHERE IdVenta = ?");
                statementValidate.setString(1, ventaCreditoTB.getIdVenta());
                ResultSet resultSet = statementValidate.executeQuery();
                if (resultSet.next()) {
                    double total = Double.parseDouble(Tools.roundingValue(resultSet.getDouble("Total"), 2));

                    statementCodigo = DBUtil.getConnection().prepareCall("{? = call Fc_Venta_Credito_Codigo_Alfanumerico()}");
                    statementCodigo.registerOutParameter(1, java.sql.Types.VARCHAR);
                    statementCodigo.execute();
                    String idVentaCredito = statementCodigo.getString(1);

                    statementAbono = DBUtil.getConnection().prepareStatement("INSERT INTO VentaCreditoTB(IdVenta,IdVentaCredito,Monto,FechaPago,HoraPago,Estado,IdUsuario,Observacion)VALUES(?,?,?,?,?,?,?,?)");
                    statementAbono.setString(1, ventaCreditoTB.getIdVenta());
                    statementAbono.setString(2, idVentaCredito);
                    statementAbono.setDouble(3, ventaCreditoTB.getMonto());
                    statementAbono.setString(4, ventaCreditoTB.getFechaPago());
                    statementAbono.setString(5, ventaCreditoTB.getHoraPago());
                    statementAbono.setShort(6, ventaCreditoTB.getEstado());
                    statementAbono.setString(7, ventaCreditoTB.getIdUsuario());
                    statementAbono.setString(8, ventaCreditoTB.getObservacion());
                    statementAbono.addBatch();

                    preparedBanco = getConnection().prepareStatement("UPDATE Banco SET SaldoInicial = SaldoInicial + ? WHERE IdBanco = ?");
                    preparedBancoHistorial = getConnection().prepareStatement("INSERT INTO BancoHistorialTB(IdBanco,IdProcedencia,Descripcion,Fecha,Hora,Entrada,Salida)VALUES(?,?,?,?,?,?,?)");
                    if (bancoHistorialTB != null) {
                        preparedBanco.setDouble(1, bancoHistorialTB.getEntrada());
                        preparedBanco.setString(2, bancoHistorialTB.getIdBanco());
                        preparedBanco.addBatch();

                        preparedBancoHistorial.setString(1, bancoHistorialTB.getIdBanco());
                        preparedBancoHistorial.setString(2, "");
                        preparedBancoHistorial.setString(3, bancoHistorialTB.getDescripcion());
                        preparedBancoHistorial.setString(4, bancoHistorialTB.getFecha());
                        preparedBancoHistorial.setString(5, bancoHistorialTB.getHora());
                        preparedBancoHistorial.setDouble(6, bancoHistorialTB.getEntrada());
                        preparedBancoHistorial.setDouble(7, bancoHistorialTB.getSalida());
                        preparedBancoHistorial.addBatch();
                    }

                    movimiento_caja = DBUtil.getConnection().prepareStatement("INSERT INTO MovimientoCajaTB(IdCaja,FechaMovimiento,HoraMovimiento,Comentario,TipoMovimiento,Monto,IdProcedencia)VALUES(?,?,?,?,?,?,?)");
                    if (movimientoCajaTB != null) {
                        movimiento_caja.setString(1, movimientoCajaTB.getIdCaja());
                        movimiento_caja.setString(2, movimientoCajaTB.getFechaMovimiento());
                        movimiento_caja.setString(3, movimientoCajaTB.getHoraMovimiento());
                        movimiento_caja.setString(4, movimientoCajaTB.getComentario());
                        movimiento_caja.setShort(5, movimientoCajaTB.getTipoMovimiento());
                        movimiento_caja.setDouble(6, movimientoCajaTB.getMonto());
                        movimiento_caja.setString(7, idVentaCredito);
                        movimiento_caja.addBatch();
                    }

                    statementValidate = DBUtil.getConnection().prepareStatement("SELECT Monto FROM VentaCreditoTB WHERE IdVenta = ?");
                    statementValidate.setString(1, ventaCreditoTB.getIdVenta());
                    resultSet = statementValidate.executeQuery();

                    double montoTotal = 0;
                    while (resultSet.next()) {
                        montoTotal += resultSet.getDouble("Monto");
                    }

                    statementVenta = DBUtil.getConnection().prepareStatement("UPDATE VentaTB SET Estado = 1 WHERE IdVenta = ?");
                    if ((montoTotal + ventaCreditoTB.getMonto()) >= total) {
                        statementVenta.setString(1, ventaCreditoTB.getIdVenta());
                        statementVenta.addBatch();
                    }

                    statementAbono.executeBatch();
                    preparedBanco.executeBatch();
                    preparedBancoHistorial.executeBatch();
                    movimiento_caja.executeBatch();
                    statementVenta.executeBatch();
                    DBUtil.getConnection().commit();
                    result.setId((short) 1);
                    result.setIdResult(idVentaCredito);
                    result.setMessage("Se completo correctamente el proceso.");
                    result.setState("inserted");
                } else {
                    DBUtil.getConnection().rollback();
                    result.setId((short) 2);
                    result.setMessage("Problemas al encontrar le venta con el id indicado " + ventaCreditoTB.getIdVenta());
                    result.setState("error");

                }
            } catch (SQLException ex) {
                try {
                    DBUtil.getConnection().rollback();
                } catch (SQLException e) {

                }
                result.setId((short) 2);
                result.setMessage(ex.getLocalizedMessage());
                result.setState("error");
            } finally {
                try {
                    if (statementAbono != null) {
                        statementAbono.close();
                    }
                    if (preparedBanco != null) {
                        preparedBanco.close();
                    }
                    if (preparedBancoHistorial != null) {
                        preparedBancoHistorial.close();
                    }
                    if (movimiento_caja != null) {
                        movimiento_caja.close();
                    }
                    if (statementCodigo != null) {
                        statementCodigo.close();
                    }
                    if (statementValidate != null) {
                        statementValidate.close();
                    }
                    if (statementVenta != null) {
                        statementVenta.close();
                    }
                    DBUtil.dbDisconnect();
                } catch (SQLException ex) {

                }
            }
        } else {
            result.setId((short) 3);
            result.setMessage("No se pudo conectar el servidor, intente nuevamente.");
            result.setState("conexion");

        }
        return result;
    }

    public static VentaCreditoTB ImprimirVetanCreditoById(String idVenta, String idVentaCredito) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        VentaCreditoTB ventaCreditoTB = null;
        VentaTB ventaTB = null;
        try {
            DBUtil.dbConnect();
            preparedStatement = DBUtil.getConnection().prepareCall("{CALL Sp_Obtener_VentaCredito_By_Id(?)}");
            preparedStatement.setString(1, idVentaCredito);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                ventaCreditoTB = new VentaCreditoTB();
                ventaCreditoTB.setIdVentaCredito(resultSet.getString("IdVentaCredito"));
                ventaCreditoTB.setMonto(resultSet.getDouble("Monto"));
                ventaCreditoTB.setFechaPago(resultSet.getDate("FechaPago").toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                ventaCreditoTB.setHoraPago(resultSet.getTime("HoraPago").toLocalTime().format(DateTimeFormatter.ofPattern("hh:mm:ss a")));
                ventaCreditoTB.setObservacion(resultSet.getString("Observacion"));

                preparedStatement = DBUtil.getConnection().prepareCall("{call Sp_Obtener_Venta_ById_For_Credito(?)}");
                preparedStatement.setString(1, idVenta);
                resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    ventaTB = new VentaTB();
                    ventaTB.setIdVenta(idVenta);
                    ventaTB.setClienteTB(new ClienteTB(resultSet.getString("NumeroDocumento"), resultSet.getString("Informacion"), resultSet.getString("Celular"), resultSet.getString("Email"), resultSet.getString("Direccion")));
                    ventaTB.setSerie(resultSet.getString("Serie"));
                    ventaTB.setNumeracion(resultSet.getString("Numeracion"));
                    ventaTB.setEstado(resultSet.getInt("Estado"));
                    ventaTB.setEstadoName(resultSet.getString("EstadoName"));
                    ventaTB.setMonedaName(resultSet.getString("Simbolo"));
                    ventaTB.setMontoTotal(resultSet.getDouble("MontoTotal"));
                    ventaTB.setMontoCobrado(resultSet.getDouble("MontoCobrado"));
                    ventaTB.setMontoRestante(ventaTB.getMontoTotal() - ventaTB.getMontoRestante());
                }
                ventaCreditoTB.setVentaTB(ventaTB);
            }

        } catch (SQLException ex) {
            Tools.println("ImprimirVetanCreditoById: " + ex.getLocalizedMessage());
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (resultSet != null) {
                    resultSet.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException ex) {

            }
        }
        return ventaCreditoTB;
    }

    public static String UpdateProductoParaLlevar(String idVenta, String idSuministro, String comprobante, double cantidad, double costo, String observacion, boolean forma) {
        PreparedStatement statementVenta = null;
        PreparedStatement statementHistorial = null;
        PreparedStatement statementSuministro = null;
        PreparedStatement statementActualizar = null;
        PreparedStatement statementKardex = null;
        try {
            DBUtil.dbConnect();
            DBUtil.getConnection().setAutoCommit(false);

            statementSuministro = DBUtil.getConnection().prepareStatement("SELECT * FROM DetalleVentaTB WHERE IdVenta = ? AND IdArticulo = ?");
            statementSuministro.setString(1, idVenta);
            statementSuministro.setString(2, idSuministro);
            ResultSet resultSet = statementSuministro.executeQuery();
            if (resultSet.next()) {
                double cantidadTotal = resultSet.getDouble("Cantidad") + resultSet.getDouble("Bonificacion");
                if (forma) {
                    statementHistorial = DBUtil.getConnection().prepareStatement("SELECT * FROM HistorialSuministroLlevar WHERE IdVenta = ? AND IdSuministro = ?");
                    statementHistorial.setString(1, idVenta);
                    statementHistorial.setString(2, idSuministro);
                    if (statementHistorial.executeQuery().next()) {
                        DBUtil.getConnection().rollback();
                        return "historial";
                    } else {

                        statementSuministro = DBUtil.getConnection().prepareStatement("UPDATE DetalleVentaTB SET PorLlevar = Cantidad+Bonificacion, Estado = 'C' WHERE IdVenta = ? AND IdArticulo = ?");
                        statementSuministro.setString(1, idVenta);
                        statementSuministro.setString(2, idSuministro);
                        statementSuministro.addBatch();

                        statementActualizar = DBUtil.getConnection().prepareStatement("UPDATE SuministroTB SET Cantidad = Cantidad - ? WHERE IdSuministro = ?");
                        statementActualizar.setDouble(1, cantidadTotal);
                        statementActualizar.setString(2, idSuministro);
                        statementActualizar.addBatch();

                        statementKardex = DBUtil.getConnection().prepareStatement("INSERT INTO "
                                + "KardexSuministroTB("
                                + "IdSuministro,"
                                + "Fecha,"
                                + "Hora,"
                                + "Tipo,"
                                + "Movimiento,"
                                + "Detalle,"
                                + "Cantidad,"
                                + "Costo, "
                                + "Total) "
                                + "VALUES(?,?,?,?,?,?,?,?,?)");
                        statementKardex.setString(1, idSuministro);
                        statementKardex.setString(2, Tools.getDate());
                        statementKardex.setString(3, Tools.getHour());
                        statementKardex.setShort(4, (short) 2);
                        statementKardex.setInt(5, 1);
                        statementKardex.setString(6, "SALIDA DEL PRODUCTO DE LA VENTA " + comprobante);
                        statementKardex.setDouble(7, cantidadTotal);
                        statementKardex.setDouble(8, costo);
                        statementKardex.setDouble(9, cantidadTotal * costo);
                        statementKardex.addBatch();

                        statementHistorial = DBUtil.getConnection().prepareStatement("INSERT INTO HistorialSuministroLlevar(IdVenta,IdSuministro, Fecha, Hora, Cantidad, Observacion)VALUES(?,?,?,?,?,?)");
                        statementHistorial.setString(1, idVenta);
                        statementHistorial.setString(2, idSuministro);
                        statementHistorial.setString(3, Tools.getDate());
                        statementHistorial.setString(4, Tools.getHour());
                        statementHistorial.setDouble(5, cantidadTotal);
                        statementHistorial.setString(6, observacion);
                        statementHistorial.addBatch();

                        statementVenta = DBUtil.getConnection().prepareStatement("UPDATE VentaTB SET Estado = 1 WHERE IdVenta = ?");
                        statementVenta.setString(1, idVenta);
                        statementVenta.addBatch();

                        statementSuministro.executeBatch();
                        statementActualizar.executeBatch();
                        statementKardex.executeBatch();
                        statementHistorial.executeBatch();
                        statementVenta.executeBatch();
                        DBUtil.getConnection().commit();
                        return "update";
                    }
                } else {

                    statementHistorial = DBUtil.getConnection().prepareStatement("SELECT Cantidad FROM HistorialSuministroLlevar WHERE IdVenta = ? AND IdSuministro = ?");
                    statementHistorial.setString(1, idVenta);
                    statementHistorial.setString(2, idSuministro);
                    resultSet = statementHistorial.executeQuery();
                    double cantidadActual = 0;
                    while (resultSet.next()) {
                        cantidadActual += resultSet.getDouble("Cantidad");
                    }

                    statementVenta = DBUtil.getConnection().prepareStatement("UPDATE VentaTB SET Estado = 1 WHERE IdVenta = ?");
                    if ((cantidad + cantidadActual) >= cantidadTotal) {
                        statementSuministro = DBUtil.getConnection().prepareStatement("UPDATE DetalleVentaTB SET PorLlevar = PorLlevar + ?, Estado = 'C' WHERE IdVenta = ? AND IdArticulo = ?");
                        statementSuministro.setDouble(1, cantidad);
                        statementSuministro.setString(2, idVenta);
                        statementSuministro.setString(3, idSuministro);
                        statementSuministro.addBatch();

                        statementVenta.setString(1, idVenta);
                        statementVenta.addBatch();
                    } else {
                        statementSuministro = DBUtil.getConnection().prepareStatement("UPDATE DetalleVentaTB SET PorLlevar = PorLlevar + ? WHERE IdVenta = ? AND IdArticulo = ?");
                        statementSuministro.setDouble(1, cantidad);
                        statementSuministro.setString(2, idVenta);
                        statementSuministro.setString(3, idSuministro);
                        statementSuministro.addBatch();
                    }

                    statementActualizar = DBUtil.getConnection().prepareStatement("UPDATE SuministroTB SET Cantidad = Cantidad - ? WHERE IdSuministro = ?");
                    statementActualizar.setDouble(1, cantidad);
                    statementActualizar.setString(2, idSuministro);
                    statementActualizar.addBatch();

                    statementKardex = DBUtil.getConnection().prepareStatement("INSERT INTO "
                            + "KardexSuministroTB("
                            + "IdSuministro,"
                            + "Fecha,"
                            + "Hora,"
                            + "Tipo,"
                            + "Movimiento,"
                            + "Detalle,"
                            + "Cantidad,"
                            + "Costo, "
                            + "Total) "
                            + "VALUES(?,?,?,?,?,?,?,?,?)");
                    statementKardex.setString(1, idSuministro);
                    statementKardex.setString(2, Tools.getDate());
                    statementKardex.setString(3, Tools.getHour());
                    statementKardex.setShort(4, (short) 2);
                    statementKardex.setInt(5, 1);
                    statementKardex.setString(6, "SALIDA DEL PRODUCTO DE LA VENTA " + comprobante);
                    statementKardex.setDouble(7, cantidad);
                    statementKardex.setDouble(8, costo);
                    statementKardex.setDouble(9, cantidad * costo);
                    statementKardex.addBatch();

                    statementHistorial = DBUtil.getConnection().prepareStatement("INSERT INTO HistorialSuministroLlevar(IdVenta,IdSuministro, Fecha, Hora, Cantidad, Observacion)VALUES(?,?,?,?,?,?)");
                    statementHistorial.setString(1, idVenta);
                    statementHistorial.setString(2, idSuministro);
                    statementHistorial.setString(3, Tools.getDate());
                    statementHistorial.setString(4, Tools.getHour());
                    statementHistorial.setDouble(5, cantidad);
                    statementHistorial.setString(6, observacion);
                    statementHistorial.addBatch();

                    statementSuministro.executeBatch();
                    statementActualizar.executeBatch();
                    statementKardex.executeBatch();
                    statementHistorial.executeBatch();
                    statementVenta.executeBatch();
                    DBUtil.getConnection().commit();
                    return "update";
                }

            } else {
                DBUtil.getConnection().rollback();
                return "noproduct";
            }

        } catch (SQLException ex) {
            try {
                DBUtil.getConnection().rollback();
            } catch (SQLException e) {
            }
            return ex.getLocalizedMessage();
        } finally {
            try {
                if (statementSuministro != null) {
                    statementSuministro.close();
                }
                if (statementActualizar != null) {
                    statementActualizar.close();
                }
                if (statementVenta != null) {
                    statementVenta.close();
                }
                if (statementHistorial != null) {
                    statementHistorial.close();
                }
                if (statementKardex != null) {
                    statementKardex.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException ex) {

            }
        }
    }

    public static Object ListarHistorialSuministroLlevar(String idVenta, String idSuministro) {
        PreparedStatement statementVenta = null;
        try {
            DBUtil.dbConnect();
            Object[] objects = new Object[3];
            statementVenta = DBUtil.getConnection().prepareStatement("SELECT v.Serie,v.Numeracion,v.FechaVenta,v.HoraVenta,c.NumeroDocumento,c.Informacion,c.Telefono,c.Celular,c.Email,c.Direccion FROM VentaTB AS v INNER JOIN ClienteTB AS c ON c.IdCliente = v.Cliente WHERE IdVenta = ?");
            statementVenta.setString(1, idVenta);
            VentaTB ventaTB = null;
            try (ResultSet resultSet = statementVenta.executeQuery();) {
                if (resultSet.next()) {
                    ventaTB = new VentaTB();
                    ventaTB.setSerie(resultSet.getString("Serie"));
                    ventaTB.setNumeracion(resultSet.getString("Numeracion"));
                    ventaTB.setFechaVenta(resultSet.getDate("FechaVenta").toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    ventaTB.setHoraVenta(resultSet.getTime("HoraVenta").toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss a")));

                    ClienteTB clienteTB = new ClienteTB();
                    clienteTB.setNumeroDocumento(resultSet.getString("NumeroDocumento"));
                    clienteTB.setInformacion(resultSet.getString("Informacion"));
                    clienteTB.setTelefono(resultSet.getString("Telefono"));
                    clienteTB.setCelular(resultSet.getString("Celular"));
                    clienteTB.setEmail(resultSet.getString("Email"));
                    clienteTB.setDireccion(resultSet.getString("Direccion"));
                    ventaTB.setClienteTB(clienteTB);
                }
            }

            statementVenta = DBUtil.getConnection().prepareStatement("SELECT s.Clave,s.NombreMarca,d.Cantidad,d.Bonificacion FROM DetalleVentaTB AS d INNER JOIN SuministroTB AS s ON s.IdSuministro = d.IdArticulo WHERE d.IdVenta = ? AND d.IdArticulo = ?");
            statementVenta.setString(1, idVenta);
            statementVenta.setString(2, idSuministro);
            DetalleVentaTB detalleVentaTB = null;
            try (ResultSet resultSet = statementVenta.executeQuery()) {
                if (resultSet.next()) {
                    detalleVentaTB = new DetalleVentaTB();
                    detalleVentaTB.setCantidad(resultSet.getDouble("Cantidad") + resultSet.getDouble("Bonificacion"));
                    SuministroTB suministroTB = new SuministroTB();
                    suministroTB.setClave(resultSet.getString("Clave"));
                    suministroTB.setNombreMarca(resultSet.getString("NombreMarca"));
                    detalleVentaTB.setSuministroTB(suministroTB);
                }
            }

            statementVenta = DBUtil.getConnection().prepareStatement("SELECT Fecha,Hora,Cantidad,Observacion FROM HistorialSuministroLlevar WHERE IdVenta = ? AND IdSuministro = ? ");
            statementVenta.setString(1, idVenta);
            statementVenta.setString(2, idSuministro);
            ArrayList<HistorialSuministroSalidaTB> suministroSalidas = new ArrayList();
            try (ResultSet resultSet = statementVenta.executeQuery()) {
                while (resultSet.next()) {
                    HistorialSuministroSalidaTB suministroSalida = new HistorialSuministroSalidaTB();
                    suministroSalida.setFecha(resultSet.getDate("Fecha").toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    suministroSalida.setHora(resultSet.getTime("Hora").toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss a")));
                    suministroSalida.setCantidad(resultSet.getDouble("Cantidad"));
                    suministroSalida.setObservacion(resultSet.getString("Observacion"));
                    suministroSalidas.add(suministroSalida);
                }
            }

            objects[0] = ventaTB;
            objects[1] = detalleVentaTB;
            objects[2] = suministroSalidas;

            return objects;
        } catch (SQLException ex) {
            return ex.getLocalizedMessage();
        } finally {
            try {
                if (statementVenta != null) {
                    statementVenta.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException ex) {
                return ex.getLocalizedMessage();
            }
        }
    }

}
