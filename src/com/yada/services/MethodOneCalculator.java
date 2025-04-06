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
            bmr = 66.473 + (13.7516 * profile.getWeight()) + (5.0033 * profile.getHeight()) - (6.755 * profile.getAge());
        } else {
            // Harris-Benedict for women
            bmr = 655.0955 + (9.5634 * profile.getWeight()) + (1.8496 * profile.getHeight()) - (4.6756 * profile.getAge());
        }
        return bmr * profile.getActivityLevel();
    }
}
