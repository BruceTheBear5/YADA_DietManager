package com.yada.services;

import com.yada.models.CompositeFood;
import com.yada.models.Food;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class FoodDatabase {
    private List<Food> basicFoods;
    private List<CompositeFood> compositeFoods;
    private final String foodFile = "data/foods.txt";

    public FoodDatabase() {
        basicFoods = new ArrayList<>();
        compositeFoods = new ArrayList<>();
    }

    // Loads foods from a text file.
    // Format for basic foods: B;id;calories;keyword1,keyword2,...
    // Format for composite foods: C;id;keyword1,keyword2,...;component1:amount,component2:amount
    public void loadFoods() {
        File file = new File(foodFile);
        if (!file.exists()) {
            System.out.println("Food file not found, starting with an empty database.");
            return;
        }
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(";");

                if (parts[0].equals("B")) {
                    // Basic food: B;id;calories;keyword1,keyword2,...
                    String id = parts[1];
                    int calories = Integer.parseInt(parts[2]);
                    List<String> keywords = Arrays.asList(parts[3].split(","));
                    basicFoods.add(new Food(id, keywords, calories));
                } else if (parts[0].equals("C")) {
                    // Composite food: C;id;keyword1,keyword2,...;component1-id:servings,component2-id:servings,...
                    String id = parts[1];
                    List<String> keywords = Arrays.asList(parts[2].split(","));

                    // Check if component details exist
                    if (parts.length > 3) {
                        String[] componentData = parts[3].split(",");
                        CompositeFood compositeFood = new CompositeFood(id, keywords);

                        for (String component : componentData) {
                            String[] componentParts = component.split(":");
                            if (componentParts.length == 2) {
                                String componentId = componentParts[0];
                                int servings = Integer.parseInt(componentParts[1]);

                                // Find the referenced component food
                                Food foundFood = findFoodById(componentId);
                                if (foundFood != null) {
                                    compositeFood.addComponent(foundFood, servings);
                                } else {
                                    System.out.println("Warning: Component " + componentId + " not found for " + id);
                                }
                            }
                        }
                        compositeFoods.add(compositeFood);
                    } else {
                        compositeFoods.add(new CompositeFood(id, keywords));
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading foods: " + e.getMessage());
        }
    }

    // Helper function to find food by ID
    public Food findFoodById(String id) {
        for (Food food : basicFoods) {
            if (food.getId().equalsIgnoreCase(id))
                return food;
        }
        for (CompositeFood cf : compositeFoods) {
            if (cf.getId().equalsIgnoreCase(id))
                return cf;
        }
        return null;
    }

    // Saves the current food database to file.
    public void saveFoods() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(foodFile))) {
            for (Food food : basicFoods) {
                writer.println("B;" + food.getId() + ";" + food.getCaloriesPerServing() + ";" + String.join(",", food.getKeywords()));
            }
            for (CompositeFood cf : compositeFoods) {
                String componentDetails = cf.getComponents().isEmpty() ? "" :
                        ";" + cf.getComponents().entrySet().stream()
                                .map(entry -> entry.getKey().getId() + ":" + entry.getValue())
                                .collect(Collectors.joining(","));

                writer.println("C;" + cf.getId() + ";" + String.join(",", cf.getKeywords()) + componentDetails);
            }
        } catch (Exception e) {
            System.out.println("Error saving foods: " + e.getMessage());
        }
    }

    public void addBasicFood(Food food) {
        basicFoods.add(food);
    }

    public void addCompositeFood(CompositeFood compositeFood) {
        compositeFoods.add(compositeFood);
    }

    public void removeFood(Food food) {
        if (food instanceof CompositeFood) {
            compositeFoods.remove(food);
        } else {
            basicFoods.remove(food);
        }
        // Also remove any CompositeFood that uses this food as a component
        compositeFoods.removeIf(cf -> cf.getComponents().containsKey(food));
    }

    public List<Food> searchFoods(List<String> keywords, boolean all) {
        List<Food> results = new ArrayList<>();

        for (Food food : basicFoods) {
            if (matchesKeywords(food.getKeywords(), keywords, all))
                results.add(food);
        }
        for (CompositeFood cf : compositeFoods) {
            if (matchesKeywords(cf.getKeywords(), keywords, all))
                results.add(cf);
        }

        return results;
    }

    // Helper function to check if food keywords match search keywords
    private boolean matchesKeywords(List<String> foodKeywords, List<String> searchKeywords, boolean all) {
        if (all) {
            // All keywords must match
            return searchKeywords.stream()
                    .allMatch(sk -> foodKeywords.stream()
                            .anyMatch(fk -> fk.equalsIgnoreCase(sk)));
        } else {
            // At least one keyword must match
            return searchKeywords.stream()
                    .anyMatch(sk -> foodKeywords.stream()
                            .anyMatch(fk -> fk.equalsIgnoreCase(sk)));
        }
    }

    public List<Food> getAllFoods() {
        List<Food> all = new ArrayList<>();
        all.addAll(basicFoods);
        all.addAll(compositeFoods);
        return all;
    }

    // List all Basic Foods
    public void listBasicFoods() {
        System.out.println("\n--- Basic Foods in Database ---");
        if (basicFoods.isEmpty()) {
            System.out.println("No basic foods found.");
        } else {
            for (Food food : basicFoods) {
                System.out.println(food.toString());
            }
        }
    }

    // List all Composite Foods
    public void listCompositeFoods() {
        System.out.println("\n--- Composite Foods in Database ---");
        if (compositeFoods.isEmpty()) {
            System.out.println("No composite foods found.");
        } else {
            for (CompositeFood cf : compositeFoods) {
                System.out.println(cf.toString());
            }
        }
    }
}
