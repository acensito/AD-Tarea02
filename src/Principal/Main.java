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
        //Lanzamos panel input
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
        //En el caso que no se cancele 
        if (menu != null) {
            //Dependiendo del valor introducido, llamamos al método correspondiente
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
                    //Si se introduce cualquier otra cosa, se llama al menu nuevamente
                    menu();
                    break;
            }
        } else {
            //Cerramos la conexión
            try {
                con.close();
            //En el caso de existir errores SQL, se notifican los mismos
            } catch (SQLException e) {
                System.err.println("SQL Error: " + e.toString());
            }
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
            //Si existe la tabla ya creada en la BD
            if (compruebaTabla("cliente")){
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
            //Llamamos al metodo compruebaCliente para comprobar si existe la tabla
            if (compruebaTabla("cliente")){
                //Se consulta si existe la tabla moto llamando al metodo
                if (compruebaTabla("moto")) {
                    //En caso de existir, se notifica al usuario feedback
                    System.err.println("Tabla existente, se omite el paso");
                } else {
                    //En el caso de no existir, se crea la tabla moto ejecutando la consulta correspondiente
                    //Desactivamos los commits
                    //con.setAutoCommit(false); //Da error paso a paso
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
                    //con.commit(); //De esta manera da error paso a paso
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
     * Método inserta clientes. Comprobará previamente si dichos clientes existen en la base de datos. Si no existen en la
     * tabla, los insertará y en caso contrario, omitirá el paso.
     */
    private static void insertaClientes() {
        try {
            //Comprobamos previamente si existe la tabla cliente
            if (compruebaTabla("cliente")) {
                //Desactivamos los commits
                con.setAutoCommit(false);
                //Consultamos si existe un cliente 
                rs = st.executeQuery("SELECT * FROM cliente WHERE DNI = '33305686F' ");
                //Si existe
                if(rs.next()) {
                    //Lanzamos mensaje feedback
                    System.err.println("Ya existe el registro, se omite su inserción");
                //Caso contrario
                } else {
                    //Ejecutamos la consulta e insertamos los datos
                    st.execute("insert into cliente (DNI, nombre, apellidos, email, telefono) "
                            + "values('33305686F' , 'JUAN', 'JUAN JUAN', 'juanjuan@juan.es', 954969696)");
                    //Lanzamos mensaje feedback de la inserción
                    System.out.println("Cliente insertado.");
                }
                //Procedemos de igual manera que la anterior
                //Consultamos si existe un cliente
                rs = st.executeQuery("SELECT * FROM cliente WHERE DNI = '72175846Y' ");
                //Si existe
                if (rs.next()) {
                    //Lanzamos mensaje feedback
                    System.err.println("Ya existe el registro, se omite su inserción");
                //Caso contrario
                } else {
                    //Ejecutamos la consulta e insertamos los datos
                    st.execute("insert into cliente (DNI, nombre, apellidos, email, telefono) "
                            + "values('72175846Y' , 'ANA', 'ANA ANA', 'anaana@ana.es', 954696969)");
                    //Lanzamos mensaje feedback de la inserción
                    System.out.println("Cliente insertado.");
                }
                //Hacemos commit a los cambios realizados
                con.commit();
            //En el caso de no existir la tabla cliente
            } else {
                //Notificamos dicha eventualidad feedback
                System.err.println("Debe crear previamente la tabla cliente.");   
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
     * Método inserta motos, que comprueba previamente si una moto esta insertada. En caso contrario, inserta la misma en
     * la base de datos.
     */
    private static void insertaMotos() {
        try {
            if(compruebaTabla("moto")) {
                //Desactivamos los commits
                con.setAutoCommit(false);
                //Consultamos si existe una moto previamente
                rs = st.executeQuery("SELECT * FROM moto WHERE matricula = '9999HHF' ");
                //Si existe
                if (rs.next()) {
                    //Lazamos mensaje feedback
                    System.err.println("Ya existe el registro, se omite su inserción");
                //Caso contrario
                } else {
                    //Ejecutamos la consulta e insertamos los datos
                    st.execute("insert into moto (matricula, modelo, marca, color, codCliente) "
                            + "values('9999HHF', 'Z750', 'KAWASAKI', 'VERDE', '33305686F')");
                    //Lanzamos mensaje feedback de la inserción
                    System.out.println("Moto insertada.");
                }
                //Proedemos de igual manera que en el anterior
                //Consultamos si existe una moto previamente
                rs = st.executeQuery("SELECT * FROM moto WHERE matricula = '6666FFH' ");
                //Si existe
                if (rs.next()) {
                    //Lanzamos mensaje feedback
                    System.err.println("Ya existe el registro, se omite su inserción");
                //Caso contrario
                } else {
                    //Ejecutamos la consulta
                    st.execute("insert into moto (matricula, modelo, marca, color, codCliente) "
                            + "values('6666FFH', 'GSR600', 'SUZUKI', 'NEGRA', '72175846Y')");
                    //Lanzamos mensaje feedback de la inserción
                    System.out.println("Moto insertada.");
                }
                //Hacemos commit a los cambios realizados
                con.commit();
            //En el caso de no existir la tabla moto
            } else {
                //Notificamos dicha eventualidad feedback
                System.err.println("Debe crear previamente la tabla moto.");   
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
     * Método recuperaClientes, que consulta y muestra los clientes de la tabla
     */
    private static void recuperaClientes() {
        try {
            //Comprobamos previamente si existe la tabla
            if (compruebaTabla("cliente")) {
                //Ejecutamos la consulta
                rs = st.executeQuery("SELECT * FROM cliente");
                //Flag de control de resultados, de inicio, damos por sentado que no hay resultados
                boolean resultados = false;
                //Si existen resultados...
                //Mientras existan resultados, se van asignando los campos a variables y posteriormente se muestran
                while (rs.next()) {
                    String dni = rs.getString("DNI");
                    String nombre = rs.getString("nombre");
                    String apellidos = rs.getString("apellidos");
                    String email = rs.getString("email");
                    int telefono = rs.getInt("telefono");
                    //Imprimimos en pantalla
                    System.out.println("DNI: " + dni + " - Nombre y apellidos: " + nombre + " " + apellidos 
                            + " - Email: " + email + " - Teléfono: " + telefono); 
                    //Al existir un resultado, cambiamos el flag de control a true
                    resultados = true;
                }
                //Si no hay resultados
                if (!resultados) {
                    System.err.println("No existen clientes que mostrar");
                }
            //En el caso de no existir la tabla cliente
            } else {
                //Notificamos dicha eventualidad feedback
                System.err.println("Debe crear previamente la tabla cliente para poder realizar consultas.");   
            }
        //Si existen errores SQL
        } catch (SQLException e) {
            //Lanzamos mensaje feedback
            System.err.println("SQL Error: " + e.toString());
        }
    }
    
    /**
     * Método recuperaMotos, que consulta y muestra las motos de la tabla
     */
    private static void recuperaMotos() {
        try {
            //Comprobamos previamente si existe la tabla
            if (compruebaTabla("moto")) {
                //Ejecutamos la consulta
                rs = st.executeQuery("SELECT * FROM moto");
                //Flag de control de resultados, de inicio, damos por sentado que no hay resultados
                boolean resultados = false;
                //Si existen resultados...
                //Mientras existan resultados, se van asignando los campos a variables y posteriormente se muestran
                while (rs.next()) {
                    String matricula = rs.getString("matricula");
                    String modelo = rs.getString("modelo");
                    String marca = rs.getString("marca");
                    String color = rs.getString("color");
                    String cliente = rs.getString("codCliente");
                    //Imprimimos en pantalla
                    System.out.println("Matricula: " + matricula + " - Marca y Modelo " + marca + " " + modelo 
                            + " - Color: " + color + " - DNI Propietario: " + cliente); 
                    //Al existir un resultado, cambiamos el flag de control a true
                    resultados = true;
                }
                //Si no hay resultados
                if (!resultados) {
                    System.err.println("No existen motos que mostrar");
                }
            //En el caso de no existir la tabla moto
            } else {
                //Notificamos dicha eventualidad feedback
                System.err.println("Debe crear previamente la tabla moto para poder realizar consultas.");   
            }
        //Si existen errores SQL
        } catch (SQLException e) {
            //Lanzamos mensaje feedback
            System.err.println("SQL Error: " + e.toString());
        }
    }
    
    /**
     * Método actualizaCliente1, que actualiza un cliente definido
     */
    private static void actualizaCliente1() {
        try {
            //Comprobamos si existe la tabla
            if (compruebaTabla("cliente")) {
                //Ejecutamos la consulta
                st.executeUpdate("UPDATE cliente SET nombre = 'PEPE' WHERE DNI = '33305686F'");
            //En el caso de no existir la tabla cliente
            } else {
                //Notificamos dicha eventualidad feedback
                System.err.println("Debe crear previamente la tabla cliente para poder realizar modificaciones.");   
            }
        //Si existen errores SQL
        } catch (SQLException e) {
            //Lanzamos mensaje feedback
            System.err.println("SQL Error: " + e.toString());
        }
    }
    
    /**
     * Método actualizaCliente2, que actualiza un cliente definido usando consultas preparadas
     */
    private static void actualizaCliente2() {
        try {
            //Comprobamos si existe la tabla
            if (compruebaTabla("cliente")) {
                //Preparamos la consulta
                ps = con.prepareStatement("UPDATE cliente SET nombre = ? WHERE DNI = ?" );
                //Definimos los parametros
                ps.setString(1, "JOSEFA");
                ps.setString(2, "72175846Y");
                //Ejecutamos la consulta
                ps.executeUpdate();
            //En el caso de no existir la tabla cliente
            } else {
                //Notificamos dicha eventualidad feedback
                System.err.println("Debe crear previamente la tabla cliente para poder realizar modificaciones.");   
            }
        //Si existen errores SQL
        } catch (SQLException e) {
            //Lanzamos mensaje feedback
            System.err.println("SQL Error: " + e.toString());
        }
    }
    
    /**
     * Método actualizaMoto1, que actualiza una moto usando consultas preparadas
     */
    private static void actualizaMoto1() {
        try {
            //Comprobamos si existe la tabla
            if (compruebaTabla("moto")) {
                //Preparamos la consulta
                ps = con.prepareStatement("UPDATE moto SET color = ? WHERE matricula = ?" );
                //Definimos los parametros
                ps.setString(1, "ROJA");
                ps.setString(2, "9999HHF");
                //Ejecutamos la consulta
                ps.executeUpdate();
            //En el caso de no existir la tabla moto
            } else {
                //Notificamos dicha eventualidad feedback
                System.err.println("Debe crear previamente la tabla moto para poder realizar modificaciones.");   
            }
        //Si existen errores SQL
        } catch (SQLException e) {
            //Lanzamos mensaje feedback
            System.err.println("SQL Error: " + e.toString());
        }
    }
    
    /**
     * Método todos, que ejecutará todos los pasos de un solo golpe. Se desactivarán los commits y en el caso de existir
     * algún fallo durante las transacciones, se regresará al estado anterior.
     */
    private static void todos() {
        try {
            //Desactivamos los commits
            con.setAutoCommit(false);
            //Llamamos a los metodos necesarios
            creaCliente();
            creaMoto();
            insertaClientes();
            insertaMotos();
            recuperaClientes();
            recuperaMotos();
            actualizaCliente1();
            actualizaCliente2();
            actualizaMoto1();
            //Realizamos commit
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
    
    /**
     * Método que comprueba si existe una tabla en la base de datos pasada por parámetros
     * 
     * @param tabla String del nombre de la tabla a comprobar
     * 
     * @return Valor true si existe o valor false en caso contrario
     */
    private static boolean compruebaTabla(String tabla) {
        //Creamos flag de control y damos por sentado previamente que no existe la tabla pasada
        boolean existe = false;
        try {
            //Obtenemos las tabla solicitada
            rs = db.getTables(null, null, tabla, null);
            //Si existe, lo asignamos al valor
            existe = (rs.next());
        } catch (SQLException e) {
            //Lanzamos mensaje feedback
            System.err.println("SQL Error: " + e.toString());
        }
        return existe;
    }
    
    
}