package io.github.springstudent.dekstop.common.command;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

/**
 * @author ZhouNing
 * @date 2025/1/2 16:25
 **/
public class CmdResRemoteClipboard extends Cmd {

    @Override
    public CmdType getType() {
        return CmdType.ResRemoteClipboard;
    }

    @Override
    public int getWireSize() {
        return 0;
    }

    @Override
    public String toString() {
        return CmdResRemoteClipboard.class.getSimpleName();
    }

    @Override
    public void encode(ByteBuf out) throws IOException {

    }
}
