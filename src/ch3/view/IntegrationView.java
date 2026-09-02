package ch3.view;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import ch3.data.*;
import java.util.List;

public class IntegrationView extends JFrame {
    JTabbedPane tabbedPane;
    AddWordView addWordView;
    UpdateWordView updateWordView;
    DelWordView delWordView;
    QueryOneWordView oneWordView;
    QueryAllWordView queryAllWordView;
    RandomQueryView queryRandomView;
    FuzzyQueryView fuzzyQueryView;
    ReviewView reviewView;
    QuizView quizView;
    StatView statView;
    HomeworkView homeworkView;
    String currentUser;

    public IntegrationView(String user) {
        this.currentUser = user;
        setTitle("单词簿 - 当前用户: " + user);
        setBounds(100, 100, 1000, 650);
        setVisible(true);

        tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
        addWordView = new AddWordView(user);
        updateWordView = new UpdateWordView(user);
        delWordView = new DelWordView(user);
        oneWordView = new QueryOneWordView(user);
        queryAllWordView = new QueryAllWordView(user);
        queryRandomView = new RandomQueryView(user);
        fuzzyQueryView = new FuzzyQueryView(user);
        reviewView = new ReviewView(user);
        quizView = new QuizView(user);
        statView = new StatView(user);
        homeworkView = new HomeworkView(user);

        tabbedPane.add("添加单词", addWordView);
        tabbedPane.add("修改单词", updateWordView);
        tabbedPane.add("删除单词", delWordView);
        tabbedPane.add("查询一个单词", oneWordView);
        tabbedPane.add("浏览全部单词", queryAllWordView);
        tabbedPane.add("随机查看单词", queryRandomView);
        tabbedPane.add("模糊查询", fuzzyQueryView);
        tabbedPane.add("艾宾浩斯复习", reviewView);
        tabbedPane.add("随机测验", quizView);
        tabbedPane.add("学习统计", statView);
        tabbedPane.add("我的作业", homeworkView);

        tabbedPane.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                int index = tabbedPane.getSelectedIndex();
                if (index == 7) reviewView.refresh();
                else if (index == 8) quizView.refresh();
                else if (index == 9) statView.refresh();
                else if (index == 10) homeworkView.loadHomework();
            }
        });

        tabbedPane.validate();
        add(tabbedPane, BorderLayout.CENTER);
        validate();

        setJMenuBar(createMenuBar());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        checkHomeworkReminder();
    }

    private void checkHomeworkReminder() {
        HomeworkDB hwDB = new HomeworkDB();
        List<Homework> unfinished = hwDB.getUnfinishedHomework(currentUser);
        if (unfinished != null && !unfinished.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("你有 ").append(unfinished.size()).append(" 个未完成的作业：\n\n");
            java.sql.Date today = new java.sql.Date(System.currentTimeMillis());
            int overdueCount = 0;
            for (int i = 0; i < Math.min(unfinished.size(), 5); i++) {
                Homework hw = unfinished.get(i);
                sb.append(i + 1).append(". ").append(hw.getTitle());
                if (hw.getDeadline() != null) {
                    sb.append("（截止: ").append(hw.getDeadline()).append("）");
                    if (hw.getDeadline().before(today)) {
                        sb.append(" ⚠已逾期");
                        overdueCount++;
                    }
                }
                sb.append("\n");
            }
            if (unfinished.size() > 5) {
                sb.append("...还有 ").append(unfinished.size() - 5).append(" 个作业\n");
            }
            sb.append("\n请点击左侧\"我的作业\"查看详情，按时完成复习！");
            if (overdueCount > 0) {
                sb.append("\n\n注意：有 ").append(overdueCount).append(" 个作业已逾期，请尽快完成！");
            }
            JOptionPane.showMessageDialog(this, sb.toString(),
                    "作业提醒", JOptionPane.WARNING_MESSAGE);
        }
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("文件");
        JMenuItem importItem = new JMenuItem("导入CSV词库");
        JMenuItem exportItem = new JMenuItem("导出CSV词库");
        JMenuItem changePwdItem = new JMenuItem("修改密码");
        JMenuItem logoutItem = new JMenuItem("退出登录");
        JMenuItem exitItem = new JMenuItem("退出程序");

        importItem.addActionListener(e -> importCsv());
        exportItem.addActionListener(e -> exportCsv());
        changePwdItem.addActionListener(e -> changePassword());
        logoutItem.addActionListener(e -> logout());
        exitItem.addActionListener(e -> System.exit(0));

        fileMenu.add(importItem);
        fileMenu.add(exportItem);
        fileMenu.addSeparator();
        fileMenu.add(changePwdItem);
        fileMenu.addSeparator();
        fileMenu.add(logoutItem);
        fileMenu.add(exitItem);

        JMenu helpMenu = new JMenu("帮助");
        JMenuItem aboutItem = new JMenuItem("关于");
        aboutItem.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "单词簿课程设计\n\n默认管理员：admin / admin123\n测试用户：user / user123",
                "关于", JOptionPane.INFORMATION_MESSAGE));
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        return menuBar;
    }

    private void importCsv() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("选择CSV文件");
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            int count = CsvImportExport.importFromCsv(currentUser, chooser.getSelectedFile());
            JOptionPane.showMessageDialog(this, "成功导入 " + count + " 个单词");
        }
    }

    private void exportCsv() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("保存CSV文件");
        chooser.setSelectedFile(new java.io.File("wordbook_export.csv"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            int count = CsvImportExport.exportToCsv(currentUser, chooser.getSelectedFile());
            JOptionPane.showMessageDialog(this, "成功导出 " + count + " 个单词");
        }
    }

    private void changePassword() {
        String oldPwd = JOptionPane.showInputDialog(this, "请输入原密码:");
        if (oldPwd == null) return;
        String newPwd = JOptionPane.showInputDialog(this, "请输入新密码:");
        if (newPwd == null || newPwd.trim().isEmpty()) return;
        UserDB userDB = new UserDB();
        if (userDB.changePassword(currentUser, oldPwd, newPwd)) {
            JOptionPane.showMessageDialog(this, "密码修改成功");
        } else {
            JOptionPane.showMessageDialog(this, "原密码错误");
        }
    }

    private void logout() {
        dispose();
        LoginView login = new LoginView();
        login.setVisible(true);
    }
}
