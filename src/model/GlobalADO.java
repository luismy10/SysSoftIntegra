package model;

import controller.tools.Tools;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;

public class GlobalADO {

    public static ArrayList<Double> ReporteGlobal(String fechaInicial, String fechaFinal) {
        PreparedStatement preparedGlobal = null;
        ResultSet resultSet = null;
        ArrayList<Double> list = new ArrayList<>();
        DBUtil.dbConnect();
        if (DBUtil.getConnection() != null) {
            try {
//              compras al contado
                preparedGlobal = DBUtil.getConnection().prepareStatement("select sum(Total) as 'totalcontado' from CompraTB where TipoCompra = 1 and EstadoCompra = 1 and FechaCompra between ? and ?");
                preparedGlobal.setString(1, fechaInicial);
                preparedGlobal.setString(2, fechaFinal);
                resultSet = preparedGlobal.executeQuery();
                double totalcontado = 0;
                if (resultSet.next()) {
                    totalcontado = resultSet.getDouble("totalcontado");
                }
                list.add(totalcontado);

//              compras al credito
                preparedGlobal = DBUtil.getConnection().prepareStatement("select sum(Total) as 'totalcredito' from CompraTB where TipoCompra = 2 and EstadoCompra = 2 and FechaCompra between ? and ?");
                preparedGlobal.setString(1, fechaInicial);
                preparedGlobal.setString(2, fechaFinal);
                resultSet = preparedGlobal.executeQuery();
                double totalcredito = 0;
                if (resultSet.next()) {
                    totalcredito = resultSet.getDouble("totalcredito");
                }
                list.add(totalcredito);

//              utilidad
                preparedGlobal = DBUtil.getConnection().prepareStatement("select \n"
                        + "	case \n"
                        + "		when a.ValorInventario = 1 then (dv.Cantidad * a.PrecioVentaGeneral )- (dv.Cantidad * dv.CostoVenta)\n"
                        + "		when a.ValorInventario = 2 then (dv.CantidadGranel * a.PrecioVentaGeneral )- (dv.CantidadGranel * dv.CostoVenta)\n"
                        + "		when a.ValorInventario = 3 then (dv.Cantidad * a.PrecioVentaGeneral )- (dv.Cantidad * dv.CostoVenta)\n"
                        + "	end as  Utilidad\n"
                        + "		from DetalleVentaTB as dv \n"
                        + "		inner join SuministroTB as a on dv.IdArticulo = a.IdSuministro \n"
                        + "		inner join VentaTB as v on v.IdVenta = dv.IdVenta\n"
                        + "		where v.Estado <> 3 and v.FechaVenta between ? and ?");
                preparedGlobal.setString(1, fechaInicial);
                preparedGlobal.setString(2, fechaFinal);
                resultSet = preparedGlobal.executeQuery();
                double utilidad = 0;
                while (resultSet.next()) {
                    utilidad += resultSet.getDouble("Utilidad");
                }
                list.add(utilidad);

                preparedGlobal = DBUtil.getConnection().prepareStatement("select sum(Total) as 'totalcancelado' from CompraTB where EstadoCompra = 3 and FechaCompra between ? and ?");
                preparedGlobal.setString(1, fechaInicial);
                preparedGlobal.setString(2, fechaFinal);
                resultSet = preparedGlobal.executeQuery();
                double totalcencelado = 0;
                if (resultSet.next()) {
                    totalcencelado += resultSet.getDouble("totalcancelado");
                }
                list.add(totalcencelado);
            } catch (SQLException ex) {
                System.out.println(ex.getLocalizedMessage());
            } finally {
                try {
                    if (resultSet != null) {
                        resultSet.close();
                    }
                    if(preparedGlobal != null){
                        preparedGlobal.close();
                    }
                    DBUtil.dbDisconnect();
                } catch (SQLException ex) {
                }
            }
        }

        return list;
    }

