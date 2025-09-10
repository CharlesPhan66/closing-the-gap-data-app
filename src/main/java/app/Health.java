package app;

public class Health {
    private String lgaName;
    private String sex;
    private String status;
    private String disease;
    private int populationValue;

    public Health() {}

    public Health(String lgaName, String sex, String status, String disease, int populationValue) {
        this.lgaName = lgaName;
        this.sex = sex;
        this.status = status;
        this.disease = disease;
        this.populationValue = populationValue;
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
