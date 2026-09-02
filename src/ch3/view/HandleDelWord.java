package ch3.view;

import java.awt.event.*;
import ch3.data.*;

/**
 * 处理删除单词的事件类（书上原类，已扩展为按用户+单词删除）
 */
public class HandleDelWord implements ActionListener {
    DelWordView view;

    public void actionPerformed(ActionEvent e) {
        String englishWord = view.inputWord.getText().trim();
        if (englishWord.length() == 0) return;

        // 先查询单词获取id
        Word queryWord = new Word();
        queryWord.setOwner(view.currentUser);
        queryWord.setEnglishWord(englishWord);
        QueryOneWord query = new QueryOneWord();
        Word result = query.queryOneWord(queryWord);

        if (result == null) {
            view.hintMess.setText("删除失败，单词不在表里");
            return;
        }

        Word word = new Word();
        word.setId(result.getId());
        DelWord del = new DelWord();           // 负责删除单词对象
        int isOK = del.delWord(word);           // 删除单词

        if (isOK != 0) {
            view.hintMess.setText("删除单词成功");
            view.inputWord.setText("");
        } else {
            view.hintMess.setText("删除失败，单词不在表里");
        }
    }

    public void setView(DelWordView view) {
        this.view = view;
    }
}
