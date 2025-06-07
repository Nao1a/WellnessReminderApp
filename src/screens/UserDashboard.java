package screens;

import java.awt.*;
import java.io.*;
import javax.swing.*;
import models.User;
import services.ReminderManager;

public class UserDashboard extends JPanel { // Changed from JFrame
    private User loggedInUser;
    private ReminderManager reminderManager;
    // private JFrame parentFrame; // No longer needed if we get the main frame dynamically

    public UserDashboard(User user) { // Constructor now only takes User
        this.loggedInUser = user;
        this.reminderManager = new ReminderManager(user);
        // JFrame specific setup (setTitle, setSize, setDefaultCloseOperation) removed
        showDashboard();
    }

    private void showDashboard() {
        setLayout(new GridLayout(9, 1, 10, 10)); // Increased rows to accommodate the logout button
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String username = (loggedInUser != null && loggedInUser.getUsername() != null && !loggedInUser.getUsername().isEmpty()) 
                        ? loggedInUser.getUsername() 
                        : "User";

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
            JFrame mainFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (mainFrame == null) {
                System.err.println("Error: Could not find the main application window.");
                return;
            }

            ViewReminderLogScreen logScreen = new ViewReminderLogScreen(loggedInUser, () -> {
                mainFrame.setContentPane(this); // Return to UserDashboard
                mainFrame.setTitle("Wellness App - User Dashboard");
                mainFrame.revalidate();
                mainFrame.repaint();
            });

            mainFrame.setContentPane(logScreen);
            mainFrame.setTitle("Wellness App - View Reminder Log");
            mainFrame.revalidate();
            mainFrame.repaint();
        });
        add(viewLogBtn);

        JButton viewRecommendationsBtn = new JButton("📋 View Recommendations");
        viewRecommendationsBtn.setFont(new Font("Arial", Font.PLAIN, 18));
        viewRecommendationsBtn.addActionListener(e -> viewRecommendations());
        add(viewRecommendationsBtn);

        // Add Logout Button
        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.PLAIN, 18));
        logoutButton.addActionListener(e -> {
            JFrame mainFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (mainFrame != null) {
                LoginScreen loginScreen = new LoginScreen();
                mainFrame.setContentPane(loginScreen);
                mainFrame.setTitle("Wellness App - Login");
                mainFrame.setSize(400, 300);
                mainFrame.setLocationRelativeTo(null);
                mainFrame.revalidate();
                mainFrame.repaint();
            }
        });
        add(logoutButton);
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
            


            ReminderSetupScreen setupScreen = new ReminderSetupScreen(loggedInUser, type, () -> {
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

    private void viewRecommendations() {
        File recommendationFile = new File("assets/recommendations_" + loggedInUser.getUsername() + ".txt");
        if (!recommendationFile.exists()) {
            JOptionPane.showMessageDialog(this, "No recommendations found for you.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Debugging information
        System.out.println("File exists: " + recommendationFile.exists());
        System.out.println("File is writable: " + recommendationFile.canWrite());
        System.out.println("File path: " + recommendationFile.getAbsolutePath());

        try (BufferedReader reader = new BufferedReader(new FileReader(recommendationFile))) {
            StringBuilder recommendations = new StringBuilder();
            recommendations.append("Recommendations for ").append(loggedInUser.getUsername()).append(":\n\n");
            String line;
            while ((line = reader.readLine()) != null) {
                recommendations.append(line).append("\n");
            }
            JOptionPane.showMessageDialog(this, recommendations.toString(), "Your Recommendations", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load recommendations.", "Error", JOptionPane.ERROR_MESSAGE);
            return; // Exit if reading fails
        }

        // Attempt to delete the file after reading
        if (recommendationFile.delete()) {
            System.out.println("Recommendations file deleted successfully.");
        } else {
            System.err.println("Failed to delete recommendations file.");
            JOptionPane.showMessageDialog(this, "Failed to delete recommendations file.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}