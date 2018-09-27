package com.anglele.netty;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

/**
 * Created by jeffeng on 2018-7-3.
 */
@ChannelHandler.Sharable
public abstract class BaseWebSocketServerHandler extends SimpleChannelInboundHandler<Object> {
    /**
     * 推送单个
     */
    public static final void push(final ChannelHandlerContext ctx, final String message) {
        TextWebSocketFrame tws = new TextWebSocketFrame(message);
        ctx.channel().writeAndFlush(tws);
    }

    /**
     * 根据用户编号发送推送消息
     *
     * @param userId
     * @param message
     */
    public static void push(String userId, String message) {
        Global.pushCtxMap.forEach((k, v) -> {
            try {
                if (userId.equals(k.split("-")[0])) {
                    TextWebSocketFrame tws = new TextWebSocketFrame(message);
                    v.channel().writeAndFlush(tws);
                }
            } catch (Exception e) {
                System.out.println("=====>推送参数错误");
            }

        });
    }

    /**
     * 群发
     */
    public static final void push(final ChannelGroup ctxGroup, final String message) {
        TextWebSocketFrame tws = new TextWebSocketFrame(message);
        ctxGroup.writeAndFlush(tws);
    }
}
