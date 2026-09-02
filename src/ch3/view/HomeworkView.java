package ch3.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import ch3.data.*;

/**
 * 我的作业视图（创新功能：管理员布置复习作业，用户查看并完成）
 */
public class HomeworkView extends JPanel {
    String currentUser;
    JTable homeworkTable;
    DefaultTableModel tableModel;
    JButton refreshBtn;
    JButton completeBtn;
    JTextArea detailArea;

    public HomeworkView(String user) {
        this.currentUser = user;
        initView();
        loadHomework();
    }

    private void initView() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // 顶部标题
        JLabel titleLabel = new JLabel("我的作业 - 管理员布置的复习任务", SwingConstants.CENTER);
        titleLabel.setFont(new Font("宋体", Font.BOLD, 18));
        titleLabel.setForeground(new Color(50, 100, 180));
        add(titleLabel, BorderLayout.NORTH);

        // 中间：表格 + 详情
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        // 表格
        String[] cols = {"ID", "作业标题", "复习单词数", "截止日期", "状态", "布置时间"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        homeworkTable = new JTable(tableModel);
        homeworkTable.setFont(new Font("宋体", Font.PLAIN, 14));
        homeworkTable.setRowHeight(28);
        homeworkTable.getSelectionModel().addListSelectionListener(e -> showDetail());

        splitPane.setTopComponent(new JScrollPane(homeworkTable));

        // 详情区域
        detailArea = new JTextArea();
        detailArea.setFont(new Font("宋体", Font.PLAIN, 14));
        detailArea.setEditable(false);
        detailArea.setBorder(BorderFactory.createTitledBorder("作业详情"));
        splitPane.setBottomComponent(new JScrollPane(detailArea));
        splitPane.setDividerLocation(300);

        add(splitPane, BorderLayout.CENTER);

        // 底部按钮
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        refreshBtn = new JButton("刷新作业列表");
        refreshBtn.setFont(new Font("宋体", Font.PLAIN, 14));
        completeBtn = new JButton("标记为已完成");
        completeBtn.setFont(new Font("宋体", Font.BOLD, 14));
        completeBtn.setBackground(new Color(34, 139, 34));
        completeBtn.setForeground(Color.WHITE);

        refreshBtn.addActionListener(e -> loadHomework());
        completeBtn.addActionListener(e -> markComplete());

        btnPanel.add(refreshBtn);
        btnPanel.add(completeBtn);
        add(btnPanel, BorderLayout.SOUTH);
    }

    /** 加载作业列表 */
    public void loadHomework() {
        tableModel.setRowCount(0);
        HomeworkDB hwDB = new HomeworkDB();
        List<Homework> list = hwDB.getHomeworkByUser(currentUser);
        for (Homework hw : list) {
            tableModel.addRow(new Object[]{
                    hw.getId(),
                    hw.getTitle(),
                    hw.getWordCount(),
                    hw.getDeadline() != null ? hw.getDeadline().toString() : "-",
                    hw.getStatus(),
                    hw.getCreatedTime() != null ? hw.getCreatedTime().toString() : "-"
            });
        }
        detailArea.setText("");
    }

    /** 显示作业详情 */
    private void showDetail() {
        int row = homeworkTable.getSelectedRow();
        if (row < 0) return;
        int id = (Integer) tableModel.getValueAt(row, 0);
        String title = (String) tableModel.getValueAt(row, 1);
        String status = (String) tableModel.getValueAt(row, 4);

        HomeworkDB hwDB = new HomeworkDB();
        List<Homework> list = hwDB.getHomeworkByUser(currentUser);
        for (Homework hw : list) {
            if (hw.getId() == id) {
                StringBuilder sb = new StringBuilder();
                sb.append("作业标题: ").append(hw.getTitle()).append("\n\n");
                sb.append("作业内容:\n").append(hw.getContent() != null ? hw.getContent() : "无").append("\n\n");
                sb.append("需要复习单词数: ").append(hw.getWordCount()).append("个\n");
                sb.append("截止日期: ").append(hw.getDeadline() != null ? hw.getDeadline().toString() : "无").append("\n");
                sb.append("布置者: ").append(hw.getCreatedBy()).append("\n");
                sb.append("布置时间: ").append(hw.getCreatedTime() != null ? hw.getCreatedTime().toString() : "无").append("\n");
                sb.append("当前状态: ").append(hw.getStatus()).append("\n");

                // 检查是否逾期
                if ("未完成".equals(hw.getStatus()) && hw.getDeadline() != null) {
                    java.sql.Date today = new java.sql.Date(System.currentTimeMillis());
                    if (hw.getDeadline().before(today)) {
                        sb.append("\n⚠ 注意：该作业已逾期！请尽快完成复习。");
                    }
                }
                detailArea.setText(sb.toString());
                break;
            }
        }
    }

    /** 标记作业完成 */
    private void markComplete() {
        int row = homeworkTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "请先选择一个作业");
            return;
        }
        String status = (String) tableModel.getValueAt(row, 4);
        if ("已完成".equals(status)) {
            JOptionPane.showMessageDialog(this, "该作业已经完成了");
            return;
        }
        int id = (Integer) tableModel.getValueAt(row, 0);
        String title = (String) tableModel.getValueAt(row, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
                "确定将作业 '" + title + "' 标记为已完成吗？",
                "确认完成", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            HomeworkDB hwDB = new HomeworkDB();
            if (hwDB.markComplete(id)) {
                JOptionPane.showMessageDialog(this, "作业已标记为完成！继续加油！");
                loadHomework();
            } else {
                JOptionPane.showMessageDialog(this, "操作失败");
            }
        }
    }
}
