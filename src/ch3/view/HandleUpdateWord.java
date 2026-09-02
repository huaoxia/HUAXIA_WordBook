package ch3.view;

import java.awt.event.*;
import ch3.data.*;

/**
 * 处理更新单词的事件类（书上原类，已扩展）
 * 扩展：支持修改例句和发音文件
 */
public class HandleUpdateWord implements ActionListener {
    UpdateWordView view;

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == view.lookWord) {
            lookWord();
        } else if (e.getSource() == view.submit) {
            updateWord();
        }
    }

    private void updateWord() {
        String englishWord = view.inputWord.getText().trim();
        String meaning = view.inputNewMeaning.getText().trim();
        String sentence = view.inputNewSentence.getText().trim();
        String voice = view.inputNewVoice.getText().trim();

        if (englishWord.length() == 0) {
            view.hintMess.setText("请输入单词");
            return;
        }
        if (view.currentWord == null) {
            view.hintMess.setText("请先点击查看原有解释");
            return;
        }

        Word word = new Word();
        word.setId(view.currentWord.getId());
        word.setEnglishWord(englishWord);
        word.setMeaning(meaning.length() > 0 ? meaning : view.currentWord.getMeaning());
        word.setSentence(sentence.length() > 0 ? sentence : view.currentWord.getSentence());
        word.setVoice(voice.length() > 0 ? voice : view.currentWord.getVoice());

        UpdateWord update = new UpdateWord();  // 负责更新的对象
        int isOK = update.updateWord(word);     // 更新单词

        if (isOK != 0) {
            view.hintMess.setText("更新单词成功");
            view.inputWord.setText("");
            view.inputNewMeaning.setText("");
            view.inputNewSentence.setText("");
            view.inputNewVoice.setText("");
            view.currentWord = null;
        } else {
            view.hintMess.setText("更新失败，单词不在表里");
        }
    }

    private void lookWord() {
        String englishWord = view.inputWord.getText().trim();
        if (englishWord.length() == 0) {
            view.hintMess.setText("请输入单词");
            return;
        }
        Word word = new Word();
        word.setOwner(view.currentUser);
        word.setEnglishWord(englishWord);
        QueryOneWord query = new QueryOneWord();
        Word result = query.queryOneWord(word);

        if (result != null) {
            view.currentWord = result;
            view.inputNewMeaning.setText(result.getMeaning());
            view.inputNewSentence.setText(result.getSentence() != null ? result.getSentence() : "");
            view.inputNewVoice.setText(result.getVoice() != null ? result.getVoice() : "");
            view.hintMess.setText("查询成功，可修改后提交");
        } else {
            view.hintMess.setText("单词不在表里");
            view.currentWord = null;
        }
    }

    public void setView(UpdateWordView view) {
        this.view = view;
    }
}
