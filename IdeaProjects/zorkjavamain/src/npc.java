import javafx.scene.control.TextArea;

import java.io.Serial;
import java.io.Serializable;

class NPC extends Character implements Serializable {
    private String dialogue; // what this NPC says when interacted with
    @Serial
    private static final long serialVersionUID = 1L;
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

    public void speak(TextArea outputArea) {
        outputArea.appendText(getName() + " says: \"" + dialogue + "\"\n");
    }


    @Override
    public String toString() {
        return "NPC{name='" + getName() + "', room='" + getCurrentRoom().getDescription() + "'}";
    }
}
