package services;

import models.Reminder;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ReminderChecker {
    private final ReminderService reminderService;
    private final NotificationService notificationService;
    private Timer timer;

    public ReminderChecker(ReminderService reminderService, NotificationService notificationService) {
        this.reminderService = reminderService;
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
        List<Reminder> reminders = reminderService.getReminders();
        LocalDateTime now = LocalDateTime.now();

        for (Reminder reminder : reminders) {
            if (now.isAfter(reminder.getNextReminderTime()) || now.isEqual(reminder.getNextReminderTime())) {
                notificationService.showNotification(
                    reminder.getType(),
                    "Time for your " + reminder.getType() + " reminder!"
                );
                reminder.updateNextReminderTime();
            }
        }
    }
} 