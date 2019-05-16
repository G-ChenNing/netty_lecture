package com.github.wangchenning.netty.fifthexample;

import com.github.wangchenning.netty.fourthexample.MyServerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import sun.plugin2.message.HeartbeatMessage;

import java.util.concurrent.TimeUnit;

/**
 * @author Musk
 */
public class MyServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new ChunkedWriteHandler());
        pipeline.addLast(new HttpObjectAggregator(8192));
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
        //空闲状态检测(参数：在一段时间内没有读，没有写,没有读写)
        pipeline.addLast(new IdleStateHandler(10,15,20, TimeUnit.SECONDS));
        pipeline.addLast(new MyServerHandler());
        pipeline.addLast(new TextWebSocketFrameHandler());

    }
}
