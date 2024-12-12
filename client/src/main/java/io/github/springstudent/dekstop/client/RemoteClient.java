package io.github.springstudent.dekstop.client;

import io.github.springstudent.dekstop.client.core.RemoteControlled;
import io.github.springstudent.dekstop.client.core.RemoteController;
import io.github.springstudent.dekstop.client.core.RemoteFrame;
import io.github.springstudent.dekstop.client.core.RemoteScreen;
import io.github.springstudent.dekstop.client.netty.RemoteChannelHandler;
import io.github.springstudent.dekstop.client.netty.RemoteStateIdleHandler;
import io.github.springstudent.dekstop.common.command.Cmd;
import io.github.springstudent.dekstop.common.protocol.NettyDecoder;
import io.github.springstudent.dekstop.common.protocol.NettyEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.concurrent.TimeUnit;

/**
 * @author ZhouNing
 * @date 2024/12/6
 */
public class RemoteClient extends RemoteFrame {
    private static RemoteClient remoteClient;

    private static final Logger logger = LoggerFactory.getLogger(RemoteClient.class);

    private String serverIp;

    private Integer serverPort;

    private boolean connectStatus;

    private RemoteScreen remoteScreen;

    private RemoteControlled controlled;

    private RemoteController controller;

    public RemoteClient(String serverIp, Integer serverPort) {
        remoteClient = this;
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        this.controlled = new RemoteControlled();
        this.controller = new RemoteController();
        this.connectServer();
    }


    @Override
    public void openRemoteScreen(String deviceCode) {
        if (!connectStatus) {
            showMessageDialog("请等待连接连接服务器成功", JOptionPane.ERROR_MESSAGE);
        } else {
            controller.openSession(deviceCode);
            this.remoteScreen = new RemoteScreen(deviceCode);
        }
    }


    @Override
    public void closeRemoteScreen() {
        controller.closeSession();
        remoteScreen.close();
    }

    public RemoteScreen getRemoteScreen() {
        return remoteScreen;
    }

    /**
     * 连接至server
     */
    public void connectServer() {
        final Bootstrap bootstrap = new Bootstrap();
        NioEventLoopGroup group = new NioEventLoopGroup();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new NettyDecoder());
                        socketChannel.pipeline().addLast(new NettyEncoder());
                        socketChannel.pipeline().addLast(new RemoteStateIdleHandler());
                        socketChannel.pipeline().addLast(new RemoteChannelHandler());
                    }
                });
        //连接至远程客户端
        connect(bootstrap, 0);
    }

    private void connect(Bootstrap bootstrap, int retry) {
        bootstrap.connect(serverIp, serverPort).addListener(future -> {
            if (future.isSuccess()) {
                logger.info("connect to remote server success");
                this.connectStatus = true;
            } else {
                this.connectStatus = false;
                Integer order = retry + 1;
                logger.info("reconnect to remote server serverIp={},serverPort={},retry times ={}", serverIp, serverPort, order);
                bootstrap.config().group().schedule(() -> connect(bootstrap, order), 5, TimeUnit
                        .SECONDS);
            }
        });
    }


    public RemoteController getController() {
        return controller;
    }

    public RemoteControlled getControlled() {
        return controlled;
    }

    public void handleCmd(Cmd cmd) {
        controller.handleCmd(cmd);
        controlled.handleCmd(cmd);
    }

    public static RemoteClient getRemoteClient() {
        return remoteClient;
    }

    public static void main(String[] args) {
        RemoteClient remoteClient = new RemoteClient("172.16.1.37", 54321);
    }

}