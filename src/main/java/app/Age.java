package app;

public class Age {
    private int ageID;
    private int ageStart;
    private int ageEnd;

    public Age(int ageID, int ageStart, int ageEnd) {
        this.ageID = ageID;
        this.ageStart = ageStart;
        this.ageEnd = ageEnd;
    }

    public int getAgeID() {
        return ageID;
    }

    public int getAgeStart() {
        return ageStart;
    }

    public int getAgeEnd() {
        return ageEnd;
    }
}
