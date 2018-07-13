package com.yxr.imtalk;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yxr.imtalk.activity.FriendsActivity;
import com.yxr.imtalk.activity.RegisterActivity;
import com.yxr.imtalk.manager.ConnectionManager;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

public class MainActivity extends Activity implements View.OnClickListener {

    private EditText et_user, et_pwd;
    private TextView tv_login, tv_register;

    private XMPPTCPConnection connection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        et_user = findViewById(R.id.login_editText_user);
        et_pwd = findViewById(R.id.login_editText_password);
        tv_login = findViewById(R.id.login_textview_enter);
        tv_register = findViewById(R.id.login_textview_phone_regster);

        tv_login.setOnClickListener(this);
        tv_register.setOnClickListener(this);
    }

    public void login() {
        // 登录
        new LoginTask().execute();
    }

    public void register() {
        // 注册
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_textview_enter: {
                login();
                break;
            }

            case R.id.login_textview_phone_regster: {
                register();
                break;
            }
        }
    }

    class LoginTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            // 获取用户名密码
            String username = et_user.getText().toString();
            String password = et_pwd.getText().toString();

            AbstractXMPPConnection connection = ConnectionManager.getConnection();
            try {
                // 登录
                connection.login(username, password);
                // 登录成功,发送状态到服务器更新用户的在线状态
                Presence presence = new Presence(Presence.Type.available);
                connection.sendStanza(presence);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                // 跳转到用户界面(好友列表界面)
                Intent intent = new Intent(MainActivity.this, FriendsActivity.class);
                startActivity(intent);

            } else {
                Toast.makeText(MainActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
