package io.github.springstudent.dekstop.common.command;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

/**
 * @author ZhouNing
 * @date 2025/4/2 9:45
 **/
public class CmdSelectScreen extends Cmd {

    private int screenIndex;

    public CmdSelectScreen(int screenIndex) {
        this.screenIndex = screenIndex;
    }

    @Override
    public CmdType getType() {
        return CmdType.SelectScreen;
    }

    public int getScreenIndex() {
        return screenIndex;
    }

    @Override
    public int getWireSize() {
        return 4;
    }

    @Override
    public String toString() {
        return String.format("CmdSelectScreen={screenIndex:%d}", screenIndex);
    }

    @Override
    public void encode(ByteBuf out) throws IOException {
        out.writeInt(screenIndex);
    }

    public static CmdSelectScreen decode(ByteBuf in) {
        return new CmdSelectScreen(in.readInt());
    }
}
