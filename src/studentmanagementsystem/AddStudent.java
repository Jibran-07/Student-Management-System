package studentmanagementsystem;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class AddStudent extends JFrame implements ActionListener {
    JTextField textFirstName, textLastName, textAddress, textPhone, textEmail, textDepartment;
    JLabel rollNoText;
    JComboBox batchBox;
    JButton add, cancel;

    AddStudent() {
        getContentPane().setBackground(new Color(128, 176, 255));

        JLabel heading = new JLabel("Add New Student");
        heading.setBounds(310, 30, 500, 50);
        heading.setFont(new Font("serif", Font.BOLD, 30));
        add(heading);

        JLabel firstName = new JLabel("First Name");
        firstName.setBounds(50, 150, 100, 30);
        firstName.setFont(new Font("serif", Font.BOLD, 20));
        add(firstName);

        textFirstName = new JTextField();
        textFirstName.setBounds(200, 150, 150, 30);
        ((AbstractDocument) textFirstName.getDocument()).setDocumentFilter(new LetterOnlyFilter());
        add(textFirstName);

        JLabel lastName = new JLabel("Last Name");
        lastName.setBounds(400, 150, 200, 30);
        lastName.setFont(new Font("serif", Font.BOLD, 20));
        add(lastName);

        textLastName = new JTextField();
        textLastName.setBounds(600, 150, 150, 30);
        ((AbstractDocument) textLastName.getDocument()).setDocumentFilter(new LetterOnlyFilter());
        add(textLastName);

        JLabel rollNo = new JLabel("Roll Number");
        rollNo.setBounds(50, 200, 200, 30);
        rollNo.setFont(new Font("serif", Font.BOLD, 20));
        add(rollNo);

        rollNoText = new JLabel("Will be auto-generated");
        rollNoText.setBounds(200, 200, 200, 30);
        rollNoText.setFont(new Font("serif", Font.ITALIC, 16));
        rollNoText.setForeground(Color.GRAY);
        add(rollNoText);

        JLabel address = new JLabel("Address");
        address.setBounds(50, 250, 200, 30);
        address.setFont(new Font("serif", Font.BOLD, 20));
        add(address);

        textAddress = new JTextField();
        textAddress.setBounds(200, 250, 150, 30);
        add(textAddress);

        JLabel phone = new JLabel("Phone");
        phone.setBounds(400, 250, 200, 30);
        phone.setFont(new Font("serif", Font.BOLD, 20));
        add(phone);

        textPhone = new JTextField();
        textPhone.setBounds(600, 250, 150, 30);
        add(textPhone);

        JLabel email = new JLabel("Email");
        email.setBounds(50, 300, 200, 30);
        email.setFont(new Font("serif", Font.BOLD, 20));
        add(email);

        textEmail = new JTextField();
        textEmail.setBounds(200, 300, 150, 30);
        textEmail.setEditable(false);
        textEmail.setBackground(Color.WHITE);
        add(textEmail);

        textFirstName.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateEmail();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateEmail();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateEmail();
            }
        });

        textLastName.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateEmail();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateEmail();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateEmail();
            }
        });

        JLabel batch = new JLabel("Batch");
        batch.setBounds(50, 350, 200, 30);
        batch.setFont(new Font("serif", Font.BOLD, 20));
        add(batch);

        batchBox = new JComboBox();
        batchBox.setBounds(200, 350, 150, 30);
        batchBox.setBackground(Color.WHITE);
        try {
            loadBatches();
        } catch (Exception e) {
            e.printStackTrace();
        }
        batchBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedBatch = (String) batchBox.getSelectedItem();
                if (selectedBatch != null) {
                    try {
                        String departmentName = getDepartmentByBatch(selectedBatch);
                        if (departmentName != null && !departmentName.isEmpty()) {
                            textDepartment.setText(departmentName);
                        } else {
                            textDepartment.setText("");
                            JOptionPane.showMessageDialog(null, "Unable to determine department for batch: " + selectedBatch);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        textDepartment.setText("");
                        JOptionPane.showMessageDialog(null, "Error determining department: " + ex.getMessage());
                    }
                } else {
                    textDepartment.setText("");
                }
            }
        });
        add(batchBox);

        JLabel department = new JLabel("Department");
        department.setBounds(50, 400, 200, 30);
        department.setFont(new Font("serif", Font.BOLD, 20));
        add(department);

        textDepartment = new JTextField();
        textDepartment.setBounds(200, 400, 150, 30);
        textDepartment.setEditable(false);
        textDepartment.setBackground(Color.WHITE);
        add(textDepartment);

        add = new JButton("Add");
        add.setBounds(250, 500, 120, 30);
        add.setBackground(Color.BLACK);
        add.setForeground(Color.WHITE);
        add.addActionListener(this);
        add(add);

        cancel = new JButton("Cancel");
        cancel.setBounds(450, 500, 120, 30);
        cancel.setBackground(Color.BLACK);
        cancel.setForeground(Color.WHITE);
        cancel.addActionListener(this);
        add(cancel);

        setSize(900, 600);
        setLocation(350, 50);
        setLayout(null);
        setVisible(true);
    }

    private class LetterOnlyFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (string == null) return;
            if (string.matches("[a-zA-Z\\s]*")) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (text == null) return;
            if (text.matches("[a-zA-Z\\s]*")) {
                super.replace(fb, offset, length, text, attrs);
            }
        }
    }

    private boolean doesEmailExist(String email) throws SQLException {
        Connection conn = new Connector().connection;
        CallableStatement cs = conn.prepareCall("{call SearchStudentsByEmail(?)}");
        cs.setString(1, email);
        ResultSet rs = cs.executeQuery();
        boolean exists = rs.next(); // Check if any row exists
        rs.close();
        cs.close();
        return exists;
    }

    private String generateUniqueEmail(String firstName, String lastName) throws SQLException {
        String baseEmail = firstName.trim().toLowerCase() + lastName.trim().toLowerCase() + "@ksbl.edu.pk";
        if (!doesEmailExist(baseEmail)) {
            return baseEmail;
        }

        int suffix = 1;
        String newEmail;
        do {
            newEmail = firstName.trim().toLowerCase() + lastName.trim().toLowerCase() + suffix + "@ksbl.edu.pk";
            suffix++;
        } while (doesEmailExist(newEmail));
        return newEmail;
    }

    private void updateEmail() {
        String firstName = textFirstName.getText();
        String lastName = textLastName.getText();
        String email = "";
        if (!firstName.isEmpty() && !lastName.isEmpty()) {
            try {
                email = generateUniqueEmail(firstName, lastName);
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error generating email: " + e.getMessage());
            }
        }
        textEmail.setText(email);
    }

    private void loadBatches() throws SQLException {
        Connection conn = new Connector().connection;
        CallableStatement cs = conn.prepareCall("{call GetBatches}");
        ResultSet rs = cs.executeQuery();
        while (rs.next()) {
            batchBox.addItem(rs.getString("BatchName"));
        }
        rs.close();
        cs.close();
    }

    private String getDepartmentByBatch(String batchName) {
        if (batchName == null || batchName.isEmpty()) {
            System.out.println("Batch name is null or empty");
            return null;
        }
        String[] parts = batchName.split("_", 2);
        if (parts.length < 1) {
            System.out.println("Invalid batch name format: " + batchName);
            return null;
        }
        String deptCode = parts[0].toUpperCase();
        switch (deptCode) {
            case "ACF":
                return "ACF";
            case "ME":
                return "ME";
            case "IT":
                return "IT";
            case "CS":
                return "CS";
            default:
                System.out.println("Unrecognized department code in batch: " + deptCode);
                return null;
        }
    }

    private int getBatchIDByName(String batchName) throws SQLException {
        Connection conn = new Connector().connection;
        CallableStatement cs = conn.prepareCall("{call GetBatchIDByName(?)}");
        cs.setString(1, batchName);
        ResultSet rs = cs.executeQuery();
        int batchID = 0;
        if (rs.next()) {
            batchID = rs.getInt("BatchID");
        }
        rs.close();
        cs.close();
        return batchID;
    }

    private int getDepartmentIDByName(String departmentName) throws SQLException {
        if (departmentName == null || departmentName.isEmpty()) {
            return 0;
        }
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

    private String getDepartmentPrefix(String departmentName) {
        if (departmentName == null) {
            return "B00";
        }
        switch (departmentName.toUpperCase()) {
            case "ACF":
            case "ACCOUNTING AND FINANCE":
                return "B01";
            case "ME":
            case "MANAGEMENT AND ENTREPRENEURSHIP":
                return "B02";
            case "IT":
            case "INFORMATION TECHNOLOGY":
                return "B03";
            case "CS":
            case "COMPUTER SCIENCE":
                return "B04";
            default:
                return "B00";
        }
    }

    private String generateRollNumber(int departmentID, String departmentName) throws SQLException {
        String prefix = getDepartmentPrefix(departmentName);
        Connection conn = new Connector().connection;
        CallableStatement cs = conn.prepareCall("{call GetNextRollNumber(?)}");
        cs.setInt(1, departmentID);
        ResultSet rs = cs.executeQuery();
        int nextNumber = 1;
        if (rs.next()) {
            nextNumber = rs.getInt("NextNumber");
        }
        rs.close();
        cs.close();
        return prefix + "-" + String.format("%05d", nextNumber);
    }

    private int getSemesterIDByName(String semesterName) throws SQLException {
        Connection conn = new Connector().connection;
        CallableStatement cs = conn.prepareCall("{call GetSemesterIDByName(?)}");
        cs.setString(1, semesterName);
        ResultSet rs = cs.executeQuery();
        int semesterID = 0;
        if (rs.next()) {
            semesterID = rs.getInt("SemesterID");
        } else {
            CallableStatement insertCs = conn.prepareCall("{call InsertSemester(?, ?, ?)}");
            insertCs.setString(1, semesterName);
            insertCs.setString(2, "2024-09-01");
            insertCs.setString(3, "2025-01-31");
            ResultSet insertRs = insertCs.executeQuery();
            if (insertRs.next()) {
                semesterID = insertRs.getInt("SemesterID");
            }
            insertRs.close();
            insertCs.close();
        }
        rs.close();
        cs.close();
        return semesterID;
    }

    private int getStudentIDByRollNo(String rollNo) throws SQLException {
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
        return studentID;
    }

    private int addNewStudent(int departmentID, int batchID, String firstName, String lastName,
                              String rollNo, String email, String phone, String address) throws SQLException {
        Connection conn = new Connector().connection;
        CallableStatement cs = conn.prepareCall("{call AddNewStudent(?, ?, ?, ?, ?, ?, ?, ?)}");
        cs.setInt(1, departmentID);
        cs.setInt(2, batchID);
        cs.setString(3, firstName);
        cs.setString(4, lastName);
        cs.setString(5, rollNo);
        cs.setString(6, email);
        cs.setString(7, phone);
        cs.setString(8, address);
        ResultSet rs = cs.executeQuery();
        int studentID = 0;
        if (rs.next()) {
            studentID = rs.getInt("StudentID");
        }
        rs.close();
        cs.close();
        return studentID;
    }

    private void enrollStudentInCourses(int studentID, int departmentID, int semesterID) throws SQLException {
        Connection conn = new Connector().connection;
        CallableStatement coursesCs = conn.prepareCall("{call GetCoursesByDepartment(?)}");
        coursesCs.setInt(1, departmentID);
        ResultSet coursesRs = coursesCs.executeQuery();
        while (coursesRs.next()) {
            int courseID = coursesRs.getInt("CourseID");
            CallableStatement enrollCs = conn.prepareCall("{call EnrollStudentInCourse(?, ?, ?)}");
            enrollCs.setInt(1, studentID);
            enrollCs.setInt(2, courseID);
            enrollCs.setInt(3, semesterID);
            enrollCs.execute();
            enrollCs.close();
        }
        coursesRs.close();
        coursesCs.close();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == add) {
            String firstName = textFirstName.getText();
            String lastName = textLastName.getText();
            String address = textAddress.getText();
            String phone = textPhone.getText();
            String email = textEmail.getText();
            String batchName = (String) batchBox.getSelectedItem();
            String departmentName = textDepartment.getText();

            try {
                if (firstName.isEmpty() || lastName.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please fill all required fields (First Name, Last Name)");
                    return;
                }

                if (!firstName.matches("[a-zA-Z\\s]+")) {
                    JOptionPane.showMessageDialog(null, "First Name can only contain letters and spaces");
                    return;
                }
                if (!lastName.matches("[a-zA-Z\\s]+")) {
                    JOptionPane.showMessageDialog(null, "Last Name can only contain letters and spaces");
                    return;
                }

                if (phone.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please enter a phone number");
                    return;
                }
                String phoneDigits = phone.replaceAll("[^0-9]", "");
                if (phoneDigits.length() != 11) {
                    JOptionPane.showMessageDialog(null, "Phone number must be exactly 11 digits (e.g., +923123456789)");
                    return;
                }

                if (batchName == null) {
                    JOptionPane.showMessageDialog(null, "Please select a Batch");
                    return;
                }

                if (departmentName == null || departmentName.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Unable to determine department for the selected batch");
                    return;
                }

                if (doesEmailExist(email)) {
                    email = generateUniqueEmail(firstName, lastName);
                    textEmail.setText(email);
                    JOptionPane.showMessageDialog(null, "Duplicate email detected. Updated to: " + email);
                }

                if (email.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Email cannot be empty (ensure First Name and Last Name are filled)");
                    return;
                }

                int batchID = getBatchIDByName(batchName);
                int departmentID = getDepartmentIDByName(departmentName);
                if (departmentID == 0) {
                    JOptionPane.showMessageDialog(null, "Invalid department: " + departmentName);
                    return;
                }

                String rollNo = generateRollNumber(departmentID, departmentName);
                int existingStudentID = getStudentIDByRollNo(rollNo);
                if (existingStudentID > 0) {
                    JOptionPane.showMessageDialog(null, "Roll number " + rollNo + " already exists. Please try again.");
                    return;
                }

                rollNoText.setText(rollNo);
                rollNoText.setFont(new Font("serif", Font.BOLD, 20));
                rollNoText.setForeground(Color.BLACK);

                int studentID = addNewStudent(departmentID, batchID, firstName, lastName, rollNo, email, phone, address);

                if (studentID <= 0) {
                    JOptionPane.showMessageDialog(null, "Failed to create student record");
                    return;
                }

                String semesterName = batchName.contains("_") ? batchName.substring(batchName.indexOf("_") + 1) : batchName;
                int semesterID = getSemesterIDByName(semesterName);

                enrollStudentInCourses(studentID, departmentID, semesterID);

                JOptionPane.showMessageDialog(null,
                        "Student Successfully Added!\n" +
                                "Name: " + firstName + " " + lastName + "\n" +
                                "Roll Number: " + rollNo + "\n" +
                                "Department: " + departmentName + "\n" +
                                "Student has been enrolled in all department courses.");

                setVisible(false);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
            }
        } else {
            setVisible(false);
        }
    }

    public static void main(String[] args) {
        new AddStudent();
    }
}