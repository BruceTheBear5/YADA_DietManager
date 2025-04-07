# 🍽️ YADA: Yet Another Diet Assistant

## Overview

A console-based Java application that helps users track their daily food intake, manage composite foods, and compute their daily calorie needs. It supports importing food data from CSV and JSON files and logging consumption across dates.

---

## How to Run

### Requirements
- Java 16 or above
- IntelliJ IDEA or any Java-supporting IDE (alternatively, run from terminal)

### Steps to Run

1. **Clone the Repository** (or download the source files):

2. **Open the project in IntelliJ** (or any IDE with Java support).

3. **Run `Main.java`**:
- Right-click on `Main.java` → `Run 'YADA'`

---

## Using the Program

### Main Features & Commands

Once running, you'll see a **menu-based interface**. You can interact with the system via numbered prompts.

### Managing Food

- **Add Basic Food**: Add a basic atomic food item.
- **Add Composite Food**: Combine multiple foods into one reusable item.
- **List All Foods**: Displays all registered food items.
- **Remove Food**: Delete a food-type from the database.
---

### Logging and Tracking

- **Add Log Entry**: Enter a date and log what food you ate and how many servings.
- **Delete Log Entry**: Enter a date and remove a log entry from that day.
- **Undo Last Action**: Quickly undo the most recent log action (add/delete).
- **View Daily Log**: View what you consumed on a given date.

---

### Calorie Computation

- **Set User Profile**: Initialize user profile.
- **Update User Profile**: Change age, height, activity level or calorie-calculation method.
- **Compute Diet Goals**: Based on user profile (gender, weight, height, age, activity level).
- **View Total Calories Consumed** on a specific date.

---

### Date Management and Persistence

- **Change Day**: Navigate between different days.
- **Save and Exit**: Persist your logs and food entries before exiting the program.

---

### Importing Foods
You can import food data from CSV or JSON files:
1. Choose the option to import foods.
2. Enter the **file path**.
3. Enter the **file type** (`csv` or `json`).

> **Tip:** Files must contain appropriate column headers like `id`, `calories`, and `keywords`.


