package ch3.view;

import java.awt.event.*;
import ch3.data.*;

/**
 * 处理查询全部单词的事件类（书上原类，已扩展）
 */
public class HandleQueryAllWord implements ActionListener {
    QueryAllWordView view;

    public void actionPerformed(ActionEvent e) {
        view.showWord.setText("");
        QueryAllWord query = new QueryAllWord();   // 查询对象
        query.setOwner(view.currentUser);
        Word[] result = query.queryAllWord();        // 执行查询

        if (result == null || result.length == 0) {
            view.showWord.setText("词库为空，请先添加单词");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("共 ").append(result.length).append(" 个单词\n\n");
        for (int i = 0; i < result.length; i++) {
            int m = i + 1;
            sb.append(m).append(". ").append(result[i].getEnglishWord());
            sb.append("  ").append(result[i].getMeaning());
            if (result[i].getSentence() != null && !result[i].getSentence().isEmpty()) {
                sb.append("\n   例句: ").append(result[i].getSentence());
            }
            sb.append("  [强度:").append(result[i].getMemoryStrength()).append("]");
            sb.append("\n");
        }
        view.showWord.setText(sb.toString());
    }

    public void setView(QueryAllWordView view) {
        this.view = view;
    }
}
