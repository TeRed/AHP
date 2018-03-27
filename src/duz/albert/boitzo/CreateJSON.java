package duz.albert.boitzo;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.*;
import java.util.Scanner;


public class CreateJSON {
    private static void getPreferences(JSONObject currentGoal, JSONArray categories, Scanner reader) {
        //((JSONObject) categories.get(i)).get("name") +

        double[][] tmpDoublePreferences = new double[categories.size()][];
        for(int i = 0; i < categories.size(); i++) tmpDoublePreferences[i] = new double[categories.size()];

        for(int i = 0; i < categories.size(); i++) {
            for(int j = 0; j < i; j++) {
                System.out.println("How do you prefer " +
                        (categories.get(i) instanceof JSONObject ? ((JSONObject) categories.get(i)).get("name") : categories.get(i))  +
                        " over " +
                        (categories.get(j) instanceof JSONObject ? ((JSONObject) categories.get(j)).get("name") : categories.get(j))  +
                        "?");
                tmpDoublePreferences[i][j] = Double.parseDouble(reader.nextLine());
                tmpDoublePreferences[j][i] = 1 / tmpDoublePreferences[i][j];
            }
            tmpDoublePreferences[i][i] = 1;
        }

        JSONArray mainPreferences = new JSONArray();

        for(int i = 0; i < categories.size(); i++) {
            JSONArray rowPreferences = new JSONArray();

            for(int j = 0; j < categories.size(); j++) {
                rowPreferences.add(tmpDoublePreferences[i][j]);
            }
            mainPreferences.add(rowPreferences);
        }

        currentGoal.put("preferences", mainPreferences);
    }

    private static JSONObject category(String path, String name, JSONArray alternatives, Scanner reader) throws TooManyCategories {

        // Reading childrens
        JSONObject currentGoal = new JSONObject();
        currentGoal.put("name", name);

        JSONArray currentCategories = new JSONArray();
        while(true) {
            String currentPath = path == "" ? name : path + "#" + name;

            System.out.println(currentPath +
                    "$ Give category name (empty line to stop recursion of categories):");

            String answer = reader.nextLine();
            if(answer.isEmpty()) break;
            if("!".equals(answer)) throw new TooManyCategories();


            try {
                currentCategories.add(category(currentPath, answer, alternatives, reader));
            } catch(TooManyCategories e) {}
        }

        // Rating children
        if(currentCategories.isEmpty()) { // Our alternatives are children
            currentGoal.put("children", "alternatives");
            getPreferences(currentGoal, alternatives, reader);
        }
        else { // Our alternatives are categories
            currentGoal.put("children", currentCategories);
            getPreferences(currentGoal, currentCategories, reader);
        }

        return currentGoal;
    }

    private static void printJson(JSONObject ahp, Scanner reader) {
        // Ask user for file to write
        // System.out.println() as alternatives
        PrintWriter writer = null;

        // Getting file if possible
        System.out.println("Enter path to file to write JSON:");

        String path = reader.nextLine();
        try {
            writer = new PrintWriter(path, "UTF-8");
        } catch(FileNotFoundException  e) {
            System.out.println("Unknown file!");
        } catch(IOException e) {
            System.out.println("Exception occured");
            System.exit(1);
        }

        // Preparing to print JSON Object
        StringWriter out = new StringWriter();
        try {
            ahp.writeJSONString(out);
        } catch (IOException e) {
            System.out.println("Exception occured");
            System.exit(1);
        }

        // Printing to console
        System.out.println("JSON:");
        System.out.print(out.toString());

        // Printing to file if possible
        if(writer != null) {
            writer.println(out.toString());
            writer.close();
        }
    }

    private static JSONObject buildAhpObject(Scanner reader) {
        String answer;
        JSONObject ahp = new JSONObject();

        System.out.println("Enter alternatives (empty line to stop):");

        // Getting alternatives
        JSONArray array = new JSONArray();
        while(true) {
            answer = reader.nextLine();
            if(answer.isEmpty()) break;
            array.add(answer);
        }
        ahp.put("alternatives", array);

        // Getting main goal and others recursively
        while(true) {
            System.out.println("Enter your main goal:");
            answer = reader.nextLine();
            try {
                ahp.put("goal", category("", answer, array, reader));
                break;
            } catch(TooManyCategories e) {}
        }

        return ahp;
    }

    public static void createJson(Scanner reader) {
        JSONObject ahp = buildAhpObject(reader);
        printJson(ahp, reader);
    }
}
