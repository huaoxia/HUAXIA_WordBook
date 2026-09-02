package ch3.data;

import java.sql.*;
import java.util.*;

/**
 * 随机查询单词的类（书上原类，已扩展）
 * 扩展：按用户查询，返回完整Word对象
 */
public class RandomQueryWord extends ConnectDatabase {
    int count = 0;           // 随机抽取的数目
    String owner;             // 创新：指定用户

    public void setCount(int n) {
        count = n;
    }
    public int getCount() {
        return count;
    }
    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Word[] randomQueryWord() {
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
            rs = sql.executeQuery(sqlStr);
            rs.last();
            int recordAmount = rs.getRow();  // 结果集中的记录数目
            count = Math.min(count, recordAmount);
            word = new Word[count];
            for (int i = 0; i < word.length; i++) {
                word[i] = new Word();
            }
            // 得到1到recordAmount之间的count个互不相同的随机整数
            int[] index = getRandomNumber(recordAmount, count);
            int m = 0;
            for (int randomNumer : index) {
                rs.absolute(randomNumer);
                word[m].setId(rs.getInt("id"));
                word[m].setOwner(rs.getString("owner"));
                word[m].setEnglishWord(rs.getString("word"));
                word[m].setMeaning(rs.getString("meaning"));
                word[m].setSentence(rs.getString("sentence"));
                word[m].setSentenceCn(rs.getString("sentence_cn"));
                word[m].setVoice(rs.getString("voice"));
                word[m].setMemoryStrength(rs.getInt("memory_strength"));
                word[m].setReviewCount(rs.getInt("review_count"));
                m++;
            }
            con.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return word;
    }

    // 得到1到max之间的amount个互不相同的随机整数（包括1和max）
    public int[] getRandomNumber(int max, int count) {
        int[] randomNumber = new int[count];
        Set<Integer> set = new HashSet<Integer>();  // set不允许有相同的元素
        int index = set.size();
        Random random = new Random();
        while (index < count) {
            int number = random.nextInt(max) + 1;
            set.add(number);  // 将number放入集合set中
            index = set.size();
        }
        Iterator<Integer> iter = set.iterator();
        index = 0;
        while (iter.hasNext()) {
            Integer te = iter.next();
            randomNumber[index] = te.intValue();
            index++;
        }
        return randomNumber;
    }
}
