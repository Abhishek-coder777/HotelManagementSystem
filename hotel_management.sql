-- ======================================================
-- Hotel Management System Database Schema
-- Database: hotel_management
-- ======================================================

-- Create and use the database
DROP DATABASE IF EXISTS hotel_management;
CREATE DATABASE hotel_management;
USE hotel_management;

-- ======================================================
-- TABLE: guests
-- ======================================================
CREATE TABLE guests (
    guest_id INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(20) UNIQUE,
    address TEXT,
    city VARCHAR(50),
    state VARCHAR(50),
    zip_code VARCHAR(20),
    country VARCHAR(50),
    id_proof_type VARCHAR(50),
    id_proof_number VARCHAR(100) UNIQUE,
    nationality VARCHAR(50),
    gender ENUM('Male', 'Female', 'Other'),
    date_of_birth DATE,
    occupation VARCHAR(100),
    company_name VARCHAR(100),
    emergency_contact_name VARCHAR(100),
    emergency_contact_phone VARCHAR(20),
    emergency_contact_relation VARCHAR(50),
    preferences TEXT,
    profile_image_path VARCHAR(500),
    notes TEXT,
    is_vip BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    loyalty_tier ENUM('Bronze', 'Silver', 'Gold', 'Platinum') DEFAULT 'Bronze',
    total_stays INT DEFAULT 0,
    total_spent DECIMAL(10,2) DEFAULT 0.00,
    last_visit DATETIME,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_guest_name (first_name, last_name),
    INDEX idx_guest_email (email),
    INDEX idx_guest_phone (phone),
    INDEX idx_guest_nationality (nationality),
    INDEX idx_guest_loyalty_tier (loyalty_tier)
);

-- ======================================================
-- TABLE: rooms
-- ======================================================
CREATE TABLE rooms (
    room_id INT PRIMARY KEY AUTO_INCREMENT,
    room_number VARCHAR(10) NOT NULL UNIQUE,
    room_type ENUM('Standard', 'Deluxe', 'Suite', 'Presidential') NOT NULL,
    price_per_night DECIMAL(10,2) NOT NULL,
    status ENUM('Available', 'Occupied', 'Maintenance', 'Reserved', 'Cleaning') DEFAULT 'Available',
    floor INT NOT NULL,
    description TEXT,
    amenities TEXT,
    capacity INT DEFAULT 2,
    area DECIMAL(8,2),
    bed_type VARCHAR(50),
    has_wifi BOOLEAN DEFAULT TRUE,
    has_tv BOOLEAN DEFAULT TRUE,
    has_ac BOOLEAN DEFAULT TRUE,
    has_attached_bathroom BOOLEAN DEFAULT TRUE,
    room_view VARCHAR(100),
    image_path VARCHAR(500),
    rating DECIMAL(3,2) DEFAULT 0.00,
    total_bookings INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_room_number (room_number),
    INDEX idx_room_type (room_type),
    INDEX idx_room_status (status),
    INDEX idx_room_floor (floor),
    INDEX idx_room_price (price_per_night)
);

-- ======================================================
-- TABLE: users
-- ======================================================
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(20),
    role ENUM('admin', 'manager', 'receptionist') DEFAULT 'receptionist',
    status ENUM('Active', 'Inactive') DEFAULT 'Active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login DATETIME,
    INDEX idx_username (username),
    INDEX idx_user_role (role),
    INDEX idx_user_status (status)
);

-- ======================================================
-- TABLE: bookings
-- ======================================================
CREATE TABLE bookings (
    booking_id INT PRIMARY KEY AUTO_INCREMENT,
    guest_id INT NOT NULL,
    room_id INT NOT NULL,
    booking_number VARCHAR(20) NOT NULL UNIQUE,
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    check_in_time DATETIME,
    check_out_time DATETIME,
    number_of_nights INT NOT NULL,
    number_of_guests INT DEFAULT 1,
    room_price_per_night DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    tax_amount DECIMAL(10,2) DEFAULT 0.00,
    discount_amount DECIMAL(10,2) DEFAULT 0.00,
    total_amount DECIMAL(10,2) NOT NULL,
    paid_amount DECIMAL(10,2) DEFAULT 0.00,
    due_amount DECIMAL(10,2) DEFAULT 0.00,
    status ENUM('Pending', 'Confirmed', 'Checked-in', 'Checked-out', 'Cancelled', 'No-Show', 'Completed') DEFAULT 'Pending',
    payment_status ENUM('Pending', 'Partial', 'Paid', 'Refunded') DEFAULT 'Pending',
    booking_source ENUM('Direct', 'Phone', 'Website', 'Travel Aggregator', 'Corporate', 'Walk-in'),
    special_requests TEXT,
    cancellation_reason TEXT,
    cancellation_date DATETIME,
    cancellation_fee DECIMAL(10,2) DEFAULT 0.00,
    booking_notes TEXT,
    booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by INT,
    modified_by INT,
    FOREIGN KEY (guest_id) REFERENCES guests(guest_id) ON DELETE RESTRICT,
    FOREIGN KEY (room_id) REFERENCES rooms(room_id) ON DELETE RESTRICT,
    FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE SET NULL,
    FOREIGN KEY (modified_by) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_booking_number (booking_number),
    INDEX idx_booking_guest (guest_id),
    INDEX idx_booking_room (room_id),
    INDEX idx_booking_dates (check_in_date, check_out_date),
    INDEX idx_booking_status (status),
    INDEX idx_booking_payment_status (payment_status),
    INDEX idx_booking_date (booking_date)
);

