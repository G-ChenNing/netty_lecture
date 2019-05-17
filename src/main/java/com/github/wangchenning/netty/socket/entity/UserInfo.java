package com.github.wangchenning.netty.socket.entity;

import io.netty.channel.Channel;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @description user实体类
 */
public class UserInfo {
    private static AtomicInteger uidGener = new AtomicInteger(65535);

    private boolean isAuth = true; // 是否认证
    private long time = 0;  // 登录时间
    private int userId;     // UID
    private String nick;    // 昵称
    private String addr;    // 地址
    private Channel channel;// 通道
    private Integer pingTime;
//    private BaiduApiConstants.WEB_PAGE webPage;
    private boolean IS_LOCK = true;
    private boolean isFaceTiming;
    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public boolean isAuth() {
        return isAuth;
    }

    public void setAuth(boolean auth) {
        isAuth = auth;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public int getUserId() {
        return userId;
    }

    public Integer getPingTime() {
        return pingTime;
    }

    public void setPingTime(Integer pingTime) {
        this.pingTime = pingTime;
    }

    public void setUserId() {
        this.userId = uidGener.incrementAndGet();
    }

    public UserInfo() {
    }

//    public BaiduApiConstants.WEB_PAGE getWebPage() {
//        return webPage;
//    }
//
//    public void setWebPage(BaiduApiConstants.WEB_PAGE webPage) {
//        this.webPage = webPage;
//    }

    public boolean isIS_LOCK() {
        return IS_LOCK;
    }

    public void setIS_LOCK(boolean IS_LOCK) {
        this.IS_LOCK = IS_LOCK;
    }

    public boolean isFaceTiming() {
        return isFaceTiming;
    }

    public void setFaceTiming(boolean faceTiming) {
        isFaceTiming = faceTiming;
    }

}
