package app;

import io.javalin.http.Context;
import io.javalin.http.Handler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

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

        // Add some Header information
        html = html + "<head>" + 
               "<title>Homepage</title>";

        // Add some CSS (external file)
        html = html + "<link rel='stylesheet' type='text/css' href='common.css' />";
        html = html + "</head>";
        
        // Add the body
        html = html + "<body>";
        
        // Add the topnav
        // This uses a Java v15+ Text Block
        html = html + """
            <div class='topnav'>
                <a href='/'>Homepage</a>
                <a href='mission.html'>Our Mission</a>
                <a href='page2A.html'>Sub Task 2.A</a>
                <a href='page2B.html'>Sub Task 2.B</a>
                <a href='page3A.html'>Sub Task 3.A</a>
                <a href='page3B.html'>Sub Task 3.B</a>
            </div>
        """;

        // Add header content block
        html = html + """
            <div class='topbar'>
                <div class='topbar-left'>
                    <a href='/' class='home-link'><img src='CtGLogo.png' class='top-image logo-main' alt='Logo'>
                        <img src='logo.png' class='top-image logo-rmit' alt='RMIT logo'>
                        <span class='site-title'></span></a>
                </div>
                <div class='topbar-right'>
                    <button class='btn signin'>Sign in</button>
                    <button class='btn menu'>Menu</button>
                </div>
            </div>

            <div class='search-row'>
                <input type='search' class='search-input big' placeholder='Search LGAs, outcomes, detailed outcomes, projects...'>
            </div>
        """;

        // Add Div for page Content
        html = html + "<div class='content'>";

        // Add HTML for the page content
        html = html + """
            <p>Homepage content</p>
            """;

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

        // Add HTML for the LGA list
        html = html + "<h1>All 2016 LGAs in the Voice to Parliament database</h1>" + "<ul>";

        // Finally we can print out all of the LGAs
        for (String name : lgaNames) {
            html = html + "<li>" + name + "</li>";
        }

        // Finish the List HTML
        html = html + "</ul>";
        

        // Carousel of cards (17 outcomes) with Next/Back pagination
        html = html + """
            <section class='carousel-wrap'>
                <div class='carousel' id='carousel'>
        """;

        // Generate 17 outcome cards
        for (int i = 1; i <= 17; i++) {
            html = html + "<div class='card' data-index='" + i + "'>" +
                   "<h4>Outcome " + i + "</h4>" +
                   "<p>Short description for outcome " + i + "</p>" +
                   "</div>";
        }

        html = html + """
                </div>
                <div class='carousel-controls'>
                    <button class='btn prev' id='prevBtn'>Back</button>
                    <div class='carousel-counter'><span id='carousel-page'>1</span> / <span id='carousel-pages'>?</span></div>
                    <button class='btn next' id='nextBtn'>Next</button>
                </div>
            </section>

            <script>
                document.addEventListener('DOMContentLoaded', function() {
                    const carousel = document.getElementById('carousel');
                    const prev = document.getElementById('prevBtn');
                    const next = document.getElementById('nextBtn');
                    const pageSpan = document.getElementById('carousel-page');
                    const pagesSpan = document.getElementById('carousel-pages');

                    function update() {
                        const pages = Math.max(1, Math.ceil(carousel.scrollWidth / carousel.clientWidth));
                        const page = Math.round(carousel.scrollLeft / carousel.clientWidth) + 1;
                        pageSpan.textContent = page;
                        pagesSpan.textContent = pages;
                    }

                    prev.addEventListener('click', function() {
                        carousel.scrollBy({ left: -carousel.clientWidth, behavior: 'smooth' });
                        setTimeout(update, 300);
                    });

                    next.addEventListener('click', function() {
                        carousel.scrollBy({ left: carousel.clientWidth, behavior: 'smooth' });
                        setTimeout(update, 300);
                    });

                    carousel.addEventListener('scroll', function() {
                        // throttle-ish update
                        if (this._updating) return;
                        this._updating = true;
                        window.requestAnimationFrame(function() { update(); carousel._updating = false; });
                    });

                    // initial update
                    update();
                });
            </script>
        """;

        // Close Content div
        html = html + "</div>";

        // Footer
        html = html + """
            <div class='footer'>
                <p>Link to About us</p>
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
