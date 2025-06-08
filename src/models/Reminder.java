package models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Reminder {
    private String type;
    private int intervalMinutes;
    private LocalDateTime createdAt;
    private LocalDateTime nextReminderTime;
    private String id; // Unique ID based on creation time
    private String medicineType; // For medication reminders
    private String dosage; // For medication reminders

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

    public void setNextReminderTime(LocalDateTime nextReminderTime) {
        this.nextReminderTime = nextReminderTime;
    }

    public String getId() {
        return id;
    }

    public void updateNextReminderTime() {
        this.nextReminderTime = LocalDateTime.now().plusMinutes(intervalMinutes);
    }

    public void setMedicineType(String medicineType) {
        this.medicineType = medicineType;
    }

    public String getMedicineType() {
        return medicineType;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getDosage() {
        return dosage;
    }

    @Override
    public String toString() {
        String base = String.format("ID: %s\nReminder Type: %s\nInterval: %d minutes\nCreated At: %s\nNext Reminder: %s",
            id, type, intervalMinutes, createdAt, nextReminderTime);
        if (medicineType != null && !medicineType.isEmpty()) {
            base += String.format("\nMedicine Type: %s", medicineType);
        }
        if (dosage != null && !dosage.isEmpty()) {
            base += String.format("\nDosage: %s", dosage);
        }
        base += "\n---------------";
        return base;
    }
}