package app;

import java.util.ArrayList;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Class for Managing the JDBC Connection to a SQLLite Database.
 * Allows SQL queries to be used with the SQLLite Databse in Java.
 *
 * @author Timothy Wiley, 2023. email: timothy.wiley@rmit.edu.au
 * @author Santha Sumanasekara, 2021. email: santha.sumanasekara@rmit.edu.au
 */
public class JDBCConnection {

    // Name of database file (contained in database folder)
    public static final String DATABASE = "jdbc:sqlite:database/CTG.db";

    /**
     * This creates a JDBC Object so we can keep talking to the database
     */
    public JDBCConnection() {
        System.out.println("Created JDBC Connection Object");
    }

    /**
     * Get all of the LGAs in the database for a given year.
     * @param year The year to filter LGAs by (2016 or 2021)
     * @return Returns an ArrayList of LGA objects
     */
    public ArrayList<LGA> getLGAsByYear(int year) {
        ArrayList<LGA> lgas = new ArrayList<LGA>();
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(DATABASE);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            // Query to get LGA name and total population for each LGA in the given year
            String query = "SELECT LGA.lgaCode, LGA.lgaName, LGA.year, " +
                          "COALESCE(SUM(Population.populationValue), 0) as totalPopulation " +
                          "FROM LGA LEFT JOIN Population ON LGA.lgaCode = Population.lgaCode AND LGA.year = Population.year " +
                          "WHERE LGA.year = '" + year + "' " +
                          "GROUP BY LGA.lgaCode, LGA.lgaName, LGA.year";
            ResultSet results = statement.executeQuery(query);
            while (results.next()) {
                String code = results.getString("lgaCode");
                String name = results.getString("lgaName");
                int population = results.getInt("totalPopulation");
                LGA lga = new LGA(code, name, year, population);
                lgas.add(lga);
            }
            statement.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
        return lgas;
    }

    /**
     * Get all of the LGAs in the database for 2016.
     */
    public ArrayList<LGA> getLGAs2016() {
        return getLGAsByYear(2016);
    }

    /**
     * Get all of the LGAs in the database for 2021.
     */
    public ArrayList<LGA> getLGAs2021() {
        return getLGAsByYear(2021);
    }
}
