package studentmanagementsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AddDepartment extends JFrame implements ActionListener {
    JTextField textDepartmentName;
    JTextField textDepartmentHead;
    JButton submit, cancel;
    JList<String> suggestionList;
    JScrollPane suggestionScrollPane;
    DefaultListModel<String> listModel;
    List<InstructorInfo> instructors;

    private static class InstructorInfo {
        String empId;
        int instructorId;
        String fullName;

        InstructorInfo(String empId, int instructorId, String firstName, String lastName) {
            this.empId = empId;
            this.instructorId = instructorId;
            this.fullName = firstName + " " + lastName;
        }

        @Override
        public String toString() {
            return empId + " - " + fullName;
        }
    }

    AddDepartment() {
        getContentPane().setBackground(new Color(128, 176, 255));
        setLayout(null);

        JLabel heading = new JLabel("Add New Department");
        heading.setBounds(280, 30, 400, 40);
        heading.setFont(new Font("serif", Font.BOLD, 30));
        add(heading);

        JLabel departmentNameLabel = new JLabel("Department Name:");
        departmentNameLabel.setBounds(100, 150, 200, 30);
        departmentNameLabel.setFont(new Font("serif", Font.BOLD, 20));
        add(departmentNameLabel);

        textDepartmentName = new JTextField();
        textDepartmentName.setBounds(300, 150, 200, 30);
        add(textDepartmentName);

        JLabel departmentHeadLabel = new JLabel("Department Head:");
        departmentHeadLabel.setBounds(100, 200, 250, 30);
        departmentHeadLabel.setFont(new Font("serif", Font.BOLD, 20));
        add(departmentHeadLabel);

        textDepartmentHead = new JTextField();
        textDepartmentHead.setBounds(300, 200, 200, 30);
        textDepartmentHead.setToolTipText("Type EMP ID to search for instructors");
        add(textDepartmentHead);

        listModel = new DefaultListModel<>();
        suggestionList = new JList<>(listModel);
        suggestionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        suggestionList.setVisibleRowCount(5);
        suggestionList.setFixedCellHeight(25);
        suggestionList.setBackground(Color.WHITE);
        suggestionList.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        suggestionScrollPane = new JScrollPane(suggestionList);
        suggestionScrollPane.setBounds(300, 230, 200, 100);
        suggestionScrollPane.setVisible(false);
        suggestionScrollPane.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        add(suggestionScrollPane);

        loadInstructors();

        textDepartmentHead.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String searchText = textDepartmentHead.getText().trim().toLowerCase();
                if (searchText.isEmpty()) {
                    suggestionScrollPane.setVisible(false);
                } else {
                    filterInstructors(searchText);
                }
                revalidate();
                repaint();
            }
        });

        suggestionList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                String selectedValue = suggestionList.getSelectedValue();
                if (selectedValue != null) {
                    String empId = selectedValue.split(" - ")[0];
                    textDepartmentHead.setText(empId);
                    suggestionScrollPane.setVisible(false);
                    revalidate();
                    repaint();
                }
            }
        });

        submit = new JButton("Add");
        submit.setBounds(200, 380, 120, 40);
        submit.setBackground(Color.BLACK);
        submit.setForeground(Color.WHITE);
        submit.addActionListener(this);
        add(submit);

        cancel = new JButton("Cancel");
        cancel.setBounds(350, 380, 120, 40);
        cancel.setBackground(Color.BLACK);
        cancel.setForeground(Color.WHITE);
        cancel.addActionListener(this);
        add(cancel);

        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void loadInstructors() {
        instructors = new ArrayList<>();
        Connection conn = null;
        CallableStatement cs = null;
        ResultSet rs = null;

        try {
            conn = new Connector().connection;

            cs = conn.prepareCall("{call GetInstructorIDs}");
            rs = cs.executeQuery();

            List<String> empIds = new ArrayList<>();
            while (rs.next()) {
                empIds.add(rs.getString("EmpID"));
            }

            rs.close();
            cs.close();

            for (String empId : empIds) {
                cs = conn.prepareCall("{call GetInstructorByEmpID(?)}");
                cs.setString(1, empId);
                rs = cs.executeQuery();

                if (rs.next()) {
                    instructors.add(new InstructorInfo(
                            empId,
                            rs.getInt("InstructorID"),
                            rs.getString("FirstName"),
                            rs.getString("LastName")
                    ));
                }

                rs.close();
                cs.close();
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading instructors: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (cs != null) cs.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void filterInstructors(String searchText) {
        listModel.clear();
        boolean hasMatches = false;

        for (InstructorInfo instructor : instructors) {
            if (instructor.empId.toLowerCase().contains(searchText) ||
                    instructor.fullName.toLowerCase().contains(searchText)) {
                listModel.addElement(instructor.toString());
                hasMatches = true;
            }
        }

        if (hasMatches && listModel.size() > 0) {
            suggestionScrollPane.setVisible(true);
            int visibleRows = Math.min(5, listModel.size());
            suggestionList.setVisibleRowCount(visibleRows);

            int rowHeight = suggestionList.getFixedCellHeight();
            if (rowHeight <= 0) rowHeight = 20;
            int scrollPaneHeight = (visibleRows * rowHeight) + 5;

            suggestionScrollPane.setBounds(300, 230, 200, scrollPaneHeight);
        } else {
            suggestionScrollPane.setVisible(false);
        }

        revalidate();
        repaint();
    }

    private int getInstructorIdByEmpId(String empId) {
        Connection conn = null;
        CallableStatement cs = null;
        ResultSet rs = null;

        try {
            conn = new Connector().connection;
            cs = conn.prepareCall("{call GetInstructorByEmpID(?)}");
            cs.setString(1, empId);
            rs = cs.executeQuery();

            if (rs.next()) {
                return rs.getInt("InstructorID");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error getting instructor ID: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (cs != null) cs.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return -1; // Not found
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submit) {
            String departmentName = textDepartmentName.getText().trim();
            String empId = textDepartmentHead.getText().trim();

            if (departmentName.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Department Name cannot be empty.");
                return;
            }
            if (empId.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Department Head EMP ID cannot be empty.");
                return;
            }

            try {
                int departmentHeadID = getInstructorIdByEmpId(empId);
                if (departmentHeadID == -1) {
                    JOptionPane.showMessageDialog(null, "Invalid EMP ID: Instructor not found.");
                    return;
                }

                int newDepartmentID = addNewDepartment(departmentName, departmentHeadID);
                JOptionPane.showMessageDialog(null,
                        "Department Added Successfully! Department ID: " + newDepartmentID);
                setVisible(false);
            } catch (SQLException ex) {
                String errorMessage = ex.getMessage();
                if (errorMessage.contains("FK_Department_Head")) {
                    errorMessage = "Invalid Department Head ID: Instructor does not exist.";
                }
                JOptionPane.showMessageDialog(null, "Error: " + errorMessage);
            }
        } else {
            setVisible(false);
        }
    }

    private int addNewDepartment(String departmentName, int departmentHeadID) throws SQLException {
        Connection conn = null;
        CallableStatement cs = null;
        ResultSet rs = null;
        int newDepartmentID = -1;

        try {
            conn = new Connector().connection;

            cs = conn.prepareCall("{call AddNewDepartment(?, ?)}");
            cs.setString(1, departmentName);
            cs.setInt(2, departmentHeadID);
            rs = cs.executeQuery();

            if (rs.next()) {
                newDepartmentID = rs.getInt("DepartmentID");
            }

            return newDepartmentID;
        } finally {
            if (rs != null) rs.close();
            if (cs != null) cs.close();
            if (conn != null) conn.close();
        }
    }

    public static void main(String[] args) {
        new AddDepartment();
    }
}