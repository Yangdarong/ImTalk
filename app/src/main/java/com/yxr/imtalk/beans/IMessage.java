package com.yxr.imtalk.beans;

import com.alibaba.fastjson.JSON;

/**
 * 信息类
 */
public class IMessage {

    private String sender;

    private String content;

    private String date;

    private int msgType;

    public static final int MESSAGE_TYPE_IN = 1;
    public static final int MESSAGE_TYPE_OUT = 2;

    // 消息的模式: 文本 录音
    private int msgModle;

    public static final int MESSAGE_MODEL_TEXT = 1;
    public static final int MESSAGE_MODEL_AUDIO = 2;
    public static final int MESSAGE_MODEL_PIC = 3;

    // 语音的消息的时长
    private int duration;

    // 文件的名称
    private String fileName;

    // 消息的状态
    private int status;

    public static final int MESSAGE_STATUS_SUCCESS = 1;
    public static final int MESSAGE_STATUS_FAIL = 2;
    public static final int MESSAGE_STATUS_WAIT = 3;


    public IMessage() {
    }

    public IMessage(String sender, String content, String date, int msgType) {
        this.sender = sender;
        this.content = content;
        this.date = date;
        this.msgType = msgType;
        this.msgModle = MESSAGE_MODEL_TEXT;
    }

    /**
     *  构造语音消息
     * @param sender
     * @param date
     * @param msgType
     * @param duration 语音文件的时长
     * @param fileName 语音文件的名称
     */
    public IMessage(String sender, String date, int msgType, int duration, String fileName) {
        this.sender = sender;
        this.date = date;
        this.msgType = msgType;
        this.duration = duration;
        this.fileName = fileName;

        this.content = " " + (duration/1000) + "\' " + (duration%1000) + "\"  语音消息";
        this.msgModle = MESSAGE_MODEL_AUDIO;
    }

    /**
     * 将IMessage 对象转为json文本,便于作为文本消息进行发送
     *
     * @return
     */
    public String toJson() {
        return JSON.toJSONString(this);
    }

    public static IMessage fromJson(String json) {
        return JSON.parseObject(json, IMessage.class);
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public static int getMessageTypeIn() {
        return MESSAGE_TYPE_IN;
    }

    public static int getMessageTypeOut() {
        return MESSAGE_TYPE_OUT;
    }

    public int getMsgModle() {
        return msgModle;
    }

    public void setMsgModle(int msgModle) {
        this.msgModle = msgModle;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
