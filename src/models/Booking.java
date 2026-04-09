// models/Booking.java
package models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class Booking {
    // Private fields
    private int bookingId;
    private int guestId;
    private int roomId;
    private String bookingNumber;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private int numberOfNights;
    private int numberOfGuests;
    private double roomPricePerNight;
    private double subtotal;
    private double taxAmount;
    private double discountAmount;
    private double totalAmount;
    private double paidAmount;
    private double dueAmount;
    private String status;
    private String paymentStatus;
    private String bookingSource;
    private String specialRequests;
    private String cancellationReason;
    private LocalDateTime cancellationDate;
    private double cancellationFee;
    private String bookingNotes;
    private LocalDateTime bookingDate;
    private LocalDateTime lastModified;
    private int createdBy;
    private int modifiedBy;

    // Guest and Room objects for convenience
    private Guest guest;
    private Room room;

    // Constants for booking status
    public static final String STATUS_PENDING = "Pending";
    public static final String STATUS_CONFIRMED = "Confirmed";
    public static final String STATUS_CHECKED_IN = "Checked-in";
    public static final String STATUS_CHECKED_OUT = "Checked-out";
    public static final String STATUS_CANCELLED = "Cancelled";
    public static final String STATUS_NO_SHOW = "No-Show";
    public static final String STATUS_COMPLETED = "Completed";

    // Constants for payment status
    public static final String PAYMENT_PENDING = "Pending";
    public static final String PAYMENT_PARTIAL = "Partial";
    public static final String PAID = "Paid";
    public static final String PAYMENT_REFUNDED = "Refunded";

    // Constants for booking source
    public static final String SOURCE_DIRECT = "Direct";
    public static final String SOURCE_PHONE = "Phone";
    public static final String SOURCE_WEBSITE = "Website";
    public static final String SOURCE_AGGREGATOR = "Travel Aggregator";
    public static final String SOURCE_CORPORATE = "Corporate";
    public static final String SOURCE_WALK_IN = "Walk-in";

    // Tax rates
    private static final double GST_RATE = 0.18; // 18% GST
    private static final double CESS_RATE = 0.02; // 2% Cess on luxury

    // Constructors
    public Booking() {
        this.bookingDate = LocalDateTime.now();
        this.lastModified = LocalDateTime.now();
        this.status = STATUS_PENDING;
        this.paymentStatus = PAYMENT_PENDING;
        this.numberOfGuests = 1;
        this.discountAmount = 0.0;
        this.paidAmount = 0.0;
        this.cancellationFee = 0.0;
        this.generateBookingNumber();
    }

    public Booking(int guestId, int roomId, LocalDate checkInDate, LocalDate checkOutDate, int numberOfGuests) {
        this();
        this.guestId = guestId;
        this.roomId = roomId;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.numberOfGuests = numberOfGuests;
        this.numberOfNights = (int) ChronoUnit.DAYS.between(checkInDate, checkOutDate);
    }

    // Getters and Setters
    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getGuestId() {
        return guestId;
    }

    public void setGuestId(int guestId) {
        this.guestId = guestId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getBookingNumber() {
        return bookingNumber;
    }

    public void setBookingNumber(String bookingNumber) {
        this.bookingNumber = bookingNumber;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
        if (checkInDate != null && checkOutDate != null) {
            this.numberOfNights = (int) ChronoUnit.DAYS.between(checkInDate, checkOutDate);
            calculateTotal();
        }
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
        if (checkInDate != null && checkOutDate != null) {
            this.numberOfNights = (int) ChronoUnit.DAYS.between(checkInDate, checkOutDate);
            calculateTotal();
        }
    }

    public LocalDateTime getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(LocalDateTime checkInTime) {
        this.checkInTime = checkInTime;
    }

    public LocalDateTime getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(LocalDateTime checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    public int getNumberOfNights() {
        return numberOfNights;
    }

    public void setNumberOfNights(int numberOfNights) {
        this.numberOfNights = numberOfNights;
    }

    public int getNumberOfGuests() {
        return numberOfGuests;
    }

    public void setNumberOfGuests(int numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
    }

    public double getRoomPricePerNight() {
        return roomPricePerNight;
    }

    public void setRoomPricePerNight(double roomPricePerNight) {
        this.roomPricePerNight = roomPricePerNight;
        calculateTotal();
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
        calculateTotal();
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(double paidAmount) {
        this.paidAmount = paidAmount;
        this.dueAmount = totalAmount - paidAmount;
        updatePaymentStatus();
    }

    public double getDueAmount() {
        return dueAmount;
    }

    public void setDueAmount(double dueAmount) {
        this.dueAmount = dueAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getBookingSource() {
        return bookingSource;
    }

    public void setBookingSource(String bookingSource) {
        this.bookingSource = bookingSource;
    }

    public String getSpecialRequests() {
        return specialRequests;
    }

    public void setSpecialRequests(String specialRequests) {
        this.specialRequests = specialRequests;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public LocalDateTime getCancellationDate() {
        return cancellationDate;
    }

    public void setCancellationDate(LocalDateTime cancellationDate) {
        this.cancellationDate = cancellationDate;
    }

    public double getCancellationFee() {
        return cancellationFee;
    }

    public void setCancellationFee(double cancellationFee) {
        this.cancellationFee = cancellationFee;
    }

    public String getBookingNotes() {
        return bookingNotes;
    }

    public void setBookingNotes(String bookingNotes) {
        this.bookingNotes = bookingNotes;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public int getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(int modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Guest getGuest() {
        return guest;
    }

    public void setGuest(Guest guest) {
        this.guest = guest;
        if (guest != null) {
            this.guestId = guest.getGuestId();
        }
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
        if (room != null) {
            this.roomId = room.getRoomId();
            this.roomPricePerNight = room.getPricePerNight();
            calculateTotal();
        }
    }

    // Business methods
    private void generateBookingNumber() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String timestamp = LocalDateTime.now().format(formatter);
        this.bookingNumber = "BK" + timestamp;
    }

    public void calculateTotal() {
        if (numberOfNights > 0 && roomPricePerNight > 0) {
            this.subtotal = roomPricePerNight * numberOfNights;
            this.taxAmount = calculateTax(subtotal);
            this.totalAmount = subtotal + taxAmount - discountAmount;
            this.dueAmount = totalAmount - paidAmount;
            updatePaymentStatus();
        }
    }

    private double calculateTax(double amount) {
        double gst = amount * GST_RATE;
        double cess = amount * CESS_RATE;
        return gst + cess;
    }

    private void updatePaymentStatus() {
        if (paidAmount >= totalAmount && totalAmount > 0) {
            this.paymentStatus = PAID;
        } else if (paidAmount > 0) {
            this.paymentStatus = PAYMENT_PARTIAL;
        } else {
            this.paymentStatus = PAYMENT_PENDING;
        }
    }

    public void addPayment(double amount) {
        this.paidAmount += amount;
        this.dueAmount = totalAmount - paidAmount;
        updatePaymentStatus();
    }

    public boolean isFullyPaid() {
        return paidAmount >= totalAmount;
    }

    public boolean isOverdue() {
        return dueAmount > 0 && checkOutDate != null && checkOutDate.isBefore(LocalDate.now());
    }

    public void checkIn() {
        this.status = STATUS_CHECKED_IN;
        this.checkInTime = LocalDateTime.now();
        this.lastModified = LocalDateTime.now();
    }

    public void checkOut() {
        this.status = STATUS_CHECKED_OUT;
        this.checkOutTime = LocalDateTime.now();
        this.lastModified = LocalDateTime.now();
    }

    public void cancel(String reason) {
        this.status = STATUS_CANCELLED;
        this.cancellationReason = reason;
        this.cancellationDate = LocalDateTime.now();
        this.lastModified = LocalDateTime.now();
        calculateCancellationFee();
    }

    private void calculateCancellationFee() {
        if (checkInDate != null) {
            long daysUntilCheckIn = ChronoUnit.DAYS.between(LocalDate.now(), checkInDate);
            if (daysUntilCheckIn >= 7) {
                cancellationFee = totalAmount * 0.10; // 10% fee
            } else if (daysUntilCheckIn >= 3) {
                cancellationFee = totalAmount * 0.25; // 25% fee
            } else if (daysUntilCheckIn >= 1) {
                cancellationFee = totalAmount * 0.50; // 50% fee
            } else {
                cancellationFee = totalAmount; // 100% fee
            }
        }
    }

    public boolean canBeCancelled() {
        return status.equals(STATUS_PENDING) || status.equals(STATUS_CONFIRMED);
    }

    public boolean canBeModified() {
        return status.equals(STATUS_PENDING) || status.equals(STATUS_CONFIRMED);
    }

    public void confirm() {
        if (status.equals(STATUS_PENDING)) {
            this.status = STATUS_CONFIRMED;
            this.lastModified = LocalDateTime.now();
        }
    }

    // Utility methods
    public String getFormattedBookingNumber() {
        return bookingNumber;
    }

    public String getFormattedCheckInDate() {
        if (checkInDate == null)
            return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return checkInDate.format(formatter);
    }

    public String getFormattedCheckOutDate() {
        if (checkOutDate == null)
            return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return checkOutDate.format(formatter);
    }

    public String getFormattedCheckInDateTime() {
        if (checkInTime == null)
            return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return checkInTime.format(formatter);
    }

    public String getFormattedCheckOutDateTime() {
        if (checkOutTime == null)
            return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return checkOutTime.format(formatter);
    }

    public String getBookingDuration() {
        if (numberOfNights == 1) {
            return "1 night";
        }
        return numberOfNights + " nights";
    }

    public String getStatusIcon() {
        if (status == null)
            return "❓";
        switch (status.toLowerCase()) {
            case "pending":
                return "⏳";
            case "confirmed":
                return "✅";
            case "checked-in":
                return "🏨";
            case "checked-out":
                return "🚪";
            case "cancelled":
                return "❌";
            case "no-show":
                return "🚫";
            case "completed":
                return "🏁";
            default:
                return "❓";
        }
    }

    public String getStatusColor() {
        if (status == null)
            return "#7f8c8d";
        switch (status.toLowerCase()) {
            case "pending":
                return "#f39c12"; // Orange
            case "confirmed":
                return "#2ecc71"; // Green
            case "checked-in":
                return "#3498db"; // Blue
            case "checked-out":
                return "#9b59b6"; // Purple
            case "cancelled":
                return "#e74c3c"; // Red
            case "no-show":
                return "#95a5a6"; // Gray
            case "completed":
                return "#27ae60"; // Dark Green
            default:
                return "#7f8c8d"; // Dark Gray
        }
    }

    public String getPaymentStatusIcon() {
        if (paymentStatus == null)
            return "❓";
        switch (paymentStatus.toLowerCase()) {
            case "pending":
                return "⏰";
            case "partial":
                return "💳";
            case "paid":
                return "💰";
            case "refunded":
                return "↩️";
            default:
                return "❓";
        }
    }

    public String getBookingSourceIcon() {
        switch (bookingSource != null ? bookingSource.toLowerCase() : "") {
            case "direct":
                return "🏢";
            case "phone":
                return "📞";
            case "website":
                return "🌐";
            case "travel aggregator":
                return "🛫";
            case "corporate":
                return "🏭";
            case "walk-in":
                return "🚶";
            default:
                return "❓";
        }
    }

    public String getBookingSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("Booking #").append(bookingNumber).append("\n");
        summary.append("==================\n");
        summary.append("Guest: ")
                .append(guest != null ? guest.getFirstName() + " " + guest.getLastName() : "Guest ID: " + guestId)
                .append("\n");
        summary.append("Room: ").append(room != null ? room.getRoomNumber() : "Room ID: " + roomId).append("\n");
        summary.append("Dates: ").append(getFormattedCheckInDate()).append(" to ").append(getFormattedCheckOutDate())
                .append("\n");
        summary.append("Duration: ").append(getBookingDuration()).append("\n");
        summary.append("Guests: ").append(numberOfGuests).append("\n");
        summary.append("Status: ").append(status).append(" ").append(getStatusIcon()).append("\n");
        summary.append("\nFinancial Summary:\n");
        summary.append("Room Charges: ₹").append(String.format("%,.2f", subtotal)).append("\n");
        summary.append("Tax (GST+Cess): ₹").append(String.format("%,.2f", taxAmount)).append("\n");
        if (discountAmount > 0) {
            summary.append("Discount: -₹").append(String.format("%,.2f", discountAmount)).append("\n");
        }
        summary.append("Total Amount: ₹").append(String.format("%,.2f", totalAmount)).append("\n");
        summary.append("Paid: ₹").append(String.format("%,.2f", paidAmount)).append("\n");
        summary.append("Due: ₹").append(String.format("%,.2f", dueAmount)).append("\n");
        summary.append("Payment Status: ").append(paymentStatus).append(" ").append(getPaymentStatusIcon());
        return summary.toString();
    }

    public Object[] toTableRow() {
        return new Object[] {
                bookingId,
                bookingNumber,
                guest != null ? guest.getFirstName() + " " + guest.getLastName() : "ID: " + guestId,
                room != null ? room.getRoomNumber() : "ID: " + roomId,
                getFormattedCheckInDate(),
                getFormattedCheckOutDate(),
                numberOfNights,
                String.format("₹%,.2f", totalAmount),
                status + " " + getStatusIcon(),
                paymentStatus + " " + getPaymentStatusIcon()
        };
    }

    public Object[] toDetailedTableRow() {
        return new Object[] {
                bookingNumber,
                guest != null ? guest.getFirstName() + " " + guest.getLastName() : "ID: " + guestId,
                room != null ? room.getRoomNumber() : "ID: " + roomId,
                getFormattedCheckInDate(),
                getFormattedCheckOutDate(),
                numberOfGuests,
                String.format("₹%,.2f", roomPricePerNight),
                getBookingDuration(),
                String.format("₹%,.2f", totalAmount),
                String.format("₹%,.2f", paidAmount),
                String.format("₹%,.2f", dueAmount),
                status + " " + getStatusIcon()
        };
    }

    // Static methods
    public static String[] getAllStatuses() {
        return new String[] { STATUS_PENDING, STATUS_CONFIRMED, STATUS_CHECKED_IN,
                STATUS_CHECKED_OUT, STATUS_CANCELLED, STATUS_NO_SHOW, STATUS_COMPLETED };
    }

    public static String[] getAllPaymentStatuses() {
        return new String[] { PAYMENT_PENDING, PAYMENT_PARTIAL, PAID, PAYMENT_REFUNDED };
    }

    public static String[] getAllBookingSources() {
        return new String[] { SOURCE_DIRECT, SOURCE_PHONE, SOURCE_WEBSITE,
                SOURCE_AGGREGATOR, SOURCE_CORPORATE, SOURCE_WALK_IN };
    }

    @Override
    public String toString() {
        return String.format("Booking %s - %s to %s (%s)",
                bookingNumber, getFormattedCheckInDate(), getFormattedCheckOutDate(), status);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Booking booking = (Booking) obj;
        return bookingId == booking.bookingId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(bookingId);
    }
}