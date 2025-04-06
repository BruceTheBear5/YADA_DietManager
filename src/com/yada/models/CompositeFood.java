package com.yada.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompositeFood extends Food {
    // Components: basic or composite foods and their serving counts
    private Map<Food, Integer> components;

    public CompositeFood(String id, List<String> keywords) {
        // Calories are computed from components; Initially set to 0 here.
        super(id, keywords, 0);
        this.components = new HashMap<>();
    }

    public void addComponent(Food food, int servings) {
        components.put(food, servings);
        updateCalories();
    }

    private void updateCalories() {
        int totalCalories = 0;
        for (Map.Entry<Food, Integer> entry : components.entrySet()) {
            totalCalories += entry.getKey().getCaloriesPerServing() * entry.getValue();
        }
        this.setCaloriesPerServing(totalCalories);
    }

    public Map<Food, Integer> getComponents() {
        return components;
    }

    @Override
    public int getCaloriesPerServing() {
        int total = 0;
        for (Map.Entry<Food, Integer> entry : components.entrySet()) {
            total += entry.getKey().getCaloriesPerServing() * entry.getValue();
        }
        return total;
    }
    @Override
    public String toString() {
        List<String> componentIds = components.keySet().stream()
                .map(Food::getId)
                .toList();
        return "CompositeFood{id='" + getId() + "', calories=" + getCaloriesPerServing()
                + ", keywords=" + getKeywords()
                + ", components=" + componentIds + "}";
    }
}
