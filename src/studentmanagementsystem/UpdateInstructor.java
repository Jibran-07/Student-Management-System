package studentmanagementsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.*;

public class UpdateInstructor extends JFrame implements ActionListener {
    JTextField textEmail, textPhone;
    JLabel empIDText, textFirstName, textLastName;
    Choice cEmpID;
    JComboBox departmentBox;
    JButton submit, cancel;

    UpdateInstructor() {
        getContentPane().setBackground(new Color(230, 210, 252));

        JLabel heading = new JLabel("Update Instructor Details");
        heading.setBounds(50, 10, 500, 50);
        heading.setFont(new Font("serif", Font.BOLD, 35));
        add(heading);

        JLabel empID = new JLabel("Select Employee ID");
        empID.setBounds(50, 100, 200, 20);
        empID.setFont(new Font("serif", Font.PLAIN, 20));
        add(empID);

        cEmpID = new Choice();
        cEmpID.setBounds(250, 100, 200, 20);
        try {
            loadInstructorIDs();
        } catch (Exception e) {
            e.printStackTrace();
        }
        add(cEmpID);

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

        JLabel empIDLabel = new JLabel("Employee ID");
        empIDLabel.setBounds(50, 200, 200, 30);
        empIDLabel.setFont(new Font("serif", Font.BOLD, 20));
        add(empIDLabel);

        empIDText = new JLabel();
        empIDText.setBounds(200, 200, 150, 30);
        empIDText.setFont(new Font("serif", Font.BOLD, 20));
        add(empIDText);

        JLabel email = new JLabel("Email");
        email.setBounds(50, 250, 200, 30);
        email.setFont(new Font("serif", Font.BOLD, 20));
        add(email);

        textEmail = new JTextField();
        textEmail.setBounds(200, 250, 150, 30);
        add(textEmail);

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

        try {
            if (cEmpID.getItemCount() > 0) {
                loadInstructorData(cEmpID.getSelectedItem());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        cEmpID.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                try {
                    loadInstructorData(cEmpID.getSelectedItem());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        submit = new JButton("Update");
        submit.setBounds(250, 400, 120, 30);
        submit.setBackground(Color.BLACK);
        submit.setForeground(Color.WHITE);
        submit.addActionListener(this);
        add(submit);

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

    private void loadInstructorIDs() throws SQLException {
        Connection conn = new Connector().connection;
        CallableStatement cs = conn.prepareCall("{call GetInstructorIDs}");
        ResultSet rs = cs.executeQuery();
        while (rs.next()) {
            cEmpID.add(rs.getString("EmpID"));
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

    private void loadInstructorData(String empID) throws SQLException {
        Connection conn = new Connector().connection;
        CallableStatement cs = conn.prepareCall("{call GetInstructorByEmpID(?)}");
        cs.setString(1, empID);
        ResultSet rs = cs.executeQuery();

        if (rs.next()) {
            textFirstName.setText(rs.getString("FirstName"));
            textLastName.setText(rs.getString("LastName"));
            empIDText.setText(rs.getString("EmpID"));
            textEmail.setText(rs.getString("Email"));
            textPhone.setText(rs.getString("Phone"));
            departmentBox.setSelectedItem(rs.getString("DepartmentName"));
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

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submit) {
            String empID = empIDText.getText();
            String email = textEmail.getText();
            String phone = textPhone.getText();
            String departmentName = (String) departmentBox.getSelectedItem();

            try {

                int departmentID = getDepartmentIDByName(departmentName);

                Connection conn = new Connector().connection;
                CallableStatement updateCs = conn.prepareCall("{call UpdateInstructorDetails(?, ?, ?, ?)}");
                updateCs.setString(1, empID);
                updateCs.setString(2, email);
                updateCs.setString(3, phone);
                updateCs.setInt(4, departmentID);
                updateCs.executeUpdate();
                updateCs.close();

                JOptionPane.showMessageDialog(null, "Instructor Details Updated");
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
        new UpdateInstructor();
    }
}