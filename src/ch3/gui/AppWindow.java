package ch3.gui;

import ch3.view.LoginView;
import ch3.data.CreateDatabaseAndTable;

/**
 * 程序主类（书上原类，已扩展）
 * 书上原代码：new CreateDatabaseAndTable() + new IntegrationView()
 * 扩展：先显示登录界面，登录成功后进入主窗口
 */
public class AppWindow {
    public static void main(String[] args) {
        // 创建数据库和表（如果已存在则不重复创建）
        new CreateDatabaseAndTable();

        // 显示登录界面（创新：多用户登录）
        LoginView login = new LoginView();
        login.setVisible(true);
    }
}
