// ui/RoomManagementPanel.java
package ui;

import models.Room;
import utils.CustomTheme;
import utils.DataManager;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class RoomManagementPanel extends JPanel {

    private JTable roomTable;
    private DefaultTableModel tableModel;
    private DataManager dataManager;
    private CustomTheme.ModernTextField searchField;
    private JComboBox<String> filterComboBox;
    private JLabel statusLabel;
    private JLabel statsLabel;
    private List<Room> currentRooms;
    
    public RoomManagementPanel() {
        dataManager = DataManager.getInstance();
        initComponents();
        loadRooms();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBackground(CustomTheme.BACKGROUND_COLOR);
        
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
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
        
        JLabel titleIcon = new JLabel("🏠");
        titleIcon.setFont(CustomTheme.getEmojiFont().deriveFont(32f));
        titlePanel.add(titleIcon);
        
        JLabel titleLabel = CustomTheme.createHeaderLabel("Room Management");
        titlePanel.add(titleLabel);
        
        JLabel subtitleLabel = CustomTheme.createLabel("Manage hotel rooms, view availability and update room details");
        subtitleLabel.setFont(CustomTheme.SMALL_FONT);
        subtitleLabel.setForeground(CustomTheme.GRAY_COLOR);
        titlePanel.add(subtitleLabel);
        
        headerPanel.add(titlePanel, BorderLayout.WEST);
        
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionPanel.setOpaque(false);
        
        searchField = new CustomTheme.ModernTextField(15);
        searchField.setPlaceholder("Search rooms...");
        searchField.setPreferredSize(new Dimension(200, 40));
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                searchRooms();
            }
        });
        actionPanel.add(searchField);
        
        String[] filterOptions = { "All Rooms", "Available", "Occupied", "Maintenance", "Reserved", "Cleaning" };
        filterComboBox = new JComboBox<>(filterOptions);
        CustomTheme.styleComboBox(filterComboBox);
        filterComboBox.addActionListener(e -> filterRooms());
        actionPanel.add(filterComboBox);
        
        CustomTheme.ModernButton addButton = new CustomTheme.ModernButton("➕ Add Room", CustomTheme.SUCCESS_COLOR);
        addButton.addActionListener(e -> showAddRoomDialog());
        actionPanel.add(addButton);
        
        CustomTheme.ModernButton refreshButton = new CustomTheme.ModernButton("🔄 Refresh", CustomTheme.PRIMARY_COLOR);
        refreshButton.addActionListener(e -> loadRooms());
        actionPanel.add(refreshButton);
        
        headerPanel.add(actionPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(CustomTheme.BACKGROUND_COLOR);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));
        
        String[] columns = { "ID", "Room No", "Type", "Price/Night", "Status", "Floor", "Capacity", "Actions" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7;
            }
        };
        
        roomTable = new JTable(tableModel);
        roomTable.setFont(CustomTheme.NORMAL_FONT);
        roomTable.setRowHeight(45);
        roomTable.setShowGrid(false);
        roomTable.setForeground(CustomTheme.TEXT_COLOR);
        roomTable.setBackground(Color.WHITE);
        roomTable.setSelectionBackground(new Color(25, 118, 210, 50));
        roomTable.setSelectionForeground(CustomTheme.TEXT_COLOR);
        
        // Fix Table Header - Make it visible
        JTableHeader header = roomTable.getTableHeader();
        header.setFont(CustomTheme.HEADER_FONT);
        header.setBackground(CustomTheme.HEADER_BACKGROUND);
        header.setForeground(CustomTheme.HEADER_TEXT_COLOR);
        header.setPreferredSize(new Dimension(header.getWidth(), 45));
        header.setReorderingAllowed(false);
        header.setOpaque(true);
        
        // Custom header renderer
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
        roomTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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
                
                // Status column with icons
                if (column == 4 && value != null) {
                    String status = value.toString();
                    label.setHorizontalAlignment(SwingConstants.CENTER);
                    switch(status) {
                        case "Available": 
                            label.setForeground(CustomTheme.SUCCESS_COLOR); 
                            label.setText("✅ " + status); 
                            break;
                        case "Occupied": 
                            label.setForeground(CustomTheme.WARNING_COLOR); 
                            label.setText("🔴 " + status); 
                            break;
                        case "Maintenance": 
                            label.setForeground(CustomTheme.DANGER_COLOR); 
                            label.setText("🔧 " + status); 
                            break;
                        case "Reserved": 
                            label.setForeground(CustomTheme.INFO_COLOR); 
                            label.setText("📅 " + status); 
                            break;
                        default: 
                            label.setText(status);
                    }
                } 
                // Price column
                else if (column == 3 && value instanceof Double) {
                    label.setText(String.format("₹%,.2f", (Double) value));
                    label.setHorizontalAlignment(SwingConstants.RIGHT);
                    label.setForeground(CustomTheme.TEXT_COLOR);
                } else {
                    label.setHorizontalAlignment(SwingConstants.CENTER);
                    label.setForeground(CustomTheme.TEXT_COLOR);
                }
                
                if (isSelected) {
                    label.setBackground(new Color(25, 118, 210, 50));
                } else {
                    label.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 249, 250));
                }
                
                label.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
                return label;
            }
        });
        
        TableColumnModel columnModel = roomTable.getColumnModel();
        columnModel.getColumn(0).setMaxWidth(50);
        columnModel.getColumn(0).setMinWidth(50);
        columnModel.getColumn(1).setPreferredWidth(100);
        columnModel.getColumn(2).setPreferredWidth(100);
        columnModel.getColumn(3).setPreferredWidth(120);
        columnModel.getColumn(4).setPreferredWidth(130);
        columnModel.getColumn(5).setMaxWidth(60);
        columnModel.getColumn(6).setMaxWidth(80);
        columnModel.getColumn(7).setPreferredWidth(150);
        
        roomTable.getColumn("Actions").setCellRenderer(new ButtonRenderer());
        roomTable.getColumn("Actions").setCellEditor(new ButtonEditor(new JCheckBox()));
        
        JScrollPane scrollPane = new JScrollPane(roomTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(CustomTheme.LIGHT_COLOR));
        scrollPane.getViewport().setBackground(Color.WHITE);
        CustomTheme.styleScrollPane(scrollPane);
        
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        return centerPanel;
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
    
    private void loadRooms() {
        currentRooms = dataManager.getAllRooms();
        updateTableData(currentRooms);
        updateStatistics(currentRooms);
        statusLabel.setText("Loaded " + currentRooms.size() + " rooms");
    }
    
    private void updateTableData(List<Room> rooms) {
        tableModel.setRowCount(0);
        for (Room room : rooms) {
            tableModel.addRow(new Object[]{
                room.getRoomId(),
                room.getRoomNumber(),
                room.getRoomType(),
                room.getPricePerNight(),
                room.getStatus(),
                room.getFloor(),
                room.getCapacity(),
                "Edit | Delete"
            });
        }
    }
    
    private void updateStatistics(List<Room> rooms) {
        int total = rooms.size();
        long available = rooms.stream().filter(r -> "Available".equals(r.getStatus())).count();
        long occupied = rooms.stream().filter(r -> "Occupied".equals(r.getStatus())).count();
        long maintenance = rooms.stream().filter(r -> "Maintenance".equals(r.getStatus())).count();
        long reserved = rooms.stream().filter(r -> "Reserved".equals(r.getStatus())).count();
        
        String stats = String.format(
            "📊 Room Statistics: Total: %d | ✅ Available: %d | 🔴 Occupied: %d | 🔧 Maintenance: %d | 📅 Reserved: %d",
            total, available, occupied, maintenance, reserved);
        statsLabel.setText(stats);
        statsLabel.setForeground(CustomTheme.TEXT_COLOR);
    }
    
    private void filterRooms() {
        String filter = (String) filterComboBox.getSelectedItem();
        if (filter == null || filter.equals("All Rooms")) {
            loadRooms();
        } else {
            List<Room> filtered = new java.util.ArrayList<>();
            for (Room room : currentRooms) {
                if (room.getStatus().equals(filter)) {
                    filtered.add(room);
                }
            }
            updateTableData(filtered);
            statusLabel.setText("Showing " + filtered.size() + " " + filter + " rooms");
        }
    }
    
    private void searchRooms() {
        String searchTerm = searchField.getText().trim().toLowerCase();
        if (searchTerm.isEmpty()) {
            loadRooms();
        } else {
            List<Room> filtered = new java.util.ArrayList<>();
            for (Room room : currentRooms) {
                if (room.getRoomNumber().contains(searchTerm) || 
                    room.getRoomType().toLowerCase().contains(searchTerm)) {
                    filtered.add(room);
                }
            }
            updateTableData(filtered);
            statusLabel.setText("Found " + filtered.size() + " rooms matching '" + searchTerm + "'");
        }
    }
    
    private void showAddRoomDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Room", true);
        dialog.setSize(500, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(CustomTheme.createLabel("Room Number:*"), gbc);
        gbc.gridx = 1;
        CustomTheme.ModernTextField roomNumberField = new CustomTheme.ModernTextField(15);
        roomNumberField.setPreferredSize(new Dimension(200, 40));
        panel.add(roomNumberField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(CustomTheme.createLabel("Room Type:*"), gbc);
        gbc.gridx = 1;
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"Standard", "Deluxe", "Suite", "Presidential"});
        CustomTheme.styleComboBox(typeCombo);
        panel.add(typeCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(CustomTheme.createLabel("Price per Night (₹):*"), gbc);
        gbc.gridx = 1;
        CustomTheme.ModernTextField priceField = new CustomTheme.ModernTextField(15);
        priceField.setPreferredSize(new Dimension(200, 40));
        panel.add(priceField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(CustomTheme.createLabel("Floor:*"), gbc);
        gbc.gridx = 1;
        JSpinner floorSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));
        floorSpinner.setFont(CustomTheme.NORMAL_FONT);
        panel.add(floorSpinner, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(CustomTheme.createLabel("Capacity:*"), gbc);
        gbc.gridx = 1;
        JSpinner capacitySpinner = new JSpinner(new SpinnerNumberModel(2, 1, 10, 1));
        capacitySpinner.setFont(CustomTheme.NORMAL_FONT);
        panel.add(capacitySpinner, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        CustomTheme.ModernButton saveButton = new CustomTheme.ModernButton("Save Room", CustomTheme.SUCCESS_COLOR);
        saveButton.addActionListener(e -> {
            if (roomNumberField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please enter room number!");
                return;
            }
            if (priceField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please enter price!");
                return;
            }
            
            Room newRoom = new Room();
            newRoom.setRoomNumber(roomNumberField.getText().trim());
            newRoom.setRoomType((String) typeCombo.getSelectedItem());
            newRoom.setPricePerNight(Double.parseDouble(priceField.getText().trim()));
            newRoom.setStatus("Available");
            newRoom.setFloor((Integer) floorSpinner.getValue());
            newRoom.setCapacity((Integer) capacitySpinner.getValue());
            
            JOptionPane.showMessageDialog(dialog, "Room added successfully!\nRoom: " + newRoom.getRoomNumber());
            dialog.dispose();
            loadRooms();
        });
        panel.add(saveButton, gbc);
        
        dialog.add(panel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }
    
    private void deleteRoom(int roomId, String roomNumber) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete Room " + roomNumber + "?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this, "Room " + roomNumber + " deleted successfully!");
            loadRooms();
        }
    }
    
    // Button renderer for actions column
    class ButtonRenderer extends JPanel implements TableCellRenderer {
        private JButton editButton;
        private JButton deleteButton;
        
        public ButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 8, 8));
            setOpaque(true);
            
            editButton = new JButton("Edit");
            editButton.setBackground(CustomTheme.PRIMARY_COLOR);
            editButton.setForeground(Color.WHITE);
            editButton.setFont(CustomTheme.SMALL_FONT);
            editButton.setFocusPainted(false);
            editButton.setBorderPainted(false);
            editButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            deleteButton = new JButton("Delete");
            deleteButton.setBackground(CustomTheme.DANGER_COLOR);
            deleteButton.setForeground(Color.WHITE);
            deleteButton.setFont(CustomTheme.SMALL_FONT);
            deleteButton.setFocusPainted(false);
            deleteButton.setBorderPainted(false);
            deleteButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            add(editButton);
            add(deleteButton);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setBackground(table.getSelectionBackground());
            } else {
                setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 249, 250));
            }
            return this;
        }
    }
    
    // Button editor for actions column
    class ButtonEditor extends DefaultCellEditor {
        private JPanel panel;
        private JButton editButton;
        private JButton deleteButton;
        private int currentRow;
        
        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
            panel.setOpaque(true);
            
            editButton = new JButton("Edit");
            editButton.setBackground(CustomTheme.PRIMARY_COLOR);
            editButton.setForeground(Color.WHITE);
            editButton.setFont(CustomTheme.SMALL_FONT);
            editButton.setFocusPainted(false);
            editButton.setBorderPainted(false);
            editButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            deleteButton = new JButton("Delete");
            deleteButton.setBackground(CustomTheme.DANGER_COLOR);
            deleteButton.setForeground(Color.WHITE);
            deleteButton.setFont(CustomTheme.SMALL_FONT);
            deleteButton.setFocusPainted(false);
            deleteButton.setBorderPainted(false);
            deleteButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            editButton.addActionListener(e -> {
                fireEditingStopped();
                JOptionPane.showMessageDialog(panel, "Edit room feature coming soon!");
            });
            
            deleteButton.addActionListener(e -> {
                fireEditingStopped();
                String roomNumber = (String) tableModel.getValueAt(currentRow, 1);
                deleteRoom(currentRow + 1, roomNumber);
            });
            
            panel.add(editButton);
            panel.add(deleteButton);
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            currentRow = row;
            if (isSelected) {
                panel.setBackground(table.getSelectionBackground());
            } else {
                panel.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 249, 250));
            }
            return panel;
        }
        
        @Override
        public Object getCellEditorValue() {
            return null;
        }
    }
}