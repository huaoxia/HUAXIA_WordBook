package ch3.data;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户管理类（创新功能：多用户登录）
 */
public class UserDB extends ConnectDatabase {

    /** 用户注册 */
    public boolean register(String username, String password) {
        connectDatabase();
        try {
            PreparedStatement ps = con.prepareStatement(
                    "insert into user_table (username, password) values (?, ?)");
            ps.setString(1, username);
            ps.setString(2, password);
            ps.executeUpdate();
            ps.close();
            con.close();
            return true;
        } catch (SQLException e) {
            return false;  // 用户名已存在
        }
    }

    /** 用户登录 */
    public boolean login(String username, String password) {
        connectDatabase();
        try {
            PreparedStatement ps = con.prepareStatement(
                    "select * from user_table where username=? and password=?");
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            boolean ok = rs.next();
            rs.close();
            ps.close();
            con.close();
            return ok;
        } catch (SQLException e) {
            return false;
        }
    }

    /** 用户修改密码 */
    public boolean changePassword(String username, String oldPwd, String newPwd) {
        if (!login(username, oldPwd)) return false;
        connectDatabase();
        try {
            PreparedStatement ps = con.prepareStatement(
                    "update user_table set password=? where username=?");
            ps.setString(1, newPwd);
            ps.setString(2, username);
            ps.executeUpdate();
            ps.close();
            con.close();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    /** 管理员重置用户密码（不需要原密码，创新功能） */
    public boolean resetPassword(String username, String newPwd) {
        connectDatabase();
        try {
            PreparedStatement ps = con.prepareStatement(
                    "update user_table set password=? where username=?");
            ps.setString(1, newPwd);
            ps.setString(2, username);
            int n = ps.executeUpdate();
            ps.close();
            con.close();
            return n > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** 获取所有用户（管理员用） */
    public List<String> getAllUsers() {
        List<String> list = new ArrayList<>();
        connectDatabase();
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select username from user_table order by username");
            while (rs.next()) {
                list.add(rs.getString("username"));
            }
            rs.close();
            stmt.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /** 管理员删除用户（同时删除该用户的所有单词和学习日志） */
    public boolean deleteUser(String username) {
        connectDatabase();
        try {
            // 删除用户的单词
            PreparedStatement ps1 = con.prepareStatement("delete from word_table where owner=?");
            ps1.setString(1, username);
            ps1.executeUpdate();
            ps1.close();
            // 删除用户的学习日志
            PreparedStatement ps2 = con.prepareStatement("delete from study_log where username=?");
            ps2.setString(1, username);
            ps2.executeUpdate();
            ps2.close();
            // 删除用户
            PreparedStatement ps3 = con.prepareStatement("delete from user_table where username=?");
            ps3.setString(1, username);
            int n = ps3.executeUpdate();
            ps3.close();
            con.close();
            return n > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
