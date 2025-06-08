package services;

import models.Reminder;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ReminderChecker {
    private final ReminderManager reminderManager;
    private final NotificationService notificationService;
    private Timer timer;

    public ReminderChecker(ReminderManager reminderManager, NotificationService notificationService) {
        this.reminderManager = reminderManager;
        this.notificationService = notificationService;
    }

    public void startChecking() {
        timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkReminders();
            }
        }, 0, 60000); // Check every minute
    }

    public void stopChecking() {
        if (timer != null) {
            timer.cancel();
        }
    }

    private void checkReminders() {
        List<Reminder> reminders = reminderManager.getReminders(); // Use ReminderManager
        LocalDateTime now = LocalDateTime.now();

        for (Reminder reminder : reminders) {
            if (now.isAfter(reminder.getNextReminderTime()) || now.isEqual(reminder.getNextReminderTime())) {
                notificationService.showNotification(
                    reminder.getType(),
                    "Time for your " + reminder.getType() + " reminder!"
                );
                reminder.updateNextReminderTime();
                reminderManager.saveReminders(); // Save updated reminders
            }
        }
    }
}