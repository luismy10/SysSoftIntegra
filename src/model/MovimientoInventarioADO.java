package model;

import controller.inventario.movimientos.FxMovimientosController;
import controller.inventario.movimientos.FxMovimientosDetalleController;
import controller.tools.FilesRouters;
import controller.tools.ObjectGlobal;
import controller.tools.Session;
import controller.tools.Tools;
import controller.tools.WindowStage;
import java.io.IOException;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class MovimientoInventarioADO {

    public static String Crud_Movimiento_Inventario(MovimientoInventarioTB inventarioTB, TableView<SuministroTB> tableView) {
        String result = "";
        DBUtil.dbConnect();
        if (DBUtil.getConnection() != null) {
            PreparedStatement statementMovimiento = null;
            PreparedStatement statementMovimientoDetalle = null;
            PreparedStatement suministro_update = null;
            PreparedStatement suministro_kardex = null;
            CallableStatement codigoMovimiento = null;
            try {
                DBUtil.getConnection().setAutoCommit(false);

                codigoMovimiento = DBUtil.getConnection().prepareCall("{? = call Fc_MovimientoInventario_Codigo_Alfanumerico()}");
                codigoMovimiento.registerOutParameter(1, java.sql.Types.VARCHAR);
                codigoMovimiento.execute();
                String idMovimiento = codigoMovimiento.getString(1);

                statementMovimiento = DBUtil.getConnection().prepareStatement("INSERT INTO "
                        + "MovimientoInventarioTB("
                        + "IdMovimientoInventario,"
                        + "Fecha,"
                        + "Hora,"
                        + "TipoAjuste,"
                        + "TipoMovimiento,"
                        + "Observacion,"
                        + "Suministro,"
                        + "Articulo,"
                        + "Proveedor,"
                        + "Estado)"
                        + "VALUES(?,?,?,?,?,?,?,?,?,?)");
                statementMovimiento.setString(1, idMovimiento);
                statementMovimiento.setString(2, inventarioTB.getFecha());
                statementMovimiento.setString(3, inventarioTB.getHora());
                statementMovimiento.setBoolean(4, inventarioTB.isTipoAjuste());
                statementMovimiento.setInt(5, inventarioTB.getTipoMovimiento());
                statementMovimiento.setString(6, inventarioTB.getObservacion());
                statementMovimiento.setBoolean(7, inventarioTB.isSuministro());
                statementMovimiento.setBoolean(8, inventarioTB.isArticulo());
                statementMovimiento.setString(9, inventarioTB.getProveedor() == null ? "" : inventarioTB.getProveedor());
                statementMovimiento.setShort(10, inventarioTB.getEstado());
                statementMovimiento.addBatch();

                suministro_update = inventarioTB.isTipoAjuste() ? DBUtil.getConnection().prepareStatement("UPDATE SuministroTB SET Cantidad = Cantidad + ? WHERE IdSuministro = ?")
                        : DBUtil.getConnection().prepareStatement("UPDATE SuministroTB SET Cantidad = Cantidad - ? WHERE IdSuministro = ?");

                suministro_kardex = DBUtil.getConnection().prepareStatement("INSERT INTO "
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

                statementMovimientoDetalle = DBUtil.getConnection().prepareStatement("INSERT INTO "
                        + "MovimientoInventarioDetalleTB("
                        + "IdMovimientoInventario,"
                        + "IdSuministro,"
                        + "Cantidad,"
                        + "Costo,"
                        + "Precio)"
                        + "VALUES(?,?,?,?,?)");

                for (int i = 0; i < tableView.getItems().size(); i++) {
                    if (tableView.getItems().get(i).getValidar().isSelected()) {
                        statementMovimientoDetalle.setString(1, idMovimiento);
                        statementMovimientoDetalle.setString(2, tableView.getItems().get(i).getIdSuministro());
                        statementMovimientoDetalle.setDouble(3, tableView.getItems().get(i).getMovimiento());
                        statementMovimientoDetalle.setDouble(4, tableView.getItems().get(i).getCostoCompra());
                        statementMovimientoDetalle.setDouble(5, tableView.getItems().get(i).getPrecioVentaGeneral());
                        statementMovimientoDetalle.addBatch();

                        if (inventarioTB.getEstado() == 1) {
                            suministro_update.setDouble(1, tableView.getItems().get(i).getMovimiento());
                            suministro_update.setString(2, tableView.getItems().get(i).getIdSuministro());
                            suministro_update.addBatch();

                            suministro_kardex.setString(1, tableView.getItems().get(i).getIdSuministro());
                            suministro_kardex.setString(2, Tools.getDate());
                            suministro_kardex.setString(3, Tools.getHour());
                            suministro_kardex.setShort(4, inventarioTB.isTipoAjuste() ? (short) 1 : (short) 2);
                            suministro_kardex.setInt(5, inventarioTB.getTipoMovimiento());
                            suministro_kardex.setString(6, inventarioTB.getObservacion());
                            suministro_kardex.setDouble(7, tableView.getItems().get(i).getMovimiento());
                            suministro_kardex.setDouble(8, tableView.getItems().get(i).getCostoCompra());
                            suministro_kardex.setDouble(9, tableView.getItems().get(i).getMovimiento() * tableView.getItems().get(i).getCostoCompra());
                            suministro_kardex.addBatch();
                        }
                    }
                }

                statementMovimiento.executeBatch();
                statementMovimientoDetalle.executeBatch();
                suministro_update.executeBatch();
                suministro_kardex.executeBatch();
                DBUtil.getConnection().commit();
                result = "registered";

            } catch (SQLException ex) {
                try {
                    DBUtil.getConnection().rollback();
                } catch (SQLException e) {

                }
                result = ex.getLocalizedMessage();
            } finally {
                try {
                    if (statementMovimiento != null) {
                        statementMovimiento.close();
                    }
                    if (statementMovimientoDetalle != null) {
                        statementMovimientoDetalle.close();
                    }
                    if (codigoMovimiento != null) {
                        codigoMovimiento.close();
                    }
                    if (suministro_update != null) {
                        suministro_update.close();
                    }
                    if (suministro_kardex != null) {
                        suministro_kardex.close();
                    }
                    DBUtil.dbDisconnect();
                } catch (SQLException ex) {
                    result = ex.getLocalizedMessage();
                }
            }
        } else {
            result = "No se puedo completar la petición por un problema con su conexión, intente nuevamente.";
        }
        return result;
    }

    public static String Crud_Movimiento_Inventario_Con_Caja(MovimientoCajaTB movimientoCajaTB, MovimientoInventarioTB inventarioTB, TableView<SuministroTB> tableView) {
        String result = "";
        DBUtil.dbConnect();
        if (DBUtil.getConnection() != null) {
            PreparedStatement statementValidar = null;
            PreparedStatement statementMovimiento = null;
            PreparedStatement statementMovimientoDetalle = null;
            PreparedStatement suministro_update = null;
            PreparedStatement suministro_kardex = null;
            CallableStatement codigoMovimiento = null;
            PreparedStatement statementMovimientoCaja = null;
            try {
                DBUtil.getConnection().setAutoCommit(false);

                statementValidar = DBUtil.getConnection().prepareStatement("SELECT IdCaja FROM CajaTB WHERE IdUsuario = ? AND Estado = 1");
                statementValidar.setString(1, Session.USER_ID);
                if (statementValidar.executeQuery().next()) {

                    ResultSet resultSet = statementValidar.executeQuery();
                    resultSet.next();
                    String idCaja = resultSet.getString("IdCaja");

                    codigoMovimiento = DBUtil.getConnection().prepareCall("{? = call Fc_MovimientoInventario_Codigo_Alfanumerico()}");
                    codigoMovimiento.registerOutParameter(1, java.sql.Types.VARCHAR);
                    codigoMovimiento.execute();
                    String idMovimiento = codigoMovimiento.getString(1);

                    statementMovimiento = DBUtil.getConnection().prepareStatement("INSERT INTO "
                            + "MovimientoInventarioTB("
                            + "IdMovimientoInventario,"
                            + "Fecha,"
                            + "Hora,"
                            + "TipoAjuste,"
                            + "TipoMovimiento,"
                            + "Observacion,"
                            + "Suministro,"
                            + "Articulo,"
                            + "Proveedor,"
                            + "Estado)"
                            + "VALUES(?,?,?,?,?,?,?,?,?,?)");
                    statementMovimiento.setString(1, idMovimiento);
                    statementMovimiento.setString(2, inventarioTB.getFecha());
                    statementMovimiento.setString(3, inventarioTB.getHora());
                    statementMovimiento.setBoolean(4, inventarioTB.isTipoAjuste());
                    statementMovimiento.setInt(5, inventarioTB.getTipoMovimiento());
                    statementMovimiento.setString(6, inventarioTB.getObservacion());
                    statementMovimiento.setBoolean(7, inventarioTB.isSuministro());
                    statementMovimiento.setBoolean(8, inventarioTB.isArticulo());
                    statementMovimiento.setString(9, inventarioTB.getProveedor() == null ? "" : inventarioTB.getProveedor());
                    statementMovimiento.setShort(10, inventarioTB.getEstado());
                    statementMovimiento.addBatch();

                    suministro_update = inventarioTB.isTipoAjuste() ? DBUtil.getConnection().prepareStatement("UPDATE SuministroTB SET Cantidad = Cantidad + ? WHERE IdSuministro = ?")
                            : DBUtil.getConnection().prepareStatement("UPDATE SuministroTB SET Cantidad = Cantidad - ? WHERE IdSuministro = ?");

                    suministro_kardex = DBUtil.getConnection().prepareStatement("INSERT INTO "
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

                    statementMovimientoDetalle = DBUtil.getConnection().prepareStatement("INSERT INTO "
                            + "MovimientoInventarioDetalleTB("
                            + "IdMovimientoInventario,"
                            + "IdSuministro,"
                            + "Cantidad,"
                            + "Costo,"
                            + "Precio)"
                            + "VALUES(?,?,?,?,?)");

                    for (int i = 0; i < tableView.getItems().size(); i++) {
                        if (tableView.getItems().get(i).getValidar().isSelected()) {
                            statementMovimientoDetalle.setString(1, idMovimiento);
                            statementMovimientoDetalle.setString(2, tableView.getItems().get(i).getIdSuministro());
                            statementMovimientoDetalle.setDouble(3, tableView.getItems().get(i).getMovimiento());
                            statementMovimientoDetalle.setDouble(4, tableView.getItems().get(i).getCostoCompra());
                            statementMovimientoDetalle.setDouble(5, tableView.getItems().get(i).getPrecioVentaGeneral());
                            statementMovimientoDetalle.addBatch();

                            if (inventarioTB.getEstado() == 1) {
                                suministro_update.setDouble(1, tableView.getItems().get(i).getMovimiento());
                                suministro_update.setString(2, tableView.getItems().get(i).getIdSuministro());
                                suministro_update.addBatch();

                                suministro_kardex.setString(1, tableView.getItems().get(i).getIdSuministro());
                                suministro_kardex.setString(2, Tools.getDate());
                                suministro_kardex.setString(3, Tools.getHour());
                                suministro_kardex.setShort(4, inventarioTB.isTipoAjuste() ? (short) 1 : (short) 2);
                                suministro_kardex.setInt(5, inventarioTB.getTipoMovimiento());
                                suministro_kardex.setString(6, inventarioTB.getObservacion());
                                suministro_kardex.setDouble(7, tableView.getItems().get(i).getMovimiento());
                                suministro_kardex.setDouble(8, tableView.getItems().get(i).getCostoCompra());
                                suministro_kardex.setDouble(9, tableView.getItems().get(i).getMovimiento() * tableView.getItems().get(i).getCostoCompra());
                                suministro_kardex.addBatch();
                            }
                        }
                    }

                    statementMovimientoCaja = DBUtil.getConnection().prepareStatement("INSERT INTO MovimientoCajaTB(IdCaja,FechaMovimiento,HoraMovimiento,Comentario,TipoMovimiento,Monto)VALUES(?,?,?,?,?,?)");
                    statementMovimientoCaja.setString(1, idCaja);
                    statementMovimientoCaja.setString(2, movimientoCajaTB.getFechaMovimiento());
                    statementMovimientoCaja.setString(3, movimientoCajaTB.getHoraMovimiento());
                    statementMovimientoCaja.setString(4, movimientoCajaTB.getComentario());
                    statementMovimientoCaja.setShort(5, movimientoCajaTB.getTipoMovimiento());
                    statementMovimientoCaja.setDouble(6, movimientoCajaTB.getMonto());
                    statementMovimientoCaja.addBatch();

                    statementMovimiento.executeBatch();
                    statementMovimientoDetalle.executeBatch();
                    suministro_update.executeBatch();
                    suministro_kardex.executeBatch();
                    statementMovimientoCaja.executeBatch();
                    DBUtil.getConnection().commit();
                    result = "registered";
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
                    if (statementMovimiento != null) {
                        statementMovimiento.close();
                    }
                    if (statementMovimientoDetalle != null) {
                        statementMovimientoDetalle.close();
                    }
                    if (codigoMovimiento != null) {
                        codigoMovimiento.close();
                    }
                    if (suministro_update != null) {
                        suministro_update.close();
                    }
                    if (suministro_kardex != null) {
                        suministro_kardex.close();
                    }
                    if (statementMovimientoCaja != null) {
                        statementMovimientoCaja.close();
                    }
                    DBUtil.dbDisconnect();
                } catch (SQLException ex) {
                    result = ex.getLocalizedMessage();
                }
            }
        } else {
            result = "No se puedo completar la petición por un problema con su conexión, intente nuevamente.";
        }
        return result;
    }

    public static ObservableList<MovimientoInventarioTB> ListMovimientoInventario(boolean init, short opcion, int movimiento, String fechaInicial, String fechaFinal, AnchorPane vbPrincipal, HBox hbWindow) {
        String selectStmt = "{call Sp_Listar_Movimiento_Inventario(?,?,?,?,?)}";
        PreparedStatement preparedStatement = null;
        ResultSet rsEmps = null;
        ObservableList<MovimientoInventarioTB> empList = FXCollections.observableArrayList();
        try {
            DBUtil.dbConnect();
            preparedStatement = DBUtil.getConnection().prepareStatement(selectStmt);
            preparedStatement.setBoolean(1, init);
            preparedStatement.setShort(2, opcion);
            preparedStatement.setInt(3, movimiento);
            preparedStatement.setString(4, fechaInicial);
            preparedStatement.setString(5, fechaFinal);
            rsEmps = preparedStatement.executeQuery();
            while (rsEmps.next()) {
                MovimientoInventarioTB movimientoInventarioTB = new MovimientoInventarioTB();
                movimientoInventarioTB.setId(rsEmps.getRow());
                movimientoInventarioTB.setIdMovimientoInventario(rsEmps.getString("IdMovimientoInventario"));
                movimientoInventarioTB.setFecha(rsEmps.getDate("Fecha").toLocalDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)));
                movimientoInventarioTB.setHora(rsEmps.getTime("Hora").toLocalTime().format(DateTimeFormatter.ofPattern("hh:mm:ss a")));
                movimientoInventarioTB.setTipoAjuste(rsEmps.getBoolean("TipoAjuste"));
                movimientoInventarioTB.setTipoMovimientoName(rsEmps.getString("TipoMovimiento"));
                movimientoInventarioTB.setObservacion(rsEmps.getString("Observacion"));
                movimientoInventarioTB.setInformacion(rsEmps.getString("Informacion"));
                movimientoInventarioTB.setProveedor(rsEmps.getString("Proveedor").toUpperCase());
                movimientoInventarioTB.setEstadoName(rsEmps.getString("Estado"));

                Label label = new Label(movimientoInventarioTB.getEstadoName());
                if (movimientoInventarioTB.getEstadoName().equalsIgnoreCase("EN PROCESO")) {
                    label.getStyleClass().add("label-medio");
                } else if (movimientoInventarioTB.getEstadoName().equalsIgnoreCase("COMPLETADO")) {
                    label.getStyleClass().add("label-asignacion");
                } else if (movimientoInventarioTB.getEstadoName().equalsIgnoreCase("CANCELADO")) {
                    label.getStyleClass().add("label-proceso");
                }

                movimientoInventarioTB.setLblEstado(label);

                Button btn = new Button();
                btn.getStyleClass().add("buttonLightWarning");
                btn.setText("Ver");
                btn.setOnAction((e) -> {
                    openWindowMovimientoDetalle(opcion, movimientoInventarioTB.getIdMovimientoInventario(), vbPrincipal, hbWindow);
                });
                movimientoInventarioTB.setValidar(btn);
                empList.add(movimientoInventarioTB);
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

    public static void openWindowMovimientoDetalle(short opcion, String idMovimiento, AnchorPane vbPrincipal, HBox hbWindow) {
        try {
            ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
            URL url = FxMovimientosController.class.getClassLoader().getClass().getResource(FilesRouters.FX_MOVIMIENTOS_DETALLE);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxMovimientosDetalleController controller = fXMLLoader.getController();
            controller.setIniciarCarga(opcion, idMovimiento);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Detalle del movimiento", hbWindow.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding((w) -> {
                vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
            });
            stage.show();

        } catch (IOException ex) {
            System.out.println(ex.getLocalizedMessage());
        }
    }

    public static ArrayList<Object> Obtener_Movimiento_Inventario_By_Id(short type, String idMovimiento) {
        ArrayList<Object> list = new ArrayList<>();
        DBUtil.dbConnect();
        if (DBUtil.getConnection() != null) {
            PreparedStatement preparedStatement = null;
            PreparedStatement preparedStatementList = null;

            MovimientoInventarioTB inventarioTB = null;
            ObservableList<MovimientoInventarioDetalleTB> empList = FXCollections.observableArrayList();
            try {
                preparedStatement = DBUtil.getConnection().prepareStatement("{call Sp_Get_Movimiento_Inventario_By_Id(?)}");
                preparedStatement.setString(1, idMovimiento);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    inventarioTB = new MovimientoInventarioTB();
                    inventarioTB.setTipoMovimientoName(resultSet.getString("TipoMovimiento"));
                    inventarioTB.setFecha(resultSet.getString("Fecha"));
                    inventarioTB.setHora(resultSet.getTime("Hora").toLocalTime().format(DateTimeFormatter.ofPattern("hh:mm:ss a")));
                    inventarioTB.setObservacion(resultSet.getString("Observacion"));
                    inventarioTB.setProveedor(resultSet.getString("Proveedor").toUpperCase());
                    inventarioTB.setEstadoName(resultSet.getString("Estado"));
                }
                list.add(inventarioTB);

                preparedStatementList = type == 1
                        ? DBUtil.getConnection().prepareStatement("SELECT m.IdMovimientoInventario,m.IdSuministro,s.Clave,s.NombreMarca,m.Cantidad,m.Costo,m.Precio \n"
                                + "FROM MovimientoInventarioDetalleTB AS m INNER JOIN SuministroTB AS s ON m.IdSuministro = s.IdSuministro \n"
                                + "WHERE m.IdMovimientoInventario = ?")
                        : DBUtil.getConnection().prepareStatement("SELECT m.IdMovimientoInventario,m.IdSuministro,s.Clave,s.NombreMarca,m.Cantidad,m.Costo,m.Precio \n"
                                + "FROM MovimientoInventarioDetalleTB AS m INNER JOIN ArticuloTB AS s ON m.IdSuministro = s.IdArticulo \n"
                                + "WHERE m.IdMovimientoInventario = ?");

                preparedStatementList.setString(1, idMovimiento);
                ResultSet rsEmps = preparedStatementList.executeQuery();
                while (rsEmps.next()) {
                    MovimientoInventarioDetalleTB detalleTB = new MovimientoInventarioDetalleTB();
                    detalleTB.setId(rsEmps.getRow());
                    detalleTB.setIdMovimientoInventario(rsEmps.getString("IdMovimientoInventario"));
                    detalleTB.setIdSuministro(rsEmps.getString("IdSuministro"));
                    detalleTB.setCantidad(rsEmps.getDouble("Cantidad"));
                    detalleTB.setCosto(rsEmps.getDouble("Costo"));
                    detalleTB.setPrecio(rsEmps.getDouble("Precio"));

                    CheckBox checkBox = new CheckBox();
                    checkBox.setDisable(true);
                    checkBox.getStyleClass().add("check-box-movimiento");
                    detalleTB.setVerificar(checkBox);

                    CheckBox checkBoxPrecios = new CheckBox();
                    checkBoxPrecios.getStyleClass().add("check-box-movimiento");
                    detalleTB.setActualizarPrecio(checkBoxPrecios);

                    SuministroTB suministroTB = new SuministroTB();
                    suministroTB.setClave(rsEmps.getString("Clave"));
                    suministroTB.setNombreMarca(rsEmps.getString("NombreMarca"));
                    detalleTB.setSuministroTB(suministroTB);
                    empList.add(detalleTB);
                }

                list.add(empList);

            } catch (SQLException ex) {
                System.out.println("Error en:" + ex.getLocalizedMessage());
            } finally {
                try {
                    if (preparedStatement != null) {
                        preparedStatement.close();
                    }
                    if (preparedStatementList != null) {
                        preparedStatementList.close();
                    }
                    DBUtil.dbDisconnect();
                } catch (SQLException ex) {

                }
            }
        }
        return list;
    }
}
