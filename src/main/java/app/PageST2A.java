package app;

import java.util.ArrayList;

import io.javalin.http.Context;
import io.javalin.http.Handler;

import java.util.Map;

/**
 * Example Index HTML class using Javalin
 * <p>
 * Generate a static HTML page using Javalin
 * by writing the raw HTML into a Java String object
 *
 * @author Timothy Wiley, 2023. email: timothy.wiley@rmit.edu.au
 * @author Santha Sumanasekara, 2021. email: santha.sumanasekara@rmit.edu.au
 */
public class PageST2A implements Handler {

    // URL of this page relative to http://localhost:7001/
    public static final String URL = "/page2A.html";
    private static final String TEMPLATE = ("PageST2A.html");

    @Override
    public void handle(Context context) throws Exception {

    JDBCConnection jdbc = new JDBCConnection();
    ArrayList<LGA> lgas = jdbc.getLGAs2016();
    ArrayList<IndigStatus> statusList = jdbc.getIndigStatus();
    ArrayList<State> states = jdbc.getStates();
    ArrayList<Age> ages = jdbc.getAgeGroup();
    ArrayList<Gender> genders = jdbc.getSexValues();
    ArrayList<Condition> conditions = jdbc.getHealthConditions();

    Map<String, Object> model = new java.util.HashMap<>();
    model.put("states", states);
    model.put("lgas", lgas);
    model.put("statusList", statusList);
    model.put("ages", ages);
    model.put("genders", genders);
    model.put("conditions", conditions);


    context.render(TEMPLATE, model);
    }

}
