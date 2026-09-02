package ch3.view;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import ch3.data.*;

/**
 * 查询全部单词视图（书上原类，已扩展）
 * 扩展：显示例句、发音文件、记忆强度等完整信息
 */
public class QueryAllWordView extends JPanel {
    JButton submit;                     // 查询按钮
    JTextArea showWord;                 // 显示查询结果
    HandleQueryAllWord handleQueryAllWord;  // 负责处理查询全部单词
    String currentUser;

    QueryAllWordView(String user) {
        this.currentUser = user;
        initView();
        registerListener();
    }

    public void initView() {
        setLayout(new BorderLayout());
        submit = new JButton("查询全部单词");
        showWord = new JTextArea();
        showWord.setFont(new Font("宋体", Font.BOLD, 20));
        add(submit, BorderLayout.NORTH);
        add(new JScrollPane(showWord), BorderLayout.CENTER);
    }

    private void registerListener() {
        handleQueryAllWord = new HandleQueryAllWord();
        handleQueryAllWord.setView(this);
        submit.addActionListener(handleQueryAllWord);
    }
}
