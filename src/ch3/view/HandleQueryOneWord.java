package ch3.view;

import java.awt.event.*;
import ch3.data.*;

/**
 * 处理查询一个单词的事件类（书上原类，已扩展）
 * 扩展：显示例句、发音文件、记忆强度等完整信息
 * 创新：查询成功后启用有道词典在线查询按钮
 */
public class HandleQueryOneWord implements ActionListener {
    QueryOneWordView view;

    public void actionPerformed(ActionEvent e) {
        String englishWord = view.inputWord.getText().trim();
        if (englishWord.length() == 0) return;

        Word word = new Word();
        word.setOwner(view.currentUser);
        word.setEnglishWord(englishWord);
        QueryOneWord query = new QueryOneWord();   // 负责查询的对象
        Word result = query.queryOneWord(word);      // 执行查询操作

        if (result == null) {
            view.showWord.setText("未找到该单词\n\n提示：可点击\"有道词典在线查询\"在浏览器中查询");
            view.currentResult = null;
            view.playVoice.setEnabled(false);
            view.onlineQuery.setEnabled(false);
            return;
        }

        view.currentResult = result;
        view.playVoice.setEnabled(true);  // 在线发音随时可用，无需本地文件
        view.onlineQuery.setEnabled(true);  // 查询成功，启用有道词典在线查询

        StringBuilder sb = new StringBuilder();
        sb.append("单词: ").append(result.getEnglishWord()).append("\n\n");
        sb.append("释义: ").append(result.getMeaning()).append("\n\n");
        if (result.getSentence() != null && !result.getSentence().isEmpty()) {
            sb.append("例句: ").append(result.getSentence()).append("\n");
            if (result.getSentenceCn() != null && !result.getSentenceCn().isEmpty()) {
                sb.append("翻译: ").append(result.getSentenceCn()).append("\n");
            }
            sb.append("\n");
        }
        if (result.getVoice() != null && !result.getVoice().isEmpty()) {
            sb.append("发音文件: ").append(result.getVoice()).append("\n\n");
        }
        sb.append("记忆强度: ").append(result.getMemoryStrength())
          .append(" (").append(ReviewScheduler.getStrengthLabel(result.getMemoryStrength())).append(")\n");
        sb.append("复习次数: ").append(result.getReviewCount()).append("\n");
        if (result.getNextReview() != null) {
            sb.append("下次复习: ").append(result.getNextReview().toString()).append("\n");
        }
        sb.append("\n点击\"有道词典在线查询\"可在浏览器中查看详细释义和发音");
        view.showWord.setText(sb.toString());
    }

    public void setView(QueryOneWordView view) {
        this.view = view;
    }
}
