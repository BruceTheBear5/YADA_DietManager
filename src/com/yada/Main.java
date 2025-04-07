package com.yada;

import com.yada.models.*;
import com.yada.services.*;
import com.yada.utils.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;

public class Main {
    private static FoodDatabase foodDatabase = new FoodDatabase();
    private static LogManager logManager = new LogManager();
    private static UndoManager undoManager = new UndoManager();
    private static UserProfile userProfile;
    // Default to Method One. User can switch later.
    private static DietGoalCalculator dietGoalCalculator = new MethodOneCalculator();
    private static String currentDate = LocalDate.now().toString();

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
                    updateUserProfile();
                    break;
                case "10":
                    computeDietGoals();
                    break;
                case "11":
                    undoManager.undo();
                    break;
                case "12":
                    changeDay();
                    break;
                case "13":
                    System.out.println("\nSave Menu:");
                    System.out.println("1. Save Food Data");
                    System.out.println("2. Save Log Data");
                    System.out.println("3. Save and Exit");
                    System.out.println("Anything else: Return");
                    String saveChoice = InputHelper.readLine("Enter choice: ");
                    switch (saveChoice) {
                        case "1":
                            foodDatabase.saveFoods();
                            System.out.println("Food data Saved.");
                            break;
                        case "2":
                            logManager.saveLogs();
                            System.out.println("Log data Saved.");
                            break;
                        case "3":
                            foodDatabase.saveFoods();
                            logManager.saveLogs();
                            System.out.println("Data saved. Exiting application.");
                            running = false;
                            break;
                        default:
                            break;
                    }
                    break;
                case "14":
                    loadFoodFromExternalSource();
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
        System.out.println("8. Set User Profile");
        System.out.println("9. Update User Profile");
        System.out.println("10. Compute Diet Goals");
        System.out.println("11. Undo Last Action");
        System.out.println("12. Change Day");
        System.out.println("13. Save or Exit");
        System.out.println("14. Load food from External Source");
    }

    private static void addBasicFood() {
        String id = InputHelper.readLine("Enter food id: ");
        if(foodDatabase.findFoodById(id) != null){
            System.out.println("Food already exists.");
            return;
        }
        if(!Validator.isValidId(id)){
            System.out.println("Invalid ID. Use only letters, numbers, underscores and spaces.");
            return;
        }
        int calories = InputHelper.readInt("Enter calories(kcal) per serving: ");
        if(!Validator.isValidCalorieValue(calories)){
            System.out.println("Invalid Calories. Must be positive.");
            return;
        }
        List<String> keywords = List.of(InputHelper.readLine("Enter keywords (comma-separated): ").split("\\s*,\\s*"));
        if (!Validator.isValidKeywordList(keywords)) {
            System.out.println("Invalid keywords. Use only letters, numbers, underscores and spaces. No more than 10.");
            return;
        }
        Food food = new Food(id, keywords, calories);
        foodDatabase.addBasicFood(food);
        System.out.println("Basic food added: " + food);
        // (For undo, one could add a command that removes this food if needed)
    }

    private static void addCompositeFood() {
        String id = InputHelper.readLine("Enter composite food id: ");
        if(foodDatabase.findFoodById(id) != null){
            System.out.println("Food already exists.");
            return;
        }
        if(!Validator.isValidId(id)){
            System.out.println("Invalid ID. Use only letters, numbers, underscores and spaces.");
            return;
        }
        List<String> keywords = List.of(InputHelper.readLine("Enter keywords (comma-separated): ").split("\\s*,\\s*"));
        if (!Validator.isValidKeywordList(keywords)) {
            System.out.println("Invalid keywords. Use only letters, numbers, underscores and spaces. No more than 10.");
            return;
        }
        CompositeFood compositeFood = new CompositeFood(id, keywords);
        System.out.println("Adding components to composite food. Press enter when finished.");
        while (true) {
            String compId = InputHelper.readLine("Enter component food id (or nothing to stop): ");
            if (compId.isBlank()) break;
            // Find the food from the database
            Food compFood = foodDatabase.findFoodById(compId);
            if (compFood == null) {
                System.out.println("Food not found.");
                continue;
            }
            int servings = InputHelper.readInt("Enter number of servings for " + compFood.getId() + ": ");
            if (!Validator.isValidServings(servings)){
                System.out.println("Invalid servings. Must be positive.");
                continue;
            }
            compositeFood.addComponent(compFood, servings);
        }
        if (compositeFood.getComponents().isEmpty()) {
            System.out.println("Composite food must have at least one component.");
            return;
        }
        foodDatabase.addCompositeFood(compositeFood);
        System.out.println("Composite food added: " + compositeFood);
    }

    private static void removeFood() {
        String id = InputHelper.readLine("Enter food id: ");
        Food food = foodDatabase.findFoodById(id);
        if (food == null) {
            System.out.println("Food not found.");
            return;
        }
        foodDatabase.removeFood(food);
        System.out.println("Food '" + id + "' removed.");
    }

    private static void addLogEntry() {
        String dateInput = InputHelper.readLine("Enter date (YYYY-MM-DD) [default/invalid: today]: ");
        String date = (dateInput.isBlank() || !Validator.isValidDate(dateInput)) ? currentDate : dateInput;

        System.out.println("\nChoose how to add food:");
        System.out.println("1. By food ID");
        System.out.println("2. Search by keywords");
        String choice = InputHelper.readLine("Enter choice: ");

        Food food;

        if (choice.equals("1")) {
            String foodId = InputHelper.readLine("\nEnter food id to log: ");
            food = foodDatabase.findFoodById(foodId);
            if (food == null) {
                System.out.println("Food not found.");
                return;
            }
        } else if (choice.equals("2")) {
            String kwInput = InputHelper.readLine("\nEnter keywords (comma-separated): ");
            List<String> keywords = List.of(kwInput.split("\\s*,\\s*"));

            String matchMode = InputHelper.readLine("Match all keywords? (y/n): ");
            boolean matchAll = matchMode.equalsIgnoreCase("y");

            List<Food> results = foodDatabase.searchFoods(keywords, matchAll);

            if (results.isEmpty()) {
                System.out.println("No matching foods found.");
                return;
            }

            System.out.println("Matching foods:");
            for (int i = 0; i < results.size(); i++) {
                System.out.println((i + 1) + ". " + results.get(i));
            }

            int selectedIndex = InputHelper.readInt("Enter the number of the food to log (0 for N.A.):");
            if (selectedIndex < 0 || selectedIndex > results.size()) {
                System.out.println("Invalid selection.");
                return;
            }

            if(selectedIndex == 0)
                return;
            food = results.get(selectedIndex - 1);
        } else {
            System.out.println("Invalid choice.");
            return;
        }

        int servings = InputHelper.readInt("Enter number of servings: ");
        if(!Validator.isValidServings(servings)){
            System.out.println("Invalid number of servings. Defaulting to 1.");
            servings = 1;
        }
        LogEntry entry = new LogEntry(food, servings);
        logManager.addLogEntry(date, entry);
        System.out.println("Log entry added: " + entry);

        int indexAdded = logManager.getLogEntries(date).size() - 1;
        undoManager.addCommand(() -> {
            logManager.deleteLogEntry(date, indexAdded);
            System.out.println("Undid log entry addition for " + date);
        });
    }

    private static void deleteLogEntry() {
        String dateInput = InputHelper.readLine("Enter date (YYYY-MM-DD) [default/invalid: today]: ");
        String date = (dateInput.isBlank() || !Validator.isValidDate(dateInput)) ? currentDate : dateInput;

        List<LogEntry> entries = logManager.getLogEntries(date);
        if (entries.isEmpty()) {
            System.out.println("No log entry found.");
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
        String dateInput = InputHelper.readLine("Enter date (YYYY-MM-DD) [default/invalid: today]: ");
        String date = (dateInput.isBlank() || !Validator.isValidDate(dateInput)) ? currentDate : dateInput;

        List<LogEntry> entries = logManager.getLogEntries(date);
        if (entries.isEmpty()) {
            System.out.println("No log entry found.");
        } else {
            System.out.println("\n========================================");
            System.out.println("📅 Log for " + date);
            System.out.println("----------------------------------------");
            System.out.printf("%-20s %-10s %-10s%n", "Food", "Servings", "Calories");

            int totalCalories = 0;
            for (LogEntry entry : entries) {
                String foodName = entry.getFood().getId();
                int servings = entry.getServings();
                int calories = entry.getTotalCalories();
                totalCalories += calories;
                System.out.printf("%-20s %-10d %-10d%n", foodName, servings, calories);
            }

            System.out.println("----------------------------------------");
            System.out.printf("%-20s %-10s %-10d%n", "Total", "", totalCalories);
            System.out.println("========================================\n");
        }
    }

    private static void setUserProfile() {
        if (userProfile != null) {
            System.out.println("User profile already set.");
            return;
        }
        String gender = InputHelper.readLine("Enter gender (male/female): ");
        if(!gender.equalsIgnoreCase("male") && !gender.equalsIgnoreCase("female")){
            System.out.println("YADA only supports binaries.");
            return;
        }
        double height = InputHelper.readDouble("Enter height (in centimeters): ");
        int age = InputHelper.readInt("Enter age: ");
        double weight = InputHelper.readDouble("Enter weight (in kg): ");
        double activityLevel = InputHelper.readDouble("Enter activity level multiplier (e.g., 1.2, 1.55): ");
        while(!Validator.isValidActivityLevel(activityLevel)){
            System.out.println("Activity level must lie between 1.2 and 2.5. Try again.");
            activityLevel = InputHelper.readDouble("Enter activity level multiplier (e.g., 1.2, 1.55): ");
        }

        userProfile = new UserProfile(gender.toLowerCase(Locale.ROOT), height, age, weight, activityLevel);
        System.out.println("User profile set: " + userProfile);

        // Let user choose the diet goal calculation method.
        String methodChoice = InputHelper.readLine("Choose diet goal calculation method (1.Harris-Benedict or 2.Mifflin-St Jeor): ");
        if (methodChoice.equals("2")) {
            dietGoalCalculator = new MethodTwoCalculator();
        } else if (methodChoice.equals("1")){
            dietGoalCalculator = new MethodOneCalculator();
        } else{
            System.out.println("Invalid Choice.");
        }
    }

    private static void updateUserProfile(){
        if (userProfile == null) {
            System.out.println("User profile not set. Please set the user profile first.");
            return;
        }
        System.out.println("\nProfile Update Menu:");
        System.out.println("1. Change age");
        System.out.println("2. Change weight");
        System.out.println("3. Change activity level");
        System.out.println("4. Change diet goal calculation method");
        System.out.println("Anything else: Return");
        String choice = InputHelper.readLine("Enter choice: ");
        switch (choice) {
            case "1":
                int age = InputHelper.readInt("Enter age: ");
                userProfile.setAge(age);
                System.out.println("Age set.");
                break;
            case "2":
                double weight = InputHelper.readDouble("Enter weight (in kg): ");
                userProfile.setWeight(weight);
                System.out.println("Weight set");
                break;
            case "3":
                double activityLevel = InputHelper.readDouble("Enter activity level multiplier (e.g., 1.2, 1.55): ");
                if(!Validator.isValidActivityLevel(activityLevel)) {
                    System.out.println("Activity level must lie between 1.2 and 2.5. Buzz off");
                    return;
                }
                userProfile.setActivityLevel(activityLevel);
                System.out.println("Activity level set");
                break;
            case "4":
                String methodChoice = InputHelper.readLine("Choose 1.Harris-Benedict or 2.Mifflin-St Jeor: ");
                if (methodChoice.equals("1")) {
                    dietGoalCalculator = new MethodOneCalculator();
                } else if (methodChoice.equals("2")){
                    dietGoalCalculator = new MethodTwoCalculator();
                } else{
                    System.out.println("Invalid Choice.");
                }
                break;
            default:
                break;
        }
    }

    private static void computeDietGoals() {
        if (userProfile == null) {
            System.out.println("User profile not set. Please set the user profile first.");
            return;
        }
        String dateInput = InputHelper.readLine("Enter date (YYYY-MM-DD) [default/invalid: today]: ");
        String date = (dateInput.isBlank() || !Validator.isValidDate(dateInput)) ? currentDate : dateInput;

        List<LogEntry> entries = logManager.getLogEntries(date);
        int totalCalories = entries.stream().mapToInt(LogEntry::getTotalCalories).sum();
        double targetCalories = dietGoalCalculator.calculateTargetCalories(userProfile);
        double difference = totalCalories - targetCalories;

        System.out.println("\n────────────── Diet Summary ──────────────");
        System.out.println("Date: " + date);
        System.out.printf("Total calories consumed : %d kcal%n", totalCalories);
        System.out.printf("Target calorie intake    : %.3f kcal%n", targetCalories);
        System.out.printf("Difference (excess/deficit): %.3f kcal%n", difference);
        System.out.println("──────────────────────────────────────────");
    }

    private static void changeDay(){
        System.out.println("\nDate Menu:");
        System.out.println("1. Next Day");
        System.out.println("2. Custom Day");
        System.out.println("3. Previous Day (for testing)");
        System.out.println("Anything else: Return");

        String choice = InputHelper.readLine("Enter choice: ");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date;
        switch (choice) {
            case "1":
                date = LocalDate.parse(currentDate, formatter);
                currentDate = date.plusDays(1).format(formatter);
                System.out.println("Moved to next day: " + currentDate);
                break;
            case "2":
                String newDate = InputHelper.readLine("Enter new date (YYYY-MM-DD): ");
                try {
                    LocalDate.parse(newDate, formatter); // validate format
                    currentDate = newDate;
                    System.out.println("Date changed to: " + currentDate);
                } catch (DateTimeParseException e) {
                    System.out.println("Invalid date format.");
                }
                break;
            case "3":
                date = LocalDate.parse(currentDate, formatter);
                currentDate = date.minusDays(1).format(formatter);
                System.out.println("Moved to previous day: " + currentDate);
                break;
            default:
                break;
        }

    }

    private static void loadFoodFromExternalSource() {
        String filePath = InputHelper.readLine("Enter file path: ");
        String fileType = InputHelper.readLine("Enter file type (csv, json, xml...): ");

        FoodImporter importer;
        if (fileType.equalsIgnoreCase("csv")) {
            importer = new FoodCSVImporter();
        } else if (fileType.equalsIgnoreCase("json")) {
            importer = new FoodJSONImporter();
        } else {
            System.out.println("Support for '" + fileType + "' can be added later.");
            return;
        }

        try {
            importer.importFoods(filePath, foodDatabase);
            System.out.println(fileType.toUpperCase() + " food data imported successfully.");
        } catch (Exception e) {
            System.out.println("Error importing " + fileType + ": " + e.getMessage());
        }
    }

}
