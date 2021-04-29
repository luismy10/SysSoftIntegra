package model;

import controller.tools.Tools;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;

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
            String[] result = new String[2];

            statementValidar = DBUtil.getConnection().prepareCall("SELECT * FROM PedidoTB WHERE IdPedido = ?");
            statementValidar.setString(1, pedidoTB.getIdPedido());
            if (statementValidar.executeQuery().next()) {

                statementPedido = DBUtil.getConnection().prepareStatement("UPDATE PedidoTB SET IdProveedor=?,IdVendedor=?,FechaEmision=?,HoraEmision=?,FechaVencimiento=?,HoraVencimiento=?,IdMoneda=?,Observacion=? WHERE IdPedido = ?");
                statementPedido.setString(1, pedidoTB.getIdProveedor());
                statementPedido.setString(2, pedidoTB.getIdVendedor());
                statementPedido.setString(3, pedidoTB.getFechaEmision());
                statementPedido.setString(4, pedidoTB.getHoraEmision());
                statementPedido.setString(5, pedidoTB.getFechaVencimiento());
                statementPedido.setString(6, pedidoTB.getHoraVencimiento());
                statementPedido.setInt(7, pedidoTB.getIdMoneda());
                statementPedido.setString(8, pedidoTB.getObservacion());
                statementPedido.setString(9, pedidoTB.getIdPedido());
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
                result[0] = "1";
                result[1] = pedidoTB.getIdPedido();
                return result;
            } else {

                statementIdPedido = DBUtil.getConnection().prepareCall("{? = call Fc_Pedido_Codigo_Alfanumerico()}");
                statementIdPedido.registerOutParameter(1, java.sql.Types.VARCHAR);
                statementIdPedido.execute();
                String idPedido = statementIdPedido.getString(1);

                statementPedido = DBUtil.getConnection().prepareStatement("INSERT INTO PedidoTB(IdPedido,IdProveedor,IdVendedor,FechaEmision,HoraEmision,FechaVencimiento,HoraVencimiento,IdMoneda,Observacion) VALUES(?,?,?,?,?,?,?,?,?)");
                statementPedido.setString(1, idPedido);
                statementPedido.setString(2, pedidoTB.getIdProveedor());
                statementPedido.setString(3, pedidoTB.getIdVendedor());
                statementPedido.setString(4, pedidoTB.getFechaEmision());
                statementPedido.setString(5, pedidoTB.getHoraEmision());
                statementPedido.setString(6, pedidoTB.getFechaVencimiento());
                statementPedido.setString(7, pedidoTB.getHoraVencimiento());
                statementPedido.setInt(8, pedidoTB.getIdMoneda());
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
                result[0] = "0";
                result[1] = idPedido;
                return result;
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
                pedidoTB.setIdPedido(result.getString("IdPedido"));
                pedidoTB.setFechaEmision(result.getDate("FechaEmision").toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                pedidoTB.setHoraEmision(result.getTime("HoraEmision").toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss a")));
                pedidoTB.setProveedorTB(new ProveedorTB(result.getString("NumeroDocumento"), result.getString("RazonSocial")));
                pedidoTB.setEmpleadoTB(new EmpleadoTB(result.getString("NumeroDocumento"), result.getString("Apellidos"), result.getString("Nombres")));

                MonedaTB monedaTB = new MonedaTB();
                monedaTB.setIdMoneda(result.getInt("IdMoneda"));
                monedaTB.setNombre(result.getString("Moneda"));
                monedaTB.setSimbolo(result.getString("Simbolo"));
                pedidoTB.setMonedaTB(monedaTB);
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

    public static Object CargarPedidoEditar(String idPedido) {

        PreparedStatement statementPedido = null;
        PreparedStatement statementPedidoDetalle = null;
        ResultSet resultSet = null;
        try {
            DBUtil.dbConnect();

            statementPedido = DBUtil.getConnection().prepareStatement("{CALL Sp_Obtener_Pedido_ById(?)}");
            statementPedido.setString(1, idPedido);
            resultSet = statementPedido.executeQuery();
            if (resultSet.next()) {
                PedidoTB pedidoTB = new PedidoTB();
                pedidoTB.setIdPedido(idPedido);
                pedidoTB.setFechaEmision(resultSet.getString("FechaEmision"));
                pedidoTB.setFechaVencimiento(resultSet.getString("FechaVencimiento"));
                pedidoTB.setIdMoneda(resultSet.getInt("IdMoneda"));
                pedidoTB.setObservacion(resultSet.getString("Observacion"));
                pedidoTB.setProveedorTB(new ProveedorTB(resultSet.getString("IdProveedor"), resultSet.getString("NumeroDocumento"), resultSet.getString("RazonSocial")));

                statementPedidoDetalle = DBUtil.getConnection().prepareStatement("{CALL Sp_Obtener_Detalle_Pedido_ById(?)}");
                statementPedidoDetalle.setString(1, idPedido);
                resultSet = statementPedidoDetalle.executeQuery();
                ObservableList<PedidoDetalleTB> pedidoDetalleTBs = FXCollections.observableArrayList();
                while (resultSet.next()) {
                    PedidoDetalleTB pedidoDetalleTB = new PedidoDetalleTB();
                    pedidoDetalleTB.setIdSuministro(resultSet.getString("IdSuministro"));
                    pedidoDetalleTB.setSuministroTB(new SuministroTB(resultSet.getString("Clave"), resultSet.getString("NombreMarca")));
                    pedidoDetalleTB.setExistencia(resultSet.getDouble("Existencia"));
                    pedidoDetalleTB.setStock(resultSet.getDouble("StockMinimo") + "/" + resultSet.getDouble("StockMaximo"));
                    pedidoDetalleTB.setCantidad(resultSet.getDouble("Cantidad"));
                    pedidoDetalleTB.setCosto(resultSet.getDouble("Costo"));
                    pedidoDetalleTB.setDescuento(resultSet.getDouble("Descuento"));
                    pedidoDetalleTB.setIdImpuesto(resultSet.getInt("IdImpuesto"));
                    pedidoDetalleTB.setImpuesto(resultSet.getDouble("Valor"));
                    double descuento = pedidoDetalleTB.getDescuento();
                    double costoDescuento = pedidoDetalleTB.getCosto() - descuento;
                    pedidoDetalleTB.setImporte(costoDescuento * pedidoDetalleTB.getCantidad());

                    Button button = new Button("X");
                    button.getStyleClass().add("buttonDark");

                    pedidoDetalleTB.setBtnQuitar(button);
                    pedidoDetalleTBs.add(pedidoDetalleTB);
                }
                pedidoTB.setPedidoDetalleTBs(pedidoDetalleTBs);
                return pedidoTB;
            } else {
                throw new Exception("No se puedo contrar el pedido, intente nuevamente por favor.");
            }
        } catch (SQLException ex) {
            return ex.getLocalizedMessage();
        } catch (Exception ex) {
            return ex.getLocalizedMessage();
        } finally {
            try {
                if (statementPedido != null) {
                    statementPedido.close();
                }
                if (statementPedidoDetalle != null) {
                    statementPedidoDetalle.close();
                }
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException ex) {

            }
        }
    }

    public static Object CargarPedidoById(String idPedido) {
        PreparedStatement statementPedido = null;
        PreparedStatement statementPedidoDetalle = null;
        ResultSet result = null;
        try {
            DBUtil.dbConnect();
            statementPedido = DBUtil.getConnection().prepareStatement("{CALL Sp_Obtener_Pedido_ById(?)}");
            statementPedido.setString(1, idPedido);
            result = statementPedido.executeQuery();
            if (result.next()) {
                PedidoTB pedidoTB = new PedidoTB();
                pedidoTB.setIdPedido(result.getString("IdPedido"));
                pedidoTB.setFechaEmision(result.getDate("FechaEmision").toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                pedidoTB.setHoraEmision(result.getTime("HoraEmision").toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss a")));
                pedidoTB.setFechaVencimiento(result.getDate("FechaVencimiento").toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                pedidoTB.setObservacion(result.getString("Observacion"));
                pedidoTB.setIdMoneda(result.getInt("IdMoneda"));

                MonedaTB monedaTB = new MonedaTB();
                monedaTB.setIdMoneda(result.getInt("IdMoneda"));
                monedaTB.setNombre(result.getString("Moneda"));
                monedaTB.setSimbolo(result.getString("Simbolo"));
                pedidoTB.setMonedaTB(monedaTB);

                ProveedorTB proveedorTB = new ProveedorTB();
                proveedorTB.setIdProveedor(result.getString("IdProveedor"));
                proveedorTB.setNumeroDocumento(result.getString("NumeroDocumento"));
                proveedorTB.setRazonSocial(result.getString("RazonSocial"));
                proveedorTB.setCelular(result.getString("Celular"));
                proveedorTB.setTelefono(result.getString("Telefono"));
                proveedorTB.setEmail(result.getString("Email"));
                proveedorTB.setDireccion(result.getString("Direccion"));
                pedidoTB.setProveedorTB(proveedorTB);

                EmpleadoTB empleadoTB = new EmpleadoTB();
                empleadoTB.setIdEmpleado(result.getString("IdEmpleado"));
                empleadoTB.setNumeroDocumento(result.getString("NumeroDocumento"));
                empleadoTB.setApellidos(result.getString("Apellidos"));
                empleadoTB.setNombres(result.getString("Nombres"));
                pedidoTB.setEmpleadoTB(empleadoTB);

                ArrayList<SuministroTB> suministroTBs = new ArrayList();
                statementPedidoDetalle = DBUtil.getConnection().prepareStatement("{CALL Sp_Obtener_Detalle_Pedido_ById(?)}");
                statementPedidoDetalle.setString(1, idPedido);
                result = statementPedidoDetalle.executeQuery();
                while (result.next()) {
                    SuministroTB suministroTB = new SuministroTB();
                    suministroTB.setId(result.getRow());
                    suministroTB.setIdSuministro(result.getString("IdSuministro"));
                    suministroTB.setClave(result.getString("Clave"));
                    suministroTB.setNombreMarca(result.getString("NombreMarca"));
                    suministroTB.setCantidad(result.getDouble("Cantidad"));
                    suministroTB.setCostoCompra(result.getDouble("Costo"));
                    suministroTB.setDescuento(result.getDouble("Descuento"));
                    suministroTB.setImpuestoValor(result.getDouble("Valor"));
                    double descuento = suministroTB.getDescuento();
                    double costoDescuento = suministroTB.getCostoCompra() - descuento;
                    suministroTB.setImporteNeto(costoDescuento * suministroTB.getCantidad());
                    suministroTBs.add(suministroTB);
                }
                pedidoTB.setSuministroTBs(suministroTBs);
                return pedidoTB;
            } else {
                throw new Exception("No se pudo encontrar datos para mostrar.");
            }
        } catch (SQLException ex) {
            return ex.getLocalizedMessage();
        } catch (Exception ex) {
            return ex.getLocalizedMessage();
        } finally {
            try {
                if (statementPedido != null) {
                    statementPedido.close();
                }
                if (statementPedidoDetalle != null) {
                    statementPedidoDetalle.close();
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
