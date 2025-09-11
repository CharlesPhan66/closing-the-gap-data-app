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
        String mode = context.formParam("mode");
        String doFilter = context.formParam("doFilter");
        // doFilter: null (not submitted), "false" (auto-submit for state/year), or "true" (user clicked Filter)
        boolean showFiltersChanged = false;
        if (doFilter != null && doFilter.equals("false")) {
            showFiltersChanged = true;
        }
        if (mode == null || (!mode.equals("summary") && !mode.equals("detail"))) {
            mode = "detail";
        }
        System.out.println("Chosen State: " + chosenState);
        System.out.println("Chosen Year: " + chosenYear);
        System.out.println("Chosen LGA: " + chosenLGA);
        System.out.println("Chosen Status: " + chosenStatus);
        // System.out.println("Chosen Age: " + chosenAge);
        System.out.println("Chosen Gender: " + chosenGender);
        System.out.println("Chosen Condition: " + chosenCondition);
        System.out.println("Mode: " + mode);
        
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

    // expose the flags to the template
    model.put("doFilter", doFilter == null ? "none" : doFilter);
    model.put("showFiltersChanged", showFiltersChanged);

    ArrayList<Health> healthList = new ArrayList<>();
        // Read sorting parameters: only apply ORDER BY if user explicitly submitted sortBy (sort is optional)
        String sortByParam = context.formParam("sortBy"); // "population", "percent", or "none"/null
        String sortDirParam = context.formParam("sortDir"); // "asc" or "desc" or null
    // Only allow sorting when in detail mode (summary is aggregated and usually returns single row)
    boolean sortRequested = (sortByParam != null && sortByParam.length() > 0 && !sortByParam.equals("none") && mode.equals("detail"));
        String sortBy = null;
        String sortDir = null;
        if (sortRequested) {
            if (sortByParam.equals("population") || sortByParam.equals("percent")) sortBy = sortByParam;
            if (sortDirParam != null && (sortDirParam.equalsIgnoreCase("asc") || sortDirParam.equalsIgnoreCase("desc"))) sortDir = sortDirParam.toLowerCase();
            if (sortDir == null) sortDir = "desc"; // default direction when user requested sort but didn't specify
        }
        // Optional Top-N ranking
        String topParam = context.formParam("topN");
        Integer topN = null;
        String topWarning = null;
        try {
            if (topParam != null && topParam.trim().length() > 0) {
                int v = Integer.parseInt(topParam);
                if (v > 0) topN = v;
            }
        } catch (NumberFormatException e) {
            topN = null;
        }
        // expose to template only if provided (allows template to show "No sort" when null)
        model.put("sortBy", sortBy);
        model.put("sortDir", sortDir);

        // Show table only when user explicitly submitted the Filter (doFilter=true)
        if ("true".equals(doFilter) && chosenState != null && !chosenState.equals("none") &&
            chosenYear != null && !chosenYear.equals("none")) {
            // compute denominator (either LGA total or State total) for percentage calculation
            int totalPopulation = 0;
            try {
                int yearInt = Integer.parseInt(chosenYear);
                if (chosenLGA != null && !chosenLGA.equals("none")) {
                    totalPopulation = jdbc.getTotalPopulationForLGA(chosenLGA, yearInt);
                } else {
                    int stateId = Integer.parseInt(chosenState);
                    totalPopulation = jdbc.getTotalPopulationForState(stateId, yearInt);
                }
            } catch (NumberFormatException e) {
                totalPopulation = 0;
            }

            model.put("totalPopulation", totalPopulation);

            // Build a safe ORDER BY clause for SQL only when user requested sorting.
            String orderByClause = null;
            if (sortBy != null) {
                String dir = "DESC";
                if (sortDir != null && sortDir.equalsIgnoreCase("asc")) dir = "ASC";
                if (mode.equals("summary")) {
                    if (sortBy.equals("population")) {
                        orderByClause = "totalPopulation " + dir;
                    } else { // percent
                        if (totalPopulation > 0) {
                            // use SUM(h.populationValue) since summary uses aggregate
                            orderByClause = "(CAST(SUM(h.populationValue) AS REAL) / " + totalPopulation + ") " + dir;
                        } else {
                            orderByClause = "totalPopulation " + dir; // fallback
                        }
                    }
                    healthList = jdbc.getHealthSummaryByFilters(chosenYear, chosenState, chosenLGA, chosenGender, chosenStatus, chosenCondition, orderByClause, topN);
                } else {
                    // If LGA is selected, filter by LGA; otherwise, show all LGAs in the State
                    String lgaParam = (chosenLGA != null && !chosenLGA.equals("none")) ? chosenLGA : null;
                    if (sortBy.equals("population")) {
                        orderByClause = "h.populationValue " + dir;
                    } else {
                        if (totalPopulation > 0) {
                            orderByClause = "(CAST(h.populationValue AS REAL) / " + totalPopulation + ") " + dir;
                        } else {
                            orderByClause = "h.populationValue " + dir;
                        }
                    }
                    healthList = jdbc.getHealthByFilters(chosenYear, lgaParam, chosenGender, chosenStatus, chosenCondition, orderByClause, topN);
                }
            } else {
                // No sorting requested; call JDBC without ORDER BY (pass null)
                if (mode.equals("summary")) {
                    healthList = jdbc.getHealthSummaryByFilters(chosenYear, chosenState, chosenLGA, chosenGender, chosenStatus, chosenCondition, null, topN);
                } else {
                    String lgaParam = (chosenLGA != null && !chosenLGA.equals("none")) ? chosenLGA : null;
                    healthList = jdbc.getHealthByFilters(chosenYear, lgaParam, chosenGender, chosenStatus, chosenCondition, null, topN);
                }
            }

            // set percentage on each Health object (applies to both summary and detail)
            for (Health h : healthList) {
                double pct = 0.0;
                if (totalPopulation > 0) {
                    pct = (double) h.getPopulationValue() * 100.0 / (double) totalPopulation;
                }
                h.setPercentage(pct);
            }
            // If topN was requested and fewer rows returned, set a friendly warning
            if (topN != null) {
                if (healthList.size() < topN) {
                    topWarning = "With your chosen filters, records found (" + healthList.size() + ") are less than your Top " + topN + ". Showing all available records in rank order.";
                }
                // attach ranks to health objects by adding a rank field via setter (add method to Health?)
                // We will repurpose a small field: set a rank string in the disease field prefix or better add a rank to Health using reflection is messy.
                // Instead expose a separate rank list to the model.
            }
        }
        model.put("healthList", healthList);
    // Ranks are provided by the database when ordering is requested; templates should use h.rank
    model.put("topWarning", topWarning);
    model.put("topN", topN);
        model.put("mode", mode);


    context.render(TEMPLATE, model);
    }

}
