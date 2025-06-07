package models;

import java.time.LocalDateTime;

public class Reminder {
    private String type;
    private int intervalMinutes;
    private LocalDateTime createdAt;
    private LocalDateTime nextReminderTime;

    public Reminder(String type, int intervalMinutes) {
        this.type = type;
        this.intervalMinutes = intervalMinutes;
        this.createdAt = LocalDateTime.now();
        this.nextReminderTime = createdAt.plusMinutes(intervalMinutes);
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

    public void updateNextReminderTime() {
        this.nextReminderTime = LocalDateTime.now().plusMinutes(intervalMinutes);
    }

    @Override
    public String toString() {
        return String.format("Reminder Type: %s\nInterval: %d minutes\nCreated At: %s\nNext Reminder: %s\n---------------",
            type, intervalMinutes, createdAt, nextReminderTime);
    }
}