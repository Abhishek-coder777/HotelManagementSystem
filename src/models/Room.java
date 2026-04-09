// models/Room.java
package models;

import java.util.ArrayList;
import java.util.List;

public class Room {
    // Private fields
    private int roomId;
    private String roomNumber;
    private String roomType;
    private double pricePerNight;
    private String status;
    private int floor;
    private String description;
    private String amenities;
    private int capacity;
    private double area;
    private String bedType;
    private boolean hasWifi;
    private boolean hasTV;
    private boolean hasAC;
    private boolean hasAttachedBathroom;
    private String view;
    private String imagePath;
    private double rating;
    private int totalBookings;

    // Constants for room types
    public static final String TYPE_STANDARD = "Standard";
    public static final String TYPE_DELUXE = "Deluxe";
    public static final String TYPE_SUITE = "Suite";
    public static final String TYPE_PRESIDENTIAL = "Presidential";

    // Constants for room status
    public static final String STATUS_AVAILABLE = "Available";
    public static final String STATUS_OCCUPIED = "Occupied";
    public static final String STATUS_MAINTENANCE = "Maintenance";
    public static final String STATUS_RESERVED = "Reserved";
    public static final String STATUS_CLEANING = "Cleaning";

    // Constructors
    public Room() {
        this.status = STATUS_AVAILABLE;
        this.capacity = 2;
        this.hasWifi = true;
        this.hasTV = true;
        this.hasAC = true;
        this.hasAttachedBathroom = true;
        this.rating = 0.0;
        this.totalBookings = 0;
    }

    public Room(String roomNumber, String roomType, double pricePerNight, int floor) {
        this();
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.pricePerNight = pricePerNight;
        this.floor = floor;
    }

    public Room(String roomNumber, String roomType, double pricePerNight, String status, int floor) {
        this(roomNumber, roomType, pricePerNight, floor);
        this.status = status;
    }

    // Getters and Setters
    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAmenities() {
        return amenities;
    }

