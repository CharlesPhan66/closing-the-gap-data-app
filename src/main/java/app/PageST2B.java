package app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.javalin.http.Context;
import io.javalin.http.Handler;

/**
 * Example Index HTML class using Javalin
 * <p>
 * Generate a static HTML page using Javalin
 * by writing the raw HTML into a Java String object
 *
 * @author Timothy Wiley, 2023. email: timothy.wiley@rmit.edu.au
 * @author Santha Sumanasekara, 2021. email: santha.sumanasekara@rmit.edu.au
 */
public class PageST2B implements Handler {

    // URL of this page relative to http://localhost:7001/
    public static final String URL = "/page2B.html";
    private static final String TEMPLATE = ("PageST2B.html");

    @Override
    public void handle(Context context) throws Exception {
        // Get user input from the form (if any)
        String chosenState = context.formParam("targetState");
        String chosenYear = context.formParam("targetYear");
        String chosenLGA = context.formParam("targetLGA");
        String chosenStatus = context.formParam("targetStatus");
        String chosenAge = context.formParam("targetAge");
        String chosenGender = context.formParam("targetGender");
        String chosenEduType = context.formParam("targetEduType");
        String chosenEduLevel = context.formParam("targetEduLevel");

        // Create the model to pass to Thymeleaf
        Map<String, Object> model = new HashMap<>();
        JDBCConnection jdbc = new JDBCConnection();

        // If a State and year are selected, then query the DB for all LGAs in that State
        if (chosenState != null && !chosenState.equals("none") && chosenYear != null && !chosenYear.equals("none")) {
            ArrayList<LGA> lgasInState = jdbc.getLGAsByState(chosenState, chosenYear);
            model.put("lgasInState", lgasInState);
        }

        // Always fetch and add the lists of all filters for the dropdowns
        ArrayList<State> states = jdbc.getStates();
        ArrayList<IndigStatus> statusList = jdbc.getIndigStatus();
        ArrayList<Age> ages = jdbc.getAgeGroup();
        ArrayList<Gender> genders = jdbc.getSexValues();
        ArrayList<Education> educationLevels = jdbc.getEducation();
        ArrayList<NonSchoolEdu> nonSchoolLevels = jdbc.getNonSchoolEdu();

        model.put("states", states);
        model.put("statusList", statusList);
        model.put("ages", ages);
        model.put("genders", genders);
        model.put("educationLevels", educationLevels);
        model.put("nonSchoolLevels", nonSchoolLevels);

        // Record the selections that the user made previously
        model.put("selectedState", chosenState);
        model.put("selectedYear", chosenYear);
        model.put("selectedLGA", chosenLGA);
        model.put("selectedStatus", chosenStatus);
        model.put("selectedAge", chosenAge);
        model.put("selectedGender", chosenGender);
        model.put("selectedEduType", chosenEduType);
        model.put("selectedEduLevel", chosenEduLevel);

        ArrayList<Health> eduList = new ArrayList<>();
        if ("POST".equalsIgnoreCase(context.method()) &&
            chosenState != null && !chosenState.equals("none") &&
            chosenYear != null && !chosenYear.equals("none") &&
            chosenLGA != null && !chosenLGA.equals("none")) {
            eduList = jdbc.getEducationByFilter(
                chosenYear,
                chosenLGA,
                chosenGender,
                chosenStatus,
                chosenAge,
                chosenEduType,
                (chosenEduLevel != null && !chosenEduLevel.equals("none")) ? chosenEduLevel : null
            );
        }
        model.put("eduList", eduList);

        // DO NOT MODIFY THIS
        context.render(TEMPLATE, model);
    }

}
