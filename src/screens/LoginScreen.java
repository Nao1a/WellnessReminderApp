package screens;

import java.awt.*;
import javax.swing.*;
import models.User;
import services.AuthService;

public class LoginScreen extends JFrame {
    public LoginScreen() {
        setTitle("wellness App - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(4,1));

        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JButton loginButton = new JButton("Login");
        JLabel messageLabel = new JLabel("", SwingConstants.CENTER);

        add(new JLabel("Username:", SwingConstants.CENTER));
        add(usernameField);
        add(new JLabel("Password:", SwingConstants.CENTER));
        add(passwordField);
        add(loginButton);
        add(messageLabel);

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            User user = AuthService.login(username, password);

            if (user != null) {
                messageLabel.setText("Login successufl as " + user.getRole());
                if (user.role.equalsIgnoreCase("USER")){
                    System.out.println("go to user Dashboeard");
                    new UserDashboard(user);
                    dispose();
                } else {
                    System.out.println("go to Doctor Dashboard"); 
                } 
                dispose();
            } else {
                messageLabel.setText("Invalid username or password");
            }
        
        });
        setVisible(true);
    }
}
