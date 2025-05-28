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

public class UpdateDepartment extends JFrame implements ActionListener {
    JTextField textDepartmentID, textDepartmentName, textDepartmentHead;
    JButton submit, cancel;

    JList<String> departmentSuggestionList;
    JScrollPane departmentSuggestionScrollPane;
    DefaultListModel<String> departmentListModel;
    List<DepartmentInfo> departments;

    JList<String> instructorSuggestionList;
    JScrollPane instructorSuggestionScrollPane;
    DefaultListModel<String> instructorListModel;
    List<InstructorInfo> instructors;

    private static class DepartmentInfo {
        int departmentId;
        String departmentName;
        String departmentHead;

        DepartmentInfo(int departmentId, String departmentName, String departmentHead) {
            this.departmentId = departmentId;
            this.departmentName = departmentName;
            this.departmentHead = departmentHead;
        }

        @Override
        public String toString() {
            return departmentId + " - " + departmentName;
        }
    }

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

    UpdateDepartment() {
        getContentPane().setBackground(new Color(128, 176, 255));
        setLayout(null);

        JLabel heading = new JLabel("Update Department");
        heading.setBounds(310, 30, 500, 50);
        heading.setFont(new Font("serif", Font.BOLD, 30));
        add(heading);

        JLabel departmentIDLabel = new JLabel("Department ID");
        departmentIDLabel.setBounds(50, 150, 200, 30);
        departmentIDLabel.setFont(new Font("serif", Font.BOLD, 20));
        add(departmentIDLabel);

        textDepartmentID = new JTextField();
        textDepartmentID.setBounds(250, 150, 200, 30);
        textDepartmentID.setToolTipText("Type Department ID or Name to search");
        add(textDepartmentID);

        departmentListModel = new DefaultListModel<>();
        departmentSuggestionList = new JList<>(departmentListModel);
        departmentSuggestionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        departmentSuggestionList.setVisibleRowCount(5);
        departmentSuggestionList.setFixedCellHeight(25);
        departmentSuggestionList.setBackground(Color.WHITE);
        departmentSuggestionList.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        departmentSuggestionScrollPane = new JScrollPane(departmentSuggestionList);
        departmentSuggestionScrollPane.setBounds(250, 180, 200, 100);
        departmentSuggestionScrollPane.setVisible(false);
        departmentSuggestionScrollPane.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        add(departmentSuggestionScrollPane);

        JLabel departmentNameLabel = new JLabel("Department Name");
        departmentNameLabel.setBounds(50, 250, 200, 30);
        departmentNameLabel.setFont(new Font("serif", Font.BOLD, 20));
        add(departmentNameLabel);

        textDepartmentName = new JTextField();
        textDepartmentName.setBounds(250, 250, 200, 30);
        add(textDepartmentName);

        JLabel departmentHeadLabel = new JLabel("Department Head");
        departmentHeadLabel.setBounds(50, 350, 200, 30);
        departmentHeadLabel.setFont(new Font("serif", Font.BOLD, 20));
        add(departmentHeadLabel);

        textDepartmentHead = new JTextField();
        textDepartmentHead.setBounds(250, 350, 200, 30);
        textDepartmentHead.setToolTipText("Type EMP ID to search for instructors");
        add(textDepartmentHead);

        instructorListModel = new DefaultListModel<>();
        instructorSuggestionList = new JList<>(instructorListModel);
        instructorSuggestionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        instructorSuggestionList.setVisibleRowCount(5);
        instructorSuggestionList.setFixedCellHeight(25);
        instructorSuggestionList.setBackground(Color.WHITE);
        instructorSuggestionList.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        instructorSuggestionScrollPane = new JScrollPane(instructorSuggestionList);
        instructorSuggestionScrollPane.setBounds(250, 380, 200, 100);
        instructorSuggestionScrollPane.setVisible(false);
        instructorSuggestionScrollPane.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        add(instructorSuggestionScrollPane);

        loadDepartments();
        loadInstructors();

        textDepartmentID.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String searchText = textDepartmentID.getText().trim().toLowerCase();
                if (searchText.isEmpty()) {
                    departmentSuggestionScrollPane.setVisible(false);
                } else {
                    filterDepartments(searchText);
                }
                revalidate();
                repaint();
            }
        });

        departmentSuggestionList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                String selectedValue = departmentSuggestionList.getSelectedValue();
                if (selectedValue != null) {
                    String[] parts = selectedValue.split(" - ");
                    String departmentId = parts[0];
                    String departmentName = parts[1];

                    textDepartmentID.setText(departmentId);
                    textDepartmentName.setText(departmentName);

                    for (DepartmentInfo dept : departments) {
                        if (dept.departmentId == Integer.parseInt(departmentId)) {
                            textDepartmentHead.setText(dept.departmentHead);
                            break;
                        }
                    }

                    departmentSuggestionScrollPane.setVisible(false);
                    revalidate();
                    repaint();
                }
            }
        });

        textDepartmentHead.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String searchText = textDepartmentHead.getText().trim().toLowerCase();
                if (searchText.isEmpty()) {
                    instructorSuggestionScrollPane.setVisible(false);
                } else {
                    filterInstructors(searchText);
                }
                revalidate();
                repaint();
            }
        });

        instructorSuggestionList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                String selectedValue = instructorSuggestionList.getSelectedValue();
                if (selectedValue != null) {
                    String empId = selectedValue.split(" - ")[0];
                    textDepartmentHead.setText(empId);
                    instructorSuggestionScrollPane.setVisible(false);
                    revalidate();
                    repaint();
                }
            }
        });

        submit = new JButton("Update");
        submit.setBounds(250, 520, 120, 30);
        submit.setBackground(Color.BLACK);
        submit.setForeground(Color.WHITE);
        submit.addActionListener(this);
        add(submit);

        cancel = new JButton("Cancel");
        cancel.setBounds(450, 520, 120, 30);
        cancel.setBackground(Color.BLACK);
        cancel.setForeground(Color.WHITE);
        cancel.addActionListener(this);
        add(cancel);

        setSize(900, 650);
        setLocation(350, 50);
        setVisible(true);
    }

    private void loadDepartments() {
        departments = new ArrayList<>();
        Connection conn = null;
        CallableStatement cs = null;
        ResultSet rs = null;

        try {
            conn = new Connector().connection;

            cs = conn.prepareCall("{call GetAllDepartments}");
            rs = cs.executeQuery();

            while (rs.next()) {
                int deptId = rs.getInt("DepartmentID");
                String deptName = rs.getString("DepartmentName");
                int deptHeadId = rs.getInt("DepartmentHeadID");

                String empId = getEmpIdByInstructorId(deptHeadId);

                departments.add(new DepartmentInfo(deptId, deptName, empId));
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading departments: " + e.getMessage());
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

    private void filterDepartments(String searchText) {
        departmentListModel.clear();
        boolean hasMatches = false;

        for (DepartmentInfo department : departments) {
            if (String.valueOf(department.departmentId).contains(searchText) ||
                    department.departmentName.toLowerCase().contains(searchText)) {
                departmentListModel.addElement(department.toString());
                hasMatches = true;
            }
        }

        if (hasMatches && departmentListModel.size() > 0) {
            departmentSuggestionScrollPane.setVisible(true);
            int visibleRows = Math.min(5, departmentListModel.size());
            departmentSuggestionList.setVisibleRowCount(visibleRows);

            int rowHeight = departmentSuggestionList.getFixedCellHeight();
            if (rowHeight <= 0) rowHeight = 20;
            int scrollPaneHeight = (visibleRows * rowHeight) + 5;

            departmentSuggestionScrollPane.setBounds(250, 180, 200, scrollPaneHeight);
        } else {
            departmentSuggestionScrollPane.setVisible(false);
        }

        revalidate();
        repaint();
    }

    private void loadInstructors() {
        instructors = new ArrayList<>();
        Connection conn = null;
        CallableStatement cs = null;
        ResultSet rs = null;

        try {
            conn = new Connector().connection;

            cs = conn.prepareCall("{call GetAllInstructors}");
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
        instructorListModel.clear();
        boolean hasMatches = false;

        for (InstructorInfo instructor : instructors) {
            if (instructor.empId.toLowerCase().contains(searchText) ||
                    instructor.fullName.toLowerCase().contains(searchText)) {
                instructorListModel.addElement(instructor.toString());
                hasMatches = true;
            }
        }

        if (hasMatches && instructorListModel.size() > 0) {
            instructorSuggestionScrollPane.setVisible(true);
            int visibleRows = Math.min(5, instructorListModel.size());
            instructorSuggestionList.setVisibleRowCount(visibleRows);

            int rowHeight = instructorSuggestionList.getFixedCellHeight();
            if (rowHeight <= 0) rowHeight = 20;
            int scrollPaneHeight = (visibleRows * rowHeight) + 5;

            instructorSuggestionScrollPane.setBounds(250, 380, 200, scrollPaneHeight);
        } else {
            instructorSuggestionScrollPane.setVisible(false);
        }

        revalidate();
        repaint();
    }

    private String getEmpIdByInstructorId(int instructorId) {
        Connection conn = null;
        CallableStatement cs = null;
        ResultSet rs = null;

        try {
            conn = new Connector().connection;

            cs = conn.prepareCall("{call GetInstructorByID(?)}");
            cs.setInt(1, instructorId);
            rs = cs.executeQuery();

            if (rs.next()) {
                return rs.getString("EmpID");
            }
        } catch (SQLException e) {
            return "";
        } finally {
            try {
                if (rs != null) rs.close();
                if (cs != null) cs.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return "";
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

        return -1;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submit) {
            String departmentIDText = textDepartmentID.getText().trim();
            String departmentName = textDepartmentName.getText().trim();
            String empId = textDepartmentHead.getText().trim();

            if (departmentIDText.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Department ID cannot be empty.");
                return;
            }
            if (departmentName.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Department Name cannot be empty.");
                return;
            }
            if (empId.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Department Head EMP ID cannot be empty.");
                return;
            }

            try {
                int departmentID = Integer.parseInt(departmentIDText);

                int departmentHeadID = getInstructorIdByEmpId(empId);
                if (departmentHeadID == -1) {
                    JOptionPane.showMessageDialog(null, "Invalid EMP ID: Instructor not found.");
                    return;
                }

                updateDepartment(departmentID, departmentName, departmentHeadID);
                JOptionPane.showMessageDialog(null, "Department Updated Successfully");
                setVisible(false);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Department ID must be a valid number.");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
            }
        } else {
            setVisible(false);
        }
    }

    private void updateDepartment(int departmentID, String departmentName, int departmentHeadID) throws SQLException {
        Connection conn = null;
        CallableStatement cs = null;

        try {
            conn = new Connector().connection;
            cs = conn.prepareCall("{call UpdateDepartment(?, ?, ?)}");
            cs.setInt(1, departmentID);
            cs.setString(2, departmentName);
            cs.setInt(3, departmentHeadID);
            cs.execute();
        } finally {
            if (cs != null) cs.close();
            if (conn != null) conn.close();
        }
    }

    public static void main(String[] args) {
        new UpdateDepartment();
    }
}