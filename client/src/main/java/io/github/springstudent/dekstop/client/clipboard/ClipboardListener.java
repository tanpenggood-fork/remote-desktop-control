package io.github.springstudent.dekstop.client.clipboard;

import io.github.springstudent.dekstop.client.bean.Listener;

import java.awt.image.BufferedImage;

/**
 * @author ZhouNing
 * @date 2024/12/20 8:48
 **/
public interface ClipboardListener extends Listener {

    void clipboardText(String text);

    void clipboardImg(BufferedImage img);
}
