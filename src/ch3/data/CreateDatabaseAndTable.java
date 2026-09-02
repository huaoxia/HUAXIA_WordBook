package ch3.data;

import java.sql.*;

/**
 * 创建数据库和表的类（书上原类，已扩展）
 *
 * 原表：word_table (word varchar(50) primary key, meaning varchar(200))
 * 扩展后：
 *   - word_table：增加 sentence, sentence_cn, voice, owner, memory_strength, review_count, last_review, next_review
 *   - user_table：用户表（创新：多用户登录）
 *   - manage_table：管理员表（创新：独立管理员模块，借鉴广告墙课设）
 *   - study_log：学习日志表（创新：统计可视化）
 *   - homework_table：作业表（创新：管理员布置复习作业）
 */
public class CreateDatabaseAndTable {
    Connection con;

    public CreateDatabaseAndTable() {
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        } catch (Exception e) {
        }
        try {
            String uri = "jdbc:derby:MyEnglishBook;create=true";
            con = DriverManager.getConnection(uri);
        } catch (Exception e) {
        }
        try {
            Statement sta = con.createStatement();

            // ===== 单词表 =====
            String SQL1 = "create table word_table(" +
                    "id int generated always as identity primary key, " +
                    "owner varchar(50), " +
                    "word varchar(50), " +
                    "meaning varchar(500), " +
                    "sentence varchar(1000), " +
                    "sentence_cn varchar(1000), " +
                    "voice varchar(200), " +
                    "memory_strength int default 0, " +
                    "review_count int default 0, " +
                    "last_review timestamp, " +
                    "next_review timestamp, " +
                    "create_time timestamp default current_timestamp)";
            sta.executeUpdate(SQL1);

            // ===== 用户表 =====
            String SQL2 = "create table user_table(" +
                    "username varchar(50) primary key, " +
                    "password varchar(50))";
            sta.executeUpdate(SQL2);

            // ===== 管理员表 =====
            String SQL3 = "create table manage_table(" +
                    "admin_id varchar(50) primary key, " +
                    "password varchar(50))";
            sta.executeUpdate(SQL3);

            // ===== 学习日志表 =====
            String SQL4 = "create table study_log(" +
                    "id int generated always as identity primary key, " +
                    "username varchar(50), " +
                    "study_date date, " +
                    "new_words int default 0, " +
                    "reviewed_words int default 0, " +
                    "quiz_correct int default 0, " +
                    "quiz_total int default 0)";
            sta.executeUpdate(SQL4);

            // ===== 作业表（创新：管理员布置复习作业） =====
            String SQL5 = "create table homework_table(" +
                    "id int generated always as identity primary key, " +
                    "username varchar(50), " +
                    "title varchar(200), " +
                    "content varchar(2000), " +
                    "word_count int default 0, " +
                    "deadline date, " +
                    "created_by varchar(50), " +
                    "created_time timestamp default current_timestamp, " +
                    "status varchar(20) default '未完成')";
            sta.executeUpdate(SQL5);

            // 插入默认管理员
            PreparedStatement ps = con.prepareStatement(
                    "insert into manage_table (admin_id, password) values (?, ?)");
            ps.setString(1, "admin");
            ps.setString(2, "admin123");
            ps.executeUpdate();
            ps.close();

            // 插入一个测试用户
            PreparedStatement ps2 = con.prepareStatement(
                    "insert into user_table (username, password) values (?, ?)");
            ps2.setString(1, "user");
            ps2.setString(2, "user123");
            ps2.executeUpdate();
            ps2.close();

            sta.close();
            con.close();
            System.out.println("数据库和表创建成功！");
            System.out.println("默认管理员：admin / admin123");
            System.out.println("测试用户：user / user123");
        } catch (SQLException e) {
            // 表已存在则跳过
        }
    }
}
