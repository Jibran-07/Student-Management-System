package studentmanagementsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdminDashboard extends JFrame implements ActionListener {
    AdminDashboard() {
        ImageIcon i1 = new ImageIcon(getClass().getResource("/icon/third.jpg"));
        Image i2 = i1.getImage().getScaledInstance(1540, 750, Image.SCALE_DEFAULT);
        ImageIcon i3 = new ImageIcon(i2);
        JLabel img = new JLabel(i3);
        add(img);

        JMenuBar mb = new JMenuBar();

        JMenu addInfo = new JMenu("Add Information");
        addInfo.setForeground(Color.BLACK);
        mb.add(addInfo);

        JMenuItem addStudent = new JMenuItem("Add Student");
        addStudent.setBackground(Color.WHITE);
        addStudent.addActionListener(this);
        addInfo.add(addStudent);

        JMenuItem addInstructor = new JMenuItem("Add Instructor");
        addInstructor.setBackground(Color.WHITE);
        addInstructor.addActionListener(this);
        addInfo.add(addInstructor);

        JMenuItem addSemester = new JMenuItem("Add Semester");
        addSemester.setBackground(Color.WHITE);
        addSemester.addActionListener(this);
        addInfo.add(addSemester);

        JMenuItem addCourse = new JMenuItem("Add Course");
        addCourse.setBackground(Color.WHITE);
        addCourse.addActionListener(this);
        addInfo.add(addCourse);

        JMenuItem addDepartment = new JMenuItem("Add Department");
        addDepartment.setBackground(Color.WHITE);
        addDepartment.addActionListener(this);
        addInfo.add(addDepartment);

        JMenuItem addBatch = new JMenuItem("Add Batch");
        addBatch.setBackground(Color.WHITE);
        addBatch.addActionListener(this);
        addInfo.add(addBatch);

        JMenu updateInfo = new JMenu("Update Information");
        updateInfo.setForeground(Color.BLACK);
        mb.add(updateInfo);

        JMenuItem updateStudent = new JMenuItem("Update Student");
        updateStudent.setBackground(Color.WHITE);
        updateStudent.addActionListener(this);
        updateInfo.add(updateStudent);

        JMenuItem updateInstructor = new JMenuItem("Update Instructor");
        updateInstructor.setBackground(Color.WHITE);
        updateInstructor.addActionListener(this);
        updateInfo.add(updateInstructor);

        JMenuItem updateSemester = new JMenuItem("Update Semester");
        updateSemester.setBackground(Color.WHITE);
        updateSemester.addActionListener(this);
        updateInfo.add(updateSemester);

        JMenuItem updateCourse = new JMenuItem("Update Course");
        updateCourse.setBackground(Color.WHITE);
        updateCourse.addActionListener(this);
        updateInfo.add(updateCourse);

        JMenuItem updateDepartment = new JMenuItem("Update Department");
        updateDepartment.setBackground(Color.WHITE);
        updateDepartment.addActionListener(this);
        updateInfo.add(updateDepartment);

        JMenu manageRecords = new JMenu("Manage Records");
        manageRecords.setForeground(Color.BLACK);
        mb.add(manageRecords);

        JMenuItem markAttendance = new JMenuItem("Mark Attendance");
        markAttendance.setBackground(Color.WHITE);
        markAttendance.addActionListener(this);
        manageRecords.add(markAttendance);

        JMenuItem markGrades = new JMenuItem("Mark Grades");
        markGrades.setBackground(Color.WHITE);
        markGrades.addActionListener(this);
        manageRecords.add(markGrades);

        JMenu viewRecords = new JMenu("View Records");
        viewRecords.setForeground(Color.BLACK);
        mb.add(viewRecords);

        JMenuItem studentDetails = new JMenuItem("Student Details");
        studentDetails.setBackground(Color.WHITE);
        studentDetails.addActionListener(this);
        viewRecords.add(studentDetails);

        JMenuItem instructorDetails = new JMenuItem("Instructor Details");
        instructorDetails.setBackground(Color.WHITE);
        instructorDetails.addActionListener(this);
        viewRecords.add(instructorDetails);

        JMenu exit = new JMenu("Exit");
        exit.setForeground(Color.BLACK);
        mb.add(exit);

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setBackground(Color.WHITE);
        exitItem.addActionListener(this);
        exit.add(exitItem);

        setJMenuBar(mb);
        setSize(1540, 850);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String sm = e.getActionCommand();
        if (sm.equals("Exit")) {
            System.exit(0);
        } else if (sm.equals("Add Student")) {
            new AddStudent();
        } else if (sm.equals("Add Instructor")) {
            new AddInstructor();
        } else if (sm.equals("Add Semester")) {
            new AddSemester();
        } else if (sm.equals("Add Course")) {
            new AddCourse();
        } else if (sm.equals("Add Department")) {
            new AddDepartment();
        } else if (sm.equals("Add Batch")) {
            new AddBatch();
        }else if (sm.equals("Update Student")) {
            new UpdateStudent();
        } else if (sm.equals("Update Instructor")) {
            new UpdateInstructor();
        } else if (sm.equals("Update Semester")) {
            new UpdateSemester();
        } else if (sm.equals("Update Course")) {
            new UpdateCourse();
        } else if (sm.equals("Update Department")) {
            new UpdateDepartment();
        } else if (sm.equals("Mark Attendance")) {
            new MarkAttendance();
        } else if (sm.equals("Mark Grades")) {
            new MarkGrades();
        } else if (sm.equals("Student Details")) {
            new StudentDetails();
        } else if (sm.equals("Instructor Details")) {
            new InstructorDetails();
        }
    }

    public static void main(String[] args) {
        new AdminDashboard();
    }
}