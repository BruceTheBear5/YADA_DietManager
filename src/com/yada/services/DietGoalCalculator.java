package com.yada.services;

import com.yada.models.UserProfile;

// Interface for the Diet Goal Computation Method
public interface DietGoalCalculator {
    double calculateTargetCalories(UserProfile profile);
}
