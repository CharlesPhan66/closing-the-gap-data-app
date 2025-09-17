package app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.javalin.http.Context;
import io.javalin.http.Handler;

public class PageST2B implements Handler {

    // URL of this page relative to http://localhost:7001/
    public static final String URL = "/page2B.html";
    private static final String TEMPLATE = ("PageST2B.html");

    @Override
    public void handle(Context context) throws Exception {
        // Get user input from the form
        String chosenState = context.formParam("targetState");
        String chosenYear = context.formParam("targetYear");
        String chosenLGA = context.formParam("targetLGA");
        String chosenStatus = context.formParam("targetStatus");
        String chosenAge = context.formParam("targetAge");
        String chosenGender = context.formParam("targetGender");
        String chosenEduType = context.formParam("targetEduType");
        String chosenEduLevel = context.formParam("targetEduLevel");
        String action = context.formParam("action");
        String viewMode = context.formParam("viewMode");
        if (viewMode == null) {
            viewMode = "detailed"; // Default to detailed view
        }

        // Sorting parameters
        String sortBy = context.formParam("sortBy");
        String sortOrder = context.formParam("sortOrder");
        String sortRequest = context.formParam("sortRequest");

        // Determine next sort state if a sort button was clicked
        if (sortRequest != null) {
            if (sortRequest.equals(sortBy)) {
                // Cycle sort order: asc -> desc -> none
                if ("asc".equals(sortOrder)) {
                    sortOrder = "desc";
                } else {
                    sortBy = null;
                    sortOrder = null;
                }
            } else {
                // New column to sort, start with ascending
                sortBy = sortRequest;
                sortOrder = "asc";
            }
        }

        Map<String, Object> model = new HashMap<>();
        JDBCConnection jdbc = new JDBCConnection();

        // Populate dropdowns for the form
        model.put("states", jdbc.getStates());
        model.put("statusList", jdbc.getIndigStatus());
        model.put("ages", jdbc.getAgeGroup());
        model.put("genders", jdbc.getSexValues());
        model.put("educationLevels", jdbc.getEducation());
        model.put("nonSchoolLevels", jdbc.getNonSchoolEdu());

        // If State and Year are selected, get LGAs for the dropdown
        if (chosenState != null && !chosenState.equals("none") && chosenYear != null && !chosenYear.equals("none")) {
            model.put("lgasInState", jdbc.getLGAsByState(chosenState, chosenYear));
        }

        // Store selected values to re-populate the form
        model.put("selectedState", chosenState);
        model.put("selectedYear", chosenYear);
        model.put("selectedLGA", chosenLGA);
        model.put("selectedStatus", chosenStatus);
        model.put("selectedAge", chosenAge);
        model.put("selectedGender", chosenGender);
        model.put("selectedEduType", chosenEduType);
        model.put("selectedEduLevel", chosenEduLevel);
        model.put("viewMode", viewMode); // Pass view mode to the template

        // Store sorting state
        model.put("sortBy", sortBy);
        model.put("sortOrder", sortOrder);

        // Only query for results if the "Filter" button was clicked or a sort was requested
        if ("filter".equals(action) || sortRequest != null) {
            ArrayList<Health> eduList;
            if ("summary".equals(viewMode)) {
                eduList = jdbc.getEducationSummaryByFilter(
                    chosenYear, chosenLGA, chosenGender, chosenStatus, chosenAge, chosenEduType, chosenEduLevel
                );
            } else { // Detailed mode
                eduList = jdbc.getEducationByFilter(
                    chosenYear, chosenLGA, chosenGender, chosenStatus, chosenAge, chosenEduType, chosenEduLevel
                );
            }

            long totalPopulation = jdbc.getTotalEducationPopulationByFilter(
                chosenYear, chosenLGA, chosenGender, chosenStatus, chosenAge
            );

            // Create a copy of the list for the ranking table
            ArrayList<Health> rankingList = new ArrayList<>(eduList);

            // Default to descending sort for the ranking table unless ascending is explicitly chosen.
            String rankingSortOrder = "asc".equals(sortOrder) ? "asc" : "desc";

            // If sorting for smallest populations, filter out any with a value of 0
            if ("asc".equals(rankingSortOrder)) {
                rankingList.removeIf(h -> h.getPopulationValue() == 0);
            }

            // Sort the ranking list
            final long finalTotalPopulation = totalPopulation;
            rankingList.sort((h1, h2) -> {
                int comparison = Integer.compare(h1.getPopulationValue(), h2.getPopulationValue());
                return "desc".equals(rankingSortOrder) ? -comparison : comparison;
            });

            // Trim the ranking list to the top 5
            if (rankingList.size() > 5) {
                rankingList = new ArrayList<>(rankingList.subList(0, 5));
            }

            // Perform sorting on the main list if requested by the user
            if (sortBy != null && sortOrder != null) {
                final String currentSortBy = sortBy;
                final String finalSortOrder = sortOrder;

                eduList.sort((h1, h2) -> {
                    int comparison = 0;
                    if ("populationValue".equals(currentSortBy)) {
                        comparison = Integer.compare(h1.getPopulationValue(), h2.getPopulationValue());
                    } else if ("populationPercentage".equals(currentSortBy) && finalTotalPopulation > 0) {
                        double p1 = (h1.getPopulationValue() * 100.0) / finalTotalPopulation;
                        double p2 = (h2.getPopulationValue() * 100.0) / finalTotalPopulation;
                        comparison = Double.compare(p1, p2);
                    }
                    return "desc".equals(finalSortOrder) ? -comparison : comparison;
                });
            }

            model.put("eduList", eduList);
            model.put("totalPopulation", totalPopulation);
            model.put("rankingList", rankingList);
        }
        // DO NOT MODIFY THIS
        // Makes Javalin render the webpage
        context.render(TEMPLATE, model);
    }
}
