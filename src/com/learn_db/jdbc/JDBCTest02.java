package com.learn_db.jdbc;

import java.sql.*;

public class JDBCTest02 {
    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;
        try {
            //注册驱动
            Driver driver = new com.mysql.cj.jdbc.Driver();
            DriverManager.registerDriver(driver);

            //获取连接
            String url = "jdbc:mysql://localhost/learn_db?useSSL=false&serverTimezone=UTC";
            String user = "root";
            String password = "root";
            conn = DriverManager.getConnection(url, user, password);
            System.out.println(conn);

            //获取数据库操作对象
            stmt = conn.createStatement();
            String sql = "delete from tb_emp3 where name = 'Tim';";
//            String sql = "insert into tb_emp3(name, salary) values('Tim', 2100);";
            int count = stmt.executeUpdate(sql);
            //如果删除条件中含有多个相同的，那么count != 1
            System.out.println(count);
//            System.out.println(count == 1 ? "删除成功！" : "删除失败！");
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
