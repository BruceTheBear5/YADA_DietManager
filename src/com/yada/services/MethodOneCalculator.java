package com.yada.services;

import com.yada.models.UserProfile;

// A simple implementation using a variant of the Harris-Benedict equation.
// Assumes weight in kilograms, height in centimeters.
public class MethodOneCalculator implements DietGoalCalculator {

    @Override
    public double calculateTargetCalories(UserProfile profile) {
        double bmr = 0;
        if (profile.getGender().equalsIgnoreCase("male")) {
            // Harris-Benedict for men
            bmr = 66.5 + (13.75 * profile.getWeight()) + (5.003 * profile.getHeight()) - (6.75 * profile.getAge());
        } else {
            // Harris-Benedict for women
            bmr = 655.1 + (9.563 * profile.getWeight()) + (1.850 * profile.getHeight()) - (4.676 * profile.getAge());
        }
        return bmr * profile.getActivityLevel();
    }
}
