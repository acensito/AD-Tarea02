/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Principal;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;

/**
 *
 * @author Felipon
 */
public class Main {
    //Definimos variables estaticas globales de cada clase
    private static Connection con;
    private static Statement st;
    private static PreparedStatement ps;
    private static DatabaseMetaData db;
    private static ResultSet rs;

    /**
     * Metodo main principal
     * 
     * @param args 
     */
    public static void main(String[] args) {
        //Creamos la conexión a la base de datos
        try {
            //Conectamos con el driver jdbc de ucanaccess
            con = DriverManager.getConnection("jdbc:ucanaccess://AD02.mdb");
            //Creamos una declaración SQL para futuros usos, la asignamos al objeto
            st = con.createStatement();
            //Obtenemos y lo asignamos al objeto los metadatos relativos a la base de datos
            db = con.getMetaData();
        } catch (SQLException e) {
            //Si existen errores, que sean impresos
            System.err.println("SQL Error: " + e.toString());
        }
        //Llamamos al menu
        menu();
    }
    
    /**
     * Método Menú, que se mostrará en un JOptionpane cada vez que sea invocado. Si se pulsa cancelar o aceptar con el
     * cuadro de dialogo vacio, se cerrará la aplicación.
     */
    private static void menu() {   
        String menu = JOptionPane.showInputDialog(
                   "Introduzca un valor a ejecutar:\n"
                + "***********************************\n"
                + "1 - Crear la tabla de clientes \n"
                + "2 - Crear la tabla de motos \n"
                + "3 - Inserta clientes\n"
                + "4 - Inserta motos\n"
                + "5 - Consulta clientes\n"
                + "6 - Consulta motos\n"
                + "7 - Actualiza un cliente\n"
                + "8 - Actualiza un cliente con una consulta preparada\n"
                + "9 - Actualiza una moto con una consulta preparada\n"
                + "0 - Todas las anteriores");
        if (menu != null) {
            switch(menu) {
                case "1":
                    creaCliente();
                    menu();
                    break;
                case "2":
                    creaMoto();
                    menu();
                    break;
                case "3":
                    insertaClientes();
                    menu();
                    break;
                case "4":
                    insertaMotos();
                    menu();
                    break;
                case "5":
                    recuperaClientes();
                    menu();
                    break;
                case "6":
                    recuperaMotos();
                    menu();
                    break;
                case "7":
                    actualizaCliente1();
                    menu();
                    break;
                case "8":
                    actualizaCliente2();
                    menu();
                    break;
                case "9":
                    actualizaMoto1();
                    menu();
                    break;
                case "0":
                    todos();
                    menu();
                    break;
                default:
                    break;
            }
        } else {
            //Mensaje feedback de fin de la aplicación
            System.err.println("Salida del programa");
        }
    }
    
