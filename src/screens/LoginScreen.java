package screens;

import java.awt.*;
import javax.swing.*;
import models.User;
import services.AuthService;

public class LoginScreen extends JPanel { // Changed from JFrame

    

    public LoginScreen() {
        // setTitle("wellness App - Login"); // No longer a JFrame
        // setSize(400, 300); // No longer a JFrame
        // setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // No longer a JFrame
        setLayout(new GridLayout(5, 1, 10, 10)); // Increased rows for better spacing
        setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50)); // Add padding

        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.PLAIN, 16));
        JLabel messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Arial", Font.ITALIC, 14));

        JPanel userPanel = new JPanel(new BorderLayout(5,0));
        userPanel.add(new JLabel("Username:"), BorderLayout.WEST);
        userPanel.add(usernameField, BorderLayout.CENTER);

        JPanel passPanel = new JPanel(new BorderLayout(5,0));
        passPanel.add(new JLabel("Password:"), BorderLayout.WEST);
        passPanel.add(passwordField, BorderLayout.CENTER);
        
        add(userPanel);
        add(passPanel);

        JPanel buttonHolder = new JPanel(new FlowLayout(FlowLayout.CENTER)); // To center the button
        buttonHolder.add(loginButton);
        add(buttonHolder);
        add(messageLabel);

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            User user = AuthService.login(username, password);

            JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);

            if (user != null) {
                messageLabel.setText("Login successful as " + user.getRole());
                if (user.getRole().equalsIgnoreCase("USER")) {
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
                        user.getUsername(), // Pass the logged-in doctor's username
                        () -> {
                            currentFrame.setContentPane(this); // Go back to LoginScreen
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
        // setVisible(true); // No longer a JFrame, container controls visibility
    }
}
