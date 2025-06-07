package services;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Timer;
import javax.swing.*;
import models.Reminder;
import models.ReminderLog;
import models.User;

public class ReminderManager {
    private final User loggedInUser;
    private final List<Reminder> reminders = new ArrayList<>();
    private final Timer timer = new Timer();
    private final NotificationService notificationService;
    private final String reminderFileName;

    public ReminderManager(User user) {
        this.loggedInUser = user;
        this.notificationService = new NotificationService();
        this.reminderFileName = "assets/reminder_" + user.getUsername() + ".txt";
        loadReminders();
        startReminderCheck();
    }

    private void loadReminders() {
        File file = new File(reminderFileName);

        if (!file.exists()) {
            System.out.println("No reminders found for user: " + loggedInUser.getUsername());
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Reminder Type:")) {
                    String type = line.split(":")[1].trim();
                    String intervalLine = reader.readLine();
                    int interval = Integer.parseInt(intervalLine.split(":")[1].trim().split(" ")[0]);
                    reminders.add(new Reminder(type, interval));
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to load reminders: " + e.getMessage());
        }
    }

    private void startReminderCheck() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkReminders();
            }
        }, 0, 60000); // Check every minute
    }

    private void checkReminders() {
        LocalDateTime now = LocalDateTime.now();
        boolean needsSave = false;
        
        for (Reminder reminder : reminders) {
            if (now.isAfter(reminder.getNextReminderTime()) || now.isEqual(reminder.getNextReminderTime())) {
                showReminderPopup(reminder);
                reminder.updateNextReminderTime();
                needsSave = true;
            }
        }
        
        if (needsSave) {
            saveReminders();
        }
    }

    private void showReminderPopup(Reminder reminder) {
        String[] options = {"Acknowledge", "Snooze", "Missed"};
        int choice = JOptionPane.showOptionDialog(
                null,
                "It's time for your " + reminder.getType() + " reminder!",
                "Reminder Alert",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]
        );

        handleUserResponse(reminder, choice);
    }

    private void handleUserResponse(Reminder reminder, int choice) {
        String response;
        switch (choice) {
            case 0 -> response = "Acknowledged";
            case 1 -> {
                response = "Snoozed";
                snoozeReminder(reminder);
            }
            case 2 -> response = "Missed";
            default -> response = "No Response";
        }

        logReminderResponse(reminder, response);
    }

    private void snoozeReminder(Reminder reminder) {
        TimerTask snoozeTask = new TimerTask() {
            @Override
            public void run() {
                showReminderPopup(reminder);
            }
        };
        timer.schedule(snoozeTask, 5 * 60 * 1000); // Snooze for 5 minutes
    }

    private void saveReminders() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(reminderFileName))) {
            for (Reminder reminder : reminders) {
                writer.write("Reminder Type: " + reminder.getType() + "\n");
                writer.write("Interval: " + reminder.getIntervalMinutes() + " minutes\n");
                writer.write("Created At: " + reminder.getCreatedAt() + "\n");
                writer.write("Next Reminder: " + reminder.getNextReminderTime() + "\n");
                writer.write("---------------\n");
            }
        } catch (IOException e) {
            System.err.println("Failed to save reminders: " + e.getMessage());
        }
    }

    private void logReminderResponse(Reminder reminder, String response) {
        String timestamp = LocalDateTime.now().toString();
        ReminderLog log = new ReminderLog(reminder.getType(), timestamp, response, "User interacted with reminder.");
        
        // Save log to user-specific file in assets folder
        String logFileName = "assets/reminder_log_" + loggedInUser.getUsername() + ".txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFileName, true))) {
            writer.write(log.toString());
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Failed to save reminder log: " + e.getMessage());
        }
    }

    public void stop() {
        timer.cancel();
    }

    public void deleteReminder(String type, String interval) {
        // Input validation
        if (type == null || type.trim().isEmpty() || interval == null || interval.trim().isEmpty()) {
            throw new IllegalArgumentException("Reminder type and interval cannot be null or empty");
        }

        int intervalMinutes = parseIntervalMinutes(interval);
        // Remove from memory with robust comparison
        boolean removed = reminders.removeIf(r -> 
            r.getType().trim().equalsIgnoreCase(type.trim()) &&
            r.getIntervalMinutes() == intervalMinutes
        );

        if (!removed) {
            throw new IllegalStateException("Reminder not found: Reminder " + type + " with interval " + interval);
        }

        // Remove from file
        try {
            Path reminderPath = Paths.get(reminderFileName);
            if (!Files.exists(reminderPath)) {
                throw new FileNotFoundException("Reminder file not found: " + reminderFileName);
            }

            List<String> lines = Files.readAllLines(reminderPath);
            List<String> newLines = new ArrayList<>();
            boolean skipNext = false;
            int linesToSkip = 0;

            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                // Check if this is the start of the reminder to delete
                if (line.startsWith("Reminder Type:") && 
                    line.substring("Reminder Type:".length()).trim().equalsIgnoreCase(type.trim()) &&
                    i + 1 < lines.size() &&
                    parseIntervalMinutes(lines.get(i + 1).substring("Interval: ".length()).trim()) == intervalMinutes) {
                    skipNext = true;
                    linesToSkip = 5; // Skip type, interval, created, next, separator
                    continue;
                }
                // Skip lines if we're in the middle of the reminder to delete
                if (skipNext) {
                    linesToSkip--;
                    if (linesToSkip <= 0) {
                        skipNext = false;
                    }
                    continue;
                }
                newLines.add(line);
            }
            // Write back the file with the reminder removed
            Files.write(reminderPath, newLines, StandardOpenOption.TRUNCATE_EXISTING);
            // Log the deletion
            logReminderResponse(new Reminder(type, intervalMinutes), "DELETED");
        } catch (IOException e) {
            // Restore the reminder in memory if file operation fails
            reminders.add(new Reminder(type, intervalMinutes));
            throw new RuntimeException("Failed to delete reminder from file: " + e.getMessage(), e);
        }
    }

    // Helper method to parse interval string to minutes
    private int parseIntervalMinutes(String interval) {
        String[] parts = interval.split(" ");
        int value = Integer.parseInt(parts[0]);
        if (parts[1].toLowerCase().startsWith("hour")) {
            return value * 60;
        } else if (parts[1].toLowerCase().startsWith("minute")) {
            return value;
        } else if (parts[1].startsWith("1:30")) {
            return 90;
        } else if (parts[1].startsWith("2:30")) {
            return 150;
        }
        throw new IllegalArgumentException("Invalid interval format: " + interval);
    }

    public void addReminder(Reminder reminder) {
        reminders.add(reminder);
        saveReminders();
    }
}