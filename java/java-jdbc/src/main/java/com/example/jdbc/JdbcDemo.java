package com.example.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JdbcDemo {

    public static void main(String[] args) throws ClassNotFoundException {

        Class.forName("org.postgresql.Driver");

        Connection conn = null;
        Statement stmt = null;
        try {
            conn = DriverManager.getConnection("jdbc:postgresql://172.28.128.3:5432/db1", "user1", "user1");
            stmt = conn.createStatement();
            String sql = "select table_name from information_schema.tables";
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
               String tableName = rs.getString("table_name");
               System.out.println("tableName = " + tableName);
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
            e.printStackTrace();
        }
    }
}