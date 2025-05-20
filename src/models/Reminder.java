package models;

public class Reminder {
    private String type;
    private String time;
    private String frequency;
    private boolean acknowledged;
    private boolean missed;

    public Reminder(String type, String time, String frequency) {
        this.type = type;
        this.time = time;
        this.frequency = frequency;
        this.acknowledged = false;
        this.missed = false;
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

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public boolean isAcknowledged() {
        return acknowledged;
    }

    public void setAcknowledged(boolean acknowledged) {
        this.acknowledged = acknowledged;
    }

    public boolean isMissed() {
        return missed;
    }

    public void setMissed(boolean missed) {
        this.missed = missed;
    }
}