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

public class AddInstructor extends JFrame implements ActionListener {
    JTextField textFirstName, textLastName, textEmail, textPhone;
    JLabel empIDText;
    JComboBox departmentBox;
    JButton add, cancel;

    AddInstructor() {
        getContentPane().setBackground(new Color(166, 164, 252));

        JLabel heading = new JLabel("Add New Instructor");
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

        JLabel empID = new JLabel("Employee ID");
        empID.setBounds(50, 200, 200, 30);
        empID.setFont(new Font("serif", Font.BOLD, 20));
        add(empID);

        empIDText = new JLabel("Will be auto-generated");
        empIDText.setBounds(200, 200, 200, 30);
        empIDText.setFont(new Font("serif", Font.ITALIC, 16));
        empIDText.setForeground(Color.GRAY);
        add(empIDText);

        JLabel email = new JLabel("Email");
        email.setBounds(50, 250, 200, 30);
        email.setFont(new Font("serif", Font.BOLD, 20));
        add(email);

        textEmail = new JTextField();
        textEmail.setBounds(200, 250, 150, 30);
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

        JLabel phone = new JLabel("Phone");
        phone.setBounds(400, 250, 200, 30);
        phone.setFont(new Font("serif", Font.BOLD, 20));
        add(phone);

        textPhone = new JTextField();
        textPhone.setBounds(600, 250, 150, 30);
        add(textPhone);

        JLabel department = new JLabel("Department");
        department.setBounds(50, 300, 200, 30);
        department.setFont(new Font("serif", Font.BOLD, 20));
        add(department);

        departmentBox = new JComboBox();
        departmentBox.setBounds(200, 300, 150, 30);
        departmentBox.setBackground(Color.WHITE);
        try {
            loadDepartments();
        } catch (Exception e) {
            e.printStackTrace();
        }
        add(departmentBox);

        add = new JButton("Add");
        add.setBounds(250, 400, 120, 30);
        add.setBackground(Color.BLACK);
        add.setForeground(Color.WHITE);
        add.addActionListener(this);
        add(add);

        cancel = new JButton("Cancel");
        cancel.setBounds(450, 400, 120, 30);
        cancel.setBackground(Color.BLACK);
        cancel.setForeground(Color.WHITE);
        cancel.addActionListener(this);
        add(cancel);

        setSize(900, 500);
        setLocation(350, 100);
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
        CallableStatement cs = conn.prepareCall("{call GetInstructorDetailsByEmail(?)}");
        cs.setString(1, email);
        ResultSet rs = cs.executeQuery();
        boolean exists = rs.next();
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

    private String getDepartmentPrefix(String departmentName) {
        switch (departmentName.toUpperCase()) {
            case "ACF":
            case "ACCOUNTING AND FINANCE":
                return "E01";
            case "ME":
            case "MANAGEMENT AND ENTREPRENEURSHIP":
                return "E02";
            case "IT":
            case "INFORMATION TECHNOLOGY":
                return "E03";
            case "CS":
            case "COMPUTER SCIENCE":
                return "E04";
            default:
                return "E00";
        }
    }

    private String generateEmployeeID(int departmentID, String departmentName) throws SQLException {
        String prefix = getDepartmentPrefix(departmentName);

        Connection conn = new Connector().connection;
        CallableStatement cs = conn.prepareCall("{call GetNextEmployeeID(?)}");
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

    private boolean isEmployeeIDExists(String empID) throws SQLException {
        Connection conn = new Connector().connection;
        CallableStatement cs = conn.prepareCall("{call GetInstructorByEmpID(?)}");
        cs.setString(1, empID);
        ResultSet rs = cs.executeQuery();
        boolean exists = rs.next();
        rs.close();
        cs.close();
        return exists;
    }

    private int addNewInstructor(int departmentID, String firstName, String lastName, String empID, String email, String phone) throws SQLException {
        Connection conn = new Connector().connection;
        CallableStatement cs = conn.prepareCall("{call AddNewInstructor(?, ?, ?, ?, ?, ?)}");
        cs.setInt(1, departmentID);
        cs.setString(2, firstName);
        cs.setString(3, lastName);
        cs.setString(4, empID);
        cs.setString(5, email);
        cs.setString(6, phone);

        ResultSet rs = cs.executeQuery();

        int instructorID = 0;
        if (rs.next()) {
            instructorID = rs.getInt("InstructorID");
        }

        rs.close();
        cs.close();

        return instructorID;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == add) {
            String firstName = textFirstName.getText();
            String lastName = textLastName.getText();
            String email = textEmail.getText();
            String phone = textPhone.getText();
            String departmentName = (String) departmentBox.getSelectedItem();

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

                if (email.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Email cannot be empty (ensure First Name and Last Name are filled)");
                    return;
                }

                if (departmentName == null) {
                    JOptionPane.showMessageDialog(null, "Please select a Department");
                    return;
                }

                int departmentID = getDepartmentIDByName(departmentName);

                String empID = generateEmployeeID(departmentID, departmentName);

                if (isEmployeeIDExists(empID)) {
                    JOptionPane.showMessageDialog(null, "Employee ID " + empID + " already exists. Please try again.");
                    return;
                }

                empIDText.setText(empID);
                empIDText.setFont(new Font("serif", Font.BOLD, 20));
                empIDText.setForeground(Color.BLACK);

                int instructorID = addNewInstructor(departmentID, firstName, lastName, empID, email, phone);

                if (instructorID <= 0) {
                    JOptionPane.showMessageDialog(null, "Failed to create instructor record");
                    return;
                }


                JOptionPane.showMessageDialog(null,
                        "Instructor Successfully Added!\n" +
                                "Name: " + firstName + " " + lastName + "\n" +
                                "Employee ID: " + empID + "\n" +
                                "Department: " + departmentName + "\n" +
                                "Instructor ID: " + instructorID);

                setVisible(false);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            setVisible(false);
        }
    }

    public static void main(String[] args) {
        new AddInstructor();
    }
}