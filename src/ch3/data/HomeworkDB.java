package ch3.data;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 作业管理类（创新功能：管理员布置复习作业）
 */
public class HomeworkDB extends ConnectDatabase {

    /**
     * 布置作业
     */
    public boolean assignHomework(String username, String title, String content,
                                   int wordCount, java.sql.Date deadline, String createdBy) {
        connectDatabase();
        try {
            PreparedStatement ps = con.prepareStatement(
                    "insert into homework_table (username, title, content, word_count, deadline, created_by) " +
                    "values (?, ?, ?, ?, ?, ?)");
            ps.setString(1, username);
            ps.setString(2, title);
            ps.setString(3, content);
            ps.setInt(4, wordCount);
            ps.setDate(5, deadline);
            ps.setString(6, createdBy);
            ps.executeUpdate();
            ps.close();
            con.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取用户的所有作业
     */
    public List<Homework> getHomeworkByUser(String username) {
        List<Homework> list = new ArrayList<>();
        connectDatabase();
        try {
            PreparedStatement ps = con.prepareStatement(
                    "select * from homework_table where username=? order by created_time desc");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Homework hw = new Homework();
                hw.setId(rs.getInt("id"));
                hw.setUsername(rs.getString("username"));
                hw.setTitle(rs.getString("title"));
                hw.setContent(rs.getString("content"));
                hw.setWordCount(rs.getInt("word_count"));
                hw.setDeadline(rs.getDate("deadline"));
                hw.setCreatedBy(rs.getString("created_by"));
                hw.setCreatedTime(rs.getTimestamp("created_time"));
                hw.setStatus(rs.getString("status"));
                list.add(hw);
            }
            rs.close();
            ps.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取用户未完成的作业
     */
    public List<Homework> getUnfinishedHomework(String username) {
        List<Homework> list = new ArrayList<>();
        connectDatabase();
        try {
            PreparedStatement ps = con.prepareStatement(
                    "select * from homework_table where username=? and status='未完成' order by deadline asc");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Homework hw = new Homework();
                hw.setId(rs.getInt("id"));
                hw.setUsername(rs.getString("username"));
                hw.setTitle(rs.getString("title"));
                hw.setContent(rs.getString("content"));
                hw.setWordCount(rs.getInt("word_count"));
                hw.setDeadline(rs.getDate("deadline"));
                hw.setCreatedBy(rs.getString("created_by"));
                hw.setCreatedTime(rs.getTimestamp("created_time"));
                hw.setStatus(rs.getString("status"));
                list.add(hw);
            }
            rs.close();
            ps.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取所有作业（管理员用）
     */
    public List<Homework> getAllHomework() {
        List<Homework> list = new ArrayList<>();
        connectDatabase();
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select * from homework_table order by created_time desc");
            while (rs.next()) {
                Homework hw = new Homework();
                hw.setId(rs.getInt("id"));
                hw.setUsername(rs.getString("username"));
                hw.setTitle(rs.getString("title"));
                hw.setContent(rs.getString("content"));
                hw.setWordCount(rs.getInt("word_count"));
                hw.setDeadline(rs.getDate("deadline"));
                hw.setCreatedBy(rs.getString("created_by"));
                hw.setCreatedTime(rs.getTimestamp("created_time"));
                hw.setStatus(rs.getString("status"));
                list.add(hw);
            }
            rs.close();
            stmt.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 标记作业完成
     */
    public boolean markComplete(int id) {
        connectDatabase();
        try {
            PreparedStatement ps = con.prepareStatement(
                    "update homework_table set status='已完成' where id=?");
            ps.setInt(1, id);
            int n = ps.executeUpdate();
            ps.close();
            con.close();
            return n > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除作业
     */
    public boolean deleteHomework(int id) {
        connectDatabase();
        try {
            PreparedStatement ps = con.prepareStatement("delete from homework_table where id=?");
            ps.setInt(1, id);
            int n = ps.executeUpdate();
            ps.close();
            con.close();
            return n > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
