package com.yada.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class Validator {

    // IDs can contain letters, numbers, underscores, and spaces
    public static boolean isValidId(String id) {
        return id != null && id.matches("[a-zA-Z0-9_ ]+");
    }

    // Keywords: allow only letters, numbers, underscores; no punctuation
    public static boolean isValidKeyword(String keyword) {
        return keyword != null && keyword.matches("[a-zA-Z0-9_ ]+") && keyword.length() <= 30;
    }

    // Keywords list: ensure it's non-empty and each keyword is valid
    public static boolean isValidKeywordList(List<String> keywords) {
        if (keywords == null || keywords.isEmpty() || keywords.size() > 10) {
            return false;
        }
        for (String kw : keywords) {
            if (!isValidKeyword(kw)) {
                return false;
            }
        }
        return true;
    }

    // Calories must be positive
    public static boolean isValidCalorieValue(double calories) {
        return calories > 0;
    }

    // Servings must be a positive integer
    public static boolean isValidServings(int servings) {
        return servings > 0;
    }

    // Activity level (BMR multiplier) typically ranges from 1.2 to 2.5
    public static boolean isValidActivityLevel(double level) {
        return level >= 1.2 && level <= 2.5;
    }

    // Date must comply with YYYY-MM-DD ISO format
    public static boolean isValidDate(String dateStr) {
        try {
            LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}

