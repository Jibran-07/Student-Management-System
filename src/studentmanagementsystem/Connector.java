package studentmanagementsystem;

import java.sql.Connection;
import java.sql.DriverManager;

public class Connector {
    Connection connection;
    public Connector() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            String url = "jdbc:sqlserver://localhost:1433;databaseName=UniversityDB;user=saa;password=dblab;encrypt=true;trustServerCertificate=true";
            connection = DriverManager.getConnection(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}