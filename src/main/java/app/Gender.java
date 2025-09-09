package app;

public class Gender {
    private String sexID;
    private String sex;

    public Gender(String sexID, String sex) {
        this.sexID = sexID;
        this.sex = sex;
    }

    public String getSexID() {
        return sexID;
    }

    public String getSex() {
        return sex;
    }
}
