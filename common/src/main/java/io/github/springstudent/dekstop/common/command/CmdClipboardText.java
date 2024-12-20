package io.github.springstudent.dekstop.common.command;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author ZhouNing
 * @date 2024/12/19 19:34
 **/
public class CmdClipboardText extends Cmd {
    private String payload;
    private String controllType;

    public CmdClipboardText(String payload, String controllType) {
        this.payload = payload;
        this.controllType = controllType;
    }

    @Override
    public CmdType getType() {
        return CmdType.ClipboardText;
    }

    @Override
    public int getWireSize() {
        // 修改为根据UTF-8字符集来计算字节数
        return 4 + payload.getBytes(StandardCharsets.UTF_8).length +
                4 + controllType.getBytes(StandardCharsets.UTF_8).length;
    }

    @Override
    public String toString() {
        return CmdClipboardText.class.getSimpleName();
    }

    public String getPayload() {
        return payload;
    }

    @Override
    public void encode(ByteBuf out) throws IOException {
        byte[] payloadBytes = payload.getBytes(StandardCharsets.UTF_8);
        out.writeInt(payloadBytes.length);
        out.writeBytes(payloadBytes);
        byte[] controllTypeBytes = controllType.getBytes(StandardCharsets.UTF_8);
        out.writeInt(controllTypeBytes.length);
        out.writeBytes(controllTypeBytes);
    }

    public static CmdClipboardText decode(ByteBuf in) {
        int payloadLength = in.readInt();
        byte[] payloadBytes = new byte[payloadLength];
        in.readBytes(payloadBytes);
        String payload = new String(payloadBytes, StandardCharsets.UTF_8);
        int controllTypeLength = in.readInt();
        byte[] controllTypeBytes = new byte[controllTypeLength];
        in.readBytes(controllTypeBytes);
        String controllType = new String(controllTypeBytes, StandardCharsets.UTF_8);
        return new CmdClipboardText(payload, controllType);
    }
}