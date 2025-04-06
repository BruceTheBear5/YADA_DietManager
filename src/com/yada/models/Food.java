package com.yada.models;

import java.util.List;

public class Food {
    private String id;
    private List<String> keywords;
    private int caloriesPerServing;

    /* Extensibility
    private double protein;
    private double carbohydrates;
    private double fat;
    private double fiber;*/

    public Food(String id, List<String> keywords, int caloriesPerServing) {
        this.id = id;
        this.keywords = keywords;
        this.caloriesPerServing = caloriesPerServing;
    }

    /* New constructor that accepts more nutrition info
    public Food(String id, List<String> keywords, int caloriesPerServing,
                double protein, double carbohydrates, double fat, double fiber) {
        this(id, keywords, caloriesPerServing);
        this.protein = protein;
        this.carbohydrates = carbohydrates;
        this.fat = fat;
        this.fiber = fiber;
    }*/

    public String getId() {
        return id;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setCaloriesPerServing(int caloriesPerServing) {
        this.caloriesPerServing = caloriesPerServing;
    }

    public int getCaloriesPerServing() {
        return caloriesPerServing;
    }

    @Override
    public String toString() {
        return "Food{id='" + id + "', calories=" + caloriesPerServing + ", keywords=" + keywords + "}";
    }

    /* Modified toString() method.
    @Override
    public String toString() {
        return "Food{id='" + id + "', calories=" + caloriesPerServing + ", keywords=" + keywords +
                ", protein=" + protein + ", carbs=" + carbohydrates + ", fat=" + fat + ", fiber=" + fiber + "}";
    }*/
}
