package ch3.test;

import ch3.data.*;

/**
 * 简单测试类（书上3.3节，已扩展）
 * 在命令行测试数据层的增删改查、模糊查询、艾宾浩斯复习等功能
 */
public class AppTest {
    public static void main(String[] args) {
        // 创建数据库和表
        new CreateDatabaseAndTable();

        String testUser = "user";

        // ===== 测试添加单词 =====
        System.out.println("===== 测试添加单词 =====");
        Word word = new Word();
        String[][] a = {
                {"boy", "男孩"},
                {"girl", "女孩"},
                {"sun", "太阳"},
                {"moon", "月亮"},
                {"book", "书籍"},
                {"water", "水"},
                {"apple", "苹果"},
                {"application", "应用程序"},
                {"comfortable", "舒适的"},
                {"reasonable", "合理的"}
        };
        AddWord addWord = new AddWord();
        for (int i = 0; i < a.length; i++) {
            word = new Word();
            word.setOwner(testUser);
            word.setEnglishWord(a[i][0]);
            word.setMeaning(a[i][1]);
            word.setSentence("This is a sentence for " + a[i][0] + ".");
            ReviewScheduler.initNewWord(word);
            int ok = addWord.insertWord(word);
            System.out.println("添加 " + a[i][0] + ": " + (ok > 0 ? "成功" : "失败"));
        }

        // ===== 测试查询一个单词 =====
        System.out.println("\n===== 测试查询一个单词 =====");
        QueryOneWord q = new QueryOneWord();
        Word queryWord = new Word();
        queryWord.setOwner(testUser);
        queryWord.setEnglishWord("boy");
        Word re = q.queryOneWord(queryWord);
        if (re != null) {
            System.out.printf("%-15s", re.getEnglishWord());
            System.out.printf("%-10s\n", re.getMeaning());
            System.out.println("例句: " + re.getSentence());
            System.out.println("记忆强度: " + re.getMemoryStrength());
        }

        // ===== 测试查询全部单词 =====
        System.out.println("\n===== 测试查询全部单词 =====");
        QueryAllWord query = new QueryAllWord();
        query.setOwner(testUser);
        Word[] result = query.queryAllWord();
        for (int i = 0; i < result.length; i++) {
            int m = i + 1;
            System.out.printf("%d. %-15s %s\n", m, result[i].getEnglishWord(), result[i].getMeaning());
        }

        // ===== 测试模糊查询（课设要求④） =====
        System.out.println("\n===== 测试模糊查询 =====");
        FuzzyQueryWord fuzzy = new FuzzyQueryWord();
        fuzzy.setOwner(testUser);

        System.out.println("前缀查询 'app':");
        Word[] prefixResult = fuzzy.fuzzyQuery("app", "prefix");
        for (Word w : prefixResult) {
            System.out.println("  " + w.getEnglishWord() + " - " + w.getMeaning());
        }

        System.out.println("后缀查询 'able':");
        Word[] suffixResult = fuzzy.fuzzyQuery("able", "suffix");
        for (Word w : suffixResult) {
            System.out.println("  " + w.getEnglishWord() + " - " + w.getMeaning());
        }

        System.out.println("包含查询 'oo':");
        Word[] containsResult = fuzzy.fuzzyQuery("oo", "contains");
        for (Word w : containsResult) {
            System.out.println("  " + w.getEnglishWord() + " - " + w.getMeaning());
        }

        // ===== 测试随机查询 =====
        System.out.println("\n===== 测试随机查询3个单词 =====");
        RandomQueryWord random = new RandomQueryWord();
        random.setOwner(testUser);
        random.setCount(3);
        result = random.randomQueryWord();
        for (int i = 0; i < result.length; i++) {
            int m = i + 1;
            System.out.printf("%d. %-15s %s\n", m, result[i].getEnglishWord(), result[i].getMeaning());
        }

        // ===== 测试艾宾浩斯复习（创新） =====
        System.out.println("\n===== 测试艾宾浩斯复习 =====");
        ReviewWord review = new ReviewWord();
        review.setOwner(testUser);
        Word[] toReview = review.getWordsToReview();
        System.out.println("待复习单词数: " + toReview.length);

        if (toReview.length > 0) {
            Word w = toReview[0];
            System.out.println("复习单词: " + w.getEnglishWord());
            System.out.println("复习前强度: " + w.getMemoryStrength());

            // 模拟"认识"反馈
            ReviewScheduler.review(w, ReviewScheduler.FEEDBACK_KNOWN);
            UpdateWord update = new UpdateWord();
            update.updateReviewStatus(w);

            System.out.println("复习后强度: " + w.getMemoryStrength());
            System.out.println("复习次数: " + w.getReviewCount());
            System.out.println("下次复习: " + w.getNextReview());
        }

        // ===== 测试统计数据（创新） =====
        System.out.println("\n===== 测试统计数据 =====");
        StatDB stat = new StatDB();
        stat.setOwner(testUser);
        System.out.println("单词总数: " + stat.getTotalWords());
        System.out.println("已掌握: " + stat.getMasteredWords());
        System.out.println("待复习: " + stat.getReviewCount());

        // ===== 测试用户管理（创新） =====
        System.out.println("\n===== 测试用户管理 =====");
        UserDB userDB = new UserDB();
        System.out.println("用户登录 user/user123: " + userDB.login("user", "user123"));
        System.out.println("注册新用户 test001: " + userDB.register("test001", "123456"));
        System.out.println("所有用户: " + userDB.getAllUsers());

        // ===== 测试管理员（创新） =====
        System.out.println("\n===== 测试管理员 =====");
        AdminDB adminDB = new AdminDB();
        System.out.println("管理员登录 admin/admin123: " + adminDB.login("admin", "admin123"));

        System.out.println("\n===== 全部测试完成 =====");
    }
}
