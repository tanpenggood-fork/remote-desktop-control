package io.github.springstudent.dekstop.server.netty;

import cn.hutool.core.util.RandomUtil;
import io.github.springstudent.dekstop.common.command.Cmd;
import io.github.springstudent.dekstop.common.command.CmdResCliInfo;
import io.github.springstudent.dekstop.common.command.CmdResPong;
import io.github.springstudent.dekstop.common.command.CmdType;
import io.github.springstudent.dekstop.common.utils.NettyUtils;
import io.netty.channel.ChannelFutureListener;
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
public class NettyServerHandler extends SimpleChannelInboundHandler<Cmd> {

    /** logger */
    private static final Logger log = LoggerFactory.getLogger(NettyServerHandler.class);

    @SuppressWarnings("unchecked")
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Cmd cmd) throws Exception {
        System.out.println(cmd);
        NettyUtils.updateReaderTime(ctx.channel(),System.currentTimeMillis());
        if (cmd.getType().equals(CmdType.ReqPing)){
            ctx.writeAndFlush(new CmdResPong()).addListeners((ChannelFutureListener) future -> {
                if (!future.isSuccess()) {
                    log.error("send pong error,close Channel");
                    future.channel().close();
                }
            }) ;
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.channel().writeAndFlush(new CmdResCliInfo(RandomUtil.randomString(8),"111111"));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info(cause.getMessage());
        ctx.close();
    }
}

