package services;

import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.Toolkit;
import java.awt.PopupMenu;
import java.awt.MenuItem;
import java.awt.AWTException;

public class NotificationService {
    private TrayIcon trayIcon;

    public NotificationService() {
        if (SystemTray.isSupported()) {
            try {
                SystemTray tray = SystemTray.getSystemTray();
                PopupMenu popup = new PopupMenu();
                trayIcon = new TrayIcon(Toolkit.getDefaultToolkit().createImage(new byte[0]), "Wellness Reminder", popup);
                trayIcon.setImageAutoSize(true);
                tray.add(trayIcon);
            } catch (AWTException e) {
                System.err.println("Error initializing system tray: " + e.getMessage());
            }
        }
    }

    public void showNotification(String title, String message) {
        if (trayIcon != null) {
            trayIcon.displayMessage(title, message, TrayIcon.MessageType.INFO);
        } else {
            // Fallback to JOptionPane if system tray is not available
            javax.swing.JOptionPane.showMessageDialog(null, message, title, javax.swing.JOptionPane.INFORMATION_MESSAGE);
        }
    }
} 