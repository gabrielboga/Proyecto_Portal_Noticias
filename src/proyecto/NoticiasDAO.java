package proyecto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NoticiasDAO {

    private Connection conn;

    public NoticiasDAO() {
        try {
            conn = ConexionOracle.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(null,
                "No se pudo conectar a la base de datos:\n" + e.getMessage(),
                "Error de conexión", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    // Listar últimas noticias (ejemplo simple con título)
    public List<String> listarUltimasNoticias() {
        List<String> noticias = new ArrayList<>();
        String sql = "SELECT TITULO FROM NOTICIA ORDER BY FECHA_PUBLICACION DESC";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                noticias.add(rs.getString("TITULO"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(null,
                "Error al listar noticias:\n" + e.getMessage(),
                "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
        return noticias;
    }

    // Aumentar visitas
    public void aumentarVisita(int idNoticia) {
        String sql = "UPDATE NOTICIA SET VISITAS = NVL(VISITAS,0) + 1 WHERE ID_NOTICIA = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idNoticia);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(null,
                "Error al aumentar visita:\n" + e.getMessage(),
                "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    // Registrar envío automático
    public void registrarEnvioAutomatico(int idNoticia) {
        String sql = "{call REGISTRAR_ENVIO_AUTOMATICO(?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setInt(1, idNoticia);
            cs.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(null,
                "Error al registrar envío automático:\n" + e.getMessage(),
                "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }
}
 




