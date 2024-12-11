package io.github.springstudent.dekstop.common.command;


import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author ZhouNing
 * @date 2024/12/11 8:57
 **/
public class CmdResCliInfo extends Cmd {

    private String deviceCode;

    private String password;

    public CmdResCliInfo(String deviceCode, String password) {
        this.deviceCode = deviceCode;
        this.password = password;
    }

    @Override
    public CmdType getType() {
        return CmdType.ResCliInfo;
    }

    @Override
    public int getWireSize() {
        return deviceCode.length() + password.length() + 8;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public void encode(ByteBuf out) throws IOException {
        out.writeInt(deviceCode.length());
        out.writeCharSequence(deviceCode, StandardCharsets.UTF_8);
        out.writeInt(password.length());
        out.writeCharSequence(password, StandardCharsets.UTF_8);
    }

    public static CmdResCliInfo decode(ByteBuf byteBuf) {
        int deviceCodeLength = byteBuf.readInt();
        String deviceCode = byteBuf.readCharSequence(deviceCodeLength, StandardCharsets.UTF_8).toString();
        int passwordLength = byteBuf.readInt();
        String password = byteBuf.readCharSequence(passwordLength, StandardCharsets.UTF_8).toString();
        return new CmdResCliInfo(deviceCode, password);
    }

    @Override
    public String toString() {
        return String.format("CmdResponseClientInfo={deviceCode:%s,password:%s}", deviceCode, password);
    }
}
