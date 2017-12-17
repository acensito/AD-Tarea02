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

/**
 *
 * @author Felipon
 */
public class Main {
    static Connection con;
    static Statement st;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            con = DriverManager.getConnection("jdbc:ucanaccess://AD02.mdb");
            st = con.createStatement();  
        } catch (SQLException ex) {
            System.err.println("SQL Exception: " + ex.toString());
        }
    }
    
    private static void creaClientes() {
        try {
            st.execute("CREATE TABLE cliente(DNI VARCHAR2(9) PRIMARY KEY NOT NULL, nombre VARCHAR2(50), apellidos VARCHAR2(100), email VARCHAR2(100), telefono INT(9))");
        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e.toString());
        }
    }
    
}
