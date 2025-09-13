package app;

public class SimilarityResult {
    private String lgaCode;
    private String lgaName;
    private int population;
    private int similarityDiff;

    public SimilarityResult(String lgaCode, String lgaName, int population, int similarityDiff) {
        this.lgaCode = lgaCode;
        this.lgaName = lgaName;
        this.population = population;
        this.similarityDiff = similarityDiff;
    }

    public String getLgaCode() { return lgaCode; }
    public String getLgaName() { return lgaName; }
    public int getPopulation() { return population; }
    public int getSimilarityDiff() { return similarityDiff; }
}