package ch3.view;

import javax.swing.*;
import java.awt.*;
import ch3.data.*;

/**
 * 添加单词视图（书上原类，已扩展）
 * 扩展：增加例句、发音文件输入框（课设要求①②）
 * 布局优化：GridBagLayout，输入框更宽，分布更均衡
 */
public class AddWordView extends JPanel {
    JTextField inputWord;          // 输入单词
    JTextField inputMeaning;       // 输入单词的解释
    JTextField inputSentence;      // 输入例句（课设新增）
    JTextField inputVoice;         // 输入发音文件名（课设新增）
    JButton submit;                 // 提交按钮
    JTextField hintMess;           // 提示信息
    HandleAddWord handleAddWord;   // 负责处理添加单词
    String currentUser;             // 当前登录用户

    AddWordView(String user) {
        this.currentUser = user;
        initView();
        registerListener();
    }

    private void initView() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(25, 40, 25, 40));

        // 表单面板
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 10, 12, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("宋体", Font.PLAIN, 15);
        Font inputFont = new Font("宋体", Font.PLAIN, 15);

        // 单词
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        JLabel l1 = new JLabel("单  词:"); l1.setFont(labelFont);
        formPanel.add(l1, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        inputWord = new JTextField(28); inputWord.setFont(inputFont);
        formPanel.add(inputWord, gbc);

        // 释义
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        JLabel l2 = new JLabel("释  义:"); l2.setFont(labelFont);
        formPanel.add(l2, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        inputMeaning = new JTextField(28); inputMeaning.setFont(inputFont);
        formPanel.add(inputMeaning, gbc);

        // 例句
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        JLabel l3 = new JLabel("例  句:"); l3.setFont(labelFont);
        formPanel.add(l3, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        inputSentence = new JTextField(28); inputSentence.setFont(inputFont);
        formPanel.add(inputSentence, gbc);

        // 发音文件
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        JLabel l4 = new JLabel("发音文件:"); l4.setFont(labelFont);
        formPanel.add(l4, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        inputVoice = new JTextField(28); inputVoice.setFont(inputFont);
        formPanel.add(inputVoice, gbc);

        add(formPanel, BorderLayout.CENTER);

        // 底部按钮和提示
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        submit = new JButton("添 加 单 词");
        submit.setFont(new Font("宋体", Font.BOLD, 16));
        submit.setPreferredSize(new Dimension(160, 42));
        submit.setBackground(new Color(50, 100, 180));
        submit.setForeground(Color.WHITE);

        hintMess = new JTextField(32);
        hintMess.setFont(new Font("宋体", Font.PLAIN, 14));
        hintMess.setEditable(false);
        hintMess.setBorder(BorderFactory.createEmptyBorder());

        bottomPanel.add(submit);
        bottomPanel.add(hintMess);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void registerListener() {
        handleAddWord = new HandleAddWord();
        handleAddWord.setView(this);
        submit.addActionListener(handleAddWord);
        // 回车也能提交
        inputWord.addActionListener(e -> submit.doClick());
        inputMeaning.addActionListener(e -> submit.doClick());
        inputSentence.addActionListener(e -> submit.doClick());
        inputVoice.addActionListener(e -> submit.doClick());
    }
}
