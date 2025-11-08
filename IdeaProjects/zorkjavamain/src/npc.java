class NPC extends Character {
    private String dialogue; // what this NPC says when interacted with

    public NPC(String name, Room startingRoom, String dialogue) {
        super(name, startingRoom);
        this.dialogue = dialogue;
    }

    public String getDialogue() {
        return dialogue;
    }

    public void setDialogue(String dialogue) {
        this.dialogue = dialogue;
    }

    public void speak() {
        System.out.println(getName() + " says: \"" + dialogue + "\"");
    }

    @Override
    public String toString() {
        return "NPC{name='" + getName() + "', room='" + getCurrentRoom().getDescription() + "'}";
    }
}
