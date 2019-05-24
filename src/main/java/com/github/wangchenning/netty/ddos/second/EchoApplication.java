package com.github.wangchenning.netty.ddos.second;


import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import io.netty.util.ResourceLeakDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_0;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * A simple HTTP ECHO application
 */
public class EchoApplication {

    private static final Logger log = LoggerFactory.getLogger(EchoApplication.class);

    public ChannelFuture server(EventLoopGroup workerGroup) {
        ServerBootstrap b = new ServerBootstrap();
        b.group(workerGroup).channel(NioServerSocketChannel.class)
                //Setting InetSocketAddress to port 0 will assign one at random
                .localAddress(new InetSocketAddress(0))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        //HttpServerCodec is a helper ChildHandler that encompasses
                        //both HTTP request decoding and HTTP response encoding
                        ch.pipeline().addLast(new HttpServerCodec());
                        //HttpObjectAggregator helps collect chunked HttpRequest pieces into
                        //a single FullHttpRequest. If you don't make use of streaming, this is
                        //much simpler to work with.
                        ch.pipeline().addLast(new HttpObjectAggregator(1048576));
                        //Finally add your FullHttpRequest handler. Real examples might replace this
                        //with a request router
                        ch.pipeline().addLast(new SimpleChannelInboundHandler<FullHttpRequest>() {
                            @Override
                            public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
                                ctx.flush();
                                //The close is important here in an HTTP request as it sets the Content-Length of a
                                //response body back to the client.
                                ctx.close();
                            }

                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
                                DefaultFullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, msg.content().copy());
                                ctx.write(response);
                            }
                        });
                    }
                });

        // Start the server & bind to a random port.
        return b.bind();
    }

    public static Channel
            channel;
    public static HttpRequest httpRequest;


    public Bootstrap client() {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(workerGroup).channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        //HttpClient codec is a helper ChildHandler that encompasses
                        //both HTTP response decoding and HTTP request encoding
                        ch.pipeline().addLast(new HttpRequestEncoder());
                        ch.pipeline().addLast(new HttpResponseDecoder());
                        //HttpObjectAggregator helps collect chunked HttpRequest pieces into
                        //a single FullHttpRequest. If you don't make use of streaming, this is
                        //much simpler to work with.
                        ch.pipeline().addLast(new HttpObjectAggregator(1048576));
                        ch.pipeline().addLast(new SimpleChannelInboundHandler<FullHttpResponse>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse msg) throws Exception {
                                final String echo = msg.content().toString(CharsetUtil.UTF_8);
                                System.out.println(msg.getStatus().code());
                                if (msg.getStatus().code() == 302) {
//                                    channel.writeAndFlush(httpRequest);
                                }

                                log.info("Response: {}", echo);
                            }
                        });
                    }
                });
        return b;
    }


    public static void main(String[] args) throws InterruptedException {
        final ByteBuf content = Unpooled.copiedBuffer("Hello World!", CharsetUtil.UTF_8);
        EchoApplication app = new EchoApplication();
        Bootstrap clientBootstrap = app.client();
        HttpRequest request = getHttpRequest();
        httpRequest = request;

//        while (true) {
        ChannelFuture channelFuture = clientBootstrap
//                .connect("127.0.0.1", 8080)
                .connect("dongmanhuayuan.myheartsite.com", 80)
//                        .connect("92.38.128.173",443)
                .addListener((ChannelFutureListener) future -> {
                    

                    //                    request.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());
                    future.channel().writeAndFlush(request);


                });
//        channel = channelFuture.channel();

//
//
//            HttpRequest request = new DefaultFullHttpRequest(
//                    HTTP_1_0, HttpMethod.GET, "/xxx");
//            channel.writeAndFlush(request);
//            TimeUnit.SECONDS.sleep(1);

//    }

//        while (true) {
//            System.out.println(1);
//        }


    }

    private static HttpRequest getHttpRequest() {
        // Prepare the HTTP request.
        HttpRequest request = new DefaultFullHttpRequest(
                HTTP_1_1, HttpMethod.GET, "/acg/search");
        // If we don't set a content length from the client, HTTP RFC
        // dictates that the body must be be empty then and Netty won't read it.
        request.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html");
//                    request.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain;charset=UTF-8");
//                    request.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html;charset=UTF-8");
        request.headers().set(HttpHeaderNames.CACHE_CONTROL, HttpHeaderValues.NO_CACHE);
                    request.headers().set(HttpHeaderNames.HOST, "127.0.0.1");
//                    request.headers().set(HttpHeaderNames.HOST, "101.68.88.211");
//        request.headers().set(HttpHeaderNames.HOST, "92.38.128.173");


//                    request.headers().set(HttpHeaderNames.SERVER, "nginx");

        
        request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        return request;
    }


}
