// ui/BookingPanel.java
package ui;

//import dao.BookingDAO;
//import dao.RoomDAO;
//import dao.GuestDAO;
import models.Booking;
import models.Room;
import models.Guest;
import utils.CustomTheme;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;

public class BookingPanel extends JPanel {

    private JTable bookingTable;
    private DefaultTableModel tableModel;
    // private BookingDAO bookingDAO;
    // private RoomDAO roomDAO;
    // private GuestDAO guestDAO;
    private CustomTheme.ModernTextField searchField;
    private JComboBox<String> statusFilterCombo;
    private JComboBox<String> dateFilterCombo;
    private JLabel statusLabel;
    private JLabel statsLabel;
    private JPanel detailPanel;
    private List<Booking> currentBookings;

    public BookingPanel() {
        // bookingDAO = new BookingDAO();
        // roomDAO = new RoomDAO();
        // guestDAO = new GuestDAO();
        currentBookings = new ArrayList<>();
        initComponents();
        loadBookings();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBackground(CustomTheme.BACKGROUND_COLOR);

        add(createHeaderPanel(), BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(createTablePanel());
        splitPane.setRightComponent(createDetailPanel());
        splitPane.setDividerLocation(900);
        splitPane.setDividerSize(5);
        splitPane.setBorder(null);

        add(splitPane, BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(15, 15));
        headerPanel.setBackground(CustomTheme.PANEL_HEADER_COLOR);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, CustomTheme.PRIMARY_COLOR),
                BorderFactory.createEmptyBorder(20, 20, 10, 20)));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titlePanel.setOpaque(false);

        JLabel titleIcon = new JLabel("📅");
        titleIcon.setFont(CustomTheme.getEmojiFont().deriveFont(32f));
        titlePanel.add(titleIcon);

        JLabel titleLabel = CustomTheme.createHeaderLabel("Booking Management");
        titlePanel.add(titleLabel);

        JLabel subtitleLabel = CustomTheme.createLabel("Manage reservations, check-ins, check-outs and track bookings");
        subtitleLabel.setFont(CustomTheme.SMALL_FONT);
        subtitleLabel.setForeground(CustomTheme.GRAY_COLOR);
        titlePanel.add(subtitleLabel);

        headerPanel.add(titlePanel, BorderLayout.WEST);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionPanel.setOpaque(false);

        dateFilterCombo = new JComboBox<>(
                new String[] { "All Bookings", "Today", "Tomorrow", "This Week", "This Month" });
        CustomTheme.styleComboBox(dateFilterCombo);
        dateFilterCombo.addActionListener(e -> filterByDate());
        actionPanel.add(dateFilterCombo);

        searchField = new CustomTheme.ModernTextField(15);
        searchField.setPlaceholder("Search bookings...");
        searchField.setPreferredSize(new Dimension(200, 40));
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                searchBookings();
            }
        });
        actionPanel.add(searchField);

        String[] statuses = { "All Statuses", "Pending", "Confirmed", "Checked-in", "Checked-out", "Cancelled" };
        statusFilterCombo = new JComboBox<>(statuses);
        CustomTheme.styleComboBox(statusFilterCombo);
        statusFilterCombo.addActionListener(e -> filterByStatus());
        actionPanel.add(statusFilterCombo);

        CustomTheme.ModernButton newBookingButton = new CustomTheme.ModernButton("➕ New Booking",
                CustomTheme.SUCCESS_COLOR);
        newBookingButton.addActionListener(e -> showNewBookingDialog());
        actionPanel.add(newBookingButton);

        CustomTheme.ModernButton refreshButton = new CustomTheme.ModernButton("🔄 Refresh", CustomTheme.PRIMARY_COLOR);
        refreshButton.addActionListener(e -> loadBookings());
        actionPanel.add(refreshButton);

        headerPanel.add(actionPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CustomTheme.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 10));

        String[] columns = { "ID", "Booking #", "Guest", "Room", "Check-in", "Check-out", "Nights", "Total", "Status",
                "Payment" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        bookingTable = new JTable(tableModel);
        CustomTheme.styleTable(bookingTable);

        TableColumnModel colModel = bookingTable.getColumnModel();
        colModel.getColumn(0).setMaxWidth(60);
        colModel.getColumn(1).setPreferredWidth(120);
        colModel.getColumn(2).setPreferredWidth(160);
        colModel.getColumn(3).setPreferredWidth(80);
        colModel.getColumn(4).setPreferredWidth(100);
        colModel.getColumn(5).setPreferredWidth(100);
        colModel.getColumn(6).setMaxWidth(70);
        colModel.getColumn(7).setPreferredWidth(120);
        colModel.getColumn(8).setPreferredWidth(120);
        colModel.getColumn(9).setPreferredWidth(100);

        bookingTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showBookingDetails();
            }
        });

        JScrollPane scrollPane = new JScrollPane(bookingTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(CustomTheme.LIGHT_COLOR));
        CustomTheme.styleScrollPane(scrollPane);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createDetailPanel() {
        detailPanel = new JPanel(new BorderLayout());
        detailPanel.setBackground(CustomTheme.CARD_BACKGROUND_COLOR);
        detailPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 1, 0, 0, CustomTheme.LIGHT_COLOR),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        JLabel initialLabel = CustomTheme.createHeaderLabel("Select a booking to view details");
        initialLabel.setHorizontalAlignment(SwingConstants.CENTER);
        initialLabel.setForeground(CustomTheme.GRAY_COLOR);
        detailPanel.add(initialLabel, BorderLayout.CENTER);

        return detailPanel;
    }

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(CustomTheme.BACKGROUND_COLOR);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        CustomTheme.ShadowPanel statsPanel = new CustomTheme.ShadowPanel(10, 2);
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 12));

        statsLabel = CustomTheme.createLabel("");
        statsPanel.add(statsLabel);

        bottomPanel.add(statsPanel, BorderLayout.WEST);

        statusLabel = CustomTheme.createLabel("Ready");
        statusLabel.setFont(CustomTheme.SMALL_FONT);
        statusLabel.setForeground(CustomTheme.GRAY_COLOR);
        bottomPanel.add(statusLabel, BorderLayout.EAST);

        return bottomPanel;
    }

    private void loadBookings() {
        // Create sample bookings for demo
        currentBookings = getSampleBookings();
        updateTableData(currentBookings);
        updateStatistics(currentBookings);
        statusLabel.setText("Loaded " + currentBookings.size() + " bookings");
    }

    private List<Booking> getSampleBookings() {
        List<Booking> bookings = new ArrayList<>();

        // Create sample guests
        Guest guest1 = new Guest("John", "Doe", "john@example.com", "9876543210", "NY", "ID123", "American");
        guest1.setGuestId(1);
        Guest guest2 = new Guest("Jane", "Smith", "jane@example.com", "9876543211", "LA", "ID456", "American");
        guest2.setGuestId(2);
        Guest guest3 = new Guest("Raj", "Patel", "raj@example.com", "9876543212", "Mumbai", "ID789", "Indian");
        guest3.setGuestId(3);

        // Create sample rooms
        Room room1 = new Room("101", "Standard", 1500.00, 1);
        room1.setRoomId(1);
        Room room2 = new Room("201", "Deluxe", 2500.00, 2);
        room2.setRoomId(2);
        Room room3 = new Room("301", "Suite", 4500.00, 3);
        room3.setRoomId(3);

        LocalDate today = LocalDate.now();

        // Booking 1 - Checked-in
        Booking b1 = new Booking();
        b1.setBookingId(1);
        b1.setBookingNumber("BK001");
        b1.setGuest(guest1);
        b1.setRoom(room1);
        b1.setCheckInDate(today);
        b1.setCheckOutDate(today.plusDays(3));
        b1.setNumberOfNights(3);
        b1.setNumberOfGuests(2);
        b1.setStatus("Checked-in");
        b1.setPaymentStatus("Paid");
        b1.setTotalAmount(4500.00);
        b1.setPaidAmount(4500.00);
        bookings.add(b1);

        // Booking 2 - Confirmed
        Booking b2 = new Booking();
        b2.setBookingId(2);
        b2.setBookingNumber("BK002");
        b2.setGuest(guest2);
        b2.setRoom(room2);
        b2.setCheckInDate(today.plusDays(1));
        b2.setCheckOutDate(today.plusDays(4));
        b2.setNumberOfNights(3);
        b2.setNumberOfGuests(2);
        b2.setStatus("Confirmed");
        b2.setPaymentStatus("Pending");
        b2.setTotalAmount(7500.00);
        b2.setPaidAmount(0);
        bookings.add(b2);

        // Booking 3 - Pending
        Booking b3 = new Booking();
        b3.setBookingId(3);
        b3.setBookingNumber("BK003");
        b3.setGuest(guest3);
        b3.setRoom(room3);
        b3.setCheckInDate(today.plusDays(2));
        b3.setCheckOutDate(today.plusDays(5));
        b3.setNumberOfNights(3);
        b3.setNumberOfGuests(2);
        b3.setStatus("Pending");
        b3.setPaymentStatus("Pending");
        b3.setTotalAmount(13500.00);
        b3.setPaidAmount(0);
        bookings.add(b3);

        // Booking 4 - Checked-out
        Booking b4 = new Booking();
        b4.setBookingId(4);
        b4.setBookingNumber("BK004");
        b4.setGuest(guest1);
        b4.setRoom(room1);
        b4.setCheckInDate(today.minusDays(5));
        b4.setCheckOutDate(today.minusDays(2));
        b4.setNumberOfNights(3);
        b4.setNumberOfGuests(2);
        b4.setStatus("Checked-out");
        b4.setPaymentStatus("Paid");
        b4.setTotalAmount(4500.00);
        b4.setPaidAmount(4500.00);
        bookings.add(b4);

        // Booking 5 - Cancelled
        Booking b5 = new Booking();
        b5.setBookingId(5);
        b5.setBookingNumber("BK005");
        b5.setGuest(guest2);
        b5.setRoom(room2);
        b5.setCheckInDate(today.minusDays(2));
        b5.setCheckOutDate(today.plusDays(1));
        b5.setNumberOfNights(3);
        b5.setNumberOfGuests(2);
        b5.setStatus("Cancelled");
        b5.setPaymentStatus("Refunded");
        b5.setTotalAmount(7500.00);
        b5.setPaidAmount(0);
        bookings.add(b5);

        return bookings;
    }

    private void updateTableData(List<Booking> bookings) {
        tableModel.setRowCount(0);
        for (Booking booking : bookings) {
            String guestName = booking.getGuest() != null
                    ? booking.getGuest().getFirstName() + " " + booking.getGuest().getLastName()
                    : "N/A";
            String roomNumber = booking.getRoom() != null ? booking.getRoom().getRoomNumber() : "N/A";

            tableModel.addRow(new Object[] {
                    booking.getBookingId(),
                    booking.getBookingNumber(),
                    guestName,
                    roomNumber,
                    booking.getCheckInDate() != null
                            ? booking.getCheckInDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                            : "N/A",
                    booking.getCheckOutDate() != null
                            ? booking.getCheckOutDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                            : "N/A",
                    booking.getNumberOfNights(),
                    String.format("₹%,.2f", booking.getTotalAmount()),
                    booking.getStatus(),
                    booking.getPaymentStatus()
            });
        }
    }

    private void updateStatistics(List<Booking> bookings) {
        long total = bookings.size();
        long confirmed = bookings.stream().filter(b -> "Confirmed".equals(b.getStatus())).count();
        long checkedIn = bookings.stream().filter(b -> "Checked-in".equals(b.getStatus())).count();
        long checkedOut = bookings.stream().filter(b -> "Checked-out".equals(b.getStatus())).count();
        long cancelled = bookings.stream().filter(b -> "Cancelled".equals(b.getStatus())).count();
        double totalRevenue = bookings.stream()
                .filter(b -> "Checked-out".equals(b.getStatus()))
                .mapToDouble(Booking::getTotalAmount).sum();

        String stats = String.format(
                "📊 Booking Statistics: Total: %d | ✅ Confirmed: %d | 🏨 Checked-in: %d | 🚪 Checked-out: %d | ❌ Cancelled: %d | 💰 Revenue: ₹%,.2f",
                total, confirmed, checkedIn, checkedOut, cancelled, totalRevenue);
        statsLabel.setText(stats);
        statsLabel.setForeground(CustomTheme.TEXT_COLOR);
    }

    private void filterByStatus() {
        String status = (String) statusFilterCombo.getSelectedItem();
        if (status == null || status.equals("All Statuses")) {
            loadBookings();
        } else {
            List<Booking> filtered = new ArrayList<>();
            for (Booking booking : currentBookings) {
                if (booking.getStatus().equals(status)) {
                    filtered.add(booking);
                }
            }
            updateTableData(filtered);
            statusLabel.setText("Showing " + filtered.size() + " " + status + " bookings");
        }
    }

    private void filterByDate() {
        String filter = (String) dateFilterCombo.getSelectedItem();
        LocalDate today = LocalDate.now();

        List<Booking> filtered = new ArrayList<>();
        for (Booking booking : currentBookings) {
            LocalDate checkIn = booking.getCheckInDate();
            if (checkIn == null)
                continue;

            switch (filter) {
                case "Today":
                    if (checkIn.equals(today))
                        filtered.add(booking);
                    break;
                case "Tomorrow":
                    if (checkIn.equals(today.plusDays(1)))
                        filtered.add(booking);
                    break;
                case "This Week":
                    if (!checkIn.isBefore(today) && !checkIn.isAfter(today.plusDays(7)))
                        filtered.add(booking);
                    break;
                case "This Month":
                    if (checkIn.getMonth() == today.getMonth() && checkIn.getYear() == today.getYear())
                        filtered.add(booking);
                    break;
                default:
                    filtered = currentBookings;
                    break;
            }
        }
        updateTableData(filtered);
        statusLabel.setText("Showing " + filtered.size() + " bookings for " + filter);
    }

    private void searchBookings() {
        String searchTerm = searchField.getText().trim().toLowerCase();
        if (searchTerm.isEmpty()) {
            loadBookings();
        } else {
            List<Booking> filtered = new ArrayList<>();
            for (Booking booking : currentBookings) {
                String guestName = booking.getGuest() != null ? booking.getGuest().getFirstName().toLowerCase() + " "
                        + booking.getGuest().getLastName().toLowerCase() : "";
                if (booking.getBookingNumber().toLowerCase().contains(searchTerm) ||
                        guestName.contains(searchTerm)) {
                    filtered.add(booking);
                }
            }
            updateTableData(filtered);
            statusLabel.setText("Found " + filtered.size() + " bookings matching '" + searchTerm + "'");
        }
    }

    private void showBookingDetails() {
        int selectedRow = bookingTable.getSelectedRow();
        if (selectedRow == -1)
            return;

        int bookingId = (int) tableModel.getValueAt(selectedRow, 0);
        Booking booking = null;
        for (Booking b : currentBookings) {
            if (b.getBookingId() == bookingId) {
                booking = b;
                break;
            }
        }

        if (booking != null) {
            updateDetailPanel(booking);
        }
    }

    private void updateDetailPanel(Booking booking) {
        detailPanel.removeAll();

        JPanel detailsCard = new JPanel();
        detailsCard.setLayout(new BoxLayout(detailsCard, BoxLayout.Y_AXIS));
        detailsCard.setBackground(Color.WHITE);
        detailsCard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JLabel titleLabel = new JLabel("Booking #" + booking.getBookingNumber());
        titleLabel.setFont(CustomTheme.HEADER_FONT);
        titleLabel.setForeground(CustomTheme.PRIMARY_COLOR);
        detailsCard.add(titleLabel);
        detailsCard.add(Box.createRigidArea(new Dimension(0, 15)));

        // Guest Info
        JPanel guestPanel = createInfoPanel("👤 Guest Information", new String[][] {
                { "Name",
                        booking.getGuest() != null
                                ? booking.getGuest().getFirstName() + " " + booking.getGuest().getLastName()
                                : "N/A" },
                { "Phone", booking.getGuest() != null ? booking.getGuest().getPhone() : "N/A" },
                { "Email", booking.getGuest() != null ? booking.getGuest().getEmail() : "N/A" }
        });
        detailsCard.add(guestPanel);
        detailsCard.add(Box.createRigidArea(new Dimension(0, 15)));

        // Room Info
        JPanel roomPanel = createInfoPanel("🏠 Room Information", new String[][] {
                { "Room Number", booking.getRoom() != null ? booking.getRoom().getRoomNumber() : "N/A" },
                { "Room Type", booking.getRoom() != null ? booking.getRoom().getRoomType() : "N/A" },
                { "Price/Night", String.format("₹%,.2f", booking.getRoomPricePerNight()) }
        });
        detailsCard.add(roomPanel);
        detailsCard.add(Box.createRigidArea(new Dimension(0, 15)));

        // Booking Details
        JPanel bookingPanel = createInfoPanel("📅 Booking Details", new String[][] {
                { "Check-in",
                        booking.getCheckInDate() != null
                                ? booking.getCheckInDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                                : "N/A" },
                { "Check-out",
                        booking.getCheckOutDate() != null
                                ? booking.getCheckOutDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                                : "N/A" },
                { "Nights", String.valueOf(booking.getNumberOfNights()) },
                { "Guests", String.valueOf(booking.getNumberOfGuests()) },
                { "Status", booking.getStatus() }
        });
        detailsCard.add(bookingPanel);
        detailsCard.add(Box.createRigidArea(new Dimension(0, 15)));

        // Payment Details
        JPanel paymentPanel = createInfoPanel("💰 Payment Details", new String[][] {
                { "Total Amount", String.format("₹%,.2f", booking.getTotalAmount()) },
                { "Paid Amount", String.format("₹%,.2f", booking.getPaidAmount()) },
                { "Due Amount", String.format("₹%,.2f", booking.getDueAmount()) },
                { "Payment Status", booking.getPaymentStatus() }
        });
        detailsCard.add(paymentPanel);
        detailsCard.add(Box.createRigidArea(new Dimension(0, 15)));

        // Action Buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        actionPanel.setBackground(Color.WHITE);

        if ("Confirmed".equals(booking.getStatus())) {
            CustomTheme.ModernButton checkInBtn = new CustomTheme.ModernButton("🏨 Check-in",
                    CustomTheme.SUCCESS_COLOR);
            checkInBtn.addActionListener(e -> {
                booking.setStatus("Checked-in");
                updateTableData(currentBookings);
                updateDetailPanel(booking);
                JOptionPane.showMessageDialog(this, "Check-in successful!");
            });
            actionPanel.add(checkInBtn);
        }

        if ("Checked-in".equals(booking.getStatus())) {
            CustomTheme.ModernButton checkOutBtn = new CustomTheme.ModernButton("🚪 Check-out",
                    CustomTheme.WARNING_COLOR);
            checkOutBtn.addActionListener(e -> {
                booking.setStatus("Checked-out");
                updateTableData(currentBookings);
                updateDetailPanel(booking);
                JOptionPane.showMessageDialog(this,
                        "Check-out successful!\nTotal: ₹" + String.format("%.2f", booking.getTotalAmount()));
            });
            actionPanel.add(checkOutBtn);
        }

        if (booking.getDueAmount() > 0) {
            CustomTheme.ModernButton paymentBtn = new CustomTheme.ModernButton("💰 Add Payment",
                    CustomTheme.PRIMARY_COLOR);
            paymentBtn.addActionListener(e -> {
                String amount = JOptionPane.showInputDialog(this, "Enter payment amount:", booking.getDueAmount());
                if (amount != null) {
                    try {
                        double amt = Double.parseDouble(amount);
                        booking.addPayment(amt);
                        updateTableData(currentBookings);
                        updateDetailPanel(booking);
                        JOptionPane.showMessageDialog(this, "Payment of ₹" + String.format("%.2f", amt) + " added!");
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "Invalid amount!");
                    }
                }
            });
            actionPanel.add(paymentBtn);
        }

        detailsCard.add(actionPanel);

        JScrollPane scrollPane = new JScrollPane(detailsCard);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);

        detailPanel.add(scrollPane, BorderLayout.CENTER);
        detailPanel.revalidate();
        detailPanel.repaint();
    }

    private JPanel createInfoPanel(String title, String[][] data) {
        CustomTheme.ShadowPanel panel = new CustomTheme.ShadowPanel(10, 2);
        panel.setBackground(Color.WHITE);
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = CustomTheme.createHeaderLabel(title);
        titleLabel.setForeground(CustomTheme.PRIMARY_COLOR);
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel gridPanel = new JPanel(new GridLayout(data.length, 2, 10, 8));
        gridPanel.setBackground(Color.WHITE);
        gridPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        for (String[] row : data) {
            JLabel keyLabel = CustomTheme.createLabel(row[0] + ":");
            keyLabel.setForeground(CustomTheme.GRAY_COLOR);
            JLabel valueLabel = CustomTheme.createLabel(row[1]);
            valueLabel.setForeground(CustomTheme.TEXT_COLOR);
            valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            gridPanel.add(keyLabel);
            gridPanel.add(valueLabel);
        }

        panel.add(gridPanel, BorderLayout.CENTER);
        return panel;
    }

    private void showNewBookingDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Create New Booking", true);
        dialog.setSize(550, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Guest Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(CustomTheme.createLabel("Guest Name:*"), gbc);
        gbc.gridx = 1;
        CustomTheme.ModernTextField guestField = new CustomTheme.ModernTextField(20);
        guestField.setPreferredSize(new Dimension(250, 40));
        panel.add(guestField, gbc);

        // Guest Phone
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(CustomTheme.createLabel("Guest Phone:*"), gbc);
        gbc.gridx = 1;
        CustomTheme.ModernTextField phoneField = new CustomTheme.ModernTextField(20);
        phoneField.setPreferredSize(new Dimension(250, 40));
        panel.add(phoneField, gbc);

        // Guest Email
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(CustomTheme.createLabel("Guest Email:"), gbc);
        gbc.gridx = 1;
        CustomTheme.ModernTextField emailField = new CustomTheme.ModernTextField(20);
        emailField.setPreferredSize(new Dimension(250, 40));
        panel.add(emailField, gbc);

        // Room Selection
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(CustomTheme.createLabel("Room Number:*"), gbc);
        gbc.gridx = 1;
        JComboBox<String> roomCombo = new JComboBox<>(
                new String[] { "101 - Standard (₹1500)", "102 - Standard (₹1500)", "103 - Standard (₹1500)",
                        "201 - Deluxe (₹2500)", "202 - Deluxe (₹2500)", "301 - Suite (₹4500)" });
        CustomTheme.styleComboBox(roomCombo);
        panel.add(roomCombo, gbc);

        // Check-in Date
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(CustomTheme.createLabel("Check-in Date:*"), gbc);
        gbc.gridx = 1;
        JTextField checkInField = new JTextField(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), 20);
        checkInField.setFont(CustomTheme.NORMAL_FONT);
        checkInField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CustomTheme.GRAY_COLOR),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        panel.add(checkInField, gbc);

        // Check-out Date
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(CustomTheme.createLabel("Check-out Date:*"), gbc);
        gbc.gridx = 1;
        JTextField checkOutField = new JTextField(
                LocalDate.now().plusDays(3).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), 20);
        checkOutField.setFont(CustomTheme.NORMAL_FONT);
        checkOutField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CustomTheme.GRAY_COLOR),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        panel.add(checkOutField, gbc);

        // Number of Guests
        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(CustomTheme.createLabel("Number of Guests:*"), gbc);
        gbc.gridx = 1;
        JSpinner guestsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        guestsSpinner.setFont(CustomTheme.NORMAL_FONT);
        panel.add(guestsSpinner, gbc);

        // Buttons
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(Color.WHITE);

        CustomTheme.ModernButton createButton = new CustomTheme.ModernButton("Create Booking",
                CustomTheme.SUCCESS_COLOR);
        createButton.addActionListener(e -> {
            if (guestField.getText().trim().isEmpty() || phoneField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please enter guest name and phone number!");
                return;
            }

            // Validate dates
            LocalDate checkIn, checkOut;
            try {
                checkIn = LocalDate.parse(checkInField.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                checkOut = LocalDate.parse(checkOutField.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                if (checkOut.isBefore(checkIn) || checkOut.equals(checkIn)) {
                    JOptionPane.showMessageDialog(dialog, "Check-out date must be after check-in date!");
                    return;
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter valid dates in dd/MM/yyyy format!");
                return;
            }

            // Calculate nights from dates
            int nights = (int) java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut);

            // Create new booking
            String roomSelection = (String) roomCombo.getSelectedItem();
            String roomNum = roomSelection.split(" ")[0];
            double price = Double.parseDouble(roomSelection.split("₹")[1].replace(")", ""));

            Guest newGuest = new Guest();
            newGuest.setFirstName(guestField.getText().trim().split(" ")[0]);
            newGuest.setLastName(
                    guestField.getText().trim().split(" ").length > 1 ? guestField.getText().trim().split(" ")[1] : "");
            newGuest.setPhone(phoneField.getText().trim());
            newGuest.setEmail(emailField.getText().trim());

            Room newRoom = new Room();
            newRoom.setRoomNumber(roomNum);
            newRoom.setRoomType(roomSelection.contains("Standard") ? "Standard"
                    : roomSelection.contains("Deluxe") ? "Deluxe" : "Suite");
            newRoom.setPricePerNight(price);

            Booking newBooking = new Booking();
            newBooking.setBookingId(currentBookings.size() + 1);
            newBooking.setBookingNumber("BK" + String.format("%03d", currentBookings.size() + 1));
            newBooking.setGuest(newGuest);
            newBooking.setRoom(newRoom);
            newBooking.setCheckInDate(checkIn);
            newBooking.setCheckOutDate(checkOut);
            newBooking.setNumberOfNights(nights);
            newBooking.setNumberOfGuests((Integer) guestsSpinner.getValue());
            newBooking.setStatus("Pending");
            newBooking.setPaymentStatus("Pending");
            newBooking.setTotalAmount(price * nights);
            newBooking.setPaidAmount(0);
            newBooking.setDueAmount(price * nights);

            currentBookings.add(newBooking);
            updateTableData(currentBookings);
            updateStatistics(currentBookings);

            JOptionPane.showMessageDialog(dialog,
                    "Booking created successfully!\nBooking #: " + newBooking.getBookingNumber());
            dialog.dispose();
        });

        CustomTheme.ModernButton cancelButton = new CustomTheme.ModernButton("Cancel", CustomTheme.DANGER_COLOR);
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(createButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel, gbc);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }
}