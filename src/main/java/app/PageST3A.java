package app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.javalin.http.Context;
import io.javalin.http.Handler;
/**
 * @author @charlesphan0206
 * Please refer to contribution document for details of assigned tasks.
 */
public class PageST3A implements Handler {
    // URL of this page relative to http://localhost:7001/
    public static final String URL = "/page3A.html";
    public static final String TEMPLATE = ("PageST3A.html");
    @Override
    public void handle(Context context) throws Exception {
    Map<String, Object> model = new HashMap<>();
    JDBCConnection jdbc = new JDBCConnection();

    // Get all filter lists
    ArrayList<IndigStatus> statusList = jdbc.getIndigStatus();
    ArrayList<Gender> sexList = jdbc.getSexValues();
    ArrayList<Age> ageGroupList = jdbc.getAgeGroup();
    ArrayList<Condition> healthList = jdbc.getHealthConditions();
    ArrayList<Education> eduList = jdbc.getEducation();
    ArrayList<NonSchoolEdu> nonSchoolList = jdbc.getNonSchoolEdu();

    // Set defaults for status and sex
    String status1 = context.formParam("status1");
    String status2 = context.formParam("status2");
    if (status1 == null && !statusList.isEmpty()) status1 = statusList.get(0).getStatusID();
    if (status2 == null && statusList.size() > 1) status2 = statusList.get(1).getStatusID();
    String sex1 = context.formParam("sex1");
    String sex2 = context.formParam("sex2");
    if (sex1 == null) sex1 = "both";
    if (sex2 == null) sex2 = "both";

    // Add lists and defaults to model
    model.put("statusList", statusList);
    model.put("sexList", sexList);
    model.put("ageGroupList", ageGroupList);
    model.put("healthList", healthList);
    model.put("eduList", eduList);
    model.put("nonSchoolList", nonSchoolList);
    model.put("status1", status1);
    model.put("status2", status2);
    model.put("sex1", sex1);
    model.put("sex2", sex2);

    // Render the template with model
    context.render(TEMPLATE, model);
    }

}
