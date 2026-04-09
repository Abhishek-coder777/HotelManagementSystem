// database/DatabaseConnection.java
package database;

import java.sql.*;
import javax.swing.*;

public class DatabaseConnection {
    // Database configuration - Update these values according to your setup
    private static final String URL = "jdbc:mysql://localhost:3306/hotel_management";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "10109";
    
    private static Connection connection = null;
    
    /**
     * Establishes and returns a database connection
     * @return Connection object or null if connection fails
     */
    public static Connection getConnection() {
        try {
            // Check if connection exists and is still valid
            if (connection == null || connection.isClosed()) {
                // Load MySQL JDBC Driver
                Class.forName("com.mysql.cj.jdbc.Driver");
                
                // Create the connection
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                
                System.out.println("Database connected successfully!");
            }
            return connection;
            
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found!");
            JOptionPane.showMessageDialog(null, 
                "MySQL JDBC Driver not found!\nPlease add mysql-connector-java.jar to classpath.",
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return null;
            
        } catch (SQLException e) {
            System.err.println("Database Connection Failed!");
            JOptionPane.showMessageDialog(null, 
                "Database Connection Error:\n" + e.getMessage() + 
                "\n\nPlease check:\n1. MySQL server is running\n2. Database 'hotel_management' exists\n3. Username/password is correct",
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Tests the database connection
     * @return true if connection is successful, false otherwise
     */
    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
    
    /**
     * Closes the database connection
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection!");
            e.printStackTrace();
        }
    }
    
    /**
     * Executes a SELECT query and returns ResultSet
     * @param query SQL SELECT query
     * @return ResultSet containing the results
     */
    public static ResultSet executeQuery(String query) throws SQLException {
        Connection conn = getConnection();
        if (conn != null) {
            Statement stmt = conn.createStatement();
            return stmt.executeQuery(query);
        }
        return null;
    }
    
    /**
     * Executes UPDATE, INSERT, or DELETE query
     * @param query SQL DML query
     * @return number of affected rows
     */
    public static int executeUpdate(String query) throws SQLException {
        Connection conn = getConnection();
        if (conn != null) {
            Statement stmt = conn.createStatement();
            return stmt.executeUpdate(query);
        }
        return 0;
    }
    
    /**
     * Creates a PreparedStatement for parameterized queries
     * @param query SQL query with placeholders
     * @return PreparedStatement object
     */
    public static PreparedStatement prepareStatement(String query) throws SQLException {
        Connection conn = getConnection();
        if (conn != null) {
            return conn.prepareStatement(query);
        }
        return null;
    }
    
    /**
     * Begins a transaction
     */
    public static void beginTransaction() throws SQLException {
        Connection conn = getConnection();
        if (conn != null) {
            conn.setAutoCommit(false);
        }
    }
    
    /**
     * Commits a transaction
     */
    public static void commitTransaction() throws SQLException {
        Connection conn = getConnection();
        if (conn != null) {
            conn.commit();
            conn.setAutoCommit(true);
        }
    }
    
    /**
     * Rolls back a transaction
     */
    public static void rollbackTransaction() throws SQLException {
        Connection conn = getConnection();
        if (conn != null) {
            conn.rollback();
            conn.setAutoCommit(true);
        }
    }
}