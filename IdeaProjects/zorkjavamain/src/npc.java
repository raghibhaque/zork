import javafx.scene.control.TextArea;

import java.io.Serial;
import java.io.Serializable;

class NPC extends Character implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String dialogue;

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
        if (outputArea == null) {
            System.out.print(dialogue);
        } else {
            outputArea.appendText(dialogue + "\n");
        }
    }
    public String speak() {
        return dialogue;
    }


    @Override
    public String toString() {
        return "NPC{name='" + getName() + "', room='" + getCurrentRoom().getDescription() + "'}";
    }
}