    public void setAmenities(String amenities) {
        this.amenities = amenities;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public String getBedType() {
        return bedType;
    }

    public void setBedType(String bedType) {
        this.bedType = bedType;
    }

    public boolean isHasWifi() {
        return hasWifi;
    }

    public void setHasWifi(boolean hasWifi) {
        this.hasWifi = hasWifi;
    }

    public boolean isHasTV() {
        return hasTV;
    }

    public void setHasTV(boolean hasTV) {
        this.hasTV = hasTV;
    }

    public boolean isHasAC() {
        return hasAC;
    }

    public void setHasAC(boolean hasAC) {
        this.hasAC = hasAC;
    }

    public boolean isHasAttachedBathroom() {
        return hasAttachedBathroom;
    }

    public void setHasAttachedBathroom(boolean hasAttachedBathroom) {
        this.hasAttachedBathroom = hasAttachedBathroom;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getTotalBookings() {
        return totalBookings;
    }

    public void setTotalBookings(int totalBookings) {
        this.totalBookings = totalBookings;
    }

    // Helper methods
    public boolean isAvailable() {
        return STATUS_AVAILABLE.equalsIgnoreCase(this.status);
    }

    public boolean isOccupied() {
        return STATUS_OCCUPIED.equalsIgnoreCase(this.status);
    }

    public boolean isUnderMaintenance() {
        return STATUS_MAINTENANCE.equalsIgnoreCase(this.status);
    }

    public boolean isReserved() {
        return STATUS_RESERVED.equalsIgnoreCase(this.status);
    }

    public double getPriceWithTax() {
        // Assuming 18% GST
        return pricePerNight * 1.18;
    }

    public double getPriceForDays(int days) {
        return pricePerNight * days;
    }

    public double getTotalPriceWithTax(int days) {
        return getPriceForDays(days) * 1.18;
    }

    public String getStatusColor() {
        if (status == null)
            return "#95a5a6";
        switch (status.toLowerCase()) {
            case "available":
                return "#2ecc71"; // Green
            case "occupied":
                return "#e74c3c"; // Red
            case "maintenance":
                return "#f39c12"; // Orange
            case "reserved":
                return "#3498db"; // Blue
            case "cleaning":
                return "#9b59b6"; // Purple
            default:
                return "#95a5a6"; // Gray
        }
    }

    public String getStatusIcon() {
        if (status == null)
            return "❓";
        switch (status.toLowerCase()) {
            case "available":
                return "✅";
            case "occupied":
                return "🔴";
            case "maintenance":
                return "🔧";
            case "reserved":
                return "📅";
            case "cleaning":
                return "🧹";
            default:
                return "❓";
        }
    }

    public String getRoomTypeIcon() {
        if (roomType == null)
            return "🏨";
        switch (roomType.toLowerCase()) {
            case "standard":
                return "🏠";
            case "deluxe":
                return "🌟";
            case "suite":
                return "💎";
            case "presidential":
                return "👑";
            default:
                return "🏨";
        }
    }

    public List<String> getAmenitiesList() {
        List<String> amenitiesList = new ArrayList<>();
        if (hasWifi)
            amenitiesList.add("WiFi");
        if (hasTV)
            amenitiesList.add("TV");
        if (hasAC)
            amenitiesList.add("Air Conditioning");
        if (hasAttachedBathroom)
            amenitiesList.add("Attached Bathroom");
        if (amenities != null && !amenities.isEmpty()) {
            String[] extraAmenities = amenities.split(",");
            for (String amenity : extraAmenities) {
                amenitiesList.add(amenity.trim());
            }
        }
        return amenitiesList;
    }

    public String getFormattedAmenities() {
        return String.join(" • ", getAmenitiesList());
    }

    public String getRoomInfo() {
        StringBuilder info = new StringBuilder();
        info.append("Room ").append(roomNumber).append(" (").append(roomType).append(")\n");
        info.append("Floor: ").append(floor).append("\n");
        info.append("Price: ₹").append(String.format("%.2f", pricePerNight)).append("/night\n");
        info.append("Status: ").append(status).append(" ").append(getStatusIcon()).append("\n");
        info.append("Capacity: ").append(capacity).append(" persons\n");
        if (area > 0) {
            info.append("Area: ").append(area).append(" sq.ft\n");
        }
        if (bedType != null) {
            info.append("Bed: ").append(bedType).append("\n");
        }
        info.append("Amenities: ").append(getFormattedAmenities());
        return info.toString();
    }

    public String getShortInfo() {
        return String.format("Room %s - %s (₹%.2f/night) %s",
                roomNumber, roomType, pricePerNight, getStatusIcon());
    }

    public Object[] toTableRow() {
        return new Object[] {
                roomId,
                roomNumber,
                roomType + " " + getRoomTypeIcon(),
                String.format("₹%.2f", pricePerNight),
                status + " " + getStatusIcon(),
                floor,
                capacity,
                getFormattedAmenities()
        };
    }

    // Static methods for room types
    public static String[] getAllRoomTypes() {
        return new String[] { TYPE_STANDARD, TYPE_DELUXE, TYPE_SUITE, TYPE_PRESIDENTIAL };
    }

    public static String[] getAllStatuses() {
        return new String[] { STATUS_AVAILABLE, STATUS_OCCUPIED, STATUS_MAINTENANCE, STATUS_RESERVED, STATUS_CLEANING };
    }

    public static double getBasePriceForType(String roomType) {
        if (roomType == null)
            return 0.00;
        switch (roomType.toLowerCase()) {
            case "standard":
                return 1500.00;
            case "deluxe":
                return 2500.00;
            case "suite":
                return 4500.00;
            case "presidential":
                return 10000.00;
            default:
                return 0.00;
        }
    }

    public static int getCapacityForType(String roomType) {
        if (roomType == null)
            return 2;
        switch (roomType.toLowerCase()) {
            case "standard":
                return 2;
            case "deluxe":
                return 3;
            case "suite":
                return 4;
            case "presidential":
                return 6;
            default:
                return 2;
        }
    }

    @Override
    public String toString() {
        return String.format("Room %s (%s) - ₹%.2f/night - %s",
                roomNumber, roomType, pricePerNight, status);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Room room = (Room) obj;
        return roomId == room.roomId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(roomId);
    }

    // Method to validate room data
    public boolean isValid() {
        return roomNumber != null && !roomNumber.trim().isEmpty()
                && roomType != null && !roomType.trim().isEmpty()
                && pricePerNight > 0
                && floor >= 0
                && capacity > 0;
    }

    // Method to get price range category
    public String getPriceCategory() {
        if (pricePerNight < 2000) {
            return "Budget";
        } else if (pricePerNight < 4000) {
            return "Moderate";
        } else if (pricePerNight < 8000) {
            return "Luxury";
        } else {
            return "Premium";
        }
    }
}