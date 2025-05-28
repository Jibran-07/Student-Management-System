package studentmanagementsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class AddBatch extends JFrame implements ActionListener {
    JTextField textBatchName;
    JButton submit, cancel;

    public AddBatch() {

        getContentPane().setBackground(new Color(210, 232, 252));
        setLayout(null);

        JLabel heading = new JLabel("Add New Batch");
        heading.setBounds(40, 30, 300, 30);
        heading.setFont(new Font("Tahoma", Font.BOLD, 20));
        add(heading);

        JLabel lblBatchName = new JLabel("Batch Name");
        lblBatchName.setBounds(60, 80, 120, 30);
        lblBatchName.setFont(new Font("Tahoma", Font.PLAIN, 16));
        add(lblBatchName);

        textBatchName = new JTextField();
        textBatchName.setBounds(60, 120, 280, 30);
        add(textBatchName);

        submit = new JButton("Add");
        submit.setBounds(60, 180, 120, 30);
        submit.setBackground(Color.BLACK);
        submit.setForeground(Color.WHITE);
        submit.addActionListener(this);
        add(submit);

        cancel = new JButton("Cancel");
        cancel.setBounds(220, 180, 120, 30);
        cancel.setBackground(Color.BLACK);
        cancel.setForeground(Color.WHITE);
        cancel.addActionListener(this);
        add(cancel);

        setSize(400, 280);
        setLocation(550, 250);
        setVisible(true);
        setTitle("Add New Batch");
    }

    private boolean checkBatchExists(String batchName) {
        boolean exists = false;
        Connection conn = null;
        CallableStatement cs = null;
        ResultSet rs = null;

        try {
            conn = new Connector().connection;
            if (conn == null) {
                JOptionPane.showMessageDialog(null, "Database connection failed");
                return false;
            }

            cs = conn.prepareCall("{call CheckBatchExists(?)}");
            cs.setString(1, batchName);

            rs = cs.executeQuery();

            if (rs.next()) {
                exists = (rs.getInt("Count") > 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error checking batch existence: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (cs != null) cs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return exists;
    }

    private void addBatch(String batchName) {
        Connection conn = null;
        CallableStatement cs = null;
        ResultSet rs = null;

        try {
            conn = new Connector().connection;
            if (conn == null) {
                JOptionPane.showMessageDialog(null, "Database connection failed");
                return;
            }

            cs = conn.prepareCall("{call AddBatch(?)}");
            cs.setString(1, batchName);

            rs = cs.executeQuery();

            if (rs.next()) {
                int batchID = rs.getInt("BatchID");
                JOptionPane.showMessageDialog(null, "Batch added successfully! Batch ID: " + batchID);
                setVisible(false);
            } else {
                JOptionPane.showMessageDialog(null, "Failed to retrieve Batch ID.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error adding batch: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (cs != null) cs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submit) {
            String batchName = textBatchName.getText();

            if (batchName == null || batchName.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter Batch Name");
                return;
            }

            if (checkBatchExists(batchName)) {
                JOptionPane.showMessageDialog(null, "A batch with this name already exists!");
                return;
            }

            addBatch(batchName);
        } else if (e.getSource() == cancel) {
            setVisible(false);
        }
    }

    public static void main(String[] args) {
        new AddBatch();
    }
}