package screens;

import java.awt.*;
import javax.swing.*;

public class WelcomeScreen extends JPanel {
    public WelcomeScreen() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        JLabel welcomeLabel = new JLabel("Welcome to the Wellness Reminder App!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 22));
        add(welcomeLabel, BorderLayout.CENTER);

        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.PLAIN, 18));
        loginButton.addActionListener(e -> {
            JFrame mainFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (mainFrame != null) {
                LoginScreen loginScreen = new LoginScreen();
                mainFrame.setContentPane(loginScreen);
                mainFrame.setTitle("Wellness App - Login");
                mainFrame.pack(); // Adjust frame size to panel
                mainFrame.setLocationRelativeTo(null);
                mainFrame.revalidate();
                mainFrame.repaint();
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(loginButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}