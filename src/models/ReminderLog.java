package models;

public class ReminderLog {
    public String reminderType;
    public String timeTriggered;
    public String userResponse; // "ACKNOWLEDGED", "MISSED", "SNOOZED"
    public String notes;

    public ReminderLog(String type, String timeTriggered, String userResponse, String notes) {
        this.reminderType = type;
        this.timeTriggered = timeTriggered;
        this.userResponse = userResponse;
        this.notes = notes;
    }

    @Override
    public String toString() {
        return reminderType + "|" + timeTriggered + "|" + userResponse + "|" + notes;
    }

    public static ReminderLog fromString(String line) {
        String[] parts = line.split("\\|");
        return new ReminderLog(parts[0], parts[1], parts[2], parts.length > 3 ? parts[3] : "");
    }
}