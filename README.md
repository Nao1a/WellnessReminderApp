# Wellness Reminder Application

## Overview
The Wellness Reminder Application is designed to help users manage their health and wellness through reminders and recommendations. It features a user-friendly GUI for both users and doctors, allowing for easy interaction and management of health-related tasks.

## Features
- User and Doctor Dashboards
- Reminder Setup for various health activities (medication, hydration, movement, sleep, etc.)
- Logging of reminder responses (acknowledged, missed, snoozed)
- Data persistence for user data, reminders, and recommendations
- User authentication system

## Project Structure
```
WellnessReminderApp
├── src
│   ├── models
│   │   ├── User.java
│   │   ├── Reminder.java
│   │   └── Recommendation.java
│   ├── gui
│   │   ├── LoginScreen.java
│   │   ├── UserDashboard.java
│   │   ├── DoctorDashboard.java
│   │   ├── ReminderSetupScreen.java
│   │   └── ResponseLoggerScreen.java
│   ├── persistence
│   │   ├── DataStore.java
│   │   └── FileManager.java
│   ├── Main.java
│   └── utils
│       └── Utils.java
├── README.md
```

## Setup Instructions
1. Clone the repository to your local machine.
2. Open the project in your preferred Java IDE.
3. Ensure you have Java Development Kit (JDK) installed.
4. Build the project to resolve dependencies.
5. Run the `Main.java` file to start the application.

## Usage Guidelines
- **Login**: Enter your username and password to access the appropriate dashboard.
- **User Dashboard**: Set reminders for various health activities and view past reminder logs.
- **Doctor Dashboard**: Select a user to view their logs and add recommendations.
- **Reminder Setup**: Input details for reminders, including type and frequency.
- **Response Logging**: Log your responses to reminders to track your health activities.

## Contribution
Contributions are welcome! Please fork the repository and submit a pull request for any enhancements or bug fixes.

## License
This project is licensed under the MIT License. See the LICENSE file for more details.