    public static ArrayList<String> ReporteInicio(String fechaActual) {
        PreparedStatement preparedLista = null;
        ResultSet resultLista = null;
        ArrayList<String> arrayList = new ArrayList<>();
        double ventasTotales = 0;
        double comprasTotales = 0;
        double articulos = 0;
        double clientes = 0;
        double proveedores = 0;
        double trabajadores = 0;
        DBUtil.dbConnect();
        if (DBUtil.getConnection() != null) {
            try {
                preparedLista = DBUtil.getConnection().prepareStatement("SELECT SUM(Total) AS Total FROM VentaTB WHERE FechaVenta = ? AND Estado <> 3");
                preparedLista.setString(1, fechaActual);
                resultLista = preparedLista.executeQuery();
                if (resultLista.next()) {
                    ventasTotales = resultLista.getDouble("Total");
                }

                preparedLista = DBUtil.getConnection().prepareStatement("SELECT SUM(Total) AS Total FROM CompraTB WHERE FechaCompra = ? AND EstadoCompra <> 3");
                preparedLista.setString(1, fechaActual);
                resultLista = preparedLista.executeQuery();
                if (resultLista.next()) {
                    comprasTotales = resultLista.getDouble("Total");
                }

                preparedLista = DBUtil.getConnection().prepareStatement("SELECT COUNT(*) AS Total FROM SuministroTB");
                resultLista = preparedLista.executeQuery();
                if (resultLista.next()) {
                    articulos = resultLista.getDouble("Total");
                }

                preparedLista = DBUtil.getConnection().prepareStatement("SELECT COUNT(*) AS Total FROM ClienteTB");
                resultLista = preparedLista.executeQuery();
                if (resultLista.next()) {
                    clientes = resultLista.getDouble("Total");
                }

                preparedLista = DBUtil.getConnection().prepareStatement("SELECT COUNT(*) AS Total FROM ProveedorTB");
                resultLista = preparedLista.executeQuery();
                if (resultLista.next()) {
                    proveedores = resultLista.getDouble("Total");
                }

                preparedLista = DBUtil.getConnection().prepareStatement("SELECT COUNT(*) AS Total FROM EmpleadoTB");
                resultLista = preparedLista.executeQuery();
                if (resultLista.next()) {
                    trabajadores = resultLista.getDouble("Total");
                }

                arrayList.add(Tools.roundingValue(ventasTotales, 2));
                arrayList.add(Tools.roundingValue(comprasTotales, 2));
                arrayList.add(Tools.roundingValue(articulos, 0));
                arrayList.add(Tools.roundingValue(clientes, 0));
                arrayList.add(Tools.roundingValue(proveedores, 0));
                arrayList.add(Tools.roundingValue(trabajadores, 0));

            } catch (SQLException ex) {
                arrayList.add(Tools.roundingValue(ventasTotales, 2));
                arrayList.add(Tools.roundingValue(comprasTotales, 2));
                arrayList.add(Tools.roundingValue(articulos, 0));
                arrayList.add(Tools.roundingValue(clientes, 0));
                arrayList.add(Tools.roundingValue(proveedores, 0));
                arrayList.add(Tools.roundingValue(trabajadores, 0));
            } finally {
                try {
                    if (preparedLista != null) {
                        preparedLista.close();
                    }
                    if (resultLista != null) {
                        resultLista.close();
                    }
                    DBUtil.dbDisconnect();
                } catch (SQLException ex) {

                }
            }
        } else {
            arrayList.add(Tools.roundingValue(ventasTotales, 2));
            arrayList.add(Tools.roundingValue(comprasTotales, 2));
            arrayList.add(Tools.roundingValue(articulos, 0));
            arrayList.add(Tools.roundingValue(clientes, 0));
            arrayList.add(Tools.roundingValue(proveedores, 0));
            arrayList.add(Tools.roundingValue(trabajadores, 0));
        }
        return arrayList;
    }

