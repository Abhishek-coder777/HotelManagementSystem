// dao/RoomDAO.java
package dao;

import database.DatabaseConnection;
import models.Room;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {

    // ==================== CREATE Operations ====================

    /**
     * Add a new room to the database
     * 
     * @param room Room object to add
     * @return true if successful, false otherwise
     */
    public boolean addRoom(Room room) {
        String query = "INSERT INTO rooms (room_number, room_type, price_per_night, status, floor, " +
                "description, amenities, capacity, area, bed_type, has_wifi, has_tv, has_ac, " +
                "has_attached_bathroom, room_view, image_path) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return false;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, room.getRoomNumber());
            pstmt.setString(2, room.getRoomType());
            pstmt.setDouble(3, room.getPricePerNight());
            pstmt.setString(4, room.getStatus());
            pstmt.setInt(5, room.getFloor());
            pstmt.setString(6, room.getDescription());
            pstmt.setString(7, room.getAmenities());
            pstmt.setInt(8, room.getCapacity());
            pstmt.setDouble(9, room.getArea());
            pstmt.setString(10, room.getBedType());
            pstmt.setBoolean(11, room.isHasWifi());
            pstmt.setBoolean(12, room.isHasTV());
            pstmt.setBoolean(13, room.isHasAC());
            pstmt.setBoolean(14, room.isHasAttachedBathroom());
            pstmt.setString(15, room.getView());
            pstmt.setString(16, room.getImagePath());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    room.setRoomId(generatedKeys.getInt(1));
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
     * Get room by ID
     * 
     * @param roomId Room ID
     * @return Room object if found, null otherwise
     */
    public Room getRoomById(int roomId) {
        String query = "SELECT room_id, room_number, room_type, price_per_night, status, floor, " +
                "description, amenities, capacity, area, bed_type, has_wifi, has_tv, has_ac, " +
                "has_attached_bathroom, room_view, image_path, rating, total_bookings " +
                "FROM rooms WHERE room_id = ?";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return null;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, roomId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractRoomFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get room by room number
     * 
     * @param roomNumber Room number
     * @return Room object if found, null otherwise
     */
    public Room getRoomByNumber(String roomNumber) {
        String query = "SELECT room_id, room_number, room_type, price_per_night, status, floor, " +
                "description, amenities, capacity, area, bed_type, has_wifi, has_tv, has_ac, " +
                "has_attached_bathroom, room_view, image_path, rating, total_bookings " +
                "FROM rooms WHERE room_number = ?";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return null;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, roomNumber);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractRoomFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get all rooms
     * 
     * @return List of all rooms
     */
    public List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<>();
        String query = "SELECT room_id, room_number, room_type, price_per_night, status, floor, " +
                "description, amenities, capacity, area, bed_type, has_wifi, has_tv, has_ac, " +
                "has_attached_bathroom, room_view, image_path, rating, total_bookings " +
                "FROM rooms ORDER BY floor, room_number";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return rooms;
        }

        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                rooms.add(extractRoomFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }

    /**
     * Get available rooms
     * 
     * @return List of available rooms
     */
    public List<Room> getAvailableRooms() {
        List<Room> rooms = new ArrayList<>();
        String query = "SELECT room_id, room_number, room_type, price_per_night, status, floor, " +
                "description, amenities, capacity, area, bed_type, has_wifi, has_tv, has_ac, " +
                "has_attached_bathroom, room_view, image_path, rating, total_bookings " +
                "FROM rooms WHERE status = 'Available' ORDER BY floor, room_number";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return rooms;
        }

        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                rooms.add(extractRoomFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }

    /**
     * Get rooms by type
     * 
     * @param roomType Room type (Standard, Deluxe, Suite, Presidential)
     * @return List of rooms of specified type
     */
    public List<Room> getRoomsByType(String roomType) {
        List<Room> rooms = new ArrayList<>();
        String query = "SELECT room_id, room_number, room_type, price_per_night, status, floor, " +
                "description, amenities, capacity, area, bed_type, has_wifi, has_tv, has_ac, " +
                "has_attached_bathroom, room_view, image_path, rating, total_bookings " +
                "FROM rooms WHERE room_type = ? ORDER BY room_number";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return rooms;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, roomType);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                rooms.add(extractRoomFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }

    /**
     * Get rooms by status
     * 
     * @param status Room status
     * @return List of rooms with specified status
     */
    public List<Room> getRoomsByStatus(String status) {
        List<Room> rooms = new ArrayList<>();
        String query = "SELECT room_id, room_number, room_type, price_per_night, status, floor, " +
                "description, amenities, capacity, area, bed_type, has_wifi, has_tv, has_ac, " +
                "has_attached_bathroom, room_view, image_path, rating, total_bookings " +
                "FROM rooms WHERE status = ? ORDER BY floor, room_number";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return rooms;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                rooms.add(extractRoomFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }

    /**
     * Get rooms by floor
     * 
     * @param floor Floor number
     * @return List of rooms on specified floor
     */
    public List<Room> getRoomsByFloor(int floor) {
        List<Room> rooms = new ArrayList<>();
        String query = "SELECT room_id, room_number, room_type, price_per_night, status, floor, " +
                "description, amenities, capacity, area, bed_type, has_wifi, has_tv, has_ac, " +
                "has_attached_bathroom, room_view, image_path, rating, total_bookings " +
                "FROM rooms WHERE floor = ? ORDER BY room_number";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return rooms;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, floor);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                rooms.add(extractRoomFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }

    /**
     * Get rooms by price range
     * 
     * @param minPrice Minimum price
     * @param maxPrice Maximum price
     * @return List of rooms within price range
     */
    public List<Room> getRoomsByPriceRange(double minPrice, double maxPrice) {
        List<Room> rooms = new ArrayList<>();
        String query = "SELECT room_id, room_number, room_type, price_per_night, status, floor, " +
                "description, amenities, capacity, area, bed_type, has_wifi, has_tv, has_ac, " +
                "has_attached_bathroom, room_view, image_path, rating, total_bookings " +
                "FROM rooms WHERE price_per_night BETWEEN ? AND ? ORDER BY price_per_night";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return rooms;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setDouble(1, minPrice);
            pstmt.setDouble(2, maxPrice);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                rooms.add(extractRoomFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }

    /**
     * Get rooms by capacity
     * 
     * @param capacity Minimum capacity
     * @return List of rooms with capacity >= specified value
     */
    public List<Room> getRoomsByCapacity(int capacity) {
        List<Room> rooms = new ArrayList<>();
        String query = "SELECT room_id, room_number, room_type, price_per_night, status, floor, " +
                "description, amenities, capacity, area, bed_type, has_wifi, has_tv, has_ac, " +
                "has_attached_bathroom, room_view, image_path, rating, total_bookings " +
                "FROM rooms WHERE capacity >= ? ORDER BY capacity, price_per_night";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return rooms;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, capacity);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                rooms.add(extractRoomFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }

    /**
     * Search rooms by various criteria
     * 
     * @param searchTerm Search term
     * @return List of matching rooms
     */
    public List<Room> searchRooms(String searchTerm) {
        List<Room> rooms = new ArrayList<>();
        String query = "SELECT room_id, room_number, room_type, price_per_night, status, floor, " +
                "description, amenities, capacity, area, bed_type, has_wifi, has_tv, has_ac, " +
                "has_attached_bathroom, room_view, image_path, rating, total_bookings " +
                "FROM rooms WHERE room_number LIKE ? OR room_type LIKE ? " +
                "OR description LIKE ? OR bed_type LIKE ? ORDER BY floor, room_number";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return rooms;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            String searchPattern = "%" + searchTerm + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            pstmt.setString(4, searchPattern);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                rooms.add(extractRoomFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }

    /**
     * Get rooms with specific amenity
     * 
     * @param amenity Amenity name
     * @return List of rooms with the amenity
     */
    public List<Room> getRoomsWithAmenity(String amenity) {
        List<Room> rooms = new ArrayList<>();

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return rooms;
        }

        String query = "";
        switch (amenity.toLowerCase()) {
            case "wifi":
                query = "SELECT room_id, room_number, room_type, price_per_night, status, floor, " +
                        "description, amenities, capacity, area, bed_type, has_wifi, has_tv, has_ac, " +
                        "has_attached_bathroom, room_view, image_path, rating, total_bookings " +
                        "FROM rooms WHERE has_wifi = true ORDER BY room_number";
                break;
            case "tv":
                query = "SELECT room_id, room_number, room_type, price_per_night, status, floor, " +
                        "description, amenities, capacity, area, bed_type, has_wifi, has_tv, has_ac, " +
                        "has_attached_bathroom, room_view, image_path, rating, total_bookings " +
                        "FROM rooms WHERE has_tv = true ORDER BY room_number";
                break;
            case "ac":
                query = "SELECT room_id, room_number, room_type, price_per_night, status, floor, " +
                        "description, amenities, capacity, area, bed_type, has_wifi, has_tv, has_ac, " +
                        "has_attached_bathroom, room_view, image_path, rating, total_bookings " +
                        "FROM rooms WHERE has_ac = true ORDER BY room_number";
                break;
            case "attached bathroom":
                query = "SELECT room_id, room_number, room_type, price_per_night, status, floor, " +
                        "description, amenities, capacity, area, bed_type, has_wifi, has_tv, has_ac, " +
                        "has_attached_bathroom, room_view, image_path, rating, total_bookings " +
                        "FROM rooms WHERE has_attached_bathroom = true ORDER BY room_number";
                break;
            default:
                query = "SELECT room_id, room_number, room_type, price_per_night, status, floor, " +
                        "description, amenities, capacity, area, bed_type, has_wifi, has_tv, has_ac, " +
                        "has_attached_bathroom, room_view, image_path, rating, total_bookings " +
                        "FROM rooms WHERE amenities LIKE ? ORDER BY room_number";
                break;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            if (!query.contains("?")) {
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    rooms.add(extractRoomFromResultSet(rs));
                }
            } else {
                pstmt.setString(1, "%" + amenity + "%");
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    rooms.add(extractRoomFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }

    /**
     * Get room statistics
     * 
     * @return Array with room statistics
     */
    public int[] getRoomStatistics() {
        int[] stats = new int[6]; // total, available, occupied, maintenance, reserved, cleaning
        String query = "SELECT " +
                "COUNT(*) as total, " +
                "SUM(CASE WHEN status = 'Available' THEN 1 ELSE 0 END) as available, " +
                "SUM(CASE WHEN status = 'Occupied' THEN 1 ELSE 0 END) as occupied, " +
                "SUM(CASE WHEN status = 'Maintenance' THEN 1 ELSE 0 END) as maintenance, " +
                "SUM(CASE WHEN status = 'Reserved' THEN 1 ELSE 0 END) as reserved, " +
                "SUM(CASE WHEN status = 'Cleaning' THEN 1 ELSE 0 END) as cleaning " +
                "FROM rooms";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return stats;
        }

        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                stats[0] = rs.getInt("total");
                stats[1] = rs.getInt("available");
                stats[2] = rs.getInt("occupied");
                stats[3] = rs.getInt("maintenance");
                stats[4] = rs.getInt("reserved");
                stats[5] = rs.getInt("cleaning");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }

    /**
     * Get room type distribution
     * 
     * @return Array with counts for each room type
     */
    public int[] getRoomTypeDistribution() {
        int[] distribution = new int[4]; // Standard, Deluxe, Suite, Presidential
        String query = "SELECT " +
                "SUM(CASE WHEN room_type = 'Standard' THEN 1 ELSE 0 END) as standard, " +
                "SUM(CASE WHEN room_type = 'Deluxe' THEN 1 ELSE 0 END) as deluxe, " +
                "SUM(CASE WHEN room_type = 'Suite' THEN 1 ELSE 0 END) as suite, " +
                "SUM(CASE WHEN room_type = 'Presidential' THEN 1 ELSE 0 END) as presidential " +
                "FROM rooms";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return distribution;
        }

        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                distribution[0] = rs.getInt("standard");
                distribution[1] = rs.getInt("deluxe");
                distribution[2] = rs.getInt("suite");
                distribution[3] = rs.getInt("presidential");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return distribution;
    }

    /**
     * Check if room number exists
     * 
     * @param roomNumber Room number
     * @return true if exists, false otherwise
     */
    public boolean isRoomNumberExists(String roomNumber) {
        String query = "SELECT COUNT(*) FROM rooms WHERE room_number = ?";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return false;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, roomNumber);
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
     * Get total room count
     * 
     * @return Total number of rooms
     */
    public int getRoomCount() {
        String query = "SELECT COUNT(*) FROM rooms";

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
     * Update room information
     * 
     * @param room Room object with updated information
     * @return true if successful, false otherwise
     */
    public boolean updateRoom(Room room) {
        String query = "UPDATE rooms SET room_number = ?, room_type = ?, price_per_night = ?, " +
                "status = ?, floor = ?, description = ?, amenities = ?, capacity = ?, " +
                "area = ?, bed_type = ?, has_wifi = ?, has_tv = ?, has_ac = ?, " +
                "has_attached_bathroom = ?, room_view = ?, image_path = ? WHERE room_id = ?";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return false;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, room.getRoomNumber());
            pstmt.setString(2, room.getRoomType());
            pstmt.setDouble(3, room.getPricePerNight());
            pstmt.setString(4, room.getStatus());
            pstmt.setInt(5, room.getFloor());
            pstmt.setString(6, room.getDescription());
            pstmt.setString(7, room.getAmenities());
            pstmt.setInt(8, room.getCapacity());
            pstmt.setDouble(9, room.getArea());
            pstmt.setString(10, room.getBedType());
            pstmt.setBoolean(11, room.isHasWifi());
            pstmt.setBoolean(12, room.isHasTV());
            pstmt.setBoolean(13, room.isHasAC());
            pstmt.setBoolean(14, room.isHasAttachedBathroom());
            pstmt.setString(15, room.getView());
            pstmt.setString(16, room.getImagePath());
            pstmt.setInt(17, room.getRoomId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Update room status
     * 
     * @param roomId Room ID
     * @param status New status
     * @return true if successful, false otherwise
     */
    public boolean updateRoomStatus(int roomId, String status) {
        String query = "UPDATE rooms SET status = ? WHERE room_id = ?";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return false;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, roomId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Update room price
     * 
     * @param roomId   Room ID
     * @param newPrice New price per night
     * @return true if successful, false otherwise
     */
    public boolean updateRoomPrice(int roomId, double newPrice) {
        String query = "UPDATE rooms SET price_per_night = ? WHERE room_id = ?";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return false;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setDouble(1, newPrice);
            pstmt.setInt(2, roomId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Update room rating
     * 
     * @param roomId Room ID
     * @param rating New rating
     * @return true if successful, false otherwise
     */
    public boolean updateRoomRating(int roomId, double rating) {
        String query = "UPDATE rooms SET rating = ? WHERE room_id = ?";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return false;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setDouble(1, rating);
            pstmt.setInt(2, roomId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Increment total bookings count for a room
     * 
     * @param roomId Room ID
     * @return true if successful, false otherwise
     */
    public boolean incrementBookingCount(int roomId) {
        String query = "UPDATE rooms SET total_bookings = total_bookings + 1 WHERE room_id = ?";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return false;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, roomId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ==================== DELETE Operations ====================

    /**
     * Delete room by ID
     * 
     * @param roomId Room ID
     * @return true if successful, false otherwise
     */
    public boolean deleteRoom(int roomId) {
        String query = "DELETE FROM rooms WHERE room_id = ?";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return false;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, roomId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Delete room by room number
     * 
     * @param roomNumber Room number
     * @return true if successful, false otherwise
     */
    public boolean deleteRoomByNumber(String roomNumber) {
        String query = "DELETE FROM rooms WHERE room_number = ?";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return false;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, roomNumber);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ==================== Bulk Operations ====================

    /**
     * Add multiple rooms in batch
     * 
     * @param rooms List of rooms
     * @return Number of rooms successfully added
     */
    public int addRoomsBatch(List<Room> rooms) {
        String query = "INSERT INTO rooms (room_number, room_type, price_per_night, status, floor, " +
                "description, amenities, capacity, area, bed_type, has_wifi, has_tv, has_ac, " +
                "has_attached_bathroom, room_view) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        int successCount = 0;

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return 0;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            // Note: Transaction methods removed - add them to DatabaseConnection if needed
            for (Room room : rooms) {
                pstmt.setString(1, room.getRoomNumber());
                pstmt.setString(2, room.getRoomType());
                pstmt.setDouble(3, room.getPricePerNight());
                pstmt.setString(4, room.getStatus());
                pstmt.setInt(5, room.getFloor());
                pstmt.setString(6, room.getDescription());
                pstmt.setString(7, room.getAmenities());
                pstmt.setInt(8, room.getCapacity());
                pstmt.setDouble(9, room.getArea());
                pstmt.setString(10, room.getBedType());
                pstmt.setBoolean(11, room.isHasWifi());
                pstmt.setBoolean(12, room.isHasTV());
                pstmt.setBoolean(13, room.isHasAC());
                pstmt.setBoolean(14, room.isHasAttachedBathroom());
                pstmt.setString(15, room.getView());
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
     * Update multiple room statuses in batch
     * 
     * @param roomIds List of room IDs
     * @param status  New status
     * @return Number of rooms successfully updated
     */
    public int updateRoomStatusBatch(List<Integer> roomIds, String status) {
        String query = "UPDATE rooms SET status = ? WHERE room_id = ?";
        int successCount = 0;

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return 0;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            for (int roomId : roomIds) {
                pstmt.setString(1, status);
                pstmt.setInt(2, roomId);
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

    // ==================== Helper Methods ====================

    /**
     * Extract Room object from ResultSet
     * 
     * @param rs ResultSet
     * @return Room object
     * @throws SQLException
     */
    private Room extractRoomFromResultSet(ResultSet rs) throws SQLException {
        Room room = new Room();
        room.setRoomId(rs.getInt("room_id"));
        room.setRoomNumber(rs.getString("room_number"));
        room.setRoomType(rs.getString("room_type"));
        room.setPricePerNight(rs.getDouble("price_per_night"));
        room.setStatus(rs.getString("status"));
        room.setFloor(rs.getInt("floor"));
        room.setDescription(rs.getString("description"));
        room.setAmenities(rs.getString("amenities"));
        room.setCapacity(rs.getInt("capacity"));
        room.setArea(rs.getDouble("area"));
        room.setBedType(rs.getString("bed_type"));
        room.setHasWifi(rs.getBoolean("has_wifi"));
        room.setHasTV(rs.getBoolean("has_tv"));
        room.setHasAC(rs.getBoolean("has_ac"));
        room.setHasAttachedBathroom(rs.getBoolean("has_attached_bathroom"));
        room.setView(rs.getString("room_view"));
        room.setImagePath(rs.getString("image_path"));
        room.setRating(rs.getDouble("rating"));
        room.setTotalBookings(rs.getInt("total_bookings"));

        return room;
    }

    // ==================== Advanced Queries ====================

    /**
     * Get rooms sorted by price
     * 
     * @param ascending true for ascending, false for descending
     * @return List of rooms sorted by price
     */
    public List<Room> getRoomsSortedByPrice(boolean ascending) {
        List<Room> rooms = new ArrayList<>();
        String query = "SELECT room_id, room_number, room_type, price_per_night, status, floor, " +
                "description, amenities, capacity, area, bed_type, has_wifi, has_tv, has_ac, " +
                "has_attached_bathroom, room_view, image_path, rating, total_bookings " +
                "FROM rooms ORDER BY price_per_night " + (ascending ? "ASC" : "DESC");

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return rooms;
        }

        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                rooms.add(extractRoomFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }

    /**
     * Get most booked rooms
     * 
     * @param limit Maximum number of rooms to return
     * @return List of most booked rooms
     */
    public List<Room> getMostBookedRooms(int limit) {
        List<Room> rooms = new ArrayList<>();
        String query = "SELECT room_id, room_number, room_type, price_per_night, status, floor, " +
                "description, amenities, capacity, area, bed_type, has_wifi, has_tv, has_ac, " +
                "has_attached_bathroom, room_view, image_path, rating, total_bookings " +
                "FROM rooms ORDER BY total_bookings DESC LIMIT ?";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return rooms;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                rooms.add(extractRoomFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }

    /**
     * Get highest rated rooms
     * 
     * @param limit Maximum number of rooms to return
     * @return List of highest rated rooms
     */
    public List<Room> getHighestRatedRooms(int limit) {
        List<Room> rooms = new ArrayList<>();
        String query = "SELECT room_id, room_number, room_type, price_per_night, status, floor, " +
                "description, amenities, capacity, area, bed_type, has_wifi, has_tv, has_ac, " +
                "has_attached_bathroom, room_view, image_path, rating, total_bookings " +
                "FROM rooms WHERE rating > 0 ORDER BY rating DESC LIMIT ?";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return rooms;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                rooms.add(extractRoomFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }

    /**
     * Get room occupancy rate
     * 
     * @return Occupancy rate as percentage
     */
    public double getOccupancyRate() {
        int[] stats = getRoomStatistics();
        int total = stats[0];
        int occupied = stats[2];

        if (total == 0) {
            return 0.0;
        }
        return (double) occupied / total * 100;
    }

    /**
     * Get revenue by room type
     * 
     * @return Array with revenue for each room type
     */
    public double[] getRevenueByRoomType() {
        double[] revenue = new double[4];
        String query = "SELECT r.room_type, SUM(b.total_amount) as revenue " +
                "FROM rooms r LEFT JOIN bookings b ON r.room_id = b.room_id " +
                "WHERE b.status = 'Completed' " +
                "GROUP BY r.room_type";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return revenue;
        }

        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String roomType = rs.getString("room_type");
                double revenueAmount = rs.getDouble("revenue");

                switch (roomType) {
                    case "Standard":
                        revenue[0] = revenueAmount;
                        break;
                    case "Deluxe":
                        revenue[1] = revenueAmount;
                        break;
                    case "Suite":
                        revenue[2] = revenueAmount;
                        break;
                    case "Presidential":
                        revenue[3] = revenueAmount;
                        break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return revenue;
    }
}