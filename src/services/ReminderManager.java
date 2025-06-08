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
import java.awt.*;
import models.Reminder;
import models.ReminderLog;
import models.User;
import java.nio.charset.StandardCharsets;

public class ReminderManager {
    private static final Map<String, ReminderManager> instances = new HashMap<>();
    private final User loggedInUser;
    private final ArrayList<Reminder> reminders = new ArrayList<>();
    private Timer timer = new Timer();
    private final NotificationService notificationService;
    private final String reminderFileName;
    private final Map<String, TimerTask> reminderTasks = new HashMap<>(); // Track timer tasks by reminder ID
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[.SSSSSSSSS]");
    private final ReminderService reminderService;

    public static ReminderManager getInstance(User user) {
        return instances.computeIfAbsent(user.getUsername(), k -> new ReminderManager(user));
    }

    private ReminderManager(User user) {
        this.loggedInUser = user;
        this.notificationService = new NotificationService();
        this.reminderFileName = "assets/reminder_" + user.getUsername() + ".txt";
        this.reminderService = new ReminderService();
        loadReminders();
    }

    public void start() {
        startReminderCheck();
    }

    public void stop() {
        if (timer != null) {
            timer.cancel();
        }
        instances.remove(loggedInUser.getUsername());
    }

    private void startReminderCheck() {
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkReminders();
            }
        }, 0, 30000); // Check every 30 seconds (30000 milliseconds)
    }

    public void loadReminders() {
        try {
            Path filePath = Paths.get(reminderFileName);
            if (!Files.exists(filePath)) {
                return;
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(reminderFileName), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.replaceAll("[\uFEFF\u200B]", ""); // Remove BOM and zero-width spaces
                    if (line.startsWith("Reminder ID:")) {
                        String id = line.substring("Reminder ID:".length()).trim();
                        String type = reader.readLine().substring("Type:".length()).trim();
                        String intervalStr = reader.readLine().substring("Interval:".length()).trim();
                        String createdAt = reader.readLine().substring("Created At:".length()).trim();
                        String nextReminder = reader.readLine().substring("Next Reminder:".length()).trim();
                        
                        // Skip the empty line
                        reader.readLine();
                        
                        try {
                            // Clean up the date string by removing duplicate nanoseconds
                            if (nextReminder.contains(".")) {
                                String[] parts = nextReminder.split("\\.");
                                if (parts.length > 2) {
                                    nextReminder = parts[0] + "." + parts[1];
                                }
                            }
                            
                            int intervalMinutes = parseIntervalMinutes(intervalStr);
                            Reminder reminder = new Reminder(type, intervalMinutes);
                            // Set the ID and next reminder time manually since they're not in the constructor
                            try {
                                java.lang.reflect.Field idField = Reminder.class.getDeclaredField("id");
                                idField.setAccessible(true);
                                idField.set(reminder, id);
                            } catch (Exception e) {
                                System.err.println("Failed to set reminder ID: " + e.getMessage());
                            }
                            reminder.setNextReminderTime(LocalDateTime.parse(nextReminder, DATE_TIME_FORMATTER));
                            
                            // Check for duplicates before adding
                            boolean isDuplicate = false;
                            for (Reminder existingReminder : reminders) {
                                if (existingReminder.getId().equals(reminder.getId())) {
                                    isDuplicate = true;
                                    break;
                                }
                            }
                            
                            if (!isDuplicate) {
                                reminders.add(reminder);
                            }
                        } catch (Exception e) {
                            System.err.println("Error parsing reminder: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading reminders: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void checkReminders() {
        LocalDateTime now = LocalDateTime.now();
        boolean needsSave = false;

        for (Reminder reminder : reminders) {
            System.out.println("Checking reminder: " + reminder.getType() + " - Next Reminder: " + reminder.getNextReminderTime());
            if (now.isAfter(reminder.getNextReminderTime()) || now.isEqual(reminder.getNextReminderTime())) {
                System.out.println("Triggering notification for reminder: " + reminder.getType());
                showReminderPopup(reminder);
                needsSave = true;
            }
        }

        if (needsSave) {
            saveReminders();
        }
    }

    private void showReminderPopup(Reminder reminder) {
        String[] options = {"Acknowledge", "Snooze", "Missed"};
        JDialog dialog = new JDialog((Window) null, "Reminder Alert", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        JPanel panel = new JPanel(new BorderLayout());
        StringBuilder message = new StringBuilder("It's time for your " + reminder.getType() + " reminder!");
        if (reminder.getType().equalsIgnoreCase("Medication")) {
            if (reminder.getMedicineType() != null && !reminder.getMedicineType().isEmpty()) {
                message.append("<br>Medicine: ").append(reminder.getMedicineType());
            }
            if (reminder.getDosage() != null && !reminder.getDosage().isEmpty()) {
                message.append("<br>Dosage: ").append(reminder.getDosage());
            }
        }
        JLabel messageLabel = new JLabel("<html>" + message.toString() + "</html>");
        messageLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(messageLabel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel();
        JButton acknowledgeBtn = new JButton("Acknowledge");
        JButton snoozeBtn = new JButton("Snooze");
        JButton missedBtn = new JButton("Missed");
        
        buttonPanel.add(acknowledgeBtn);
        buttonPanel.add(snoozeBtn);
        buttonPanel.add(missedBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        
        // Set up button actions
        acknowledgeBtn.addActionListener(e -> {
            dialog.dispose();
            handleUserResponse(reminder, 0); // Acknowledge
        });
        
        snoozeBtn.addActionListener(e -> {
            dialog.dispose();
            handleUserResponse(reminder, 1); // Snooze
        });
        
        missedBtn.addActionListener(e -> {
            dialog.dispose();
            handleUserResponse(reminder, 2); // Missed
        });
        
        // Create a timer to automatically handle missed notification after 30 seconds
        javax.swing.Timer autoCloseTimer = new javax.swing.Timer(30000, e -> {
            if (dialog.isVisible()) {
                dialog.dispose();
                handleUserResponse(reminder, 2); // Treat as missed
            }
        });
        autoCloseTimer.setRepeats(false);
        
        // Show the dialog and start the timer
        dialog.setVisible(true);
        autoCloseTimer.start();
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
            case 2 -> { // Missed
                response = "Missed";
                updateNextReminder(reminder); // Update the next reminder time even if missed
            }
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
        try {
            Path filePath = Paths.get(reminderFileName);
            Files.createDirectories(filePath.getParent());
            
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(reminderFileName), StandardCharsets.UTF_8))) {
                for (Reminder reminder : reminders) {
                    writer.write("Reminder ID:" + reminder.getId());
                    writer.newLine();
                    writer.write("Type:" + reminder.getType());
                    writer.newLine();
                    writer.write("Interval:" + reminder.getIntervalMinutes() + " minutes");
                    writer.newLine();
                    writer.write("Created At:" + reminder.getCreatedAt());
                    writer.newLine();
                    writer.write("Next Reminder:" + reminder.getNextReminderTime().format(DATE_TIME_FORMATTER));
                    writer.newLine();
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Error saving reminders: " + e.getMessage());
            e.printStackTrace();
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
                ArrayList<String> lines = new ArrayList<>(Files.readAllLines(reminderPath));
                ArrayList<String> newLines = new ArrayList<>();
                boolean skipNext = false;
                int linesToSkip = 0;

                for (int i = 0; i < lines.size(); i++) {
                    String line = lines.get(i);
                    if (line.startsWith("Reminder ID:") && line.substring("Reminder ID:".length()).trim().equals(id.trim())) {
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

                Files.write(reminderPath, newLines);
            }
        } catch (IOException e) {
            System.err.println("Error removing reminder from file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void updateNextReminder(Reminder reminder) {
        // Calculate the new next reminder time based on the last reminder time
        LocalDateTime lastReminderTime = reminder.getNextReminderTime();
        LocalDateTime nextReminderTime = lastReminderTime.plusMinutes(reminder.getIntervalMinutes());
        
        // If the calculated next time is in the past or too close to now, adjust it
        LocalDateTime now = LocalDateTime.now();
        if (nextReminderTime.isBefore(now) || nextReminderTime.isEqual(now)) {
            nextReminderTime = now.plusMinutes(reminder.getIntervalMinutes());
        }
        
        reminder.setNextReminderTime(nextReminderTime);
        
        // Save the updated reminders to the file
        saveReminders();
        
        System.out.println("Updated next reminder for " + reminder.getType() + " to: " + nextReminderTime);
    }

    public ArrayList<Reminder> getReminders() {
        return new ArrayList<>(reminders);
    }
}