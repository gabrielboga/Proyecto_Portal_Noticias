package proyecto;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class InterfazNoticias extends JFrame {

    private final NoticiasDAO dao = new NoticiasDAO();
    private final DefaultListModel<String> modeloNoticias = new DefaultListModel<>();
    private final JList<String> listaNoticias = new JList<>(modeloNoticias);

    private final JButton btnActualizar = new JButton("Actualizar lista");
    private final JButton btnAumentarVisita = new JButton("Aumentar visita");
    private final JButton btnVerVisitas = new JButton("Ver visitas");
    private final JButton btnCalificar = new JButton("Calificar");
    private final JButton btnEnvio = new JButton("Enviar automáticamente");

    public InterfazNoticias() {
        super("📰 Sistema de Noticias Digitales");
        setLayout(new BorderLayout(10, 10));

        // Panel superior con botones principales
        JPanel panelBotones = new JPanel();
        panelBotones.add(btnActualizar);
        panelBotones.add(btnAumentarVisita);
        panelBotones.add(btnVerVisitas);
        panelBotones.add(btnCalificar);
        add(panelBotones, BorderLayout.NORTH);

        // Lista de noticias en el centro
        add(new JScrollPane(listaNoticias), BorderLayout.CENTER);

        // Panel inferior solo para envío automático
        JPanel panelEnvio = new JPanel();
        panelEnvio.add(btnEnvio);
        add(panelEnvio, BorderLayout.SOUTH);

        // ==============================
        // Acciones de los botones
        // ==============================
        btnActualizar.addActionListener(e -> cargarNoticias());
        btnAumentarVisita.addActionListener(e -> aumentarVisitaSeleccionada());
        btnVerVisitas.addActionListener(e -> new InterfazVisitas());
        btnCalificar.addActionListener(e -> new InterfazCalificacion());

        // NUEVO: abrir ventana de envío automático
        btnEnvio.addActionListener(e -> {
            int index = listaNoticias.getSelectedIndex();
            if (index == -1) {
                JOptionPane.showMessageDialog(this, "Selecciona una noticia primero.");
                return;
            }
            int idNoticia = index + 1; // o usa el ID real desde tu DAO
            new InterfazEnvioAutomatico(idNoticia);
        });

        // Configuración general
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setVisible(true);

        cargarNoticias();
    }

    private void cargarNoticias() {
        modeloNoticias.clear();
        List<String> noticias = dao.listarUltimasNoticias();
        if (noticias.isEmpty()) {
            modeloNoticias.addElement("⚠️ No hay noticias disponibles.");
        } else {
            for (String n : noticias) modeloNoticias.addElement(n);
        }
    }

    private void aumentarVisitaSeleccionada() {
        int index = listaNoticias.getSelectedIndex();
        if (index == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una noticia primero.");
            return;
        }
        int idNoticia = index + 1; // o el ID real
        dao.aumentarVisita(idNoticia);
        JOptionPane.showMessageDialog(this,
                "Visita registrada para la noticia #" + idNoticia,
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(InterfazNoticias::new);
    }
}

