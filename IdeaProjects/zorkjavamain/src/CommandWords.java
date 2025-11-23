import java.util.HashMap;

public class CommandWords {

    private HashMap<String, CommandWord> commands;

    public CommandWords() {
        commands = new HashMap<>();

        // Load all enum names into the map
        for (CommandWord cw : CommandWord.values()) {
            if (cw != CommandWord.UNKNOWN) {
                commands.put(cw.name().toLowerCase(), cw);
            }
        }
    }

    public CommandWord getCommandWord(String word) {
        CommandWord cw = commands.get(word.toLowerCase());
        return (cw != null) ? cw : CommandWord.UNKNOWN;
    }

    public boolean isCommand(String word) {
        return commands.containsKey(word.toLowerCase());
    }

    public void showAll() {
        System.out.print("Valid commands are: ");
        for (String cmd : commands.keySet()) {
            System.out.print(cmd + " ");
        }
        System.out.println();
    }
}
