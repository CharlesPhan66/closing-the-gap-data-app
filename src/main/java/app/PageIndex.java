package app;

import java.util.ArrayList;

import io.javalin.http.Context;
import io.javalin.http.Handler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Example Index HTML class using Javalin
 * <p>
 * Generate a static HTML page using Javalin
 * by writing the raw HTML into a Java String object
 *
 * @author Timothy Wiley, 2023. email: timothy.wiley@rmit.edu.au
 * @author Santha Sumanasekara, 2021. email: santha.sumanasekara@rmit.edu.au
 */
public class PageIndex implements Handler {

    // URL of this page relative to http://localhost:7001/
    public static final String URL = "/";

    @Override
    public void handle(Context context) throws Exception {
        // Create a simple HTML webpage in a String
        String html = "<html>";

        // Add some CSS (external file)
        html = html + "<link rel='stylesheet' type='text/css' href='common.css' />";
        html = html + "</head>";

        // Add the body
        html = html + "<body>";

        // Top bar with logo, search and actions
        html = html + """
            <div class='topbar'>
                <div class='topbar-left'>
                    <a href='/' class='home-link'><img src='CtGLogo.png' class='top-image' alt='Logo' height='50'> <span class='site-title'></span></a>
                </div>
                <div class='topbar-center'>
                    <input type='search' class='search-input' placeholder='Search LGAs, outcomes, projects...'>
                </div>
                <div class='topbar-right'>
                    <button class='btn signin'>Sign in</button>
                    <button class='btn menu'>Menu</button>
                </div>
            </div>
        """;
        
        // Add Div for page Content
        html = html + "<div class='content'>";

        // Hero area: narrative + graph
        html = html + """
            <section class='hero'>
                <div class='hero-left'>
                    <h2>Narrative about population</h2>
                    <p>This area contains a short narrative summary and quick links. You can add charts or summaries here. Below is a list of LGAs from the 2016 dataset.</p>
                </div>
                <div class='hero-right'>
                    <div class='graph-placeholder'>Graph</div>
                </div>
            </section>
        """;

        // Get the ArrayList of Strings of all LGAs
        ArrayList<String> lgaNames = getLGAs2016();

        // Add HTML for the LGA list inside the content area
        html = html + "<h3>All 2016 LGAs in the Voice to Parliament database</h3>" + "<ul class='lga-list'>";

        // Print out all of the LGAs
        for (String name : lgaNames) {
            html = html + "<li>" + name + "</li>";
        }

        // Finish the List HTML
        html = html + "</ul>";

        // Carousel of cards (static placeholder)
        html = html + """
            <section class='carousel-wrap'>
                <div class='carousel'>
                    <div class='card'>
                        <h4>Highlight outcome 5 and 7</h4>
                        <p>Short description.</p>
                    </div>
                    <div class='card'>
                        <h4>17 Outcomes Expanding</h4>
                        <p>Short description.</p>
                    </div>
                    <div class='card'>
                        <h4>Pattern: Favourite</h4>
                        <p>Short description.</p>
                    </div>
                    <div class='card'>
                        <h4>More content</h4>
                        <p>Short description.</p>
                    </div>
                </div>
                <div class='carousel-controls'>
                    <button class='btn prev'>Back</button>
                    <button class='btn next'>Next</button>
                </div>
            </section>
        """;

        // Close Content div
        html = html + "</div>";

        // Footer
        html = html + """
            <div class='footer fat'>
                <p>COSC3056 - Studio Project Starter Code (Sep23)</p>
            </div>
        """;

        // Finish the HTML webpage
        html = html + "</body>" + "</html>";


        // DO NOT MODIFY THIS
        // Makes Javalin render the webpage
        context.html(html);
    }


    /**
     * Get the names of the LGAs in the database.
     */
    public ArrayList<String> getLGAs2016() {
        // Create the ArrayList of LGA objects to return
        ArrayList<String> lgas = new ArrayList<String>();

        // Setup the variable for the JDBC connection
        Connection connection = null;

        try {
            // Connect to JDBC data base
            connection = DriverManager.getConnection(JDBCConnection.DATABASE);

            // Prepare a new SQL Query & Set a timeout
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            // The Query
            String query = "SELECT * FROM LGA WHERE year='2016'";
            
            // Get Result
            ResultSet results = statement.executeQuery(query);

            // Process all of the results
            while (results.next()) {
                String name16  = results.getString("name");

                // Add the lga object to the array
                lgas.add(name16);
            }

            // Close the statement because we are done with it
            statement.close();
        } catch (SQLException e) {
            // If there is an error, lets just pring the error
            System.err.println(e.getMessage());
        } finally {
            // Safety code to cleanup
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }

        // Finally we return all of the lga
        return lgas;
    }
}
