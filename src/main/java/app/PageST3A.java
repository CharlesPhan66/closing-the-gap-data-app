package app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
        // Render the static HTML template for Page 3A.
        context.render(TEMPLATE, model);
    }

}
