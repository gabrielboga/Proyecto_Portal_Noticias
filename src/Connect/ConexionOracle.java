
package Connect;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionOracle {
    public static void main(String[] args) {
        String url = "jdbc:oracle:thin:@//localhost:1521/XEPDB1";
        String user = "LENGUAJE1";
        String password = "123";

        try {
            // Cargar el driver
            Class.forName("oracle.jdbc.driver.OracleDriver");

            // Conectar
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("✅ Conexión exitosa a Oracle Database");

            conn.close();
        } catch (ClassNotFoundException e) {
            System.out.println("No se encontró el driver JDBC: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Error al conectar: " + e.getMessage());
        }
    }
}

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
