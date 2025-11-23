public class Command {
    private CommandWord commandWord;
    private String secondWord;
    private String thirdWord;

    public Command(CommandWord commandWord, String secondWord, String thirdWord) {
        this.commandWord = commandWord;
        this.secondWord = secondWord;
        this.thirdWord = thirdWord;
    }

    public CommandWord getCommandWord() {
        return commandWord;
    }

    public String getSecondWord() {
        return secondWord;
    }

    public String getThirdWord() {
        return thirdWord;
    }

    public boolean hasSecondWord() {
        return secondWord != null;
    }

    public boolean hasThirdWord() {
        return thirdWord != null;
    }

    public boolean isUnknown() {
        return commandWord == CommandWord.UNKNOWN;
    }
}
