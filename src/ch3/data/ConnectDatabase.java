package ch3.data;

import java.sql.*;

/**
 * 连接数据库的类（书上原类，未改动）
 * 后续很多类的实例都需要连接数据库，因此将连接数据库的代码封装到此类中，
 * 其他需要连接数据库的类只要扩展该类就可以使用连接数据库的代码。
 */
public class ConnectDatabase {
    Connection con;

    public final void connectDatabase() {
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            String uri = "jdbc:derby:MyEnglishBook;create=false";
            con = DriverManager.getConnection(uri);  // 连接数据库的代码
        } catch (Exception e) {
            // 数据库不存在时，由CreateDatabaseAndTable负责创建
        }
    }
}
