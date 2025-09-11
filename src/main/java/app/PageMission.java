package app;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

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
public class PageMission implements Handler {

    // URL of this page relative to http://localhost:7001/
    public static final String URL = "/mission.html";
    private static final String TEMPLATE = ("PageMission.html");

    @Override
    public void handle(Context context) throws Exception {
        // Create a simple HTML webpage in a String
        // This example uses JDBC to lookup the LGAs
        JDBCConnection jdbc = new JDBCConnection();

        // Next we will ask this *class* for the LGAs
        ArrayList<Members> members = jdbc.getMembers();
        Map<String, Object> model = new HashMap<>();
        model.put("members", members);

        ArrayList<Persona> persona = jdbc.getPersona();
        model.put("persona", persona);

        ArrayList<Priorities> priorities = jdbc.getPriorities();
        model.put("priorities", priorities);

        // DO NOT MODIFY THIS
        // Makes Javalin render the webpage
        context.render(TEMPLATE, model);
    }
}
