package io.github.springstudent.dekstop.server.netty;

import io.github.springstudent.dekstop.common.bean.Constants;
import io.github.springstudent.dekstop.common.command.CmdResCapture;
import io.github.springstudent.dekstop.common.utils.NettyUtils;
import io.netty.channel.Channel;

import java.util.concurrent.atomic.AtomicBoolean;

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

    public boolean startControll() {
        NettyUtils.updateControllFlag(controller, Constants.CONTROLLER);
        NettyUtils.updateControllFlag(controlled, Constants.CONTROLLED);
        AtomicBoolean result = new AtomicBoolean();
        controlled.writeAndFlush(new CmdResCapture(CmdResCapture.START_)).addListener(future -> {
            if (future.isDone()) {
                controller.writeAndFlush(new CmdResCapture(CmdResCapture.START));
                result.set(true);
            } else {
                controller.writeAndFlush(new CmdResCapture(CmdResCapture.FAIL));
                result.set(false);
            }
        });
        return result.get();
    }

    public void stopControll() {
        NettyUtils.updateControllFlag(controller, null);
        NettyUtils.updateControllFlag(controlled, null);
        controller.writeAndFlush(new CmdResCapture(CmdResCapture.STOP));
        controlled.writeAndFlush(new CmdResCapture(CmdResCapture.STOP_));
    }

}
