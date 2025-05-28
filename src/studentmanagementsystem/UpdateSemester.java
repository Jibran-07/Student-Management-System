package studentmanagementsystem;

import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UpdateSemester extends JFrame implements ActionListener {
    Choice cSemesterID;
    JTextField textSemesterName;
    JDateChooser startDate, endDate;
    JButton submit, cancel;
    SimpleDateFormat dateFormat;

    public UpdateSemester() {
        setTitle("Update Semester");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(new Color(210, 232, 252));
        setLayout(null);
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        JLabel heading = new JLabel("Update Semester Details");
        heading.setBounds(40, 30, 300, 30);
        heading.setFont(new Font("Tahoma", Font.BOLD, 20));
        add(heading);

        JLabel semesterID = new JLabel("Select Semester ID");
        semesterID.setBounds(60, 80, 200, 20);
        semesterID.setFont(new Font("Tahoma", Font.PLAIN, 16));
        add(semesterID);

        cSemesterID = new Choice();
        cSemesterID.setBounds(60, 105, 200, 20);
        add(cSemesterID);

        JLabel semesterName = new JLabel("Semester Name");
        semesterName.setBounds(60, 140, 200, 20);
        semesterName.setFont(new Font("Tahoma", Font.PLAIN, 16));
        add(semesterName);

        textSemesterName = new JTextField();
        textSemesterName.setBounds(60, 165, 200, 25);
        add(textSemesterName);

        JLabel lblStartDate = new JLabel("Start Date");
        lblStartDate.setBounds(60, 200, 200, 20);
        lblStartDate.setFont(new Font("Tahoma", Font.PLAIN, 16));
        add(lblStartDate);

        startDate = new JDateChooser();
        startDate.setBounds(60, 225, 200, 25);
        add(startDate);

        JLabel lblEndDate = new JLabel("End Date");
        lblEndDate.setBounds(60, 260, 200, 20);
        lblEndDate.setFont(new Font("Tahoma", Font.PLAIN, 16));
        add(lblEndDate);

        endDate = new JDateChooser();
        endDate.setBounds(60, 285, 200, 25);
        add(endDate);

        submit = new JButton("Update");
        submit.setBounds(60, 340, 100, 30);
        submit.setBackground(Color.BLACK);
        submit.setForeground(Color.WHITE);
        submit.addActionListener(this);
        add(submit);

        cancel = new JButton("Cancel");
        cancel.setBounds(180, 340, 100, 30);
        cancel.setBackground(Color.BLACK);
        cancel.setForeground(Color.WHITE);
        cancel.addActionListener(this);
        add(cancel);

        cSemesterID.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    loadSemesterData(cSemesterID.getSelectedItem());
                }
            }
        });


        loadSemesterIDs();

        setSize(370, 450);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void loadSemesterIDs() {
        try {
            Connection conn = new Connector().connection;
            CallableStatement cs = conn.prepareCall("{call GetAllSemesterIDs}");
            ResultSet rs = cs.executeQuery();

            cSemesterID.removeAll();

            while (rs.next()) {
                cSemesterID.add(rs.getString("SemesterID"));
            }

            if (cSemesterID.getItemCount() > 0) {
                cSemesterID.select(0);
                loadSemesterData(cSemesterID.getSelectedItem());
            }

            rs.close();
            cs.close();
        } catch (Exception e) {
            showError("Error loading Semester IDs: " + e.getMessage());
        }
    }

    private void loadSemesterData(String semesterID) {
        if (semesterID == null || semesterID.trim().isEmpty()) {
            textSemesterName.setText("");
            startDate.setDate(null);
            endDate.setDate(null);
            return;
        }

        try {
            Connection conn = new Connector().connection;
            CallableStatement cs = conn.prepareCall("{call GetSemesterByID(?)}");
            cs.setString(1, semesterID);
            ResultSet rs = cs.executeQuery();

            if (rs.next()) {
                textSemesterName.setText(rs.getString("SemesterName"));

                String start = rs.getString("StartDate");
                String end = rs.getString("EndDate");

                startDate.setDate(start != null ? dateFormat.parse(start) : null);
                endDate.setDate(end != null ? dateFormat.parse(end) : null);
            } else {
                textSemesterName.setText("");
                startDate.setDate(null);
                endDate.setDate(null);
            }

            rs.close();
            cs.close();
        } catch (Exception e) {
            showError("Error loading semester data: " + e.getMessage());
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submit) {
            String semesterID = cSemesterID.getSelectedItem();
            String semesterName = textSemesterName.getText();
            Date start = startDate.getDate();
            Date end = endDate.getDate();

            if (semesterName.isEmpty() || start == null || end == null) {
                showError("Please fill all fields.");
                return;
            }

            if (start.after(end)) {
                showError("End Date must be after Start Date.");
                return;
            }

            try {
                Connection conn = new Connector().connection;
                CallableStatement cs = conn.prepareCall("{call UpdateSemesterDetails(?, ?, ?, ?)}");
                cs.setString(1, semesterID);
                cs.setString(2, semesterName);
                cs.setString(3, dateFormat.format(start));
                cs.setString(4, dateFormat.format(end));

                cs.execute();
                cs.close();

                JOptionPane.showMessageDialog(this, "Semester Details Updated Successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } catch (Exception ex) {
                showError("Error updating semester: " + ex.getMessage());
            }
        } else if (e.getSource() == cancel) {
            dispose();
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UpdateSemester());
    }
}
