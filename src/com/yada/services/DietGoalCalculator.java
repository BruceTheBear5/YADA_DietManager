package com.yada.services;

import com.yada.models.UserProfile;

public interface DietGoalCalculator {
    double calculateTargetCalories(UserProfile profile);
}
