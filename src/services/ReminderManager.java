package services;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Timer;
import javax.swing.*;
import models.Reminder;
import models.ReminderLog;
import models.User;
import java.nio.charset.StandardCharsets;

public class ReminderManager {
    private final User loggedInUser;
    private final List<Reminder> reminders = new ArrayList<>();
    private final Timer timer = new Timer();
    private final NotificationService notificationService;
    private final String reminderFileName;
    private final Map<String, TimerTask> reminderTasks = new HashMap<>(); // Track timer tasks by reminder ID
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[.SSSSSSSSS][.SSSSSS]");
    private final ReminderService reminderService;

    public ReminderManager(User user) {
        this.loggedInUser = user;
        this.notificationService = new NotificationService();
        this.reminderFileName = "assets/reminder_" + user.getUsername() + ".txt";
        this.reminderService = new ReminderService();
        loadReminders();
        startReminderCheck();
    }

    private void loadReminders() {
        File file = new File(reminderFileName);

        if (!file.exists()) {
            System.out.println("No reminders found for user: " + loggedInUser.getUsername());
            return;
        }

        try {
            // Read the entire file content first
            String content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
            // Normalize line endings and remove any BOM
            content = content.replace("\r\n", "\n").replace("\r", "\n").replace("\uFEFF", "");
            
            String[] lines = content.split("\n");
            String currentId = null;
            String currentType = null;
            int currentInterval = 0;
            LocalDateTime currentNextReminder = null;

            for (String line : lines) {
                line = line.trim();
                System.out.println("Processing line: [" + line + "]"); // Debug log
                
                if (line.startsWith("ID:")) {
                    currentId = line.substring(3).trim();
                } else if (line.startsWith("Reminder Type:")) {
                    currentType = line.substring(13).trim();
                } else if (line.startsWith("Interval:")) {
                    currentInterval = Integer.parseInt(line.substring(9).trim().split(" ")[0]);
                } else if (line.startsWith("Next Reminder:")) {
                    String dateStr = line.substring(14).trim();
                    System.out.println("Parsing date string: [" + dateStr + "]"); // Debug log
                    try {
                        currentNextReminder = LocalDateTime.parse(dateStr, DATE_TIME_FORMATTER);
                    } catch (Exception e) {
                        System.err.println("Failed to parse date: [" + dateStr + "]");
                        System.err.println("Error: " + e.getMessage());
                        throw e;
                    }
                } else if (line.startsWith("---------------")) {
                    // Check for duplicates before adding
                    final String idToCheck = currentId;
                    boolean exists = reminders.stream().anyMatch(r -> r.getId().equals(idToCheck));
                    if (!exists) {
                        Reminder reminder = new Reminder(currentType, currentInterval);
                        reminder.setNextReminderTime(currentNextReminder);
                        try {
                            java.lang.reflect.Field idField = Reminder.class.getDeclaredField("id");
                            idField.setAccessible(true);
                            idField.set(reminder, currentId);
                        } catch (Exception e) {
                            System.err.println("Failed to set reminder ID: " + e.getMessage());
                        }
                        reminders.add(reminder);
                    }
                    // Reset current reminder fields
                    currentId = null;
                    currentType = null;
                    currentInterval = 0;
                    currentNextReminder = null;
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
            System.out.println("Checking reminder: " + reminder.getType() + " - Next Reminder: " + reminder.getNextReminderTime());
            if (now.isAfter(reminder.getNextReminderTime()) || now.isEqual(reminder.getNextReminderTime())) {
                System.out.println("Triggering notification for reminder: " + reminder.getType());
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
            case 0 -> { // Acknowledge
                response = "Acknowledged";
                updateNextReminder(reminder); // Update the next reminder time
            }
            case 1 -> { // Snooze
                response = "Snoozed";
                snoozeReminder(reminder);
            }
            case 2 -> response = "Missed";
            default -> response = "No Response";
        }

        logReminderResponse(reminder, response);
    }

    private void snoozeReminder(Reminder reminder) {
        // Cancel any existing task for this reminder
        TimerTask existingTask = reminderTasks.remove(reminder.getId());
        if (existingTask != null) {
            existingTask.cancel();
        }

        // Schedule a new snooze task
        TimerTask snoozeTask = new TimerTask() {
            @Override
            public void run() {
                showReminderPopup(reminder);
            }
        };
        timer.schedule(snoozeTask, 5 * 60 * 1000); // Snooze for 5 minutes
        reminderTasks.put(reminder.getId(), snoozeTask); // Track the snooze task
    }

    public void saveReminders() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(reminderFileName))) {
            for (Reminder reminder : reminders) {
                writer.write("ID: " + reminder.getId() + "\n");
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
        // Check for duplicates
        for (Reminder existingReminder : reminders) {
            if (existingReminder.getType().equalsIgnoreCase(reminder.getType()) &&
                existingReminder.getIntervalMinutes() == reminder.getIntervalMinutes()) {
                throw new IllegalStateException("A reminder with this type and interval already exists.");
            }
        }

        // Add the reminder and save
        reminders.add(reminder);
        saveReminders();
    }

    /**
     * Deletes a reminder by its unique ID.
     * @param id The unique ID of the reminder to delete
     * @throws IllegalArgumentException if id is null or empty
     * @throws IllegalStateException if no reminder with the given ID is found
     */
    public void deleteReminderById(String id) {
        // Input validation
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Reminder ID cannot be null or empty");
        }

        // Cancel any existing timer tasks for this reminder
        TimerTask task = reminderTasks.remove(id);
        if (task != null) {
            task.cancel();
        }

        // Find the reminder to get its type before removing
        Reminder reminderToDelete = reminders.stream()
            .filter(r -> r.getId().equals(id.trim()))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("No reminder found with ID: " + id));

        // Remove from memory in ReminderManager
        boolean removed = reminders.removeIf(r -> r.getId().equals(id.trim()));

        if (!removed) {
            throw new IllegalStateException("No reminder found with ID: " + id);
        }

        // Remove from ReminderService
        reminderService.getReminders().removeIf(r -> 
            r.getType().equals(reminderToDelete.getType()) && 
            r.getIntervalMinutes() == reminderToDelete.getIntervalMinutes()
        );

        // Remove from file
        try {
            Path reminderPath = Paths.get(reminderFileName);
            if (Files.exists(reminderPath)) {
                List<String> lines = Files.readAllLines(reminderPath);
                List<String> newLines = new ArrayList<>();
                boolean skipNext = false;
                int linesToSkip = 0;

                for (int i = 0; i < lines.size(); i++) {
                    String line = lines.get(i);
                    if (line.startsWith("ID:") && line.substring("ID:".length()).trim().equals(id.trim())) {
                        skipNext = true;
                        linesToSkip = 6; // Skip ID, type, interval, created, next, separator
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
                Files.write(reminderPath, newLines, StandardOpenOption.TRUNCATE_EXISTING);
            }

            // Log the deletion
            logReminderResponse(reminderToDelete, "DELETED_BY_ID");
        } catch (IOException e) {
            // Restore the reminder in memory if file operation fails
            throw new RuntimeException("Failed to delete reminder from file: " + e.getMessage(), e);
        }
    }

    public void updateNextReminder(Reminder reminder) {
        // Calculate the new next reminder time
        LocalDateTime nextReminderTime = reminder.getNextReminderTime().plusMinutes(reminder.getIntervalMinutes());
        reminder.setNextReminderTime(nextReminderTime);

        // Save the updated reminders to the file
        saveReminders();

        System.out.println("Updated next reminder for " + reminder.getType() + " to: " + nextReminderTime);
    }

    public List<Reminder> getReminders() {
        return reminders;
    }
}