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
    // Determine whether this request is an autosubmit for updating dropdowns or an explicit Apply
    String submitAction = context.formParam("submitAction");
    if (submitAction == null) submitAction = "";
    String minSchoolYearsNum = context.formParam("minSchoolYears");
    if (minSchoolYearsNum != null && minSchoolYearsNum.isEmpty()) minSchoolYearsNum = null;
    String maxSchoolYearsNum = context.formParam("maxSchoolYears");
    if (maxSchoolYearsNum != null && maxSchoolYearsNum.isEmpty()) maxSchoolYearsNum = null;
    // If dataset changed, clear previous min stored in session
    if (datasetChanged) {
        context.sessionAttribute("prevMinSchoolYears", null);
    }
    // Reset max when user changes min: compare to previous min stored in session
    String prevMinSchoolYears = context.sessionAttribute("prevMinSchoolYears");
    if (prevMinSchoolYears != null && minSchoolYearsNum != null && !prevMinSchoolYears.equals(minSchoolYearsNum)) {
        // user changed min selection, clear max so template shows --Select Max--
        maxSchoolYearsNum = null;
    }
    // update session with current min for next request
    context.sessionAttribute("prevMinSchoolYears", minSchoolYearsNum);
    // Build numeric ranges from eduList: for y8_below treat as min=1,max=8; others min=max=number extracted
    class EduRange { int min; int max; String levelID; }
    List<EduRange> ranges = new ArrayList<>();
    for (Education e : eduList) {
        String lid = e.getLevelID();
        if (lid == null) continue;
        if (lid.contains("did_not_go_to_school")) continue; // skip for numeric ranges
        EduRange r = new EduRange();
        r.levelID = lid;
        String digits = e.getLevelID().replaceAll("\\D", "");
        int num = -1;
        try { num = Integer.parseInt(digits); } catch (Exception ex) { num = -1; }
        // if label/text contains 'below' treat as x_below -> min=1,max=num
        String label = e.getLevel() == null ? "" : e.getLevel().toLowerCase();
        if (label.contains("below") || lid.contains("8_below") || lid.contains("y8_below")) {
            r.min = 1;
            r.max = num > 0 ? num : 8;
        } else if (num > 0) {
            r.min = num;
            r.max = num;
        } else {
            // fallback: treat as single-year equal to num
            r.min = num;
            r.max = num;
        }
        ranges.add(r);
    }
    // Build min and max option lists (preserve eduList order where possible)
    List<String> minOptions = new ArrayList<>();
    List<String> maxOptions = new ArrayList<>();
    // special first option for min
    minOptions.add("did_not_go_to_school");
    for (EduRange r : ranges) {
        String minStr = Integer.toString(r.min);
        if (!minOptions.contains(minStr)) minOptions.add(minStr);
        String maxStr = Integer.toString(r.max);
        if (!maxOptions.contains(maxStr)) maxOptions.add(maxStr);
    }
    // Map selected numeric choices to levelIDs: find a level whose range includes that numeric
    String minSchoolYears = null, maxSchoolYears = null;
    if (minSchoolYearsNum != null) {
        if ("did_not_go_to_school".equals(minSchoolYearsNum)) {
            minSchoolYears = "did_not_go_to_school";
        } else {
            try {
                int selMin = Integer.parseInt(minSchoolYearsNum);
                for (EduRange r : ranges) {
                    if (r.min == selMin) { minSchoolYears = r.levelID; break; }
                }
            } catch (Exception ex) { /* ignore */ }
        }
    }
    if (maxSchoolYearsNum != null) {
        try {
            int selMax = Integer.parseInt(maxSchoolYearsNum);
            for (EduRange r : ranges) {
                if (r.max == selMax) { maxSchoolYears = r.levelID; break; }
            }
        } catch (Exception ex) { /* ignore */ }
    }
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
            // Determine if view mode is needed (multiple age groups)
            boolean populationViewModeNeeded = selectedAgeGroupIDs != null && selectedAgeGroupIDs.size() > 1;
            model.put("populationViewModeNeeded", populationViewModeNeeded);
            String populationViewMode = context.formParam("populationViewMode");
            if (populationViewMode == null || populationViewMode.isEmpty()) populationViewMode = "byAgeGroups";
            model.put("populationViewMode", populationViewMode);
            // Get population gap results only when user clicked Apply
            if ("apply".equals(submitAction) && status1 != null && status2 != null && sex1 != null && selectedAgeGroupIDs != null && !selectedAgeGroupIDs.isEmpty()) {
                // The actual SQL logic will be updated in the backend function next
                ArrayList<PopulationGapResult> populationGapResults = jdbc.getPopulationGapResults(status1, status2, sex1, selectedAgeGroupIDs, populationViewMode);
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
    model.put("minSchoolYears", minSchoolYearsNum);
    model.put("maxSchoolYears", maxSchoolYearsNum);
    model.put("minOptions", minOptions);
    // filter max options based on selected min (no max if did_not_go_to_school)
    List<String> filteredMaxOptions = new ArrayList<>();
    if (minSchoolYearsNum == null) {
        filteredMaxOptions = maxOptions;
    } else if ("did_not_go_to_school".equals(minSchoolYearsNum)) {
        // no max options
        filteredMaxOptions = new ArrayList<>();
    } else {
        try {
            int selMin = Integer.parseInt(minSchoolYearsNum);
            for (String m : maxOptions) {
                try {
                    int mv = Integer.parseInt(m);
                    if (mv >= selMin) filteredMaxOptions.add(m);
                } catch (Exception ex) { }
            }
        } catch (Exception ex) { filteredMaxOptions = maxOptions; }
    }
    model.put("maxOptions", filteredMaxOptions);
    // Build filtered maxSchoolYearsList for dropdown (only show options after min)
    List<Education> maxSchoolYearsList = new ArrayList<>();
    if (minSchoolYears != null && !"did_not_go_to_school".equals(minSchoolYears)) {
        boolean atOrAfterMin = false;
        for (Education e : eduList) {
            if (e.getLevelID().equals(minSchoolYears)) {
                atOrAfterMin = true;
            }
            if (atOrAfterMin) {
                maxSchoolYearsList.add(e);
            }
        }
    }
    model.put("maxSchoolYearsList", maxSchoolYearsList);
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
            if ("apply".equals(submitAction)) {
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
            }
        } else {
            System.out.println("Chosen health condition(s): none");
            model.put("healthGapResults", null);
            model.put("healthGapResultType", null);
        }
    }
    if ("Education".equals(dataset)) {
        List<String> selectedLevelIDs = getSchoolLevelIDsInRange(minSchoolYears, maxSchoolYears, eduList);
        System.out.println("Chosen min school years: " + minSchoolYears);
        System.out.println("Chosen max school years: " + maxSchoolYears);
        System.out.println("Selected levelIDs for SQL: " + selectedLevelIDs);
        model.put("selectedLevelIDs", selectedLevelIDs);
        boolean educationViewModeNeeded = selectedLevelIDs != null && selectedLevelIDs.size() > 1;
        model.put("educationViewModeNeeded", educationViewModeNeeded);
        boolean educationFiltersComplete = status1 != null && status2 != null && sex1 != null && selectedLevelIDs != null && !selectedLevelIDs.isEmpty();
        model.put("educationFiltersComplete", educationFiltersComplete);
        if (educationFiltersComplete && "apply".equals(submitAction)) {
            String educationViewMode = context.formParam("educationViewMode");
            if (educationViewMode == null || educationViewMode.isEmpty()) educationViewMode = "byLevels";
            model.put("educationViewMode", educationViewMode);
            if (!educationViewModeNeeded) {
                ArrayList<PopulationGapResult> eduResults = jdbc.getEducationGapTotal(status1, status2, sex1, selectedLevelIDs);
                model.put("educationGapResultsTotal", eduResults);
            } else {
                if ("byTotal".equals(educationViewMode)) {
                    ArrayList<PopulationGapResult> eduResults = jdbc.getEducationGapTotal(status1, status2, sex1, selectedLevelIDs);
                    model.put("educationGapResultsTotal", eduResults);
                } else {
                    ArrayList<EducationGapResult> eduResults = jdbc.getEducationGapByLevel(status1, status2, sex1, selectedLevelIDs);
                    model.put("educationGapResultsByLevel", eduResults);
                }
            }
        } else {
            model.put("educationGapResultsTotal", null);
            model.put("educationGapResultsByLevel", null);
        }
    }
    if ("NonSchoolEdu".equals(dataset)) {
        boolean nonSchoolFiltersComplete = status1 != null && status2 != null && sex1 != null && selectedNonSchoolIDs != null && !selectedNonSchoolIDs.isEmpty();
        model.put("nonSchoolFiltersComplete", nonSchoolFiltersComplete);
        if (nonSchoolFiltersComplete && "apply".equals(submitAction)) {
            // Determine view mode: by degree or by total
            String nonSchoolViewMode = context.formParam("nonschoolViewMode");
            boolean byDegree = "byDegree".equals(nonSchoolViewMode) && selectedNonSchoolIDs.size() > 1;
            String viewBy = byDegree ? "degree" : "total";
            System.out.println("Chosen non-school qualification(s): " + String.join(", ", selectedNonSchoolIDs) + ", viewBy=" + viewBy);
            ArrayList<NonSchoolGapResult> nonSchoolGapResults = jdbc.getNonSchoolGapResults(status1, status2, sex1, selectedNonSchoolIDs, viewBy);
            model.put("nonSchoolGapResults", nonSchoolGapResults);
            model.put("nonSchoolViewMode", viewBy);
        } else {
            model.put("nonSchoolGapResults", null);
            model.put("nonSchoolViewMode", null);
        }
    }


    // Render the template with model
    context.render(TEMPLATE, model);
    }

    /**
     * Given min and max school years (as levelIDs), and the eduList, return the correct list of levelIDs for SQL query.
     * Rules:
     * 1. If min is did_not_go_to_school, return ["did_not_go_to_school"]
     * 2. Max must be after min in eduList order
     * 3. For any range, select all levelIDs between min and max (inclusive)
     * 4. Do not hardcode levelIDs; use eduList order
     */
    private List<String> getSchoolLevelIDsInRange(String minLevelID, String maxLevelID, List<Education> eduList) {
        List<String> result = new ArrayList<>();
        if (minLevelID == null || minLevelID.isEmpty()) return result;
        if ("did_not_go_to_school".equals(minLevelID)) {
            result.add("did_not_go_to_school");
            return result;
        }
        int minIdx = -1, maxIdx = -1;
        for (int i = 0; i < eduList.size(); i++) {
            if (eduList.get(i).getLevelID().equals(minLevelID)) minIdx = i;
            if (maxLevelID != null && !maxLevelID.isEmpty() && eduList.get(i).getLevelID().equals(maxLevelID)) maxIdx = i;
        }
        if (minIdx == -1) return result;
        if (maxLevelID == null || maxLevelID.isEmpty() || maxIdx == -1 || maxIdx < minIdx) {
            // Only min selected, or invalid max: just return min
            result.add(eduList.get(minIdx).getLevelID());
            return result;
        }
        for (int i = minIdx; i <= maxIdx; i++) {
            result.add(eduList.get(i).getLevelID());
        }
        return result;
    }
}
