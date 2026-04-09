// models/User.java
package models;

import java.time.LocalDateTime;

public class User {
    // Private fields
    private int userId;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String phone;
    private String role;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;

    // Constructors
    public User() {
        this.status = "Active";
        this.role = "receptionist";
    }

    public User(String username, String password, String fullName, String email, String phone, String role) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.status = "Active";
    }

    public User(int userId, String username, String fullName, String email, String phone, String role) {
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.status = "Active";
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    // Helper methods
    public boolean isAdmin() {
        return role != null && role.equalsIgnoreCase("admin");
    }

    public boolean isManager() {
        return role != null && role.equalsIgnoreCase("manager");
    }

    public boolean isReceptionist() {
        return role != null && role.equalsIgnoreCase("receptionist");
    }

    public boolean isActive() {
        return "Active".equalsIgnoreCase(this.status);
    }

    @Override
    public String toString() {
        return (fullName != null ? fullName : "Unknown") + " (" +
                (username != null ? username : "N/A") + ") - " +
                (role != null ? role : "N/A");
    }

    // Method to get user display name
    public String getDisplayName() {
        return (fullName != null ? fullName : "Unknown") + " [" +
                (role != null ? role : "N/A") + "]";
    }

    // Method to validate user data
    public boolean isValid() {
        return username != null && !username.trim().isEmpty()
                && password != null && !password.trim().isEmpty()
                && fullName != null && !fullName.trim().isEmpty();
    }

    // Method to get role icon (for UI)
    public String getRoleIcon() {
        if (role == null)
            return "👤";
        switch (role.toLowerCase()) {
            case "admin":
                return "👑";
            case "manager":
                return "⭐";
            case "receptionist":
                return "📞";
            default:
                return "👤";
        }
    }

    // Method to get formatted user info (for display)
    public String getFormattedInfo() {
        StringBuilder info = new StringBuilder();
        info.append("User Information:\n");
        info.append("----------------\n");
        info.append("Name: ").append(fullName).append("\n");
        info.append("Username: ").append(username).append("\n");
        info.append("Role: ").append(role != null ? role : "N/A").append(" ").append(getRoleIcon()).append("\n");
        info.append("Email: ").append(email != null ? email : "Not provided").append("\n");
        info.append("Phone: ").append(phone != null ? phone : "Not provided").append("\n");
        info.append("Status: ").append(status).append("\n");
        return info.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        User user = (User) obj;
        return userId == user.userId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(userId);
    }
}