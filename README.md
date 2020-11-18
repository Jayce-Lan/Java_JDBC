

# JDBC



## JDBC是什么

> Java DataBase Connectivity（Java连接数据库）

JDBC是SUN公司制定的一套接口（interface）（java.sql.*）；由于每个数据库的底层实现原理各不相同，因此需要一套接口去调用数据库。数据库厂家编写了JDBC的实现类，而Java程序员负责去调用接口以达到连接数据库的目的。

所有数据库的驱动都以jar包的形式存在，驱动由各大数据库厂商提供（需要去该数据库官网下载）。



## 模拟JDBC本质的程序

### JDBC接口类

```java
package simulationJDBC;

//接口类 定义一个JDBC接口，(sun公司JDBC接口的角色)

public interface JDBC {
    /**
     * @getgetConnection() 连接数据库的方法
     */
    void getConnection();
}

```

### 数据库类

> 数据库类负责去实现接口，值得注意的是，这里的实现类必须要被编译，否则将无法被调用

```java
package simulationJDBC;

//实现类 数据库厂家负责实现JDBC接口，实现类被称为驱动

public class MySQL implements JDBC {
    @Override
    public void getConnection() {
        //这里的逻辑代码涉及到数据库底层实现原理，但是与Java程序员无关
        System.out.println("连接MySQL成功...");
    }
}
```

### 程序员

> 负责调用接口

```java
package simulationJDBC;

/*
    调用类 程序员负责调用接口，而不需要再去关心数据库底层的实现类
    面向接口/面向抽象编程
 */

import java.util.ResourceBundle;

public class JavaProgram {

    public static void main(String[] args) throws Exception {
        JDBC jdbc = new MySQL();
        //JDBC jdbc = new Oracle();
        //由于调用的是接口方法，因此，即使上面的数据库类型更换，也不需要改变下面的调用方法，而是更改数据库类型即可
        jdbc.getConnection();
    }
}
```



上面的jdbc调用也可以使用反射机制去创建对象

```java
//main方法中的语句可以改变为
Class c = Class.forName("MySQL");
JDBC jdbc = (JDBC)c.newInstance();
jdbc.getConnection();
```



### 配置文件

增加一个配置文件`jdbc.properties`通过键值对的形式调用接口，可以实现只改变配置文件而不改变Java代码去调用数据库

```properties
className=MySQL
```



而Java程序员类中的main方法中可以改变为如下代码

```java
//配置文件(本文件夹中的jdbc.properties)读取对象
ResourceBundle bundle = ResourceBundle.getBundle("jdbc");
//这里获取的是配置文件中的值，由于配置文件都由键值对形成，因此，这里写入的是key，从而获取value
String className = bundle.getString("className");
Class c = Class.forName(className);
JDBC jdbc = (JDBC)c.newInstance();
jdbc.getConnection();
```



## JDBC开发前的准备工作

> 从官网下载对应的jar包，并配置到环境变量classpath中(IDEA有自己的配置方式)



## JDBC编程六步

- **注册驱动**（声明数据库类型）

  - 使用Driver类驱动数据库

  - 由Java的父类去调用引入数据库jar包的子类

  - ```java
    //mysql8.0以及以下版本需要com.mysql.jdbc.Driver();
    Driver driver = new com.mysql.cj.jdbc.Driver();
    //Driver driver = new oracle.jdbc.driver.OracleDriver();    //Oracle数据库的驱动
    DriverManager.registerDriver(driver);	//注册一个加载驱动的方法
    ```

- **获取连接**（声明JVM进程和数据库进程之间的通道打开，属于进程之间的通信，由于是重量级的，因此使用完毕后必须关闭）

  - ```java
    //后缀如果不添加&useSSL=false&serverTimezone=UTC会发生报错
    String url = "jdbc:mysql://localhost/learn_db?useSSL=false&serverTimezone=UTC";
    String user = "root";
    String password = "root";
    //由于方便再finally语句中关闭，Connection conn = null在开始时就需要声明
    conn = DriverManager.getConnection(url, user, password);	
    ```

