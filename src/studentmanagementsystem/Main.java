package studentmanagementsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Main extends JFrame {
    Main() {
        setTitle("Student Management System");
        setBounds(100, 100, 800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        ImageIcon i1 = new ImageIcon(getClass().getResource("/icon/first.jpg"));
        Image i2 = i1.getImage().getScaledInstance(800, 500, Image.SCALE_DEFAULT);
        ImageIcon i3 = new ImageIcon(i2);
        JLabel image = new JLabel(i3);
        image.setBounds(0, 0, 800, 500);
        add(image);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(null);
        textPanel.setBounds(0, 20, 800, 100);
        textPanel.setBackground(new Color(0, 0, 0, 80));
        image.add(textPanel);

        JLabel welcomeLabel = new JLabel("WELCOME TO");
        welcomeLabel.setBounds(250, 10, 300, 30);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 28));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        textPanel.add(welcomeLabel);

        JLabel heading = new JLabel("STUDENT MANAGEMENT SYSTEM");
        heading.setBounds(50, 45, 700, 40);
        heading.setFont(new Font("Arial", Font.BOLD, 36));
        heading.setForeground(Color.WHITE);
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        textPanel.add(heading);

        JButton continueButton = new JButton("CONTINUE");
        continueButton.setBounds(300, 300, 200, 50);
        continueButton.setFont(new Font("Arial", Font.BOLD, 20));
        continueButton.setBackground(new Color(30, 144, 254));
        continueButton.setForeground(Color.WHITE);
        continueButton.setFocusPainted(false);
        continueButton.setBorderPainted(false);
        
        continueButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                continueButton.setBackground(new Color(0, 119, 238));
                continueButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            public void mouseExited(MouseEvent e) {
                continueButton.setBackground(new Color(30, 144, 254));
            }
        });

        continueButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                dispose();
                new Login();
            }
        });
        image.add(continueButton);

        setVisible(true);
    }

    public static void main(String[] args) {
        new Main();
    }
}