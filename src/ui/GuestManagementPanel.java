// ui/GuestManagementPanel.java
package ui;

import dao.GuestDAO;
import dao.BookingDAO;
import models.Guest;
import models.Booking;
import utils.CustomTheme;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.Timer;

public class GuestManagementPanel extends JPanel {
    
    private JTable guestTable;
    private DefaultTableModel tableModel;
    private GuestDAO guestDAO;
    private BookingDAO bookingDAO;
    private CustomTheme.ModernTextField searchField;
    private JComboBox<String> filterComboBox;
    private JLabel statusLabel;
    private JLabel statsLabel;
    private Timer refreshTimer;
    private JPanel detailPanel;
    
    public GuestManagementPanel() {
        guestDAO = new GuestDAO();
        bookingDAO = new BookingDAO();
        initComponents();
        loadGuests();
        startAutoRefresh();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBackground(CustomTheme.BACKGROUND_COLOR);
        
        add(createHeaderPanel(), BorderLayout.NORTH);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(createTablePanel());
        splitPane.setRightComponent(createDetailPanel());
        splitPane.setDividerLocation(800);
        splitPane.setDividerSize(0);
        splitPane.setBorder(null);
        
        add(splitPane, BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(15, 15));
        headerPanel.setBackground(CustomTheme.PANEL_HEADER_COLOR);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, CustomTheme.PRIMARY_COLOR),
            BorderFactory.createEmptyBorder(20, 20, 10, 20)
        ));
        
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titlePanel.setOpaque(false);
        
        JLabel titleIcon = new JLabel("👥");
        titleIcon.setFont(CustomTheme.EMOJI_FONT.deriveFont(32f));
        titlePanel.add(titleIcon);
        
        JLabel titleLabel = new JLabel("Guest Management");
        titleLabel.setFont(CustomTheme.HEADER_FONT);
        titleLabel.setForeground(CustomTheme.DARK_COLOR);
        titlePanel.add(titleLabel);
        
        JLabel subtitleLabel = new JLabel("Manage guest information, view profiles and track stay history");
        subtitleLabel.setFont(CustomTheme.SMALL_FONT);
        subtitleLabel.setForeground(CustomTheme.GRAY_COLOR);
        titlePanel.add(subtitleLabel);
        
        headerPanel.add(titlePanel, BorderLayout.WEST);
        
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionPanel.setOpaque(false);
        
        searchField = new CustomTheme.ModernTextField(15);
        searchField.setPreferredSize(new Dimension(200, 40));
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                searchGuests();
            }
        });
        actionPanel.add(searchField);
        
        filterComboBox = new JComboBox<>(new String[]{"All Guests", "Active Guests"});
        filterComboBox.setFont(CustomTheme.NORMAL_FONT);
        filterComboBox.setBackground(Color.WHITE);
        filterComboBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CustomTheme.GRAY_COLOR),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        filterComboBox.addActionListener(e -> filterGuests());
        actionPanel.add(filterComboBox);
        
        CustomTheme.ModernButton addButton = new CustomTheme.ModernButton("➕ Add Guest", CustomTheme.SUCCESS_COLOR);
        addButton.addActionListener(e -> showAddGuestDialog());
        actionPanel.add(addButton);
        
        CustomTheme.ModernButton refreshButton = new CustomTheme.ModernButton("🔄 Refresh", CustomTheme.PRIMARY_COLOR);
        refreshButton.addActionListener(e -> loadGuests());
        actionPanel.add(refreshButton);
        
        headerPanel.add(actionPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CustomTheme.CARD_BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 10));
        
        String[] columns = {"ID", "Name", "Phone", "Email", "Nationality", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        guestTable = new JTable(tableModel);
        CustomTheme.styleTable(guestTable);
        
        guestTable.getColumnModel().getColumn(0).setMaxWidth(60);
        guestTable.getColumnModel().getColumn(0).setMinWidth(60);
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
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel initialLabel = new JLabel("Select a guest to view details", SwingConstants.CENTER);
        initialLabel.setFont(CustomTheme.HEADER_FONT);
        initialLabel.setForeground(CustomTheme.GRAY_COLOR);
        detailPanel.add(initialLabel, BorderLayout.CENTER);
        
        return detailPanel;
    }
    
    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(CustomTheme.BACKGROUND_COLOR);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        
        CustomTheme.ShadowPanel statsPanel = new CustomTheme.ShadowPanel(10, 2);
        statsPanel.setBackground(CustomTheme.CARD_BACKGROUND_COLOR);
        statsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 12));
        
        statsLabel = new JLabel();
        statsLabel.setFont(CustomTheme.NORMAL_FONT);
        statsLabel.setForeground(CustomTheme.DARK_COLOR);
        statsPanel.add(statsLabel);
        
        bottomPanel.add(statsPanel, BorderLayout.WEST);
        
        statusLabel = new JLabel("Ready");
        statusLabel.setFont(CustomTheme.SMALL_FONT);
        statusLabel.setForeground(CustomTheme.GRAY_COLOR);
        bottomPanel.add(statusLabel, BorderLayout.EAST);
        
        return bottomPanel;
    }
    
    private void loadGuests() {
        SwingWorker<List<Guest>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Guest> doInBackground() {
                statusLabel.setText("Loading guests...");
                return guestDAO.getAllGuests();
            }
            
            @Override
            protected void done() {
                try {
                    List<Guest> guests = get();
                    updateTableData(guests);
                    updateStatistics(guests);
                    statusLabel.setText("Loaded " + guests.size() + " guests");
                } catch (Exception e) {
                    statusLabel.setText("Error loading guests: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    
    private void updateTableData(List<Guest> guests) {
        tableModel.setRowCount(0);
        for (Guest guest : guests) {
            tableModel.addRow(new Object[]{
                guest.getGuestId(),
                guest.getFirstName() + " " + guest.getLastName(),
                guest.getPhone(),
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
    }
    
    private void filterGuests() {
        String filter = (String) filterComboBox.getSelectedItem();
        SwingWorker<List<Guest>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Guest> doInBackground() {
                switch(filter) {
                    case "Active Guests": return guestDAO.getActiveGuests();
                    default: return guestDAO.getAllGuests();
                }
            }
            
            @Override
            protected void done() {
                try {
                    List<Guest> guests = get();
                    updateTableData(guests);
                    statusLabel.setText("Showing " + guests.size() + " " + filter.toLowerCase());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    
    private void searchGuests() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadGuests();
        } else {
            SwingWorker<List<Guest>, Void> worker = new SwingWorker<>() {
                @Override
                protected List<Guest> doInBackground() {
                    return guestDAO.searchGuests(searchTerm);
                }
                
                @Override
                protected void done() {
                    try {
                        List<Guest> guests = get();
                        updateTableData(guests);
                        statusLabel.setText("Found " + guests.size() + " guests matching '" + searchTerm + "'");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            worker.execute();
        }
    }
    
    private void showGuestDetails() {
        int selectedRow = guestTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        int guestId = (int) tableModel.getValueAt(selectedRow, 0);
        Guest guest = guestDAO.getGuestById(guestId);
        
        if (guest != null) {
            updateDetailPanel(guest);
        }
    }
    
    private void updateDetailPanel(Guest guest) {
        detailPanel.removeAll();
        
        CustomTheme.ShadowPanel detailsCard = new CustomTheme.ShadowPanel(12, 3);
        detailsCard.setBackground(Color.WHITE);
        detailsCard.setLayout(new BorderLayout());
        
        JPanel headerPanel = createDetailHeader(guest);
        detailsCard.add(headerPanel, BorderLayout.NORTH);
        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(CustomTheme.NORMAL_FONT);
        tabbedPane.addTab("Personal Information", createPersonalInfoPanel(guest));
        tabbedPane.addTab("Booking History", createBookingHistoryPanel(guest));
        
        detailsCard.add(tabbedPane, BorderLayout.CENTER);
        
        detailPanel.add(detailsCard, BorderLayout.CENTER);
        detailPanel.revalidate();
        detailPanel.repaint();
    }
    
    private JPanel createDetailHeader(Guest guest) {
        JPanel headerPanel = new JPanel(new BorderLayout(15, 15));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, CustomTheme.LIGHT_COLOR),
            BorderFactory.createEmptyBorder(0, 0, 20, 0)
        ));
        
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        infoPanel.setOpaque(false);
        
        // Avatar
        String initials = String.valueOf(guest.getFirstName().charAt(0)) + 
                         (guest.getLastName() != null && !guest.getLastName().isEmpty() ? 
                          guest.getLastName().charAt(0) : ' ');
        
        JLabel avatarLabel = new JLabel(initials);
        avatarLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        avatarLabel.setForeground(Color.WHITE);
        avatarLabel.setBackground(CustomTheme.PRIMARY_COLOR);
        avatarLabel.setOpaque(true);
        avatarLabel.setPreferredSize(new Dimension(80, 80));
        avatarLabel.setHorizontalAlignment(SwingConstants.CENTER);
        avatarLabel.setBorder(BorderFactory.createLineBorder(CustomTheme.LIGHT_COLOR));
        infoPanel.add(avatarLabel);
        
        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        textPanel.setOpaque(false);
        
        JLabel nameLabel = new JLabel(guest.getFirstName() + " " + guest.getLastName());
        nameLabel.setFont(CustomTheme.HEADER_FONT);
        nameLabel.setForeground(CustomTheme.DARK_COLOR);
        textPanel.add(nameLabel);
        
        JLabel nationalityLabel = new JLabel(guest.getNationality() != null ? guest.getNationality() : "Nationality not specified");
        nationalityLabel.setFont(CustomTheme.NORMAL_FONT);
        nationalityLabel.setForeground(CustomTheme.GRAY_COLOR);
        textPanel.add(nationalityLabel);
        
        infoPanel.add(textPanel);
        headerPanel.add(infoPanel, BorderLayout.WEST);
        
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionPanel.setOpaque(false);
        
        CustomTheme.ModernButton editButton = new CustomTheme.ModernButton("✏️ Edit", CustomTheme.PRIMARY_COLOR);
        editButton.addActionListener(e -> showEditGuestDialog(guest));
        actionPanel.add(editButton);
        
        headerPanel.add(actionPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createPersonalInfoPanel(Guest guest) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        addInfoRow(panel, gbc, 0, "Full Name:", guest.getFirstName() + " " + guest.getLastName());
        addInfoRow(panel, gbc, 1, "Email:", guest.getEmail() != null ? guest.getEmail() : "N/A");
        addInfoRow(panel, gbc, 2, "Phone:", guest.getPhone() != null ? guest.getPhone() : "N/A");
        addInfoRow(panel, gbc, 3, "Address:", guest.getAddress() != null ? guest.getAddress() : "N/A");
        addInfoRow(panel, gbc, 4, "Nationality:", guest.getNationality() != null ? guest.getNationality() : "N/A");
        
        return panel;
    }
    
    private JPanel createBookingHistoryPanel(Guest guest) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        List<Booking> bookings = bookingDAO.getBookingsByGuestId(guest.getGuestId());
        
        if (bookings.isEmpty()) {
            JLabel noBookingsLabel = new JLabel("No booking history available", SwingConstants.CENTER);
            noBookingsLabel.setFont(CustomTheme.NORMAL_FONT);
            noBookingsLabel.setForeground(CustomTheme.GRAY_COLOR);
            panel.add(noBookingsLabel, BorderLayout.CENTER);
        } else {
            String[] columns = {"Booking ID", "Room", "Check-in", "Check-out", "Nights", "Total", "Status"};
            Object[][] data = new Object[bookings.size()][7];
            
            for (int i = 0; i < bookings.size(); i++) {
                Booking booking = bookings.get(i);
                data[i][0] = booking.getBookingNumber();
                data[i][1] = booking.getRoom() != null ? booking.getRoom().getRoomNumber() : "N/A";
                data[i][2] = booking.getFormattedCheckInDate();
                data[i][3] = booking.getFormattedCheckOutDate();
                data[i][4] = booking.getNumberOfNights();
                data[i][5] = String.format("₹%,.2f", booking.getTotalAmount());
                data[i][6] = booking.getStatus();
            }
            
            JTable historyTable = new JTable(data, columns);
            CustomTheme.styleTable(historyTable);
            
            JScrollPane scrollPane = new JScrollPane(historyTable);
            scrollPane.setBorder(null);
            CustomTheme.styleScrollPane(scrollPane);
            panel.add(scrollPane, BorderLayout.CENTER);
        }
        
        return panel;
    }
    
    private void addInfoRow(JPanel panel, GridBagConstraints gbc, int row, String label, String value) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(CustomTheme.NORMAL_FONT);
        labelComp.setForeground(CustomTheme.GRAY_COLOR);
        panel.add(labelComp, gbc);
        
        gbc.gridx = 1;
        JLabel valueComp = new JLabel(value);
        valueComp.setFont(CustomTheme.NORMAL_FONT);
        valueComp.setForeground(CustomTheme.DARK_COLOR);
        panel.add(valueComp, gbc);
    }
    
    private void showAddGuestDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Guest", true);
        dialog.setSize(550, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        JPanel formPanel = createGuestForm();
        dialog.add(formPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 20));
        buttonPanel.setBackground(Color.WHITE);
        
        CustomTheme.ModernButton saveButton = new CustomTheme.ModernButton("Save Guest", CustomTheme.SUCCESS_COLOR);
        saveButton.addActionListener(e -> {
            if (saveGuest(formPanel, dialog)) {
                dialog.dispose();
                loadGuests();
            }
        });
        
        CustomTheme.ModernButton cancelButton = new CustomTheme.ModernButton("Cancel", CustomTheme.DANGER_COLOR);
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    private JPanel createGuestForm() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // First Name
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(createLabel("First Name:*"), gbc);
        gbc.gridx = 1;
        CustomTheme.ModernTextField firstNameField = new CustomTheme.ModernTextField(20);
        firstNameField.setPreferredSize(new Dimension(250, 40));
        panel.add(firstNameField, gbc);
        
        // Last Name
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(createLabel("Last Name:*"), gbc);
        gbc.gridx = 1;
        CustomTheme.ModernTextField lastNameField = new CustomTheme.ModernTextField(20);
        lastNameField.setPreferredSize(new Dimension(250, 40));
        panel.add(lastNameField, gbc);
        
        // Email
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(createLabel("Email:"), gbc);
        gbc.gridx = 1;
        CustomTheme.ModernTextField emailField = new CustomTheme.ModernTextField(20);
        emailField.setPreferredSize(new Dimension(250, 40));
        panel.add(emailField, gbc);
        
        // Phone
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(createLabel("Phone:*"), gbc);
        gbc.gridx = 1;
        CustomTheme.ModernTextField phoneField = new CustomTheme.ModernTextField(20);
        phoneField.setPreferredSize(new Dimension(250, 40));
        panel.add(phoneField, gbc);
        
        // Address
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(createLabel("Address:"), gbc);
        gbc.gridx = 1;
        JTextArea addressArea = new JTextArea(3, 20);
        addressArea.setFont(CustomTheme.NORMAL_FONT);
        addressArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CustomTheme.GRAY_COLOR),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        JScrollPane addressScroll = new JScrollPane(addressArea);
        addressScroll.setBorder(null);
        panel.add(addressScroll, gbc);
        
        // Nationality
        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(createLabel("Nationality:"), gbc);
        gbc.gridx = 1;
        CustomTheme.ModernTextField nationalityField = new CustomTheme.ModernTextField(20);
        nationalityField.setPreferredSize(new Dimension(250, 40));
        panel.add(nationalityField, gbc);
        
        panel.putClientProperty("firstName", firstNameField);
        panel.putClientProperty("lastName", lastNameField);
        panel.putClientProperty("email", emailField);
        panel.putClientProperty("phone", phoneField);
        panel.putClientProperty("address", addressArea);
        panel.putClientProperty("nationality", nationalityField);
        
        return panel;
    }
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(CustomTheme.NORMAL_FONT);
        label.setForeground(CustomTheme.DARK_COLOR);
        return label;
    }
    
    private boolean saveGuest(JPanel formPanel, JDialog dialog) {
        try {
            Guest guest = new Guest();
            
            JTextField firstNameField = (JTextField) formPanel.getClientProperty("firstName");
            JTextField lastNameField = (JTextField) formPanel.getClientProperty("lastName");
            JTextField emailField = (JTextField) formPanel.getClientProperty("email");
            JTextField phoneField = (JTextField) formPanel.getClientProperty("phone");
            JTextArea addressArea = (JTextArea) formPanel.getClientProperty("address");
            JTextField nationalityField = (JTextField) formPanel.getClientProperty("nationality");
            
            if (firstNameField.getText().trim().isEmpty() || lastNameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "First name and last name are required!");
                return false;
            }
            
            if (phoneField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Phone number is required!");
                return false;
            }
            
            guest.setFirstName(firstNameField.getText().trim());
            guest.setLastName(lastNameField.getText().trim());
            guest.setEmail(emailField.getText().trim());
            guest.setPhone(phoneField.getText().trim());
            guest.setAddress(addressArea.getText().trim());
            guest.setNationality(nationalityField.getText().trim());
            
            if (guestDAO.addGuest(guest)) {
                JOptionPane.showMessageDialog(dialog, "Guest added successfully!");
                return true;
            } else {
                JOptionPane.showMessageDialog(dialog, "Failed to add guest!");
                return false;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(dialog, "Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private void showEditGuestDialog(Guest guest) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Guest", true);
        dialog.setSize(550, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        JPanel formPanel = createEditGuestForm(guest);
        dialog.add(formPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 20));
        buttonPanel.setBackground(Color.WHITE);
        
        CustomTheme.ModernButton updateButton = new CustomTheme.ModernButton("Update Guest", CustomTheme.SUCCESS_COLOR);
        updateButton.addActionListener(e -> {
            if (updateGuest(guest.getGuestId(), formPanel, dialog)) {
                dialog.dispose();
                loadGuests();
                showGuestDetails();
            }
        });
        
        CustomTheme.ModernButton cancelButton = new CustomTheme.ModernButton("Cancel", CustomTheme.DANGER_COLOR);
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(updateButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    private JPanel createEditGuestForm(Guest guest) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(createLabel("First Name:*"), gbc);
        gbc.gridx = 1;
        CustomTheme.ModernTextField firstNameField = new CustomTheme.ModernTextField(20);
        firstNameField.setText(guest.getFirstName());
        firstNameField.setPreferredSize(new Dimension(250, 40));
        panel.add(firstNameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(createLabel("Last Name:*"), gbc);
        gbc.gridx = 1;
        CustomTheme.ModernTextField lastNameField = new CustomTheme.ModernTextField(20);
        lastNameField.setText(guest.getLastName());
        lastNameField.setPreferredSize(new Dimension(250, 40));
        panel.add(lastNameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(createLabel("Email:"), gbc);
        gbc.gridx = 1;
        CustomTheme.ModernTextField emailField = new CustomTheme.ModernTextField(20);
        emailField.setText(guest.getEmail() != null ? guest.getEmail() : "");
        emailField.setPreferredSize(new Dimension(250, 40));
        panel.add(emailField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(createLabel("Phone:*"), gbc);
        gbc.gridx = 1;
        CustomTheme.ModernTextField phoneField = new CustomTheme.ModernTextField(20);
        phoneField.setText(guest.getPhone());
        phoneField.setPreferredSize(new Dimension(250, 40));
        panel.add(phoneField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(createLabel("Address:"), gbc);
        gbc.gridx = 1;
        JTextArea addressArea = new JTextArea(3, 20);
        addressArea.setText(guest.getAddress() != null ? guest.getAddress() : "");
        addressArea.setFont(CustomTheme.NORMAL_FONT);
        addressArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CustomTheme.GRAY_COLOR),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        JScrollPane addressScroll = new JScrollPane(addressArea);
        addressScroll.setBorder(null);
        panel.add(addressScroll, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(createLabel("Nationality:"), gbc);
        gbc.gridx = 1;
        CustomTheme.ModernTextField nationalityField = new CustomTheme.ModernTextField(20);
        nationalityField.setText(guest.getNationality() != null ? guest.getNationality() : "");
        nationalityField.setPreferredSize(new Dimension(250, 40));
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
            Guest guest = guestDAO.getGuestById(guestId);
            if (guest == null) return false;
            
            JTextField firstNameField = (JTextField) formPanel.getClientProperty("firstName");
            JTextField lastNameField = (JTextField) formPanel.getClientProperty("lastName");
            JTextField emailField = (JTextField) formPanel.getClientProperty("email");
            JTextField phoneField = (JTextField) formPanel.getClientProperty("phone");
            JTextArea addressArea = (JTextArea) formPanel.getClientProperty("address");
            JTextField nationalityField = (JTextField) formPanel.getClientProperty("nationality");
            
            guest.setFirstName(firstNameField.getText().trim());
            guest.setLastName(lastNameField.getText().trim());
            guest.setEmail(emailField.getText().trim());
            guest.setPhone(phoneField.getText().trim());
            guest.setAddress(addressArea.getText().trim());
            guest.setNationality(nationalityField.getText().trim());
            
            if (guestDAO.updateGuest(guest)) {
                JOptionPane.showMessageDialog(dialog, "Guest updated successfully!");
                return true;
            } else {
                JOptionPane.showMessageDialog(dialog, "Failed to update guest!");
                return false;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(dialog, "Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private void startAutoRefresh() {
        refreshTimer = new Timer(60000, e -> loadGuests());
        refreshTimer.start();
    }
}