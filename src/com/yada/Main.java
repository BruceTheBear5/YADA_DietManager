package com.yada;

import com.yada.models.*;
import com.yada.services.*;
import com.yada.utils.InputHelper;

import java.net.SocketOption;
import java.util.List;

public class Main {
    private static FoodDatabase foodDatabase = new FoodDatabase();
    private static LogManager logManager = new LogManager();
    private static UndoManager undoManager = new UndoManager();
    private static UserProfile userProfile;
    // Default to Method One. User can switch later.
    private static DietGoalCalculator dietGoalCalculator = new MethodOneCalculator();

    public static void main(String[] args) {
        // Load foods and logs from files
        foodDatabase.loadFoods();
        logManager.loadLogs(foodDatabase);

        System.out.println("Welcome to YADA - Yet Another Diet Assistant (CLI Version)");

        boolean running = true;
        while (running) {
            printMenu();
            String choice = InputHelper.readLine("Enter choice: ");
            switch (choice) {
                case "1":
                    addBasicFood();
                    break;
                case "2":
                    addCompositeFood();
                    break;
                case "3":
                    removeFood();
                    break;
                case "4":
                    foodDatabase.listBasicFoods();
                    foodDatabase.listCompositeFoods();
                    break;
                case "5":
                    addLogEntry();
                    break;
                case "6":
                    deleteLogEntry();
                    break;
                case "7":
                    viewDailyLog();
                    break;
                case "8":
                    setUserProfile();
                    break;
                case "9":
                    computeDietGoals();
                    break;
                case "10":
                    undoManager.undo();
                    break;
                case "11":
                    foodDatabase.saveFoods();
                    System.out.println("Food data Saved.");
                    break;
                case "12":
                    logManager.saveLogs();
                    System.out.println("Log data Saved.");
                    break;
                case "13":
                    foodDatabase.saveFoods();
                    logManager.saveLogs();
                    System.out.println("Data saved. Exiting application.");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void printMenu() {
        System.out.println("\nMenu:");
        System.out.println("1. Add Basic Food");
        System.out.println("2. Add Composite Food");
        System.out.println("3. Remove Food");
        System.out.println("4. List All Foods");
        System.out.println("5. Add Log Entry");
        System.out.println("6. Delete Log Entry");
        System.out.println("7. View Daily Log");
        System.out.println("8. Set/Update User Profile");
        System.out.println("9. Compute Diet Goals");
        System.out.println("10. Undo Last Action");
        System.out.println("11. Save Food Data");
        System.out.println("12. Save Log Data");
        System.out.println("13. Save and Exit");
    }

    private static void addBasicFood() {
        String id = InputHelper.readLine("Enter food id: ");
        List<Food> results = foodDatabase.searchFoods(id);
        if(!results.isEmpty()){
            System.out.println("Food already exists.");
            return;
        }
        int calories = InputHelper.readInt("Enter calories per serving: ");
        String kwInput = InputHelper.readLine("Enter keywords (comma-separated): ");
        Food food = new Food(id, List.of(kwInput.split("\\s*,\\s*")), calories);
        foodDatabase.addBasicFood(food);
        System.out.println("Basic food added: " + food);
        // (For undo, one could add a command that removes this food if needed)
    }

    private static void addCompositeFood() {
        String id = InputHelper.readLine("Enter composite food id: ");
        String kwInput = InputHelper.readLine("Enter keywords (comma-separated): ");
        CompositeFood compositeFood = new CompositeFood(id, List.of(kwInput.split("\\s*,\\s*")));
        System.out.println("Adding components to composite food. Type 'done' when finished.");
        while (true) {
            String compId = InputHelper.readLine("Enter component food id (or 'done'): ");
            if (compId.equalsIgnoreCase("done")) break;
            // Find the food from the database
            List<Food> results = foodDatabase.searchFoods(compId);
            if (results.isEmpty()) {
                System.out.println("Food not found.");
                continue;
            }
            Food compFood = results.get(0); // assume the first match
            int servings = InputHelper.readInt("Enter number of servings for " + compFood.getId() + ": ");
            compositeFood.addComponent(compFood, servings);
        }
        foodDatabase.addCompositeFood(compositeFood);
        System.out.println("Composite food added: " + compositeFood);
    }

    private static void removeFood() {
        String id = InputHelper.readLine("Enter food id: ");
        List<Food> results = foodDatabase.searchFoods(id);
        if(results.isEmpty()){
            System.out.println("Food doesn't exist.");
            return;
        }
        foodDatabase.removeFood(results.get(0));
        System.out.println("Food '" + id + "' removed.");
    }

    private static void addLogEntry() {
        String date = InputHelper.readLine("Enter date (YYYY-MM-DD): ");
        String foodId = InputHelper.readLine("Enter food id to log: ");
        List<Food> results = foodDatabase.searchFoods(foodId);
        if (results.isEmpty()) {
            System.out.println("Food not found.");
            return;
        }
        Food food = results.get(0);
        int servings = InputHelper.readInt("Enter number of servings: ");
        LogEntry entry = new LogEntry(food, servings);
        logManager.addLogEntry(date, entry);
        System.out.println("Log entry added: " + entry);

        // Example undo command for adding log entry.
        // In a complete implementation, you’d store more details.
        undoManager.addCommand(() -> {
            logManager.deleteLogEntry(date, logManager.getLogEntries(date).size() - 1);
            System.out.println("Undid log entry addition for " + date);
        });
    }

    private static void deleteLogEntry() {
        String date = InputHelper.readLine("Enter date (YYYY-MM-DD): ");
        List<LogEntry> entries = logManager.getLogEntries(date);
        if (entries.isEmpty()) {
            System.out.println("No entries for that date.");
            return;
        }
        for (int i = 0; i < entries.size(); i++) {
            System.out.println((i + 1) + ". " + entries.get(i));
        }
        int index = InputHelper.readInt("Enter entry number to delete: ") - 1;
        if (index < 0 || index >= entries.size()) {
            System.out.println("Invalid entry number.");
            return;
        }
        LogEntry removed = entries.get(index);
        logManager.deleteLogEntry(date, index);
        System.out.println("Deleted entry: " + removed);

        // Add an undo command
        undoManager.addCommand(() -> {
            logManager.addLogEntry(date, removed);
            System.out.println("Undid deletion of log entry for " + date);
        });
    }

    private static void viewDailyLog() {
        String date = InputHelper.readLine("Enter date (YYYY-MM-DD): ");
        List<LogEntry> entries = logManager.getLogEntries(date);
        if (entries.isEmpty()) {
            System.out.println("No log entries for this date.");
        } else {
            System.out.println("Log for " + date + ":");
            int totalCalories = 0;
            for (LogEntry entry : entries) {
                System.out.println(" - " + entry);
                totalCalories += entry.getTotalCalories();
            }
            System.out.println("Total calories consumed: " + totalCalories);
        }
    }

    private static void setUserProfile() {
        String gender = InputHelper.readLine("Enter gender (male/female): ");
        if(!gender.equalsIgnoreCase("male") && !gender.equalsIgnoreCase("female")){
            System.out.println("Error: YADA only supports binaries. :P");
        }
        double height = InputHelper.readDouble("Enter height (in centimeters): ");
        int age = InputHelper.readInt("Enter age: ");
        double weight = InputHelper.readDouble("Enter weight (in kg): ");
        double activityLevel = InputHelper.readDouble("Enter activity level multiplier (e.g., 1.2, 1.55): ");
        userProfile = new UserProfile(gender, height, age, weight, activityLevel);
        System.out.println("User profile set: " + userProfile);

        // Optionally, let user choose the diet goal calculation method.
        String methodChoice = InputHelper.readLine("Choose diet goal calculation method (1 or 2): ");
        if (methodChoice.equals("2")) {
            dietGoalCalculator = new MethodTwoCalculator();
        } else {
            dietGoalCalculator = new MethodOneCalculator();
        }
    }

    private static void computeDietGoals() {
        if (userProfile == null) {
            System.out.println("User profile not set. Please set the user profile first.");
            return;
        }
        String date = InputHelper.readLine("Enter date (YYYY-MM-DD) to view log: ");
        List<LogEntry> entries = logManager.getLogEntries(date);
        int totalCalories = entries.stream().mapToInt(LogEntry::getTotalCalories).sum();
        double targetCalories = dietGoalCalculator.calculateTargetCalories(userProfile);
        double difference = totalCalories - targetCalories;
        System.out.println("For date " + date + ":");
        System.out.println("Total calories consumed: " + totalCalories);
        System.out.println("Target calorie intake: " + targetCalories);
        System.out.println("Difference (excess if positive, available if negative): " + difference);
    }
}
