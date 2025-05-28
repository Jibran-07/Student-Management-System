package studentmanagementsystem;

import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class MarkAttendance extends JFrame implements ActionListener {
    JComboBox<String> batchBox, semesterBox, courseBox;
    JLabel departmentLabel;
    Choice choiceRollNo;
    JDateChooser attendanceDate;
    JComboBox<String> statusBox;
    JButton submit, cancel;

    private final Map<String, Integer> departmentMap = new HashMap<>();
    private final Map<String, Integer> batchMap = new HashMap<>();
    private final Map<String, Integer> semesterMap = new HashMap<>();
    private final Map<String, Integer> courseMap = new HashMap<>();
    private final Map<String, Integer> enrollmentMap = new HashMap<>();
    private Integer currentDeptId;

    MarkAttendance() {
        getContentPane().setBackground(new Color(230, 230, 250));
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel heading = new JLabel("Mark Student Attendance", SwingConstants.CENTER);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 32));
        heading.setForeground(new Color(33, 37, 41));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 30, 10);
        add(heading, gbc);

        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel batch = new JLabel("Select Batch");
        batch.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        batch.setForeground(new Color(66, 66, 66));
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(batch, gbc);

        batchBox = new JComboBox<>();
        batchBox.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        batchBox.setBackground(Color.WHITE);
        try {
            loadBatches();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading batches: " + e.getMessage());
        }
        batchBox.addActionListener(e -> {
            String selectedBatch = (String) batchBox.getSelectedItem();
            if (selectedBatch != null) {
                try {
                    autoFillDepartment(selectedBatch);
                    loadSemesters(selectedBatch);
                    semesterBox.setSelectedItem(null);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error loading semesters: " + ex.getMessage());
                }
            }
        });
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(batchBox, gbc);

        JLabel department = new JLabel("Department");
        department.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        department.setForeground(new Color(66, 66, 66));
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(department, gbc);

        departmentLabel = new JLabel(""); // Display-only label for department
        departmentLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        departmentLabel.setForeground(new Color(33, 37, 41));
        gbc.gridx = 1;
        gbc.gridy = 2;
        add(departmentLabel, gbc);

        JLabel semester = new JLabel("Select Semester");
        semester.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        semester.setForeground(new Color(66, 66, 66));
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(semester, gbc);

        semesterBox = new JComboBox<>();
        semesterBox.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        semesterBox.setBackground(Color.WHITE);
        gbc.gridx = 1;
        gbc.gridy = 3;
        add(semesterBox, gbc);

        JLabel course = new JLabel("Select Course");
        course.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        course.setForeground(new Color(66, 66, 66));
        gbc.gridx = 0;
        gbc.gridy = 4;
        add(course, gbc);

        courseBox = new JComboBox<>();
        courseBox.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        courseBox.setBackground(Color.WHITE);
        gbc.gridx = 1;
        gbc.gridy = 4;
        add(courseBox, gbc);

        JLabel rollNo = new JLabel("Select Roll Number");
        rollNo.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        rollNo.setForeground(new Color(66, 66, 66));
        gbc.gridx = 0;
        gbc.gridy = 5;
        add(rollNo, gbc);

        choiceRollNo = new Choice();
        choiceRollNo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        choiceRollNo.setVisible(true);
        gbc.gridx = 1;
        gbc.gridy = 5;
        add(choiceRollNo, gbc);

        JLabel date = new JLabel("Attendance Date");
        date.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        date.setForeground(new Color(66, 66, 66));
        gbc.gridx = 0;
        gbc.gridy = 6;
        add(date, gbc);

        attendanceDate = new JDateChooser();
        attendanceDate.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        attendanceDate.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        attendanceDate.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        gbc.gridy = 6;
        add(attendanceDate, gbc);

        JLabel status = new JLabel("Status");
        status.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        status.setForeground(new Color(66, 66, 66));
        gbc.gridx = 0;
        gbc.gridy = 7;
        add(status, gbc);

        String[] statuses = {"Present", "Absent"};
        statusBox = new JComboBox<>(statuses);
        statusBox.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        statusBox.setBackground(Color.WHITE);
        gbc.gridx = 1;
        gbc.gridy = 7;
        add(statusBox, gbc);


        submit = new JButton("Mark");
        submit.setFont(new Font("Segoe UI", Font.BOLD, 16));
        submit.setBackground(new Color(0, 123, 255));
        submit.setForeground(Color.WHITE);
        submit.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        submit.setFocusPainted(false);
        submit.addActionListener(this);
        gbc.gridx = 0;
        gbc.gridy = 8;
        add(submit, gbc);

        cancel = new JButton("Cancel");
        cancel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        cancel.setBackground(new Color(108, 117, 125));
        cancel.setForeground(Color.WHITE);
        cancel.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        cancel.setFocusPainted(false);
        cancel.addActionListener(this);
        gbc.gridx = 1;
        gbc.gridy = 8;
        add(cancel, gbc);

        setSize(600, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void loadBatches() throws SQLException {
        batchMap.clear();
        batchBox.removeAllItems();
        try (Connection conn = new Connector().connection;
             CallableStatement cs = conn.prepareCall("{call GetAllBatches()}")) {
            try (ResultSet rs = cs.executeQuery()) {
                boolean found = false;
                while (rs.next()) {
                    found = true;
                    String batchName = rs.getString("BatchName");
                    batchBox.addItem(batchName);
                    batchMap.put(batchName, rs.getInt("BatchID"));
                }
                if (!found) {
                    JOptionPane.showMessageDialog(null, "No batches found.");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading batches: " + ex.getMessage());
        }
    }

    private void autoFillDepartment(String batchName) throws SQLException {
        if (batchName == null) {
            departmentLabel.setText("");
            currentDeptId = null;
            return;
        }


        String deptName = batchName.split("_")[0];
        departmentLabel.setText(deptName);

        try (Connection conn = new Connector().connection;
             CallableStatement cs = conn.prepareCall("{call GetDepartmentIDByName(?)}")) {
            cs.setString(1, deptName);
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    currentDeptId = rs.getInt("DepartmentID");
                    departmentMap.put(deptName, currentDeptId);
                } else {
                    JOptionPane.showMessageDialog(null, "Department not found for batch: " + batchName);
                    currentDeptId = null;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error fetching department: " + ex.getMessage());
        }
    }

    private void loadSemesters(String batchName) throws SQLException {
        semesterMap.clear();
        semesterBox.removeAllItems();
        if (batchName == null) {
            System.out.println("BatchName is null, skipping semester load.");
            return;
        }

        System.out.println("Loading semesters for batch: " + batchName);

        try (Connection conn = new Connector().connection;
             CallableStatement cs = conn.prepareCall("{call GetSemesters()}");
             ResultSet rs = cs.executeQuery()) {
            boolean found = false;
            while (rs.next()) {
                found = true;
                String semesterName = rs.getString("SemesterName");
                Integer semesterId = rs.getInt("SemesterID");
                System.out.println("Adding semester: " + semesterName + ", SemesterID: " + semesterId);
                semesterBox.addItem(semesterName);
                semesterMap.put(semesterName, semesterId);
            }
            if (!found) {
                JOptionPane.showMessageDialog(null, "No semesters found.");
            }
            System.out.println("semesterMap after load: " + semesterMap);
            semesterBox.addActionListener(e -> {
                String selectedSemester = (String) semesterBox.getSelectedItem();
                if (selectedSemester != null) {
                    try {
                        loadCourses(selectedSemester);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error loading courses: " + ex.getMessage());
                    }
                } else {
                    System.out.println("No semester selected, skipping course load.");
                }
            });
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading semesters: " + ex.getMessage());
        }
    }

    private void loadCourses(String semesterName) throws SQLException {
        courseMap.clear();
        courseBox.removeAllItems();
        if (semesterName == null) {
            System.out.println("SemesterName is null, skipping course load.");
            return;
        }

        Integer semesterId = semesterMap.get(semesterName);
        if (semesterId == null) {
            System.out.println("SemesterID is null for semesterName: " + semesterName);
            System.out.println("Current semesterMap: " + semesterMap);
            return;
        }

        if (currentDeptId == null) {
            System.out.println("DepartmentID is null for selected batch");
            return;
        }

        System.out.println("Loading courses for DepartmentID: " + currentDeptId + ", SemesterID: " + semesterId);

        try (Connection conn = new Connector().connection;
             CallableStatement cs = conn.prepareCall("{call GetCoursesByDepartment(?,?)}")) {
            cs.setInt(1, currentDeptId);
            cs.setInt(2, semesterId);
            try (ResultSet rs = cs.executeQuery()) {
                boolean found = false;
                while (rs.next()) {
                    found = true;
                    String courseCode = rs.getString("CourseCode");
                    String courseName = rs.getString("CourseName");
                    if (courseCode == null || courseName == null) {
                        System.err.println("Warning: Null CourseCode or CourseName for CourseID: " + rs.getInt("CourseID"));
                        continue;
                    }
                    String courseInfo = courseCode.trim() + " - " + courseName.trim();
                    System.out.println("Adding course: " + courseInfo + ", CourseID: " + rs.getInt("CourseID"));
                    courseBox.addItem(courseInfo);
                    courseMap.put(courseInfo, rs.getInt("CourseID"));
                }
                if (!found) {
                    JOptionPane.showMessageDialog(null, "No courses found for this department and semester.");
                }
                System.out.println("courseMap after load: " + courseMap);
                courseBox.addActionListener(e -> {
                    try {
                        loadRollNumbers((String) courseBox.getSelectedItem());
                        revalidate();
                        repaint();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error loading roll numbers: " + ex.getMessage());
                    }
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading courses: " + ex.getMessage());
        }
    }

    private void loadRollNumbers(String courseInfo) throws SQLException {
        enrollmentMap.clear();
        choiceRollNo.removeAll();
        choiceRollNo.setVisible(true);

        if (courseInfo == null) {
            System.out.println("No course selected.");
            return;
        }

        System.out.println("Selected courseInfo: " + courseInfo);

        Integer courseId = courseMap.get(courseInfo);
        Integer semesterId = semesterMap.get((String) semesterBox.getSelectedItem());
        Integer batchId = batchMap.get((String) batchBox.getSelectedItem());

        if (courseId == null) {
            System.out.println("CourseID not found in courseMap for courseInfo: " + courseInfo);
            System.out.println("Current courseMap: " + courseMap);
            return;
        }
        if (semesterId == null || batchId == null) {
            System.out.println("Invalid parameters: CourseID=" + courseId + ", SemesterID=" + semesterId + ", BatchID=" + batchId);
            return;
        }

        System.out.println("Loading roll numbers for CourseID: " + courseId + ", SemesterID: " + semesterId + ", BatchID: " + batchId);

        try (Connection conn = new Connector().connection;
             CallableStatement cs = conn.prepareCall("{call GetRollNumbersByCourse(?,?,?)}")) {
            cs.setInt(1, courseId);
            cs.setInt(2, semesterId);
            cs.setInt(3, batchId);
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    String rollNo = rs.getString("RollNo");
                    if (rollNo == null) {
                        System.err.println("Warning: Null RollNo for EnrollmentID: " + rs.getInt("EnrollmentID"));
                        continue;
                    }
                    choiceRollNo.add(rollNo);
                    enrollmentMap.put(rollNo, rs.getInt("EnrollmentID"));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading roll numbers: " + ex.getMessage());
        }
        revalidate();
        repaint();
    }

    private void markAttendance(int enrollmentID, String date, String status) throws SQLException {
        try (Connection conn = new Connector().connection;
             CallableStatement cs = conn.prepareCall("{call MarkStudentAttendance(?,?,?)}")) {
            cs.setInt(1, enrollmentID);
            cs.setString(2, date);
            cs.setString(3, status);
            cs.execute();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submit) {
            String rollNo = choiceRollNo.getSelectedItem();
            String courseInfo = (String) courseBox.getSelectedItem();
            String status = (String) statusBox.getSelectedItem();

            if (batchBox.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(null, "Please select a batch.");
                return;
            }
            if (semesterBox.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(null, "Please select a semester.");
                return;
            }
            if (courseInfo == null) {
                JOptionPane.showMessageDialog(null, "Please select a course.");
                return;
            }
            if (rollNo == null || rollNo.equals("No students found")) {
                JOptionPane.showMessageDialog(null, "Please select a valid roll number.");
                return;
            }
            if (attendanceDate.getDate() == null) {
                JOptionPane.showMessageDialog(null, "Please select an attendance date.");
                return;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String date = sdf.format(attendanceDate.getDate());

            Integer enrollmentID = enrollmentMap.get(rollNo);
            if (enrollmentID == null) {
                JOptionPane.showMessageDialog(null, "Enrollment ID not found.");
                return;
            }

            try {
                markAttendance(enrollmentID, date, status);
                JOptionPane.showMessageDialog(null, "Attendance Marked Successfully");
                setVisible(false);
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error marking attendance: " + ex.getMessage());
            }
        } else {
            setVisible(false);
        }
    }

    public static void main(String[] args) {
        new MarkAttendance();
    }
}