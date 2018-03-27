package duz.albert.boitzo;

import java.util.Scanner;

public class MainProgram {
    public static void main(String[] args) {
        Scanner reader = new Scanner(System.in);

        while(true) {
            String option = "0";
            System.out.print("[0] Close\n[1] Creating JSON\n[2] Reading JSON\n");
            option = reader.nextLine();
            if("1".equals(option)) CreateJSON.createJson(reader);
            else if("2".equals(option)) ReadJSON.readJson(reader);
            else if("0".equals(option)) break;
        }

        reader.close();
    }
}
