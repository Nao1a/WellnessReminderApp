package screens;

import models.User;

import javax.swing.*;
import java.awt.*;

public class UserDashboard extends JFrame {
    private User loggedInUser;

    public UserDashboard(User user) {
        this.loggedInUser = user;

        setTitle("wellness App - User Dashboard" + user.getUsername());
        setSize(500, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(8,1,10,10)); // 6 reminders + 1 log + title

        JLabel title = new JLabel("Welcome to your Dashboard, " + user.getUsername(), SwingConstants.CENTER);
        title.setFont(new Font ("Arial", Font.BOLD , 24));
        add(title);

        add(createReminderButton("💊 Medication Reminder"));
        add(createReminderButton("🚰 Hydration Reminder"));
        add(createReminderButton("🏃 Movement Reminder"));
        add(createReminderButton("😴 Sleep Reminder"));
        add(createReminderButton("👀 Eye Strain Break"));
        add(createReminderButton("🍽️ Meal Reminder"));
        
        JButton viewLogBtn = new JButton(" View Reminder Log");
        viewLogBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Feature coming soon!");
        });
        add(viewLogBtn);

        setVisible(true);
    }
     private JButton createReminderButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.PLAIN, 18));
        btn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, text + " setup screen coming soon!");
            // You’ll later connect this to ReminderSetupScreen
        });
        return btn;
    }
}