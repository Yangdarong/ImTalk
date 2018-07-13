package com.yxr.imtalk.beans;

import java.util.List;

/**
 *  联系人分组
 */
public class GroupInfo {

    private String groupName;

    // 所属的好友信息
    private List<FriendInfo> friends;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<FriendInfo> getFriends() {
        return friends;
    }

    public void setFriends(List<FriendInfo> friends) {
        this.friends = friends;
    }
}
