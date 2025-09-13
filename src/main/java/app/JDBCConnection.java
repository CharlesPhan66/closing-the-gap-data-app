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
    * Get population gap results for the Population dataset, flexible for user-chosen status, sex, and age group filters.
    * @param status1 statusID for group 1 (e.g. indig)
    * @param status2 statusID for group 2 (e.g. non_indig)
    * @param sex sexID ("both", "f", "m")
    * @param ageIDs list of ageID strings (e.g. ["0_4_yrs", "5_9_yrs"])
    * @return list of GapResult
    */
    public ArrayList<GapResult> getPopulationGapResults(String status1, String status2, String sex, java.util.List<String> ageIDs) {
        ArrayList<GapResult> results = new ArrayList<>();
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(DATABASE);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            // SELECT 
            //     y2021.lgaName AS lga,
            //     y2016.status1Value AS indig_2016,
            //     y2016.status2Value AS non_indig_2016,
            //     y2016.gap AS gap_2016,
            //     y2021.status1Value AS indig_2021,
            //     y2021.status2Value AS non_indig_2021,
            //     y2021.gap AS gap_2021
            // FROM (
            //     SELECT
            //         p1.lgaCode,
            //         LGA.lgaName,
            //         SUM(p1.populationValue) AS status1Value, 
            //         SUM(p2.populationValue) AS status2Value,
            //         SUM(p2.populationValue) - SUM(p1.populationValue) AS gap
            //     FROM Population p1 
            //     JOIN Population p2
            //         ON p1.lgaCode = p2.lgaCode 
            //         AND p1.year = p2.year 
            //         AND p1.ageID = p2.ageID
            //         AND p1.sexID = p2.sexID
            //     JOIN LGA ON p1.lgaCode = LGA.lgaCode
            //     WHERE p1.year = 2016 
            //         AND p1.statusID = 'indig'
            //         AND p2.statusID = 'non_indig'
            //         AND p1.ageID IN (age groups selected)
            //         AND p1.sexID = 'f'
            //     GROUP BY p1.lgaCode
            //     ) y2016
            // RIGHT JOIN (
            //     SELECT
            //         p1.lgaCode,
            //         LGA.lgaName,
            //         SUM(p1.populationValue) AS status1Value, 
            //         SUM(p2.populationValue) AS status2Value,
            //         SUM(p2.populationValue) - SUM(p1.populationValue) AS gap
            //     FROM Population p1 
            //     JOIN Population p2
            //         ON p1.lgaCode = p2.lgaCode 
            //         AND p1.year = p2.year 
            //         AND p1.ageID = p2.ageID
            //         AND p1.sexID = p2.sexID
            //     JOIN LGA ON p1.lgaCode = LGA.lgaCode
            //     WHERE p1.year = 2021 
            //         AND p1.statusID = 'indig'
            //         AND p2.statusID = 'non_indig'
            //         AND p1.ageID IN ()
            //         AND p1.sexID = 'f'
            //     GROUP BY p1.lgaCode
            //     ) y2021
            // ON y2016.lgaCode = y2021.lgaCode;
            
            // Build IN clause for ageIDs
            StringBuilder ageIn = new StringBuilder();
            if (ageIDs != null && !ageIDs.isEmpty()) {
                ageIn.append("('");
                ageIn.append(String.join("','", ageIDs));
                ageIn.append("')");
            } else {
                ageIn.append("('')");
            }

            // Sex filter logic
            String sexJoin = "";
            String sexWhere = "";
            if (!"both".equalsIgnoreCase(sex)) {
                sexJoin = " AND p1.sexID = p2.sexID ";
                sexWhere = " AND p1.sexID = '" + sex + "' ";
            }

            // Query for 2016 and 2021 subqueries
            String subquery =
                "SELECT p1.lgaCode, LGA.lgaName, " +
                "SUM(p1.populationValue) AS status1Value, " +
                "SUM(p2.populationValue) AS status2Value, " +
                "SUM(p2.populationValue) - SUM(p1.populationValue) AS gap " +
                "FROM Population p1 " +
                "JOIN Population p2 ON p1.lgaCode = p2.lgaCode AND p1.year = p2.year AND p1.ageID = p2.ageID" + sexJoin + " " +
                "JOIN LGA ON p1.lgaCode = LGA.lgaCode " +
                "WHERE p1.year = %YEAR% " +
                "AND p1.statusID = '" + status1 + "' " +
                "AND p2.statusID = '" + status2 + "' " +
                "AND p1.ageID IN " + ageIn.toString() + sexWhere + " " +
                "GROUP BY p1.lgaCode";

            String sub2016 = subquery.replace("%YEAR%", "2016");
            String sub2021 = subquery.replace("%YEAR%", "2021");

            StringBuilder query = new StringBuilder();
            query.append("SELECT y2021.lgaName AS lga, ");
            query.append("y2016.status1Value AS status1_2016, y2016.status2Value AS status2_2016, y2016.gap AS gap_2016, ");
            query.append("y2021.status1Value AS status1_2021, y2021.status2Value AS status2_2021, y2021.gap AS gap_2021 ");
            query.append("FROM (").append(sub2016).append(") y2016 ");
            query.append("RIGHT JOIN (").append(sub2021).append(") y2021 ");
            query.append("ON y2016.lgaCode = y2021.lgaCode");

            ResultSet rs = statement.executeQuery(query.toString());
            while (rs.next()) {
                String lga = rs.getString("lga");
                Integer status1_2016 = rs.getObject("status1_2016") != null ? rs.getInt("status1_2016") : null;
                Integer status2_2016 = rs.getObject("status2_2016") != null ? rs.getInt("status2_2016") : null;
                Integer gap_2016 = rs.getObject("gap_2016") != null ? rs.getInt("gap_2016") : null;
                Integer status1_2021 = rs.getObject("status1_2021") != null ? rs.getInt("status1_2021") : null;
                Integer status2_2021 = rs.getObject("status2_2021") != null ? rs.getInt("status2_2021") : null;
                Integer gap_2021 = rs.getObject("gap_2021") != null ? rs.getInt("gap_2021") : null;
                results.add(new GapResult(lga, status1_2016, status2_2016, gap_2016, status1_2021, status2_2021, gap_2021));
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
        return results;
    }

    /**
     * Get Health records summarized by demographics.
     * if any filter is null, instead of listing all records for that filter, using aggregate the populationValue.
     * Example: if statusID is null, then get total populationValue for all statusID.
     * @author @charlesphan0206
     */
    public ArrayList<Health> getHealthSummaryByFilters(String year, String stateID, String lgaCode, String sexID, String statusID, String conditionID, String orderByClause, Integer limit) {
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
            // If an ORDER BY is requested, use a CTE to compute the aggregates then apply ROW_NUMBER() over the requested ordering.
            boolean hasOrder = (orderByClause != null && !orderByClause.trim().isEmpty());
            String finalQuery;
            if (hasOrder) {
                // sanitize orderByClause so it references columns available in the outer SELECT from the CTE
                String sanitizedOrder = orderByClause;
                // Replace CAST(SUM(h.populationValue) AS REAL) with CAST(totalPopulation AS REAL)
                sanitizedOrder = sanitizedOrder.replaceAll("(?i)CAST\\(SUM\\(h\\.populationValue\\) AS REAL\\)", "CAST(totalPopulation AS REAL)");
                // Replace SUM(h.populationValue) with totalPopulation
                sanitizedOrder = sanitizedOrder.replaceAll("(?i)SUM\\(h\\.populationValue\\)", "totalPopulation");
                // Remove h. prefix references (e.g., h.populationValue -> populationValue)
                sanitizedOrder = sanitizedOrder.replaceAll("(?i)h\\.", "");
                StringBuilder cte = new StringBuilder();
                cte.append("WITH summary AS (\n");
                cte.append("  SELECT ").append(String.join(", ", selectFields)).append(", SUM(h.populationValue) AS totalPopulation\n");
                cte.append("  FROM Health h \n");
                cte.append("  JOIN LGA l ON h.lgaCode = l.lgaCode AND h.year = l.year \n");
                cte.append("  JOIN States stt ON l.stateID = stt.stateID \n");
                cte.append("  JOIN Sex s ON h.sexID = s.sexID \n");
                cte.append("  JOIN indigStatus st ON h.statusID = st.statusID \n");
                cte.append("  JOIN healthCondition hC ON h.conditionID = hC.conditionID \n");
                cte.append("  WHERE 1=1 \n");
                if (year != null && !year.equals("none")) {
                    cte.append(" AND h.year='").append(year).append("' \n");
                }
                if (lgaCode != null && !lgaCode.equals("none")) {
                    cte.append(" AND h.lgaCode='").append(lgaCode).append("' \n");
                } else if (stateID != null && !stateID.equals("none")) {
                    cte.append(" AND stt.stateID='").append(stateID).append("' \n");
                }
                if (!groupSex) {
                    cte.append(" AND h.sexID='").append(sexID).append("' \n");
                }
                if (!groupStatus) {
                    cte.append(" AND h.statusID='").append(statusID).append("' \n");
                }
                if (!groupDisease) {
                    cte.append(" AND h.conditionID='").append(conditionID).append("' \n");
                }
                cte.append("  GROUP BY ").append(String.join(", ", groupByFields)).append("\n");
                cte.append(")\n");
                cte.append("SELECT *, ROW_NUMBER() OVER (ORDER BY ").append(sanitizedOrder).append(") AS rn FROM summary");
                if (limit != null && limit > 0) {
                    cte.append(" LIMIT ").append(limit);
                }
                finalQuery = cte.toString();
            } else {
                if (limit != null && limit > 0) {
                    query.append(" LIMIT ").append(limit);
                }
                finalQuery = query.toString();
            }
            ResultSet results = statement.executeQuery(finalQuery);
            while (results.next()) {
                String stateName = hasColumn(results, "stateName") ? results.getString("stateName") : null;
                String lgaName = hasColumn(results, "lgaName") ? results.getString("lgaName") : null;
                int totalPopulation = results.getInt("totalPopulation");
                String sex = hasColumn(results, "sex") ? results.getString("sex") : null;
                String status = hasColumn(results, "status") ? results.getString("status") : null;
                String diseaseName = hasColumn(results, "diseaseName") ? results.getString("diseaseName") : null;
                Health health = new Health(stateName, lgaName, sex, status, diseaseName, totalPopulation);
                // If rn exists, populate rank
                if (hasColumn(results, "rn")) {
                    try { health.setRank(results.getInt("rn")); } catch (SQLException e) { /* ignore */ }
                }
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
    public ArrayList<Health> getHealthByFilters(String year, String lgaCode, String sexID, String statusID, String conditionID, String orderByClause, Integer limit) {
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
            boolean hasOrder = (orderByClause != null && !orderByClause.trim().isEmpty());
            String finalQuery;
            if (hasOrder) {
                // sanitize as above: remove h. and replace population aggregates
                String sanitizedOrder = orderByClause;
                sanitizedOrder = sanitizedOrder.replaceAll("(?i)CAST\\(SUM\\(h\\.populationValue\\) AS REAL\\)", "CAST(totalPopulation AS REAL)");
                sanitizedOrder = sanitizedOrder.replaceAll("(?i)SUM\\(h\\.populationValue\\)", "totalPopulation");
                sanitizedOrder = sanitizedOrder.replaceAll("(?i)h\\.", "");
                StringBuilder cte = new StringBuilder();
                cte.append("WITH detail AS (\n");
                cte.append(query.toString()).append("\n");
                if (limit != null && limit > 0) {
                    cte.append(")\nSELECT *, ROW_NUMBER() OVER (ORDER BY ").append(sanitizedOrder).append(") AS rn FROM detail LIMIT ").append(limit);
                } else {
                    cte.append(")\nSELECT *, ROW_NUMBER() OVER (ORDER BY ").append(sanitizedOrder).append(") AS rn FROM detail");
                }
                finalQuery = cte.toString();
            } else {
                if (limit != null && limit > 0) {
                    query.append(" LIMIT ").append(limit);
                }
                finalQuery = query.toString();
            }
            ResultSet results = statement.executeQuery(finalQuery);
            while (results.next()) {
                String resultLgaName = results.getString("lgaName");
                String resultSex = results.getString("sex");
                String resultStatus = results.getString("status");
                String resultDisease = results.getString("diseaseName");
                int populationValue = results.getInt("populationValue");
                Health health = new Health(resultLgaName, resultSex, resultStatus, resultDisease, populationValue);
                if (hasColumn(results, "rn")) {
                    try { health.setRank(results.getInt("rn")); } catch (SQLException e) { /* ignore */ }
                }
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
                String ageID = results.getString("ageID");
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
     * If eduType is "none", it will return data for both school and non-school education.
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

            // Case 1: Only School Education is selected
            if ("Education".equals(eduType)) {
                String query = buildEducationQuery("Education", year, lgaCode, sexID, statusID, ageID, eduLevel);
                ResultSet results = statement.executeQuery(query);
                while (results.next()) {
                    eduList.add(new Health(results.getString("lgaName"), results.getString("sex"), results.getString("status"), results.getString("level"), results.getInt("populationValue")));
                }
            } 
            // Case 2: Only Non-School Education is selected
            else if ("NonSchoolEdu".equals(eduType)) {
                String query = buildEducationQuery("NonSchoolEdu", year, lgaCode, sexID, statusID, ageID, eduLevel);
                ResultSet results = statement.executeQuery(query);
                while (results.next()) {
                    eduList.add(new Health(results.getString("lgaName"), results.getString("sex"), results.getString("status"), results.getString("name"), results.getInt("populationValue")));
                }
            } 
            // Case 3: No Education Type is selected (get both)
            else {
                // Query for School Education (ignoring eduLevel)
                String schoolQuery = buildEducationQuery("Education", year, lgaCode, sexID, statusID, ageID, "none");
                ResultSet schoolResults = statement.executeQuery(schoolQuery);
                while (schoolResults.next()) {
                    eduList.add(new Health(schoolResults.getString("lgaName"), schoolResults.getString("sex"), schoolResults.getString("status"), schoolResults.getString("level"), schoolResults.getInt("populationValue")));
                }
                schoolResults.close();

                // Query for Non-School Education (ignoring eduLevel)
                String nonSchoolQuery = buildEducationQuery("NonSchoolEdu", year, lgaCode, sexID, statusID, ageID, "none");
                ResultSet nonSchoolResults = statement.executeQuery(nonSchoolQuery);
                while (nonSchoolResults.next()) {
                    eduList.add(new Health(nonSchoolResults.getString("lgaName"), nonSchoolResults.getString("sex"), nonSchoolResults.getString("status"), nonSchoolResults.getString("name"), nonSchoolResults.getInt("populationValue")));
                }
                nonSchoolResults.close();
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

    private String buildEducationQuery(String eduType, String year, String lgaCode, String sexID, String statusID, String ageID, String eduLevel) {
        StringBuilder query = new StringBuilder();
        if ("Education".equals(eduType)) {
            query.append("SELECT l.lgaName, s.sex, st.status, e.level, ed.populationValue FROM Education ed JOIN LGA l ON ed.lgaCode = l.lgaCode AND ed.year = l.year JOIN Sex s ON ed.sexID = s.sexID JOIN indigStatus st ON ed.statusID = st.statusID JOIN Edu e ON ed.levelID = e.levelID WHERE 1=1");
            if (eduLevel != null && !eduLevel.equals("none")) query.append(" AND ed.levelID='").append(eduLevel).append("'");
        } else { // NonSchoolEdu
            query.append("SELECT l.lgaName, s.sex, st.status, ns.name, nse.populationValue FROM NonSchoolEdu nse JOIN LGA l ON nse.lgaCode = l.lgaCode AND nse.year = l.year JOIN Sex s ON nse.sexID = s.sexID JOIN indigStatus st ON nse.statusID = st.statusID JOIN nonSchool ns ON nse.d_cID = ns.d_cID WHERE 1=1");
            if (eduLevel != null && !eduLevel.equals("none")) query.append(" AND nse.d_cID='").append(eduLevel).append("'");
        }

        String tablePrefix = "Education".equals(eduType) ? "ed" : "nse";
        if (year != null && !year.equals("none")) query.append(" AND ").append(tablePrefix).append(".year='").append(year).append("'");
        if (lgaCode != null && !lgaCode.equals("none")) query.append(" AND ").append(tablePrefix).append(".lgaCode='").append(lgaCode).append("'");
        if (sexID != null && !sexID.equals("none")) query.append(" AND ").append(tablePrefix).append(".sexID='").append(sexID).append("'");
        if (statusID != null && !statusID.equals("none")) query.append(" AND ").append(tablePrefix).append(".statusID='").append(statusID).append("'");
        if (ageID != null && !ageID.equals("none")) query.append(" AND ").append(tablePrefix).append(".ageID='").append(ageID).append("'");

        return query.toString();
    }

    /**
     * Gets the total population from both Education and NonSchoolEdu tables for a given set of filters.
     * This is used to calculate percentages.
     */
    public long getTotalEducationPopulationByFilter(String year, String lgaCode, String sexID, String statusID, String ageID) {
        long totalPopulation = 0;
        
        // Base query to combine both tables
        String baseQuery = """
            SELECT SUM(populationValue) AS total_population
            FROM (
                SELECT populationValue, year, lgaCode, sexID, statusID, ageID FROM Education
                UNION ALL
                SELECT populationValue, year, lgaCode, sexID, statusID, ageID FROM NonSchoolEdu
            ) AS combined
            WHERE 1=1
        """;

        StringBuilder conditions = new StringBuilder();
        if (year != null && !year.equals("none")) {
            conditions.append(" AND year = '").append(year).append("'");
        }
        if (lgaCode != null && !lgaCode.equals("none")) {
            conditions.append(" AND lgaCode = '").append(lgaCode).append("'");
        }
        if (sexID != null && !sexID.equals("none")) {
            conditions.append(" AND sexID = '").append(sexID).append("'");
        }
        if (statusID != null && !statusID.equals("none")) {
            conditions.append(" AND statusID = '").append(statusID).append("'");
        }
        if (ageID != null && !ageID.equals("none")) {
            conditions.append(" AND ageID = '").append(ageID).append("'");
        }

        String finalQuery = baseQuery + conditions.toString();

        try (Connection connection = DriverManager.getConnection(DATABASE);
             Statement statement = connection.createStatement()) {
            
            statement.setQueryTimeout(30);
            ResultSet results = statement.executeQuery(finalQuery);
            if (results.next()) {
                totalPopulation = results.getLong("total_population");
            }
        } catch (SQLException e) {
            System.err.println("Error in getTotalEducationPopulationByFilter: " + e.getMessage());
        }
        return totalPopulation;
    }

    /**
     * Gets summarized (aggregated) education data based on filters.
     * It groups by education level/degree, respecting the chosen filters.
     */
    public ArrayList<Health> getEducationSummaryByFilter(String year, String lgaCode, String sexID, String statusID, String ageID, String eduType, String eduLevel) {
        ArrayList<Health> summaryList = new ArrayList<>();
        
        String queryTemplate = """
            SELECT
                CASE
                    WHEN combined.type = 'Education' THEN e.level
                    ELSE ns.name
                END AS category,
                SUM(combined.populationValue) AS populationValue
            FROM (
                SELECT 'Education' as type, year, lgaCode, sexID, statusID, ageID, levelID as category_id, populationValue FROM Education
                UNION ALL
                SELECT 'NonSchoolEdu' as type, year, lgaCode, sexID, statusID, ageID, d_cID as category_id, populationValue FROM NonSchoolEdu
            ) AS combined
            LEFT JOIN Edu e ON combined.category_id = e.levelID AND combined.type = 'Education'
            LEFT JOIN nonSchool ns ON combined.category_id = ns.d_cID AND combined.type = 'NonSchoolEdu'
            WHERE 1=1 %s
            GROUP BY category
            HAVING SUM(combined.populationValue) > 0
            ORDER BY category
        """;

        StringBuilder conditions = new StringBuilder();
        // Apply filters to the combined dataset
        if (year != null && !year.equals("none")) {
            conditions.append(" AND combined.year = '").append(year).append("'");
        }
        if (lgaCode != null && !lgaCode.equals("none")) {
            conditions.append(" AND combined.lgaCode = '").append(lgaCode).append("'");
        }
        if (sexID != null && !sexID.equals("none")) {
            conditions.append(" AND combined.sexID = '").append(sexID).append("'");
        }
        if (statusID != null && !statusID.equals("none")) {
            conditions.append(" AND combined.statusID = '").append(statusID).append("'");
        }
        if (ageID != null && !ageID.equals("none")) {
            conditions.append(" AND combined.ageID = '").append(ageID).append("'");
        }
        if (eduType != null && !eduType.equals("none")) {
            conditions.append(" AND combined.type = '").append(eduType).append("'");
            if (eduLevel != null && !eduLevel.equals("none")) {
                conditions.append(" AND combined.category_id = '").append(eduLevel).append("'");
            }
        }

        String finalQuery = String.format(queryTemplate, conditions.toString());

        try (Connection connection = DriverManager.getConnection(DATABASE);
             Statement statement = connection.createStatement()) {
            statement.setQueryTimeout(30);
            ResultSet results = statement.executeQuery(finalQuery);
            while (results.next()) {
                // Using placeholders for fields that are not applicable in summary view
                summaryList.add(new Health(
                    "Summary", // LGA Name is not relevant when grouped
                    "All",     // Sex is aggregated
                    "All",     // Status is aggregated
                    results.getString("category"),
                    results.getInt("populationValue")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error in getEducationSummaryByFilter: " + e.getMessage());
            e.printStackTrace();
        }
        return summaryList;
    }

}
