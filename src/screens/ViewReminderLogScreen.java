package screens;

import java.awt.*;
import java.io.*;
import javax.swing.*;
import models.User;
import services.ReminderManager;

public class ViewReminderLogScreen extends JPanel {
    private User loggedInUser;
    private ReminderManager reminderManager;
    private JTextArea logArea;
    private JList<String> reminderList;
    private DefaultListModel<String> reminderListModel;

    public ViewReminderLogScreen(User user, Runnable goBackCallback) {
        this.loggedInUser = user;
        this.reminderManager = new ReminderManager(user);
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create a split pane for reminders and logs
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(200);

        // Reminders Panel
        JPanel remindersPanel = new JPanel(new BorderLayout());
        JLabel remindersLabel = new JLabel("Active Reminders", SwingConstants.CENTER);
        remindersLabel.setFont(new Font("Arial", Font.BOLD, 18));
        remindersPanel.add(remindersLabel, BorderLayout.NORTH);

        reminderListModel = new DefaultListModel<>();
        reminderList = new JList<>(reminderListModel);
        reminderList.setFont(new Font("Arial", Font.PLAIN, 14));
        loadReminders();
        remindersPanel.add(new JScrollPane(reminderList), BorderLayout.CENTER);

        // Logs Panel
        JPanel logsPanel = new JPanel(new BorderLayout());
        JLabel logsLabel = new JLabel("Reminder Log", SwingConstants.CENTER);
        logsLabel.setFont(new Font("Arial", Font.BOLD, 18));
        logsPanel.add(logsLabel, BorderLayout.NORTH);

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Arial", Font.PLAIN, 14));
        loadLogs();
        logsPanel.add(new JScrollPane(logArea), BorderLayout.CENTER);

        splitPane.setTopComponent(remindersPanel);
        splitPane.setBottomComponent(logsPanel);
        add(splitPane, BorderLayout.CENTER);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        JButton deleteButton = new JButton("Delete Selected Reminder");
        deleteButton.addActionListener(e -> {
            int selectedIndex = reminderList.getSelectedIndex();
            if (selectedIndex != -1) {
                String selectedReminder = reminderListModel.get(selectedIndex);
                String[] parts = selectedReminder.split("\n");
                String type = parts[0].replace("Type: ", "").trim();
                String interval = parts[1].replace("Interval: ", "").trim();
                
                int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete this reminder?",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION
                );
                
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        reminderManager.deleteReminder(type, interval);
                        reminderListModel.remove(selectedIndex);
                        loadReminders(); // Refresh the reminder list
                        loadLogs(); // Refresh logs after deletion
                    } catch (IllegalArgumentException e1) {
                        JOptionPane.showMessageDialog(this, e1.getMessage(), 
                            "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    } catch (IllegalStateException e1) {
                        JOptionPane.showMessageDialog(this, e1.getMessage(), 
                            "Reminder Not Found", JOptionPane.ERROR_MESSAGE);
                    } catch (RuntimeException e1) {
                        JOptionPane.showMessageDialog(this, 
                            "Failed to delete reminder: " + e1.getMessage(), 
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a reminder to delete.");
            }
        });
        buttonPanel.add(deleteButton);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> goBackCallback.run());
        buttonPanel.add(backButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadReminders() {
        String filename = "assets/reminder_" + loggedInUser.getUsername() + ".txt";
        File file = new File(filename);

        if (!file.exists()) {
            reminderListModel.addElement("No active reminders");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            StringBuilder reminder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                if (line.equals("---------------")) {
                    if (reminder.length() > 0) {
                        reminderListModel.addElement(reminder.toString().trim());
                        reminder.setLength(0);
                    }
                } else {
                    reminder.append(line).append("\n");
                }
            }
        } catch (IOException e) {
            reminderListModel.addElement("Error loading reminders: " + e.getMessage());
        }
    }

    private void loadLogs() {
        String logFileName = "assets/reminder_log_" + loggedInUser.getUsername() + ".txt";
        File logFile = new File(logFileName);
        
        if (!logFile.exists()) {
            logArea.setText("No reminder logs found.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
            StringBuilder logs = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 4) {
                    logs.append("Type: ").append(parts[0]).append("\n");
                    logs.append("Time: ").append(parts[1]).append("\n");
                    logs.append("Response: ").append(parts[2]).append("\n");
                    logs.append("Notes: ").append(parts[3]).append("\n");
                    logs.append("---------------\n");
                }
            }
            logArea.setText(logs.toString());
        } catch (IOException e) {
            logArea.setText("Error reading log file: " + e.getMessage());
        }
    }
}