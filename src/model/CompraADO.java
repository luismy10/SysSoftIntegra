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
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class CompraADO extends DBUtil {

    public static String Compra_Contado(BancoHistorialTB bancoHistorialTB, CompraTB compraTB, TableView<DetalleCompraTB> tableView, ObservableList<LoteTB> loteTBs) {

        CallableStatement codigo_compra = null;
        PreparedStatement compra = null;
        PreparedStatement detalle_compra = null;
        PreparedStatement suministro_precios_remover = null;
        PreparedStatement suministro_precios_insertar = null;
        PreparedStatement suministro_update = null;
        PreparedStatement suministro_kardex = null;
        //PreparedStatement lote_compra = null;

        PreparedStatement preparedBanco = null;
        PreparedStatement preparedBancoHistorial = null;

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
                        + "Serie,"
                        + "Numeracion,"
                        + "TipoMoneda,"
                        + "FechaCompra,"
                        + "HoraCompra,"
                        + "FechaVencimiento,"
                        + "HoraVencimiento,"
                        + "Observaciones,"
                        + "Notas,"
                        + "TipoCompra,"
                        + "EstadoCompra,"
                        + "Usuario) "
                        + "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

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
                        + "Importe,"
                        + "Descripcion)"
                        + "VALUES(?,?,?,?,?,?,?,?,?,?)");

                suministro_precios_remover = getConnection().prepareStatement("DELETE FROM PreciosTB WHERE IdSuministro = ?");

                suministro_precios_insertar = getConnection().prepareStatement("INSERT INTO PreciosTB(IdArticulo, IdSuministro, Nombre, Valor, Factor,Estado) VALUES(?,?,?,?,?,?)");

                suministro_update = getConnection().prepareStatement("UPDATE SuministroTB SET Cantidad = Cantidad + ?,PrecioCompra = ?,Impuesto=?,PrecioVentaGeneral=? WHERE IdSuministro = ?");

                suministro_kardex = getConnection().prepareStatement("INSERT INTO "
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
                compra.setString(2, compraTB.getIdProveedor());
                compra.setInt(3, compraTB.getTipoDocumento());
                compra.setString(4, compraTB.getSerie());
                compra.setString(5, compraTB.getNumeracion());
                compra.setInt(6, compraTB.getIdMoneda());
                compra.setString(7, compraTB.getFechaCompra());
                compra.setString(8, compraTB.getHoraCompra());
                compra.setString(9, compraTB.getFechaVencimiento());
                compra.setString(10, compraTB.getHoraVencimiento());
                compra.setString(11, compraTB.getObservaciones());
                compra.setString(12, compraTB.getNotas());
                compra.setInt(13, compraTB.getTipo());
                compra.setInt(14, compraTB.getEstado());
                compra.setString(15, compraTB.getUsuario());
                compra.addBatch();

                for (DetalleCompraTB dc : tableView.getItems()) {
                    detalle_compra.setString(1, id_compra);
                    detalle_compra.setString(2, dc.getIdArticulo());
                    detalle_compra.setDouble(3, dc.getCantidad());
                    detalle_compra.setDouble(4, dc.getPrecioCompra());
                    detalle_compra.setDouble(5, dc.getDescuento());

                    detalle_compra.setInt(6, dc.getIdImpuesto());
                    detalle_compra.setString(7, dc.getNombreImpuesto());
                    detalle_compra.setDouble(8, dc.getValorImpuesto());
                    detalle_compra.setDouble(9, dc.getImporte());
                    detalle_compra.setString(10, dc.getDescripcion());
                    detalle_compra.addBatch();

                    suministro_update.setDouble(1, dc.getCantidad());
                    suministro_update.setDouble(2, dc.getPrecioCompra());

                    suministro_update.setInt(3, dc.getSuministroTB().getImpuestoId());
                    suministro_update.setDouble(4, dc.getSuministroTB().getPrecioVentaGeneral());
                    suministro_update.setString(5, dc.getIdArticulo());
                    suministro_update.addBatch();

                    suministro_kardex.setString(1, dc.getIdArticulo());
                    suministro_kardex.setString(2, Tools.getDate());
                    suministro_kardex.setString(3, Tools.getHour());
                    suministro_kardex.setShort(4, (short) 1);
                    suministro_kardex.setInt(5, 2);
                    suministro_kardex.setString(6, "COMPRA CON SERIE Y NUMERACIÓN: " + compraTB.getSerie().toUpperCase() + "-" + compraTB.getNumeracion().toUpperCase());
                    suministro_kardex.setDouble(7, dc.getCantidad());
                    suministro_kardex.setDouble(8, dc.getPrecioCompra());
                    suministro_kardex.setDouble(9, dc.getCantidad() * dc.getPrecioCompra());
                    suministro_kardex.addBatch();

                    suministro_precios_remover.setString(1, dc.getIdArticulo());
                    suministro_precios_remover.addBatch();

                    for (PreciosTB t : dc.getListPrecios()) {
                        suministro_precios_insertar.setString(1, "");
                        suministro_precios_insertar.setString(2, dc.getIdArticulo());
                        suministro_precios_insertar.setString(3, t.getNombre());
                        suministro_precios_insertar.setDouble(4, t.getValor());
                        suministro_precios_insertar.setDouble(5, t.getFactor() <= 0 ? 1 : t.getFactor());
                        suministro_precios_insertar.setBoolean(6, true);
                        suministro_precios_insertar.addBatch();
                    }
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
                suministro_precios_remover.executeBatch();
                suministro_precios_insertar.executeBatch();
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
                    if (suministro_precios_remover != null) {
                        suministro_precios_remover.close();
                    }
                    if (suministro_precios_insertar != null) {
                        suministro_precios_insertar.close();
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

    public static String Compra_Credito(CompraTB compraTB, TableView<DetalleCompraTB> tableView, ObservableList<LoteTB> loteTBs) {
        CallableStatement codigo_compra = null;
        CallableStatement codigo_credito = null;
        PreparedStatement compra = null;
        PreparedStatement detalle_compra = null;
        PreparedStatement suministro_precios_remover = null;
        PreparedStatement suministro_precios_insertar = null;
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
                        + "Comprobante,"
                        + "Serie,"
                        + "Numeracion,"
                        + "TipoMoneda,"
                        + "FechaCompra,"
                        + "HoraCompra,"
                        + "FechaVencimiento,"
                        + "HoraVencimiento,"
                        + "Observaciones,"
                        + "Notas,"
                        + "TipoCompra,"
                        + "EstadoCompra,"
                        + "Usuario) "
                        + "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

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
                        + "Importe,"
                        + "Descripcion)"
                        + "VALUES(?,?,?,?,?,?,?,?,?,?)");

                suministro_precios_remover = getConnection().prepareStatement("DELETE FROM PreciosTB WHERE IdSuministro = ?");

                suministro_precios_insertar = getConnection().prepareStatement("INSERT INTO PreciosTB(IdArticulo, IdSuministro, Nombre, Valor, Factor,Estado) VALUES(?,?,?,?,?,?)");

                suministro_update = getConnection().prepareStatement("UPDATE SuministroTB SET Cantidad = Cantidad + ?,PrecioCompra = ?,Impuesto=?,PrecioVentaGeneral=? WHERE IdSuministro = ?");

                suministro_kardex = getConnection().prepareStatement("INSERT INTO "
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
                compra.setString(2, compraTB.getIdProveedor());
                compra.setInt(3, compraTB.getTipoDocumento());
                compra.setString(4, compraTB.getSerie());
                compra.setString(5, compraTB.getNumeracion());
                compra.setInt(6, compraTB.getIdMoneda());
                compra.setString(7, compraTB.getFechaCompra());
                compra.setString(8, compraTB.getHoraCompra());
                compra.setString(9, compraTB.getFechaVencimiento());
                compra.setString(10, compraTB.getHoraVencimiento());
                compra.setString(11, compraTB.getObservaciones());
                compra.setString(12, compraTB.getNotas());
                compra.setInt(13, compraTB.getTipo());
                compra.setInt(14, compraTB.getEstado());
                compra.setString(15, compraTB.getUsuario());
                compra.addBatch();

                for (DetalleCompraTB dc : tableView.getItems()) {
                    detalle_compra.setString(1, id_compra);
                    detalle_compra.setString(2, dc.getIdArticulo());
                    detalle_compra.setDouble(3, dc.getCantidad());
                    detalle_compra.setDouble(4, dc.getPrecioCompra());
                    detalle_compra.setDouble(5, dc.getDescuento());

                    detalle_compra.setInt(6, dc.getIdImpuesto());
                    detalle_compra.setString(7, dc.getNombreImpuesto());
                    detalle_compra.setDouble(8, dc.getValorImpuesto());
                    detalle_compra.setDouble(9, dc.getImporte());
                    detalle_compra.setString(10, dc.getDescripcion());
                    detalle_compra.addBatch();

                    suministro_update.setDouble(1, dc.getCantidad());
                    suministro_update.setDouble(2, dc.getPrecioCompra());
                    suministro_update.setInt(3, dc.getSuministroTB().getImpuestoId());
                    suministro_update.setDouble(4, dc.getSuministroTB().getPrecioVentaGeneral());
                    suministro_update.setString(5, dc.getIdArticulo());
                    suministro_update.addBatch();

                    suministro_kardex.setString(1, dc.getIdArticulo());
                    suministro_kardex.setString(2, Tools.getDate());
                    suministro_kardex.setString(3, Tools.getHour());
                    suministro_kardex.setShort(4, (short) 1);
                    suministro_kardex.setInt(5, 2);
                    suministro_kardex.setString(6, "COMPRA CON SERIE Y NUMERACIÓN: " + compraTB.getSerie().toUpperCase() + "-" + compraTB.getNumeracion().toUpperCase());
                    suministro_kardex.setDouble(7, dc.getCantidad());
                    suministro_kardex.setDouble(8, dc.getPrecioCompra());
                    suministro_kardex.setDouble(9, dc.getCantidad() * dc.getPrecioCompra());
                    suministro_kardex.addBatch();

                    suministro_precios_remover.setString(1, dc.getIdArticulo());
                    suministro_precios_remover.addBatch();

                    for (PreciosTB t : dc.getListPrecios()) {
                        suministro_precios_insertar.setString(1, "");
                        suministro_precios_insertar.setString(2, dc.getIdArticulo());
                        suministro_precios_insertar.setString(3, t.getNombre());
                        suministro_precios_insertar.setDouble(4, t.getValor());
                        suministro_precios_insertar.setDouble(5, t.getFactor() <= 0 ? 1 : t.getFactor());
                        suministro_precios_insertar.setBoolean(6, true);
                        suministro_precios_insertar.addBatch();
                    }
                }

                compra.executeBatch();
                detalle_compra.executeBatch();
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
                    if (suministro_precios_remover != null) {
                        suministro_precios_remover.close();
                    }
                    if (suministro_precios_insertar != null) {
                        suministro_precios_insertar.close();
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
                compra.setString(2, compraTB.getIdProveedor());
                compra.setString(3, compraTB.getSerie());
                compra.setString(4, compraTB.getNumeracion());
                compra.setInt(5, compraTB.getIdMoneda());
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
                compraTB.setId(rsEmps.getRow());
                compraTB.setIdCompra(rsEmps.getString("IdCompra"));
                compraTB.setFechaCompra(rsEmps.getString("FechaCompra"));
                compraTB.setHoraCompra(rsEmps.getTime("HoraCompra").toLocalTime().format(DateTimeFormatter.ofPattern("hh:mm:ss a")));
                compraTB.setSerie(rsEmps.getString("Serie"));
                compraTB.setNumeracion(rsEmps.getString("Numeracion"));
                compraTB.setProveedorTB(new ProveedorTB(rsEmps.getString("NumeroDocumento"), rsEmps.getString("RazonSocial")));

                compraTB.setTipo(rsEmps.getInt("TipoCompra"));
                compraTB.setTipoName(rsEmps.getString("Tipo"));
                Label lblTipo = new Label(compraTB.getTipoName());
                lblTipo.getStyleClass().add("labelRobotoBold13");
                lblTipo.setStyle("-fx-text-fill:" + (compraTB.getTipo() == 2 ? "#ff0202" : "#020203"));
                compraTB.setTipoLabel(lblTipo);

                compraTB.setEstado(rsEmps.getInt("EstadoCompra"));
                compraTB.setEstadoName(rsEmps.getString("Estado"));
                Label label = new Label(compraTB.getEstadoName());
                label.getStyleClass().add(compraTB.getEstado() == 1 ? "label-asignacion" : compraTB.getEstado() == 2 ? "label-medio" : compraTB.getEstado() == 3 ? "label-proceso" : "label-ultimo");

                compraTB.setEstadoLabel(label);
                compraTB.setMonedaNombre(rsEmps.getString("Simbolo"));
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
                    compraTB.setFechaCompra(resultSet.getDate("FechaCompra").toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    compraTB.setHoraCompra(resultSet.getTime("HoraCompra").toLocalTime().format(DateTimeFormatter.ofPattern("hh:mm:ss a")));
                    compraTB.setSerie(resultSet.getString("Serie").toUpperCase());
                    compraTB.setNumeracion(resultSet.getString("Numeracion"));
                    compraTB.setMonedaNombre(resultSet.getString("Simbolo"));
                    compraTB.setTipoName(resultSet.getString("Tipo"));
                    compraTB.setEstado(resultSet.getInt("EstadoCompra"));
                    compraTB.setEstadoName(resultSet.getString("Estado"));
                    compraTB.setTotal(resultSet.getDouble("Total"));
                    compraTB.setObservaciones(resultSet.getString("Observaciones"));
                    compraTB.setNotas(resultSet.getString("Notas"));

                    ProveedorTB proveedorTB = new ProveedorTB();
                    proveedorTB.setNumeroDocumento(resultSet.getString("NumeroDocumento"));
                    proveedorTB.setRazonSocial(resultSet.getString("RazonSocial"));
                    proveedorTB.setTelefono(resultSet.getString("Telefono"));
                    proveedorTB.setCelular(resultSet.getString("Celular"));
                    proveedorTB.setDireccion(resultSet.getString("Direccion"));
                    proveedorTB.setEmail(resultSet.getString("Email"));
                    compraTB.setProveedorTB(proveedorTB);

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

                    double valor_sin_impuesto = detalleCompraTB.getPrecioCompra() / ((detalleCompraTB.getValorImpuesto() / 100.00) + 1);
                    double descuento = detalleCompraTB.getDescuento();
                    double porcentajeRestante = valor_sin_impuesto * (descuento / 100.00);
                    double preciocalculado = valor_sin_impuesto - porcentajeRestante;

                    detalleCompraTB.setDescuento(descuento);
                    detalleCompraTB.setDescuentoSumado(descuento * detalleCompraTB.getCantidad());

                    detalleCompraTB.setPrecioCompraUnico(valor_sin_impuesto);
                    detalleCompraTB.setPrecioCompraReal(preciocalculado);

                    double impuesto = Tools.calculateTax(detalleCompraTB.getValorImpuesto(), detalleCompraTB.getPrecioCompraReal());
                    detalleCompraTB.setImpuestoSumado(detalleCompraTB.getCantidad() * impuesto);
                    detalleCompraTB.setPrecioCompra(detalleCompraTB.getPrecioCompraReal() + impuesto);

                    detalleCompraTB.setImporte(detalleCompraTB.getPrecioCompra() * detalleCompraTB.getCantidad());

                    empList.add(detalleCompraTB);
                }
                objects.add(empList);

                preparedCredito = getConnection().prepareStatement("{call Sp_Listar_Detalle_Compra_Credito(?)}");
                preparedCredito.setString(1, idCompra);
                ResultSet rsCredito = preparedCredito.executeQuery();
                ArrayList<CompraCreditoTB> listComprasCredito = new ArrayList();
                while (rsCredito.next()) {
                    CompraCreditoTB creditoTB = new CompraCreditoTB();
                    creditoTB.setMonto(rsCredito.getDouble("Monto"));
                    creditoTB.setFechaPago(rsCredito.getDate("FechaPago").toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    creditoTB.setHoraPago(rsCredito.getTime("HoraPago").toLocalTime().format(DateTimeFormatter.ofPattern("hh:mm:ss a")));
                    creditoTB.setEstado(rsCredito.getBoolean("Estado"));
                    listComprasCredito.add(creditoTB);
                }
                objects.add(listComprasCredito);
            } catch (SQLException ex) {
                Tools.println("Error en listar compra: " + ex.getLocalizedMessage());
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
                    compraTB.setIdProveedor(resultSet.getString("RazonSocial"));
                    compraTB.setMonedaNombre(resultSet.getString("Simbolo"));
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
                        + "Cantidad, "
                        + "Costo, "
                        + "Total) "
                        + "VALUES(?,?,?,?,?,?,?,?,?)");

                for (DetalleCompraTB detalleCompraTB : tableView) {
                    statementSuministro.setDouble(1, detalleCompraTB.getCantidad());
                    statementSuministro.setString(2, detalleCompraTB.getIdArticulo());
                    statementSuministro.addBatch();

                    statementSuministroKardex.setString(1, detalleCompraTB.getIdArticulo());
                    statementSuministroKardex.setString(2, Tools.getDate());
                    statementSuministroKardex.setString(3, Tools.getHour());
                    statementSuministroKardex.setShort(4, (short) 2);
                    statementSuministroKardex.setInt(5, 1);
                    statementSuministroKardex.setString(6, "CANCELAR COMPRA");
                    statementSuministroKardex.setDouble(7, detalleCompraTB.getCantidad());
                    statementSuministroKardex.setDouble(8, detalleCompraTB.getPrecioCompra());
                    statementSuministroKardex.setDouble(9, detalleCompraTB.getCantidad() * detalleCompraTB.getPrecioCompra());
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
            return "No se pudo completar la petición por problemas de red, intente nuevamente.";
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
                result = "scrambled";
            } else {
                statementValidate = DBUtil.getConnection().prepareStatement("SELECT * FROM CompraCreditoTB WHERE IdCompra = ? and Estado = 1");
                statementValidate.setString(1, result);
                if (statementValidate.executeQuery().next()) {
                    DBUtil.getConnection().rollback();
                    result = "historial";
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
                            + "Cantidad, "
                            + "Costo, "
                            + "Total) "
                            + "VALUES(?,?,?,?,?,?,?,?,?)");

                    for (DetalleCompraTB detalleCompraTB : tableView) {
                        statementSuministro.setDouble(1, detalleCompraTB.getCantidad());
                        statementSuministro.setString(2, detalleCompraTB.getIdArticulo());
                        statementSuministro.addBatch();

                        statementSuministroKardex.setString(1, detalleCompraTB.getIdArticulo());
                        statementSuministroKardex.setString(2, Tools.getDate());
                        statementSuministroKardex.setString(3, Tools.getHour());
                        statementSuministroKardex.setShort(4, (short) 2);
                        statementSuministroKardex.setInt(5, 1);
                        statementSuministroKardex.setString(6, "CANCELAR COMPRA");
                        statementSuministroKardex.setDouble(7, detalleCompraTB.getCantidad());
                        statementSuministroKardex.setDouble(8, detalleCompraTB.getPrecioCompra());
                        statementSuministroKardex.setDouble(9, detalleCompraTB.getCantidad() * detalleCompraTB.getPrecioCompra());
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
                compraTB.setIdProveedor(resultSet.getString("Proveedor"));
                compraTB.setProveedorTB(new ProveedorTB(resultSet.getString("DatosProveedor")));
                compraTB.setFechaCompra(resultSet.getString("Fecha"));
                compraTB.setSerie(resultSet.getString("Comprobante"));
                compraTB.setNumeracion(resultSet.getString("Numeracion"));
//                compraTB.setMonedaNombre(resultSet.getInt("TipoMoneda"));
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

                suministroTB.setImporteBruto(suministroTB.getCantidad() * suministroTB.getCostoCompraReal());
                suministroTB.setImporteNeto(suministroTB.getCantidad() * suministroTB.getCostoCompra());

                suministroTB.setImpuestoId(resultSet1.getInt("IdImpuesto"));
                suministroTB.setImpuestoNombre(resultSet1.getString("NombreImpuesto"));
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

    public static Object Listar_Compra_Credito(String idCompra) {
        PreparedStatement preparedProveedor = null;
        PreparedStatement preparedCompraCredito = null;
        PreparedStatement preparedDetalleCompra = null;
        ResultSet resultSet = null;
        try {
            dbConnect();
            preparedProveedor = getConnection().prepareStatement("{call Sp_Obtener_Compra_ById_For_Credito(?)}");
            preparedProveedor.setString(1, idCompra);
            resultSet = preparedProveedor.executeQuery();
            if (resultSet.next()) {
                CompraTB compraTB = new CompraTB();
                compraTB.setIdCompra(idCompra);
                compraTB.setSerie(resultSet.getString("Serie"));
                compraTB.setNumeracion(resultSet.getString("Numeracion"));
                compraTB.setEstado(resultSet.getInt("EstadoCompra"));
                compraTB.setEstadoName(resultSet.getString("Estado"));
                compraTB.setMontoTotal(resultSet.getDouble("MontoTotal"));
                compraTB.setMontoPagado(resultSet.getDouble("MontoPagado"));
                compraTB.setMontoRestante(compraTB.getMontoTotal() - compraTB.getMontoPagado());

                ProveedorTB proveedorTB = new ProveedorTB();
                proveedorTB.setNumeroDocumento(resultSet.getString("NumeroDocumento"));
                proveedorTB.setRazonSocial(resultSet.getString("RazonSocial"));
                proveedorTB.setTelefono(resultSet.getString("Telefono"));
                proveedorTB.setCelular(resultSet.getString("Celular"));
                proveedorTB.setDireccion(resultSet.getString("Direccion"));
                proveedorTB.setEmail(resultSet.getString("Email"));
                compraTB.setProveedorTB(proveedorTB);

                preparedCompraCredito = getConnection().prepareStatement("{call Sp_Listar_Detalle_Compra_Credito(?)}");
                preparedCompraCredito.setString(1, idCompra);

                resultSet = preparedCompraCredito.executeQuery();
                ArrayList<CompraCreditoTB> ventaCreditoTBs = new ArrayList<>();
                while (resultSet.next()) {
                    CompraCreditoTB compraCreditoTB = new CompraCreditoTB();
                    compraCreditoTB.setId(resultSet.getRow());
                    compraCreditoTB.setIdCompraCredito(resultSet.getString("IdCompraCredito"));
                    compraCreditoTB.setFechaPago(resultSet.getDate("FechaPago").toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    compraCreditoTB.setHoraPago(resultSet.getTime("HoraPago").toLocalTime().format(DateTimeFormatter.ofPattern("hh:mm:ss a")));
                    compraCreditoTB.setEstado(resultSet.getBoolean("Estado"));
                    compraCreditoTB.setObservacion(resultSet.getString("Observacion"));
                    compraCreditoTB.setMonto(resultSet.getDouble("Monto"));

                    Button btnImprimir = new Button();
                    ImageView imageViewVisualizar = new ImageView(new Image("/view/image/print.png"));
                    imageViewVisualizar.setFitWidth(24);
                    imageViewVisualizar.setFitHeight(24);
                    btnImprimir.setGraphic(imageViewVisualizar);
                    btnImprimir.getStyleClass().add("buttonLightSuccess");
                    compraCreditoTB.setBtnImprimir(btnImprimir);

                    ventaCreditoTBs.add(compraCreditoTB);
                }
                compraTB.setCompraCreditoTBs(ventaCreditoTBs);

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

                    double valor_sin_impuesto = detalleCompraTB.getPrecioCompra() / ((detalleCompraTB.getValorImpuesto() / 100.00) + 1);
                    double descuento = detalleCompraTB.getDescuento();
                    double porcentajeRestante = valor_sin_impuesto * (descuento / 100.00);
                    double preciocalculado = valor_sin_impuesto - porcentajeRestante;

                    detalleCompraTB.setDescuento(descuento);
                    detalleCompraTB.setDescuentoSumado(descuento * detalleCompraTB.getCantidad());

                    detalleCompraTB.setPrecioCompraUnico(valor_sin_impuesto);
                    detalleCompraTB.setPrecioCompraReal(preciocalculado);

                    double impuesto = Tools.calculateTax(detalleCompraTB.getValorImpuesto(), detalleCompraTB.getPrecioCompraReal());
                    detalleCompraTB.setImpuestoSumado(detalleCompraTB.getCantidad() * impuesto);
                    detalleCompraTB.setPrecioCompra(detalleCompraTB.getPrecioCompraReal() + impuesto);

                    detalleCompraTB.setImporte(detalleCompraTB.getPrecioCompra() * detalleCompraTB.getCantidad());

                    empList.add(detalleCompraTB);
                }
                compraTB.setDetalleCompraTBs(empList);
                
                return compraTB;
            } else {
                throw new Exception("No se pudo carga la información, intente nuevamente.");
            }
        } catch (SQLException ex) {
            return ex.getLocalizedMessage();
        } catch (Exception ex) {
            return ex.getLocalizedMessage();
        } finally {
            try {
                if (preparedCompraCredito != null) {
                    preparedCompraCredito.close();
                }
                if (resultSet != null) {
                    resultSet.close();
                }
                if (preparedProveedor != null) {
                    preparedProveedor.close();
                }
                if (preparedDetalleCompra != null) {
                    preparedDetalleCompra.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException ex) {
                return ex.getLocalizedMessage();
            }
        }
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

    public static ModeloObject Registrar_Amortizacion(CompraCreditoTB compraCreditoTB, BancoHistorialTB bancoHistorialTB, MovimientoCajaTB movimientoCajaTB) {
        ModeloObject result = new ModeloObject();

        dbConnect();
        if (getConnection() != null) {
            PreparedStatement statementValidate = null;
            PreparedStatement statementCompra = null;
            CallableStatement callableIdCompraCredito = null;
            PreparedStatement preparedBanco = null;
            PreparedStatement preparedBancoHistorial = null;
            PreparedStatement preparedCompraCredito = null;
            PreparedStatement movimiento_caja = null;
            try {
                getConnection().setAutoCommit(false);

                statementValidate = DBUtil.getConnection().prepareStatement("SELECT sum(dc.Cantidad * (dc.PrecioCompra-dc.Descuento)) as Total FROM CompraTB AS c \n"
                        + "INNER JOIN DetalleCompraTB AS dc ON dc.IdCompra = c.IdCompra\n"
                        + "where c.IdCompra = ?");
                statementValidate.setString(1, compraCreditoTB.getIdCompra());
                ResultSet resultSet = statementValidate.executeQuery();
                if (resultSet.next()) {
                    double total = Double.parseDouble(Tools.roundingValue(resultSet.getDouble("Total"), 2));

                    callableIdCompraCredito = DBUtil.getConnection().prepareCall("{? = call Fc_Compra_Credito_Codigo_Alfanumerico()}");
                    callableIdCompraCredito.registerOutParameter(1, java.sql.Types.VARCHAR);
                    callableIdCompraCredito.execute();
                    String idCompraCreditoCredito = callableIdCompraCredito.getString(1);

                    preparedBanco = getConnection().prepareStatement("UPDATE Banco SET SaldoInicial = SaldoInicial - ? WHERE IdBanco = ?");
                    preparedBancoHistorial = getConnection().prepareStatement("INSERT INTO BancoHistorialTB(IdBanco,IdProcedencia,Descripcion,Fecha,Hora,Entrada,Salida)VALUES(?,?,?,?,?,?,?)");

                    if (bancoHistorialTB != null) {
                        preparedBanco.setDouble(1, bancoHistorialTB.getSalida());
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

                    movimiento_caja = DBUtil.getConnection().prepareStatement("INSERT INTO MovimientoCajaTB(IdCaja,FechaMovimiento,HoraMovimiento,Comentario,TipoMovimiento,Monto)VALUES(?,?,?,?,?,?)");
                    if (movimientoCajaTB != null) {
                        movimiento_caja.setString(1, movimientoCajaTB.getIdCaja());
                        movimiento_caja.setString(2, movimientoCajaTB.getFechaMovimiento());
                        movimiento_caja.setString(3, movimientoCajaTB.getHoraMovimiento());
                        movimiento_caja.setString(4, movimientoCajaTB.getComentario());
                        movimiento_caja.setShort(5, movimientoCajaTB.getTipoMovimiento());
                        movimiento_caja.setDouble(6, movimientoCajaTB.getMonto());
                        movimiento_caja.addBatch();
                    }

                    preparedCompraCredito = DBUtil.getConnection().prepareStatement("INSERT INTO CompraCreditoTB(IdCompra,IdCompraCredito,Monto,FechaPago,HoraPago,Estado,IdUsuario,Observacion) VALUES(?,?,?,?,?,?,?,?)");
                    preparedCompraCredito.setString(1, compraCreditoTB.getIdCompra());
                    preparedCompraCredito.setString(2, idCompraCreditoCredito);
                    preparedCompraCredito.setDouble(3, compraCreditoTB.getMonto());
                    preparedCompraCredito.setString(4, compraCreditoTB.getFechaPago());
                    preparedCompraCredito.setString(5, compraCreditoTB.getHoraPago());
                    preparedCompraCredito.setBoolean(6, compraCreditoTB.isEstado());
                    preparedCompraCredito.setString(7, compraCreditoTB.getIdEmpleado());
                    preparedCompraCredito.setString(8, compraCreditoTB.getObservacion());
                    preparedCompraCredito.addBatch();

                    statementValidate = DBUtil.getConnection().prepareStatement("SELECT Monto FROM CompraCreditoTB WHERE IdCompra = ?");
                    statementValidate.setString(1, compraCreditoTB.getIdCompra());
                    resultSet = statementValidate.executeQuery();
                    double montoTotal = 0;
                    while (resultSet.next()) {
                        montoTotal += resultSet.getDouble("Monto");
                    }

                    statementCompra = DBUtil.getConnection().prepareStatement("UPDATE CompraTB SET EstadoCompra = 1 WHERE IdCompra = ?");
                    if ((montoTotal + compraCreditoTB.getMonto()) >= total) {
                        statementCompra.setString(1, compraCreditoTB.getIdCompra());
                        statementCompra.addBatch();
                    }

                    preparedBanco.executeBatch();
                    preparedBancoHistorial.executeBatch();
                    preparedCompraCredito.executeBatch();
                    movimiento_caja.executeBatch();
                    statementCompra.executeBatch();
                    DBUtil.getConnection().commit();
                    result.setId((short) 1);
                    result.setState("inserted");
                    result.setIdResult(idCompraCreditoCredito);
                    result.setMessage("Se completo correctamente el proceso.");
                } else {
                    result.setId((short) 2);
                    result.setMessage("Problemas al encontrar le venta con el id indicado " + compraCreditoTB.getIdCompra());
                    result.setState("error");
                }
            } catch (SQLException e) {
                try {
                    getConnection().rollback();
                } catch (SQLException ex) {

                }
                result.setId((short) 2);
                result.setMessage(e.getLocalizedMessage());
                result.setState("error");
            } finally {
                try {
                    if (callableIdCompraCredito != null) {
                        callableIdCompraCredito.close();
                    }
                    if (preparedCompraCredito != null) {
                        preparedCompraCredito.close();
                    }
                    if (movimiento_caja != null) {
                        movimiento_caja.close();
                    }
                    if (preparedBanco != null) {
                        preparedBanco.close();
                    }
                    if (preparedBancoHistorial != null) {
                        preparedBancoHistorial.close();
                    }

                    dbDisconnect();
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

    public static String Actualizar_Compra_Estado(String idCompra) {
        String result = "";
        dbConnect();
        if (getConnection() != null) {
            CallableStatement callableValidate = null;
            PreparedStatement preparedCompra = null;
            try {
                getConnection().setAutoCommit(false);

                callableValidate = getConnection().prepareCall("{? = call Fc_Cambiar_Estado_Compra(?)}");
                callableValidate.registerOutParameter(1, java.sql.Types.VARCHAR);
                callableValidate.setString(2, idCompra);
                callableValidate.execute();
                String estadoCompra = callableValidate.getString(1);

                preparedCompra = DBUtil.getConnection().prepareStatement("UPDATE CompraTB SET EstadoCompra = 1 WHERE IdCompra = ?");
                if (estadoCompra.equalsIgnoreCase("completado")) {
                    preparedCompra.setString(1, idCompra);
                    preparedCompra.addBatch();
                }

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
                    if (callableValidate != null) {
                        callableValidate.close();
                    }
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
                compraTB.setIdProveedor(resultSet.getString("NumeroDocumento") + "\n" + resultSet.getString("Proveedor").toUpperCase());
                compraTB.setSerie(resultSet.getString("Serie").toUpperCase());
                compraTB.setNumeracion(resultSet.getString("Numeracion"));
                compraTB.setTipoName(resultSet.getString("Tipo"));
                compraTB.setEstado(resultSet.getInt("EstadoCompra"));
                compraTB.setEstadoName(resultSet.getString("EstadoName"));
                compraTB.setMonedaNombre(resultSet.getString("Simbolo"));
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

    public static ObservableList<CompraTB> ListComprasCredito(String result, String fechaInicio, String fechaFinal, short opcion) {
        String selectStmt = "{call Sp_Listar_Compras_Credito(?,?,?,?)}";
        PreparedStatement preparedStatement = null;
        ResultSet rsEmps = null;
        ObservableList<CompraTB> empList = FXCollections.observableArrayList();
        try {
            dbConnect();
            preparedStatement = getConnection().prepareStatement(selectStmt);
            preparedStatement.setString(1, result);
            preparedStatement.setString(2, fechaInicio);
            preparedStatement.setString(3, fechaFinal);
            preparedStatement.setShort(4, opcion);
            rsEmps = preparedStatement.executeQuery();
            while (rsEmps.next()) {
                CompraTB compraTB = new CompraTB();
                compraTB.setId(rsEmps.getRow());
                compraTB.setIdCompra(rsEmps.getString("IdCompra"));
                compraTB.setIdProveedor(rsEmps.getString("NumeroDocumento") + "\n" + rsEmps.getString("RazonSocial"));
                compraTB.setFechaCompra(rsEmps.getDate("FechaCompra").toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                compraTB.setHoraCompra(rsEmps.getTime("HoraCompra").toLocalTime().format(DateTimeFormatter.ofPattern("hh:mm:ss a")));
                compraTB.setSerie(rsEmps.getString("Serie").toUpperCase());
                compraTB.setNumeracion(rsEmps.getString("Numeracion").toUpperCase());
                compraTB.setProveedorTB(new ProveedorTB(rsEmps.getString("NumeroDocumento"), rsEmps.getString("RazonSocial")));

                compraTB.setEstado(rsEmps.getInt("EstadoCompra"));
                compraTB.setEstadoName(rsEmps.getString("Estado"));
                Label label = new Label(compraTB.getEstadoName());
                label.getStyleClass().add(compraTB.getEstado() == 1 ? "label-asignacion" : compraTB.getEstado() == 2 ? "label-medio" : compraTB.getEstado() == 3 ? "label-proceso" : "label-ultimo");

                compraTB.setEstadoLabel(label);

                compraTB.setMonedaNombre(rsEmps.getString("Simbolo"));
                compraTB.setMontoTotal(rsEmps.getDouble("MontoTotal"));
                compraTB.setMontoPagado(rsEmps.getDouble("MontoPagado"));
                compraTB.setMontoRestante(compraTB.getMontoTotal() - compraTB.getMontoPagado());

                HBox hBox = new HBox();
                hBox.setAlignment(Pos.CENTER);
                hBox.setStyle(";-fx-spacing:0.8333333333333334em;");

                Button btnVisualizar = new Button();
                ImageView imageViewVisualizar = new ImageView(new Image("/view/image/search_caja.png"));
                imageViewVisualizar.setFitWidth(24);
                imageViewVisualizar.setFitHeight(24);
                btnVisualizar.setGraphic(imageViewVisualizar);
                btnVisualizar.getStyleClass().add("buttonLightWarning");

                hBox.getChildren().add(btnVisualizar);
                compraTB.setHbOpciones(hBox);

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

    public static CompraCreditoTB ImprimirCompraCreditoById(String idCompra, String idCompraCredito) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        CompraCreditoTB compraCreditoTB = null;
        CompraTB compraTB = null;
        try {
            DBUtil.dbConnect();
            preparedStatement = DBUtil.getConnection().prepareCall("select IdCompraCredito,Monto,FechaPago,HoraPago,Observacion from CompraCreditoTB where IdCompraCredito = ?");
            preparedStatement.setString(1, idCompraCredito);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                compraCreditoTB = new CompraCreditoTB();
                compraCreditoTB.setIdCompraCredito(resultSet.getString("IdCompraCredito"));
                compraCreditoTB.setMonto(resultSet.getDouble("Monto"));
                compraCreditoTB.setFechaPago(resultSet.getDate("FechaPago").toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                compraCreditoTB.setHoraPago(resultSet.getTime("HoraPago").toLocalTime().format(DateTimeFormatter.ofPattern("hh:mm:ss a")));
                compraCreditoTB.setObservacion(resultSet.getString("Observacion"));

                preparedStatement = DBUtil.getConnection().prepareCall("{call Sp_Obtener_Compra_ById_For_Credito(?)}");
                preparedStatement.setString(1, idCompra);
                resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    compraTB = new CompraTB();
                    compraTB.setIdCompra(idCompra);
                    compraTB.setProveedorTB(new ProveedorTB(resultSet.getString("NumeroDocumento"), resultSet.getString("RazonSocial"), resultSet.getString("Telefono"), resultSet.getString("Celular"), resultSet.getString("Email"), resultSet.getString("Direccion")));
                    compraTB.setSerie(resultSet.getString("Serie"));
                    compraTB.setNumeracion(resultSet.getString("Numeracion"));
                    compraTB.setEstado(resultSet.getInt("EstadoCompra"));
                    compraTB.setEstadoName(resultSet.getString("Estado"));
                    compraTB.setMonedaNombre(resultSet.getString("Simbolo"));
                    compraTB.setMontoTotal(resultSet.getDouble("MontoTotal"));
                    compraTB.setMontoPagado(resultSet.getDouble("MontoPagado"));
                    compraTB.setMontoRestante(compraTB.getMontoTotal() - compraTB.getMontoPagado());
                }
                compraCreditoTB.setCompraTB(compraTB);
            }

        } catch (SQLException ex) {
            Tools.println("ImprimirCompraCreditoById: " + ex.getLocalizedMessage());
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
        return compraCreditoTB;
    }

    public static String Compra_Contado_Insumos(BancoHistorialTB bancoHistorialTB, CompraTB compraTB, TableView<DetalleCompraTB> tableView, ObservableList<LoteTB> loteTBs) {
        CallableStatement codigo_compra = null;
        PreparedStatement compra = null;
        PreparedStatement detalle_compra = null;
        PreparedStatement insumos_update = null;
        PreparedStatement insumos_kardex = null;
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
                        + "Comprobante,"
                        + "Serie,"
                        + "Numeracion,"
                        + "TipoMoneda,"
                        + "FechaCompra,"
                        + "HoraCompra,"
                        + "FechaVencimiento,"
                        + "HoraVencimiento,"
                        + "Observaciones,"
                        + "Notas,"
                        + "TipoCompra,"
                        + "EstadoCompra,"
                        + "Usuario,"
                        + "Destino)"
                        + "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

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
                        + "Importe,"
                        + "Descripcion)"
                        + "VALUES(?,?,?,?,?,?,?,?,?,?)");

                insumos_update = getConnection().prepareStatement("UPDATE InsumoTB SET Cantidad = Cantidad + ?,PrecioCompra = ? WHERE IdInsumo = ?");

                insumos_kardex = getConnection().prepareStatement("INSERT INTO "
                        + "KardexInsumoTB("
                        + "IdInsumo,"
                        + "Fecha,"
                        + "Hora,"
                        + "Tipo,"
                        + "Movimiento,"
                        + "Detalle,"
                        + "Cantidad, "
                        + "Costo, "
                        + "Total) "
                        + "VALUES(?,?,?,?,?,?,?,?,?)");

                preparedBanco = DBUtil.getConnection().prepareStatement("UPDATE Banco SET SaldoInicial = SaldoInicial - ? WHERE IdBanco = ?");

                preparedBancoHistorial = DBUtil.getConnection().prepareStatement("INSERT INTO BancoHistorialTB(IdBanco,IdEmpleado,IdProcedencia,Descripcion,Fecha,Hora,Entrada,Salida)VALUES(?,?,?,?,?,?,?,?)");

                compra.setString(1, id_compra);
                compra.setString(2, compraTB.getIdProveedor());
                compra.setInt(3, compraTB.getTipoDocumento());
                compra.setString(4, compraTB.getSerie());
                compra.setString(5, compraTB.getNumeracion());
                compra.setInt(6, compraTB.getIdMoneda());
                compra.setString(7, compraTB.getFechaCompra());
                compra.setString(8, compraTB.getHoraCompra());
                compra.setString(9, compraTB.getFechaVencimiento());
                compra.setString(10, compraTB.getHoraVencimiento());
                compra.setString(11, compraTB.getObservaciones());
                compra.setString(12, compraTB.getNotas());
                compra.setInt(13, compraTB.getTipo());
                compra.setInt(14, compraTB.getEstado());
                compra.setString(15, compraTB.getUsuario());
                compra.setString(16, compraTB.getDestino());
                compra.addBatch();

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

                for (DetalleCompraTB dc : tableView.getItems()) {
                    detalle_compra.setString(1, id_compra);
                    detalle_compra.setString(2, dc.getIdArticulo());
                    detalle_compra.setDouble(3, dc.getCantidad());
                    detalle_compra.setDouble(4, dc.getPrecioCompra());
                    detalle_compra.setDouble(5, dc.getDescuento());
                    detalle_compra.setInt(6, dc.getIdImpuesto());
                    detalle_compra.setString(7, dc.getNombreImpuesto());
                    detalle_compra.setDouble(8, dc.getValorImpuesto());
                    detalle_compra.setDouble(9, dc.getImporte());
                    detalle_compra.setString(10, dc.getDescripcion());
                    detalle_compra.addBatch();

                    insumos_update.setDouble(1, dc.getCantidad());
                    insumos_update.setDouble(2, dc.getPrecioCompra());
                    insumos_update.setString(3, dc.getIdArticulo());
                    insumos_update.addBatch();

                    insumos_kardex.setString(1, dc.getIdArticulo());
                    insumos_kardex.setString(2, Tools.getDate());
                    insumos_kardex.setString(3, Tools.getHour());
                    insumos_kardex.setShort(4, (short) 1);
                    insumos_kardex.setInt(5, 2);
                    insumos_kardex.setString(6, "COMPRA CON SERIE Y NUMERACIÓN: " + compraTB.getSerie().toUpperCase() + "-" + compraTB.getNumeracion().toUpperCase());
                    insumos_kardex.setDouble(7, dc.getCantidad());
                    insumos_kardex.setDouble(8, dc.getPrecioCompra());
                    insumos_kardex.setDouble(9, dc.getCantidad() * dc.getPrecioCompra());
                    insumos_kardex.addBatch();
                }
                compra.executeBatch();
                detalle_compra.executeBatch();
                insumos_update.executeBatch();
                insumos_kardex.executeBatch();
                preparedBanco.executeBatch();
                preparedBancoHistorial.executeBatch();

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
                    if (insumos_update != null) {
                        insumos_update.close();
                    }
                    if (insumos_kardex != null) {
                        insumos_kardex.close();
                    }
                    if (preparedBanco != null) {
                        preparedBanco.close();
                    }
                    if (preparedBancoHistorial != null) {
                        preparedBancoHistorial.close();
                    }
                } catch (SQLException ex) {
                    return ex.getLocalizedMessage();
                }
            }
        } else {
            return "No se pudo conectar el servidor, intente nuevamente.";
        }
    }

}
