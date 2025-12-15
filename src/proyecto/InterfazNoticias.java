
package proyecto;

import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import oracle.jdbc.OracleTypes;

public class InterfazNoticias extends JFrame {
    private Connection conn;
    
    // Componentes principales
    private JTabbedPane tabbedPane;
    
    // Paneles
    private JPanel pnlNoticias;
    private JPanel pnlUsuarios;
    private JPanel pnlTemas;
    private JPanel pnlReportes;
    
    // Variables para acceder a las tablas
    private JTable tablaTemas;
    private JTable tablaSubtemas;
    private JTable tablaNoticias;
    private JTable tablaUsuarios;
    
    // Constructor
    public InterfazNoticias() {
        inicializarConexion();
        inicializarUI();
        cargarDatosIniciales();
    }
    
    private void inicializarConexion() {
        String url = "jdbc:oracle:thin:@//localhost:1521/XEPDB1";
        String user = "LENGUAJE1";
        String password = "123";
        
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("✅ Conectado a Oracle");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error de conexión: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void inicializarUI() {
        setTitle("Sistema de Gestión de Noticias");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Crear pestañas
        tabbedPane = new JTabbedPane();
        
        // Crear paneles
        pnlNoticias = crearPanelNoticias();
        pnlUsuarios = crearPanelUsuarios();
        pnlTemas = crearPanelTemas();
        pnlReportes = crearPanelReportes();
        
        // Agregar pestañas
        tabbedPane.addTab("📰 Noticias", pnlNoticias);
        tabbedPane.addTab("👥 Usuarios", pnlUsuarios);
        tabbedPane.addTab("🏷️ Temas", pnlTemas);
        tabbedPane.addTab("📊 Reportes", pnlReportes);
        
        add(tabbedPane);
    }
    
    // PANEL DE NOTICIAS
    private JPanel crearPanelNoticias() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel superior - Controles
        JPanel pnlControles = new JPanel(new GridLayout(2, 4, 5, 5));
        
        JButton btnAumentarVisita = new JButton("Aumentar Visita");
        JButton btnCalificar = new JButton("Calificar Noticia");
        JButton btnComentar = new JButton("Agregar Comentario");
        JButton btnPublicar = new JButton("Publicar Noticia");
        
        JButton btnUltimas = new JButton("Últimas Noticias");
        JButton btnTopVisitas = new JButton("Top Visitas");
        JButton btnTopCalif = new JButton("Top Calificaciones");
        JButton btnTopEnvios = new JButton("Top Envíos");
        
        pnlControles.add(btnAumentarVisita);
        pnlControles.add(btnCalificar);
        pnlControles.add(btnComentar);
        pnlControles.add(btnPublicar);
        pnlControles.add(btnUltimas);
        pnlControles.add(btnTopVisitas);
        pnlControles.add(btnTopCalif);
        pnlControles.add(btnTopEnvios);
        
        // Tabla para mostrar noticias
        String[] columnas = {"ID", "Título", "Autor", "Fecha", "Visitas", "Calificación"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0);
        tablaNoticias = new JTable(modelo);
        JScrollPane scrollPane = new JScrollPane(tablaNoticias);
        
        // Panel inferior - Detalles
        JPanel pnlDetalles = new JPanel(new BorderLayout());
        JTextArea txtDetalle = new JTextArea(8, 50);
        txtDetalle.setEditable(false);
        pnlDetalles.add(new JLabel("Detalles de Noticia:"), BorderLayout.NORTH);
        pnlDetalles.add(new JScrollPane(txtDetalle), BorderLayout.CENTER);
        
        // Agregar componentes al panel principal
        panel.add(pnlControles, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(pnlDetalles, BorderLayout.SOUTH);
        
        // Listeners
        btnAumentarVisita.addActionListener(e -> mostrarDialogoAumentarVisita());
        btnCalificar.addActionListener(e -> mostrarDialogoCalificar());
        btnComentar.addActionListener(e -> mostrarDialogoComentar());
        btnPublicar.addActionListener(e -> mostrarDialogoPublicar());
        
        btnUltimas.addActionListener(e -> listarUltimasNoticias());
        btnTopVisitas.addActionListener(e -> listarTopNoticias("VISITAS"));
        btnTopCalif.addActionListener(e -> listarTopNoticias("CALIFICACION"));
        btnTopEnvios.addActionListener(e -> listarTopNoticias("ENVIOS"));
        
        // Selección en tabla
        tablaNoticias.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaNoticias.getSelectedRow() != -1) {
                mostrarDetalleNoticia(tablaNoticias, txtDetalle);
            }
        });
        
        return panel;
    }
    
    // PANEL DE USUARIOS
    private JPanel crearPanelUsuarios() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Controles
        JPanel pnlControles = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        
        JButton btnNuevoUsuario = new JButton("Nuevo Usuario");
        JButton btnModificarUsuario = new JButton("Modificar Usuario");
        JButton btnEliminarUsuario = new JButton("Eliminar Usuario");
        JButton btnPrefEnvio = new JButton("Preferencias Envío");
        JButton btnRefrescar = new JButton("Refrescar");
        
        pnlControles.add(btnNuevoUsuario);
        pnlControles.add(btnModificarUsuario);
        pnlControles.add(btnEliminarUsuario);
        pnlControles.add(btnPrefEnvio);
        pnlControles.add(btnRefrescar);
        
        // Tabla de usuarios
        String[] columnas = {"ID", "Nombre", "Email", "Fecha Registro"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaUsuarios = new JTable(modelo);
        JScrollPane scrollPane = new JScrollPane(tablaUsuarios);
        
        panel.add(pnlControles, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Listeners
        btnNuevoUsuario.addActionListener(e -> mostrarDialogoGestionUsuario("INSERTAR"));
        btnModificarUsuario.addActionListener(e -> mostrarDialogoGestionUsuario("MODIFICAR"));
        btnEliminarUsuario.addActionListener(e -> mostrarDialogoGestionUsuario("ELIMINAR"));
        btnPrefEnvio.addActionListener(e -> mostrarDialogoPreferencias());
        btnRefrescar.addActionListener(e -> cargarUsuarios(modelo));
        
        // Cargar datos iniciales
        cargarUsuarios(modelo);
        
        return panel;
    }
    
    // PANEL DE TEMAS
    private JPanel crearPanelTemas() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JTabbedPane subtabs = new JTabbedPane();
        
        // Sub-pestaña para Temas
        JPanel pnlTemasPanel = new JPanel(new BorderLayout());
        JPanel pnlControlesTemas = new JPanel(new FlowLayout());
        JButton btnNuevoTema = new JButton("Nuevo Tema");
        JButton btnModificarTema = new JButton("Modificar Tema");
        JButton btnEliminarTema = new JButton("Eliminar Tema");
        pnlControlesTemas.add(btnNuevoTema);
        pnlControlesTemas.add(btnModificarTema);
        pnlControlesTemas.add(btnEliminarTema);
        
        String[] columnasTemas = {"ID", "Nombre"};
        DefaultTableModel modeloTemas = new DefaultTableModel(columnasTemas, 0);
        tablaTemas = new JTable(modeloTemas);
        
        pnlTemasPanel.add(pnlControlesTemas, BorderLayout.NORTH);
        pnlTemasPanel.add(new JScrollPane(tablaTemas), BorderLayout.CENTER);
        
        // Sub-pestaña para Subtemas
        JPanel pnlSubtemas = new JPanel(new BorderLayout());
        JPanel pnlControlesSubtemas = new JPanel(new FlowLayout());
        JButton btnNuevoSubtema = new JButton("Nuevo Subtema");
        JButton btnModificarSubtema = new JButton("Modificar Subtema");
        JButton btnEliminarSubtema = new JButton("Eliminar Subtema");
        
        // Inicialmente deshabilitar modificar y eliminar
        btnModificarSubtema.setEnabled(false);
        btnEliminarSubtema.setEnabled(false);
        
        pnlControlesSubtemas.add(btnNuevoSubtema);
        pnlControlesSubtemas.add(btnModificarSubtema);
        pnlControlesSubtemas.add(btnEliminarSubtema);
        
        String[] columnasSubtemas = {"ID", "Tema", "Nombre"};
        DefaultTableModel modeloSubtemas = new DefaultTableModel(columnasSubtemas, 0);
        tablaSubtemas = new JTable(modeloSubtemas);
        
        pnlSubtemas.add(pnlControlesSubtemas, BorderLayout.NORTH);
        pnlSubtemas.add(new JScrollPane(tablaSubtemas), BorderLayout.CENTER);
        
        subtabs.addTab("Temas", pnlTemasPanel);
        subtabs.addTab("Subtemas", pnlSubtemas);
        
        panel.add(subtabs, BorderLayout.CENTER);
        
        // Listeners
        btnNuevoTema.addActionListener(e -> mostrarDialogoTema("CREAR"));
        btnModificarTema.addActionListener(e -> mostrarDialogoTema("MODIFICAR"));
        btnEliminarTema.addActionListener(e -> mostrarDialogoTema("ELIMINAR"));
        
        btnNuevoSubtema.addActionListener(e -> mostrarDialogoSubtema("CREAR"));
        btnModificarSubtema.addActionListener(e -> mostrarDialogoSubtema("MODIFICAR"));
        btnEliminarSubtema.addActionListener(e -> mostrarDialogoSubtema("ELIMINAR"));
        
        // Listener para habilitar/deshabilitar botones de subtemas según la selección
        tablaSubtemas.getSelectionModel().addListSelectionListener(e -> {
            boolean haySeleccion = tablaSubtemas.getSelectedRow() != -1;
            btnModificarSubtema.setEnabled(haySeleccion);
            btnEliminarSubtema.setEnabled(haySeleccion);
        });
        
        // Listener para cuando se selecciona un tema
        tablaTemas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaTemas.getSelectedRow() != -1) {
                int idTema = Integer.parseInt(tablaTemas.getValueAt(tablaTemas.getSelectedRow(), 0).toString());
                System.out.println("Tema seleccionado ID: " + idTema);
            }
        });
        
        // Cargar datos
        cargarTemas(modeloTemas);
        cargarSubtemas(modeloSubtemas);
        
        return panel;
    }
    
    // PANEL DE REPORTES
    private JPanel crearPanelReportes() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Controles
        JPanel pnlControles = new JPanel(new GridLayout(1, 4, 5, 5));
        
        JButton btnReporteUsuario = new JButton("Reporte por Usuario");
        JButton btnRankingAutores = new JButton("Ranking de Autores");
        JButton btnTemasPopulares = new JButton("Temas Populares");
        JButton btnEstadisticas = new JButton("Estadísticas");
        
        pnlControles.add(btnReporteUsuario);
        pnlControles.add(btnRankingAutores);
        pnlControles.add(btnTemasPopulares);
        pnlControles.add(btnEstadisticas);
        
        // Área de resultados
        JTextArea txtResultados = new JTextArea(20, 60);
        txtResultados.setEditable(false);
        JScrollPane scrollResultados = new JScrollPane(txtResultados);
        
        panel.add(pnlControles, BorderLayout.NORTH);
        panel.add(scrollResultados, BorderLayout.CENTER);
        
        // Listeners
        btnReporteUsuario.addActionListener(e -> generarReporteUsuario(txtResultados));
        btnRankingAutores.addActionListener(e -> mostrarRankingAutores(txtResultados));
        btnTemasPopulares.addActionListener(e -> mostrarTemasPopulares(txtResultados));
        btnEstadisticas.addActionListener(e -> mostrarEstadisticas(txtResultados));
        
        return panel;
    }
    
    // ========== MÉTODOS DE FUNCIONALIDAD ==========
    
    private void cargarDatosIniciales() {
        
    }
    
    private void mostrarDialogoAumentarVisita() {
        JDialog dialog = new JDialog(this, "Aumentar Visita", true);
        dialog.setLayout(new GridLayout(3, 2, 10, 10));
        dialog.setSize(300, 150);
        
        JLabel lblId = new JLabel("ID de Noticia:");
        JTextField txtId = new JTextField();
        JButton btnAceptar = new JButton("Aumentar Visita");
        JButton btnCancelar = new JButton("Cancelar");
        
        dialog.add(lblId);
        dialog.add(txtId);
        dialog.add(btnAceptar);
        dialog.add(btnCancelar);
        
        btnAceptar.addActionListener(e -> {
            try {
                int idNoticia = Integer.parseInt(txtId.getText());
                
                try (CallableStatement cs = conn.prepareCall("{call PKG_NOTICIAS.AUMENTAR_VISITA(?)}")) {
                    cs.setInt(1, idNoticia);
                    cs.execute();
                    JOptionPane.showMessageDialog(dialog, 
                        "Visita aumentada exitosamente a la noticia " + idNoticia,
                        "Éxito", 
                        JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "ID debe ser un número válido", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Error: " + ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        btnCancelar.addActionListener(e -> dialog.dispose());
        
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void mostrarDialogoCalificar() {
        JDialog dialog = new JDialog(this, "Calificar Noticia", true);
        dialog.setLayout(new GridLayout(4, 2, 10, 10));
        dialog.setSize(300, 200);
        
        JLabel lblIdNoticia = new JLabel("ID Noticia:");
        JTextField txtIdNoticia = new JTextField();
        JLabel lblIdUsuario = new JLabel("ID Usuario:");
        JTextField txtIdUsuario = new JTextField();
        JLabel lblPuntaje = new JLabel("Puntaje (1-5):");
        JComboBox<Integer> cmbPuntaje = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
        
        JButton btnAceptar = new JButton("Calificar");
        JButton btnCancelar = new JButton("Cancelar");
        
        dialog.add(lblIdNoticia);
        dialog.add(txtIdNoticia);
        dialog.add(lblIdUsuario);
        dialog.add(txtIdUsuario);
        dialog.add(lblPuntaje);
        dialog.add(cmbPuntaje);
        dialog.add(btnAceptar);
        dialog.add(btnCancelar);
        
        btnAceptar.addActionListener(e -> {
            try {
                int idNoticia = Integer.parseInt(txtIdNoticia.getText());
                int idUsuario = Integer.parseInt(txtIdUsuario.getText());
                int puntaje = (int) cmbPuntaje.getSelectedItem();
                
                try (CallableStatement cs = conn.prepareCall("{call PKG_NOTICIAS.REGISTRAR_CALIFICACION(?, ?, ?)}")) {
                    cs.setInt(1, idNoticia);
                    cs.setInt(2, idUsuario);
                    cs.setInt(3, puntaje);
                    cs.execute();
                    
                    JOptionPane.showMessageDialog(dialog, 
                        "Calificación registrada exitosamente",
                        "Éxito", 
                        JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "IDs deben ser números válidos", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Error: " + ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        btnCancelar.addActionListener(e -> dialog.dispose());
        
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void listarUltimasNoticias() {
        try (CallableStatement cs = conn.prepareCall("{ ? = call PKG_NOTICIAS.LISTAR_ULTIMAS_NOTICIAS(?) }")) {
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setNull(2, Types.INTEGER);
            cs.execute();
            
            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                DefaultTableModel model = (DefaultTableModel) tablaNoticias.getModel();
                model.setRowCount(0);
                
                while (rs.next()) {
                    Object[] row = {
                        rs.getInt("ID_NOTICIA"),
                        rs.getString("TITULO"),
                        rs.getString("AUTOR"),
                        rs.getDate("FECHA_PUBLICACION")
                    };
                    model.addRow(row);
                }
                
                JOptionPane.showMessageDialog(this, 
                    "Se cargaron " + model.getRowCount() + " noticias",
                    "Información", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Error: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void listarTopNoticias(String tipo) {
        try (CallableStatement cs = conn.prepareCall("{ ? = call PKG_NOTICIAS.LISTAR_TOP_NOTICIAS(?, ?) }")) {
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setString(2, tipo);
            cs.setNull(3, Types.INTEGER);
            cs.execute();
            
            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                DefaultTableModel model = (DefaultTableModel) tablaNoticias.getModel();
                model.setRowCount(0);
                
                while (rs.next()) {
                    int id = rs.getInt("ID_NOTICIA");
                    String titulo = rs.getString("TITULO");

                    Integer visitas = null;
                    Integer envios = null;
                    Double promedio = null;

                    
                    if (tipo.equals("VISITAS")) {
                        visitas = rs.getInt("VISITAS");
                    } else if (tipo.equals("ENVIOS")) {
                        envios = rs.getInt("ENVIOS");
                    } else if (tipo.equals("CALIFICACION")) {
                        promedio = rs.getDouble("PROMEDIO_CALIFICACION");
                    }

                    Object[] row = {
                        id,
                        titulo,
                        null,   
                        null,   
                        visitas,
                        promedio,
                        envios
                    };

                    model.addRow(row);
                }
                
                JOptionPane.showMessageDialog(this, 
                    "Top noticias por " + tipo.toLowerCase() + " cargadas.",
                    "Información", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Error: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void cargarUsuarios(DefaultTableModel modelo) {
        try {
            modelo.setRowCount(0);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT ID_USUARIO, NOMBRE, EMAIL, FECHA_REGISTRO FROM USUARIO ORDER BY ID_USUARIO");
            
            while (rs.next()) {
                modelo.addRow(new Object[]{
                    rs.getInt("ID_USUARIO"),
                    rs.getString("NOMBRE"),
                    rs.getString("EMAIL"),
                    rs.getDate("FECHA_REGISTRO")
                });
            }
            
            rs.close();
            stmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    private void mostrarDetalleNoticia(JTable tabla, JTextArea txtDetalle) {
        int row = tabla.getSelectedRow();
        if (row >= 0) {
            int idNoticia = (int) tabla.getValueAt(row, 0);
            
            try {
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(
                    "SELECT N.*, T.NOMBRE AS TEMA, S.NOMBRE AS SUBTEMA " +
                    "FROM NOTICIA N " +
                    "JOIN TEMA T ON N.ID_TEMA = T.ID_TEMA " +
                    "LEFT JOIN SUBTEMA S ON N.ID_SUBTEMA = S.ID_SUBTEMA " +
                    "WHERE N.ID_NOTICIA = " + idNoticia
                );
                
                if (rs.next()) {
                    StringBuilder detalle = new StringBuilder();
                    detalle.append("ID: ").append(rs.getInt("ID_NOTICIA")).append("\n");
                    detalle.append("Título: ").append(rs.getString("TITULO")).append("\n");
                    detalle.append("Autor: ").append(rs.getString("AUTOR")).append("\n");
                    detalle.append("Fecha: ").append(rs.getDate("FECHA_PUBLICACION")).append("\n");
                    detalle.append("Tema: ").append(rs.getString("TEMA")).append("\n");
                    detalle.append("Subtema: ").append(rs.getString("SUBTEMA")).append("\n");
                    detalle.append("Visitas: ").append(rs.getInt("VISITAS")).append("\n");
                    detalle.append("Envíos: ").append(rs.getInt("ENVIOS")).append("\n");
                    detalle.append("Comentarios: ").append(rs.getInt("COMENTARIOS")).append("\n");
                    detalle.append("Calificación promedio: ").append(rs.getDouble("PROMEDIO_CALIFICACION")).append("\n");
                    
                    txtDetalle.setText(detalle.toString());
                }
                
                rs.close();
                stmt.close();
            } catch (SQLException ex) {
                txtDetalle.setText("Error al cargar detalles: " + ex.getMessage());
            }
        }
    }
    
    private void mostrarDialogoGestionUsuario(String accion) {
        JDialog dialog = new JDialog(this, "Gestión de Usuario", true);
        dialog.setLayout(new GridLayout(5, 2, 10, 10));
        dialog.setSize(400, 250);

        JLabel lblId = new JLabel("ID Usuario:");
        JTextField txtId = new JTextField();
        JLabel lblNombre = new JLabel("Nombre:");
        JTextField txtNombre = new JTextField();
        JLabel lblEmail = new JLabel("Email:");
        JTextField txtEmail = new JTextField();
        JLabel lblPassword = new JLabel("Password:");
        JPasswordField txtPassword = new JPasswordField();

        JButton btnAceptar = new JButton(
            accion.equals("INSERTAR") ? "Crear" :
            accion.equals("MODIFICAR") ? "Modificar" :
            "Eliminar"
        );
        JButton btnCancelar = new JButton("Cancelar");

        // OBTENEMOS TABLA DE USUARIOS (para modificar o eliminar)
        if (accion.equals("MODIFICAR")) {
            int row = tablaUsuarios.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Seleccione un usuario primero.");
                return;
            }

            txtId.setText(tablaUsuarios.getValueAt(row, 0).toString());
            txtId.setEditable(false);
            txtNombre.setText(tablaUsuarios.getValueAt(row, 1).toString());
            txtEmail.setText(tablaUsuarios.getValueAt(row, 2).toString());
        }

        if (accion.equals("INSERTAR")) {
            txtId.setEnabled(false);
        }

        if (accion.equals("ELIMINAR")) {
            int row = tablaUsuarios.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Seleccione un usuario para eliminar.");
                return;
            }

            txtId.setText(tablaUsuarios.getValueAt(row, 0).toString());
            txtId.setEditable(false);

            
            dialog.getContentPane().removeAll();
            dialog.setLayout(new GridLayout(3, 2, 10, 10));
            dialog.setSize(300,150);

            dialog.add(lblId);
            dialog.add(txtId);
            dialog.add(btnAceptar);
            dialog.add(btnCancelar);
        } else {
           
            dialog.add(lblId); dialog.add(txtId);
            dialog.add(lblNombre); dialog.add(txtNombre);
            dialog.add(lblEmail); dialog.add(txtEmail);
            dialog.add(lblPassword); dialog.add(txtPassword);
            dialog.add(btnAceptar); dialog.add(btnCancelar);
        }

        btnAceptar.addActionListener(e -> {
            try {
                try (CallableStatement cs = conn.prepareCall("{call PKG_USUARIOS.GESTIONAR_USUARIO(?, ?, ?, ?, ?)}")) {
                    cs.setString(1, accion);

                    if (!accion.equals("INSERTAR"))
                        cs.setInt(2, Integer.parseInt(txtId.getText()));
                    else
                        cs.setNull(2, Types.INTEGER);

                    cs.setString(3, txtNombre.getText().isEmpty() ? null : txtNombre.getText());
                    cs.setString(4, txtEmail.getText().isEmpty() ? null : txtEmail.getText());

                    String pass = new String(txtPassword.getPassword());
                    cs.setString(5, pass.isEmpty() ? null : pass);

                    cs.execute();

                    JOptionPane.showMessageDialog(dialog, "Operación exitosa.");
                    dialog.dispose();

                    
                    DefaultTableModel model = (DefaultTableModel) tablaUsuarios.getModel();
                    cargarUsuarios(model);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancelar.addActionListener(e -> dialog.dispose());

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void mostrarDialogoPreferencias() {
        JDialog dialog = new JDialog(this, "Preferencias de Envío", true);
        dialog.setLayout(new GridLayout(5, 2, 10, 10));
        dialog.setSize(350, 250);
        
        JLabel lblUsuario = new JLabel("ID Usuario:");
        JTextField txtUsuario = new JTextField();
        JLabel lblTema = new JLabel("ID Tema:");
        JTextField txtTema = new JTextField();
        JLabel lblSubtema = new JLabel("ID Subtema:");
        JTextField txtSubtema = new JTextField();
        JLabel lblAutor = new JLabel("Autor:");
        JTextField txtAutor = new JTextField();
        
        JButton btnAceptar = new JButton("Registrar");
        JButton btnCancelar = new JButton("Cancelar");
        
        dialog.add(lblUsuario);
        dialog.add(txtUsuario);
        dialog.add(lblTema);
        dialog.add(txtTema);
        dialog.add(lblSubtema);
        dialog.add(txtSubtema);
        dialog.add(lblAutor);
        dialog.add(txtAutor);
        dialog.add(btnAceptar);
        dialog.add(btnCancelar);
        
        btnAceptar.addActionListener(e -> {
            try {
                int idUsuario = Integer.parseInt(txtUsuario.getText());
                Integer idTema = txtTema.getText().isEmpty() ? null : Integer.parseInt(txtTema.getText());
                Integer idSubtema = txtSubtema.getText().isEmpty() ? null : Integer.parseInt(txtSubtema.getText());
                String autor = txtAutor.getText().isEmpty() ? null : txtAutor.getText();
                
                try (CallableStatement cs = conn.prepareCall("{call PKG_USUARIOS.REGISTRAR_PREFERENCIA_ENVIO(?, ?, ?, ?)}")) {
                    cs.setInt(1, idUsuario);
                    if (idTema == null) cs.setNull(2, Types.INTEGER);
                    else cs.setInt(2, idTema);
                    
                    if (idSubtema == null) cs.setNull(3, Types.INTEGER);
                    else cs.setInt(3, idSubtema);
                    
                    if (autor == null) cs.setNull(4, Types.VARCHAR);
                    else cs.setString(4, autor);
                    
                    cs.execute();
                    
                    JOptionPane.showMessageDialog(dialog, 
                        "Preferencia registrada exitosamente",
                        "Éxito", 
                        JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "IDs deben ser números válidos", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Error: " + ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        btnCancelar.addActionListener(e -> dialog.dispose());
        
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void cargarTemas(DefaultTableModel modelo) {
        try {
            modelo.setRowCount(0);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT ID_TEMA, NOMBRE FROM TEMA ORDER BY ID_TEMA");
            
            while (rs.next()) {
                modelo.addRow(new Object[]{
                    rs.getInt("ID_TEMA"),
                    rs.getString("NOMBRE")
                });
            }
            
            rs.close();
            stmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    private void cargarSubtemas(DefaultTableModel modelo) {
        try {
            modelo.setRowCount(0);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(
                "SELECT S.ID_SUBTEMA, T.NOMBRE AS TEMA, S.NOMBRE " +
                "FROM SUBTEMA S " +
                "JOIN TEMA T ON S.ID_TEMA = T.ID_TEMA " +
                "ORDER BY S.ID_SUBTEMA"
            );
            
            while (rs.next()) {
                modelo.addRow(new Object[]{
                    rs.getInt("ID_SUBTEMA"),
                    rs.getString("TEMA"),
                    rs.getString("NOMBRE")
                });
            }
            
            rs.close();
            stmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    private void mostrarDialogoTema(String accion) {
        JDialog dialog = new JDialog(this, "Gestión de Tema", true);
        
        if (accion.equals("CREAR")) {
            dialog.setLayout(new GridLayout(3, 2, 10, 10));
            dialog.setSize(300, 150);
            
            JLabel lblNombre = new JLabel("Nombre:");
            JTextField txtNombre = new JTextField();
            JButton btnAceptar = new JButton("Crear");
            JButton btnCancelar = new JButton("Cancelar");
            
            dialog.add(lblNombre);
            dialog.add(txtNombre);
            dialog.add(btnAceptar);
            dialog.add(btnCancelar);
            
            btnAceptar.addActionListener(e -> {
                try {
                    String nombre = txtNombre.getText().trim();
                    if (nombre.isEmpty()) {
                        JOptionPane.showMessageDialog(dialog, 
                            "El nombre no puede estar vacío", 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    try (CallableStatement cs = conn.prepareCall("{call PKG_TEMAS.CREAR_TEMA(?)}")) {
                        cs.setString(1, nombre);
                        cs.execute();
                        
                        JOptionPane.showMessageDialog(dialog, 
                            "Tema creado exitosamente: " + nombre,
                            "Éxito", 
                            JOptionPane.INFORMATION_MESSAGE);
                        
                        // Refresca la tabla de temas
                        DefaultTableModel model = (DefaultTableModel) tablaTemas.getModel();
                        cargarTemas(model);
                        
                        dialog.dispose();
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Error: " + ex.getMessage(), 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            });
            
            btnCancelar.addActionListener(e -> dialog.dispose());
            
        } else if (accion.equals("MODIFICAR")) {
            dialog.setLayout(new GridLayout(4, 2, 10, 10));
            dialog.setSize(300, 200);
            
            JLabel lblId = new JLabel("ID Tema:");
            JTextField txtId = new JTextField();
            JLabel lblNombre = new JLabel("Nombre:");
            JTextField txtNombre = new JTextField();
            JButton btnAceptar = new JButton("Modificar");
            JButton btnCancelar = new JButton("Cancelar");
            
            dialog.add(lblId);
            dialog.add(txtId);
            dialog.add(lblNombre);
            dialog.add(txtNombre);
            dialog.add(btnAceptar);
            dialog.add(btnCancelar);
            
            // Precargar datos si hay una fila seleccionada
            int selectedRow = tablaTemas.getSelectedRow();
            if (selectedRow != -1) {
                txtId.setText(tablaTemas.getValueAt(selectedRow, 0).toString());
                txtNombre.setText(tablaTemas.getValueAt(selectedRow, 1).toString());
            }
            
            btnAceptar.addActionListener(e -> {
                try {
                    int idTema = Integer.parseInt(txtId.getText());
                    String nombre = txtNombre.getText().trim();
                    
                    if (nombre.isEmpty()) {
                        JOptionPane.showMessageDialog(dialog, 
                            "El nombre no puede estar vacío", 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    try (CallableStatement cs = conn.prepareCall("{call PKG_TEMAS.MODIFICAR_TEMA(?, ?)}")) {
                        cs.setInt(1, idTema);
                        cs.setString(2, nombre);
                        cs.execute();
                        
                        JOptionPane.showMessageDialog(dialog, 
                            "Tema modificado exitosamente",
                            "Éxito", 
                            JOptionPane.INFORMATION_MESSAGE);
                        
                        // Refresca la tabla de temas
                        DefaultTableModel model = (DefaultTableModel) tablaTemas.getModel();
                        cargarTemas(model);
                        
                        dialog.dispose();
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, 
                        "ID debe ser un número válido", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Error: " + ex.getMessage(), 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            });
            
            btnCancelar.addActionListener(e -> dialog.dispose());
            
        } else if (accion.equals("ELIMINAR")) {
            dialog.setLayout(new GridLayout(3, 2, 10, 10));
            dialog.setSize(300, 150);
            
            JLabel lblId = new JLabel("ID Tema:");
            JTextField txtId = new JTextField();
            JButton btnAceptar = new JButton("Eliminar");
            JButton btnCancelar = new JButton("Cancelar");
            
            dialog.add(lblId);
            dialog.add(txtId);
            dialog.add(btnAceptar);
            dialog.add(btnCancelar);
            
            // Precarga datos si hay una fila seleccionada
            int selectedRow = tablaTemas.getSelectedRow();
            if (selectedRow != -1) {
                txtId.setText(tablaTemas.getValueAt(selectedRow, 0).toString());
            }
            
            btnAceptar.addActionListener(e -> {
                try {
                    int idTema = Integer.parseInt(txtId.getText());
                    
                    int confirm = JOptionPane.showConfirmDialog(dialog, 
                        "¿Está seguro de eliminar el tema ID " + idTema + "?", 
                        "Confirmar eliminación", 
                        JOptionPane.YES_NO_OPTION);
                    
                    if (confirm == JOptionPane.YES_OPTION) {
                        try (CallableStatement cs = conn.prepareCall("{call PKG_TEMAS.ELIMINAR_TEMA(?)}")) {
                            cs.setInt(1, idTema);
                            cs.execute();
                            
                            JOptionPane.showMessageDialog(dialog, 
                                "Tema eliminado exitosamente",
                                "Éxito", 
                                JOptionPane.INFORMATION_MESSAGE);
                            
                            // Refresca la tabla de temas
                            DefaultTableModel model = (DefaultTableModel) tablaTemas.getModel();
                            cargarTemas(model);
                            
                            dialog.dispose();
                        }
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, 
                        "ID debe ser un número válido", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Error: " + ex.getMessage(), 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            });
            
            btnCancelar.addActionListener(e -> dialog.dispose());
        }
        
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void generarReporteUsuario(JTextArea txtResultados) {
        String idStr = JOptionPane.showInputDialog(this, "Ingrese ID de usuario:");
        if (idStr == null || idStr.trim().isEmpty()) return;
        
        try {
            int idUsuario = Integer.parseInt(idStr);
            
            try (CallableStatement cs = conn.prepareCall("{ ? = call PKG_REPORTES.GENERAR_REPORTE_USUARIO(?) }")) {
                cs.registerOutParameter(1, OracleTypes.CURSOR);
                cs.setInt(2, idUsuario);
                cs.execute();
                
                try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                    StringBuilder resultado = new StringBuilder();
                    resultado.append("=== REPORTE DE USUARIO ID: ").append(idUsuario).append(" ===\n\n");
                    
                    int contador = 0;
                    while (rs.next()) {
                        contador++;
                        resultado.append("Noticia #").append(contador).append(":\n");
                        resultado.append("  ID: ").append(rs.getInt("ID_NOTICIA")).append("\n");
                        resultado.append("  Título: ").append(rs.getString("TITULO")).append("\n");
                        resultado.append("  Autor: ").append(rs.getString("AUTOR")).append("\n");
                        resultado.append("  Fecha: ").append(rs.getDate("FECHA_PUBLICACION")).append("\n");
                        resultado.append("  Contenido: ").append(rs.getString("CONTENIDO")).append("\n");
                        resultado.append("-".repeat(50)).append("\n");
                    }
                    
                    if (contador == 0) {
                        resultado.append("El usuario no ha interactuado con ninguna noticia.\n");
                    } else {
                        resultado.append("Total de noticias: ").append(contador).append("\n");
                    }
                    
                    txtResultados.setText(resultado.toString());
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, 
                "ID debe ser un número válido", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Error: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void mostrarRankingAutores(JTextArea txtResultados) {
        try {
            try (CallableStatement cs = conn.prepareCall("{ ? = call PKG_REPORTES.RANKING_AUTORES() }")) {
                cs.registerOutParameter(1, OracleTypes.CURSOR);
                cs.execute();
                
                try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                    StringBuilder resultado = new StringBuilder();
                    resultado.append("=== RANKING DE AUTORES ===\n\n");
                    
                    int posicion = 1;
                    while (rs.next()) {
                        resultado.append(posicion).append(". ").append(rs.getString("AUTOR")).append("\n");
                        resultado.append("   Visitas totales: ").append(rs.getInt("TOTAL_VISITAS")).append("\n");
                        resultado.append("   Envíos totales: ").append(rs.getInt("TOTAL_ENVIOS")).append("\n");
                        resultado.append("   Calificación promedio: ").append(String.format("%.2f", rs.getDouble("PROMEDIO_CALIFICACION"))).append("\n");
                        resultado.append("-".repeat(40)).append("\n");
                        posicion++;
                    }
                    
                    txtResultados.setText(resultado.toString());
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Error: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void mostrarDialogoComentar() {
        JDialog dialog = new JDialog(this, "Agregar Comentario", true);
        dialog.setLayout(new GridLayout(4, 2, 10, 10));
        dialog.setSize(400, 220);

        JLabel lblIdUsuario = new JLabel("ID Usuario:");
        JTextField txtIdUsuario = new JTextField();

        JLabel lblIdNoticia = new JLabel("ID Noticia:");
        JTextField txtIdNoticia = new JTextField();

        JLabel lblComentario = new JLabel("Comentario:");
        JTextField txtComentario = new JTextField();

        JButton btnAceptar = new JButton("Registrar");
        JButton btnCancelar = new JButton("Cancelar");

        dialog.add(lblIdUsuario);
        dialog.add(txtIdUsuario);
        dialog.add(lblIdNoticia);
        dialog.add(txtIdNoticia);
        dialog.add(lblComentario);
        dialog.add(txtComentario);
        dialog.add(btnAceptar);
        dialog.add(btnCancelar);

        btnAceptar.addActionListener(e -> {
            try {
                int idUsuario = Integer.parseInt(txtIdUsuario.getText().trim());
                int idNoticia = Integer.parseInt(txtIdNoticia.getText().trim());
                String comentario = txtComentario.getText().trim();

                if (comentario.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog,
                        "El comentario no puede estar vacío.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try (CallableStatement cs = conn.prepareCall("{call PKG_NOTICIAS.REGISTRAR_COMENTARIO(?, ?, ?)}")) {
                    cs.setInt(1, idUsuario);
                    cs.setInt(2, idNoticia);
                    cs.setString(3, comentario);

                    cs.execute();

                    JOptionPane.showMessageDialog(dialog,
                        "Comentario registrado exitosamente.",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);

                    dialog.dispose();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Los IDs deben ser números válidos.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Error: " + ex.getMessage(),
                    "Error SQL", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancelar.addActionListener(e -> dialog.dispose());

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void mostrarDialogoPublicar() {
        JDialog dialog = new JDialog(this, "Publicar Noticia", true);
        dialog.setLayout(new GridLayout(7, 2, 10, 10));
        dialog.setSize(400, 300);
        
        JLabel lblTema = new JLabel("ID Tema:");
        JTextField txtIdTema = new JTextField();
        
        JLabel lblSubtema = new JLabel("ID Subtema (opcional):");
        JTextField txtIdSubtema = new JTextField();
        
        JLabel lblTitulo = new JLabel("Título:");
        JTextField txtTitulo = new JTextField();
        
        JLabel lblAutor = new JLabel("Autor:");
        JTextField txtAutor = new JTextField();
        
        JLabel lblContenido = new JLabel("Contenido:");
        JTextArea txtContenido = new JTextArea(4, 20);
        JScrollPane scrollContenido = new JScrollPane(txtContenido);
        
        JButton btnAceptar = new JButton("Publicar");
        JButton btnCancelar = new JButton("Cancelar");
        
        dialog.add(lblTema);
        dialog.add(txtIdTema);
        dialog.add(lblSubtema);
        dialog.add(txtIdSubtema);
        dialog.add(lblTitulo);
        dialog.add(txtTitulo);
        dialog.add(lblAutor);
        dialog.add(txtAutor);
        dialog.add(lblContenido);
        dialog.add(scrollContenido);
        dialog.add(btnAceptar);
        dialog.add(btnCancelar);
        
        btnAceptar.addActionListener(e -> {
            try {
                int idTema = Integer.parseInt(txtIdTema.getText());
                String titulo = txtTitulo.getText().trim();
                String autor = txtAutor.getText().trim();
                String contenido = txtContenido.getText().trim();
                
                if (titulo.isEmpty() || autor.isEmpty() || contenido.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Título, autor y contenido son obligatorios", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Verificar si hay subtema
                String subtemaText = txtIdSubtema.getText().trim();
                if (subtemaText.isEmpty()) {
                    
                    try (CallableStatement cs = conn.prepareCall("{call PKG_NOTICIAS.PUBLICAR_NOTICIA(?, null, ?, ?, ?)}")) {
                        cs.setInt(1, idTema);
                        cs.setString(2, titulo);
                        cs.setString(3, contenido);
                        cs.setString(4, autor);
                        cs.execute();
                        
                        JOptionPane.showMessageDialog(dialog, 
                            "Noticia publicada exitosamente",
                            "Éxito", 
                            JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();
                    }
                } else {
                    
                    int idSubtema = Integer.parseInt(subtemaText);
                    try (CallableStatement cs = conn.prepareCall("{call PKG_NOTICIAS.PUBLICAR_NOTICIA(?, ?, ?, ?, ?)}")) {
                        cs.setInt(1, idTema);
                        cs.setInt(2, idSubtema);
                        cs.setString(3, titulo);
                        cs.setString(4, contenido);
                        cs.setString(5, autor);
                        cs.execute();
                        
                        JOptionPane.showMessageDialog(dialog, 
                            "Noticia publicada exitosamente",
                            "Éxito", 
                            JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();
                    }
                }
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "ID de tema y subtema deben ser números válidos", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Error: " + ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        btnCancelar.addActionListener(e -> dialog.dispose());
        
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void mostrarDialogoSubtema(String accion) {
        JDialog dialog = new JDialog(this, "Gestión de Subtema", true);
        
        // Verificar que haya un tema seleccionado (para CREAR)
        int selectedTemaRow = tablaTemas.getSelectedRow();
        if (selectedTemaRow == -1 && accion.equals("CREAR")) {
            JOptionPane.showMessageDialog(this, 
                "Seleccione un tema primero", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        
        final int idTemaSeleccionado;
        if (selectedTemaRow != -1) {
            idTemaSeleccionado = Integer.parseInt(tablaTemas.getValueAt(selectedTemaRow, 0).toString());
        } else {
            idTemaSeleccionado = -1;
        }
        
        if (accion.equals("CREAR")) {
            dialog.setLayout(new GridLayout(3, 2, 10, 10));
            dialog.setSize(350, 180);
            
            JLabel lblTema = new JLabel("Tema ID:");
            JTextField txtIdTema = new JTextField(String.valueOf(idTemaSeleccionado));
            txtIdTema.setEditable(false);
            
            JLabel lblNombre = new JLabel("Nombre Subtema:");
            JTextField txtNombre = new JTextField();
            JButton btnAceptar = new JButton("Crear");
            JButton btnCancelar = new JButton("Cancelar");
            
            dialog.add(lblTema);
            dialog.add(txtIdTema);
            dialog.add(lblNombre);
            dialog.add(txtNombre);
            dialog.add(btnAceptar);
            dialog.add(btnCancelar);
            
            btnAceptar.addActionListener(e -> {
                try {
                    String nombre = txtNombre.getText().trim();
                    if (nombre.isEmpty()) {
                        JOptionPane.showMessageDialog(dialog, 
                            "El nombre no puede estar vacío", 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    try (CallableStatement cs = conn.prepareCall("{call PKG_TEMAS.CREAR_SUBTEMA(?, ?)}")) {
                        cs.setInt(1, idTemaSeleccionado);
                        cs.setString(2, nombre);
                        cs.execute();
                        
                        JOptionPane.showMessageDialog(dialog, 
                            "Subtema creado exitosamente",
                            "Éxito", 
                            JOptionPane.INFORMATION_MESSAGE);
                        
                        // Refresca tabla de subtemas
                        cargarSubtemas((DefaultTableModel) tablaSubtemas.getModel());
                        
                        dialog.dispose();
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Error: " + ex.getMessage(), 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            });
            
            btnCancelar.addActionListener(e -> dialog.dispose());
            
        } else if (accion.equals("MODIFICAR")) {
            // Verifica que haya un subtema seleccionado
            int selectedSubtemaRow = tablaSubtemas.getSelectedRow();
            if (selectedSubtemaRow == -1) {
                JOptionPane.showMessageDialog(this, 
                    "Seleccione un subtema primero", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            dialog.setLayout(new GridLayout(4, 2, 10, 10));
            dialog.setSize(350, 200);
            
            // Crea variables finales para lambda
            final String subtemaIdText = tablaSubtemas.getValueAt(selectedSubtemaRow, 0).toString();
            final String subtemaNombreText = tablaSubtemas.getValueAt(selectedSubtemaRow, 2).toString();
            
            JLabel lblIdSubtema = new JLabel("ID Subtema:");
            JTextField txtIdSubtema = new JTextField(subtemaIdText);
            txtIdSubtema.setEditable(false);
            
            JLabel lblNombre = new JLabel("Nuevo Nombre:");
            JTextField txtNombre = new JTextField(subtemaNombreText);
            JButton btnAceptar = new JButton("Modificar");
            JButton btnCancelar = new JButton("Cancelar");
            
            dialog.add(lblIdSubtema);
            dialog.add(txtIdSubtema);
            dialog.add(lblNombre);
            dialog.add(txtNombre);
            dialog.add(btnAceptar);
            dialog.add(btnCancelar);
            
            btnAceptar.addActionListener(e -> {
                try {
                    int idSubtema = Integer.parseInt(txtIdSubtema.getText());
                    String nombre = txtNombre.getText().trim();
                    
                    if (nombre.isEmpty()) {
                        JOptionPane.showMessageDialog(dialog, 
                            "El nombre no puede estar vacío", 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    try (CallableStatement cs = conn.prepareCall("{call PKG_TEMAS.MODIFICAR_SUBTEMA(?, ?)}")) {
                        cs.setInt(1, idSubtema);
                        cs.setString(2, nombre);
                        cs.execute();
                        
                        JOptionPane.showMessageDialog(dialog, 
                            "Subtema modificado exitosamente",
                            "Éxito", 
                            JOptionPane.INFORMATION_MESSAGE);
                        
                        cargarSubtemas((DefaultTableModel) tablaSubtemas.getModel());
                        dialog.dispose();
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, 
                        "ID inválido", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Error: " + ex.getMessage(), 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            });
            
            btnCancelar.addActionListener(e -> dialog.dispose());
            
        } else if (accion.equals("ELIMINAR")) {
            int selectedSubtemaRow = tablaSubtemas.getSelectedRow();
            if (selectedSubtemaRow == -1) {
                JOptionPane.showMessageDialog(this, 
                    "Seleccione un subtema primero", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            dialog.setLayout(new GridLayout(3, 2, 10, 10));
            dialog.setSize(350, 150);
            
            
            final String subtemaIdText = tablaSubtemas.getValueAt(selectedSubtemaRow, 0).toString();
            
            JLabel lblIdSubtema = new JLabel("ID Subtema:");
            JTextField txtIdSubtema = new JTextField(subtemaIdText);
            txtIdSubtema.setEditable(false);
            JButton btnAceptar = new JButton("Eliminar");
            JButton btnCancelar = new JButton("Cancelar");
            
            dialog.add(lblIdSubtema);
            dialog.add(txtIdSubtema);
            dialog.add(btnAceptar);
            dialog.add(btnCancelar);
            
            btnAceptar.addActionListener(e -> {
                try {
                    int idSubtema = Integer.parseInt(txtIdSubtema.getText());
                    
                    int confirm = JOptionPane.showConfirmDialog(dialog, 
                        "¿Eliminar subtema ID " + idSubtema + "?", 
                        "Confirmar", 
                        JOptionPane.YES_NO_OPTION);
                    
                    if (confirm == JOptionPane.YES_OPTION) {
                        try (CallableStatement cs = conn.prepareCall("{call PKG_TEMAS.ELIMINAR_SUBTEMA(?)}")) {
                            cs.setInt(1, idSubtema);
                            cs.execute();
                            
                            JOptionPane.showMessageDialog(dialog, 
                                "Subtema eliminado",
                                "Éxito", 
                                JOptionPane.INFORMATION_MESSAGE);
                            
                            cargarSubtemas((DefaultTableModel) tablaSubtemas.getModel());
                            dialog.dispose();
                        }
                    }
                } catch (NumberFormatException | SQLException ex) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Error: " + ex.getMessage(), 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            });
            
            btnCancelar.addActionListener(e -> dialog.dispose());
        }
        
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void mostrarTemasPopulares(JTextArea txtResultados) {
        try {
            try (CallableStatement cs = conn.prepareCall("{ ? = call PKG_TEMAS.TEMAS_MAS_POPULARES() }")) {
                cs.registerOutParameter(1, OracleTypes.CURSOR);
                cs.execute();
                
                try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                    StringBuilder resultado = new StringBuilder();
                    resultado.append("=== TEMAS MÁS POPULARES ===\n\n");
                    
                    int posicion = 1;
                    while (rs.next()) {
                        resultado.append(posicion).append(". ").append(rs.getString("NOMBRE")).append("\n");
                        resultado.append("   Total noticias: ").append(rs.getInt("TOTAL_NOTICIAS")).append("\n");
                        resultado.append("   Total visitas: ").append(rs.getInt("TOTAL_VISITAS")).append("\n");
                        resultado.append("   Total envíos: ").append(rs.getInt("TOTAL_ENVIOS")).append("\n");
                        resultado.append("   Calificación promedio: ").append(String.format("%.2f", rs.getDouble("PROMEDIO_CALIFICACION"))).append("\n");
                        resultado.append("-".repeat(40)).append("\n");
                        posicion++;
                    }
                    
                    txtResultados.setText(resultado.toString());
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Error: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void mostrarEstadisticas(JTextArea txtResultados) {
        try {
            StringBuilder resultado = new StringBuilder();
            resultado.append("=== ESTADÍSTICAS DEL SISTEMA ===\n\n");
            
            Statement stmt = conn.createStatement();
            
            // Total de usuarios
            ResultSet rs1 = stmt.executeQuery("SELECT COUNT(*) FROM USUARIO");
            if (rs1.next()) {
                resultado.append("Total de usuarios: ").append(rs1.getInt(1)).append("\n");
            }
            
            // Total de noticias
            ResultSet rs2 = stmt.executeQuery("SELECT COUNT(*) FROM NOTICIA");
            if (rs2.next()) {
                resultado.append("Total de noticias: ").append(rs2.getInt(1)).append("\n");
            }
            
            // Total de temas
            ResultSet rs3 = stmt.executeQuery("SELECT COUNT(*) FROM TEMA");
            if (rs3.next()) {
                resultado.append("Total de temas: ").append(rs3.getInt(1)).append("\n");
            }
            
            // Total de subtemas
            ResultSet rs4 = stmt.executeQuery("SELECT COUNT(*) FROM SUBTEMA");
            if (rs4.next()) {
                resultado.append("Total de subtemas: ").append(rs4.getInt(1)).append("\n");
            }
            
            // Noticia más visitada
            ResultSet rs5 = stmt.executeQuery(
                "SELECT TITULO, VISITAS FROM NOTICIA WHERE VISITAS = (SELECT MAX(VISITAS) FROM NOTICIA) AND ROWNUM = 1"
            );
            if (rs5.next()) {
                resultado.append("Noticia más visitada: ").append(rs5.getString("TITULO")).append(" (").append(rs5.getInt("VISITAS")).append(" visitas)\n");
            }
            
            // Noticia mejor calificada
            ResultSet rs6 = stmt.executeQuery(
                "SELECT TITULO, PROMEDIO_CALIFICACION FROM NOTICIA WHERE PROMEDIO_CALIFICACION = (SELECT MAX(PROMEDIO_CALIFICACION) FROM NOTICIA) AND ROWNUM = 1"
            );
            if (rs6.next()) {
                resultado.append("Noticia mejor calificada: ").append(rs6.getString("TITULO")).append(" (").append(String.format("%.2f", rs6.getDouble("PROMEDIO_CALIFICACION"))).append(")\n");
            }
            
            txtResultados.setText(resultado.toString());
            
            rs1.close();
            rs2.close();
            rs3.close();
            rs4.close();
            rs5.close();
            rs6.close();
            stmt.close();
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Error: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Método adicional para cargar subtemas por tema
    private void cargarSubtemasPorTema(int idTema) {
        DefaultTableModel model = (DefaultTableModel) tablaSubtemas.getModel();
        model.setRowCount(0); 
        
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT S.ID_SUBTEMA, T.NOMBRE AS TEMA, S.NOMBRE " +
                "FROM SUBTEMA S " +
                "JOIN TEMA T ON S.ID_TEMA = T.ID_TEMA " +
                "WHERE S.ID_TEMA = ? " +
                "ORDER BY S.ID_SUBTEMA")) {
            ps.setInt(1, idTema);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("ID_SUBTEMA"),
                    rs.getString("TEMA"),
                    rs.getString("NOMBRE")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Método main
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            InterfazNoticias interfaz = new InterfazNoticias();
            interfaz.setVisible(true);
        });
    }
}