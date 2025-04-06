package com.yada.services;

import com.yada.models.LogEntry;
import com.yada.models.Food;

import java.io.*;
import java.util.*;

public class LogManager {
    // Using a simple map of date (String in format YYYY-MM-DD) to list of log entries
    private Map<String, List<LogEntry>> logs;
    private final String logFile = "data/log.txt";

    public LogManager() {
        logs = new HashMap<>();
    }

    // Load logs from a text file.
    // Each line: date;foodId;servings
    public void loadLogs(FoodDatabase foodDatabase) {
        File file = new File(logFile);
        if (!file.exists()) {
            System.out.println("Log file not found, starting with empty logs.");
            return;
        }
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(";");
                String date = parts[0];
                String foodId = parts[1];
                int servings = Integer.parseInt(parts[2]);
                // Find the food by id (search in both basic and composite)
                Food food = foodDatabase.getAllFoods().stream()
                        .filter(f -> f.getId().equalsIgnoreCase(foodId))
                        .findFirst().orElse(null);
                if (food != null) {
                    LogEntry entry = new LogEntry(food, servings);
                    logs.computeIfAbsent(date, k -> new ArrayList<>()).add(entry);
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading logs: " + e.getMessage());
        }
    }

    // Save logs to file
    public void saveLogs() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(logFile))) {
            for (String date : logs.keySet()) {
                for (LogEntry entry : logs.get(date)) {
                    writer.println(date + ";" + entry.getFood().getId() + ";" + entry.getServings());
                }
            }
        } catch (Exception e) {
            System.out.println("Error saving logs: " + e.getMessage());
        }
    }

    public void addLogEntry(String date, LogEntry entry) {
        logs.computeIfAbsent(date, k -> new ArrayList<>()).add(entry);
    }

    public void deleteLogEntry(String date, int index) {
        List<LogEntry> entries = logs.get(date);
        if (entries != null && index >= 0 && index < entries.size()) {
            entries.remove(index);
        }
    }

    public List<LogEntry> getLogEntries(String date) {
        return logs.getOrDefault(date, new ArrayList<>());
    }
}
