package proyecto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import oracle.jdbc.OracleTypes;

public class InterfazVisitas extends JFrame {

    private JTable tabla;
    private DefaultTableModel modelo;

    public InterfazVisitas() {
        super("📊 Noticias más visitadas");
        setLayout(new BorderLayout(10, 10));

        // Configurar columnas de la tabla
        String[] columnas = {"ID", "Título", "Autor", "Visitas", "Calificación"};
        modelo = new DefaultTableModel(columnas, 0);
        tabla = new JTable(modelo);

        // Botones
        JButton btnCargar = new JButton("Cargar más visitadas");
        JButton btnVolver = new JButton("Volver");

        JPanel panelBotones = new JPanel();
        panelBotones.add(btnCargar);
        panelBotones.add(btnVolver);

        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);

        // Eventos
        btnCargar.addActionListener(e -> cargarTopNoticias());
        btnVolver.addActionListener(e -> {
            dispose(); // Cierra esta ventana
            new InterfazNoticias(); // Abre la principal
        });

        // Configuración general
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(700, 400);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void cargarTopNoticias() {
        modelo.setRowCount(0); // Limpia tabla

        try (Connection conn = ConexionOracle.getConnection();
             CallableStatement cs = conn.prepareCall("{ ? = call LISTAR_TOP_NOTICIAS(?, ?) }")) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setString(2, "VISITAS"); // Tipo de ranking
            cs.setNull(3, Types.INTEGER); // Todos los temas
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) {
                    int id = rs.getInt("ID_NOTICIA");
                    String titulo = rs.getString("TITULO");
                    String autor = rs.getString("AUTOR");
                    int visitas = rs.getInt("VISITAS");
                    double promedio = rs.getDouble("PROMEDIO_CALIFICACION");
                    modelo.addRow(new Object[]{id, titulo, autor, visitas, promedio});
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al obtener las noticias: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(InterfazVisitas::new);
    }
}
