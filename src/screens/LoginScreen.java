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
        setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50)); // Add padding

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
                    System.out.println("go to user Dashboard");
                    UserDashboard dashboardPanel = new UserDashboard(user); // UserDashboard is now a JPanel
                    if (currentFrame != null) {
                        currentFrame.setContentPane(dashboardPanel);
                        currentFrame.setTitle("Wellness App - User Dashboard");
                        currentFrame.setSize(600, 500); // Resize for dashboard
                        currentFrame.setLocationRelativeTo(null);
                        currentFrame.revalidate();
                        currentFrame.repaint();
                    }
                } else if (user.getRole().equalsIgnoreCase("DOCTOR")) {
                    System.out.println("go to Doctor Dashboard");
                    // Placeholder for Doctor Dashboard as a JPanel
                    // DoctorDashboard doctorPanel = new DoctorDashboard(user);
                    // if (currentFrame != null) {
                    //    currentFrame.setContentPane(doctorPanel);
                    //    currentFrame.setTitle("Wellness App - Doctor Dashboard");
                    //    currentFrame.setSize(600, 500);
                    //    currentFrame.setLocationRelativeTo(null);
                    //    currentFrame.revalidate();
                    //    currentFrame.repaint();
                    // }
                    messageLabel.setText("Doctor Dashboard not yet fully implemented in this panel flow.");
                }
            } else {
                messageLabel.setText("Invalid username or password. Please try again.");
            }
        });
        // setVisible(true); // No longer a JFrame, container controls visibility
    }
}
