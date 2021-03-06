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
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class NotaCreditoADO {

    public static Object ListarComprobanteParaNotaCredito(String comprobante) {
        PreparedStatement statementNotaCredito = null;
        try {
            DBUtil.dbConnect();

            Object[] objects = new Object[7];

            statementNotaCredito = DBUtil.getConnection().prepareStatement("SELECT IdTipoDocumento,Nombre FROM TipoDocumentoTB WHERE NotaCredito = 1");
            ArrayList<TipoDocumentoTB> arrayNotaCredito = new ArrayList<>();
            try (ResultSet resultSet = statementNotaCredito.executeQuery()) {
                while (resultSet.next()) {
                    arrayNotaCredito.add(new TipoDocumentoTB(resultSet.getInt("IdTipoDocumento"), resultSet.getString("Nombre")));
                }
            }

            statementNotaCredito = DBUtil.getConnection().prepareStatement("SELECT IdMoneda,Nombre,Predeterminado FROM MonedaTB");
            ArrayList<MonedaTB> arrayMoneda = new ArrayList<>();
            try (ResultSet resultSet = statementNotaCredito.executeQuery()) {
                while (resultSet.next()) {
                    arrayMoneda.add(new MonedaTB(resultSet.getInt("IdMoneda"), resultSet.getString("Nombre")));
                }
            }

            statementNotaCredito = DBUtil.getConnection().prepareStatement("SELECT IdTipoDocumento,Nombre FROM TipoDocumentoTB");
            ArrayList<TipoDocumentoTB> arrayTipoComprobante = new ArrayList<>();
            try (ResultSet resultSet = statementNotaCredito.executeQuery()) {
                while (resultSet.next()) {
                    arrayTipoComprobante.add(new TipoDocumentoTB(resultSet.getInt("IdTipoDocumento"), resultSet.getString("Nombre")));
                }
            }

            statementNotaCredito = DBUtil.getConnection().prepareStatement("SELECT IdDetalle,Nombre FROM DetalleTB WHERE IdMantenimiento = '0019'");
            ArrayList<DetalleTB> arrayMotivoAnulacion = new ArrayList<>();
            try (ResultSet resultSet = statementNotaCredito.executeQuery()) {
                while (resultSet.next()) {
                    arrayMotivoAnulacion.add(new DetalleTB(resultSet.getInt("IdDetalle"), resultSet.getString("Nombre")));
                }
            }

            statementNotaCredito = DBUtil.getConnection().prepareStatement("SELECT IdDetalle,Nombre FROM DetalleTB WHERE IdMantenimiento = '0003'");
            ArrayList<DetalleTB> arrayTipoDocumento = new ArrayList<>();
            try (ResultSet resultSet = statementNotaCredito.executeQuery()) {
                while (resultSet.next()) {
                    arrayTipoDocumento.add(new DetalleTB(resultSet.getInt("IdDetalle"), resultSet.getString("Nombre")));
                }
            }

            statementNotaCredito = DBUtil.getConnection().prepareStatement("SELECT \n"
                    + "            v.IdVenta,\n"
                    + "            v.Comprobante,\n"
                    + "            v.Moneda,\n"
                    + "            v.Serie,\n"
                    + "            v.Numeracion,\n"
                    + "            c.IdCliente,\n"
                    + "            c.TipoDocumento,\n"
                    + "            c.NumeroDocumento,\n"
                    + "            c.Informacion,\n"
                    + "            c.Direccion,\n"
                    + "            c.Celular,\n"
                    + "            c.Email \n"
                    + "            FROM VentaTB AS v INNER JOIN ClienteTB AS c ON c.IdCliente = v.Cliente\n"
                    + "            WHERE CONCAT(v.Serie,'-',v.Numeracion) = ? ");
            statementNotaCredito.setString(1, comprobante);
            VentaTB ventaTB = null;
            try (ResultSet resultSet = statementNotaCredito.executeQuery()) {
                if (resultSet.next()) {
                    ventaTB = new VentaTB();
                    ventaTB.setIdVenta(resultSet.getString("IdVenta"));
                    ventaTB.setIdComprobante(resultSet.getInt("Comprobante"));
                    ventaTB.setIdMoneda(resultSet.getInt("Moneda"));
                    ventaTB.setSerie(resultSet.getString("Serie"));
                    ventaTB.setNumeracion(resultSet.getString("Numeracion"));
                    ClienteTB clienteTB = new ClienteTB();
                    clienteTB.setIdCliente(resultSet.getString("IdCliente"));
                    clienteTB.setTipoDocumento(resultSet.getInt("TipoDocumento"));
                    clienteTB.setNumeroDocumento(resultSet.getString("NumeroDocumento"));
                    clienteTB.setInformacion(resultSet.getString("Informacion"));
                    clienteTB.setDireccion(resultSet.getString("Direccion"));
                    clienteTB.setCelular(resultSet.getString("Celular"));
                    clienteTB.setEmail(resultSet.getString("Email"));
                    ventaTB.setClienteTB(clienteTB);
                } else {
                    throw new NullPointerException("No se encontro ning??n comprobante");
                }
            }

            statementNotaCredito = DBUtil.getConnection().prepareStatement("SELECT \n"
                    + "            dv.IdVenta,\n"
                    + "            dv.IdArticulo,\n"
                    + "            s.Clave,\n"
                    + "            s.NombreMarca,\n"
                    + "            isnull(d.Nombre,'') UnidadMarca,\n"
                    + "            dv.Cantidad,\n"
                    + "            dv.CostoVenta,\n"
                    + "            dv.PrecioVenta,\n"
                    + "            dv.Descuento,\n"
                    + "            dv.ValorImpuesto,\n"
                    + "            dv.NombreImpuesto, \n"
                    + "            s.Inventario, \n"
                    + "            s.ValorInventario \n"
                    + "            FROM DetalleVentaTB AS dv \n"
                    + "            INNER JOIN SuministroTB AS s ON s.IdSuministro = dv.IdArticulo\n"
                    + "            LEFT JOIN DetalleTB AS d ON d.IdDetalle = s.UnidadCompra AND d.IdMantenimiento = '0013'\n"
                    + "            WHERE dv.IdVenta = ?");
            statementNotaCredito.setString(1, ventaTB.getIdVenta());
            ArrayList<DetalleVentaTB> arrayDetalleVenta = new ArrayList<>();
            try (ResultSet resultSet = statementNotaCredito.executeQuery()) {
                while (resultSet.next()) {
                    DetalleVentaTB detalleVentaTB = new DetalleVentaTB();
                    detalleVentaTB.setIdVenta(resultSet.getString("IdVenta"));
                    detalleVentaTB.setIdArticulo(resultSet.getString("IdArticulo"));
                    SuministroTB suministroTB = new SuministroTB();
                    suministroTB.setClave(resultSet.getString("Clave"));
                    suministroTB.setNombreMarca(resultSet.getString("NombreMarca"));
                    suministroTB.setUnidadCompraName(resultSet.getString("UnidadMarca"));
                    suministroTB.setCantidad(resultSet.getDouble("Cantidad"));
                    suministroTB.setPrecioVentaGeneral(resultSet.getDouble("PrecioVenta"));
                    suministroTB.setCostoCompra(resultSet.getDouble("CostoVenta"));
                    suministroTB.setDescuento(resultSet.getDouble("Descuento"));
                    suministroTB.setImpuestoValor(resultSet.getDouble("ValorImpuesto"));
                    suministroTB.setImpuestoNombre(resultSet.getString("NombreImpuesto"));
                    suministroTB.setInventario(resultSet.getBoolean("Inventario"));
                    suministroTB.setValorInventario(resultSet.getShort("ValorInventario"));
                    detalleVentaTB.setSuministroTB(suministroTB);

                    Button button = new Button();
                    button.setDisable(true);
                    button.getStyleClass().add("buttonLightError");
                    button.setAlignment(Pos.CENTER);
                    button.setPrefWidth(Control.USE_COMPUTED_SIZE);
                    button.setPrefHeight(Control.USE_COMPUTED_SIZE);
//                    button.setMaxWidth(Double.MAX_VALUE);
//                    button.setMaxHeight(Double.MAX_VALUE);
                    ImageView imageView = new ImageView(new Image("/view/image/remove-gray.png"));
                    imageView.setFitWidth(20);
                    imageView.setFitHeight(20);
                    button.setGraphic(imageView);
                    detalleVentaTB.setBtnRemove(button);
                    arrayDetalleVenta.add(detalleVentaTB);
                }
            }

            objects[0] = arrayNotaCredito;
            objects[1] = arrayMoneda;
            objects[2] = arrayTipoComprobante;
            objects[3] = arrayMotivoAnulacion;
            objects[4] = arrayTipoDocumento;
            objects[5] = ventaTB;
            objects[6] = arrayDetalleVenta;

            return objects;
        } catch (SQLException | NullPointerException ex) {
            return ex.getLocalizedMessage();
        } finally {
            try {
                if (statementNotaCredito != null) {
                    statementNotaCredito.close();
                }
            } catch (SQLException ex) {
                return ex.getLocalizedMessage();
            }

        }

    }

    public static Object ListarNotasCredito(int opcion, String buscar, String fechaInico, String fechaFinal, int posicionPagina, int filasPorPagina) {
        PreparedStatement statementNotaCredito = null;
        ResultSet resultSet = null;
        try {
            DBUtil.dbConnect();
            Object[] objects = new Object[2];
            ObservableList<NotaCreditoTB> notaCreditoTBs = FXCollections.observableArrayList();
            statementNotaCredito = DBUtil.getConnection().prepareStatement("{CALL Sp_Listar_NotaCredito(?,?,?,?,?,?)}");
            statementNotaCredito.setInt(1, opcion);
            statementNotaCredito.setString(2, buscar);
            statementNotaCredito.setString(3, fechaInico);
            statementNotaCredito.setString(4, fechaFinal);
            statementNotaCredito.setInt(5, posicionPagina);
            statementNotaCredito.setInt(6, filasPorPagina);
            resultSet = statementNotaCredito.executeQuery();
            while (resultSet.next()) {
                NotaCreditoTB notaCreditoTB = new NotaCreditoTB();
                notaCreditoTB.setId(resultSet.getRow() + posicionPagina);
                notaCreditoTB.setIdVenta(resultSet.getString("IdVenta"));
                notaCreditoTB.setIdNotaCredito(resultSet.getString("IdNotaCredito"));
                notaCreditoTB.setFechaRegistro(resultSet.getDate("FechaRegistro").toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                notaCreditoTB.setHoraRegistro(resultSet.getTime("HoraRegistro").toLocalTime().format(DateTimeFormatter.ofPattern("hh:mm:ss a")));
                notaCreditoTB.setNombreComprobante(resultSet.getString("NotaCreditoComprobante"));
                notaCreditoTB.setSerie(resultSet.getString("SerieNotaCredito"));
                notaCreditoTB.setNumeracion(resultSet.getString("NumeracioNotaCredito"));

                ClienteTB clienteTB = new ClienteTB();
                clienteTB.setNumeroDocumento(resultSet.getString("NumeroDocumento"));
                clienteTB.setInformacion(resultSet.getString("Informacion"));
                notaCreditoTB.setClienteTB(clienteTB);

                VentaTB ventaTB = new VentaTB();
                ventaTB.setComprobanteName(resultSet.getString("VentaComprobante"));
                ventaTB.setSerie(resultSet.getString("SerieVenta"));
                ventaTB.setNumeracion(resultSet.getString("NumeracionVenta"));
                notaCreditoTB.setVentaTB(ventaTB);

                MonedaTB monedaTB = new MonedaTB();
                monedaTB.setSimbolo(resultSet.getString("Simbolo"));
                notaCreditoTB.setMonedaTB(monedaTB);

                notaCreditoTB.setImporteNeto(resultSet.getDouble("Total"));
                notaCreditoTBs.add(notaCreditoTB);
            }

            statementNotaCredito = DBUtil.getConnection().prepareStatement("{CALL Sp_Listar_NotaCredito_Count(?,?,?,?)}");
            statementNotaCredito.setInt(1, opcion);
            statementNotaCredito.setString(2, buscar);
            statementNotaCredito.setString(3, fechaInico);
            statementNotaCredito.setString(4, fechaFinal);
            resultSet = statementNotaCredito.executeQuery();
            Integer cantidadTotal = 0;
            if (resultSet.next()) {
                cantidadTotal = resultSet.getInt("Total");
            }

            objects[0] = notaCreditoTBs;
            objects[1] = cantidadTotal;
            return objects;
        } catch (SQLException ex) {
            return ex.getLocalizedMessage();
        } finally {
            try {
                if (statementNotaCredito != null) {
                    statementNotaCredito.close();
                }
                if (resultSet != null) {
                    resultSet.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException ex) {
                return ex.getLocalizedMessage();
            }
        }
    }

    public static Object DetalleNotaCreditoById(String idNotaCredito) {
        PreparedStatement statementNotaCredito = null;
        ResultSet resultSet = null;
        try {
            DBUtil.dbConnect();
            statementNotaCredito = DBUtil.getConnection().prepareStatement("{CALL Sp_Obtener_NotaCredito_ById(?)}");
            statementNotaCredito.setString(1, idNotaCredito);
            resultSet = statementNotaCredito.executeQuery();
            NotaCreditoTB notaCreditoTB = null;
            if (resultSet.next()) {
                notaCreditoTB = new NotaCreditoTB();
                notaCreditoTB.setId(resultSet.getRow());
                notaCreditoTB.setIdVenta(resultSet.getString("IdVenta"));
                notaCreditoTB.setCodigoAlterno(resultSet.getString("CodigoAlterno"));
                notaCreditoTB.setIdNotaCredito(resultSet.getString("IdNotaCredito"));
                notaCreditoTB.setFechaRegistro(resultSet.getDate("FechaRegistro").toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                notaCreditoTB.setHoraRegistro(resultSet.getTime("HoraRegistro").toLocalTime().format(DateTimeFormatter.ofPattern("hh:mm:ss a")));
                notaCreditoTB.setNombreComprobante(resultSet.getString("NotaCreditoComprobante"));
                notaCreditoTB.setSerie(resultSet.getString("SerieNotaCredito"));
                notaCreditoTB.setNumeracion(resultSet.getString("NumeracioNotaCredito"));
                notaCreditoTB.setNombreMotivo(resultSet.getString("MotivoAnulacion"));

                MonedaTB monedaTB = new MonedaTB();
                monedaTB.setNombre(resultSet.getString("Moneda"));
                monedaTB.setAbreviado(resultSet.getString("Abreviado"));
                monedaTB.setSimbolo(resultSet.getString("Simbolo"));
                notaCreditoTB.setMonedaTB(monedaTB);

                ClienteTB clienteTB = new ClienteTB();
                clienteTB.setTipoDocumentoName(resultSet.getString("TipoDocumento"));
                clienteTB.setIdAuxiliar(resultSet.getString("IdAuxiliar"));
                clienteTB.setNumeroDocumento(resultSet.getString("NumeroDocumento"));
                clienteTB.setInformacion(resultSet.getString("Informacion"));
                clienteTB.setCelular(resultSet.getString("Celular"));
                clienteTB.setTelefono(resultSet.getString("Telefono"));
                clienteTB.setEmail(resultSet.getString("Email"));
                clienteTB.setDireccion(resultSet.getString("Direccion"));
                notaCreditoTB.setClienteTB(clienteTB);

                VentaTB ventaTB = new VentaTB();
                ventaTB.setComprobanteName(resultSet.getString("VentaComprobante"));
                ventaTB.setSerie(resultSet.getString("SerieVenta"));
                ventaTB.setNumeracion(resultSet.getString("NumeracionVenta"));
                notaCreditoTB.setVentaTB(ventaTB);

                statementNotaCredito = DBUtil.getConnection().prepareStatement("{CALL Sp_Obtener_NotaCredito_Detalle_ById(?)}");
                statementNotaCredito.setString(1, idNotaCredito);
                resultSet = statementNotaCredito.executeQuery();
                ArrayList<NotaCreditoDetalleTB> creditoDetalleTBs = new ArrayList<>();
                while (resultSet.next()) {
                    NotaCreditoDetalleTB notaCreditoDetalleTB = new NotaCreditoDetalleTB();
                    notaCreditoDetalleTB.setId(resultSet.getRow());
                    SuministroTB suministroTB = new SuministroTB();
                    suministroTB.setClave(resultSet.getString("Clave"));
                    suministroTB.setNombreMarca(resultSet.getString("NombreMarca"));
                    suministroTB.setUnidadCompraName(resultSet.getString("Unidad"));

                    notaCreditoDetalleTB.setCantidad(resultSet.getDouble("Cantidad"));
                    notaCreditoDetalleTB.setPrecio(resultSet.getDouble("Precio"));
                    notaCreditoDetalleTB.setDescuento(resultSet.getDouble("Descuento"));
                    notaCreditoDetalleTB.setValorImpuesto(resultSet.getDouble("ValorImpuesto"));
                    notaCreditoDetalleTB.setImporte(notaCreditoDetalleTB.getCantidad() * (notaCreditoDetalleTB.getPrecio() - notaCreditoDetalleTB.getDescuento()));

                    notaCreditoDetalleTB.setSuministroTB(suministroTB);
                    creditoDetalleTBs.add(notaCreditoDetalleTB);
                }
                notaCreditoTB.setNotaCreditoDetalleTBs(creditoDetalleTBs);
            }

            return notaCreditoTB;
        } catch (SQLException ex) {
            return ex.getLocalizedMessage();
        } finally {
            try {
                if (statementNotaCredito != null) {
                    statementNotaCredito.close();
                }
                if (resultSet != null) {
                    resultSet.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException ex) {
                return ex.getLocalizedMessage();
            }
        }
    }

    public static String Registrar_NotaCredito(NotaCreditoTB notaCreditoTB) {
        PreparedStatement statementValidate = null;
        PreparedStatement statementNotaCredito = null;
        PreparedStatement statementComprobante = null;
        PreparedStatement statementDetalle = null;
        PreparedStatement statementSuministro = null;
        PreparedStatement statementKardex = null;
        PreparedStatement statementIngreso = null;
        CallableStatement statementCodigoNotaCredito = null;
        CallableStatement statementCodigoSerieNumeracion = null;
        try {
            DBUtil.dbConnect();
            DBUtil.getConnection().setAutoCommit(false);

            statementValidate = DBUtil.getConnection().prepareStatement("SELECT * FROM NotaCreditoTB WHERE IdVenta = ?");
            statementValidate.setString(1, notaCreditoTB.getIdVenta());
            if (statementValidate.executeQuery().next()) {
                DBUtil.getConnection().rollback();
                return "nota";
            } else {
                statementCodigoNotaCredito = DBUtil.getConnection().prepareCall("{? = call Fc_NotaCredito_Codigo_Alfanumerico()}");
                statementCodigoNotaCredito.registerOutParameter(1, java.sql.Types.VARCHAR);
                statementCodigoNotaCredito.execute();
                String idNotaCredito = statementCodigoNotaCredito.getString(1);

                statementCodigoSerieNumeracion = DBUtil.getConnection().prepareCall("{? = call Fc_Serie_Numero(?)}");
                statementCodigoSerieNumeracion.registerOutParameter(1, java.sql.Types.VARCHAR);
                statementCodigoSerieNumeracion.setInt(2, notaCreditoTB.getIdComprobante());
                statementCodigoSerieNumeracion.execute();
                String[] id_comprabante = statementCodigoSerieNumeracion.getString(1).split("-");

                statementNotaCredito = DBUtil.getConnection().prepareStatement("INSERT INTO NotaCreditoTB(\n"
                        + "IdNotaCredito,\n"
                        + "Vendedor,\n"
                        + "Cliente,\n"
                        + "Comprobante,\n"
                        + "Moneda,\n"
                        + "Serie,\n"
                        + "Numeracion,\n"
                        + "Motivo,\n"
                        + "FechaRegistro,\n"
                        + "HoraRegistro,\n"
                        + "IdVenta,\n"
                        + "Estado)\n"
                        + "VALUES(?,?,?,?,?,?,?,?,?,?,?,?)");
                statementNotaCredito.setString(1, idNotaCredito);
                statementNotaCredito.setString(2, notaCreditoTB.getIdVendedor());
                statementNotaCredito.setString(3, notaCreditoTB.getIdCliente());
                statementNotaCredito.setInt(4, notaCreditoTB.getIdComprobante());
                statementNotaCredito.setInt(5, notaCreditoTB.getIdMoneda());
                statementNotaCredito.setString(6, id_comprabante[0]);
                statementNotaCredito.setString(7, id_comprabante[1]);
                statementNotaCredito.setInt(8, notaCreditoTB.getIdMotivo());
                statementNotaCredito.setString(9, notaCreditoTB.getFechaRegistro());
                statementNotaCredito.setString(10, notaCreditoTB.getHoraRegistro());
                statementNotaCredito.setString(11, notaCreditoTB.getIdVenta());
                statementNotaCredito.setInt(12, notaCreditoTB.getEstado());
                statementNotaCredito.addBatch();

                statementComprobante = DBUtil.getConnection().prepareStatement("INSERT INTO ComprobanteTB(IdTipoDocumento,Serie,Numeracion,FechaRegistro)VALUES(?,?,?,?)");
                statementComprobante.setInt(1, notaCreditoTB.getIdComprobante());
                statementComprobante.setString(2, id_comprabante[0]);
                statementComprobante.setString(3, id_comprabante[1]);
                statementComprobante.setString(4, notaCreditoTB.getFechaRegistro());
                statementComprobante.addBatch();
                statementComprobante.executeBatch();

                statementDetalle = DBUtil.getConnection().prepareStatement("INSERT INTO NotaCreditoDetalleTB("
                        + "IdNotaCredito,\n"
                        + "IdSuministro,\n"
                        + "Cantidad,\n"
                        + "Precio,\n"
                        + "Descuento,\n"
                        + "ValorImpuesto)\n "
                        + "VALUES(?,?,?,?,?,?)");

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

                for (NotaCreditoDetalleTB detalleTB : notaCreditoTB.getNotaCreditoDetalleTBs()) {
                    statementDetalle.setString(1, idNotaCredito);
                    statementDetalle.setString(2, detalleTB.getIdSuministro());
                    statementDetalle.setDouble(3, detalleTB.getCantidad());
                    statementDetalle.setDouble(4, detalleTB.getPrecio());
                    statementDetalle.setDouble(5, detalleTB.getDescuento());
                    statementDetalle.setDouble(6, detalleTB.getValorImpuesto());
                    statementDetalle.addBatch();

                    if (detalleTB.getSuministroTB().isInventario() && detalleTB.getSuministroTB().getValorInventario() == 1) {
                        statementSuministro.setDouble(1, detalleTB.getCantidad());
                        statementSuministro.setString(2, detalleTB.getIdSuministro());
                        statementSuministro.addBatch();
                    } else if (detalleTB.getSuministroTB().isInventario() && detalleTB.getSuministroTB().getValorInventario() == 2) {
                        statementSuministro.setDouble(1, detalleTB.getCantidad());
                        statementSuministro.setString(2, detalleTB.getIdSuministro());
                        statementSuministro.addBatch();
                    } else if (detalleTB.getSuministroTB().isInventario() && detalleTB.getSuministroTB().getValorInventario() == 3) {
                        statementSuministro.setDouble(1, detalleTB.getCantidad());
                        statementSuministro.setString(2, detalleTB.getIdSuministro());
                        statementSuministro.addBatch();
                    }

                    double cantidadTotal = detalleTB.getSuministroTB().getValorInventario() == 1
                            ? detalleTB.getCantidad()
                            : detalleTB.getSuministroTB().getValorInventario() == 2
                            ? detalleTB.getCantidad()
                            : detalleTB.getCantidad();

                    statementKardex.setString(1, detalleTB.getIdSuministro());
                    statementKardex.setString(2, Tools.getDate());
                    statementKardex.setString(3, Tools.getTime());
                    statementKardex.setShort(4, (short) 1);
                    statementKardex.setInt(5, 2);
                    statementKardex.setString(6, "DEVOLUCI??N DE PRODUCTO");
                    statementKardex.setDouble(7, cantidadTotal);
                    statementKardex.setDouble(8, detalleTB.getSuministroTB().getCostoCompra());
                    statementKardex.setDouble(9, cantidadTotal * detalleTB.getSuministroTB().getCostoCompra());
                    statementKardex.addBatch();
                }

                statementIngreso = DBUtil.getConnection().prepareStatement("DELETE FROM IngresoTB WHERE IdProcedencia = ?");
                statementIngreso.setString(1, notaCreditoTB.getIdVenta());
                statementIngreso.addBatch();

                statementNotaCredito.executeBatch();
                statementDetalle.executeBatch();
                statementSuministro.executeBatch();
                statementKardex.executeBatch();
                statementIngreso.executeBatch();

                DBUtil.getConnection().commit();
                return "registrado";
            }
        } catch (SQLException ex) {
            try {
                DBUtil.getConnection().rollback();
            } catch (SQLException e) {
                return e.getLocalizedMessage();
            }
            return ex.getLocalizedMessage();
        } finally {
            try {
                if (statementNotaCredito != null) {
                    statementNotaCredito.close();
                }
                if (statementComprobante != null) {
                    statementComprobante.close();
                }
                if (statementDetalle != null) {
                    statementDetalle.close();
                }
                if (statementCodigoNotaCredito != null) {
                    statementCodigoNotaCredito.close();
                }
                if (statementCodigoSerieNumeracion != null) {
                    statementCodigoSerieNumeracion.close();
                }
                if (statementSuministro != null) {
                    statementSuministro.close();
                }
                if (statementKardex != null) {
                    statementKardex.close();
                }
                if (statementIngreso != null) {
                    statementIngreso.close();
                }
            } catch (SQLException ex) {
                return ex.getLocalizedMessage();
            }
        }
    }

    public static ArrayList<NotaCreditoTB> GetReporteNotaCredito(String fechaInicio, String fechaFinal, int cliente, String idCliente) {
        PreparedStatement statementNotaCredito = null;
        ResultSet resultSet = null;
        ArrayList<NotaCreditoTB> creditoTBs = new ArrayList<>();
        try {
            DBUtil.dbConnect();
            statementNotaCredito = DBUtil.getConnection().prepareStatement("{CALL Sp_Reporte_General_NotaCredito(?,?,?,?)}");
            statementNotaCredito.setString(1, fechaInicio);
            statementNotaCredito.setString(2, fechaFinal);
            statementNotaCredito.setInt(3, cliente);
            statementNotaCredito.setString(4, idCliente);
            resultSet = statementNotaCredito.executeQuery();
            while (resultSet.next()) {
                NotaCreditoTB notaCreditoTB = new NotaCreditoTB();
                notaCreditoTB.setId(resultSet.getRow());
                notaCreditoTB.setIdCliente(resultSet.getString("NumeroDocumento") + "\n" + resultSet.getString("Informacion"));
                notaCreditoTB.setClienteTB(new ClienteTB(resultSet.getString("NumeroDocumento"), resultSet.getString("Informacion")));
                notaCreditoTB.setFechaRegistro(resultSet.getDate("FechaRegistro").toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/YYYY")));
                notaCreditoTB.setSerie(resultSet.getString("Serie"));
                notaCreditoTB.setNumeracion(resultSet.getString("Numeracion"));
                notaCreditoTB.setImporteNeto(resultSet.getDouble("Total"));
                creditoTBs.add(notaCreditoTB);
            }
        } catch (SQLException ex) {
            Tools.println("Error de nota credito: " + ex.getLocalizedMessage());
        } finally {
            try {
                if (statementNotaCredito != null) {
                    statementNotaCredito.close();
                }
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException ex) {

            }
        }
        return creditoTBs;
    }

}
