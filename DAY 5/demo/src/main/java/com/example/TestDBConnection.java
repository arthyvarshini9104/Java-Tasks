package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestDBConnection {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/splitwise";
        String username = "root";
        String password = "Arthy2004*";

        try {
            Connection conn = DriverManager.getConnection(url, username, password);
            System.out.println("Database connection successful!");
            conn.close();
        } catch (SQLException e) {
            System.out.println("Database connection failed!");
            e.printStackTrace();
        }
    }
}
