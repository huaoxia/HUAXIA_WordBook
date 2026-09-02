package ch3.data;

import java.sql.*;

/**
 * 更新单词的类（书上原类，已扩展）
 * 扩展：支持修改 meaning, sentence, voice（课设要求①②）
 */
public class UpdateWord extends ConnectDatabase {
    int isOK;

    public int updateWord(Word word) {
        connectDatabase();  // 连接数据库（继承的方法）
        try {
            // 扩展：同时更新释义、例句、例句翻译、发音文件
            String SQL = "update word_table set meaning=?, sentence=?, sentence_cn=?, voice=? where id=?";
            PreparedStatement sta = con.prepareStatement(SQL);
            sta.setString(1, word.getMeaning());
            sta.setString(2, word.getSentence());
            sta.setString(3, word.getSentenceCn());
            sta.setString(4, word.getVoice());
            sta.setInt(5, word.getId());
            isOK = sta.executeUpdate();
            sta.close();
            con.close();
        } catch (SQLException e) {
            isOK = 0;
        }
        return isOK;
    }

    /**
     * 更新复习状态（创新：艾宾浩斯复习后调用）
     */
    public int updateReviewStatus(Word word) {
        connectDatabase();
        try {
            String SQL = "update word_table set memory_strength=?, review_count=?, " +
                    "last_review=?, next_review=? where id=?";
            PreparedStatement sta = con.prepareStatement(SQL);
            sta.setInt(1, word.getMemoryStrength());
            sta.setInt(2, word.getReviewCount());
            sta.setTimestamp(3, word.getLastReview());
            sta.setTimestamp(4, word.getNextReview());
            sta.setInt(5, word.getId());
            isOK = sta.executeUpdate();
            sta.close();
            con.close();

            // 记录学习日志
            try {
                Connection c = DriverManager.getConnection("jdbc:derby:MyEnglishBook;create=false");
                java.sql.Date today = new java.sql.Date(System.currentTimeMillis());
                PreparedStatement ps1 = c.prepareStatement(
                        "select * from study_log where username=? and study_date=?");
                ps1.setString(1, word.getOwner());
                ps1.setDate(2, today);
                ResultSet rs = ps1.executeQuery();
                if (rs.next()) {
                    int current = rs.getInt("reviewed_words");
                    PreparedStatement ps2 = c.prepareStatement(
                            "update study_log set reviewed_words=? where username=? and study_date=?");
                    ps2.setInt(1, current + 1);
                    ps2.setString(2, word.getOwner());
                    ps2.setDate(3, today);
                    ps2.executeUpdate();
                    ps2.close();
                } else {
                    PreparedStatement ps2 = c.prepareStatement(
                            "insert into study_log (username, study_date, reviewed_words) values (?,?,?)");
                    ps2.setString(1, word.getOwner());
                    ps2.setDate(2, today);
                    ps2.setInt(3, 1);
                    ps2.executeUpdate();
                    ps2.close();
                }
                rs.close();
                ps1.close();
                c.close();
            } catch (SQLException e) {
            }
        } catch (SQLException e) {
            isOK = 0;
        }
        return isOK;
    }
}
