package io.github.springstudent.dekstop.client.core;

import io.github.springstudent.dekstop.client.RemoteClient;
import io.github.springstudent.dekstop.client.utils.DialogFactory;
import io.github.springstudent.dekstop.common.bean.Gray8Bits;
import io.github.springstudent.dekstop.common.configuration.CaptureEngineConfiguration;
import io.github.springstudent.dekstop.common.log.Log;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.Math.abs;
import static java.lang.String.format;
import static javax.swing.SwingConstants.HORIZONTAL;

/**
 * 远程端桌面
 *
 * @author ZhouNing
 * @date 2024/12/9 8:42
 **/
public class RemoteScreen extends JFrame {

    private static final int OFFSET = 6;

    private static final int DEFAULT_FACTOR = 1;
    private double xFactor = DEFAULT_FACTOR;

    private double yFactor = DEFAULT_FACTOR;

    private Dimension canvas;

    private CanvasPannel screenPannel;

    private JScrollPane screenPanelWrapper;

    private JToggleButton fitToScreenButton;

    private JToggleButton keepAspectRatioButton;

    private final AtomicBoolean fitToScreenActivated = new AtomicBoolean(false);

    private final AtomicBoolean isImmutableWindowsSize = new AtomicBoolean(false);

    private final AtomicBoolean keepAspectRatioActivated = new AtomicBoolean(false);

    public RemoteScreen() {
        // 创建主窗口
        super("远程桌面");
        initFrame();
        initPannel();
    }

