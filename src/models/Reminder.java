package models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Reminder {
    private String type;
    private int intervalMinutes;
    private LocalDateTime createdAt;
    private LocalDateTime nextReminderTime;
    private String id; // Unique ID based on creation time

    public Reminder(String type, int intervalMinutes) {
        this.type = type;
        this.intervalMinutes = intervalMinutes;
        this.createdAt = LocalDateTime.now();
        this.nextReminderTime = createdAt.plusMinutes(intervalMinutes);
        // Generate ID using timestamp format: yyyyMMddHHmmss
        this.id = createdAt.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }

    // Getters and setters
    public String getType() {
        return type;
    }

    public int getIntervalMinutes() {
        return intervalMinutes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getNextReminderTime() {
        return nextReminderTime;
    }

    public String getId() {
        return id;
    }

    public void updateNextReminderTime() {
        this.nextReminderTime = LocalDateTime.now().plusMinutes(intervalMinutes);
    }

    @Override
    public String toString() {
        return String.format("ID: %s\nReminder Type: %s\nInterval: %d minutes\nCreated At: %s\nNext Reminder: %s\n---------------",
            id, type, intervalMinutes, createdAt, nextReminderTime);
    }
}