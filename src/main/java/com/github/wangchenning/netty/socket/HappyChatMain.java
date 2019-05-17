package com.github.wangchenning.netty.socket;

import com.github.wangchenning.netty.socket.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * WebSocket聊天室，客户端参考docs目录下的websocket.html
 */
@Component
public class HappyChatMain {
    private static final Logger logger = LoggerFactory.getLogger(HappyChatMain.class);

    @Autowired
    private HappyChatServer server;

    public void startNetty() {
//        final HappyChatServer server = new HappyChatServer(Constants.DEFAULT_PORT);
        server.bind(Constants.DEFAULT_PORT);
        server.init();
        server.start();
        // 注册进程钩子，在JVM进程关闭前释放资源
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                server.shutdown();
                logger.warn(">>>>>>>>>> jvm shutdown");
                System.exit(0);
            }
        });
    }
}
