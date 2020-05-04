package model;

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
import javafx.scene.control.TableView;

public class VentaADO {

    public static String registrarVentaContado(VentaTB ventaTB, BancoHistorialTB bancoHistorialEfectivo, BancoHistorialTB bancoHistorialBancaria, ArrayList<FormaPagoTB> formaPagoTBs, TableView<SuministroTB> tvList, int idTipoDocumento, CuentasClienteTB cuentasClienteTB) {

        CallableStatement serie_numeracion = null;
        PreparedStatement venta = null;
        PreparedStatement comprobante = null;
        CallableStatement codigo_venta = null;
        PreparedStatement detalle_venta = null;
        PreparedStatement suministro_update_unidad = null;
        PreparedStatement suministro_update_granel = null;
        PreparedStatement suministro_kardex = null;
        //PreparedStatement cuentas_cliente = null;
        //PreparedStatement movimiento_caja = null;
        PreparedStatement forma_pago = null;

        CallableStatement codigoBancoHistorial = null;
        PreparedStatement preparedBanco = null;
        PreparedStatement preparedBancoHistorial = null;
        try {

            DBUtil.dbConnect();
            DBUtil.getConnection().setAutoCommit(false);

            Random rd = new Random();
            int dig5 = rd.nextInt(90000) + 10000;

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
                    + "           ([IdVenta]\n"
                    + "           ,[Cliente]\n"
                    + "           ,[Vendedor]\n"
                    + "           ,[Comprobante]\n"
                    + "           ,[Moneda]\n"
                    + "           ,[Serie]\n"
                    + "           ,[Numeracion]\n"
                    + "           ,[FechaVenta]\n"
                    + "           ,[HoraVenta]\n"
                    + "            ,[FechaVencimiento]\n"
                    + "            ,[HoraVencimiento]\n"
                    + "           ,[SubTotal]\n"
                    + "           ,[Descuento]\n"
                    + "           ,[Total]"
                    + "           ,[Tipo]"
                    + "           ,[Estado]"
                    + "           ,[Observaciones]"
                    + "           ,[Efectivo]"
                    + "           ,[Vuelto]"
                    + "           ,[Codigo])\n"
                    + "     VALUES\n"
                    + "           (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

            // movimiento_caja = DBUtil.getConnection().prepareStatement("INSERT INTO MovimientoCajaTB(IdCaja,IdUsuario,FechaMovimiento,HoraMovimiento,Comentario,Movimiento,Entrada,Salidas,Saldo)VALUES(?,?,?,?,?,?,?,?,?)");
            // cuentas_cliente = DBUtil.getConnection().prepareStatement("INSERT INTO CuentasClienteTB(IdVenta,IdCliente,Plazos,FechaVencimiento,MontoInicial)VALUES(?,?,?,?,?)");
            //aca tenemos que cambiar eso amiguito de venta ok? solo jalamor el valor de el txt?
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
                    + "Cantidad) "
                    + "VALUES(?,?,?,?,?,?,?)");

            forma_pago = DBUtil.getConnection().prepareStatement("INSERT INTO dbo.FormaPagoTB (IdVenta,Nombre,Monto) VALUES(?,?,?)");

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
            venta.setDouble(14, ventaTB.getTotal());
            venta.setInt(15, ventaTB.getTipo());
            venta.setInt(16, ventaTB.getEstado());
            venta.setString(17, ventaTB.getObservaciones());
            venta.setDouble(18, ventaTB.getEfectivo());
            venta.setDouble(19, ventaTB.getVuelto());
            venta.setString(20, Integer.toString(dig5) + id_comprabante[1]);
            venta.addBatch();

            preparedBanco = DBUtil.getConnection().prepareStatement("UPDATE Banco SET SaldoInicial = SaldoInicial + ? WHERE IdBanco = ?");
            preparedBancoHistorial = DBUtil.getConnection().prepareStatement("INSERT INTO BancoHistorialTB(IdBanco,IdProcedencia,Descripcion,Fecha,Hora,Entrada,Salida)VALUES(?,?,?,?,?,?,?)");

            if (bancoHistorialEfectivo != null) {

                preparedBanco.setDouble(1, bancoHistorialEfectivo.getEntrada());
                preparedBanco.setString(2, bancoHistorialEfectivo.getIdBanco());
                preparedBanco.addBatch();

                preparedBancoHistorial.setString(1, bancoHistorialEfectivo.getIdBanco());
                preparedBancoHistorial.setString(2, "");
                preparedBancoHistorial.setString(3, bancoHistorialEfectivo.getDescripcion());
                preparedBancoHistorial.setString(4, bancoHistorialEfectivo.getFecha());
                preparedBancoHistorial.setString(5, bancoHistorialEfectivo.getHora());
                preparedBancoHistorial.setDouble(6, bancoHistorialEfectivo.getEntrada());
                preparedBancoHistorial.setDouble(7, 0);
                preparedBancoHistorial.addBatch();
            }
            if (bancoHistorialBancaria != null) {

                preparedBanco.setDouble(1, bancoHistorialBancaria.getEntrada());
                preparedBanco.setString(2, bancoHistorialBancaria.getIdBanco());
                preparedBanco.addBatch();

                preparedBancoHistorial.setString(1, bancoHistorialBancaria.getIdBanco());
                preparedBancoHistorial.setString(2, "");
                preparedBancoHistorial.setString(3, bancoHistorialBancaria.getDescripcion());
                preparedBancoHistorial.setString(4, bancoHistorialBancaria.getFecha());
                preparedBancoHistorial.setString(5, bancoHistorialBancaria.getHora());
                preparedBancoHistorial.setDouble(6, bancoHistorialBancaria.getEntrada());
                preparedBancoHistorial.setDouble(7, 0);
                preparedBancoHistorial.addBatch();
            }
            //else{
//                System.out.println(ventaTB.isTipopago());
//            }

            //
//            movimiento_caja.setString(1, Session.CAJA_ID);
//            movimiento_caja.setString(2, ventaTB.getVendedor());
//            movimiento_caja.setString(3, Tools.getDate());
//            movimiento_caja.setString(4, Tools.getHour());
//            movimiento_caja.setString(5, ventaTB.getEstado() == 2 ? "Venta al crédito" : "Venta al contado");
//            movimiento_caja.setString(6, ventaTB.getEstado() == 2 ? "VENCRE" : "VEN");
//            movimiento_caja.setDouble(7, ventaTB.getTotal());
//            movimiento_caja.setDouble(8, 0);
//            movimiento_caja.setDouble(9, ventaTB.getTotal() - 0);
//            movimiento_caja.addBatch();
            for (FormaPagoTB formaPagoTB : formaPagoTBs) {
                forma_pago.setString(1, id_venta);
                forma_pago.setString(2, formaPagoTB.getNombre());
                forma_pago.setDouble(3, formaPagoTB.getMonto());
                forma_pago.addBatch();
            }

//            if (ventaTB.getEstado() == 2) {
//                cuentas_cliente.setString(1, id_venta);
//                cuentas_cliente.setString(2, ventaTB.getCliente());
//                cuentas_cliente.setInt(3, cuentasClienteTB.getPlazos());
//                cuentas_cliente.setTimestamp(4, Timestamp.valueOf(cuentasClienteTB.getFechaVencimiento()));
//                cuentas_cliente.setDouble(5, ventaTB.getTotal());
//                cuentas_cliente.addBatch();
//            }
            comprobante.setInt(1, idTipoDocumento);
            comprobante.setString(2, id_comprabante[0]);
            comprobante.setString(3, id_comprabante[1]);
            comprobante.setTimestamp(4, Tools.getDateHour());
            comprobante.addBatch();

            for (int i = 0; i < tvList.getItems().size(); i++) {
                detalle_venta.setString(1, id_venta);
                detalle_venta.setString(2, tvList.getItems().get(i).getIdSuministro());
                detalle_venta.setDouble(3, tvList.getItems().get(i).getCantidad());

                double cantidadGranel = tvList.getItems().get(i).getPrecioVentaGeneralAuxiliar() <= 0 ? 0
                        : tvList.getItems().get(i).getPrecioVentaGeneralReal() / tvList.getItems().get(i).getPrecioVentaGeneralAuxiliar();

                detalle_venta.setDouble(4, cantidadGranel);
                detalle_venta.setDouble(5, tvList.getItems().get(i).getCostoCompra());
                detalle_venta.setDouble(6, tvList.getItems().get(i).getPrecioVentaGeneralReal());
                detalle_venta.setDouble(7, tvList.getItems().get(i).getDescuento());
                detalle_venta.setDouble(8, tvList.getItems().get(i).getDescuentoCalculado());
                detalle_venta.setDouble(9, tvList.getItems().get(i).getImpuestoOperacion());
                detalle_venta.setDouble(10, tvList.getItems().get(i).getImpuestoArticulo());
                detalle_venta.setString(11, tvList.getItems().get(i).getImpuestoArticuloName());
                detalle_venta.setDouble(12, tvList.getItems().get(i).getImpuestoValor());
                detalle_venta.setDouble(13, tvList.getItems().get(i).getTotalImporte());
                detalle_venta.addBatch();

                if (tvList.getItems().get(i).isInventario() && tvList.getItems().get(i).getValorInventario() == 1) {
                    suministro_update_unidad.setDouble(1, tvList.getItems().get(i).getCantidad());
                    suministro_update_unidad.setString(2, tvList.getItems().get(i).getIdSuministro());
                    suministro_update_unidad.addBatch();
                } else if (tvList.getItems().get(i).isInventario() && tvList.getItems().get(i).getValorInventario() == 2) {
                    suministro_update_granel.setDouble(1, tvList.getItems().get(i).getTotalImporte());
                    suministro_update_granel.setString(2, tvList.getItems().get(i).getIdSuministro());
                    suministro_update_granel.addBatch();
                } else if (tvList.getItems().get(i).isInventario() && tvList.getItems().get(i).getValorInventario() == 3) {
                    suministro_update_unidad.setDouble(1, tvList.getItems().get(i).getCantidad());
                    suministro_update_unidad.setString(2, tvList.getItems().get(i).getIdSuministro());
                    suministro_update_unidad.addBatch();
                }

                suministro_kardex.setString(1, tvList.getItems().get(i).getIdSuministro());
                suministro_kardex.setString(2, Tools.getDate());
                suministro_kardex.setString(3, Tools.getHour());
                suministro_kardex.setShort(4, (short) 2);
                suministro_kardex.setInt(5, 1);
                suministro_kardex.setString(6, "Venta");
                suministro_kardex.setDouble(7,
                        tvList.getItems().get(i).getValorInventario() == 1
                        ? tvList.getItems().get(i).getCantidad()
                        : cantidadGranel);
                suministro_kardex.addBatch();

            }

            venta.executeBatch();
            preparedBanco.executeBatch();
            preparedBancoHistorial.executeBatch();
            forma_pago.executeBatch();
//            cuentas_cliente.executeBatch();
            comprobante.executeBatch();
            detalle_venta.executeBatch();
            suministro_update_unidad.executeBatch();
            suministro_update_granel.executeBatch();
            suministro_kardex.executeBatch();
//            movimiento_caja.executeBatch();
            DBUtil.getConnection().commit();
            return "register/" + id_comprabante[0] + "-" + id_comprabante[1] + "/" + (Integer.toString(dig5) + id_comprabante[1]);
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
                if (venta != null) {
                    venta.close();
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

//                if (cuentas_cliente != null) {
//                    cuentas_cliente.close();
//                }
//                if (movimiento_caja != null) {
//                    movimiento_caja.close();
//                }
                if (forma_pago != null) {
                    forma_pago.close();
                }

                if (codigoBancoHistorial != null) {
                    codigoBancoHistorial.close();
                }
                if (preparedBanco != null) {
                    preparedBanco.close();
                }
                if (preparedBancoHistorial != null) {
                    preparedBancoHistorial.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException e) {
            }
        }
    }

