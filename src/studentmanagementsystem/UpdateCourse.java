package studentmanagementsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.*;

public class UpdateCourse extends JFrame implements ActionListener {
    Choice cCourseID;
    JTextField textCourseName, textCourseCode, textCredits;
    JComboBox departmentBox, courseTypeBox;
    JLabel departmentHeadLabel;
    JButton submit, cancel;

    UpdateCourse() {
        getContentPane().setBackground(new Color(210, 232, 252));

        JLabel heading = new JLabel("Update Course Details");
        heading.setBounds(40, 50, 300, 30);
        heading.setFont(new Font("Tahoma", Font.BOLD, 20));
        add(heading);

        JLabel courseID = new JLabel("Select Course ID");
        courseID.setBounds(60, 100, 200, 20);
        courseID.setFont(new Font("Tahoma", Font.PLAIN, 18));
        add(courseID);

        cCourseID = new Choice();
        cCourseID.setBounds(60, 130, 200, 20);
        try {
            loadCourseIDs();
        } catch (Exception e) {
            e.printStackTrace();
        }
        add(cCourseID);

        JLabel courseName = new JLabel("Course Name");
        courseName.setBounds(60, 160, 200, 20);
        courseName.setFont(new Font("Tahoma", Font.PLAIN, 18));
        add(courseName);

        textCourseName = new JTextField();
        textCourseName.setBounds(60, 190, 200, 25);
        add(textCourseName);

        JLabel courseCode = new JLabel("Course Code");
        courseCode.setBounds(60, 220, 200, 20);
        courseCode.setFont(new Font("Tahoma", Font.PLAIN, 18));
        add(courseCode);

        textCourseCode = new JTextField();
        textCourseCode.setBounds(60, 250, 200, 25);
        add(textCourseCode);

        JLabel department = new JLabel("Department");
        department.setBounds(60, 280, 200, 20);
        department.setFont(new Font("Tahoma", Font.PLAIN, 18));
        add(department);

        departmentBox = new JComboBox();
        departmentBox.setBounds(60, 310, 200, 25);
        departmentBox.setBackground(Color.WHITE);
        try {
            loadDepartments();
        } catch (Exception e) {
            e.printStackTrace();
        }
        add(departmentBox);

        JLabel deptHead = new JLabel("Department Head");
        deptHead.setBounds(60, 340, 200, 20);
        deptHead.setFont(new Font("Tahoma", Font.PLAIN, 18));
        add(deptHead);

        departmentHeadLabel = new JLabel();
        departmentHeadLabel.setBounds(60, 370, 200, 25);
        add(departmentHeadLabel);

        departmentBox.addActionListener(e -> {
            String selectedDept = (String) departmentBox.getSelectedItem();
            try {
                updateDepartmentHead(selectedDept);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        JLabel courseType = new JLabel("Course Type");
        courseType.setBounds(60, 400, 200, 20);
        courseType.setFont(new Font("Tahoma", Font.PLAIN, 18));
        add(courseType);

        String[] courseTypes = {"Core", "Elective"};
        courseTypeBox = new JComboBox(courseTypes);
        courseTypeBox.setBounds(60, 430, 200, 25);
        courseTypeBox.setBackground(Color.WHITE);
        add(courseTypeBox);

        JLabel credits = new JLabel("Credits");
        credits.setBounds(60, 460, 200, 20);
        credits.setFont(new Font("Tahoma", Font.PLAIN, 18));
        add(credits);

        textCredits = new JTextField();
        textCredits.setBounds(60, 490, 200, 25);
        add(textCredits);

        // Load initial course data if available
        if (cCourseID.getItemCount() > 0) {
            try {
                loadCourseDetails(cCourseID.getSelectedItem());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        cCourseID.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                try {
                    loadCourseDetails(cCourseID.getSelectedItem());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        submit = new JButton("Update");
        submit.setBounds(60, 530, 100, 25);
        submit.setBackground(Color.BLACK);
        submit.setForeground(Color.WHITE);
        submit.addActionListener(this);
        add(submit);

        cancel = new JButton("Cancel");
        cancel.setBounds(200, 530, 100, 25);
        cancel.setBackground(Color.BLACK);
        cancel.setForeground(Color.WHITE);
        cancel.addActionListener(this);
        add(cancel);

        setSize(400, 600);
        setLocation(550, 100);
        setLayout(null);
        setVisible(true);
    }

    private void loadCourseIDs() throws SQLException {
        Connection conn = new Connector().connection;
        CallableStatement cs = conn.prepareCall("{call GetAllCourseIDs}");
        ResultSet rs = cs.executeQuery();
        while (rs.next()) {
            cCourseID.add(rs.getString("CourseID"));
        }
        rs.close();
        cs.close();
    }

    private void loadDepartments() throws SQLException {
        Connection conn = new Connector().connection;
        CallableStatement cs = conn.prepareCall("{call GetDepartments}");
        ResultSet rs = cs.executeQuery();
        while (rs.next()) {
            departmentBox.addItem(rs.getString("DepartmentName"));
        }
        rs.close();
        cs.close();
    }

    private void updateDepartmentHead(String departmentName) throws SQLException {
        Connection conn = new Connector().connection;
        CallableStatement cs = conn.prepareCall("{call GetDepartmentHeadByDepartmentName(?)}");
        cs.setString(1, departmentName);
        ResultSet rs = cs.executeQuery();
        if (rs.next()) {
            departmentHeadLabel.setText(rs.getString("HeadName"));
        } else {
            departmentHeadLabel.setText("No Head Assigned");
        }
        rs.close();
        cs.close();
    }

    private void loadCourseDetails(String courseID) throws SQLException {
        Connection conn = new Connector().connection;
        CallableStatement cs = conn.prepareCall("{call GetCourseDetailsByID(?)}");
        cs.setString(1, courseID);
        ResultSet rs = cs.executeQuery();
        if (rs.next()) {
            textCourseName.setText(rs.getString("CourseName"));
            textCourseCode.setText(rs.getString("CourseCode"));
            departmentBox.setSelectedItem(rs.getString("DepartmentName"));
            courseTypeBox.setSelectedItem(rs.getString("CourseType"));
            textCredits.setText(rs.getString("Credits"));

            updateDepartmentHead(rs.getString("DepartmentName"));
        }
        rs.close();
        cs.close();
    }

    private int getDepartmentIDByName(String departmentName) throws SQLException {
        Connection conn = new Connector().connection;
        CallableStatement cs = conn.prepareCall("{call GetDepartmentIDByName(?)}");
        cs.setString(1, departmentName);
        ResultSet rs = cs.executeQuery();
        int departmentID = 0;
        if (rs.next()) {
            departmentID = rs.getInt("DepartmentID");
        }
        rs.close();
        cs.close();
        return departmentID;
    }

    private void updateCourse(String courseID, String courseName, String courseCode,
                              int departmentID, String courseType, int credits) throws SQLException {
        Connection conn = new Connector().connection;
        CallableStatement cs = conn.prepareCall("{call UpdateCourseDetails(?, ?, ?, ?, ?, ?)}");
        cs.setString(1, courseID);
        cs.setString(2, courseName);
        cs.setString(3, courseCode);
        cs.setInt(4, departmentID);
        cs.setString(5, courseType);
        cs.setInt(6, credits);
        cs.execute();
        cs.close();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submit) {
            String courseID = cCourseID.getSelectedItem();
            String courseName = textCourseName.getText().trim();
            String courseCode = textCourseCode.getText().trim();
            String departmentName = (String) departmentBox.getSelectedItem();
            String courseType = (String) courseTypeBox.getSelectedItem();
            String creditsText = textCredits.getText().trim();

            // Validation
            if (courseName.isEmpty() || courseCode.isEmpty() || creditsText.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please fill all required fields.");
                return;
            }

            int credits = 0;
            try {
                credits = Integer.parseInt(creditsText);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Please enter a valid number for credits.");
                return;
            }

            try {
                int departmentID = getDepartmentIDByName(departmentName);

                if (departmentID == 0) {
                    JOptionPane.showMessageDialog(null, "Invalid department selected.");
                    return;
                }

                updateCourse(courseID, courseName, courseCode, departmentID, courseType, credits);

                JOptionPane.showMessageDialog(null, "Course Updated Successfully");
                setVisible(false);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
            }
        } else if (e.getSource() == cancel) {
            setVisible(false);
        }
    }

    public static void main(String[] args) {
        new UpdateCourse();
    }
}