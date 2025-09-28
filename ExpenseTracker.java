import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class ExpenseTracker {
    private List<Expense> expenses;
    private int nextId;
    private Scanner scanner;
    
    // Color codes
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    
    private static final String[] CATEGORIES = {
        "Food", "Transport", "Entertainment", "Utilities", "Shopping", "Healthcare", "Other"
    };
    
    public ExpenseTracker() {
        expenses = new ArrayList<>();
        scanner = new Scanner(System.in);
        nextId = 1;
        loadFromFile();
    }
    
    // File operations
    private void saveToFile() {
        try (PrintWriter writer = new PrintWriter("expenses.txt")) {
            for (Expense expense : expenses) {
                writer.println(expense.getId() + "|" + expense.getAmount() + "|" + 
                              expense.getCategory() + "|" + expense.getDate() + "|" + 
                              expense.getDescription());
            }
        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }
    
    private void loadFromFile() {
        try {
            List<String> lines = Files.readAllLines(Paths.get("expenses.txt"));
            for (String line : lines) {
                String[] data = line.split("\\|");
                if (data.length == 5) {
                    Expense expense = new Expense(
                        Integer.parseInt(data[0]), 
                        Double.parseDouble(data[1]), 
                        data[2], data[3], data[4]
                    );
                    expenses.add(expense);
                }
            }
            nextId = expenses.stream().mapToInt(Expense::getId).max().orElse(0) + 1;
            System.out.println(GREEN + "✓ Previous expenses loaded successfully!" + RESET);
        } catch (IOException e) {
            System.out.println(YELLOW + "Starting fresh - no previous data found." + RESET);
        }
    }
    
    // Main menu and core functionality
    public void start() {
        System.out.println(CYAN + "=== Personal Expense Tracker ===" + RESET);
        
        while (true) {
            displayMenu();
            int choice = getIntInput("Enter your choice: ");
            switch (choice) {
    case 1: addExpense(); break;
    case 2: viewAllExpenses(); break;
    case 3: viewExpensesByCategory(); break;
    case 4: viewAllCategoryTotals(); break;
    case 5: viewTotalSpending(); break;
    case 6: timeBasedAnalytics(); break;
    case 7: budgetAlerts(); break;
    case 8: calculatorMode(); break;  // NEW CALCULATOR! 🧮
    case 9: exportToCSV(); break;
    case 10: deleteExpense(); break;
    case 11: 
        System.out.println(GREEN + "Thank you for using Expense Tracker!" + RESET);
        return;
    default: System.out.println(RED + "Invalid choice! Please try again." + RESET);
}
        }
    }
    // ADD THIS CSV EXPORT METHOD
private void exportToCSV() {
    System.out.println("\n--- Export Expenses to CSV ---");
    
    if (expenses.isEmpty()) {
        System.out.println(YELLOW + "No expenses to export." + RESET);
        return;
    }
    
    try (PrintWriter writer = new PrintWriter("expenses_export.csv")) {
        // Write CSV header
        writer.println("ID,Amount,Category,Date,Description");
        
        // Write all expenses
        for (Expense expense : expenses) {
            writer.printf("%d,%.2f,%s,%s,%s\n",
                expense.getId(), 
                expense.getAmount(), 
                expense.getCategory(), 
                expense.getDate(),
                expense.getDescription().replace(",", ";") // Avoid CSV issues
            );
        }
        
        System.out.println(GREEN + "✅ Data exported to 'expenses_export.csv'" + RESET);
        System.out.println(CYAN + "📁 File saved in your project folder!" + RESET);
        System.out.println(CYAN + "💼 You can open this in Excel for analysis!" + RESET);
        
    } catch (IOException e) {
        System.out.println(RED + "❌ Error exporting data: " + e.getMessage() + RESET);
    }
}
private void displayMenu() {
    System.out.println(CYAN + "\n===== MAIN MENU =====" + RESET);
    System.out.println("1. Add New Expense");
    System.out.println("2. View All Expenses");
    System.out.println("3. View Expenses by Category"); 
    System.out.println("4. View All Category Totals");
    System.out.println("5. View Total Spending");
    System.out.println("6. Time-Based Analytics");
    System.out.println("7. Budget Alerts");
    System.out.println("8. Calculator Mode");  // NEW! 🧮
    System.out.println("9. Export to CSV");
    System.out.println("10. Delete an Expense");
    System.out.println("11. Exit");
    System.out.println(CYAN + "=====================" + RESET);
}
    
    // NEW: Time-Based Analytics Menu
    private void timeBasedAnalytics() {
        System.out.println(CYAN + "\n--- Time-Based Analytics ---" + RESET);
        System.out.println("1. Daily Expenses");
        System.out.println("2. Weekly Expenses");
        System.out.println("3. Monthly Expenses");
        System.out.println("4. Yearly Expenses");
        System.out.println("5. Custom Date Range");
        System.out.println("6. Back to Main Menu");
        
        int choice = getIntInput("Enter your choice: ");
        
        switch (choice) {
            case 1: viewDailyExpenses(); break;
            case 2: viewWeeklyExpenses(); break;
            case 3: viewMonthlyExpenses(); break;
            case 4: viewYearlyExpenses(); break;
            case 5: viewCustomDateRange(); break;
            case 6: return;
            default: System.out.println(RED + "Invalid choice!" + RESET);
        }
    }
    
    // NEW: Daily Expenses
    private void viewDailyExpenses() {
        System.out.print("Enter date (DD/MM/YYYY): ");
        String date = scanner.nextLine();
        
        List<Expense> dailyExpenses = new ArrayList<>();
        double dailyTotal = 0;
        
        for (Expense expense : expenses) {
            if (expense.getDate().equals(date)) {
                dailyExpenses.add(expense);
                dailyTotal += expense.getAmount();
            }
        }
        
        displayTimeBasedResults("DAILY", date, dailyExpenses, dailyTotal);
    }
    
    // NEW: Weekly Expenses
    private void viewWeeklyExpenses() {
        System.out.print("Enter start date of week (DD/MM/YYYY): ");
        String startDate = scanner.nextLine();
        
        // Calculate end date (6 days after start)
        String endDate = calculateEndDate(startDate, 6);
        List<Expense> weeklyExpenses = getExpensesByDateRange(startDate, endDate);
        double weeklyTotal = calculateTotal(weeklyExpenses);
        
        displayTimeBasedResults("WEEKLY", startDate + " to " + endDate, weeklyExpenses, weeklyTotal);
    }
    
    // NEW: Monthly Expenses
    private void viewMonthlyExpenses() {
        System.out.print("Enter month and year (MM/YYYY): ");
        String monthYear = scanner.nextLine();
        
        List<Expense> monthlyExpenses = new ArrayList<>();
        double monthlyTotal = 0;
        
        for (Expense expense : expenses) {
            if (expense.getDate().endsWith(monthYear)) {
                monthlyExpenses.add(expense);
                monthlyTotal += expense.getAmount();
            }
        }
        
        displayTimeBasedResults("MONTHLY", monthYear, monthlyExpenses, monthlyTotal);
    }
    
    // NEW: Yearly Expenses
    private void viewYearlyExpenses() {
        System.out.print("Enter year (YYYY): ");
        String year = scanner.nextLine();
        
        List<Expense> yearlyExpenses = new ArrayList<>();
        double yearlyTotal = 0;
        
        for (Expense expense : expenses) {
            if (expense.getDate().endsWith(year)) {
                yearlyExpenses.add(expense);
                yearlyTotal += expense.getAmount();
            }
        }
        
        displayTimeBasedResults("YEARLY", year, yearlyExpenses, yearlyTotal);
    }
    
    // NEW: Custom Date Range
    private void viewCustomDateRange() {
        System.out.print("Enter start date (DD/MM/YYYY): ");
        String startDate = scanner.nextLine();
        System.out.print("Enter end date (DD/MM/YYYY): ");
        String endDate = scanner.nextLine();
        
        List<Expense> rangeExpenses = getExpensesByDateRange(startDate, endDate);
        double rangeTotal = calculateTotal(rangeExpenses);
        
        displayTimeBasedResults("CUSTOM RANGE", startDate + " to " + endDate, rangeExpenses, rangeTotal);
    }
    
    // NEW: Helper methods for time-based analytics
    private List<Expense> getExpensesByDateRange(String startDate, String endDate) {
        List<Expense> filteredExpenses = new ArrayList<>();
        for (Expense expense : expenses) {
            if (isDateInRange(expense.getDate(), startDate, endDate)) {
                filteredExpenses.add(expense);
            }
        }
        return filteredExpenses;
    }
    
    private boolean isDateInRange(String expenseDate, String startDate, String endDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date expDate = sdf.parse(expenseDate);
            Date start = sdf.parse(startDate);
            Date end = sdf.parse(endDate);
            return !expDate.before(start) && !expDate.after(end);
        } catch (Exception e) {
            return false;
        }
    }
    
    private String calculateEndDate(String startDate, int daysToAdd) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date date = sdf.parse(startDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.DATE, daysToAdd);
            return sdf.format(cal.getTime());
        } catch (Exception e) {
            return startDate;
        }
    }
    
    private double calculateTotal(List<Expense> expenseList) {
        double total = 0;
        for (Expense expense : expenseList) {
            total += expense.getAmount();
        }
        return total;
    }
    
    // NEW: Display time-based results
    private void displayTimeBasedResults(String periodType, String period, List<Expense> expenseList, double total) {
        System.out.println(CYAN + "\n══════════════════════════════════════════════════════" + RESET);
        System.out.println(CYAN + periodType + " EXPENSES: " + period + RESET);
        System.out.println(CYAN + "══════════════════════════════════════════════════════" + RESET);
        
        if (expenseList.isEmpty()) {
            System.out.println(YELLOW + "No expenses found for this period." + RESET);
            return;
        }
        
        for (Expense expense : expenseList) {
            displayExpense(expense);
        }
        
        System.out.println(CYAN + "──────────────────────────────────────────────────────" + RESET);
        System.out.printf(CYAN + "💰 TOTAL FOR " + periodType + ": $%.2f\n" + RESET, total);
        System.out.printf(CYAN + "📊 NUMBER OF EXPENSES: %d\n" + RESET, expenseList.size());
        System.out.println(CYAN + "──────────────────────────────────────────────────────" + RESET);
    }
    
    // Existing methods (addExpense, viewAllExpenses, etc.)
    private void addExpense() {
        System.out.println("\n--- Add New Expense ---");
        
        double amount = getDoubleInput("Enter amount: $");
        
        System.out.println("Select category:");
        for (int i = 0; i < CATEGORIES.length; i++) {
            System.out.println((i + 1) + ". " + CATEGORIES[i]);
        }
        int catChoice = getIntInput("Enter category number: ") - 1;
        
        if (catChoice < 0 || catChoice >= CATEGORIES.length) {
            System.out.println(RED + "Invalid category selection!" + RESET);
            return;
        }
        
        String category = CATEGORIES[catChoice];
        System.out.print("Enter date (DD/MM/YYYY): ");
        String date = scanner.nextLine();
        System.out.print("Enter description: ");
        String description = scanner.nextLine();
        
        Expense expense = new Expense(nextId++, amount, category, date, description);
        expenses.add(expense);
        saveToFile();
        System.out.println(GREEN + "Expense added successfully!" + RESET);
    }
    
    private void viewAllExpenses() {
        System.out.println("\n--- All Expenses ---");
        if (expenses.isEmpty()) {
            System.out.println(YELLOW + "No expenses recorded yet." + RESET);
            return;
        }
        
        for (Expense expense : expenses) {
            displayExpense(expense);
        }
    }
    
    private void displayExpense(Expense expense) {
        String color;
        if (expense.getAmount() > 100) {
            color = RED;
        } else if (expense.getAmount() > 50) {
            color = YELLOW;
        } else if (expense.getAmount() > 20) {
            color = BLUE;
        } else {
            color = GREEN;
        }
        
        System.out.println(color + "┌─────────────────────────────────────────────────────┐" + RESET);
        System.out.printf(color + "│ ID: %-4d │ Date: %-10s │ Amount: $%-8.2f │\n" + RESET, 
                         expense.getId(), expense.getDate(), expense.getAmount());
        System.out.printf(color + "│ Category: %-42s │\n" + RESET, expense.getCategory());
        
        String description = expense.getDescription();
        if (description.length() > 35) {
            description = description.substring(0, 32) + "...";
        }
        System.out.printf(color + "│ Description: %-35s │\n" + RESET, description);
        System.out.println(color + "└─────────────────────────────────────────────────────┘" + RESET);
    }
    
    private void viewExpensesByCategory() {
        System.out.println("\n--- Expenses by Category ---");
        System.out.println("Select category:");
        for (int i = 0; i < CATEGORIES.length; i++) {
            System.out.println((i + 1) + ". " + CATEGORIES[i]);
        }
        
        int catChoice = getIntInput("Enter category number: ") - 1;
        if (catChoice < 0 || catChoice >= CATEGORIES.length) {
            System.out.println(RED + "Invalid category selection!" + RESET);
            return;
        }
        
        String selectedCategory = CATEGORIES[catChoice];
        boolean found = false;
        double categoryTotal = 0;
        
        System.out.println(CYAN + "\n══════════════════════════════════════════════════════" + RESET);
        System.out.println(CYAN + "CATEGORY: " + selectedCategory.toUpperCase() + RESET);
        System.out.println(CYAN + "══════════════════════════════════════════════════════" + RESET);
        
        for (Expense expense : expenses) {
            if (expense.getCategory().equals(selectedCategory)) {
                displayExpense(expense);
                categoryTotal += expense.getAmount();
                found = true;
            }
        }
        
        if (found) {
            System.out.println(CYAN + "──────────────────────────────────────────────────────" + RESET);
            System.out.printf(CYAN + "💰 TOTAL SPENT IN %s: $%.2f\n" + RESET, 
                             selectedCategory.toUpperCase(), categoryTotal);
            System.out.println(CYAN + "──────────────────────────────────────────────────────" + RESET);
        } else {
            System.out.println(YELLOW + "No expenses found in category: " + selectedCategory + RESET);
        }
    }
    
    private void viewAllCategoryTotals() {
        System.out.println("\n--- Spending Summary by Category ---");
        
        if (expenses.isEmpty()) {
            System.out.println(YELLOW + "No expenses recorded yet." + RESET);
            return;
        }
        
        Map<String, Double> categoryTotals = new HashMap<>();
        double overallTotal = 0;
        
        for (Expense expense : expenses) {
            String category = expense.getCategory();
            categoryTotals.put(category, categoryTotals.getOrDefault(category, 0.0) + expense.getAmount());
            overallTotal += expense.getAmount();
        }
        
        System.out.println(CYAN + "┌────────────────────────────────────────────┐" + RESET);
        System.out.println(CYAN + "│           CATEGORY SPENDING SUMMARY        │" + RESET);
        System.out.println(CYAN + "├────────────────────────────────────────────┤" + RESET);
        
        for (String category : CATEGORIES) {
            double total = categoryTotals.getOrDefault(category, 0.0);
            if (total > 0) {
                double percentage = (total / overallTotal) * 100;
                System.out.printf(CYAN + "│ %-15s: $%-8.2f (%5.1f%%)     │\n" + RESET, 
                                 category, total, percentage);
            }
        }
        
        System.out.println(CYAN + "├────────────────────────────────────────────┤" + RESET);
        System.out.printf(CYAN + "│ %-15s: $%-8.2f (100.0%%)   │\n" + RESET, 
                         "OVERALL TOTAL", overallTotal);
        System.out.println(CYAN + "└────────────────────────────────────────────┘" + RESET);
    }
    
    private void viewTotalSpending() {
        double total = 0;
        for (Expense expense : expenses) {
            total += expense.getAmount();
        }
        System.out.printf(CYAN + "\n💰 Total Spending: $%.2f\n" + RESET, total);
    }
    
    private void budgetAlerts() {
        System.out.println("\n--- Budget Alerts ---");
        
        Map<String, Double> budgets = Map.of(
            "Food", 200.0, "Transport", 100.0, "Entertainment", 150.0,
            "Shopping", 300.0, "Utilities", 250.0, "Healthcare", 100.0, "Other", 50.0
        );
        
        String currentMonth = new SimpleDateFormat("MM/yyyy").format(new Date());
        boolean anyAlerts = false;
        
        for (String category : CATEGORIES) {
            double spent = 0;
            for (Expense expense : expenses) {
                if (expense.getCategory().equals(category) && expense.getDate().endsWith(currentMonth)) {
                    spent += expense.getAmount();
                }
            }
            
            double budget = budgets.getOrDefault(category, 0.0);
            if (budget > 0 && spent > budget) {
                System.out.println(RED + "⚠️  ALERT: You exceeded " + category + " budget!" + RESET);
                System.out.printf("   Spent: $%.2f | Budget: $%.2f | Over by: $%.2f\n\n", spent, budget, spent - budget);
                anyAlerts = true;
            } else if (budget > 0 && spent > budget * 0.8) {
                System.out.println(YELLOW + "⚠️  WARNING: " + category + " budget almost reached!" + RESET);
                System.out.printf("   Spent: $%.2f | Budget: $%.2f | Left: $%.2f\n\n", spent, budget, budget - spent);
                anyAlerts = true;
            }
        }
        
        if (!anyAlerts) {
            System.out.println(GREEN + "✅ All budgets are under control! Good job!" + RESET);
        }
    }
    
    private void deleteExpense() {
        viewAllExpenses();
        if (expenses.isEmpty()) return;
        
        int idToDelete = getIntInput("Enter expense ID to delete: ");
        for (int i = 0; i < expenses.size(); i++) {
            if (expenses.get(i).getId() == idToDelete) {
                expenses.remove(i);
                saveToFile();
                System.out.println(GREEN + "Expense deleted successfully!" + RESET);
                return;
            }
        }
        System.out.println(RED + "Expense with ID " + idToDelete + " not found!" + RESET);
    }
    // ADD THIS CALCULATOR METHOD
