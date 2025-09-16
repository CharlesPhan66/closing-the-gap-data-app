package app;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles HTTP requests for the index page and prepares data for the Thymeleaf template.
 */
public class PageIndex implements Handler {

    public static final String URL = "/";
    private static final String TEMPLATE = "index.html";

    @Override
    public void handle(Context context) throws Exception {
        // Get parameters from the URL
        String yearParam = context.queryParam("year");
        String lgaCodeParam = context.queryParam("lgaCode");
        String stateIdParam = context.queryParam("stateId");
        
        int year = (yearParam != null && yearParam.equals("2021")) ? 2021 : 2016;

        // Establish database connection and retrieve data
        JDBCConnection jdbc = new JDBCConnection();
        ArrayList<LGA> allLGAs = jdbc.getLGAsByYear(year);
        ArrayList<State> states = jdbc.getStates();
        ArrayList<Outcome> outcomes = jdbc.getOutcomes();

        // Determine the selected state
        int selectedStateId = 1; // Default to the first state
        if (stateIdParam != null) {
            try {
                selectedStateId = Integer.parseInt(stateIdParam);
            } catch (NumberFormatException e) {
                // Keep the default if the parameter is invalid
            }
        }

        // Filter LGAs based on the selected state
        ArrayList<LGA> filteredLGAs = new ArrayList<>();
        for (LGA lga : allLGAs) {
            if (lga.getStateID() == selectedStateId) {
                filteredLGAs.add(lga);
            }
        }

        // Find the selected LGA object and validate it
        LGA selectedLGA = null;
        boolean isLgaInState = false;
        if (lgaCodeParam != null) {
            for (LGA lga : filteredLGAs) {
                if (lga.getCode().equals(lgaCodeParam)) {
                    selectedLGA = lga;
                    isLgaInState = true;
                    break;
                }
            }
        }
        // If the selected LGA is not in the current state, reset it
        if (!isLgaInState) {
            lgaCodeParam = null;
            selectedLGA = null; // Also nullify the object
        }

        // Find the selected State object
        State selectedState = null;
        for (State state : states) {
            if (state.getCode().equals(String.valueOf(selectedStateId))) {
                selectedState = state;
                break;
            }
        }
        
        // Get the total population for the selected state
        int statePopulation = jdbc.getTotalPopulationForState(selectedStateId, year);
        
        // Prepare the data model to be passed to the template
        Map<String, Object> model = new HashMap<>();
        model.put("year", year);
        model.put("states", states);
        model.put("selectedStateId", selectedStateId);
        model.put("selectedState", selectedState);
        model.put("filteredLGAs", filteredLGAs);
        model.put("lgaCodeParam", lgaCodeParam);
        model.put("selectedLGA", selectedLGA);
        model.put("statePopulation", statePopulation);
        model.put("outcomes", outcomes);
        
        // Render the template with the model data
        context.render("index.html", model);
    }
}