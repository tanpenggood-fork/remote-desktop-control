package io.github.springstudent.dekstop.client.netty;

import io.github.springstudent.dekstop.client.RemoteClient;
import io.github.springstudent.dekstop.common.command.Cmd;
import io.github.springstudent.dekstop.common.command.CmdResCliInfo;
import io.github.springstudent.dekstop.common.command.CmdType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author ZhouNing
 * @date 2024/12/11 13:17
 **/
public class RemoteChannelHandler extends SimpleChannelInboundHandler<Cmd> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Cmd cmd) throws Exception {
        System.out.println("接收到消息"+cmd.getType());
        if (cmd.getType().equals(CmdType.ResCliInfo)) {
            CmdResCliInfo clientInfo = (CmdResCliInfo) cmd;
            RemoteClient.getRemoteClient().setDeviceCodeAndPassword(clientInfo.getDeviceCode(), clientInfo.getPassword());
            RemoteClient.getRemoteClient().updateConnectionStatus(true);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        RemoteClient.getRemoteClient().updateConnectionStatus(false);
        RemoteClient.getRemoteClient().connectServer();
    }
}
