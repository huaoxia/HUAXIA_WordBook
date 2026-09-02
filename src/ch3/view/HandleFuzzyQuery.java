package ch3.view;

import java.awt.event.*;
import ch3.data.*;

/**
 * 模糊查询事件处理类（课设要求④，新增类）
 */
public class HandleFuzzyQuery implements ActionListener {
    FuzzyQueryView view;

    public void actionPerformed(ActionEvent e) {
        String keyword = view.inputKeyword.getText().trim();
        if (keyword.length() == 0) {
            view.showWord.setText("请输入关键词");
            return;
        }

        int typeIndex = view.queryType.getSelectedIndex();
        String type;
        String typeName;
        switch (typeIndex) {
            case 0:
                type = "prefix";
                typeName = "前缀";
                break;
            case 1:
                type = "suffix";
                typeName = "后缀";
                break;
            default:
                type = "contains";
                typeName = "包含";
                break;
        }

        FuzzyQueryWord fuzzy = new FuzzyQueryWord();
        fuzzy.setOwner(view.currentUser);
        Word[] result = fuzzy.fuzzyQuery(keyword, type);

        if (result == null || result.length == 0) {
            view.showWord.setText("未找到" + typeName + "为 '" + keyword + "' 的单词");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(typeName).append("为 '").append(keyword).append("' 的单词共 ")
          .append(result.length).append(" 个:\n\n");
        for (int i = 0; i < result.length; i++) {
            int m = i + 1;
            sb.append(m).append(". ").append(result[i].getEnglishWord());
            sb.append("  ").append(result[i].getMeaning());
            if (result[i].getSentence() != null && !result[i].getSentence().isEmpty()) {
                sb.append("\n   例句: ").append(result[i].getSentence());
            }
            sb.append("\n");
        }
        view.showWord.setText(sb.toString());
    }

    public void setView(FuzzyQueryView view) {
        this.view = view;
    }
}
