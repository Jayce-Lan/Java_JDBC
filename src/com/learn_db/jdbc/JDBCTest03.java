package com.learn_db.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCTest03 {
    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;
        try {
            //注册驱动
            DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());

            //获取连接
            conn = DriverManager
                    .getConnection("jdbc:mysql://localhost/learn_db?useSSL=false&serverTimezone=UTC", "root", "root");

            //获取数据库操作对象
            stmt = conn.createStatement();

            //执行SQL语句
            String sql = "update tb_emp3 set salary = 1800, name = 'Tony' where id = 3";
            int count = stmt.executeUpdate(sql);
            System.out.println(count == 1 ? "修改成功！" : "修改失败！");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
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
