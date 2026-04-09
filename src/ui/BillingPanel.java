// ui/BillingPanel.java
package ui;

import models.Booking;
import utils.CustomTheme;
import utils.DataManager;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BillingPanel extends JPanel {
    
    private JTable invoiceTable;
    private DefaultTableModel tableModel;
    private DataManager dataManager;
    private CustomTheme.ModernTextField searchField;
    private JComboBox<String> statusFilterCombo;
    private JComboBox<String> periodFilterCombo;
    private JLabel statusLabel;
    private JPanel invoicePreviewPanel;
    private JLabel totalRevenueLabel;
    private JLabel paidAmountLabel;
    private JLabel pendingAmountLabel;
    private List<Booking> currentBookings;
    
    public BillingPanel() {
        dataManager = DataManager.getInstance();
        initComponents();
        loadInvoices();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBackground(CustomTheme.BACKGROUND_COLOR);
        
        add(createHeaderPanel(), BorderLayout.NORTH);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(createTablePanel());
        splitPane.setRightComponent(createInvoicePreviewPanel());
        splitPane.setDividerLocation(850);
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
            BorderFactory.createEmptyBorder(20, 20, 10, 20)
        ));
        
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titlePanel.setOpaque(false);
        
        JLabel titleIcon = new JLabel("💰");
        titleIcon.setFont(CustomTheme.getEmojiFont().deriveFont(32f));
        titlePanel.add(titleIcon);
        
        JLabel titleLabel = CustomTheme.createHeaderLabel("Billing & Invoicing");
        titlePanel.add(titleLabel);
        
        JLabel subtitleLabel = CustomTheme.createLabel("Manage invoices, process payments and track revenue");
        subtitleLabel.setFont(CustomTheme.SMALL_FONT);
        subtitleLabel.setForeground(CustomTheme.GRAY_COLOR);
        titlePanel.add(subtitleLabel);
        
        headerPanel.add(titlePanel, BorderLayout.WEST);
        
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionPanel.setOpaque(false);
        
        periodFilterCombo = new JComboBox<>(new String[]{"All Time", "Today", "This Week", "This Month"});
        CustomTheme.styleComboBox(periodFilterCombo);
        periodFilterCombo.setPreferredSize(new Dimension(120, 35));
        periodFilterCombo.addActionListener(e -> filterByPeriod());
        actionPanel.add(periodFilterCombo);
        
        searchField = new CustomTheme.ModernTextField(15);
        searchField.setPlaceholder("Search invoices...");
        searchField.setPreferredSize(new Dimension(180, 35));
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                searchInvoices();
            }
        });
        actionPanel.add(searchField);
        
        statusFilterCombo = new JComboBox<>(new String[]{"All", "Paid", "Partial", "Pending"});
        CustomTheme.styleComboBox(statusFilterCombo);
        statusFilterCombo.setPreferredSize(new Dimension(100, 35));
        statusFilterCombo.addActionListener(e -> filterByStatus());
        actionPanel.add(statusFilterCombo);
        
        CustomTheme.ModernButton reportButton = new CustomTheme.ModernButton("📊 Generate Report", CustomTheme.PRIMARY_COLOR);
        reportButton.addActionListener(e -> generateReport());
        actionPanel.add(reportButton);
        
        CustomTheme.ModernButton refreshButton = new CustomTheme.ModernButton("🔄 Refresh", CustomTheme.SUCCESS_COLOR);
        refreshButton.addActionListener(e -> loadInvoices());
        actionPanel.add(refreshButton);
        
        headerPanel.add(actionPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CustomTheme.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 10));
        
        String[] columns = {"ID", "Invoice #", "Guest", "Room", "Check-in", "Check-out", "Total", "Paid", "Due", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        invoiceTable = new JTable(tableModel);
        invoiceTable.setFont(CustomTheme.NORMAL_FONT);
        invoiceTable.setRowHeight(45);
        invoiceTable.setShowGrid(false);
        invoiceTable.setForeground(CustomTheme.TEXT_COLOR);
        invoiceTable.setBackground(Color.WHITE);
        invoiceTable.setSelectionBackground(new Color(25, 118, 210, 50));
        
        // Fix Table Header
        JTableHeader header = invoiceTable.getTableHeader();
        header.setFont(CustomTheme.HEADER_FONT);
        header.setBackground(CustomTheme.HEADER_BACKGROUND);
        header.setForeground(CustomTheme.HEADER_TEXT_COLOR);
        header.setPreferredSize(new Dimension(header.getWidth(), 45));
        header.setReorderingAllowed(false);
        header.setOpaque(true);
        
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = new JLabel(value != null ? value.toString() : "");
                label.setFont(CustomTheme.HEADER_FONT);
                label.setBackground(CustomTheme.HEADER_BACKGROUND);
                label.setForeground(CustomTheme.HEADER_TEXT_COLOR);
                label.setOpaque(true);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                return label;
            }
        });
        
        // Custom cell renderer
        invoiceTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = new JLabel();
                label.setFont(CustomTheme.NORMAL_FONT);
                label.setOpaque(true);
                label.setForeground(CustomTheme.TEXT_COLOR);
                
                if (value != null) {
                    label.setText(value.toString());
                } else {
                    label.setText("");
                }
                
                if (column == 9 && value != null) {
                    String status = value.toString();
                    label.setHorizontalAlignment(SwingConstants.CENTER);
                    switch(status) {
                        case "Paid":
                            label.setForeground(CustomTheme.SUCCESS_COLOR);
                            label.setText("✅ " + status);
                            break;
                        case "Partial":
                            label.setForeground(CustomTheme.WARNING_COLOR);
                            label.setText("💳 " + status);
                            break;
                        case "Pending":
                            label.setForeground(CustomTheme.DANGER_COLOR);
                            label.setText("⏰ " + status);
                            break;
                        default:
                            label.setText(status);
                    }
                } else if ((column == 6 || column == 7 || column == 8) && value instanceof Double) {
                    label.setText(String.format("₹%,.2f", (Double) value));
                    label.setHorizontalAlignment(SwingConstants.RIGHT);
                } else {
                    label.setHorizontalAlignment(SwingConstants.CENTER);
                }
                
                if (isSelected) {
                    label.setBackground(new Color(25, 118, 210, 50));
                } else {
                    label.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 249, 250));
                }
                
                label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return label;
            }
        });
        
        TableColumnModel columnModel = invoiceTable.getColumnModel();
        columnModel.getColumn(0).setMaxWidth(50);
        columnModel.getColumn(1).setPreferredWidth(100);
        columnModel.getColumn(2).setPreferredWidth(150);
        columnModel.getColumn(3).setPreferredWidth(80);
        columnModel.getColumn(4).setPreferredWidth(100);
        columnModel.getColumn(5).setPreferredWidth(100);
        columnModel.getColumn(6).setPreferredWidth(120);
        columnModel.getColumn(7).setPreferredWidth(120);
        columnModel.getColumn(8).setPreferredWidth(120);
        columnModel.getColumn(9).setPreferredWidth(100);
        
        invoiceTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showInvoicePreview();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(invoiceTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(CustomTheme.LIGHT_COLOR));
        scrollPane.getViewport().setBackground(Color.WHITE);
        CustomTheme.styleScrollPane(scrollPane);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createInvoicePreviewPanel() {
        invoicePreviewPanel = new JPanel(new BorderLayout());
        invoicePreviewPanel.setBackground(CustomTheme.CARD_BACKGROUND_COLOR);
        invoicePreviewPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 1, 0, 0, CustomTheme.LIGHT_COLOR),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel initialLabel = CustomTheme.createHeaderLabel("Select an invoice to preview");
        initialLabel.setHorizontalAlignment(SwingConstants.CENTER);
        initialLabel.setForeground(CustomTheme.GRAY_COLOR);
        invoicePreviewPanel.add(initialLabel, BorderLayout.CENTER);
        
        return invoicePreviewPanel;
    }
    
    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(CustomTheme.BACKGROUND_COLOR);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        
        JPanel statsGrid = new JPanel(new GridLayout(1, 3, 20, 0));
        statsGrid.setOpaque(false);
        
        totalRevenueLabel = new JLabel("₹0.00");
        paidAmountLabel = new JLabel("₹0.00");
        pendingAmountLabel = new JLabel("₹0.00");
        
        statsGrid.add(createStatCard("💰", "Total Revenue", totalRevenueLabel, CustomTheme.PRIMARY_COLOR));
        statsGrid.add(createStatCard("✅", "Amount Collected", paidAmountLabel, CustomTheme.SUCCESS_COLOR));
        statsGrid.add(createStatCard("⏰", "Pending Amount", pendingAmountLabel, CustomTheme.WARNING_COLOR));
        
        bottomPanel.add(statsGrid, BorderLayout.CENTER);
        
        statusLabel = CustomTheme.createLabel("Ready");
        statusLabel.setFont(CustomTheme.SMALL_FONT);
        statusLabel.setForeground(CustomTheme.GRAY_COLOR);
        bottomPanel.add(statusLabel, BorderLayout.EAST);
        
        return bottomPanel;
    }
    
    private JPanel createStatCard(String icon, String title, JLabel valueLabel, Color color) {
        CustomTheme.ShadowPanel card = new CustomTheme.ShadowPanel(10, 2);
        card.setBackground(Color.WHITE);
        card.setLayout(new BorderLayout(10, 5));
        card.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(CustomTheme.getEmojiFont().deriveFont(28f));
        
        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        textPanel.setOpaque(false);
        
        JLabel titleLabelComp = CustomTheme.createLabel(title);
        titleLabelComp.setFont(CustomTheme.SMALL_FONT);
        titleLabelComp.setForeground(CustomTheme.GRAY_COLOR);
        
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        valueLabel.setForeground(color);
        
        textPanel.add(titleLabelComp);
        textPanel.add(valueLabel);
        
        card.add(iconLabel, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private void loadInvoices() {
        currentBookings = dataManager.getAllBookings();
        updateTableData(currentBookings);
        updateStatistics(currentBookings);
        statusLabel.setText("Loaded " + currentBookings.size() + " invoices");
    }
    
    private void updateTableData(List<Booking> bookings) {
        tableModel.setRowCount(0);
        for (Booking booking : bookings) {
            String guestName = booking.getGuest() != null ? 
                booking.getGuest().getFirstName() + " " + booking.getGuest().getLastName() : "N/A";
            String roomNumber = booking.getRoom() != null ? booking.getRoom().getRoomNumber() : "N/A";
            
            tableModel.addRow(new Object[]{
                booking.getBookingId(),
                booking.getBookingNumber(),
                guestName,
                roomNumber,
                booking.getCheckInDate() != null ? booking.getCheckInDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A",
                booking.getCheckOutDate() != null ? booking.getCheckOutDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A",
                booking.getTotalAmount(),
                booking.getPaidAmount(),
                booking.getDueAmount(),
                booking.getPaymentStatus()
            });
        }
    }
    
    private void updateStatistics(List<Booking> bookings) {
        double totalRevenue = bookings.stream()
            .filter(b -> "Checked-out".equals(b.getStatus()) || "Completed".equals(b.getStatus()))
            .mapToDouble(Booking::getTotalAmount).sum();
        
        double totalPaid = bookings.stream()
            .mapToDouble(Booking::getPaidAmount).sum();
        
        double totalPending = bookings.stream()
            .mapToDouble(Booking::getDueAmount).sum();
        
        totalRevenueLabel.setText(String.format("₹%,.2f", totalRevenue));
        paidAmountLabel.setText(String.format("₹%,.2f", totalPaid));
        pendingAmountLabel.setText(String.format("₹%,.2f", totalPending));
    }
    
    private void filterByPeriod() {
        String period = (String) periodFilterCombo.getSelectedItem();
        if (period == null || period.equals("All Time")) {
            loadInvoices();
            return;
        }
        
        LocalDate now = LocalDate.now();
        List<Booking> filtered = new java.util.ArrayList<>();
        
        for (Booking booking : currentBookings) {
            LocalDate checkIn = booking.getCheckInDate();
            if (checkIn == null) continue;
            
            switch(period) {
                case "Today":
                    if (checkIn.equals(now)) filtered.add(booking);
                    break;
                case "This Week":
                    if (!checkIn.isBefore(now) && !checkIn.isAfter(now.plusDays(7))) filtered.add(booking);
                    break;
                case "This Month":
                    if (checkIn.getMonth() == now.getMonth() && checkIn.getYear() == now.getYear()) filtered.add(booking);
                    break;
                default:
                    filtered = currentBookings;
                    break;
            }
        }
        updateTableData(filtered);
        statusLabel.setText("Showing " + filtered.size() + " invoices from " + period);
    }
    
    private void filterByStatus() {
        String status = (String) statusFilterCombo.getSelectedItem();
        if (status == null || status.equals("All")) {
            loadInvoices();
        } else {
            List<Booking> filtered = new java.util.ArrayList<>();
            for (Booking booking : currentBookings) {
                if (booking.getPaymentStatus().equals(status)) {
                    filtered.add(booking);
                }
            }
            updateTableData(filtered);
            statusLabel.setText("Showing " + filtered.size() + " " + status.toLowerCase() + " invoices");
        }
    }
    
    private void searchInvoices() {
        String searchTerm = searchField.getText().trim().toLowerCase();
        if (searchTerm.isEmpty()) {
            loadInvoices();
        } else {
            List<Booking> filtered = new java.util.ArrayList<>();
            for (Booking booking : currentBookings) {
                String guestName = booking.getGuest() != null ? 
                    (booking.getGuest().getFirstName() + " " + booking.getGuest().getLastName()).toLowerCase() : "";
                if (booking.getBookingNumber().toLowerCase().contains(searchTerm) ||
                    guestName.contains(searchTerm)) {
                    filtered.add(booking);
                }
            }
            updateTableData(filtered);
            statusLabel.setText("Found " + filtered.size() + " invoices matching '" + searchTerm + "'");
        }
    }
    
    private void showInvoicePreview() {
        int selectedRow = invoiceTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        int bookingId = (int) tableModel.getValueAt(selectedRow, 0);
        Booking booking = dataManager.getBookingById(bookingId);
        
        if (booking != null) {
            updateInvoicePreview(booking);
        }
    }
    
    private void updateInvoicePreview(Booking booking) {
        invoicePreviewPanel.removeAll();
        
        JPanel invoiceCard = new JPanel();
        invoiceCard.setLayout(new BoxLayout(invoiceCard, BoxLayout.Y_AXIS));
        invoiceCard.setBackground(Color.WHITE);
        invoiceCard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, CustomTheme.PRIMARY_COLOR));
        
        JLabel hotelName = new JLabel("🏨 GRAND HOTEL");
        hotelName.setFont(new Font("Segoe UI", Font.BOLD, 22));
        hotelName.setForeground(CustomTheme.PRIMARY_COLOR);
        headerPanel.add(hotelName, BorderLayout.WEST);
        
        JLabel invoiceTitle = new JLabel("TAX INVOICE");
        invoiceTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        invoiceTitle.setForeground(CustomTheme.DARK_COLOR);
        headerPanel.add(invoiceTitle, BorderLayout.EAST);
        
        invoiceCard.add(headerPanel);
        invoiceCard.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Invoice Info
        JPanel infoPanel = new JPanel(new GridLayout(4, 2, 15, 10));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        infoPanel.add(CustomTheme.createLabel("Invoice #:"));
        infoPanel.add(CustomTheme.createLabel(booking.getBookingNumber()));
        infoPanel.add(CustomTheme.createLabel("Date:"));
        infoPanel.add(CustomTheme.createLabel(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
        infoPanel.add(CustomTheme.createLabel("Guest:"));
        infoPanel.add(CustomTheme.createLabel(booking.getGuest() != null ? 
            booking.getGuest().getFirstName() + " " + booking.getGuest().getLastName() : "N/A"));
        infoPanel.add(CustomTheme.createLabel("Room:"));
        infoPanel.add(CustomTheme.createLabel(booking.getRoom() != null ? booking.getRoom().getRoomNumber() : "N/A"));
        
        invoiceCard.add(infoPanel);
        invoiceCard.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Booking Dates
        JPanel datePanel = new JPanel(new GridLayout(2, 2, 15, 10));
        datePanel.setBackground(Color.WHITE);
        
        datePanel.add(CustomTheme.createLabel("Check-in:"));
        datePanel.add(CustomTheme.createLabel(booking.getCheckInDate() != null ? 
            booking.getCheckInDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A"));
        datePanel.add(CustomTheme.createLabel("Check-out:"));
        datePanel.add(CustomTheme.createLabel(booking.getCheckOutDate() != null ? 
            booking.getCheckOutDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A"));
        
        invoiceCard.add(datePanel);
        invoiceCard.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Charges
        JPanel chargesPanel = new JPanel(new GridLayout(4, 2, 15, 10));
        chargesPanel.setBackground(Color.WHITE);
        chargesPanel.setBorder(BorderFactory.createTitledBorder("Charges Breakdown"));
        
        chargesPanel.add(CustomTheme.createLabel("Room Charges:"));
        chargesPanel.add(CustomTheme.createLabel(String.format("₹%,.2f", booking.getTotalAmount())));
        chargesPanel.add(CustomTheme.createLabel("Tax (18%):"));
        chargesPanel.add(CustomTheme.createLabel(String.format("₹%,.2f", booking.getTotalAmount() * 0.18)));
        chargesPanel.add(CustomTheme.createLabel("Discount:"));
        chargesPanel.add(CustomTheme.createLabel(String.format("₹%,.2f", booking.getDiscountAmount())));
        
        JLabel totalLabel = CustomTheme.createLabel("TOTAL:");
        totalLabel.setFont(CustomTheme.HEADER_FONT);
        chargesPanel.add(totalLabel);
        
        JLabel totalAmountLabel = CustomTheme.createLabel(String.format("₹%,.2f", booking.getTotalAmount()));
        totalAmountLabel.setFont(CustomTheme.HEADER_FONT);
        totalAmountLabel.setForeground(CustomTheme.PRIMARY_COLOR);
        chargesPanel.add(totalAmountLabel);
        
        invoiceCard.add(chargesPanel);
        invoiceCard.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Payment Status
        JPanel paymentPanel = new JPanel(new GridLayout(3, 2, 15, 10));
        paymentPanel.setBackground(Color.WHITE);
        paymentPanel.setBorder(BorderFactory.createTitledBorder("Payment Status"));
        
        paymentPanel.add(CustomTheme.createLabel("Total Amount:"));
        paymentPanel.add(CustomTheme.createLabel(String.format("₹%,.2f", booking.getTotalAmount())));
        paymentPanel.add(CustomTheme.createLabel("Paid Amount:"));
        paymentPanel.add(CustomTheme.createLabel(String.format("₹%,.2f", booking.getPaidAmount())));
        paymentPanel.add(CustomTheme.createLabel("Due Amount:"));
        JLabel dueLabel = CustomTheme.createLabel(String.format("₹%,.2f", booking.getDueAmount()));
        dueLabel.setForeground(booking.getDueAmount() > 0 ? CustomTheme.WARNING_COLOR : CustomTheme.SUCCESS_COLOR);
        paymentPanel.add(dueLabel);
        
        invoiceCard.add(paymentPanel);
        invoiceCard.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Action Buttons
        if (booking.getDueAmount() > 0) {
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
            buttonPanel.setBackground(Color.WHITE);
            
            CustomTheme.ModernButton payButton = new CustomTheme.ModernButton("💰 Process Payment", CustomTheme.SUCCESS_COLOR);
            payButton.addActionListener(e -> {
                String amount = JOptionPane.showInputDialog(this, "Enter payment amount:", booking.getDueAmount());
                if (amount != null) {
                    try {
                        double amt = Double.parseDouble(amount);
                        dataManager.addPayment(booking.getBookingId(), amt);
                        loadInvoices();
                        updateInvoicePreview(dataManager.getBookingById(booking.getBookingId()));
                        JOptionPane.showMessageDialog(this, "Payment of ₹" + String.format("%.2f", amt) + " processed!");
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "Invalid amount!");
                    }
                }
            });
            buttonPanel.add(payButton);
            invoiceCard.add(buttonPanel);
        }
        
        JScrollPane scrollPane = new JScrollPane(invoiceCard);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        invoicePreviewPanel.add(scrollPane, BorderLayout.CENTER);
        invoicePreviewPanel.revalidate();
        invoicePreviewPanel.repaint();
    }
    
    private void generateReport() {
        StringBuilder report = new StringBuilder();
        report.append("╔══════════════════════════════════════════════════════════════════╗\n");
        report.append("║                    FINANCIAL REPORT                              ║\n");
        report.append("╚══════════════════════════════════════════════════════════════════╝\n\n");
        report.append("Generated: ").append(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("\n\n");
        
        double totalRevenue = currentBookings.stream()
            .filter(b -> "Checked-out".equals(b.getStatus()))
            .mapToDouble(Booking::getTotalAmount).sum();
        double totalPaid = currentBookings.stream().mapToDouble(Booking::getPaidAmount).sum();
        double totalPending = currentBookings.stream().mapToDouble(Booking::getDueAmount).sum();
        
        report.append("REVENUE SUMMARY\n");
        report.append("----------------------------------------\n");
        report.append(String.format("Total Revenue: ₹%,.2f\n", totalRevenue));
        report.append(String.format("Amount Collected: ₹%,.2f\n", totalPaid));
        report.append(String.format("Pending Amount: ₹%,.2f\n", totalPending));
        report.append("\n");
        
        report.append("PAYMENT STATUS BREAKDOWN\n");
        report.append("----------------------------------------\n");
        long paidCount = currentBookings.stream().filter(b -> "Paid".equals(b.getPaymentStatus())).count();
        long partialCount = currentBookings.stream().filter(b -> "Partial".equals(b.getPaymentStatus())).count();
        long pendingCount = currentBookings.stream().filter(b -> "Pending".equals(b.getPaymentStatus())).count();
        
        report.append(String.format("Paid Invoices: %d\n", paidCount));
        report.append(String.format("Partial Payments: %d\n", partialCount));
        report.append(String.format("Pending Invoices: %d\n", pendingCount));
        report.append("\n");
        report.append("╔══════════════════════════════════════════════════════════════════╗\n");
        report.append("║                         End of Report                              ║\n");
        report.append("╚══════════════════════════════════════════════════════════════════╝\n");
        
        JTextArea reportArea = new JTextArea(report.toString());
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        reportArea.setForeground(CustomTheme.TEXT_COLOR);
        reportArea.setBackground(Color.WHITE);
        reportArea.setEditable(false);
        reportArea.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JScrollPane scrollPane = new JScrollPane(reportArea);
        scrollPane.setPreferredSize(new Dimension(600, 450));
        CustomTheme.styleScrollPane(scrollPane);
        
        JOptionPane.showMessageDialog(this, scrollPane, "Financial Report", JOptionPane.INFORMATION_MESSAGE);
    }
}