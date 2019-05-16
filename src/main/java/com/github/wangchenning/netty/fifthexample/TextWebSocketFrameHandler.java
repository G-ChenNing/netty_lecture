package com.github.wangchenning.netty.fifthexample;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

import java.time.LocalDateTime;

/**
 * @author Musk
 */
public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame msg) throws Exception {
        if (msg instanceof PingWebSocketFrame) {
            System.out.println("收到ping");
            ChannelFuture channelFuture = ctx.channel().writeAndFlush(new PongWebSocketFrame(msg.content().retain()));
            System.out.println(channelFuture.isSuccess());
        }

        if (msg instanceof PongWebSocketFrame) {
            System.out.println("客户端连上");
        }

        if (msg instanceof TextWebSocketFrame) {
//            msg = (TextWebSocketFrame) msg;

            System.out.println("收到消息：" + ((TextWebSocketFrame) msg).text());

            ctx.channel().writeAndFlush(new TextWebSocketFrame("服务器时间：" + LocalDateTime.now()));
        }


//        ctx.fireChannelRead(msg);

    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("handlerAdded: " + ctx.channel().id().asLongText());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("handlerRemoved: " + ctx.channel().id().asLongText());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("异常发生");
        ctx.close();
    }
}
