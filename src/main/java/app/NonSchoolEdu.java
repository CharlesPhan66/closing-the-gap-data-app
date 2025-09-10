package app;

public class NonSchoolEdu {
    private String d_cID;
    private String name;

    public NonSchoolEdu(String d_cID, String name) {
        this.d_cID = d_cID;
        this.name = name;
    }

    public String getDCID() {
        return d_cID;
    }

    public String getName() {
        return name;
    }
}
