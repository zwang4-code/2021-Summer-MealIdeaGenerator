/*
 * Personal Project
 */

package MealIdea;

import java.io.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;

/**
 * This MealIdea program randomly generates meal ideas based on user-selected culture
 * category or randomly generated culture category.
 *
 * @author Zi Wang
 * @date Jun 28, 2021
 */

public class MealIdea {

    public static void main(String[] arg) throws IOException {

        ArrayList<String> categoryList = new ArrayList();
        ArrayList<String> dishList = new ArrayList<>();

        Scanner keyboard = new Scanner(System.in);

        welcome();
        viewMenu();
        pickFromMenu(keyboard, categoryList, dishList);
        goodbye();

        keyboard.close();
    }

    private static void welcome() {
        System.out.println("Welcome to Meal Idea Generator where your stomach's happiness is our " +
                "top priority!\nLet's begin.");
    }

    private static void viewMenu() {
        System.out.println("\nMain Menu: \n" +
                " 1. View all the dishes within a chosen culture category. \n" +
                " 2. Insert a new culture category \n" +
                " 3. Insert a new dish and its category\n" +
                " 4. Select a culture category and then randomly generate a dish.\n" +
                " 5. Randomly generate category and dish");
    }

    private static void pickFromMenu(Scanner keyboard, ArrayList<String> categoryList,
                                     ArrayList<String> dishList) throws IOException {

        int menuPick;

        File categoryFile = new File("AllCategories.txt");

        do {
            menuPick = getMenuPick(keyboard);
            if (menuPick != -1) {
                switch (menuPick) {
                    case 1:
                        File childFile = getChildFile(keyboard, categoryFile, categoryList);
                        viewAllDishes(childFile, dishList);
                        break;
                    case 2:
                        insertNewCategory(keyboard, categoryFile);
                        break;
                    case 3:
                        insertNewDish(keyboard, categoryFile);
                        break;
                    case 4:
                        getRandomDish(keyboard, categoryFile, categoryList, dishList);
                        break;
                    case 5:
                        getTotalRandomDish(keyboard, categoryFile, categoryList, dishList);
                        break;
                }
            }
        } while (menuPick != -1);
    }

    private static int getMenuPick(Scanner keyboard) {
        int menuPick = -1;
        boolean invalid;
        do {
            invalid = false;
            System.out.print(ANSI.ANSI_GREEN);
            System.out.print("\nWhat would you like to do?" +
                    "\nEnter a number from menu or -1 to exit or -2 to see the menu again: ");
            System.out.print(ANSI.ANSI_RESET);
            try {
                menuPick = keyboard.nextInt();
                if (menuPick == -1)
                    return menuPick;
                if (menuPick == -2) {
                    viewMenu();
                    return getMenuPick(keyboard);
                }
                if (menuPick < 1 || menuPick > 6) {
                    System.out.print(ANSI.ANSI_RED);
                    System.out.println("Error: Please pick a number between 1 and 6.");
                    System.out.print(ANSI.ANSI_RESET);
                    invalid = true;
                }
            } catch (InputMismatchException err) {
                System.out.print(ANSI.ANSI_RED);
                System.out.println("Error: Please enter an integer.");
                System.out.print(ANSI.ANSI_RESET);
                invalid = true;
            }
            keyboard.nextLine();
        } while (invalid);

        return menuPick;
    }

    /* Option 1: view all the dishes within a category */
    private static void viewAllDishes(File childFile, ArrayList<String> dishList)
            throws FileNotFoundException {

        saveChildDishes(childFile, dishList);
        if (!dishList.isEmpty()) {
            System.out.print(ANSI.ANSI_PURPLE);
            System.out.println("\nHere is a list of dishes within this category: ");
            for (String dish : dishList) {
                System.out.println("\t" + dish);
            }
            System.out.print(ANSI.ANSI_RESET);
        }
    }

    // Option 1 helper
    private static void viewCategoryList(File file, ArrayList<String> categoryList)
            throws FileNotFoundException {
        saveCategoryList(file, categoryList);

        if (categoryList.size() != 0) {
            System.out.print(ANSI.ANSI_PURPLE);
            System.out.println("\nHere are all the culture categories: ");
            for (int i = 0; i < categoryList.size(); i++) {
                System.out.println((i + 1) + ". " + categoryList.get(i));
            }
            System.out.print(ANSI.ANSI_RESET);
        }
    }

    // Option 1 & 5 helper
    private static void saveCategoryList(File file, ArrayList<String> categoryList)
            throws FileNotFoundException {
        categoryList.clear();
        if (!file.exists()) {
            System.out.print(ANSI.ANSI_YELLOW);
            System.out.println("Warning: There is no category yet.");
            System.out.print(ANSI.ANSI_RESET);
        } else {
            Scanner inputFile = new Scanner(file);
            while (inputFile.hasNext()) {
                categoryList.add(inputFile.nextLine());
            }
            inputFile.close();
        }
    }

