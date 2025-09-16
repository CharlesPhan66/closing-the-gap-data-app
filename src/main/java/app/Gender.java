package app;

public class Gender {
    private String sexID;
    private String sex;

    public Gender() {}

    public Gender(String sexID, String sex) {
        this.sexID = sexID;
        this.sex = sex;
    }

    public void setSexID(String sexID) {
        this.sexID = sexID;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getSexID() {
        return sexID;
    }

    public String getSex() {
        return sex;
    }
}
