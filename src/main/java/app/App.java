package app;

import java.io.InputStream;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.staticfiles.Location;
import io.javalin.plugin.rendering.template.JavalinThymeleaf;
import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

/**
 * Main Application Class.
 * <p>
 * Running this class as regular java application will start the Javalin HTTP
 * Server and our web application will be running at
 * http://localhost:7000
 */
public class App {

    public static final int         JAVALIN_PORT    = 7003;
    public static final String      CSS_DIR         = "css/";
    public static final String      IMAGES_DIR      = "images/";
    public static final String      GEO_DIR         = "geo/";

    public static void main(String[] args) {
        // Create our HTTP server and listen in port 7000
        Javalin app = Javalin.create(config -> {
            config.addStaticFiles(CSS_DIR, Location.CLASSPATH);
            config.addStaticFiles(IMAGES_DIR, Location.CLASSPATH);
            config.addStaticFiles(GEO_DIR, Location.CLASSPATH);
            config.enableDevLogging();
            JavalinThymeleaf.configure(configureTemplateEngine());
        }).start(JAVALIN_PORT);

        // Add routes
        app.get("/", App::handleIndex);
        app.post("/", App::handleIndex);
        app.get(PageMission.URL, new PageMission());
        app.get(PageST2A.URL, new PageST2A());
        app.get(PageST2B.URL, new PageST2B());
        app.get(PageST3A.URL, new PageST3A());
        app.get(PageST3B.URL, new PageST3B());

        // API route for GeoJSON data
        app.get("/api/states-geo", ctx -> {
            // use absolute classpath lookup so the file in /geo/ is found (not package-relative)
            try (InputStream is = App.class.getResourceAsStream("/geo/states.geojson")) {
                if (is == null) {
                    ctx.status(404).result("GeoJSON file not found");
                    return;
                }
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode geoJson = (ObjectNode) mapper.readTree(is);
                ctx.json(geoJson);
            } catch (Exception e) {
                e.printStackTrace();
                ctx.status(500).result("Error reading GeoJSON file");
            }
        });
    }

    private static void handleIndex(Context context) {
        Map<String, Object> model = PageIndex.getModel(context);
        context.render("index.html", model);
    }

    private static TemplateEngine configureTemplateEngine() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML");
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setOrder(1);
        templateResolver.setCheckExistence(true);

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        templateEngine.addDialect(new LayoutDialect());
        return templateEngine;
    }

}
