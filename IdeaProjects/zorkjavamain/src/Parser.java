import java.util.Scanner;

public class Parser {
    private CommandWords commands;
    private Scanner reader;

    public Parser() {
        commands = new CommandWords();
        reader = new Scanner(System.in);
    }

    public Command getCommand() {
        System.out.print("> ");

        String inputLine = reader.nextLine().trim();
        String word1 = null, word2 = null, word3 = null;

        String[] words = inputLine.split("\\s+");

        if (words.length > 0) word1 = words[0];
        if (words.length > 1) word2 = words[1];
        if (words.length > 2) word3 = words[2];

        // FIX: use word1, not w1
        CommandWord cw = commands.getCommandWord(word1);

        return new Command(cw, word2, word3);
    }

    public void showCommands() {
        commands.showAll();
    }
}
