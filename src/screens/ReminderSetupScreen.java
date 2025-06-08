package screens;

import java.awt.GridLayout;
import javax.swing.*;
import models.Reminder;
import models.User;
import services.ReminderManager;

public class ReminderSetupScreen extends JPanel {
    private User loggedInUser;
    private ReminderManager reminderManager;

    public ReminderSetupScreen(User user, String type, Runnable goBackCallback) {
        this.loggedInUser = user;
        this.reminderManager = ReminderManager.getInstance(user);

        setLayout(new GridLayout(0, 2, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(new JLabel("Reminder Type:"));
        add(new JLabel(type));

        final JComboBox<String>[] timeDropdown = new JComboBox[]{null};
        final JComboBox<String>[] intervalDropdown = new JComboBox[]{null};
        final JTextField[] medicineTypeField = new JTextField[]{null};
        final JTextField[] dosageField = new JTextField[]{null};

        // Configure dropdowns and fields based on reminder type
        if (type.equalsIgnoreCase("Hydration")) {
            add(new JLabel("Interval:"));
            intervalDropdown[0] = new JComboBox<>(new String[]{"30 minutes", "1 hour", "2 hours", "3 hours", "4 hours"});
            add(intervalDropdown[0]);
        } else if (type.equalsIgnoreCase("Medication")) {
            add(new JLabel("Medicine Type:"));
            medicineTypeField[0] = new JTextField();
            add(medicineTypeField[0]);
            add(new JLabel("Dosage:"));
            dosageField[0] = new JTextField();
            add(dosageField[0]);
            add(new JLabel("Interval:"));
            intervalDropdown[0] = new JComboBox<>(new String[]{"1 hour", "2 hours", "3 hours", "4 hours", "6 hours", "8 hours", "12 hours", "24 hours"});
            add(intervalDropdown[0]);
        } else if (type.equalsIgnoreCase("Sleep")) {
            add(new JLabel("Time:"));
            timeDropdown[0] = new JComboBox<>(new String[]{
                "8:00 PM", "8:30 PM", "9:00 PM", "9:30 PM", "10:00 PM", "10:30 PM", 
                "11:00 PM", "11:30 PM", "12:00 AM", "12:30 AM", "1:00 AM"
            });
            add(timeDropdown[0]);
            add(new JLabel("Interval:"));
            intervalDropdown[0] = new JComboBox<>(new String[]{"24 hours", "12 hours"});
            add(intervalDropdown[0]);
        } else if (type.equalsIgnoreCase("Eye Strain")) {
            add(new JLabel("Interval:"));
            intervalDropdown[0] = new JComboBox<>(new String[]{"1 minute", "30 minutes", "1 hour", "1:30 hours", "2 hours", "2:30 hours"});
            add(intervalDropdown[0]);
        } else if (type.equalsIgnoreCase("Meal")) {
            add(new JLabel("Time:"));
            timeDropdown[0] = new JComboBox<>(new String[]{
                "7:00 AM", "7:30 AM", "8:00 AM", "8:30 AM", "9:00 AM", // Breakfast
                "11:30 AM", "12:00 PM", "12:30 PM", "1:00 PM", "1:30 PM", // Lunch
                "5:30 PM", "6:00 PM", "6:30 PM", "7:00 PM", "7:30 PM" // Dinner
            });
            add(timeDropdown[0]);
            add(new JLabel("Interval:"));
            intervalDropdown[0] = new JComboBox<>(new String[]{"24 hours", "12 hours"});
            add(intervalDropdown[0]);
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

            String medicineType = null;
            String dosage = null;
            if (type.equalsIgnoreCase("Medication")) {
                medicineType = (medicineTypeField[0] != null) ? medicineTypeField[0].getText().trim() : null;
                dosage = (dosageField[0] != null) ? dosageField[0].getText().trim() : null;
                if (medicineType == null || medicineType.isEmpty() || dosage == null || dosage.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter both medicine type and dosage.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            if (selectedTime == null && selectedInterval == null) {
                JOptionPane.showMessageDialog(this, "Please select a valid time or interval.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String reminderSpecificNotes = (selectedTime != null) ? "Time: " + selectedTime : "Interval: " + selectedInterval;
            if (type.equalsIgnoreCase("Medication")) {
                reminderSpecificNotes += " | Medicine: " + medicineType + " | Dosage: " + dosage;
            }

            int intervalMinutes = 0;
            java.time.LocalDateTime nextReminderTime = java.time.LocalDateTime.now();

            if (type.equalsIgnoreCase("Sleep") || type.equalsIgnoreCase("Meal")) {
                // Normalize selectedTime to have only one space before AM/PM
                String normalizedTime = selectedTime.trim().replaceAll("\\s+(AM|PM)", " $1");
                java.time.LocalTime time = java.time.LocalTime.parse(
                    normalizedTime,
                    java.time.format.DateTimeFormatter.ofPattern("h:mm a")
                );
                java.time.LocalDateTime now = java.time.LocalDateTime.now();
                nextReminderTime = now.withHour(time.getHour()).withMinute(time.getMinute()).withSecond(0).withNano(0);
                if (nextReminderTime.isBefore(now)) {
                    nextReminderTime = nextReminderTime.plusDays(1); // If time has passed today, set for tomorrow
                }
                if (selectedInterval != null) {
                    String[] parts = selectedInterval.split(" ");
                    int value = Integer.parseInt(parts[0]);
                    if (parts[1].toLowerCase().startsWith("hour")) {
                        intervalMinutes = value * 60;
                    } else if (parts[1].toLowerCase().startsWith("minute")) {
                        intervalMinutes = value;
                    } else if (parts[1].startsWith("1:30")) {
                        intervalMinutes = 90;
                    } else if (parts[1].startsWith("2:30")) {
                        intervalMinutes = 150;
                    }
                } else {
                    intervalMinutes = 24 * 60; // Default to daily
                }
            } else if (selectedInterval != null) {
                String[] parts = selectedInterval.split(" ");
                int value = Integer.parseInt(parts[0]);
                if (parts[1].toLowerCase().startsWith("hour")) {
                    intervalMinutes = value * 60;
                } else if (parts[1].toLowerCase().startsWith("minute")) {
                    intervalMinutes = value;
                } else if (parts[1].startsWith("1:30")) {
                    intervalMinutes = 90;
                } else if (parts[1].startsWith("2:30")) {
                    intervalMinutes = 150;
                }
                nextReminderTime = java.time.LocalDateTime.now().plusMinutes(intervalMinutes);
            }

            Reminder reminder = new Reminder(type, intervalMinutes);
            if (type.equalsIgnoreCase("Medication")) {
                reminder.setMedicineType(medicineType);
                reminder.setDosage(dosage);
            }
            reminder.setNextReminderTime(nextReminderTime);
            try {
                reminderManager.addReminder(reminder);
                JOptionPane.showMessageDialog(this, "Reminder set for " + type + ".\n" + reminderSpecificNotes);
                goBackCallback.run();
            } catch (IllegalStateException ex) {
                JOptionPane.showMessageDialog(
                    this,
                    "A reminder with this type and interval already exists.\nPlease choose a different interval or type.",
                    "Duplicate Reminder",
                    JOptionPane.ERROR_MESSAGE
                );
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                    this,
                    "An error occurred while adding the reminder:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });

        // Back Button Action Listener
        backButton.addActionListener(e -> goBackCallback.run());
    }
}