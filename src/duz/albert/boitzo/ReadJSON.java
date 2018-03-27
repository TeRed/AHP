package duz.albert.boitzo;

import com.google.gson.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class ReadJSON {
    private static String deleteChildrensWithNoValueFromJson(String json) {

        // Replacing childrens with nothing
        // Gson will set array of childrens as null to indicate "Alternatives"

        json = json.replaceAll("\\s+","");

        json = json.replaceAll("\"children\":\"alternatives\"","");
        json = json.replaceAll("\\{,","{");
        json = json.replaceAll(",}","}");
        json = json.replaceAll(",,",",");

        return json;
    }

    private static String getJsonFromFile(Scanner reader) {

        // Reading file name and reading file text
        // Default value is "ahp.json" from current directory
        String path;
        String json = "";

        while(true) {
            System.out.println("Default value is \"ahp.json\" in current directory - ENTER to confirm");
            System.out.println("Enter path to file:");
            path = reader.nextLine();

            if(path.length() == 0) path = "ahp.json";

            try(BufferedReader br = new BufferedReader(new FileReader(path))) {
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();

                while (line != null) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                    line = br.readLine();
                }
                json = sb.toString();
            } catch(FileNotFoundException e) {
                System.out.println("Unknown file!");
                continue;
            } catch(IOException e) {
                System.out.println("Exception occurred!");
                System.exit(1);
            }
            break;
        }

        return json;
    }

    public static void readJson(Scanner reader) {
        // Get Json
        String json = getJsonFromFile(reader);

        // Replacing childrens with nothing
        json = deleteChildrensWithNoValueFromJson(json);

        // Parsing Json
        MainGoal mainGoal = new Gson().fromJson(json, MainGoal.class);

        System.out.println(json);
    }
}
