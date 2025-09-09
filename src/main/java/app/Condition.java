package app;

public class Condition {
    private String conditionID;
    private String diseaseName;
    private String description;
    
    public Condition() {}

    public Condition(String conditionID, String diseaseName, String description) {
        this.conditionID = conditionID;
        this.diseaseName = diseaseName;
        this.description = description;
    }

    public String getConditionID() {
        return conditionID;
    }

    public void setConditionID(String conditionID) {
        this.conditionID = conditionID;
    }

    public String getDiseaseName() {
        return diseaseName;
    }

    public void setDiseaseName(String diseaseName) {
        this.diseaseName = diseaseName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
}
