package com.learn_db.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

//将连接数据库的所有信息配置到配置文件中
public class JDBCTest04 {
    public static void main(String[] args) {
        //引入配置文件
        ResourceBundle bundle = ResourceBundle.getBundle("jdbc");
        String driver = bundle.getString("driver");
        String url = bundle.getString("url");
        String user = bundle.getString("user");
        String password = bundle.getString("password");

        Connection conn = null;
        Statement stmt = null;
        try {
            /*
                由于数据库厂商把Driver类作为一个静态的类进行了加载，因此只需静态引入
                常用的注册驱动的方式，因为中间的字符串参数可以写入到配置文件当中
                因为我们只需要这个加载动作，因此不需要接收返回值
            */
            Class.forName(driver);

            conn = DriverManager.getConnection(url, user, password);

            stmt = conn.createStatement();

            String sql = "insert into tb_emp3(name, salary) values('Tim', 2100)";
            int count = stmt.executeUpdate(sql);

            System.out.println(count == 1 ? "添加成功" : "添加失败");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if(conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
