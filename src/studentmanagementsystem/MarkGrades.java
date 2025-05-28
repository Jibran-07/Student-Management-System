package studentmanagementsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class MarkGrades extends JFrame implements ActionListener {
    Choice choiceRollNo;
    JComboBox<String> semesterBox, courseBox;
    JTextField marksObtained;
    JTextArea gradeRangeArea;
    JScrollPane gradeScrollPane;
    JButton submit, cancel;

    MarkGrades() {
        getContentPane().setBackground(new Color(230, 230, 250));
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel heading = new JLabel("Mark Student Grades", SwingConstants.CENTER);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 32));
        heading.setForeground(new Color(33, 37, 41));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 30, 10);
        add(heading, gbc);

        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel rollNo = new JLabel("Select Roll Number");
        rollNo.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        rollNo.setForeground(new Color(66, 66, 66));
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(rollNo, gbc);

        choiceRollNo = new Choice();
        choiceRollNo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        try {
            loadRollNumbers();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading roll numbers: " + e.getMessage());
        }
        choiceRollNo.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                try {
                    loadCourses(choiceRollNo.getSelectedItem(), (String) semesterBox.getSelectedItem());
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error loading courses: " + ex.getMessage());
                }
            }
        });
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(choiceRollNo, gbc);

        JLabel semester = new JLabel("Select Semester");
        semester.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        semester.setForeground(new Color(66, 66, 66));
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(semester, gbc);

        semesterBox = new JComboBox<>();
        semesterBox.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        semesterBox.setBackground(Color.WHITE);
        semesterBox.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        try {
            loadSemesters();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading semesters: " + e.getMessage());
        }
        semesterBox.addActionListener(e -> {
            try {
                loadCourses(choiceRollNo.getSelectedItem(), (String) semesterBox.getSelectedItem());
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error loading courses: " + ex.getMessage());
            }
        });
        gbc.gridx = 1;
        gbc.gridy = 2;
        add(semesterBox, gbc);

        JLabel course = new JLabel("Select Course");
        course.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        course.setForeground(new Color(66, 66, 66));
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(course, gbc);

        courseBox = new JComboBox<>();
        courseBox.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        courseBox.setBackground(Color.WHITE);
        courseBox.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        gbc.gridx = 1;
        gbc.gridy = 3;
        add(courseBox, gbc);

        JLabel marksLabel = new JLabel("Marks Obtained");
        marksLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        marksLabel.setForeground(new Color(66, 66, 66));
        gbc.gridx = 0;
        gbc.gridy = 4;
        add(marksLabel, gbc);

        marksObtained = new JTextField();
        marksObtained.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        marksObtained.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        gbc.gridx = 1;
        gbc.gridy = 4;
        add(marksObtained, gbc);

        JLabel gradeRangeHeader = new JLabel("Grade Ranges:");
        gradeRangeHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        gradeRangeHeader.setForeground(new Color(66, 66, 66));
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        add(gradeRangeHeader, gbc);

        gradeRangeArea = new JTextArea();
        gradeRangeArea.setEditable(false);
        gradeRangeArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gradeRangeArea.setBackground(new Color(245, 245, 245));
        gradeRangeArea.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        gradeRangeArea.setLineWrap(true);

        gradeScrollPane = new JScrollPane(gradeRangeArea);
        gradeScrollPane.setBorder(BorderFactory.createEmptyBorder());
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.ipady = 60;
        add(gradeScrollPane, gbc);

        try {
            updateGradeRanges();
        } catch (SQLException e) {
            e.printStackTrace();
            gradeRangeArea.setText("Error loading grade ranges");
        }

        gbc.gridwidth = 1;
        gbc.ipady = 0;

        submit = new JButton("Mark");
        submit.setFont(new Font("Segoe UI", Font.BOLD, 16));
        submit.setBackground(new Color(0, 123, 255));
        submit.setForeground(Color.WHITE);
        submit.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        submit.setFocusPainted(false);
        submit.addActionListener(this);
        gbc.gridx = 0;
        gbc.gridy = 7;
        add(submit, gbc);

        cancel = new JButton("Cancel");
        cancel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        cancel.setBackground(new Color(108, 117, 125));
        cancel.setForeground(Color.WHITE);
        cancel.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        cancel.setFocusPainted(false);
        cancel.addActionListener(this);
        gbc.gridx = 1;
        gbc.gridy = 7;
        add(cancel, gbc);

        setSize(500, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    private void loadRollNumbers() throws SQLException {
        Connection conn = new Connector().connection;
        CallableStatement cs = conn.prepareCall("{call SearchStudentsByRollNo(?)}");
        cs.setString(1, "%");
        ResultSet rs = cs.executeQuery();
        while (rs.next()) {
            choiceRollNo.add(rs.getString("RollNo"));
        }
        rs.close();
        cs.close();
    }

    private void loadSemesters() throws SQLException {
        Connection conn = new Connector().connection;
        CallableStatement cs = conn.prepareCall("{call GetSemesters}");
        ResultSet rs = cs.executeQuery();
        while (rs.next()) {
            semesterBox.addItem(rs.getString("SemesterName"));
        }
        rs.close();
        cs.close();
    }

    private void loadCourses(String rollNo, String semesterName) throws SQLException {
        courseBox.removeAllItems();
        if (rollNo == null || semesterName == null) {
            return;
        }

        Connection conn = new Connector().connection;
        CallableStatement cs = conn.prepareCall("{call GetStudentIDByRollNo(?)}");
        cs.setString(1, rollNo);
        ResultSet rs = cs.executeQuery();
        int studentID = 0;
        if (rs.next()) {
            studentID = rs.getInt("StudentID");
        }
        rs.close();
        cs.close();

        if (studentID == 0) {
            return;
        }

        CallableStatement semesterCs = conn.prepareCall("{call GetSemesterIDByName(?)}");
        semesterCs.setString(1, semesterName);
        ResultSet semesterRs = semesterCs.executeQuery();
        int semesterID = 0;
        if (semesterRs.next()) {
            semesterID = semesterRs.getInt("SemesterID");
        }
        semesterRs.close();
        semesterCs.close();

        if (semesterID == 0) {
            return;
        }

        CallableStatement enrollCs = conn.prepareCall("{call GetEnrollmentsByStudentAndSemester(?,?)}");
        enrollCs.setInt(1, studentID);
        enrollCs.setInt(2, semesterID);
        ResultSet enrollRs = enrollCs.executeQuery();
        boolean coursesFound = false;
        while (enrollRs.next()) {
            coursesFound = true;
            String courseName = enrollRs.getString("CourseName");
            courseBox.addItem(courseName);
            courseBox.putClientProperty(courseName, enrollRs.getInt("EnrollmentID"));
        }
        enrollRs.close();
        enrollCs.close();

        if (!coursesFound) {
            JOptionPane.showMessageDialog(null,
                    "No enrollments found for this student in the selected semester.\n" +
                            "Please check if the student is enrolled in any courses this semester.");
        }
    }

    private void updateGradeRanges() throws SQLException {
        Connection conn = new Connector().connection;
        CallableStatement cs = conn.prepareCall("{call GetGradeRanges}");
        ResultSet rs = cs.executeQuery();
        StringBuilder ranges = new StringBuilder();
        while (rs.next()) {
            int start = rs.getInt("GradeStart");
            int end = rs.getInt("GradeEnd");
            String value = rs.getString("GradeValue");
            ranges.append(start).append("-").append(end).append(": ").append(value).append("\n");
        }
        rs.close();
        cs.close();
        gradeRangeArea.setText(ranges.toString());
    }

    private void insertMarks(int enrollmentID, int gradeID, float marksValue) throws SQLException {
        Connection conn = new Connector().connection;
        CallableStatement cs = conn.prepareCall("{call InsertMarks(?,?,?)}");
        cs.setInt(1, enrollmentID);
        cs.setInt(2, gradeID);
        cs.setFloat(3, marksValue);
        cs.execute();
        cs.close();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submit) {
            String rollNo = choiceRollNo.getSelectedItem();
            String semesterName = (String) semesterBox.getSelectedItem();
            String courseName = (String) courseBox.getSelectedItem();
            String marksText = marksObtained.getText();

            if (courseName == null) {
                JOptionPane.showMessageDialog(null, "Please select a course.");
                return;
            }
            if (marksText.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter marks obtained.");
                return;
            }

            try {
                float marksValue = Float.parseFloat(marksText);
                if (marksValue < 0 || marksValue > 100) {
                    JOptionPane.showMessageDialog(null, "Marks must be between 0 and 100.");
                    return;
                }

                Connection conn = new Connector().connection;
                CallableStatement gradeCs = conn.prepareCall("{call GetGradeIDByMarks(?)}");
                gradeCs.setFloat(1, marksValue);
                ResultSet gradeRs = gradeCs.executeQuery();
                int gradeID = 0;
                if (gradeRs.next()) {
                    gradeID = gradeRs.getInt("GradeID");
                } else {
                    JOptionPane.showMessageDialog(null,
                            "No grade range found for marks: " + marksValue +
                                    "\nPlease ensure grade ranges are properly defined in the database.");
                    gradeRs.close();
                    gradeCs.close();
                    return;
                }
                gradeRs.close();
                gradeCs.close();

                Integer enrollmentID = (Integer) courseBox.getClientProperty(courseName);
                if (enrollmentID == null) {
                    JOptionPane.showMessageDialog(null, "Invalid enrollment selected.");
                    return;
                }

                insertMarks(enrollmentID, gradeID, marksValue);

                JOptionPane.showMessageDialog(null, "Marks Recorded Successfully");
                setVisible(false);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Marks Obtained must be a valid number.");
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error recording marks: " + ex.getMessage());
            }
        } else {
            setVisible(false);
        }
    }

    public static void main(String[] args) {
        new MarkGrades();
    }
}