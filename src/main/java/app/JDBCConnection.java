package app;

import java.util.ArrayList;
import java.lang.StringBuilder;
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
     * Get Health records summarized by demographics.
     * if any filter is null, instead of listing all records for that filter, using aggregate the populationValue.
     * Example: if statusID is null, then get total populationValue for all statusID.
     * @author @charlesphan0206
     */
    public ArrayList<Health> getHealthSummaryByFilters(String year, String stateID, String lgaCode, String sexID, String statusID, String conditionID) {
        ArrayList<Health> healthSummaryList = new ArrayList<>();
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(DATABASE);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            // Build SELECT and GROUP BY dynamically for summary mode
            ArrayList<String> selectFields = new ArrayList<>();
            ArrayList<String> groupByFields = new ArrayList<>();
            boolean groupByLGA = (lgaCode != null && !lgaCode.equals("none"));
            boolean groupByState = (lgaCode == null || lgaCode.equals("none"));
            if (groupByLGA) {
                selectFields.add("l.lgaName");
                groupByFields.add("l.lgaName");
            }
            if (groupByState) {
                selectFields.add("stt.name AS stateName");
                groupByFields.add("stt.name");
            }
            boolean groupSex = (sexID == null || sexID.equals("none"));
            boolean groupStatus = (statusID == null || statusID.equals("none"));
            boolean groupDisease = (conditionID == null || conditionID.equals("none"));
            if (!groupSex) {
                selectFields.add("s.sex");
                groupByFields.add("s.sex");
            }
            if (!groupStatus) {
                selectFields.add("st.status");
                groupByFields.add("st.status");
            }
            if (!groupDisease) {
                selectFields.add("hC.diseaseName");
                groupByFields.add("hC.diseaseName");
            }
            selectFields.add("SUM(h.populationValue) AS totalPopulation");
            StringBuilder query = new StringBuilder();
            query.append("SELECT ").append(String.join(", ", selectFields)).append(" ");
            query.append("FROM Health h ");
            query.append("JOIN LGA l ON h.lgaCode = l.lgaCode AND h.year = l.year ");
            query.append("JOIN States stt ON l.stateID = stt.stateID ");
            query.append("JOIN Sex s ON h.sexID = s.sexID ");
            query.append("JOIN indigStatus st ON h.statusID = st.statusID ");
            query.append("JOIN healthCondition hC ON h.conditionID = hC.conditionID ");
            query.append("WHERE 1=1 ");
            if (year != null && !year.equals("none")) {
                query.append("AND h.year='").append(year).append("' ");
            }
            if (lgaCode != null && !lgaCode.equals("none")) {
                query.append("AND h.lgaCode='").append(lgaCode).append("' ");
            } else if (stateID != null && !stateID.equals("none")) {
                query.append("AND stt.stateID='").append(stateID).append("' ");
            }
            if (!groupSex) {
                query.append("AND h.sexID='").append(sexID).append("' ");
            }
            if (!groupStatus) {
                query.append("AND h.statusID='").append(statusID).append("' ");
            }
            if (!groupDisease) {
                query.append("AND h.conditionID='").append(conditionID).append("' ");
            }
            query.append("GROUP BY ").append(String.join(", ", groupByFields));
            ResultSet results = statement.executeQuery(query.toString());
            while (results.next()) {
                String stateName = hasColumn(results, "stateName") ? results.getString("stateName") : null;
                String lgaName = hasColumn(results, "lgaName") ? results.getString("lgaName") : null;
                int totalPopulation = results.getInt("totalPopulation");
                String sex = hasColumn(results, "sex") ? results.getString("sex") : null;
                String status = hasColumn(results, "status") ? results.getString("status") : null;
                String diseaseName = hasColumn(results, "diseaseName") ? results.getString("diseaseName") : null;
                Health health = new Health(stateName, lgaName, sex, status, diseaseName, totalPopulation);
                healthSummaryList.add(health);
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
        return healthSummaryList;
    }

        // Helper method to check if a column exists in the ResultSet
        private boolean hasColumn(ResultSet rs, String columnName) {
            try {
                return rs.findColumn(columnName) > 0;
            } catch (SQLException e) {
                return false;
            }
        }
    /**
     * Get Health records by filters: year, lgaCode, sexID, statusID
     * Any filter can be null to ignore that filter.
     * @author @charlesphan0206
     */
    public ArrayList<Health> getHealthByFilters(String year, String lgaCode, String sexID, String statusID, String conditionID) {
        ArrayList<Health> healthList = new ArrayList<>();
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(DATABASE);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            StringBuilder query = new StringBuilder();
            query.append("""
                            SELECT 
                                l.lgaName, 
                                s.sex, 
                                st.status, 
                                hC.diseaseName, 
                                h.populationValue 
                            FROM Health h
                            JOIN LGA l 
                                ON h.lgaCode = l.lgaCode AND h.year = l.year
                            JOIN Sex s
                                ON h.sexID = s.sexID
                            JOIN indigStatus st
                                ON h.statusID = st.statusID
                            JOIN healthCondition hC
                                ON h.conditionID = hC.conditionID
                            WHERE 1=1
                        """);
            if (year != null && !year.equals("none")) {
                query.append(" AND h.year='").append(year).append("'");
            }
            if (lgaCode != null && !lgaCode.equals("none")) {
                query.append(" AND h.lgaCode='").append(lgaCode).append("'");
            }
            if (sexID != null && !sexID.equals("none")) {
                query.append(" AND h.sexID='").append(sexID).append("'");
            }
            if (statusID != null && !statusID.equals("none")) {
                query.append(" AND h.statusID='").append(statusID).append("'");
            }
            if (conditionID != null && !conditionID.equals("none")) {
                query.append(" AND h.conditionID='").append(conditionID).append("'");
            }
            ResultSet results = statement.executeQuery(query.toString());
            while (results.next()) {
                String resultLgaName = results.getString("lgaName");
                String resultSex = results.getString("sex");
                String resultStatus = results.getString("status");
                String resultDisease = results.getString("diseaseName");
                int populationValue = results.getInt("populationValue");
                Health health = new Health(resultLgaName, resultSex, resultStatus, resultDisease, populationValue);
                healthList.add(health);
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
        return healthList;
    }
    /**
     * Get all the LGAs of a specific State.
     * @return ArrayList of LGA objects
     */
    public ArrayList<LGA> getLGAsByState(String stateID, String year) {
        ArrayList<LGA> lgas = new ArrayList<>();
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(DATABASE);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            StringBuilder qb = new StringBuilder();
            qb.append("SELECT lgaCode, lgaName FROM LGA JOIN States ON LGA.stateID = States.stateID WHERE 1=1 ");
            if (stateID != null && !stateID.equals("none")) {
                qb.append(" AND States.stateID = '").append(stateID).append("'");
            }
            if (year != null && !year.equals("none")) {
                qb.append(" AND year = '").append(year).append("'");
            }
            String query = qb.toString();
            ResultSet results = statement.executeQuery(query);
            while (results.next()) {
                String code = results.getString("lgaCode");
                String name = results.getString("lgaName");
                int yr = 0;
                try { yr = Integer.parseInt(year); } catch (NumberFormatException e) { yr = 0; }
                LGA lga = new LGA(code, name, yr);
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
     * Get all health conditions in the database.
     * @return ArrayList of HealthCondition objects
     * @author @charlesphan0206
     */
    public ArrayList<Condition> getHealthConditions() {
        ArrayList<Condition> conditions = new ArrayList<>();
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(DATABASE);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            String query = "SELECT conditionID, diseaseName, description FROM healthCondition";
            ResultSet results = statement.executeQuery(query);
            while (results.next()) {
                String conditionID = results.getString("conditionID");
                String diseaseName = results.getString("diseaseName");
                String description = results.getString("description");
                Condition condition = new Condition(conditionID, diseaseName, description);
                conditions.add(condition);
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
        return conditions;
    }

    /**
     * Get all States in the database.
     * @return ArrayList of State objects
     * @author @charlesphan0206
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
                String stateID = results.getString("stateID");
                String stateName = results.getString("name");
                State state = new State(stateID, stateName);
                state.setName(stateName);
                states.add(state);
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

    /**
     * Get all status from the indigStatus table.
     * @return ArrayList of IndigStatus objects
     * @author @charlesphan0206
     */
    public ArrayList<IndigStatus> getIndigStatus() {
        ArrayList<IndigStatus> statusList = new ArrayList<>();
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(DATABASE);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            String query = "SELECT * FROM indigStatus";
            ResultSet results = statement.executeQuery(query);
            while (results.next()) {
                String statusID = results.getString(1);
                String statusName = results.getString(2);
                IndigStatus status = new IndigStatus(statusID, statusName);
                statusList.add(status);
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
        return statusList;
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
     * Get the total population for a specific LGA and year.
     * @param lgaCode the LGA code
     * @param year the year
     * @return total population (0 if not found)
     * @author @charlesphan0206
     */
    public int getTotalPopulationForLGA(String lgaCode, int year) {
        int total = 0;
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(DATABASE);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            String query = "SELECT SUM(populationValue) as lgaPopulation FROM Population WHERE lgaCode = '" + lgaCode + "' AND year = " + year;
            ResultSet results = statement.executeQuery(query);
            if (results.next()) {
                total = results.getInt("lgaPopulation");
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

        // Finally we return all of the lga
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
    public ArrayList<Gender> getSexValues() {
        ArrayList<Gender> sexList = new ArrayList<>();
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(DATABASE);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String query = "SELECT sexID, sex FROM Sex ORDER BY sexID DESC";
            ResultSet results = statement.executeQuery(query);

            while (results.next()) {
                String sexID = results.getString("sexID");
                String sex = results.getString("sex");
                Gender gender = new Gender(sexID, sex);
                sexList.add(gender);
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

    /**
     * Get all education levels from the Edu table.
     * @return ArrayList of Education objects
     */
    public ArrayList<Education> getEducation() {
        ArrayList<Education> educationList = new ArrayList<>();
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(DATABASE);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String query = "SELECT levelID, level FROM Edu";
            ResultSet results = statement.executeQuery(query);

            while (results.next()) {
                String levelID = results.getString("levelID");
                String level = results.getString("level");
                Education education = new Education(levelID, level);
                educationList.add(education);
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

        return educationList;
    }

    /**
     * Get all non-school education degree types from nonSchool table.
     * @return ArrayList of NonSchoolEdu objects
     */
    public ArrayList<NonSchoolEdu> getNonSchoolEdu() {
        ArrayList<NonSchoolEdu> degreeList = new ArrayList<>();
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(DATABASE);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String query = "SELECT d_cID, name FROM nonSchool";
            ResultSet results = statement.executeQuery(query);

            while (results.next()) {
                String d_cID = results.getString("d_cID");
                String name = results.getString("name");
                NonSchoolEdu degree = new NonSchoolEdu(d_cID, name);
                degreeList.add(degree);
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

        return degreeList;
    }

    /**
     * Get education records by filters: year, lgaCode, sexID, statusID, ageID, eduType, eduLevel
     * Any filter can be null or "none" to ignore that filter.
     */
    public ArrayList<Health> getEducationByFilter(
        String year, String lgaCode, String sexID, String statusID, String ageID, String eduType, String eduLevel
    ) {
        ArrayList<Health> eduList = new ArrayList<>();
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(DATABASE);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            StringBuilder query = new StringBuilder();
            if ("Education".equals(eduType)) {
                query.append("""
                    SELECT l.lgaName, s.sex, st.status, e.level, ed.populationValue
                    FROM Education ed
                    JOIN LGA l ON ed.lgaCode = l.lgaCode AND ed.year = l.year
                    JOIN Sex s ON ed.sexID = s.sexID
                    JOIN indigStatus st ON ed.statusID = st.statusID
                    JOIN Edu e ON ed.levelID = e.levelID
                    WHERE 1=1
                """);
                if (year != null && !year.equals("none")) {
                    query.append(" AND ed.year='").append(year).append("'");
                }
                if (lgaCode != null && !lgaCode.equals("none")) {
                    query.append(" AND ed.lgaCode='").append(lgaCode).append("'");
                }
                if (sexID != null && !sexID.equals("none")) {
                    query.append(" AND ed.sexID='").append(sexID).append("'");
                }
                if (statusID != null && !statusID.equals("none")) {
                    query.append(" AND ed.statusID='").append(statusID).append("'");
                }
                if (ageID != null && !ageID.equals("none")) {
                    query.append(" AND ed.ageID='").append(ageID).append("'");
                }
                if (eduLevel != null && !eduLevel.equals("none")) {
                    query.append(" AND ed.levelID='").append(eduLevel).append("'");
                }
            } else if ("NonSchoolEdu".equals(eduType)) {
                query.append("""
                    SELECT l.lgaName, s.sex, st.status, ns.name, nse.populationValue
                    FROM NonSchoolEdu nse
                    JOIN LGA l ON nse.lgaCode = l.lgaCode AND nse.year = l.year
                    JOIN Sex s ON nse.sexID = s.sexID
                    JOIN indigStatus st ON nse.statusID = st.statusID
                    JOIN nonSchool ns ON nse.d_cID = ns.d_cID
                    WHERE 1=1
                """);
                if (year != null && !year.equals("none")) {
                    query.append(" AND nse.year='").append(year).append("'");
                }
                if (lgaCode != null && !lgaCode.equals("none")) {
                    query.append(" AND nse.lgaCode='").append(lgaCode).append("'");
                }
                if (sexID != null && !sexID.equals("none")) {
                    query.append(" AND nse.sexID='").append(sexID).append("'");
                }
                if (statusID != null && !statusID.equals("none")) {
                    query.append(" AND nse.statusID='").append(statusID).append("'");
                }
                if (ageID != null && !ageID.equals("none")) {
                    query.append(" AND nse.ageID='").append(ageID).append("'");
                }
                if (eduLevel != null && !eduLevel.equals("none")) {
                    query.append(" AND nse.d_cID='").append(eduLevel).append("'");
                }
            } else {
                // If no eduType selected, return empty list
                return eduList;
            }

            ResultSet results = statement.executeQuery(query.toString());
            while (results.next()) {
                String lgaName = results.getString("lgaName");
                String sex = results.getString("sex");
                String status = results.getString("status");
                String levelOrName = ("Education".equals(eduType)) ? results.getString("level") : results.getString("name");
                int populationValue = results.getInt("populationValue");
                // Use Health class for display, set disease field as education level/name
                Health edu = new Health(lgaName, sex, status, levelOrName, populationValue);
                eduList.add(edu);
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
        return eduList;
    }

}