    private void initPannel() {
        // 主容器
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        //顶部菜单
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(new EmptyBorder(5, 10, 10, 10));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        this.fitToScreenButton = new JToggleButton();
        final Action fitScreenAction = new AbstractAction("适配屏幕") {
            @Override
            public void actionPerformed(ActionEvent ev) {
                fitToScreenActivated.set(!fitToScreenActivated.get());
                if (fitToScreenActivated.get()) {
                    keepAspectRatioButton.setVisible(true);
                    resetCanvas();
                } else {
                    keepAspectRatioButton.setVisible(false);
                    resetFactors();
                }
                repaint();
            }
        };
        fitToScreenButton.setAction(fitScreenAction);
        fitToScreenButton.setVisible(true);
        this.keepAspectRatioButton = new JToggleButton();
        final Action keepAspectRatio = new AbstractAction("保持宽高比") {
            @Override
            public void actionPerformed(ActionEvent ev) {
                keepAspectRatioActivated.set(!keepAspectRatioActivated.get());
                resetCanvas();
                repaint();
            }
        };
        keepAspectRatioButton.setAction(keepAspectRatio);
        keepAspectRatioButton.setVisible(false);

        JButton sessionButton = new JButton();
        sessionButton.setAction(RemoteClient.getRemoteClient().getController().createCaptureConfigurationAction());
        JButton settingsButton = new JButton();
        settingsButton.setAction(RemoteClient.getRemoteClient().getController().createCompressionConfigurationAction());
        buttonPanel.add(fitToScreenButton);
        buttonPanel.add(keepAspectRatioButton);
        buttonPanel.add(sessionButton);
        buttonPanel.add(settingsButton);
        buttonPanel.setOpaque(false);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        //中间画布
        this.screenPannel = new CanvasPannel();
        screenPannel.setBackground(Color.WHITE);
        screenPannel.setFocusable(false);
        screenPanelWrapper = new JScrollPane(screenPannel);
        // 底部状态栏
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(new EmptyBorder(5, 10, 5, 10));
        JLabel statusLabel = new JLabel("0.00 bit/s    0 (-%)    0    74 M of 112 M    00:00:00", SwingConstants.LEFT);
        statusLabel.setFont(new Font("Monospaced", Font.PLAIN, 10));
        bottomPanel.add(statusLabel, BorderLayout.WEST);
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(screenPanelWrapper, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        this.add(mainPanel);
    }

    private void initFrame() {
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setSize(800, 600);
        this.setLayout(new BorderLayout());
        this.setLocationRelativeTo(null);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                RemoteClient.getRemoteClient().closeRemoteScreen();
            }
        });
    }

    public void launch() {
        SwingUtilities.invokeLater(() -> {
            this.setVisible(true);
        });
    }

    public void close() {
        SwingUtilities.invokeLater(() -> {
            this.setVisible(false);
            this.dispose();
        });
    }

    public CanvasPannel getScreenPannel() {
        return screenPannel;
    }

    public double getxFactor() {
        return xFactor;
    }

    public double getyFactor() {
        return yFactor;
    }

    public Dimension getCanvas() {
        return canvas;
    }

    public boolean getFitToScreenActivated() {
        return fitToScreenActivated.get();
    }

    public boolean getKeepAspectRatioActivated() {
        return keepAspectRatioActivated.get();
    }

    public void computeScaleFactors(int sourceWidth, int sourceHeight, boolean keepAspectRatio) {
        canvas = screenPanelWrapper.getSize();
        canvas.setSize(canvas.getWidth() - OFFSET, canvas.getHeight() - OFFSET);
        xFactor = canvas.getWidth() / sourceWidth;
        yFactor = canvas.getHeight() / sourceHeight;
        if (keepAspectRatio && !isImmutableWindowsSize.get() && abs(xFactor - yFactor) > 0.01) {
            resizeWindow(sourceWidth, sourceHeight);
        }
    }

    private void resizeWindow(int sourceWidth, int sourceHeight) {
        Log.debug("%s", () -> format("Resize  W:H %s:%s x:y %s:%s", this.getWidth(), this.getHeight(), xFactor, yFactor));
        int menuHeight = this.getHeight() - canvas.height;
        final Rectangle maximumWindowBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        if (xFactor < yFactor) {
            if ((sourceWidth * yFactor) + OFFSET < maximumWindowBounds.width) {
                xFactor = yFactor;
                Log.debug("Get wider");
                this.setSize((int) (sourceWidth * xFactor) + OFFSET, this.getHeight());
            } else {
                yFactor = xFactor;
                Log.debug("Get lower");
                this.setSize(this.getWidth(), (int) (sourceHeight * yFactor) + menuHeight + OFFSET);
            }
        } else {
            if ((sourceHeight * xFactor) + menuHeight + OFFSET < maximumWindowBounds.height) {
                yFactor = xFactor;
                Log.debug("Get higher");
                this.setSize(this.getWidth(), (int) (sourceHeight * yFactor) + menuHeight + OFFSET);
            } else {
                xFactor = yFactor;
                Log.debug("Get narrower");
                this.setSize((int) (sourceWidth * xFactor) + OFFSET, this.getHeight());
            }
        }
        Log.debug("%s", () -> format("Resized W:H %s:%s x:y %s:%s", this.getWidth(), this.getHeight(), xFactor, yFactor));
    }

    private void resetFactors() {
        xFactor = DEFAULT_FACTOR;
        yFactor = DEFAULT_FACTOR;
    }

    void resetCanvas() {
        canvas = null;
    }

    static class CanvasPannel extends JPanel {

        private int captureWidth = -1;
        private int captureHeight = -1;

        private transient BufferedImage captureImage;

        CanvasPannel() {
            setOpaque(true);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (captureImage != null) {
                g.drawImage(captureImage, 0, 0, this);
            }
        }

        /**
         * Called from within the de-compressor engine thread (!)
         */
        void onCaptureUpdated(final BufferedImage captureImage) {
            SwingUtilities.invokeLater(() -> {
                final int captureImageWidth = captureImage.getWidth();
                final int captureImageHeight = captureImage.getHeight();
                if (captureWidth != captureImageWidth || captureHeight != captureImageHeight) {
                    this.captureWidth = captureImageWidth;
                    this.captureHeight = captureImageHeight;
                    final Dimension size = new Dimension(captureImageWidth, captureImageHeight);
                    setSize(size);
                    setPreferredSize(size);
                }
                this.captureImage = captureImage;
                repaint();
            });
        }
    }

}