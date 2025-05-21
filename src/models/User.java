package models;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String username; // Changed from public static
    private String password;
    public String role; // Consider making this private with a getter too

    private List<Reminder> reminders;
    private List<Recommendation> recommendations;

    public User(String username, String password, String role) {
        this.username = username; // Now correctly assigns to the instance field
        this.password = password;
        this.role = role;
        this.reminders = new ArrayList<>();
        this.recommendations = new ArrayList<>();
    }

    public String getUsername() { // Getter for the instance field
        return this.username;
    }
    public String getPassword() {
        return password;
    }
    public String getRole() {
        return role;
    }
    public List<Reminder> getReminders() {
        return new ArrayList<>(reminders);
    }
    public List<Recommendation> getRecommendations() {
        return new ArrayList<>(recommendations);
    }

    public void addReminder(Reminder reminder) {
        reminders.add(reminder);
    }
    public void addRecommendation(Recommendation recommendation) {
        recommendations.add(recommendation);
    }

    @Override
    public String toString() {
        return "User{" +
            "username='" + username + '\'' +
            ", role='" + role + '\'' +
            ", reminders=" + reminders.size() +
            ", recommendations=" + recommendations.size() +
            '}';
    }
}