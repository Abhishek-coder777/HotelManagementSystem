// ui/LoginFrame.java
package ui;

import dao.UserDAO;
import models.User;
import utils.CustomTheme;
import utils.DataManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private CustomTheme.ModernButton loginButton;
    private CustomTheme.OutlinedButton exitButton;
    private JCheckBox showPasswordCheckBox;
    private JLabel statusLabel;
    private UserDAO userDAO;
    private boolean isLoading = false;

    public LoginFrame() {
        userDAO = new UserDAO();
        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        setTitle("Hotel Management System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainContainer = new JPanel(new GridBagLayout());
        mainContainer.setBackground(CustomTheme.PRIMARY_COLOR);

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.5;
        gbc.weighty = 1;

        JPanel leftPanel = createLeftPanel();
        contentPanel.add(leftPanel, gbc);

        JPanel rightPanel = createRightPanel();
        gbc.gridx = 1;
        contentPanel.add(rightPanel, gbc);

        mainContainer.add(contentPanel);
        add(mainContainer);

        getRootPane().setDefaultButton(loginButton);

        KeyStroke escapeKey = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        getRootPane().registerKeyboardAction(e -> exitApplication(),
                escapeKey, JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(25, 118, 210, 200));
        panel.setOpaque(true);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 40, 20, 40);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel logoLabel = new JLabel("🏨");
        logoLabel.setFont(CustomTheme.EMOJI_FONT.deriveFont(80f));
        logoLabel.setForeground(Color.WHITE);
        gbc.gridy = 0;
        panel.add(logoLabel, gbc);

        JLabel titleLabel = new JLabel("Grand Hotel");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        gbc.gridy = 1;
        panel.add(titleLabel, gbc);

        JLabel subtitleLabel = new JLabel("Management System");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        subtitleLabel.setForeground(new Color(255, 255, 255, 200));
        gbc.gridy = 2;
        panel.add(subtitleLabel, gbc);

        JPanel featuresPanel = new JPanel(new GridLayout(4, 1, 10, 15));
        featuresPanel.setOpaque(false);
        featuresPanel.setBorder(BorderFactory.createEmptyBorder(40, 0, 0, 0));

        String[][] features = {
                { "📊", "Real-time Dashboard Analytics" },
                { "🏠", "Room Management & Booking" },
                { "👥", "Guest & Loyalty Management" },
                { "💰", "Billing & Invoicing" }
        };

        for (String[] feature : features) {
            JPanel featurePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
            featurePanel.setOpaque(false);

            JLabel iconLabel = new JLabel(feature[0]);
            iconLabel.setFont(CustomTheme.EMOJI_FONT.deriveFont(24f));
            iconLabel.setForeground(Color.WHITE);

            JLabel textLabel = new JLabel(feature[1]);
            textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            textLabel.setForeground(Color.WHITE);

            featurePanel.add(iconLabel);
            featurePanel.add(textLabel);
            featuresPanel.add(featurePanel);
        }

        gbc.gridy = 3;
        panel.add(featuresPanel, gbc);

        return panel;
    }

    private JPanel createRightPanel() {
        CustomTheme.ShadowPanel card = new CustomTheme.ShadowPanel(20, 5);
        card.setBackground(CustomTheme.CARD_BACKGROUND_COLOR);
        card.setLayout(new GridBagLayout());
        card.setPreferredSize(new Dimension(450, 550));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 220, 245), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 35, 10, 35);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Welcome Back!");
        titleLabel.setFont(CustomTheme.TITLE_FONT);
        titleLabel.setForeground(CustomTheme.DARK_COLOR);
        gbc.gridy = 0;
        gbc.insets = new Insets(40, 35, 5, 35);
        card.add(titleLabel, gbc);

        JLabel subtitleLabel = new JLabel("Please sign in to continue");
        subtitleLabel.setFont(CustomTheme.SMALL_FONT);
        subtitleLabel.setForeground(CustomTheme.GRAY_COLOR);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 35, 30, 35);
        card.add(subtitleLabel, gbc);

        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(CustomTheme.NORMAL_FONT);
        userLabel.setForeground(CustomTheme.DARK_COLOR);
        gbc.gridy = 2;
        gbc.insets = new Insets(5, 35, 5, 35);
        card.add(userLabel, gbc);

        usernameField = new CustomTheme.ModernTextField();
        usernameField.setFont(CustomTheme.NORMAL_FONT);
        usernameField.setPreferredSize(new Dimension(300, 45));
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 35, 15, 35);
        card.add(usernameField, gbc);

        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(CustomTheme.NORMAL_FONT);
        passLabel.setForeground(CustomTheme.DARK_COLOR);
        gbc.gridy = 4;
        gbc.insets = new Insets(5, 35, 5, 35);
        card.add(passLabel, gbc);

        passwordField = new CustomTheme.ModernPasswordField();
        passwordField.setFont(CustomTheme.NORMAL_FONT);
        passwordField.setPreferredSize(new Dimension(300, 45));
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 35, 10, 35);
        card.add(passwordField, gbc);

        showPasswordCheckBox = new JCheckBox("Show Password");
        showPasswordCheckBox.setFont(CustomTheme.SMALL_FONT);
        showPasswordCheckBox.setOpaque(false);
        showPasswordCheckBox.addActionListener(e -> {
            if (showPasswordCheckBox.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar('•');
            }
        });
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 35, 20, 35);
        card.add(showPasswordCheckBox, gbc);

        loginButton = new CustomTheme.ModernButton("SIGN IN");
        loginButton.setBackground(CustomTheme.PRIMARY_COLOR);
        loginButton.setFont(CustomTheme.EMOJI_FONT.deriveFont(Font.BOLD, 16f));
        loginButton.setPreferredSize(new Dimension(200, 50));
        loginButton.addActionListener(e -> performLogin());
        gbc.gridy = 7;
        gbc.insets = new Insets(10, 35, 15, 35);
        card.add(loginButton, gbc);

        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setFont(CustomTheme.SMALL_FONT);
        statusLabel.setForeground(CustomTheme.DANGER_COLOR);
        gbc.gridy = 8;
        gbc.insets = new Insets(0, 35, 20, 35);
        card.add(statusLabel, gbc);

        exitButton = new CustomTheme.OutlinedButton("EXIT", CustomTheme.DANGER_COLOR);
        exitButton.setPreferredSize(new Dimension(100, 40));
        exitButton.addActionListener(e -> exitApplication());

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setOpaque(false);
        footerPanel.add(exitButton);
        gbc.gridy = 9;
        gbc.insets = new Insets(10, 35, 30, 35);
        card.add(footerPanel, gbc);

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.add(card);

        return wrapper;
    }

    private void performLogin() {
        if (isLoading)
            return;

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

        // For demo purposes - allow login with any credentials
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
        statusLabel.setForeground(CustomTheme.SUCCESS_COLOR);
        statusLabel.setText("✓ Login successful!");

        SwingUtilities.invokeLater(() -> {
            new DashboardFrame(user);
            dispose();
        });
    }

    private void showError(String message) {
        statusLabel.setText("✗ " + message);
        statusLabel.setForeground(CustomTheme.DANGER_COLOR);

        Timer timer = new Timer(3000, e -> statusLabel.setText(" "));
        timer.setRepeats(false);
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

    // In LoginFrame.java, update the main method:
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            // Initialize DataManager first
            DataManager.getInstance();
            new LoginFrame();
        });
    }
}