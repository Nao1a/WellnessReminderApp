package services;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
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
        // Remove from memory
        reminders.removeIf(r -> r.getType().equals(type) && 
            String.valueOf(r.getIntervalMinutes()).equals(interval.split(" ")[0]));

        // Remove from file
        try {
            List<String> lines = Files.readAllLines(Paths.get(reminderFileName));
            List<String> newLines = new ArrayList<>();
            boolean skipNext = false;
            int linesToSkip = 0;
            
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.startsWith("Reminder Type:") && 
                    line.contains(type) && 
                    i + 1 < lines.size() && 
                    lines.get(i + 1).contains(interval.split(" ")[0])) {
                    skipNext = true;
                    linesToSkip = 5; // Skip the next 5 lines (type, interval, created, next, separator)
                    continue;
                }
                if (skipNext) {
                    linesToSkip--;
                    if (linesToSkip <= 0) {
                        skipNext = false;
                    }
                    continue;
                }
                newLines.add(line);
            }
            
            Files.write(Paths.get(reminderFileName), newLines);
        } catch (IOException e) {
            System.err.println("Failed to delete reminder: " + e.getMessage());
        }
    }

    public void addReminder(Reminder reminder) {
        reminders.add(reminder);
        saveReminders();
    }
}