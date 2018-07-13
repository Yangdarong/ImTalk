package com.yxr.imtalk.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.yxr.imtalk.R;
import com.yxr.imtalk.beans.FriendInfo;
import com.yxr.imtalk.beans.GroupInfo;

import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FriendsExpandableListAdapter extends BaseExpandableListAdapter {

    private final LayoutInflater inflater;

    // 好友分组集合
    private List<GroupInfo> groupDatas;

    // 好友集合 (一个分组下多个联系人)
    private List<List<FriendInfo>> friendDatas;

    /**
     *
     * @param context
     * @param groups 好友列表分组
     */
    public FriendsExpandableListAdapter(Context context, Collection<RosterGroup> groups) {
        inflater = LayoutInflater.from(context);
        setData(groups);
    }

    /**
     * 获取所有的好友数据
     *
     * @param groups
     */
    public void setData(Collection<RosterGroup> groups) {
        // 时刻更新好友数据
        groupDatas = new ArrayList<>();
        friendDatas = new ArrayList<>();

        // 获取分组
        for (RosterGroup group : groups) {
            GroupInfo groupInfo = new GroupInfo();
            groupInfo.setGroupName(group.getName());
            groupDatas.add(groupInfo);

            // 获取分组下的所有好友
            List<RosterEntry> entries = group.getEntries();
            List<FriendInfo> friendsList = new ArrayList<>();

            for (RosterEntry entry : entries) {
                // 如果双方都是好友, 才显示到列表中
                if (TextUtils.equals("both", entry.getType().name())) {
                    FriendInfo friend = new FriendInfo();
                    // 用户名
                    friend.setUsername(entry.getName());
                    // 昵称
                    friend.setName(entry.getName());
                    // 心情
                    friend.setMood("该用户很懒,什么都不想说...");

                    friendsList.add(friend);
                }
            }

            groupInfo.setFriends(friendsList);
            friendDatas.add(friendsList);
        }


    }

    @Override
    public int getGroupCount() {
        return groupDatas.size();
    }

    /**
     * 获取 对应分组下的好友个数
     * @param groupPosition
     * @return
     */
    @Override
    public int getChildrenCount(int groupPosition) {
        List<FriendInfo> list = friendDatas.get(groupPosition);
        if (list != null && !list.isEmpty()) {
            return list.size();
        }
        return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupDatas.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return friendDatas.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int i, int i1) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    // 联系人分组条目
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View view, ViewGroup convertView) {
        View layout = inflater.inflate(R.layout.friend_group_layout, null);
        TextView tv_group_name = layout.findViewById(R.id.tv_group_name);

        // 设置 组名
        GroupInfo group = (GroupInfo) getGroup(groupPosition);
        tv_group_name.setText(group.getGroupName() + " ("+ group.getFriends().size() + ")" );

        return layout;
    }

    // 联系人条目
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean b, View view, ViewGroup viewGroup) {
        View layout = inflater.inflate(R.layout.friend_child_layout, null);
        FriendInfo friend = (FriendInfo) getChild(groupPosition, childPosition);
        // 昵称
        TextView tv_friend_nikename = layout.findViewById(R.id.friend_child_textView_name);
        tv_friend_nikename.setText(friend.getName());



        return layout;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
