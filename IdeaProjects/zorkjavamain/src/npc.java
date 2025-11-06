public class npc extends Character {
    private  String name;

    public npc(String name, Room startingRoom) {
        super();
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    @Override
    public String toString() {
        return "npc{" + "name=" + name + '}';
    }
}
