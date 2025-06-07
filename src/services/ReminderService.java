package services;

import models.Reminder;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class ReminderService {
    private static final String REMINDER_LOG_FILE = "assets/reminderlog.txt";
    private List<Reminder> reminders;

    public ReminderService() {
        this.reminders = new ArrayList<>();
        ensureAssetsDirectoryExists();
        loadReminders();
    }

    private void ensureAssetsDirectoryExists() {
        File dir = new File("assets");
        if (!dir.exists()) {
            if (dir.mkdir()) {
                System.out.println("Assets directory created.");
            } else {
                System.err.println("Failed to create assets directory.");
            }
        }
    }

    public void addReminder(String type, int intervalMinutes) {
        Reminder reminder = new Reminder(type, intervalMinutes);
        reminders.add(reminder);
        saveReminders();
    }

    public List<Reminder> getReminders() {
        return new ArrayList<>(reminders);
    }

    private void loadReminders() {
        try {
            if (Files.exists(Paths.get(REMINDER_LOG_FILE))) {
                List<String> lines = Files.readAllLines(Paths.get(REMINDER_LOG_FILE));
                for (int i = 0; i < lines.size(); i += 6) {
                    if (i + 5 < lines.size()) {
                        String type = lines.get(i).replace("Reminder Type: ", "").trim();
                        int interval = Integer.parseInt(lines.get(i + 1).replace("Interval: ", "").replace(" minutes", "").trim());
                        reminders.add(new Reminder(type, interval));
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading reminders: " + e.getMessage());
        }
    }

    private void saveReminders() {
        try {
            StringBuilder content = new StringBuilder();
            for (Reminder reminder : reminders) {
                content.append("Reminder Type: ").append(reminder.getType()).append("\n");
                content.append("Interval: ").append(reminder.getIntervalMinutes()).append(" minutes\n");
                content.append("Created At: ").append(reminder.getCreatedAt()).append("\n");
                content.append("Next Reminder: ").append(reminder.getNextReminderTime()).append("\n");
                content.append("---------------\n");
            }
            Files.write(Paths.get(REMINDER_LOG_FILE), content.toString().getBytes());
        } catch (IOException e) {
            System.err.println("Error saving reminders: " + e.getMessage());
        }
    }
} 