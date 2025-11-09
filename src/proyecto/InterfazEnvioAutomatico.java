package proyecto;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import oracle.jdbc.OracleTypes;

public class InterfazEnvioAutomatico extends JFrame {

    private JComboBox<String> comboNoticias;
    private JTextArea txtResultado;
    private JButton btnEnviar;
    private JButton btnVolver;

    // Constructor por defecto
    public InterfazEnvioAutomatico() {
        initComponents();
        cargarNoticias();
    }

    // Nuevo constructor que recibe el ID de noticia
    public InterfazEnvioAutomatico(int idNoticiaSeleccionada) {
        this(); // llama al constructor por defecto para inicializar todo

        // Seleccionar automáticamente la noticia en el combo
        for (int i = 0; i < comboNoticias.getItemCount(); i++) {
            String item = comboNoticias.getItemAt(i);
            int id = Integer.parseInt(item.split(" - ")[0]);
            if (id == idNoticiaSeleccionada) {
                comboNoticias.setSelectedIndex(i);
                break;
            }
        }
    }

    // Inicialización de componentes
    private void initComponents() {
        super.setTitle("📬 Envío Automático de Noticias");
        setLayout(new BorderLayout(10, 10));

        // Panel superior: selección de noticia
        JPanel panelSuperior = new JPanel(new GridLayout(1, 2, 10, 10));
        panelSuperior.add(new JLabel("Seleccione una noticia:"));
        comboNoticias = new JComboBox<>();
        panelSuperior.add(comboNoticias);
        add(panelSuperior, BorderLayout.NORTH);

        // Panel central: resultados
        txtResultado = new JTextArea();
        txtResultado.setEditable(false);
        add(new JScrollPane(txtResultado), BorderLayout.CENTER);

        // Panel inferior: botones
        JPanel panelBotones = new JPanel();
        btnEnviar = new JButton("Registrar Envío");
        btnVolver = new JButton("Volver");
        panelBotones.add(btnEnviar);
        panelBotones.add(btnVolver);
        add(panelBotones, BorderLayout.SOUTH);

        // Eventos
        btnEnviar.addActionListener(e -> registrarEnvio());
        btnVolver.addActionListener(e -> {
            dispose();
            new InterfazNoticias(); // volver a la interfaz principal
        });

        // Configuración de la ventana
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Cargar noticias desde la base de datos
    private void cargarNoticias() {
        try (Connection conn = ConexionOracle.getConnection();
             CallableStatement cs = conn.prepareCall("{ ? = call LISTAR_ULTIMAS_NOTICIAS(?) }")) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setNull(2, Types.INTEGER); // todas las noticias
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

    // Registrar envío automático
    private void registrarEnvio() {
        if (comboNoticias.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una noticia.");
            return;
        }

        try (Connection conn = ConexionOracle.getConnection();
             CallableStatement cs = conn.prepareCall("{ call REGISTRAR_ENVIO_AUTOMATICO(?) }")) {

            // Obtener ID de noticia seleccionada
            String seleccion = comboNoticias.getSelectedItem().toString();
            int idNoticia = Integer.parseInt(seleccion.split(" - ")[0]);

            cs.setInt(1, idNoticia);
            cs.execute();

            // Mostrar resultados (usuarios a los que se envió)
            mostrarEnvios(idNoticia);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al registrar el envío: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Mostrar envíos en el JTextArea
    private void mostrarEnvios(int idNoticia) {
        txtResultado.setText("");

        String query = """
            SELECT U.NOMBRE, U.EMAIL, N.TITULO, E.FECHA_ENVIO
            FROM ENVIO_AUTOMATICO E
            JOIN USUARIO U ON E.ID_USUARIO = U.ID_USUARIO
            JOIN NOTICIA N ON E.ID_NOTICIA = N.ID_NOTICIA
            WHERE N.ID_NOTICIA = ?
            ORDER BY E.FECHA_ENVIO DESC
        """;

        try (Connection conn = ConexionOracle.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, idNoticia);
            try (ResultSet rs = ps.executeQuery()) {
                StringBuilder sb = new StringBuilder();
                sb.append("Usuarios que recibieron la noticia:\n");
                sb.append("----------------------------------------------------\n");

                boolean hayEnvios = false;
                while (rs.next()) {
                    hayEnvios = true;
                    sb.append("👤 ").append(rs.getString("NOMBRE"))
                      .append(" (").append(rs.getString("EMAIL")).append(")")
                      .append(" — ").append(rs.getDate("FECHA_ENVIO"))
                      .append("\n");
                }

                if (!hayEnvios) {
                    sb.append("⚠️ No se enviaron notificaciones (ningún usuario coincide con las preferencias).");
                }

                txtResultado.setText(sb.toString());
            }

        } catch (SQLException e) {
            txtResultado.setText("Error al mostrar resultados: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new InterfazEnvioAutomatico());
    }
}



