package com.yada.models;

public class LogEntry {
    private Food food;
    private int servings;

    public LogEntry(Food food, int servings) {
        this.food = food;
        this.servings = servings;
    }

    public Food getFood() {
        return food;
    }

    public int getServings() {
        return servings;
    }

    public int getTotalCalories() {
        return food.getCaloriesPerServing() * servings;
    }

    @Override
    public String toString() {
        return food.getId() + " (servings: " + servings + ", total calories: " + getTotalCalories() + ")";
    }
}
