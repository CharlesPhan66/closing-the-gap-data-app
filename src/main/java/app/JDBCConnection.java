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
     * Get all of the LGAs in the database.
     * @return
     *    Returns an ArrayList of LGA objects
     */
    public ArrayList<LGA> getLGAs2016() {
        // Create the ArrayList of LGA objects to return
        ArrayList<LGA> lgas = new ArrayList<LGA>();

        // Setup the variable for the JDBC connection
        Connection connection = null;

        try {
            // Connect to JDBC data base
            connection = DriverManager.getConnection(DATABASE);

            // Prepare a new SQL Query & Set a timeout
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            // The Query
            String query = "SELECT * FROM LGA WHERE year='2016'";
            
            // Get Result
            ResultSet results = statement.executeQuery(query);

            // Process all of the results
            while (results.next()) {
                // Lookup the columns we need
                String code     = results.getString("code");
                String name  = results.getString("name");

                // Create a LGA Object
                LGA lga = new LGA(code, name, 2016);

                // Add the lga object to the array
                lgas.add(lga);
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

    /**
     * Get population value by age range and gender.
     */
    public int getPopulationValue(int ageStart, String ageEnd, String gender) {
        int populationValue = 0;
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(DATABASE);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String query = String.format(
                "SELECT p.populationValue " +
                "FROM population p " +
                "JOIN Sex s ON p.sexID = s.sexID " +
                "JOIN ageGroup ag ON p.ageID = ag.ageID " +
                "WHERE ag.ageStart = '%d' AND ag.ageEnd = '%s' AND s.sex = '%s'",
                ageStart, ageEnd, gender
            );

            ResultSet results = statement.executeQuery(query);

            if (results.next()) {
                populationValue = results.getInt("populationValue");
            }

            statement.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }

        return populationValue;
    }

    /**
     * Get all age groups from the ageGroup table.
     * @return ArrayList of Age objects
     */
    public ArrayList<Age> getAgeGroup() {
        ArrayList<Age> ageGroups = new ArrayList<>();
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(DATABASE);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String query = "SELECT ageID, ageStart, ageEnd FROM ageGroup";
            ResultSet results = statement.executeQuery(query);

            while (results.next()) {
                int ageID = results.getInt("ageID");
                int ageStart = results.getInt("ageStart");
                int ageEnd = results.getInt("ageEnd");
                Age age = new Age(ageID, ageStart, ageEnd);
                ageGroups.add(age);
            }

            statement.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }

        return ageGroups;
    }

    /**
     * Get all sex values from the Sex table.
     * @return ArrayList of String representing sex ("Female", "Male")
     */
    public ArrayList<String> getSexValues() {
        ArrayList<String> sexList = new ArrayList<>();
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(DATABASE);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String query = "SELECT sex FROM Sex ORDER BY sexID DESC";
            ResultSet results = statement.executeQuery(query);

            while (results.next()) {
                String sex = results.getString("sex");
                sexList.add(sex);
            }

            statement.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }

        return sexList;
    }

    // TODO: Add your required methods here
}
