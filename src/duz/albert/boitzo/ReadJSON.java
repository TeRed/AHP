package duz.albert.boitzo;

import com.google.gson.*;
import weka.core.matrix.EigenvalueDecomposition;
import weka.core.matrix.Matrix;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

class ReadJSON {
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

    static void readJson(Scanner reader) {
        // Get Json
        String json = getJsonFromFile(reader);

        // Replacing childrens with nothing
        json = deleteChildrensWithNoValueFromJson(json);

        // Parsing Json
        MainGoal mainGoal = new Gson().fromJson(json, MainGoal.class);

        showAhpComparison(mainGoal);

//        System.out.println(mainGoal.goal.children[1].children[0].name);

//        System.out.println(json);
//        System.out.println("JSON:\n");

//        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//        JsonParser jp = new JsonParser();
//        JsonElement je = jp.parse(json);
//        System.out.println(gson.toJson(je));
//        System.out.println();

    }

    private static void showAhpComparison(MainGoal mainGoal) {
        double comparison[] = eigenMethod(mainGoal.goal, mainGoal.alternatives.length);

        System.out.print("Eigen Method: ");
        for(String x : mainGoal.alternatives) System.out.print(x + " ");
        System.out.println();
        for(double x : comparison) System.out.print(x + " ");
        System.out.println("\n");

        comparison = meanMethod(mainGoal.goal, mainGoal.alternatives.length);

        System.out.print("Mean Method: ");
        for(String x : mainGoal.alternatives) System.out.print(x + " ");
        System.out.println();
        for(double x : comparison) System.out.print(x + " ");
        System.out.println("\n");
    }

    private static double[] meanMethod(Goal goal, int alternativesNumber) {
        double[] priorityVector = getMeanPriorityVector(goal.preferences);

        if(goal.children == null) {
            return priorityVector;
        }

        double[][] vectors = new double[goal.children.length][];

        for(int i = 0; i < goal.children.length; i++) {
            vectors[i] = meanMethod(goal.children[i], alternativesNumber);
        }

        double[] decisionVector = new double[alternativesNumber];

        for(int i = 0; i < vectors.length; i++) {
            for(int j = 0; j < alternativesNumber; j++) {
                decisionVector[j] += priorityVector[i] * vectors[i][j];
            }
        }

        return decisionVector;
    }

    private static double[] getMeanPriorityVector(double[][] preferences) {
        double normalizationTerm = 0.0;
        double eigenVector[] = new double[preferences.length];

        for(int i = 0; i < preferences.length; i++) {
            double rowMulti = preferences[i][0];
            for(int j = 1; j < preferences[i].length; j++) {
                rowMulti *= preferences[i][j];
            }
            normalizationTerm += Math.pow(rowMulti, 1.0 / preferences.length);
            eigenVector[i] = Math.pow(rowMulti, 1.0 / preferences.length);
        }

        for(int i = 0; i < preferences.length; i++) {
            eigenVector[i] /= normalizationTerm;
        }

        return eigenVector;
    }

    private static double[] eigenMethod(Goal goal, int alternativesNumber) {
        double[] priorityVector = getEigenPriorityVector(goal.preferences);

        if(goal.children == null) {
            return priorityVector;
        }

        double[][] vectors = new double[goal.children.length][];

        for(int i = 0; i < goal.children.length; i++) {
            vectors[i] = eigenMethod(goal.children[i], alternativesNumber);
        }

        double[] decisionVector = new double[alternativesNumber];

        for(int i = 0; i < vectors.length; i++) {
            for(int j = 0; j < alternativesNumber; j++) {
                decisionVector[j] += priorityVector[i] * vectors[i][j];
            }
        }

        return decisionVector;
    }

    private static double[] getEigenPriorityVector(double[][] preferences) {
        Matrix m = new Matrix(preferences);
        EigenvalueDecomposition decomposition = m.eig();

        m = decomposition.getD();
        int index = 0;
        for(int i = 1; i < m.getRowDimension(); i++) {
            if(m.get(i,i) > m.get(index,index)) index = i;
        }

        double[] priorityVector = new double[m.getRowDimension()];

        m = decomposition.getV();
        for(int i = 0; i < m.getRowDimension(); i++) {
            priorityVector[i] = m.get(i,index);
        }

        double sum = 0;
        for(double x : priorityVector) {
            sum += Math.abs(x);
        }

        for(int i = 0; i < priorityVector.length; i++) {
            priorityVector[i] = Math.abs(priorityVector[i]) / sum;
        }

        return priorityVector;

    }
}
