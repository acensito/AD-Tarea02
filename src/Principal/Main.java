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
    private static Connection con;
    private static Statement st;

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
    
    private static void menu(){
        
        int menu = Integer.parseInt(JOptionPane.showInputDialog(""
                + "Introduzca un valor a ejecutar: \n"
                + "1 - Crear la tabla de clientes \n"
                + "2 - Crear la tabla de motos \n"));
        
        switch(menu) {
            case 1:
                creaCliente();
                break;
            case 2:
                creaMoto();
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
            menu();
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
            menu();
        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e.toString());
        }
    }
    
}
