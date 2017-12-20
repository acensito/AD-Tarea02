/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Principal;

import java.sql.Connection;
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
    //Definimos variables estaticas globales de la clase
    private static Connection con;
    private static Statement st;
    private static PreparedStatement ps;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //Creamos la conexión a la base de datos
        try {
            con = DriverManager.getConnection("jdbc:ucanaccess://AD02.mdb");
            st = con.createStatement();  
        } catch (SQLException ex) {
            System.err.println("SQL Exception: " + ex.toString());
        }
        //Llamamos al menu
        menu();
    }
    
    private static void menu() {   
        String menu = JOptionPane.showInputDialog(
                   "Introduzca un valor a ejecutar: \n\n"
                + "**************************************************************\n"
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
            System.err.println("Salida del programa");
        }
         
    }
    
    private static void creaCliente() {
        try {
            //Ejecutamos la consulta
            st.execute("CREATE TABLE cliente (DNI VARCHAR2(9) PRIMARY KEY NOT NULL, "
                    + "nombre VARCHAR2(50), "
                    + "apellidos VARCHAR2(100), "
                    + "email VARCHAR2(100), "
                    + "telefono INT)");
            //Llamamos al menu de nuevo para que sea mostrado al terminar
        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e.toString());
        }
    }
    
    private static void creaMoto() {
        try {
            st.execute("CREATE TABLE moto (matricula VARCHAR2(8) PRIMARY KEY NOT NULL, "
                    + "modelo VARCHAR2(50), "
                    + "marca VARCHAR2(100), "
                    + "color VARCHAR2(100), "
                    + "codCliente VARCHAR2(9))");
            //modificamos y añadimos la relación
            st.execute("ALTER TABLE moto ADD CONSTRAINT fk_cliente FOREIGN KEY (codCliente) "
                    + "REFERENCES cliente (DNI) ON DELETE CASCADE");
            //Llamamos al menu de nuevo para que sea mostrado al terminar
        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e.toString());
        }
    }
    
    private static void insertaClientes() {
        try {
            con.setAutoCommit(false);
            st.execute("insert into cliente (DNI, nombre, apellidos, email, telefono) "
                    + "values('33305686F' , 'JUAN', 'JUAN JUAN', 'juanjuan@juan.es', 954969696)");
            st.execute("insert into cliente (DNI, nombre, apellidos, email, telefono) "
                    + "values('72175846Y' , 'ANA', 'ANA ANA', 'anaana@ana.es', 954696969)");
            con.commit();
        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e.toString());
        }
    }
    
    private static void insertaMotos() {
        try {
            con.setAutoCommit(false);
            st.execute("insert into moto (matricula, modelo, marca, color, codCliente) "
                    + "values('9999HHF', 'Z750', 'KAWASAKI', 'VERDE', '33305686F')");
            st.execute("insert into moto (matricula, modelo, marca, color, codCliente) "
                    + "values('6666FFH', 'GSR600', 'SUZUKI', 'NEGRA', '72175846Y')");
            con.commit();
        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e.toString());
        }
    }
    
    private static void recuperaClientes() {
        try {
            ResultSet rs = st.executeQuery("SELECT * FROM cliente");
            
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
            System.err.println("SQL Exception: " + e.toString());
        }
    }
    
    private static void recuperaMotos() {
        try {
            ResultSet rs = st.executeQuery("SELECT * FROM moto");
            
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
            System.err.println("SQL Exception: " + e.toString());
        }
    }
    
    private static void actualizaCliente1() {
        try {
            st.executeUpdate("UPDATE cliente SET nombre = 'PEPE' WHERE DNI = '33305686F'");
        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e.toString());
        }
    }
    
    private static void actualizaCliente2() {
        try {
            ps = con.prepareStatement("UPDATE cliente SET nombre = ? WHERE DNI = ?" );
            ps.setString(1, "JOSEFA");
            ps.setString(2, "72175846Y");
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e.toString());
        }
    }
    
    private static void actualizaMoto1() {
        try {
            ps = con.prepareStatement("UPDATE moto SET color = ? WHERE matricula = ?" );
            ps.setString(1, "ROJA");
            ps.setString(2, "9999HHF");
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e.toString());
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
        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e.toString());
        }
    }
    
}
