package model;

import java.io.Serializable;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.CheckBox;

public class EmpleadoTB implements Serializable {

    public static List<EmpleadoTB> getSearchComboBoxEmpleados(String buscar) {
        String selectStmt = "SELECT IdEmpleado,NumeroDocumento,Apellidos,Nombres FROM EmpleadoTB WHERE NumeroDocumento LIKE ? OR Apellidos LIKE ? OR Nombres LIKE ?";
        PreparedStatement preparedStatement = null;
        List<EmpleadoTB> empleadoTBs = new ArrayList<>();
        try {
            DBUtil.dbConnect();
            preparedStatement = DBUtil.getConnection().prepareStatement(selectStmt);
            preparedStatement.setString(1, buscar + "%");
            preparedStatement.setString(2, buscar + "%");
            preparedStatement.setString(3, buscar + "%");
            try (ResultSet rsEmps = preparedStatement.executeQuery()) {
                while (rsEmps.next()) {
                    EmpleadoTB empleadoTB = new EmpleadoTB();
                    empleadoTB.setIdEmpleado(rsEmps.getString("IdEmpleado"));
                    empleadoTB.setNumeroDocumento(rsEmps.getString("NumeroDocumento"));
                    empleadoTB.setApellidos(rsEmps.getString("Apellidos"));
                    empleadoTB.setNombres(rsEmps.getString("Nombres"));
                    empleadoTBs.add(empleadoTB);
                }
            }
        } catch (SQLException e) {

        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException e) {

            }
        }
        return empleadoTBs;
    }

    private SimpleIntegerProperty id;

    private String idEmpleado;

    private int tipoDocumento;

    private String numeroDocumento;

    private String apellidos;

    private String nombres;

    private Integer sexo;

    private Date fechaNacimiento;

    private int puesto;

    private String puestoName;

    private int estado;

    private String estadoName;

    private String telefono;

    private String celular;

    private String email;

    private String direccion;

    private String usuario;

    private String clave;

    private int rol;

    private String rolName;

    private CheckBox validarEm;

    private String informacion;

    public EmpleadoTB() {
    }

    public EmpleadoTB(String informacion) {
        this.informacion = informacion == null ? "" : informacion;
    }

    public EmpleadoTB(String idEmpleado, String apellidos, String nombres) {
        this.idEmpleado = idEmpleado;
        this.apellidos = apellidos;
        this.nombres = nombres;
    }

    public EmpleadoTB(String apellidos, String nombres) {
        this.apellidos = apellidos;
        this.nombres = nombres;
    }

    public EmpleadoTB(String numeroDocumento, String apellidos, String nombres, String celular, String direccion) {
        this.numeroDocumento = numeroDocumento;
        this.apellidos = apellidos;
        this.nombres = nombres;
        this.celular = celular;
        this.direccion = direccion;
    }

    public EmpleadoTB(String idEmpleado, int tipoDocumento, String apellidos, String nombres, int puesto, int estado) {
        this.idEmpleado = idEmpleado;
        this.tipoDocumento = tipoDocumento;
        this.apellidos = apellidos;
        this.nombres = nombres;
        this.puesto = puesto;
        this.estado = estado;
    }

    public SimpleIntegerProperty getId() {
        return id;
    }

    public void setId(int id) {
        this.id = new SimpleIntegerProperty(id);
    }

    public String getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(String idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public int getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(int tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public Integer getSexo() {
        return sexo;
    }

    public void setSexo(Integer sexo) {
        this.sexo = sexo;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public int getPuesto() {
        return puesto;
    }

    public void setPuesto(int puesto) {
        this.puesto = puesto;
    }

    public String getPuestoName() {
        return puestoName;
    }

    public void setPuestoName(String puestoName) {
        this.puestoName = puestoName;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public String getEstadoName() {
        return estadoName;
    }

    public void setEstadoName(String estadoName) {
        this.estadoName = estadoName;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public int getRol() {
        return rol;
    }

    public void setRol(int rol) {
        this.rol = rol;
    }

    public String getRolName() {
        return rolName;
    }

    public void setRolName(String rolName) {
        this.rolName = rolName == null ? "Global" : rolName;
    }

    public CheckBox getValidarEm() {
        return validarEm;
    }

    public void setValidarEm(CheckBox validarEm) {
        this.validarEm = validarEm;
    }

    public String getInformacion() {
        return informacion;
    }

    public void setInformacion(String informacion) {
        this.informacion = informacion == null ? "" : informacion;
    }

    @Override
    public String toString() {
        return apellidos + " " + nombres;
    }

}
