package com.yxr.imtalk.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.yxr.imtalk.R;
import com.yxr.imtalk.adapter.ChatRoomListAdapter;
import com.yxr.imtalk.beans.ChatRoom;
import com.yxr.imtalk.manager.ChatRoomManager;
import com.yxr.imtalk.manager.ConnectionManager;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smackx.muc.RoomInfo;

import java.util.ArrayList;
import java.util.List;

public class ChatRoomOperateActivity extends AppCompatActivity {

    private AbstractXMPPConnection connection;

    private ListView lv_groups;

    private List<ChatRoom> roomList = new ArrayList<>();

    private ChatRoomListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom_operate);
        initView();

        adapter = new ChatRoomListAdapter(this, roomList);
        lv_groups.setAdapter(adapter);
    }

    private void initView() {
        lv_groups = findViewById(R.id.list_group);
    }



    /**
     * 创建群聊
     * @param layout
     */
    public void createRoom(View layout) {
        // 页面跳转,到创建群的页面
        connection = ConnectionManager.getConnection();
        Intent intent = new Intent(this, ChatRoomCreateActivity.class);
        startActivity(intent);
    }

    /**
     * 加入群聊
     * @param layout
     */
    public void joinRoom(View layout) {
        Intent intent = new Intent(this, ChatRoomJoinActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

    }

    class ChatRoomLoader extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            // 等待服务器更新
            SystemClock.sleep(1000);
            return null;
        }
    }
}
