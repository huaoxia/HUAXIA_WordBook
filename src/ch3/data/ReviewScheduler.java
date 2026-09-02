package ch3.data;

import java.sql.Timestamp;
import java.util.Calendar;

/**
 * 艾宾浩斯遗忘曲线复习调度器（创新功能）
 * 核心算法：根据复习次数和用户反馈，动态计算下次复习时间和记忆强度
 *
 * 复习间隔序列（天）：1, 2, 4, 7, 15, 30, 60
 * 用户反馈三种：认识 / 模糊 / 忘记
 */
public class ReviewScheduler {

    // 艾宾浩斯标准复习间隔（单位：天）
    private static final int[] INTERVALS = {1, 2, 4, 7, 15, 30, 60};

    // 用户反馈
    public static final int FEEDBACK_KNOWN = 1;      // 认识
    public static final int FEEDBACK_FUZZY = 2;      // 模糊
    public static final int FEEDBACK_FORGOTTEN = 3;  // 忘记

    /** 根据复习次数获取标准间隔天数 */
    private static int getIntervalDays(int reviewCount) {
        int index = Math.min(reviewCount, INTERVALS.length - 1);
        return INTERVALS[index];
    }

    /**
     * 处理一次复习，更新单词的记忆强度和下次复习时间
     * @param word 被复习的单词
     * @param feedback 用户反馈（KNOWN/FUZZY/FORGOTTEN）
     * @return 更新后的单词对象
     */
    public static Word review(Word word, int feedback) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        int newStrength = word.getMemoryStrength();
        int newCount = word.getReviewCount();
        int intervalDays;

        switch (feedback) {
            case FEEDBACK_KNOWN:
                // 认识：强度+20，复习次数+1，使用下一级间隔
                newStrength = Math.min(100, newStrength + 20);
                newCount++;
                intervalDays = getIntervalDays(newCount);
                break;
            case FEEDBACK_FUZZY:
                // 模糊：强度+5，复习次数不变，间隔不变
                newStrength = Math.min(100, newStrength + 5);
                intervalDays = getIntervalDays(newCount);
                break;
            case FEEDBACK_FORGOTTEN:
            default:
                // 忘记：强度-15，复习次数重置为0，间隔重置为1天
                newStrength = Math.max(0, newStrength - 15);
                newCount = 0;
                intervalDays = 1;
                break;
        }

        // 计算下次复习时间
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.DAY_OF_MONTH, intervalDays);

        word.setMemoryStrength(newStrength);
        word.setReviewCount(newCount);
        word.setLastReview(now);
        word.setNextReview(new Timestamp(cal.getTimeInMillis()));

        return word;
    }

    /** 判断单词当前是否需要复习 */
    public static boolean needsReview(Word word) {
        if (word.getNextReview() == null) {
            return true;
        }
        return word.getNextReview().getTime() <= System.currentTimeMillis();
    }

    /** 新单词初始化复习时间 */
    public static void initNewWord(Word word) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        word.setMemoryStrength(0);
        word.setReviewCount(0);
        word.setLastReview(null);
        word.setNextReview(now);
    }

    /** 获取记忆强度的文字描述 */
    public static String getStrengthLabel(int strength) {
        if (strength >= 80) return "牢固";
        if (strength >= 50) return "熟悉";
        if (strength >= 20) return "一般";
        return "陌生";
    }
}
