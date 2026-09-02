package ch3.view;

import java.awt.event.*;
import ch3.data.*;

/**
 * 处理随机查询单词的事件类（书上原类，已扩展）
 */
public class HandleRandomQueryWord implements ActionListener {
    RandomQueryView view;

    public void actionPerformed(ActionEvent e) {
        view.showWord.setText("");
        String n = view.inputQueryNumber.getText().trim();
        if (n.length() == 0) return;

        int count = 0;
        try {
            count = Integer.parseInt(n);
        } catch (NumberFormatException exp) {
            view.showWord.setText("请输入正整数");
            return;
        }

        RandomQueryWord random = new RandomQueryWord();  // 查询对象
        random.setOwner(view.currentUser);
        random.setCount(count);                            // 随机抽取count个单词
        Word[] result = random.randomQueryWord();          // 执行查询

        if (result == null || result.length == 0) {
            view.showWord.setText("词库为空");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("随机抽取 ").append(result.length).append(" 个单词:\n\n");
        for (int i = 0; i < result.length; i++) {
            int m = i + 1;
            sb.append(m).append(". ").append(result[i].getEnglishWord());
            sb.append("  ").append(result[i].getMeaning()).append("\n");
        }
        view.showWord.setText(sb.toString());
    }

    public void setView(RandomQueryView view) {
        this.view = view;
    }
}
