// dao/GuestDAO.java
package dao;

import database.DatabaseConnection;
import models.Guest;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GuestDAO {

    // ==================== CREATE Operations ====================

    /**
     * Add a new guest to the database
     * 
     * @param guest Guest object to add
     * @return true if successful, false otherwise
     */
    public boolean addGuest(Guest guest) {
        String query = "INSERT INTO guests (first_name, last_name, email, phone, address, " +
                "id_proof_number, nationality, is_active) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query,
                Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, guest.getFirstName());
            pstmt.setString(2, guest.getLastName());
            pstmt.setString(3, guest.getEmail());
            pstmt.setString(4, guest.getPhone());
            pstmt.setString(5, guest.getAddress());
            pstmt.setString(6, guest.getIdProof());
            pstmt.setString(7, guest.getNationality());
            pstmt.setBoolean(8, true);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    guest.setGuestId(generatedKeys.getInt(1));
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
     * Get guest by ID
     * 
     * @param guestId Guest ID
     * @return Guest object if found, null otherwise
     */
    public Guest getGuestById(int guestId) {
        String query = "SELECT * FROM guests WHERE guest_id = ?";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, guestId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractGuestFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get guest by email
     * 
     * @param email Email address
     * @return Guest object if found, null otherwise
     */
    public Guest getGuestByEmail(String email) {
        String query = "SELECT * FROM guests WHERE email = ?";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractGuestFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get guest by phone number
     * 
     * @param phone Phone number
     * @return Guest object if found, null otherwise
     */
    public Guest getGuestByPhone(String phone) {
        String query = "SELECT * FROM guests WHERE phone = ?";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setString(1, phone);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractGuestFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get guest by ID proof number
     * 
     * @param idProofNumber ID proof number
     * @return Guest object if found, null otherwise
     */
    public Guest getGuestByIdProof(String idProofNumber) {
        String query = "SELECT * FROM guests WHERE id_proof_number = ?";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setString(1, idProofNumber);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractGuestFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get all guests
     * 
     * @return List of all guests
     */
    public List<Guest> getAllGuests() {
        List<Guest> guests = new ArrayList<>();
        String query = "SELECT * FROM guests ORDER BY last_name, first_name";

        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                guests.add(extractGuestFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return guests;
    }

    /**
     * Get active guests only
     * 
     * @return List of active guests
     */
    public List<Guest> getActiveGuests() {
        List<Guest> guests = new ArrayList<>();
        String query = "SELECT * FROM guests WHERE is_active = true ORDER BY last_name, first_name";

        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                guests.add(extractGuestFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return guests;
    }

    /**
     * Get VIP guests
     * 
     * @return List of VIP guests
     */
    public List<Guest> getVIPGuests() {
        List<Guest> guests = new ArrayList<>();
        String query = "SELECT * FROM guests WHERE is_vip = true AND is_active = true ORDER BY total_spent DESC";

        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                guests.add(extractGuestFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return guests;
    }

    /**
     * Get guests by loyalty tier
     * 
     * @param loyaltyTier Loyalty tier (Bronze, Silver, Gold, Platinum)
     * @return List of guests in specified tier
     */
    public List<Guest> getGuestsByLoyaltyTier(String loyaltyTier) {
        List<Guest> guests = new ArrayList<>();
        String query = "SELECT * FROM guests WHERE loyalty_tier = ? AND is_active = true ORDER BY total_spent DESC";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setString(1, loyaltyTier);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                guests.add(extractGuestFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return guests;
    }

    /**
     * Get guests by nationality
     * 
     * @param nationality Nationality
     * @return List of guests from specified nationality
     */
    public List<Guest> getGuestsByNationality(String nationality) {
        List<Guest> guests = new ArrayList<>();
        String query = "SELECT * FROM guests WHERE nationality = ? ORDER BY last_name";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setString(1, nationality);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                guests.add(extractGuestFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return guests;
    }

    /**
     * Get guests by date of birth range
     * 
     * @param startDate Start date
     * @param endDate   End date
     * @return List of guests with birthdays in range
     */
    public List<Guest> getGuestsByBirthDateRange(LocalDate startDate, LocalDate endDate) {
        List<Guest> guests = new ArrayList<>();
        String query = "SELECT * FROM guests WHERE date_of_birth BETWEEN ? AND ? ORDER BY date_of_birth";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setDate(1, Date.valueOf(startDate));
            pstmt.setDate(2, Date.valueOf(endDate));
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                guests.add(extractGuestFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return guests;
    }

    /**
     * Search guests by various criteria
     * 
     * @param searchTerm Search term (name, email, phone)
     * @return List of matching guests
     */
    public List<Guest> searchGuests(String searchTerm) {
        List<Guest> guests = new ArrayList<>();
        String query = "SELECT * FROM guests WHERE first_name LIKE ? OR last_name LIKE ? " +
                "OR email LIKE ? OR phone LIKE ? OR id_proof_number LIKE ? " +
                "ORDER BY last_name, first_name";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            String searchPattern = "%" + searchTerm + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            pstmt.setString(4, searchPattern);
            pstmt.setString(5, searchPattern);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                guests.add(extractGuestFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return guests;
    }

    /**
     * Get guests with pending balance (from bookings)
     * 
     * @return List of guests with due payments
     */
    public List<Guest> getGuestsWithPendingBalance() {
        List<Guest> guests = new ArrayList<>();
        String query = "SELECT DISTINCT g.* FROM guests g " +
                "INNER JOIN bookings b ON g.guest_id = b.guest_id " +
                "WHERE b.due_amount > 0 AND b.status != 'Cancelled' " +
                "ORDER BY g.last_name";

        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                guests.add(extractGuestFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return guests;
    }

    /**
     * Get frequent guests (based on total stays)
     * 
     * @param minStays Minimum number of stays
     * @return List of frequent guests
     */
    public List<Guest> getFrequentGuests(int minStays) {
        List<Guest> guests = new ArrayList<>();
        String query = "SELECT * FROM guests WHERE total_stays >= ? AND is_active = true " +
                "ORDER BY total_stays DESC, total_spent DESC";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, minStays);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                guests.add(extractGuestFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return guests;
    }

    /**
     * Get guests who haven't visited in specified months
     * 
     * @param months Number of months
     * @return List of inactive guests
     */
    public List<Guest> getInactiveGuests(int months) {
        List<Guest> guests = new ArrayList<>();
        String query = "SELECT * FROM guests WHERE last_visit < DATE_SUB(NOW(), INTERVAL ? MONTH) " +
                "AND is_active = true ORDER BY last_visit";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, months);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                guests.add(extractGuestFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return guests;
    }

    /**
     * Get guest statistics
     * 
     * @return Array with guest statistics
     */
    public int[] getGuestStatistics() {
        int[] stats = new int[6]; // total, active, vip, male, female, other
        String query = "SELECT " +
                "COUNT(*) as total, " +
                "SUM(CASE WHEN is_active = true THEN 1 ELSE 0 END) as active, " +
                "SUM(CASE WHEN is_vip = true THEN 1 ELSE 0 END) as vip, " +
                "SUM(CASE WHEN gender = 'Male' THEN 1 ELSE 0 END) as male, " +
                "SUM(CASE WHEN gender = 'Female' THEN 1 ELSE 0 END) as female, " +
                "SUM(CASE WHEN gender = 'Other' THEN 1 ELSE 0 END) as other " +
                "FROM guests";

        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                stats[0] = rs.getInt("total");
                stats[1] = rs.getInt("active");
                stats[2] = rs.getInt("vip");
                stats[3] = rs.getInt("male");
                stats[4] = rs.getInt("female");
                stats[5] = rs.getInt("other");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }

    /**
     * Get loyalty tier distribution
     * 
     * @return Array with counts for each loyalty tier
     */
    public int[] getLoyaltyTierDistribution() {
        int[] distribution = new int[4]; // Bronze, Silver, Gold, Platinum
        String query = "SELECT " +
                "SUM(CASE WHEN loyalty_tier = 'Bronze' THEN 1 ELSE 0 END) as bronze, " +
                "SUM(CASE WHEN loyalty_tier = 'Silver' THEN 1 ELSE 0 END) as silver, " +
                "SUM(CASE WHEN loyalty_tier = 'Gold' THEN 1 ELSE 0 END) as gold, " +
                "SUM(CASE WHEN loyalty_tier = 'Platinum' THEN 1 ELSE 0 END) as platinum " +
                "FROM guests WHERE is_active = true";

        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                distribution[0] = rs.getInt("bronze");
                distribution[1] = rs.getInt("silver");
                distribution[2] = rs.getInt("gold");
                distribution[3] = rs.getInt("platinum");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return distribution;
    }

    /**
     * Get top spending guests
     * 
     * @param limit Maximum number of guests to return
     * @return List of top spending guests
     */
    public List<Guest> getTopSpendingGuests(int limit) {
        List<Guest> guests = new ArrayList<>();
        String query = "SELECT * FROM guests WHERE is_active = true ORDER BY total_spent DESC LIMIT ?";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                guests.add(extractGuestFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return guests;
    }

    /**
     * Get guest count by nationality
     * 
     * @return Map of nationalities and counts (simplified as 2D array)
     */
    public String[][] getGuestNationalityStats() {
        List<String[]> stats = new ArrayList<>();
        String query = "SELECT nationality, COUNT(*) as count FROM guests " +
                "WHERE nationality IS NOT NULL GROUP BY nationality ORDER BY count DESC LIMIT 10";

        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                stats.add(new String[] { rs.getString("nationality"), String.valueOf(rs.getInt("count")) });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats.toArray(new String[0][0]);
    }

    /**
     * Check if email exists
     * 
     * @param email Email to check
     * @return true if exists, false otherwise
     */
    public boolean isEmailExists(String email) {
        String query = "SELECT COUNT(*) FROM guests WHERE email = ?";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
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
     * Check if phone exists
     * 
     * @param phone Phone to check
     * @return true if exists, false otherwise
     */
    public boolean isPhoneExists(String phone) {
        String query = "SELECT COUNT(*) FROM guests WHERE phone = ?";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setString(1, phone);
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
     * Check if ID proof number exists
     * 
     * @param idProofNumber ID proof number to check
     * @return true if exists, false otherwise
     */
    public boolean isIdProofExists(String idProofNumber) {
        String query = "SELECT COUNT(*) FROM guests WHERE id_proof_number = ?";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setString(1, idProofNumber);
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
     * Get total guest count
     * 
     * @return Total number of guests
     */
    public int getGuestCount() {
        String query = "SELECT COUNT(*) FROM guests";

        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
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
     * Update guest information
     * 
     * @param guest Guest object with updated information
     * @return true if successful, false otherwise
     */
    public boolean updateGuest(Guest guest) {
        String query = "UPDATE guests SET first_name = ?, last_name = ?, email = ?, phone = ?, " +
                "address = ?, id_proof_number = ?, nationality = ? WHERE guest_id = ?";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setString(1, guest.getFirstName());
            pstmt.setString(2, guest.getLastName());
            pstmt.setString(3, guest.getEmail());
            pstmt.setString(4, guest.getPhone());
            pstmt.setString(5, guest.getAddress());
            pstmt.setString(6, guest.getIdProof());
            pstmt.setString(7, guest.getNationality());
            pstmt.setInt(8, guest.getGuestId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Update guest loyalty tier
     * 
     * @param guestId     Guest ID
     * @param loyaltyTier New loyalty tier
     * @return true if successful, false otherwise
     */
    public boolean updateLoyaltyTier(int guestId, String loyaltyTier) {
        String query = "UPDATE guests SET loyalty_tier = ? WHERE guest_id = ?";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setString(1, loyaltyTier);
            pstmt.setInt(2, guestId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Update guest VIP status
     * 
     * @param guestId Guest ID
     * @param isVIP   VIP status
     * @return true if successful, false otherwise
     */
    public boolean updateVIPStatus(int guestId, boolean isVIP) {
        String query = "UPDATE guests SET is_vip = ? WHERE guest_id = ?";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setBoolean(1, isVIP);
            pstmt.setInt(2, guestId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Update guest status (active/inactive)
     * 
     * @param guestId  Guest ID
     * @param isActive Active status
     * @return true if successful, false otherwise
     */
    public boolean updateGuestStatus(int guestId, boolean isActive) {
        String query = "UPDATE guests SET is_active = ? WHERE guest_id = ?";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setBoolean(1, isActive);
            pstmt.setInt(2, guestId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Update guest stays and spending
     * 
     * @param guestId Guest ID
     * @param amount  Amount to add to total spent
     * @return true if successful, false otherwise
     */
    public boolean updateGuestStaysAndSpending(int guestId, double amount) {
        String query = "UPDATE guests SET total_stays = total_stays + 1, " +
                "total_spent = total_spent + ?, last_visit = ? WHERE guest_id = ?";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setDouble(1, amount);
            pstmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setInt(3, guestId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Update guest last visit date
     * 
     * @param guestId Guest ID
     * @return true if successful, false otherwise
     */
    public boolean updateLastVisit(int guestId) {
        String query = "UPDATE guests SET last_visit = ? WHERE guest_id = ?";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setInt(2, guestId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Update guest profile image
     * 
     * @param guestId   Guest ID
     * @param imagePath Image file path
     * @return true if successful, false otherwise
     */
    public boolean updateProfileImage(int guestId, String imagePath) {
        String query = "UPDATE guests SET profile_image_path = ? WHERE guest_id = ?";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setString(1, imagePath);
            pstmt.setInt(2, guestId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ==================== DELETE Operations ====================

    /**
     * Delete guest by ID (Hard delete)
     * 
     * @param guestId Guest ID
     * @return true if successful, false otherwise
     */
    public boolean deleteGuest(int guestId) {
        // Check if guest has any bookings
        if (hasBookings(guestId)) {
            return false; // Cannot delete guest with existing bookings
        }

        String query = "DELETE FROM guests WHERE guest_id = ?";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, guestId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Check if guest has bookings
     * 
     * @param guestId Guest ID
     * @return true if has bookings, false otherwise
     */
    public boolean hasBookings(int guestId) {
        String query = "SELECT COUNT(*) FROM bookings WHERE guest_id = ?";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, guestId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ==================== Bulk Operations ====================

    /**
     * Add multiple guests in batch
     * 
     * @param guests List of guests
     * @return Number of guests successfully added
     */
    public int addGuestsBatch(List<Guest> guests) {
        String query = "INSERT INTO guests (first_name, last_name, email, phone, address, city, state, " +
                "zip_code, country, id_proof_type, id_proof_number, nationality, gender, date_of_birth, " +
                "occupation, company_name, emergency_contact_name, emergency_contact_phone, " +
                "emergency_contact_relation, preferences, is_vip, is_active) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        int successCount = 0;

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            DatabaseConnection.beginTransaction();

            for (Guest guest : guests) {
                pstmt.setString(1, guest.getFirstName());
                pstmt.setString(2, guest.getLastName());
                pstmt.setString(3, guest.getEmail());
                pstmt.setString(4, guest.getPhone());
                pstmt.setString(5, guest.getAddress());
                pstmt.setString(12, guest.getNationality());
                pstmt.addBatch();
            }

            int[] results = pstmt.executeBatch();
            for (int result : results) {
                if (result > 0)
                    successCount++;
            }

            DatabaseConnection.commitTransaction();
        } catch (SQLException e) {
            try {
                DatabaseConnection.rollbackTransaction();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }

        return successCount;
    }

    // ==================== Helper Methods ====================

    /**
     * Extract Guest object from ResultSet
     * 
     * @param rs ResultSet
     * @return Guest object
     * @throws SQLException
     */
    private Guest extractGuestFromResultSet(ResultSet rs) throws SQLException {
        Guest guest = new Guest();
        guest.setGuestId(rs.getInt("guest_id"));
        guest.setFirstName(rs.getString("first_name"));
        guest.setLastName(rs.getString("last_name"));
        guest.setEmail(rs.getString("email"));
        guest.setPhone(rs.getString("phone"));
        guest.setAddress(rs.getString("address"));
        guest.setIdProof(rs.getString("id_proof_number"));
        guest.setNationality(rs.getString("nationality"));

        return guest;
    }

    // ==================== Report Methods ====================

    /**
     * Get guest registration trend (last 12 months)
     * 
     * @return Array of monthly registration counts
     */
    public int[] getGuestRegistrationTrend() {
        int[] trend = new int[12];
        String query = "SELECT MONTH(created_at) as month, COUNT(*) as count " +
                "FROM guests WHERE created_at >= DATE_SUB(NOW(), INTERVAL 12 MONTH) " +
                "GROUP BY MONTH(created_at) ORDER BY month";

        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int month = rs.getInt("month") - 1; // 0-based index
                if (month >= 0 && month < 12) {
                    trend[month] = rs.getInt("count");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return trend;
    }

    /**
     * Get guest lifetime value distribution
     * 
     * @return Array with guest counts by spending range
     */
    public int[] getGuestLifetimeValueDistribution() {
        int[] distribution = new int[5]; // 0-5k, 5k-10k, 10k-25k, 25k-50k, 50k+
        String query = "SELECT " +
                "SUM(CASE WHEN total_spent BETWEEN 0 AND 5000 THEN 1 ELSE 0 END) as range1, " +
                "SUM(CASE WHEN total_spent BETWEEN 5001 AND 10000 THEN 1 ELSE 0 END) as range2, " +
                "SUM(CASE WHEN total_spent BETWEEN 10001 AND 25000 THEN 1 ELSE 0 END) as range3, " +
                "SUM(CASE WHEN total_spent BETWEEN 25001 AND 50000 THEN 1 ELSE 0 END) as range4, " +
                "SUM(CASE WHEN total_spent > 50000 THEN 1 ELSE 0 END) as range5 " +
                "FROM guests WHERE is_active = true";

        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                distribution[0] = rs.getInt("range1");
                distribution[1] = rs.getInt("range2");
                distribution[2] = rs.getInt("range3");
                distribution[3] = rs.getInt("range4");
                distribution[4] = rs.getInt("range5");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return distribution;
    }
}