package io.github.springstudent.dekstop.common.utils;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

/**
 * @author ZhouNing
 * @date 2024/12/11 16:37
 **/
public class NettyUtils {
    private static final AttributeKey<String> ATTR_KEY_READER_TIME = AttributeKey.valueOf("readerTime");

    public static void updateReaderTime(Channel channel, Long time) {
        channel.attr(ATTR_KEY_READER_TIME).set(time.toString());
    }

    public static Long getReaderTime(Channel channel) {
        String value = channel.attr(ATTR_KEY_READER_TIME).get();
        if (value != null) {
            return Long.valueOf(value);
        }
        return null;
    }

}
