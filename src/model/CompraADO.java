package model;

import controller.tools.Tools;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class CompraADO extends DBUtil {

    public static String Compra_Contado(BancoHistorialTB bancoHistorialTB, CompraTB compraTB, TableView<DetalleCompraTB> tableView, ObservableList<LoteTB> loteTBs) {

        CallableStatement codigo_compra = null;
        PreparedStatement compra = null;
        PreparedStatement detalle_compra = null;
        PreparedStatement suministro_update = null;
        PreparedStatement suministro_kardex = null;
        //PreparedStatement lote_compra = null;

        PreparedStatement preparedBanco = null;
        PreparedStatement preparedBancoHistorial = null;

        dbConnect();
        if (getConnection() != null) {

            try {
                dbConnect();
                getConnection().setAutoCommit(false);

                codigo_compra = getConnection().prepareCall("{? = call Fc_Compra_Codigo_Alfanumerico()}");
                codigo_compra.registerOutParameter(1, java.sql.Types.VARCHAR);
                codigo_compra.execute();
                String id_compra = codigo_compra.getString(1);

                compra = getConnection().prepareStatement("INSERT INTO "
                        + "CompraTB("
                        + "IdCompra,"
                        + "Proveedor,"
                        + "Serie,"
                        + "Numeracion,"
                        + "TipoMoneda,"
                        + "FechaCompra,"
                        + "HoraCompra,"
                        + "FechaVencimiento,"
                        + "HoraVencimiento,"
                        + "SubTotal,"
                        + "Descuento,"
                        + "Total,"
                        + "Observaciones,"
                        + "Notas,"
                        + "TipoCompra,"
                        + "EstadoCompra,"
                        + "Usuario) "
                        + "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

                detalle_compra = getConnection().prepareStatement("INSERT INTO "
                        + "DetalleCompraTB("
                        + "IdCompra,"
                        + "IdArticulo,"
                        + "Cantidad,"
                        + "PrecioCompra,"
                        + "Descuento,"
                        + "IdImpuesto,"
                        + "NombreImpuesto,"
                        + "ValorImpuesto,"
                        + "ImpuestoSumado,"
                        + "Importe,"
                        + "Descripcion)"
                        + "VALUES(?,?,?,?,?,?,?,?,?,?,?)");

                suministro_update = getConnection().prepareStatement("UPDATE SuministroTB SET Cantidad = Cantidad + ?,PrecioCompra = ? WHERE IdSuministro = ?");

                suministro_kardex = getConnection().prepareStatement("INSERT INTO "
                        + "KardexSuministroTB("
                        + "IdSuministro,"
                        + "Fecha,"
                        + "Hora,"
                        + "Tipo,"
                        + "Movimiento,"
                        + "Detalle,"
                        + "Cantidad) "
                        + "VALUES(?,?,?,?,?,?,?)");

                /*         lote_compra = getConnection().prepareStatement("INSERT INTO "
                        + "LoteTB("
                        + "NumeroLote,"
                        + "FechaCaducidad,"
                        + "ExistenciaInicial,"
                        + "ExistenciaActual,"
                        + "IdArticulo,"
                        + "IdCompra) "
                        + "VALUES(?,?,?,?,?,?)");*/
                preparedBanco = DBUtil.getConnection().prepareStatement("UPDATE Banco SET SaldoInicial = SaldoInicial - ? WHERE IdBanco = ?");

                preparedBancoHistorial = DBUtil.getConnection().prepareStatement("INSERT INTO BancoHistorialTB(IdBanco,IdEmpleado,IdProcedencia,Descripcion,Fecha,Hora,Entrada,Salida)VALUES(?,?,?,?,?,?,?,?)");

                preparedBanco.setDouble(1, bancoHistorialTB.getSalida());
                preparedBanco.setString(2, bancoHistorialTB.getIdBanco());
                preparedBanco.addBatch();

                preparedBancoHistorial.setString(1, bancoHistorialTB.getIdBanco());
                preparedBancoHistorial.setString(2, bancoHistorialTB.getIdEmpleado());
                preparedBancoHistorial.setString(3, "");
                preparedBancoHistorial.setString(4, bancoHistorialTB.getDescripcion());
                preparedBancoHistorial.setString(5, bancoHistorialTB.getFecha());
                preparedBancoHistorial.setString(6, bancoHistorialTB.getHora());
                preparedBancoHistorial.setDouble(7, 0);
                preparedBancoHistorial.setDouble(8, bancoHistorialTB.getSalida());
                preparedBancoHistorial.addBatch();

                compra.setString(1, id_compra);
                compra.setString(2, compraTB.getProveedor());
                compra.setString(3, compraTB.getSerie());
                compra.setString(4, compraTB.getNumeracion());
                compra.setInt(5, compraTB.getTipoMoneda());
                compra.setString(6, compraTB.getFechaCompra());
                compra.setString(7, compraTB.getHoraCompra());
                compra.setString(8, compraTB.getFechaVencimiento());
                compra.setString(9, compraTB.getHoraVencimiento());
                compra.setDouble(10, compraTB.getSubTotal());
                compra.setDouble(11, compraTB.getDescuento());
                compra.setDouble(12, compraTB.getTotal());
                compra.setString(13, compraTB.getObservaciones());
                compra.setString(14, compraTB.getNotas());
                compra.setInt(15, compraTB.getTipo());
                compra.setInt(16, compraTB.getEstado());
                compra.setString(17, compraTB.getUsuario());
                compra.addBatch();

                for (int i = 0; i < tableView.getItems().size(); i++) {
                    detalle_compra.setString(1, id_compra);
                    detalle_compra.setString(2, tableView.getItems().get(i).getIdArticulo());
                    detalle_compra.setDouble(3, tableView.getItems().get(i).getCantidad());
                    detalle_compra.setDouble(4, tableView.getItems().get(i).getPrecioCompra());
                    detalle_compra.setDouble(5, tableView.getItems().get(i).getDescuento());

                    detalle_compra.setInt(6, tableView.getItems().get(i).getIdImpuesto());
                    detalle_compra.setString(7, tableView.getItems().get(i).getNombreImpuesto());
                    detalle_compra.setDouble(8, tableView.getItems().get(i).getValorImpuesto());
                    detalle_compra.setDouble(9, tableView.getItems().get(i).getImpuestoSumado());
                    detalle_compra.setDouble(10, tableView.getItems().get(i).getImporte());
                    detalle_compra.setString(11, tableView.getItems().get(i).getDescripcion());
                    detalle_compra.addBatch();

                    suministro_update.setDouble(1, tableView.getItems().get(i).getCantidad());
                    suministro_update.setDouble(2, tableView.getItems().get(i).getPrecioCompra());
                    suministro_update.setString(3, tableView.getItems().get(i).getIdArticulo());
                    suministro_update.addBatch();

                    suministro_kardex.setString(1, tableView.getItems().get(i).getIdArticulo());
                    suministro_kardex.setString(2, Tools.getDate());
                    suministro_kardex.setString(3, Tools.getHour());
                    suministro_kardex.setShort(4, (short) 1);
                    suministro_kardex.setInt(5, 2);
                    suministro_kardex.setString(6, "Compra con numeración " + compraTB.getNumeracion().toUpperCase());
                    suministro_kardex.setDouble(7, tableView.getItems().get(i).getCantidad());
                    suministro_kardex.addBatch();
                }

//            for (int i = 0; i < loteTBs.size(); i++) {
//                lote_compra.setString(1, loteTBs.get(i).getNumeroLote().equalsIgnoreCase("") ? id_compra + loteTBs.get(i).getIdArticulo() : loteTBs.get(i).getNumeroLote());
//                lote_compra.setDate(2, Date.valueOf(loteTBs.get(i).getFechaCaducidad()));
//                lote_compra.setDouble(3, loteTBs.get(i).getExistenciaInicial());
//                lote_compra.setDouble(4, loteTBs.get(i).getExistenciaActual());
//                lote_compra.setString(5, loteTBs.get(i).getIdArticulo());
//                lote_compra.setString(6, id_compra);
//                lote_compra.addBatch();
//            }
                compra.executeBatch();
                detalle_compra.executeBatch();
                suministro_update.executeBatch();
                suministro_kardex.executeBatch();
                preparedBanco.executeBatch();
                preparedBancoHistorial.executeBatch();
                // lote_compra.executeBatch();
                getConnection().commit();
                return "register";
            } catch (SQLException ex) {
                try {
                    getConnection().rollback();
                    return ex.getLocalizedMessage();
                } catch (SQLException ex1) {
                    return ex1.getLocalizedMessage();
                }
            } finally {
                try {
                    if (codigo_compra != null) {
                        codigo_compra.close();
                    }
                    if (compra != null) {
                        compra.close();
                    }
                    if (detalle_compra != null) {
                        detalle_compra.close();
                    }
                    if (preparedBanco != null) {
                        preparedBanco.close();
                    }
                    if (preparedBancoHistorial != null) {
                        preparedBancoHistorial.close();
                    }
                    if (suministro_update != null) {
                        suministro_update.close();
                    }
                    if (suministro_kardex != null) {
                        suministro_kardex.close();
                    }
                    /*if (lote_compra != null) {
                        lote_compra.close();
                    }*/
                    dbDisconnect();
                } catch (SQLException ex) {
                    return ex.getLocalizedMessage();
                }
            }
        } else {
            return "No se pudo conectar el servidor, intente nuevamente.";
        }
    }

    public static String Compra_Credito(CompraTB compraTB, TableView<DetalleCompraTB> tableView, ObservableList<LoteTB> loteTBs, TableView<CompraCreditoTB> tvListaCredito) {
        CallableStatement codigo_compra = null;
        CallableStatement codigo_credito = null;
        PreparedStatement compra = null;
        PreparedStatement detalle_compra = null;
        PreparedStatement compra_credito = null;
        PreparedStatement suministro_update = null;
        PreparedStatement suministro_kardex = null;
        PreparedStatement lote_compra = null;
        dbConnect();
        if (getConnection() != null) {
            try {
                getConnection().setAutoCommit(false);
                codigo_compra = getConnection().prepareCall("{? = call Fc_Compra_Codigo_Alfanumerico()}");
                codigo_compra.registerOutParameter(1, java.sql.Types.VARCHAR);
                codigo_compra.execute();
                String id_compra = codigo_compra.getString(1);

                compra = getConnection().prepareStatement("INSERT INTO "
                        + "CompraTB("
                        + "IdCompra,"
                        + "Proveedor,"
                        + "Serie,"
                        + "Numeracion,"
                        + "TipoMoneda,"
                        + "FechaCompra,"
                        + "HoraCompra,"
                        + "FechaVencimiento,"
                        + "HoraVencimiento,"
                        + "SubTotal,"
                        + "Descuento,"
                        + "Total,"
                        + "Observaciones,"
                        + "Notas,"
                        + "TipoCompra,"
                        + "EstadoCompra,"
                        + "Usuario) "
                        + "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

                detalle_compra = getConnection().prepareStatement("INSERT INTO "
                        + "DetalleCompraTB("
                        + "IdCompra,"
                        + "IdArticulo,"
                        + "Cantidad,"
                        + "PrecioCompra,"
                        + "Descuento,"
                        + "IdImpuesto,"
                        + "NombreImpuesto,"
                        + "ValorImpuesto,"
                        + "ImpuestoSumado,"
                        + "Importe,"
                        + "Descripcion)"
                        + "VALUES(?,?,?,?,?,?,?,?,?,?,?)");

                compra_credito = getConnection().prepareStatement("INSERT INTO "
                        + "CompraCreditoTB("
                        + "IdCompra"
                        + ",Monto"
                        + ",FechaRegistro"
                        + ",HoraRegistro"
                        + ",fechaPago"
                        + ",horaPago"
                        + ",Estado)"
                        + "VALUES(?,?,?,?,?,?,?)");

                suministro_update = getConnection().prepareStatement("UPDATE SuministroTB SET Cantidad = Cantidad + ?,PrecioCompra = ? WHERE IdSuministro = ?");

                suministro_kardex = getConnection().prepareStatement("INSERT INTO "
                        + "KardexSuministroTB("
                        + "IdSuministro,"
                        + "Fecha,"
                        + "Hora,"
                        + "Tipo,"
                        + "Movimiento,"
                        + "Detalle,"
                        + "Cantidad) "
                        + "VALUES(?,?,?,?,?,?,?)");

                lote_compra = getConnection().prepareStatement("INSERT INTO "
                        + "LoteTB("
                        + "NumeroLote,"
                        + "FechaCaducidad,"
                        + "ExistenciaInicial,"
                        + "ExistenciaActual,"
                        + "IdArticulo,"
                        + "IdCompra) "
                        + "VALUES(?,?,?,?,?,?)");

                compra.setString(1, id_compra);
                compra.setString(2, compraTB.getProveedor());
                compra.setString(3, compraTB.getSerie());
                compra.setString(4, compraTB.getNumeracion());
                compra.setInt(5, compraTB.getTipoMoneda());
                compra.setString(6, compraTB.getFechaCompra());
                compra.setString(7, compraTB.getHoraCompra());
                compra.setString(8, compraTB.getFechaVencimiento());
                compra.setString(9, compraTB.getHoraVencimiento());
                compra.setDouble(10, compraTB.getSubTotal());
                compra.setDouble(11, compraTB.getDescuento());
                compra.setDouble(12, compraTB.getTotal());
                compra.setString(13, compraTB.getObservaciones());
                compra.setString(14, compraTB.getNotas());
                compra.setInt(15, compraTB.getTipo());
                compra.setInt(16, compraTB.getEstado());
                compra.setString(17, compraTB.getUsuario());
                compra.addBatch();

                for (CompraCreditoTB item : tvListaCredito.getItems()) {
                    compra_credito.setString(1, id_compra);
                    compra_credito.setDouble(2, item.getMonto());
                    compra_credito.setString(3, item.getFechaRegistro());
                    compra_credito.setString(4, item.getHoraRegistro());
                    compra_credito.setString(5, "");
                    compra_credito.setString(6, "");
                    compra_credito.setBoolean(7, item.isEstado());
                    compra_credito.addBatch();
                }

                for (int i = 0; i < tableView.getItems().size(); i++) {
                    detalle_compra.setString(1, id_compra);
                    detalle_compra.setString(2, tableView.getItems().get(i).getIdArticulo());
                    detalle_compra.setDouble(3, tableView.getItems().get(i).getCantidad());
                    detalle_compra.setDouble(4, tableView.getItems().get(i).getPrecioCompra());
                    detalle_compra.setDouble(5, tableView.getItems().get(i).getDescuento());

                    detalle_compra.setInt(6, tableView.getItems().get(i).getIdImpuesto());
                    detalle_compra.setString(7, tableView.getItems().get(i).getNombreImpuesto());
                    detalle_compra.setDouble(8, tableView.getItems().get(i).getValorImpuesto());
                    detalle_compra.setDouble(9, tableView.getItems().get(i).getImpuestoSumado());
                    detalle_compra.setDouble(10, tableView.getItems().get(i).getImporte());
                    detalle_compra.setString(11, tableView.getItems().get(i).getDescripcion());
                    detalle_compra.addBatch();

                    suministro_update.setDouble(1, tableView.getItems().get(i).getCantidad());
                    suministro_update.setDouble(2, tableView.getItems().get(i).getPrecioCompra());
                    suministro_update.setString(3, tableView.getItems().get(i).getIdArticulo());
                    suministro_update.addBatch();

                    suministro_kardex.setString(1, tableView.getItems().get(i).getIdArticulo());
                    suministro_kardex.setString(2, Tools.getDate());
                    suministro_kardex.setString(3, Tools.getHour());
                    suministro_kardex.setShort(4, (short) 1);
                    suministro_kardex.setInt(5, 2);
                    suministro_kardex.setString(6, "Compra con numeración " + compraTB.getNumeracion().toUpperCase());
                    suministro_kardex.setDouble(7, tableView.getItems().get(i).getCantidad());
                    suministro_kardex.addBatch();
                }

                compra.executeBatch();
                detalle_compra.executeBatch();
                compra_credito.executeBatch();
                suministro_update.executeBatch();
                suministro_kardex.executeBatch();
                lote_compra.executeBatch();
                getConnection().commit();
                return "register";
            } catch (SQLException ex) {
                try {
                    getConnection().rollback();
                    return ex.getLocalizedMessage();
                } catch (SQLException ex1) {
                    return ex1.getLocalizedMessage();
                }
            } finally {
                try {
                    if (codigo_compra != null) {
                        codigo_compra.close();
                    }
                    if (compra != null) {
                        compra.close();
                    }
                    if (detalle_compra != null) {
                        detalle_compra.close();
                    }
                    if (compra_credito != null) {
                        compra_credito.close();
                    }
                    if (codigo_credito != null) {
                        codigo_credito.close();
                    }
                    if (suministro_update != null) {
                        suministro_update.close();
                    }
                    if (suministro_kardex != null) {
                        suministro_kardex.close();
                    }
                    if (lote_compra != null) {
                        lote_compra.close();
                    }
                    dbDisconnect();
                } catch (SQLException ex) {
                    return ex.getLocalizedMessage();
                }
            }
        } else {
            return "No se pudo conectar el servidor, intente nuevamente.";
        }
    }

    public static String Compra_Borrado(CompraTB compraTB, TableView<DetalleCompraTB> tableView, ObservableList<LoteTB> loteTBs) {
        CallableStatement codigo_compra = null;
        CallableStatement codigo_credito = null;
        PreparedStatement compra = null;
        PreparedStatement detalle_compra = null;
        PreparedStatement lote_compra = null;
        dbConnect();
        if (getConnection() != null) {
            try {
                getConnection().setAutoCommit(false);
                codigo_compra = getConnection().prepareCall("{? = call Fc_Compra_Codigo_Alfanumerico()}");
                codigo_compra.registerOutParameter(1, java.sql.Types.VARCHAR);
                codigo_compra.execute();
                String id_compra = codigo_compra.getString(1);

                compra = getConnection().prepareStatement("INSERT INTO "
                        + "CompraTB("
                        + "IdCompra,"
                        + "Proveedor,"
                        + "Comprobante,"
                        + "Numeracion,"
                        + "TipoMoneda,"
                        + "FechaCompra,"
                        + "HoraCompra,"
                        + "FechaVencimiento,"
                        + "HoraVencimiento,"
                        + "SubTotal,"
                        + "Descuento,"
                        + "Total,"
                        + "Observaciones,"
                        + "Notas,"
                        + "TipoCompra,"
                        + "EstadoCompra,"
                        + "Usuario) "
                        + "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

                detalle_compra = getConnection().prepareStatement("INSERT INTO "
                        + "DetalleCompraTB("
                        + "IdCompra,"
                        + "IdArticulo,"
                        + "Cantidad,"
                        + "PrecioCompra,"
                        + "Descuento,"
                        + "IdImpuesto,"
                        + "NombreImpuesto,"
                        + "ValorImpuesto,"
                        + "ImpuestoSumado,"
                        + "Importe,"
                        + "Descripcion)"
                        + "VALUES(?,?,?,?,?,?,?,?,?,?,?)");

                lote_compra = getConnection().prepareStatement("INSERT INTO "
                        + "LoteTB("
                        + "NumeroLote,"
                        + "FechaCaducidad,"
                        + "ExistenciaInicial,"
                        + "ExistenciaActual,"
                        + "IdArticulo,"
                        + "IdCompra) "
                        + "VALUES(?,?,?,?,?,?)");

                compra.setString(1, id_compra);
                compra.setString(2, compraTB.getProveedor());
                compra.setString(3, compraTB.getSerie());
                compra.setString(4, compraTB.getNumeracion());
                compra.setInt(5, compraTB.getTipoMoneda());
                compra.setString(6, compraTB.getFechaCompra());
                compra.setString(7, compraTB.getHoraCompra());
                compra.setString(8, compraTB.getFechaVencimiento());
                compra.setString(9, compraTB.getHoraVencimiento());
                compra.setDouble(10, compraTB.getSubTotal());
                compra.setDouble(11, compraTB.getDescuento());
                compra.setDouble(12, compraTB.getTotal());
                compra.setString(13, compraTB.getObservaciones());
                compra.setString(14, compraTB.getNotas());
                compra.setInt(15, compraTB.getTipo());
                compra.setInt(16, compraTB.getEstado());
                compra.setString(17, compraTB.getUsuario());
                compra.addBatch();

                for (int i = 0; i < tableView.getItems().size(); i++) {
                    detalle_compra.setString(1, id_compra);
                    detalle_compra.setString(2, tableView.getItems().get(i).getIdArticulo());
                    detalle_compra.setDouble(3, tableView.getItems().get(i).getCantidad());
                    detalle_compra.setDouble(4, tableView.getItems().get(i).getPrecioCompra());
                    detalle_compra.setDouble(5, tableView.getItems().get(i).getDescuento());

                    detalle_compra.setInt(6, tableView.getItems().get(i).getIdImpuesto());
                    detalle_compra.setString(7, tableView.getItems().get(i).getNombreImpuesto());
                    detalle_compra.setDouble(8, tableView.getItems().get(i).getValorImpuesto());
                    detalle_compra.setDouble(9, tableView.getItems().get(i).getImpuestoSumado());
                    detalle_compra.setDouble(10, tableView.getItems().get(i).getImporte());
                    detalle_compra.setString(11, tableView.getItems().get(i).getDescripcion());
                    detalle_compra.addBatch();
                }

                compra.executeBatch();
                detalle_compra.executeBatch();
                lote_compra.executeBatch();
                getConnection().commit();
                return "register";
            } catch (SQLException ex) {
                try {
                    getConnection().rollback();
                    return ex.getLocalizedMessage();
                } catch (SQLException ex1) {
                    return ex1.getLocalizedMessage();
                }
            } finally {
                try {
                    if (codigo_compra != null) {
                        codigo_compra.close();
                    }
                    if (compra != null) {
                        compra.close();
                    }
                    if (detalle_compra != null) {
                        detalle_compra.close();
                    }
                    if (codigo_credito != null) {
                        codigo_credito.close();
                    }
                    if (lote_compra != null) {
                        lote_compra.close();
                    }
                    dbDisconnect();
                } catch (SQLException ex) {
                    return ex.getLocalizedMessage();
                }
            }
        } else {
            return "No se pudo conectar el servidor, intente nuevamente.";
        }
    }

    public static ObservableList<CompraTB> ListComprasRealizadas(short opcion, String value, String fechaInicial, String fechaFinal, int estadoCompra) {
        String selectStmt = "{call Sp_Listar_Compras(?,?,?,?,?)}";
        PreparedStatement preparedStatement = null;
        ResultSet rsEmps = null;
        ObservableList<CompraTB> empList = FXCollections.observableArrayList();
        try {
            dbConnect();
            preparedStatement = getConnection().prepareStatement(selectStmt);
            preparedStatement.setShort(1, opcion);
            preparedStatement.setString(2, value);
            preparedStatement.setString(3, fechaInicial);
            preparedStatement.setString(4, fechaFinal);
            preparedStatement.setInt(5, estadoCompra);
            rsEmps = preparedStatement.executeQuery();

            while (rsEmps.next()) {
                CompraTB compraTB = new CompraTB();
                compraTB.setId(rsEmps.getInt("Filas"));
                compraTB.setIdCompra(rsEmps.getString("IdCompra"));
                compraTB.setFechaCompra(rsEmps.getString("FechaCompra"));
                compraTB.setHoraCompra(rsEmps.getTime("HoraCompra").toLocalTime().format(DateTimeFormatter.ofPattern("hh:mm:ss a")));
                compraTB.setSerie(rsEmps.getString("Serie"));
                compraTB.setNumeracion(rsEmps.getString("Numeracion"));
                compraTB.setProveedorTB(new ProveedorTB(rsEmps.getString("NumeroDocumento"), rsEmps.getString("RazonSocial")));
                compraTB.setTipoName(rsEmps.getString("Tipo"));
                compraTB.setEstado(rsEmps.getInt("EstadoCompra"));
                compraTB.setEstadoName(rsEmps.getString("Estado"));

                Label label = new Label(compraTB.getEstadoName());
                label.getStyleClass().add(compraTB.getEstado() == 1 ? "label-asignacion" : compraTB.getEstado() == 2 ? "label-medio" : compraTB.getEstado() == 3 ? "label-proceso" : "label-ultimo");

                compraTB.setEstadoLabel(label);
                compraTB.setTipoMonedaName(rsEmps.getString("Simbolo"));
                compraTB.setTotal(rsEmps.getDouble("Total"));
                empList.add(compraTB);
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
                dbDisconnect();
            } catch (SQLException ex) {

            }
        }
        return empList;
    }

    public static ArrayList<Object> ListCompletaDetalleCompra(String idCompra) {
        PreparedStatement preparedCompra = null;
        PreparedStatement preparedProveedor = null;
        PreparedStatement preparedDetalleCompra = null;
        PreparedStatement preparedCredito = null;
        ArrayList<Object> objects = new ArrayList<>();
        dbConnect();
        if (getConnection() != null) {
            
            try {
                preparedCompra = getConnection().prepareStatement("{call Sp_Obtener_Compra_ById(?)}");
                preparedCompra.setString(1, idCompra);
                ResultSet resultSet = preparedCompra.executeQuery();
                CompraTB compraTB = null;
                if (resultSet.next()) {
                    compraTB = new CompraTB();
                    compraTB.setIdCompra(idCompra);
                    compraTB.setFechaCompra(resultSet.getDate("FechaCompra").toLocalDate().format(DateTimeFormatter.ofPattern("EEEE d 'de' MMMM 'de' yyyy")));
                    compraTB.setHoraCompra(resultSet.getTime("HoraCompra").toLocalTime().format(DateTimeFormatter.ofPattern("hh:mm:ss a")));
                    compraTB.setSerie(resultSet.getString("Serie").toUpperCase());
                    compraTB.setNumeracion(resultSet.getString("Numeracion"));
                    compraTB.setTipoMonedaName(resultSet.getString("Simbolo"));
                    compraTB.setTipoName(resultSet.getString("Tipo"));
                    compraTB.setEstado(resultSet.getInt("EstadoCompra"));
                    compraTB.setEstadoName(resultSet.getString("Estado"));
                    compraTB.setTotal(resultSet.getDouble("Total"));
                    compraTB.setObservaciones(resultSet.getString("Observaciones"));
                    compraTB.setNotas(resultSet.getString("Notas"));
                    //moneda start
                    MonedaTB monedaTB = new MonedaTB();
                    monedaTB.setNombre(resultSet.getString("Nombre"));
                    monedaTB.setSimbolo(resultSet.getString("Simbolo"));
                    compraTB.setMonedaTB(monedaTB);
                    //moneda end
                    objects.add(compraTB);
                } else {
                    objects.add(compraTB);
                }

                preparedProveedor = getConnection().prepareStatement("{call Sp_Obtener_Proveedor_ByIdCompra(?)}");
                preparedProveedor.setString(1, idCompra);
                ResultSet resultSetProveedor = preparedProveedor.executeQuery();
                ProveedorTB proveedorTB = null;
                if (resultSetProveedor.next()) {
                    proveedorTB = new ProveedorTB();

                    proveedorTB.setIdProveedor(resultSetProveedor.getString("IdProveedor"));
                    proveedorTB.setTipoDocumentoName(resultSetProveedor.getString("NombreDocumento"));
                    proveedorTB.setNumeroDocumento(resultSetProveedor.getString("NumeroDocumento"));
                    proveedorTB.setRazonSocial(resultSetProveedor.getString("Proveedor"));
                    proveedorTB.setTelefono(resultSetProveedor.getString("Telefono"));
                    proveedorTB.setCelular(resultSetProveedor.getString("Celular"));
                    proveedorTB.setDireccion(resultSetProveedor.getString("Direccion"));
                    proveedorTB.setEmail(resultSetProveedor.getString("Email"));
                }
                if (compraTB != null) {
                    compraTB.setProveedorTB(proveedorTB);
                }

                preparedDetalleCompra = getConnection().prepareStatement("{call Sp_Listar_Detalle_Compra(?)}");
                preparedDetalleCompra.setString(1, idCompra);
                ResultSet rsEmps = preparedDetalleCompra.executeQuery();
                ObservableList<DetalleCompraTB> empList = FXCollections.observableArrayList();
                while (rsEmps.next()) {
                    DetalleCompraTB detalleCompraTB = new DetalleCompraTB();
                    detalleCompraTB.setId(rsEmps.getRow());
                    detalleCompraTB.setIdArticulo(rsEmps.getString("IdSuministro"));
                    //
                    SuministroTB suministrosTB = new SuministroTB();
                    suministrosTB.setClave(rsEmps.getString("Clave"));
                    suministrosTB.setNombreMarca(rsEmps.getString("NombreMarca"));
                    suministrosTB.setUnidadVenta(rsEmps.getInt("UnidadVenta"));
                    suministrosTB.setUnidadCompraName(rsEmps.getString("UnidadCompra"));
                    detalleCompraTB.setSuministroTB(suministrosTB);
                    // 
                    detalleCompraTB.setCantidad(rsEmps.getDouble("Cantidad"));
                    detalleCompraTB.setPrecioCompra(rsEmps.getDouble("PrecioCompra"));
                    detalleCompraTB.setDescuento(rsEmps.getDouble("Descuento"));
                    detalleCompraTB.setIdImpuesto(rsEmps.getInt("IdImpuesto"));
                    detalleCompraTB.setValorImpuesto(rsEmps.getDouble("ValorImpuesto"));
                    double totalDescuento = detalleCompraTB.getPrecioCompra() * (detalleCompraTB.getDescuento() / 100.00);
                    double nuevoPrecioCompra = detalleCompraTB.getPrecioCompra() - totalDescuento;
                    double totalImpuesto = Tools.calculateTax(detalleCompraTB.getValorImpuesto(), nuevoPrecioCompra);
                    detalleCompraTB.setPrecioCompraCalculado((totalDescuento == 0 ? 0 : -1 * totalDescuento));
                    detalleCompraTB.setImpuestoSumado(detalleCompraTB.getCantidad() * totalImpuesto);
                    detalleCompraTB.setImporte(detalleCompraTB.getCantidad() * (nuevoPrecioCompra + totalImpuesto));

                    empList.add(detalleCompraTB);
                }
                objects.add(empList);

                preparedCredito = getConnection().prepareStatement("{call Sp_Listar_Compra_Credito_Por_IdCompra(?)}");
                preparedCredito.setString(1, idCompra);
                ResultSet rsCredito = preparedCredito.executeQuery();
                ArrayList<CompraCreditoTB> listComprasCredito = new ArrayList();
                while (rsCredito.next()) {
                    CompraCreditoTB creditoTB = new CompraCreditoTB();
                    creditoTB.setMonto(rsCredito.getDouble("Monto"));
                    creditoTB.setFechaRegistro(rsCredito.getDate("FechaRegistro").toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    creditoTB.setFechaPago(rsCredito.getDate("FechaPago").toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    creditoTB.setHoraPago(rsCredito.getTime("HoraPago").toLocalTime().format(DateTimeFormatter.ofPattern("hh:mm:ss a")));
                    creditoTB.setEstado(rsCredito.getBoolean("Estado"));
                    listComprasCredito.add(creditoTB);
                }
                objects.add(listComprasCredito);
            } catch (SQLException ex) {

            } finally {
                try {
                    if (preparedCompra != null) {
                        preparedCompra.close();
                    }
                    if (preparedProveedor != null) {
                        preparedProveedor.close();
                    }
                    if (preparedDetalleCompra != null) {
                        preparedDetalleCompra.close();
                    }
                    if (preparedCredito != null) {
                        preparedCredito.close();
                    }
                    dbDisconnect();
                } catch (SQLException ex) {

                }
            }
        }
        return objects;
    }

    public static ObservableList<CompraTB> List_Compras_Realizadas(String value, String fecha, short opcion) {
        PreparedStatement statementCompra = null;
        ObservableList<CompraTB> compraTBs = FXCollections.observableArrayList();
        dbConnect();
        if (getConnection() != null) {
            try {
                statementCompra = getConnection().prepareStatement("{call Sp_Listar_Compras_For_Movimiento(?,?,?)}");
                statementCompra.setString(1, value);
                statementCompra.setString(2, fecha);
                statementCompra.setShort(3, opcion);
                ResultSet resultSet = statementCompra.executeQuery();
                while (resultSet.next()) {
                    CompraTB compraTB = new CompraTB();
                    compraTB.setId(resultSet.getRow());
                    compraTB.setIdCompra(resultSet.getString("IdCompra"));
                    compraTB.setFechaCompra(resultSet.getDate("Fecha").toLocalDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
                    compraTB.setHoraCompra(resultSet.getTime("Hora").toLocalTime().format(DateTimeFormatter.ofPattern("hh:mm:ss a")));
                    compraTB.setNumeracion(resultSet.getString("Numeracion"));
                    compraTB.setProveedor(resultSet.getString("RazonSocial"));
                    compraTB.setTipoMonedaName(resultSet.getString("Simbolo"));
                    compraTB.setTotal(resultSet.getDouble("Total"));
                    compraTBs.add(compraTB);

                }
            } catch (SQLException ex) {
                System.out.println("Compras Ado:" + ex.getLocalizedMessage());
            } finally {
                try {
                    if (statementCompra != null) {
                        statementCompra.close();
                    }
                    dbDisconnect();
                } catch (SQLException ex) {
                }
            }

        }
        return compraTBs;
    }

    private static ObservableList<SuministroTB> Listar_Detalle_Compra_By_IdCompra(String idCompra) {
        ObservableList<SuministroTB> list = FXCollections.observableArrayList();
        dbConnect();
        if (getConnection() != null) {
            PreparedStatement statementDetalleComra = null;
            try {
                statementDetalleComra = getConnection().prepareStatement("{call Sp_Listar_Detalle_Compra_By_IdCompra(?)}");
                statementDetalleComra.setString(1, idCompra);
                ResultSet rsEmps = statementDetalleComra.executeQuery();
                while (rsEmps.next()) {
                    SuministroTB suministroTB = new SuministroTB();
                    suministroTB.setIdSuministro(rsEmps.getString("IdSuministro"));
                    suministroTB.setClave(rsEmps.getString("Clave"));
                    suministroTB.setNombreMarca(rsEmps.getString("NombreMarca"));
                    suministroTB.setCantidad(rsEmps.getDouble("Cantidad"));
                    suministroTB.setUnidadCompraName(rsEmps.getString("UnidadCompraNombre"));
                    suministroTB.setCostoCompra(rsEmps.getDouble("PrecioCompra"));
                    suministroTB.setPrecioVentaGeneral(rsEmps.getDouble("PrecioVentaGeneral"));
                    suministroTB.setInventario(rsEmps.getBoolean("Inventario"));
                    suministroTB.setValorInventario(rsEmps.getShort("ValorInventario"));
                    suministroTB.setMovimiento(suministroTB.getCantidad());
                    TextField tf = new TextField(Tools.roundingValue(suministroTB.getCantidad(), 4));
                    tf.getStyleClass().add("text-field-normal");
                    tf.setOnKeyTyped((KeyEvent event) -> {
                        char c = event.getCharacter().charAt(0);
                        if ((c < '0' || c > '9') && (c != '\b') && (c != '.') && (c != '-')) {
                            event.consume();
                        }
                        if (c == '.' && tf.getText().contains(".") || c == '-' && tf.getText().contains("-")) {
                            event.consume();
                        }
                    });
                    suministroTB.setTxtMovimiento(tf);

                    CheckBox checkbox = new CheckBox("");
                    checkbox.getStyleClass().add("check-box-contenido");
                    checkbox.setSelected(true);
                    suministroTB.setValidar(checkbox);

                    list.add(suministroTB);
                }
            } catch (SQLException ex) {

            } finally {
                try {
                    if (statementDetalleComra != null) {
                        statementDetalleComra.close();
                    }
                    dbDisconnect();
                } catch (SQLException ex) {

                }
            }
        }
        return list;
    }

    public static String cancelarCompraProducto(String idCompra, ObservableList<DetalleCompraTB> tableView) {
        String result = "";
        DBUtil.dbConnect();
        if (DBUtil.getConnection() == null) {
            return "No se pudo completar la petición por problemas de red, intente nuevamente.";
        }
        PreparedStatement statementValidate = null;
        PreparedStatement statementCompra = null;
        PreparedStatement statementSuministro = null;
        PreparedStatement statementSuministroKardex = null;
        try {
            DBUtil.getConnection().setAutoCommit(false);
            statementValidate = DBUtil.getConnection().prepareStatement("SELECT * FROM CompraTB WHERE IdCompra = ? AND EstadoCompra = ?");
            statementValidate.setString(1, idCompra);
            statementValidate.setInt(2, 3);
            if (statementValidate.executeQuery().next()) {
                DBUtil.getConnection().rollback();
                result = "cancel";
            } else {

                statementCompra = DBUtil.getConnection().prepareStatement("UPDATE CompraTB SET EstadoCompra = ? WHERE IdCompra = ?");
                statementCompra.setInt(1, 3);
                statementCompra.setString(2, idCompra);
                statementCompra.addBatch();

                statementSuministro = DBUtil.getConnection().prepareStatement("UPDATE SuministroTB SET Cantidad = Cantidad - ? WHERE IdSuministro = ?");

                statementSuministroKardex = DBUtil.getConnection().prepareStatement("INSERT INTO KardexSuministroTB("
                        + "IdSuministro,"
                        + "Fecha,"
                        + "Hora,"
                        + "Tipo,"
                        + "Movimiento,"
                        + "Detalle,"
                        + "Cantidad) "
                        + "VALUES(?,?,?,?,?,?,?)");

                for (DetalleCompraTB detalleCompraTB : tableView) {
                    statementSuministro.setDouble(1, detalleCompraTB.getCantidad());
                    statementSuministro.setString(2, detalleCompraTB.getIdArticulo());
                    statementSuministro.addBatch();

                    statementSuministroKardex.setString(1, detalleCompraTB.getIdArticulo());
                    statementSuministroKardex.setString(2, Tools.getDate());
                    statementSuministroKardex.setString(3, Tools.getHour());
                    statementSuministroKardex.setShort(4, (short) 2);
                    statementSuministroKardex.setInt(5, 1);
                    statementSuministroKardex.setString(6, "Cancelar Compra");
                    statementSuministroKardex.setDouble(7, detalleCompraTB.getCantidad());
                    statementSuministroKardex.addBatch();
                }
                

                statementCompra.executeBatch();
                statementSuministro.executeBatch();
                statementSuministroKardex.executeBatch();
                DBUtil.getConnection().commit();
                result = "updated";
            }

        } catch (SQLException ex) {
            try {
                DBUtil.getConnection().rollback();
            } catch (SQLException e) {

            }
            result = ex.getLocalizedMessage();
        } finally {
            try {
                if (statementValidate != null) {
                    statementValidate.close();
                }
                if (statementCompra != null) {
                    statementCompra.close();
                }
                if (statementSuministro != null) {
                    statementSuministro.close();
                }
                if (statementSuministroKardex != null) {
                    statementSuministroKardex.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException ex) {
                result = ex.getLocalizedMessage();
            }
        }

        return result;
    }

    public static String cancelarCompraTotal(String idCompra, ObservableList<DetalleCompraTB> tableView, BancoHistorialTB bancoHistorialBancaria) {
        String result = "";
        DBUtil.dbConnect();
        if (DBUtil.getConnection() != null) {

        }
        PreparedStatement statementValidate = null;
        PreparedStatement statementCompra = null;
        PreparedStatement statementSuministro = null;
        PreparedStatement statementSuministroKardex = null;
        PreparedStatement statementBanco = null;
        PreparedStatement statementBancoHistorial = null;
        try {
            DBUtil.getConnection().setAutoCommit(false);

            statementValidate = DBUtil.getConnection().prepareStatement("SELECT * FROM CompraTB WHERE IdCompra = ? and EstadoCompra = ?");
            statementValidate.setString(1, idCompra);
            statementValidate.setInt(2, 3);
            if (statementValidate.executeQuery().next()) {
                DBUtil.getConnection().rollback();
                return "scrambled";
            } else {
                statementCompra = DBUtil.getConnection().prepareStatement("UPDATE CompraTB SET EstadoCompra = ? WHERE IdCompra = ?");
                statementCompra.setInt(1, 3);
                statementCompra.setString(2, idCompra);
                statementCompra.addBatch();

                statementSuministro = DBUtil.getConnection().prepareStatement("UPDATE SuministroTB SET Cantidad = Cantidad - ? WHERE IdSuministro = ?");

                statementSuministroKardex = DBUtil.getConnection().prepareStatement("INSERT INTO KardexSuministroTB("
                        + "IdSuministro,"
                        + "Fecha,"
                        + "Hora,"
                        + "Tipo,"
                        + "Movimiento,"
                        + "Detalle,"
                        + "Cantidad) "
                        + "VALUES(?,?,?,?,?,?,?)");
                
                for (DetalleCompraTB detalleCompraTB : tableView) {
                    statementSuministro.setDouble(1, detalleCompraTB.getCantidad());
                    statementSuministro.setString(2, detalleCompraTB.getIdArticulo());
                    statementSuministro.addBatch();

                    statementSuministroKardex.setString(1, detalleCompraTB.getIdArticulo());
                    statementSuministroKardex.setString(2, Tools.getDate());
                    statementSuministroKardex.setString(3, Tools.getHour());
                    statementSuministroKardex.setShort(4, (short) 2);
                    statementSuministroKardex.setInt(5, 1);
                    statementSuministroKardex.setString(6, "Cancelar Compra");
                    statementSuministroKardex.setDouble(7, detalleCompraTB.getCantidad());
                    statementSuministroKardex.addBatch();
                }
                
                statementBanco = DBUtil.getConnection().prepareStatement("UPDATE Banco "
                        + "SET SaldoInicial = SaldoInicial + ? "
                        + "WHERE IdBanco = ?");
                
                statementBanco.setDouble(1, bancoHistorialBancaria.getEntrada());
                statementBanco.setString(2, bancoHistorialBancaria.getIdBanco());
                statementBanco.addBatch();
                
                statementBancoHistorial = DBUtil.getConnection().prepareStatement("INSERT INTO BancoHistorialTB"
                        + "(IdBanco,"
                        + "IdEmpleado,"
                        + "IdProcedencia,"
                        + "Descripcion,"
                        + "Fecha,"
                        + "Hora,"
                        + "Entrada,"
                        + "Salida)"
                        + "VALUES(?,?,?,?,?,?,?,?)");
                
                statementBancoHistorial.setString(1, bancoHistorialBancaria.getIdBanco());
                statementBancoHistorial.setString(2, bancoHistorialBancaria.getIdEmpleado());
                statementBancoHistorial.setString(3, "");
                statementBancoHistorial.setString(4, bancoHistorialBancaria.getDescripcion());
                statementBancoHistorial.setString(5, bancoHistorialBancaria.getFecha());
                statementBancoHistorial.setString(6, bancoHistorialBancaria.getHora());
                statementBancoHistorial.setDouble(7, bancoHistorialBancaria.getEntrada());
                statementBancoHistorial.setDouble(8, bancoHistorialBancaria.getSalida());
                statementBancoHistorial.addBatch();

                statementCompra.executeBatch();
                statementSuministro.executeBatch();
                statementSuministroKardex.executeBatch();
                statementBanco.executeBatch();
                statementBancoHistorial.executeBatch();
                DBUtil.getConnection().commit();
                result = "updated";
            }
        } catch (SQLException ex) {
            try {
                DBUtil.getConnection().rollback();
            } catch (SQLException e) {

            }
            result = ex.getLocalizedMessage();
        } finally {
            try {
                if (statementValidate != null) {
                    statementValidate.close();
                }
                if (statementCompra != null) {
                    statementCompra.close();
                }
                if (statementSuministro != null) {
                    statementSuministro.close();
                }
                if (statementSuministroKardex != null) {
                    statementSuministroKardex.close();
                }
                if (statementBanco != null) {
                    statementBanco.close();
                }
                if (statementBancoHistorial != null) {
                    statementBancoHistorial.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException ex) {
                result = ex.getLocalizedMessage();
            }
        }

        return result;
    }

    public static ArrayList<Object> GetComprasForEditar(String idCompra) {
        ArrayList<Object> arrayList = new ArrayList<>();
        DBUtil.dbConnect();
        PreparedStatement statementCompra = null;
        PreparedStatement statementCompraDetalle = null;
        try {
            statementCompra = DBUtil.getConnection().prepareStatement("{call Sp_Compra_For_Editar(?)}");
            statementCompra.setString(1, idCompra);
            ResultSet resultSet = statementCompra.executeQuery();
            if (resultSet.next()) {
                CompraTB compraTB = new CompraTB();
                compraTB.setIdCompra(resultSet.getString("IdCompra"));
                compraTB.setProveedor(resultSet.getString("Proveedor"));
                compraTB.setProveedorTB(new ProveedorTB(resultSet.getString("DatosProveedor")));
                compraTB.setFechaCompra(resultSet.getString("Fecha"));
                compraTB.setSerie(resultSet.getString("Comprobante"));
                compraTB.setNumeracion(resultSet.getString("Numeracion"));
                compraTB.setTipoMoneda(resultSet.getInt("TipoMoneda"));
                compraTB.setObservaciones(resultSet.getString("Observaciones"));
                compraTB.setNotas(resultSet.getString("Notas"));
                arrayList.add(compraTB);
            }
            ObservableList<SuministroTB> list = FXCollections.observableArrayList();
            statementCompraDetalle = DBUtil.getConnection().prepareStatement("{call Sp_Detalle_Compra_For_Editar(?)}");
            statementCompraDetalle.setString(1, idCompra);
            ResultSet resultSet1 = statementCompraDetalle.executeQuery();
            while (resultSet1.next()) {
                SuministroTB suministroTB = new SuministroTB();
                suministroTB.setIdSuministro(resultSet1.getString("IdArticulo"));
                suministroTB.setClave(resultSet1.getString("Clave"));
                suministroTB.setNombreMarca(resultSet1.getString("NombreMarca"));
                suministroTB.setCantidad(resultSet1.getDouble("Cantidad"));
                suministroTB.setDescuento(resultSet1.getDouble("Descuento"));

                double porcentajeDecimal = suministroTB.getDescuento() / 100.00;
                double porcentajeRestante = resultSet1.getDouble("PrecioCompra") * porcentajeDecimal;

                suministroTB.setDescuentoSumado(porcentajeRestante * suministroTB.getCantidad());

                suministroTB.setCostoCompra(resultSet1.getDouble("PrecioCompra") - porcentajeRestante);
                suministroTB.setCostoCompraReal(resultSet1.getDouble("PrecioCompra"));

                suministroTB.setSubImporte(suministroTB.getCantidad() * suministroTB.getCostoCompraReal());
                suministroTB.setTotalImporte(suministroTB.getCantidad() * suministroTB.getCostoCompra());

                suministroTB.setImpuestoArticulo(resultSet1.getInt("IdImpuesto"));
                suministroTB.setImpuestoArticuloName(resultSet1.getString("NombreImpuesto"));
                suministroTB.setImpuestoValor(resultSet1.getDouble("ValorImpuesto"));
                suministroTB.setImpuestoSumado(suministroTB.getCantidad() * (suministroTB.getCostoCompra() * (suministroTB.getImpuestoValor() / 100.00)));

                suministroTB.setPrecioVentaGeneral(resultSet1.getDouble("PrecioVenta1"));
                suministroTB.setPrecioMargenGeneral(resultSet1.getShort("Margen1"));
                suministroTB.setPrecioUtilidadGeneral(resultSet1.getDouble("Utilidad1"));

                suministroTB.setDescripcion(resultSet1.getString("Descripcion"));
                list.add(suministroTB);
            }
            arrayList.add(list);
        } catch (SQLException ex) {
            System.out.println("Compras ADO:" + ex.getLocalizedMessage());
        } finally {
            try {
                if (statementCompra != null) {
                    statementCompra.close();
                }
                if (statementCompraDetalle != null) {
                    statementCompraDetalle.close();
                }
            } catch (SQLException ex) {

            }
        }
        return arrayList;
    }

    public static ObservableList<CompraCreditoTB> Listar_Compra_Credito(String value) {
        PreparedStatement preparedCompraCredito = null;
        ObservableList<CompraCreditoTB> empList = FXCollections.observableArrayList();
        try {
            dbConnect();
            preparedCompraCredito = getConnection().prepareStatement("{call Sp__Listar_Compra_Credito_Abonar_Por_IdCompra(?)}");
            preparedCompraCredito.setString(1, value);
            try (ResultSet rsEmps = preparedCompraCredito.executeQuery()) {
                while (rsEmps.next()) {
                    CompraCreditoTB compraCreditoTB = new CompraCreditoTB();
                    compraCreditoTB.setId(rsEmps.getRow());
                    compraCreditoTB.setIdCompraCredito(rsEmps.getInt("IdCompraCredito"));
                    compraCreditoTB.setCuota("Nro." + (rsEmps.getRow() < 10 ? "00" + rsEmps.getRow() : (rsEmps.getRow() >= 10 && rsEmps.getRow() <= 99 ? "0" + rsEmps.getRow() : rsEmps.getRow() + "")));
                    compraCreditoTB.setMonto(rsEmps.getDouble("Monto"));
                    compraCreditoTB.setFechaRegistro(rsEmps.getDate("FechaRegistro").toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    compraCreditoTB.setFechaPago(rsEmps.getDate("FechaPago").toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    compraCreditoTB.setEstado(rsEmps.getBoolean("Estado"));
                    Text txtEstado = new Text(compraCreditoTB.isEstado() ? "PAGADO" : "PENDIENTE");
                    txtEstado.setFill(Color.web(compraCreditoTB.isEstado() ? "#04a421" : "#d32300"));
                    txtEstado.getStyleClass().add("labelRobotoBold14");
                    compraCreditoTB.setTxtEstado(txtEstado);
                    CheckBox cbSeleccion = new CheckBox();
                    cbSeleccion.getStyleClass().add("check-box-contenido");
                    cbSeleccion.setDisable(compraCreditoTB.isEstado());
                    cbSeleccion.setSelected(compraCreditoTB.isEstado());
                    compraCreditoTB.setCbSeleccion(cbSeleccion);
                    empList.add(compraCreditoTB);
                }
            }
        } catch (SQLException e) {
            System.out.println("Listar_Compra_Credito - La operación de selección de SQL ha fallado: " + e);
        } finally {
            try {
                if (preparedCompraCredito != null) {
                    preparedCompraCredito.close();
                }
                if (preparedCompraCredito != null) {
                    preparedCompraCredito.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException ex) {

            }
        }
        return empList;
    }

    public static ProveedorTB Obtener_Proveedor_Por_Id_Compra(String value) {
        ProveedorTB proveedorTB = null;
        dbConnect();
        if (getConnection() != null) {
            PreparedStatement preparedCompraProveedor = null;

            try {
                preparedCompraProveedor = getConnection().prepareStatement("select dbo.Fc_Obtener_Nombre_Detalle(p.TipoDocumento,'0003') as NombreDocumento,p.NumeroDocumento,p.RazonSocial,p.Direccion,p.Telefono,p.Celular,p.Email from CompraTB as c inner join ProveedorTB as p on c.Proveedor = p.IdProveedor where c.IdCompra = ?");
                preparedCompraProveedor.setString(1, value);
                try (ResultSet resultSet = preparedCompraProveedor.executeQuery()) {
                    if (resultSet.next()) {
                        proveedorTB = new ProveedorTB();
                        proveedorTB.setTipoDocumentoName(resultSet.getString("NombreDocumento"));
                        proveedorTB.setNumeroDocumento(resultSet.getString("NumeroDocumento"));
                        proveedorTB.setRazonSocial(resultSet.getString("RazonSocial"));
                        proveedorTB.setDireccion(resultSet.getString("Direccion"));
                        proveedorTB.setTelefono(resultSet.getString("Telefono"));
                        proveedorTB.setCelular(resultSet.getString("Celular"));
                        proveedorTB.setEmail(resultSet.getString("Email"));
                    }
                }
            } catch (SQLException ex) {

            } finally {
                try {
                    if (preparedCompraProveedor != null) {
                        preparedCompraProveedor.close();
                    }
                } catch (SQLException ex) {

                }
            }
        }
        return proveedorTB;
    }

    public static String Registrar_Amortizacion(TableView<CompraCreditoTB> tvList, BancoHistorialTB bancoHistorialTB) {
        String result = "";
        dbConnect();
        if (getConnection() != null) {
            CallableStatement codigoBancoHistorial = null;
            PreparedStatement preparedCompraCredito = null;
            PreparedStatement preparedBanco = null;
            PreparedStatement preparedBancoHistorial = null;
            try {
                getConnection().setAutoCommit(false);

                codigoBancoHistorial = DBUtil.getConnection().prepareCall("{? = call Fc_Banco_Historial_Codigo_Alfanumerico()}");
                codigoBancoHistorial.registerOutParameter(1, java.sql.Types.VARCHAR);
                codigoBancoHistorial.execute();
                String idBancoHistorial = codigoBancoHistorial.getString(1);

                preparedBanco = getConnection().prepareStatement("UPDATE Banco SET SaldoInicial = SaldoInicial - ? WHERE IdBanco = ?");
                preparedBanco.setDouble(1, bancoHistorialTB.getSalida());
                preparedBanco.setString(2, bancoHistorialTB.getIdBanco());
                preparedBanco.addBatch();

                preparedBancoHistorial = getConnection().prepareStatement("INSERT INTO BancoHistorialTB(IdBanco,IdBancoHistorial,IdProcedencia,Descripcion,Fecha,Hora,Entrada,Salida)VALUES(?,?,?,?,?,?,?,?)");
                preparedBancoHistorial.setString(1, bancoHistorialTB.getIdBanco());
                preparedBancoHistorial.setString(2, idBancoHistorial);
                preparedBancoHistorial.setString(3, "");
                preparedBancoHistorial.setString(4, bancoHistorialTB.getDescripcion());
                preparedBancoHistorial.setString(5, bancoHistorialTB.getFecha());
                preparedBancoHistorial.setString(6, bancoHistorialTB.getHora());
                preparedBancoHistorial.setDouble(7, bancoHistorialTB.getEntrada());
                preparedBancoHistorial.setDouble(8, bancoHistorialTB.getSalida());
                preparedBancoHistorial.addBatch();

                preparedCompraCredito = DBUtil.getConnection().prepareStatement("UPDATE CompraCreditoTB SET FechaPago = ?,HoraPago = ?,Estado = ?  WHERE IdCompraCredito = ?");
                for (CompraCreditoTB cctb : tvList.getItems()) {
                    if (cctb.getCbSeleccion().isSelected() && !cctb.getCbSeleccion().isDisable()) {
                        preparedCompraCredito.setString(1, cctb.getFechaPago());
                        preparedCompraCredito.setString(2, cctb.getHoraPago());
                        preparedCompraCredito.setBoolean(3, cctb.isEstado());
                        preparedCompraCredito.setInt(4, cctb.getIdCompraCredito());
                        preparedCompraCredito.addBatch();
                    }
                }

                preparedCompraCredito.executeBatch();
                preparedBanco.executeBatch();
                preparedBancoHistorial.executeBatch();
                DBUtil.getConnection().commit();
                result = "updated";
            } catch (SQLException e) {
                try {
                    getConnection().rollback();
                } catch (SQLException ex) {

                }
                result = e.getLocalizedMessage();
            } finally {
                try {
                    if (codigoBancoHistorial != null) {
                        codigoBancoHistorial.close();
                    }
                    if (preparedCompraCredito != null) {
                        preparedCompraCredito.close();
                    }
                    if (preparedBanco != null) {
                        preparedBanco.close();
                    }
                    if (preparedBancoHistorial != null) {
                        preparedBancoHistorial.close();
                    }
                    dbDisconnect();
                } catch (SQLException ex) {
                    result = ex.getLocalizedMessage();
                }
            }
        } else {
            result = "No se pudo conectar el servidor, intente nuevamente.";
        }
        return result;
    }

    public static String Actualizar_Compra_Estado(String idCompra) {
        String result = "";
        dbConnect();
        if (getConnection() != null) {
            PreparedStatement preparedCompra = null;
            try {
                getConnection().setAutoCommit(false);
                preparedCompra = getConnection().prepareStatement("UPDATE CompraTB SET EstadoCompra = ? WHERE IdCompra = ?");
                preparedCompra.setInt(1, 1);
                preparedCompra.setString(2, idCompra);
                preparedCompra.addBatch();
                preparedCompra.executeBatch();
                getConnection().commit();
                result = "updated";
            } catch (SQLException e) {
                try {
                    getConnection().rollback();
                } catch (SQLException ex) {

                }
                result = e.getLocalizedMessage();
            } finally {
                try {
                    if (preparedCompra != null) {
                        preparedCompra.close();
                    }
                    dbDisconnect();
                } catch (SQLException ex) {
                    result = ex.getLocalizedMessage();
                }
            }
        } else {
            result = "No se pudo conectar el servidor, intente nuevamente.";
        }
        return result;
    }

    public static ArrayList<CompraTB> GetReporteGenetalCompras(String fechaInicial, String fechaFinal, String idProveedor, int tipoCompra) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ArrayList<CompraTB> arrayList = new ArrayList<>();
        try {
            DBUtil.dbConnect();
            preparedStatement = DBUtil.getConnection().prepareCall("{call Sp_Reporte_General_Compras(?,?,?,?)}");
            preparedStatement.setString(1, fechaInicial);
            preparedStatement.setString(2, fechaFinal);
            preparedStatement.setString(3, idProveedor);
            preparedStatement.setInt(4, tipoCompra);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                CompraTB compraTB = new CompraTB();
                compraTB.setFechaCompra(resultSet.getDate("FechaCompra").toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                compraTB.setProveedor(resultSet.getString("Proveedor").toUpperCase());
                compraTB.setSerie(resultSet.getString("Serie").toUpperCase());
                compraTB.setNumeracion(resultSet.getString("Numeracion"));
                compraTB.setTipoName(resultSet.getString("Tipo"));
                compraTB.setEstado(resultSet.getInt("EstadoCompra"));
                compraTB.setEstadoName(resultSet.getString("EstadoName"));
                compraTB.setTipoMonedaName(resultSet.getString("Simbolo"));
                compraTB.setTotal(resultSet.getDouble("Total"));
                arrayList.add(compraTB);
            }
        } catch (SQLException ex) {
            System.out.println("Compra ADO GetReporteGenetalCompras():" + ex.getLocalizedMessage());
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

}
