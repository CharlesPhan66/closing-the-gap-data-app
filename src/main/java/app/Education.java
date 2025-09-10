package app;

public class Education {
    private String levelID;
    private String level;

    public Education(String levelID, String level) {
        this.levelID = levelID;
        this.level = level;
    }

    public String getLevelID() {
        return levelID;
    }

    public String getLevel() {
        return level;
    }
}
