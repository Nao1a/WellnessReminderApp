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

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.PLAIN, 18));
        loginButton.addActionListener(e -> {
            JFrame mainFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (mainFrame != null) {
                LoginScreen loginScreen = new LoginScreen();
                mainFrame.setContentPane(loginScreen);
                mainFrame.setTitle("Wellness App - Login");
                mainFrame.pack();
                mainFrame.setLocationRelativeTo(null);
                mainFrame.revalidate();
                mainFrame.repaint();
            }
        });

        JButton signupButton = new JButton("Sign Up");
        signupButton.setFont(new Font("Arial", Font.PLAIN, 18));
        signupButton.addActionListener(e -> {
            JFrame mainFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (mainFrame != null) {
                SignupScreen signupScreen = new SignupScreen(() -> {
                    mainFrame.setContentPane(this); // Go back to WelcomeScreen
                    mainFrame.setTitle("Wellness App - Welcome");
                    mainFrame.revalidate();
                    mainFrame.repaint();
                });
                mainFrame.setContentPane(signupScreen);
                mainFrame.setTitle("Wellness App - Sign Up");
                mainFrame.pack();
                mainFrame.setLocationRelativeTo(null);
                mainFrame.revalidate();
                mainFrame.repaint();
            }
        });

        buttonPanel.add(loginButton);
        buttonPanel.add(signupButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}