- **获取数据库操作对象**（专门执行sql语句的对象）

  - ```java
    //创建一个Statement对象将SQL语句发送到数据库
    //由于方便再finally语句中关闭，Statement stmt = null在开始时就需要声明
    stmt = conn.createStatement();
    ```

  - 

- **执行SQL语句**

  - ```java
    String sql = "insert into tb_emp3(name, salary) values('Jim', 2000);";
    //executeUpdate() 执行DML语句(INSERT， UPDATE，或 DELETE语句)
    //返回值时影响数据库中的记录条数 插入多少条或者删除多少条就是它的返回值
    int count =  stmt.executeUpdate(sql);
    System.out.println(count == 1 ? "保存成功" : "保存失败");
    ```

- **处理查询结果集**（只有第四步执行的是select语句时才会出现该步骤）

- **释放资源**（使用完毕后一定要关闭资源，由于Java和数据库属于进程间的通信，因此开启后必须关闭）

  - 关闭顺序（由小到大）

    - 先关闭结果集`ResultSet rs`
    - 再关闭`Statement stmt`
    - 最后关闭`Connection conn`

  - ```java
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
    ```



## JDBC实例

> 值得注意的是，JDBC的sql语句不需要在结尾提供分号

### 项目目录

- jdbc_test
  - src
    - jdbc.properties
    - com.learn.db_jdbc
      - JDBCTest01.java
      - JDBCTest02.java
      - JDBCTest03.java
      - JDBCTest04.java
  - 数据库的jar依赖



### 数据库表插入数据(增)

```java
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
            String sql = "insert into tb_emp3(name, salary) values('Jim', 2000)";
            //executeUpdate() 执行DML语句(INSERT， UPDATE，或 DELETE语句)
            //返回值时影响数据库中的记录条数 插入多少条或者删除多少条就是它的返回值
            int count =  stmt.executeUpdate(sql);
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
```



### 删除数据库表中的内容(删)

```java
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
            String sql = "delete from tb_emp3 where name = 'Tim'";
            int count = stmt.executeUpdate(sql);
            //如果删除条件中含有多个相同的，那么count != 1
            System.out.println(count);
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
```



### 修改数据表中的内容(改)

```java
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
            if (conn != null) {
                try {
                    conn.close();
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
        }
    }
}
```



### 引入Driver的加载动作

> 在上述代码中，注册驱动语句可以做如下改动

```java
//注册驱动
DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
->
Class.forName("com.mysql.cj.jdbc.Driver");
```

- 由于数据库厂商把Driver类作为一个静态的类进行了加载，因此只需静态引入
- 常用的注册驱动的方式，因为中间的字符串参数可以写入到配置文件当中
- 因为我们只需要这个加载动作，因此不需要接收返回值

#### jdbc.properties

> 注意：这里的配置文件一定要放在src目录下

在实际业务中，由于Java程序内部修改会比较麻烦，因此会使用配置文件来对数据库的各项进行操作

```properties
driver=com.mysql.cj.jdbc.Driver
url=jdbc:mysql://localhost/learn_db?useSSL=false&serverTimezone=UTC
user=root
password=root
```



#### JDBCTest04文件

```java
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
```



### 查询数据库语句(查)

> 相比于前面的语句，查询语句多了一个结果集

`ResultSet rs`表示数据库结果集的数据表，它通常是通过执行查询数据库的语句生成的，查询结果会被封装到该对象中

```java
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
```



### executeUpdate()/executeQuery()

**executeUpdate()**

`executeUpdate(insert/delete/update)`专门用于执行增删改语句，返回一个int对象，该值为修改数据库表数量的值



**executeQuery()**

`executeQuery(select)`专门用于执行查询语句，执行给顶的SQL语句，返回单个的`ResultSet`对象
