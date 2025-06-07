package services;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import models.ReminderLog;
import models.User;

public class ReminderLogger {

    public static void log(User user, ReminderLog log) {
        String filename = "assets/logs_" + user.getUsername() + ".txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            writer.write(log.toString());
            writer.newLine();
            System.out.println("Reminder interaction logged: " + log.toString());
        } catch (IOException e) {
            System.err.println("Failed to log reminder interaction: " + e.getMessage());
            e.printStackTrace(); // Debugging
        }
    }
}