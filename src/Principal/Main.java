/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Principal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;

/**
 *
 * @author Felipon
 */
public class Main {
    //Definimos variables estaticas globales de la clase
    private static Connection conn;
    private static Statement st;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //Creamos la conexión a la base de datos
        try {
            conn = DriverManager.getConnection("jdbc:ucanaccess://AD02.mdb");
            st = conn.createStatement();  
        } catch (SQLException ex) {
            System.err.println("SQL Exception: " + ex.toString());
        }
        //Llamamos al menu
        menu();
    }
    
    private static void menu(){
        
        int menu = Integer.parseInt(JOptionPane.showInputDialog(""
                + "Introduzca un valor a ejecutar: \n"
                + "1 - Crear la tabla de clientes \n"
                + "2 - Crear la tabla de motos \n"
                + "3 -  Inserta clientes"));
        switch(menu) {
            case 1:
                creaCliente();
                menu();
                break;
            case 2:
                creaMoto();
                menu();
            case 3:
                insertaClientes();
            default:
                break;
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
            //Ejecutamos la consulta
            st.execute("CREATE TABLE moto (matricula VARCHAR2(8) PRIMARY KEY NOT NULL, "
                    + "modelo VARCHAR2(50), "
                    + "marca VARCHAR2(100), "
                    + "color VARCHAR2(100), "
                    + "codCliente VARCHAR2(9))");
            //modificamos y añadimos la relación
            st.execute("ALTER TABLE moto ADD CONSTRAINT fk_cliente FOREIGN KEY (codCliente) REFERENCES cliente (DNI) ON DELETE CASCADE");
            //Llamamos al menu de nuevo para que sea mostrado al terminar
        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e.toString());
        }
    }
    
    private static void insertaClientes() {
        try {
            conn.setAutoCommit(false);
            st.execute("insert into cliente (DNI, nombre, apellidos, email, telefono) values('33305686F' , 'JUAN', 'JUAN JUAN', 'juanjuan@juan.es', 954969696)");
            st.execute("insert into cliente (DNI, nombre, apellidos, email, telefono) values('72175846Y' , 'ANA', 'ANA ANA', 'anaana@ana.es', 954696969)");
            conn.commit();
        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e.toString());
        }
    }
    
}
