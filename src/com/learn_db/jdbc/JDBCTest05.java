package com.learn_db.jdbc;

import java.sql.*;
import java.util.ResourceBundle;

public class JDBCTest05 {
    public static void main(String[] args) {
        ResourceBundle bundle = ResourceBundle.getBundle("jdbc");
        String driver = bundle.getString("driver");
        String url = bundle.getString("url");
        String user = bundle.getString("user");
        String password = bundle.getString("password");

        Connection conn = null;
        Statement stmt = null;
        //表示数据库结果集的数据表，它通常是通过执行查询数据库的语句生成的
        ResultSet rs = null;    //结果集，查询结果会被封装到该对象中

        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, user, password);
            stmt = conn.createStatement();
            String sql = "select id, name, deptId, salary from tb_emp3";
            rs = stmt.executeQuery(sql);    //executeQuery()专门用于执行查询语句，执行给顶的SQL语句，返回单个的ResultSet对象

            /*
                遍历结果集，ResultSet当中的.next()方法在遍历集合当中，每遍历一行，如果有数据就返回true
                我们需要取数据，可以使用getString()方法取出一列的数据，不管数据库内的内容为什么，都已String取出，而且该方法下标由1开始，JDBC中的所有下标都从1开始
                getString();里面的参数也可以设置为查询结果的列名
                除了可以以String形式取出，也可以以特定类型取出
            */
            System.out.println("id name deptId salary");
            while (rs.next()) {
                /*String eid = rs.getString(1);
                String ename = rs.getString(2);
                String deptId = rs.getString(3);
                String salary = rs.getString(4);*/
                String eid = rs.getString("id");
                String ename = rs.getString("name");
                String deptId = rs.getString("deptId");
                int salary = rs.getInt("salary");
                System.out.println(eid + " " + ename + " " + deptId + " " + (salary + 100));
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
