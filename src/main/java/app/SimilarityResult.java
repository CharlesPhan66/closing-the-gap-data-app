package app;

public class SimilarityResult {
    private String lgaCode;
    private String lgaName;
    private int population;
    // FIX: This field will now store the raw difference (+/-)
    private int rawDiff;

    public SimilarityResult(String lgaCode, String lgaName, int population, int rawDiff) {
        this.lgaCode = lgaCode;
        this.lgaName = lgaName;
        this.population = population;
        this.rawDiff = rawDiff;
    }

    public String getLgaCode() { return lgaCode; }
    public String getLgaName() { return lgaName; }
    public int getPopulation() { return population; }
    // FIX: Renamed getter for clarity
    public int getRawDiff() { return rawDiff; }
}