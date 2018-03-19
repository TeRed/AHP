package duz.albert.boitzo;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Scanner;

public class Help {
    public static JSONObject category(String path, String name, JSONArray alternatives) {
        Scanner reader = new Scanner(System.in);
        String answer;

        JSONObject category = new JSONObject();
        category.put("name", name);

        JSONArray array = new JSONArray();
        while(true) {
            System.out.println(path + "#" + name + " : Give category name('end' to stop this level - children will inherit):");
            answer = reader.nextLine();
            if("end".equals(answer)) break;

            JSONObject tmp = category(path + "#" + name, answer, alternatives);
            array.add(tmp);
        }

        if(array.isEmpty()) {
            category.put("children", "alternatives");

            double[][] pref = new double[alternatives.size()][];
            for(int i = 0; i < alternatives.size(); i++) pref[i] = new double[alternatives.size()];

            for(int i = 0; i < alternatives.size(); i++) {
                for(int j = 0; j < i; j++) {
                    System.out.println("How do you prefer " + alternatives.get(i) + " over " + alternatives.get(j) + "?");
                    pref[i][j] = reader.nextDouble();
                    pref[j][i] = 1 / pref[i][j];
                }
                System.out.println(pref[i][i]);
                pref[i][i] = 1;
            }

            JSONArray mainPreferences = new JSONArray();

            for(int i = 0; i < alternatives.size(); i++) {
                JSONArray rowPreferences = new JSONArray();

                for(int j = 0; j < alternatives.size(); j++) {
                    rowPreferences.add(pref[i][j]);
                }
                mainPreferences.add(rowPreferences);
            }

            category.put("preferences", mainPreferences);
        }
        else {
            category.put("children", array);

            double[][] pref = new double[array.size()][];
            for(int i = 0; i < array.size(); i++) pref[i] = new double[array.size()];

            for(int i = 0; i < array.size(); i++) {
                for(int j = 0; j < i; j++) {
                    System.out.println("How do you prefer " + ((JSONObject) array.get(i)).get("name") + " over " + ((JSONObject) array.get(j)).get("name") + "?");
                    pref[i][j] = reader.nextDouble();
                    pref[j][i] = 1 / pref[i][j];
                }
                pref[i][i] = 1;
            }

            JSONArray mainPreferences = new JSONArray();

            for(int i = 0; i < array.size(); i++) {
                JSONArray rowPreferences = new JSONArray();

                for(int j = 0; j < array.size(); j++) {
                    rowPreferences.add(pref[i][j]);
                }
                mainPreferences.add(rowPreferences);
            }

            category.put("preferences", mainPreferences);
        }

        return category;
    }

    public static void main(String[] args) {
        Scanner reader = new Scanner(System.in);
        String answer;

        JSONObject ahp = new JSONObject();

        System.out.println("Enter alternatives('end' to stop):");

        JSONArray array = new JSONArray();
        while(true) {
            answer = reader.nextLine();
            if("end".equals(answer)) break;
            array.add(answer);
        }
        ahp.put("alternatives", array);




        System.out.println("Enter your main goal:");
        answer = reader.nextLine();

        ahp.put("goal", category("", answer, array));


        //Printing JSON
        StringWriter out = new StringWriter();
        try {
            ahp.writeJSONString(out);
        } catch (Exception e) {}

        String jsonText = out.toString();
        System.out.print(jsonText);

        try{
            PrintWriter writer = new PrintWriter("ahp.json", "UTF-8");
            writer.println(jsonText);
            writer.close();
        } catch(Exception e) {}

        reader.close();
    }
}