    public static String RegistrarInicioPrograma(EmpresaTB empresaTB, MonedaTB monedaTB, EmpleadoTB empleadoTB, ImpuestoTB impuestoTB, TipoDocumentoTB tipoDocumentoTB, ClienteTB clienteTB) {
        PreparedStatement statementEmpresa = null;
        PreparedStatement statementMoneda = null;
        PreparedStatement statementEmpleado = null;
        PreparedStatement statementImpuesto = null;
        PreparedStatement statementTipoDocumento = null;
        PreparedStatement statementCliente = null;
        CallableStatement codigoEmpleado = null;
        CallableStatement codigoCliente = null;
        String result = "";
        try {
            DBUtil.dbConnect();
            DBUtil.getConnection().setAutoCommit(false);
            statementEmpresa = DBUtil.getConnection().prepareStatement("INSERT INTO EmpresaTB("
                    + "GiroComercial,"
                    + "Nombre,"
                    + "Telefono,"
                    + "Celular,"
                    + "PaginaWeb,"
                    + "Email,"
                    + "Domicilio,"
                    + "TipoDocumento,"
                    + "NumeroDocumento,"
                    + "RazonSocial,"
                    + "NombreComercial,"
                    + "Pais,"
                    + "Ciudad,"
                    + "Provincia,"
                    + "Distrito)VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            statementEmpresa.setInt(1, empresaTB.getGiroComerial());
            statementEmpresa.setString(2, empresaTB.getNombre());
            statementEmpresa.setString(3, empresaTB.getTelefono());
            statementEmpresa.setString(4, empresaTB.getCelular());
            statementEmpresa.setString(5, empresaTB.getPaginaWeb());
            statementEmpresa.setString(6, empresaTB.getEmail());
            statementEmpresa.setString(7, empresaTB.getDomicilio());
            statementEmpresa.setInt(8, empresaTB.getTipoDocumento());
            statementEmpresa.setString(9, empresaTB.getNumeroDocumento());
            statementEmpresa.setString(10, empresaTB.getRazonSocial());
            statementEmpresa.setString(11, empresaTB.getNombreComercial());
            statementEmpresa.setString(12, "");
            statementEmpresa.setInt(13, 0);
            statementEmpresa.setInt(14, 0);
            statementEmpresa.setInt(15, 0);
            statementEmpresa.addBatch();

            statementMoneda = DBUtil.getConnection().prepareStatement("INSERT INTO MonedaTB("
                    + "Nombre,"
                    + "Abreviado,"
                    + "Simbolo,"
                    + "TipoCambio,"
                    + "Predeterminado,"
                    + "Sistema)VALUES(?,?,?,?,?,?)");
            statementMoneda.setString(1, monedaTB.getNombre());
            statementMoneda.setString(2, monedaTB.getAbreviado());
            statementMoneda.setString(3, monedaTB.getSimbolo());
            statementMoneda.setDouble(4, monedaTB.getTipoCambio());
            statementMoneda.setBoolean(5, monedaTB.getPredeterminado());
            statementMoneda.setBoolean(6, monedaTB.getSistema());
            statementMoneda.addBatch();

            codigoEmpleado = DBUtil.getConnection().prepareCall("{? = call Fc_Empleado_Codigo_Alfanumerico()}");
            codigoEmpleado.registerOutParameter(1, java.sql.Types.VARCHAR);
            codigoEmpleado.execute();
            String idEmpleado = codigoEmpleado.getString(1);

            statementEmpleado = DBUtil.getConnection().prepareStatement("INSERT INTO EmpleadoTB"
                    + "           (IdEmpleado"
                    + "           ,TipoDocumento"
                    + "           ,NumeroDocumento"
                    + "           ,Apellidos"
                    + "           ,Nombres"
                    + "           ,Sexo"
                    + "           ,FechaNacimiento"
                    + "           ,Puesto"
                    + "           ,Rol"
                    + "           ,Estado"
                    + "           ,Telefono"
                    + "           ,Celular"
                    + "           ,Email"
                    + "           ,Direccion"
                    + "           ,Pais"
                    + "           ,Ciudad"
                    + "           ,Provincia"
                    + "           ,Distrito"
                    + "           ,Usuario"
                    + "           ,Clave)VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

            statementEmpleado.setString(1, idEmpleado);
            statementEmpleado.setInt(2, empleadoTB.getTipoDocumento());
            statementEmpleado.setString(3, empleadoTB.getNumeroDocumento());
            statementEmpleado.setString(4, empleadoTB.getApellidos());
            statementEmpleado.setString(5, empleadoTB.getNombres());
            statementEmpleado.setInt(6, 0);
            statementEmpleado.setDate(7, Tools.getCurrentDate());
            statementEmpleado.setInt(8, 1);
            statementEmpleado.setInt(9, 1);
            statementEmpleado.setInt(10, 1);
            statementEmpleado.setString(11, "");
            statementEmpleado.setString(12, "");
            statementEmpleado.setString(13, "");
            statementEmpleado.setString(14, "");
            statementEmpleado.setString(15, "");
            statementEmpleado.setInt(16, 0);
            statementEmpleado.setInt(17, 0);
            statementEmpleado.setInt(18, 0);
            statementEmpleado.setString(19, empleadoTB.getUsuario());
            statementEmpleado.setString(20, empleadoTB.getClave());
            statementEmpleado.addBatch();

            statementImpuesto = DBUtil.getConnection().prepareStatement("INSERT INTO ImpuestoTB(Operacion,Nombre,Valor,Predeterminado,CodigoAlterno,Sistema)VALUES(?,?,?,?,?,?)");
            statementImpuesto.setInt(1, impuestoTB.getOperacion());
            statementImpuesto.setString(2, impuestoTB.getNombreImpuesto());
            statementImpuesto.setDouble(3, impuestoTB.getValor());
            statementImpuesto.setBoolean(4, impuestoTB.getPredeterminado());
            statementImpuesto.setString(5, impuestoTB.getCodigoAlterno());
            statementImpuesto.setBoolean(6, impuestoTB.isSistema());
            statementImpuesto.addBatch();

            statementTipoDocumento = DBUtil.getConnection().prepareStatement("INSERT INTO TipoDocumentoTB(Nombre,Serie,Predeterminado,NombreImpresion,Sistema)VALUES(?,?,?,?,?)");
            statementTipoDocumento.setString(1, tipoDocumentoTB.getNombre());
            statementTipoDocumento.setString(2, tipoDocumentoTB.getSerie());
            statementTipoDocumento.setBoolean(3, tipoDocumentoTB.isPredeterminado());
            statementTipoDocumento.setString(4, tipoDocumentoTB.getNombreDocumento());
            statementTipoDocumento.setBoolean(5, tipoDocumentoTB.isSistema());
            statementTipoDocumento.addBatch();

            codigoCliente = DBUtil.getConnection().prepareCall("{? = call Fc_Cliente_Codigo_Alfanumerico()}");
            codigoCliente.registerOutParameter(1, java.sql.Types.VARCHAR);
            codigoCliente.execute();
            String idCliente = codigoCliente.getString(1);

            statementCliente = DBUtil.getConnection().prepareStatement("INSERT INTO ClienteTB(IdCliente,TipoDocumento,NumeroDocumento,Informacion,Telefono,Celular,Email,Direccion,Representante,Estado,Predeterminado,Sistema)VALUES(?,?,?,?,?,?,?,?,?,?,?,?)");
            statementCliente.setString(1, idCliente);
            statementCliente.setInt(2, clienteTB.getTipoDocumento());
            statementCliente.setString(3, clienteTB.getNumeroDocumento());
            statementCliente.setString(4, clienteTB.getInformacion());
            statementCliente.setString(5, clienteTB.getTelefono());
            statementCliente.setString(6, clienteTB.getCelular());
            statementCliente.setString(7, clienteTB.getEmail());
            statementCliente.setString(8, clienteTB.getDireccion());
            statementCliente.setString(9, clienteTB.getRepresentante());
            statementCliente.setInt(10, clienteTB.getEstado());
            statementCliente.setBoolean(11, clienteTB.isPredeterminado());
            statementCliente.setBoolean(12, clienteTB.isSistema());
            statementCliente.addBatch();

            statementEmpresa.executeBatch();
            statementMoneda.executeBatch();
            statementEmpleado.executeBatch();
            statementImpuesto.executeBatch();
            statementTipoDocumento.executeBatch();
            statementCliente.executeBatch();
            DBUtil.getConnection().commit();
            result = "inserted";
        } catch (SQLException | ParseException ex) {
            try {
                DBUtil.getConnection().rollback();
            } catch (SQLException e) {

            }
            result = ex.getLocalizedMessage();
        } finally {
            try {
                if (statementEmpresa != null) {
                    statementEmpresa.close();
                }
                if (statementMoneda != null) {
                    statementMoneda.close();
                }
                if (statementEmpleado != null) {
                    statementEmpleado.close();
                }
                if (statementImpuesto != null) {
                    statementImpuesto.close();
                }
                if (statementTipoDocumento != null) {
                    statementTipoDocumento.close();
                }
                if (codigoEmpleado != null) {
                    codigoEmpleado.close();
                }
                if (statementCliente != null) {
                    statementCliente.close();
                }
                if(codigoCliente != null){
                    codigoCliente.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException ex) {

            }
        }
        return result;
    }
}
