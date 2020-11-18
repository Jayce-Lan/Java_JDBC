package com.learn_db.jdbc;

import java.sql.*;

public class JDBCTest01 {
    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;
        try {
            //----------------注册驱动
            //mysql8.0以及以下版本需要com.mysql.jdbc.Driver();
            Driver driver = new com.mysql.cj.jdbc.Driver();
            //Driver driver = new oracle.jdbc.driver.OracleDriver();    //Oracle数据库的驱动
            DriverManager.registerDriver(driver);


            //----------------获取连接
			/*
				url: 统一资源定位符（网络中某个资源的绝对路径）
				如：http://182.61.200.7:80/index.html
					http://	通信协议
					182.61.200.7	服务器IP地址
					80	服务器上软件的端口
					index.html	服务器上某个资源的名称

				这里的url：
					jdbc:mysql://	协议
					127.0.0.1/localhost	IP地址
					3306	MySQL的端口号
					learn_db	MySQL在上面指定IP地址端口号下的数据库实例名称
				Oracle协议：
				jdbc:oracle:thin:@loaclhost:1521:orcl
			*/
			//后缀如果不添加&useSSL=false&serverTimezone=UTC会发生报错
            String url = "jdbc:mysql://localhost/learn_db?useSSL=false&serverTimezone=UTC";
            String user = "root";
            String password = "root";
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("数据库连接对象" + conn);


            //----------------获取数据库操作对象(Statement对象专门执行SQL语句)
            //创建一个Statement对象将SQL语句发送到数据库
            stmt = conn.createStatement();


            //----------------执行SQL语句
            String sql = "insert into tb_emp3(name, salary) values('Tom', 2200)";
            //executeUpdate() 执行DML语句(INSERT， UPDATE，或 DELETE语句)
            //返回值时影响数据库中的记录条数 插入多少条或者删除多少条就是它的返回值
            int count = stmt.executeUpdate(sql);
            System.out.println(count == 1 ? "保存成功" : "保存失败");


            //----------------查询处理结果

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //----------------释放资源
            //为了保证资源一定释放，要在finally语句中关闭资源，并且要遵循从小到大依次关闭
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
