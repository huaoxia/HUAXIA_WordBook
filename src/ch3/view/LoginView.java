package ch3.view;

import javax.swing.*;
import java.awt.*;
import ch3.data.*;

public class LoginView extends JFrame {
    JTextField usernameField;
    JPasswordField passwordField;
    JButton loginBtn;
    JButton registerBtn;
    JButton adminBtn;
    String loggedInUser;

    public LoginView() {
        setTitle("单词簿 - 登录v3");
        setSize(420, 360);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // 主面板
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 40, 25, 40));
        mainPanel.setBackground(new Color(245, 248, 252));

        // 标题
        JLabel titleLabel = new JLabel("单 词 簿");
        titleLabel.setFont(new Font("宋体", Font.BOLD, 32));
        titleLabel.setForeground(new Color(50, 100, 180));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);

        mainPanel.add(Box.createVerticalStrut(5));

        // 副标题
        JLabel subLabel = new JLabel("英语单词记忆系统");
        subLabel.setFont(new Font("宋体", Font.PLAIN, 14));
        subLabel.setForeground(new Color(120, 120, 120));
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(subLabel);

        mainPanel.add(Box.createVerticalStrut(20));

        // 用户名行
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        row1.setBackground(new Color(245, 248, 252));
        JLabel l1 = new JLabel("用户名:");
        l1.setFont(new Font("宋体", Font.PLAIN, 15));
        usernameField = new JTextField(18);
        usernameField.setFont(new Font("宋体", Font.PLAIN, 14));
        row1.add(l1);
        row1.add(usernameField);
        mainPanel.add(row1);

        mainPanel.add(Box.createVerticalStrut(10));

        // 密码行
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        row2.setBackground(new Color(245, 248, 252));
        JLabel l2 = new JLabel("密  码:");
        l2.setFont(new Font("宋体", Font.PLAIN, 15));
        passwordField = new JPasswordField(18);
        passwordField.setFont(new Font("宋体", Font.PLAIN, 14));
        row2.add(l2);
        row2.add(passwordField);
        mainPanel.add(row2);

        mainPanel.add(Box.createVerticalStrut(20));

        // 按钮行 —— 登录和注册两个按钮并排
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        btnPanel.setBackground(new Color(245, 248, 252));

        loginBtn = new JButton("登 录");
        loginBtn.setFont(new Font("宋体", Font.BOLD, 14));
        loginBtn.setPreferredSize(new Dimension(100, 35));
        loginBtn.setBackground(new Color(50, 100, 180));
        loginBtn.setForeground(Color.WHITE);

        registerBtn = new JButton("注 册");
        registerBtn.setFont(new Font("宋体", Font.PLAIN, 14));
        registerBtn.setPreferredSize(new Dimension(100, 35));

        btnPanel.add(loginBtn);
        btnPanel.add(registerBtn);
        mainPanel.add(btnPanel);

        mainPanel.add(Box.createVerticalStrut(15));

        // 管理员入口
        adminBtn = new JButton("管理员入口 →");
        adminBtn.setFont(new Font("宋体", Font.PLAIN, 12));
        adminBtn.setBorderPainted(false);
        adminBtn.setContentAreaFilled(false);
        adminBtn.setForeground(new Color(100, 100, 100));
        adminBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(adminBtn);

        add(mainPanel);

        // 事件监听
        loginBtn.addActionListener(e -> doLogin());
        registerBtn.addActionListener(e -> showRegisterDialog());
        adminBtn.addActionListener(e -> openAdmin());
        passwordField.addActionListener(e -> doLogin());
    }

    /** 登录 */
    private void doLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "用户名和密码不能为空");
            return;
        }
        UserDB userDB = new UserDB();
        if (userDB.login(username, password)) {
            loggedInUser = username;
            dispose();
            IntegrationView main = new IntegrationView(username);
            main.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "用户名或密码错误");
        }
    }

    /** 弹出独立的注册窗口 */
    private void showRegisterDialog() {
        JDialog dialog = new JDialog(this, "用户注册", true);
        dialog.setSize(380, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));
        panel.setBackground(new Color(245, 248, 252));

        // 标题
        JLabel titleLabel = new JLabel("注 册 新 用 户");
        titleLabel.setFont(new Font("宋体", Font.BOLD, 22));
        titleLabel.setForeground(new Color(50, 100, 180));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);

        panel.add(Box.createVerticalStrut(20));

        // 用户名
        JPanel r1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        r1.setBackground(new Color(245, 248, 252));
        r1.add(new JLabel("用户名:"));
        JTextField regUsername = new JTextField(16);
        regUsername.setFont(new Font("宋体", Font.PLAIN, 14));
        r1.add(regUsername);
        panel.add(r1);

        panel.add(Box.createVerticalStrut(10));

        // 密码
        JPanel r2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        r2.setBackground(new Color(245, 248, 252));
        r2.add(new JLabel("密  码:"));
        JPasswordField regPassword = new JPasswordField(16);
        regPassword.setFont(new Font("宋体", Font.PLAIN, 14));
        r2.add(regPassword);
        panel.add(r2);

        panel.add(Box.createVerticalStrut(10));

        // 确认密码
        JPanel r3 = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        r3.setBackground(new Color(245, 248, 252));
        r3.add(new JLabel("确认密码:"));
        JPasswordField regConfirm = new JPasswordField(16);
        regConfirm.setFont(new Font("宋体", Font.PLAIN, 14));
        r3.add(regConfirm);
        panel.add(r3);

        panel.add(Box.createVerticalStrut(20));

        // 按钮
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        btnPanel.setBackground(new Color(245, 248, 252));

        JButton confirmBtn = new JButton("确认注册");
        confirmBtn.setFont(new Font("宋体", Font.BOLD, 14));
        confirmBtn.setPreferredSize(new Dimension(100, 35));
        confirmBtn.setBackground(new Color(50, 100, 180));
        confirmBtn.setForeground(Color.WHITE);

        JButton cancelBtn = new JButton("取消");
        cancelBtn.setFont(new Font("宋体", Font.PLAIN, 14));
        cancelBtn.setPreferredSize(new Dimension(100, 35));

        btnPanel.add(confirmBtn);
        btnPanel.add(cancelBtn);
        panel.add(btnPanel);

        dialog.add(panel);

        // 确认注册
        confirmBtn.addActionListener(e -> {
            String username = regUsername.getText().trim();
            String password = new String(regPassword.getPassword());
            String confirm = new String(regConfirm.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "用户名和密码不能为空");
                return;
            }
            if (username.length() < 2) {
                JOptionPane.showMessageDialog(dialog, "用户名至少2个字符");
                return;
            }
            if (password.length() < 4) {
                JOptionPane.showMessageDialog(dialog, "密码至少4个字符");
                return;
            }
            if (!password.equals(confirm)) {
                JOptionPane.showMessageDialog(dialog, "两次输入的密码不一致");
                return;
            }

            UserDB userDB = new UserDB();
            if (userDB.register(username, password)) {
                JOptionPane.showMessageDialog(dialog, "注册成功！请用新账号登录");
                dialog.dispose();
                usernameField.setText(username);
                passwordField.setText("");
            } else {
                JOptionPane.showMessageDialog(dialog, "用户名已存在，请换一个");
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }

    /** 管理员入口 */
    private void openAdmin() {
        String adminId = JOptionPane.showInputDialog(this, "请输入管理员ID:");
        if (adminId == null || adminId.trim().isEmpty()) return;
        String password = JOptionPane.showInputDialog(this, "请输入管理员密码:");
        if (password == null) return;
        AdminDB adminDB = new AdminDB();
        if (adminDB.login(adminId.trim(), password)) {
            dispose();
            ch3.admin.AdminWindow admin = new ch3.admin.AdminWindow(adminId.trim());
            admin.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "管理员ID或密码错误");
        }
    }
}
