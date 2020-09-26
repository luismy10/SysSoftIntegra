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
import javafx.scene.control.Label;

public class VentaADO {

    public static String registrarVentaContado(VentaTB ventaTB, ArrayList<SuministroTB> tvList, int idTipoDocumento) {

        CallableStatement serie_numeracion = null;
        CallableStatement codigoCliente = null;
        CallableStatement codigo_venta = null;
        PreparedStatement venta = null;
        PreparedStatement clienteVerificar = null;
        PreparedStatement cliente = null;
        PreparedStatement comprobante = null;
        PreparedStatement detalle_venta = null;
        PreparedStatement suministro_update_unidad = null;
        PreparedStatement suministro_update_granel = null;
        PreparedStatement suministro_kardex = null;
        PreparedStatement movimiento_caja = null;
        try {

            DBUtil.dbConnect();
            DBUtil.getConnection().setAutoCommit(false);

            Random rd = new Random();
            int dig5 = rd.nextInt(90000) + 10000;

            cliente = DBUtil.getConnection().prepareStatement("INSERT INTO ClienteTB(IdCliente,TipoDocumento,NumeroDocumento,Informacion,Telefono,Celular,Email,Direccion,Representante,Estado,Predeterminado,Sistema)VALUES(?,?,?,?,?,?,?,?,?,?,?,?)");

            ventaTB.setCliente(ventaTB.getClienteTB().getIdCliente());

            clienteVerificar = DBUtil.getConnection().prepareStatement("SELECT IdCliente FROM ClienteTB WHERE NumeroDocumento = ?");
            clienteVerificar.setString(1, ventaTB.getClienteTB().getNumeroDocumento().trim());
            if (clienteVerificar.executeQuery().next()) {
                ResultSet resultSet = clienteVerificar.executeQuery();
                resultSet.next();
                ventaTB.setCliente(resultSet.getString("IdCliente"));

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

                ventaTB.setCliente(idCliente);
            }

            serie_numeracion = DBUtil.getConnection().prepareCall("{? = call Fc_Serie_Numero(?)}");
            serie_numeracion.registerOutParameter(1, java.sql.Types.VARCHAR);
            serie_numeracion.setInt(2, idTipoDocumento);
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
                    + ",CantidadGranel\n"
                    + ",CostoVenta\n"
                    + ",PrecioVenta\n"
                    + ",Descuento\n"
                    + ",DescuentoCalculado\n"
                    + ",IdOperacion\n"
                    + ",IdImpuesto\n"
                    + ",NombreImpuesto\n"
                    + ",ValorImpuesto\n"
                    + ",Importe)\n"
                    + "VALUES\n"
                    + "(?,?,?,?,?,?,?,?,?,?,?,?,?)");

            suministro_update_unidad = DBUtil.getConnection().prepareStatement("UPDATE SuministroTB SET Cantidad = Cantidad - ? WHERE IdSuministro = ?");
            suministro_update_granel = DBUtil.getConnection().prepareStatement("UPDATE SuministroTB SET Cantidad = Cantidad - (? / PrecioVentaGeneral) WHERE IdSuministro = ?");

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
            venta.setString(2, ventaTB.getCliente());
            venta.setString(3, ventaTB.getVendedor());
            venta.setInt(4, ventaTB.getComprobante());
            venta.setInt(5, ventaTB.getMoneda());
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

            comprobante.setInt(1, idTipoDocumento);
            comprobante.setString(2, id_comprabante[0]);
            comprobante.setString(3, id_comprabante[1]);
            comprobante.setTimestamp(4, Tools.getDateHour());
            comprobante.addBatch();

            for (int i = 0; i < tvList.size(); i++) {
                detalle_venta.setString(1, id_venta);
                detalle_venta.setString(2, tvList.get(i).getIdSuministro());
                detalle_venta.setDouble(3, tvList.get(i).getCantidad());

                double cantidadGranel = tvList.get(i).getPrecioVentaGeneralAuxiliar() <= 0 ? 0
                        : tvList.get(i).getPrecioVentaGeneralReal() / tvList.get(i).getPrecioVentaGeneralAuxiliar();

                detalle_venta.setDouble(4, cantidadGranel);
                detalle_venta.setDouble(5, tvList.get(i).getCostoCompra());
                detalle_venta.setDouble(6, tvList.get(i).getPrecioVentaGeneralReal());
                detalle_venta.setDouble(7, tvList.get(i).getDescuento());
                detalle_venta.setDouble(8, tvList.get(i).getDescuentoCalculado());
                detalle_venta.setDouble(9, tvList.get(i).getImpuestoOperacion());
                detalle_venta.setDouble(10, tvList.get(i).getImpuestoId());
                detalle_venta.setString(11, tvList.get(i).getImpuestoNombre());
                detalle_venta.setDouble(12, tvList.get(i).getImpuestoValor());
                detalle_venta.setDouble(13, tvList.get(i).getTotalImporte());
                detalle_venta.addBatch();

                if (tvList.get(i).isInventario() && tvList.get(i).getValorInventario() == 1) {
                    suministro_update_unidad.setDouble(1, tvList.get(i).getCantidad());
                    suministro_update_unidad.setString(2, tvList.get(i).getIdSuministro());
                    suministro_update_unidad.addBatch();
                } else if (tvList.get(i).isInventario() && tvList.get(i).getValorInventario() == 2) {
                    suministro_update_granel.setDouble(1, tvList.get(i).getTotalImporte());
                    suministro_update_granel.setString(2, tvList.get(i).getIdSuministro());
                    suministro_update_granel.addBatch();
                } else if (tvList.get(i).isInventario() && tvList.get(i).getValorInventario() == 3) {
                    suministro_update_unidad.setDouble(1, tvList.get(i).getCantidad());
                    suministro_update_unidad.setString(2, tvList.get(i).getIdSuministro());
                    suministro_update_unidad.addBatch();
                }

                double cantidadKardex = tvList.get(i).getValorInventario() == 1
                        ? tvList.get(i).getCantidad()
                        : tvList.get(i).getValorInventario() == 2
                        ? cantidadGranel
                        : tvList.get(i).getCantidad();

                suministro_kardex.setString(1, tvList.get(i).getIdSuministro());
                suministro_kardex.setString(2, Tools.getDate());
                suministro_kardex.setString(3, Tools.getHour());
                suministro_kardex.setShort(4, (short) 2);
                suministro_kardex.setInt(5, 1);
                suministro_kardex.setString(6, "VENTA CON SERIE Y NUMERACIÓN: " + id_comprabante[0] + "-" + id_comprabante[1]);
                suministro_kardex.setDouble(7, cantidadKardex);
                suministro_kardex.setDouble(8, tvList.get(i).getCostoCompra());
                suministro_kardex.setDouble(9, cantidadKardex * tvList.get(i).getCostoCompra());
                suministro_kardex.addBatch();

            }

            movimiento_caja = DBUtil.getConnection().prepareStatement("INSERT INTO MovimientoCajaTB(IdCaja,FechaMovimiento,HoraMovimiento,Comentario,TipoMovimiento,Monto)VALUES(?,?,?,?,?,?)");

            if (ventaTB.getEfectivo() > 0) {
                movimiento_caja.setString(1, Session.CAJA_ID);
                movimiento_caja.setString(2, ventaTB.getFechaVenta());
                movimiento_caja.setString(3, ventaTB.getHoraVenta());
                movimiento_caja.setString(4, "VENTA CON EFECTIVO DE SERIE Y NUMERACIÓN DEL COMPROBANTE " + id_comprabante[0] + "-" + id_comprabante[1]);
                movimiento_caja.setShort(5, (short) 2);
                movimiento_caja.setDouble(6, ventaTB.getTarjeta() > 0 ? ventaTB.getEfectivo() : ventaTB.getTotal());
                movimiento_caja.addBatch();
            }

            if (ventaTB.getTarjeta() > 0) {
                movimiento_caja.setString(1, Session.CAJA_ID);
                movimiento_caja.setString(2, ventaTB.getFechaVenta());
                movimiento_caja.setString(3, ventaTB.getHoraVenta());
                movimiento_caja.setString(4, "VENTA CON TAJETA DE SERIE Y NUMERACIÓN DEL COMPROBANTE  " + id_comprabante[0] + "-" + id_comprabante[1]);
                movimiento_caja.setShort(5, (short) 3);
                movimiento_caja.setDouble(6, ventaTB.getTarjeta());
                movimiento_caja.addBatch();
            }

            cliente.executeBatch();
            venta.executeBatch();
            movimiento_caja.executeBatch();
            comprobante.executeBatch();
            detalle_venta.executeBatch();
            suministro_update_unidad.executeBatch();
            suministro_update_granel.executeBatch();
            suministro_kardex.executeBatch();
            DBUtil.getConnection().commit();
            return "register/" + id_venta;
        } catch (SQLException ex) {
            try {
                DBUtil.getConnection().rollback();
                return ex.getLocalizedMessage() + "/";
            } catch (SQLException ex1) {
                return ex1.getLocalizedMessage() + "/";
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

                if (suministro_update_unidad != null) {
                    suministro_update_unidad.close();
                }
                if (suministro_update_granel != null) {
                    suministro_update_granel.close();
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
    }

    public static ArrayList<Object> ListVentas(short opcion, String value, String fechaInicial, String fechaFinal, int comprobante, int estado, String usuario, int posicionPagina, int filasPorPagina) {
        PreparedStatement preparedStatement = null;
        ResultSet rsEmps = null;
        ArrayList<Object> objects = new ArrayList<>();
        ObservableList<VentaTB> empList = FXCollections.observableArrayList();
        try {
            DBUtil.dbConnect();
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
                ventaTB.setFechaVenta(rsEmps.getDate("FechaVenta").toLocalDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)));
                ventaTB.setHoraVenta(rsEmps.getTime("HoraVenta").toLocalTime().format(DateTimeFormatter.ofPattern("hh:mm:ss a")));
                ventaTB.setCliente(rsEmps.getString("Cliente"));
                ventaTB.setComprobanteName(rsEmps.getString("Comprobante"));
                ventaTB.setSerie(rsEmps.getString("Serie"));
                ventaTB.setNumeracion(rsEmps.getString("Numeracion"));
                ventaTB.setTipoName(rsEmps.getString("Tipo"));
                ventaTB.setEstadoName(rsEmps.getString("Estado"));
                ventaTB.setMonedaName(rsEmps.getString("Simbolo"));
                ventaTB.setTotal(rsEmps.getDouble("Total"));
                ventaTB.setObservaciones(rsEmps.getString("Observaciones"));

                Label label = new Label(ventaTB.getEstadoName());
                if (ventaTB.getEstadoName().equalsIgnoreCase("PAGADO")) {
                    label.getStyleClass().add("label-asignacion");
                } else if (ventaTB.getEstadoName().equalsIgnoreCase("PENDIENTE")) {
                    label.getStyleClass().add("label-medio");
                } else if (ventaTB.getEstadoName().equalsIgnoreCase("ANULADO")) {
                    label.getStyleClass().add("label-proceso");
                }
                ventaTB.setEstadoLabel(label);
                empList.add(ventaTB);
            }
            objects.add(empList);

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
            objects.add(cantidadTotal);

            preparedStatement = DBUtil.getConnection().prepareStatement("select sum(Total) as Total from VentaTB where FechaVenta between ? and ? and Vendedor = ? and Estado = 1");
            preparedStatement.setString(1, fechaInicial);
            preparedStatement.setString(2, fechaFinal);
            preparedStatement.setString(3, usuario);
            rsEmps = preparedStatement.executeQuery();
            double ventalTotal = 0;
            if (rsEmps.next()) {
                ventalTotal = rsEmps.getDouble("Total");
            }
            objects.add(ventalTotal);
        } catch (SQLException ex) {
            System.out.println(ex.getLocalizedMessage());
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (rsEmps != null) {
                    rsEmps.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException e) {
            }
        }
        return objects;
    }

    public static ObservableList<VentaTB> ListVentasMostrar(String search) {
        String selectStmt = "{call Sp_Listar_Ventas_Mostrar(?)}";
        PreparedStatement preparedStatement = null;
        ResultSet rsEmps = null;
        ObservableList<VentaTB> empList = FXCollections.observableArrayList();
        try {
            DBUtil.dbConnect();
            preparedStatement = DBUtil.getConnection().prepareStatement(selectStmt);
            preparedStatement.setString(1, search);
            rsEmps = preparedStatement.executeQuery();
            while (rsEmps.next()) {
                VentaTB ventaTB = new VentaTB();
                ventaTB.setId(rsEmps.getRow());
                ventaTB.setClienteTB(new ClienteTB(rsEmps.getString("Cliente")));
                ventaTB.setIdVenta(rsEmps.getString("IdVenta"));
                ventaTB.setFechaVenta(rsEmps.getDate("FechaVenta").toLocalDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
                ventaTB.setHoraVenta(rsEmps.getTime("HoraVenta").toLocalTime().format(DateTimeFormatter.ofPattern("hh:mm:ss a")));
                ventaTB.setSerie(rsEmps.getString("Serie"));
                ventaTB.setNumeracion(rsEmps.getString("Numeracion"));
                ventaTB.setMonedaName(rsEmps.getString("Simbolo"));
                ventaTB.setTotal(rsEmps.getDouble("Total"));
                empList.add(ventaTB);
            }
        } catch (SQLException ex) {
            System.out.println(ex.getLocalizedMessage());
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (rsEmps != null) {
                    rsEmps.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException e) {
            }
        }
        return empList;
    }

    public static ObservableList<VentaTB> ListVentas10Primeras() {
        String selectStmt = "{call Sp_Listar_Ventas_10_Primeras()}";
        PreparedStatement preparedStatement = null;
        ResultSet rsEmps = null;
        ObservableList<VentaTB> empList = FXCollections.observableArrayList();
        try {
            DBUtil.dbConnect();
            preparedStatement = DBUtil.getConnection().prepareStatement(selectStmt);
            rsEmps = preparedStatement.executeQuery();
            while (rsEmps.next()) {
                VentaTB ventaTB = new VentaTB();
                ventaTB.setId(rsEmps.getRow());
                ventaTB.setClienteTB(new ClienteTB(rsEmps.getString("Cliente")));
                ventaTB.setIdVenta(rsEmps.getString("IdVenta"));
                ventaTB.setFechaVenta(rsEmps.getDate("FechaVenta").toLocalDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
                ventaTB.setHoraVenta(rsEmps.getTime("HoraVenta").toLocalTime().format(DateTimeFormatter.ofPattern("hh:mm:ss a")));
                ventaTB.setSerie(rsEmps.getString("Serie"));
                ventaTB.setNumeracion(rsEmps.getString("Numeracion"));
                ventaTB.setMonedaName(rsEmps.getString("Simbolo"));
                ventaTB.setTotal(rsEmps.getDouble("Total"));
                empList.add(ventaTB);
            }
        } catch (SQLException ex) {
            System.out.println(ex.getLocalizedMessage());
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (rsEmps != null) {
                    rsEmps.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException e) {
            }
        }
        return empList;
    }

    public static ArrayList<Object> ListCompletaVentasDetalle(String idVenta) {
        PreparedStatement statementVenta = null;
        PreparedStatement statementCliente = null;
        PreparedStatement statementVentaDetalle = null;

        ArrayList<Object> objects = new ArrayList<>();
        DBUtil.dbConnect();
        if (DBUtil.getConnection() != null) {
            try {
                statementVenta = DBUtil.getConnection().prepareStatement("{call Sp_Obtener_Venta_ById(?)}");
                statementVenta.setString(1, idVenta);
                ResultSet resultSetVenta = statementVenta.executeQuery();
                VentaTB ventaTB = null;
                if (resultSetVenta.next()) {
                    ventaTB = new VentaTB();
                    ventaTB.setFechaVenta(resultSetVenta.getDate("FechaVenta").toLocalDate().format(DateTimeFormatter.ofPattern("YYYY-MM-dd")));
                    ventaTB.setHoraVenta(resultSetVenta.getTime("HoraVenta").toLocalTime().format(DateTimeFormatter.ofPattern("hh:mm:ss a")));
                    //Cliente start
                    ClienteTB clienteTB = new ClienteTB();
                    clienteTB.setIdAuxiliar(resultSetVenta.getString("IdAuxiliar"));
                    clienteTB.setTipoDocumentoName(resultSetVenta.getString("NombreDocumento"));
                    clienteTB.setNumeroDocumento(resultSetVenta.getString("NumeroDocumento"));
                    clienteTB.setInformacion(resultSetVenta.getString("Informacion"));
                    clienteTB.setTelefono(resultSetVenta.getString("Telefono"));
                    clienteTB.setCelular(resultSetVenta.getString("Celular"));
                    clienteTB.setEmail(resultSetVenta.getString("Email"));
                    clienteTB.setDireccion(resultSetVenta.getString("Direccion"));
                    ventaTB.setClienteTB(clienteTB);
                    //Cliente end
                    ventaTB.setCodigoAlterno(resultSetVenta.getString("CodigoAlterno"));
                    ventaTB.setComprobanteName(resultSetVenta.getString("Comprobante"));
                    ventaTB.setSerie(resultSetVenta.getString("Serie"));
                    ventaTB.setNumeracion(resultSetVenta.getString("Numeracion"));
                    ventaTB.setObservaciones(resultSetVenta.getString("Observaciones"));
                    ventaTB.setTipoName(resultSetVenta.getString("Tipo"));
                    ventaTB.setEstadoName(resultSetVenta.getString("Estado"));
                    ventaTB.setEfectivo(resultSetVenta.getDouble("Efectivo"));
                    ventaTB.setVuelto(resultSetVenta.getDouble("Vuelto"));
                    ventaTB.setTarjeta(resultSetVenta.getDouble("Tarjeta"));
                    ventaTB.setSubTotal(resultSetVenta.getDouble("SubTotal"));
                    ventaTB.setDescuento(resultSetVenta.getDouble("Descuento"));
                    ventaTB.setSubImporte(ventaTB.getSubTotal() - ventaTB.getDescuento());
                    ventaTB.setImpuesto(resultSetVenta.getDouble("Impuesto"));
                    ventaTB.setTotal(resultSetVenta.getDouble("Total"));
                    ventaTB.setCodigo(resultSetVenta.getString("Codigo"));
                    //moneda start
                    MonedaTB monedaTB = new MonedaTB();
                    monedaTB.setNombre(resultSetVenta.getString("Nombre"));
                    monedaTB.setAbreviado(resultSetVenta.getString("Abreviado"));
                    monedaTB.setSimbolo(resultSetVenta.getString("Simbolo"));
                    ventaTB.setMonedaTB(monedaTB);
                    //moneda end
                    objects.add(ventaTB);
                } else {
                    objects.add(ventaTB);
                }

                statementCliente = DBUtil.getConnection().prepareStatement("select e.Apellidos,e.Nombres \n"
                        + "from VentaTB as v inner join EmpleadoTB as e \n"
                        + "on v.Vendedor = e.IdEmpleado\n"
                        + "where v.IdVenta = ?");
                statementCliente.setString(1, idVenta);
                ResultSet resultSetCliente = statementCliente.executeQuery();
                EmpleadoTB empleadoTB = null;
                if (resultSetCliente.next()) {
                    empleadoTB = new EmpleadoTB();
                    empleadoTB.setApellidos(resultSetCliente.getString("Apellidos"));
                    empleadoTB.setNombres(resultSetCliente.getString("Nombres"));
                    objects.add(empleadoTB);
                } else {
                    objects.add(empleadoTB);
                }

                statementVentaDetalle = DBUtil.getConnection().prepareStatement("{call Sp_Listar_Ventas_Detalle_By_Id(?)}");
                statementVentaDetalle.setString(1, idVenta);
                ResultSet resultSetLista = statementVentaDetalle.executeQuery();
                ObservableList<SuministroTB> empList = FXCollections.observableArrayList();
                while (resultSetLista.next()) {
                    SuministroTB suministroTB = new SuministroTB();
                    suministroTB.setId(resultSetLista.getRow());
                    suministroTB.setIdSuministro(resultSetLista.getString("IdSuministro"));
                    suministroTB.setClave(resultSetLista.getString("Clave"));
                    suministroTB.setNombreMarca(resultSetLista.getString("NombreMarca"));
                    suministroTB.setInventario(resultSetLista.getBoolean("Inventario"));
                    suministroTB.setValorInventario(resultSetLista.getShort("ValorInventario"));
                    suministroTB.setUnidadCompraName(resultSetLista.getString("UnidadCompra"));

                    suministroTB.setCantidad(resultSetLista.getDouble("Cantidad"));
                    suministroTB.setCantidadGranel(resultSetLista.getDouble("CantidadGranel"));
                    suministroTB.setCostoCompra(resultSetLista.getDouble("CostoVenta"));

                    suministroTB.setDescuento(resultSetLista.getDouble("Descuento"));

                    suministroTB.setPrecioVentaGeneralUnico(resultSetLista.getDouble("PrecioVenta") + resultSetLista.getDouble("DescuentoCalculado"));
                    suministroTB.setPrecioVentaGeneralReal(resultSetLista.getDouble("PrecioVenta"));

                    double porcentajeRestante = suministroTB.getPrecioVentaGeneralUnico() * (suministroTB.getDescuento() / 100.00);
                    suministroTB.setDescuentoSumado(porcentajeRestante * suministroTB.getCantidad());

                    suministroTB.setImpuestoNombre(resultSetLista.getString("NombreImpuesto"));
                    suministroTB.setImpuestoId(resultSetLista.getInt("IdImpuesto"));
                    suministroTB.setImpuestoValor(resultSetLista.getDouble("ValorImpuesto"));

                    double impuesto = Tools.calculateTax(suministroTB.getImpuestoValor(), suministroTB.getPrecioVentaGeneralReal());
                    suministroTB.setImpuestoSumado(suministroTB.getCantidad() * impuesto);

                    suministroTB.setPrecioVentaGeneral(suministroTB.getPrecioVentaGeneralReal() + impuesto);

                    suministroTB.setSubImporte(suministroTB.getPrecioVentaGeneralUnico() * suministroTB.getCantidad());
                    suministroTB.setSubImporteDescuento(suministroTB.getCantidad() * suministroTB.getPrecioVentaGeneralReal());
                    suministroTB.setTotalImporte(suministroTB.getCantidad() * suministroTB.getPrecioVentaGeneralReal());

                    empList.add(suministroTB);
                }
                objects.add(empList);

            } catch (SQLException e) {
                System.out.println("ListCompletaVentasDetalle() La operación de selección de SQL ha fallado: " + e.getLocalizedMessage());
            } finally {
                try {
                    if (statementVenta != null) {
                        statementVenta.close();
                    }
                    if (statementCliente != null) {
                        statementCliente.close();
                    }
                    if (statementVentaDetalle != null) {
                        statementVentaDetalle.close();
                    }
                    DBUtil.dbDisconnect();
                } catch (SQLException ex) {

                }
            }
        }
        return objects;
    }

    public static String CancelTheSale(String idVenta, ObservableList<SuministroTB> arrList, MovimientoCajaTB movimientoCajaTB) {

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
                ResultSet resultSet = statementValidar.executeQuery();
                resultSet.next();

                String idCaja = resultSet.getString("IdCaja");

                statementValidar = DBUtil.getConnection().prepareStatement("SELECT * FROM VentaTB WHERE IdVenta = ? and Estado = ?");
                statementValidar.setString(1, idVenta);
                statementValidar.setInt(2, 3);
                if (statementValidar.executeQuery().next()) {
                    DBUtil.getConnection().rollback();
                    return "scrambled";
                } else {

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

                    for (SuministroTB stb : arrList) {

                        if (stb.isInventario() && stb.getValorInventario() == 1) {
                            statementSuministro.setDouble(1, stb.getCantidad());
                            statementSuministro.setString(2, stb.getIdSuministro());
                            statementSuministro.addBatch();
                        } else if (stb.isInventario() && stb.getValorInventario() == 2) {
                            statementSuministro.setDouble(1, stb.getCantidadGranel());
                            statementSuministro.setString(2, stb.getIdSuministro());
                            statementSuministro.addBatch();
                        } else if (stb.isInventario() && stb.getValorInventario() == 3) {
                            statementSuministro.setDouble(1, stb.getCantidad());
                            statementSuministro.setString(2, stb.getIdSuministro());
                            statementSuministro.addBatch();
                        }

                        double cantidadGranel = stb.getPrecioVentaGeneralAuxiliar() <= 0 ? 0
                                : stb.getPrecioVentaGeneralReal() / stb.getPrecioVentaGeneralAuxiliar();

                        double cantidadTotal = stb.getValorInventario() == 1
                                ? stb.getCantidad()
                                : stb.getValorInventario() == 2
                                ? cantidadGranel
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

                    statementMovimientoCaja = DBUtil.getConnection().prepareStatement("INSERT INTO MovimientoCajaTB(IdCaja,FechaMovimiento,HoraMovimiento,Comentario,TipoMovimiento,Monto)VALUES(?,?,?,?,?,?)");
                    statementMovimientoCaja.setString(1, idCaja);
                    statementMovimientoCaja.setString(2, movimientoCajaTB.getFechaMovimiento());
                    statementMovimientoCaja.setString(3, movimientoCajaTB.getHoraMovimiento());
                    statementMovimientoCaja.setString(4, movimientoCajaTB.getComentario());
                    statementMovimientoCaja.setShort(5, movimientoCajaTB.getTipoMovimiento());
                    statementMovimientoCaja.setDouble(6, movimientoCajaTB.getMonto());
                    statementMovimientoCaja.addBatch();

                    statementVenta.executeBatch();
                    statementSuministro.executeBatch();
                    statementKardex.executeBatch();
                    statementMovimientoCaja.executeBatch();

                    DBUtil.getConnection().commit();
                    result = "updated";
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
                ventaTB.setCliente(resultSet.getString("Cliente"));
                ventaTB.setSerie(resultSet.getString("Serie") + "-" + resultSet.getString("Numeracion"));
                ventaTB.setTipoName(resultSet.getString("Tipo"));
                ventaTB.setEstado(resultSet.getInt("Estado"));
                ventaTB.setEstadoName(resultSet.getString("EstadoName"));
                ventaTB.setMonedaName(resultSet.getString("Simbolo"));
                ventaTB.setTotal(resultSet.getDouble("Total"));
                ventaTB.setTotalFormat(ventaTB.getMonedaName() + " " + Tools.roundingValue(
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

}
