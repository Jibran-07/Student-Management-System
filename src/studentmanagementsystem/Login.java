package studentmanagementsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Login extends JFrame implements ActionListener {
    JTextField textFieldName;
    JPasswordField passwordField;
    JButton login, back, resetPassword;

    Login() {
        getContentPane().setBackground(new Color(166, 164, 252));

        JLabel labelName = new JLabel("Email");
        labelName.setBounds(40, 20, 100, 30);
        labelName.setFont(new Font("serif", Font.BOLD, 20));
        add(labelName);

        textFieldName = new JTextField();
        textFieldName.setBounds(150, 20, 150, 30);
        add(textFieldName);

        JLabel labelPass = new JLabel("Password");
        labelPass.setBounds(40, 70, 100, 30);
        labelPass.setFont(new Font("serif", Font.BOLD, 20));
        add(labelPass);

        passwordField = new JPasswordField();
        passwordField.setBounds(150, 70, 150, 30);
        add(passwordField);

        login = new JButton("Login");
        login.setBounds(40, 140, 120, 30);
        login.setBackground(Color.BLACK);
        login.setForeground(Color.WHITE);
        login.addActionListener(this);
        add(login);

        back = new JButton("Back");
        back.setBounds(180, 140, 120, 30);
        back.setBackground(Color.BLACK);
        back.setForeground(Color.WHITE);
        back.addActionListener(this);
        add(back);

        resetPassword = new JButton("Reset Password");
        resetPassword.setBounds(40, 190, 260, 30);
        resetPassword.setBackground(Color.BLACK);
        resetPassword.setForeground(Color.WHITE);
        resetPassword.addActionListener(this);
        add(resetPassword);

        ImageIcon i1 = new ImageIcon(getClass().getResource("/icon/second.jpg"));
        Image i2 = i1.getImage().getScaledInstance(200, 200, Image.SCALE_DEFAULT);
        ImageIcon i3 = new ImageIcon(i2);
        JLabel img = new JLabel(i3);
        img.setBounds(350, 20, 200, 200);
        add(img);

        setSize(600, 300);
        setLocation(500, 250);
        setLayout(null);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == login) {
            String email = textFieldName.getText().trim();
            String password = new String(passwordField.getPassword());

            if (email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter both email and password.");
                return;
            }

            try {
                Connection conn = new Connector().connection;
                CallableStatement cs = conn.prepareCall("{call ValidateAdminLogin(?,?)}");
                cs.setString(1, email);
                cs.setString(2, password);
                ResultSet rs = cs.executeQuery();

                if (rs.next()) {
                    setVisible(false);
                    new AdminDashboard();
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid email or password.");
                }

                rs.close();
                cs.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error during login: " + ex.getMessage());
            }
        } else if (e.getSource() == resetPassword) {
            String email = textFieldName.getText().trim();
            if (email.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter an email to reset the password.");
                return;
            }
            new ResetPassword(email);
        } else {
            setVisible(false);
        }
    }

    public static void main(String[] args) {
        new Login();
    }
}