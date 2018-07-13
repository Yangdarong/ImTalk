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
import org.jxmpp.util.XmppStringUtils;

public class ChatRoomJoinActivity extends AppCompatActivity {

    private EditText et_name, et_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom_join);
        initView();
    }

    private void initView() {
        et_name = findViewById(R.id.et_name1);
        et_password = findViewById(R.id.et_password1);
    }

    public void closeActivity(View layout) {
        finish();
    }

    public void join(View layout) {
        new CreateJoinTask().execute();
    }

    class CreateJoinTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                AbstractXMPPConnection connection = ConnectionManager.getConnection();

                // 房间名称
                String name = et_name.getText().toString();
                // 进入的密码
                String password = et_password.getText().toString();

                String nikename = XmppStringUtils.parseLocalpart(connection.getUser());
                ChatRoomManager.joinRoom(connection, name, password, nikename);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                Toast.makeText(ChatRoomJoinActivity.this, "群加入成功!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(ChatRoomJoinActivity.this, "群加入失败!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
