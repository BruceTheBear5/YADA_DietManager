package com.yada.services;

import com.yada.models.Food;
import java.io.FileReader;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.IOException;

import org.json.simple.*;
import org.json.simple.parser.*;

public class FoodJSONImporter implements FoodImporter{
//    private static final Set<String> ID_HEADERS = Set.of("id", "name", "foodname", "food_name", "title");
//    private static final Set<String> CAL_HEADERS = Set.of("cal", "calories", "energy", "kcal", "calorie");
//    private static final Set<String> KEYWORDS_HEADERS = Set.of("keywords", "tags", "descriptors");

    public void importFoods(String filePath, FoodDatabase db) {
        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader(filePath)) {
            Object obj = parser.parse(reader);
            if (!(obj instanceof JSONArray)) {
                System.err.println("JSON must be an array of food objects.");
                return;
            }

            JSONArray array = (JSONArray) obj;

            if (array.isEmpty()) {
                System.out.println("No data in JSON.");
                return;
            }

            // Flatten keys for the first object
            JSONObject first = (JSONObject) array.get(0);
            Map<String, String> flattened = flatten(first, "");

            List<String> keys = new ArrayList<>(flattened.keySet());
            Map<String, String> keyMapping = askUserForKeyMapping(keys);

            String idKey = keyMapping.get("id");
            String calKey = keyMapping.get("calories");
            String kwKey = keyMapping.get("keywords");

            for (Object o : array) {
                if (!(o instanceof JSONObject)) continue;
                JSONObject foodObj = (JSONObject) o;
                Map<String, String> flat = flatten(foodObj, "");

                String id = flat.getOrDefault(idKey, "").trim();
                if (id.isEmpty()) {
                    System.out.println("Skipping entry with empty ID: " + foodObj);
                    continue;
                }

                Food results = db.findFoodById(id);
                if(results != null){
                    System.out.println("Skipping entry with duplicate ID: " + id);
                    continue;
                }

                int calories = extractCalories(flat.get(calKey));

                List<String> keywords = new ArrayList<>();
                if (kwKey != null && flat.containsKey(kwKey)) {
                    String raw = flat.get(kwKey);
                    if (raw != null && !raw.isBlank()) {
                        keywords = Arrays.stream(raw.split("[|;/,]"))
                                .map(String::trim)
                                .filter(s -> !s.isEmpty())
                                .toList();
                    }
                }

                db.addBasicFood(new Food(id, keywords, calories));
            }

            System.out.println("JSON import complete.");

        } catch (IOException | ParseException e) {
            System.err.println("Failed to parse JSON: " + e.getMessage());
        }
    }

    private static Map<String, String> askUserForKeyMapping(List<String> keys) {
        Scanner scanner = new Scanner(System.in);
        Map<String, String> mapping = new HashMap<>();

        System.out.println("\nAvailable flattened keys from JSON:");
        for (int i = 0; i < keys.size(); i++) {
            System.out.printf("[%d] %s\n", i, keys.get(i));
        }

        mapping.put("id", promptForKey("ID", ID_HEADERS, keys, scanner));
        mapping.put("calories", promptForKey("Calories", CAL_HEADERS, keys, scanner));
        mapping.put("keywords", promptForKey("Keywords (optional)", KEYWORDS_HEADERS, keys, scanner, true));

        return mapping;
    }

    private static String promptForKey(String label, Set<String> aliases, List<String> keys, Scanner scanner) {
        return promptForKey(label, aliases, keys, scanner, false);
    }

    private static String promptForKey(String label, Set<String> aliases, List<String> keys, Scanner scanner, boolean optional) {
        String suggested = null;
        for (String k : keys) {
            for (String alias : aliases) {
                if (k.toLowerCase().contains(alias)) {
                    suggested = k;
                    break;
                }
            }
            if (suggested != null) break;
        }

        System.out.printf("\nWhich key corresponds to '%s'? ", label);
        if (suggested != null) {
            System.out.printf("Suggested: %s\n", suggested);
        }

        while (true) {
            System.out.print("Enter key index" + (optional ? " or press Enter to skip: " : ": "));
            String input = scanner.nextLine().trim();
            if (optional && input.isEmpty()) return null;

            try {
                int index = Integer.parseInt(input);
                if (index >= 0 && index < keys.size()) return keys.get(index);
            } catch (NumberFormatException ignored) {}

            System.out.println("Invalid input. Try again.");
        }
    }

    private static Map<String, String> flatten(JSONObject obj, String prefix) {
        Map<String, String> flat = new HashMap<>();
        for (Object keyObj : obj.keySet()) {
            String key = (String) keyObj;
            Object val = obj.get(key);

            if (val instanceof JSONObject) {
                flat.putAll(flatten((JSONObject) val, prefix + key + "."));
            } else {
                flat.put(prefix + key, val == null ? "" : val.toString());
            }
        }
        return flat;
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
