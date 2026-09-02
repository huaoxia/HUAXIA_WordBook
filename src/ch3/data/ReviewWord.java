package ch3.data;

import java.sql.*;

/**
 * 获取待复习单词的类（创新功能）
 * 查询当前需要复习的单词（下次复习时间 <= 当前时间）
 */
public class ReviewWord extends ConnectDatabase {
    String owner;

    public void setOwner(String owner) {
        this.owner = owner;
    }

    /** 获取当前需要复习的单词列表，按记忆强度从低到高排序 */
    public Word[] getWordsToReview() {
        connectDatabase();
        Word[] word = null;
        try {
            String sqlStr = "select * from word_table where owner=? " +
                    "and (next_review is null or next_review <= ?) " +
                    "order by memory_strength asc, next_review asc";
            PreparedStatement ps = con.prepareStatement(sqlStr,
                    ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ps.setString(1, owner);
            ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            ResultSet rs = ps.executeQuery();
            rs.last();
            int recordAmount = rs.getRow();
            word = new Word[recordAmount];
            for (int i = 0; i < word.length; i++) {
                word[i] = new Word();
            }
            rs.beforeFirst();
            int i = 0;
            while (rs.next()) {
                word[i].setId(rs.getInt("id"));
                word[i].setOwner(rs.getString("owner"));
                word[i].setEnglishWord(rs.getString("word"));
                word[i].setMeaning(rs.getString("meaning"));
                word[i].setSentence(rs.getString("sentence"));
                word[i].setSentenceCn(rs.getString("sentence_cn"));
                word[i].setVoice(rs.getString("voice"));
                word[i].setMemoryStrength(rs.getInt("memory_strength"));
                word[i].setReviewCount(rs.getInt("review_count"));
                word[i].setLastReview(rs.getTimestamp("last_review"));
                word[i].setNextReview(rs.getTimestamp("next_review"));
                i++;
            }
            rs.close();
            ps.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return word;
    }
}
