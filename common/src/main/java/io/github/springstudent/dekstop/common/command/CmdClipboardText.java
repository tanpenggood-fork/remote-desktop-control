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

    public CmdClipboardText(String payload) {
        this.payload = payload;
    }

    @Override
    public CmdType getType() {
        return CmdType.ClipboardText;
    }

    @Override
    public int getWireSize() {
        // 修改为根据UTF-8字符集来计算字节数
        return 4 + payload.getBytes(StandardCharsets.UTF_8).length;
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
    }

    public static CmdClipboardText decode(ByteBuf in) {
        int payloadLength = in.readInt();
        byte[] payloadBytes = new byte[payloadLength];
        in.readBytes(payloadBytes);
        String payload = new String(payloadBytes, StandardCharsets.UTF_8);
        return new CmdClipboardText(payload);
    }
}