package studentmanagementsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class AddCourse extends JFrame implements ActionListener {
    JTextField textCourseName, textCourseCode, textCredits;
    JComboBox departmentBox, courseTypeBox;
    JLabel departmentHeadLabel;
    JButton submit, cancel;

    AddCourse() {
        getContentPane().setBackground(new Color(210, 232, 252));

        JLabel heading = new JLabel("Add New Course");
        heading.setBounds(40, 50, 300, 30);
        heading.setFont(new Font("Tahoma", Font.BOLD, 20));
        add(heading);

        JLabel courseName = new JLabel("Course Name");
        courseName.setBounds(60, 100, 200, 20);
        courseName.setFont(new Font("Tahoma", Font.PLAIN, 18));
        add(courseName);

        textCourseName = new JTextField();
        textCourseName.setBounds(60, 130, 200, 25);
        add(textCourseName);

        JLabel courseCode = new JLabel("Course Code");
        courseCode.setBounds(60, 160, 200, 20);
        courseCode.setFont(new Font("Tahoma", Font.PLAIN, 18));
        add(courseCode);

        textCourseCode = new JTextField();
        textCourseCode.setBounds(60, 190, 200, 25);
        add(textCourseCode);

        JLabel department = new JLabel("Department");
        department.setBounds(60, 220, 200, 20);
        department.setFont(new Font("Tahoma", Font.PLAIN, 18));
        add(department);

        departmentBox = new JComboBox();
        departmentBox.setBounds(60, 250, 200, 25);
        departmentBox.setBackground(Color.WHITE);
        add(departmentBox);

        JLabel deptHead = new JLabel("Department Head");
        deptHead.setBounds(60, 280, 200, 20);
        deptHead.setFont(new Font("Tahoma", Font.PLAIN, 18));
        add(deptHead);

        departmentHeadLabel = new JLabel();
        departmentHeadLabel.setBounds(60, 310, 200, 25);
        add(departmentHeadLabel);

        try {
            loadDepartments();
        } catch (Exception e) {
            e.printStackTrace();
        }

        departmentBox.addActionListener(e -> {
            String selectedDept = (String) departmentBox.getSelectedItem();
            try {
                updateDepartmentHead(selectedDept);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        JLabel courseType = new JLabel("Course Type");
        courseType.setBounds(60, 340, 200, 20);
        courseType.setFont(new Font("Tahoma", Font.PLAIN, 18));
        add(courseType);

        String[] courseTypes = {"Core", "Elective"};
        courseTypeBox = new JComboBox(courseTypes);
        courseTypeBox.setBounds(60, 370, 200, 25);
        courseTypeBox.setBackground(Color.WHITE);
        add(courseTypeBox);

        JLabel credits = new JLabel("Credits");
        credits.setBounds(60, 400, 200, 20);
        credits.setFont(new Font("Tahoma", Font.PLAIN, 18));
        add(credits);

        textCredits = new JTextField();
        textCredits.setBounds(60, 430, 200, 25);
        add(textCredits);

        submit = new JButton("Add");
        submit.setBounds(60, 470, 100, 25);
        submit.setBackground(Color.BLACK);
        submit.setForeground(Color.WHITE);
        submit.addActionListener(this);
        add(submit);

        cancel = new JButton("Cancel");
        cancel.setBounds(200, 470, 100, 25);
        cancel.setBackground(Color.BLACK);
        cancel.setForeground(Color.WHITE);
        cancel.addActionListener(this);
        add(cancel);

        setSize(400, 550);
        setLocation(550, 100);
        setLayout(null);
        setVisible(true);
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

        if (departmentBox.getItemCount() > 0) {
            updateDepartmentHead((String) departmentBox.getSelectedItem());
        }
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

    private int addNewCourse(int departmentID, String courseName, String courseCode, String courseType, int credits) throws SQLException {
        Connection conn = new Connector().connection;
        CallableStatement cs = conn.prepareCall("{call AddNewCourse(?, ?, ?, ?, ?)}");
        cs.setInt(1, departmentID);
        cs.setString(2, courseName);
        cs.setString(3, courseCode);
        cs.setString(4, courseType);
        cs.setInt(5, credits);

        ResultSet rs = cs.executeQuery();
        int courseID = 0;
        if (rs.next()) {
            courseID = rs.getInt("CourseID");
        }

        rs.close();
        cs.close();

        return courseID;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submit) {
            String courseName = textCourseName.getText().trim();
            String courseCode = textCourseCode.getText().trim();
            String departmentName = (String) departmentBox.getSelectedItem();
            String courseType = (String) courseTypeBox.getSelectedItem();
            String creditsText = textCredits.getText().trim();

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

                int courseID = addNewCourse(departmentID, courseName, courseCode, courseType, credits);

                if (courseID > 0) {
                    JOptionPane.showMessageDialog(null, "Course Added Successfully (ID: " + courseID + ")");
                } else {
                    JOptionPane.showMessageDialog(null, "Course Added Successfully but ID could not be retrieved");
                }

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
        new AddCourse();
    }
}
