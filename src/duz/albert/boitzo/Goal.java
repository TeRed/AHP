package duz.albert.boitzo;

public class Goal {
    String name;
    double[][] preferences;
    Goal[] children;

    @Override
    public String toString() {
        String value = "{\n";

        value += "name: " + name + "\n";
        value += "preferences:\n";

        for(int i = 0; i < preferences.length; i++)
        {
            for(int j = 0; j < preferences[i].length; j++)
                value += preferences[i][j] + " ";

            value += "\n";
        }

        value += "children:[\n";
        if(children == null) value += " alternatives\n";
        else {
            for(int i = 0; i < children.length; i++) {
                value += children[i].toString();
            }
        }
        value += "]\n}\n";

        return value;
    }
}
