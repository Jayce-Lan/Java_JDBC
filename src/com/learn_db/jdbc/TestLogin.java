package com.learn_db.jdbc;

/*
    @作者 Jayce
    @需求 模拟用户登录功能
    @业务描述
        1.程序运行时，提供一个输入的入口，让用户输入用户名和密码
        2.用户输入用户名和密码，验证是否登录成功
        3.解决sql注入
    @数据准备
        实际开发中，表的设计会使用建模工具
        设计数据库表PowerDesigner
 */

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Scanner;

public class TestLogin {
    public static void main(String[] args) {
        //初始化界面，并接收其返回值
        Map<String, String> userLoginInfo = initUI();
        //验证用户名和密码
        boolean loginSuccess = login(userLoginInfo);
        //输出结果
        System.out.println(loginSuccess ? "登录成功" : "用户名或密码错误！");
    }

    /**
     * 初始化一个界面，让用户输入账号和密码
     * @return 方法返回一个集合，该集合键值对指向的为账号、密码等用户登录信息
     */
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

    /**
     * 传入一个用户信息集合，判断用户是否登录成功
     * @param userLoginInfo 验证用户信息集合
     * @return  返回一个布尔类型的值，true登录成功，false登录失败
     *
     * @程序问题
     *         输入以下字段：
     *         用户名：fdsa
     *         密码：fdsa' or '1'='1
     *         登录成功
     *         这种现象被成为SQL注入(黑客经常使用)
     * @SQL注入：
     *         在写入密码时，xxx' or '1'='1 语句使得数据库被写入了'1'='1'这种语句，而且判断条件为or
     *         select * from tb_user where loginName = 'user' and loginPwd = 'user' or '1'='1';
     *         因此该语句会恒成立
     * @导致SQL注入的根本原因：
     *         用户输入的语句中含有sql关键字，并且这些关键字参与sql语句的编译过程
     *         这就导致了sql原意被扭曲，而达到sql注入
     */
    private static boolean login(Map<String, String> userLoginInfo) {
        boolean loginSuccess = false;

        ResourceBundle bundle = ResourceBundle.getBundle("jdbc");
        String driver = bundle.getString("driver");
        String url = bundle.getString("url");
        String user = bundle.getString("user");
        String password = bundle.getString("password");

        //JDBC
        Connection conn = null;
        //数据库的操作对象
        Statement stmt = null;
        ResultSet rs = null;

        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, user, password);
            stmt = conn.createStatement();
            String sql =
                    "select * from tb_user where loginName = '" + userLoginInfo.get("loginName") + "' and loginPwd = '" + userLoginInfo.get("loginPwd") + "'";
            rs = stmt.executeQuery(sql);
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

        return loginSuccess;
    }
}