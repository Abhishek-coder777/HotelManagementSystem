// dao/UserDAO.java
package dao;

import database.DatabaseConnection;
import models.User;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    // ==================== CREATE Operations ====================

    /**
     * Add a new user to the database
     * 
     * @param user User object to add
     * @return true if successful, false otherwise
     */
    public boolean addUser(User user) {
        String query = "INSERT INTO users (username, password, full_name, email, phone, role, status, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return false;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getFullName());
            pstmt.setString(4, user.getEmail());
            pstmt.setString(5, user.getPhone());
            pstmt.setString(6, user.getRole());
            pstmt.setString(7, user.getStatus());
            pstmt.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    user.setUserId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ==================== READ Operations ====================

    /**
     * Authenticate user with username and password
     * 
     * @param username Username
     * @param password Password
     * @return User object if authenticated, null otherwise
     */
    public User authenticate(String username, String password) {
        String query = "SELECT user_id, username, full_name, email, phone, role, status, created_at, last_login FROM users WHERE username = ? AND password = ? AND status = 'Active'";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return null;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                User user = extractUserFromResultSet(rs);
                updateLastLogin(user.getUserId());
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get user by ID
     * 
     * @param userId User ID
     * @return User object if found, null otherwise
     */
    public User getUserById(int userId) {
        String query = "SELECT user_id, username, full_name, email, phone, role, status, created_at, last_login FROM users WHERE user_id = ?";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return null;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get user by username
     * 
     * @param username Username
     * @return User object if found, null otherwise
     */
    public User getUserByUsername(String username) {
        String query = "SELECT user_id, username, full_name, email, phone, role, status, created_at, last_login FROM users WHERE username = ?";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return null;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get user by email
     * 
     * @param email Email address
     * @return User object if found, null otherwise
     */
    public User getUserByEmail(String email) {
        String query = "SELECT user_id, username, full_name, email, phone, role, status, created_at, last_login FROM users WHERE email = ?";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return null;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get all users
     * 
     * @return List of all users
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT user_id, username, full_name, email, phone, role, status, created_at, last_login FROM users ORDER BY user_id";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return users;
        }

        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                users.add(extractUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    /**
     * Get users by role
     * 
     * @param role Role (admin, manager, receptionist)
     * @return List of users with specified role
     */
    public List<User> getUsersByRole(String role) {
        List<User> users = new ArrayList<>();
        String query = "SELECT user_id, username, full_name, email, phone, role, status, created_at, last_login FROM users WHERE role = ? ORDER BY full_name";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return users;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, role);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                users.add(extractUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    /**
     * Get active users only
     * 
     * @return List of active users
     */
    public List<User> getActiveUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT user_id, username, full_name, email, phone, role, status, created_at, last_login FROM users WHERE status = 'Active' ORDER BY full_name";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return users;
        }

        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                users.add(extractUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    /**
     * Get inactive users
     * 
     * @return List of inactive users
     */
    public List<User> getInactiveUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT user_id, username, full_name, email, phone, role, status, created_at, last_login FROM users WHERE status = 'Inactive' ORDER BY full_name";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return users;
        }

        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                users.add(extractUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    /**
     * Search users by name or username
     * 
     * @param searchTerm Search term
     * @return List of matching users
     */
    public List<User> searchUsers(String searchTerm) {
        List<User> users = new ArrayList<>();
        String query = "SELECT user_id, username, full_name, email, phone, role, status, created_at, last_login FROM users WHERE full_name LIKE ? OR username LIKE ? OR email LIKE ? ORDER BY full_name";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return users;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            String searchPattern = "%" + searchTerm + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                users.add(extractUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    /**
     * Check if username exists
     * 
     * @param username Username to check
     * @return true if exists, false otherwise
     */
    public boolean isUsernameExists(String username) {
        String query = "SELECT COUNT(*) FROM users WHERE username = ?";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return false;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Check if email exists
     * 
     * @param email Email to check
     * @return true if exists, false otherwise
     */
    public boolean isEmailExists(String email) {
        String query = "SELECT COUNT(*) FROM users WHERE email = ?";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return false;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get total user count
     * 
     * @return Total number of users
     */
    public int getUserCount() {
        String query = "SELECT COUNT(*) FROM users";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return 0;
        }

        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Get user count by role
     * 
     * @param role Role
     * @return Number of users with specified role
     */
    public int getUserCountByRole(String role) {
        String query = "SELECT COUNT(*) FROM users WHERE role = ?";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return 0;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, role);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Get active user count
     * 
     * @return Number of active users
     */
    public int getActiveUserCount() {
        String query = "SELECT COUNT(*) FROM users WHERE status = 'Active'";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return 0;
        }

        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // ==================== UPDATE Operations ====================

    /**
     * Update user information
     * 
     * @param user User object with updated information
     * @return true if successful, false otherwise
     */
    public boolean updateUser(User user) {
        String query = "UPDATE users SET full_name = ?, email = ?, phone = ?, role = ?, status = ? WHERE user_id = ?";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return false;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, user.getFullName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPhone());
            pstmt.setString(4, user.getRole());
            pstmt.setString(5, user.getStatus());
            pstmt.setInt(6, user.getUserId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Update user password
     * 
     * @param userId      User ID
     * @param newPassword New password
     * @return true if successful, false otherwise
     */
    public boolean updatePassword(int userId, String newPassword) {
        String query = "UPDATE users SET password = ? WHERE user_id = ?";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return false;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, newPassword);
            pstmt.setInt(2, userId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Update user role
     * 
     * @param userId  User ID
     * @param newRole New role
     * @return true if successful, false otherwise
     */
    public boolean updateUserRole(int userId, String newRole) {
        String query = "UPDATE users SET role = ? WHERE user_id = ?";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return false;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, newRole);
            pstmt.setInt(2, userId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Update user status (Active/Inactive)
     * 
     * @param userId User ID
     * @param status New status
     * @return true if successful, false otherwise
     */
    public boolean updateUserStatus(int userId, String status) {
        String query = "UPDATE users SET status = ? WHERE user_id = ?";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return false;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, userId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Update last login timestamp
     * 
     * @param userId User ID
     */
    private void updateLastLogin(int userId) {
        String query = "UPDATE users SET last_login = ? WHERE user_id = ?";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Update user email
     * 
     * @param userId User ID
     * @param email  New email
     * @return true if successful, false otherwise
     */
    public boolean updateUserEmail(int userId, String email) {
        String query = "UPDATE users SET email = ? WHERE user_id = ?";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return false;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, email);
            pstmt.setInt(2, userId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Update user phone
     * 
     * @param userId User ID
     * @param phone  New phone
     * @return true if successful, false otherwise
     */
    public boolean updateUserPhone(int userId, String phone) {
        String query = "UPDATE users SET phone = ? WHERE user_id = ?";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return false;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, phone);
            pstmt.setInt(2, userId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ==================== DELETE Operations ====================

    /**
     * Delete user by ID (Hard delete)
     * 
     * @param userId User ID
     * @return true if successful, false otherwise
     */
    public boolean deleteUser(int userId) {
        String query = "DELETE FROM users WHERE user_id = ?";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return false;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Soft delete user (deactivate)
     * 
     * @param userId User ID
     * @return true if successful, false otherwise
     */
    public boolean deactivateUser(int userId) {
        return updateUserStatus(userId, "Inactive");
    }

    /**
     * Activate user
     * 
     * @param userId User ID
     * @return true if successful, false otherwise
     */
    public boolean activateUser(int userId) {
        return updateUserStatus(userId, "Active");
    }

    /**
     * Delete all users (use with caution - only for testing)
     * 
     * @return Number of users deleted
     */
    public int deleteAllUsers() {
        String query = "DELETE FROM users";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return 0;
        }

        try (Statement stmt = conn.createStatement()) {
            return stmt.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // ==================== Helper Methods ====================

    /**
     * Extract User object from ResultSet
     * 
     * @param rs ResultSet
     * @return User object
     * @throws SQLException
     */
    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        user.setRole(rs.getString("role"));
        user.setStatus(rs.getString("status"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            user.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp lastLogin = rs.getTimestamp("last_login");
        if (lastLogin != null) {
            user.setLastLogin(lastLogin.toLocalDateTime());
        }

        return user;
    }

    // ==================== Bulk Operations ====================

    /**
     * Add multiple users in batch
     * 
     * @param users List of users
     * @return Number of users successfully added
     */
    public int addUsersBatch(List<User> users) {
        String query = "INSERT INTO users (username, password, full_name, email, phone, role, status, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        int successCount = 0;

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return 0;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            for (User user : users) {
                pstmt.setString(1, user.getUsername());
                pstmt.setString(2, user.getPassword());
                pstmt.setString(3, user.getFullName());
                pstmt.setString(4, user.getEmail());
                pstmt.setString(5, user.getPhone());
                pstmt.setString(6, user.getRole());
                pstmt.setString(7, user.getStatus());
                pstmt.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
                pstmt.addBatch();
            }

            int[] results = pstmt.executeBatch();
            for (int result : results) {
                if (result > 0) {
                    successCount++;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return successCount;
    }

    /**
     * Update multiple user statuses in batch
     * 
     * @param userIds List of user IDs
     * @param status  New status
     * @return Number of users successfully updated
     */
    public int updateUserStatusBatch(List<Integer> userIds, String status) {
        String query = "UPDATE users SET status = ? WHERE user_id = ?";
        int successCount = 0;

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return 0;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            for (int userId : userIds) {
                pstmt.setString(1, status);
                pstmt.setInt(2, userId);
                pstmt.addBatch();
            }

            int[] results = pstmt.executeBatch();
            for (int result : results) {
                if (result > 0) {
                    successCount++;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return successCount;
    }

    // ==================== Validation Methods ====================

    /**
     * Validate user credentials
     * 
     * @param username Username
     * @param password Password
     * @return true if valid, false otherwise
     */
    public boolean validateCredentials(String username, String password) {
        User user = authenticate(username, password);
        return user != null;
    }

    /**
     * Change password with old password verification
     * 
     * @param userId      User ID
     * @param oldPassword Old password
     * @param newPassword New password
     * @return true if successful, false otherwise
     */
    public boolean changePassword(int userId, String oldPassword, String newPassword) {
        // First verify old password
        String verifyQuery = "SELECT COUNT(*) FROM users WHERE user_id = ? AND password = ?";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return false;
        }

        try (PreparedStatement verifyStmt = conn.prepareStatement(verifyQuery)) {
            verifyStmt.setInt(1, userId);
            verifyStmt.setString(2, oldPassword);
            ResultSet rs = verifyStmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                // Update to new password
                return updatePassword(userId, newPassword);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Validate user data completeness
     * 
     * @param user User to validate
     * @return true if valid, false otherwise
     */
    public boolean isValidUser(User user) {
        if (user == null) {
            return false;
        }
        
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            return false;
        }
        
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            return false;
        }
        
        if (user.getFullName() == null || user.getFullName().trim().isEmpty()) {
            return false;
        }
        
        return true;
    }

    /**
     * Validate email format
     * 
     * @param email Email to validate
     * @return true if valid email format, false otherwise
     */
    public boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }

    /**
     * Validate phone format
     * 
     * @param phone Phone to validate
     * @return true if valid phone format, false otherwise
     */
    public boolean isValidPhone(String phone) {
        if (phone == null || phone.isEmpty()) {
            return false;
        }
        String phoneRegex = "^[0-9]{10,15}$";
        return phone.replaceAll("[^0-9]", "").matches(phoneRegex);
    }

    // ==================== Statistics Methods ====================

    /**
     * Get user statistics
     * 
     * @return String array with statistics [total, admins, managers, receptionists, active]
     */
    public String[] getUserStatistics() {
        String[] stats = new String[5];

        String query = "SELECT " +
                "COUNT(*) as total, " +
                "SUM(CASE WHEN role = 'admin' THEN 1 ELSE 0 END) as admins, " +
                "SUM(CASE WHEN role = 'manager' THEN 1 ELSE 0 END) as managers, " +
                "SUM(CASE WHEN role = 'receptionist' THEN 1 ELSE 0 END) as receptionists, " +
                "SUM(CASE WHEN status = 'Active' THEN 1 ELSE 0 END) as active " +
                "FROM users";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return stats;
        }

        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                stats[0] = String.valueOf(rs.getInt("total"));
                stats[1] = String.valueOf(rs.getInt("admins"));
                stats[2] = String.valueOf(rs.getInt("managers"));
                stats[3] = String.valueOf(rs.getInt("receptionists"));
                stats[4] = String.valueOf(rs.getInt("active"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return stats;
    }

    /**
     * Get user statistics as int array
     * 
     * @return int array with statistics [total, admins, managers, receptionists, active]
     */
    public int[] getUserStatisticsInt() {
        int[] stats = new int[5];

        String query = "SELECT " +
                "COUNT(*) as total, " +
                "SUM(CASE WHEN role = 'admin' THEN 1 ELSE 0 END) as admins, " +
                "SUM(CASE WHEN role = 'manager' THEN 1 ELSE 0 END) as managers, " +
                "SUM(CASE WHEN role = 'receptionist' THEN 1 ELSE 0 END) as receptionists, " +
                "SUM(CASE WHEN status = 'Active' THEN 1 ELSE 0 END) as active " +
                "FROM users";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return stats;
        }

        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                stats[0] = rs.getInt("total");
                stats[1] = rs.getInt("admins");
                stats[2] = rs.getInt("managers");
                stats[3] = rs.getInt("receptionists");
                stats[4] = rs.getInt("active");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return stats;
    }

    // ==================== Display Methods ====================

    /**
     * Get user display name with role
     * 
     * @param user User object
     * @return Formatted display string
     */
    public String getUserDisplayString(User user) {
        if (user == null) {
            return "";
        }
        return user.getFullName() + " (" + user.getRole() + ")";
    }

    /**
     * Get user short info (for combo boxes)
     * 
     * @param user User object
     * @return Short display string
     */
    public String getShortUserInfo(User user) {
        if (user == null) {
            return "";
        }
        return user.getUsername() + " - " + user.getFullName();
    }
}