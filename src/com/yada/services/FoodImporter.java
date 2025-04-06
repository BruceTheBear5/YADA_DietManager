package com.yada.services;

import java.util.Set;

// Interface for the External Source Food Importer
public interface FoodImporter {
    Set<String> ID_HEADERS = Set.of("id", "name", "foodname", "food_name", "title");
    Set<String> CAL_HEADERS = Set.of("cal", "calories", "energy", "kcal", "calorie");
    Set<String> KEYWORDS_HEADERS = Set.of("keywords", "tags", "descriptors");

    void importFoods(String filePath, FoodDatabase db);
}
