package proyecto;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import oracle.jdbc.OracleTypes;

public class InterfazCalificacion extends JFrame {

    private JComboBox<String> comboNoticias;
    private JSpinner spinnerPuntaje;
    private JButton btnRegistrar;
    private JButton btnVolver;
    private final JButton btnCalificar = new JButton("Calificar");
    
    
    
    public InterfazCalificacion() {
        super("⭐ Calificar Noticias");
        setLayout(new BorderLayout(10, 10));

        // Panel principal
        JPanel panelCentro = new JPanel(new GridLayout(3, 2, 10, 10));

        // Componentes
        panelCentro.add(new JLabel("Seleccione una noticia:"));
        comboNoticias = new JComboBox<>();
        panelCentro.add(comboNoticias);

        panelCentro.add(new JLabel("Puntaje (1 a 5):"));
        spinnerPuntaje = new JSpinner(new SpinnerNumberModel(3, 1, 5, 1));
        panelCentro.add(spinnerPuntaje);

        add(panelCentro, BorderLayout.CENTER);

        // Panel inferior con botones
        JPanel panelBotones = new JPanel();
        btnRegistrar = new JButton("Registrar Calificación");
        btnVolver = new JButton("Volver");
        panelBotones.add(btnRegistrar);
        panelBotones.add(btnVolver);
        add(panelBotones, BorderLayout.SOUTH);

        // Eventos
        btnRegistrar.addActionListener(e -> registrarCalificacion());
        btnVolver.addActionListener(e -> {
            dispose();
            new InterfazNoticias();
        });

        // Configuración general
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 250);
        setLocationRelativeTo(null);
        setVisible(true);

        // Cargar noticias al inicio
        cargarNoticias();
    }

    private void cargarNoticias() {
        try (Connection conn = ConexionOracle.getConnection();
             CallableStatement cs = conn.prepareCall("{ ? = call LISTAR_ULTIMAS_NOTICIAS(?) }")) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setNull(2, Types.INTEGER);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                comboNoticias.removeAllItems();
                while (rs.next()) {
                    int id = rs.getInt("ID_NOTICIA");
                    String titulo = rs.getString("TITULO");
                    comboNoticias.addItem(id + " - " + titulo);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar noticias: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void registrarCalificacion() {
        if (comboNoticias.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una noticia.");
            return;
        }

        try (Connection conn = ConexionOracle.getConnection();
             CallableStatement cs = conn.prepareCall("{call REGISTRAR_CALIFICACION(?, ?, ?)}")) {

            // Obtener ID de noticia del combo
            String seleccion = comboNoticias.getSelectedItem().toString();
            int idNoticia = Integer.parseInt(seleccion.split(" - ")[0]);

            int puntaje = (int) spinnerPuntaje.getValue();
            int idUsuario = 1; // Usuario fijo para la prueba

            cs.setInt(1, idNoticia);
            cs.setInt(2, idUsuario);
            cs.setInt(3, puntaje);
            cs.execute();

            JOptionPane.showMessageDialog(this,
                    "Calificación registrada con éxito.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al registrar la calificación: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(InterfazCalificacion::new);
    }
}
