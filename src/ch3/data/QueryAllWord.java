package ch3.data;

import java.sql.*;

/**
 * 查询全部单词的类（书上原类，已扩展）
 * 扩展：按用户查询，返回完整Word对象
 */
public class QueryAllWord extends ConnectDatabase {
    String owner;  // 创新：指定查询哪个用户的单词

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Word[] queryAllWord() {
        connectDatabase();  // 连接数据库（继承的方法）
        Word[] word = null;
        Statement sql;
        ResultSet rs;
        try {
            sql = con.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String sqlStr = "select * from word_table";
            if (owner != null && !owner.isEmpty()) {
                sqlStr += " where owner='" + owner + "'";
            }
            sqlStr += " order by id desc";
            rs = sql.executeQuery(sqlStr);
            rs.last();
            int recordAmount = rs.getRow();  // 结果集中的全部记录
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
            con.close();
        } catch (SQLException e) {
        }
        return word;
    }
}
