
package proyecto;

import java.sql.*;

public class AppNoticias {
    public static void main(String[] args) {
        String url = "jdbc:oracle:thin:@localhost:1521:ORCL";
        String user = "practica1";
        String password = "12345";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("✅ Conectado a Oracle");

            // -----------------------------
            // 1️⃣ Ejecutar un procedimiento simple
            // AUMENTAR_VISITA(1)
            // -----------------------------
            try (CallableStatement cs = conn.prepareCall("{call AUMENTAR_VISITA(?)}")) {
                cs.setInt(1, 1);
                cs.execute();
                System.out.println("👍 Visita aumentada para la noticia 1");
            }

            // -----------------------------
            // 2️⃣ Ejecutar una función que retorna un cursor
            // LISTAR_ULTIMAS_NOTICIAS(NULL)
            // -----------------------------
            try (CallableStatement cs = conn.prepareCall("{ ? = call LISTAR_ULTIMAS_NOTICIAS(?) }")) {
                cs.registerOutParameter(1, oracle.jdbc.OracleTypes.CURSOR);
                cs.setNull(2, java.sql.Types.INTEGER); // NULL -> todas las noticias
                cs.execute();

                try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                    System.out.println("\n📰 Últimas noticias:");
                    while (rs.next()) {
                        System.out.printf("• %s (%s)\n", rs.getString("TITULO"), rs.getDate("FECHA_PUBLICACION"));
                    }
                }
            }

            // -----------------------------
            // 3️⃣ Ejecutar LISTAR_TOP_NOTICIAS('VISITAS', NULL)
            // -----------------------------
            try (CallableStatement cs = conn.prepareCall("{ ? = call LISTAR_TOP_NOTICIAS(?, ?) }")) {
                cs.registerOutParameter(1, oracle.jdbc.OracleTypes.CURSOR);
                cs.setString(2, "VISITAS");
                cs.setNull(3, java.sql.Types.INTEGER); // NULL -> todos los temas
                cs.execute();

                try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                    System.out.println("\n🔥 Noticias más visitadas:");
                    while (rs.next()) {
                        System.out.printf("• %s (Visitas: %d)\n", 
                            rs.getString("TITULO"), rs.getInt("VISITAS"));
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