    // Option 1 & 4 helper
    private static File getChildFile(Scanner keyboard, File file, ArrayList<String> categoryList)
            throws FileNotFoundException {
        String input;
        String category = "";
        String childFileName;

        System.out.print(ANSI.ANSI_PURPLE);
        System.out.print("\nEnter a culture category or ? to pick from a list: ");
        System.out.print(ANSI.ANSI_RESET);
        input = keyboard.nextLine();

        if (input.charAt(0) == '?') {
            category = pickFromList(keyboard, file, categoryList);
        } else {
            category = capitalizeCategoryName(input);
        }

        childFileName = createChildFile(category);
        File childFile = new File(childFileName);
        return childFile;
    }

    // Option 1 & 4 helper
    private static void saveChildDishes(File childFile, ArrayList<String> dishList)
            throws FileNotFoundException {
        dishList.clear();
        if (!childFile.exists()) {
            System.out.print(ANSI.ANSI_YELLOW);
            System.out.println("Warning: This culture category is not found or contains no dish yet.");
            System.out.print(ANSI.ANSI_RESET);
        } else {
            Scanner inputFile = new Scanner(childFile);
            while (inputFile.hasNext()) {
                String dish = inputFile.nextLine();
                dishList.add(dish);
            }
            inputFile.close();
        }
    }

    // Option 1 helper
    private static String pickFromList(Scanner keyboard, File file, ArrayList<String> categoryList)
            throws FileNotFoundException {
        int number = -1;
        boolean invalid;
        viewCategoryList(file, categoryList);
        do {
            invalid = false;
            System.out.print(ANSI.ANSI_PURPLE);
            System.out.print("\nPick a category by entering a number: ");
            System.out.print(ANSI.ANSI_RESET);
            try {
                number = keyboard.nextInt();
                if (number < 1 || number > categoryList.size()) {
                    System.out.print(ANSI.ANSI_RED);
                    System.out.print("Error: Please pick a number between 1 and " + categoryList.size() + ": ");
                    invalid = true;
                    System.out.print(ANSI.ANSI_RESET);
                }
            } catch (InputMismatchException err) {
                System.out.print(ANSI.ANSI_RED);
                System.out.println("Error: Please enter an integer.");
                System.out.print(ANSI.ANSI_RESET);
                invalid = true;
            }
            keyboard.nextLine();
        } while (invalid);
        return categoryList.get(number - 1);
    }

    // Option 1, 3, & 5 helper
    private static String createChildFile(String category) {
        StringBuilder childFile = new StringBuilder();
        childFile.append(category);
        childFile.append("Dishes.txt");

        return childFile.toString();
    }

    // Option 1, 2, & 3 helper: this step trims the category name of leading and trailing
    // whitespaces, capitalize the first letter, and convert the rest to lowercase.
    private static String capitalizeCategoryName(String categoryName) {
        String category = categoryName.trim();

        StringBuilder capitalizedName = new StringBuilder();

        capitalizedName.append(Character.toUpperCase(category.charAt(0)));

        for (int i = 1; i < category.length(); i++)
            capitalizedName.append(Character.toLowerCase(category.charAt(i)));

        return capitalizedName.toString();
    }

    /* Option 2: insert a new culture category. */
    private static void insertNewCategory(Scanner keyboard, File file) throws IOException {
        String newCategory;
        String capitalizedCategory;

        System.out.print(ANSI.ANSI_PURPLE);
        System.out.print("\nWhat cultural category would you like to insert? ");
        System.out.print(ANSI.ANSI_RESET);
        newCategory = keyboard.nextLine();

        capitalizedCategory = capitalizeCategoryName(newCategory);

        // If file doesn't exist, create the file and add the category.
        if (!file.exists()) {
            createCategoryFile(capitalizedCategory);
        } else {
            // If file exists, check if category already exists.
            Scanner inputFile = new Scanner(file);
            while (inputFile.hasNext()) {
                String cultureCategory = inputFile.next();
                if (capitalizedCategory.equalsIgnoreCase(cultureCategory)) {
                    System.out.print(ANSI.ANSI_YELLOW);
                    System.out.println("Warning: This category already exists.");
                    System.out.print(ANSI.ANSI_RESET);
                    return;
                }
            }
            inputFile.close();

            // If category doesn't exist yet, append it to the file.
            addNewCategory(capitalizedCategory);
        }
    }

    // Option 2 & 3 helper
    private static void createCategoryFile(String newCategory) throws FileNotFoundException {
        PrintWriter outputFile = new PrintWriter("AllCategories.txt");
        outputFile.println(newCategory);
        outputFile.close();
    }

