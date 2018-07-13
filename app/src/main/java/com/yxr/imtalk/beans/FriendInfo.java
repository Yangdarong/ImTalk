package com.yxr.imtalk.beans;

import com.yxr.imtalk.manager.ConnectionManager;

/**
 *  联系人信息
 *
 */
public class FriendInfo {

    private String username;

    // 昵称
    private String name;

    // 个性签名
    private String mood;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    /**
     *  like jack@域名
     * @return
     */
    public String getJid() {
        return username + "@" + ConnectionManager.getConnection().getServiceName();
    }
}
