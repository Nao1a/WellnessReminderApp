package models;

public class Reminder {
    private String type;
    private String time;
    private int frequency;
    private boolean acknowledged;
    private String notes;

    public Reminder(String type, String time, int frequency , boolean acknowledged, String notes) {
        this.type = type;
        this.time = time;
        this.frequency = frequency;
        this.acknowledged = false;
        this.notes = notes;
    }

    // Getters and Setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public boolean isAcknowledged() {
        return acknowledged;
    }

    public void setAcknowledged(boolean acknowledged) {
        this.acknowledged = acknowledged;
    }
}