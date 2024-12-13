package io.github.springstudent.dekstop.common.command;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

/**
 * @author ZhouNing
 * @date 2024/12/13 10:55
 **/
public class CmdCaptureConf extends Cmd{
    @Override
    public CmdType getType() {
        return null;
    }

    @Override
    public int getWireSize() {
        return 0;
    }

    @Override
    public String toString() {
        return null;
    }

    @Override
    public void encode(ByteBuf out) throws IOException {

    }
}
