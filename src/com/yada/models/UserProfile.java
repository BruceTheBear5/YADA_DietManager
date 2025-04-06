package com.yada.models;

public class UserProfile {
    private String gender;
    private double height;      // height in centimeters (or inches as needed)
    private int age;
    private double weight;      // weight in kilograms (or pounds)
    private double activityLevel; // a multiplier (e.g., 1.2 for sedentary, 1.55 for moderate, etc.)

    public UserProfile(String gender, double height, int age, double weight, double activityLevel) {
        this.gender = gender;
        this.height = height;
        this.age = age;
        this.weight = weight;
        this.activityLevel = activityLevel;
    }

    public String getGender() {
        return gender;
    }

    public double getHeight() {
        return height;
    }

    public int getAge() {
        return age;
    }

    public double getWeight() {
        return weight;
    }

    public double getActivityLevel() {
        return activityLevel;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void setActivityLevel(double activityLevel) {
        this.activityLevel = activityLevel;
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "gender='" + gender + '\'' +
                ", height=" + height +
                ", age=" + age +
                ", weight=" + weight +
                ", activityLevel=" + activityLevel +
                '}';
    }
}
