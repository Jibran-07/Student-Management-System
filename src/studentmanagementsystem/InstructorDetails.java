package studentmanagementsystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class InstructorDetails extends JFrame implements ActionListener {
    private JTable instructorTable;
    private JTextField searchField;
    private JButton searchButton, viewDetailsButton, backButton;
    private DefaultTableModel tableModel;

    public InstructorDetails() {
        setTitle("View Instructors");
        getContentPane().setBackground(new Color(166, 164, 252));
        setLayout(null);

        JLabel headingLabel = new JLabel("Instructor Information");
        headingLabel.setBounds(310, 30, 500, 50);
        headingLabel.setFont(new Font("serif", Font.BOLD, 30));
        add(headingLabel);

        JLabel searchLabel = new JLabel("Enter Employee ID:");
        searchLabel.setBounds(50, 100, 150, 30);
        searchLabel.setFont(new Font("serif", Font.BOLD, 16));
        add(searchLabel);

        searchField = new JTextField();
        searchField.setBounds(210, 100, 220, 30);
        add(searchField);

        searchButton = new JButton("Search");
        searchButton.setBounds(450, 100, 100, 30);
        searchButton.setBackground(Color.BLACK);
        searchButton.setForeground(Color.WHITE);
        searchButton.addActionListener(this);
        add(searchButton);

        String[] columnNames = {"Instructor ID", "Employee ID", "Name", "Department"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        instructorTable = new JTable(tableModel);
        instructorTable.getTableHeader().setFont(new Font("serif", Font.BOLD, 14));
        instructorTable.setRowHeight(25);

        instructorTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    viewInstructorDetails();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(instructorTable);
        scrollPane.setBounds(50, 150, 800, 300);
        add(scrollPane);

        viewDetailsButton = new JButton("View Details");
        viewDetailsButton.setBounds(250, 470, 150, 30);
        viewDetailsButton.setBackground(Color.BLACK);
        viewDetailsButton.setForeground(Color.WHITE);
        viewDetailsButton.addActionListener(this);
        add(viewDetailsButton);

        backButton = new JButton("Back");
        backButton.setBounds(500, 470, 150, 30);
        backButton.setBackground(Color.BLACK);
        backButton.setForeground(Color.WHITE);
        backButton.addActionListener(this);
        add(backButton);

        loadAllInstructors();

        setSize(900, 550);
        setLocation(350, 50);
        setVisible(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void loadAllInstructors() {
        clearTable();
        try {
            Connection conn = new Connector().connection;
            CallableStatement cs = conn.prepareCall("{call GetAllInstructors}");
            ResultSet rs = cs.executeQuery();
            populateTable(rs, false);
            rs.close();
            cs.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading instructors: " + e.getMessage());
        }
    }

    private void searchInstructorsByEmpID() {
        clearTable();
        String empID = searchField.getText().trim();

        if (empID.isEmpty()) {
            loadAllInstructors();
            return;
        }

        try {
            Connection conn = new Connector().connection;
            CallableStatement cs = conn.prepareCall("{call SearchInstructorsByEmpID(?)}");
            cs.setString(1, empID);
            ResultSet rs = cs.executeQuery();
            populateTable(rs, true);
            rs.close();
            cs.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error searching instructors: " + e.getMessage());
        }
    }

    private void clearTable() {
        tableModel.setRowCount(0);
    }

    private void populateTable(ResultSet rs, boolean showNoResultsMessage) throws SQLException {
        while (rs.next()) {
            String instructorId = rs.getString("InstructorID");
            String empID = rs.getString("EmpID");
            String name = rs.getString("FirstName") + " " + rs.getString("LastName");
            String department = rs.getString("DepartmentName");

            tableModel.addRow(new Object[]{instructorId, empID, name, department});
        }

        if (showNoResultsMessage && tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No instructors found matching your criteria.");
        }
    }

    private void viewInstructorDetails() {
        int selectedRow = instructorTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an instructor to view details.");
            return;
        }

        String instructorId = tableModel.getValueAt(selectedRow, 0).toString();
        new InstructorDetailView(Integer.parseInt(instructorId));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == searchButton) {
            searchInstructorsByEmpID();
        } else if (e.getSource() == viewDetailsButton) {
            viewInstructorDetails();
        } else if (e.getSource() == backButton) {
            dispose();
        }
    }

    class InstructorDetailView extends JFrame {
        private JLabel nameLabel, empIDLabel, departmentLabel, emailLabel, phoneLabel;
        private JTable coursesTable;
        private DefaultTableModel tableModel;

        public InstructorDetailView(int instructorId) {
            setTitle("Instructor Details");
            getContentPane().setBackground(new Color(166, 164, 252));
            setLayout(null);

            JLabel headingLabel = new JLabel("Instructor Details");
            headingLabel.setBounds(250, 20, 300, 40);
            headingLabel.setFont(new Font("serif", Font.BOLD, 25));
            add(headingLabel);

            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(null);
            infoPanel.setBounds(50, 70, 600, 150);
            infoPanel.setBackground(new Color(197, 196, 255));

            nameLabel = createInfoLabel("Name: ", 20, 20);
            empIDLabel = createInfoLabel("Employee ID: ", 20, 50);
            departmentLabel = createInfoLabel("Department: ", 20, 80);
            emailLabel = createInfoLabel("Email: ", 320, 20);
            phoneLabel = createInfoLabel("Phone: ", 320, 50);

            infoPanel.add(nameLabel);
            infoPanel.add(empIDLabel);
            infoPanel.add(departmentLabel);
            infoPanel.add(emailLabel);
            infoPanel.add(phoneLabel);

            add(infoPanel);

            JLabel coursesHeading = new JLabel("Courses Taught");
            coursesHeading.setBounds(250, 240, 300, 30);
            coursesHeading.setFont(new Font("serif", Font.BOLD, 20));
            add(coursesHeading);

            String[] columnNames = {"Course Code", "Course Name", "Credits", "Semester"};
            tableModel = new DefaultTableModel(columnNames, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            coursesTable = new JTable(tableModel);
            coursesTable.getTableHeader().setFont(new Font("serif", Font.BOLD, 14));
            coursesTable.setRowHeight(25);

            JScrollPane scrollPane = new JScrollPane(coursesTable);
            scrollPane.setBounds(50, 280, 600, 150);
            add(scrollPane);

            JButton closeButton = new JButton("Close");
            closeButton.setBounds(300, 450, 100, 30);
            closeButton.setBackground(Color.BLACK);
            closeButton.setForeground(Color.WHITE);
            closeButton.addActionListener(e -> dispose());
            add(closeButton);

            loadInstructorData(instructorId);

            setSize(700, 550);
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

        private void loadInstructorData(int instructorId) {
            try {
                Connection conn = new Connector().connection;

                CallableStatement cs = conn.prepareCall("{call GetInstructorDetailsById(?)}");
                cs.setInt(1, instructorId);
                ResultSet rs = cs.executeQuery();

                if (rs.next()) {
                    nameLabel.setText("Name: " + rs.getString("FirstName") + " " + rs.getString("LastName"));
                    empIDLabel.setText("Employee ID: " + rs.getString("EmpID"));
                    departmentLabel.setText("Department: " + rs.getString("DepartmentName"));
                    emailLabel.setText("Email: " + rs.getString("Email"));
                    phoneLabel.setText("Phone: " + rs.getString("Phone"));
                }
                rs.close();
                cs.close();

                CallableStatement coursesCs = conn.prepareCall("{call GetInstructorCourses(?)}");
                coursesCs.setInt(1, instructorId);
                ResultSet coursesRs = coursesCs.executeQuery();

                while (coursesRs.next()) {
                    String courseCode = coursesRs.getString("CourseCode");
                    String courseName = coursesRs.getString("CourseName");
                    String credits = coursesRs.getString("Credits");
                    String semester = coursesRs.getString("SemesterName");

                    tableModel.addRow(new Object[]{courseCode, courseName, credits, semester});
                }
                coursesRs.close();
                coursesCs.close();

                if (tableModel.getRowCount() == 0) {

                    CallableStatement deptCoursesCs = conn.prepareCall("{call GetDepartmentCoursesByInstructor(?)}");
                    deptCoursesCs.setInt(1, instructorId);
                    ResultSet deptCoursesRs = deptCoursesCs.executeQuery();

                    while (deptCoursesRs.next()) {
                        String courseCode = deptCoursesRs.getString("CourseCode");
                        String courseName = deptCoursesRs.getString("CourseName");
                        String credits = deptCoursesRs.getString("Credits");
                        String semester = "Not Assigned";

                        tableModel.addRow(new Object[]{courseCode, courseName, credits, semester});
                    }
                    deptCoursesRs.close();
                    deptCoursesCs.close();
                }

                if (tableModel.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(this, "No courses assigned to this instructor yet.");
                }

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error loading instructor data: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        new InstructorDetails();
    }
}