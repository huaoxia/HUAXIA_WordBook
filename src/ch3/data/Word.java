package ch3.data;

import java.sql.Timestamp;

/**
 * 封装单词数据的类（书上原类，已扩展）
 * 原字段：englishWord, meaning
 * 课设新增：sentence（例句）, voice（发音文件名）
 * 创新扩展：id, owner, sentenceCn（例句翻译）, memoryStrength, reviewCount, lastReview, nextReview
 */
public class Word {
    int id;                     // 主键，自增（创新：支持多用户）
    String owner;               // 所属用户名（创新：多用户隔离）
    String englishWord;         // 单词（书上原字段）
    String meaning;             // 单词的解释（书上原字段）
    String sentence;            // 例句（课设新增字段）
    String sentenceCn;          // 例句中文翻译（创新）
    String voice;               // 发音音频文件名（课设新增字段）
    // ===== 艾宾浩斯遗忘曲线复习字段（创新） =====
    int memoryStrength;         // 记忆强度 0~100
    int reviewCount;            // 已复习次数
    Timestamp lastReview;       // 上次复习时间
    Timestamp nextReview;       // 下次复习时间

    public Word() {
        this.memoryStrength = 0;
        this.reviewCount = 0;
    }

    public Word(String englishWord, String meaning) {
        this();
        this.englishWord = englishWord;
        this.meaning = meaning;
    }

    // ===== getter / setter =====
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; }

    public void setEnglishWord(String englishWord) {
        this.englishWord = englishWord;
    }
    public String getEnglishWord() {
        return englishWord;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }
    public String getMeaning() {
        return meaning;
    }

    public String getSentence() { return sentence; }
    public void setSentence(String sentence) { this.sentence = sentence; }

    public String getSentenceCn() { return sentenceCn; }
    public void setSentenceCn(String sentenceCn) { this.sentenceCn = sentenceCn; }

    public String getVoice() { return voice; }
    public void setVoice(String voice) { this.voice = voice; }

    public int getMemoryStrength() { return memoryStrength; }
    public void setMemoryStrength(int memoryStrength) {
        this.memoryStrength = Math.max(0, Math.min(100, memoryStrength));
    }

    public int getReviewCount() { return reviewCount; }
    public void setReviewCount(int reviewCount) { this.reviewCount = reviewCount; }

    public Timestamp getLastReview() { return lastReview; }
    public void setLastReview(Timestamp lastReview) { this.lastReview = lastReview; }

    public Timestamp getNextReview() { return nextReview; }
    public void setNextReview(Timestamp nextReview) { this.nextReview = nextReview; }
}
