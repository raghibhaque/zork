public class Command {
    private String commandWord;
    private String secondWord;
    private String thirdWord;

    public Command(String firstWord, String secondWord, String thirdWord) {
        this.commandWord = firstWord;
        this.secondWord = secondWord;
        this.thirdWord = thirdWord;
    }

    public String getCommandWord() {
        return commandWord;
    }

    public String getSecondWord() {
        return secondWord;
    }

    public boolean isUnknown() {
        return commandWord == null;
    }

    public boolean hasSecondWord() {
        return secondWord != null;
    }
    public boolean hasThirdWord() {
        return thirdWord != null;
    }

    public String getThirdWord() {
        return thirdWord;
    }

}
