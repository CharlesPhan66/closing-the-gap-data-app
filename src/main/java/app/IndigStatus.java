package app;

public class IndigStatus {
    private String statusID;
    private String statusName;

    public IndigStatus() {}

    public IndigStatus(String statusID, String statusName) {
        this.statusID = statusID;
        this.statusName = statusName;
    }

    public String getStatusID() {
        return statusID;
    }

    public void setStatusID(String statusID) {
        this.statusID = statusID;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }
}