    public static ObservableList<VentaTB> ListVentas(short opcion, String value, String fechaInicial, String fechaFinal, int comprobante, int estado, String usuario) {
        String selectStmt = "{call Sp_Listar_Ventas(?,?,?,?,?,?,?)}";
        PreparedStatement preparedStatement = null;
        ResultSet rsEmps = null;
        ObservableList<VentaTB> empList = FXCollections.observableArrayList();
        try {
            DBUtil.dbConnect();
            preparedStatement = DBUtil.getConnection().prepareStatement(selectStmt);
            preparedStatement.setShort(1, opcion);
            preparedStatement.setString(2, value);
            preparedStatement.setString(3, fechaInicial);
            preparedStatement.setString(4, fechaFinal);
            preparedStatement.setInt(5, comprobante);
            preparedStatement.setInt(6, estado);
            preparedStatement.setString(7, usuario);
            rsEmps = preparedStatement.executeQuery();
            while (rsEmps.next()) {
                VentaTB ventaTB = new VentaTB();
                ventaTB.setId(rsEmps.getRow());
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
                ventaTB.setIdVenta(rsEmps.getString("IdVenta"));
                ventaTB.setFechaVenta(rsEmps.getDate("FechaVenta").toLocalDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
                ventaTB.setHoraVenta(rsEmps.getTime("HoraVenta").toLocalTime().format(DateTimeFormatter.ofPattern("hh:mm:ss a")));
                ventaTB.setMonedaName(rsEmps.getString("Simbolo"));
                ventaTB.setTotal(rsEmps.getDouble("Total"));
                ventaTB.setCodigo(rsEmps.getString("Codigo"));

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
        PreparedStatement statementFormasPago = null;

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
                    ventaTB.setFechaVenta(resultSetVenta.getDate("FechaVenta").toLocalDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
                    ventaTB.setHoraVenta(resultSetVenta.getTime("HoraVenta").toLocalTime().format(DateTimeFormatter.ofPattern("hh:mm:ss a")));
                    //Cliente start
                    ClienteTB clienteTB = new ClienteTB();
                    clienteTB.setTipoDocumentoName(resultSetVenta.getString("NombreDocumento"));
                    clienteTB.setNumeroDocumento(resultSetVenta.getString("NumeroDocumento"));
                    clienteTB.setInformacion(resultSetVenta.getString("Informacion"));
                    clienteTB.setDireccion(resultSetVenta.getString("Direccion"));
                    ventaTB.setClienteTB(clienteTB);
                    //Cliente end
                    ventaTB.setComprobanteName(resultSetVenta.getString("Comprobante"));
                    ventaTB.setComproabanteNameImpresion(resultSetVenta.getString("NombreImpresion"));
                    ventaTB.setSerie(resultSetVenta.getString("Serie"));
                    ventaTB.setNumeracion(resultSetVenta.getString("Numeracion"));
                    ventaTB.setObservaciones(resultSetVenta.getString("Observaciones"));
                    ventaTB.setTipoName(resultSetVenta.getString("Tipo"));
                    ventaTB.setEstadoName(resultSetVenta.getString("Estado"));
                    ventaTB.setEfectivo(resultSetVenta.getDouble("Efectivo"));
                    ventaTB.setVuelto(resultSetVenta.getDouble("Vuelto"));
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

                    suministroTB.setImpuestoArticuloName(resultSetLista.getString("NombreImpuesto"));
                    suministroTB.setImpuestoArticulo(resultSetLista.getInt("IdImpuesto"));
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

                statementFormasPago = DBUtil.getConnection().prepareStatement("SELECT Nombre, Monto FROM FormaPagotB WHERE IdVenta = ?");
                statementFormasPago.setString(1, idVenta);
                ResultSet resulSetFormaPago = statementFormasPago.executeQuery();
                ArrayList<FormaPagoTB> formaPagoTBs = new ArrayList<>();
                while (resulSetFormaPago.next()) {
                    FormaPagoTB formaPagoTB = new FormaPagoTB();
                    formaPagoTB.setNombre(resulSetFormaPago.getString("Nombre"));
                    formaPagoTB.setMonto(resulSetFormaPago.getDouble("Monto"));
                    formaPagoTBs.add(formaPagoTB);
                }
                objects.add(formaPagoTBs);

            } catch (SQLException e) {
                System.out.println("ListVentasDetalle:La operación de selección de SQL ha fallado: " + e.getLocalizedMessage());

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
                    if (statementFormasPago != null) {
                        statementFormasPago.close();
                    }
                    DBUtil.dbDisconnect();
                } catch (SQLException ex) {

                }
            }
        }
        return objects;
    }

    public static String CancelTheSale(String idVenta, ObservableList<SuministroTB> arrList) {
        String result = null;

        PreparedStatement statementVenta = null;
        PreparedStatement statementSuministro = null;
        PreparedStatement statementValidar = null;
        PreparedStatement preparedBanco = null;
        PreparedStatement preparedBancoHistorial = null;
        PreparedStatement suministro_kardex = null;

        try {
            DBUtil.dbConnect();
            DBUtil.getConnection().setAutoCommit(false);

            statementValidar = DBUtil.getConnection().prepareStatement("select * from VentaTB where IdVenta = ? and Estado = ?");
            statementValidar.setString(1, idVenta);
            statementValidar.setInt(2, 3);
            if (statementValidar.executeQuery().next()) {
                DBUtil.getConnection().rollback();
                return "scrambled";
            } else {

                statementVenta = DBUtil.getConnection().prepareStatement("update VentaTB set Estado = ? where IdVenta = ?");
                statementVenta.setInt(1, 3);
                statementVenta.setString(2, idVenta);
                statementVenta.addBatch();
                statementVenta.executeBatch();

                statementSuministro = DBUtil.getConnection().prepareStatement("update SuministroTB set Cantidad = Cantidad + ? where IdSuministro = ?");

                suministro_kardex = DBUtil.getConnection().prepareStatement("INSERT INTO "
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

                for (int i = 0; i < arrList.size(); i++) {
                    statementSuministro.setDouble(1, arrList.get(i).getCantidad());
                    statementSuministro.setString(2, arrList.get(i).getIdSuministro());
                    statementSuministro.addBatch();
                    statementSuministro.executeBatch();
                }

                preparedBanco = DBUtil.getConnection().prepareStatement("UPDATE Banco SET SaldoInicial = SaldoInicial + ? WHERE IdBanco = ?");
                preparedBancoHistorial = DBUtil.getConnection().prepareStatement("INSERT INTO BancoHistorialTB(IdBanco,IdProcedencia,Descripcion,Fecha,Hora,Entrada,Salida)VALUES(?,?,?,?,?,?,?)");

//                for (int i = 0; i < tvList.size(); i++) {
//                    if (tvList.get(i).isInventario() && tvList.get(i).getValorInventario() == 1) {
//                        statementSuministro.setDouble(1, tvList.get(i).getCantidad());
//                        statementSuministro.setString(2, tvList.get(i).getIdSuministro());
//                        statementSuministro.addBatch();
//                    } else if (tvList.get(i).isInventario() && tvList.get(i).getValorInventario() == 2) {
//                        statementSuministro.setDouble(1, tvList.get(i).getCantidadGranel());
//                        statementSuministro.setString(2, tvList.get(i).getIdSuministro());
//                        statementSuministro.addBatch();
//                    }
////
//                    suministro_kardex.setString(1, tvList.get(i).getIdSuministro());
//                    suministro_kardex.setString(2, Tools.getDate());
//                    suministro_kardex.setString(3, Tools.getHour());
//                    suministro_kardex.setShort(4, (short) 1);
//                    suministro_kardex.setInt(5, 2);
//                    suministro_kardex.setString(6, "Devolución");
//                    suministro_kardex.setDouble(7,
//                            tvList.get(i).getValorInventario() == 1
//                            ? tvList.get(i).getCantidad()
//                            : tvList.get(i).getCantidadGranel());
//                    suministro_kardex.setDouble(8, tvList.get(i).getCostoCompra());
//                    suministro_kardex.setDouble(9, tvList.get(i).getValorInventario() == 2
//                            ? tvList.get(i).getCantidad() * tvList.get(i).getCostoCompra()
//                            : tvList.get(i).getCantidadGranel() * tvList.get(i).getCostoCompra());
//                    suministro_kardex.addBatch();
//                }
//                movimiento_caja = DBUtil.getConnection().prepareStatement("INSERT INTO MovimientoCajaTB(IdCaja,IdUsuario,FechaMovimiento,HoraMovimiento,Comentario,Movimiento,Entrada,Salidas,Saldo)VALUES(?,?,?,?,?,?,?,?,?)");
//                movimiento_caja.setString(1, cajaTB.getIdCaja());
//                movimiento_caja.setString(2, cajaTB.getIdUsuario());
//                movimiento_caja.setString(3, cajaTB.getFechaMovimiento());
//                movimiento_caja.setString(4, cajaTB.getHoraMovimiento());
//                movimiento_caja.setString(5, cajaTB.getComentario());
//                movimiento_caja.setString(6, cajaTB.getMovimiento());
//                movimiento_caja.setDouble(7, 0);
//                movimiento_caja.setDouble(8, cajaTB.getSalidas());
//                movimiento_caja.setDouble(9, cajaTB.getSaldo());
//                movimiento_caja.addBatch();
//
//                statementVenta.executeBatch();
//                statementSuministro.executeBatch();
//                movimiento_caja.executeBatch();
//                suministro_kardex.executeBatch();
//                DBUtil.getConnection().commit();
//                return "update";
//            }
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
                if (statementValidar != null) {
                    statementValidar.close();
                }
                if (statementVenta != null) {
                    statementVenta.close();
                }
                if (statementSuministro != null) {
                    statementSuministro.close();
                }
//                if (historial_Banco != null) {
//                    historial_Banco.close();
//                }
                if (suministro_kardex != null) {
                    suministro_kardex.close();
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
