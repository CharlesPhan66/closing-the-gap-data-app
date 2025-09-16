package app;

public class Age {
    private String ageID;
    private int ageStart;
    private int ageEnd;

    public Age(String ageID, int ageStart, int ageEnd) {
        this.ageID = ageID;
        this.ageStart = ageStart;
        this.ageEnd = ageEnd;
    }

    public String getAgeID() {
        return ageID;
    }

    public int getAgeStart() {
        return ageStart;
    }

    public int getAgeEnd() {
        return ageEnd;
    }
}
