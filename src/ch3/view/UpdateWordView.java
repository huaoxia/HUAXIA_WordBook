package ch3.view;

import javax.swing.*;
import java.awt.*;
import ch3.data.*;

/**
 * 更新单词视图（书上原类，已扩展）
 * 扩展：支持修改释义、例句、发音文件（课设要求①②）
 * 布局优化：GridBagLayout，输入框更宽，分布更均衡
 */
public class UpdateWordView extends JPanel {
    JTextField inputWord;          // 输入要更新的单词
    JTextField inputNewMeaning;    // 输入单词的新解释
    JTextField inputNewSentence;   // 输入新例句（课设新增）
    JTextField inputNewVoice;      // 输入新发音文件名（课设新增）
    JButton lookWord;               // 提交查看
    JButton submit;                 // 提交更新按钮
    JTextField hintMess;            // 提示信息
    HandleUpdateWord handleUpdateWord;  // 负责处理更新单词
    String currentUser;
    Word currentWord;               // 当前查到的单词

    UpdateWordView(String user) {
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
        gbc.insets = new Insets(10, 10, 10, 10);
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

        // 查看原有解释按钮
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        JLabel l2 = new JLabel("查  看:"); l2.setFont(labelFont);
        formPanel.add(l2, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        lookWord = new JButton("查看原有解释");
        lookWord.setFont(new Font("宋体", Font.PLAIN, 14));
        lookWord.setPreferredSize(new Dimension(160, 32));
        formPanel.add(lookWord, gbc);

        // 新释义
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        JLabel l3 = new JLabel("新释义:"); l3.setFont(labelFont);
        formPanel.add(l3, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        inputNewMeaning = new JTextField(28); inputNewMeaning.setFont(inputFont);
        formPanel.add(inputNewMeaning, gbc);

        // 新例句
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        JLabel l4 = new JLabel("新例句:"); l4.setFont(labelFont);
        formPanel.add(l4, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        inputNewSentence = new JTextField(28); inputNewSentence.setFont(inputFont);
        formPanel.add(inputNewSentence, gbc);

        // 新发音文件
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        JLabel l5 = new JLabel("发音文件:"); l5.setFont(labelFont);
        formPanel.add(l5, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        inputNewVoice = new JTextField(28); inputNewVoice.setFont(inputFont);
        formPanel.add(inputNewVoice, gbc);

        add(formPanel, BorderLayout.CENTER);

        // 底部按钮和提示
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        submit = new JButton("提 交 修 改");
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
        handleUpdateWord = new HandleUpdateWord();
        handleUpdateWord.setView(this);
        submit.addActionListener(handleUpdateWord);
        lookWord.addActionListener(handleUpdateWord);
        // 回车也能提交
        inputWord.addActionListener(e -> lookWord.doClick());
        inputNewMeaning.addActionListener(e -> submit.doClick());
        inputNewSentence.addActionListener(e -> submit.doClick());
        inputNewVoice.addActionListener(e -> submit.doClick());
    }
}
