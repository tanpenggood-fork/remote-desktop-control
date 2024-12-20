package io.github.springstudent.dekstop.client.core;


import io.github.springstudent.dekstop.client.RemoteClient;
import io.github.springstudent.dekstop.client.clipboard.ClipboardListener;
import io.github.springstudent.dekstop.client.clipboard.ClipboardPoller;
import io.github.springstudent.dekstop.common.bean.TransferableImage;
import io.github.springstudent.dekstop.common.command.Cmd;
import io.github.springstudent.dekstop.common.command.CmdClipboardImg;
import io.github.springstudent.dekstop.common.command.CmdClipboardText;
import io.github.springstudent.dekstop.common.command.CmdType;
import io.github.springstudent.dekstop.common.log.Log;
import io.netty.channel.Channel;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;

/**
 * @author ZhouNing
 * @date 2024/12/10 14:20
 **/
public abstract class RemoteControll implements ClipboardListener {

    protected Channel channel;

    private ClipboardPoller clipboardPoller;

    public RemoteControll() {
        clipboardPoller = new ClipboardPoller();
        clipboardPoller.addListener(this);
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    protected void fireCmd(Cmd cmd) {
        if (channel != null && channel.isActive()) {
            channel.writeAndFlush(cmd);
        } else {
            Log.error("client fireCmd error,please check network connect");
        }
    }

    protected void showMessageDialog(Object msg, int messageType) {
        SwingUtilities.invokeLater(() ->
                RemoteClient.getRemoteClient().showMessageDialog(msg, messageType)
        );
    }

    public abstract void handleCmd(Cmd cmd);

    public abstract String getType();

    public void start() {
        clipboardPoller.start();
    }

    public void stop() {
        clipboardPoller.stop();
    }


    @Override
    public void clipboardText(String text) {
        new Thread(() -> this.fireCmd(new CmdClipboardText(text, getType()))).start();
    }

    @Override
    public void clipboardImg(BufferedImage img) {
        new Thread(() -> this.fireCmd(new CmdClipboardImg(new TransferableImage(img), getType()))).start();
    }

    protected final void setClipboard(Cmd cmd) {
        SwingUtilities.invokeLater(() -> {
            synchronized (ClipboardPoller.class) {
                if (cmd.getType().equals(CmdType.ClipboardText)) {
                    CmdClipboardText cmdClipboardText = (CmdClipboardText) cmd;
                    StringSelection stringSelection = new StringSelection(cmdClipboardText.getPayload());
                    clipboardPoller.setLastText(cmdClipboardText.getPayload());
                    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
                } else if (cmd.getType().equals(CmdType.ClipboardImg)) {
                    CmdClipboardImg cmdClipboardImg = (CmdClipboardImg) cmd;
                    clipboardPoller.setLastImage(cmdClipboardImg.getGraphic().getTransferData(DataFlavor.imageFlavor));
                    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new TransferableImage(cmdClipboardImg.getGraphic().getTransferData(DataFlavor.imageFlavor)), null);
                }
            }
        });

    }
}
