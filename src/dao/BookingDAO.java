// dao/BookingDAO.java
package dao;

import database.DatabaseConnection;
import models.Booking;
import models.Room;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {

    private RoomDAO roomDAO;
    private GuestDAO guestDAO;

    public BookingDAO() {
        this.roomDAO = new RoomDAO();
        this.guestDAO = new GuestDAO();
    }

    // ==================== CREATE Operations ====================

    /**
     * Create a new booking
     * 
     * @param booking Booking object to create
     * @return true if successful, false otherwise
     */
    public boolean createBooking(Booking booking) {
        String query = "INSERT INTO bookings (guest_id, room_id, booking_number, check_in_date, check_out_date, " +
                "number_of_nights, number_of_guests, room_price_per_night, subtotal, tax_amount, " +
                "discount_amount, total_amount, paid_amount, due_amount, status, payment_status, " +
                "booking_source, special_requests, booking_notes, created_by) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query,
                Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, booking.getGuestId());
            pstmt.setInt(2, booking.getRoomId());
            pstmt.setString(3, booking.getBookingNumber());
            pstmt.setDate(4, Date.valueOf(booking.getCheckInDate()));
            pstmt.setDate(5, Date.valueOf(booking.getCheckOutDate()));
            pstmt.setInt(6, booking.getNumberOfNights());
            pstmt.setInt(7, booking.getNumberOfGuests());
            pstmt.setDouble(8, booking.getRoomPricePerNight());
            pstmt.setDouble(9, booking.getSubtotal());
            pstmt.setDouble(10, booking.getTaxAmount());
            pstmt.setDouble(11, booking.getDiscountAmount());
            pstmt.setDouble(12, booking.getTotalAmount());
            pstmt.setDouble(13, booking.getPaidAmount());
            pstmt.setDouble(14, booking.getDueAmount());
            pstmt.setString(15, booking.getStatus());
            pstmt.setString(16, booking.getPaymentStatus());
            pstmt.setString(17, booking.getBookingSource());
            pstmt.setString(18, booking.getSpecialRequests());
            pstmt.setString(19, booking.getBookingNotes());
            pstmt.setInt(20, booking.getCreatedBy());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    booking.setBookingId(generatedKeys.getInt(1));
                }

                // Update room status to Reserved
                roomDAO.updateRoomStatus(booking.getRoomId(), Room.STATUS_RESERVED);

                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ==================== READ Operations ====================

    /**
     * Get booking by ID
     * 
     * @param bookingId Booking ID
     * @return Booking object if found, null otherwise
     */
    public Booking getBookingById(int bookingId) {
        String query = "SELECT * FROM bookings WHERE booking_id = ?";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, bookingId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Booking booking = extractBookingFromResultSet(rs);
                // Load associated guest and room
                booking.setGuest(guestDAO.getGuestById(booking.getGuestId()));
                booking.setRoom(roomDAO.getRoomById(booking.getRoomId()));
                return booking;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get booking by booking number
     * 
     * @param bookingNumber Booking number
     * @return Booking object if found, null otherwise
     */
    public Booking getBookingByNumber(String bookingNumber) {
        String query = "SELECT * FROM bookings WHERE booking_number = ?";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setString(1, bookingNumber);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Booking booking = extractBookingFromResultSet(rs);
                booking.setGuest(guestDAO.getGuestById(booking.getGuestId()));
                booking.setRoom(roomDAO.getRoomById(booking.getRoomId()));
                return booking;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get all bookings
     * 
     * @return List of all bookings
     */
    public List<Booking> getAllBookings() {
        List<Booking> bookings = new ArrayList<>();
        String query = "SELECT * FROM bookings ORDER BY booking_date DESC";

        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Booking booking = extractBookingFromResultSet(rs);
                booking.setGuest(guestDAO.getGuestById(booking.getGuestId()));
                booking.setRoom(roomDAO.getRoomById(booking.getRoomId()));
                bookings.add(booking);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    /**
     * Get bookings by guest ID
     * 
     * @param guestId Guest ID
     * @return List of bookings for the guest
     */
    public List<Booking> getBookingsByGuestId(int guestId) {
        List<Booking> bookings = new ArrayList<>();
        String query = "SELECT * FROM bookings WHERE guest_id = ? ORDER BY booking_date DESC";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, guestId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Booking booking = extractBookingFromResultSet(rs);
                booking.setGuest(guestDAO.getGuestById(guestId));
                booking.setRoom(roomDAO.getRoomById(booking.getRoomId()));
                bookings.add(booking);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    /**
     * Get bookings by room ID
     * 
     * @param roomId Room ID
     * @return List of bookings for the room
     */
    public List<Booking> getBookingsByRoomId(int roomId) {
        List<Booking> bookings = new ArrayList<>();
        String query = "SELECT * FROM bookings WHERE room_id = ? ORDER BY booking_date DESC";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, roomId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Booking booking = extractBookingFromResultSet(rs);
                booking.setGuest(guestDAO.getGuestById(booking.getGuestId()));
                booking.setRoom(roomDAO.getRoomById(roomId));
                bookings.add(booking);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    /**
     * Get bookings by status
     * 
     * @param status Booking status
     * @return List of bookings with specified status
     */
    public List<Booking> getBookingsByStatus(String status) {
        List<Booking> bookings = new ArrayList<>();
        String query = "SELECT * FROM bookings WHERE status = ? ORDER BY check_in_date";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Booking booking = extractBookingFromResultSet(rs);
                booking.setGuest(guestDAO.getGuestById(booking.getGuestId()));
                booking.setRoom(roomDAO.getRoomById(booking.getRoomId()));
                bookings.add(booking);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    /**
     * Get today's check-ins
     * 
     * @return List of bookings for today's check-in
     */
    public List<Booking> getTodayCheckIns() {
        List<Booking> bookings = new ArrayList<>();
        String query = "SELECT * FROM bookings WHERE check_in_date = CURDATE() AND status IN ('Confirmed', 'Pending') ORDER BY booking_date";

        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Booking booking = extractBookingFromResultSet(rs);
                booking.setGuest(guestDAO.getGuestById(booking.getGuestId()));
                booking.setRoom(roomDAO.getRoomById(booking.getRoomId()));
                bookings.add(booking);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    /**
     * Get today's check-outs
     * 
     * @return List of bookings for today's check-out
     */
    public List<Booking> getTodayCheckOuts() {
        List<Booking> bookings = new ArrayList<>();
        String query = "SELECT * FROM bookings WHERE check_out_date = CURDATE() AND status = 'Checked-in' ORDER BY booking_date";

        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Booking booking = extractBookingFromResultSet(rs);
                booking.setGuest(guestDAO.getGuestById(booking.getGuestId()));
                booking.setRoom(roomDAO.getRoomById(booking.getRoomId()));
                bookings.add(booking);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    /**
     * Get active bookings (Checked-in)
     * 
     * @return List of active bookings
     */
    public List<Booking> getActiveBookings() {
        List<Booking> bookings = new ArrayList<>();
        String query = "SELECT * FROM bookings WHERE status = 'Checked-in' ORDER BY check_in_date";

        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Booking booking = extractBookingFromResultSet(rs);
                booking.setGuest(guestDAO.getGuestById(booking.getGuestId()));
                booking.setRoom(roomDAO.getRoomById(booking.getRoomId()));
                bookings.add(booking);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    /**
     * Get upcoming bookings
     * 
     * @param days Number of days to look ahead
     * @return List of upcoming bookings
     */
    public List<Booking> getUpcomingBookings(int days) {
        List<Booking> bookings = new ArrayList<>();
        String query = "SELECT * FROM bookings WHERE check_in_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL ? DAY) "
                +
                "AND status IN ('Confirmed', 'Pending') ORDER BY check_in_date";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, days);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Booking booking = extractBookingFromResultSet(rs);
                booking.setGuest(guestDAO.getGuestById(booking.getGuestId()));
                booking.setRoom(roomDAO.getRoomById(booking.getRoomId()));
                bookings.add(booking);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    /**
     * Get bookings by date range
     * 
     * @param startDate Start date
     * @param endDate   End date
     * @return List of bookings within date range
     */
    public List<Booking> getBookingsByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Booking> bookings = new ArrayList<>();
        String query = "SELECT * FROM bookings WHERE check_in_date BETWEEN ? AND ? " +
                "OR check_out_date BETWEEN ? AND ? ORDER BY check_in_date";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setDate(1, Date.valueOf(startDate));
            pstmt.setDate(2, Date.valueOf(endDate));
            pstmt.setDate(3, Date.valueOf(startDate));
            pstmt.setDate(4, Date.valueOf(endDate));
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Booking booking = extractBookingFromResultSet(rs);
                booking.setGuest(guestDAO.getGuestById(booking.getGuestId()));
                booking.setRoom(roomDAO.getRoomById(booking.getRoomId()));
                bookings.add(booking);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    /**
     * Check if room is available for given dates
     * 
     * @param roomId       Room ID
     * @param checkInDate  Check-in date
     * @param checkOutDate Check-out date
     * @return true if available, false otherwise
     */
    public boolean isRoomAvailable(int roomId, LocalDate checkInDate, LocalDate checkOutDate) {
        String query = "SELECT COUNT(*) FROM bookings WHERE room_id = ? AND status NOT IN ('Cancelled', 'Checked-out') "
                +
                "AND ((check_in_date BETWEEN ? AND ?) OR (check_out_date BETWEEN ? AND ?) " +
                "OR (? BETWEEN check_in_date AND check_out_date))";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, roomId);
            pstmt.setDate(2, Date.valueOf(checkInDate));
            pstmt.setDate(3, Date.valueOf(checkOutDate));
            pstmt.setDate(4, Date.valueOf(checkInDate));
            pstmt.setDate(5, Date.valueOf(checkOutDate));
            pstmt.setDate(6, Date.valueOf(checkInDate));
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get available rooms for given dates
     * 
     * @param checkInDate  Check-in date
     * @param checkOutDate Check-out date
     * @return List of available rooms
     */
    public List<Room> getAvailableRoomsForDates(LocalDate checkInDate, LocalDate checkOutDate) {
        List<Room> allRooms = roomDAO.getAllRooms();
        List<Room> availableRooms = new ArrayList<>();

        for (Room room : allRooms) {
            if (room.isAvailable() && isRoomAvailable(room.getRoomId(), checkInDate, checkOutDate)) {
                availableRooms.add(room);
            }
        }
        return availableRooms;
    }

    /**
     * Get booking statistics
     * 
     * @return Array with booking statistics
     */
    public int[] getBookingStatistics() {
        int[] stats = new int[7]; // total, pending, confirmed, checked-in, checked-out, cancelled, no-show
        String query = "SELECT " +
                "COUNT(*) as total, " +
                "SUM(CASE WHEN status = 'Pending' THEN 1 ELSE 0 END) as pending, " +
                "SUM(CASE WHEN status = 'Confirmed' THEN 1 ELSE 0 END) as confirmed, " +
                "SUM(CASE WHEN status = 'Checked-in' THEN 1 ELSE 0 END) as checked_in, " +
                "SUM(CASE WHEN status = 'Checked-out' THEN 1 ELSE 0 END) as checked_out, " +
                "SUM(CASE WHEN status = 'Cancelled' THEN 1 ELSE 0 END) as cancelled, " +
                "SUM(CASE WHEN status = 'No-Show' THEN 1 ELSE 0 END) as no_show " +
                "FROM bookings WHERE booking_date >= DATE_SUB(NOW(), INTERVAL 30 DAY)";

        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                stats[0] = rs.getInt("total");
                stats[1] = rs.getInt("pending");
                stats[2] = rs.getInt("confirmed");
                stats[3] = rs.getInt("checked_in");
                stats[4] = rs.getInt("checked_out");
                stats[5] = rs.getInt("cancelled");
                stats[6] = rs.getInt("no_show");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }

    /**
     * Get revenue statistics
     * 
     * @return Array with revenue statistics
     */
    public double[] getRevenueStatistics() {
        double[] revenue = new double[4]; // total, today, this month, this year
        String query = "SELECT " +
                "SUM(total_amount) as total_revenue, " +
                "SUM(CASE WHEN DATE(booking_date) = CURDATE() THEN total_amount ELSE 0 END) as today_revenue, " +
                "SUM(CASE WHEN MONTH(booking_date) = MONTH(CURDATE()) AND YEAR(booking_date) = YEAR(CURDATE()) THEN total_amount ELSE 0 END) as monthly_revenue, "
                +
                "SUM(CASE WHEN YEAR(booking_date) = YEAR(CURDATE()) THEN total_amount ELSE 0 END) as yearly_revenue " +
                "FROM bookings WHERE status IN ('Checked-out', 'Completed')";

        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                revenue[0] = rs.getDouble("total_revenue");
                revenue[1] = rs.getDouble("today_revenue");
                revenue[2] = rs.getDouble("monthly_revenue");
                revenue[3] = rs.getDouble("yearly_revenue");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return revenue;
    }

    /**
     * Get occupancy rate for date range
     * 
     * @param startDate Start date
     * @param endDate   End date
     * @return Occupancy rate percentage
     */
    public double getOccupancyRate(LocalDate startDate, LocalDate endDate) {
        int totalRooms = roomDAO.getRoomCount();
        if (totalRooms == 0)
            return 0.0;

        String query = "SELECT COUNT(DISTINCT room_id) as occupied_rooms FROM bookings " +
                "WHERE status IN ('Checked-in', 'Confirmed') " +
                "AND ((check_in_date BETWEEN ? AND ?) OR (check_out_date BETWEEN ? AND ?))";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setDate(1, Date.valueOf(startDate));
            pstmt.setDate(2, Date.valueOf(endDate));
            pstmt.setDate(3, Date.valueOf(startDate));
            pstmt.setDate(4, Date.valueOf(endDate));
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int occupiedRooms = rs.getInt("occupied_rooms");
                return (double) occupiedRooms / totalRooms * 100;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    // ==================== UPDATE Operations ====================

    /**
     * Update booking
     * 
     * @param booking Booking object with updated information
     * @return true if successful, false otherwise
     */
    public boolean updateBooking(Booking booking) {
        String query = "UPDATE bookings SET check_in_date = ?, check_out_date = ?, number_of_nights = ?, " +
                "number_of_guests = ?, room_price_per_night = ?, subtotal = ?, tax_amount = ?, " +
                "discount_amount = ?, total_amount = ?, paid_amount = ?, due_amount = ?, " +
                "status = ?, payment_status = ?, special_requests = ?, booking_notes = ?, " +
                "modified_by = ?, last_modified = ? WHERE booking_id = ?";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setDate(1, Date.valueOf(booking.getCheckInDate()));
            pstmt.setDate(2, Date.valueOf(booking.getCheckOutDate()));
            pstmt.setInt(3, booking.getNumberOfNights());
            pstmt.setInt(4, booking.getNumberOfGuests());
            pstmt.setDouble(5, booking.getRoomPricePerNight());
            pstmt.setDouble(6, booking.getSubtotal());
            pstmt.setDouble(7, booking.getTaxAmount());
            pstmt.setDouble(8, booking.getDiscountAmount());
            pstmt.setDouble(9, booking.getTotalAmount());
            pstmt.setDouble(10, booking.getPaidAmount());
            pstmt.setDouble(11, booking.getDueAmount());
            pstmt.setString(12, booking.getStatus());
            pstmt.setString(13, booking.getPaymentStatus());
            pstmt.setString(14, booking.getSpecialRequests());
            pstmt.setString(15, booking.getBookingNotes());
            pstmt.setInt(16, booking.getModifiedBy());
            pstmt.setTimestamp(17, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setInt(18, booking.getBookingId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Update booking status
     * 
     * @param bookingId Booking ID
     * @param status    New status
     * @return true if successful, false otherwise
     */
    public boolean updateBookingStatus(int bookingId, String status) {
        String query = "UPDATE bookings SET status = ?, last_modified = ? WHERE booking_id = ?";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setString(1, status);
            pstmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setInt(3, bookingId);

            boolean updated = pstmt.executeUpdate() > 0;

            if (updated) {
                // Update room status based on booking status
                Booking booking = getBookingById(bookingId);
                if (booking != null) {
                    if (status.equals(Booking.STATUS_CHECKED_IN)) {
                        roomDAO.updateRoomStatus(booking.getRoomId(), Room.STATUS_OCCUPIED);
                    } else if (status.equals(Booking.STATUS_CHECKED_OUT) || status.equals(Booking.STATUS_CANCELLED)) {
                        roomDAO.updateRoomStatus(booking.getRoomId(), Room.STATUS_AVAILABLE);
                    } else if (status.equals(Booking.STATUS_CONFIRMED)) {
                        roomDAO.updateRoomStatus(booking.getRoomId(), Room.STATUS_RESERVED);
                    }
                }
            }

            return updated;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Update payment status
     * 
     * @param bookingId     Booking ID
     * @param paymentStatus New payment status
     * @param paidAmount    Amount paid
     * @return true if successful, false otherwise
     */
    public boolean updatePaymentStatus(int bookingId, String paymentStatus, double paidAmount) {
        String query = "UPDATE bookings SET payment_status = ?, paid_amount = ?, due_amount = total_amount - ?, " +
                "last_modified = ? WHERE booking_id = ?";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setString(1, paymentStatus);
            pstmt.setDouble(2, paidAmount);
            pstmt.setDouble(3, paidAmount);
            pstmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setInt(5, bookingId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Check-in guest
     * 
     * @param bookingId Booking ID
     * @return true if successful, false otherwise
     */
    public boolean checkIn(int bookingId) {
        String query = "UPDATE bookings SET status = 'Checked-in', check_in_time = ?, last_modified = ? WHERE booking_id = ?";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setInt(3, bookingId);

            boolean updated = pstmt.executeUpdate() > 0;

            if (updated) {
                Booking booking = getBookingById(bookingId);
                if (booking != null) {
                    // Update room status
                    roomDAO.updateRoomStatus(booking.getRoomId(), Room.STATUS_OCCUPIED);
                    // Update guest last visit
                    guestDAO.updateLastVisit(booking.getGuestId());
                }
            }

            return updated;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Check-out guest
     * 
     * @param bookingId Booking ID
     * @return true if successful, false otherwise
     */
    public boolean checkOut(int bookingId) {
        String query = "UPDATE bookings SET status = 'Checked-out', check_out_time = ?, last_modified = ? WHERE booking_id = ?";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setInt(3, bookingId);

            boolean updated = pstmt.executeUpdate() > 0;

            if (updated) {
                Booking booking = getBookingById(bookingId);
                if (booking != null) {
                    // Update room status
                    roomDAO.updateRoomStatus(booking.getRoomId(), Room.STATUS_AVAILABLE);
                    // Update guest stays and spending
                    guestDAO.updateGuestStaysAndSpending(booking.getGuestId(), booking.getTotalAmount());
                }
            }

            return updated;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Cancel booking
     * 
     * @param bookingId       Booking ID
     * @param reason          Cancellation reason
     * @param cancellationFee Cancellation fee
     * @return true if successful, false otherwise
     */
    public boolean cancelBooking(int bookingId, String reason, double cancellationFee) {
        String query = "UPDATE bookings SET status = 'Cancelled', cancellation_reason = ?, " +
                "cancellation_date = ?, cancellation_fee = ?, last_modified = ? WHERE booking_id = ?";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setString(1, reason);
            pstmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setDouble(3, cancellationFee);
            pstmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setInt(5, bookingId);

            boolean updated = pstmt.executeUpdate() > 0;

            if (updated) {
                Booking booking = getBookingById(bookingId);
                if (booking != null) {
                    // Update room status back to available
                    roomDAO.updateRoomStatus(booking.getRoomId(), Room.STATUS_AVAILABLE);
                }
            }

            return updated;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ==================== DELETE Operations ====================

    /**
     * Delete booking (only if cancelled or pending)
     * 
     * @param bookingId Booking ID
     * @return true if successful, false otherwise
     */
    public boolean deleteBooking(int bookingId) {
        Booking booking = getBookingById(bookingId);
        if (booking == null)
            return false;

        // Only allow deletion of cancelled or pending bookings
        if (!booking.getStatus().equals(Booking.STATUS_CANCELLED) &&
                !booking.getStatus().equals(Booking.STATUS_PENDING)) {
            return false;
        }

        String query = "DELETE FROM bookings WHERE booking_id = ?";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, bookingId);

            boolean deleted = pstmt.executeUpdate() > 0;

            if (deleted && booking.getStatus().equals(Booking.STATUS_CANCELLED)) {
                // Update room status back to available
                roomDAO.updateRoomStatus(booking.getRoomId(), Room.STATUS_AVAILABLE);
            }

            return deleted;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ==================== Helper Methods ====================

    /**
     * Extract Booking object from ResultSet
     * 
     * @param rs ResultSet
     * @return Booking object
     * @throws SQLException
     */
    private Booking extractBookingFromResultSet(ResultSet rs) throws SQLException {
        Booking booking = new Booking();
        booking.setBookingId(rs.getInt("booking_id"));
        booking.setGuestId(rs.getInt("guest_id"));
        booking.setRoomId(rs.getInt("room_id"));
        booking.setBookingNumber(rs.getString("booking_number"));
        booking.setCheckInDate(rs.getDate("check_in_date").toLocalDate());
        booking.setCheckOutDate(rs.getDate("check_out_date").toLocalDate());

        Timestamp checkInTime = rs.getTimestamp("check_in_time");
        if (checkInTime != null) {
            booking.setCheckInTime(checkInTime.toLocalDateTime());
        }

        Timestamp checkOutTime = rs.getTimestamp("check_out_time");
        if (checkOutTime != null) {
            booking.setCheckOutTime(checkOutTime.toLocalDateTime());
        }

        booking.setNumberOfNights(rs.getInt("number_of_nights"));
        booking.setNumberOfGuests(rs.getInt("number_of_guests"));
        booking.setRoomPricePerNight(rs.getDouble("room_price_per_night"));
        booking.setSubtotal(rs.getDouble("subtotal"));
        booking.setTaxAmount(rs.getDouble("tax_amount"));
        booking.setDiscountAmount(rs.getDouble("discount_amount"));
        booking.setTotalAmount(rs.getDouble("total_amount"));
        booking.setPaidAmount(rs.getDouble("paid_amount"));
        booking.setDueAmount(rs.getDouble("due_amount"));
        booking.setStatus(rs.getString("status"));
        booking.setPaymentStatus(rs.getString("payment_status"));
        booking.setBookingSource(rs.getString("booking_source"));
        booking.setSpecialRequests(rs.getString("special_requests"));
        booking.setCancellationReason(rs.getString("cancellation_reason"));

        Timestamp cancellationDate = rs.getTimestamp("cancellation_date");
        if (cancellationDate != null) {
            booking.setCancellationDate(cancellationDate.toLocalDateTime());
        }

        booking.setCancellationFee(rs.getDouble("cancellation_fee"));
        booking.setBookingNotes(rs.getString("booking_notes"));

        Timestamp bookingDate = rs.getTimestamp("booking_date");
        if (bookingDate != null) {
            booking.setBookingDate(bookingDate.toLocalDateTime());
        }

        Timestamp lastModified = rs.getTimestamp("last_modified");
        if (lastModified != null) {
            booking.setLastModified(lastModified.toLocalDateTime());
        }

        booking.setCreatedBy(rs.getInt("created_by"));
        booking.setModifiedBy(rs.getInt("modified_by"));

        return booking;
    }

    // ==================== Report Methods ====================

    /**
     * Get monthly revenue for the year
     * 
     * @param year Year
     * @return Array of monthly revenue
     */
    public double[] getMonthlyRevenue(int year) {
        double[] monthlyRevenue = new double[12];
        String query = "SELECT MONTH(booking_date) as month, SUM(total_amount) as revenue " +
                "FROM bookings WHERE YEAR(booking_date) = ? AND status IN ('Checked-out', 'Completed') " +
                "GROUP BY MONTH(booking_date)";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, year);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int month = rs.getInt("month") - 1;
                if (month >= 0 && month < 12) {
                    monthlyRevenue[month] = rs.getDouble("revenue");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return monthlyRevenue;
    }

    /**
     * Get booking source distribution
     * 
     * @return Array with booking source counts
     */
    public int[] getBookingSourceDistribution() {
        int[] distribution = new int[6]; // Direct, Phone, Website, Aggregator, Corporate, Walk-in
        String query = "SELECT " +
                "SUM(CASE WHEN booking_source = 'Direct' THEN 1 ELSE 0 END) as direct, " +
                "SUM(CASE WHEN booking_source = 'Phone' THEN 1 ELSE 0 END) as phone, " +
                "SUM(CASE WHEN booking_source = 'Website' THEN 1 ELSE 0 END) as website, " +
                "SUM(CASE WHEN booking_source = 'Travel Aggregator' THEN 1 ELSE 0 END) as aggregator, " +
                "SUM(CASE WHEN booking_source = 'Corporate' THEN 1 ELSE 0 END) as corporate, " +
                "SUM(CASE WHEN booking_source = 'Walk-in' THEN 1 ELSE 0 END) as walkin " +
                "FROM bookings WHERE booking_date >= DATE_SUB(NOW(), INTERVAL 30 DAY)";

        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                distribution[0] = rs.getInt("direct");
                distribution[1] = rs.getInt("phone");
                distribution[2] = rs.getInt("website");
                distribution[3] = rs.getInt("aggregator");
                distribution[4] = rs.getInt("corporate");
                distribution[5] = rs.getInt("walkin");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return distribution;
    }

    /**
     * Get average daily rate (ADR)
     * 
     * @param startDate Start date
     * @param endDate   End date
     * @return Average daily rate
     */
    public double getAverageDailyRate(LocalDate startDate, LocalDate endDate) {
        String query = "SELECT AVG(total_amount / number_of_nights) as adr FROM bookings " +
                "WHERE check_in_date BETWEEN ? AND ? AND status IN ('Checked-out', 'Completed')";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setDate(1, Date.valueOf(startDate));
            pstmt.setDate(2, Date.valueOf(endDate));
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("adr");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    /**
     * Get revenue by room type
     * 
     * @return Array with revenue by room type
     */
    public double[] getRevenueByRoomType() {
        double[] revenue = new double[4]; // Standard, Deluxe, Suite, Presidential
        String query = "SELECT r.room_type, SUM(b.total_amount) as revenue " +
                "FROM bookings b JOIN rooms r ON b.room_id = r.room_id " +
                "WHERE b.status IN ('Checked-out', 'Completed') " +
                "GROUP BY r.room_type";

        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String roomType = rs.getString("room_type");
                double amount = rs.getDouble("revenue");

                switch (roomType) {
                    case "Standard":
                        revenue[0] = amount;
                        break;
                    case "Deluxe":
                        revenue[1] = amount;
                        break;
                    case "Suite":
                        revenue[2] = amount;
                        break;
                    case "Presidential":
                        revenue[3] = amount;
                        break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return revenue;
    }
}