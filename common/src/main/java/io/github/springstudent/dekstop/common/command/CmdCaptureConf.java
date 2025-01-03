package io.github.springstudent.dekstop.common.command;

import io.github.springstudent.dekstop.common.bean.Gray8Bits;
import io.github.springstudent.dekstop.common.configuration.CaptureEngineConfiguration;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

/**
 * @author ZhouNing
 * @date 2024/12/13 10:55
 **/
public class CmdCaptureConf extends Cmd {

    private final CaptureEngineConfiguration configuration;

    public CmdCaptureConf(CaptureEngineConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public CmdType getType() {
        return CmdType.CaptureConfig;
    }

    public CaptureEngineConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public int getWireSize() {
        return 7;
    }

    @Override
    public String toString() {
        return String.format("[quantization:%s][tick:%d][colors:%b]", configuration.getCaptureQuantization(), configuration.getCaptureTick(), configuration.isCaptureColors());
    }

    @Override
    public void encode(ByteBuf out) throws IOException {
        encodeEnum(out, configuration.getCaptureQuantization());
        out.writeInt(configuration.getCaptureTick());
        out.writeShort(configuration.isCaptureColors() ? 1 : 0);
    }

    public static CmdCaptureConf decode(ByteBuf in) throws IOException {
        final Gray8Bits quantization = decodeEnum(in, Gray8Bits.class);
        final int tick = in.readInt();
        final boolean colors = in.readShort() == 1;
        return new CmdCaptureConf(new CaptureEngineConfiguration(tick, quantization, colors));
    }
}
