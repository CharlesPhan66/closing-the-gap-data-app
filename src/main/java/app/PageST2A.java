package app;

import java.util.ArrayList;
import java.util.HashMap;

import io.javalin.http.Context;
import io.javalin.http.Handler;

import java.util.Map;

public class PageST2A implements Handler {
    
    // URL of this page relative to http://localhost:7001/
    public static final String URL = "/page2A.html";
    private static final String TEMPLATE = ("PageST2A.html");
    
    @Override
    public void handle(Context context) throws Exception {
        // Get user input from the form (if any)
        String chosenYear = context.formParam("targetYear");
        String chosenLGA = context.formParam("targetLGA");
        String chosenStatus = context.formParam("targetStatus");
        String chosenState = context.formParam("targetState");
        // String chosenAge = context.formParam("targetAge");
        String chosenGender = context.formParam("targetGender");
        String chosenCondition = context.formParam("targetCondition");
        System.out.println("Chosen State: " + chosenState);
        System.out.println("Chosen Year: " + chosenYear);
        System.out.println("Chosen LGA: " + chosenLGA);
        System.out.println("Chosen Status: " + chosenStatus);
        // System.out.println("Chosen Age: " + chosenAge);
        System.out.println("Chosen Gender: " + chosenGender);
        System.out.println("Chosen Condition: " + chosenCondition);
        
        // Create the model to pass to Thymeleaf
        Map<String, Object> model = new HashMap<>();
        
        // Add in title for the h1 tag to the model
        model.put("title", "Find Health Conditions By Demographics");
        
        JDBCConnection jdbc = new JDBCConnection();

        // If a State and year are selected, then query the DB for all LGAs in that State
        if (chosenState != null && !chosenState.equals("none") && chosenYear != null && !chosenYear.equals("none")) {
            ArrayList<LGA> lgasInState = jdbc.getLGAsByState(chosenState, chosenYear);
            model.put("lgasInState", lgasInState);
        }

        // Always fetch and add the lists of all filters for the dropdowns
        ArrayList<IndigStatus> statusList = jdbc.getIndigStatus();
        ArrayList<State> states = jdbc.getStates();
        ArrayList<Gender> genders = jdbc.getSexValues();
        ArrayList<Condition> conditions = jdbc.getHealthConditions();
        model.put("states", states);
        model.put("statusList", statusList);
        model.put("genders", genders);
        model.put("conditions", conditions);


         
         // Record the selections that the user made previously
         model.put("selectedYear", chosenYear);
         model.put("selectedLGA", chosenLGA);
         model.put("selectedStatus", chosenStatus);
         model.put("selectedState", chosenState);
        //  model.put("selectedAge", chosenAge);
         model.put("selectedGender", chosenGender);
         model.put("selectedCondition", chosenCondition);

        ArrayList<Health> healthList = new ArrayList<>();
        // Show table if State, Year, and LGA are selected
        if (chosenState != null && !chosenState.equals("none") &&
            chosenYear != null && !chosenYear.equals("none") &&
            chosenLGA != null && !chosenLGA.equals("none")) {
            healthList = jdbc.getHealthByFilters(chosenYear, chosenLGA, chosenGender, chosenStatus, chosenCondition);
        }
        model.put("healthList", healthList);


    context.render(TEMPLATE, model);
    }

}