private void calculatorMode() {
    System.out.println(CYAN + "\n🧮 CALCULATOR MODE" + RESET);
    System.out.println("Quickly calculate expenses before adding them!");
    System.out.println("Enter amounts separated by +, -, *, /");
    System.out.println("Example: 25+18+42 or 100-15 or 50*0.18" + RESET);
    
    while (true) {
        System.out.print("\nEnter calculation (or 'back' to return): ");
        String input = scanner.nextLine().trim();
        
        if (input.equalsIgnoreCase("back")) {
            return;
        }
        
        if (input.isEmpty()) {
            continue;
        }
        
        try {
            double result = evaluateExpression(input);
            System.out.printf(GREEN + "✅ Result: $%.2f\n" + RESET, result);
            
            // Ask if user wants to add as expense
            System.out.print("Add this amount as expense? (y/n): ");
            String addExpense = scanner.nextLine().trim();
            
            if (addExpense.equalsIgnoreCase("y")) {
                quickAddExpense(result);
            }
            
        } catch (Exception e) {
            System.out.println(RED + "❌ Invalid calculation. Use format: 25+18+42" + RESET);
        }
    }
}

// Helper method to evaluate math expressions
private double evaluateExpression(String expression) {
    // Remove spaces and $ signs for easier parsing
    expression = expression.replaceAll("\\s+", "").replace("$", "");
    
    // Simple expression evaluation (supports +, -, *, /)
    if (expression.contains("+")) {
        String[] parts = expression.split("\\+");
        double sum = 0;
        for (String part : parts) {
            sum += Double.parseDouble(part);
        }
        return sum;
    } else if (expression.contains("-")) {
        String[] parts = expression.split("\\-");
        double result = Double.parseDouble(parts[0]);
        for (int i = 1; i < parts.length; i++) {
            result -= Double.parseDouble(parts[i]);
        }
        return result;
    } else if (expression.contains("*")) {
        String[] parts = expression.split("\\*");
        double result = 1;
        for (String part : parts) {
            result *= Double.parseDouble(part);
        }
        return result;
    } else if (expression.contains("/")) {
        String[] parts = expression.split("\\/");
        double result = Double.parseDouble(parts[0]);
        for (int i = 1; i < parts.length; i++) {
            result /= Double.parseDouble(parts[i]);
        }
        return result;
    } else {
        // Single number
        return Double.parseDouble(expression);
    }
}

// Quick add expense from calculator result
private void quickAddExpense(double amount) {
    System.out.println("\n--- Quick Add Expense ---");
    
    System.out.println("Select category:");
    for (int i = 0; i < CATEGORIES.length; i++) {
        System.out.println((i + 1) + ". " + CATEGORIES[i]);
    }
    int catChoice = getIntInput("Enter category number: ") - 1;
    
    if (catChoice < 0 || catChoice >= CATEGORIES.length) {
        System.out.println(RED + "Invalid category!" + RESET);
        return;
    }
    
    String category = CATEGORIES[catChoice];
    
    // Use current date
    String currentDate = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
    
    System.out.print("Enter description: ");
    String description = scanner.nextLine();
    
    Expense expense = new Expense(nextId++, amount, category, currentDate, description);
    expenses.add(expense);
    saveToFile();
    
    System.out.println(GREEN + "✅ Expense added successfully from calculator!" + RESET);
}
    private int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println(RED + "Please enter a valid number!" + RESET);
            }
        }
    }
    
    private double getDoubleInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println(RED + "Please enter a valid amount!" + RESET);
            }
        }
    }
    
    public static void main(String[] args) {
        ExpenseTracker tracker = new ExpenseTracker();
        tracker.start();
    }
}