package io.github.springstudent.dekstop.client.core;

import io.github.springstudent.dekstop.client.RemoteClient;
import io.github.springstudent.dekstop.common.log.Log;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.Math.abs;
import static java.lang.String.format;

/**
 *
 * @author ZhouNing
 * @date 2024/12/9 8:42
 */
public class RemoteScreen extends JFrame {

    private static final int OFFSET = 6;

    private static final int DEFAULT_FACTOR = 1;
    private double xFactor = DEFAULT_FACTOR;
    private double yFactor = DEFAULT_FACTOR;

    private Dimension canvas;

    private CanvasPannel screenPannel;

    private JScrollPane screenPanelWrapper;

    private final AtomicBoolean fitToScreenActivated = new AtomicBoolean(false);

    private final AtomicBoolean keepAspectRatioActivated = new AtomicBoolean(false);

    public RemoteScreen() {
        super("远程桌面");
        initFrame();
        initCanvasPanel();
        initMenuBar();
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

    private void initCanvasPanel() {
        this.screenPannel = new CanvasPannel();
        screenPannel.setBackground(Color.WHITE);
        screenPannel.setFocusable(false);
        this.screenPanelWrapper = new JScrollPane(screenPannel);
        this.add(screenPanelWrapper, BorderLayout.CENTER);
    }

    private void initMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        // 适配屏幕菜单项
        JCheckBoxMenuItem fitToScreenItem = new JCheckBoxMenuItem(new AbstractAction("适配屏幕") {
            @Override
            public void actionPerformed(ActionEvent ev) {
                fitToScreenActivated.set(!fitToScreenActivated.get());
                if (fitToScreenActivated.get()) {
                    resetCanvas();
                } else {
                    resetFactors();
                }
                repaint();
            }
        });
        // 保持宽高比菜单项
        JCheckBoxMenuItem keepAspectRatioItem = new JCheckBoxMenuItem(new AbstractAction("保持宽高比") {
            @Override
            public void actionPerformed(ActionEvent ev) {
                keepAspectRatioActivated.set(!keepAspectRatioActivated.get());
                resetCanvas();
                repaint();
            }
        });
        keepAspectRatioItem.setEnabled(false);
        // 根据适配屏幕的状态动态控制保持宽高比的可见性
        fitToScreenItem.addActionListener(e -> keepAspectRatioItem.setEnabled(fitToScreenActivated.get()));
        // 会话配置
        JMenuItem sessionConfigItem = new JMenuItem(RemoteClient.getRemoteClient().getController().createCaptureConfigurationAction());
        // 压缩设置
        JMenuItem compressionConfigItem = new JMenuItem(RemoteClient.getRemoteClient().getController().createCompressionConfigurationAction());
        // 菜单分组
        JMenu optionsMenu = new JMenu("选项");
        optionsMenu.add(fitToScreenItem);
        optionsMenu.add(keepAspectRatioItem);
        optionsMenu.addSeparator();
        optionsMenu.add(sessionConfigItem);
        optionsMenu.add(compressionConfigItem);
        menuBar.add(optionsMenu);
        this.setJMenuBar(menuBar);
    }

    public boolean getFitToScreenActivated() {
        return fitToScreenActivated.get();
    }

    public boolean getKeepAspectRatioActivated() {
        return keepAspectRatioActivated.get();
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

    public CanvasPannel getScreenPannel() {
        return screenPannel;
    }

    public JScrollPane getScreenPanelWrapper() {
        return screenPanelWrapper;
    }

    public void launch() {
        SwingUtilities.invokeLater(() -> this.setVisible(true));
    }

    public void close() {
        SwingUtilities.invokeLater(() -> {
            this.setVisible(false);
            this.dispose();
        });
    }

    public void computeScaleFactors(int sourceWidth, int sourceHeight, boolean keepAspectRatio) {
        canvas = screenPanelWrapper.getSize();
        canvas.setSize(canvas.getWidth() - OFFSET, canvas.getHeight() - OFFSET);
        xFactor = canvas.getWidth() / sourceWidth;
        yFactor = canvas.getHeight() / sourceHeight;
        if (keepAspectRatio && abs(xFactor - yFactor) > 0.01) {
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
                this.setSize((int) (sourceWidth * xFactor) + OFFSET, this.getHeight());
            } else {
                yFactor = xFactor;
                this.setSize(this.getWidth(), (int) (sourceHeight * yFactor) + menuHeight + OFFSET);
            }
        } else {
            if ((sourceHeight * xFactor) + menuHeight + OFFSET < maximumWindowBounds.height) {
                yFactor = xFactor;
                this.setSize(this.getWidth(), (int) (sourceHeight * yFactor) + menuHeight + OFFSET);
            } else {
                xFactor = yFactor;
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
