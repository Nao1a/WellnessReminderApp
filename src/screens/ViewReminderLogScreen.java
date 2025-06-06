package screens;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import models.User;

public class ViewReminderLogScreen extends JPanel {
    private final User loggedInUser;
    private final DefaultListModel<String> reminderListModel = new DefaultListModel<>();

    public ViewReminderLogScreen(User user, Runnable goBackCallback) {
        this.loggedInUser = user;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Your Reminders", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        add(title, BorderLayout.NORTH);

        // Load reminders into the list
        JList<String> reminderList = new JList<>(reminderListModel);
        reminderList.setFont(new Font("Arial", Font.PLAIN, 18));
        loadReminders();
        add(new JScrollPane(reminderList), BorderLayout.CENTER);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));

        // Delete Button
        JButton deleteButton = new JButton("Delete Selected");
        deleteButton.setFont(new Font("Arial", Font.PLAIN, 18));
        deleteButton.addActionListener(e -> {
            int selectedIndex = reminderList.getSelectedIndex();
            if (selectedIndex != -1) {
                String selectedReminder = reminderListModel.get(selectedIndex);
                reminderListModel.remove(selectedIndex);
                deleteReminderFromFile(selectedReminder);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a reminder to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        buttonPanel.add(deleteButton);

        // Back Button
        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.PLAIN, 18));
        backButton.addActionListener(e -> goBackCallback.run());
        buttonPanel.add(backButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    // Load reminders from the file
    private void loadReminders() {
        String filename = "assets/reminder_" + loggedInUser.getUsername() + ".txt";
        File file = new File(filename);

        if (!file.exists()) {
            JOptionPane.showMessageDialog(this, "No reminders found.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            StringBuilder reminder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                if (line.equals("---------------")) {
                    reminderListModel.addElement(reminder.toString().trim());
                    reminder.setLength(0); // Clear the StringBuilder
                } else {
                    reminder.append(line).append("\n");
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to load reminders.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Delete a reminder from the file
    private void deleteReminderFromFile(String reminderToDelete) {
        String filename = "assets/reminder_" + loggedInUser.getUsername() + ".txt";
        File file = new File(filename);
        List<String> updatedReminders = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            StringBuilder reminder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                if (line.equals("---------------")) {
                    if (!reminder.toString().trim().equals(reminderToDelete.trim())) {
                        updatedReminders.add(reminder.toString().trim());
                    }
                    reminder.setLength(0); // Clear the StringBuilder
                } else {
                    reminder.append(line).append("\n");
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to delete reminder.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Write updated reminders back to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String updatedReminder : updatedReminders) {
                writer.write(updatedReminder);
                writer.write("\n---------------\n");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to update reminders file.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}