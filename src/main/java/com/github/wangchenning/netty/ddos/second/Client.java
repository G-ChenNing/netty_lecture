package com.github.wangchenning.netty.ddos.second;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class Client {

    //线程组
    private static final EventLoopGroup group = new NioEventLoopGroup();

    //启动类
    private static final Bootstrap bootstrap = new Bootstrap();

    private static final int PORT = 80;

    private static final String HOST = "92.38.128.173";



    public static void main(String[] args) throws Exception {
            EventLoopGroup bossGroup = new NioEventLoopGroup();
            try{
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(bossGroup).channel(NioSocketChannel.class)
                        .handler(new MyClientInitializer());
                ChannelFuture connect = bootstrap.connect("92.38.128.173", 80).sync();
                connect.channel().closeFuture().sync();
            }finally {
                bossGroup.shutdownGracefully();
            }

        }

}

