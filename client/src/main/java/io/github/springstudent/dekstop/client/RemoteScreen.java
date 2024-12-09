package io.github.springstudent.dekstop.client;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * 远程端桌面
 * @author ZhouNing
 * @date 2024/12/9 8:42
 **/
public class RemoteScreen implements Reconfigureable{
    private BufferedImage image;

    private JFrame jFrame;

    private JPanel screenPannel;

    private String screenName;

    public RemoteScreen(String screenName){
        // 创建主窗口
        this.jFrame = new JFrame("远程桌面");
        this.screenName = String.format("%s的远程桌面",screenName);
        initFrame();
        initPannel();
    }

    private void initPannel() {
        // 主容器
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        //顶部菜单
        JPanel topPanel = new JPanel(new BorderLayout()); // 使用 BorderLayout
        topPanel.setBorder(new EmptyBorder(5, 10, 10, 10));
        JLabel titleLabel = new JLabel(screenName, SwingConstants.CENTER);
        titleLabel.setFont(new Font("宋体", Font.BOLD, 12));
        topPanel.add(titleLabel, BorderLayout.NORTH);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton connectButton = new JButton("打开全屏");
        JButton sessionButton = new JButton("画面设置");
        JButton settingsButton = new JButton("压缩算法");
        buttonPanel.add(connectButton);
        buttonPanel.add(sessionButton);
        buttonPanel.add(settingsButton);
        buttonPanel.setOpaque(false);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        //中间画布
        this.screenPannel = new JPanel();
        screenPannel.setBackground(Color.WHITE);
        // 底部状态栏
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(new EmptyBorder(5, 10, 5, 10));
        JLabel statusLabel = new JLabel("0.00 bit/s    0 (-%)    0    74 M of 112 M    00:00:00", SwingConstants.LEFT);
        statusLabel.setFont(new Font("Monospaced", Font.PLAIN, 10));
        bottomPanel.add(statusLabel, BorderLayout.WEST);
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(screenPannel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        jFrame.add(mainPanel);
    }

    private void initFrame() {
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setSize(800, 600);
        jFrame.setLayout(new BorderLayout());
        jFrame.setLocationRelativeTo(null);
    }

    public void launch(){
        SwingUtilities.invokeLater(()->{
            jFrame.setVisible(true);
        });
    }

}