package io.github.springstudent.dekstop.client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * @author ZhouNing
 * @date 2024/12/9 10:53
 **/
public abstract class RemoteFrame extends JFrame {

    protected JTextField deviceCodeField;

    protected JTextField passwordField;

    protected JTextField remoteDeviceField;

    protected JButton connectButton;

    protected Boolean isController;

    public RemoteFrame() {
        super("远程桌面控制");
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        super.setSize(600, 400);
        super.setLayout(new BorderLayout());
        super.setLocationRelativeTo(null);
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(5, 20, 5, 20));
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
        // 底部区域 - 远程设备输入和连接按钮
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
        // 控制topPanel和bottomPanel之间的间距
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(bottomPanel);
        super.add(mainPanel, BorderLayout.CENTER);
        super.setVisible(true);
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
}
