
package proyecto;

import javax.swing.SwingUtilities;

public class AppNoticias {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                
                javax.swing.UIManager.setLookAndFeel(
                    javax.swing.UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            InterfazNoticias interfaz = new InterfazNoticias();
            interfaz.setVisible(true);
        });
    }
}

