package com.anglele.netty;


import com.alibaba.fastjson.JSONObject;
import com.anglele.config.Constant;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;

/**
 * websocket 具体业务处理方法
 */
public class WebSocketHandler extends ChannelInboundHandlerAdapter {
    private WebSocketServerHandshaker handshaker;

    /**
     * 当客户端连接成功，返回个成功信息
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

    }

    /**
     * 当客户端断开连接
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        for (String key : Global.pushCtxMap.keySet()) {

            if (ctx.equals(Global.pushCtxMap.get(key))) {
                //从连接池内剔除
                Global.pushCtxMap.remove(key);
                System.out.println("===========>用户[" + key.split("-")[0] + "]离开websocket连接池,当前有效总连接数( " + Global.pushCtxMap.size() + " )");
            }
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        // 传统的HTTP接入
        if (msg instanceof FullHttpRequest) {
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {
            handlerWebSocketFrame(ctx, (WebSocketFrame) msg);

        }
        super.channelRead(ctx, msg);
    }

    public void handlerWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
        //关闭请求
        if (frame instanceof CloseWebSocketFrame) {
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }
        //只支持文本格式，不支持二进制消息
        if (!(frame instanceof TextWebSocketFrame)) {
            throw new Exception("仅支持文本格式");
        }

        //客服端发送过来的消息
        String request = ((TextWebSocketFrame) frame).text();
//        System.out.println("服务端收到：" + request);
        JSONObject jsonObject = null;
        try {
            jsonObject = JSONObject.parseObject(request);
        } catch (Exception e) {
        }
        if (jsonObject == null) {

            return;
        }

        String userId = (String) jsonObject.get("userId");
        String random = (String) jsonObject.get("random");

        //根据id判断是否登陆或者是否有权限等
        if (userId != null && !"".equals("userId") && random != null && !"".equals("random")) {

            //用户是否有权限
            boolean userIdAccess = true;
            //类型是否符合定义
            boolean randomAccess = true;

            if (userIdAccess && randomAccess) {
//                InputStream is = Runtime.getRuntime().exec(new String[] {"bash", "-c", "ulimit -a"}).getInputStream();
//                int c;
//                while ((c = is.read()) != -1) {
//                    System.out.write(c);
//                }

                Global.pushCtxMap.put(userId + "-" + random, ctx);
//				Global.aaChannelGroup.add(ctx.channel());
                System.out.println("===========>用户[" + userId + "]添加到连接池,当前有效websocket总连接数( " + Global.pushCtxMap.size() + " )");
            }
        }

    }

    //第一次请求是http请求，请求头包括ws的信息
    public void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
        if (!req.decoderResult().isSuccess()) {
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }
        // 构造握手响应返回
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory("ws:/" + ctx.channel() + "/"+Constant.WS_NAME, null, false);
        handshaker = wsFactory.newHandshaker(req);
        // 请求头不合法, 导致handshaker没创建成功
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            handshaker.handshake(ctx.channel(), req);
        }
    }

    public static void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, DefaultFullHttpResponse res) {
        // 返回应答给客户端
        if (res.status().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(), CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
        }
        // 如果是非Keep-Alive，关闭连接
        ChannelFuture f = ctx.channel().writeAndFlush(res);
        if (!isKeepAlive(req) || res.status().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    private static boolean isKeepAlive(FullHttpRequest req) {
        return false;
    }

    //异常处理，netty默认是关闭channel
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        //输出日志
        cause.printStackTrace();
        ctx.close();
    }


}