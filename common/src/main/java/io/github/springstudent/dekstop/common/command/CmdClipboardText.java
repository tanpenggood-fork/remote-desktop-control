package io.github.springstudent.dekstop.common.command;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author ZhouNing
 * @date 2024/12/19 19:34
 **/
public class CmdClipboardText extends Cmd {
    private final String payload;

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
        return 8 + payload.length() + controllType.length();
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
        out.writeInt(payload.length());
        out.writeCharSequence(payload, StandardCharsets.UTF_8);
        out.writeInt(controllType.length());
        out.writeCharSequence(controllType, StandardCharsets.UTF_8);
    }

    public static CmdClipboardText decode(ByteBuf in) {
        int payloadLength = in.readInt();
        String payload = in.readCharSequence(payloadLength, StandardCharsets.UTF_8).toString();
        int controllTypeLength = in.readInt();
        String controllType = in.readCharSequence(controllTypeLength, StandardCharsets.UTF_8).toString();
        return new CmdClipboardText(payload, controllType);
    }
}
