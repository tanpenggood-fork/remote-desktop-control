package io.github.springstudent.dekstop.server.netty;


import io.github.springstudent.dekstop.common.utils.NettyUtils;
import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ZhouNing
 * @date 2024/12/12 9:18
 **/
public class NettyChannelManager {


    private static Map<String, Channel> deviceCodeChannelMap = new ConcurrentHashMap<>(32);

    private static Map<String, NettyChannelBrother> channelBrotherMap = new ConcurrentHashMap<>(32);

    public static void addChannel(String deviceCode, Channel channel) {
        deviceCodeChannelMap.put(deviceCode, channel);
    }

    public static Channel getChannel(String deviceCode) {
        return deviceCodeChannelMap.get(deviceCode);
    }

    public static void bindChannelBrother(Channel controller, Channel controlled) {
        NettyChannelBrother channelBrother = new NettyChannelBrother(controller, controlled);
        channelBrother.startControll();
        channelBrotherMap.putIfAbsent(NettyUtils.getDeviceCode(controlled), channelBrother);

    }

    public static NettyChannelBrother unbindChannelBrother(Channel controller, Channel controlled) {
        NettyChannelBrother channelBrother = channelBrotherMap.get(NettyUtils.getDeviceCode(controlled));
        if (channelBrother != null) {
            channelBrother.stopControll();
            channelBrotherMap.remove(NettyUtils.getDeviceCode(controlled));
        }
        return channelBrother;
    }

    public static Channel getControllerChannel(Channel controlled) {
        NettyChannelBrother channelBrother = channelBrotherMap.get(NettyUtils.getDeviceCode(controlled));
        if (channelBrother != null) {
            return channelBrother.getController();
        }
        return null;
    }

    public static Channel getControlledChannel(Channel controller) {
        NettyChannelBrother channelBrother = channelBrotherMap.get(NettyUtils.getControllDeviceCode(controller));
        if (channelBrother != null) {
            return channelBrother.getControlled();
        }
        return null;
    }

}
