package ch3.data;

import java.sql.*;

/**
 * 添加单词的类（书上原类，已扩展）
 * 扩展：支持 sentence, voice, owner 字段，初始化艾宾浩斯复习时间
 */
public class AddWord extends ConnectDatabase {
    int isOK;

    public int insertWord(Word word) {
        connectDatabase();  // 连接数据库（继承的方法）
        try {
            String SQL = "insert into word_table (owner, word, meaning, sentence, sentence_cn, voice, " +
                    "memory_strength, review_count, next_review) values (?,?,?,?,?,?,?,?,?)";
            PreparedStatement sta = con.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
            sta.setString(1, word.getOwner());
            sta.setString(2, word.getEnglishWord());
            sta.setString(3, word.getMeaning());
            sta.setString(4, word.getSentence());
            sta.setString(5, word.getSentenceCn());
            sta.setString(6, word.getVoice());
            sta.setInt(7, word.getMemoryStrength());
            sta.setInt(8, word.getReviewCount());
            sta.setTimestamp(9, word.getNextReview());
            isOK = sta.executeUpdate();

            // 获取自增ID
            ResultSet keys = sta.getGeneratedKeys();
            if (keys.next()) {
                word.setId(keys.getInt(1));
            }
            keys.close();
            sta.close();
            con.close();

            // 记录学习日志（创新：统计用）
            incrementStudyLog(word.getOwner(), "new_words", 1);
        } catch (SQLException e) {
            isOK = 0;  // word_table表中的word字段是主键，即不允许单词重复
        }
        return isOK;
    }

    /** 增量更新学习日志（创新） */
    private void incrementStudyLog(String username, String field, int value) {
        try {
            Connection c = DriverManager.getConnection("jdbc:derby:MyEnglishBook;create=false");
            java.sql.Date today = new java.sql.Date(System.currentTimeMillis());

            PreparedStatement ps1 = c.prepareStatement(
                    "select * from study_log where username=? and study_date=?");
            ps1.setString(1, username);
            ps1.setDate(2, today);
            ResultSet rs = ps1.executeQuery();

            if (rs.next()) {
                int current = rs.getInt(field);
                PreparedStatement ps2 = c.prepareStatement(
                        "update study_log set " + field + "=? where username=? and study_date=?");
                ps2.setInt(1, current + value);
                ps2.setString(2, username);
                ps2.setDate(3, today);
                ps2.executeUpdate();
                ps2.close();
            } else {
                PreparedStatement ps2 = c.prepareStatement(
                        "insert into study_log (username, study_date, " + field + ") values (?,?,?)");
                ps2.setString(1, username);
                ps2.setDate(2, today);
                ps2.setInt(3, value);
                ps2.executeUpdate();
                ps2.close();
            }
            rs.close();
            ps1.close();
            c.close();
        } catch (SQLException e) {
        }
    }
}
