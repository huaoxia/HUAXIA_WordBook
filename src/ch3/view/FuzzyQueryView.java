package ch3.view;

import javax.swing.*;
import java.awt.*;
import ch3.data.*;

/**
 * 模糊查询视图（课设要求④，新增类）
 * 支持三种查询模式：前缀查询、后缀查询、包含查询
 */
public class FuzzyQueryView extends JPanel {
    JTextField inputKeyword;           // 输入关键词
    JComboBox<String> queryType;       // 查询类型选择
    JButton submit;                     // 查询按钮
    JTextArea showWord;                 // 显示查询结果
    HandleFuzzyQuery handleFuzzyQuery; // 事件处理
    String currentUser;

    FuzzyQueryView(String user) {
        this.currentUser = user;
        initView();
        registerListener();
    }

    private void initView() {
        setLayout(new BorderLayout());
        JPanel pNorth = new JPanel();

        inputKeyword = new JTextField(15);
        String[] types = {"前缀查询(开头)", "后缀查询(结尾)", "包含查询"};
        queryType = new JComboBox<>(types);
        submit = new JButton("模糊查询");
        showWord = new JTextArea();
        showWord.setFont(new Font("宋体", Font.BOLD, 18));

        pNorth.add(new JLabel("关键词:"));
        pNorth.add(inputKeyword);
        pNorth.add(queryType);
        pNorth.add(submit);

        add(pNorth, BorderLayout.NORTH);
        add(new JScrollPane(showWord), BorderLayout.CENTER);
    }

    private void registerListener() {
        handleFuzzyQuery = new HandleFuzzyQuery();
        handleFuzzyQuery.setView(this);
        submit.addActionListener(handleFuzzyQuery);
    }
}
