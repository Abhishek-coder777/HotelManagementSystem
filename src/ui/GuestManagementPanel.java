// ui/GuestManagementPanel.java
package ui;

import models.Guest;
import models.Booking;
import utils.CustomTheme;
import utils.DataManager;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;

public class GuestManagementPanel extends JPanel {

    private JTable guestTable;
    private DefaultTableModel tableModel;
    private DataManager dataManager;
    private CustomTheme.ModernTextField searchField;
    private JComboBox<String> filterComboBox;
    private JLabel statusLabel;
    private JLabel statsLabel;
    private JPanel detailPanel;
    private List<Guest> currentGuests;

    public GuestManagementPanel() {
        dataManager = DataManager.getInstance();
        initComponents();
        loadGuests();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBackground(CustomTheme.BACKGROUND_COLOR);

        add(createHeaderPanel(), BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(createTablePanel());
        splitPane.setRightComponent(createDetailPanel());
        splitPane.setDividerLocation(850);
        splitPane.setDividerSize(5);
        splitPane.setBorder(null);

        add(splitPane, BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(CustomTheme.PANEL_HEADER_COLOR);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, CustomTheme.PRIMARY_COLOR),
                BorderFactory.createEmptyBorder(20, 20, 15, 20)));

        // Left side - Title and subtitle
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);

        // Title row
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setOpaque(false);

        JLabel titleIcon = new JLabel("👥");
        titleIcon.setFont(CustomTheme.getEmojiFont().deriveFont(32f));
        titlePanel.add(titleIcon);
        titlePanel.add(Box.createRigidArea(new Dimension(10, 0)));

        JLabel titleLabel = CustomTheme.createHeaderLabel("Guest Management");
        titlePanel.add(titleLabel);

        leftPanel.add(titlePanel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 8)));

        // Subtitle - directly below title
        JLabel subtitleLabel = CustomTheme
                .createLabel("Manage guest information, view profiles and track stay history");
        subtitleLabel.setFont(CustomTheme.SMALL_FONT);
        subtitleLabel.setForeground(CustomTheme.GRAY_COLOR);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(subtitleLabel);

        headerPanel.add(leftPanel, BorderLayout.WEST);

        // Right side - Search, filter and buttons (NO BOXES)
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);

        // Search field
        searchField = new CustomTheme.ModernTextField(15);
        searchField.setPlaceholder("Search guests...");
        searchField.setPreferredSize(new Dimension(200, 38));
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                searchGuests();
            }
        });
        rightPanel.add(searchField);

        // Filter combo
        filterComboBox = new JComboBox<>(new String[] { "All Guests", "Active Guests" });
        filterComboBox.setFont(CustomTheme.NORMAL_FONT);
        filterComboBox.setBackground(Color.WHITE);
        filterComboBox.setForeground(CustomTheme.TEXT_COLOR);
        filterComboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        filterComboBox.setPreferredSize(new Dimension(130, 38));
        filterComboBox.addActionListener(e -> filterGuests());
        rightPanel.add(filterComboBox);

        // Add Guest button - NO BOX
        JButton addButton = new JButton("➕ Add Guest");
        addButton.setFont(CustomTheme.BUTTON_FONT);
        addButton.setBackground(CustomTheme.SUCCESS_COLOR);
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setBorderPainted(false);
        addButton.setContentAreaFilled(true);
        addButton.setOpaque(true);
        addButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addButton.setPreferredSize(new Dimension(130, 38));
        addButton.addActionListener(e -> showAddGuestDialog());
        rightPanel.add(addButton);

        // Refresh button - NO BOX
        JButton refreshButton = new JButton("🔄 Refresh");
        refreshButton.setFont(CustomTheme.BUTTON_FONT);
        refreshButton.setBackground(CustomTheme.PRIMARY_COLOR);
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.setBorderPainted(false);
        refreshButton.setContentAreaFilled(true);
        refreshButton.setOpaque(true);
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshButton.setPreferredSize(new Dimension(110, 38));
        refreshButton.addActionListener(e -> loadGuests());
        rightPanel.add(refreshButton);

        headerPanel.add(rightPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CustomTheme.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 10));

        String[] columns = { "ID", "Name", "Phone", "Email", "Nationality", "Status" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        guestTable = new JTable(tableModel);
        CustomTheme.styleTable(guestTable);

        guestTable.getColumnModel().getColumn(0).setMaxWidth(60);
        guestTable.getColumnModel().getColumn(1).setPreferredWidth(180);
        guestTable.getColumnModel().getColumn(2).setPreferredWidth(130);
        guestTable.getColumnModel().getColumn(3).setPreferredWidth(200);
        guestTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        guestTable.getColumnModel().getColumn(5).setMaxWidth(100);

        guestTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showGuestDetails();
            }
        });

        JScrollPane scrollPane = new JScrollPane(guestTable);
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

        JLabel initialLabel = CustomTheme.createHeaderLabel("Select a guest to view details");
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

    private void loadGuests() {
        currentGuests = dataManager.getAllGuests();
        updateTableData(currentGuests);
        updateStatistics(currentGuests);
        statusLabel.setText("Loaded " + currentGuests.size() + " guests");
    }

    private void updateTableData(List<Guest> guests) {
        tableModel.setRowCount(0);
        for (Guest guest : guests) {
            tableModel.addRow(new Object[] {
                    guest.getGuestId(),
                    guest.getFirstName() + " " + guest.getLastName(),
                    guest.getPhone() != null && !guest.getPhone().isEmpty() ? guest.getPhone() : "-",
                    guest.getEmail() != null && !guest.getEmail().isEmpty() ? guest.getEmail() : "-",
                    guest.getNationality() != null && !guest.getNationality().isEmpty() ? guest.getNationality() : "-",
                    "Active"
            });
        }
    }

    private void updateStatistics(List<Guest> guests) {
        int total = guests.size();
        String stats = String.format("📊 Guest Statistics: Total Guests: %d", total);
        statsLabel.setText(stats);
        statsLabel.setForeground(CustomTheme.TEXT_COLOR);
    }

    private void filterGuests() {
        String filter = (String) filterComboBox.getSelectedItem();
        if (filter == null || filter.equals("All Guests")) {
            loadGuests();
        } else if (filter.equals("Active Guests")) {
            List<Guest> filtered = new ArrayList<>();
            for (Guest guest : currentGuests) {
                filtered.add(guest);
            }
            updateTableData(filtered);
            statusLabel.setText("Showing " + filtered.size() + " active guests");
        }
    }

    private void searchGuests() {
        String searchTerm = searchField.getText().trim().toLowerCase();
        if (searchTerm.isEmpty()) {
            loadGuests();
        } else {
            List<Guest> filtered = new ArrayList<>();
            for (Guest guest : currentGuests) {
                String fullName = (guest.getFirstName() + " " + guest.getLastName()).toLowerCase();
                String phone = guest.getPhone() != null ? guest.getPhone().toLowerCase() : "";
                if (fullName.contains(searchTerm) || phone.contains(searchTerm)) {
                    filtered.add(guest);
                }
            }
            updateTableData(filtered);
            statusLabel.setText("Found " + filtered.size() + " guests matching '" + searchTerm + "'");
        }
    }

    private void showGuestDetails() {
        int selectedRow = guestTable.getSelectedRow();
        if (selectedRow == -1)
            return;

        int guestId = (int) tableModel.getValueAt(selectedRow, 0);
        Guest guest = dataManager.getGuestById(guestId);

        if (guest != null) {
            updateDetailPanel(guest);
        }
    }

    private void updateDetailPanel(Guest guest) {
        detailPanel.removeAll();

        JPanel detailsCard = new JPanel();
        detailsCard.setLayout(new BoxLayout(detailsCard, BoxLayout.Y_AXIS));
        detailsCard.setBackground(Color.WHITE);
        detailsCard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header with Avatar
        JPanel headerPanel = new JPanel(new BorderLayout(15, 15));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, CustomTheme.LIGHT_COLOR));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        infoPanel.setOpaque(false);

        // Avatar
        String initials = String.valueOf(guest.getFirstName().charAt(0)) +
                (guest.getLastName() != null && !guest.getLastName().isEmpty() ? guest.getLastName().charAt(0) : ' ');

        JLabel avatarLabel = new JLabel(initials);
        avatarLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        avatarLabel.setForeground(Color.WHITE);
        avatarLabel.setBackground(CustomTheme.PRIMARY_COLOR);
        avatarLabel.setOpaque(true);
        avatarLabel.setPreferredSize(new Dimension(70, 70));
        avatarLabel.setHorizontalAlignment(SwingConstants.CENTER);
        avatarLabel.setBorder(BorderFactory.createLineBorder(CustomTheme.LIGHT_COLOR));
        infoPanel.add(avatarLabel);

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        textPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(guest.getFirstName() + " " + guest.getLastName());
        nameLabel.setFont(CustomTheme.HEADER_FONT);
        nameLabel.setForeground(CustomTheme.DARK_COLOR);
        textPanel.add(nameLabel);

        JLabel nationalityLabel = new JLabel(
                guest.getNationality() != null ? guest.getNationality() : "Nationality not specified");
        nationalityLabel.setFont(CustomTheme.NORMAL_FONT);
        nationalityLabel.setForeground(CustomTheme.GRAY_COLOR);
        textPanel.add(nationalityLabel);

        infoPanel.add(textPanel);
        headerPanel.add(infoPanel, BorderLayout.WEST);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionPanel.setOpaque(false);

        JButton editButton = new JButton("✏️ Edit");
        editButton.setFont(CustomTheme.BUTTON_FONT);
        editButton.setBackground(CustomTheme.PRIMARY_COLOR);
        editButton.setForeground(Color.WHITE);
        editButton.setFocusPainted(false);
        editButton.setBorderPainted(false);
        editButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        editButton.setPreferredSize(new Dimension(80, 35));
        editButton.addActionListener(e -> showEditGuestDialog(guest));
        actionPanel.add(editButton);

        headerPanel.add(actionPanel, BorderLayout.EAST);

        detailsCard.add(headerPanel);
        detailsCard.add(Box.createRigidArea(new Dimension(0, 20)));

        // Personal Information
        JPanel personalPanel = createInfoPanel("📋 Personal Information", new String[][] {
                { "Full Name", guest.getFirstName() + " " + guest.getLastName() },
                { "Email",
                        guest.getEmail() != null && !guest.getEmail().isEmpty() ? guest.getEmail() : "Not provided" },
                { "Phone",
                        guest.getPhone() != null && !guest.getPhone().isEmpty() ? guest.getPhone() : "Not provided" },
                { "Address",
                        guest.getAddress() != null && !guest.getAddress().isEmpty() ? guest.getAddress()
                                : "Not provided" },
                { "Nationality",
                        guest.getNationality() != null && !guest.getNationality().isEmpty() ? guest.getNationality()
                                : "Not specified" }
        });
        detailsCard.add(personalPanel);
        detailsCard.add(Box.createRigidArea(new Dimension(0, 15)));

        // Booking History
        List<Booking> bookings = dataManager.getBookingsByGuestId(guest.getGuestId());
        JPanel bookingPanel = createBookingHistoryPanel(guest, bookings);
        detailsCard.add(bookingPanel);

        JScrollPane scrollPane = new JScrollPane(detailsCard);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);
        CustomTheme.styleScrollPane(scrollPane);

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

    private JPanel createBookingHistoryPanel(Guest guest, List<Booking> bookings) {
        CustomTheme.ShadowPanel panel = new CustomTheme.ShadowPanel(10, 2);
        panel.setBackground(Color.WHITE);
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = CustomTheme.createHeaderLabel("📅 Booking History");
        titleLabel.setForeground(CustomTheme.PRIMARY_COLOR);
        panel.add(titleLabel, BorderLayout.NORTH);

        if (bookings == null || bookings.isEmpty()) {
            JLabel noBookingsLabel = CustomTheme.createLabel("No booking history available");
            noBookingsLabel.setHorizontalAlignment(SwingConstants.CENTER);
            noBookingsLabel.setForeground(CustomTheme.GRAY_COLOR);
            noBookingsLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
            panel.add(noBookingsLabel, BorderLayout.CENTER);
        } else {
            String[] columns = { "Booking ID", "Room", "Check-in", "Check-out", "Nights", "Total", "Status" };
            Object[][] data = new Object[bookings.size()][7];

            for (int i = 0; i < bookings.size(); i++) {
                Booking booking = bookings.get(i);
                data[i][0] = booking.getBookingNumber();
                data[i][1] = booking.getRoom() != null ? booking.getRoom().getRoomNumber() : "N/A";
                data[i][2] = booking.getCheckInDate() != null
                        ? booking.getCheckInDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                        : "N/A";
                data[i][3] = booking.getCheckOutDate() != null
                        ? booking.getCheckOutDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                        : "N/A";
                data[i][4] = booking.getNumberOfNights();
                data[i][5] = String.format("₹%,.2f", booking.getTotalAmount());
                data[i][6] = booking.getStatus();
            }

            JTable historyTable = new JTable(data, columns);
            CustomTheme.styleTable(historyTable);
            historyTable.setRowHeight(40);

            JScrollPane scrollPane = new JScrollPane(historyTable);
            scrollPane.setBorder(null);
            scrollPane.setPreferredSize(new Dimension(0, 200));
            CustomTheme.styleScrollPane(scrollPane);
            panel.add(scrollPane, BorderLayout.CENTER);
        }

        return panel;
    }

    private void showAddGuestDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Guest", true);
        dialog.setSize(550, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(Color.WHITE);

        JPanel formPanel = createGuestForm();

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 20));
        buttonPanel.setBackground(Color.WHITE);

        JButton saveButton = new JButton("Save Guest");
        saveButton.setFont(CustomTheme.BUTTON_FONT);
        saveButton.setBackground(CustomTheme.SUCCESS_COLOR);
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setBorderPainted(false);
        saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveButton.setPreferredSize(new Dimension(120, 40));
        saveButton.addActionListener(e -> {
            if (saveGuest(formPanel, dialog)) {
                dialog.dispose();
                loadGuests();
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(CustomTheme.BUTTON_FONT);
        cancelButton.setBackground(CustomTheme.DANGER_COLOR);
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.setBorderPainted(false);
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelButton.setPreferredSize(new Dimension(120, 40));
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private JPanel createGuestForm() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // First Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel firstNameLabel = new JLabel("First Name:*");
        firstNameLabel.setFont(CustomTheme.NORMAL_FONT);
        firstNameLabel.setForeground(CustomTheme.TEXT_COLOR);
        panel.add(firstNameLabel, gbc);
        gbc.gridx = 1;
        JTextField firstNameField = new JTextField();
        firstNameField.setFont(CustomTheme.NORMAL_FONT);
        firstNameField.setForeground(CustomTheme.TEXT_COLOR);
        firstNameField.setBackground(Color.WHITE);
        firstNameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        firstNameField.setPreferredSize(new Dimension(250, 42));
        panel.add(firstNameField, gbc);

        // Last Name
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel lastNameLabel = new JLabel("Last Name:*");
        lastNameLabel.setFont(CustomTheme.NORMAL_FONT);
        lastNameLabel.setForeground(CustomTheme.TEXT_COLOR);
        panel.add(lastNameLabel, gbc);
        gbc.gridx = 1;
        JTextField lastNameField = new JTextField();
        lastNameField.setFont(CustomTheme.NORMAL_FONT);
        lastNameField.setForeground(CustomTheme.TEXT_COLOR);
        lastNameField.setBackground(Color.WHITE);
        lastNameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        lastNameField.setPreferredSize(new Dimension(250, 42));
        panel.add(lastNameField, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(CustomTheme.NORMAL_FONT);
        emailLabel.setForeground(CustomTheme.TEXT_COLOR);
        panel.add(emailLabel, gbc);
        gbc.gridx = 1;
        JTextField emailField = new JTextField();
        emailField.setFont(CustomTheme.NORMAL_FONT);
        emailField.setForeground(CustomTheme.TEXT_COLOR);
        emailField.setBackground(Color.WHITE);
        emailField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        emailField.setPreferredSize(new Dimension(250, 42));
        panel.add(emailField, gbc);

        // Phone
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel phoneLabel = new JLabel("Phone:*");
        phoneLabel.setFont(CustomTheme.NORMAL_FONT);
        phoneLabel.setForeground(CustomTheme.TEXT_COLOR);
        panel.add(phoneLabel, gbc);
        gbc.gridx = 1;
        JTextField phoneField = new JTextField();
        phoneField.setFont(CustomTheme.NORMAL_FONT);
        phoneField.setForeground(CustomTheme.TEXT_COLOR);
        phoneField.setBackground(Color.WHITE);
        phoneField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        phoneField.setPreferredSize(new Dimension(250, 42));
        panel.add(phoneField, gbc);

        // Address
        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel addressLabel = new JLabel("Address:");
        addressLabel.setFont(CustomTheme.NORMAL_FONT);
        addressLabel.setForeground(CustomTheme.TEXT_COLOR);
        panel.add(addressLabel, gbc);
        gbc.gridx = 1;
        JTextArea addressArea = new JTextArea(3, 20);
        addressArea.setFont(CustomTheme.NORMAL_FONT);
        addressArea.setForeground(CustomTheme.TEXT_COLOR);
        addressArea.setBackground(Color.WHITE);
        addressArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        JScrollPane addressScroll = new JScrollPane(addressArea);
        addressScroll.setBorder(null);
        panel.add(addressScroll, gbc);

        // Nationality
        gbc.gridx = 0;
        gbc.gridy = 5;
        JLabel nationalityLabel = new JLabel("Nationality:");
        nationalityLabel.setFont(CustomTheme.NORMAL_FONT);
        nationalityLabel.setForeground(CustomTheme.TEXT_COLOR);
        panel.add(nationalityLabel, gbc);
        gbc.gridx = 1;
        JTextField nationalityField = new JTextField();
        nationalityField.setFont(CustomTheme.NORMAL_FONT);
        nationalityField.setForeground(CustomTheme.TEXT_COLOR);
        nationalityField.setBackground(Color.WHITE);
        nationalityField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        nationalityField.setPreferredSize(new Dimension(250, 42));
        panel.add(nationalityField, gbc);

        panel.putClientProperty("firstName", firstNameField);
        panel.putClientProperty("lastName", lastNameField);
        panel.putClientProperty("email", emailField);
        panel.putClientProperty("phone", phoneField);
        panel.putClientProperty("address", addressArea);
        panel.putClientProperty("nationality", nationalityField);

        return panel;
    }

    private boolean saveGuest(JPanel formPanel, JDialog dialog) {
        try {
            JTextField firstNameField = (JTextField) formPanel.getClientProperty("firstName");
            JTextField lastNameField = (JTextField) formPanel.getClientProperty("lastName");
            JTextField emailField = (JTextField) formPanel.getClientProperty("email");
            JTextField phoneField = (JTextField) formPanel.getClientProperty("phone");
            JTextArea addressArea = (JTextArea) formPanel.getClientProperty("address");
            JTextField nationalityField = (JTextField) formPanel.getClientProperty("nationality");

            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String phone = phoneField.getText().trim();

            if (firstName.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "First name is required!", "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                firstNameField.requestFocus();
                return false;
            }

            if (lastName.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Last name is required!", "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                lastNameField.requestFocus();
                return false;
            }

            if (phone.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Phone number is required!", "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                phoneField.requestFocus();
                return false;
            }

            Guest guest = new Guest();
            guest.setFirstName(firstName);
            guest.setLastName(lastName);
            guest.setEmail(emailField.getText().trim());
            guest.setPhone(phone);
            guest.setAddress(addressArea.getText().trim());
            guest.setNationality(nationalityField.getText().trim());

            dataManager.addGuest(guest);

            JOptionPane.showMessageDialog(dialog, "Guest added successfully!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            return true;

        } catch (Exception e) {
            JOptionPane.showMessageDialog(dialog, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
    }

    private void showEditGuestDialog(Guest guest) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Guest", true);
        dialog.setSize(550, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(Color.WHITE);

        JPanel formPanel = createEditGuestForm(guest);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 20));
        buttonPanel.setBackground(Color.WHITE);

        JButton updateButton = new JButton("Update Guest");
        updateButton.setFont(CustomTheme.BUTTON_FONT);
        updateButton.setBackground(CustomTheme.SUCCESS_COLOR);
        updateButton.setForeground(Color.WHITE);
        updateButton.setFocusPainted(false);
        updateButton.setBorderPainted(false);
        updateButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        updateButton.setPreferredSize(new Dimension(130, 40));
        updateButton.addActionListener(e -> {
            if (updateGuest(guest.getGuestId(), formPanel, dialog)) {
                dialog.dispose();
                loadGuests();
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(CustomTheme.BUTTON_FONT);
        cancelButton.setBackground(CustomTheme.DANGER_COLOR);
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.setBorderPainted(false);
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelButton.setPreferredSize(new Dimension(120, 40));
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(updateButton);
        buttonPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private JPanel createEditGuestForm(Guest guest) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // First Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel firstNameLabel = new JLabel("First Name:*");
        firstNameLabel.setFont(CustomTheme.NORMAL_FONT);
        firstNameLabel.setForeground(CustomTheme.TEXT_COLOR);
        panel.add(firstNameLabel, gbc);
        gbc.gridx = 1;
        JTextField firstNameField = new JTextField(guest.getFirstName());
        firstNameField.setFont(CustomTheme.NORMAL_FONT);
        firstNameField.setForeground(CustomTheme.TEXT_COLOR);
        firstNameField.setBackground(Color.WHITE);
        firstNameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        firstNameField.setPreferredSize(new Dimension(250, 42));
        panel.add(firstNameField, gbc);

        // Last Name
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel lastNameLabel = new JLabel("Last Name:*");
        lastNameLabel.setFont(CustomTheme.NORMAL_FONT);
        lastNameLabel.setForeground(CustomTheme.TEXT_COLOR);
        panel.add(lastNameLabel, gbc);
        gbc.gridx = 1;
        JTextField lastNameField = new JTextField(guest.getLastName());
        lastNameField.setFont(CustomTheme.NORMAL_FONT);
        lastNameField.setForeground(CustomTheme.TEXT_COLOR);
        lastNameField.setBackground(Color.WHITE);
        lastNameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        lastNameField.setPreferredSize(new Dimension(250, 42));
        panel.add(lastNameField, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(CustomTheme.NORMAL_FONT);
        emailLabel.setForeground(CustomTheme.TEXT_COLOR);
        panel.add(emailLabel, gbc);
        gbc.gridx = 1;
        JTextField emailField = new JTextField(guest.getEmail());
        emailField.setFont(CustomTheme.NORMAL_FONT);
        emailField.setForeground(CustomTheme.TEXT_COLOR);
        emailField.setBackground(Color.WHITE);
        emailField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        emailField.setPreferredSize(new Dimension(250, 42));
        panel.add(emailField, gbc);

        // Phone
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel phoneLabel = new JLabel("Phone:*");
        phoneLabel.setFont(CustomTheme.NORMAL_FONT);
        phoneLabel.setForeground(CustomTheme.TEXT_COLOR);
        panel.add(phoneLabel, gbc);
        gbc.gridx = 1;
        JTextField phoneField = new JTextField(guest.getPhone());
        phoneField.setFont(CustomTheme.NORMAL_FONT);
        phoneField.setForeground(CustomTheme.TEXT_COLOR);
        phoneField.setBackground(Color.WHITE);
        phoneField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        phoneField.setPreferredSize(new Dimension(250, 42));
        panel.add(phoneField, gbc);

        // Address
        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel addressLabel = new JLabel("Address:");
        addressLabel.setFont(CustomTheme.NORMAL_FONT);
        addressLabel.setForeground(CustomTheme.TEXT_COLOR);
        panel.add(addressLabel, gbc);
        gbc.gridx = 1;
        JTextArea addressArea = new JTextArea(guest.getAddress());
        addressArea.setFont(CustomTheme.NORMAL_FONT);
        addressArea.setForeground(CustomTheme.TEXT_COLOR);
        addressArea.setBackground(Color.WHITE);
        addressArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        JScrollPane addressScroll = new JScrollPane(addressArea);
        addressScroll.setBorder(null);
        panel.add(addressScroll, gbc);

        // Nationality
        gbc.gridx = 0;
        gbc.gridy = 5;
        JLabel nationalityLabel = new JLabel("Nationality:");
        nationalityLabel.setFont(CustomTheme.NORMAL_FONT);
        nationalityLabel.setForeground(CustomTheme.TEXT_COLOR);
        panel.add(nationalityLabel, gbc);
        gbc.gridx = 1;
        JTextField nationalityField = new JTextField(guest.getNationality());
        nationalityField.setFont(CustomTheme.NORMAL_FONT);
        nationalityField.setForeground(CustomTheme.TEXT_COLOR);
        nationalityField.setBackground(Color.WHITE);
        nationalityField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        nationalityField.setPreferredSize(new Dimension(250, 42));
        panel.add(nationalityField, gbc);

        panel.putClientProperty("firstName", firstNameField);
        panel.putClientProperty("lastName", lastNameField);
        panel.putClientProperty("email", emailField);
        panel.putClientProperty("phone", phoneField);
        panel.putClientProperty("address", addressArea);
        panel.putClientProperty("nationality", nationalityField);

        return panel;
    }

    private boolean updateGuest(int guestId, JPanel formPanel, JDialog dialog) {
        try {
            Guest guest = dataManager.getGuestById(guestId);
            if (guest == null)
                return false;

            JTextField firstNameField = (JTextField) formPanel.getClientProperty("firstName");
            JTextField lastNameField = (JTextField) formPanel.getClientProperty("lastName");
            JTextField emailField = (JTextField) formPanel.getClientProperty("email");
            JTextField phoneField = (JTextField) formPanel.getClientProperty("phone");
            JTextArea addressArea = (JTextArea) formPanel.getClientProperty("address");
            JTextField nationalityField = (JTextField) formPanel.getClientProperty("nationality");

            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String phone = phoneField.getText().trim();

            if (firstName.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "First name is required!", "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }

            if (lastName.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Last name is required!", "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }

            guest.setFirstName(firstName);
            guest.setLastName(lastName);
            guest.setEmail(emailField.getText().trim());
            guest.setPhone(phone);
            guest.setAddress(addressArea.getText().trim());
            guest.setNationality(nationalityField.getText().trim());

            JOptionPane.showMessageDialog(dialog, "Guest updated successfully!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            return true;

        } catch (Exception e) {
            JOptionPane.showMessageDialog(dialog, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
    }
}