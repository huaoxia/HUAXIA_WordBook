package ch3.view;

import java.awt.event.*;
import ch3.data.*;

/**
 * 处理添加单词的事件类（书上原类，已扩展）
 */
public class HandleAddWord implements ActionListener {
    AddWordView view;

    public void actionPerformed(ActionEvent e) {
        String englishWord = view.inputWord.getText().trim();
        String meaning = view.inputMeaning.getText().trim();
        String sentence = view.inputSentence.getText().trim();
        String voice = view.inputVoice.getText().trim();

        if (englishWord.length() == 0 || meaning.length() == 0) {
            view.hintMess.setText("单词和解释不能为空");
            return;
        }

        Word word = new Word();
        word.setOwner(view.currentUser);
        word.setEnglishWord(englishWord);
        word.setMeaning(meaning);
        word.setSentence(sentence);
        word.setVoice(voice);
        ReviewScheduler.initNewWord(word);

        AddWord addWord = new AddWord();          // 负责添加单词的对象
        int isOK = addWord.insertWord(word);       // 向数据库中的表添加单词

        if (isOK != 0) {
            view.hintMess.setText("添加单词成功");
            view.inputWord.setText("");
            view.inputMeaning.setText("");
            view.inputSentence.setText("");
            view.inputVoice.setText("");
        } else {
            view.hintMess.setText("添加单词失败，也许单词已经在表里");
        }
    }

    public void setView(AddWordView view) {
        this.view = view;
    }
}
