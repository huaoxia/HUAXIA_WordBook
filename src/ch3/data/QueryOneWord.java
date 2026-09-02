package ch3.data;

import java.sql.*;

/**
 * 查询一个单词的类（书上原类，已扩展）
 * 扩展：返回完整Word对象（含sentence, voice, 艾宾浩斯字段）
 */
public class QueryOneWord extends ConnectDatabase {

    public Word queryOneWord(Word word) {
        connectDatabase();  // 连接数据库（继承的方法）
        Word result = null;
        Statement sql;
        ResultSet rs;
        String str = "select * from word_table where owner='" + word.getOwner()
                + "' and word='" + word.getEnglishWord() + "'";
        try {
            sql = con.createStatement();
            rs = sql.executeQuery(str);
            if (rs.next()) {
                result = new Word();
                result.setId(rs.getInt("id"));
                result.setOwner(rs.getString("owner"));
                result.setEnglishWord(rs.getString("word"));
                result.setMeaning(rs.getString("meaning"));
                result.setSentence(rs.getString("sentence"));
                result.setSentenceCn(rs.getString("sentence_cn"));
                result.setVoice(rs.getString("voice"));
                result.setMemoryStrength(rs.getInt("memory_strength"));
                result.setReviewCount(rs.getInt("review_count"));
                result.setLastReview(rs.getTimestamp("last_review"));
                result.setNextReview(rs.getTimestamp("next_review"));
            }
            con.close();
        } catch (SQLException e) {
        }
        return result;
    }

    /** 按id查询单词（创新） */
    public Word queryById(int id) {
        connectDatabase();
        Word result = null;
        try {
            PreparedStatement ps = con.prepareStatement("select * from word_table where id=?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                result = new Word();
                result.setId(rs.getInt("id"));
                result.setOwner(rs.getString("owner"));
                result.setEnglishWord(rs.getString("word"));
                result.setMeaning(rs.getString("meaning"));
                result.setSentence(rs.getString("sentence"));
                result.setSentenceCn(rs.getString("sentence_cn"));
                result.setVoice(rs.getString("voice"));
                result.setMemoryStrength(rs.getInt("memory_strength"));
                result.setReviewCount(rs.getInt("review_count"));
                result.setLastReview(rs.getTimestamp("last_review"));
                result.setNextReview(rs.getTimestamp("next_review"));
            }
            rs.close();
            ps.close();
            con.close();
        } catch (SQLException e) {
        }
        return result;
    }
}
