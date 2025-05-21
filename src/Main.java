import screens.WelcomeScreen; // Changed from LoginScreen
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.Dimension;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame mainFrame = new JFrame("Wellness Reminder App");
            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            WelcomeScreen welcomeScreen = new WelcomeScreen();
            mainFrame.setContentPane(welcomeScreen);
            
            mainFrame.setMinimumSize(new Dimension(400, 300)); // Set a minimum size
            mainFrame.pack(); // Adjusts frame to preferred size of WelcomeScreen
            mainFrame.setLocationRelativeTo(null); // Center the frame
            mainFrame.setVisible(true);
        });
    }
}