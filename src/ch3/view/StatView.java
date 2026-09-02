package ch3.view;

import javax.swing.*;
import java.awt.*;
import ch3.data.*;

/**
 * 学习统计可视化视图（创新功能）
 * 自绘柱状图展示最近7天的学习数据（新增单词数、复习单词数）
 * 展示总单词数、已掌握数、待复习数等关键指标
 */
public class StatView extends JPanel {
    String currentUser;
    StatChartPanel chartPanel;

    StatView(String user) {
        this.currentUser = user;
        initView();
    }

    private void initView() {
        setLayout(new BorderLayout());

        // 顶部统计卡片
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 10));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        StatCard totalCard = new StatCard("单词总数", new Color(70, 130, 180));
        StatCard masteredCard = new StatCard("已掌握", new Color(60, 170, 90));
        StatCard reviewCard = new StatCard("待复习", new Color(255, 150, 50));
        StatCard rateCard = new StatCard("掌握率", new Color(150, 80, 180));

        statsPanel.add(totalCard);
        statsPanel.add(masteredCard);
        statsPanel.add(reviewCard);
        statsPanel.add(rateCard);

        add(statsPanel, BorderLayout.NORTH);

        // 中间图表
        chartPanel = new StatChartPanel();
        chartPanel.setBorder(BorderFactory.createTitledBorder("最近7天学习数据"));
        add(chartPanel, BorderLayout.CENTER);

        refresh();
    }

    /** 刷新统计数据 */
    public void refresh() {
        StatDB stat = new StatDB();
        stat.setOwner(currentUser);

        int total = stat.getTotalWords();
        int mastered = stat.getMasteredWords();
        int review = stat.getReviewCount();
        int rate = total > 0 ? mastered * 100 / total : 0;

        // 更新统计卡片
        Container parent = getParent();
        if (parent != null) {
            Component[] comps = ((JPanel) ((BorderLayout) getLayout()).getLayoutComponent(BorderLayout.NORTH)).getComponents();
            if (comps.length >= 4) {
                ((StatCard) comps[0]).setValue(total);
                ((StatCard) comps[1]).setValue(mastered);
                ((StatCard) comps[2]).setValue(review);
                ((StatCard) comps[3]).setValue(rate + "%");
            }
        }

        // 更新图表
        int[][] data = stat.getRecentStudyData(7);
        String[] labels = StatDB.getRecentDateLabels(7);
        chartPanel.setData(data, labels);
        chartPanel.repaint();
    }

    /** 统计卡片内部类 */
    class StatCard extends JPanel {
        JLabel valueLabel;
        String title;
        Color color;

        StatCard(String title, Color color) {
            this.title = title;
            this.color = color;
            setLayout(new BorderLayout());
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createLineBorder(color, 2));

            JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
            titleLabel.setFont(new Font("宋体", Font.PLAIN, 14));
            titleLabel.setForeground(color);

            valueLabel = new JLabel("0", SwingConstants.CENTER);
            valueLabel.setFont(new Font("Arial", Font.BOLD, 32));
            valueLabel.setForeground(color);

            add(titleLabel, BorderLayout.NORTH);
            add(valueLabel, BorderLayout.CENTER);
        }

        void setValue(int value) {
            valueLabel.setText(String.valueOf(value));
        }

        void setValue(String value) {
            valueLabel.setText(value);
        }
    }

    /** 自绘图表面板 */
    class StatChartPanel extends JPanel {
        int[][] data;       // data[天][0]=新增, data[天][1]=复习
        String[] labels;
        int maxValue = 10;

        void setData(int[][] data, String[] labels) {
            this.data = data;
            this.labels = labels;
            maxValue = 10;
            for (int[] d : data) {
                maxValue = Math.max(maxValue, Math.max(d[0], d[1]));
            }
            // 向上取整到5的倍数
            maxValue = ((maxValue + 4) / 5) * 5;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (data == null || labels == null) return;

            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int paddingLeft = 50;
            int paddingBottom = 40;
            int paddingTop = 20;
            int chartWidth = width - paddingLeft - 20;
            int chartHeight = height - paddingBottom - paddingTop;

            // 画网格线和Y轴刻度
            g2d.setColor(new Color(220, 220, 220));
            g2d.setFont(new Font("Arial", Font.PLAIN, 11));
            int gridLines = 5;
            for (int i = 0; i <= gridLines; i++) {
                int y = paddingTop + chartHeight - (chartHeight * i / gridLines);
                g2d.drawLine(paddingLeft, y, width - 20, y);
                int value = maxValue * i / gridLines;
                g2d.setColor(new Color(100, 100, 100));
                g2d.drawString(String.valueOf(value), 10, y + 4);
                g2d.setColor(new Color(220, 220, 220));
            }

            // 画柱状图
            int barGroupWidth = chartWidth / data.length;
            int barWidth = barGroupWidth / 3;

            for (int i = 0; i < data.length; i++) {
                int x = paddingLeft + i * barGroupWidth + barGroupWidth / 4;

                // 新增单词柱（蓝色）
                int h1 = (int) ((double) data[i][0] / maxValue * chartHeight);
                g2d.setColor(new Color(70, 130, 180));
                g2d.fillRect(x, paddingTop + chartHeight - h1, barWidth, h1);
                g2d.setColor(new Color(70, 130, 180));
                if (data[i][0] > 0) {
                    g2d.drawString(String.valueOf(data[i][0]), x + barWidth / 2 - 5,
                            paddingTop + chartHeight - h1 - 3);
                }

                // 复习单词柱（橙色）
                int h2 = (int) ((double) data[i][1] / maxValue * chartHeight);
                g2d.setColor(new Color(255, 150, 50));
                g2d.fillRect(x + barWidth + 5, paddingTop + chartHeight - h2, barWidth, h2);
                if (data[i][1] > 0) {
                    g2d.drawString(String.valueOf(data[i][1]), x + barWidth + 5 + barWidth / 2 - 5,
                            paddingTop + chartHeight - h2 - 3);
                }

                // X轴标签
                g2d.setColor(new Color(80, 80, 80));
                g2d.setFont(new Font("Arial", Font.PLAIN, 12));
                g2d.drawString(labels[i], x + barWidth - 5, height - 15);
            }

            // 图例
            g2d.setColor(new Color(70, 130, 180));
            g2d.fillRect(width - 180, 5, 15, 15);
            g2d.setColor(new Color(50, 50, 50));
            g2d.drawString("新增单词", width - 160, 17);

            g2d.setColor(new Color(255, 150, 50));
            g2d.fillRect(width - 90, 5, 15, 15);
            g2d.setColor(new Color(50, 50, 50));
            g2d.drawString("复习单词", width - 70, 17);
        }
    }
}
