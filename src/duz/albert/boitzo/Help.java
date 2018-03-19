package duz.albert.boitzo;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

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

            JSONArray mainPreferences = new JSONArray();


            for(int i = 0; i < alternatives.size(); i++) {
                JSONArray rowPreferences = new JSONArray();

                for(int j = 0; j < alternatives.size(); j++) {
                    System.out.println("How do you prefer " + alternatives.get(i) + " over " + alternatives.get(j) + "?");
                    answer = reader.nextLine();
                    rowPreferences.add(answer);
                }
                mainPreferences.add(rowPreferences);
            }

            category.put("preferences", mainPreferences);
        }
        else {
            category.put("children", array);
            JSONArray mainPreferences = new JSONArray();


            for(int i = 0; i < array.size(); i++) {
                JSONArray rowPreferences = new JSONArray();

                for(int j = 0; j < array.size(); j++) {
                    System.out.println("How do you prefer " + ((JSONObject) array.get(i)).get("name") + " over " + ((JSONObject) array.get(j)).get("name") + "?");
                    answer = reader.nextLine();
                    rowPreferences.add(answer);
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

        reader.close();
    }
}
