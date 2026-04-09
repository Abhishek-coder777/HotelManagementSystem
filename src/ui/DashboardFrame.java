// ui/DashboardFrame.java
package ui;

//import dao.*;
import models.*;
import utils.CustomTheme;
import utils.DataManager;

import javax.swing.*;
//import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
//import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class DashboardFrame extends JFrame {

    private User currentUser;
    private JPanel contentPanel;
    private JLabel clockLabel;
    private JLabel statusLabel;
    private JPanel sidebarPanel;
    private CardLayout cardLayout;
    private Timer clockTimer;

    private CustomTheme.StatsCard totalRoomsCard;
    private CustomTheme.StatsCard availableRoomsCard;
    private CustomTheme.StatsCard occupiedRoomsCard;
    private CustomTheme.StatsCard todayBookingsCard;
    private CustomTheme.StatsCard monthlyRevenueCard;
    private CustomTheme.StatsCard activeGuestsCard;

    // private RoomDAO roomDAO;
    // private BookingDAO bookingDAO;
    // private GuestDAO guestDAO;

    private JButton[] menuButtons;
    private String[] menuNames = { "Dashboard", "Rooms", "Guests", "Bookings", "Billing", "Reports", "Settings",
            "Help" };
    private String[] menuIcons = { "📊", "🏠", "👥", "📅", "💰", "📈", "⚙️", "❓" };
    private String[] menuPanelNames = { "dashboard", "rooms", "guests", "bookings", "billing", "reports", "settings",
            "help" };

    public DashboardFrame(User user) {
        this.currentUser = user;
        // this.roomDAO = new RoomDAO();
        // this.bookingDAO = new BookingDAO();
        // this.guestDAO = new GuestDAO();
        initComponents();
        startClock();
        loadDashboardData();
        setVisible(true);
    }

    private void initComponents() {
        setTitle("Hotel Management System - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1200, 700));
        setLocationRelativeTo(null);

        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(CustomTheme.BACKGROUND_COLOR);

        mainContainer.add(createHeader(), BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                createSidebar(), createContentArea());
        splitPane.setDividerLocation(280);
        splitPane.setDividerSize(0);
        splitPane.setBorder(null);

        mainContainer.add(splitPane, BorderLayout.CENTER);
        mainContainer.add(createStatusBar(), BorderLayout.SOUTH);

        add(mainContainer);
    }

    private JPanel createHeader() {
        JPanel headerPanel = new CustomTheme.GradientPanel(CustomTheme.PRIMARY_COLOR, CustomTheme.SECONDARY_COLOR);
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(getWidth(), 80));

        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        logoPanel.setOpaque(false);

        JLabel logoLabel = new JLabel("🏨");
        logoLabel.setFont(CustomTheme.getEmojiFont().deriveFont(36f));
        logoLabel.setForeground(Color.WHITE);
        logoPanel.add(logoLabel);

        JLabel titleLabel = new JLabel("Grand Hotel Management");
        titleLabel.setFont(CustomTheme.TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        logoPanel.add(titleLabel);

        headerPanel.add(logoPanel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        rightPanel.setOpaque(false);

        clockLabel = new JLabel();
        clockLabel.setFont(CustomTheme.HEADER_FONT);
        clockLabel.setForeground(Color.WHITE);
        rightPanel.add(clockLabel);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        userPanel.setOpaque(false);

        JLabel userIcon = new JLabel("👤");
        userIcon.setFont(CustomTheme.getEmojiFont().deriveFont(22f));
        userPanel.add(userIcon);

        JLabel userNameLabel = new JLabel(currentUser.getFullName());
        userNameLabel.setFont(CustomTheme.NORMAL_FONT);
        userNameLabel.setForeground(Color.WHITE);
        userPanel.add(userNameLabel);

        JLabel roleLabel = new JLabel("(" + currentUser.getRole() + ")");
        roleLabel.setFont(CustomTheme.SMALL_FONT);
        roleLabel.setForeground(new Color(255, 255, 255, 200));
        userPanel.add(roleLabel);

        rightPanel.add(userPanel);

        headerPanel.add(rightPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createSidebar() {
        sidebarPanel = new JPanel();
        sidebarPanel.setBackground(CustomTheme.SIDEBAR_COLOR);
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));

        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        menuButtons = new JButton[menuNames.length];

        for (int i = 0; i < menuNames.length; i++) {
            JButton menuButton = createSidebarButton(menuIcons[i] + "  " + menuNames[i]);
            final String panelName = menuPanelNames[i];
            final int index = i;
            menuButton.addActionListener(e -> {
                cardLayout.show(contentPanel, panelName);
                updateActiveMenu(index);
                statusLabel.setText("Viewing: " + menuNames[index]);
            });
            sidebarPanel.add(menuButton);
            sidebarPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            menuButtons[i] = menuButton;
        }

        updateActiveMenu(0);

        sidebarPanel.add(Box.createVerticalGlue());
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton logoutButton = createSidebarButton("🚪  Logout");
        logoutButton.addActionListener(e -> logout());
        sidebarPanel.add(logoutButton);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        return sidebarPanel;
    }

    private JButton createSidebarButton(String text) {
        JButton button = new JButton(text);
        button.setFont(CustomTheme.getEmojiFont().deriveFont(Font.BOLD, 14f));
        button.setForeground(Color.WHITE);
        button.setBackground(CustomTheme.SIDEBAR_COLOR);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.getBackground() != CustomTheme.PRIMARY_COLOR) {
                    button.setBackground(CustomTheme.PRIMARY_COLOR);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (button.getBackground() == CustomTheme.PRIMARY_COLOR &&
                        !button.getText().contains("Logout")) {
                    button.setBackground(CustomTheme.SIDEBAR_COLOR);
                }
            }
        });

        return button;
    }

    private void updateActiveMenu(int activeIndex) {
        for (int i = 0; i < menuButtons.length; i++) {
            if (i == activeIndex) {
                menuButtons[i].setBackground(CustomTheme.PRIMARY_COLOR);
            } else {
                menuButtons[i].setBackground(CustomTheme.SIDEBAR_COLOR);
            }
        }
    }

    private JPanel createContentArea() {
        contentPanel = new JPanel();
        cardLayout = new CardLayout();
        contentPanel.setLayout(cardLayout);
        contentPanel.setBackground(CustomTheme.BACKGROUND_COLOR);

        contentPanel.add(createDashboardPanel(), "dashboard");
        contentPanel.add(new RoomManagementPanel(), "rooms");
        contentPanel.add(new GuestManagementPanel(), "guests");
        contentPanel.add(new BookingPanel(), "bookings");
        contentPanel.add(new BillingPanel(), "billing");
        contentPanel.add(createReportsPanel(), "reports");
        contentPanel.add(createSettingsPanel(), "settings");
        contentPanel.add(createHelpPanel(), "help");

        cardLayout.show(contentPanel, "dashboard");

        return contentPanel;
    }

    private JPanel createDashboardPanel() {
        JPanel dashboard = new JPanel(new BorderLayout(15, 15));
        dashboard.setBackground(CustomTheme.BACKGROUND_COLOR);
        dashboard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Welcome Banner
        JPanel welcomeCard = new CustomTheme.RoundedPanel(12, new Color(66, 165, 245, 50));
        welcomeCard.setLayout(new BorderLayout());
        welcomeCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CustomTheme.SECONDARY_COLOR, 1),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)));

        JLabel welcomeLabel = new JLabel("Welcome back, " + currentUser.getFullName() + "! 👋");
        welcomeLabel.setFont(CustomTheme.HEADER_FONT);
        welcomeLabel.setForeground(CustomTheme.DARK_COLOR);
        welcomeCard.add(welcomeLabel, BorderLayout.WEST);

        JLabel dateLabel = new JLabel();
        dateLabel.setFont(CustomTheme.NORMAL_FONT);
        dateLabel.setForeground(CustomTheme.DARK_COLOR);
        dateLabel.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy")));
        welcomeCard.add(dateLabel, BorderLayout.EAST);

        dashboard.add(welcomeCard, BorderLayout.NORTH);

        // Stats Grid
        JPanel statsGrid = new JPanel(new GridLayout(2, 3, 20, 20));
        statsGrid.setOpaque(false);
        statsGrid.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        totalRoomsCard = new CustomTheme.StatsCard("🏨", "Total Rooms", CustomTheme.PRIMARY_COLOR);
        availableRoomsCard = new CustomTheme.StatsCard("✅", "Available Rooms", CustomTheme.SUCCESS_COLOR);
        occupiedRoomsCard = new CustomTheme.StatsCard("🔴", "Occupied Rooms", CustomTheme.WARNING_COLOR);
        todayBookingsCard = new CustomTheme.StatsCard("�", "Total Bookings", CustomTheme.INFO_COLOR);
        monthlyRevenueCard = new CustomTheme.StatsCard("💰", "Monthly Revenue", CustomTheme.PRIMARY_COLOR);
        activeGuestsCard = new CustomTheme.StatsCard("👥", "Active Guests", CustomTheme.SUCCESS_COLOR);

        statsGrid.add(totalRoomsCard);
        statsGrid.add(availableRoomsCard);
        statsGrid.add(occupiedRoomsCard);
        statsGrid.add(todayBookingsCard);
        statsGrid.add(monthlyRevenueCard);
        statsGrid.add(activeGuestsCard);

        dashboard.add(statsGrid, BorderLayout.CENTER);

        // Bottom Section
        JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 20, 20));
        bottomPanel.setOpaque(false);
        bottomPanel.add(createRecentBookingsPanel());
        bottomPanel.add(createQuickActionsPanel());

        dashboard.add(bottomPanel, BorderLayout.SOUTH);

        return dashboard;
    }

    private JPanel createRecentBookingsPanel() {
        CustomTheme.ShadowPanel card = new CustomTheme.ShadowPanel(12, 3);
        card.setBackground(Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(3, 0, 0, 0, CustomTheme.PRIMARY_COLOR),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        JLabel titleLabel = new JLabel("📋 Recent Bookings");
        titleLabel.setFont(CustomTheme.HEADER_FONT);
        titleLabel.setForeground(CustomTheme.PRIMARY_COLOR);
        card.add(titleLabel, BorderLayout.NORTH);

        String[] columns = { "Booking ID", "Guest", "Room", "Check-in", "Status" };
        Object[][] data = getRecentBookingsData();

        JTable table = new JTable(data, columns);
        CustomTheme.styleTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);
        CustomTheme.styleScrollPane(scrollPane);

        card.add(scrollPane, BorderLayout.CENTER);

        return card;
    }

    private JPanel createQuickActionsPanel() {
        CustomTheme.ShadowPanel card = new CustomTheme.ShadowPanel(12, 3);
        card.setBackground(Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(3, 0, 0, 0, CustomTheme.SUCCESS_COLOR),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        JLabel titleLabel = new JLabel("⚡ Quick Actions");
        titleLabel.setFont(CustomTheme.HEADER_FONT);
        titleLabel.setForeground(CustomTheme.SUCCESS_COLOR);
        card.add(titleLabel, BorderLayout.NORTH);

        JPanel actionsPanel = new JPanel(new GridLayout(4, 2, 15, 15));
        actionsPanel.setOpaque(false);
        actionsPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));

        String[][] actions = {
                { "➕", "New Booking", "#1976D2" },
                { "🏨", "Check-in", "#388E3C" },
                { "🚪", "Check-out", "#F57C00" },
                { "🏠", "Add Room", "#0097A7" },
                { "👥", "Add Guest", "#7B1FA2" },
                { "🔄", "Refresh", "#1976D2" },
                { "📊", "Generate Reports", "#C2185B" },
                { "⚙️", "Settings", "#455A64" }
        };

        for (String[] action : actions) {
            CustomTheme.ModernButton btn = new CustomTheme.ModernButton(action[0] + "  " + action[1],
                    Color.decode(action[2]));
            btn.setHorizontalAlignment(SwingConstants.LEFT);

            switch (action[1]) {
                case "New Booking":
                    btn.addActionListener(e -> showNewBookingDialog());
                    break;
                case "Check-in":
                    btn.addActionListener(e -> showCheckInDialog());
                    break;
                case "Check-out":
                    btn.addActionListener(e -> showCheckOutDialog());
                    break;
                case "Add Room":
                    btn.addActionListener(e -> cardLayout.show(contentPanel, "rooms"));
                    break;
                case "Add Guest":
                    btn.addActionListener(e -> cardLayout.show(contentPanel, "guests"));
                    break;
                case "Refresh":
                    btn.addActionListener(e -> refreshDashboard());
                    break;
                case "Generate Reports":
                    btn.addActionListener(e -> cardLayout.show(contentPanel, "reports"));
                    break;
                case "Settings":
                    btn.addActionListener(e -> cardLayout.show(contentPanel, "settings"));
                    break;
            }
            actionsPanel.add(btn);
        }

        card.add(actionsPanel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(CustomTheme.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        headerPanel.setBackground(CustomTheme.PANEL_HEADER_COLOR);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, CustomTheme.PRIMARY_COLOR),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        JLabel titleIcon = new JLabel("📈");
        titleIcon.setFont(CustomTheme.getEmojiFont().deriveFont(32f));
        headerPanel.add(titleIcon);

        JLabel titleLabel = new JLabel("Reports & Analytics");
        titleLabel.setFont(CustomTheme.HEADER_FONT);
        titleLabel.setForeground(CustomTheme.DARK_COLOR);
        headerPanel.add(titleLabel);

        panel.add(headerPanel, BorderLayout.NORTH);

        JPanel cardsPanel = new JPanel(new GridLayout(2, 3, 20, 20));
        cardsPanel.setOpaque(false);
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        String[][] reports = {
                { "📊", "Occupancy Report", "View room occupancy rates", "#1976D2" },
                { "💰", "Revenue Report", "Track daily/monthly revenue", "#388E3C" },
                { "👥", "Guest Report", "Guest demographics", "#F57C00" },
                { "📅", "Booking Report", "Booking trends analysis", "#7B1FA2" },
                { "🏠", "Room Performance", "Revenue per room type", "#C2185B" },
                { "💳", "Payment Report", "Payment method breakdown", "#0097A7" }
        };

        for (String[] report : reports) {
            CustomTheme.ShadowPanel card = new CustomTheme.ShadowPanel(12, 3);
            card.setBackground(Color.WHITE);
            card.setLayout(new BorderLayout(10, 10));
            card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            card.setCursor(new Cursor(Cursor.HAND_CURSOR));

            JLabel icon = new JLabel(report[0]);
            icon.setFont(CustomTheme.getEmojiFont().deriveFont(40f));
            card.add(icon, BorderLayout.NORTH);

            JLabel name = new JLabel(report[1]);
            name.setFont(CustomTheme.HEADER_FONT);
            name.setForeground(CustomTheme.DARK_COLOR);
            card.add(name, BorderLayout.CENTER);

            JLabel desc = new JLabel(report[2]);
            desc.setFont(CustomTheme.SMALL_FONT);
            desc.setForeground(CustomTheme.GRAY_COLOR);
            card.add(desc, BorderLayout.SOUTH);

            final String reportName = report[1];
            card.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    showReportDialog(reportName);
                }
            });

            cardsPanel.add(card);
        }

        panel.add(cardsPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(CustomTheme.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        headerPanel.setBackground(CustomTheme.PANEL_HEADER_COLOR);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, CustomTheme.PRIMARY_COLOR),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        JLabel titleIcon = new JLabel("⚙️");
        titleIcon.setFont(CustomTheme.getEmojiFont().deriveFont(32f));
        headerPanel.add(titleIcon);

        JLabel titleLabel = new JLabel("Settings");
        titleLabel.setFont(CustomTheme.HEADER_FONT);
        titleLabel.setForeground(CustomTheme.DARK_COLOR);
        headerPanel.add(titleLabel);

        panel.add(headerPanel, BorderLayout.NORTH);

        JPanel settingsContent = new JPanel();
        settingsContent.setLayout(new BoxLayout(settingsContent, BoxLayout.Y_AXIS));
        settingsContent.setOpaque(false);
        settingsContent.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        settingsContent.add(createSettingsSection("👤 Profile Settings", new String[][] {
                { "Full Name", currentUser.getFullName() },
                { "Username", currentUser.getUsername() },
                { "Email", currentUser.getEmail() != null ? currentUser.getEmail() : "Not set" },
                { "Phone", currentUser.getPhone() != null ? currentUser.getPhone() : "Not set" },
                { "Role", currentUser.getRole() }
        }));
        settingsContent.add(Box.createRigidArea(new Dimension(0, 15)));

        settingsContent.add(createSettingsSection("🏨 Hotel Settings", new String[][] {
                { "Hotel Name", "Grand Hotel" },
                { "Address", "123 Luxury Avenue, Downtown City" },
                { "Phone", "+91-1234567890" },
                { "Email", "info@grandhotel.com" },
                { "GST Rate", "18%" },
                { "Check-in Time", "12:00 PM" },
                { "Check-out Time", "11:00 AM" }
        }));
        settingsContent.add(Box.createRigidArea(new Dimension(0, 15)));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setOpaque(false);

        CustomTheme.ModernButton changePasswordBtn = new CustomTheme.ModernButton("🔒 Change Password");
        changePasswordBtn.setBackground(CustomTheme.PRIMARY_COLOR);
        changePasswordBtn.addActionListener(e -> showChangePasswordDialog());
        buttonPanel.add(changePasswordBtn);

        settingsContent.add(buttonPanel);

        JScrollPane scrollPane = new JScrollPane(settingsContent);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(CustomTheme.BACKGROUND_COLOR);
        CustomTheme.styleScrollPane(scrollPane);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createSettingsSection(String title, String[][] settings) {
        CustomTheme.ShadowPanel section = new CustomTheme.ShadowPanel(12, 3);
        section.setBackground(Color.WHITE);
        section.setLayout(new BorderLayout());
        section.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel sectionTitle = new JLabel(title);
        sectionTitle.setFont(CustomTheme.HEADER_FONT);
        sectionTitle.setForeground(CustomTheme.PRIMARY_COLOR);
        section.add(sectionTitle, BorderLayout.NORTH);

        JPanel fieldsPanel = new JPanel(new GridLayout(settings.length, 2, 15, 12));
        fieldsPanel.setOpaque(false);
        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        for (String[] setting : settings) {
            JLabel keyLabel = new JLabel(setting[0] + ":");
            keyLabel.setFont(CustomTheme.NORMAL_FONT);
            keyLabel.setForeground(CustomTheme.GRAY_COLOR);
            fieldsPanel.add(keyLabel);

            JLabel valueLabel = new JLabel(setting[1]);
            valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            valueLabel.setForeground(CustomTheme.DARK_COLOR);
            fieldsPanel.add(valueLabel);
        }

        section.add(fieldsPanel, BorderLayout.CENTER);
        return section;
    }

    private JPanel createHelpPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(CustomTheme.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        CustomTheme.ShadowPanel card = new CustomTheme.ShadowPanel(12, 3);
        card.setBackground(Color.WHITE);
        card.setLayout(new BorderLayout());

        JTextArea helpText = new JTextArea();
        helpText.setFont(new Font("Monospaced", Font.PLAIN, 12));
        helpText.setForeground(CustomTheme.DARK_COLOR);
        helpText.setBackground(Color.WHITE);
        helpText.setEditable(false);
        helpText.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String helpContent = "╔══════════════════════════════════════════════════════════════════╗\n" +
                "║                    HOTEL MANAGEMENT SYSTEM                       ║\n" +
                "║                          HELP GUIDE                              ║\n" +
                "╚══════════════════════════════════════════════════════════════════╝\n\n" +

                "📊 DASHBOARD\n" +
                "   • View key metrics and statistics at a glance\n" +
                "   • Monitor room occupancy, revenue, and bookings\n" +
                "   • Quick access to common operations\n\n" +

                "🏠 ROOM MANAGEMENT\n" +
                "   • Add, edit, or delete room information\n" +
                "   • Update room status\n" +
                "   • Set room pricing and amenities\n\n" +

                "👥 GUEST MANAGEMENT\n" +
                "   • Register new guests\n" +
                "   • Update guest information\n" +
                "   • Track guest stay history\n\n" +

                "📅 BOOKING MANAGEMENT\n" +
                "   • Create new reservations\n" +
                "   • Process check-in and check-out\n" +
                "   • Manage cancellations\n\n" +

                "💰 BILLING & INVOICING\n" +
                "   • Generate invoices\n" +
                "   • Process payments\n" +
                "   • Track payment status\n\n" +

                "📞 SUPPORT\n" +
                "   Email: support@grandhotel.com\n" +
                "   Phone: +91-1234567890\n\n" +

                "╔══════════════════════════════════════════════════════════════════╗\n" +
                "║              © 2024 Grand Hotel Management System                ║\n" +
                "╚══════════════════════════════════════════════════════════════════╝";

        helpText.setText(helpContent);

        JScrollPane scrollPane = new JScrollPane(helpText);
        scrollPane.setBorder(null);
        CustomTheme.styleScrollPane(scrollPane);
        card.add(scrollPane, BorderLayout.CENTER);

        panel.add(card, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(CustomTheme.SIDEBAR_COLOR);
        statusBar.setPreferredSize(new Dimension(getWidth(), 35));
        statusBar.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        statusLabel = new JLabel("Ready");
        statusLabel.setFont(CustomTheme.SMALL_FONT);
        statusLabel.setForeground(Color.WHITE);
        statusBar.add(statusLabel, BorderLayout.WEST);

        JLabel connectionLabel = new JLabel("● Database Connected");
        connectionLabel.setFont(CustomTheme.SMALL_FONT);
        connectionLabel.setForeground(CustomTheme.SUCCESS_COLOR);
        statusBar.add(connectionLabel, BorderLayout.EAST);

        return statusBar;
    }

    private void refreshDashboard() {
        statusLabel.setText("Refreshing dashboard...");
        loadDashboardData();
        statusLabel.setText("Dashboard refreshed");
    }

    //In DashboardFrame.java, replace the loadDashboardData method with:
    private void loadDashboardData() {
        DataManager dm = DataManager.getInstance();

        int[] roomStats = dm.getRoomStatistics();
        totalRoomsCard.setValue(roomStats[0]);
        availableRoomsCard.setValue(roomStats[1]);
        occupiedRoomsCard.setValue(roomStats[2]);

        int[] bookingStats = dm.getBookingStatistics();
        todayBookingsCard.setValue(bookingStats[0]);

        double[] revenueStats = dm.getRevenueStatistics();
        monthlyRevenueCard.setValue(revenueStats[2]);

        int[] guestStats = dm.getGuestStatistics();
        activeGuestsCard.setValue(guestStats[1]);
    }

    private Object[][] getRecentBookingsData() {
        return new Object[][] {
                { "BK001", "John Doe", "101", "01/04/2026", "Checked-in" },
                { "BK002", "Jane Smith", "201", "02/04/2026", "Confirmed" },
                { "BK003", "Raj Patel", "301", "03/04/2026", "Pending" },
                { "BK004", "Maria Garcia", "102", "04/04/2026", "Confirmed" },
                { "BK005", "Chen Wei", "202", "05/04/2026", "Pending" }
        };
    }

    private void showCheckInDialog() {
        JDialog dialog = new JDialog(this, "Guest Check-in", true);
        dialog.setSize(450, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Booking Number:"), gbc);
        gbc.gridx = 1;
        CustomTheme.ModernTextField bookingField = new CustomTheme.ModernTextField(15);
        bookingField.setPreferredSize(new Dimension(200, 40));
        panel.add(bookingField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        CustomTheme.ModernButton checkInButton = new CustomTheme.ModernButton("Process Check-in",
                CustomTheme.SUCCESS_COLOR);
        checkInButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(dialog, "Check-in successful for booking: " + bookingField.getText());
            dialog.dispose();
            loadDashboardData();
        });
        panel.add(checkInButton, gbc);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private void showCheckOutDialog() {
        JDialog dialog = new JDialog(this, "Guest Check-out", true);
        dialog.setSize(450, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Booking Number:"), gbc);
        gbc.gridx = 1;
        CustomTheme.ModernTextField bookingField = new CustomTheme.ModernTextField(15);
        bookingField.setPreferredSize(new Dimension(200, 40));
        panel.add(bookingField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        CustomTheme.ModernButton checkOutButton = new CustomTheme.ModernButton("Process Check-out",
                CustomTheme.WARNING_COLOR);
        checkOutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(dialog,
                    "Total Amount: ₹5,000.00\nPaid: ₹5,000.00\nDue: ₹0.00\n\nProceed with check-out?",
                    "Confirm Check-out", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(dialog, "Check-out successful for booking: " + bookingField.getText());
                dialog.dispose();
                loadDashboardData();
            }
        });
        panel.add(checkOutButton, gbc);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private void showNewBookingDialog() {
        JDialog dialog = new JDialog(this, "Create New Booking", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Guest Name:"), gbc);
        gbc.gridx = 1;
        CustomTheme.ModernTextField guestField = new CustomTheme.ModernTextField(15);
        guestField.setPreferredSize(new Dimension(200, 40));
        panel.add(guestField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Room Number:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> roomCombo = new JComboBox<>(new String[] { "101", "102", "103", "201", "202" });
        CustomTheme.styleComboBox(roomCombo);
        panel.add(roomCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Check-in Date:"), gbc);
        gbc.gridx = 1;
        JTextField checkInField = new JTextField("01/04/2026", 15);
        checkInField.setFont(CustomTheme.NORMAL_FONT);
        panel.add(checkInField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Check-out Date:"), gbc);
        gbc.gridx = 1;
        JTextField checkOutField = new JTextField("05/04/2026", 15);
        checkOutField.setFont(CustomTheme.NORMAL_FONT);
        panel.add(checkOutField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        CustomTheme.ModernButton createButton = new CustomTheme.ModernButton("Create Booking",
                CustomTheme.SUCCESS_COLOR);
        createButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(dialog, "Booking created successfully!\nGuest: " + guestField.getText() +
                    "\nRoom: " + roomCombo.getSelectedItem() + "\nDates: " + checkInField.getText() + " to "
                    + checkOutField.getText());
            dialog.dispose();
            loadDashboardData();
        });
        panel.add(createButton, gbc);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private void showReportDialog(String reportName) {
        StringBuilder report = new StringBuilder();
        report.append("╔══════════════════════════════════════════════════════════════════╗\n");
        report.append("║                    ").append(String.format("%-40s", reportName.toUpperCase())).append("║\n");
        report.append("╚══════════════════════════════════════════════════════════════════╝\n\n");
        report.append("Generated: ")
                .append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))).append("\n\n");

        switch (reportName) {
            case "Occupancy Report":
                report.append("Total Rooms: 10\n");
                report.append("Available Rooms: 8\n");
                report.append("Occupied Rooms: 2\n");
                report.append("Occupancy Rate: 20.0%\n");
                break;
            case "Revenue Report":
                report.append("Total Revenue: ₹125,000.00\n");
                report.append("Today's Revenue: ₹5,000.00\n");
                report.append("Monthly Revenue: ₹45,000.00\n");
                report.append("Yearly Revenue: ₹125,000.00\n");
                break;
            case "Guest Report":
                report.append("Total Guests: 25\n");
                report.append("Active Guests: 12\n");
                report.append("VIP Guests: 3\n");
                break;
            case "Booking Report":
                report.append("Total Bookings: 15\n");
                report.append("Confirmed: 8\n");
                report.append("Checked-in: 2\n");
                report.append("Cancelled: 1\n");
                break;
            default:
                report.append("Report data loading...\n");
                break;
        }

        report.append("\n╔══════════════════════════════════════════════════════════════════╗\n");
        report.append("║                         End of Report                              ║\n");
        report.append("╚══════════════════════════════════════════════════════════════════╝\n");

        JTextArea reportArea = new JTextArea(report.toString());
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        reportArea.setForeground(CustomTheme.DARK_COLOR);
        reportArea.setBackground(Color.WHITE);
        reportArea.setEditable(false);
        reportArea.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JScrollPane scrollPane = new JScrollPane(reportArea);
        scrollPane.setPreferredSize(new Dimension(600, 450));
        CustomTheme.styleScrollPane(scrollPane);

        JOptionPane.showMessageDialog(this, scrollPane, reportName, JOptionPane.PLAIN_MESSAGE);
    }

    private void showChangePasswordDialog() {
        JDialog dialog = new JDialog(this, "Change Password", true);
        dialog.setSize(450, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Current Password:"), gbc);
        gbc.gridx = 1;
        JPasswordField currentPass = new CustomTheme.ModernPasswordField(15);
        formPanel.add(currentPass, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("New Password:"), gbc);
        gbc.gridx = 1;
        JPasswordField newPass = new CustomTheme.ModernPasswordField(15);
        formPanel.add(newPass, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Confirm Password:"), gbc);
        gbc.gridx = 1;
        JPasswordField confirmPass = new CustomTheme.ModernPasswordField(15);
        formPanel.add(confirmPass, gbc);

        dialog.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 20));
        buttonPanel.setBackground(Color.WHITE);

        CustomTheme.ModernButton saveBtn = new CustomTheme.ModernButton("Save", CustomTheme.SUCCESS_COLOR);
        saveBtn.addActionListener(e -> {
            String newPassword = new String(newPass.getPassword());
            String confirmPassword = new String(confirmPass.getPassword());
            if (newPassword.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Password cannot be empty!");
            } else if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(dialog, "Passwords do not match!");
            } else if (newPassword.length() < 6) {
                JOptionPane.showMessageDialog(dialog, "Password must be at least 6 characters!");
            } else {
                JOptionPane.showMessageDialog(dialog, "Password changed successfully!");
                dialog.dispose();
            }
        });

        CustomTheme.ModernButton cancelBtn = new CustomTheme.ModernButton("Cancel", CustomTheme.DANGER_COLOR);
        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void startClock() {
        clockTimer = new Timer();
        clockTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    LocalDateTime now = LocalDateTime.now();
                    String time = now.format(DateTimeFormatter.ofPattern("hh:mm:ss a"));
                    String date = now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    if (clockLabel != null) {
                        clockLabel.setText(date + "  |  " + time);
                    }
                });
            }
        }, 0, 1000);
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (clockTimer != null) {
                clockTimer.cancel();
            }
            dispose();
            new LoginFrame().setVisible(true);
        }
    }

    @Override
    public void dispose() {
        if (clockTimer != null) {
            clockTimer.cancel();
        }
        super.dispose();
    }
}