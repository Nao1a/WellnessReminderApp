package screens;

import java.awt.*;
import javax.swing.*;
import models.Reminder;
public class ReminderSetupScreen extends JFrame {
    public ReminderSetupScreen(String type) {
        setTitle("Set " + type + "Reminder");
        setSize(400, 400);
        setLayout(new GridLayout(0, 2, 10, 10));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); // So dashboard stays open

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
            dispose();
        });
        setVisible(true);
    }
}