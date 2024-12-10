package io.github.springstudent.dekstop.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author ZhouNing
 * @date 2024/12/6
 */
public class RemoteClient extends RemoteFrame {

    private static final Logger logger = LoggerFactory.getLogger(RemoteClient.class);
    private RemoteControll controll;

    private RemoteScreen screen;

    private Boolean isController;

    private String serverIp;

    private Integer serverPort;

    private boolean connectStatus;

    public RemoteClient(String serverIp, Integer serverPort) {
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        connectServer();
    }


    @Override
    protected boolean openRemoteScreen(String remoteName) {
        if (connectStatus) {
            isController = true;
            screen = new RemoteScreen(remoteName, this);
            screen.launch();
            openSession();
            return true;
        }
        return false;

    }


    @Override
    protected void closeRemoteScreen() {
        if (isController) {
            this.isController = false;
            super.closeRemoteScreen();
        }
        closeSession();
    }

    /**
     * 连接至server
     */
    private void connectServer() {
        final Bootstrap bootstrap = new Bootstrap();
        NioEventLoopGroup group = new NioEventLoopGroup();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {

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
                RemoteClient.super.updateConnectionStatus(true);
            } else {
                this.connectStatus = false;
                RemoteClient.super.updateConnectionStatus(false);
                Integer order = retry + 1;
                logger.info("reconnect to remote server serverIp={},serverPort={},retry times ={}", serverIp, serverPort, order);
                bootstrap.config().group().schedule(() -> connect(bootstrap, order), 5, TimeUnit
                        .SECONDS);
            }
        });
    }

    /**
     * 关闭会话
     */
    private void closeSession() {

    }

    /**
     * 打开会话
     */
    private void openSession() {

    }


    public static void main(String[] args) {
        RemoteClient remoteClient = new RemoteClient("172.16.1.72", 54321);
    }

}