package app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import io.javalin.http.Context;
import io.javalin.http.Handler;

public class PageST3B implements Handler {

    public static final String URL = "/page3B.html";
    public static final String TEMPLATE = ("PageST3B.html");

    @Override
    public void handle(Context context) throws Exception {
        Map<String, Object> model = new HashMap<>();
        JDBCConnection jdbc = new JDBCConnection();

        model.put("lgas", jdbc.getLGAsAll());
        model.put("statusList", jdbc.getIndigStatus());
        model.put("healthConditions", jdbc.getHealthConditions());
        model.put("schoolLevels", jdbc.getEducation());
        model.put("nonSchoolDegrees", jdbc.getNonSchoolEdu());

        String selectedLga = context.formParam("selectedLga");
        String year = context.formParam("year");
        String outcome = context.formParam("outcome");
        String status = context.formParam("status");
        String ageMinStr = context.formParam("ageMin");
        String ageMaxStr = context.formParam("ageMax");
        String category = context.formParam("category");
        String numLgasStr = context.formParam("numLgas");
        String sortOrder = context.formParam("sortOrder");

        if ("POST".equals(context.method())) {
            try {
                int yearInt = (year != null) ? Integer.parseInt(year) : 2021;
                int ageMin = (ageMinStr != null && !ageMinStr.isEmpty()) ? Integer.parseInt(ageMinStr) : 0;
                int ageMax = (ageMaxStr != null && !ageMaxStr.isEmpty()) ? Integer.parseInt(ageMaxStr) : 100;
                int numLgas = (numLgasStr != null && !numLgasStr.isEmpty()) ? Integer.parseInt(numLgasStr) : 5;

                // FIX: Get the chosen LGA's data separately.
                SimilarityResult targetLgaResult = jdbc.getTargetLGAData(selectedLga, yearInt, outcome, status, ageMin, ageMax, category);

                // FIX: Get the list of *other* similar LGAs. The number of LGAs is now correct.
                ArrayList<SimilarityResult> similarLgas = jdbc.findSimilarLGAs(selectedLga, yearInt, outcome, status, ageMin, ageMax, category, numLgas, sortOrder);

                // Add the separated results to the model
                model.put("targetLgaResult", targetLgaResult);
                model.put("similarLgas", similarLgas);
                
                // We create a dummy 'results' list so the 'Filters Applied' section still appears.
                // It's not used for table data anymore.
                if (targetLgaResult != null) {
                    ArrayList<SimilarityResult> results = new ArrayList<>();
                    results.add(targetLgaResult);
                    model.put("results", results);
                }


            } catch (NumberFormatException e) {
                model.put("error", "Invalid number format provided.");
            }
        }
        
        model.put("selectedLga", selectedLga);
        model.put("selectedYear", year);
        model.put("selectedOutcome", outcome);
        model.put("selectedStatus", status);
        model.put("selectedAgeMin", ageMinStr);
        model.put("selectedAgeMax", ageMaxStr);
        model.put("selectedCategory", category);
        model.put("selectedNumLgas", numLgasStr);
        model.put("selectedSortOrder", sortOrder);

        context.render(TEMPLATE, model);
    }
}
