package app;

public class PopulationGapResult {
    public String lga;
    public Integer ageStart; // nullable, only for view by age groups
    public Integer ageEnd;   // nullable, only for view by age groups
    public Integer status1_2016;
    public Integer status2_2016;
    public Integer gap_2016;
    public Integer status1_2021;
    public Integer status2_2021;
    public Integer gap_2021;

    // For normal/total view
    public PopulationGapResult(String lga, Integer status1_2016, Integer status2_2016, Integer gap_2016, Integer status1_2021, Integer status2_2021, Integer gap_2021) {
        this.lga = lga;
        this.ageStart = null;
        this.ageEnd = null;
        this.status1_2016 = status1_2016;
        this.status2_2016 = status2_2016;
        this.gap_2016 = gap_2016;
        this.status1_2021 = status1_2021;
        this.status2_2021 = status2_2021;
        this.gap_2021 = gap_2021;
    }

    // For view by age groups
    public PopulationGapResult(String lga, Integer ageStart, Integer ageEnd, Integer status1_2016, Integer status2_2016, Integer gap_2016, Integer status1_2021, Integer status2_2021, Integer gap_2021) {
        this.lga = lga;
        this.ageStart = ageStart;
        this.ageEnd = ageEnd;
        this.status1_2016 = status1_2016;
        this.status2_2016 = status2_2016;
        this.gap_2016 = gap_2016;
        this.status1_2021 = status1_2021;
        this.status2_2021 = status2_2021;
        this.gap_2021 = gap_2021;
    }
}