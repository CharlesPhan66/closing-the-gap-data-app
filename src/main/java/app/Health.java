package app;

public class Health {
    private String stateName;
    private String lgaName;
    private String sex;
    private String status;
    private String disease;
    private int populationValue;
    private double percentage;
    private int rank = 0;

    public Health() {}

    public Health(String stateName, String lgaName, String sex, String status, String disease, int populationValue) {
        this.stateName = stateName;
        this.lgaName = lgaName;
        this.sex = sex;
        this.status = status;
        this.disease = disease;
        this.populationValue = populationValue;
    }

    public Health(String lgaName, String sex, String status, String disease, int populationValue) {
        this(null, lgaName, sex, status, disease, populationValue);
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getFormattedPercentage() {
        return String.format("%.2f%%", this.percentage);
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getLgaName() {
        return lgaName;
    }

    public void setLgaName(String lgaName) {
        this.lgaName = lgaName;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public int getPopulationValue() {
        return populationValue;
    }

    public void setPopulationValue(int populationValue) {
        this.populationValue = populationValue;
    }
    

}
