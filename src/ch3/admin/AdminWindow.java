package ch3.admin;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import ch3.data.*;

/**
 * 管理员独立程序（创新功能）
 * 管理员功能：
 *   1. 用户管理：查看用户列表、删除用户、重置密码、清空单词本、布置作业、未掌握单词汇总
 *   2. 单词管理：搜索单词、查看全部单词、删除单词
 *   3. 系统总览：总用户数、总单词数、总复习次数等统计
 *   4. 退出登录：回到登录界面
 */
public class AdminWindow extends JFrame {
    String adminId;
    JTable userTable;
    JTable wordTable;
    DefaultTableModel userModel;
    DefaultTableModel wordModel;
    JTextField searchWordField;
    JLabel totalUsersLabel;
    JLabel totalWordsLabel;
    JLabel totalReviewsLabel;
    JLabel totalQuizLabel;
    JTextArea statsArea;

    public AdminWindow(String adminId) {
        this.adminId = adminId;
        setTitle("单词簿 - 管理员控制台 (" + adminId + ")");
        setSize(950, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 顶部面板：标题 + 退出登录按钮
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        topPanel.setBackground(new Color(245, 248, 252));

        JLabel titleLabel = new JLabel("管理员控制台 - 只能删除/重置，不能修改用户单词", SwingConstants.CENTER);
        titleLabel.setFont(new Font("宋体", Font.BOLD, 18));
        titleLabel.setForeground(new Color(180, 50, 50));
        topPanel.add(titleLabel, BorderLayout.CENTER);

        JButton logoutBtn = new JButton("退出登录");
        logoutBtn.setFont(new Font("宋体", Font.PLAIN, 13));
        logoutBtn.setPreferredSize(new Dimension(100, 30));
        logoutBtn.setBackground(new Color(180, 50, 50));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "确定要退出登录吗？", "确认退出", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                ch3.view.LoginView login = new ch3.view.LoginView();
                login.setVisible(true);
            }
        });
        topPanel.add(logoutBtn, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // 选项卡
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("宋体", Font.PLAIN, 14));

        // ===== 用户管理面板 =====
        JPanel userPanel = new JPanel(new BorderLayout());
        String[] userCols = {"用户名", "单词数量", "复习次数"};
        userModel = new DefaultTableModel(userCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        userTable = new JTable(userModel);
        userTable.setFont(new Font("宋体", Font.PLAIN, 14));
        userTable.setRowHeight(28);

        JPanel userBtnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton refreshUserBtn = new JButton("刷新列表");
        JButton viewDetailBtn = new JButton("查看学习详情");
        JButton resetPwdBtn = new JButton("重置密码");
        resetPwdBtn.setBackground(new Color(255, 165, 0));
        resetPwdBtn.setForeground(Color.WHITE);
        JButton clearWordsBtn = new JButton("清空单词本");
        clearWordsBtn.setBackground(new Color(255, 140, 0));
        clearWordsBtn.setForeground(Color.WHITE);
        JButton assignHomeworkBtn = new JButton("布置作业");
        assignHomeworkBtn.setBackground(new Color(70, 130, 180));
        assignHomeworkBtn.setForeground(Color.WHITE);
        JButton weakWordsBtn = new JButton("未掌握单词汇总");
        weakWordsBtn.setBackground(new Color(138, 43, 226));
        weakWordsBtn.setForeground(Color.WHITE);
        JButton deleteUserBtn = new JButton("删除用户");
        deleteUserBtn.setBackground(new Color(220, 80, 80));
        deleteUserBtn.setForeground(Color.WHITE);

        refreshUserBtn.addActionListener(e -> loadUsers());
        viewDetailBtn.addActionListener(e -> viewUserDetail());
        resetPwdBtn.addActionListener(e -> resetUserPassword());
        clearWordsBtn.addActionListener(e -> clearUserWords());
        assignHomeworkBtn.addActionListener(e -> assignHomework());
        weakWordsBtn.addActionListener(e -> showWeakWords());
        deleteUserBtn.addActionListener(e -> deleteSelectedUser());

        userBtnPanel.add(refreshUserBtn);
        userBtnPanel.add(viewDetailBtn);
        userBtnPanel.add(resetPwdBtn);
        userBtnPanel.add(clearWordsBtn);
        userBtnPanel.add(assignHomeworkBtn);
        userBtnPanel.add(weakWordsBtn);
        userBtnPanel.add(deleteUserBtn);

        userPanel.add(new JScrollPane(userTable), BorderLayout.CENTER);
        userPanel.add(userBtnPanel, BorderLayout.SOUTH);

        // ===== 单词管理面板 =====
        JPanel wordPanel = new JPanel(new BorderLayout());
        String[] wordCols = {"ID", "所属用户", "单词", "释义", "记忆强度", "复习次数"};
        wordModel = new DefaultTableModel(wordCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        wordTable = new JTable(wordModel);
        wordTable.setFont(new Font("宋体", Font.PLAIN, 14));
        wordTable.setRowHeight(28);

        JPanel wordTopPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        searchWordField = new JTextField(15);
        JButton searchBtn = new JButton("搜索单词");
        JButton refreshWordBtn = new JButton("刷新全部");
        JButton deleteWordBtn = new JButton("删除选中单词");
        deleteWordBtn.setBackground(new Color(220, 80, 80));
        deleteWordBtn.setForeground(Color.WHITE);

        searchBtn.addActionListener(e -> searchWords());
        refreshWordBtn.addActionListener(e -> loadAllWords());
        deleteWordBtn.addActionListener(e -> deleteSelectedWord());

        wordTopPanel.add(new JLabel("搜索:"));
        wordTopPanel.add(searchWordField);
        wordTopPanel.add(searchBtn);
        wordTopPanel.add(refreshWordBtn);
        wordTopPanel.add(deleteWordBtn);

        wordPanel.add(wordTopPanel, BorderLayout.NORTH);
        wordPanel.add(new JScrollPane(wordTable), BorderLayout.CENTER);

        // ===== 系统总览面板 =====
        JPanel statsPanel = new JPanel(new BorderLayout(10, 10));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JPanel cardsPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        totalUsersLabel = createStatCard("总用户数", "0", new Color(50, 100, 180));
        totalWordsLabel = createStatCard("总单词数", "0", new Color(34, 139, 34));
        totalReviewsLabel = createStatCard("总复习次数", "0", new Color(255, 140, 0));
        totalQuizLabel = createStatCard("测验答题数", "0", new Color(138, 43, 226));
        cardsPanel.add(totalUsersLabel);
        cardsPanel.add(totalWordsLabel);
        cardsPanel.add(totalReviewsLabel);
        cardsPanel.add(totalQuizLabel);
        statsPanel.add(cardsPanel, BorderLayout.NORTH);

        statsArea = new JTextArea();
        statsArea.setFont(new Font("宋体", Font.PLAIN, 14));
        statsArea.setEditable(false);
        statsArea.setBorder(BorderFactory.createTitledBorder("各用户详细统计"));
        statsPanel.add(new JScrollPane(statsArea), BorderLayout.CENTER);

        JPanel statsBtnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton refreshStatsBtn = new JButton("刷新统计");
        refreshStatsBtn.setFont(new Font("宋体", Font.BOLD, 14));
        refreshStatsBtn.setPreferredSize(new Dimension(120, 35));
        refreshStatsBtn.addActionListener(e -> loadSystemStats());
        statsBtnPanel.add(refreshStatsBtn);
        statsPanel.add(statsBtnPanel, BorderLayout.SOUTH);

        tabbedPane.add("用户管理", userPanel);
        tabbedPane.add("单词管理", wordPanel);
        tabbedPane.add("系统总览", statsPanel);
        add(tabbedPane, BorderLayout.CENTER);

        // 底部：修改管理员密码
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton changePwdBtn = new JButton("修改管理员密码");
        changePwdBtn.addActionListener(e -> changeAdminPassword());
        bottomPanel.add(changePwdBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        // 初始加载
        loadUsers();
        loadAllWords();
        loadSystemStats();
    }

    private JLabel createStatCard(String title, String value, Color color) {
        JLabel label = new JLabel("<html><div style='text-align:center;'>"
                + "<span style='font-size:14px;color:gray;'>" + title + "</span><br>"
                + "<span style='font-size:32px;font-weight:bold;color:"
                + String.format("rgb(%d,%d,%d)", color.getRed(), color.getGreen(), color.getBlue())
                + ";'>" + value + "</span></div></html>");
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2),
                BorderFactory.createEmptyBorder(15, 10, 15, 10)));
        label.setOpaque(true);
        label.setBackground(new Color(250, 250, 250));
        return label;
    }

    private void updateStatCard(JLabel label, String title, int value, Color color) {
        label.setText("<html><div style='text-align:center;'>"
                + "<span style='font-size:14px;color:gray;'>" + title + "</span><br>"
                + "<span style='font-size:32px;font-weight:bold;color:"
                + String.format("rgb(%d,%d,%d)", color.getRed(), color.getGreen(), color.getBlue())
                + ";'>" + value + "</span></div></html>");
    }

    private void loadUsers() {
        userModel.setRowCount(0);
        UserDB userDB = new UserDB();
        List<String> users = userDB.getAllUsers();
        QueryAllWord query = new QueryAllWord();
        for (String username : users) {
            query.setOwner(username);
            Word[] words = query.queryAllWord();
            int wordCount = words != null ? words.length : 0;
            int reviewCount = 0;
            if (words != null) {
                for (Word w : words) {
                    reviewCount += w.getReviewCount();
                }
            }
            userModel.addRow(new Object[]{username, wordCount, reviewCount});
        }
    }

    private void viewUserDetail() {
        int row = userTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "请先选择一个用户");
            return;
        }
        String username = (String) userModel.getValueAt(row, 0);
        QueryAllWord query = new QueryAllWord();
        query.setOwner(username);
        Word[] words = query.queryAllWord();
        int total = words != null ? words.length : 0;
        int mastered = 0, learning = 0, newWords = 0;
        int totalReviews = 0;
        if (words != null) {
            for (Word w : words) {
                totalReviews += w.getReviewCount();
                if (w.getMemoryStrength() >= 80) mastered++;
                else if (w.getMemoryStrength() >= 30) learning++;
                else newWords++;
            }
        }
        String detail = "用户 '" + username + "' 学习详情\n"
                + "============================\n"
                + "单词总数: " + total + "\n"
                + "已掌握 (强度≥80): " + mastered + "\n"
                + "学习中 (强度30-79): " + learning + "\n"
                + "新词 (强度<30): " + newWords + "\n"
                + "总复习次数: " + totalReviews + "\n";
        JTextArea area = new JTextArea(detail);
        area.setFont(new Font("宋体", Font.PLAIN, 14));
        area.setEditable(false);
        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(350, 280));
        JOptionPane.showMessageDialog(this, scroll, "学习详情", JOptionPane.INFORMATION_MESSAGE);
    }

    private void resetUserPassword() {
        int row = userTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "请先选择一个用户");
            return;
        }
        String username = (String) userModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "确定将用户 '" + username + "' 的密码重置为 123456 吗？",
                "确认重置密码", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            UserDB userDB = new UserDB();
            if (userDB.resetPassword(username, "123456")) {
                JOptionPane.showMessageDialog(this,
                        "密码重置成功！\n用户 '" + username + "' 的新密码为: 123456");
            } else {
                JOptionPane.showMessageDialog(this, "重置失败");
            }
        }
    }

    private void assignHomework() {
        int row = userTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "请先选择一个用户");
            return;
        }
        String username = (String) userModel.getValueAt(row, 0);
        JTextField titleField = new JTextField(20);
        JTextArea contentArea = new JTextArea(5, 20);
        JTextField wordCountField = new JTextField("20", 5);
        JTextField deadlineField = new JTextField("2026-09-07", 10);
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("作业标题:"), gbc);
        gbc.gridx = 1;
        panel.add(titleField, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("作业内容:"), gbc);
        gbc.gridx = 1;
        panel.add(new JScrollPane(contentArea), gbc);
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("复习单词数:"), gbc);
        gbc.gridx = 1;
        panel.add(wordCountField, gbc);
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("截止日期:"), gbc);
        gbc.gridx = 1;
        panel.add(deadlineField, gbc);
        gbc.gridx = 1; gbc.gridy = 4;
        panel.add(new JLabel("格式: 2026-09-07"), gbc);
        int result = JOptionPane.showConfirmDialog(this, panel,
                "给用户 '" + username + "' 布置作业", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String title = titleField.getText().trim();
            String content = contentArea.getText().trim();
            if (title.isEmpty()) {
                JOptionPane.showMessageDialog(this, "作业标题不能为空");
                return;
            }
            try {
                int wordCount = Integer.parseInt(wordCountField.getText().trim());
                java.sql.Date deadline = java.sql.Date.valueOf(deadlineField.getText().trim());
                HomeworkDB hwDB = new HomeworkDB();
                if (hwDB.assignHomework(username, title, content, wordCount, deadline, adminId)) {
                    JOptionPane.showMessageDialog(this, "作业布置成功！\n用户登录后会收到提醒");
                } else {
                    JOptionPane.showMessageDialog(this, "布置失败");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "输入格式错误:\n" + e.getMessage());
            }
        }
    }

    private void showWeakWords() {
        int row = userTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "请先选择一个用户");
            return;
        }
        String username = (String) userModel.getValueAt(row, 0);
        QueryAllWord query = new QueryAllWord();
        query.setOwner(username);
        Word[] allWords = query.queryAllWord();
        java.util.List<Word> weakList = new java.util.ArrayList<>();
        if (allWords != null) {
            for (Word w : allWords) {
                if (w.getMemoryStrength() < 60) {
                    weakList.add(w);
                }
            }
        }
        if (weakList.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "用户 '" + username + "' 没有未掌握单词（记忆强度均≥60）\n继续保持！");
            return;
        }
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel infoLabel = new JLabel("用户 '" + username + "' 的未掌握单词（记忆强度<60）共 "
                + weakList.size() + " 个，勾选后布置为复习作业:");
        infoLabel.setFont(new Font("宋体", Font.PLAIN, 13));
        panel.add(infoLabel, BorderLayout.NORTH);
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        JCheckBox[] checkBoxes = new JCheckBox[weakList.size()];
        for (int i = 0; i < weakList.size(); i++) {
            Word w = weakList.get(i);
            checkBoxes[i] = new JCheckBox(w.getEnglishWord() + "  ——  " + w.getMeaning()
                    + "  (强度:" + w.getMemoryStrength() + ")");
            checkBoxes[i].setFont(new Font("宋体", Font.PLAIN, 13));
            checkBoxes[i].setSelected(true);
            listPanel.add(checkBoxes[i]);
        }
        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setPreferredSize(new Dimension(500, 350));
        panel.add(scrollPane, BorderLayout.CENTER);
        JPanel ctrlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton selectAllBtn = new JButton("全选");
        JButton selectNoneBtn = new JButton("全不选");
        selectAllBtn.addActionListener(e -> {
            for (JCheckBox cb : checkBoxes) cb.setSelected(true);
        });
        selectNoneBtn.addActionListener(e -> {
            for (JCheckBox cb : checkBoxes) cb.setSelected(false);
        });
        ctrlPanel.add(selectAllBtn);
        ctrlPanel.add(selectNoneBtn);
        panel.add(ctrlPanel, BorderLayout.SOUTH);
        int result = JOptionPane.showConfirmDialog(this, panel,
                "未掌握单词汇总 - " + username, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            java.util.List<String> selected = new java.util.ArrayList<>();
            for (int i = 0; i < checkBoxes.length; i++) {
                if (checkBoxes[i].isSelected()) {
                    selected.add(weakList.get(i).getEnglishWord());
                }
            }
            if (selected.isEmpty()) {
                JOptionPane.showMessageDialog(this, "请至少勾选一个单词");
                return;
            }
            StringBuilder content = new StringBuilder();
            content.append("请重点复习以下").append(selected.size()).append("个未掌握单词：\n\n");
            for (int i = 0; i < selected.size(); i++) {
                content.append(i + 1).append(". ").append(selected.get(i));
                for (Word w : weakList) {
                    if (w.getEnglishWord().equals(selected.get(i))) {
                        content.append(" —— ").append(w.getMeaning());
                        break;
                    }
                }
                content.append("\n");
            }
            content.append("\n建议：结合艾宾浩斯复习功能，反复记忆直到掌握！");
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.add(java.util.Calendar.DAY_OF_MONTH, 7);
            java.sql.Date deadline = new java.sql.Date(cal.getTimeInMillis());
            HomeworkDB hwDB = new HomeworkDB();
            String title = "未掌握单词强化复习（" + selected.size() + "词）";
            if (hwDB.assignHomework(username, title, content.toString(), selected.size(), deadline, adminId)) {
                JOptionPane.showMessageDialog(this,
                        "作业布置成功！\n\n" +
                        "用户: " + username + "\n" +
                        "标题: " + title + "\n" +
                        "单词数: " + selected.size() + "\n" +
                        "截止日期: " + deadline + "\n\n" +
                        "用户登录后会收到提醒。");
            } else {
                JOptionPane.showMessageDialog(this, "布置失败");
            }
        }
    }

    private void clearUserWords() {
        int row = userTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "请先选择一个用户");
            return;
        }
        String username = (String) userModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "确定清空用户 '" + username + "' 的所有单词吗？\n此操作不可恢复！",
                "确认清空", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            DelWord del = new DelWord();
            int n = del.delByOwner(username);
            JOptionPane.showMessageDialog(this, "已清空 " + n + " 个单词");
            loadUsers();
            loadAllWords();
            loadSystemStats();
        }
    }

    private void deleteSelectedUser() {
        int row = userTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "请先选择要删除的用户");
            return;
        }
        String username = (String) userModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "确定删除用户 '" + username + "' 吗？\n该用户的所有单词和学习记录也会被删除！",
                "确认删除", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            UserDB userDB = new UserDB();
            if (userDB.deleteUser(username)) {
                JOptionPane.showMessageDialog(this, "用户删除成功");
                loadUsers();
                loadAllWords();
                loadSystemStats();
            } else {
                JOptionPane.showMessageDialog(this, "删除失败");
            }
        }
    }

    private void loadAllWords() {
        wordModel.setRowCount(0);
        QueryAllWord query = new QueryAllWord();
        query.setOwner(null);
        Word[] words = query.queryAllWord();
        if (words != null) {
            for (Word w : words) {
                wordModel.addRow(new Object[]{
                        w.getId(), w.getOwner(), w.getEnglishWord(),
                        w.getMeaning(), w.getMemoryStrength(), w.getReviewCount()
                });
            }
        }
    }

    private void searchWords() {
        String keyword = searchWordField.getText().trim();
        if (keyword.isEmpty()) {
            loadAllWords();
            return;
        }
        wordModel.setRowCount(0);
        UserDB userDB = new UserDB();
        List<String> users = userDB.getAllUsers();
        FuzzyQueryWord fuzzy = new FuzzyQueryWord();
        for (String user : users) {
            fuzzy.setOwner(user);
            Word[] result = fuzzy.fuzzyQuery(keyword, "contains");
            if (result != null) {
                for (Word w : result) {
                    wordModel.addRow(new Object[]{
                            w.getId(), w.getOwner(), w.getEnglishWord(),
                            w.getMeaning(), w.getMemoryStrength(), w.getReviewCount()
                    });
                }
            }
        }
    }

    private void deleteSelectedWord() {
        int row = wordTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "请先选择要删除的单词");
            return;
        }
        int id = (Integer) wordModel.getValueAt(row, 0);
        String wordText = (String) wordModel.getValueAt(row, 2);
        int confirm = JOptionPane.showConfirmDialog(this,
                "确定删除单词 '" + wordText + "' 吗？",
                "确认删除", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            DelWord del = new DelWord();
            if (del.delWordById(id) > 0) {
                JOptionPane.showMessageDialog(this, "单词删除成功");
                loadAllWords();
                loadUsers();
                loadSystemStats();
            } else {
                JOptionPane.showMessageDialog(this, "删除失败");
            }
        }
    }

    private void loadSystemStats() {
        UserDB userDB = new UserDB();
        List<String> users = userDB.getAllUsers();
        int totalUsers = users.size();
        int totalWords = 0;
        int totalReviews = 0;
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-15s %-10s %-10s %-10s%n", "用户名", "单词数", "复习次数", "掌握率"));
        sb.append("------------------------------------------------------------\n");
        QueryAllWord query = new QueryAllWord();
        for (String username : users) {
            query.setOwner(username);
            Word[] words = query.queryAllWord();
            int wordCount = words != null ? words.length : 0;
            int reviewCount = 0;
            int mastered = 0;
            if (words != null) {
                for (Word w : words) {
                    reviewCount += w.getReviewCount();
                    if (w.getMemoryStrength() >= 80) mastered++;
                }
            }
            totalWords += wordCount;
            totalReviews += reviewCount;
            String rate = wordCount > 0 ? String.format("%.1f%%", (double) mastered / wordCount * 100) : "0%";
            sb.append(String.format("%-15s %-10d %-10d %-10s%n", username, wordCount, reviewCount, rate));
        }
        updateStatCard(totalUsersLabel, "总用户数", totalUsers, new Color(50, 100, 180));
        updateStatCard(totalWordsLabel, "总单词数", totalWords, new Color(34, 139, 34));
        updateStatCard(totalReviewsLabel, "总复习次数", totalReviews, new Color(255, 140, 0));
        updateStatCard(totalQuizLabel, "测验答题数", 0, new Color(138, 43, 226));
        statsArea.setText(sb.toString());
    }

    private void changeAdminPassword() {
        String oldPwd = JOptionPane.showInputDialog(this, "请输入原密码:");
        if (oldPwd == null) return;
        String newPwd = JOptionPane.showInputDialog(this, "请输入新密码:");
        if (newPwd == null || newPwd.trim().isEmpty()) return;
        AdminDB adminDB = new AdminDB();
        if (adminDB.changePassword(adminId, oldPwd, newPwd)) {
            JOptionPane.showMessageDialog(this, "密码修改成功");
        } else {
            JOptionPane.showMessageDialog(this, "原密码错误");
        }
    }
}
