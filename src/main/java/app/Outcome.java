package app;

public class Outcome {
    private int id;
    private String outcome;
    private String target;

    public Outcome(int id, String outcome, String target) {
        this.id = id;
        this.outcome = outcome;
        this.target = target;
    }

    public int getId() {
        return id;
    }

    public String getOutcome() {
        return outcome;
    }

    public String getTarget() {
        return target;
    }
}
