package ch3.data;

import java.sql.*;

/**
 * 统计数据类（创新功能：学习统计可视化）
 */
public class StatDB extends ConnectDatabase {
    String owner;

    public void setOwner(String owner) {
        this.owner = owner;
    }

    /** 获取单词总数 */
    public int getTotalWords() {
        connectDatabase();
        try {
            PreparedStatement ps = con.prepareStatement(
                    "select count(*) from word_table where owner=?");
            ps.setString(1, owner);
            ResultSet rs = ps.executeQuery();
            int n = rs.next() ? rs.getInt(1) : 0;
            rs.close();
            ps.close();
            con.close();
            return n;
        } catch (SQLException e) {
            return 0;
        }
    }

    /** 获取已掌握单词数（记忆强度 >= 80） */
    public int getMasteredWords() {
        connectDatabase();
        try {
            PreparedStatement ps = con.prepareStatement(
                    "select count(*) from word_table where owner=? and memory_strength >= 80");
            ps.setString(1, owner);
            ResultSet rs = ps.executeQuery();
            int n = rs.next() ? rs.getInt(1) : 0;
            rs.close();
            ps.close();
            con.close();
            return n;
        } catch (SQLException e) {
            return 0;
        }
    }

    /** 获取当前待复习单词数 */
    public int getReviewCount() {
        connectDatabase();
        try {
            PreparedStatement ps = con.prepareStatement(
                    "select count(*) from word_table where owner=? and (next_review is null or next_review <= ?)");
            ps.setString(1, owner);
            ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            ResultSet rs = ps.executeQuery();
            int n = rs.next() ? rs.getInt(1) : 0;
            rs.close();
            ps.close();
            con.close();
            return n;
        } catch (SQLException e) {
            return 0;
        }
    }

    /**
     * 获取最近N天的学习数据（用于统计图表）
     * 返回二维数组：data[天][0]=新增单词数, data[天][1]=复习单词数
     */
    public int[][] getRecentStudyData(int days) {
        int[][] data = new int[days][2];
        connectDatabase();
        try {
            PreparedStatement ps = con.prepareStatement(
                    "select study_date, new_words, reviewed_words from study_log " +
                    "where username=? and study_date >= current_date - ? day(s) order by study_date");
            ps.setString(1, owner);
            ps.setInt(2, days);
            ResultSet rs = ps.executeQuery();

            java.util.Map<String, int[]> map = new java.util.HashMap<>();
            while (rs.next()) {
                String date = rs.getDate("study_date").toString();
                map.put(date, new int[]{rs.getInt("new_words"), rs.getInt("reviewed_words")});
            }
            rs.close();
            ps.close();
            con.close();

            // 填充最近days天的数据
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.add(java.util.Calendar.DAY_OF_MONTH, -(days - 1));
            for (int i = 0; i < days; i++) {
                String date = new java.sql.Date(cal.getTimeInMillis()).toString();
                int[] d = map.get(date);
                if (d != null) {
                    data[i][0] = d[0];
                    data[i][1] = d[1];
                } else {
                    data[i][0] = 0;
                    data[i][1] = 0;
                }
                cal.add(java.util.Calendar.DAY_OF_MONTH, 1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    /** 获取最近N天的日期标签（用于图表X轴） */
    public static String[] getRecentDateLabels(int days) {
        String[] labels = new String[days];
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.add(java.util.Calendar.DAY_OF_MONTH, -(days - 1));
        for (int i = 0; i < days; i++) {
            labels[i] = String.format("%d/%d",
                    cal.get(java.util.Calendar.MONTH) + 1,
                    cal.get(java.util.Calendar.DAY_OF_MONTH));
            cal.add(java.util.Calendar.DAY_OF_MONTH, 1);
        }
        return labels;
    }

    /** 记录测验结果 */
    public void recordQuizResult(int correct, int total) {
        connectDatabase();
        try {
            java.sql.Date today = new java.sql.Date(System.currentTimeMillis());
            PreparedStatement ps1 = con.prepareStatement(
                    "select * from study_log where username=? and study_date=?");
            ps1.setString(1, owner);
            ps1.setDate(2, today);
            ResultSet rs = ps1.executeQuery();
            if (rs.next()) {
                int c = rs.getInt("quiz_correct");
                int t = rs.getInt("quiz_total");
                PreparedStatement ps2 = con.prepareStatement(
                        "update study_log set quiz_correct=?, quiz_total=? where username=? and study_date=?");
                ps2.setInt(1, c + correct);
                ps2.setInt(2, t + total);
                ps2.setString(3, owner);
                ps2.setDate(4, today);
                ps2.executeUpdate();
                ps2.close();
            } else {
                PreparedStatement ps2 = con.prepareStatement(
                        "insert into study_log (username, study_date, quiz_correct, quiz_total) values (?,?,?,?)");
                ps2.setString(1, owner);
                ps2.setDate(2, today);
                ps2.setInt(3, correct);
                ps2.setInt(4, total);
                ps2.executeUpdate();
                ps2.close();
            }
            rs.close();
            ps1.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
