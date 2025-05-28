package studentmanagementsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.*;

public class UpdateStudent extends JFrame implements ActionListener {
    JTextField textAddress, textPhone, textEmail, textDepartment;
    JLabel rollNoText, textFirstName, textLastName;
    Choice cRollNo;
    JComboBox batchBox;
    JButton submit, cancel;

    UpdateStudent() {
        getContentPane().setBackground(new Color(230, 210, 252));

        JLabel heading = new JLabel("Update Student Details");
        heading.setBounds(50, 10, 500, 50);
        heading.setFont(new Font("serif", Font.BOLD, 35));
        add(heading);

        JLabel rollNo = new JLabel("Select Roll Number");
        rollNo.setBounds(50, 100, 200, 20);
        rollNo.setFont(new Font("serif", Font.PLAIN, 20));
        add(rollNo);

        cRollNo = new Choice();
        cRollNo.setBounds(250, 100, 200, 20);
        try {
            loadRollNumbers();
        } catch (Exception e) {
            e.printStackTrace();
        }
        add(cRollNo);

        JLabel firstName = new JLabel("First Name");
        firstName.setBounds(50, 150, 100, 30);
        firstName.setFont(new Font("serif", Font.BOLD, 20));
        add(firstName);

        textFirstName = new JLabel();
        textFirstName.setBounds(200, 150, 150, 30);
        add(textFirstName);

        JLabel lastName = new JLabel("Last Name");
        lastName.setBounds(400, 150, 200, 30);
        lastName.setFont(new Font("serif", Font.BOLD, 20));
        add(lastName);

        textLastName = new JLabel();
        textLastName.setBounds(600, 150, 150, 30);
        add(textLastName);

        JLabel rollNoLabel = new JLabel("Roll Number");
        rollNoLabel.setBounds(50, 200, 200, 30);
        rollNoLabel.setFont(new Font("serif", Font.BOLD, 20));
        add(rollNoLabel);

        rollNoText = new JLabel();
        rollNoText.setBounds(200, 200, 150, 30);
        rollNoText.setFont(new Font("serif", Font.BOLD, 20));
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
        add(textEmail);

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
                    String departmentName = getDepartmentByBatch(selectedBatch);
                    if (departmentName != null && !departmentName.isEmpty()) {
                        textDepartment.setText(departmentName);
                    } else {
                        textDepartment.setText("");
                        JOptionPane.showMessageDialog(null, "Unable to determine department for batch: " + selectedBatch);
                    }
                } else {
                    textDepartment.setText("");
                }
            }
        });
        add(batchBox);

        JLabel department = new JLabel("Department");
        department.setBounds(400, 350, 200, 30);
        department.setFont(new Font("serif", Font.BOLD, 20));
        add(department);

        textDepartment = new JTextField();
        textDepartment.setBounds(600, 350, 150, 30);
        textDepartment.setEditable(false);
        textDepartment.setBackground(Color.WHITE);
        add(textDepartment);

        try {
            loadStudentData(cRollNo.getSelectedItem());
        } catch (Exception e) {
            e.printStackTrace();
        }

        cRollNo.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                try {
                    loadStudentData(cRollNo.getSelectedItem());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        submit = new JButton("Update");
        submit.setBounds(250, 450, 120, 30);
        submit.setBackground(Color.BLACK);
        submit.setForeground(Color.WHITE);
        submit.addActionListener(this);
        add(submit);

        cancel = new JButton("Cancel");
        cancel.setBounds(450, 450, 120, 30);
        cancel.setBackground(Color.BLACK);
        cancel.setForeground(Color.WHITE);
        cancel.addActionListener(this);
        add(cancel);

        setSize(900, 550);
        setLocation(350, 50);
        setLayout(null);
        setVisible(true);
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

    private void loadRollNumbers() throws SQLException {
        Connection conn = new Connector().connection;
        Statement stmt = conn.createStatement();
        CallableStatement cs = conn.prepareCall("{call GetAllStudentswithEmail}");
        ResultSet rs = cs.executeQuery();
        while (rs.next()) {
            cRollNo.add(rs.getString("RollNo"));
        }
        rs.close();
        stmt.close();
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

    private void loadStudentData(String rollNo) throws SQLException {
        Connection conn = new Connector().connection;
        CallableStatement cs = conn.prepareCall("{call GetStudentByRollNo(?)}");
        cs.setString(1, rollNo);
        ResultSet rs = cs.executeQuery();

        if (rs.next()) {
            textFirstName.setText(rs.getString("FirstName"));
            textLastName.setText(rs.getString("LastName"));
            rollNoText.setText(rs.getString("RollNo"));
            textAddress.setText(rs.getString("Address"));
            textPhone.setText(rs.getString("Phone"));
            textEmail.setText(rs.getString("Email"));
            batchBox.setSelectedItem(rs.getString("BatchName"));
            textDepartment.setText(rs.getString("DepartmentName"));
        }

        rs.close();
        cs.close();
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
            insertCs.setString(2, "2023-09-01");
            insertCs.setString(3, "2023-12-31");
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

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submit) {
            String rollNo = rollNoText.getText();
            String address = textAddress.getText();
            String phone = textPhone.getText();
            String email = textEmail.getText();
            String batchName = (String) batchBox.getSelectedItem();
            String departmentName = textDepartment.getText();

            try {
                Connection conn = new Connector().connection;

                int batchID = getBatchIDByName(batchName);
                int departmentID = getDepartmentIDByName(departmentName);

                CallableStatement updateStudent = conn.prepareCall("{call UpdateStudentDetails(?, ?, ?, ?, ?, ?)}");
                updateStudent.setString(1, rollNo);
                updateStudent.setString(2, address);
                updateStudent.setString(3, phone);
                updateStudent.setString(4, email);
                updateStudent.setInt(5, batchID);
                updateStudent.setInt(6, departmentID);
                updateStudent.executeUpdate();
                updateStudent.close();

                String semesterName = batchName.substring(batchName.indexOf("_") + 1);
                int semesterID = getSemesterIDByName(semesterName);

                int studentID = getStudentIDByRollNo(rollNo);

                CallableStatement updateEnrollment = conn.prepareCall("{call UpdateStudentEnrollment(?, ?)}");
                updateEnrollment.setInt(1, studentID);
                updateEnrollment.setInt(2, semesterID);
                updateEnrollment.executeUpdate();
                updateEnrollment.close();

                JOptionPane.showMessageDialog(null, "Student Details Updated");
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
        new UpdateStudent();
    }
}