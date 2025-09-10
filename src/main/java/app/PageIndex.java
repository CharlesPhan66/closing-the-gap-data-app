package app;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Xử lý yêu cầu HTTP và chuẩn bị dữ liệu cho Thymeleaf template.
 */
public class PageIndex implements Handler {

    public static final String URL = "/";

    @Override
    public void handle(Context context) throws Exception {
        // Lấy và xử lý các tham số từ URL
        String yearParam = context.queryParam("year");
        String lgaCodeParam = context.queryParam("lgaCode");
        String stateIdParam = context.queryParam("stateId");
        
        int year = 2016;
        if (yearParam != null && (yearParam.equals("2016") || yearParam.equals("2021"))) {
            year = Integer.parseInt(yearParam);
        }

        // Khởi tạo kết nối database và lấy dữ liệu
        JDBCConnection jdbc = new JDBCConnection();
        ArrayList<LGA> allLGAs = (year == 2021) ? jdbc.getLGAs2021() : jdbc.getLGAs2016();
        ArrayList<State> states = jdbc.getStates();

        // Xử lý selected state và lọc LGAs
        int selectedStateId = 1;
        State selectedState = null;
        if (stateIdParam != null) {
            try {
                selectedStateId = Integer.parseInt(stateIdParam);
            } catch (NumberFormatException e) {
                selectedStateId = 1;
            }
        }
        for (State state : states) {
            if (state.getStateID() == selectedStateId) {
                selectedState = state;
                break;
            }
        }

        // Lọc LGAs theo state đã chọn
        ArrayList<LGA> filteredLGAs = new ArrayList<>();
        for (LGA lga : allLGAs) {
            if (lga.getStateID() == selectedStateId) {
                filteredLGAs.add(lga);
            }
        }

        // Tìm LGA đã chọn
        LGA selectedLGA = null;
        if (lgaCodeParam != null) {
            for (LGA lga : filteredLGAs) {
                if (lga.getCode().equals(lgaCodeParam)) {
                    selectedLGA = lga;
                    break;
                }
            }
        }
        
        // Lấy dân số của bang đã chọn
        int statePopulation = jdbc.getTotalPopulationForState(selectedStateId, year);
        
        // Lấy danh sách outcomes
        ArrayList<Outcome> outcomes = jdbc.getOutcomes();

        // Chuẩn bị model để truyền dữ liệu tới template
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
        
        // Render template với model
        context.render("index.html", model);
    }
}
