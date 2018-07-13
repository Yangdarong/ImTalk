package com.yxr.imtalk.activity;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.yxr.imtalk.R;
import com.yxr.imtalk.manager.ChatRoomManager;
import com.yxr.imtalk.manager.ConnectionManager;

import org.jivesoftware.smack.AbstractXMPPConnection;

public class ChatRoomCreateActivity extends AppCompatActivity {

    private AbstractXMPPConnection connection;

    private EditText et_name, et_password, et_desc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom_create);
        initView();
    }

    private void initView() {
        et_name = findViewById(R.id.et_name);
        et_password = findViewById(R.id.et_password);
        et_desc = findViewById(R.id.et_desc);
    }

    public void closeActivity(View layout) {
        finish();
    }

    /**
     * 创建房间
     *
     * @param layout
     */
    public void createChatRoom(View layout) {
        new CreateRoomTask().execute();
    }

    class CreateRoomTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                connection = ConnectionManager.getConnection();

                // 房间名称
                String name = et_name.getText().toString();
                // 进入的密码
                String password = et_password.getText().toString();
                // 概述
                String desc = et_desc.getText().toString();

                ChatRoomManager.createRoom(connection, name, password, desc);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                Toast.makeText(ChatRoomCreateActivity.this, "群创建成功!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(ChatRoomCreateActivity.this, "群创建失败!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
