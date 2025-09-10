package app;

public class Priorities {
    
    private String priorityID;
    private String description;

public Priorities () {}

public Priorities(String priorityID, String description) {
    this.priorityID = priorityID;
    this.description = description;
}

public String getPriorityID() {
    return priorityID;
}

public String getDescription() {
    return description;
}

}
