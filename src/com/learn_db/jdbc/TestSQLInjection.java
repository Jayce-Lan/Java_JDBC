package com.learn_db.jdbc;

/*
    解决SQL注入
        只要用户提供的信息不参与SQL语句的编译过程问题就解决了
        即使用户提供的信息中含有SQL语句关键字，但是不参与编译
        必须使用java.sql.PreparedStatement
        PreparedStatement继承了java.sql.Statement
        PreparedStatement属于预编译的数据库操作对象
        PreparedStatement原理：预先对SQL语句框架编译，然后再给SQL语句传值

    Statement和PreparedStatement
        Statement：每次编译都会执行，效率较低
        PreparedStatement：只要值不变，编译一次可以执行多次，查询效率高
 */

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Scanner;

public class TestSQLInjection {
    public static void main(String[] args) {
        //初始化界面，并接收其返回值
        Map<String, String> userLoginInfo = initUI();
        //验证用户名和密码
        boolean loginSuccess = login(userLoginInfo);
        //输出结果
        System.out.println(loginSuccess ? "登录成功" : "用户名或密码错误！");
    }

    private static Map<String, String> initUI() {
        Scanner sc = new Scanner(System.in);
        System.out.print("用户名：");
        String loginName = sc.nextLine();
        System.out.print("密码：");
        String loginPwd = sc.nextLine();

        Map<String, String> userLoginInfo = new HashMap<>();
        userLoginInfo.put("loginName", loginName);
        userLoginInfo.put("loginPwd", loginPwd);

        return userLoginInfo;
    }

    private static boolean login(Map<String, String> userLoginInfo) {
        boolean loginSuccess = false;

        ResourceBundle bundle = ResourceBundle.getBundle("jdbc");
        String driver = bundle.getString("driver");
        String url = bundle.getString("url");
        String user = bundle.getString("user");
        String password = bundle.getString("password");

        //JDBC
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, user, password);

            //由于需要预编译数据库对象，因此要先声明语句,并且原来的字符串需要用"?"代替（占位符），而且不能使用单引号括起来，否则会被当作普通字符
            String sql = "select * from tb_user where loginName = ? and loginPwd = ?";  //SQL框架
            ps = conn.prepareStatement(sql);
            //给占位符传值，第一个?下标为1，第二个下标为2，以此类推
            //@setString(num, str); num:占位符的下标，str:HashMap的键
            //如果是其他类型的值，也可以使用setInt()...等方法
            ps.setString(1, userLoginInfo.get("loginName"));
            ps.setString(2, userLoginInfo.get("loginPwd"));
            //因为已经预编译，因此不用再传值，直接编译即可
            rs = ps.executeQuery();
            //由于返回结果有且只有一条，因此只需要用if语句判断即可
            if (rs.next()) {
                //登陆成功
                loginSuccess = true;
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
            if (ps != null) {
                try {
                    ps.close();
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

        return loginSuccess;
    }
}
