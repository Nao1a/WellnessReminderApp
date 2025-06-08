package screens;

import java.awt.*;
import javax.swing.*;
import models.User;
import services.AuthService;
import services.ReminderManager;

public class LoginScreen extends JPanel {
    public LoginScreen() {
        setPreferredSize(new Dimension(400, 300));
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel userLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField();
        usernameField.setPreferredSize(new Dimension(180, 28));

        JLabel passLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(180, 28));

        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.PLAIN, 16));
        JLabel messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Arial", Font.ITALIC, 14));

        // Username row
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        add(userLabel, gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(usernameField, gbc);

        // Password row
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        add(passLabel, gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(passwordField, gbc);

        // Login button row
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(loginButton, gbc);

        // Message label row
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(messageLabel, gbc);

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            User user = AuthService.login(username, password);

            JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);

            if (user != null) {
                messageLabel.setText("Login successful as " + user.getRole());
                if (user.getRole().equalsIgnoreCase("USER")) {
                    ReminderManager reminderManager = ReminderManager.getInstance(user);
                    reminderManager.start();
                    UserDashboard dashboardPanel = new UserDashboard(user);
                    if (currentFrame != null) {
                        currentFrame.setContentPane(dashboardPanel);
                        currentFrame.setTitle("Wellness App - User Dashboard");
                        currentFrame.setSize(600, 500);
                        currentFrame.setLocationRelativeTo(null);
                        currentFrame.revalidate();
                        currentFrame.repaint();
                    }
                } else if (user.getRole().equalsIgnoreCase("DOCTOR")) {
                    DoctorDashboard doctorPanel = new DoctorDashboard(
                        user.getUsername(),
                        () -> {
                            currentFrame.setContentPane(this);
                            currentFrame.setTitle("Wellness App - Login");
                            currentFrame.revalidate();
                            currentFrame.repaint();
                        }
                    );
                    if (currentFrame != null) {
                        currentFrame.setContentPane(doctorPanel);
                        currentFrame.setTitle("Wellness App - Doctor Dashboard");
                        currentFrame.setSize(800, 600);
                        currentFrame.setLocationRelativeTo(null);
                        currentFrame.revalidate();
                        currentFrame.repaint();
                    }
                }
            } else {
                messageLabel.setText("Invalid username or password. Please try again.");
            }
        });
    }
}
