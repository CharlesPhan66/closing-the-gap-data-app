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
        // Get year and lgaCode from query params
        String yearParam = context.queryParam("year");
        String lgaCodeParam = context.queryParam("lgaCode");
        int year = 2016;
        if (yearParam != null && (yearParam.equals("2016") || yearParam.equals("2021"))) {
            year = Integer.parseInt(yearParam);
        }

        JDBCConnection jdbc = new JDBCConnection();
        ArrayList<LGA> lgas = (year == 2021) ? jdbc.getLGAs2021() : jdbc.getLGAs2016();

        // Find selected LGA if any
        LGA selectedLGA = null;
        if (lgaCodeParam != null) {
            for (LGA lga : lgas) {
                if (lga.getCode().equals(lgaCodeParam)) {
                    selectedLGA = lga;
                    break;
                }
            }
        }

        // Build HTML
        String html = "<html>";
        html += "<head><title>Homepage</title>";
        html += "<link rel='stylesheet' type='text/css' href='common.css' />";
        html += "</head>";
        html += "<body>";
        html += """
            <div class='topnav'>
                <a href='/'>Homepage</a>
                <a href='mission.html'>Our Mission</a>
                <a href='page2A.html'>Sub Task 2.A</a>
                <a href='page2B.html'>Sub Task 2.B</a>
                <a href='page3A.html'>Sub Task 3.A</a>
                <a href='page3B.html'>Sub Task 3.B</a>
            </div>
        """;
        html += """
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
        html += "<div class='content'>";
        html += "<section class='hero'>";
        html += "<div class='hero-left'>";
        html += "<h2>Narrative about population</h2>";
        html += "<p>Select a year and LGA to view its population.</p>";
        html += "</div>";
        html += "<div class='hero-right'>";
        // Dropdown form for year and LGA
        html += "<form method='get' action='/'>";
        html += "<label for='year'>Year: </label>";
        html += "<select name='year' id='year' onchange='this.form.submit()'>";
        html += "<option value='2016'" + (year == 2016 ? " selected" : "") + ">2016</option>";
        html += "<option value='2021'" + (year == 2021 ? " selected" : "") + ">2021</option>";
        html += "</select> ";
        html += "<label for='lgaCode'>LGA: </label>";
        html += "<select name='lgaCode' id='lgaCode' onchange='this.form.submit()'>";
        html += "<option value=''>--Select LGA--</option>";
        for (LGA lga : lgas) {
            html += "<option value='" + lga.getCode() + "'" + (lgaCodeParam != null && lga.getCode().equals(lgaCodeParam) ? " selected" : "") + ">" + lga.getName() + "</option>";
        }
        html += "</select>";
        html += "</form>";
        html += "</div></section>";

        // Show selected LGA info
        html += "<div class='lga-list-section'>";
        if (selectedLGA != null) {
            html += "<h2>" + selectedLGA.getName() + "</h2>";
            html += "<p>Population (" + year + "): <b>" + selectedLGA.getPopulation() + "</b></p>";
        } else {
            html += "<p>Please select an LGA to view its population.</p>";
        }
        html += "</div>";
        // Carousel of cards (17 outcomes) with Next/Back pagination
        html += """
            <section class='carousel-wrap'>
                <div class='carousel' id='carousel'>
        """;
        for (int i = 1; i <= 17; i++) {
            html += "<div class='card' data-index='" + i + "'>" +
                   "<h4>Outcome " + i + "</h4>" +
                   "<p>Short description for outcome " + i + "</p>" +
                   "</div>";
        }
        html += """
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
                        if (this._updating) return;
                        this._updating = true;
                        window.requestAnimationFrame(function() { update(); carousel._updating = false; });
                    });
                    update();
                });
            </script>
        """;
        html += "</div>";
        html += """
            <div class='footer'>
                <p>COSC3056 - Studio Project Starter Code (Sep23)</p>
            </div>
        """;
        html += "</body></html>";
        context.html(html);
    }
}
