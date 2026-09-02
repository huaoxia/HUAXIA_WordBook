package ch3.view;

import javax.swing.*;
import java.awt.*;
import ch3.data.*;

/**
 * 艾宾浩斯复习视图（创新功能）
 * 展示待复习单词，用户选择认识/模糊/忘记，系统动态计算下次复习时间
 */
public class ReviewView extends JPanel {
    String currentUser;
    Word[] wordsToReview;
    int currentIndex;

    JLabel wordLabel;           // 单词显示
    JLabel meaningLabel;        // 释义显示（默认隐藏，点击显示）
    JLabel sentenceLabel;       // 例句显示
    JLabel strengthLabel;       // 记忆强度显示
    JLabel progressLabel;       // 进度显示
    JButton showAnswerBtn;      // 显示答案按钮
    JButton knownBtn;           // 认识按钮
    JButton fuzzyBtn;           // 模糊按钮
    JButton forgottenBtn;       // 忘记按钮
    JButton playVoiceBtn;       // 发音按钮

    ReviewView(String user) {
        this.currentUser = user;
        initView();
        loadWords();
    }

    private void initView() {
        setLayout(new BorderLayout());

        // 顶部进度
        progressLabel = new JLabel("待复习: 0", SwingConstants.CENTER);
        progressLabel.setFont(new Font("宋体", Font.BOLD, 16));
        add(progressLabel, BorderLayout.NORTH);

        // 中间单词展示
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        wordLabel = new JLabel("点击开始复习", SwingConstants.CENTER);
        wordLabel.setFont(new Font("Arial", Font.BOLD, 48));

        meaningLabel = new JLabel("", SwingConstants.CENTER);
        meaningLabel.setFont(new Font("宋体", Font.PLAIN, 24));
        meaningLabel.setForeground(new Color(0, 100, 0));

        sentenceLabel = new JLabel("", SwingConstants.CENTER);
        sentenceLabel.setFont(new Font("宋体", Font.PLAIN, 16));
        sentenceLabel.setForeground(Color.GRAY);

        strengthLabel = new JLabel("", SwingConstants.CENTER);
        strengthLabel.setFont(new Font("宋体", Font.PLAIN, 14));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        centerPanel.add(wordLabel, gbc);
        gbc.gridy = 1;
        centerPanel.add(meaningLabel, gbc);
        gbc.gridy = 2;
        centerPanel.add(sentenceLabel, gbc);
        gbc.gridy = 3;
        centerPanel.add(strengthLabel, gbc);

        add(centerPanel, BorderLayout.CENTER);

        // 底部按钮
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));

        showAnswerBtn = new JButton("显示答案");
        showAnswerBtn.setFont(new Font("宋体", Font.PLAIN, 16));
        playVoiceBtn = new JButton("发音");
        playVoiceBtn.setFont(new Font("宋体", Font.PLAIN, 16));
        knownBtn = new JButton("认识");
        knownBtn.setFont(new Font("宋体", Font.PLAIN, 16));
        knownBtn.setBackground(new Color(100, 200, 100));
        fuzzyBtn = new JButton("模糊");
        fuzzyBtn.setFont(new Font("宋体", Font.PLAIN, 16));
        fuzzyBtn.setBackground(new Color(255, 200, 100));
        forgottenBtn = new JButton("忘记");
        forgottenBtn.setFont(new Font("宋体", Font.PLAIN, 16));
        forgottenBtn.setBackground(new Color(255, 100, 100));

        bottomPanel.add(showAnswerBtn);
        bottomPanel.add(playVoiceBtn);
        bottomPanel.add(knownBtn);
        bottomPanel.add(fuzzyBtn);
        bottomPanel.add(forgottenBtn);

        add(bottomPanel, BorderLayout.SOUTH);

        // 事件处理
        showAnswerBtn.addActionListener(e -> showAnswer());
        knownBtn.addActionListener(e -> handleFeedback(ReviewScheduler.FEEDBACK_KNOWN));
        fuzzyBtn.addActionListener(e -> handleFeedback(ReviewScheduler.FEEDBACK_FUZZY));
        forgottenBtn.addActionListener(e -> handleFeedback(ReviewScheduler.FEEDBACK_FORGOTTEN));
        playVoiceBtn.addActionListener(e -> playVoice());
    }

    private void loadWords() {
        ReviewWord review = new ReviewWord();
        review.setOwner(currentUser);
        wordsToReview = review.getWordsToReview();
        currentIndex = 0;
        if (wordsToReview != null && wordsToReview.length > 0) {
            showCurrentWord();
        } else {
            wordLabel.setText("没有需要复习的单词");
            meaningLabel.setText("");
            sentenceLabel.setText("");
            strengthLabel.setText("");
            progressLabel.setText("待复习: 0");
        }
    }

    private void showCurrentWord() {
        if (wordsToReview == null || currentIndex >= wordsToReview.length) {
            wordLabel.setText("复习完成！");
            meaningLabel.setText("");
            sentenceLabel.setText("");
            strengthLabel.setText("");
            progressLabel.setText("全部复习完毕");
            return;
        }
        Word w = wordsToReview[currentIndex];
        wordLabel.setText(w.getEnglishWord());
        meaningLabel.setText("****** 点击显示答案 ******");
        sentenceLabel.setText(w.getSentence() != null ? w.getSentence() : "");
        strengthLabel.setText("记忆强度: " + w.getMemoryStrength()
                + " (" + ReviewScheduler.getStrengthLabel(w.getMemoryStrength()) + ")"
                + "  复习次数: " + w.getReviewCount());
        progressLabel.setText("进度: " + (currentIndex + 1) + " / " + wordsToReview.length);
    }

    private void showAnswer() {
        if (wordsToReview == null || currentIndex >= wordsToReview.length) return;
        Word w = wordsToReview[currentIndex];
        meaningLabel.setText(w.getMeaning());
    }

    private void playVoice() {
        if (wordsToReview == null || currentIndex >= wordsToReview.length) return;
        Word w = wordsToReview[currentIndex];
        String word = w.getEnglishWord();

        // 优先用有道词典在线发音（创新功能）
        PlayMusic player = new PlayMusic();
        boolean onlineOk = player.playOnline(word, 2);
        if (onlineOk) {
            return;
        }

        // 在线发音失败，尝试本地文件
        if (w.getVoice() != null && !w.getVoice().trim().isEmpty()) {
            if (player.load(w.getVoice().trim())) {
                player.play();
                return;
            }
        }

        JOptionPane.showMessageDialog(this,
                "在线发音启动失败，且未设置本地发音文件\n请检查网络连接");
    }

    private void handleFeedback(int feedback) {
        if (wordsToReview == null || currentIndex >= wordsToReview.length) return;

        Word w = wordsToReview[currentIndex];
        ReviewScheduler.review(w, feedback);
        UpdateWord update = new UpdateWord();
        update.updateReviewStatus(w);

        currentIndex++;
        showCurrentWord();
    }

    /** 刷新数据（切换到该选项卡时调用） */
    public void refresh() {
        loadWords();
    }
}
