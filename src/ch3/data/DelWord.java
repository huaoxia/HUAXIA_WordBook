package ch3.data;

import java.sql.*;

/**
 * 删除单词的类（书上原类，已扩展为按id删除）
 */
public class DelWord extends ConnectDatabase {
    int isOK;

    public int delWord(Word word) {
        connectDatabase();
        try {
            String SQL = "delete from word_table where id = ?";
            PreparedStatement sta = con.prepareStatement(SQL);
            sta.setInt(1, word.getId());
            isOK = sta.executeUpdate();
            sta.close();
            con.close();
        } catch (SQLException e) {
            isOK = 0;
        }
        return isOK;
    }

    /** 管理员删除任意单词（创新：管理员模块） */
    public int delWordById(int id) {
        connectDatabase();
        try {
            String SQL = "delete from word_table where id = ?";
            PreparedStatement sta = con.prepareStatement(SQL);
            sta.setInt(1, id);
            isOK = sta.executeUpdate();
            sta.close();
            con.close();
        } catch (SQLException e) {
            isOK = 0;
        }
        return isOK;
    }

    /** 清空指定用户的所有单词（管理员用） */
    public int delByOwner(String owner) {
        connectDatabase();
        try {
            String SQL = "delete from word_table where owner = ?";
            PreparedStatement sta = con.prepareStatement(SQL);
            sta.setString(1, owner);
            isOK = sta.executeUpdate();
            sta.close();
            con.close();
        } catch (SQLException e) {
            isOK = 0;
        }
        return isOK;
    }
}
