package ch3.data;

import java.sql.*;

/**
 * 管理员管理类（创新功能：独立管理员模块，借鉴广告墙课设）
 * 管理员可以删除用户、删除单词，但不能修改用户和单词
 */
public class AdminDB extends ConnectDatabase {

    /** 管理员登录 */
    public boolean login(String adminId, String password) {
        connectDatabase();
        try {
            PreparedStatement ps = con.prepareStatement(
                    "select * from manage_table where admin_id=? and password=?");
            ps.setString(1, adminId);
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

    /** 管理员修改密码 */
    public boolean changePassword(String adminId, String oldPwd, String newPwd) {
        if (!login(adminId, oldPwd)) return false;
        connectDatabase();
        try {
            PreparedStatement ps = con.prepareStatement(
                    "update manage_table set password=? where admin_id=?");
            ps.setString(1, newPwd);
            ps.setString(2, adminId);
            ps.executeUpdate();
            ps.close();
            con.close();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}
