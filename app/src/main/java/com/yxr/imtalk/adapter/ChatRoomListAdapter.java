package com.yxr.imtalk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yxr.imtalk.R;
import com.yxr.imtalk.beans.ChatRoom;

import java.util.List;

public class ChatRoomListAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<ChatRoom> roomList;
    private Context context;

    public ChatRoomListAdapter(Context context, List<ChatRoom> roomList) {
        setData(roomList);
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public void setData(List<ChatRoom> roomList) {
        this.roomList = roomList;
    }

    @Override
    public int getCount() {
        return roomList.size();
    }

    @Override
    public Object getItem(int position) {
        return roomList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatRoom info = roomList.get(position);

        if (convertView != null) {
            convertView = inflater.inflate(R.layout.group_item, null);
        }

        TextView tv_name = convertView.findViewById(R.id.group_textView_name);
        tv_name.setText(info.getName());

        TextView tv_desc = convertView.findViewById(R.id.group_textView_status);
        tv_desc.setText(info.getName());

        TextView tv_occu = convertView.findViewById(R.id.group_textView_occu);
        tv_occu.setText(String.valueOf(info.getOccuptionCount()));

        return convertView;
    }
}
