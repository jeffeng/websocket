package com.anglele.netty;


import com.anglele.config.Constant;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import java.util.concurrent.TimeUnit;

/**
 * @author User
 * 该类在全局当中只能创建一个对象（GameServerInitializer.main中已经创建）,其它对象用该类的静态方法getInstance（）获取该实例即可。
 */
public class WebsocketServer {
    public static WebsocketServer instance;

    public static final WebsocketServer getInstance() {
        return instance;
    }

    private int port;

    public WebsocketServer(int port) {
        if (instance != null) {
            return;
        }

        this.port = port;
        try {
            this.start();
            instance = this;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void start() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup);
            b.channel(NioServerSocketChannel.class);
            b.childHandler(new myChannelInitializer());
            b.option(ChannelOption.SO_BACKLOG, 128);
            b.childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture f = b.bind(port);
            System.out.println("Netty Websocket is start .........");
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    private class myChannelInitializer extends ChannelInitializer<SocketChannel> {
        @Override
        public void initChannel(SocketChannel ch) throws Exception {
            SSLContext sslContext = SslUtil.createSSLContext("JKS", Constant.JKS_PATH, Constant.JKS_PSD);
            //SSLEngine 此类允许使用ssl安全套接层协议进行安全通信
            SSLEngine engine = sslContext.createSSLEngine();
            engine.setUseClientMode(false);

            ch.pipeline().addLast(new SslHandler(engine));
            ch.pipeline().addLast(new IdleStateHandler(5, 0, 0, TimeUnit.SECONDS));
            ch.pipeline().addLast("http-codec", new HttpServerCodec());
            ch.pipeline().addLast("aggregator", new HttpObjectAggregator(65536));
            ch.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
//			ch.pipeline().addLast(new AcceptorIdleStateTrigger());
            ch.pipeline().addLast("handler", new WebSocketHandler());
        }
    }

}
