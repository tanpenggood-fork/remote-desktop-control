package io.github.springstudent.dekstop.client.clipboard;

import io.github.springstudent.dekstop.client.bean.Listeners;
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

    private String lastText = "";
    private BufferedImage lastImage = null;
    private List<File> lastFiles = null;

    private Thread pollerThread;

    private volatile boolean toStop = false;

    public void addListener(ClipboardListener listener) {
        listeners.add(listener);
    }

    public void start() {
        Log.info("client start clipboard poller");
        pollerThread = new Thread(() -> {
            while (!toStop) {
                try {
                    checkClipboard();
                } catch (Exception e) {
                    Log.error(e.getMessage(), e);
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(1000);
                } catch (InterruptedException e) {
                    if (!toStop) {
                        Log.error(e.getMessage(), e);
                    }
                }
            }
        });
        pollerThread.setDaemon(true);
        pollerThread.setName("clipboard-poller");
        pollerThread.start();
    }

    public void stop() {
        Log.info("client stop clipboard poller");
        toStop = true;
        if (pollerThread == null) {
            return;
        }
        pollerThread.interrupt();
        try {
            pollerThread.join();
        } catch (InterruptedException e) {
            Log.error(e.getMessage(), e);
        }
    }


    private void checkClipboard() throws Exception {
        try {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            if (clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
                String currentText = (String) clipboard.getData(DataFlavor.stringFlavor);
                if (!currentText.equals(lastText)) {
                    Log.info("Clipboard contains new text: " + currentText);
                    lastText = currentText;
                    fireClipboardText(currentText);
                }
            }
            if (clipboard.isDataFlavorAvailable(DataFlavor.imageFlavor)) {
                BufferedImage currentImage = (BufferedImage) clipboard.getData(DataFlavor.imageFlavor);
                if (currentImage != lastImage) {
                    Log.info("Clipboard contains new image: " + currentImage.getWidth() + "x" + currentImage.getHeight());
                    lastImage = currentImage;
                    fireClipboardImg(currentImage);
                }
            }
            if (clipboard.isDataFlavorAvailable(DataFlavor.javaFileListFlavor)) {
                List<File> currentFiles = (List<File>) clipboard.getData(DataFlavor.javaFileListFlavor);
                if (currentFiles != null && !currentFiles.equals(lastFiles)) {
                    Log.info("Clipboard contains new files:");
                    for (File file : currentFiles) {
                        System.out.println(file.getAbsolutePath());
                    }
                    lastFiles = currentFiles;
                }
            }

        } catch (Exception e) {
            throw e;
        }
    }

    private void fireClipboardText(String text) {
        listeners.getListeners().forEach(listener -> listener.clipboardText(text));
    }

    private void fireClipboardImg(BufferedImage img) {
        listeners.getListeners().forEach(listener -> listener.clipboardImg(img));
    }
}
