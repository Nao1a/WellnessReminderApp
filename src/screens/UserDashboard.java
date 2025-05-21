package screens;

import java.awt.*;
import javax.swing.*;
import models.User;

public class UserDashboard extends JPanel { // Changed from JFrame
    private User loggedInUser;
    // private JFrame parentFrame; // No longer needed if we get the main frame dynamically

    public UserDashboard(User user) { // Constructor now only takes User
        this.loggedInUser = user;
        // JFrame specific setup (setTitle, setSize, setDefaultCloseOperation) removed
        showDashboard();
    }

    private void showDashboard() {
        setLayout(new GridLayout(8, 1, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add some padding

        // Use the loggedInUser's actual username
        String username = (loggedInUser != null && loggedInUser.getUsername() != null && !loggedInUser.getUsername().isEmpty()) 
                        ? loggedInUser.getUsername() 
                        : "User"; // Fallback if username is not set
        // Note: Your User.java has 'public static String username', which is problematic.
        // It should be an instance field 'private String username' with a getter.
        // The code above tries to use the instance's username.

        JLabel title = new JLabel("Welcome, " + username + "!", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        add(title);

        add(createReminderButton("💊 Medication Reminder"));
        add(createReminderButton("🚰 Hydration Reminder"));
        add(createReminderButton("🏃 Movement Reminder"));
        add(createReminderButton("😴 Sleep Reminder"));
        add(createReminderButton("👀 Eye Strain Break"));
        add(createReminderButton("🍽️ Meal Reminder"));

        JButton viewLogBtn = new JButton("📜 View Reminder Log");
        viewLogBtn.setFont(new Font("Arial", Font.PLAIN, 18));
        viewLogBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "View Reminder Log functionality coming soon!");
        });
        add(viewLogBtn);
    }

    private JButton createReminderButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.PLAIN, 18));
        btn.addActionListener(e -> {
            JFrame mainFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (mainFrame == null) {
                System.err.println("Error: Could not find the main application window.");
                return;
            }

            String type = text.substring(text.indexOf(" ") + 1).replace(" Reminder", "").replace(" Break", "");
            if (text.contains("Eye Strain Break")) {
                type = "Eye Strain";
            }


            ReminderSetupScreen setupScreen = new ReminderSetupScreen(type, () -> {
                // This is the goBackCallback
                mainFrame.setContentPane(this); // 'this' is the UserDashboard instance
                mainFrame.setTitle("Wellness App - User Dashboard");
                mainFrame.revalidate();
                mainFrame.repaint();
            });
            mainFrame.setContentPane(setupScreen);
            mainFrame.setTitle("Wellness App - Setup " + type + " Reminder");
            mainFrame.revalidate();
            mainFrame.repaint();
        });
        return btn;
    }
}