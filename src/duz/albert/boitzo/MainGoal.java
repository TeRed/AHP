package duz.albert.boitzo;

public class MainGoal {
    String[] alternatives;
    Goal goal;

    @Override
    public String toString() {
        String value = "{\nAlternatives:\n[";
        for(int i = 0; i < alternatives.length; i++)
            value += (alternatives[i] + ", ");

        value = value.substring(0, value.length() - 2);
        value += "]\n";
        value += "goal:\n";

        value += goal.toString();
        value += "}\n";

        return value;
    }
}
