package screens;

import java.awt.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.*;

public class SignupScreen extends JPanel {
    public SignupScreen(Runnable goBackCallback) {
        setLayout(new GridLayout(0, 2, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Fields for user input
        add(new JLabel("Username:"));
        JTextField usernameField = new JTextField();
        add(usernameField);

        add(new JLabel("Password:"));
        JPasswordField passwordField = new JPasswordField();
        add(passwordField);

        add(new JLabel("Role:"));
        JComboBox<String> roleComboBox = new JComboBox<>(new String[]{"USER", "DOCTOR"});
        add(roleComboBox);

        add(new JLabel("Special Code (Doctors Only):"));
        JTextField specialCodeField = new JTextField();
        specialCodeField.setEnabled(false); // Initially disabled
        add(specialCodeField);

        // Enable special code field only if "DOCTOR" is selected
        roleComboBox.addActionListener(e -> {
            String selectedRole = (String) roleComboBox.getSelectedItem();
            specialCodeField.setEnabled("DOCTOR".equalsIgnoreCase(selectedRole));
        });

        JButton signupButton = new JButton("Sign Up");
        JButton backButton = new JButton("Back");

        add(new JLabel()); // Empty space for alignment
        add(signupButton);
        add(new JLabel()); // Empty space for alignment
        add(backButton);

        signupButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            String role = (String) roleComboBox.getSelectedItem();
            String specialCode = specialCodeField.getText().trim();

            // Validate input
            if (username.isEmpty() || password.isEmpty() || role.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validate password: only letters and numbers allowed
            if (!password.matches("[a-zA-Z0-9]+")) {
                JOptionPane.showMessageDialog(this, "Password can only contain letters and numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if ("DOCTOR".equalsIgnoreCase(role)) {
                if (specialCode.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Special code is required for doctors.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Validate the special code against doctor_codes.txt
                File doctorCodesFile = new File("assets/doctor_codes.txt");
                boolean isValidCode = false;

                try (BufferedReader reader = new BufferedReader(new FileReader(doctorCodesFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.trim().equals(specialCode)) {
                            isValidCode = true;
                            break;
                        }
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Failed to validate doctor code.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!isValidCode) {
                    JOptionPane.showMessageDialog(this, "Invalid doctor code. Please try again.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            // Save user details to file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("assets/users.txt", true))) {
                writer.write(username + "|" + password + "|" + role);
                writer.newLine();
                JOptionPane.showMessageDialog(this, "Signup successful!", "Success", JOptionPane.INFORMATION_MESSAGE);

                // Clear fields after successful signup
                usernameField.setText("");
                passwordField.setText("");
                roleComboBox.setSelectedIndex(0);
                specialCodeField.setText("");
                specialCodeField.setEnabled(false);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Failed to save user details.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        backButton.addActionListener(e -> goBackCallback.run());
    }
}