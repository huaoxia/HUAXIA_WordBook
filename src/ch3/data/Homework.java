package ch3.data;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * 作业实体类（创新功能：管理员布置复习作业）
 */
public class Homework {
    int id;
    String username;       // 所属用户
    String title;          // 作业标题
    String content;        // 作业内容
    int wordCount;         // 需要复习的单词数量
    Date deadline;         // 截止日期
    String createdBy;      // 布置者（管理员ID）
    Timestamp createdTime; // 布置时间
    String status;         // 状态：未完成 / 已完成

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public int getWordCount() { return wordCount; }
    public void setWordCount(int wordCount) { this.wordCount = wordCount; }

    public Date getDeadline() { return deadline; }
    public void setDeadline(Date deadline) { this.deadline = deadline; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public Timestamp getCreatedTime() { return createdTime; }
    public void setCreatedTime(Timestamp createdTime) { this.createdTime = createdTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
