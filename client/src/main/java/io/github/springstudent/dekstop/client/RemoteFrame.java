package io.github.springstudent.dekstop.client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author ZhouNing
 * @date 2024/12/9 10:53
 **/
public abstract class RemoteFrame extends JFrame {

    private String serverIp = "172.16.1.72";
    private String serverPort = "54321";

    private JLabel titleLabel;
    private JTextField deviceCodeField;
    private JTextField passwordField;
    private JTextField remoteDeviceField;

    private JButton connectButton;
    protected Boolean isController;


    public RemoteFrame() {
        /**
         * 设置主窗体信息
         */
        this.setUndecorated(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(600, 400);
        this.setLayout(new BorderLayout());
        this.setLocationRelativeTo(null);
        /**
         * 自定义标题栏
         */
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBorder(new EmptyBorder(5, 10, 5, 10));
        titleBar.setBackground(Color.WHITE);
        titleLabel = new JLabel();
        titleLabel.setText("<html>远程桌面控制—<span style='color:#ffdc15;'>准备中</span></html>");
        titleLabel.setForeground(Color.black);
        titleBar.add(titleLabel, BorderLayout.WEST);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        buttonPanel.setOpaque(false);
        // 最小化按钮
        JButton minimizeButton = createTitleBarButton("最小化(-)");
        minimizeButton.addActionListener(e -> this.setState(JFrame.ICONIFIED));
        // 关闭按钮
        JButton closeButton = createTitleBarButton("关闭(x)");
        closeButton.addActionListener(e -> System.exit(0));
        // 设置按钮
        JButton settingsButton = createTitleBarButton("设置(⚙)");
        settingsButton.addActionListener(e -> openSettingsDialog());
        // 添加按钮到按钮区域
        buttonPanel.add(settingsButton);
        buttonPanel.add(minimizeButton);
        buttonPanel.add(closeButton);
        // 添加按钮区域到标题栏
        titleBar.add(buttonPanel, BorderLayout.EAST);
        // 拖动窗口功能
        MouseAdapter dragListener = new MouseAdapter() {
            private Point initialClick;

            @Override
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                int xMoved = e.getXOnScreen() - initialClick.x;
                int yMoved = e.getYOnScreen() - initialClick.y;
                setLocation(xMoved, yMoved);
            }
        };
        titleBar.addMouseListener(dragListener);
        titleBar.addMouseMotionListener(dragListener);
        /**
         * 主面板
         */
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(5, 20, 5, 20));

        /**
         * 顶部输入区域
         */
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 10, 0, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.weighty = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        topPanel.add(new JLabel("设备代码:"), gbc);

        gbc.gridx = 1;
        this.deviceCodeField = new JTextField(10);
        deviceCodeField.setText("");
        deviceCodeField.setEditable(false);
        topPanel.add(deviceCodeField, gbc);

        gbc.gridx = 2;
        topPanel.add(new JLabel("临时密码:"), gbc);

        gbc.gridx = 3;
        this.passwordField = new JTextField(10);
        passwordField.setText("");
        topPanel.add(passwordField, gbc);

        /**
         * 底部控制区域
         */
        JPanel bottomPanel = new JPanel(new GridBagLayout());
        bottomPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(0, 10, 0, 10);
        gbc.weighty = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        bottomPanel.add(new JLabel("远程控制设备:"), gbc);

        gbc.gridx = 1;
        this.remoteDeviceField = new JTextField(15);
        bottomPanel.add(remoteDeviceField, gbc);

        gbc.gridx = 2;
        this.connectButton = new JButton("连接");
        this.connectButton.addActionListener(e -> {
            openRemoteScreen(remoteDeviceField.getText());
            connectButton.setText("断开");
            this.isController = true;
        });
        bottomPanel.add(connectButton, gbc);
        mainPanel.add(topPanel);
        mainPanel.add(Box.createVerticalStrut(5)); // 间距
        mainPanel.add(bottomPanel);
        // 添加自定义标题栏和主面板到窗体
        this.add(titleBar, BorderLayout.NORTH);
        this.add(mainPanel, BorderLayout.CENTER);
        this.setVisible(true);
    }

    /**
     * 创建标题栏按钮
     */
    private JButton createTitleBarButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        button.setBackground(new Color(255, 255, 255));
        button.setForeground(Color.BLACK);
        return button;
    }

    /**
     * 显示设置对话框
     */
    private void openSettingsDialog() {
        JDialog settingsDialog = new JDialog(this, "设置", true);
        settingsDialog.setSize(300, 200);
        settingsDialog.setLayout(new BorderLayout());
        settingsDialog.setLocationRelativeTo(this);

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        contentPanel.add(new JLabel("服务器ip:"), gbc);
        gbc.gridx = 1;
        JTextField serverIpField = new JTextField(15);
        serverIpField.setText(serverIp);
        contentPanel.add(serverIpField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        contentPanel.add(new JLabel("服务器port:"), gbc);
        gbc.gridx = 1;
        JTextField serverPortField = new JTextField(15);
        serverPortField.setText(serverPort);
        contentPanel.add(serverPortField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton confirmButton = new JButton("确定");
        confirmButton.addActionListener(e -> {
            this.serverIp = serverIpField.getText();
            this.serverPort = serverPortField.getText();
            settingsDialog.dispose();
        });
        buttonPanel.add(confirmButton);

        settingsDialog.add(contentPanel, BorderLayout.CENTER);
        settingsDialog.add(buttonPanel, BorderLayout.SOUTH);
        settingsDialog.setVisible(true);
    }


    protected abstract void openRemoteScreen(String remoteName);

    protected void closeRemoteScreen() {
        this.connectButton.setText("连接");
        this.isController = false;
    }

    protected final void setDeviceCode(String deviceCode) {
        this.deviceCodeField.setText(deviceCode);
    }

    protected final void setPassword(String password) {
        this.passwordField.setText(password);
    }

    protected final void updateConnectionStatus(boolean connected) {
        if (connected) {
            titleLabel.setText("<html>远程桌面控制—<span style='color:blue;'>已就绪</span></html>");
        } else {
            titleLabel.setText("<html>远程桌面控制—<span style='color:red;'>连接中</span></html>");
        }
    }


    public String getServerIp() {
        return serverIp;
    }

    public Integer getServerPort() {
        return Integer.parseInt(serverPort);
    }
}