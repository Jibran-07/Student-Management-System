package studentmanagementsystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDetails extends JFrame implements ActionListener {
    private JTable studentTable;
    private JTextField searchField;
    private JComboBox<String> searchTypeCombo;
    private JButton searchButton, viewDetailsButton, backButton, clearButton;
    private DefaultTableModel tableModel;
    private JPopupMenu suggestionPopup;
    private Timer suggestionTimer;

    public StudentDetails() {
        setTitle("View Students");
        getContentPane().setBackground(new Color(128,176,255));
        setLayout(null);

        JLabel headingLabel = new JLabel("Student Information");
        headingLabel.setBounds(310, 30, 500, 50);
        headingLabel.setFont(new Font("serif", Font.BOLD, 30));
        add(headingLabel);

        JLabel searchTypeLabel = new JLabel("Search By:");
        searchTypeLabel.setBounds(50, 100, 100, 30);
        searchTypeLabel.setFont(new Font("serif", Font.BOLD, 16));
        add(searchTypeLabel);

        String[] searchTypes = {"Roll No", "Name", "Department", "Batch", "Email"};
        searchTypeCombo = new JComboBox<>(searchTypes);
        searchTypeCombo.setBounds(150, 100, 120, 30);
        searchTypeCombo.setFont(new Font("serif", Font.PLAIN, 14));
        searchTypeCombo.addActionListener(this);
        add(searchTypeCombo);

        JLabel searchLabel = new JLabel("Enter Search Term:");
        searchLabel.setBounds(50, 140, 150, 30);
        searchLabel.setFont(new Font("serif", Font.BOLD, 16));
        add(searchLabel);

        searchField = new JTextField();
        searchField.setBounds(210, 140, 250, 30);
        searchField.setFont(new Font("serif", Font.PLAIN, 14));
        add(searchField);

        suggestionPopup = new JPopupMenu();
        suggestionPopup.setFocusable(false);

        setupSearchFieldListeners();

        suggestionTimer = new Timer(300, e -> showAutoCompleteSuggestions());
        suggestionTimer.setRepeats(false);

        searchButton = new JButton("Search");
        searchButton.setBounds(480, 140, 100, 30);
        searchButton.setBackground(Color.BLACK);
        searchButton.setForeground(Color.WHITE);
        searchButton.addActionListener(this);
        add(searchButton);

        clearButton = new JButton("Clear");
        clearButton.setBounds(590, 140, 100, 30);
        clearButton.setBackground(Color.DARK_GRAY);
        clearButton.setForeground(Color.WHITE);
        clearButton.addActionListener(this);
        add(clearButton);

        String[] columnNames = {"Student ID", "Roll No", "Name", "Department", "Batch", "Email"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        studentTable = new JTable(tableModel);
        studentTable.getTableHeader().setFont(new Font("serif", Font.BOLD, 14));
        studentTable.setRowHeight(25);

        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentTable.setRowSelectionAllowed(true);

        setupTableMouseListener();

        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setBounds(50, 190, 800, 280);
        add(scrollPane);

        viewDetailsButton = new JButton("View Details");
        viewDetailsButton.setBounds(250, 490, 150, 30);
        viewDetailsButton.setBackground(Color.BLACK);
        viewDetailsButton.setForeground(Color.WHITE);
        viewDetailsButton.addActionListener(this);
        add(viewDetailsButton);

        backButton = new JButton("Back");
        backButton.setBounds(500, 490, 150, 30);
        backButton.setBackground(Color.BLACK);
        backButton.setForeground(Color.WHITE);
        backButton.addActionListener(this);
        add(backButton);

        loadAllStudents();

        setSize(900, 580);
        setLocation(350, 50);
        setVisible(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void setupSearchFieldListeners() {
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                scheduleAutoComplete();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                scheduleAutoComplete();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                scheduleAutoComplete();
            }
        });

        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    suggestionPopup.setVisible(false);
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    suggestionPopup.setVisible(false);
                    performSearch();
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN && suggestionPopup.isVisible()) {
                    e.consume();
                }
            }
        });

        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (!suggestionPopup.isAncestorOf(e.getOppositeComponent())) {
                    Timer timer = new Timer(200, ae -> {
                        if (!searchField.hasFocus()) {
                            suggestionPopup.setVisible(false);
                        }
                    });
                    timer.setRepeats(false);
                    timer.start();
                }
            }

            @Override
            public void focusGained(FocusEvent e) {
                if (!searchField.getText().trim().isEmpty()) {
                    scheduleAutoComplete();
                }
            }
        });
    }

    private void setupTableMouseListener() {
        studentTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    int row = studentTable.rowAtPoint(e.getPoint());
                    if (row >= 0) {
                        studentTable.setRowSelectionInterval(row, row);
                        studentDetailsView();
                    }
                }
            }
        });
    }

    private void scheduleAutoComplete() {
        suggestionTimer.restart();
    }

    private void showAutoCompleteSuggestions() {
        String searchText = searchField.getText().trim();

        if (searchText.length() < 1) {
            suggestionPopup.setVisible(false);
            return;
        }

        String selectedSearchType = (String) searchTypeCombo.getSelectedItem();
        List<String> suggestions = getSuggestions(searchText, selectedSearchType);

        if (suggestions.isEmpty()) {
            suggestionPopup.setVisible(false);
            return;
        }

        suggestionPopup.removeAll();

        for (String suggestion : suggestions) {
            JMenuItem item = new JMenuItem(suggestion);
            item.addActionListener(e -> {
                SwingUtilities.invokeLater(() -> {
                    searchField.setText(suggestion);
                    suggestionPopup.setVisible(false);
                    performSearch();
                });
            });
            suggestionPopup.add(item);
        }

        if (suggestionPopup.getComponentCount() > 0) {
            Point location = searchField.getLocationOnScreen();
            suggestionPopup.setLocation(location.x, location.y + searchField.getHeight());
            suggestionPopup.setVisible(true);
        }
    }

    private List<String> getSuggestions(String searchText, String searchType) {
        List<String> suggestions = new ArrayList<>();

        try {
            Connection conn = new Connector().connection;
            CallableStatement cs = null;

            switch (searchType) {
                case "Roll No":
                    cs = conn.prepareCall("{call GetRollNoSuggestions(?)}");
                    break;
                case "Name":
                    cs = conn.prepareCall("{call GetNameSuggestions(?)}");
                    break;
                case "Department":
                    cs = conn.prepareCall("{call GetDepartmentSuggestions(?)}");
                    break;
                case "Batch":
                    cs = conn.prepareCall("{call GetBatchSuggestions(?)}");
                    break;
                case "Email":
                    cs = conn.prepareCall("{call GetEmailSuggestions(?)}");
                    break;
            }

            if (cs != null) {
                cs.setString(1, searchText);
                ResultSet rs = cs.executeQuery();

                while (rs.next()) {
                    String suggestion = rs.getString(1);
                    if (suggestion != null && !suggestion.trim().isEmpty()) {
                        suggestions.add(suggestion.trim());
                    }
                }

                rs.close();
                cs.close();
            }
            conn.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error getting suggestions: " + e.getMessage());
        }

        return suggestions;
    }

    private void loadAllStudents() {
        clearTable();
        try {
            Connection conn = new Connector().connection;
            CallableStatement cs = conn.prepareCall("{call GetAllStudentsWithEmail}");
            ResultSet rs = cs.executeQuery();
            populateTableWithEmail(rs, false);
            rs.close();
            cs.close();
            conn.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading students: " + e.getMessage());
        }
    }

    private void performSearch() {
        clearTable();
        String searchText = searchField.getText().trim();
        String searchType = (String) searchTypeCombo.getSelectedItem();

        if (searchText.isEmpty()) {
            loadAllStudents();
            return;
        }

        try {
            Connection conn = new Connector().connection;
            CallableStatement cs = null;

            switch (searchType) {
                case "Roll No":
                    cs = conn.prepareCall("{call SearchStudentsByRollNo(?)}");
                    break;
                case "Name":
                    cs = conn.prepareCall("{call SearchStudentsByName(?)}");
                    break;
                case "Department":
                    cs = conn.prepareCall("{call SearchStudentsByDepartment(?)}");
                    break;
                case "Batch":
                    cs = conn.prepareCall("{call SearchStudentsByBatch(?)}");
                    break;
                case "Email":
                    cs = conn.prepareCall("{call SearchStudentsByEmail(?)}");
                    break;
            }

            if (cs != null) {
                cs.setString(1, searchText);
                ResultSet rs = cs.executeQuery();

                boolean hasEmailColumn = false;
                try {
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    for (int i = 1; i <= columnCount; i++) {
                        if ("Email".equalsIgnoreCase(metaData.getColumnName(i))) {
                            hasEmailColumn = true;
                            break;
                        }
                    }
                } catch (SQLException e) {
                    hasEmailColumn = false;
                }

                if (hasEmailColumn) {
                    populateTableWithEmail(rs, true);
                } else {
                    populateTableWithoutEmail(rs, true);
                }

                rs.close();
                cs.close();
            }
            conn.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error searching students: " + e.getMessage());
        }
    }

    private void clearTable() {
        tableModel.setRowCount(0);
    }

    private void populateTableWithEmail(ResultSet rs, boolean showNoResultsMessage) throws SQLException {
        while (rs.next()) {
            String studentId = rs.getString("StudentID");
            String rollNo = rs.getString("RollNo");
            String name = rs.getString("FirstName") + " " + rs.getString("LastName");
            String department = rs.getString("DepartmentName");
            String batch = rs.getString("BatchName");
            String email = rs.getString("Email");

            tableModel.addRow(new Object[]{studentId, rollNo, name, department, batch, email});
        }

        if (showNoResultsMessage && tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No students found matching your search criteria.");
        }
    }

    private void populateTableWithoutEmail(ResultSet rs, boolean showNoResultsMessage) throws SQLException {
        while (rs.next()) {
            String studentId = rs.getString("StudentID");
            String rollNo = rs.getString("RollNo");
            String name = rs.getString("FirstName") + " " + rs.getString("LastName");
            String department = rs.getString("DepartmentName");
            String batch = rs.getString("BatchName");
            String email = "N/A";

            tableModel.addRow(new Object[]{studentId, rollNo, name, department, batch, email});
        }

        if (showNoResultsMessage && tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No students found matching your search criteria.");
        }
    }

    private void studentDetailsView() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student to view details.");
            return;
        }

        try {
            Object studentIdObj = tableModel.getValueAt(selectedRow, 0);

            if (studentIdObj == null) {
                JOptionPane.showMessageDialog(this, "Invalid student ID selected: null value.");
                return;
            }

            String studentIdStr = studentIdObj.toString();
            if (studentIdStr.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Invalid student ID selected: empty value.");
                return;
            }

            int studentId = Integer.parseInt(studentIdStr);
            new StudentDetailView(studentId);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid student ID format: " + e.getMessage());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error opening student details: " + e.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == searchButton) {
            performSearch();
        } else if (e.getSource() == viewDetailsButton) {
            studentDetailsView();
        } else if (e.getSource() == backButton) {
            dispose();
        } else if (e.getSource() == clearButton) {
            searchField.setText("");
            searchTypeCombo.setSelectedIndex(0);
            suggestionPopup.setVisible(false);
            loadAllStudents();
        } else if (e.getSource() == searchTypeCombo) {
            searchField.setText("");
            suggestionPopup.setVisible(false);
        }
    }

    class StudentDetailView extends JFrame {
        private JLabel nameLabel, rollNoLabel, departmentLabel, batchLabel, addressLabel, phoneLabel, emailLabel;
        private JTable enrollmentTable;
        private DefaultTableModel tableModel;

        public StudentDetailView(int studentId) {
            setTitle("Student Details");
            getContentPane().setBackground(new Color(128, 176, 255));
            setLayout(null);

            JLabel headingLabel = new JLabel("Student Details");
            headingLabel.setBounds(250, 20, 300, 40);
            headingLabel.setFont(new Font("serif", Font.BOLD, 25));
            add(headingLabel);

            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(null);
            infoPanel.setBounds(50, 70, 600, 200);
            infoPanel.setBackground(new Color(192, 217, 255));

            nameLabel = createInfoLabel("Name: ", 20, 20);
            rollNoLabel = createInfoLabel("Roll No: ", 20, 50);
            departmentLabel = createInfoLabel("Department: ", 20, 80);
            batchLabel = createInfoLabel("Batch: ", 20, 110);
            emailLabel = createInfoLabel("Email: ", 20, 140);
            phoneLabel = createInfoLabel("Phone: ", 320, 20);
            addressLabel = createInfoLabel("Address: ", 320, 50);

            infoPanel.add(nameLabel);
            infoPanel.add(rollNoLabel);
            infoPanel.add(departmentLabel);
            infoPanel.add(batchLabel);
            infoPanel.add(emailLabel);
            infoPanel.add(phoneLabel);
            infoPanel.add(addressLabel);

            add(infoPanel);

            JLabel enrollmentHeading = new JLabel("Course Enrollment");
            enrollmentHeading.setBounds(250, 290, 300, 30);
            enrollmentHeading.setFont(new Font("serif", Font.BOLD, 20));
            add(enrollmentHeading);

            String[] columnNames = {"Course Code", "Course Name", "Credits", "Semester", "Marks", "Grade"};
            tableModel = new DefaultTableModel(columnNames, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            enrollmentTable = new JTable(tableModel);
            enrollmentTable.getTableHeader().setFont(new Font("serif", Font.BOLD, 14));
            enrollmentTable.setRowHeight(25);

            JScrollPane scrollPane = new JScrollPane(enrollmentTable);
            scrollPane.setBounds(50, 330, 600, 150);
            add(scrollPane);

            JButton closeButton = new JButton("Close");
            closeButton.setBounds(300, 500, 100, 30);
            closeButton.setBackground(Color.BLACK);
            closeButton.setForeground(Color.WHITE);
            closeButton.addActionListener(e -> dispose());
            add(closeButton);

            loadStudentData(studentId);

            setSize(700, 600);
            setLocationRelativeTo(null);
            setVisible(true);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        }

        private JLabel createInfoLabel(String labelText, int x, int y) {
            JLabel label = new JLabel(labelText);
            label.setBounds(x, y, 500, 25);
            label.setFont(new Font("serif", Font.PLAIN, 16));
            return label;
        }

        private void loadStudentData(int studentId) {
            try {
                Connection conn = new Connector().connection;

                CallableStatement csDetails = conn.prepareCall("{call GetStudentDetailsById(?)}");
                csDetails.setInt(1, studentId);
                ResultSet rs = csDetails.executeQuery();

                if (rs.next()) {
                    nameLabel.setText("Name: " + rs.getString("FirstName") + " " + rs.getString("LastName"));
                    rollNoLabel.setText("Roll No: " + rs.getString("RollNo"));
                    departmentLabel.setText("Department: " + rs.getString("DepartmentName"));
                    batchLabel.setText("Batch: " + rs.getString("BatchName"));
                    emailLabel.setText("Email: " + rs.getString("Email"));
                    phoneLabel.setText("Phone: " + rs.getString("Phone"));
                    addressLabel.setText("Address: " + rs.getString("Address"));
                }
                rs.close();
                csDetails.close();

                CallableStatement csEnrollment = conn.prepareCall("{call GetStudentEnrollments(?)}");
                csEnrollment.setInt(1, studentId);
                ResultSet enrollmentRs = csEnrollment.executeQuery();

                while (enrollmentRs.next()) {
                    String courseCode = enrollmentRs.getString("CourseCode");
                    String courseName = enrollmentRs.getString("CourseName");
                    String credits = enrollmentRs.getString("Credits");
                    String semester = enrollmentRs.getString("SemesterName");

                    String marksValue = enrollmentRs.getString("MarksValue");
                    if (marksValue == null || marksValue.isEmpty()) {
                        marksValue = "Not Assessed";
                    }

                    String gradeValue = enrollmentRs.getString("GradeValue");
                    if (gradeValue == null || gradeValue.isEmpty()) {
                        gradeValue = "Not Graded";
                    }

                    tableModel.addRow(new Object[]{courseCode, courseName, credits, semester, marksValue, gradeValue});
                }
                enrollmentRs.close();
                conn.close();

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error loading student data: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        new StudentDetails();
    }
}