    /**
     * Método creaCliente, que creará la tabla cliente previa comprobación de su existencia en la base de datos. En el caso
     * de existir la misma, omitirá el paso y lo notificará.
     */
    private static void creaCliente() {
        try {
            //Consultamos si existe la tabla llamada cliente
            rs = db.getTables(null, null, "cliente", null);
            //Si existe la tabla ya creada en la BD
            if (rs.next()){
                //En caso de existir, se notifica al usuario feedback
                System.err.println("Tabla existente, se omite el paso");
            } else {
                //En el caso de no existir, ejecutamos la sentencia
                st.execute("CREATE TABLE cliente (DNI VARCHAR2(9) PRIMARY KEY NOT NULL, "
                        + "nombre VARCHAR2(50), "
                        + "apellidos VARCHAR2(100), "
                        + "email VARCHAR2(100), "
                        + "telefono INT)");
                //Mensaje de feedback
                System.out.println("Tabla cliente, creada.");
            }
        //En el caso de existir errores SQL, se notifican los mismos
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.toString());
        }
    }
    
    /**
     * Método creaCliente, que creará la tabla moto previa comprobación de la existencia en la base de datos de la tabla 
     * cliente dado que al no existir puede dar error. En el caso de existir la misma, comprobará posteriormente si existe la 
     * tabla moto, y en el caso de existir, omitirá el paso y lo notificará.
     */
    private static void creaMoto() {
        try {
            //Consultamos si existe una tabla llamada cliente
            rs = db.getTables(null, null, "cliente", null);
             if (rs.next()){
                //Se consulta si existe la tabla moto
                rs = db.getTables(null, null, "moto", null);
                if (rs.next()) {
                    //En caso de existir, se notifica al usuario feedback
                    System.err.println("Tabla existente, se omite el paso");
                } else {
                    //En el caso de no existir, se crea la tabla moto ejecutando la consulta correspondiente
                    //Desactivamos los commits
                    con.setAutoCommit(false);
                    //Ejecutamos la consulta de creacion de tabla
                    st.execute("CREATE TABLE moto (matricula VARCHAR2(8) PRIMARY KEY NOT NULL, "
                            + "modelo VARCHAR2(50), "
                            + "marca VARCHAR2(100), "
                            + "color VARCHAR2(100), "
                            + "codCliente VARCHAR2(9))");
                    //Se notifica la creacion feedback
                    System.out.println("Tabla moto, creada.");
                    //Modificamos y añadimos la relación entre las tablas cliente y moto
                    st.execute("ALTER TABLE moto ADD CONSTRAINT fk_cliente FOREIGN KEY (codCliente) "
                            + "REFERENCES cliente (DNI) ON DELETE CASCADE");
                    //Realizamos commit
                    con.commit();
                    //Se notitica la creación de la relación feedback
                    System.out.println("Relación cliente-moto, creada.");
                }
            //En el caso de no existir la tabla cliente
            } else {
                //Notificamos dicha eventualidad feedback
                System.err.println("Debe crear previamente la tabla cliente.");
                //EXTRA+ por si somos algo más sibaritas
                //Podemos crear la tabla cliente y posteriormente rellamar a este método
                //creaCliente();
                //creaMoto();
            }
        //Si existen errores
        } catch (SQLException e) {
            //Mensaje feedback del error
            System.err.println("SQL Error: " + e.toString());
            try {
                //Deshacer los cambios realizados al estado anterior
                con.rollback();
            } catch (SQLException ex) {
                //Mensaje feedback del error por el que no se ha podido volver al estado anterior
                System.err.println("SQL Error: " + ex.toString());
            }
        }
    }
    
    /**
     * Método inserta clientes. Comprobará previamente si dichos clientes existen en la base de datos
     */
    private static void insertaClientes() {
        try {
            con.setAutoCommit(false);
            
            rs = st.executeQuery("SELECT * FROM cliente WHERE DNI = '33305686F' ");
            if(rs.next()) {
                System.out.println("Ya existe el registro, se omite su inserción");
            } else {
                st.execute("insert into cliente (DNI, nombre, apellidos, email, telefono) "
                        + "values('33305686F' , 'JUAN', 'JUAN JUAN', 'juanjuan@juan.es', 954969696)");
            }
            
            rs = st.executeQuery("SELECT * FROM cliente WHERE DNI = '72175846Y' ");
            if (rs.next()) {
                System.out.println("Ya existe el registro, se omite su inserción");
            } else {
            st.execute("insert into cliente (DNI, nombre, apellidos, email, telefono) "
                    + "values('72175846Y' , 'ANA', 'ANA ANA', 'anaana@ana.es', 954696969)");
            }
            con.commit();
        //Si existen errores
        } catch (SQLException e) {
            //Mensaje feedback del error
            System.err.println("SQL Error: " + e.toString());
            try {
                //Deshacer los cambios realizados al estado anterior
                con.rollback();
            } catch (SQLException ex) {
                //Mensaje feedback del error por el que no se ha podido volver al estado anterior
                System.err.println("SQL Error: " + ex.toString());
            }
        }
    }
    
    private static void insertaMotos() {
        try {
            con.setAutoCommit(false);
            rs = st.executeQuery("SELECT * FROM moto WHERE matricula = '9999HHF' ");
            if (rs.next()) {
                System.out.println("Ya existe el registro, se omite su inserción");
            } else {
                st.execute("insert into moto (matricula, modelo, marca, color, codCliente) "
                        + "values('9999HHF', 'Z750', 'KAWASAKI', 'VERDE', '33305686F')");
            }
            
            rs = st.executeQuery("SELECT * FROM moto WHERE matricula = '6666FFH' ");
            if (rs.next()) {
                System.out.println("Ya existe el registro, se omite su inserción");
            } else {
                st.execute("insert into moto (matricula, modelo, marca, color, codCliente) "
                        + "values('6666FFH', 'GSR600', 'SUZUKI', 'NEGRA', '72175846Y')");
            }

            con.commit();
        //Si existen errores
        } catch (SQLException e) {
            //Mensaje feedback del error
            System.err.println("SQL Error: " + e.toString());
            try {
                //Deshacer los cambios realizados al estado anterior
                con.rollback();
            } catch (SQLException ex) {
                //Mensaje feedback del error por el que no se ha podido volver al estado anterior
                System.err.println("SQL Error: " + ex.toString());
            }
        }
    }
    
    private static void recuperaClientes() {
        try {
            rs = st.executeQuery("SELECT * FROM cliente");
            
            while (rs.next()) {
                String dni = rs.getString("DNI");
                String nombre = rs.getString("nombre");
                String apellidos = rs.getString("apellidos");
                String email = rs.getString("email");
                int telefono = rs.getInt("telefono");
                //imprimimos
                System.out.println("DNI: " + dni + " - Nombre y apellidos: " + nombre + " " + apellidos 
                        + " - Email: " + email + " - Teléfono: " + telefono); 
            }
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.toString());
        }
    }
    
    private static void recuperaMotos() {
        try {
            rs = st.executeQuery("SELECT * FROM moto");
            
            while (rs.next()) {
                String matricula = rs.getString("matricula");
                String modelo = rs.getString("modelo");
                String marca = rs.getString("marca");
                String color = rs.getString("color");
                String cliente = rs.getString("codCliente");
                //imprimimos
                System.out.println("Matricula: " + matricula + " - Marca y Modelo " + marca + " " + modelo 
                        + " - Color: " + color + " - DNI Propietario: " + cliente); 
            }
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.toString());
        }
    }
    
    private static void actualizaCliente1() {
        try {
            st.executeUpdate("UPDATE cliente SET nombre = 'PEPE' WHERE DNI = '33305686F'");
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.toString());
        }
    }
    
    private static void actualizaCliente2() {
        try {
            ps = con.prepareStatement("UPDATE cliente SET nombre = ? WHERE DNI = ?" );
            ps.setString(1, "JOSEFA");
            ps.setString(2, "72175846Y");
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.toString());
        }
    }
    
    private static void actualizaMoto1() {
        try {
            ps = con.prepareStatement("UPDATE moto SET color = ? WHERE matricula = ?" );
            ps.setString(1, "ROJA");
            ps.setString(2, "9999HHF");
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.toString());
        }
    }
    
    private static void todos() {
        try {
            con.setAutoCommit(false);
            creaCliente();
            creaMoto();
            insertaClientes();
            insertaMotos();
            recuperaClientes();
            recuperaMotos();
            actualizaCliente1();
            actualizaCliente2();
            actualizaMoto1();
            con.commit();   
        //Si existen errores
        } catch (SQLException e) {
            //Mensaje feedback del error
            System.err.println("SQL Error: " + e.toString());
            try {
                //Deshacer los cambios realizados al estado anterior
                con.rollback();
            } catch (SQLException ex) {
                //Mensaje feedback del error por el que no se ha podido volver al estado anterior
                System.err.println("SQL Error: " + ex.toString());
            }
        }
    }
    
}
