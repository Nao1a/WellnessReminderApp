package screens;

import java.awt.GridLayout;
import java.text.SimpleDateFormat;
import java.util.Date; // Import User model
import javax.swing.*; // Import ReminderLog model
import models.Reminder; // Import ReminderLogger service
import models.ReminderLog; // For better layout
import models.User; // For timestamp
import services.ReminderLogger; // For timestamp

public class ReminderSetupScreen extends JPanel {
    private User loggedInUser; // Store the logged-in user

    // Modified constructor to accept User
    public ReminderSetupScreen(User user, String type, Runnable goBackCallback) {
        this.loggedInUser = user; // Assign the user

        // Consider a more structured layout
        setLayout(new GridLayout(0, 2, 10, 10)); // 0 rows means as many as needed, 2 columns
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(new JLabel("Reminder Type:"));
        add(new JLabel(type));

        add(new JLabel("Reminder Time (e.g. 08:00 AM):"));
        JTextField timeField = new JTextField();
        add(timeField);

        add(new JLabel("Frequency (in hours, 0 for once):"));
        JTextField freqField = new JTextField("0"); // Default to 0 for one-time
        add(freqField);

        JTextField nameField = null;
        JTextField dosageField = null;

        if (type.equalsIgnoreCase("Medication")) {
            add(new JLabel("Medication Name:"));
            nameField = new JTextField();
            add(nameField);

            add(new JLabel("Dosage:"));
            dosageField = new JTextField();
            add(dosageField);
        }
        // Ensure components are added in pairs for GridLayout or use a different layout manager
        // For simplicity, if not medication, add empty JLabels to keep grid alignment
        else {
            add(new JLabel()); // Placeholder for name label
            add(new JLabel()); // Placeholder for name field
            add(new JLabel()); // Placeholder for dosage label
            add(new JLabel()); // Placeholder for dosage field
        }


        JButton saveBtn = new JButton("Save Reminder");
        // Add an empty label then the button to span it correctly if needed, or adjust layout
        add(new JLabel()); // Span for the button
        add(saveBtn);


        JTextField finalNameField = nameField;
        JTextField finalDosageField = dosageField;

        saveBtn.addActionListener(e -> {
            String time = timeField.getText().trim();
            if (time.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Time cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int freq;
            try {
                freq = Integer.parseInt(freqField.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Frequency must be a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String reminderSpecificNotes = "";
            if (type.equalsIgnoreCase("Medication")) {
                String medName = (finalNameField != null) ? finalNameField.getText().trim() : "";
                String dosage = (finalDosageField != null) ? finalDosageField.getText().trim() : "";
                if (medName.isEmpty()) {
                     JOptionPane.showMessageDialog(this, "Medication Name cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
                     return;
                }
                reminderSpecificNotes = "Name:" + medName + ", Dosage:" + dosage;
            }
            // Create the reminder object
            Reminder reminder = new Reminder(type, time, freq, false, reminderSpecificNotes);

            // Log the reminder setup event
            if (loggedInUser != null) {
                // Using current time for when the log is created (reminder is set)
                String logTimestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                String logNotes = "Reminder configured. Details: " + reminder.getNotes();
                if (reminder.getNotes() == null || reminder.getNotes().isEmpty()) {
                    logNotes = "Reminder configured for " + type + " at " + time + ".";
                }
                
                ReminderLog logEntry = new ReminderLog(reminder.getType(), logTimestamp, "SET", logNotes);
                ReminderLogger.log(loggedInUser, logEntry);
            } else {
                System.err.println("ReminderSetupScreen: LoggedInUser is null, cannot log reminder setup.");
            }
            
            // Add reminder to user's list (if User model supports it directly)
            // For now, this part is not implemented, but it's where you'd persist the reminder to the user
            if (loggedInUser != null) {
                // loggedInUser.addReminder(reminder); // Assuming User class has addReminder
                // And then you'd save the user data, e.g., using DataStore
                System.out.println("Reminder object created: " + reminder.getType() + " for user " + loggedInUser.getUsername());
            }


            JOptionPane.showMessageDialog(this, "Reminder set for " + type + " at " + time + " every " + freq + " hours.\n" + reminderSpecificNotes);
            System.out.println("Reminder set for " + type + " at " + time + " every " + freq + " hours.\n" + reminderSpecificNotes);
            
            goBackCallback.run();
        });
    }
}