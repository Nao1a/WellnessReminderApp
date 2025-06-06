package screens;

import java.awt.*;
import java.io.*;
import java.util.List;
import javax.swing.*;

public class DoctorDashboard extends JPanel {
    private JComboBox<String> userDropdown;
    private JTextArea logArea;
    private JTextArea recommendationArea;
    private String loggedInDoctor;

    public DoctorDashboard(String doctorName, Runnable goBackCallback) {
        this.loggedInDoctor = doctorName; // Store the logged-in doctor's username

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top Panel: User Selection
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Select User:"));
        userDropdown = new JComboBox<>(loadUsers().toArray(new String[0]));
        topPanel.add(userDropdown);

        JButton viewLogsButton = new JButton("View Logs");
        viewLogsButton.addActionListener(e -> viewLogs());
        topPanel.add(viewLogsButton);

        add(topPanel, BorderLayout.NORTH);

        // Center Panel: Logs and Recommendations
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 10));

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setBorder(BorderFactory.createTitledBorder("User Logs"));
        centerPanel.add(new JScrollPane(logArea));

        recommendationArea = new JTextArea();
        recommendationArea.setBorder(BorderFactory.createTitledBorder("Add Recommendation"));
        centerPanel.add(new JScrollPane(recommendationArea));

        add(centerPanel, BorderLayout.CENTER);

        // Bottom Panel: Buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton addRecommendationButton = new JButton("Add Recommendation");
        addRecommendationButton.addActionListener(e -> addRecommendation());
        bottomPanel.add(addRecommendationButton);

        JButton logoutButton = new JButton("Logout");
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
        bottomPanel.add(logoutButton);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> goBackCallback.run());
        bottomPanel.add(backButton);

        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    // Method to load users from users.txt
    private List<String> loadUsers() {
        List<String> users = new java.util.ArrayList<>();
        java.nio.file.Path path = java.nio.file.Paths.get("assets/users.txt");
        try {
            if (!java.nio.file.Files.exists(path)) {
                JOptionPane.showMessageDialog(this, "File not found: " + path.toAbsolutePath(), "Error", JOptionPane.ERROR_MESSAGE);
                return users;
            }

            List<String> lines = java.nio.file.Files.readAllLines(path);
            for (String line : lines) {
                String[] parts = line.split("\\|");
                if (parts.length >= 3) {
                    String username = parts[0];
                    String role = parts[2];
                    // Only add users with the role "USER"
                    if ("USER".equalsIgnoreCase(role)) {
                        users.add(username);
                    }
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Unable to load users list.", "Error", JOptionPane.ERROR_MESSAGE);
            System.err.println("Error reading users file: " + e.getMessage());
        }
        return users;
    }

    private void viewLogs() {
        String selectedUser = (String) userDropdown.getSelectedItem();
        if (selectedUser == null || selectedUser.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a user.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        File logFile = new File("assets/logs_" + selectedUser + ".txt");
        if (!logFile.exists()) {
            logArea.setText("No logs found for " + selectedUser + ".");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
            StringBuilder logs = new StringBuilder();
            logs.append("Logged Reminders for ").append(selectedUser).append(":\n\n");
            String line;
            while ((line = reader.readLine()) != null) {
                logs.append(line).append("\n");
            }
            logArea.setText(logs.toString());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load logs for " + selectedUser + ".", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addRecommendation() {
        String selectedUser = (String) userDropdown.getSelectedItem();
        if (selectedUser == null || selectedUser.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a user.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String recommendation = recommendationArea.getText().trim();
        if (recommendation.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Recommendation cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Use the logged-in doctor's name
        String doctorName = loggedInDoctor;

        // Add a timestamp
        String timestamp = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());

        // Format the recommendation
        String formattedRecommendation = "From: " + doctorName + " | Timestamp: " + timestamp + " | Message: " + recommendation;

        File recommendationFile = new File("assets/recommendations_" + selectedUser + ".txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(recommendationFile, true))) {
            writer.write(formattedRecommendation);
            writer.newLine();
            JOptionPane.showMessageDialog(this, "Recommendation added for " + selectedUser + ".", "Success", JOptionPane.INFORMATION_MESSAGE);
            recommendationArea.setText(""); // Clear the recommendation area
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Failed to save recommendation for " + selectedUser + ".", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}