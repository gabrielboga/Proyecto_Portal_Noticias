package proyecto;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionOracle {

    private static final String URL = "jdbc:oracle:thin:@localhost:1521:ORCL"; // Cambia según tu BD
    private static final String USER = "practica1";
    private static final String PASS = "12345";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}


    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
