import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import models.User;
import screens.WelcomeScreen;
import services.*;

public class Main {
    public static void main(String[] args) {
        // Initialize services
        NotificationService notificationService = new NotificationService();

        // Start the GUI
        SwingUtilities.invokeLater(() -> {
            JFrame mainFrame = new JFrame("Wellness App");
            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            mainFrame.setSize(800, 600);
            mainFrame.setLocationRelativeTo(null);
            mainFrame.setContentPane(new WelcomeScreen());
            mainFrame.setVisible(true);
        });
    }
}