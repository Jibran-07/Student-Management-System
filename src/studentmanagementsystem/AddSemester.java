package studentmanagementsystem;

import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddSemester extends JFrame implements ActionListener {
    JTextField textSemesterName;
    JDateChooser startDate, endDate;
    JButton submit, cancel;
    SimpleDateFormat dateFormat;

    AddSemester() {
        getContentPane().setBackground(new Color(166, 164, 252));
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        setLayout(null);

        JLabel heading = new JLabel("Add New Semester");
        heading.setBounds(100, 30, 300, 50);
        heading.setFont(new Font("serif", Font.BOLD, 30));
        add(heading);

        JLabel semesterName = new JLabel("Semester Name");
        semesterName.setBounds(60, 100, 200, 30);
        semesterName.setFont(new Font("serif", Font.BOLD, 20));
        add(semesterName);

        textSemesterName = new JTextField();
        textSemesterName.setBounds(60, 130, 200, 30);
        add(textSemesterName);

        JLabel lblStartDate = new JLabel("Start Date");
        lblStartDate.setBounds(60, 170, 200, 30);
        lblStartDate.setFont(new Font("serif", Font.BOLD, 20));
        add(lblStartDate);

        startDate = new JDateChooser();
        startDate.setBounds(60, 200, 200, 30);
        add(startDate);

        JLabel lblEndDate = new JLabel("End Date");
        lblEndDate.setBounds(60, 240, 200, 30);
        lblEndDate.setFont(new Font("serif", Font.BOLD, 20));
        add(lblEndDate);

        endDate = new JDateChooser();
        endDate.setBounds(60, 270, 200, 30);
        add(endDate);

        submit = new JButton("Add");
        submit.setBounds(60, 320, 100, 30);
        submit.setBackground(Color.BLACK);
        submit.setForeground(Color.WHITE);
        submit.addActionListener(this);
        add(submit);

        cancel = new JButton("Cancel");
        cancel.setBounds(200, 320, 100, 30);
        cancel.setBackground(Color.BLACK);
        cancel.setForeground(Color.WHITE);
        cancel.addActionListener(this);
        add(cancel);

        setSize(400, 420);
        setLocation(550, 100);
        setVisible(true);
    }

    private boolean checkSemesterNameExists(String semesterName) throws SQLException {
        Connection conn = new Connector().connection;
        CallableStatement cs = conn.prepareCall("{call CheckSemesterNameExists(?)}");
        cs.setString(1, semesterName);
        ResultSet rs = cs.executeQuery();
        boolean exists = false;
        if (rs.next()) {
            exists = rs.getInt("Count") > 0;
        }
        rs.close();
        cs.close();
        return exists;
    }

    private int addSemester(String semesterName, String startDateStr, String endDateStr) throws SQLException {
        Connection conn = new Connector().connection;
        CallableStatement cs = conn.prepareCall("{call AddSemester(?,?,?)}");
        cs.setString(1, semesterName);
        cs.setString(2, startDateStr);
        cs.setString(3, endDateStr);

        ResultSet rs = cs.executeQuery();
        int newSemesterID = -1;
        if (rs.next()) {
            newSemesterID = rs.getInt("SemesterID");
        }
        rs.close();
        cs.close();
        return newSemesterID;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submit) {
            String semesterName = textSemesterName.getText().trim();
            Date startDateValue = startDate.getDate();
            Date endDateValue = endDate.getDate();

            if (semesterName.isEmpty() || startDateValue == null || endDateValue == null) {
                JOptionPane.showMessageDialog(null, "Please fill all fields.");
                return;
            }

            if (startDateValue.after(endDateValue)) {
                JOptionPane.showMessageDialog(null, "End Date must be after Start Date.");
                return;
            }

            try {
                if (checkSemesterNameExists(semesterName)) {
                    JOptionPane.showMessageDialog(null, "A semester with this name already exists.");
                    return;
                }

                String startDateStr = dateFormat.format(startDateValue);
                String endDateStr = dateFormat.format(endDateValue);

                int newSemesterID = addSemester(semesterName, startDateStr, endDateStr);

                if (newSemesterID > 0) {
                    JOptionPane.showMessageDialog(null, "Semester Added Successfully. Semester ID: " + newSemesterID);
                } else {
                    JOptionPane.showMessageDialog(null, "Error adding semester. Please try again.");
                    return;
                }

                setVisible(false);
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error adding semester: " + ex.getMessage());
            }
        } else {
            setVisible(false);
        }
    }

    public static void main(String[] args) {
        new AddSemester();
    }
}