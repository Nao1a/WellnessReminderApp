import screens.WelcomeScreen; // Changed from LoginScreen
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.Dimension;
import services.*;
import models.User;

public class Main {
    public static void main(String[] args) {
        // Initialize services
        User currentUser = new User("naol", "pass231", "USER");
        ReminderService reminderService = new ReminderService();
        NotificationService notificationService = new NotificationService();
        ReminderChecker checker = new ReminderChecker(reminderService, notificationService);

        // Start the reminder checker
        checker.startChecking();

        // Start the GUI
        SwingUtilities.invokeLater(() -> {
            JFrame mainFrame = new JFrame("Wellness Reminder App");
            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            mainFrame.setSize(new Dimension(800, 600));
            mainFrame.setLocationRelativeTo(null);
            mainFrame.setContentPane(new WelcomeScreen());
            mainFrame.setVisible(true);
        });
    }
}