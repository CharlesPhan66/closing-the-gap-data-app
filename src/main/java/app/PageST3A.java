package app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

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

        // Track previous dataset to reset filters if dataset changes
        String prevDataset = context.sessionAttribute("prevDataset");

        // Get selected dataset from form, leave as null if not chosen (default is --Select dataset--)
        String dataset = context.formParam("dataset");
        if (dataset != null && dataset.isEmpty()) dataset = null;
        boolean datasetChanged = prevDataset != null && dataset != null && !dataset.equals(prevDataset);
        context.sessionAttribute("prevDataset", dataset);

        // Get all filter lists
        ArrayList<IndigStatus> statusList = jdbc.getIndigStatus();
        ArrayList<Gender> sexList = jdbc.getSexValues();
        ArrayList<Age> ageGroupList = jdbc.getAgeGroup();
        ArrayList<Condition> healthList = jdbc.getHealthConditions();
        ArrayList<Education> eduList = jdbc.getEducation();
        ArrayList<NonSchoolEdu> nonSchoolList = jdbc.getNonSchoolEdu();

        // Build age start and end lists for Population dataset
        List<Integer> ageStartList = new ArrayList<>();
        List<Integer> ageEndList = new ArrayList<>();
        for (Age a : ageGroupList) {
            if (!ageStartList.contains(a.getAgeStart())) {
                ageStartList.add(a.getAgeStart());
            }
        }
        ageStartList.sort(Integer::compareTo);

        // Parse selected age start/end
        String selectedAgeStartStr = context.formParam("ageStart");
        String selectedAgeEndStr = context.formParam("ageEnd");
        Integer selectedAgeStart = null, selectedAgeEnd = null;
        if (selectedAgeStartStr != null && !selectedAgeStartStr.isEmpty()) {
            try { selectedAgeStart = Integer.parseInt(selectedAgeStartStr); } catch (Exception e) { selectedAgeStart = null; }
        }
        if (selectedAgeEndStr != null && !selectedAgeEndStr.isEmpty()) {
            try { selectedAgeEnd = Integer.parseInt(selectedAgeEndStr); } catch (Exception e) { selectedAgeEnd = null; }
        }

        // Build ageEndList for dropdown (first add group with selected start, then all after)
        if (selectedAgeStart != null && selectedAgeStart < 65) {
            for (Age a : ageGroupList) {
                if (a.getAgeStart() == selectedAgeStart && a.getAgeEnd() > selectedAgeStart && a.getAgeStart() < 65) {
                    ageEndList.add(a.getAgeEnd());
                }
            }
            for (Age a : ageGroupList) {
                if (a.getAgeStart() > selectedAgeStart && a.getAgeStart() < 65 && a.getAgeEnd() > a.getAgeStart()) {
                    if (!ageEndList.contains(a.getAgeEnd())) {
                        ageEndList.add(a.getAgeEnd());
                    }
                }
            }
            ageEndList.sort(Integer::compareTo);
        }

        // Get filter values, leave as null if not chosen
    String status1, status2, sex1;
        if (datasetChanged) {
            status1 = null;
            status2 = null;
            sex1 = "both";
        } else {
            status1 = context.formParam("status1");
            if (status1 != null && status1.isEmpty()) status1 = null;
            status2 = context.formParam("status2");
            if (status2 != null && status2.isEmpty()) status2 = null;
            if (status1 != null && status1.equals(status2)) status2 = null;
            sex1 = context.formParam("sex1");
            if (sex1 == null || sex1.isEmpty()) sex1 = "both";
        }
    List<String> selectedHealthIDs = context.formParams("healthIDs");
    if (selectedHealthIDs != null && selectedHealthIDs.isEmpty()) selectedHealthIDs = null;
    String healthViewMode = context.formParam("healthViewMode");
    if (healthViewMode == null || healthViewMode.isEmpty()) healthViewMode = "byCondition";
        String edu = context.formParam("eduLevel");
        if (edu != null && edu.isEmpty()) edu = null;
        List<String> selectedNonSchoolIDs = context.formParams("nonSchoolIDs");
        if (selectedNonSchoolIDs != null && selectedNonSchoolIDs.isEmpty()) selectedNonSchoolIDs = null;

        // For Population: get all age group IDs in selected range
        List<String> selectedAgeGroupIDs = null;
        String status1Name = null, status2Name = null;
        if (status1 != null) {
            for (IndigStatus s : statusList) {
                if (status1.equals(s.getStatusID())) {
                    status1Name = s.getStatusName();
                    break;
                }
            }
        }
        if (status2 != null) {
            for (IndigStatus s : statusList) {
                if (status2.equals(s.getStatusID())) {
                    status2Name = s.getStatusName();
                    break;
                }
            }
        }
        model.put("status1Name", status1Name);
        model.put("status2Name", status2Name);
        if ("Population".equals(dataset)) {
            if (selectedAgeStart != null) {
                selectedAgeGroupIDs = new ArrayList<>();
                if (selectedAgeStart == 65) {
                    for (Age a : ageGroupList) {
                        if (a.getAgeStart() == 65) {
                            selectedAgeGroupIDs.add(a.getAgeID());
                        }
                    }
                } else if (selectedAgeEnd != null) {
                    for (Age a : ageGroupList) {
                        if (a.getAgeStart() >= selectedAgeStart && a.getAgeEnd() <= selectedAgeEnd && a.getAgeStart() < 65 && a.getAgeEnd() > a.getAgeStart()) {
                            selectedAgeGroupIDs.add(a.getAgeID());
                        }
                    }
                }
            }
            // Get population gap results if all required filters are selected
            if (status1 != null && status2 != null && sex1 != null && selectedAgeGroupIDs != null && !selectedAgeGroupIDs.isEmpty()) {
                ArrayList<PopulationGapResult> populationGapResults = jdbc.getPopulationGapResults(status1, status2, sex1, selectedAgeGroupIDs);
                model.put("populationGapResults", populationGapResults);
            } else {
                model.put("populationGapResults", null);
            }
        }

    // Add lists and filter values to model
    model.put("dataset", dataset);
    model.put("statusList", statusList);
    model.put("sexList", sexList);
    model.put("ageGroupList", ageGroupList);
    model.put("healthList", healthList);
    model.put("eduList", eduList);
    model.put("nonSchoolList", nonSchoolList);
    model.put("status1", status1);
    model.put("status2", status2);
    model.put("sex1", sex1);
    model.put("selectedAgeGroupIDs", selectedAgeGroupIDs);
    model.put("selectedHealthIDs", selectedHealthIDs);
    model.put("edu", edu);
    model.put("selectedNonSchoolIDs", selectedNonSchoolIDs);
    model.put("ageStartList", ageStartList);
    model.put("ageEndList", ageEndList);
    model.put("selectedAgeStart", selectedAgeStart);
    model.put("selectedAgeEnd", selectedAgeEnd);
    model.put("healthViewMode", healthViewMode);
    
    System.out.println("Chosen dataset: " + (dataset == null ? "null" : dataset));
    System.out.println("Chosen status1: " + status1);
    System.out.println("Chosen status2: " + status2);
    System.out.println("Chosen sex: " + sex1);
    if ("Population".equals(dataset)) {
        if (selectedAgeGroupIDs != null && !selectedAgeGroupIDs.isEmpty()) {
            // Print string IDs (e.g., 0_4_yrs) and their ranges for debug
            StringBuilder sb = new StringBuilder();
            sb.append("Chosen age group IDs: ");
            for (String id : selectedAgeGroupIDs) {
                for (Age a : ageGroupList) {
                    if (a.getAgeID().equals(id)) {
                        sb.append(id).append(" (").append(a.getAgeStart()).append("-").append(a.getAgeEnd()).append(") ");
                        break;
                    }
                }
            }
            System.out.println(sb.toString().trim());
        } else {
            System.out.println("Chosen age group: none");
        }
    }
    if ("Health".equals(dataset)) {
        if (selectedHealthIDs != null && !selectedHealthIDs.isEmpty()) {
            System.out.println("Chosen health condition(s): " + String.join(", ", selectedHealthIDs));
            // Health gap logic
            if (selectedHealthIDs.size() == 1) {
                // Single condition
                ArrayList<PopulationGapResult> healthGapResults = jdbc.getHealthGapSingleCondition(status1, status2, sex1, selectedHealthIDs.get(0));
                model.put("healthGapResults", healthGapResults);
                model.put("healthGapResultType", "single");
            } else if (selectedHealthIDs.size() > 1) {
                if ("byTotal".equals(healthViewMode)) {
                    ArrayList<PopulationGapResult> healthGapResults = jdbc.getHealthGapMultiConditionTotal(status1, status2, sex1, selectedHealthIDs);
                    model.put("healthGapResults", healthGapResults);
                    model.put("healthGapResultType", "total");
                } else {
                    ArrayList<HealthGapResult> healthGapResults = jdbc.getHealthGapMultiConditionByDisease(status1, status2, sex1, selectedHealthIDs);
                    model.put("healthGapResults", healthGapResults);
                    model.put("healthGapResultType", "byCondition");
                }
            }
        } else {
            System.out.println("Chosen health condition(s): none");
            model.put("healthGapResults", null);
            model.put("healthGapResultType", null);
        }
    }
    if ("Education".equals(dataset)) {
        System.out.println("Chosen education level: " + edu);
    }
    if ("NonSchoolEdu".equals(dataset)) {
        if (selectedNonSchoolIDs != null && !selectedNonSchoolIDs.isEmpty()) {
            System.out.println("Chosen non-school qualification(s): " + String.join(", ", selectedNonSchoolIDs));
        } else {
            System.out.println("Chosen non-school qualification(s): none");
        }
    }


    // Render the template with model
    context.render(TEMPLATE, model);
    }

}
