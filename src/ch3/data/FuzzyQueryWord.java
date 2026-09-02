package ch3.data;

import java.sql.*;

/**
 * 模糊查询单词的类（课设要求④，新增类）
 * 支持三种查询模式：
 *   PREFIX  - 前缀查询（如 app 开头的单词）
 *   SUFFIX  - 后缀查询（如 able 结尾的单词）
 *   CONTAINS - 包含查询（如包含 sum 的单词）
 */
public class FuzzyQueryWord extends ConnectDatabase {
    String owner;

    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * 模糊查询
     * @param keyword 关键词
     * @param type 查询类型："prefix" / "suffix" / "contains"
     */
    public Word[] fuzzyQuery(String keyword, String type) {
        connectDatabase();
        Word[] word = null;
        try {
            String pattern;
            if ("prefix".equalsIgnoreCase(type)) {
                pattern = keyword + "%";
            } else if ("suffix".equalsIgnoreCase(type)) {
                pattern = "%" + keyword;
            } else {
                pattern = "%" + keyword + "%";
            }

            String sqlStr = "select * from word_table where word like ?";
            if (owner != null && !owner.isEmpty()) {
                sqlStr += " and owner=?";
            }
            sqlStr += " order by word";

            PreparedStatement ps = con.prepareStatement(sqlStr,
                    ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ps.setString(1, pattern);
            if (owner != null && !owner.isEmpty()) {
                ps.setString(2, owner);
            }
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
