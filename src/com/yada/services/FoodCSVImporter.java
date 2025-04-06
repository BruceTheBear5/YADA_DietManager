package com.yada.services;

import com.yada.models.Food;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class FoodCSVImporter implements FoodImporter{

//    private static final Set<String> ID_HEADERS = Set.of("id", "name", "foodname", "food_name", "title");
//    private static final Set<String> CAL_HEADERS = Set.of("cal", "calories", "energy", "kcal", "calorie");
//    private static final Set<String> KEYWORDS_HEADERS = Set.of("keywords", "tags", "descriptors");

    public void importFoods(String filePath, FoodDatabase db) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                System.err.println("CSV is empty.");
                return;
            }

            String[] headers = headerLine.split(",");
            Map<String, Integer> headerMap = askUserForHeaderMapping(headers);

            int idIdx = headerMap.get("id");
            int calIdx = headerMap.get("calories");
            int kwIdx = headerMap.get("keywords");

            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",", -1); // -1 keeps trailing empty fields

                if (tokens.length <= Math.max(idIdx, Math.max(calIdx, kwIdx))) {
                    System.out.println("Skipping malformed line: " + line);
                    continue;
                }

                String id = tokens[idIdx].trim();
                if (id.isEmpty()) {
                    System.out.println("Skipping entry with empty ID: " + line);
                    continue;
                }

                Food results = db.findFoodById(id);
                if(results != null){
                    System.out.println("Skipping entry with duplicate ID: " + id);
                    continue;
                }

                int calories = extractCalories(tokens[calIdx]);

                List<String> keywords = new ArrayList<>();
                if (kwIdx != -1 && kwIdx < tokens.length) {
                    String rawKeywords = tokens[kwIdx];
                    if (!rawKeywords.isBlank()) {
                        keywords = Arrays.stream(rawKeywords.split("[|;/,]"))
                                .map(String::trim)
                                .filter(s -> !s.isEmpty())
                                .toList();
                    }
                }

                Food food = new Food(id, keywords, calories);
                db.addBasicFood(food);
            }

            System.out.println("Import completed.");
        } catch (IOException e) {
            System.err.println("Error reading CSV: " + e.getMessage());
        }
    }

    private static Map<String, Integer> askUserForHeaderMapping(String[] headers) {
        Scanner scanner = new Scanner(System.in);
        Map<String, Integer> finalMapping = new HashMap<>();

        // Lowercase header to index mapping
        Map<String, Integer> headerIndexMap = new HashMap<>();
        for (int i = 0; i < headers.length; i++) {
            headerIndexMap.put(headers[i].trim().toLowerCase(), i);
        }

        System.out.println("\nDetected CSV headers:");
        for (int i = 0; i < headers.length; i++) {
            System.out.printf("[%d] %s\n", i, headers[i]);
        }

        finalMapping.put("id", promptForHeader("ID", ID_HEADERS, headerIndexMap, headers, scanner));
        finalMapping.put("calories", promptForHeader("Calories", CAL_HEADERS, headerIndexMap, headers, scanner));
        finalMapping.put("keywords", promptForHeader("Keywords (optional)", KEYWORDS_HEADERS, headerIndexMap, headers, scanner, true));

        return finalMapping;
    }

    private static int promptForHeader(
            String label,
            Set<String> knownAliases,
            Map<String, Integer> headerMap,
            String[] headers,
            Scanner scanner
    ) {
        return promptForHeader(label, knownAliases, headerMap, headers, scanner, false);
    }

    private static int promptForHeader(
            String label,
            Set<String> knownAliases,
            Map<String, Integer> headerMap,
            String[] headers,
            Scanner scanner,
            boolean optional
    ) {
        Integer suggestedIndex = null;
        for (String alias : knownAliases) {
            if (headerMap.containsKey(alias)) {
                suggestedIndex = headerMap.get(alias);
                break;
            }
        }

        System.out.printf("\nWhich column corresponds to '%s'? ", label);
        if (suggestedIndex != null) {
            System.out.printf("Suggested: [%d] %s\n", suggestedIndex, headers[suggestedIndex]);
        }

        while (true) {
            System.out.printf("Enter column number (0 to %d)%s: ", headers.length - 1, optional ? " or press Enter to skip" : "");
            String input = scanner.nextLine().trim();

            if (optional && input.isEmpty()) return -1;

            try {
                int index = Integer.parseInt(input);
                if (index >= 0 && index < headers.length) return index;
            } catch (NumberFormatException ignored) {}

            System.out.println("Invalid input. Please enter a valid column index.");
        }
    }

    private static int extractCalories(String rawCal) {
        rawCal = rawCal.toLowerCase().replaceAll("[^0-9.]", "");
        try {
            return (int) Math.round(Double.parseDouble(rawCal));
        } catch (NumberFormatException e) {
            System.err.println("Failed to parse calories from: " + rawCal + ", defaulting to 0");
            return 0;
        }
    }
}
