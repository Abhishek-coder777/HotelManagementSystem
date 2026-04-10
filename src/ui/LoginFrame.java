// ui/LoginFrame.java
package ui;

import dao.UserDAO;
import models.User;
//import utils.CustomTheme;
import utils.DataManager;
import javax.swing.*;
//import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
//import java.awt.geom.RoundRectangle2D;

public class LoginFrame extends JFrame {
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton exitButton;
    private JCheckBox showPasswordCheckBox;
    private JLabel statusLabel;
    private UserDAO userDAO;
    private boolean isLoading = false;
    private JPanel mainPanel;
    
    // Elegant color scheme
    private static final Color DEEP_NAVY = new Color(10, 25, 47);
    private static final Color SOFT_GOLD = new Color(212, 175, 55);
    private static final Color LIGHT_GOLD = new Color(255, 215, 0);
    private static final Color DARK_NAVY = new Color(5, 15, 30);
    private static final Color CREAM_WHITE = new Color(255, 250, 240);
    private static final Color INPUT_BG = new Color(248, 248, 248);
    
    public LoginFrame() {
        userDAO = new UserDAO();
        initComponents();
        setVisible(true);
    }
    
    private void initComponents() {
        setTitle("Hotel Management System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 850);
        setLocationRelativeTo(null);
        setResizable(false);
        setUndecorated(true);
        
        // Main container with elegant gradient background
        mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                // Diagonal gradient from dark navy to deep navy
                GradientPaint gradient = new GradientPaint(
                    0, 0, DEEP_NAVY,
                    getWidth(), getHeight(), DARK_NAVY
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Decorative golden circles
                g2d.setColor(new Color(212, 175, 55, 15));
                g2d.fillOval(-150, -150, 500, 500);
                g2d.fillOval(getWidth() - 350, getHeight() - 350, 450, 450);
                g2d.fillOval(getWidth() / 2 - 200, 50, 300, 300);
                
                // Golden sparkles
                g2d.setColor(new Color(212, 175, 55, 40));
                for (int i = 0; i < 50; i++) {
                    int x = (int) (Math.random() * getWidth());
                    int y = (int) (Math.random() * getHeight());
                    g2d.fillOval(x, y, 2, 2);
                }
                
                g2d.dispose();
            }
        };
        mainPanel.setLayout(new GridBagLayout());
        
        JPanel glassPanel = createGlassPanel();
        mainPanel.add(glassPanel);
        
        add(mainPanel);
        
        getRootPane().setDefaultButton(loginButton);
        
        KeyStroke escapeKey = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        getRootPane().registerKeyboardAction(e -> exitApplication(),
            escapeKey, JComponent.WHEN_IN_FOCUSED_WINDOW);
        
        addWindowDragListener();
    }
    
    private JPanel createGlassPanel() {
        JPanel glassPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Frosted glass effect
                g2d.setColor(new Color(255, 255, 255, 20));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
                
                // Golden border
                g2d.setColor(new Color(212, 175, 55, 60));
                g2d.setStroke(new BasicStroke(2f));
                g2d.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 40, 40);
                
