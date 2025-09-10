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
    /**
     * Get all states (stateID, name) from the States table.
     */
    public ArrayList<State> getStates() {
        ArrayList<State> states = new ArrayList<>();
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(DATABASE);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            String query = "SELECT stateID, name FROM States";
            ResultSet results = statement.executeQuery(query);
            while (results.next()) {
                int id = results.getInt("stateID");
                String name = results.getString("name");
                states.add(new State(id, name));
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
        return states;
    }

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
            String query = "SELECT LGA.lgaCode, LGA.lgaName, LGA.year, LGA.stateID, " +
                          "COALESCE(SUM(Population.populationValue), 0) as totalPopulation " +
                          "FROM LGA LEFT JOIN Population ON LGA.lgaCode = Population.lgaCode AND LGA.year = Population.year " +
                          "WHERE LGA.year = " + year + " " +
                          "GROUP BY LGA.lgaCode, LGA.lgaName, LGA.year, LGA.stateID";
            ResultSet results = statement.executeQuery(query);
            while (results.next()) {
                String code = results.getString("lgaCode");
                String name = results.getString("lgaName");
                int population = results.getInt("totalPopulation");
                int stateID = results.getInt("stateID");
                LGA lga = new LGA(code, name, year, population, stateID);
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
     * Get the total population for a state and year (sum of all LGAs in the state).
     */
    public int getTotalPopulationForState(int stateId, int year) {
        int total = 0;
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(DATABASE);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            String query = "SELECT SUM(Population.populationValue) as statePopulation " +
                          "FROM LGA INNER JOIN Population ON LGA.lgaCode = Population.lgaCode AND LGA.year = Population.year " +
                          "WHERE LGA.stateID = " + stateId + " AND LGA.year = " + year;
            ResultSet results = statement.executeQuery(query);
            if (results.next()) {
                total = results.getInt("statePopulation");
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
        return total;
    }

    /**
     * Get all of the outcomes from the database.
     * @return Returns an ArrayList of Outcome objects.
     */
    public ArrayList<Outcome> getOutcomes() {
        ArrayList<Outcome> outcomes = new ArrayList<Outcome>();
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(DATABASE);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            String query = "SELECT outcomeID, outcome, target FROM Outcomes";
            ResultSet results = statement.executeQuery(query);
            while (results.next()) {
                int id = results.getInt("outcomeID");
                String outcomeText = results.getString("outcome");
                String targetText = results.getString("target");
                Outcome outcome = new Outcome(id, outcomeText, targetText);
                outcomes.add(outcome);
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
        System.out.println("Found " + outcomes.size() + " outcomes in the database."); // Logging
        return outcomes;
    }

    public ArrayList<Members> getMembers() {
        // Create the ArrayList of Member objects to return
        ArrayList<Members> members = new ArrayList<Members>();

        // Setup the variable for the JDBC connection
        Connection connection = null;

        try {
            // Connect to JDBC data base
            connection = DriverManager.getConnection(DATABASE);

            // Prepare a new SQL Query & Set a timeout
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            // The Query
            String query = "SELECT sID AS code, name FROM MEMBERS";
            
            // Get Result
            ResultSet results = statement.executeQuery(query);

            // Process all of the results
            while (results.next()) {
                // Lookup the columns we need
                String code     = results.getString("code");
                String name  = results.getString("name");

                // Create a Member Object
                Members member = new Members(code, name);

                // Add the member object to the array
                members.add(member);
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

        // Finally we return all of the members
        return members;
    }

    public ArrayList<Persona> getPersona() {
        // Create the ArrayList of Persona objects to return
        ArrayList<Persona> persona = new ArrayList<Persona>();

        // Setup the variable for the JDBC connection
        Connection connection = null;

        try {
            // Connect to JDBC data base
            connection = DriverManager.getConnection(DATABASE);

            // Prepare a new SQL Query & Set a timeout
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            // The Query
            String query = "SELECT id AS code, quote, background, needs_goals, pain_points, skills_experience FROM PERSONA";
            
            // Get Result
            ResultSet results = statement.executeQuery(query);

            // Process all of the results
            while (results.next()) {
                // Lookup the columns we need
                String code     = results.getString("code");
                String quote  = results.getString("quote");
                String background = results.getString("background");
                String needs_goals = results.getString("needs_goals");
                String pain_points = results.getString("pain_points");
                String skills_experience = results.getString("skills_experience");

                // Create a Person Object
                Persona p = new Persona(code, quote, background, needs_goals, pain_points, skills_experience);

                // Add the persona object to the array
                persona.add(p);
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

        // Finally we return all of the persona
        return persona;
    }

    public ArrayList<Priorities> getPriorities() {
        // Create the ArrayList of Priority objects to return
        ArrayList<Priorities> priorities = new ArrayList<Priorities>();

        // Setup the variable for the JDBC connection
        Connection connection = null;

        try {
            // Connect to JDBC data base
            connection = DriverManager.getConnection(DATABASE);

            // Prepare a new SQL Query & Set a timeout
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            // The Query
            String query = "SELECT priorityID, description FROM PRIORITIES";
            
            // Get Result
            ResultSet results = statement.executeQuery(query);

            // Process all of the results
            while (results.next()) {
                // Lookup the columns we need
                String priorityID     = results.getString("priorityID");
                String description  = results.getString("description");

                // Create a Priority Object
                Priorities pr = new Priorities(priorityID, description);

                // Add the priority object to the array
                priorities.add(pr);
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

        // Finally we return all of the priorities
        return priorities;
    }
}
