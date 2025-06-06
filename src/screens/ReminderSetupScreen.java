package screens;

import java.awt.GridLayout;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
import models.Reminder;
import models.ReminderLog;
import models.User;
import services.ReminderLogger;

public class ReminderSetupScreen extends JPanel {
    private User loggedInUser;

    public ReminderSetupScreen(User user, String type, Runnable goBackCallback) {
        this.loggedInUser = user;

        setLayout(new GridLayout(0, 2, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(new JLabel("Reminder Type:"));
        add(new JLabel(type));

        final JComboBox<String>[] timeDropdown = new JComboBox[]{null};
        final JComboBox<String>[] intervalDropdown = new JComboBox[]{null};

        // Configure dropdowns based on reminder type
        if (type.equalsIgnoreCase("Hydration")) {
            add(new JLabel("Interval:"));
            intervalDropdown[0] = new JComboBox<>(new String[]{"30 minutes", "1 hour", "2 hours", "3 hours", "4 hours"});
            add(intervalDropdown[0]);
        } else if (type.equalsIgnoreCase("Medication")) {
            add(new JLabel("Interval:"));
            intervalDropdown[0] = new JComboBox<>(new String[]{"1 hour", "2 hours", "3 hours", "4 hours", "6 hours", "8 hours", "12 hours", "24 hours"});
            add(intervalDropdown[0]);
        } else if (type.equalsIgnoreCase("Sleep")) {
            add(new JLabel("Time:"));
            timeDropdown[0] = new JComboBox<>(new String[]{"7:00 PM", "8:00 PM", "9:00 PM", "10:00 PM", "11:00 PM", "12:00 AM", "1:00 AM", "2:00 AM"});
            add(timeDropdown[0]);
        } else if (type.equalsIgnoreCase("Eye Strain")) {
            add(new JLabel("Interval:"));
            intervalDropdown[0] = new JComboBox<>(new String[]{"30 minutes", "1 hour", "1:30 hours", "2 hours", "2:30 hours"});
            add(intervalDropdown[0]);
        } else if (type.equalsIgnoreCase("Meal")) {
            add(new JLabel("Time:"));
            timeDropdown[0] = new JComboBox<>(new String[]{"7:30 AM", "12:00 PM", "6:30 PM"});
            add(timeDropdown[0]);
        } else if (type.equalsIgnoreCase("Movement")) {
            add(new JLabel("Interval:"));
            intervalDropdown[0] = new JComboBox<>(new String[]{"1 hour", "2 hours", "3 hours", "4 hours"});
            add(intervalDropdown[0]);
        }

        // Save Button
        JButton saveBtn = new JButton("Save Reminder");
        add(new JLabel()); // Placeholder for alignment
        add(saveBtn);

        // Back Button
        JButton backButton = new JButton("Back");
        add(new JLabel()); // Placeholder for alignment
        add(backButton);

        // Save Button Action Listener
        saveBtn.addActionListener(e -> {
            String selectedTime = (timeDropdown[0] != null) ? (String) timeDropdown[0].getSelectedItem() : null;
            String selectedInterval = (intervalDropdown[0] != null) ? (String) intervalDropdown[0].getSelectedItem() : null;

            if (selectedTime == null && selectedInterval == null) {
                JOptionPane.showMessageDialog(this, "Please select a valid time or interval.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String reminderSpecificNotes = (selectedTime != null) ? "Time: " + selectedTime : "Interval: " + selectedInterval;

            Reminder reminder = new Reminder(type, selectedTime != null ? selectedTime : selectedInterval, 0, false, reminderSpecificNotes);

            // Log the reminder setup event
            if (loggedInUser != null) {
                String logTimestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                String logNotes = "Reminder configured. Details: " + reminder.getNotes();
                ReminderLog logEntry = new ReminderLog(reminder.getType(), logTimestamp, "SET", logNotes);
                ReminderLogger.log(loggedInUser, logEntry);
            }

            JOptionPane.showMessageDialog(this, "Reminder set for " + type + ".\n" + reminderSpecificNotes);
            goBackCallback.run();
        });

        // Back Button Action Listener
        backButton.addActionListener(e -> goBackCallback.run());
    }
}