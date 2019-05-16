package com.github.wangchenning.netty.fourthexample;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.timeout.IdleStateEvent;

import java.time.LocalDateTime;

/**
 * @author Musk
 */
public class MyServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;

            String eventType = null;

            switch (event.state()) {
                case READER_IDLE:
                    eventType = "读空闲";
                    ChannelFuture channelFuture = ctx.channel().writeAndFlush(new PingWebSocketFrame());
                    System.out.println("是否成功"+channelFuture.isSuccess());
                    break;
                case WRITER_IDLE:
                    eventType = "写空闲";
                    ctx.channel().writeAndFlush(new PingWebSocketFrame());
                    break;
                case ALL_IDLE:
                    eventType = "读写空闲";
                    ctx.channel().writeAndFlush(new PingWebSocketFrame());
                    break;
                default:;
            }
            System.out.println(ctx.channel().remoteAddress() + " 超时事件： " + eventType);
        }
        ctx.fireChannelRead(evt);
    }


}
