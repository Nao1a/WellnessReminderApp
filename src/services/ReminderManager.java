package services;

import java.io.*;
import java.text.SimpleDateFormat;
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
    private final String assetsDir = "src/assets"; // Directory for storing reminder files

    public ReminderManager(User user) {
        this.loggedInUser = user;
        ensureAssetsDirectoryExists();
        loadReminders();
        startReminderCheck();
    }

    // Ensure the assets directory exists
    private void ensureAssetsDirectoryExists() {
        File dir = new File(assetsDir);
        if (!dir.exists()) {
            if (dir.mkdir()) {
                System.out.println("Assets directory created.");
            } else {
                System.err.println("Failed to create assets directory.");
            }
        }
    }

    // Load reminders from the user's reminder file
    private void loadReminders() {
        String filename = assetsDir + "/reminder_" + loggedInUser.getUsername() + ".txt";
        File file = new File(filename);

        if (!file.exists()) {
            System.out.println("No reminders found for user: " + loggedInUser.getUsername());
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Reminder Type:")) {
                    String type = line.split(":")[1].trim();
                    String timeOrInterval = reader.readLine().split(":")[1].trim();
                    reminders.add(new Reminder(type, timeOrInterval, 0, false, ""));
                }
                reader.readLine(); // Skip separator line
            }
        } catch (IOException e) {
            System.err.println("Failed to load reminders: " + e.getMessage());
        }
    }

    // Start a timer to check reminders periodically
    private void startReminderCheck() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkReminders();
            }
        }, 0, 60 * 1000); // Check every minute
    }

    // Check if any reminders are due
    private void checkReminders() {
        String currentTime = new SimpleDateFormat("hh:mm a").format(new Date());

        for (Reminder reminder : reminders) {
            if (reminder.getTime().equalsIgnoreCase(currentTime)) {
                showReminderPopup(reminder);
            }
        }
    }

    // Show a popup for the due reminder
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

    // Handle the user's response to the reminder
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

    // Snooze the reminder for 5 minutes
    private void snoozeReminder(Reminder reminder) {
        TimerTask snoozeTask = new TimerTask() {
            @Override
            public void run() {
                showReminderPopup(reminder);
            }
        };
        timer.schedule(snoozeTask, 5 * 60 * 1000); // Snooze for 5 minutes
    }

    // Log the user's response to a log file
    private void logReminderResponse(Reminder reminder, String response) {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        ReminderLog log = new ReminderLog(reminder.getType(), timestamp, response, "User interacted with reminder.");

        ReminderLogger.log(loggedInUser, log);
    }

    // Stop the reminder manager
    public void stop() {
        timer.cancel();
    }
}