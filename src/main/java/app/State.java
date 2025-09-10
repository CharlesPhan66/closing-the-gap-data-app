package app;

public class State {
    private int stateID;
    private String name;

    public State(int stateID, String name) {
        this.stateID = stateID;
        this.name = name;
    }

    public int getStateID() {
        return stateID;
    }

    public String getName() {
        return name;
    }
}
