package io.github.springstudent.dekstop.server.netty;

import io.netty.channel.Channel;

/**
 * @author ZhouNing
 * @date 2024/12/10 14:15
 **/
public class NettyChannelBrother {
    /**
     * 控制端
     */
    private Channel controller;
    /**
     * 被控制端
     */
    private Channel controlled;

    public NettyChannelBrother(Channel controller, Channel controlled) {
        this.controller = controller;
        this.controlled = controlled;
    }

    public Channel getController() {
        return controller;
    }

    public void setController(Channel controller) {
        this.controller = controller;
    }

    public Channel getControlled() {
        return controlled;
    }

    public void setControlled(Channel controlled) {
        this.controlled = controlled;
    }
}
