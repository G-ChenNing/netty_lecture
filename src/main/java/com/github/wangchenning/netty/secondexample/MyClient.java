package com.github.wangchenning.netty.secondexample;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author Musk
 */
public class MyClient {
    public static void main(String[] args) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        try{
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(bossGroup).channel(NioSocketChannel.class)
                    .handler(new MyClientInitializer());
            ChannelFuture connect = bootstrap.connect("localhost", 8899).sync();
            connect.channel().closeFuture().sync();
        }finally {
            bossGroup.shutdownGracefully();
        }
        
    }
}
