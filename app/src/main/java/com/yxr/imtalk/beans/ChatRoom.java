package com.yxr.imtalk.beans;

/**
 * 群组信息
 */
public class ChatRoom {

    private String jid;
    private String name;
    private String password;

    // 人数
    private int occuptionCount;

    // 描述
    private String description;

    public ChatRoom() {
    }

    public String getJid() {
        return jid;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getOccuptionCount() {
        return occuptionCount;
    }

    public void setOccuptionCount(int occuptionCount) {
        this.occuptionCount = occuptionCount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