-- ======================================================
-- TABLE: payments (Additional payment tracking)
-- ======================================================
CREATE TABLE payments (
    payment_id INT PRIMARY KEY AUTO_INCREMENT,
    booking_id INT NOT NULL,
    user_id INT,
    amount DECIMAL(10,2) NOT NULL,
    payment_method ENUM('Cash', 'Credit Card', 'Debit Card', 'UPI', 'Bank Transfer', 'Other') NOT NULL,
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    transaction_id VARCHAR(100),
    reference_number VARCHAR(100),
    notes TEXT,
    status ENUM('Pending', 'Completed', 'Failed', 'Refunded') DEFAULT 'Completed',
    FOREIGN KEY (booking_id) REFERENCES bookings(booking_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_payment_booking (booking_id),
    INDEX idx_payment_date (payment_date)
);

-- ======================================================
-- TABLE: invoices
-- ======================================================
CREATE TABLE invoices (
    invoice_id INT PRIMARY KEY AUTO_INCREMENT,
    booking_id INT NOT NULL,
    invoice_number VARCHAR(20) NOT NULL UNIQUE,
    invoice_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    subtotal DECIMAL(10,2) NOT NULL,
    tax_amount DECIMAL(10,2) NOT NULL,
    discount_amount DECIMAL(10,2) DEFAULT 0.00,
    total_amount DECIMAL(10,2) NOT NULL,
    paid_amount DECIMAL(10,2) DEFAULT 0.00,
    due_amount DECIMAL(10,2) DEFAULT 0.00,
    invoice_status ENUM('Draft', 'Issued', 'Paid', 'Cancelled') DEFAULT 'Issued',
    pdf_path VARCHAR(500),
    notes TEXT,
    FOREIGN KEY (booking_id) REFERENCES bookings(booking_id) ON DELETE CASCADE,
    INDEX idx_invoice_number (invoice_number),
    INDEX idx_invoice_booking (booking_id)
);

-- ======================================================
-- TABLE: room_maintenance
-- ======================================================
CREATE TABLE room_maintenance (
    maintenance_id INT PRIMARY KEY AUTO_INCREMENT,
    room_id INT NOT NULL,
    maintenance_type VARCHAR(100) NOT NULL,
    description TEXT,
    start_date DATE NOT NULL,
    end_date DATE,
    priority ENUM('Low', 'Medium', 'High', 'Urgent') DEFAULT 'Medium',
    status ENUM('Scheduled', 'In Progress', 'Completed', 'Cancelled') DEFAULT 'Scheduled',
    assigned_to INT,
    cost DECIMAL(10,2),
    completed_by INT,
    completed_date DATETIME,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (room_id) REFERENCES rooms(room_id) ON DELETE CASCADE,
    FOREIGN KEY (assigned_to) REFERENCES users(user_id) ON DELETE SET NULL,
    FOREIGN KEY (completed_by) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_maintenance_room (room_id),
    INDEX idx_maintenance_dates (start_date, end_date),
    INDEX idx_maintenance_status (status)
);

-- ======================================================
-- TABLE: guest_feedback
-- ======================================================
CREATE TABLE guest_feedback (
    feedback_id INT PRIMARY KEY AUTO_INCREMENT,
    guest_id INT NOT NULL,
    booking_id INT,
    rating INT CHECK (rating BETWEEN 1 AND 5),
    cleanliness_rating INT CHECK (cleanliness_rating BETWEEN 1 AND 5),
    service_rating INT CHECK (service_rating BETWEEN 1 AND 5),
    amenities_rating INT CHECK (amenities_rating BETWEEN 1 AND 5),
    value_rating INT CHECK (value_rating BETWEEN 1 AND 5),
    comment TEXT,
    suggestion TEXT,
    response TEXT,
    responded_by INT,
    responded_date DATETIME,
    is_public BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (guest_id) REFERENCES guests(guest_id) ON DELETE CASCADE,
    FOREIGN KEY (booking_id) REFERENCES bookings(booking_id) ON DELETE SET NULL,
    FOREIGN KEY (responded_by) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_feedback_guest (guest_id),
    INDEX idx_feedback_rating (rating),
    INDEX idx_feedback_date (created_at)
);

-- ======================================================
-- TABLE: audit_log
-- ======================================================
CREATE TABLE audit_log (
    log_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    action VARCHAR(50) NOT NULL,
    table_name VARCHAR(50),
    record_id INT,
    old_data TEXT,
    new_data TEXT,
    ip_address VARCHAR(45),
    user_agent VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_audit_user (user_id),
    INDEX idx_audit_table (table_name),
    INDEX idx_audit_action (action),
    INDEX idx_audit_date (created_at)
);

-- ======================================================
-- Insert Default Data
-- ======================================================

-- Insert default admin user (password: admin123)
INSERT INTO users (username, password, full_name, email, phone, role, status) VALUES
('admin', 'admin123', 'System Administrator', 'admin@hotel.com', '9999999999', 'admin', 'Active');

-- Insert sample room types
INSERT INTO rooms (room_number, room_type, price_per_night, status, floor, capacity, bed_type, has_wifi, has_tv, has_ac) VALUES
('101', 'Standard', 1500.00, 'Available', 1, 2, 'Queen Bed', TRUE, TRUE, TRUE),
('102', 'Standard', 1500.00, 'Available', 1, 2, 'Queen Bed', TRUE, TRUE, TRUE),
('103', 'Standard', 1500.00, 'Available', 1, 2, 'Queen Bed', TRUE, TRUE, TRUE),
('104', 'Standard', 1500.00, 'Available', 1, 2, 'Queen Bed', TRUE, TRUE, TRUE),
('201', 'Deluxe', 2500.00, 'Available', 2, 3, 'King Bed', TRUE, TRUE, TRUE),
('202', 'Deluxe', 2500.00, 'Available', 2, 3, 'King Bed', TRUE, TRUE, TRUE),
('203', 'Deluxe', 2500.00, 'Available', 2, 3, 'King Bed', TRUE, TRUE, TRUE),
('301', 'Suite', 4500.00, 'Available', 3, 4, 'King Bed', TRUE, TRUE, TRUE),
('302', 'Suite', 4500.00, 'Available', 3, 4, 'King Bed', TRUE, TRUE, TRUE),
('401', 'Presidential', 10000.00, 'Available', 4, 6, 'Emperor Bed', TRUE, TRUE, TRUE);

-- Insert sample guests
INSERT INTO guests (first_name, last_name, email, phone, address, nationality, is_active) VALUES
('John', 'Doe', 'john.doe@example.com', '9876543210', '123 Main Street, New York, NY 10001', 'American', TRUE),
('Jane', 'Smith', 'jane.smith@example.com', '9876543211', '456 Oak Avenue, Los Angeles, CA 90001', 'American', TRUE),
('Raj', 'Patel', 'raj.patel@example.com', '9876543212', '789 Gandhi Road, Mumbai 400001', 'Indian', TRUE),
('Maria', 'Garcia', 'maria.garcia@example.com', '9876543213', '321 Prado Street, Madrid 28001', 'Spanish', TRUE),
('Chen', 'Wei', 'chen.wei@example.com', '9876543214', '567 Nanjing Road, Shanghai 200001', 'Chinese', TRUE);

-- ======================================================
-- Views for Reporting
-- ======================================================

-- View: Current occupancy
CREATE VIEW current_occupancy AS
SELECT 
    r.room_id,
    r.room_number,
    r.room_type,
    r.floor,
    b.booking_id,
    b.check_in_date,
    b.check_out_date,
    CONCAT(g.first_name, ' ', g.last_name) AS guest_name,
    DATEDIFF(b.check_out_date, CURDATE()) AS days_remaining
FROM rooms r
LEFT JOIN bookings b ON r.room_id = b.room_id 
    AND b.status = 'Checked-in'
LEFT JOIN guests g ON b.guest_id = g.guest_id
WHERE r.status = 'Occupied';

-- View: Daily revenue summary
CREATE VIEW daily_revenue_summary AS
SELECT 
    DATE(payment_date) AS date,
    COUNT(*) AS transaction_count,
    SUM(amount) AS total_amount,
    payment_method
FROM payments
WHERE status = 'Completed'
GROUP BY DATE(payment_date), payment_method
ORDER BY DATE(payment_date) DESC;

-- View: Booking statistics by room type
CREATE VIEW booking_stats_by_room_type AS
SELECT 
    r.room_type,
    COUNT(b.booking_id) AS total_bookings,
    SUM(b.total_amount) AS total_revenue,
    AVG(b.total_amount) AS avg_booking_value,
    AVG(b.number_of_nights) AS avg_stay_duration,
    COUNT(CASE WHEN b.status = 'Checked-out' THEN 1 END) AS completed_bookings,
    COUNT(CASE WHEN b.status = 'Cancelled' THEN 1 END) AS cancelled_bookings
FROM rooms r
LEFT JOIN bookings b ON r.room_id = b.room_id
GROUP BY r.room_type;

-- View: Monthly performance
CREATE VIEW monthly_performance AS
SELECT 
    YEAR(booking_date) AS year,
    MONTH(booking_date) AS month,
    COUNT(*) AS total_bookings,
    SUM(total_amount) AS total_revenue,
    AVG(total_amount) AS avg_booking_value,
    COUNT(CASE WHEN status = 'Cancelled' THEN 1 END) AS cancellations,
    ROUND(COUNT(CASE WHEN status = 'Cancelled' THEN 1 END) * 100.0 / COUNT(*), 2) AS cancellation_rate
FROM bookings
WHERE status != 'Pending'
GROUP BY YEAR(booking_date), MONTH(booking_date)
ORDER BY year DESC, month DESC;

-- View: Guest loyalty summary
CREATE VIEW guest_loyalty_summary AS
SELECT 
    loyalty_tier,
    COUNT(*) AS guest_count,
    AVG(total_stays) AS avg_stays,
    AVG(total_spent) AS avg_spent,
    SUM(total_spent) AS total_revenue
FROM guests
WHERE is_active = TRUE
GROUP BY loyalty_tier
ORDER BY 
    CASE loyalty_tier
        WHEN 'Platinum' THEN 1
        WHEN 'Gold' THEN 2
        WHEN 'Silver' THEN 3
        WHEN 'Bronze' THEN 4
    END;

-- ======================================================
-- Stored Procedures
-- ======================================================

DELIMITER //

-- Procedure: Check room availability
CREATE PROCEDURE check_room_availability(
    IN p_check_in DATE,
    IN p_check_out DATE
)
BEGIN
    SELECT 
        r.room_id,
        r.room_number,
        r.room_type,
        r.price_per_night,
        r.floor,
        r.capacity
    FROM rooms r
    WHERE r.status = 'Available'
        AND NOT EXISTS (
            SELECT 1 FROM bookings b
            WHERE b.room_id = r.room_id
                AND b.status NOT IN ('Cancelled', 'Checked-out')
                AND b.check_in_date < p_check_out
                AND b.check_out_date > p_check_in
        )
    ORDER BY r.price_per_night;
END //

-- Procedure: Generate monthly report
CREATE PROCEDURE generate_monthly_report(
    IN p_year INT,
    IN p_month INT
)
BEGIN
    SELECT 
        'Bookings' AS report_section,
        COUNT(*) AS total,
        SUM(total_amount) AS amount,
        AVG(total_amount) AS average
    FROM bookings
    WHERE YEAR(booking_date) = p_year AND MONTH(booking_date) = p_month;
    
    SELECT 
        'Revenue by Room Type' AS report_section,
        r.room_type,
        COUNT(b.booking_id) AS bookings,
        SUM(b.total_amount) AS revenue
    FROM bookings b
    JOIN rooms r ON b.room_id = r.room_id
    WHERE YEAR(b.booking_date) = p_year AND MONTH(b.booking_date) = p_month
        AND b.status IN ('Checked-out', 'Completed')
    GROUP BY r.room_type;
    
    SELECT 
        'Cancellations' AS report_section,
        COUNT(*) AS cancelled_bookings,
        SUM(cancellation_fee) AS total_fees,
        AVG(cancellation_fee) AS avg_fee
    FROM bookings
    WHERE YEAR(cancellation_date) = p_year AND MONTH(cancellation_date) = p_month
        AND status = 'Cancelled';
END //

-- Procedure: Update loyalty tier based on spending
CREATE PROCEDURE update_loyalty_tiers()
BEGIN
    UPDATE guests
    SET loyalty_tier = CASE
        WHEN total_spent >= 100000 THEN 'Platinum'
        WHEN total_spent >= 50000 THEN 'Gold'
        WHEN total_spent >= 20000 THEN 'Silver'
        WHEN total_spent >= 5000 THEN 'Bronze'
        ELSE loyalty_tier
    END
    WHERE is_active = TRUE;
END //

DELIMITER ;

-- ======================================================
-- Triggers
-- ======================================================

DELIMITER //

-- Trigger: Update room status when booking is created
CREATE TRIGGER after_booking_insert
AFTER INSERT ON bookings
FOR EACH ROW
BEGIN
    IF NEW.status IN ('Confirmed', 'Pending') THEN
        UPDATE rooms SET status = 'Reserved' WHERE room_id = NEW.room_id;
    END IF;
END //

-- Trigger: Update room status when booking status changes
CREATE TRIGGER after_booking_status_update
AFTER UPDATE ON bookings
FOR EACH ROW
BEGIN
    IF NEW.status = 'Checked-in' AND OLD.status != 'Checked-in' THEN
        UPDATE rooms SET status = 'Occupied' WHERE room_id = NEW.room_id;
    ELSEIF NEW.status IN ('Checked-out', 'Cancelled') AND OLD.status NOT IN ('Checked-out', 'Cancelled') THEN
        UPDATE rooms SET status = 'Available' WHERE room_id = NEW.room_id;
    ELSEIF NEW.status = 'Confirmed' AND OLD.status != 'Confirmed' THEN
        UPDATE rooms SET status = 'Reserved' WHERE room_id = NEW.room_id;
    END IF;
END //

-- Trigger: Update guest total spending on checkout
CREATE TRIGGER after_booking_checkout
AFTER UPDATE ON bookings
FOR EACH ROW
BEGIN
    IF NEW.status = 'Checked-out' AND OLD.status != 'Checked-out' THEN
        UPDATE guests 
        SET total_stays = total_stays + 1,
            total_spent = total_spent + NEW.total_amount,
            last_visit = NOW()
        WHERE guest_id = NEW.guest_id;
        
        -- Update room total bookings
        UPDATE rooms 
        SET total_bookings = total_bookings + 1 
        WHERE room_id = NEW.room_id;
    END IF;
END //

-- Trigger: Update due amount when payment is added
CREATE TRIGGER after_payment_insert
AFTER INSERT ON payments
FOR EACH ROW
BEGIN
    IF NEW.status = 'Completed' THEN
        UPDATE bookings 
        SET paid_amount = paid_amount + NEW.amount,
            due_amount = total_amount - (paid_amount + NEW.amount)
        WHERE booking_id = NEW.booking_id;
        
        -- Update payment status
        UPDATE bookings 
        SET payment_status = CASE
            WHEN (paid_amount + NEW.amount) >= total_amount THEN 'Paid'
            WHEN (paid_amount + NEW.amount) > 0 THEN 'Partial'
            ELSE 'Pending'
        END
        WHERE booking_id = NEW.booking_id;
    END IF;
END //

-- Trigger: Audit log for bookings
CREATE TRIGGER audit_booking_update
AFTER UPDATE ON bookings
FOR EACH ROW
BEGIN
    INSERT INTO audit_log (user_id, action, table_name, record_id, old_data, new_data)
    VALUES (
        NEW.modified_by,
        'UPDATE',
        'bookings',
        NEW.booking_id,
        CONCAT('status:', OLD.status, ',payment:', OLD.payment_status),
        CONCAT('status:', NEW.status, ',payment:', NEW.payment_status)
    );
END //

DELIMITER ;

-- ======================================================
-- Indexes for Performance Optimization
-- ======================================================

-- Additional indexes for better query performance
CREATE INDEX idx_bookings_dates_status ON bookings(check_in_date, check_out_date, status);
CREATE INDEX idx_bookings_guest_status ON bookings(guest_id, status);
CREATE INDEX idx_payments_booking_status ON payments(booking_id, status);
CREATE INDEX idx_guests_last_visit ON guests(last_visit);
CREATE INDEX idx_rooms_status_type ON rooms(status, room_type);

-- ======================================================
-- Sample Queries for Testing
-- ======================================================

-- Get available rooms for specific dates
-- CALL check_room_availability('2024-12-01', '2024-12-05');

-- Get current occupancy
-- SELECT * FROM current_occupancy;

-- Get monthly performance
-- SELECT * FROM monthly_performance LIMIT 6;

-- Get top spending guests
-- SELECT first_name, last_name, total_spent, total_stays, loyalty_tier 
-- FROM guests ORDER BY total_spent DESC LIMIT 10;

-- Get revenue summary by room type
-- SELECT * FROM booking_stats_by_room_type;

-- ======================================================
-- End of Database Schema
-- ======================================================