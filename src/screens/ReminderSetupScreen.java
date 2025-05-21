package screens;

import javax.swing.*;
import models.Reminder;
public class ReminderSetupScreen extends JPanel {
    public ReminderSetupScreen(String type , Runnable goBackCallback) {
        
       

        // Shared fields
        add(new JLabel("Reminder Time (e.g. 08:00 AM):"));
        JTextField timeField = new JTextField();
        add(timeField);

        add(new JLabel("Frequency (in hours):"));
        JTextField freqField = new JTextField();
        add(freqField);

        // Optional fields
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
        JButton saveBtn = new JButton(" Save Reminder");
        add(saveBtn);
        add(new JLabel());

        JTextField finalNameField = nameField;
        JTextField finalDosageField = dosageField;

        saveBtn.addActionListener(e -> {
            String time = timeField.getText().trim();
            int freq = Integer.parseInt(freqField.getText().trim());

            String notes = "";
            if (type.equalsIgnoreCase("Medication")) {
                notes = "Name:" + finalNameField.getText().trim() + ", Dosage:" + finalDosageField.getText().trim();
            }
            Reminder reminder = new Reminder(type, time , freq, false , notes);

            JOptionPane.showMessageDialog(this, "Reminder set for " + type + " at " + time + " every " + freq + " hours.\n" + notes);
            // Later: save to user.reminder or file
            // For now, just print to console
            System.out.println("Reminder set for " + type + " at " + time + " every " + freq + " hours.\n" + notes);
            goBackCallback.run();
        });
        
    }
}