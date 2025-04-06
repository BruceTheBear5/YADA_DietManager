package com.yada.services;

import com.yada.models.UserProfile;

// A simple implementation using a variant of the Mifflin-St Jeor equation.
// Assumes weight in kilograms, height in centimeters.
public class MethodTwoCalculator implements DietGoalCalculator {

    @Override
    public double calculateTargetCalories(UserProfile profile) {
        double bmr = 0;
        if (profile.getGender().equalsIgnoreCase("male")) {
            bmr = (10 * profile.getWeight()) + (6.25 * profile.getHeight()) - (5 * profile.getAge()) + 5;
        } else {
            bmr = (10 * profile.getWeight()) + (6.25 * profile.getHeight()) - (5 * profile.getAge()) - 161;
        }
        return bmr * profile.getActivityLevel();
    }
}
