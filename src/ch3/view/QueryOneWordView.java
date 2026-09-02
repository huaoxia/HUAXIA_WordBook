package ch3.view;

import javax.swing.*;
import java.awt.*;
import java.awt.Desktop;
import java.net.URI;
import ch3.data.*;

/**
 * 查询一个单词视图（书上原类，已扩展）
 * 扩展：显示例句、发音文件，增加发音播放按钮（课设要求③）
 * 创新：增加有道词典在线查询按钮，点击打开浏览器查单词
 * 布局优化：输入框更宽，按钮分两行分布更均衡
 */
public class QueryOneWordView extends JPanel {
    JTextField inputWord;              // 输入要查询的单词
    JButton submit;                     // 提交按钮
    JTextArea showWord;                 // 显示查询结果
    JButton playVoice;                  // 发音按钮（课设新增）
    JButton onlineQuery;                // 有道词典在线查询（创新）
    HandleQueryOneWord handleQueryOneWord;  // 负责处理查询单词
    String currentUser;
    Word currentResult;                 // 当前查询结果（用于发音播放）

    QueryOneWordView(String user) {
        this.currentUser = user;
        initView();
        registerListener();
    }

    private void initView() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // 顶部面板：两行布局
        JPanel pNorth = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 第一行：标签 + 输入框
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        JLabel label = new JLabel("输入要查询的单词:");
        label.setFont(new Font("宋体", Font.PLAIN, 15));
        pNorth.add(label, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        inputWord = new JTextField(30);
        inputWord.setFont(new Font("宋体", Font.PLAIN, 16));
        pNorth.add(inputWord, gbc);

        // 第二行：三个按钮
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        submit = new JButton("查 询 单 词");
        submit.setFont(new Font("宋体", Font.BOLD, 14));
        submit.setPreferredSize(new Dimension(130, 35));
        submit.setBackground(new Color(50, 100, 180));
        submit.setForeground(Color.WHITE);

        playVoice = new JButton("发  音");
        playVoice.setFont(new Font("宋体", Font.PLAIN, 14));
        playVoice.setPreferredSize(new Dimension(100, 35));
        playVoice.setEnabled(false);

        onlineQuery = new JButton("有道词典在线查询");
        onlineQuery.setFont(new Font("宋体", Font.PLAIN, 13));
        onlineQuery.setPreferredSize(new Dimension(160, 35));
        onlineQuery.setEnabled(false);
        onlineQuery.setBackground(new Color(30, 144, 255));
        onlineQuery.setForeground(Color.WHITE);

        btnPanel.add(submit);
        btnPanel.add(playVoice);
        btnPanel.add(onlineQuery);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2; gbc.weightx = 1;
        pNorth.add(btnPanel, gbc);

        add(pNorth, BorderLayout.NORTH);

        // 查询结果区域
        showWord = new JTextArea();
        showWord.setFont(new Font("宋体", Font.PLAIN, 18));
        showWord.setLineWrap(true);
        showWord.setWrapStyleWord(true);
        showWord.setEditable(false);
        showWord.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        add(new JScrollPane(showWord), BorderLayout.CENTER);
    }

    private void registerListener() {
        handleQueryOneWord = new HandleQueryOneWord();
        handleQueryOneWord.setView(this);
        submit.addActionListener(handleQueryOneWord);

        // 发音按钮事件（课设要求③）—— 优先播放有道词典在线发音，程序内直接播放
        playVoice.addActionListener(e -> {
            if (currentResult == null || currentResult.getEnglishWord() == null) return;
            String word = currentResult.getEnglishWord();

            // 先尝试有道词典在线美式发音（创新功能，程序内直接播放，不弹外部播放器）
            PlayMusic player = new PlayMusic();
            boolean onlineOk = player.playOnline(word, 2);
            if (onlineOk) {
                return;
            }

            // 在线发音失败，尝试本地文件
            if (currentResult.getVoice() != null && !currentResult.getVoice().trim().isEmpty()) {
                if (player.load(currentResult.getVoice().trim())) {
                    player.play();
                    return;
                }
            }

            // 都失败了，提示用户
            JOptionPane.showMessageDialog(this,
                    "在线发音启动失败，且未设置本地发音文件\n" +
                    "请检查网络连接，或点击\"有道词典在线查询\"在网页中听发音",
                    "发音提示", JOptionPane.WARNING_MESSAGE);
        });

        // 有道词典在线查询按钮事件（创新）
        onlineQuery.addActionListener(e -> {
            if (currentResult != null && currentResult.getEnglishWord() != null) {
                openInYoudao(currentResult.getEnglishWord());
            }
        });

        // 输入框回车也能查询
        inputWord.addActionListener(e -> submit.doClick());
    }

    /**
     * 用浏览器打开有道词典查询单词（创新功能）
     */
    private void openInYoudao(String word) {
        try {
            String url = "https://dict.youdao.com/w/" + word + "/";
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                JOptionPane.showMessageDialog(this,
                        "当前系统不支持打开浏览器\n请手动访问: " + url,
                        "提示", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "打开浏览器失败: " + ex.getMessage(),
                    "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
}
