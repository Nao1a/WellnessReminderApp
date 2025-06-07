package services;

import java.io.*;
import models.User;

public class AuthService {
    public static User login(String username , String password){
        try (BufferedReader br = new BufferedReader(new FileReader("assets/users.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 3) {
                    String storedUsername = parts[0];
                    String storedPassword = parts[1];
                    String role = parts[2];

                    if (storedUsername.equals(username) && storedPassword.equals(password)) {
                        return new User(storedUsername, storedPassword, role);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading user file: " + e.getMessage());
        }
    return null;
    }

}