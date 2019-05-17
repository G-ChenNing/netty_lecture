package com.github.wangchenning.netty.socket;

import com.github.wangchenning.netty.socket.core.BaseServer;
import com.github.wangchenning.netty.socket.handler.MessageHandler;
import com.github.wangchenning.netty.socket.handler.UserAuthHandler;
import com.github.wangchenning.netty.socket.handler.UserInfoManager;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @description server端
 */
@Component
@Slf4j
public class HappyChatServer extends BaseServer {
    private ScheduledExecutorService executorService;

    public HappyChatServer() {

    }

    @Override
    public void start() {
        b.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .localAddress(new InetSocketAddress(port))
                .childHandler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(defLoopGroup,
                                //请求解码器
                                new HttpServerCodec(),
                                //将多个消息转换成单一的消息对象
                                new HttpObjectAggregator(65536),
                                //支持异步发送大的码流，一般用于发送文件流
                                new ChunkedWriteHandler(),
                                new WebSocketServerProtocolHandler("/ai_websocket"),
                                //检测链路是否读空闲
                                new IdleStateHandler(60, 0, 0),
//                                //处理握手和认证
                                new UserAuthHandler(),
//                                //处理消息的发送
                                new MessageHandler()
                        );
                    }
                });

        try {
            cf = b.bind().sync();
            InetSocketAddress addr = (InetSocketAddress) cf.channel().localAddress();
            log.info("WebSocketServer start success, port is:{}", addr.getPort());

            // 定时扫描所有的Channel，关闭失效的Channel
            executorService.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    log.info("scanNotActiveChannel --------");
                    UserInfoManager.scanNotActiveChannel();
                }
            }, 3, 60, TimeUnit.SECONDS);

            // 定时向所有客户端发送Ping消息
            executorService.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    UserInfoManager.broadCastPing();
                }
            }, 3, 50, TimeUnit.SECONDS);

        } catch (InterruptedException e) {
            log.error("WebSocketServer start fail,", e);
        }
    }

    public void bind(int port) {
        this.port = port;
//        executorService = Executors.newScheduledThreadPool(2);
        executorService = new ScheduledThreadPoolExecutor(2, new ThreadFactory() {
            private AtomicInteger index = new AtomicInteger(0);
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "WORK_" + index.incrementAndGet());
            }
        });
    }



    @Override
    public void shutdown() {
        if (executorService != null) {
            executorService.shutdown();
        }
        super.shutdown();
    }
}