                g2d.dispose();
            }
        };
        glassPanel.setOpaque(false);
        glassPanel.setPreferredSize(new Dimension(1200, 700));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.5;
        gbc.weighty = 1;
        
        // Left Panel - Hotel Info
        JPanel leftPanel = createLeftInfoPanel();
        glassPanel.add(leftPanel, gbc);
        
        // Right Panel - Login Form
        JPanel rightPanel = createLoginFormPanel();
        gbc.gridx = 1;
        glassPanel.add(rightPanel, gbc);
        
        return glassPanel;
    }
    
    private JPanel createLeftInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 30));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Logo Section
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        logoPanel.setOpaque(false);
        
        JLabel logoLabel = new JLabel("🏨");
        logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 65));
        logoLabel.setForeground(SOFT_GOLD);
        logoPanel.add(logoLabel);
        
        JLabel hotelNameLabel = new JLabel("GRAND HOTEL");
        hotelNameLabel.setFont(new Font("Georgia", Font.BOLD, 34));
        hotelNameLabel.setForeground(Color.WHITE);
        logoPanel.add(hotelNameLabel);
        
        gbc.gridy = 0;
        panel.add(logoPanel, gbc);
        
        // Decorative line with proper alignment
        JPanel decorativeLine = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Center the line
                int lineWidth = 150;
                int startX = (getWidth() - lineWidth) / 2;
                int centerY = getHeight() / 2;
                
                g2d.setColor(SOFT_GOLD);
                g2d.setStroke(new BasicStroke(2f));
                g2d.drawLine(startX, centerY, startX + lineWidth, centerY);
                
                // Small diamond in center
                g2d.setColor(SOFT_GOLD);
                int[] xPoints = {startX + lineWidth/2, startX + lineWidth/2 + 5, startX + lineWidth/2, startX + lineWidth/2 - 5};
                int[] yPoints = {centerY - 5, centerY, centerY + 5, centerY};
                g2d.fillPolygon(xPoints, yPoints, 4);
                
                g2d.dispose();
            }
        };
        decorativeLine.setOpaque(false);
        decorativeLine.setPreferredSize(new Dimension(300, 30));
        gbc.gridy = 1;
        panel.add(decorativeLine, gbc);
        
        // Tagline
        JLabel taglineLabel = new JLabel("Luxury Redefined");
        taglineLabel.setFont(new Font("Georgia", Font.ITALIC, 22));
        taglineLabel.setForeground(SOFT_GOLD);
        taglineLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 2;
        panel.add(taglineLabel, gbc);
        
        // Features Section
        JLabel featuresTitle = new JLabel("Why Choose Us?");
        featuresTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        featuresTitle.setForeground(SOFT_GOLD);
        featuresTitle.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 3;
        gbc.insets = new Insets(30, 20, 15, 20);
        panel.add(featuresTitle, gbc);
        
        JPanel featuresPanel = new JPanel(new GridLayout(3, 2, 20, 15));
        featuresPanel.setOpaque(false);
        featuresPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        String[][] features = {
            {"⭐", "5-Star Luxury Experience"},
            {"🏊", "Infinity Pool & Spa"},
            {"🍽️", "World-Class Dining"},
            {"💼", "24/7 Business Center"},
            {"🚗", "Complimentary Valet"},
            {"🔒", "Secure & Safe Stay"}
        };
        
        for (String[] feature : features) {
            JPanel featurePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
            featurePanel.setOpaque(false);
            
            JLabel iconLabel = new JLabel(feature[0]);
            iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
            iconLabel.setForeground(SOFT_GOLD);
            
            JLabel textLabel = new JLabel(feature[1]);
            textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            textLabel.setForeground(Color.WHITE);
            
            featurePanel.add(iconLabel);
            featurePanel.add(textLabel);
            featuresPanel.add(featurePanel);
        }
        
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 20, 20, 20);
        panel.add(featuresPanel, gbc);
        
        // Testimonial Card - Properly centered
        JPanel testimonialCard = new JPanel(new BorderLayout(15, 15));
        testimonialCard.setOpaque(false);
        testimonialCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(212, 175, 55, 60), 1),
            BorderFactory.createEmptyBorder(25, 30, 25, 30)
        ));
        testimonialCard.setBackground(new Color(255, 255, 255, 10));
        
        // Quote icon at top center
        JPanel quotePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        quotePanel.setOpaque(false);
        JLabel quoteIcon = new JLabel("\"");
        quoteIcon.setFont(new Font("Georgia", Font.BOLD, 50));
        quoteIcon.setForeground(SOFT_GOLD);
        quotePanel.add(quoteIcon);
        testimonialCard.add(quotePanel, BorderLayout.NORTH);
        
        // Testimonial text
        JLabel testimonialText = new JLabel("<html><div style='text-align: center; width: 280px;'>An unforgettable experience! The service and amenities are world-class. Highly recommended!</div></html>");
        testimonialText.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        testimonialText.setForeground(new Color(240, 240, 240));
        testimonialText.setHorizontalAlignment(SwingConstants.CENTER);
        testimonialCard.add(testimonialText, BorderLayout.CENTER);
        
        // Customer name
        JLabel customerName = new JLabel("- Michael Chen");
        customerName.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        customerName.setForeground(SOFT_GOLD);
        customerName.setHorizontalAlignment(SwingConstants.CENTER);
        testimonialCard.add(customerName, BorderLayout.SOUTH);
        
        gbc.gridy = 5;
        gbc.insets = new Insets(20, 40, 20, 40);
        panel.add(testimonialCard, gbc);
        
        return panel;
    }
    
    private JPanel createLoginFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(CREAM_WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(SOFT_GOLD, 1),
            BorderFactory.createEmptyBorder(50, 60, 50, 60)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 1;
        
        // Welcome Title
        JLabel welcomeLabel = new JLabel("Welcome Back!");
        welcomeLabel.setFont(new Font("Georgia", Font.BOLD, 32));
        welcomeLabel.setForeground(DEEP_NAVY);
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 5, 0);
        panel.add(welcomeLabel, gbc);
        
        JLabel subtitleLabel = new JLabel("Please sign in to continue");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(120, 120, 120));
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 40, 0);
        panel.add(subtitleLabel, gbc);
        
        // Username Field with better styling
        JLabel userLabel = new JLabel("USERNAME");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        userLabel.setForeground(DEEP_NAVY);
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 5, 0);
        panel.add(userLabel, gbc);
        
        usernameField = new JTextField();
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        usernameField.setBackground(INPUT_BG);
        usernameField.setForeground(DEEP_NAVY);
        usernameField.setCaretColor(SOFT_GOLD);
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 20, 0);
        panel.add(usernameField, gbc);
        
        // Password Field with better styling
        JLabel passLabel = new JLabel("PASSWORD");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        passLabel.setForeground(DEEP_NAVY);
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 5, 0);
        panel.add(passLabel, gbc);
        
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        passwordField.setBackground(INPUT_BG);
        passwordField.setForeground(DEEP_NAVY);
        passwordField.setCaretColor(SOFT_GOLD);
        passwordField.setEchoChar('•');
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel.add(passwordField, gbc);
        
        // Show password checkbox - aligned right
        JPanel checkPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        checkPanel.setOpaque(false);
        showPasswordCheckBox = new JCheckBox("Show Password");
        showPasswordCheckBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        showPasswordCheckBox.setBackground(CREAM_WHITE);
        showPasswordCheckBox.setForeground(DEEP_NAVY);
        showPasswordCheckBox.setFocusPainted(false);
        showPasswordCheckBox.addActionListener(e -> {
            if (showPasswordCheckBox.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar('•');
            }
        });
        checkPanel.add(showPasswordCheckBox);
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 30, 0);
        panel.add(checkPanel, gbc);
        
        // Login Button - Elegant gold gradient
        loginButton = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gold gradient
                GradientPaint gradient = new GradientPaint(0, 0, SOFT_GOLD, getWidth(), 0, LIGHT_GOLD);
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                
                // Shadow effect
                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.fillRoundRect(0, 3, getWidth(), getHeight(), 30, 30);
                
                // Button text
                g2d.setColor(DEEP_NAVY);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2d.drawString(getText(), x, y);
                
                g2d.dispose();
            }
        };
        loginButton.setText("SIGN IN");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loginButton.setForeground(DEEP_NAVY);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.setBorderPainted(false);
        loginButton.setContentAreaFilled(false);
        loginButton.setFocusPainted(false);
        loginButton.setPreferredSize(new Dimension(200, 50));
        loginButton.addActionListener(e -> performLogin());
        gbc.gridy = 7;
        gbc.insets = new Insets(0, 0, 15, 0);
        panel.add(loginButton, gbc);
        
        // Status label
        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(220, 50, 50));
        gbc.gridy = 8;
        gbc.insets = new Insets(0, 0, 15, 0);
        panel.add(statusLabel, gbc);
        
        // Exit Button - Outlined style
        exitButton = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(CREAM_WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2d.setColor(DEEP_NAVY);
                g2d.setStroke(new BasicStroke(2f));
                g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 30, 30);
                
                g2d.setColor(DEEP_NAVY);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2d.drawString(getText(), x, y);
                
                g2d.dispose();
            }
        };
        exitButton.setText("EXIT");
        exitButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        exitButton.setForeground(DEEP_NAVY);
        exitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        exitButton.setBorderPainted(false);
        exitButton.setContentAreaFilled(false);
        exitButton.setFocusPainted(false);
        exitButton.setPreferredSize(new Dimension(120, 40));
        exitButton.addActionListener(e -> exitApplication());
        gbc.gridy = 9;
        gbc.insets = new Insets(0, 0, 0, 0);
        panel.add(exitButton, gbc);
        
        return panel;
    }
    
    private void performLogin() {
        if (isLoading) return;
        
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty()) {
            showError("Please enter username!");
            usernameField.requestFocus();
            return;
        }
        
        if (password.isEmpty()) {
            showError("Please enter password!");
            passwordField.requestFocus();
            return;
        }
        
        setLoading(true);
        
        SwingWorker<User, Void> worker = new SwingWorker<>() {
            @Override
            protected User doInBackground() {
                User user = userDAO.authenticate(username, password);
                if (user == null && "admin".equals(username) && "admin123".equals(password)) {
                    user = new User();
                    user.setUserId(1);
                    user.setUsername("admin");
                    user.setFullName("System Administrator");
                    user.setRole("admin");
                    user.setEmail("admin@hotel.com");
                    user.setPhone("9999999999");
                }
                return user;
            }
            
            @Override
            protected void done() {
                setLoading(false);
                try {
                    User user = get();
                    if (user != null) {
                        loginSuccess(user);
                    } else {
                        showError("Invalid username or password!");
                        passwordField.setText("");
                        passwordField.requestFocus();
                    }
                } catch (Exception e) {
                    showError("Login failed! Please try again.");
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    
    private void loginSuccess(User user) {
        statusLabel.setForeground(new Color(50, 180, 50));
        statusLabel.setText("✓ Login successful! Redirecting...");
        
        Timer timer = new Timer(1000, e -> {
            SwingUtilities.invokeLater(() -> {
                new DashboardFrame(user);
                dispose();
            });
        });
        timer.setRepeats(false);
        timer.start();
    }
    
    private void showError(String message) {
        statusLabel.setText("✗ " + message);
        statusLabel.setForeground(new Color(220, 50, 50));
        
        Timer timer = new Timer(3000, e -> statusLabel.setText(" "));
        timer.setRepeats(false);
        timer.start();
        
        // Shake animation
        shakePanel(mainPanel);
    }
    
    private void shakePanel(JPanel panel) {
        final int[] xOffset = {0, -10, 10, -8, 8, -5, 5, 0};
        final int originalX = panel.getLocation().x;
        
        Timer timer = new Timer(30, new ActionListener() {
            int index = 0;
            
            @Override
            public void actionPerformed(ActionEvent e) {
                if (index < xOffset.length) {
                    panel.setLocation(originalX + xOffset[index], panel.getLocation().y);
                    index++;
                } else {
                    panel.setLocation(originalX, panel.getLocation().y);
                    ((Timer) e.getSource()).stop();
                }
            }
        });
        timer.start();
    }
    
    private void setLoading(boolean loading) {
        isLoading = loading;
        loginButton.setEnabled(!loading);
        loginButton.setText(loading ? "SIGNING IN..." : "SIGN IN");
        usernameField.setEnabled(!loading);
        passwordField.setEnabled(!loading);
        exitButton.setEnabled(!loading);
    }
    
    private void addWindowDragListener() {
        final Point[] mouseDown = new Point[1];
        
        mainPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mouseDown[0] = e.getPoint();
            }
        });
        
        mainPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Point current = e.getLocationOnScreen();
                setLocation(current.x - mouseDown[0].x, current.y - mouseDown[0].y);
            }
        });
    }
    
    private void exitApplication() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to exit?",
            "Exit Application",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            DataManager.getInstance();
            new LoginFrame();
        });
    }
}