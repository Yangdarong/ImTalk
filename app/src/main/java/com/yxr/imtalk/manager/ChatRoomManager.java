package com.yxr.imtalk.manager;

import com.yxr.imtalk.beans.ChatRoom;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.muc.RoomInfo;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.FormField;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class ChatRoomManager {

    public static final String CONFERENCE = "@conference.";

    /**
     *  创建一个房间
     *
     * @param connection
     * @param name
     * @param password
     * @param description
     * @throws XMPPException.XMPPErrorException
     * @throws SmackException
     */
    public static void createRoom(AbstractXMPPConnection connection, String name, String password, String description) throws XMPPException.XMPPErrorException, SmackException {
        // 群聊管理器
        MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);
        // 群聊对话
        // 群聊房间的Jid
        String jid = name + CONFERENCE + connection.getServiceName();
        MultiUserChat muc = manager.getMultiUserChat(jid);

        muc.createOrJoin(name);

        // 对房间进行配置
        Form configForm = muc.getConfigurationForm();

        // 负责提交的配置
        Form submitForm = configForm.createAnswerForm();

        // 遍历所有的默认配置
        for (FormField formField : configForm.getFields()) {
            // 为空字符串,或者为NULL的默认配置项,不用读取
            if (!formField.getType().equals(FormField.Type.hidden)
                    && formField.getVariable() != null) {
                submitForm.setDefaultAnswer(formField.getVariable());;
            }
        }

        // 一些而外的配置
        List<String> owners = new ArrayList<>();
        owners.add(connection.getUser());

        submitForm.setAnswer("muc#roomconfig_roomowners", owners);
        // 设置聊天室是持久聊天室，即将要被保存下来
        submitForm.setAnswer("muc#roomconfig_persistentroom", false);
        // 房间仅对成员开放
        submitForm.setAnswer("muc#roomconfig_membersonly", false);
        // 允许占有者邀请其他人
        submitForm.setAnswer("muc#roomconfig_allowinvites", true);
        // 进入是否需要密码
        //submitForm.setAnswer("muc#roomconfig_passwordprotectedroom", true);
        // 设置进入密码
        //submitForm.setAnswer("muc#roomconfig_roomsecret", "password");
        // 能够发现占有者真实 JID 的角色
        // submitForm.setAnswer("muc#roomconfig_whois", "anyone");
        // 登录房间对话
        submitForm.setAnswer("muc#roomconfig_enablelogging", true);
        // 仅允许注册的昵称登录
        submitForm.setAnswer("x-muc#roomconfig_reservednick", true);
        // 允许使用者修改昵称
        submitForm.setAnswer("x-muc#roomconfig_canchangenick", false);
        // 允许用户注册房间
        submitForm.setAnswer("x-muc#roomconfig_registration", false);
        // 发送已完成的表单（有默认值）到服务器来配置聊天室
        submitForm.setAnswer("muc#roomconfig_passwordprotectedroom", true);
        // 发送已完成的表单（有默认值）到服务器来配置聊天室
        submitForm.setAnswer("muc#roomconfig_roomsecret", password);
        submitForm.setAnswer("muc#roomconfig_roomdesc", description);

    }

    /**
     *
     *
     * @param connection
     * @param roomName
     * @param roomPwd
     * @param nikename
     */
    public static MultiUserChat joinRoom(AbstractXMPPConnection connection, String roomName, String roomPwd, String nikename) throws SmackException.NotConnectedException, XMPPException.XMPPErrorException, SmackException.NoResponseException {
        // 群聊管理器
        MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);
        // 群聊对话
        // 群聊房间的Jid
        String jid = roomName + CONFERENCE + connection.getServiceName();
        MultiUserChat muc = manager.getMultiUserChat(jid);

        // 获取加入房间之前的历史消息
        DiscussionHistory history = new DiscussionHistory();
        history.setSince(new Date(2018, 1, 1));
        //
        muc.join(nikename, roomPwd, history, 5000);

        return muc;
    }

    /**
     * 获取已经加入的群组的列表
     *
     * @param connection
     * @return
     */
    public static List<ChatRoom> getJoinedChatRoom(AbstractXMPPConnection connection) {

        List<ChatRoom> list = new ArrayList<>();
        MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);
        Set<String> rooms = manager.getJoinedRooms();

        for (String room : rooms) {
            try {
                RoomInfo info = manager.getRoomInfo(room);
                ChatRoom cr = new ChatRoom();

                // jid
                cr.setJid(info.getRoom());
                // 组名
                cr.setName(info.getName());
                // 小组描述
                cr.setDescription(info.getDescription());

                // 人数
                cr.setOccuptionCount(info.getOccupantsCount());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return list;
    }
}