    // Option 2 & 3 helper
    private static void addNewCategory(String newCategory) throws IOException {
        FileWriter fw = new FileWriter("AllCategories.txt", true);
        PrintWriter outputFile = new PrintWriter(fw);
        outputFile.println(newCategory);
        outputFile.close();
    }

    /* Option 3: insert a new dish. */
    private static void insertNewDish(Scanner keyboard, File file) throws IOException {
        String newDish, dishCategory, capitalizedCategory, childFile;

        System.out.print(ANSI.ANSI_BLUE);
        System.out.print("\nWhat new dish would you like to add? ");
        newDish = keyboard.nextLine();

        System.out.print("\nWhat culture does this cuisine belong to? ");
        dishCategory = keyboard.nextLine();
        System.out.print(ANSI.ANSI_RESET);

        capitalizedCategory = capitalizeCategoryName(dishCategory);
        childFile = createChildFile(capitalizedCategory);

        // Check if the category exists. If file doesn't exist, create file and add category
        if (!file.exists()) {
            createCategoryFile(capitalizedCategory);
        } else {
            // If file exists, check if category already exists
            Scanner inputFile = new Scanner(file);
            boolean notFound = true;    // whether the category already exists
            while (notFound && inputFile.hasNext()) {
                String currentCategory = inputFile.next();
                // If the category already exists, open the child file and add the dish.
                if (capitalizedCategory.equalsIgnoreCase(currentCategory)) {
                    notFound = false;
                    appendNewDish(childFile, newDish);
                }
            }
            inputFile.close();

            // If category not found, add it to the file. Then create a child file and add new dish.
            if (notFound) {
                addNewCategory(capitalizedCategory);
                addNewDish(childFile, newDish);
            }
        }
    }

    // Option 3 helper
    private static void appendNewDish(String fileName, String newDish) throws IOException {
        FileWriter fw = new FileWriter(fileName, true);
        PrintWriter outputFile = new PrintWriter(fw);
        outputFile.println(newDish);
        outputFile.close();
    }

    // Option 3 helper
    private static void addNewDish(String fileName, String newDish) throws FileNotFoundException {
        PrintWriter outputFile = new PrintWriter(fileName);
        outputFile.println(newDish);
        outputFile.close();
    }

    /* Option 4: Select a culture category and randomly generate a dish. */
    private static void getRandomDish(Scanner keyboard, File file, ArrayList<String> categoryList,
                                      ArrayList<String> dishList) throws FileNotFoundException {

        boolean satisfy;
        String changeCategory = "";

        do {
            File childFile = getChildFile(keyboard, file, categoryList);
            do {
                satisfy = generateRandomDish(keyboard, childFile, dishList);
                if (!satisfy) {
                    System.out.print(ANSI.ANSI_PURPLE);
                    System.out.print("\nWould you like to generate a dish from a different category? ");
                    changeCategory = keyboard.nextLine();
                    System.out.print(ANSI.ANSI_RESET);
                }
            } while (!satisfy && (changeCategory.charAt(0) == 'N' || changeCategory.charAt(0) == 'n'));
        } while (!satisfy);
    }

    // Option 4 & 5 helper
    private static boolean generateRandomDish(Scanner keyboard, File childFile, ArrayList<String> dishList)
            throws FileNotFoundException {

        String satisfy = "";
        Random randomPick = new Random();
        saveChildDishes(childFile, dishList);

        if (dishList.size() != 0) {
            int randomNum = randomPick.nextInt(dishList.size());
            System.out.print(ANSI.ANSI_CYAN);
            System.out.println("\nThe dish we have picked for you is\n>> " + dishList.get(randomNum) + " << !!!");

            System.out.print("\nAre you satisfied with the dish? ");
            satisfy = keyboard.nextLine();
            System.out.print(ANSI.ANSI_RESET);
            return satisfy.charAt(0) == 'Y' || satisfy.charAt(0) == 'y';
        }
        return false;
    }

    /* Option 5: Randomly generate a category and then a dish. */
    private static void getTotalRandomDish(Scanner keyboard, File
            file, ArrayList<String> categoryList,
                                           ArrayList<String> dishList) throws FileNotFoundException {

        boolean satisfy;
        saveCategoryList(file, categoryList);

        do {
            File childFile = new File(generateRandomCategory(categoryList));
            satisfy = generateRandomDish(keyboard, childFile, dishList);
        } while (!satisfy);
    }

    // Option 5 helper: This method generates a random child file name
    private static String generateRandomCategory(ArrayList<String> categoryList) {

        String randomCategory;
        Random randomPick = new Random();
        int randomNum = randomPick.nextInt(categoryList.size());
        randomCategory = categoryList.get(randomNum);
        System.out.println("\nThe category we have picked for you is\n>> " + randomCategory + " <<!!!");
        return createChildFile(randomCategory);
    }

    private static void goodbye() {
        System.out.println("\nThank you for using the Meal Idea Generator!\n" +
                "Have a good day and bon appetit!");
    }
}
