package studentmanagementsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class ResetPassword extends JFrame implements ActionListener {
    JPasswordField oldPasswordField, newPasswordField, confirmPasswordField;
    JButton submit, cancel;
    String email;

    public ResetPassword(String email) {
        this.email = email;

        setSize(500, 400);
        setLocation(500, 200);
        setLayout(null);
        getContentPane().setBackground(new Color(166, 164, 252));

        JLabel heading = new JLabel("Reset Password");
        heading.setBounds(150, 20, 200, 50);
        heading.setFont(new Font("serif", Font.BOLD, 30));
        add(heading);

        JLabel oldPassword = new JLabel("Old Password");
        oldPassword.setBounds(50, 80, 150, 30);
        oldPassword.setFont(new Font("serif", Font.BOLD, 20));
        add(oldPassword);

        oldPasswordField = new JPasswordField();
        oldPasswordField.setBounds(200, 80, 150, 30);
        add(oldPasswordField);

        JLabel newPassword = new JLabel("New Password");
        newPassword.setBounds(50, 130, 150, 30);
        newPassword.setFont(new Font("serif", Font.BOLD, 20));
        add(newPassword);

        newPasswordField = new JPasswordField();
        newPasswordField.setBounds(200, 130, 150, 30);
        add(newPasswordField);

        JLabel confirmPassword = new JLabel("Confirm Password");
        confirmPassword.setBounds(50, 180, 150, 30);
        confirmPassword.setFont(new Font("serif", Font.BOLD, 20));
        add(confirmPassword);

        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setBounds(200, 180, 150, 30);
        add(confirmPasswordField);

        submit = new JButton("Submit");
        submit.setBounds(100, 240, 100, 30);
        submit.setBackground(Color.BLACK);
        submit.setForeground(Color.WHITE);
        submit.addActionListener(this);
        add(submit);

        cancel = new JButton("Cancel");
        cancel.setBounds(250, 240, 100, 30);
        cancel.setBackground(Color.BLACK);
        cancel.setForeground(Color.WHITE);
        cancel.addActionListener(this);
        add(cancel);

        setVisible(true);
    }

    private boolean verifyPassword(String email, String oldPassword) throws SQLException {
        Connection conn = new Connector().connection;
        CallableStatement cs = conn.prepareCall("{call VerifyUserPassword(?,?)}");
        cs.setString(1, email);
        cs.setString(2, oldPassword);
        ResultSet rs = cs.executeQuery();
        boolean isValid = rs.next();
        rs.close();
        cs.close();
        return isValid;
    }

    private void updatePassword(String email, String newPassword) throws SQLException {
        Connection conn = new Connector().connection;
        CallableStatement cs = conn.prepareCall("{call UpdateUserPassword(?,?)}");
        cs.setString(1, email);
        cs.setString(2, newPassword);
        cs.execute();
        cs.close();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submit) {
            String oldPassword = new String(oldPasswordField.getPassword()).trim();
            String newPassword = new String(newPasswordField.getPassword()).trim();
            String confirmPassword = new String(confirmPasswordField.getPassword()).trim();

            if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please fill all password fields.");
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(null, "New passwords do not match.");
                return;
            }

            if (newPassword.length() < 6) {
                JOptionPane.showMessageDialog(null, "New password must be at least 6 characters long.");
                return;
            }

            try {
                if (!verifyPassword(email, oldPassword)) {
                    JOptionPane.showMessageDialog(null, "Old password is incorrect.");
                    return;
                }

                updatePassword(email, newPassword);

                JOptionPane.showMessageDialog(null, "Password updated successfully.");
                setVisible(false);
                new Login();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error resetting password: " + ex.getMessage());
            }
        } else {
            setVisible(false);
            new Login();
        }
    }
}