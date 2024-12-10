package io.github.springstudent.dekstop.server.netty;

import io.github.springstudent.dekstop.common.command.RemoteRequest;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * @author ZhouNing
 * @date 2024/12/10 20:43
 **/
@ChannelHandler.Sharable
public class NettyServerHandler extends SimpleChannelInboundHandler<RemoteRequest> {

    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyServerHandler.class);

    @SuppressWarnings("unchecked")
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RemoteRequest remoteRequest) throws Exception {

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.info(cause.getMessage());
        ctx.close();
    }
}

