package com.learn_db.jdbc;

/*
    使用Statement的场景
    用户在控制台输入desc为降序，asc为升序
 */

import java.sql.*;
import java.util.Scanner;

public class AscendingOrder {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("请输入desc(降序)/asc(升序)：");
        String keyWords = sc.nextLine();

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/learn_db?useSSL=false&serverTimezone=UTC", "root", "root");
            stmt = conn.createStatement();
            String sql = "select name, salary from tb_emp3 order by salary " + keyWords;
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                System.out.println(rs.getString("name") + " " + rs.getInt("salary"));
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
