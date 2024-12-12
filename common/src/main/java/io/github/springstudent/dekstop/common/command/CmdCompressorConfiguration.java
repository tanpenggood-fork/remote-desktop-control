package io.github.springstudent.dekstop.common.command;

import io.github.springstudent.dekstop.common.bean.CompressionMethod;
import io.github.springstudent.dekstop.common.configuration.CompressorEngineConfiguration;
import io.netty.buffer.ByteBuf;
import java.io.IOException;

/**
 * @author ZhouNing
 * @date 2024/12/12 18:05
 **/
public class CmdCompressorConfiguration extends Cmd{
    private final CompressorEngineConfiguration configuration;

    public CmdCompressorConfiguration(CompressorEngineConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public CmdType getType() {
        return CmdType.CompressorConfig;
    }

    public CompressorEngineConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public int getWireSize() {
        return 10;
    }

    @Override
    public void encode(ByteBuf out) throws IOException {
        encodeEnum(out, configuration.getMethod());
        out.writeByte(configuration.useCache() ? 1 : 0);
        out.writeInt(configuration.getCacheMaxSize());
        out.writeInt(configuration.getCachePurgeSize());
    }

    public static CmdCompressorConfiguration decode(ByteBuf in) throws IOException {
        final CompressionMethod method = decodeEnum(in, CompressionMethod.class);
        final boolean useCase = in.readByte() == 1;
        final int maxSize = in.readInt();
        final int purgeSize = in.readInt();
        return new CmdCompressorConfiguration(new CompressorEngineConfiguration(method, useCase, maxSize, purgeSize));
    }

    public String toString() {
        return String.format("[configuration:%s]", configuration.getMethod());
    }
}
