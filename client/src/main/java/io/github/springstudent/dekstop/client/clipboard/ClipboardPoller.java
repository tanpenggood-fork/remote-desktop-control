package io.github.springstudent.dekstop.client.clipboard;

import io.github.springstudent.dekstop.client.bean.Listeners;
import io.github.springstudent.dekstop.client.utils.FileUtilities;
import io.github.springstudent.dekstop.common.log.Log;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author ZhouNing
 * @date 2024/12/20 8:40
 **/
public class ClipboardPoller {

    private Listeners<ClipboardListener> listeners = new Listeners<>();

    private volatile String lastText = "";
    private volatile BufferedImage lastImage = null;
    private volatile List<File> lastFiles = null;

    private Thread pollerThread;

    public void addListener(ClipboardListener listener) {
        listeners.add(listener);
    }

    public void start() {
        Log.info("client start clipboard poller");
        pollerThread = new Thread(() -> {
            try {
                checkClipboard();
            } catch (InterruptedException e) {
                pollerThread.interrupt();
            }

        });
        pollerThread.setDaemon(true);
        pollerThread.setName("clipboard-poller");
        pollerThread.start();
    }

    public void stop() {
        Log.info("client stop clipboard poller");
        if (pollerThread == null) {
            return;
        }
        pollerThread.interrupt();
    }


    private void checkClipboard() throws InterruptedException {
        while (true) {
            synchronized (ClipboardPoller.class) {
                try {
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    if (clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
                        String currentText = (String) clipboard.getData(DataFlavor.stringFlavor);
                        if (!currentText.equals(lastText)) {
                            Log.info("Clipboard contains new text: " + currentText);
                            lastText = currentText;
                            fireClipboardText(currentText);
                        }
                    } else if (clipboard.isDataFlavorAvailable(DataFlavor.imageFlavor)) {
                        BufferedImage currentImage = (BufferedImage) clipboard.getData(DataFlavor.imageFlavor);
                        if (currentImage != null) {
                            if (lastImage == null || !FileUtilities.bufferedImgMd5(currentImage).equals(FileUtilities.bufferedImgMd5(lastImage))) {
                                Log.info("Clipboard contains new image: " + currentImage.getWidth() + "x" + currentImage.getHeight());
                                lastImage = currentImage;
//                                fireClipboardImg(currentImage);
                            }
                        }
                    } else if (clipboard.isDataFlavorAvailable(DataFlavor.javaFileListFlavor)) {
                        List<File> currentFiles = (List<File>) clipboard.getData(DataFlavor.javaFileListFlavor);
                        if (currentFiles != null && !currentFiles.equals(lastFiles)) {
                            lastFiles = currentFiles;
                        }
                    }
                } catch (Exception e) {
                    Log.error("client checkClipboard occur error", e);
                }
                TimeUnit.MILLISECONDS.sleep(1000);
            }
        }

    }

    public void setLastText(String lastText) {
        this.lastText = lastText;
    }

    public void setLastImage(BufferedImage lastImage) {
        this.lastImage = lastImage;
    }

    private void fireClipboardText(String text) {
        listeners.getListeners().forEach(listener -> listener.clipboardText(text));
    }

    private void fireClipboardImg(BufferedImage img) {
        listeners.getListeners().forEach(listener -> listener.clipboardImg(img));
    }
}
