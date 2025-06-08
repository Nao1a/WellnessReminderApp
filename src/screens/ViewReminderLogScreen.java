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

        // Create button panel for delete functionality
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton deleteButton = new JButton("Delete Selected");
        deleteButton.addActionListener(e -> deleteSelectedReminder());
        buttonPanel.add(deleteButton);

        reminderListModel = new DefaultListModel<>();
        reminderList = new JList<>(reminderListModel);
        reminderList.setFont(new Font("Arial", Font.PLAIN, 14));
        loadReminders();
        
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.add(new JScrollPane(reminderList), BorderLayout.CENTER);
        listPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        remindersPanel.add(listPanel, BorderLayout.CENTER);

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

        // Add panels to split pane
        splitPane.setTopComponent(remindersPanel);
        splitPane.setBottomComponent(logsPanel);

        // Add split pane to main panel
        add(splitPane, BorderLayout.CENTER);

        // Add back button
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> goBackCallback.run());
        add(backButton, BorderLayout.SOUTH);
    }

    private void loadReminders() {
        reminderListModel.clear();
        File file = new File("assets/reminder_" + loggedInUser.getUsername() + ".txt");
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            StringBuilder reminderText = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("ID:")) {
                    if (reminderText.length() > 0) {
                        reminderListModel.addElement(reminderText.toString());
                        reminderText = new StringBuilder();
                    }
                }
                reminderText.append(line).append("\n");
            }
            if (reminderText.length() > 0) {
                reminderListModel.addElement(reminderText.toString());
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                "Error loading reminders: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedReminder() {
        int selectedIndex = reminderList.getSelectedIndex();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a reminder to delete",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        String selectedReminder = reminderListModel.get(selectedIndex);
        String id = selectedReminder.split("\n")[0].substring(4).trim(); // Extract ID from "ID: xxxxxx"

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this reminder?",
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                reminderManager.deleteReminderById(id);
                reminderListModel.remove(selectedIndex);
                loadLogs(); // Refresh logs to show deletion
                JOptionPane.showMessageDialog(this,
                    "Reminder deleted successfully",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error deleting reminder: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadLogs() {
        logArea.setText("");
        File file = new File("assets/reminder_log_" + loggedInUser.getUsername() + ".txt");
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                logArea.append(line + "\n");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                "Error loading logs: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}