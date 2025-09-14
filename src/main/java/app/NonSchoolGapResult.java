package app;

public class NonSchoolGapResult {
    public String lga;
    public String degreeName; // nullable, only for view by degree
    public Integer status1_2016;
    public Integer status2_2016;
    public Integer gap_2016;
    public Integer status1_2021;
    public Integer status2_2021;
    public Integer gap_2021;

    // For view by total
    public NonSchoolGapResult(String lga, Integer status1_2016, Integer status2_2016, Integer gap_2016, Integer status1_2021, Integer status2_2021, Integer gap_2021) {
        this.lga = lga;
        this.degreeName = null;
        this.status1_2016 = status1_2016;
        this.status2_2016 = status2_2016;
        this.gap_2016 = gap_2016;
        this.status1_2021 = status1_2021;
        this.status2_2021 = status2_2021;
        this.gap_2021 = gap_2021;
    }

    // For view by degree
    public NonSchoolGapResult(String lga, String degreeName, Integer status1_2016, Integer status2_2016, Integer gap_2016, Integer status1_2021, Integer status2_2021, Integer gap_2021) {
        this.lga = lga;
        this.degreeName = degreeName;
        this.status1_2016 = status1_2016;
        this.status2_2016 = status2_2016;
        this.gap_2016 = gap_2016;
        this.status1_2021 = status1_2021;
        this.status2_2021 = status2_2021;
        this.gap_2021 = gap_2021;
    }
}
