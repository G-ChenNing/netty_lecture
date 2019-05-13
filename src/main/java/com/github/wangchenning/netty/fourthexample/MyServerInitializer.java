package com.github.wangchenning.netty.fourthexample;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class MyServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        //空闲状态检测(参数：在一段时间内没有读，没有写,没有读写)
        pipeline.addLast(new IdleStateHandler(180,120,60,TimeUnit.SECONDS));
        pipeline.addLast(new MyServerHandler());


    }
}
