package model;

import controller.tools.Session;
import controller.tools.Tools;
import java.sql.CallableStatement;
import java.sql.Date;
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

public class CompraADO extends DBUtil {

    public static String CrudCompra(short opcion, String idCaja, CompraTB compraTB, TableView<SuministroTB> tableView, ObservableList<LoteTB> loteTBs, CuentasProveedorTB cuentasProveedorTB) {

        CallableStatement codigo_compra = null;
        PreparedStatement compra = null;
        PreparedStatement detalle_compra = null;
        PreparedStatement suministro_update = null;
        PreparedStatement pago_Proveedores = null;
        PreparedStatement movimiento_caja = null;
        PreparedStatement suministro_kardex = null;
        PreparedStatement lote_compra = null;

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
                    + "Numeracion,"
                    + "TipoMoneda,"
                    + "Fecha,"
                    + "Hora,"
                    + "SubTotal,"
                    + "Descuento,"
                    + "Total,"
                    + "Observaciones,"
                    + "Notas,"
                    + "TipoCompra,"
                    + "EstadoCompra,"
                    + "Usuario) "
                    + "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

            movimiento_caja = getConnection().prepareStatement("INSERT INTO "
                    + "MovimientoCajaTB("
                    + "IdCaja,"
                    + "IdUsuario,"
                    + "FechaMovimiento,"
                    + "HoraMovimiento,"
                    + "Comentario,"
                    + "Movimiento,"
                    + "Entrada,"
                    + "Salidas,"
                    + "Saldo)"
                    + "VALUES(?,?,?,?,?,?,?,?,?)");

            detalle_compra = getConnection().prepareStatement("INSERT INTO "
                    + "DetalleCompraTB("
                    + "IdCompra,"
                    + "IdArticulo,"
                    + "Cantidad,"
                    + "PrecioCompra,"
                    + "Descuento,"
                    + "PrecioVenta1,"
                    + "Margen1,"
                    + "Utilidad1,"
                    + "IdImpuesto,"
                    + "NombreImpuesto,"
                    + "ValorImpuesto,"
                    + "ImpuestoSumado,"
                    + "Importe,"
                    + "Descripcion)"
                    + "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

            suministro_update = getConnection().prepareStatement("UPDATE "
                    + "SuministroTB SET "
                    + "Cantidad = Cantidad + ?,"
                    + "PrecioCompra = ?,"
                    + "PrecioVentaGeneral = ?, "
                    + "PrecioMargenGeneral = ?, "
                    + "PrecioUtilidadGeneral = ?, "
                    + "Impuesto = ? "
                    + "WHERE IdSuministro = ?");

            pago_Proveedores = getConnection().prepareStatement("INSERT INTO "
                    + "CuentasProveedorTB("
                    + "IdCompra,"
                    + "MontoTotal,"
                    + "Plazos,"
                    + "FechaPago,"
                    + "FechaRegistro) "
                    + "values(?,?,?,?,?)");

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
            compra.setInt(3, compraTB.getComprobante());
            compra.setString(4, compraTB.getNumeracion().toUpperCase());
            compra.setInt(5, compraTB.getTipoMoneda());
            compra.setString(6, compraTB.getFecha());
            compra.setString(7, compraTB.getHora());
            compra.setDouble(8, compraTB.getSubTotal());
            compra.setDouble(9, compraTB.getDescuento());
            compra.setDouble(10, compraTB.getTotal().get());
            compra.setString(11, compraTB.getObservaciones());
            compra.setString(12, compraTB.getNotas());
            compra.setInt(13, compraTB.getTipo());
            compra.setInt(14, compraTB.getEstado());
            compra.setString(15, compraTB.getUsuario());
            compra.addBatch();

            if (opcion == 1) {
                movimiento_caja.setString(1, idCaja);
                movimiento_caja.setString(2, Session.USER_ID);
                movimiento_caja.setString(3, compraTB.getFecha());
                movimiento_caja.setString(4, compraTB.getHora());
                movimiento_caja.setString(5, "Compra al contado");
                movimiento_caja.setString(6, "COM");
                movimiento_caja.setDouble(7, 0);
                movimiento_caja.setDouble(8, compraTB.getTotal().get());
                movimiento_caja.setDouble(9, compraTB.getTotal().get() - 0);
                movimiento_caja.addBatch();
            } else if (opcion == 2) {
                pago_Proveedores.setString(1, id_compra);
                pago_Proveedores.setDouble(2, cuentasProveedorTB.getMontoTotal());
                pago_Proveedores.setInt(3, cuentasProveedorTB.getPlazos());
                pago_Proveedores.setString(4, cuentasProveedorTB.getFechaPago());
                pago_Proveedores.setString(5, cuentasProveedorTB.getFechaRegistro());
                pago_Proveedores.addBatch();
            }

            for (int i = 0; i < tableView.getItems().size(); i++) {
                detalle_compra.setString(1, id_compra);
                detalle_compra.setString(2, tableView.getItems().get(i).getIdSuministro());
                detalle_compra.setDouble(3, tableView.getItems().get(i).getCantidad());
                detalle_compra.setDouble(4, tableView.getItems().get(i).getCostoCompraReal());
                detalle_compra.setDouble(5, tableView.getItems().get(i).getDescuento());

                detalle_compra.setDouble(6, tableView.getItems().get(i).getPrecioVentaGeneral());
                detalle_compra.setShort(7, tableView.getItems().get(i).getPrecioMargenGeneral());
                detalle_compra.setDouble(8, tableView.getItems().get(i).getPrecioUtilidadGeneral());

                detalle_compra.setInt(9, tableView.getItems().get(i).getImpuestoArticulo());
                detalle_compra.setString(10, tableView.getItems().get(i).getImpuestoArticuloName());
                detalle_compra.setDouble(11, tableView.getItems().get(i).getImpuestoValor());
                detalle_compra.setDouble(12, tableView.getItems().get(i).getImpuestoSumado());
                detalle_compra.setDouble(13, tableView.getItems().get(i).getTotalImporte());
                detalle_compra.setString(14, tableView.getItems().get(i).getDescripcion());
                detalle_compra.addBatch();

                suministro_update.setDouble(1, tableView.getItems().get(i).getCantidad());
                suministro_update.setDouble(2, tableView.getItems().get(i).getCostoCompraReal());
                suministro_update.setDouble(3, tableView.getItems().get(i).getPrecioVentaGeneral());
                suministro_update.setInt(4, tableView.getItems().get(i).getPrecioMargenGeneral());
                suministro_update.setDouble(5, tableView.getItems().get(i).getPrecioUtilidadGeneral());
                suministro_update.setInt(6, tableView.getItems().get(i).getImpuestoArticulo());
                suministro_update.setString(7, tableView.getItems().get(i).getIdSuministro());
                suministro_update.addBatch();

                suministro_kardex.setString(1, tableView.getItems().get(i).getIdSuministro());
                suministro_kardex.setString(2, Tools.getDate());
                suministro_kardex.setString(3, Tools.getHour());
                suministro_kardex.setShort(4, (short) 1);
                suministro_kardex.setInt(5, 2);
                suministro_kardex.setString(6, "Compra");
                suministro_kardex.setDouble(7, tableView.getItems().get(i).getCantidad());  
                suministro_kardex.addBatch();
            }

            for (int i = 0; i < loteTBs.size(); i++) {
                lote_compra.setString(1, loteTBs.get(i).getNumeroLote().equalsIgnoreCase("") ? id_compra + loteTBs.get(i).getIdArticulo() : loteTBs.get(i).getNumeroLote());
                lote_compra.setDate(2, Date.valueOf(loteTBs.get(i).getFechaCaducidad()));
                lote_compra.setDouble(3, loteTBs.get(i).getExistenciaInicial());
                lote_compra.setDouble(4, loteTBs.get(i).getExistenciaActual());
                lote_compra.setString(5, loteTBs.get(i).getIdArticulo());
                lote_compra.setString(6, id_compra);
                lote_compra.addBatch();
            }

            compra.executeBatch();
            movimiento_caja.executeBatch();
            detalle_compra.executeBatch();
            suministro_update.executeBatch();
            pago_Proveedores.executeBatch();
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
                if (suministro_update != null) {
                    suministro_update.close();
                }
                if (movimiento_caja != null) {
                    movimiento_caja.close();
                }
                if (suministro_kardex != null) {
                    suministro_kardex.close();
                }
                if (lote_compra != null) {
                    lote_compra.close();
                }
                if (pago_Proveedores != null) {
                    pago_Proveedores.close();
                }
                dbDisconnect();
            } catch (SQLException ex) {
                return ex.getLocalizedMessage();
            }
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
                compraTB.setFecha(rsEmps.getString("Fecha"));
                compraTB.setHora(rsEmps.getTime("Hora").toLocalTime().format(DateTimeFormatter.ofPattern("hh:mm:ss a")));
                compraTB.setNumeracion(rsEmps.getString("Numeracion"));
                compraTB.setProveedorTB(new ProveedorTB(rsEmps.getString("NumeroDocumento"), rsEmps.getString("RazonSocial")));
                compraTB.setTipoName(rsEmps.getString("Tipo"));
                compraTB.setEstadoName(rsEmps.getString("Estado"));

                Label label = new Label(compraTB.getEstadoName());
                if (compraTB.getEstadoName().equalsIgnoreCase("PAGADO")) {
                    label.getStyleClass().add("label-asignacion");
                } else if (compraTB.getEstadoName().equalsIgnoreCase("PENDIENTE")) {
                    label.getStyleClass().add("label-medio");
                } else if (compraTB.getEstadoName().equalsIgnoreCase("ANULADO")) {
                    label.getStyleClass().add("label-proceso");
                }

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

    public static ArrayList<Object> ListCompletaDetalleCompra(String value) {
        PreparedStatement statementCompra = null;
        PreparedStatement statementProveedor = null;
        PreparedStatement preparedStatement = null;
        ArrayList<Object> objects = new ArrayList<>();
        dbConnect();
        if (getConnection() != null) {
            try {

                statementCompra = getConnection().prepareStatement("{call Sp_Obtener_Compra_ById(?)}");
                statementCompra.setString(1, value);
                ResultSet resultSet = statementCompra.executeQuery();
                CompraTB compraTB = null;
                if (resultSet.next()) {
                    compraTB = new CompraTB();
                    compraTB.setFecha(resultSet.getDate("Fecha").toLocalDate().format(DateTimeFormatter.ofPattern("EEEE d 'de' MMMM 'de' yyyy")));
                    compraTB.setHora(resultSet.getTime("Hora").toLocalTime().format(DateTimeFormatter.ofPattern("hh:mm:ss a")));
                    compraTB.setComprobanteName(resultSet.getString("Nombre"));
                    compraTB.setNumeracion(resultSet.getString("Numeracion"));
                    compraTB.setTipoMonedaName(resultSet.getString("Simbolo"));
                    compraTB.setTipoName(resultSet.getString("Tipo"));
                    compraTB.setEstadoName(resultSet.getString("Estado"));
                    compraTB.setTotal(resultSet.getDouble("Total"));
                    compraTB.setObservaciones(resultSet.getString("Observaciones"));
                    compraTB.setNotas(resultSet.getString("Notas"));
                    objects.add(compraTB);
                } else {
                    objects.add(compraTB);
                }

                statementProveedor = getConnection().prepareStatement("select p.IdProveedor,p.NumeroDocumento,p.RazonSocial as Proveedor,p.Telefono,p.Celular,p.Direccion \n"
                        + "from CompraTB as c inner join ProveedorTB as p\n"
                        + "on c.Proveedor = p.IdProveedor\n"
                        + "where c.IdCompra = ?");
                statementProveedor.setString(1, value);
                ResultSet resultSetProveedor = statementProveedor.executeQuery();
                ProveedorTB proveedorTB = null;
                if (resultSetProveedor.next()) {
                    proveedorTB = new ProveedorTB();

                    proveedorTB.setIdProveedor(resultSetProveedor.getString("IdProveedor"));
                    proveedorTB.setNumeroDocumento(resultSetProveedor.getString("NumeroDocumento"));
                    proveedorTB.setRazonSocial(resultSetProveedor.getString("Proveedor"));
                    proveedorTB.setTelefono(resultSetProveedor.getString("Telefono"));
                    proveedorTB.setCelular(resultSetProveedor.getString("Celular"));
                    proveedorTB.setDireccion(resultSetProveedor.getString("Direccion"));
                    objects.add(proveedorTB);
                } else {
                    objects.add(proveedorTB);
                }

                preparedStatement = getConnection().prepareStatement("{call Sp_Listar_Detalle_Compra(?)}");
                preparedStatement.setString(1, value);
                ResultSet rsEmps = preparedStatement.executeQuery();
                ObservableList<SuministroTB> empList = FXCollections.observableArrayList();
                while (rsEmps.next()) {
                    SuministroTB suministroTB = new SuministroTB();
                    suministroTB.setId(rsEmps.getRow());
                    suministroTB.setIdSuministro(rsEmps.getString("IdSuministro"));
                    suministroTB.setClave(rsEmps.getString("Clave"));
                    suministroTB.setNombreMarca(rsEmps.getString("NombreMarca"));
                    suministroTB.setCantidad(rsEmps.getDouble("Cantidad"));
                    suministroTB.setUnidadVenta(rsEmps.getInt("UnidadVenta"));
                    suministroTB.setUnidadCompraName(rsEmps.getString("UnidadCompra"));
                    suministroTB.setCostoCompra(rsEmps.getDouble("PrecioCompra"));
                    suministroTB.setDescuento(rsEmps.getDouble("Descuento"));
                    suministroTB.setImpuestoArticulo(rsEmps.getInt("IdImpuesto"));
                    suministroTB.setSubImporte(suministroTB.getCantidad() * suministroTB.getCostoCompra());
                    double porcentajeDecimal = suministroTB.getDescuento() / 100.00;
                    double porcentajeRestante = suministroTB.getCostoCompra() * porcentajeDecimal;
                    suministroTB.setDescuentoSumado(porcentajeRestante * suministroTB.getCantidad());
                    suministroTB.setImpuestoValor(rsEmps.getDouble("ValorImpuesto"));
                    suministroTB.setImpuestoSumado(rsEmps.getDouble("ImpuestoSumado"));
                    suministroTB.setSubImporteDescuento(suministroTB.getSubImporte() - suministroTB.getDescuentoSumado());
                    suministroTB.setTotalImporte(rsEmps.getDouble("Importe"));
                    empList.add(suministroTB);
                }
                objects.add(empList);

            } catch (SQLException ex) {

            } finally {
                try {
                    if (statementCompra != null) {
                        statementCompra.close();
                    }
                    if (statementProveedor != null) {
                        statementProveedor.close();
                    }
                    if (preparedStatement != null) {
                        preparedStatement.close();
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
                    compraTB.setFecha(resultSet.getDate("Fecha").toLocalDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
                    compraTB.setHora(resultSet.getTime("Hora").toLocalTime().format(DateTimeFormatter.ofPattern("hh:mm:ss a")));
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

    public static String cancelarVenta(String idCompra, ObservableList<SuministroTB> tableView, boolean retirar, boolean regresar) {
        String result = "";
        DBUtil.dbConnect();
        if (DBUtil.getConnection() != null) {
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
                    statementSuministro = DBUtil.getConnection().prepareStatement("UPDATE SuministroTB SET Cantidad = Cantidad - ? WHERE IdSuministro = ?");
                    statementSuministroKardex = DBUtil.getConnection().prepareStatement("INSERT INTO "
                            + "KardexSuministroTB("
                            + "IdSuministro,"
                            + "Fecha,"
                            + "Hora,"
                            + "Tipo,"
                            + "Movimiento,"
                            + "Detalle,"
                            + "Cantidad,"
                            + "CUnitario,"
                            + "CTotal) "
                            + "VALUES(?,?,?,?,?,?,?,?,?)");
                    if (retirar) {
                        for (int i = 0; i < tableView.size(); i++) {
                            statementSuministro.setDouble(1, tableView.get(i).getCantidad());
                            statementSuministro.setString(2, tableView.get(i).getIdSuministro());
                            statementSuministro.addBatch();
                            statementSuministro.executeBatch();

                            statementSuministroKardex.setString(1, tableView.get(i).getIdSuministro());
                            statementSuministroKardex.setString(2, Tools.getDate());
                            statementSuministroKardex.setString(3, Tools.getHour());
                            statementSuministroKardex.setShort(4, (short) 2);
                            statementSuministroKardex.setInt(5, 1);
                            statementSuministroKardex.setString(6, "Cancelar Compra");
                            statementSuministroKardex.setDouble(7, tableView.get(i).getCantidad());
                            statementSuministroKardex.setDouble(8, tableView.get(i).getCostoCompra());
                            statementSuministroKardex.setDouble(9, tableView.get(i).getCantidad() * tableView.get(i).getCostoCompra());
                            statementSuministroKardex.addBatch();
                            statementSuministroKardex.executeBatch();
                        }

                    }

                    statementCompra = DBUtil.getConnection().prepareStatement("UPDATE CompraTB SET EstadoCompra = ? WHERE IdCompra = ?");
                    statementCompra.setInt(1, 3);
                    statementCompra.setString(2, idCompra);
                    statementCompra.addBatch();
                    statementCompra.executeBatch();

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
        } else {
            result = "No se pudo completar la petición por problemas de red, revise su conexión e intente nuevamente.";
        }
        return result;
    }

    public static ArrayList<Object> GetComprasForEditar(String idCompra) {
        ArrayList<Object> arrayList = new ArrayList<>();
        DBUtil.dbConnect();
        PreparedStatement statementCompra = null;
        PreparedStatement statementCompraDetalle = null;
        try {
            statementCompra = DBUtil.getConnection().prepareStatement("select IdCompra,Proveedor,dbo.Fc_Obtener_Datos_Proveedor(Proveedor)as DatosProveedor,Fecha,Comprobante,Numeracion,TipoMoneda,Observaciones,Notas from CompraTB WHERE IdCompra = ?");
            statementCompra.setString(1, idCompra);
            ResultSet resultSet = statementCompra.executeQuery();
            if (resultSet.next()) {
                CompraTB compraTB = new CompraTB();
                compraTB.setIdCompra(resultSet.getString("IdCompra"));
                compraTB.setProveedor(resultSet.getString("Proveedor"));
                compraTB.setProveedorTB(new ProveedorTB(resultSet.getString("DatosProveedor")));
                compraTB.setFecha(resultSet.getString("Fecha"));
                compraTB.setComprobante(resultSet.getInt("Comprobante"));
                compraTB.setNumeracion(resultSet.getString("Numeracion"));
                compraTB.setTipoMoneda(resultSet.getInt("TipoMoneda"));
                compraTB.setObservaciones(resultSet.getString("Observaciones"));
                compraTB.setNotas(resultSet.getString("Notas"));
                arrayList.add(compraTB);
            }
            ObservableList<SuministroTB> list = FXCollections.observableArrayList();
            statementCompraDetalle = DBUtil.getConnection().prepareStatement("select d.IdArticulo,s.Clave,s.NombreMarca,d.Cantidad,d.PrecioCompra,d.Descuento,d.Importe,d.IdImpuesto,d.NombreImpuesto,d.ValorImpuesto,d.PrecioVenta1,d.Margen1,d.Utilidad1,d.Descripcion from DetalleCompraTB as d inner join SuministroTB as s on d.IdArticulo = s.IdSuministro where d.IdCompra = ?");
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
}
