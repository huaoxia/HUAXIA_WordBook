package ch3.view;

import javax.swing.*;
import java.sql.Connection;
import ch3.data.*;

/**
 * 删除单词视图（书上原类，未改动结构）
 */
public class DelWordView extends JPanel {
    JTextField inputWord;          // 输入要删除的单词
    JButton submit;                 // 提交按钮
    JTextField hintMess;            // 提示信息
    HandleDelWord handleDelWord;    // 负责处理删除单词
    String currentUser;

    DelWordView(String user) {
        this.currentUser = user;
        initView();
        registerListener();
    }

    private void initView() {
        inputWord = new JTextField(12);
        submit = new JButton("删除单词");
        hintMess = new JTextField(20);
        hintMess.setEditable(false);

        add(new JLabel("输入要删除的单词:"));
        add(inputWord);
        add(submit);
        add(new JLabel("提示:"));
        add(hintMess);
    }

    private void registerListener() {
        handleDelWord = new HandleDelWord();
        handleDelWord.setView(this);
        submit.addActionListener(handleDelWord);
    }
}
