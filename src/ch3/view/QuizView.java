package ch3.view;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import ch3.data.*;

/**
 * 随机测验视图（创新功能）
 * 从词库随机抽取单词，给出中文释义让用户选择英文单词，即时判分
 */
public class QuizView extends JPanel {
    String currentUser;
    Word[] quizWords;
    int currentIndex;
    int correctCount;
    int totalCount;

    JLabel questionLabel;
    JLabel progressLabel;
    JLabel resultLabel;
    JButton[] optionButtons;
    JButton nextBtn;
    JButton restartBtn;

    QuizView(String user) {
        this.currentUser = user;
        initView();
        startQuiz();
    }

    private void initView() {
        setLayout(new BorderLayout());

        // 顶部
        progressLabel = new JLabel("测验进度", SwingConstants.CENTER);
        progressLabel.setFont(new Font("宋体", Font.BOLD, 16));
        add(progressLabel, BorderLayout.NORTH);

        // 中间题目
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(30, 20, 20, 20));

        questionLabel = new JLabel("选择正确的单词", SwingConstants.CENTER);
        questionLabel.setFont(new Font("宋体", Font.BOLD, 32));
        questionLabel.setForeground(new Color(0, 0, 150));

        resultLabel = new JLabel("", SwingConstants.CENTER);
        resultLabel.setFont(new Font("宋体", Font.BOLD, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 10, 20, 10);
        centerPanel.add(questionLabel, gbc);
        gbc.gridy = 1;
        centerPanel.add(resultLabel, gbc);

        // 选项按钮
        JPanel optionsPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        optionButtons = new JButton[4];
        for (int i = 0; i < 4; i++) {
            optionButtons[i] = new JButton();
            optionButtons[i].setFont(new Font("Arial", Font.PLAIN, 20));
            optionButtons[i].setPreferredSize(new Dimension(200, 50));
            final int idx = i;
            optionButtons[i].addActionListener(e -> checkAnswer(idx));
            optionsPanel.add(optionButtons[i]);
        }

        gbc.gridy = 2;
        centerPanel.add(optionsPanel, gbc);

        add(centerPanel, BorderLayout.CENTER);

        // 底部按钮
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        nextBtn = new JButton("下一题");
        nextBtn.setFont(new Font("宋体", Font.PLAIN, 16));
        nextBtn.setEnabled(false);
        nextBtn.addActionListener(e -> nextQuestion());

        restartBtn = new JButton("重新开始");
        restartBtn.setFont(new Font("宋体", Font.PLAIN, 16));
        restartBtn.addActionListener(e -> startQuiz());

        bottomPanel.add(nextBtn);
        bottomPanel.add(restartBtn);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void startQuiz() {
        RandomQueryWord random = new RandomQueryWord();
        random.setOwner(currentUser);
        random.setCount(10);
        quizWords = random.randomQueryWord();

        if (quizWords == null || quizWords.length < 4) {
            questionLabel.setText("词库单词不足4个，无法开始测验");
            for (JButton btn : optionButtons) btn.setEnabled(false);
            return;
        }

        currentIndex = 0;
        correctCount = 0;
        totalCount = quizWords.length;
        resultLabel.setText("");
        nextBtn.setEnabled(false);
        for (JButton btn : optionButtons) {
            btn.setEnabled(true);
            btn.setBackground(null);
        }
        showQuestion();
    }

    private void showQuestion() {
        if (currentIndex >= totalCount) {
            showFinalResult();
            return;
        }

        Word correct = quizWords[currentIndex];
        questionLabel.setText("释义: " + correct.getMeaning());
        progressLabel.setText("第 " + (currentIndex + 1) + " / " + totalCount + " 题"
                + "    正确: " + correctCount);
        resultLabel.setText("");
        nextBtn.setEnabled(false);

        // 生成4个选项（1个正确 + 3个干扰）
        List<String> options = new ArrayList<>();
        options.add(correct.getEnglishWord());

        // 从其他单词中选3个干扰项
        List<Word> others = new ArrayList<>();
        for (Word w : quizWords) {
            if (!w.getEnglishWord().equals(correct.getEnglishWord())) {
                others.add(w);
            }
        }
        Collections.shuffle(others);
        for (int i = 0; i < Math.min(3, others.size()); i++) {
            options.add(others.get(i).getEnglishWord());
        }
        // 如果干扰项不够，用默认值
        while (options.size() < 4) {
            options.add("option" + options.size());
        }
        Collections.shuffle(options);

        for (int i = 0; i < 4; i++) {
            optionButtons[i].setText(options.get(i));
            optionButtons[i].setEnabled(true);
            optionButtons[i].setBackground(null);
        }
    }

    private void checkAnswer(int idx) {
        if (currentIndex >= totalCount) return;

        Word correct = quizWords[currentIndex];
        String selected = optionButtons[idx].getText();

        for (JButton btn : optionButtons) btn.setEnabled(false);

        if (selected.equals(correct.getEnglishWord())) {
            correctCount++;
            resultLabel.setText("回答正确！");
            resultLabel.setForeground(new Color(0, 150, 0));
            optionButtons[idx].setBackground(new Color(100, 200, 100));
        } else {
            resultLabel.setText("回答错误！正确答案: " + correct.getEnglishWord());
            resultLabel.setForeground(Color.RED);
            optionButtons[idx].setBackground(new Color(255, 100, 100));
            // 高亮正确答案
            for (JButton btn : optionButtons) {
                if (btn.getText().equals(correct.getEnglishWord())) {
                    btn.setBackground(new Color(100, 200, 100));
                }
            }
        }
        nextBtn.setEnabled(true);
    }

    private void nextQuestion() {
        currentIndex++;
        showQuestion();
    }

    private void showFinalResult() {
        questionLabel.setText("测验完成！");
        progressLabel.setText("");
        resultLabel.setText("得分: " + correctCount + " / " + totalCount
                + "  (" + (totalCount > 0 ? correctCount * 100 / totalCount : 0) + "分)");
        resultLabel.setForeground(new Color(0, 0, 150));
        for (JButton btn : optionButtons) {
            btn.setText("");
            btn.setEnabled(false);
        }
        nextBtn.setEnabled(false);

        // 记录测验结果
        StatDB stat = new StatDB();
        stat.setOwner(currentUser);
        stat.recordQuizResult(correctCount, totalCount);
    }

    public void refresh() {
        startQuiz();
    }
}
